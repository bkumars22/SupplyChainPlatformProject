/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.parentFunctionalGroup.loader;

import com.scplatform.pcm.allocationAudit.entity.AllocationAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.FunctionalGroupAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.ParentFunctionalGroupAuditHistory;
import com.scplatform.pcm.allocationAudit.service.FunctionalGroupAuditService;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ParentFunctionalGroupConfigLoader extends BaseImporter {

    int parentFunctionalGroupConfig = 0;
    Map<Integer, String> errors = null;
    List<String> pfgList = new ArrayList<>();

    private final ParentFunctionalGroupRepository pfgRepository;
    private final FunctionalGroupRepository fgRepository;
    private final FunctionalGroupAuditService auditService;

    public ParentFunctionalGroupConfigLoader(LoadJobRepository loadJobRepository,
            LoadEventRepository loadEventRepository,
            PcmConfigUtil pcmConfigUtil,
            ParentFunctionalGroupRepository pfgRepository,
            FunctionalGroupRepository fgRepository,
            FunctionalGroupAuditService auditService) {
        super(loadJobRepository, loadEventRepository, pcmConfigUtil);
        this.pfgRepository = pfgRepository;
        this.fgRepository = fgRepository;
        this.auditService = auditService;
    }

    // -----------------------------------------------------------------------
    // BaseImporter abstract method implementations
    // -----------------------------------------------------------------------

    @Override
    public void updateStats(MessageLoaderStatus status) {
        status.setStatistic("parentFunctionalGroupConfig", parentFunctionalGroupConfig);
        status.setStatistic("LoadEvents", this.loadEventCount);
    }

    @Override
    public int getCount() {
        return parentFunctionalGroupConfig;
    }

    public int getParentFunctionalGroupConfig() {
        return parentFunctionalGroupConfig;
    }

    public void setParentFunctionalGroupConfig(int parentFunctionalGroupConfig) {
        this.parentFunctionalGroupConfig = parentFunctionalGroupConfig;
    }

    @Override
    public void process(XMLStreamReader xmlr, int passNumber) throws Exception {
        StopWatch batchTimer = new StopWatch();
        parentFunctionalGroupConfig = 0;
        errors = new LinkedHashMap<>();

        while (xmlr.hasNext()) {
            int event = xmlr.next();
            if (!xmlr.isStartElement() && !xmlr.isEndElement()) {
                continue;
            }

            if (log.isDebugEnabled() && xmlr.hasName()) {
                log.debug(getEventTypeString(event) + " " + xmlr.getLocalName());
            }

            if ("ParentFunctionalGroupConfigMessage".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    batchTimer.start();
                    parentFunctionalGroupConfig = 0;
                }
                if (xmlr.isEndElement()) {
                    if (log.isInfoEnabled()) {
                        batchTimer.stop();
                        log.info("Parent Functional Group Config processed: " + parentFunctionalGroupConfig + " ("
                                + batchTimer.getTime() + " ms)");
                    }
                }
            } else if ("ParentFunctionalGroupConfig".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    try {
                        parentFunctionalGroupConfig++;
                        loadParentFunctionalGroupConfig(xmlr);
                    } catch (MessageLoaderException e) {
                        if (errors.containsKey(parentFunctionalGroupConfig)) {
                            errors.put(parentFunctionalGroupConfig,
                                    errors.get(parentFunctionalGroupConfig) + "|" + e.getMessage());
                        } else {
                            errors.put(parentFunctionalGroupConfig, e.getMessage());
                        }
                        skipUntilEnd(xmlr, "ParentFunctionalGroupConfig");
                        continue;
                    }
                } else if (xmlr.isEndElement()) {
                    if (parentFunctionalGroupConfig % getBatchSize() == 0) {
                        if (log.isInfoEnabled()) {
                            batchTimer.split();
                            log.info("Parent Functional Group Config processed so far: "
                                    + parentFunctionalGroupConfig + " (" + batchTimer.getSplitTime() + " ms) "
                                    + (isCommitOnBatchEnabled() ? "committed" : ""));
                        }
                    }
                }
            }
        }

        if (errors.isEmpty()) {
            // transaction committed by caller (@Transactional on MessageLoader.load())
        } else {
            parentFunctionalGroupConfig = 0;
            // propagate errors so caller rolls back the transaction
            throw new MessageLoaderException(StringUtils.join(errors.values(), "\n"));
        }

        if (log.isInfoEnabled()) {
            log.info("Total Parent Functional Group Config processed:" + parentFunctionalGroupConfig);
            this.dumpCacheStats();
        }
    }

    // -----------------------------------------------------------------------
    // Record-level processing
    // -----------------------------------------------------------------------

    private void loadParentFunctionalGroupConfig(XMLStreamReader xmlr) throws MessageLoaderException {
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        ParentFunctionalGroup parentFunctionalGroup = null;
        String opCode = null;
        String parentName = null;
        if (xmlr.getAttributeValue(null, "OpCode") != null) {
            opCode = xmlr.getAttributeValue(null, "OpCode").trim().toUpperCase();
        }
        if (xmlr.getAttributeValue(null, "ParentName") != null) {
            parentName = xmlr.getAttributeValue(null, "ParentName").trim().replaceAll("( )+", " ");
        }
        if (opCode != null) {
            if (parentName != null) {
                if (opCode.equals("DELETE")) {
                    parentFunctionalGroup = pfgRepository.getParentFunctionalGroupByName(parentName);
                    if (parentFunctionalGroup != null) {
                        String oldPFGName = parentFunctionalGroup.getName();
                        List<FunctionalGroup> functionalGroupList =
                                fgRepository.getFunctionalGroupListByParent(parentFunctionalGroup.getParentFunctionalGroupId());
                        auditService.recordParentFunctionalGroupAudit(
                                activeUserId, getActiveUserRoleId(),
                                AllocationAuditHistory.ACTIONUPLOAD_DELETE,
                                ParentFunctionalGroupAuditHistory.OPERATION_DELETEPFG,
                                parentFunctionalGroup,
                                oldPFGName + " Parent Functional Group deleted of type " + parentFunctionalGroup.getType(),
                                timeStamp);
                        for (FunctionalGroup functionalGroup : functionalGroupList) {
                            auditService.recordFunctionalGroupAudit(
                                    activeUserId, getActiveUserRoleId(),
                                    AllocationAuditHistory.ACTIONUPLOAD_DELETE,
                                    FunctionalGroupAuditHistory.OPERATION_REMOVEPFG,
                                    functionalGroup,
                                    "Removed Parent Functional Group " + parentName + " from " + functionalGroup.getName(),
                                    timeStamp);
                        }
                        pfgRepository.deleteParentFunctionalGroup(parentFunctionalGroup);
                    } else {
                        log.error("Parent " + parentName + " does not exist");
                        throw new MessageLoaderException("Parent " + parentName + " does not exist");
                    }
                } else if (opCode.equals("RENAME")) {
                    String newName = null;
                    if (xmlr.getAttributeValue(null, "Name") != null) {
                        newName = StringUtils.trimToNull(xmlr.getAttributeValue(null, "Name")).trim().replaceAll("( )+", " ");
                    }
                    if (newName == null) {
                        log.error("New Parent Name shouldn't be Empty for Parent " + parentName);
                        throw new MessageLoaderException("New Parent Name shouldn't be Empty for Parent " + parentName);
                    }
                    if (newName != null) {
                        String validatePName = pcmConfigUtil.getString("pcm.parentfunctional.group.allowedCharacters.upload", null);
                        Pattern pattern = Pattern.compile(validatePName);
                        Matcher matcher = pattern.matcher(newName);
                        if (!matcher.matches()) {
                            List<Object> args = new ArrayList<>();
                            String allowedCharacters = SCPlatformMessages.INSTANCE
                                    .getMessage("parentfunctionalgroup.allowed.characters", null, null);
                            args.add(newName);
                            args.add(allowedCharacters);
                            String errorMsg = SCPlatformMessages.INSTANCE
                                    .getMessage("warn.error_wrong_data_value_for_pfg_loader", args.toArray(), null);
                            log.error(errorMsg);
                            throw new MessageLoaderException(errorMsg);
                        }
                    }

                    parentFunctionalGroup = pfgRepository.getParentFunctionalGroupByName(parentName);
                    ParentFunctionalGroup newParentFunctionalGroup = pfgRepository.getParentFunctionalGroupByName(newName);

                    if (pfgList != null && pfgList.contains(parentName.toUpperCase())) {
                        return;
                    } else {
                        if (parentFunctionalGroup == null) {
                            log.error("Parent " + parentName + " does not exist");
                            throw new MessageLoaderException("Parent " + parentName + " does not exist");
                        }

                        if (newParentFunctionalGroup != null) {
                            if (!errors.values().contains("Duplicate new name :  " + newName + " For PFG : " + parentName)) {
                                log.error("Duplicate new name :  " + newName + " For PFG : " + parentName);
                                throw new MessageLoaderException("Duplicate new name :  " + newName + " For PFG : " + parentName);
                            } else {
                                return;
                            }
                        }

                        String oldPFGName = parentFunctionalGroup.getName();
                        parentFunctionalGroup.setName(newName);
                        pfgList.add(parentName.toUpperCase());
                        parentFunctionalGroup = updateAllFunctionalGroupFlagForExistingParent(parentFunctionalGroup);
                        pfgRepository.updateParentFunctionalGroup(parentFunctionalGroup);
                        auditService.recordParentFunctionalGroupAudit(
                                activeUserId, getActiveUserRoleId(),
                                AllocationAuditHistory.ACTIONUPLOAD_UPDATE,
                                ParentFunctionalGroupAuditHistory.OPERATION_RENAMEPARENT,
                                parentFunctionalGroup,
                                "Parent Functional Group " + oldPFGName + " Renamed to " + parentFunctionalGroup.getName(),
                                timeStamp);
                    }
                }
            } else {
                log.error("Parent Name shouldn't be blank");
                throw new MessageLoaderException("Parent Name shouldn't be blank");
            }
        } else {
            log.error("Operation Code does not exist");
            throw new MessageLoaderException("OpCode shouldn't be blank");
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    public void updateAllFunctionalGroupFlag(ParentFunctionalGroup parentFunctionalGroup) {
        for (FunctionalGroup functionalGroup : parentFunctionalGroup.getFunctionalGroups()) {
            functionalGroup.setExtractFlag("P");
            fgRepository.save(functionalGroup);
        }
    }

    public ParentFunctionalGroup updateAllFunctionalGroupFlagForExistingParent(
            ParentFunctionalGroup parentFunctionalGroup) {
        for (FunctionalGroup functionalGroup : parentFunctionalGroup.getFunctionalGroups()) {
            functionalGroup.setExtractFlag("P");
        }
        return parentFunctionalGroup;
    }
}
