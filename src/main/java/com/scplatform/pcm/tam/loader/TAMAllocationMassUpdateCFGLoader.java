/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.tam.loader;

import com.scplatform.pcm.allocationAudit.entity.AllocationAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.TAMAuditHistory;
import com.scplatform.pcm.allocationAudit.repository.AllocationAuditHistoryRepository;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.repository.BusinessEntityRepository;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;
import com.scplatform.pcm.fiscalPeriod.repository.FiscalPeriodRepository;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.repository.SiteRepository;
import com.scplatform.pcm.tam.entity.FunctionalGroupSupplierAllocation;
import com.scplatform.pcm.tam.entity.TAMAllocation;
import com.scplatform.pcm.tam.repository.FunctionalGroupSupplierAllocationRepository;
import com.scplatform.pcm.tam.repository.TAMAllocationRepository;
import com.scplatform.pcm.upload.loader.BaseImporter;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TAMAllocationMassUpdateCFGLoader extends BaseImporter {

    private static final List<String> BUCKET_KEYS = List.of(
            "M1W1", "M1W2", "M1W3", "M1W4", "M1W5",
            "M2W1", "M2W2", "M2W3", "M2W4", "M2W5",
            "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "M11", "M12"
    );

    private static final String DEFAULT_SITE_TYPE = "SITE";

    int tamMassUpdateCount = 0;
    Map<Integer, String> errors = null;

    private final FunctionalGroupRepository fgRepository;
    private final TAMAllocationRepository tamAllocationRepository;
    private final FunctionalGroupSupplierAllocationRepository supplierAllocationRepository;
    private final BusinessEntityRepository businessEntityRepository;
    private final SiteRepository siteRepository;
    private final FiscalPeriodRepository fiscalPeriodRepository;
    private final AllocationAuditHistoryRepository auditRepository;

    public TAMAllocationMassUpdateCFGLoader(LoadJobRepository loadJobRepository,
            LoadEventRepository loadEventRepository,
            PcmConfigUtil pcmConfigUtil,
            FunctionalGroupRepository fgRepository,
            TAMAllocationRepository tamAllocationRepository,
            FunctionalGroupSupplierAllocationRepository supplierAllocationRepository,
            BusinessEntityRepository businessEntityRepository,
            SiteRepository siteRepository,
            FiscalPeriodRepository fiscalPeriodRepository,
            AllocationAuditHistoryRepository auditRepository) {
        super(loadJobRepository, loadEventRepository, pcmConfigUtil);
        this.fgRepository = fgRepository;
        this.tamAllocationRepository = tamAllocationRepository;
        this.supplierAllocationRepository = supplierAllocationRepository;
        this.businessEntityRepository = businessEntityRepository;
        this.siteRepository = siteRepository;
        this.fiscalPeriodRepository = fiscalPeriodRepository;
        this.auditRepository = auditRepository;
    }

    @Override
    public void updateStats(MessageLoaderStatus status) {
        status.setStatistic("tamMassUpdate", tamMassUpdateCount);
        status.setStatistic("LoadEvents", this.loadEventCount);
    }

    @Override
    public int getCount() {
        return tamMassUpdateCount;
    }

    // -----------------------------------------------------------------------
    // process() — main entry point
    // -----------------------------------------------------------------------

    @Override
    public void process(XMLStreamReader xmlr, int passNumber) throws Exception {
        StopWatch batchTimer = new StopWatch();
        tamMassUpdateCount = 0;
        errors = new LinkedHashMap<>();

        while (xmlr.hasNext()) {
            int event = xmlr.next();
            if (!xmlr.isStartElement() && !xmlr.isEndElement()) {
                continue;
            }
            if (log.isDebugEnabled() && xmlr.hasName()) {
                log.debug("{} {}", getEventTypeString(event), xmlr.getLocalName());
            }

            String localName = xmlr.getLocalName();

            if ("TAMMessage".equals(localName)) {
                if (xmlr.isStartElement()) {
                    batchTimer.start();
                    tamMassUpdateCount = 0;
                } else if (xmlr.isEndElement()) {
                    if (log.isInfoEnabled()) {
                        batchTimer.stop();
                        log.info("TAM Allocation Mass Update processed: {} ({} ms)",
                                tamMassUpdateCount, batchTimer.getTime());
                    }
                }
            } else if ("TAMAllocation".equals(localName)
                    || "TAMAllocationMRPSite".equals(localName)) {
                if (xmlr.isStartElement()) {
                    try {
                        tamMassUpdateCount++;
                        loadTAMMassUpdate(xmlr);
                    } catch (MessageLoaderException e) {
                        errors.merge(tamMassUpdateCount, e.getMessage(), (a, b) -> a + "|" + b);
                        skipUntilEnd(xmlr, localName);
                    }
                } else if (xmlr.isEndElement()) {
                    if (tamMassUpdateCount % getBatchSize() == 0 && log.isInfoEnabled()) {
                        batchTimer.split();
                        log.info("TAM Allocation Mass Update processed so far: {} ({} ms)",
                                tamMassUpdateCount, batchTimer.getSplitTime());
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            tamMassUpdateCount = 0;
            throw new MessageLoaderException(StringUtils.join(errors.values(), "\n"));
        }

        if (log.isInfoEnabled()) {
            log.info("Total TAM Allocation Mass Update processed: {}", tamMassUpdateCount);
            this.dumpCacheStats();
        }
    }

    // -----------------------------------------------------------------------
    // Record-level processing
    // -----------------------------------------------------------------------

    private void loadTAMMassUpdate(XMLStreamReader xmlr) throws MessageLoaderException {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        String fgName       = attr(xmlr, "functionalGroupName");
        String fgType       = attr(xmlr, "fgType");
        String siteDesc     = attr(xmlr, "siteDescription");
        String supplierName = attr(xmlr, "supplierName");
        String allowHedging = attr(xmlr, "allowHedging");
        String siteTypeAttr = attr(xmlr, "siteType");
        String siteType     = StringUtils.isNotBlank(siteTypeAttr) ? siteTypeAttr : DEFAULT_SITE_TYPE;

        if (StringUtils.isBlank(fgName)) {
            throw new MessageLoaderException("functionalGroupName is required for TAMAllocation");
        }
        if (StringUtils.isBlank(siteDesc)) {
            throw new MessageLoaderException("siteDescription is required for TAMAllocation");
        }

        // Resolve FunctionalGroup
        FunctionalGroup fg = StringUtils.isNotBlank(fgType)
                ? fgRepository.getFunctionalGroupByNameAndType(fgName, fgType)
                : fgRepository.getFunctionalGroupByName(fgName);
        if (fg == null) {
            throw new MessageLoaderException("Functional Group not found: " + fgName);
        }

        // Resolve Site — if no explicit siteType in XML, match by description only
        Optional<Site> siteOpt;
        if (StringUtils.isNotBlank(siteTypeAttr)) {
            siteOpt = siteRepository.findBySiteDescriptionAndSiteType(siteDesc, siteType);
        } else {
            List<Site> sites = siteRepository.findBySiteDescriptionOnly(siteDesc);
            siteOpt = sites.isEmpty() ? Optional.empty() : Optional.of(sites.get(0));
        }
        if (siteOpt.isEmpty()) {
            throw new MessageLoaderException("Site not found: " + siteDesc
                    + (StringUtils.isNotBlank(siteTypeAttr) ? " (type=" + siteType + ")" : ""));
        }
        Site site = siteOpt.get();

        // Resolve or create TAMAllocation
        TAMAllocation tamAllocation = tamAllocationRepository.getTAMAllocationByFGAndSiteWithoutFilter(fg, site);
        if (tamAllocation == null) {
            tamAllocation = new TAMAllocation();
            tamAllocation.setFunctionalGroup(fg);
            tamAllocation.setSite(site);
            tamAllocation.setCreatedBy(activeUserId);
            tamAllocation.setCreatedOn(now);
            tamAllocation.setExtractFlag("P");
        }

        // Update allowHedging if present
        if (StringUtils.isNotBlank(allowHedging)) {
            tamAllocation.setAllowHedging("Y".equalsIgnoreCase(allowHedging) || "true".equalsIgnoreCase(allowHedging));
        }
        tamAllocation.setLastChangedBy(activeUserId);
        tamAllocation.setLastChangedOn(now);
        tamAllocation = tamAllocationRepository.save(tamAllocation);

        // Resolve supplier if provided — mass update for specific supplier's allocation
        BusinessEntity supplier = null;
        if (StringUtils.isNotBlank(supplierName)) {
            List<BusinessEntity> suppliers = businessEntityRepository.findBusinessByName(supplierName, "Supplier");
            if (suppliers == null || suppliers.isEmpty()) {
                throw new MessageLoaderException("Supplier not found: " + supplierName);
            }
            supplier = suppliers.get(0);

            // Delete existing supplier allocations for this supplier using a targeted bulk-delete
            // (avoids iterating over a live collection and ConcurrentModificationException)
            supplierAllocationRepository.deleteByTamAllocationIdAndSupplierKey(
                    tamAllocation.getId(), supplier.getBusinessEntityKey());
        }

        // Persist new bucket allocations
        int bucketsProcessed = 0;
        for (String bucket : BUCKET_KEYS) {
            String rawVal = attr(xmlr, bucket);
            if (StringUtils.isBlank(rawVal)) {
                continue;
            }
            double allocationPct;
            try {
                allocationPct = Double.parseDouble(rawVal.trim());
            } catch (NumberFormatException e) {
                log.warn("Invalid allocation value '{}' for bucket {} in FG {}", rawVal, bucket, fgName);
                continue;
            }

            if (supplier == null) {
                log.warn("No supplier resolved for FG={} Bucket={} — skipping bucket", fgName, bucket);
                continue;
            }

            Optional<FiscalPeriod> periodOpt = fiscalPeriodRepository.findByFiscalPeriodName(bucket);
            if (periodOpt.isEmpty()) {
                log.warn("Fiscal period not found for bucket: {}", bucket);
                continue;
            }
            FiscalPeriod period = periodOpt.get();

            FunctionalGroupSupplierAllocation supplierAlloc = new FunctionalGroupSupplierAllocation();
            supplierAlloc.setTamAllocation(tamAllocation);
            supplierAlloc.setBusinessEntity(supplier);
            supplierAlloc.setAllocation(allocationPct);
            supplierAlloc.setStartDate(period.getFiscalPeriodStartDate());
            supplierAlloc.setEndDate(period.getFiscalPeriodEndDate());
            supplierAllocationRepository.save(supplierAlloc);

            TAMAuditHistory audit = new TAMAuditHistory(
                    activeUserId, getActiveUserRoleId(),
                    AllocationAuditHistory.ACTIONUPLOAD_UPDATE,
                    "UPLOAD_TAM_MASS_UPDATE_CFG",
                    tamAllocation, site, supplier,
                    period.getFiscalPeriodStartDate(), period.getFiscalPeriodEndDate(),
                    "TAM Allocation Mass Update: FG=" + fgName + " Supplier=" + supplierName
                            + " Bucket=" + bucket + " Alloc=" + allocationPct,
                    now);
            auditRepository.save(audit);
            bucketsProcessed++;
        }

        log.debug("TAMAllocationMassUpdateCFGLoader: FG={} Site={} Supplier={} — {} buckets persisted",
                fgName, siteDesc, supplierName, bucketsProcessed);
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private String attr(XMLStreamReader xmlr, String name) {
        String v = xmlr.getAttributeValue(null, name);
        return v != null ? v.trim() : "";
    }
}
