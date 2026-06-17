/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.functionalGroup.loader;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.scplatform.pcm.allocationAudit.entity.AllocationAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.FunctionalGroupAuditHistory;
import com.scplatform.pcm.allocationAudit.repository.AllocationAuditHistoryRepository;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.repository.BusinessEntityRepository;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.functionalGroup.constant.FunctionalGroupConstants;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroupLob;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.repository.ItemRepository;
import com.scplatform.pcm.upload.loader.BaseImporter;
import com.scplatform.pcm.upload.loader.MessageLoaderException;
import com.scplatform.pcm.upload.loader.MessageLoaderStatus;
import com.scplatform.pcm.upload.repository.LoadEventRepository;
import com.scplatform.pcm.upload.repository.LoadJobRepository;
import com.scplatform.pcm.util.common.InterconnectConstants;
import com.scplatform.pcm.util.message.SCPlatformMessages;

import jakarta.persistence.NonUniqueResultException;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FunctionalGroupLoader extends BaseImporter {

    private final FunctionalGroupRepository       fgRepository;
    private final ItemRepository                  itemRepository;
    private final BusinessEntityRepository        businessEntityRepository;
    private final AllocationAuditHistoryRepository auditRepository;

    public static final String KEY_FG_CFG_ADDITEM    = "FGItemAdd";
    public static final String KEY_FG_CFG_DELETEITEM = "FGItemDelete";
    public static final String KEY_FG_CFG_UPDATE     = "FGUpdateItem";
    public static final String KEY_FG_CFG_RENAME     = "FGRenameItem";
    public static final String KEY_FG_CFG_ACTIVATE   = "FGCFGActivate";
    public static final String KEY_FG_CFG_INACTIVE   = "FGCFGInactivate";
    public static final String KEY_FG_ACTIVATE       = "FGActivate";
    public static final String KEY_FG_DEACTIVATE     = "FGDeactivate";

    int functionalGroupCount = 0;
    Map<Integer, String> fgErrors = null;
    Map<Integer, String> errorForDownload = null;
    Map<FunctionalGroup, Set<Item>> fgItemMap = null;
    Map<FunctionalGroup, Map<Item, Integer>> fgItemToRemove = new HashMap<>();
    Map<FunctionalGroup, Item> fgAndGenericItemMap = new HashMap<>();
    boolean userItemIntegrationEnabled;
    Timestamp timeStamp = null;
    Set<String> fgSiteKeys = null;
    Set<FunctionalGroup> fgToBeReactivated = null;
    Map<FunctionalGroup, Integer> fgIdSerialCountMap = null;
    Map<String, String> itemFunctionalGroupMap = null;
    String fromEmail;
    String altMaverickName;
    boolean errorExists = false;
    int countOfGoodRecords = 0;

    public FunctionalGroupLoader(LoadJobRepository loadJobRepository,
                                   LoadEventRepository loadEventRepository,
                                   PcmConfigUtil pcmConfigUtil,
                                   FunctionalGroupRepository fgRepository,
                                   ItemRepository itemRepository,
                                   BusinessEntityRepository businessEntityRepository,
                                   AllocationAuditHistoryRepository auditRepository) {
        super(loadJobRepository, loadEventRepository, pcmConfigUtil);
        this.fgRepository             = fgRepository;
        this.itemRepository           = itemRepository;
        this.businessEntityRepository = businessEntityRepository;
        this.auditRepository          = auditRepository;
    }

    // -----------------------------------------------------------------------
    // BaseImporter abstract method implementations
    // -----------------------------------------------------------------------

    @Override
    public void updateStats(MessageLoaderStatus status) {
        status.setStatistic("functionalGroup", functionalGroupCount);
        status.setStatistic("LoadEvents", this.loadEventCount);
    }

    @Override
    public int getCount() {
        return functionalGroupCount;
    }

    // -----------------------------------------------------------------------
    // process() — main entry point called by MessageLoader
    // Transaction is managed externally by @Transactional on MessageLoader.load()
    // -----------------------------------------------------------------------

    @Override
    public void process(XMLStreamReader xmlr, int passNumber) throws Exception {
        StopWatch batchTimer = new StopWatch();
        functionalGroupCount = 0;
        fgIdSerialCountMap = new HashMap<>();
        fgErrors = new LinkedHashMap<>();
        errorForDownload = new LinkedHashMap<>();
        fgItemMap = new LinkedHashMap<>();
        fgSiteKeys = new HashSet<>();
        fgToBeReactivated = new LinkedHashSet<>();
        fgItemToRemove = new HashMap<>();
        fgAndGenericItemMap = new HashMap<>();
        itemFunctionalGroupMap = new HashMap<>();
        countOfGoodRecords = 0;
        errorExists = false;
        timeStamp = new Timestamp(System.currentTimeMillis());

        userItemIntegrationEnabled = pcmConfigUtil.getBoolean(
                "scplatform.feature.enable.userItemType.CFG.Integration", false);
        fromEmail = pcmConfigUtil.getString("pcm.fgupload.alert.fromEmail", "noreply@scplatform.local");
        altMaverickName = pcmConfigUtil.getString("pcm.functionalGroup.aliasName.xmlAttr", "ODWName");

        log.info("FunctionalGroupLoader: starting process pass={}", passNumber);

        try {
            while (xmlr.hasNext()) {
                int event = xmlr.next();
                if (!xmlr.isStartElement() && !xmlr.isEndElement()) {
                    continue;
                }
                logXmlEventType(xmlr, event);

                if ("FunctionalGroupMessage".equals(xmlr.getLocalName())) {
                    handleStartForFunctionalGroupMessage(xmlr, batchTimer);
                    if (xmlr.isEndElement()) {
                        handleEndForFunctionalGroupMessage(batchTimer);
                    }
                } else if ("FunctionalGroup".equals(xmlr.getLocalName())) {
                    if (xmlr.isStartElement()) {
                        try {
                            functionalGroupCount++;
                            errorExists = false;
                            loadFunctionalGroup(xmlr);
                        } catch (MessageLoaderException e) {
                            whenFunctionalGroupLoaderFails(xmlr, e);
                            continue;
                        }
                    } else if (xmlr.isEndElement()) {
                        handleEndForFunctionalGroup(xmlr, batchTimer);
                    }
                }
            }

            // Post-processing
            if (!fgErrors.isEmpty()) {
                postProcessErrors();
            }

        } catch (Exception e) {
            log.error("FunctionalGroupLoader: fatal error during processing", e);
            throw e;
        }

        logFGProcessedCount();

        // Propagate per-record errors to BaseImporter.errors so MessageLoader reports them
        if (!fgErrors.isEmpty()) {
            for (Map.Entry<Integer, String> entry : fgErrors.entrySet()) {
                super.errors.add("Row " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    // -----------------------------------------------------------------------
    // XML Event helpers
    // -----------------------------------------------------------------------

    private void logXmlEventType(XMLStreamReader xmlr, int event) {
        if (log.isDebugEnabled() && xmlr.hasName()) {
            log.debug("{} {}", getEventTypeString(event), xmlr.getLocalName());
        }
    }

    private void logFGProcessedCount() {
        if (log.isInfoEnabled()) {
            log.info("Total Functional Group processed: {}", functionalGroupCount);
            this.dumpCacheStats();
        }
    }

    private void handleStartForFunctionalGroupMessage(XMLStreamReader xmlr, StopWatch batchTimer) {
        if (xmlr.isStartElement()) {
            batchTimer.start();
            functionalGroupCount = 0;
        }
    }

    private void handleEndForFunctionalGroupMessage(StopWatch batchTimer) throws MessageLoaderException {
        processFgItemMap();
        handleItemDeletions();
        handleFunctionalGroupReactivations();
        stopTimer(batchTimer);
    }

    private void handleEndForFunctionalGroup(XMLStreamReader xmlr, StopWatch batchTimer) throws Exception {
        if (functionalGroupCount % getBatchSize() == 0) {
            if (log.isInfoEnabled()) {
                batchTimer.split();
                log.info("Functional Group processed so far: {} ({} ms) {}",
                        functionalGroupCount, batchTimer.getSplitTime(),
                        isCommitOnBatchEnabled() ? "flushed" : "");
            }
        }
    }

    private void whenFunctionalGroupLoaderFails(XMLStreamReader xmlr, MessageLoaderException e)
            throws XMLStreamException {
        if (fgErrors.containsKey(functionalGroupCount)) {
            fgErrors.put(functionalGroupCount, fgErrors.get(functionalGroupCount) + "|" + e.getMessage());
        } else {
            fgErrors.put(functionalGroupCount, e.getMessage());
        }
        skipUntilEnd(xmlr, "FunctionalGroup");
    }

    private void stopTimer(StopWatch batchTimer) {
        if (log.isInfoEnabled()) {
            batchTimer.stop();
            log.info("Functional Group processed: {} ({} ms)", functionalGroupCount, batchTimer.getTime());
        }
    }

    // -----------------------------------------------------------------------
    // Post-processing: error reporting
    // -----------------------------------------------------------------------

    private void postProcessErrors() throws MessageLoaderException {
        if (!fgErrors.isEmpty()) {
            functionalGroupCount = 0;
            String fullPathToFile = inputFileName;
            String extractedFileName = (fullPathToFile != null)
                    ? fullPathToFile.substring(fullPathToFile.lastIndexOf("/") + 1) : "unknown";
            int xmlIndex = extractedFileName.indexOf(".xml");
            String cleanFileName = (xmlIndex != -1) ? extractedFileName.substring(0, xmlIndex + 4) : extractedFileName;

            // TODO: Generate error spreadsheet when FunctionalGroupErrorSSWritter is migrated
            // FunctionalGroupErrorSSWritter.generateSheet(loadProps.get("filename"), errorForDownload);

            // TODO: Send error email when EmailUtil is migrated
            String toEmail = pcmConfigUtil.getString("pcm.fgupload.alert.toEmail", null);
            if (toEmail == null || toEmail.isEmpty()) {
                log.warn("Email address not found for FG upload error notification");
            } else {
                log.info("FG upload errors exist for file '{}'. Email notification would be sent to: {}",
                        cleanFileName, toEmail);
            }

            // The exception causes MessageLoader to set ERROR status and the
            // @Transactional to rollback the transaction
            throw new MessageLoaderException(StringUtils.join(fgErrors.values(), "\n"));
        }
    }

    // -----------------------------------------------------------------------
    // FunctionalGroup reactivation handling
    // -----------------------------------------------------------------------

    private void handleFunctionalGroupReactivations() throws MessageLoaderException {
        if (!fgToBeReactivated.isEmpty()) {
            for (FunctionalGroup fgToReactive : fgToBeReactivated) {
                try {
                    // TODO: TAM validation and allocation logic (requires FiscalPeriodUtil)
                    // For now, just activate the FG
                    fgToReactive.setStatus(FunctionalGroupConstants.STATUS_ACTIVE);
                    fgRepository.save(fgToReactive);
                    log.info("Reactivated FunctionalGroup: '{}'", fgToReactive.getName());
                } catch (Exception e) {
                    String errorMessage = "Error reactivating Functional Group: " + fgToReactive.getName()
                            + " - " + e.getMessage();
                    log.error(errorMessage, e);
                    Integer serialCount = fgIdSerialCountMap.get(fgToReactive);
                    if (serialCount != null) {
                        if (fgErrors.containsKey(serialCount)) {
                            fgErrors.put(serialCount, fgErrors.get(serialCount) + "|" + errorMessage);
                        } else {
                            fgErrors.put(serialCount, errorMessage);
                        }
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Item deletion handling
    // -----------------------------------------------------------------------

    private void handleItemDeletions() {
        if (!fgItemToRemove.isEmpty()) {
            for (Map.Entry<FunctionalGroup, Map<Item, Integer>> entry : fgItemToRemove.entrySet()) {
                FunctionalGroup fg = entry.getKey();
                int fgCount = fgIdSerialCountMap.getOrDefault(fg, 0);
                try {
                    for (Item item : entry.getValue().keySet()) {
                        Set<Item> items = fg.getFunctionalGroupItems();
                        if (items != null && items.remove(item)) {
                            log.debug("Removed item '{}' from FG '{}'", item.getItemNumber(), fg.getName());
                            recordAuditWithItem(FunctionalGroupAuditHistory.OPERATION_REMOVEITEM,
                                    AllocationAuditHistory.ACTIONUPLOAD_DELETE, fg, item,
                                    "Item removed from FunctionalGroup via upload");
                        }
                    }
                    // If last item removed, deactivate FG
                    if (fg.getFunctionalGroupItems() == null || fg.getFunctionalGroupItems().isEmpty()) {
                        fg.setStatus(FunctionalGroupConstants.STATUS_INACTIVE);
                        fg.setLastChangedBy(activeUserId);
                        fg.setLastChangedOn(new Date());
                        fgRepository.save(fg);

                        recordAudit(FunctionalGroupAuditHistory.OPERATION_DEACTIVATE,
                                AllocationAuditHistory.ACTIONUPLOAD_UPDATE, fg, null,
                                "FG deactivated - last item removed");
                    } else {
                        fgRepository.save(fg);
                    }
                } catch (Exception e) {
                    if (e.getMessage() != null && !e.getMessage().equals("IGNORE") && !e.getMessage().isBlank()) {
                        if (fgErrors.containsKey(fgCount)) {
                            fgErrors.put(fgCount, fgErrors.get(fgCount) + "|" + e.getMessage()
                                    + " Functional Group: " + fg.getName());
                        } else {
                            fgErrors.put(fgCount, e.getMessage() + " Functional Group: " + fg.getName());
                        }
                    }
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // TAM allocation creation (post-process for ADD operations)
    // -----------------------------------------------------------------------

    private void processFgItemMap() throws MessageLoaderException {
        if (!fgItemMap.isEmpty()) {
            for (FunctionalGroup fg : fgItemMap.keySet()) {
                try {
                    // TODO: TAM allocation creation logic (requires FiscalPeriodUtil, TAMAllocation entities)
                    log.info("TODO: Create TAM allocation for FG '{}' with {} new items",
                            fg.getName(), fgItemMap.get(fg).size());
                } catch (Exception e) {
                    log.error("Error on creating allocation for Functional group: {}", fg.getName());
                    throw new MessageLoaderException(
                            "Error on creating allocation for Functional group: " + fg.getName());
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Per-element processing
    // -----------------------------------------------------------------------

    private void loadFunctionalGroup(XMLStreamReader xmlr) throws MessageLoaderException {
        timeStamp = new Timestamp(System.currentTimeMillis());
        String maverickName = null;
        String opCode = getAttr(xmlr, "opCode");
        String itemIdentifier = getAttr(xmlr, "item");
        String itemType = getAttr(xmlr, "itemType");
        String platformName = getAttr(xmlr, "platform");
        String lobName = getAttr(xmlr, "lob");
        String functionalGroupName = getAttr(xmlr, "functionalGroupName");
        String description = getAttrUpperCase(xmlr, "description");
        String type = getAttrUpperCase(xmlr, "type");
        String status = getAttrUpperCase(xmlr, "status");
        String functionalGroupExternalId = getAttr(xmlr, "functionalGroupId");
        String itemBusinessName = getAttr(xmlr, "itemBusinessName");
        String parentItemNumber = getAttr(xmlr, "parentItemNumber");
        String parentItemBusinessName = getAttr(xmlr, "parentItemBusinessName");
        String odmPartNumber = getAttr(xmlr, "ODMPartNumber");
        String odmPartBusinessName = getAttr(xmlr, "ODMPartBusinessName");
        String userItemType = getAttr(xmlr, "userItemType");

        // Read alias name using configurable XML attribute name
        String maverickNameAttr = pcmConfigUtil.getString("pcm.functionalGroup.aliasName.xmlAttr", "ODWName");
        maverickName = getAttr(xmlr, maverickNameAttr);

        // Normalize spaces in certain fields (matching legacy behavior)
        if (functionalGroupName != null) functionalGroupName = functionalGroupName.replaceAll("( )+", " ");
        if (platformName != null) platformName = platformName.replaceAll("( )+", " ");
        if (lobName != null) lobName = lobName.replaceAll("( )+", " ");
        if (description != null) description = description.replaceAll("( )+", " ");

        // Normalize opCode
        if (opCode != null) opCode = opCode.toUpperCase();

        String userId = activeUserId;
        String roleId = getActiveUserRoleId(); // Role lookup deferred

        // Resolve itemBusinessName via BusinessEntity lookup
        if (itemBusinessName != null) {
            String beType = (itemType != null && itemType.equalsIgnoreCase("Item")) ? "ENTERPRISE" : "SUPPLIER";
            log.debug("Resolving BE: raw beName='{}', beType='{}'", itemBusinessName, beType);
            BusinessEntity be = findBusinessEntity(itemBusinessName, beType);
            if (be != null) {
                log.debug("BE resolved: '{}' -> '{}'", itemBusinessName, be.getBusinessEntityName());
                itemBusinessName = be.getBusinessEntityName();
            } else {
                log.debug("BE not found for name='{}', keeping raw value", itemBusinessName);
            }
        }

        processFunctionalGroupAccordingToOperationCode(opCode, itemIdentifier, itemType, platformName,
                functionalGroupName, description, type, status, userId, roleId, itemBusinessName, lobName,
                parentItemNumber, parentItemBusinessName, odmPartNumber, odmPartBusinessName,
                userItemType, maverickName, functionalGroupExternalId, xmlr);
    }

    // -----------------------------------------------------------------------
    // Action dispatching
    // -----------------------------------------------------------------------

    private void processFunctionalGroupAccordingToOperationCode(String opCode, String itemIdentifier,
            String itemType, String platformName, String functionalGroupName, String description,
            String type, String status, String userId, String roleId, String itemBusinessName,
            String lobName, String parentItemNumber, String parentItemBusinessName,
            String odmPartNumber, String odmPartBusinessName, String userItemType,
            String maverickName, String functionalGroupExternalId, XMLStreamReader xmlr)
            throws MessageLoaderException {
        if (opCode != null) {
            whenOperationCodeIsGiven(opCode, itemIdentifier, itemType, platformName, functionalGroupName,
                    description, type, status, userId, roleId, itemBusinessName, lobName,
                    parentItemNumber, parentItemBusinessName, odmPartNumber, odmPartBusinessName,
                    userItemType, maverickName, functionalGroupExternalId, xmlr);
        } else {
            whenOperationCodeDoesNotExist();
        }
    }

    private void whenOperationCodeIsGiven(String opCode, String itemIdentifier, String itemType,
            String platformName, String functionalGroupName, String description, String type,
            String status, String userId, String roleId, String itemBusinessName, String lobName,
            String parentItemNumber, String parentItemBusinessName, String odmPartNumber,
            String odmPartBusinessName, String userItemType, String maverickName,
            String functionalGroupExternalId, XMLStreamReader xmlr) throws MessageLoaderException {

        FunctionalGroup fg = null;
        if (functionalGroupName != null || functionalGroupExternalId != null) {
            if (functionalGroupName != null) {
                functionalGroupName = functionalGroupName.trim();
            }

            // Resolve FG by external ID or by name+type
            if (pcmConfigUtil.getBoolean("scplatform.feature.enable.store.functionalGroupExternalId", false)
                    && (userId != null && (userId.equalsIgnoreCase("BATCH-CFG-OPP") || userId.equalsIgnoreCase("BATCH")))) {
                if (functionalGroupExternalId != null) {
                    functionalGroupExternalId = functionalGroupExternalId.trim();
                    fg = findFgByExternalId(functionalGroupExternalId);
                } else {
                    String errorMessage = getMessage("error.functionalGroup.externalId.mandatory",
                            new Object[]{functionalGroupName});
                    log.error(errorMessage);
                    throw new MessageLoaderException(errorMessage);
                }
            } else {
                fg = findFgByNameAndType(functionalGroupName, type);
            }

            if (fg != null) {
                fgIdSerialCountMap.put(fg, this.functionalGroupCount);
            }

            Item item = null;
            String platform = platformName;

            switch (opCode) {
                case "ADD":
                    handleAdd(fg, itemIdentifier, itemType, itemBusinessName, functionalGroupName,
                            description, type, userId, roleId, platform, lobName, parentItemNumber,
                            parentItemBusinessName, odmPartNumber, odmPartBusinessName, userItemType,
                            maverickName, functionalGroupExternalId, xmlr);
                    break;
                case "DELETE":
                    handleDelete(fg, itemIdentifier, itemType, itemBusinessName, functionalGroupName,
                            maverickName, xmlr);
                    break;
                case "UPDATE":
                    validateLob(lobName, type);
                    validatePlatform(platformName, type, maverickName);
                    validateFgType(type, maverickName, xmlr);
                    if (maverickName != null && !maverickName.isEmpty()) {
                        validateAliasNameLength(functionalGroupName, itemIdentifier, userItemType, maverickName);
                    }
                    handleUpdate(fg, itemIdentifier, itemType, platformName, description, type, status,
                            userId, roleId, itemBusinessName, lobName, parentItemNumber, parentItemBusinessName,
                            odmPartNumber, odmPartBusinessName, maverickName, functionalGroupExternalId,
                            functionalGroupName, xmlr);
                    break;
                case "ACTIVATE":
                    handleActivate(fg, functionalGroupName, type);
                    break;
                case "INACTIVATE":
                case "DEACTIVATE":
                    handleDeactivate(fg, functionalGroupName, type);
                    break;
                case "RENAME":
                    handleRename(fg, functionalGroupName, type, description, maverickName);
                    break;
                default:
                    log.warn("Unknown opCode '{}' for FG '{}'", opCode, functionalGroupName);
                    fgErrors.put(functionalGroupCount, "Unknown opCode: " + opCode);
            }
        } else {
            fgDoesNotExistError();
        }
    }

    // -----------------------------------------------------------------------
    // ADD operation
    // -----------------------------------------------------------------------

    private void handleAdd(FunctionalGroup fg, String itemIdentifier, String itemType,
            String itemBusinessName, String functionalGroupName, String description, String type,
            String userId, String roleId, String platform, String lobName, String parentItemNumber,
            String parentItemBusinessName, String odmPartNumber, String odmPartBusinessName,
            String userItemType, String maverickName, String functionalGroupExternalId,
            XMLStreamReader xmlr) throws MessageLoaderException {
        try {
            if (itemType == null) {
                emptyItemTypeError();
                return;
            }
            if (itemBusinessName == null) {
                emptyItemBusinessNameError();
                return;
            }
            if (itemIdentifier == null) {
                return;
            }

            itemType = initItemType(itemType);
            validateItemTypeForCfg(itemType, type, maverickName);

            Item item = findItemByIdentifierTypeAndBE(itemIdentifier, itemType, itemBusinessName);
            if (item == null) {
                emptyItemError(itemIdentifier, itemType, itemBusinessName, functionalGroupName, type, maverickName);
                return;
            }

            validateLob(lobName, type);
            validatePlatform(platform, type, maverickName);
            validateFgType(type, maverickName, xmlr);

            if (userItemType != null && userItemIntegrationEnabled) {
                validateUserItemType(item, userItemType);
            }

            if (maverickName != null && !maverickName.isEmpty()) {
                validateAliasNameLength(functionalGroupName, itemIdentifier, userItemType, maverickName);
            }

            onFunctionalGroupAdd(fg, itemIdentifier, functionalGroupName, description, type, userId,
                    roleId, item, platform, lobName, parentItemNumber, parentItemBusinessName,
                    odmPartNumber, odmPartBusinessName, userItemType, maverickName,
                    functionalGroupExternalId, xmlr);

        } catch (NonUniqueResultException n) {
            log.error("Load problem caught - non-unique item", n);
            String message = getMessage("error.fg_no_unique_item",
                    new Object[]{itemIdentifier, itemType, itemBusinessName});
            throw new MessageLoaderException(message);
        }
    }

    private void onFunctionalGroupAdd(FunctionalGroup fg, String itemIdentifier,
            String functionalGroupName, String description, String type, String userId,
            String roleId, Item item, String platform, String lob, String parentItemNumber,
            String parentItemBusinessName, String odmPartNumber, String odmPartBusinessName,
            String userItemType, String maverickName, String functionalGroupExternalId,
            XMLStreamReader xmlr) throws MessageLoaderException {

        // Check item ownership
        checkIfSelectedItemHasDefinedOwnership(item, functionalGroupName, type, maverickName);

        if (fg == null && item != null) {
            // Creating new FunctionalGroup
            validateFunctionalGroupName(functionalGroupName, itemIdentifier, type, maverickName, xmlr);
            validateItemPresentInMultipleFGs(itemIdentifier, functionalGroupName, type, maverickName, xmlr);
            itemPresentInOtherFunctionalGroupValidation(itemIdentifier, type, item, maverickName);

            if (!errorExists) {
                fg = createNewFunctionalGroup(functionalGroupName, description, type, item, platform,
                        lob, parentItemNumber, parentItemBusinessName, odmPartNumber, odmPartBusinessName,
                        maverickName, functionalGroupExternalId);
                fg = fgRepository.save(fg);

                if (fg.getFunctionalGroupExternalId() == null && fg.getFunctionalGroupId() != null) {
                    fg.setFunctionalGroupExternalId(fg.getFunctionalGroupId().toString());
                }

                recordAudit(FunctionalGroupAuditHistory.OPERATION_CREATEFG,
                        AllocationAuditHistory.ACTIONUPLOAD_CREATE, fg, null,
                        "FunctionalGroup created via upload");
                recordAuditWithItem(FunctionalGroupAuditHistory.OPERATION_ADDITEM,
                        AllocationAuditHistory.ACTIONUPLOAD_UPDATE, fg, item,
                        "Item added to new FunctionalGroup via upload");
                ++countOfGoodRecords;
            }

        } else if (fg != null && item != null) {
            // Adding item to existing FG
            String config = pcmConfigUtil.getString(
                    "pcm.functional.group.to.item.relationship." + type.trim(), FunctionalGroup.MANY_TO_MANY);
            Set<Item> items = fg.getFunctionalGroupItems();

            if (!errorExists) {
                itemPresentInOtherFGValidation(fg, itemIdentifier, item, config, type, maverickName, xmlr);
                createMapForTamAllocationCreation(fg, item, items);
                addItemsToExistingFG(fg, item, items);
                fgRepository.save(fg);

                recordAuditWithItem(FunctionalGroupAuditHistory.OPERATION_ADDITEM,
                        AllocationAuditHistory.ACTIONUPLOAD_UPDATE, fg, item,
                        "Item added to FunctionalGroup via upload");
                ++countOfGoodRecords;
            }
        }
    }

    // -----------------------------------------------------------------------
    // DELETE operation
    // -----------------------------------------------------------------------

    private void handleDelete(FunctionalGroup fg, String itemIdentifier, String itemType,
            String itemBusinessName, String functionalGroupName, String maverickName,
            XMLStreamReader xmlr) throws MessageLoaderException {
        try {
            Item item = null;
            if (itemType == null) {
                emptyItemTypeError();
                return;
            }
            if (itemBusinessName == null) {
                emptyItemBusinessNameError();
                return;
            }
            if (itemIdentifier != null) {
                itemType = initItemType(itemType);
                item = findItemByIdentifierTypeAndBE(itemIdentifier, itemType, itemBusinessName);
            }
            onFunctionalGroupDelete(fg, itemIdentifier, functionalGroupName, item, itemType,
                    itemBusinessName, maverickName, xmlr);
        } catch (NonUniqueResultException n) {
            log.error("Load problem caught - non-unique item", n);
            String message = getMessage("error.fg_no_unique_item",
                    new Object[]{itemIdentifier, itemType, itemBusinessName});
            throw new MessageLoaderException(message);
        }
    }

    private void onFunctionalGroupDelete(FunctionalGroup fg, String itemIdentifier,
            String functionalGroupName, Item item, String itemType, String itemBusinessName,
            String maverickName, XMLStreamReader xmlr) throws MessageLoaderException {
        if (item != null && fg != null) {
            updateItemsToBeRemoved(fg, itemIdentifier, functionalGroupName, item, maverickName, xmlr);
        } else {
            handleItemDeleteErrors(fg, item, itemIdentifier, itemType, itemBusinessName, maverickName, xmlr);
        }
    }

    private void updateItemsToBeRemoved(FunctionalGroup fg, String itemIdentifier,
            String functionalGroupName, Item item, String maverickName, XMLStreamReader xmlr)
            throws MessageLoaderException {
        if (fg.getFunctionalGroupItems() == null || !fg.getFunctionalGroupItems().contains(item)) {
            throw new MessageLoaderException(
                    "Functional Group " + functionalGroupName + " doesn't contain item " + itemIdentifier
                            + " " + altMaverickName + ":" + maverickName);
        } else {
            if (fgItemToRemove.containsKey(fg)) {
                fgItemToRemove.get(fg).put(item, this.functionalGroupCount);
            } else {
                Map<Item, Integer> itemRowMap = new HashMap<>();
                itemRowMap.put(item, this.functionalGroupCount);
                fgItemToRemove.put(fg, itemRowMap);
            }
        }
    }

    private void handleItemDeleteErrors(FunctionalGroup fg, Item item, String itemIdentifier,
            String itemType, String itemBusinessName, String maverickName, XMLStreamReader xmlr)
            throws MessageLoaderException {
        functionalGroupDoesNotExistError(fg, maverickName, xmlr);
        if (item == null) {
            emptyItemError(itemIdentifier, itemType, itemBusinessName,
                    fg != null ? fg.getName() : "unknown", fg != null ? fg.getType() : "unknown", maverickName);
        }
    }

    // -----------------------------------------------------------------------
    // UPDATE operation
    // -----------------------------------------------------------------------

    private void handleUpdate(FunctionalGroup fg, String itemIdentifier, String itemType,
            String platformName, String description, String type, String status, String userId,
            String roleId, String itemBusinessName, String lobName, String parentItemNumber,
            String parentItemBusinessName, String odmPartNumber, String odmPartBusinessName,
            String maverickName, String functionalGroupExternalId, String functionalGroupName,
            XMLStreamReader xmlr) throws MessageLoaderException {

        if (fg != null) {
            Item item = null;
            if (itemIdentifier != null) {
                if (itemType == null) {
                    itemTypeAbsentWhenItemPresentError(fg, maverickName);
                    return;
                } else if (itemBusinessName == null) {
                    emptyItemBusinessNameError();
                    return;
                } else {
                    itemType = initItemType(itemType);
                    validateItemTypeForCfg(itemType, type, maverickName);
                    item = findItemByIdentifierTypeAndBE(itemIdentifier, itemType, itemBusinessName);
                }
            }

            if (item != null && fg.getFunctionalGroupItems() != null
                    && !fg.getFunctionalGroupItems().contains(item)) {
                itemAbsentInFunctionalGroupError(fg, item, maverickName);
                return;
            }
            if (item == null && itemIdentifier != null) {
                emptyItemError(itemIdentifier, itemType, itemBusinessName, fg.getName(), fg.getType(), maverickName);
                return;
            }

            if (!errorExists) {
                updateFunctionalGroupAttributes(fg, platformName, description, type, status, userId,
                        roleId, lobName, maverickName, functionalGroupExternalId, functionalGroupName, xmlr);

                // Update FG name during B2B upload if config flag is enabled
                String fgNameAudit = updateFunctionalGroupNameIfRequired(fg, functionalGroupName,
                        functionalGroupExternalId, userId);
                if (!fgNameAudit.isEmpty()) {
                    persistFunctionalGroup(fg, userId, roleId, fgNameAudit);
                }
                countOfGoodRecords++;
            }
        } else {
            functionalGroupDoesNotExistError(fg, maverickName, xmlr);
        }
    }

    private void updateFunctionalGroupAttributes(FunctionalGroup fg, String platformName,
            String description, String type, String status, String userId, String roleId,
            String lobName, String maverickName, String functionalGroupExternalId,
            String functionalGroupName, XMLStreamReader xmlr) throws MessageLoaderException {

        String auditUpdateComment;
        String oldDescription = fg.getDescription();
        String oldStatus = fg.getStatus();
        String oldMaverickName = fg.getAliasName();

        // Update description
        auditUpdateComment = updateFunctionalGroupDescription(fg, description, oldDescription);
        if (!auditUpdateComment.isEmpty()) {
            persistFunctionalGroup(fg, userId, roleId, auditUpdateComment);
        }

        // Update platform
        auditUpdateComment = updateFunctionalGroupPlatform(fg, platformName);
        if (!auditUpdateComment.isEmpty()) {
            persistFunctionalGroup(fg, userId, roleId, auditUpdateComment);
        }

        // Update status
        auditUpdateComment = updateFunctionalGroupStatus(fg, status, oldStatus);
        if (!auditUpdateComment.isEmpty()) {
            persistFunctionalGroup(fg, userId, roleId, auditUpdateComment);
        }

        // Update LOB
        auditUpdateComment = updateFunctionalGroupLob(fg, lobName);
        if (!auditUpdateComment.isEmpty()) {
            persistFunctionalGroup(fg, userId, roleId, auditUpdateComment);
        }

        // Update alias/maverick name
        auditUpdateComment = updateFunctionalGroupMaverickName(fg, oldMaverickName, maverickName);
        if (!auditUpdateComment.isEmpty()) {
            persistFunctionalGroup(fg, userId, roleId, auditUpdateComment);
        }
    }

    private String updateFunctionalGroupStatus(FunctionalGroup fg, String status, String oldStatus)
            throws MessageLoaderException {
        if (status == null || status.equals(oldStatus)) {
            return "";
        }
        if ((oldStatus == null && status != null) || (oldStatus != null && !oldStatus.equals(status))) {
            if (FunctionalGroup.CFG.equalsIgnoreCase(fg.getType())) {
                // CFG type - check specific permissions
                if ("INACTIVE".equalsIgnoreCase(oldStatus) && "ACTIVE".equalsIgnoreCase(status)) {
                    if ("false".equalsIgnoreCase(getProperty(KEY_FG_CFG_ACTIVATE))) {
                        throw new MessageLoaderException("User doesn't have permission to ACTIVATE the CFG Functional Group");
                    }
                    fgToBeReactivated.add(fg);
                } else if ("ACTIVE".equalsIgnoreCase(oldStatus) && "INACTIVE".equalsIgnoreCase(status)) {
                    if ("false".equalsIgnoreCase(getProperty(KEY_FG_CFG_INACTIVE))) {
                        throw new MessageLoaderException("User doesn't have permission to DEACTIVATE the CFG Functional Group");
                    }
                }
            } else {
                // Non-CFG type
                if ("INACTIVE".equalsIgnoreCase(oldStatus) && "ACTIVE".equalsIgnoreCase(status)) {
                    if ("false".equalsIgnoreCase(getProperty(KEY_FG_ACTIVATE))) {
                        throw new MessageLoaderException("User doesn't have permission to ACTIVATE the Functional Group");
                    }
                    fgToBeReactivated.add(fg);
                } else if ("ACTIVE".equalsIgnoreCase(oldStatus) && "INACTIVE".equalsIgnoreCase(status)) {
                    if ("false".equalsIgnoreCase(getProperty(KEY_FG_DEACTIVATE))) {
                        throw new MessageLoaderException("User doesn't have permission to DEACTIVATE the Functional Group");
                    }
                }
            }
            fg.setStatus(status);
            return " Status Changed From " + oldStatus + " To " + status;
        }
        return "";
    }

    private String updateFunctionalGroupPlatform(FunctionalGroup fg, String platformName) {
        String oldPlatform = fg.getFgPlatform();
        if ((oldPlatform == null && platformName != null)
                || (oldPlatform != null && !oldPlatform.equals(platformName))) {
            fg.setFgPlatform(platformName);
            if (oldPlatform == null) {
                return " " + platformName + " Platform added";
            }
            return " Platform Updated From " + oldPlatform + " To " + platformName;
        }
        return "";
    }

    private String updateFunctionalGroupLob(FunctionalGroup fg, String lobName) {
        String oldLob = (fg.getFgLob() != null) ? fg.getFgLob().getLobValue() : null;
        if ((oldLob == null && lobName != null) || (oldLob != null && !oldLob.equals(lobName))) {
            if (fg.getFgLob() == null) {
                fg.setFgLob(new FunctionalGroupLob(fg));
            }
            fg.getFgLob().setLobValue(lobName);
            if (oldLob == null) {
                return " " + lobName + " LOB added";
            }
            return " LOB Updated From " + oldLob + " To " + lobName;
        }
        return "";
    }

    private String updateFunctionalGroupDescription(FunctionalGroup fg, String description, String oldDescription) {
        if ((oldDescription == null && description != null)
                || (oldDescription != null && !oldDescription.equals(description))) {
            fg.setDescription(description);
            return "Description Changed From " + oldDescription + " To " + description;
        }
        return "";
    }

    private String updateFunctionalGroupMaverickName(FunctionalGroup fg, String oldMaverickName, String maverickName) {
        if (pcmConfigUtil.getBoolean("scplatform.feature.functionalgroup.aliasName.show", true)) {
            if ((oldMaverickName == null && maverickName != null)
                    || (oldMaverickName != null && !oldMaverickName.equals(maverickName))) {
                fg.setAliasName(maverickName);
                return getMessage("functionalGroup.record_audit.aliasname.update",
                        new Object[]{oldMaverickName, maverickName});
            }
        }
        return "";
    }

    private String updateFunctionalGroupNameIfRequired(FunctionalGroup fg, String functionalGroupName,
            String functionalGroupExternalId, String userId) {
        boolean isFeatureEnabled = pcmConfigUtil.getBoolean("scplatform.feature.enable.store.functionalGroupExternalId", false);
        if (!isFeatureEnabled) return "";

        if ((userId != null && (userId.equalsIgnoreCase("BATCH") || userId.equalsIgnoreCase("BATCH-CFG-OPP")))
                && functionalGroupExternalId != null && !functionalGroupExternalId.isEmpty()
                && functionalGroupName != null && !functionalGroupName.isEmpty()
                && fg != null
                && FunctionalGroup.CFG.equalsIgnoreCase(fg.getType())) {
            String oldName = fg.getName();
            if ((oldName == null && functionalGroupName != null)
                    || (oldName != null && !oldName.equals(functionalGroupName))) {
                fg.setName(functionalGroupName);
                log.info("FG name updated from '{}' to '{}'", oldName, functionalGroupName);
                return "CFG Name Changed From " + oldName + " To " + functionalGroupName;
            }
        }
        return "";
    }

    // -----------------------------------------------------------------------
    // ACTIVATE / DEACTIVATE / RENAME operations
    // -----------------------------------------------------------------------

    private void handleActivate(FunctionalGroup fg, String fgName, String fgType) throws MessageLoaderException {
        if (fg == null) {
            fgErrors.put(functionalGroupCount, "FunctionalGroup not found: " + fgName);
            return;
        }
        if ("false".equalsIgnoreCase(getProperty(KEY_FG_CFG_ACTIVATE))) {
            fgErrors.put(functionalGroupCount, "No access for ACTIVATE");
            return;
        }
        if (FunctionalGroupConstants.STATUS_ACTIVE.equals(fg.getStatus())) {
            log.info("FunctionalGroup '{}' is already ACTIVE – skipping", fgName);
            return;
        }
        fgToBeReactivated.add(fg);
        countOfGoodRecords++;
    }

    private void handleDeactivate(FunctionalGroup fg, String fgName, String fgType) throws MessageLoaderException {
        if (fg == null) {
            fgErrors.put(functionalGroupCount, "FunctionalGroup not found: " + fgName);
            return;
        }
        if ("false".equalsIgnoreCase(getProperty(KEY_FG_CFG_INACTIVE))) {
            fgErrors.put(functionalGroupCount, "No access for DEACTIVATE");
            return;
        }
        fg.setStatus(FunctionalGroupConstants.STATUS_INACTIVE);
        fg.setLastChangedBy(activeUserId);
        fg.setLastChangedOn(new Date());
        fgRepository.save(fg);
        recordAudit(FunctionalGroupAuditHistory.OPERATION_DEACTIVATE,
                AllocationAuditHistory.ACTIONUPLOAD_UPDATE, fg, null,
                "FunctionalGroup deactivated via upload");
        countOfGoodRecords++;
    }

    private void handleRename(FunctionalGroup fg, String fgName, String fgType,
            String newName, String maverickName) throws MessageLoaderException {
        if (fg == null) {
            fgErrors.put(functionalGroupCount, "FunctionalGroup not found: " + fgName);
            return;
        }
        if ("false".equalsIgnoreCase(getProperty(KEY_FG_CFG_RENAME))) {
            fgErrors.put(functionalGroupCount, "No access for RENAME");
            return;
        }
        if (StringUtils.isBlank(newName)) {
            fgErrors.put(functionalGroupCount, "New name (Description column) is required for RENAME");
            return;
        }
        String oldName = fg.getName();
        fg.setName(newName);
        fg.setLastChangedBy(activeUserId);
        fg.setLastChangedOn(new Date());
        fgRepository.save(fg);
        recordAudit(FunctionalGroupAuditHistory.OPERATION_UPDATEFG,
                AllocationAuditHistory.ACTIONUPLOAD_UPDATE, fg, null,
                "FunctionalGroup renamed from '" + oldName + "' to '" + newName + "'");
        countOfGoodRecords++;
    }

    // -----------------------------------------------------------------------
    // Validation methods
    // -----------------------------------------------------------------------

    private void validateItemTypeForCfg(String itemType, String fgType, String maverickName) throws MessageLoaderException {
        if (!Item.ITEM.equals(itemType) && FunctionalGroup.CFG.equalsIgnoreCase(fgType)) {
            log.error("Item type is {}. Only Item type 'Item' is allowed in CFG, {}:{}",
                    itemType, altMaverickName, maverickName);
            throw new MessageLoaderException("Item type is " + itemType
                    + ". Only Item type 'Item' is allowed in CFG, " + altMaverickName + ":" + maverickName);
        }
    }

    private void validateFunctionalGroupName(String functionalGroupName, String itemIdentifier,
            String type, String maverickName, XMLStreamReader xmlr) throws MessageLoaderException {
        String validateFName = pcmConfigUtil.getString("pcm.functional.group.allowedCharacters.upload", null);
        if (validateFName != null) {
            Pattern pattern = Pattern.compile(validateFName);
            Matcher matcher = pattern.matcher(functionalGroupName);
            if (!matcher.matches()) {
                String allowedCharacters = getMessage("functionalgroup.allowed.characters", null);
                String errorMsg = getLocation(xmlr) + ":" + getMessage("warn.error_wrong_data_value_for_fg_loader",
                        new Object[]{functionalGroupName, allowedCharacters, altMaverickName, maverickName});
                log.error(errorMsg);
                addFgError(errorMsg);
            }
        }
        validateFgNameLength(functionalGroupName, itemIdentifier, type, maverickName, xmlr);
    }

    private void validateFgNameLength(String functionalGroupName, String itemIdentifier,
            String type, String maverickName, XMLStreamReader xmlr) {
        int maxlength = pcmConfigUtil.getIntValue("pcm.functional.name.maxlength", 255);
        if (functionalGroupName != null && functionalGroupName.length() > maxlength) {
            String errorMsg = getLocation(xmlr) + ":" + getMessage("errors.fg_name_too_large",
                    new Object[]{Integer.toString(maxlength), itemIdentifier, functionalGroupName, type, altMaverickName, maverickName});
            log.error(errorMsg);
            addFgError(errorMsg);
        }
    }

    private void validateAliasNameLength(String functionalGroupName, String itemIdentifier,
            String type, String maverickName) {
        int maxlength = pcmConfigUtil.getIntValue("pcm.functional.name.maxlength", 255);
        if (maverickName != null && maverickName.length() > maxlength) {
            String errorMsg = getMessage("errors.fg_alias_name_too_large",
                    new Object[]{Integer.toString(maxlength), itemIdentifier, functionalGroupName, type});
            log.error(errorMsg);
            addFgError(errorMsg);
        }
    }

    private void validatePlatform(String platform, String fgType, String maverickName) throws MessageLoaderException {
        String validatePattern = pcmConfigUtil.getString("pcm.functional.group.allowedCharacters.upload", null);
        if (validatePattern != null && platform != null) {
            Pattern pattern = Pattern.compile(validatePattern);
            Matcher matcher = pattern.matcher(platform);
            if (!matcher.matches()) {
                String allowedCharacters = getMessage("functionalgroup.allowed.characters", null);
                String errorMsg = getMessage("warn.error_wrong_data_value_for_fg_platform_loader",
                        new Object[]{platform, allowedCharacters, altMaverickName, maverickName});
                log.error(errorMsg);
                addFgError(errorMsg);
            }
        }
        validateLobAndPlatform(platform, "Platform", fgType,
                "pcm.functional.platform.mandatory.fgTypes", "pcm.functional.platform.types");
    }

    private void validateLob(String lob, String fgType) throws MessageLoaderException {
        String validatePattern = pcmConfigUtil.getString("pcm.functional.group.allowedCharacters.upload", null);
        if (validatePattern != null && lob != null) {
            Pattern pattern = Pattern.compile(validatePattern);
            Matcher matcher = pattern.matcher(lob);
            if (!matcher.matches()) {
                String allowedCharacters = getMessage("functionalgroup.allowed.characters", null);
                String errorMsg = getMessage("warn.error_wrong_data_value_for_fg_lob_loader",
                        new Object[]{lob, allowedCharacters});
                log.error(errorMsg);
                addFgError(errorMsg);
            }
        }
        validateLobAndPlatform(lob, "Lob", fgType, "pcm.lob.mandatory.fgTypes", "pcm.functional.lob.types");
    }

    private void validateLobAndPlatform(String value, String fieldType, String fgType,
            String mandTypesConfig, String valuesConfig) {
        List<String> mandTypes = pcmConfigUtil.getList(mandTypesConfig);
        if (mandTypes == null) mandTypes = Collections.emptyList();
        List<String> values = pcmConfigUtil.getList(valuesConfig);
        if (values == null) values = Collections.emptyList();

        int maxlength = "Lob".equals(fieldType)
                ? pcmConfigUtil.getIntValue("pcm.functional.lob.maxlength", 20)
                : pcmConfigUtil.getIntValue("pcm.functional.platform.maxlength", 20);

        if (mandTypes.contains(fgType) && (value == null || value.isEmpty())) {
            String errorMsg = getMessage("warn.error_lob_or_platform_should_not_be_empty",
                    new Object[]{fieldType, fgType});
            log.error(errorMsg);
            addFgError(errorMsg);
        }
        if (value != null && !values.isEmpty() && !values.contains(value)) {
            String errorMsg = getMessage("warn.error_wrong_lob_or_platform_value_for_fg_loader",
                    new Object[]{fieldType, value, String.join(",", values)});
            log.error(errorMsg);
            addFgError(errorMsg);
        }
        if (value != null && value.length() > maxlength) {
            String errorMsg = "Lob".equals(fieldType)
                    ? getMessage("errors.fg_lob_too_large", new Object[]{Integer.toString(maxlength)})
                    : getMessage("errors.fg_platform_too_large", new Object[]{Integer.toString(maxlength)});
            log.error(errorMsg);
            addFgError(errorMsg);
        }
    }

    private void validateFgType(String fgType, String maverickName, XMLStreamReader xmlr) {
        List<String> allowedFgTypes = pcmConfigUtil.getList("pcm.functional.groupType");
        if (allowedFgTypes != null && !allowedFgTypes.isEmpty() && !allowedFgTypes.contains(fgType)) {
            String message = getLocation(xmlr) + ":" + getMessage("error.functional_group_invalid_fg_type",
                    new Object[]{fgType, allowedFgTypes, altMaverickName, maverickName});
            log.error(message);
            addFgError(message);
        }
    }

    private void validateUserItemType(Item item, String userItemType) {
        // TODO: FlexAttribute validation when FlexAttributeManager is migrated
        log.debug("TODO: validateUserItemType for item '{}' userItemType '{}'",
                item.getItemNumber(), userItemType);
    }

    private void validateItemPresentInMultipleFGs(String itemIdentifier, String functionalGroupName,
            String type, String maverickName, XMLStreamReader xmlr) throws MessageLoaderException {
        String config = pcmConfigUtil.getString("pcm.functional.group.to.item.relationship." + type.trim(),
                FunctionalGroup.MANY_TO_MANY);
        if (FunctionalGroup.ONE_TO_MANY.equalsIgnoreCase(config)) {
            if (itemFunctionalGroupMap.containsKey(itemIdentifier)
                    && !itemFunctionalGroupMap.get(itemIdentifier).equals(functionalGroupName)) {
                log.error("Item '{}' is present in more than one FG and the Mapping is OneToMany, {}:{}",
                        itemIdentifier, altMaverickName, maverickName);
                throw new MessageLoaderException(getLocation(xmlr)
                        + ": Item " + itemIdentifier + " is present in more than one Functional Group and the Mapping is OneToMany, "
                        + altMaverickName + ":" + maverickName);
            }
            itemFunctionalGroupMap.put(itemIdentifier, functionalGroupName);
        }
    }

    private void itemPresentInOtherFunctionalGroupValidation(String itemIdentifier, String type, Item item,
            String maverickName) throws MessageLoaderException {
        if (item.getFunctionalGroups() != null && !item.getFunctionalGroups().isEmpty()) {
            String config = pcmConfigUtil.getString("pcm.functional.group.to.item.relationship." + type.trim(),
                    FunctionalGroup.MANY_TO_MANY);
            if (FunctionalGroup.ONE_TO_MANY.equalsIgnoreCase(config)) {
                List<FunctionalGroup> fgs = findFGsByItem(itemIdentifier);
                List<FunctionalGroup> typeFGs = new ArrayList<>();
                for (FunctionalGroup f : fgs) {
                    if (type.equals(f.getType())) typeFGs.add(f);
                }
                if (!typeFGs.isEmpty()) {
                    log.error("Item '{}' present in other FG and Mapping is OneToMany, {}:'{}'",
                            item.getItemNumber(), altMaverickName, maverickName);
                    throw new MessageLoaderException("Item " + item.getItemNumber()
                            + " present in other Functional Group and the Mapping is OneToMany, "
                            + altMaverickName + ":" + maverickName);
                }
            }
        }
    }

    private void itemPresentInOtherFGValidation(FunctionalGroup fg, String itemIdentifier, Item item,
            String config, String type, String maverickName, XMLStreamReader xmlr)
            throws MessageLoaderException {
        if (FunctionalGroup.ONE_TO_MANY.equalsIgnoreCase(config)) {
            List<FunctionalGroup> fgs = findFGsByItem(itemIdentifier);
            List<FunctionalGroup> typeFGs = new ArrayList<>();
            for (FunctionalGroup f : fgs) {
                if (type.equals(f.getType())) typeFGs.add(f);
            }
            if (typeFGs.contains(fg)) {
                if (typeFGs.size() > 1) {
                    throw new MessageLoaderException(getLocation(xmlr) + ": Item " + item.getItemNumber()
                            + " present in other Functional Group and the Mapping is OneToMany "
                            + altMaverickName + ":" + maverickName);
                }
            } else if (!typeFGs.isEmpty()) {
                throw new MessageLoaderException("Item " + item.getItemNumber()
                        + " present in other Functional Group and the Mapping is OneToMany, "
                        + altMaverickName + ":" + maverickName);
            }
        }
    }

    private void checkIfSelectedItemHasDefinedOwnership(Item item, String functionalGroupName,
            String type, String maverickName) throws MessageLoaderException {
        List<String> ownershipCriteria = pcmConfigUtil.getList("pcm.functional.group.itemownership.check");
        if (ownershipCriteria != null && !ownershipCriteria.isEmpty()) {
            // TODO: ownership validation when responsibility data is available
            log.debug("TODO: Check item ownership criteria for item '{}'", item.getItemNumber());
        }
    }

    // -----------------------------------------------------------------------
    // Entity creation / update helpers
    // -----------------------------------------------------------------------

    private FunctionalGroup createNewFunctionalGroup(String functionalGroupName, String description,
            String type, Item item, String platform, String lob, String parentItemNumber,
            String parentItemBusinessName, String odmPartNumber, String odmPartBusinessName,
            String maverickName, String functionalGroupExternalId) {
        FunctionalGroup fg = new FunctionalGroup();
        fg.setName(functionalGroupName);
        if (pcmConfigUtil.getBoolean("scplatform.feature.functionalgroup.aliasName.show", true)) {
            fg.setAliasName(maverickName);
        }
        fg.setFunctionalGroupExternalId(functionalGroupExternalId);
        fg.setDescription(description);
        fg.setType(type);
        fg.setStatus(userItemIntegrationEnabled ? FunctionalGroupConstants.STATUS_NEW
                : FunctionalGroupConstants.STATUS_ACTIVE);
        if (platform != null) {
            fg.setFgPlatform(platform);
        }
        Set<Item> items = new HashSet<>();
        items.add(item);
        fg.setFunctionalGroupItems(items);
        fg.setExtractFlag("P");
        fg.setCreatedBy(activeUserId);
        fg.setCreatedOn(new Date());
        fg.setLastChangedBy(activeUserId);
        fg.setLastChangedOn(new Date());
        fg.setStatusChangedBy(activeUserId);
        fg.setFgLob(new FunctionalGroupLob(fg, lob));
        return fg;
    }

    private void addItemsToExistingFG(FunctionalGroup fg, Item item, Set<Item> items) {
        if (items == null) {
            items = new HashSet<>();
        }
        items.add(item);
        fg.setFunctionalGroupItems(items);
        fg.setExtractFlag("P");
        fg.setLastChangedBy(activeUserId);
        fg.setLastChangedOn(new Date());
    }

    private void createMapForTamAllocationCreation(FunctionalGroup fg, Item item, Set<Item> items) {
        Set<Item> newItems;
        if (fgItemMap.containsKey(fg)) {
            newItems = fgItemMap.get(fg);
            if (!newItems.contains(item) && (items == null || !items.contains(item))) {
                newItems.add(item);
                fgItemMap.put(fg, newItems);
            }
        } else {
            if (items == null || !items.contains(item)) {
                newItems = new HashSet<>();
                newItems.add(item);
                fgItemMap.put(fg, newItems);
            }
        }
    }

    private void persistFunctionalGroup(FunctionalGroup fg, String userId, String roleId,
            String auditUpdateComment) {
        fg.setExtractFlag("P");
        fg.setLastChangedBy(activeUserId);
        fg.setLastChangedOn(new Date());
        fgRepository.save(fg);

        recordAudit(FunctionalGroupAuditHistory.OPERATION_UPDATEFG,
                AllocationAuditHistory.ACTIONUPLOAD_UPDATE, fg, null, auditUpdateComment);
    }

    // -----------------------------------------------------------------------
    // JPA lookup helpers (replaces BomUtil / PcmUtil)
    // -----------------------------------------------------------------------

    private FunctionalGroup findFgByNameAndType(String name, String type) {
        try {
            if (StringUtils.isBlank(type)) {
                return fgRepository.getFunctionalGroupByName(name);
            }
            return fgRepository.getFunctionalGroupByNameAndType(name, type);
        } catch (Exception e) {
            log.warn("Error looking up FunctionalGroup '{}' type '{}': {}", name, type, e.getMessage());
            return null;
        }
    }

    private FunctionalGroup findFgByExternalId(String externalId) {
        try {
            return fgRepository.findByFunctionalGroupExternalId(externalId).orElse(null);
        } catch (Exception e) {
            log.warn("Error looking up FG by externalId '{}': {}", externalId, e.getMessage());
            return null;
        }
    }

    private Item findItemByIdentifierTypeAndBE(String itemNumber, String itemType, String beName) {
        try {
            log.debug("findItemByIdentifierTypeAndBE: itemNumber='{}', itemType='{}', beName='{}'",
                    itemNumber, itemType, beName);
            List<Item> results;
            if (StringUtils.isNotBlank(beName)) {
                results = itemRepository.findByItemNumberAndTypeAndBusinessEntityName(itemNumber, itemType, beName);
            } else {
                results = itemRepository.findByItemNumberAndType(itemNumber, itemType);
            }
            log.debug("findItemByIdentifierTypeAndBE: query returned {} result(s)", results.size());
            if (results.isEmpty()) {
                log.info("Item not found in DB: itemNumber='{}', itemType='{}', beName='{}'",
                        itemNumber, itemType, beName);
                return null;
            }
            if (results.size() > 1) throw new NonUniqueResultException();
            return results.get(0);
        } catch (NonUniqueResultException e) {
            throw e; // let caller handle
        } catch (Exception e) {
            log.error("Exception while finding item '{}' type '{}' BE '{}': {} - {}",
                    itemNumber, itemType, beName, e.getClass().getName(), e.getMessage(), e);
            return null;
        }
    }

    private BusinessEntity findBusinessEntity(String beName, String beType) {
        try {
            List<BusinessEntity> results = businessEntityRepository.findBusinessByName(beName, null);
            return results.isEmpty() ? null : results.get(0);
        } catch (Exception e) {
            log.warn("Error finding BusinessEntity '{}': {}", beName, e.getMessage());
            return null;
        }
    }

    private List<FunctionalGroup> findFGsByItem(String itemIdentifier) {
        try {
            return fgRepository.getFunctionalGroupListByItem(itemIdentifier);
        } catch (Exception e) {
            log.warn("Error finding FGs by item '{}': {}", itemIdentifier, e.getMessage());
            return Collections.emptyList();
        }
    }

    // -----------------------------------------------------------------------
    // Audit helpers (replaces PcmUtil.recordFunctionalGroupAudit)
    // -----------------------------------------------------------------------

    private void recordAudit(String operation, String action,
            FunctionalGroup fg, Item item, String comment) {
        try {
            String b2bSource = getProperty(InterconnectConstants.SCPLATFORM_B2BSOURCE);
            if (b2bSource != null && !b2bSource.isEmpty()) {
                comment = b2bSource + " : " + comment;
            }
            String transactionId = ", transactionId: "
                    + (getProperty("transactionId") == null ? "null" : getProperty("transactionId"));

            FunctionalGroupAuditHistory hist = new FunctionalGroupAuditHistory(
                    activeUserId != null ? activeUserId : "system",
                    getActiveUserRoleId(),
                    action,
                    operation,
                    fg,
                    comment + transactionId,
                    timeStamp);
            auditRepository.save(hist);
        } catch (Exception e) {
            log.error("Failed to save FG audit record: {}", e.getMessage(), e);
        }
    }

    private void recordAuditWithItem(String operation, String action,
            FunctionalGroup fg, Item item, String comment) {
        try {
            String b2bSource = getProperty(InterconnectConstants.SCPLATFORM_B2BSOURCE);
            if (b2bSource != null && !b2bSource.isEmpty()) {
                comment = b2bSource + " : " + comment;
            }
            String transactionId = ", transactionId: "
                    + (getProperty("transactionId") == null ? "null" : getProperty("transactionId"));

            FunctionalGroupAuditHistory hist = new FunctionalGroupAuditHistory(
                    activeUserId != null ? activeUserId : "system",
                    getActiveUserRoleId(),
                    action,
                    operation,
                    fg,
                    item,
                    comment + transactionId,
                    timeStamp);
            auditRepository.save(hist);
        } catch (Exception e) {
            log.error("Failed to save FG audit record with item: {}", e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Error helpers
    // -----------------------------------------------------------------------

    private void addFgError(String errorMsg) {
        if (!fgErrors.containsKey(functionalGroupCount)) {
            fgErrors.put(functionalGroupCount, errorMsg);
        } else {
            fgErrors.put(functionalGroupCount, errorMsg + "\n\n" + fgErrors.get(functionalGroupCount));
        }
        errorExists = true;
    }

    private void fgDoesNotExistError() throws MessageLoaderException {
        log.error("Functional Group does not exist");
        throw new MessageLoaderException("Functional Group does not exist");
    }

    private void functionalGroupDoesNotExistError(FunctionalGroup fg, String maverickName,
            XMLStreamReader xmlr) throws MessageLoaderException {
        if (fg == null) {
            throw new MessageLoaderException(getLocation(xmlr)
                    + ": Functional Group does not exist, " + altMaverickName + ":" + maverickName);
        }
    }

    private void emptyItemError(String itemIdentifier, String itemType, String itemBusinessName,
            String fgName, String fgType, String maverickName) {
        log.error("Unable to find Item with itemIdentifier-{}, itemType-{} and itemBusinessName-{} and {}-{}",
                itemIdentifier, itemType, itemBusinessName, altMaverickName, maverickName);
        String message = getMessage("error.fg_item_not_found_maverick",
                new Object[]{itemIdentifier, itemType, itemBusinessName, altMaverickName, maverickName});
        addFgError(message);
    }

    private void emptyItemTypeError() throws MessageLoaderException {
        log.error("Item Type can not be null");
        throw new MessageLoaderException("Item Type can not be null");
    }

    private void emptyItemBusinessNameError() throws MessageLoaderException {
        String errorMessage = getMessage("error.fg_itemBusinessName_not_added", null);
        log.error(errorMessage);
        throw new MessageLoaderException(errorMessage);
    }

    private void itemAbsentInFunctionalGroupError(FunctionalGroup fg, Item item, String maverickName)
            throws MessageLoaderException {
        log.error("Item '{}' not present in Functional group '{}', {}:{}",
                item.getItemNumber(), fg.getName(), altMaverickName, maverickName);
        throw new MessageLoaderException("Item " + item.getItemNumber()
                + " is not present in Functional group " + fg.getName()
                + " " + altMaverickName + ":" + maverickName);
    }

    private void itemTypeAbsentWhenItemPresentError(FunctionalGroup fg, String maverickName)
            throws MessageLoaderException {
        log.error("Item Type is required if item number present, check FG '{}', {}:{}",
                fg.getName(), altMaverickName, maverickName);
        throw new MessageLoaderException("Item Type can not be null, " + altMaverickName + ":" + maverickName);
    }

    private void whenOperationCodeDoesNotExist() throws MessageLoaderException {
        log.error("Operation Code does not exist");
        throw new MessageLoaderException("Operation Code does not exist");
    }

    // -----------------------------------------------------------------------
    // Item type initialization
    // -----------------------------------------------------------------------

    private String initItemType(String itemType) {
        switch (itemType.toUpperCase()) {
            case ("ITEM"):
                itemType = "I";
                break;
            case ("SUPPLIER ITEM"):
                itemType = "S";
                break;
            case ("MFG ITEM"):
                itemType = "M";
                break;
            default:
                itemType = "";
        }
        return itemType;
    }

    // -----------------------------------------------------------------------
    // XML attribute helpers
    // -----------------------------------------------------------------------

    private String getAttr(XMLStreamReader xmlr, String name) {
        String v = xmlr.getAttributeValue(null, name);
        return (v != null) ? v.trim() : null;
    }

    private String getAttrUpperCase(XMLStreamReader xmlr, String name) {
        String v = xmlr.getAttributeValue(null, name);
        return (v != null) ? v.trim().toUpperCase() : null;
    }

    // -----------------------------------------------------------------------
    // Message helper
    // -----------------------------------------------------------------------

    public String getMessage(String key, Object[] args) {
        try {
            if (SCPlatformMessages.INSTANCE != null) {
                return SCPlatformMessages.INSTANCE.getMessage(key, args, null);
            }
        } catch (Exception e) {
            log.debug("Message key '{}' not found: {}", key, e.getMessage());
        }
        return key + (args != null ? " " + Arrays.toString(args) : "");
    }

    // -----------------------------------------------------------------------
    // Accessors for diagnostics
    // -----------------------------------------------------------------------

    public Map<Integer, String> getFgErrors() {
        return fgErrors;
    }

    public int getCountOfGoodRecords() {
        return countOfGoodRecords;
    }
}
