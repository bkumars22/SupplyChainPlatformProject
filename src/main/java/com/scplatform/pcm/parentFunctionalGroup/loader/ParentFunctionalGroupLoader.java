/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.parentFunctionalGroup.loader;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
import com.scplatform.pcm.parentFunctionalGroup.entity.ParentFunctionalGroup;
import com.scplatform.pcm.parentFunctionalGroup.repository.ParentFunctionalGroupRepository;
import com.scplatform.pcm.upload.loader.BaseImporter;
import com.scplatform.pcm.upload.loader.MessageLoaderException;
import com.scplatform.pcm.upload.loader.MessageLoaderStatus;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.scplatform.pcm.upload.repository.LoadEventRepository;
import com.scplatform.pcm.upload.repository.LoadJobRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLStreamReader;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ParentFunctionalGroupLoader extends BaseImporter {

    private final ParentFunctionalGroupRepository pfgRepository;
    private final FunctionalGroupRepository       fgRepository;

    private static final String OP_ADD    = "Add";
    private static final String OP_UPDATE = "Update";
    private static final String OP_DELETE = "Delete";

    private int pfgCount = 0;
    private Map<Integer, String> pfgErrors;
    public ParentFunctionalGroupLoader(LoadJobRepository loadJobRepository,
                                        LoadEventRepository loadEventRepository,
                                        PcmConfigUtil pcmConfigUtil,
                                        ParentFunctionalGroupRepository pfgRepository,
                                        FunctionalGroupRepository fgRepository) {
        super(loadJobRepository, loadEventRepository, pcmConfigUtil);
        this.pfgRepository = pfgRepository;
        this.fgRepository  = fgRepository;
    }

    // -----------------------------------------------------------------------
    // BaseImporter abstract method implementations
    // -----------------------------------------------------------------------

    @Override
    public void updateStats(MessageLoaderStatus status) {
        status.setStatistic("parentFunctionalGroup", pfgCount);
        status.setStatistic("LoadEvents", this.loadEventCount);
    }

    @Override
    public int getCount() {
        return pfgCount;
    }

    // -----------------------------------------------------------------------
    // process() – main entry point called by MessageLoader
    // -----------------------------------------------------------------------

    @Override
    public void process(XMLStreamReader xmlr, int passNumber) throws Exception {
        StopWatch batchTimer = new StopWatch();
        pfgCount = 0;
        pfgErrors = new LinkedHashMap<>();

        log.info("ParentFunctionalGroupLoader: starting process pass={}", passNumber);

        try {
            while (xmlr.hasNext()) {
                int event = xmlr.next();
                if (!xmlr.isStartElement() && !xmlr.isEndElement()) {
                    continue;
                }

                String localName = xmlr.getLocalName();

                if ("ParentFunctionalGroupMessage".equals(localName)) {
                    if (xmlr.isStartElement()) {
                        batchTimer.start();
                        pfgCount = 0;
                    } else {
                        stopTimer(batchTimer);
                    }

                } else if ("ParentFunctionalGroup".equals(localName)) {
                    if (xmlr.isStartElement()) {
                        try {
                            pfgCount++;
                            loadParentFunctionalGroup(xmlr);
                        } catch (MessageLoaderException e) {
                            handleRecordError(xmlr, e);
                        }
                    } else if (xmlr.isEndElement()) {
                        handleBatchFlush(batchTimer);
                    }
                }
            }
        } catch (Exception e) {
            log.error("ParentFunctionalGroupLoader: fatal error during processing", e);
            throw e;
        }

        log.info("ParentFunctionalGroupLoader: total processed={}", pfgCount);

        // Propagate per-record errors to BaseImporter.errors
        if (!pfgErrors.isEmpty()) {
            for (Map.Entry<Integer, String> entry : pfgErrors.entrySet()) {
                super.errors.add("Row " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // -----------------------------------------------------------------------
    // Record-level processing
    // -----------------------------------------------------------------------

    private void loadParentFunctionalGroup(XMLStreamReader xmlr) throws MessageLoaderException {
        String parentName         = xmlr.getAttributeValue(null, "ParentName");
        String functionalGroupName = xmlr.getAttributeValue(null, "FunctionalGroupName");
        String opCode             = xmlr.getAttributeValue(null, "OpCode");
        String description        = xmlr.getAttributeValue(null, "Description");
        String type               = xmlr.getAttributeValue(null, "Type");
        String purpose            = xmlr.getAttributeValue(null, "Purpose");

        log.info("Processing PFG record: parentName={} fgName={} opCode={}", parentName, functionalGroupName, opCode);

        if (StringUtils.isBlank(parentName)) {
            throw new MessageLoaderException("ParentName is required but was blank or missing.");
        }
        if (StringUtils.isBlank(opCode)) {
            throw new MessageLoaderException("OpCode is required but was blank or missing for ParentName=" + parentName);
        }

        if (OP_ADD.equalsIgnoreCase(opCode)) {
            addParentFunctionalGroup(parentName, functionalGroupName, description, type, purpose);
        } else if (OP_UPDATE.equalsIgnoreCase(opCode)) {
            updateParentFunctionalGroup(parentName, functionalGroupName, description, type, purpose);
        } else if (OP_DELETE.equalsIgnoreCase(opCode)) {
            deleteParentFunctionalGroupMapping(parentName, functionalGroupName);
        } else {
            throw new MessageLoaderException("Unknown opCode '" + opCode + "' for ParentName=" + parentName);
        }
    }

    // -----------------------------------------------------------------------
    // Add
    // -----------------------------------------------------------------------

    private void addParentFunctionalGroup(String parentName, String functionalGroupName,
                                          String description, String type, String purpose)
            throws MessageLoaderException {

        ParentFunctionalGroup existing = findPfgByName(parentName);
        if (existing != null) {
            // PFG already exists — just add the FG mapping if provided
            if (StringUtils.isNotBlank(functionalGroupName)) {
                linkFunctionalGroup(existing, functionalGroupName);
                pfgRepository.save(existing);
                log.info("PFG '{}' already exists; linked FG '{}'", parentName, functionalGroupName);
            } else {
                throw new MessageLoaderException(
                        "Add opCode: ParentFunctionalGroup '" + parentName + "' already exists.");
            }
            return;
        }

        validateRequiredFields(parentName, type, purpose);

        LocalDateTime now = LocalDateTime.now();
        String userId = getActiveUserId();

        ParentFunctionalGroup pfg = new ParentFunctionalGroup();
        pfg.setName(parentName);
        pfg.setDescription(StringUtils.defaultString(description));
        pfg.setType(type);
        pfg.setPurpose(purpose);
        pfg.setCreatedOn(now);
        pfg.setCreatedBy(userId);
        pfg.setLastChangedOn(now);
        pfg.setLastChangedBy(userId);

        pfgRepository.save(pfg); // flush happens automatically; Spring Data returns persisted entity with ID

        if (StringUtils.isNotBlank(functionalGroupName)) {
            linkFunctionalGroup(pfg, functionalGroupName);
            pfgRepository.save(pfg);
        }

        log.info("Created new ParentFunctionalGroup: '{}'", parentName);
    }

    // -----------------------------------------------------------------------
    // Update
    // -----------------------------------------------------------------------

    private void updateParentFunctionalGroup(String parentName, String functionalGroupName,
                                             String description, String type, String purpose)
            throws MessageLoaderException {

        ParentFunctionalGroup pfg = findPfgByName(parentName);
        if (pfg == null) {
            throw new MessageLoaderException(
                    "Update opCode: ParentFunctionalGroup '" + parentName + "' not found.");
        }

        LocalDateTime now = LocalDateTime.now();
        String userId = getActiveUserId();

        if (StringUtils.isNotBlank(description)) pfg.setDescription(description);
        if (StringUtils.isNotBlank(type))        pfg.setType(type);
        if (StringUtils.isNotBlank(purpose))     pfg.setPurpose(purpose);
        pfg.setLastChangedOn(now);
        pfg.setLastChangedBy(userId);

        if (StringUtils.isNotBlank(functionalGroupName)) {
            linkFunctionalGroup(pfg, functionalGroupName);
        }

        pfgRepository.save(pfg);
        log.info("Updated ParentFunctionalGroup: '{}'", parentName);
    }

    // -----------------------------------------------------------------------
    // Delete (remove FG → PFG mapping)
    // -----------------------------------------------------------------------

    private void deleteParentFunctionalGroupMapping(String parentName, String functionalGroupName)
            throws MessageLoaderException {

        ParentFunctionalGroup pfg = findPfgByName(parentName);
        if (pfg == null) {
            throw new MessageLoaderException(
                    "Delete opCode: ParentFunctionalGroup '" + parentName + "' not found.");
        }

        if (StringUtils.isBlank(functionalGroupName)) {
            throw new MessageLoaderException(
                    "Delete opCode requires FunctionalGroupName for ParentName=" + parentName);
        }

        FunctionalGroup fg = findFgByName(functionalGroupName);
        if (fg == null) {
            throw new MessageLoaderException(
                    "Delete opCode: FunctionalGroup '" + functionalGroupName + "' not found.");
        }

        boolean removed = pfg.getFunctionalGroups() != null && pfg.getFunctionalGroups().remove(fg);
        if (!removed) {
            throw new MessageLoaderException(
                    "Delete opCode: FunctionalGroup '" + functionalGroupName
                            + "' is not mapped to ParentFunctionalGroup '" + parentName + "'.");
        }

        pfg.setLastChangedOn(LocalDateTime.now());
        pfg.setLastChangedBy(getActiveUserId());
        pfgRepository.save(pfg);
        log.info("Removed FG '{}' from PFG '{}'", functionalGroupName, parentName);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private void linkFunctionalGroup(ParentFunctionalGroup pfg, String functionalGroupName)
            throws MessageLoaderException {
        FunctionalGroup fg = findFgByName(functionalGroupName);
        if (fg == null) {
            throw new MessageLoaderException(
                    "FunctionalGroup '" + functionalGroupName + "' not found.");
        }
        if (pfg.getFunctionalGroups() == null) {
            throw new MessageLoaderException(
                    "FunctionalGroups collection is null on PFG '" + pfg.getName() + "'.");
        }
        pfg.getFunctionalGroups().add(fg);
    }

    /**
     * Lookup ParentFunctionalGroup by name using JPA query.
     * Returns {@code null} if not found.
     */
    private ParentFunctionalGroup findPfgByName(String name) {
        return pfgRepository.getParentFunctionalGroupByName(name);
    }

    /**
     * Lookup FunctionalGroup by name using JPA query.
     * Returns {@code null} if not found.
     */
    private FunctionalGroup findFgByName(String name) {
        try {
            return fgRepository.getFunctionalGroupByName(name);
        } catch (Exception e) {
            log.error("Error looking up FunctionalGroup by name: {}", name, e);
            return null;
        }
    }

    private void validateRequiredFields(String parentName, String type, String purpose)
            throws MessageLoaderException {
        if (StringUtils.isBlank(type)) {
            throw new MessageLoaderException("Type is required for Add opCode, ParentName=" + parentName);
        }
        if (StringUtils.isBlank(purpose)) {
            throw new MessageLoaderException("Purpose is required for Add opCode, ParentName=" + parentName);
        }
    }

    private void handleBatchFlush(StopWatch batchTimer) {
        if (pfgCount % getBatchSize() == 0) {
            // Spring Data JPA flushes automatically within the transaction.
            if (log.isInfoEnabled()) {
                if (!batchTimer.isStopped() && !batchTimer.isSuspended()) {
                    batchTimer.split();
                    log.info("ParentFunctionalGroup processed so far: {} ({} ms)",
                            pfgCount, batchTimer.getSplitTime());
                }
            }
        }
    }

    private void handleRecordError(XMLStreamReader xmlr, MessageLoaderException e)
            throws javax.xml.stream.XMLStreamException {
        if (pfgErrors.containsKey(pfgCount)) {
            pfgErrors.put(pfgCount, pfgErrors.get(pfgCount) + "|" + e.getMessage());
        } else {
            pfgErrors.put(pfgCount, e.getMessage());
        }
        skipUntilEnd(xmlr, "ParentFunctionalGroup");
    }

    private void stopTimer(StopWatch timer) {
        if (timer.isStarted() && !timer.isStopped()) {
            timer.stop();
            log.info("ParentFunctionalGroupLoader completed in {} ms", timer.getTime());
        }
    }
}
