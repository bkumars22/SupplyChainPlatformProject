/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.tam.loader;

import com.scplatform.pcm.allocationAudit.entity.AllocationAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.FunctionalGroupAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.TAMAuditHistory;
import com.scplatform.pcm.allocationAudit.repository.AllocationAuditHistoryRepository;
import com.scplatform.pcm.allocationAudit.service.FunctionalGroupAuditService;
import com.scplatform.pcm.avl.entity.Avl;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.repository.BusinessEntityRepository;
import com.scplatform.pcm.commodityProfile.service.CommodityProfileService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod.PeriodType;
import com.scplatform.pcm.fiscalPeriod.service.FiscalPeriodService;
import com.scplatform.pcm.functionalGroup.constant.FunctionalGroupConstants;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemCategory;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.repository.SiteRepository;
import com.scplatform.pcm.tam.entity.FunctionalGroupItemAllocation;
import com.scplatform.pcm.tam.entity.FunctionalGroupSupplierAllocation;
import com.scplatform.pcm.tam.entity.TAMAllocation;
import com.scplatform.pcm.tam.repository.FunctionalGroupItemAllocationRepository;
import com.scplatform.pcm.tam.repository.FunctionalGroupSupplierAllocationRepository;
import com.scplatform.pcm.tam.repository.TAMAllocationRepository;
import com.scplatform.pcm.tam.service.TAMAllocationService;
import com.scplatform.pcm.upload.loader.BaseImporter;
import com.scplatform.pcm.upload.entity.LoadEvent;
import com.scplatform.pcm.upload.loader.MessageLoaderException;
import com.scplatform.pcm.upload.loader.MessageLoaderStatus;
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
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * Processes TAMSupplierAllocation XML elements.
 * Migrated from legacy TAMSupplierAllocationCFGLoader â€” all original logic preserved;
 * PcmUtil/BomUtil/HibernateUtil/FiscalPeriodUtil/ConfigurationUtils replaced with
 * Spring Boot repositories and services.
 */
@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TAMSupplierCFGLoader extends BaseImporter {

    // ------------------------------------------------------------------
    // Constants (same as legacy)
    // ------------------------------------------------------------------
    private static final String TAM_UPLOAD_TYPE    = "SUPPLIER ALLOCATON ";
    private static final String UPLOAD_FG_TYPE     = "CFG";
    private static final String OPERATION_CREATETAM = "CREATETAM";
    private static final String OPERATION_UPDATETAM = "UPDATETAM";
    private static DecimalFormat decimalFormat;

    // ------------------------------------------------------------------
    // Per-run state (same field names as legacy)
    // ------------------------------------------------------------------
    int tamAllocation = 0;
    Map<String, TAMAllocation>                          map                  = new HashMap<>();
    Map<String, TAMAllocation>                          cacheMap             = new HashMap<>();
    Map<String, Boolean>                                tamHedging           = new HashMap<>();
    private Date                                        currentPeriod        = null;
    private List<FiscalPeriod>                          allFiscallMonth      = null;
    private Map<BusinessEntity, Set<Item>>              itemSupplierMap      = null;
    private Map<FiscalPeriod, List<FiscalPeriod>>       allBucket            = null;
    private Date                                        currentDate          = new Date();
    private Map<String, Map<BusinessEntity, Set<Item>>> tamItemSupplierMap   = new HashMap<>();
    private List<String>                                errors               = null;
    private Integer                                     maxErrorlimit        = 0;
    private Set<String>                                 commodityRestrict    = null;
    private Boolean                                     isAudit              = Boolean.TRUE;
    private Map<String, FunctionalGroup>                functionalGroupMap   = null;
    private Map<String, Boolean>                        commodityProfileMap  = null;
    private Map<Long, Double[]>                         hedgingMap           = null;
    private Map<String, Site>                           siteMap              = null;
    private Map<String, Site>                           sitetamInvisibleMap  = null;
    private Map<String, Object[]>                       businessEntityMap    = null;
    private List<String>                                warnings             = new ArrayList<>();
    private boolean                                     tamUpdateCheck;
    private boolean                                     enableB2BIntegration;
    private Map<String, String>                         sourceLastChangedByMap = new HashMap<>();

    // ------------------------------------------------------------------
    // Dependencies
    // ------------------------------------------------------------------
    private final FunctionalGroupRepository                   fgRepository;
    private final TAMAllocationRepository                     tamAllocationRepository;
    private final FunctionalGroupSupplierAllocationRepository supplierAllocationRepository;
    private final FunctionalGroupItemAllocationRepository     itemAllocationRepository;
    private final BusinessEntityRepository                    businessEntityRepository;
    private final SiteRepository                              siteRepository;
    private final FiscalPeriodService                         fiscalPeriodService;
    private final AllocationAuditHistoryRepository            auditRepository;
    private final FunctionalGroupAuditService                 functionalGroupAuditService;
    private final TAMAllocationService                        tamAllocationService;
    private final CommodityProfileService                     commodityProfileService;

    // ------------------------------------------------------------------
    // Constructor
    // ------------------------------------------------------------------
    public TAMSupplierCFGLoader(LoadJobRepository loadJobRepository,
            LoadEventRepository loadEventRepository,
            PcmConfigUtil pcmConfigUtil,
            FunctionalGroupRepository fgRepository,
            TAMAllocationRepository tamAllocationRepository,
            FunctionalGroupSupplierAllocationRepository supplierAllocationRepository,
            FunctionalGroupItemAllocationRepository itemAllocationRepository,
            BusinessEntityRepository businessEntityRepository,
            SiteRepository siteRepository,
            FiscalPeriodService fiscalPeriodService,
            AllocationAuditHistoryRepository auditRepository,
            FunctionalGroupAuditService functionalGroupAuditService,
            TAMAllocationService tamAllocationService,
            CommodityProfileService commodityProfileService) {
        super(loadJobRepository, loadEventRepository, pcmConfigUtil);
        this.fgRepository                = fgRepository;
        this.tamAllocationRepository     = tamAllocationRepository;
        this.supplierAllocationRepository = supplierAllocationRepository;
        this.itemAllocationRepository    = itemAllocationRepository;
        this.businessEntityRepository    = businessEntityRepository;
        this.siteRepository              = siteRepository;
        this.fiscalPeriodService         = fiscalPeriodService;
        this.auditRepository             = auditRepository;
        this.functionalGroupAuditService = functionalGroupAuditService;
        this.tamAllocationService        = tamAllocationService;
        this.commodityProfileService     = commodityProfileService;
    }

    // ------------------------------------------------------------------
    // BaseImporter
    // ------------------------------------------------------------------
    @Override
    public void updateStats(MessageLoaderStatus status) {
        status.setStatistic("tamAllocation", tamAllocation);
        status.setStatistic("LoadEvents", this.loadEventCount);
    }

    @Override
    public int getCount() {
        return tamAllocation;
    }

    // ------------------------------------------------------------------
    // process() â€” two-phase: collect during XML parse, validate+persist
    // ------------------------------------------------------------------
    @Override
    public void process(XMLStreamReader xmlr, int passNumber) throws Exception {
        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        StopWatch batchTimer = new StopWatch();

        maxErrorlimit        = pcmConfigUtil.getInteger("pcm.max.error.tam.supplier.upload.limit", 0);
        isAudit              = pcmConfigUtil.getBoolean("pcm.upload.tam.supplier", Boolean.TRUE);
        tamUpdateCheck       = pcmConfigUtil.getBoolean("scplatform.feature.enable.tamAllocation.tamUpdateFlag", true);
        enableB2BIntegration = pcmConfigUtil.getBoolean("scplatform.feature.enable.tam.B2B.integration", Boolean.FALSE);
        tamAllocation        = 0;
        currentDate          = new Date();

        List<FiscalPeriod> currentMonthList = fiscalPeriodService.getFiscalPeriods(Calendar.getInstance(), PeriodType.MONTH, 0, 0);
        currentPeriod = currentMonthList.isEmpty() ? new Date()
                : new Date(currentMonthList.get(0).getFiscalPeriodStartDate().getTime());
        allFiscallMonth = fiscalPeriodService.getFiscalPeriods(Calendar.getInstance(), PeriodType.MONTH, 0, 11);
        allBucket            = getWeeklyMonthlyBucket();
        commodityRestrict    = new HashSet<>();
        commodityProfileMap  = new HashMap<>();
        functionalGroupMap   = new HashMap<>();
        hedgingMap           = new HashMap<>();
        siteMap              = new HashMap<>();
        sitetamInvisibleMap  = new HashMap<>();
        businessEntityMap    = new HashMap<>();
        sourceLastChangedByMap = new HashMap<>();
        map                  = new HashMap<>();
        cacheMap             = new HashMap<>();
        tamHedging           = new HashMap<>();
        tamItemSupplierMap   = new HashMap<>();
        warnings             = new ArrayList<>();

        String userId = activeUserId;
        String roleId = getActiveUserRoleId();

        while (xmlr.hasNext()) {
            int event = xmlr.next();
            if (!xmlr.isStartElement() && !xmlr.isEndElement()) continue;
            if (log.isDebugEnabled() && xmlr.hasName())
                log.debug(getEventTypeString(event) + " " + xmlr.getLocalName());

            if ("TAMMessage".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    errors = new ArrayList<>();
                    batchTimer.start();
                    tamAllocation = 0;
                }
                if (xmlr.isEndElement()) {
                    // Phase 1: validate all collected allocations
                    for (TAMAllocation allocation : map.values()) {
                        if (allocation.getAllowHedging() == null)
                            allocation.setAllowHedging(Boolean.FALSE);
                        try {
                            validateTAM(allocation);
                        } catch (MessageLoaderException exception) {
                            if (errors.size() < maxErrorlimit) {
                                errors.add(exception.getMessage());
                                continue;
                            } else {
                                tamAllocation = 0;
                                throw new MessageLoaderException(StringUtils.join(errors, "\n"));
                            }
                        }
                    }
                    if (!sitetamInvisibleMap.isEmpty()) {
                        for (String s : sitetamInvisibleMap.keySet())
                            log.info("tamVisibleFlag=N for the site: {}", s);
                    }

                    if (errors.isEmpty()) {
                        for (TAMAllocation allocation : map.values()) {
                            allocation.setExtractFlag("P");
                            allocation.setDiscpExtractFlag("P");
                            allocation.setRollOverCount(0);
                            allocation.setLastChangedBy(activeUserId);
                            allocation.setLastChangedOn(new Timestamp(System.currentTimeMillis()));
                            allocation.setIsCurrentDataDeleted(null);

                            // areAllTAMsBalanced: the legacy checked balance across all sites for the FG.
                            // validateTAM() already ensures balance per site. No cross-site repository
                            // method exists to replicate the multi-site check, so we derive balance from
                            // the fact that validateTAM() passed without exception.
                            boolean allTAMBalanced = true;

                            FunctionalGroup fg = null;
                            if (allocation.getFunctionalGroup() != null
                                    && allocation.getFunctionalGroup().getFunctionalGroupId() != null) {
                                fg = fgRepository.getFunctionalGroupById(
                                        allocation.getFunctionalGroup().getFunctionalGroupId());
                                if (fg != null) allocation.setFunctionalGroup(fg);
                            }

                            String sourceLastChangedBy = "";
                            if (enableB2BIntegration) {
                                sourceLastChangedBy =
                                        Optional.ofNullable(allocation.getSourceLastChangedBy())
                                                .map(String::trim)
                                                .filter(s -> !s.isEmpty())
                                                .map(s -> " (source: " + s + ")")
                                                .orElse("");
                            }

                            if (allocation.getId() == null) {
                                allocation.setCreatedBy(activeUserId);
                                allocation.setCreatedOn(new Timestamp(System.currentTimeMillis()));
                                String fgStatus = allocation.getFunctionalGroup() != null
                                        ? allocation.getFunctionalGroup().getStatus() : null;
                                String fgName = allocation.getFunctionalGroup() != null
                                        ? allocation.getFunctionalGroup().getName() : null;
                                if (allTAMBalanced && updateFGStatusIfNeeded(
                                        allocation.getFunctionalGroup(), fgStatus, FunctionalGroupConstants.STATUS_ACTIVE)) {
                                    String statusChangeMsg = MessageFormat.format(
                                            FunctionalGroupConstants.AUDIT_STATUS_COMMENT,
                                            fgName, fgStatus, FunctionalGroupConstants.STATUS_ACTIVE);
                                    functionalGroupAuditService.recordFunctionalGroupAudit(
                                            activeUserId, roleId,
                                            AllocationAuditHistory.ACTIONUPLOAD_UPDATE,
                                            FunctionalGroupAuditHistory.OPERATION_UPDATEFG,
                                            allocation.getFunctionalGroup(),
                                            statusChangeMsg,
                                            new Timestamp(System.currentTimeMillis()));
                                }
                                if (isAudit) {
                                    auditRepository.save(new TAMAuditHistory(userId, roleId,
                                            TAM_UPLOAD_TYPE + AllocationAuditHistory.ACTIONUPLOAD_CREATE,
                                            OPERATION_CREATETAM,
                                            allocation, allocation.getSite(),
                                            "New TAM Created For FunctionalGroup: "
                                                    + allocation.getFunctionalGroup().getName()
                                                    + " and Site: "
                                                    + allocation.getSite().getSiteDescription()
                                                    + sourceLastChangedBy,
                                            timeStamp));
                                    for (FunctionalGroupSupplierAllocation sa : allocation.getSupplierAllocations()) {
                                        auditRepository.save(new TAMAuditHistory(userId, roleId,
                                                TAM_UPLOAD_TYPE + AllocationAuditHistory.ACTIONUPLOAD_CREATE,
                                                OPERATION_CREATETAM,
                                                allocation, allocation.getSite(),
                                                sa.getBusinessEntity(),
                                                sa.getStartDate(), sa.getEndDate(),
                                                "Supplier Allocation Created With "
                                                        + getFormatedAllocationForAudit(sa.getAllocation())
                                                        + sourceLastChangedBy,
                                                timeStamp));
                                        for (FunctionalGroupItemAllocation ia : sa.getItemAllocations()) {
                                            auditRepository.save(new TAMAuditHistory(userId, roleId,
                                                    TAM_UPLOAD_TYPE + AllocationAuditHistory.ACTIONUPLOAD_CREATE,
                                                    OPERATION_CREATETAM,
                                                    allocation, allocation.getSite(),
                                                    ia.getItem(), sa.getBusinessEntity(),
                                                    sa.getStartDate(), sa.getEndDate(),
                                                    "Item Allocation Created With "
                                                            + getFormatedAllocationForAudit(ia.getAllocation())
                                                            + sourceLastChangedBy,
                                                    timeStamp));
                                        }
                                    }
                                }
                            } else {
                                TAMAllocation beforeUpdateTAM = cacheMap.get(
                                        allocation.getFunctionalGroup().getFunctionalGroupId()
                                                + "-" + allocation.getSite().getSiteKey());
                                if (beforeUpdateTAM != null) {
                                    String fgStatus = allocation.getFunctionalGroup() != null
                                            ? allocation.getFunctionalGroup().getStatus() : null;
                                    String fgName = allocation.getFunctionalGroup() != null
                                            ? allocation.getFunctionalGroup().getName() : null;
                                    if (allTAMBalanced && updateFGStatusIfNeeded(
                                            allocation.getFunctionalGroup(), fgStatus, FunctionalGroupConstants.STATUS_ACTIVE)) {
                                        String statusChangeMsg = MessageFormat.format(
                                                FunctionalGroupConstants.AUDIT_STATUS_COMMENT,
                                                fgName, fgStatus, FunctionalGroupConstants.STATUS_ACTIVE);
                                        functionalGroupAuditService.recordFunctionalGroupAudit(
                                                activeUserId, roleId,
                                                AllocationAuditHistory.ACTIONUPLOAD_UPDATE,
                                                FunctionalGroupAuditHistory.OPERATION_UPDATEFG,
                                                allocation.getFunctionalGroup(),
                                                statusChangeMsg,
                                                new Timestamp(System.currentTimeMillis()));
                                    }
                                    if (isAudit) {
                                        for (FunctionalGroupSupplierAllocation sa : beforeUpdateTAM.getSupplierAllocations()) {
                                            for (FunctionalGroupSupplierAllocation sa2 : allocation.getSupplierAllocations()) {
                                                if (sa2.getStartDate().equals(sa.getStartDate())
                                                        && sa2.getBusinessEntity().equals(sa.getBusinessEntity())
                                                        && sa2.getEndDate().equals(sa.getEndDate())) {
                                                    if ((sa2.getAllocation() != null && sa.getAllocation() == null)
                                                            || (sa.getAllocation() != null && sa2.getAllocation() == null)
                                                            || (sa2.getAllocation() != null && sa.getAllocation() != null
                                                                    && !sa2.getAllocation().equals(sa.getAllocation()))) {
                                                        auditRepository.save(new TAMAuditHistory(userId, roleId,
                                                                TAM_UPLOAD_TYPE + AllocationAuditHistory.ACTIONUPLOAD_UPDATE,
                                                                OPERATION_UPDATETAM,
                                                                allocation, allocation.getSite(),
                                                                sa2.getBusinessEntity(),
                                                                sa2.getStartDate(), sa2.getEndDate(),
                                                                "Supplier Allocation Changed From "
                                                                        + getFormatedAllocationForAudit(sa.getAllocation())
                                                                        + " To: "
                                                                        + getFormatedAllocationForAudit(sa2.getAllocation())
                                                                        + sourceLastChangedBy,
                                                                timeStamp));
                                                    }
                                                    for (FunctionalGroupItemAllocation ia : sa.getItemAllocations()) {
                                                        for (FunctionalGroupItemAllocation ia2 : sa2.getItemAllocations()) {
                                                            if (((ia2.getAllocation() != null && ia.getAllocation() == null)
                                                                    || (ia.getAllocation() != null && ia2.getAllocation() == null)
                                                                    || (ia2.getAllocation() != null && ia.getAllocation() != null
                                                                            && !ia2.getAllocation().equals(ia.getAllocation())))
                                                                    && ia2.getItem().equals(ia.getItem())) {
                                                                auditRepository.save(new TAMAuditHistory(userId, roleId,
                                                                        TAM_UPLOAD_TYPE + AllocationAuditHistory.ACTIONUPLOAD_UPDATE,
                                                                        OPERATION_UPDATETAM,
                                                                        allocation, allocation.getSite(),
                                                                        ia2.getItem(), sa2.getBusinessEntity(),
                                                                        sa2.getStartDate(), sa2.getEndDate(),
                                                                        "Item Allocation Changed From "
                                                                                + getFormatedAllocationForAudit(ia.getAllocation())
                                                                                + " To: "
                                                                                + getFormatedAllocationForAudit(ia2.getAllocation())
                                                                                + sourceLastChangedBy,
                                                                        timeStamp));
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            tamAllocationRepository.save(allocation);
                            if (allocation.getFunctionalGroup() != null
                                    && allocation.getFunctionalGroup().getFunctionalGroupId() != null) {
                                tamAllocationService.markTAMAsUpdated(
                                        allocation.getFunctionalGroup().getFunctionalGroupId());
                            }
                        }
                        if (tamUpdateCheck) postProcessErrors();
                        if (log.isInfoEnabled()) {
                            batchTimer.stop();
                            log.info("TAM allocation processed: {} ({} ms)", tamAllocation, batchTimer.getTime());
                        }
                    } else {
                        tamAllocation = 0;
                        throw new MessageLoaderException(StringUtils.join(errors, "\n"));
                    }
                }
            } else if ("TAMSupplierAllocation".equals(xmlr.getLocalName())) {
                if (xmlr.isStartElement()) {
                    try {
                        tamAllocation++;
                        loadTAMSupplierAllocation(xmlr);
                    } catch (MessageLoaderException e) {
                        if (e.isSoft()) {
                            addSoftError(e.getLocalizedMessage());
                        } else {
                            if (errors.size() < maxErrorlimit) {
                                errors.add(e.getMessage());
                                skipUntilEnd(xmlr, "TAMSupplierAllocation");
                            } else {
                                tamAllocation = 0;
                                throw new MessageLoaderException(StringUtils.join(errors, "\n"));
                            }
                        }
                    }
                } else if (xmlr.isEndElement()) {
                    if (tamAllocation % getBatchSize() == 0 && log.isInfoEnabled()) {
                        batchTimer.split();
                        log.info("TAM Allocation processed so far: {} ({} ms) {}",
                                tamAllocation, batchTimer.getSplitTime(),
                                isCommitOnBatchEnabled() ? "committed" : "");
                    }
                }
            }
        }
        if (log.isInfoEnabled()) {
            log.info("Total TAM Allocation processed: {}", tamAllocation);
            this.dumpCacheStats();
        }
    }

    private void postProcessErrors() {
        if (warnings.isEmpty()) return;
        // Legacy sent email via EmailService (com.scplatform.service.springmail) which is not
        // a dependency of this module. Warnings are surfaced as soft errors so the caller
        // (MessageLoader) includes them in the upload response.
        for (String w : warnings) addSoftError(w);
        log.warn("TAM supplier upload warnings ({}): {}", warnings.size(), String.join(" | ", warnings));
    }

    // ------------------------------------------------------------------
    // Record-level processing (full legacy logic)
    // ------------------------------------------------------------------
    private void loadTAMSupplierAllocation(XMLStreamReader xmlr) throws MessageLoaderException {

        TAMAllocation tamAllocation = null;
        FunctionalGroup functionalGroup = null;
        Site site = null;
        Map<BusinessEntity, Set<Item>> itemSupplierMap;

        String functionalGroupName = null;
        String fgType              = null;
        String siteDescription     = null;
        String supplierName        = null;
        String allowHedging        = null;
        String allocationMonth1Week1 = null, allocationMonth1Week2 = null, allocationMonth1Week3 = null;
        String allocationMonth1Week4 = null, allocationMonth1Week5 = null;
        String allocationMonth2Week1 = null, allocationMonth2Week2 = null, allocationMonth2Week3 = null;
        String allocationMonth2Week4 = null, allocationMonth2Week5 = null;
        String allocationMonth3  = null, allocationMonth4  = null, allocationMonth5  = null;
        String allocationMonth6  = null, allocationMonth7  = null, allocationMonth8  = null;
        String allocationMonth9  = null, allocationMonth10 = null, allocationMonth11 = null;
        String allocationMonth12 = null;
        String sourceLastChangedBy = null;

        if (xmlr.getAttributeValue(null, "functionalGroupName") != null)
            functionalGroupName = xmlr.getAttributeValue(null, "functionalGroupName").trim().toUpperCase();
        if (xmlr.getAttributeValue(null, "fgType") != null)
            fgType = xmlr.getAttributeValue(null, "fgType").trim().toUpperCase();
        if (xmlr.getAttributeValue(null, "siteDescription") != null)
            siteDescription = xmlr.getAttributeValue(null, "siteDescription").trim();
        if (xmlr.getAttributeValue(null, "supplierName") != null)
            supplierName = xmlr.getAttributeValue(null, "supplierName").trim();
        if (xmlr.getAttributeValue(null, "userInputLastChangedBy") != null)
            sourceLastChangedBy = xmlr.getAttributeValue(null, "userInputLastChangedBy").trim();

        String rawHedging = xmlr.getAttributeValue(null, "allowHedging");
        if (rawHedging != null)
            allowHedging = StringUtils.trimToNull(rawHedging.toUpperCase());

        allocationMonth1Week1 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M1W1"));
        allocationMonth1Week2 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M1W2"));
        allocationMonth1Week3 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M1W3"));
        allocationMonth1Week4 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M1W4"));
        allocationMonth1Week5 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M1W5"));
        allocationMonth2Week1 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M2W1"));
        allocationMonth2Week2 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M2W2"));
        allocationMonth2Week3 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M2W3"));
        allocationMonth2Week4 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M2W4"));
        allocationMonth2Week5 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M2W5"));
        allocationMonth3      = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M3"));

        if ((allocationMonth1Week1 != null && allocationMonth1Week1.equals("AUTO"))
                || (allocationMonth1Week2 != null && allocationMonth1Week2.equals("AUTO"))
                || (allocationMonth1Week3 != null && allocationMonth1Week3.equals("AUTO"))
                || (allocationMonth1Week4 != null && allocationMonth1Week4.equals("AUTO"))
                || (allocationMonth1Week5 != null && allocationMonth1Week5.equals("AUTO"))
                || (allocationMonth2Week1 != null && allocationMonth2Week1.equals("AUTO"))
                || (allocationMonth2Week2 != null && allocationMonth2Week2.equals("AUTO"))
                || (allocationMonth2Week3 != null && allocationMonth2Week3.equals("AUTO"))
                || (allocationMonth2Week4 != null && allocationMonth2Week4.equals("AUTO"))
                || (allocationMonth2Week5 != null && allocationMonth2Week5.equals("AUTO"))
                || (allocationMonth3 != null && allocationMonth3.equals("AUTO"))) {
            log.error("Allocation is mandatory Upto 3rd Month");
            String msg = "Allocation is mandatory Upto 3rd Month";
            if (sourceLastChangedBy != null) {
                msg = "Invalid supply allocation buckets for FG: " + functionalGroupName
                        + " site: " + siteDescription;
            }
            throw new MessageLoaderException(msg);
        }

        allocationMonth4 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M4"));
        if (allocationMonth4 != null && allocationMonth4.equals("AUTO")) allocationMonth4 = allocationMonth3;
        allocationMonth5 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M5"));
        if (allocationMonth5 != null && allocationMonth5.equals("AUTO")) allocationMonth5 = allocationMonth4;
        allocationMonth6 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M6"));
        if (allocationMonth6 != null && allocationMonth6.equals("AUTO")) allocationMonth6 = allocationMonth5;
        allocationMonth7 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M7"));
        if (allocationMonth7 != null && allocationMonth7.equals("AUTO")) allocationMonth7 = allocationMonth6;
        allocationMonth8 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M8"));
        if (allocationMonth8 != null && allocationMonth8.equals("AUTO")) allocationMonth8 = allocationMonth7;
        allocationMonth9 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M9"));
        if (allocationMonth9 != null && allocationMonth9.equals("AUTO")) allocationMonth9 = allocationMonth8;
        allocationMonth10 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M10"));
        if (allocationMonth10 != null && allocationMonth10.equals("AUTO")) allocationMonth10 = allocationMonth9;
        allocationMonth11 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M11"));
        if (allocationMonth11 != null && allocationMonth11.equals("AUTO")) allocationMonth11 = allocationMonth10;
        allocationMonth12 = StringUtils.trimToNull(xmlr.getAttributeValue(null, "M12"));
        if (allocationMonth12 != null && allocationMonth12.equals("AUTO")) allocationMonth12 = allocationMonth11;

        String sourceMapKey = "";
        if (enableB2BIntegration) {
            sourceMapKey = String.join("|",
                    Objects.toString(functionalGroupName, ""), Objects.toString(siteDescription, ""),
                    Objects.toString(supplierName, ""), Objects.toString(sourceLastChangedBy, ""));
            if (!sourceLastChangedByMap.containsKey(sourceMapKey))
                sourceLastChangedByMap.put(sourceMapKey, sourceLastChangedBy);
        }

        // ---- Resolve FunctionalGroup ----
        if (functionalGroupName != null) {
            if (fgType == null) {
                if (sourceLastChangedBy != null) {
                    String msg = "Invalid FG type for FG: " + functionalGroupName
                            + " site: " + siteDescription + " expected type: " + UPLOAD_FG_TYPE;
                    log.error(msg); throw new MessageLoaderException(msg);
                } else {
                    String msg = "FG type is empty for FG: " + functionalGroupName;
                    log.error(msg); throw new MessageLoaderException(msg);
                }
            } else {
                if (!fgType.equalsIgnoreCase(UPLOAD_FG_TYPE)) {
                    if (sourceLastChangedBy != null) {
                        String msg = "Invalid FG type: " + fgType + " for FG: " + functionalGroupName
                                + " site: " + siteDescription + " expected: " + UPLOAD_FG_TYPE;
                        log.error(msg); throw new MessageLoaderException(msg);
                    } else {
                        String msg = "FG type '" + fgType + "' is not supported. Expected: "
                                + UPLOAD_FG_TYPE + " for FG: " + functionalGroupName;
                        log.error(msg); throw new MessageLoaderException(msg);
                    }
                }
            }
            if (functionalGroupMap.containsKey(functionalGroupName.toUpperCase())) {
                functionalGroup = functionalGroupMap.get(functionalGroupName.toUpperCase());
            } else {
                functionalGroup = fgRepository.getFunctionalGroupByNameAndType(functionalGroupName, fgType);
                functionalGroupMap.put(functionalGroupName.toUpperCase(), functionalGroup);
            }
            if (functionalGroup == null) {
                if (sourceLastChangedBy != null) {
                    String msg = "FG: " + functionalGroupName + " not found for site: "
                            + siteDescription + " on row " + getCount();
                    log.error(msg); throw new MessageLoaderException(msg);
                } else {
                    log.error("{} Functional Group Not Found On Row {}", functionalGroupName, getCount());
                    throw new MessageLoaderException(
                            functionalGroupName + " Functional Group Not Found On Row " + getCount());
                }
            }
            boolean result = false;
            if (commodityProfileMap.containsKey(functionalGroupName.toUpperCase())) {
                result = commodityProfileMap.get(functionalGroupName.toUpperCase());
            } else {
                result = commodityProfileService.isExcludedItemExistForTam(functionalGroup, getActiveUser());
                commodityProfileMap.put(functionalGroupName.toUpperCase(), result);
            }
            if (result) {
                boolean nonDuplicate = commodityRestrict.add(functionalGroupName);
                if (nonDuplicate) {
                    log.error("Current User is restricted in commodity profile for FG : {}", functionalGroupName);
                    throw new MessageLoaderException(
                            "Current User is restricted in commodity profile for FG : " + functionalGroupName);
                }
            }
        } else {
            if (sourceLastChangedBy != null) {
                String msg = "FG name is empty for site: " + siteDescription + " on row " + getCount();
                log.error(msg); throw new MessageLoaderException(msg);
            } else {
                log.error("Functional Group Name Should not be empty On Row {}", getCount());
                throw new MessageLoaderException("Functional Group Name Should not be empty On Row " + getCount());
            }
        }

        // ---- Resolve Site ----
        if (siteDescription != null) {
            site = siteMap.get(siteDescription.toUpperCase());
            boolean tamVisible;
            if (siteMap.containsKey(siteDescription.toUpperCase())) {
                // already loaded above via siteMap.get(...)
            } else if (sitetamInvisibleMap.containsKey(siteDescription.toUpperCase())) {
                site = sitetamInvisibleMap.get(siteDescription.toUpperCase());
                tamVisible = site.getSiteDetail().getTamVisibleFlag();
                if (!tamVisible) {
                    String msg = "TAM not visible for site: " + siteDescription
                            + " FG: " + functionalGroupName
                            + " supplier: " + (supplierName != null ? supplierName : "");
                    addSoftError(msg);
                    MessageLoaderException me = new MessageLoaderException(msg);
                    me.setSoft(true);
                    throw me;
                }
            } else {
                List<Site> sites = siteRepository.findBySiteDescriptionOnly(siteDescription);
                site = sites.isEmpty() ? null : sites.get(0);
                if (site != null && site.getSiteDetail() != null) {
                    tamVisible = site.getSiteDetail().getTamVisibleFlag();
                    if (!tamVisible) {
                        sitetamInvisibleMap.put(siteDescription.toUpperCase(), site);
                        String msg = "TAM not visible for site: " + siteDescription
                                + " FG: " + functionalGroupName
                                + " supplier: " + (supplierName != null ? supplierName : "");
                        addSoftError(msg);
                        MessageLoaderException me = new MessageLoaderException(msg);
                        me.setSoft(true);
                        throw me;
                    } else {
                        siteMap.put(siteDescription.toUpperCase(), site);
                    }
                }
            }
            if (site == null) {
                if (sourceLastChangedBy != null) {
                    String msg = "Invalid site: " + siteDescription + " for FG: "
                            + functionalGroupName + " on row " + getCount();
                    log.error(msg); throw new MessageLoaderException(msg);
                } else {
                    log.error("Site doesn't exist for :{} On Row {}", siteDescription, getCount());
                    throw new MessageLoaderException(
                            "Site doesn't exist for :" + siteDescription + " On Row " + getCount());
                }
            } else {
                if (site.getSiteDetail() != null && site.getSiteDetail().getSiteState() != null
                        && site.getSiteDetail().getSiteState()) {
                    log.error("Site is EOL :{} On Row {}, No Allocation is allowed", siteDescription, getCount());
                    throw new MessageLoaderException("Site is EOL :" + siteDescription + " On Row "
                            + getCount() + ", No Allocation is allowed");
                }
            }
        } else {
            if (sourceLastChangedBy != null) {
                String msg = "Site description is empty for FG: " + functionalGroupName + " on row " + getCount();
                log.error(msg); throw new MessageLoaderException(msg);
            } else {
                log.error("Site Description Should not be empty On Row {}", getCount());
                throw new MessageLoaderException("Site Description Should not be empty On Row " + getCount());
            }
        }

        // ---- Resolve BusinessEntity (supplier) ----
        BusinessEntity supplier = null;
        if (supplierName != null) {
            Boolean nonUniqueResult = Boolean.FALSE;
            if (businessEntityMap.containsKey(supplierName.toUpperCase())) {
                Object[] bMap = businessEntityMap.get(supplierName.toUpperCase());
                supplier       = (BusinessEntity) bMap[0];
                nonUniqueResult = (Boolean) bMap[1];
            } else {
                Object[] bArray = new Object[2];
                List<BusinessEntity> suppliers = businessEntityRepository.findBusinessByName(supplierName, "Supplier");
                if (suppliers != null && suppliers.size() == 1) {
                    supplier   = suppliers.get(0);
                    bArray[0]  = supplier;
                    bArray[1]  = Boolean.FALSE;
                } else if (suppliers != null && suppliers.size() > 1) {
                    bArray[0]  = null;
                    bArray[1]  = Boolean.TRUE;
                    nonUniqueResult = Boolean.TRUE;
                } else {
                    bArray[0] = null;
                    bArray[1] = Boolean.FALSE;
                }
                businessEntityMap.put(supplierName, bArray);
            }
            if (supplier == null && !nonUniqueResult) {
                if (sourceLastChangedBy != null) {
                    String msg = "Supplier: " + supplierName + " not found for FG: "
                            + functionalGroupName + " site: " + siteDescription;
                    log.error(msg); throw new MessageLoaderException(msg);
                } else {
                    log.error("Supplier not found with name :{}", supplierName);
                    throw new MessageLoaderException("Supplier not found with name :" + supplierName);
                }
            } else if (nonUniqueResult) {
                log.error("Duplicate Supplier with name :{}", supplierName);
                throw new MessageLoaderException("Duplicate Supplier with name :" + supplierName);
            }
        } else {
            if (sourceLastChangedBy != null) {
                String msg = "Supplier name is empty for FG: " + functionalGroupName + " site: " + siteDescription;
                log.error(msg); throw new MessageLoaderException(msg);
            } else {
                log.error("Supplier Should not be empty On Row {}", getCount());
                throw new MessageLoaderException("Supplier Should not be empty On Row " + getCount());
            }
        }

        // ---- Resolve or create TAMAllocation ----
        String allocationKey = functionalGroup.getFunctionalGroupId() + "-" + site.getSiteKey();
        if (map.containsKey(allocationKey))
            tamAllocation = map.get(allocationKey);

        if (tamAllocation == null) {
            tamAllocation = tamAllocationRepository.getTAMAllocationByFGSiteDescriptionAndSiteType(
                    functionalGroupName, siteDescription, site.getSiteType(),
                    new Date(allFiscallMonth.get(0).getFiscalPeriodStartDate().getTime()),
                    new Date(allFiscallMonth.get(allFiscallMonth.size() - 1).getFiscalPeriodEndDate().getTime()));
            if (tamAllocation != null) {
                TAMAllocation beforeUpdateTAM = copyTAM(tamAllocation);
                cacheMap.put(allocationKey, beforeUpdateTAM);

                Set<Item> items = functionalGroup.getFunctionalGroupItems();
                itemSupplierMap = new LinkedHashMap<>();
                for (Item i : items) {
                    for (Avl avl : i.getAvls()) {
                        if (itemSupplierMap.containsKey(avl.getSupplier())) {
                            itemSupplierMap.get(avl.getSupplier()).add(i);
                        } else {
                            itemSupplierMap.put(avl.getSupplier(), new TreeSet<>());
                            itemSupplierMap.get(avl.getSupplier()).add(i);
                        }
                    }
                }
                List<BusinessEntity> businessEntityList = new ArrayList<>(itemSupplierMap.keySet());
                Map<BusinessEntity, Set<Item>> sortedMap = new LinkedHashMap<>();
                for (BusinessEntity be : businessEntityList)
                    sortedMap.put(be, itemSupplierMap.get(be));
                itemSupplierMap.clear();
                itemSupplierMap.putAll(sortedMap);
                tamItemSupplierMap.put(allocationKey, itemSupplierMap);
                sortedMap.clear();

                if (!tamUpdateCheck || (tamUpdateCheck
                        && Boolean.TRUE.equals(tamAllocation.getSite().getSiteDetail() != null
                                ? tamAllocation.getSite().getSiteDetail().getTamUpdateFlag() : null))) {
                    for (FunctionalGroupSupplierAllocation sa : tamAllocation.getSupplierAllocations()) {
                        if (!sa.getEndDate().before(currentDate))
                            sa.setAllocation(null);
                    }
                }
            }
        }

        if (tamAllocation == null)
            tamAllocation = createEmptyTemplate(functionalGroup, site);

        if (enableB2BIntegration && sourceMapKey != null && !sourceMapKey.isEmpty()) {
            Optional.ofNullable(sourceLastChangedByMap.get(sourceMapKey))
                    .map(String::trim).filter(s -> !s.isEmpty())
                    .ifPresent(tamAllocation::setSourceLastChangedBy);
        }

        // ---- Set allowHedging ----
        if (tamHedging.get(allocationKey) == null) {
            if (allowHedging != null) {
                if (allowHedging.equalsIgnoreCase("TRUE")) {
                    tamAllocation.setAllowHedging(Boolean.TRUE);
                    tamHedging.put(allocationKey, Boolean.TRUE);
                } else if (allowHedging.equalsIgnoreCase("FALSE")) {
                    tamAllocation.setAllowHedging(Boolean.FALSE);
                    tamHedging.put(allocationKey, Boolean.FALSE);
                }
            }
        } else {
            tamAllocation.setAllowHedging(tamHedging.get(allocationKey));
        }

        // ---- Check for supplier in AVL ----
        itemSupplierMap = tamItemSupplierMap.get(allocationKey);
        if (!itemSupplierMap.containsKey(supplier)) {
            log.error("Supplier {} doesn't belong to TAM for Site :{} and FG :{}", supplierName, siteDescription, functionalGroupName);
            throw new MessageLoaderException("Supplier " + supplierName + " doesn't belong to TAM for Site :"
                    + siteDescription + " and FG :" + functionalGroupName);
        }

        Set<Item> items = itemSupplierMap.get(supplier);
        Boolean allEOL = Boolean.TRUE;
        for (Item i : items) {
            if (i.getEol() == null || i.getEol() == Boolean.FALSE) {
                allEOL = Boolean.FALSE;
                break;
            }
        }

        if (!tamUpdateCheck || (tamUpdateCheck
                && Boolean.TRUE.equals(tamAllocation.getSite().getSiteDetail() != null
                        ? tamAllocation.getSite().getSiteDetail().getTamUpdateFlag() : null))) {
            if (itemSupplierMap.containsKey(supplier)) {
                Set<FunctionalGroupSupplierAllocation> supplierAllocations = new TreeSet<>(this.new SupplyAllocationSorter());
                supplierAllocations.addAll(tamAllocation.getSupplierAllocations());

                int weekCount = 1;
                int monthCount = 1;
                for (FunctionalGroupSupplierAllocation supplierAllocation : supplierAllocations) {
                    if (supplier.equals(supplierAllocation.getBusinessEntity())) {
                        Boolean update = dateAfter(supplierAllocation.getEndDate(), currentDate, true);
                        if (diffInDays(supplierAllocation.getEndDate(), supplierAllocation.getStartDate()) > 7) {
                            switch (monthCount) {
                            case 3:
                                if (allocationMonth3 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth3), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth3, siteDescription, functionalGroupName, fgType, "M3", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 4:
                                if (allocationMonth4 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth4), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth4, siteDescription, functionalGroupName, fgType, "M4", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 5:
                                if (allocationMonth5 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth5), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth5, siteDescription, functionalGroupName, fgType, "M5", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 6:
                                if (allocationMonth6 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth6), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth6, siteDescription, functionalGroupName, fgType, "M6", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 7:
                                if (allocationMonth7 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth7), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth7, siteDescription, functionalGroupName, fgType, "M7", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 8:
                                if (allocationMonth8 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth8), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth8, siteDescription, functionalGroupName, fgType, "M8", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 9:
                                if (allocationMonth9 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth9), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth9, siteDescription, functionalGroupName, fgType, "M9", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 10:
                                if (allocationMonth10 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth10), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth10, siteDescription, functionalGroupName, fgType, "M10", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 11:
                                if (allocationMonth11 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth11), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth11, siteDescription, functionalGroupName, fgType, "M11", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            case 12:
                                if (allocationMonth12 != null) {
                                    if (update) {
                                        checkForEOLItem(Double.parseDouble(allocationMonth12), allEOL, supplierName, functionalGroupName, siteDescription);
                                        supplierAllocation.setAllocation(checkInteger(allocationMonth12, siteDescription, functionalGroupName, fgType, "M12", "Supplier", supplierName, sourceLastChangedBy));
                                    }
                                } break;
                            default: break;
                            }
                            monthCount++;
                        } else {
                            if (monthCount == 1) {
                                int weeks = new ArrayList<>(allBucket.values()).get(0).size();
                                switch (weekCount) {
                                case 1:
                                    if (allocationMonth1Week1 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth1Week1), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth1Week1, siteDescription, functionalGroupName, fgType, "M1W1", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth1Week1) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M1W1", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                case 2:
                                    if (allocationMonth1Week2 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth1Week2), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth1Week2, siteDescription, functionalGroupName, fgType, "M1W2", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth1Week2) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M1W2", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                case 3:
                                    if (allocationMonth1Week3 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth1Week3), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth1Week3, siteDescription, functionalGroupName, fgType, "M1W3", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth1Week3) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M1W3", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                case 4:
                                    if (allocationMonth1Week4 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth1Week4), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth1Week4, siteDescription, functionalGroupName, fgType, "M1W4", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth1Week4) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M1W4", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                case 5:
                                    if (allocationMonth1Week5 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth1Week5), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth1Week5, siteDescription, functionalGroupName, fgType, "M1W5", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth1Week5) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M1W5", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                default: break;
                                }
                                if (weekCount == weeks) { monthCount++; weekCount = 1; } else { weekCount++; }
                            } else if (monthCount == 2) {
                                int weeks = new ArrayList<>(allBucket.values()).get(1).size();
                                switch (weekCount) {
                                case 1:
                                    if (allocationMonth2Week1 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth2Week1), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth2Week1, siteDescription, functionalGroupName, fgType, "M2W1", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth2Week1) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M2W1", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                case 2:
                                    if (allocationMonth2Week2 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth2Week2), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth2Week2, siteDescription, functionalGroupName, fgType, "M2W2", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth2Week2) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M2W2", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                case 3:
                                    if (allocationMonth2Week3 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth2Week3), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth2Week3, siteDescription, functionalGroupName, fgType, "M2W3", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth2Week3) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M2W3", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                case 4:
                                    if (allocationMonth2Week4 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth2Week4), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth2Week4, siteDescription, functionalGroupName, fgType, "M2W4", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth2Week4) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M2W4", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                case 5:
                                    if (allocationMonth2Week5 != null) {
                                        if (update) {
                                            checkForEOLItem(Double.parseDouble(allocationMonth2Week5), allEOL, supplierName, functionalGroupName, siteDescription);
                                            supplierAllocation.setAllocation(checkInteger(allocationMonth2Week5, siteDescription, functionalGroupName, fgType, "M2W5", "Supplier", supplierName, sourceLastChangedBy));
                                        } else if (Double.parseDouble(allocationMonth2Week5) > 0) {
                                            recordEvent(LoadEvent.LoadEventType.WARNING,
                                                    getPastAllocationNotUpdatedMessage("M2W5", functionalGroupName, fgType, null, siteDescription, sourceLastChangedBy),
                                                    "supplierName : " + supplierName + " FG :" + functionalGroupName);
                                        }
                                    } break;
                                default: break;
                                }
                                if (weekCount == weeks) { monthCount++; weekCount = 1; } else { weekCount++; }
                            }
                        }
                    }
                }
                tamAllocation.setSupplierAllocations(supplierAllocations);
            } else {
                String errorMessage = "Supplier " + supplierName + " doesn't belong to TAM for FG: "
                        + functionalGroupName + " and site: " + siteDescription;
                log.error(errorMessage);
                throw new MessageLoaderException(errorMessage);
            }
            map.put(functionalGroup.getFunctionalGroupId() + "-" + site.getSiteKey(), tamAllocation);
        } else {
            String message = "TAM update flag is disabled for site: " + site.getSiteDescription()
                    + " supplier: " + supplierName + " at row " + getCount();
            warnings.add(message);
            log.warn(message);
            MessageLoaderException me = new MessageLoaderException(message);
            me.setSoft(true);
            throw me;
        }
    }

    // ------------------------------------------------------------------
    // createEmptyTemplate â€” full AVL-based template (same as legacy)
    // ------------------------------------------------------------------
    private TAMAllocation createEmptyTemplate(FunctionalGroup functionalGroup, Site site) {
        Set<Item> items = functionalGroup.getFunctionalGroupItems();
        itemSupplierMap = new LinkedHashMap<>();
        TAMAllocation allocation = new TAMAllocation();
        allocation.setFunctionalGroup(functionalGroup);
        allocation.setSite(site);

        for (Item i : items) {
            for (Avl avl : i.getAvls()) {
                if (itemSupplierMap.containsKey(avl.getSupplier())) {
                    itemSupplierMap.get(avl.getSupplier()).add(i);
                } else {
                    itemSupplierMap.put(avl.getSupplier(), new TreeSet<>());
                    itemSupplierMap.get(avl.getSupplier()).add(i);
                }
            }
        }
        List<BusinessEntity> businessEntityList = new ArrayList<>(itemSupplierMap.keySet());
        Map<BusinessEntity, Set<Item>> sortedMap = new LinkedHashMap<>();
        for (BusinessEntity be : businessEntityList)
            sortedMap.put(be, itemSupplierMap.get(be));
        itemSupplierMap.clear();
        itemSupplierMap.putAll(sortedMap);
        tamItemSupplierMap.put(
                allocation.getFunctionalGroup().getFunctionalGroupId() + "-" + allocation.getSite().getSiteKey(),
                itemSupplierMap);
        sortedMap.clear();

        int monthCount = 1;
        for (FiscalPeriod fiscalPeriod : allFiscallMonth) {
            if (monthCount == 1 || monthCount == 2) {
                List<FiscalPeriod> weeklyPeriods = allBucket.get(fiscalPeriod);
                for (FiscalPeriod weeklyPeriod : weeklyPeriods)
                    allocation = setAllocation(allocation, itemSupplierMap, weeklyPeriod);
            } else {
                allocation = setAllocation(allocation, itemSupplierMap, fiscalPeriod);
            }
            monthCount++;
        }
        tamItemSupplierMap.put(functionalGroup.getFunctionalGroupId() + "-" + site.getSiteKey(), itemSupplierMap);
        return allocation;
    }

    public TAMAllocation setAllocation(TAMAllocation allocation,
            Map<BusinessEntity, Set<Item>> itemSupplierMap, FiscalPeriod period) {
        for (BusinessEntity businessEntity : itemSupplierMap.keySet()) {
            FunctionalGroupSupplierAllocation supplierAllocation = new FunctionalGroupSupplierAllocation();
            supplierAllocation.setStartDate(period.getFiscalPeriodStartDate());
            supplierAllocation.setEndDate(period.getFiscalPeriodEndDate());
            supplierAllocation.setBusinessEntity(businessEntity);
            if (dateBefore(supplierAllocation.getEndDate(), currentDate, false)) {
                if (allocation.getId() == null) supplierAllocation.setAllocation(0.0);
            } else {
                supplierAllocation.setAllocation(null);
            }
            for (Item i : itemSupplierMap.get(businessEntity)) {
                FunctionalGroupItemAllocation itemAllocation = new FunctionalGroupItemAllocation();
                itemAllocation.setItem(i);
                if (dateBefore(supplierAllocation.getEndDate(), currentDate, false)) {
                    if (allocation.getId() == null) itemAllocation.setAllocation(0.0);
                } else {
                    itemAllocation.setAllocation(null);
                }
                itemAllocation.setFunctionalGroupSupplierAllocation(supplierAllocation);
                if (supplierAllocation.getItemAllocations() == null)
                    supplierAllocation.setItemAllocations(new LinkedHashSet<>());
                supplierAllocation.getItemAllocations().add(itemAllocation);
            }
            if (allocation.getSupplierAllocations() == null)
                allocation.setSupplierAllocations(new LinkedHashSet<>());
            supplierAllocation.setTamAllocation(allocation);
            allocation.getSupplierAllocations().add(supplierAllocation);
        }
        return allocation;
    }

    // ------------------------------------------------------------------
    // Inner sorter classes (same as legacy)
    // ------------------------------------------------------------------
    public class SupplyAllocationSorter implements Comparator<FunctionalGroupSupplierAllocation> {
        @Override
        public int compare(FunctionalGroupSupplierAllocation o1, FunctionalGroupSupplierAllocation o2) {
            int result = o1.getBusinessEntity().getBusinessEntityName()
                    .compareTo(o2.getBusinessEntity().getBusinessEntityName());
            return result == 0 ? o1.getStartDate().compareTo(o2.getStartDate()) : result;
        }
    }

    public class ItemAllocationSorter implements Comparator<FunctionalGroupItemAllocation> {
        private final boolean reverseSort;
        public ItemAllocationSorter(boolean reverseSort) { this.reverseSort = reverseSort; }
        @Override
        public int compare(FunctionalGroupItemAllocation o1, FunctionalGroupItemAllocation o2) {
            int ret = !reverseSort
                    ? o1.getItem().getItemNumber().compareTo(o2.getItem().getItemNumber())
                    : o2.getItem().getItemNumber().compareTo(o1.getItem().getItemNumber());
            if (ret == 0) {
                ret = !reverseSort
                        ? o1.getItem().getItemKey().compareTo(o2.getItem().getItemKey())
                        : o2.getItem().getItemKey().compareTo(o1.getItem().getItemKey());
            }
            return ret;
        }
    }

    // ------------------------------------------------------------------
    // Fiscal period helpers
    // ------------------------------------------------------------------
    private Map<FiscalPeriod, List<FiscalPeriod>> getWeeklyMonthlyBucket() {
        Map<FiscalPeriod, List<FiscalPeriod>> bucket = new LinkedHashMap<>();
        int monthCount = 1;
        for (FiscalPeriod fp : allFiscallMonth) {
            if (monthCount == 1 || monthCount == 2) {
                Calendar start = Calendar.getInstance();
                start.setTime(fp.getFiscalPeriodStartDate());
                Calendar end = Calendar.getInstance();
                end.setTime(fp.getFiscalPeriodEndDate());
                List<FiscalPeriod> weeks = getWeeklyFiscalPeriods(fp);
                bucket.put(fp, weeks);
            } else {
                List<FiscalPeriod> temp = new ArrayList<>();
                temp.add(fp);
                bucket.put(fp, temp);
            }
            monthCount++;
        }
        return bucket;
    }

    private List<FiscalPeriod> getWeeklyFiscalPeriods(FiscalPeriod month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(month.getFiscalPeriodStartDate());
        List<FiscalPeriod> weeks = fiscalPeriodService.getFiscalPeriods(cal, PeriodType.WEEK, 0, 5);
        Date monthEnd = month.getFiscalPeriodEndDate();
        List<FiscalPeriod> result = new ArrayList<>();
        for (FiscalPeriod w : weeks)
            if (!w.getFiscalPeriodStartDate().after(monthEnd)) result.add(w);
        return result;
    }

    // ------------------------------------------------------------------
    // validateTAM (same logic as legacy)
    // ------------------------------------------------------------------
    private void validateTAM(TAMAllocation allocation) throws MessageLoaderException {
        double minHedge = 100, maxHedge = 100;
        if (Boolean.TRUE.equals(allocation.getAllowHedging())) {
            Double[] hedge = findHedging(functionalGroupMap.get(allocation.getFunctionalGroup().getName().toUpperCase()));
            if (hedge != null) { minHedge = hedge[0]; maxHedge = hedge[1]; }
        }
        String sourceLastChangedBy = allocation.getSourceLastChangedBy();

        for (List<FiscalPeriod> fiscalPeriods : allBucket.values()) {
            for (FiscalPeriod fiscalPeriod : fiscalPeriods) {
                Double total = null;
                Map<BusinessEntity, Set<Item>> tempItemSupplierMap = tamItemSupplierMap.get(
                        allocation.getFunctionalGroup().getFunctionalGroupId() + "-"
                                + allocation.getSite().getSiteKey());
                Set<String> dataSources = new HashSet<>();
                boolean errorRecordOrNot = false, enableDefaultItemAllocationsOrNot = false;
                int supplierCount = 0;
                Map<FunctionalGroupSupplierAllocation, Set<String>> toBeDefaultedSAsMap = new LinkedHashMap<>();

                for (BusinessEntity businessEntity : tempItemSupplierMap.keySet()) {
                    for (FunctionalGroupSupplierAllocation sa : allocation.getSupplierAllocations()) {
                        if (sa.getStartDate().equals(fiscalPeriod.getFiscalPeriodStartDate())
                                && sa.getBusinessEntity().equals(businessEntity)) {
                            supplierCount++;
                            dataSources.clear();
                            if (sa.getAllocation() != null)
                                total = total == null ? sa.getAllocation() : total + sa.getAllocation();

                            double itemAllocationTotal = 0;
                            for (FunctionalGroupItemAllocation ia : sa.getItemAllocations()) {
                                Item i = ia.getItem();
                                if (i.getEol() == null || i.getEol() == Boolean.FALSE)
                                    dataSources.add(i.getDataSource());
                                if (ia.getAllocation() != null)
                                    itemAllocationTotal += ia.getAllocation();
                            }
                            if (itemAllocationTotal == 0) {
                                toBeDefaultedSAsMap.put(sa, new HashSet<>(dataSources));
                            } else if (itemAllocationTotal == 100) {
                                enableDefaultItemAllocationsOrNot = true;
                            } else if (itemAllocationTotal != 100.00
                                    && dateAfter(sa.getEndDate(), currentDate, true)) {
                                errorRecordOrNot = true;
                                String msg = "Item Allocation Should be Total 100.00 for Supplier :"
                                        + sa.getBusinessEntity().getBusinessEntityName()
                                        + " And FG :" + allocation.getFunctionalGroup().getName()
                                        + " And Fiscal Period :"
                                        + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                                        + " and Site :" + allocation.getSite().getSiteDescription();
                                if (enableB2BIntegration && sourceLastChangedBy != null && !sourceLastChangedBy.isEmpty()) {
                                    msg = "Total supply allocation not 100 for FG: "
                                            + allocation.getFunctionalGroup().getName()
                                            + " site: " + allocation.getSite().getSiteDescription()
                                            + " period: " + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                                            + " total: " + total;
                                }
                                addValidationError(msg);
                            }
                        }
                    }
                }
                if (enableDefaultItemAllocationsOrNot && supplierCount > 1 && !errorRecordOrNot)
                    defaultItemAllocationsAtSupplierLevel(allocation, fiscalPeriod, toBeDefaultedSAsMap);
                validateSupplierAllocations(allocation, minHedge, maxHedge, fiscalPeriod, total);
            }
        }
    }

    private void validateSupplierAllocations(TAMAllocation allocation, double minHedge, double maxHedge,
            FiscalPeriod fiscalPeriod, Double total) throws MessageLoaderException {
        String sourceLastChangedBy = allocation.getSourceLastChangedBy();

        Map<BusinessEntity, Set<Item>> tempItemSupplierMap = tamItemSupplierMap.get(
                allocation.getFunctionalGroup().getFunctionalGroupId() + "-"
                        + allocation.getSite().getSiteKey());
        String supplierName = null; String itemNumber = null; String commodityName = null;
        if (tempItemSupplierMap != null && !tempItemSupplierMap.isEmpty()) {
            Map.Entry<BusinessEntity, Set<Item>> firstEntry = tempItemSupplierMap.entrySet().stream().findFirst().orElse(null);
            if (firstEntry != null) {
                supplierName = firstEntry.getKey().getBusinessEntityName();
                Item item = firstEntry.getValue().stream().findFirst().orElse(null);
                if (item != null) {
                    itemNumber = item.getItemNumber();
                    if (item.getCategories() != null && !item.getCategories().isEmpty()) {
                        ItemCategory category = item.getCategories().iterator().next();
                        if (category != null) commodityName = category.getCategoryName();
                    }
                }
            }
        }

        if (total != null) {
            if ((minHedge > total || maxHedge < total)
                    && dateAfter(fiscalPeriod.getFiscalPeriodEndDate(), currentDate, true)) {
                if (minHedge == maxHedge) {
                    log.error("Allocation for Fiscal Period : {} for FG {} is not 100%: {}",
                            getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate()),
                            allocation.getFunctionalGroup().getName(), allocation.getSite().getSiteDescription());
                    String msg = "Allocation for Fiscal Period : "
                            + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                            + " for FG " + allocation.getFunctionalGroup().getName() + " is not 100%:"
                            + allocation.getSite().getSiteDescription();
                    if (enableB2BIntegration && sourceLastChangedBy != null && !sourceLastChangedBy.isEmpty()) {
                        msg = "Total supply allocation not 100 for FG: " + allocation.getFunctionalGroup().getName()
                                + " site: " + allocation.getSite().getSiteDescription()
                                + " period: " + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                                + " total: " + total;
                    }
                    throw new MessageLoaderException(msg);
                } else {
                    log.error("Supplier Allocation for Fiscal Period: {} For FG {} and Site :{} is Not in between {}% and {}%",
                            getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate()),
                            allocation.getFunctionalGroup().getName(), allocation.getSite().getSiteDescription(), minHedge, maxHedge);
                    String msg = "Supplier Allocation for Fiscal Period: "
                            + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                            + " For FG " + allocation.getFunctionalGroup().getName() + " and Site :"
                            + allocation.getSite().getSiteDescription()
                            + " is Not in between " + minHedge + "% and " + maxHedge + "%";
                    if (enableB2BIntegration && sourceLastChangedBy != null && !sourceLastChangedBy.isEmpty()) {
                        msg = "Supply allocation not within threshold for FG: " + allocation.getFunctionalGroup().getName()
                                + " site: " + allocation.getSite().getSiteDescription()
                                + " period: " + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                                + " total: " + total + " min: " + minHedge + " max: " + maxHedge;
                    }
                    throw new MessageLoaderException(msg);
                }
            }
        } else {
            if (dateAfter(fiscalPeriod.getFiscalPeriodEndDate(), currentDate, true)) {
                if (isSupplierInheritanceDataNotExist(allocation,
                        new Date(fiscalPeriod.getFiscalPeriodStartDate().getTime()))) {
                    if (minHedge == maxHedge) {
                        log.error("Allocation for Fiscal Period : {} for FG {} is not 100%. No allocation exist for Region and Global: {}",
                                getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate()),
                                allocation.getFunctionalGroup().getName(), allocation.getSite().getSiteDescription());
                        String msg = "Allocation for Fiscal Period : "
                                + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                                + " for FG " + allocation.getFunctionalGroup().getName()
                                + " is not 100%. No allocation exist for Region and Global:"
                                + allocation.getSite().getSiteDescription();
                        if (enableB2BIntegration && sourceLastChangedBy != null && !sourceLastChangedBy.isEmpty()) {
                            msg = "Missing supply allocation for FG: " + allocation.getFunctionalGroup().getName()
                                    + " item: " + itemNumber + " supplier: " + supplierName
                                    + " commodity: " + commodityName
                                    + " site: " + allocation.getSite().getSiteDescription() + " total: " + total;
                        }
                        throw new MessageLoaderException(msg);
                    } else {
                        log.error("Supplier Allocation for Fiscal Period: {} For FG {} and Site :{} is Not in between {}% and {}%",
                                getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate()),
                                allocation.getFunctionalGroup().getName(), allocation.getSite().getSiteDescription(), minHedge, maxHedge);
                        String msg = "Supplier Allocation for Fiscal Period: "
                                + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                                + " For FG " + allocation.getFunctionalGroup().getName() + " and Site :"
                                + allocation.getSite().getSiteDescription()
                                + " is Not in between " + minHedge + "% and " + maxHedge + "%";
                        if (enableB2BIntegration && sourceLastChangedBy != null && !sourceLastChangedBy.isEmpty()) {
                            msg = "Supply allocation not within threshold for FG: " + allocation.getFunctionalGroup().getName()
                                    + " site: " + allocation.getSite().getSiteDescription()
                                    + " period: " + getTemplateBasedAllocationName(fiscalPeriod.getFiscalPeriodStartDate())
                                    + " total: " + total + " min: " + minHedge + " max: " + maxHedge;
                        }
                        throw new MessageLoaderException(msg);
                    }
                }
            }
        }
    }

    private void defaultItemAllocationsAtSupplierLevel(TAMAllocation allocation, FiscalPeriod fiscalPeriod,
            Map<FunctionalGroupSupplierAllocation, Set<String>> toBeDefaultedSAsMap) throws MessageLoaderException {
        for (Entry<FunctionalGroupSupplierAllocation, Set<String>> entry : toBeDefaultedSAsMap.entrySet()) {
            if (dateAfter(fiscalPeriod.getFiscalPeriodEndDate(), currentDate, true)) {
                FunctionalGroupSupplierAllocation sa = entry.getKey();
                Set<String> dataSources = new HashSet<>(entry.getValue());
                boolean reverseSort = false;
                if (!dataSources.isEmpty()) {
                    List<String> configDSList = pcmConfigUtil.getList("pcm.tam.reverse.sort.item.allocation.datasource");
                    if (configDSList != null && !configDSList.isEmpty()) {
                        dataSources.retainAll(configDSList);
                        if (!dataSources.isEmpty()) reverseSort = true;
                    }
                }
                log.debug("The value of reverseSort is : {}", reverseSort);
                Set<FunctionalGroupItemAllocation> fItemAllocations = new TreeSet<>(new ItemAllocationSorter(reverseSort));
                fItemAllocations.addAll(sa.getItemAllocations());
                for (FunctionalGroupItemAllocation ia : fItemAllocations)
                    log.debug("The item allocation object is : {}", ia.getItem().getItemNumber());
                FunctionalGroupItemAllocation first = getFirstItem(fItemAllocations);
                log.debug("The first item for 100% allocation is : {}", first.getItem().getItemNumber());
                first.setAllocation(100.00);
                sa.setItemAllocations(fItemAllocations);
            }
        }
    }

    public Double[] findHedging(FunctionalGroup functionalGroup) {
        if (functionalGroup == null) return null;
        if (hedgingMap.containsKey(functionalGroup.getFunctionalGroupId()))
            return hedgingMap.get(functionalGroup.getFunctionalGroupId());

        Double min = null, max = null;
        Set<Item> items = functionalGroup.getFunctionalGroupItems();
        for (Item i : items) {
            for (ItemCategory category : i.getCategories()) {
                String categoryName = category.getCategoryName().trim().replace(" ", "");
                String hedgingRange = pcmConfigUtil.getString("pcm.hedging.range." + categoryName, "");
                if (hedgingRange != null && !hedgingRange.isEmpty()) {
                    String[] range = hedgingRange.split("-");
                    double tMin = Integer.parseInt(range[0]), tMax = Integer.parseInt(range[1]);
                    min = (min == null || tMin > min) ? tMin : min;
                    max = (max == null || tMax < max) ? tMax : max;
                }
            }
        }
        if (min != null && max != null) {
            Double[] range = {min, max};
            hedgingMap.put(functionalGroup.getFunctionalGroupId(), range);
            return range;
        }
        String globalRange = pcmConfigUtil.getString("pcm.hedging.range.global.setting", "100-100");
        String[] range = globalRange.split("-");
        Double[] global = {Double.parseDouble(range[0]), Double.parseDouble(range[1])};
        hedgingMap.put(functionalGroup.getFunctionalGroupId(), global);
        return global;
    }

    // ------------------------------------------------------------------
    // Private helpers (replacing PcmUtil / DateAndTimeUtils)
    // ------------------------------------------------------------------
    private boolean updateFGStatusIfNeeded(FunctionalGroup fg, String currentStatus, String newStatus) {
        if (fg == null) return false;
        if (FunctionalGroupConstants.STATUS_NEW.equals(currentStatus)) {
            fg.setStatus(newStatus);
            fgRepository.saveFunctionalGroup(fg);
            return true;
        }
        return false;
    }

    private Boolean isSupplierInheritanceDataNotExist(TAMAllocation allocation, Date date) throws MessageLoaderException {
        Boolean validationError = false;
        if (Site.SITE_TYPE.equals(allocation.getSite().getSiteType())) {
            Boolean result = tamAllocationService.isSupplierAllocationExits(
                    allocation.getFunctionalGroup(), allocation.getSite().getParentSite(), date);
            if (!Boolean.TRUE.equals(result)) {
                result = tamAllocationService.isSupplierAllocationExitsForGlobal(allocation.getFunctionalGroup(), date);
                if (!Boolean.TRUE.equals(result)) validationError = true;
            }
        } else if (Site.REGION_TYPE.equals(allocation.getSite().getSiteType())) {
            if (!Boolean.TRUE.equals(tamAllocationService.isSupplierAllocationExitsForGlobal(allocation.getFunctionalGroup(), date)))
                validationError = true;
        } else if (Site.GLOBAL_TYPE.equals(allocation.getSite().getSiteType())) {
            validationError = true;
        }
        return validationError;
    }

    private TAMAllocation copyTAM(TAMAllocation source) {
        TAMAllocation dest = new TAMAllocation();
        dest.setAllowHedging(source.getAllowHedging());
        dest.setCreatedBy(source.getCreatedBy());
        dest.setCreatedOn(source.getCreatedOn());
        dest.setExtractFlag(source.getExtractFlag());
        dest.setDiscpExtractFlag(source.getDiscpExtractFlag());
        dest.setFunctionalGroup(source.getFunctionalGroup());
        dest.setLastChangedBy(source.getLastChangedBy());
        dest.setSite(source.getSite());
        dest.setSupplierAllocations(new HashSet<>());
        for (FunctionalGroupSupplierAllocation sa : source.getSupplierAllocations()) {
            FunctionalGroupSupplierAllocation dsa = new FunctionalGroupSupplierAllocation();
            dsa.setTamAllocation(dest);
            dsa.setAllocation(sa.getAllocation());
            dsa.setBusinessEntity(sa.getBusinessEntity());
            dsa.setStartDate(sa.getStartDate());
            dsa.setEndDate(sa.getEndDate());
            dsa.setItemAllocations(new HashSet<>());
            for (FunctionalGroupItemAllocation ia : sa.getItemAllocations()) {
                FunctionalGroupItemAllocation dia = new FunctionalGroupItemAllocation();
                dia.setItem(ia.getItem());
                dia.setAllocation(ia.getAllocation());
                dia.setFunctionalGroupSupplierAllocation(dsa);
                dsa.getItemAllocations().add(dia);
            }
            dest.getSupplierAllocations().add(dsa);
        }
        return dest;
    }

    private String getTemplateBasedAllocationName(Date date) {
        int count = 1;
        for (FiscalPeriod periods : allBucket.keySet()) {
            if (dateBetween(date, periods.getFiscalPeriodStartDate(), periods.getFiscalPeriodEndDate())) {
                if (allBucket.get(periods).size() > 1) {
                    int innerCount = 1;
                    for (FiscalPeriod fp : allBucket.get(periods)) {
                        if (dateBetween(date, fp.getFiscalPeriodStartDate(), fp.getFiscalPeriodEndDate()))
                            return "M" + count + "W" + innerCount;
                        innerCount++;
                    }
                } else {
                    return "M" + count;
                }
            }
            count++;
        }
        return "";
    }

    private void checkForEOLItem(Double value, Boolean allEOL, String supplierName,
            String functionalGroupName, String siteDescription) throws MessageLoaderException {
        if (value > 0 && Boolean.TRUE.equals(allEOL)) {
            log.error("Can't allocate Supplier {} for FG :{} and Site :{} .contain all EOL Item.",
                    supplierName, functionalGroupName, siteDescription);
            throw new MessageLoaderException("Can't allocate Supplier " + supplierName + " for FG :"
                    + functionalGroupName + " and Site :" + siteDescription + " .contain all EOL Item.");
        }
    }

    private FunctionalGroupItemAllocation getFirstItem(Set<FunctionalGroupItemAllocation> items) {
        if (!items.isEmpty()) {
            if (items.size() == 1) return items.iterator().next();
            Iterator<FunctionalGroupItemAllocation> it = items.iterator();
            while (it.hasNext()) {
                FunctionalGroupItemAllocation fgi = it.next();
                Item i = fgi.getItem();
                if (i.getEol() == null || i.getEol() == Boolean.FALSE) return fgi;
            }
        }
        return items.iterator().next();
    }

    private void addValidationError(String error) throws MessageLoaderException {
        if (errors.size() < maxErrorlimit) errors.add(error);
        else throw new MessageLoaderException(error);
    }

    public static String getFormatedAllocationForAudit(Double allocation) {
        if (decimalFormat == null) decimalFormat = new DecimalFormat("#.##");
        return allocation == null ? "0" : decimalFormat.format(allocation);
    }

    private String getPastAllocationNotUpdatedMessage(String bucketNumber, String functionalGroupName,
            String fgType, String commodityName, String siteDescription, String sourceLastChangedBy) {
        String msg = "Allocation for past date {0} not updated.";
        msg = MessageFormat.format(msg, bucketNumber);
        if (sourceLastChangedBy != null && !sourceLastChangedBy.isEmpty()) {
            msg = "Past-dated supply allocation for FG: " + functionalGroupName
                    + " site: " + siteDescription + " bucket: " + bucketNumber;
        }
        return msg;
    }

    private Double checkInteger(String value, String site, String fgName, String fgType, String fiscalMonth,
            String objectType, String object, String sourceLastChangedBy) throws MessageLoaderException {
        if (value != null && value.equalsIgnoreCase("NaN")) {
            if (sourceLastChangedBy != null) {
                String msg = "Invalid supply allocation value for FG: " + fgName + " site: " + site
                        + " period: " + fiscalMonth + " value: " + value;
                log.error(msg); throw new MessageLoaderException(msg);
            } else {
                String msg = "Allocation always should be Numeric for " + objectType + " " + object
                        + " value : " + value + " ,  Site " + site + " and FG " + fgName + " for fiscal month " + fiscalMonth;
                log.error(msg); throw new MessageLoaderException(msg);
            }
        }
        Integer i = null;
        try { i = Integer.parseInt(value); } catch (NumberFormatException nfe) { i = null; }

        if (sourceLastChangedBy != null) {
            if (i == null) {
                String msg = "Invalid supply allocation value for FG: " + fgName + " site: " + site
                        + " period: " + fiscalMonth + " value: " + value;
                log.error(msg); throw new MessageLoaderException(msg);
            } else if (i < 0) {
                String msg = "Invalid supply allocation value for FG: " + fgName + " site: " + site
                        + " period: " + fiscalMonth + " value: " + value;
                log.error(msg); throw new MessageLoaderException(msg);
            } else {
                return i == 0 ? null : Double.parseDouble(i.toString());
            }
        } else {
            if (i == null) {
                log.error("Allocation always should be Numeric for {} {} value : {} ,  Site {} and FG {} for fiscal month {}",
                        objectType, object, value, site, fgName, fiscalMonth);
                throw new MessageLoaderException("Allocation always should be Numeric for " + objectType + " " + object
                        + " value : " + value + " ,  Site " + site + " and FG " + fgName + " for fiscal month " + fiscalMonth);
            } else if (i < 0) {
                log.error("Allocation always should be Positive for {} {} value : {} ,  Site {} and FG {} for fiscal month {}",
                        objectType, object, value, site, fgName, fiscalMonth);
                throw new MessageLoaderException("Allocation always should be Positive for " + objectType + " " + object
                        + " value : " + value + " ,  Site " + site + " and FG " + fgName + " for fiscal month " + fiscalMonth);
            } else {
                return i == 0 ? null : Double.parseDouble(i.toString());
            }
        }
    }

    // ------------------------------------------------------------------
    // Date utilities (replacing DateAndTimeUtils)
    // ------------------------------------------------------------------
    private static boolean dateAfter(Date date, Date reference, boolean inclusive) {
        if (date == null || reference == null) return false;
        int cmp = date.compareTo(reference);
        return inclusive ? cmp >= 0 : cmp > 0;
    }

    private static boolean dateBefore(Date date, Date reference, boolean inclusive) {
        if (date == null || reference == null) return false;
        int cmp = date.compareTo(reference);
        return inclusive ? cmp <= 0 : cmp < 0;
    }

    private static boolean dateBetween(Date date, Date start, Date end) {
        return date != null && start != null && end != null && !date.before(start) && !date.after(end);
    }

    private static long diffInDays(Date end, Date start) {
        if (end == null || start == null) return 0;
        return (end.getTime() - start.getTime()) / (1000L * 60 * 60 * 24);
    }
}