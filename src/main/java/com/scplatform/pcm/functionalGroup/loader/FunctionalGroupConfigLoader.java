/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.functionalGroup.loader;

import com.scplatform.pcm.allocationAudit.entity.AllocationAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.FunctionalGroupAuditHistory;
import com.scplatform.pcm.allocationAudit.service.FunctionalGroupAuditService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FunctionalGroupConfigLoader extends BaseImporter {

    int functionalGroupConfig = 0;
    Map<Integer, String> errors = null;
    List<String> fgList = new ArrayList<>();

    private final FunctionalGroupRepository fgRepository;
    private final FunctionalGroupAuditService auditService;

    public FunctionalGroupConfigLoader(LoadJobRepository loadJobRepository,
            LoadEventRepository loadEventRepository,
            PcmConfigUtil pcmConfigUtil,
            FunctionalGroupRepository fgRepository,
            FunctionalGroupAuditService auditService) {
        super(loadJobRepository, loadEventRepository, pcmConfigUtil);
        this.fgRepository = fgRepository;
        this.auditService = auditService;
    }

    // -----------------------------------------------------------------------
    // BaseImporter abstract method implementations
    // -----------------------------------------------------------------------

    @Override
    public void updateStats(MessageLoaderStatus status) {
        status.setStatistic("functionalGroupConfig", functionalGroupConfig);
        status.setStatistic("LoadEvents", this.loadEventCount);
    }

    @Override
    public int getCount() {
        return functionalGroupConfig;
    }

    public int getfunctionalGroupConfig() {
        return functionalGroupConfig;
    }

    public void setfunctionalGroupConfig(int functionalGroupConfig) {
        this.functionalGroupConfig = functionalGroupConfig;
    }

    // -----------------------------------------------------------------------
    // process() – main entry point called by MessageLoader
    // -----------------------------------------------------------------------

    @Override
    public void process(XMLStreamReader xmlr, int passNumber) throws Exception {
        StopWatch batchTimer = new StopWatch();
        functionalGroupConfig = 0;
        errors = new LinkedHashMap<>();

        while (xmlr.hasNext()) {
            int event = xmlr.next();
            if (!xmlr.isStartElement() && !xmlr.isEndElement()) {
                continue;
            }
            debugEventType(xmlr, event);

            if ("FunctionalGroupMessage".equals(xmlr.getLocalName())) {
                handleStartForFunctionalGroupMessage(xmlr, batchTimer);
                handleEndForFunctionalGroupMessage(xmlr, batchTimer);
            } else if ("FunctionalGroup".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    try {
                        handleFunctionalGroupStart(xmlr);
                    } catch (MessageLoaderException e) {
                        onFunctionalGroupStartError(xmlr, e);
                        continue;
                    }
                } else if (xmlr.isEndElement()) {
                    onFunctionalGroupEnd(batchTimer);
                }
            }
        }
        postProcess();
    }

    private void postProcess() throws MessageLoaderException {
        if (errors.isEmpty()) {
            // transaction committed by caller (@Transactional on MessageLoader.load())
        } else {
            postProcessErrors();
        }
        tracePostProcessing();
    }

    private void tracePostProcessing() {
        if (log.isInfoEnabled()) {
            log.info("Total Functional Group Config processed:" + functionalGroupConfig);
            this.dumpCacheStats();
        }
    }

    private void postProcessErrors() throws MessageLoaderException {
        functionalGroupConfig = 0;
        throw new MessageLoaderException(StringUtils.join(errors.values(), "\n"));
    }

    private void onFunctionalGroupEnd(StopWatch batchTimer) {
        if (functionalGroupConfig % getBatchSize() == 0) {
            if (log.isInfoEnabled()) {
                batchTimer.split();
                log.info("Functional Group Config processed so far: " + functionalGroupConfig + " ("
                        + batchTimer.getSplitTime() + " ms) "
                        + (isCommitOnBatchEnabled() ? "committed" : ""));
            }
        }
    }

    private void onFunctionalGroupStartError(XMLStreamReader xmlr, MessageLoaderException e)
            throws Exception {
        if (errors.containsKey(functionalGroupConfig)) {
            errors.put(functionalGroupConfig, errors.get(functionalGroupConfig) + "|" + e.getMessage());
        } else {
            errors.put(functionalGroupConfig, e.getMessage());
        }
        skipUntilEnd(xmlr, "FunctionalGroup");
    }

    private void handleFunctionalGroupStart(XMLStreamReader xmlr) throws MessageLoaderException {
        functionalGroupConfig++;
        loadfunctionalGroupConfig(xmlr);
    }

    private void handleEndForFunctionalGroupMessage(XMLStreamReader xmlr, StopWatch batchTimer) {
        if (xmlr.isEndElement()) {
            if (log.isInfoEnabled()) {
                batchTimer.stop();
                log.info("Functional Group Config processed: " + functionalGroupConfig
                        + " (" + batchTimer.getTime() + " ms)");
            }
        }
    }

    private void handleStartForFunctionalGroupMessage(XMLStreamReader xmlr, StopWatch batchTimer) {
        if (xmlr.isStartElement()) {
            batchTimer.start();
            functionalGroupConfig = 0;
        }
    }

    private void debugEventType(XMLStreamReader xmlr, int event) {
        if (log.isDebugEnabled() && xmlr.hasName()) {
            log.debug(getEventTypeString(event) + " " + xmlr.getLocalName());
        }
    }

    // -----------------------------------------------------------------------
    // Record-level processing
    // -----------------------------------------------------------------------

    private void loadfunctionalGroupConfig(XMLStreamReader xmlr) throws MessageLoaderException {
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        FunctionalGroup functionalGroup = null;
        String opCode = null;
        String name = null;
        opCode = getOpCode(xmlr, opCode);
        name = getFunctionalGroupName(xmlr, name);

        if (opCode != null) {
            if (name != null) {
                if (opCode.equals("RENAME")) {
                    String newName = null;
                    newName = getNewName(xmlr, newName);
                    throwErrorOnEmptyNewName(name, newName);
                    validateNewName(newName);
                    functionalGroup = fgRepository.getFunctionalGroupByName(name);
                    FunctionalGroup functionalGroupWithNewName = fgRepository.getFunctionalGroupByName(newName);

                    if (fgList != null && fgList.contains(name.toUpperCase())) {
                        return;
                    } else {
                        throwErrorWhenFunctionalGroupDoesNotExist(functionalGroup, name);
                        if (functionalGroupWithNewName != null) {
                            if (!errors.values().contains("Duplicate New Name " + newName + " For FG : " + name)) {
                                throwDuplicateNameError(name, newName);
                            } else {
                                return;
                            }
                        }
                        updateFunctionalGroup(timeStamp, functionalGroup, newName);
                    }
                }
            } else {
                functionalGroupDoesNotExist();
            }
        } else {
            operationCodeDoesNotExist();
        }
    }

    private void operationCodeDoesNotExist() throws MessageLoaderException {
        log.error("Operation Code does not exist");
        throw new MessageLoaderException("OpCode shouldn't be blank");
    }

    private void functionalGroupDoesNotExist() throws MessageLoaderException {
        log.error("Name does not exist");
        throw new MessageLoaderException("Functional name shouldn't be blank");
    }

    private void updateFunctionalGroup(Timestamp timeStamp, FunctionalGroup functionalGroup, String newName) {
        String oldFGName = updateFunctionalGroupAttributes(functionalGroup, newName);
        persist(timeStamp, functionalGroup, oldFGName);
    }

    private void persist(Timestamp timeStamp, FunctionalGroup functionalGroup, String oldFGName) {
        fgRepository.save(functionalGroup);
        auditService.recordFunctionalGroupAudit(
                activeUserId, getActiveUserRoleId(),
                AllocationAuditHistory.ACTIONUPLOAD_UPDATE,
                FunctionalGroupAuditHistory.OPERATION_RENAMEFG,
                functionalGroup,
                "Functional Group " + oldFGName + " Renamed to " + functionalGroup.getName(),
                timeStamp);
    }

    private String updateFunctionalGroupAttributes(FunctionalGroup functionalGroup, String newName) {
        String oldFGName = functionalGroup.getName();
        fgList.add(oldFGName.toUpperCase());
        functionalGroup.setName(newName);
        functionalGroup.setExtractFlag("P");
        functionalGroup.setLastChangedBy(activeUserId);
        functionalGroup.setLastChangedOn(new Date());
        functionalGroup.setStatusChangedBy(activeUserId);
        return oldFGName;
    }

    private void throwDuplicateNameError(String name, String newName) throws MessageLoaderException {
        log.error("Duplicate New Name " + newName + " For FG : " + name);
        throw new MessageLoaderException("Duplicate New Name " + newName + " For FG : " + name);
    }

    private void throwErrorWhenFunctionalGroupDoesNotExist(FunctionalGroup functionalGroup, String name)
            throws MessageLoaderException {
        if (functionalGroup == null) {
            log.error("Functional Group " + name + " does not exist");
            throw new MessageLoaderException("Functional Group " + name + " does not exist");
        }
    }

    private void throwErrorOnEmptyNewName(String name, String newName) throws MessageLoaderException {
        if (newName == null) {
            log.error("Functional Group New name for Functional group" + name + " shouldn't be Empty");
            throw new MessageLoaderException(
                    "Functional Group New name for Functional group" + name + " shouldn't be Empty");
        }
    }

    private String getNewName(XMLStreamReader xmlr, String newName) {
        if (xmlr.getAttributeValue(null, "value") != null) {
            newName = StringUtils.trimToNull(xmlr.getAttributeValue(null, "value"))
                    .trim().replaceAll("( )+", " ");
        }
        return newName;
    }

    private String getFunctionalGroupName(XMLStreamReader xmlr, String name) {
        if (xmlr.getAttributeValue(null, "functionalGroupName") != null) {
            name = xmlr.getAttributeValue(null, "functionalGroupName").trim().replaceAll("( )+", " ");
        }
        return name;
    }

    private String getOpCode(XMLStreamReader xmlr, String opCode) {
        if (xmlr.getAttributeValue(null, "opCode") != null) {
            opCode = xmlr.getAttributeValue(null, "opCode").trim().toUpperCase();
        }
        return opCode;
    }

    private void validateNewName(String newName) throws MessageLoaderException {
        if (newName != null) {
            String validateFName = pcmConfigUtil.getString("pcm.functional.group.allowedCharacters.upload", null);
            if (validateFName != null) {
                Pattern pattern = Pattern.compile(validateFName);
                Matcher matcher = pattern.matcher(newName);
                if (!matcher.matches()) {
                    List<Object> args = new ArrayList<>();
                    String allowedCharacters = SCPlatformMessages.INSTANCE
                            .getMessage("functionalgroup.allowed.characters", null, null);
                    args.add(newName);
                    args.add(allowedCharacters);
                    String errorMsg = SCPlatformMessages.INSTANCE
                            .getMessage("warn.error_wrong_data_value_for_fg_loader", args.toArray(), null);
                    log.error(errorMsg);
                    throw new MessageLoaderException(errorMsg);
                }
            }
        }
    }
}
