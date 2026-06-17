/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.tam.loader;

import com.scplatform.pcm.allocationAudit.entity.AllocationAuditHistory;
import com.scplatform.pcm.allocationAudit.entity.TAMAuditHistory;
import com.scplatform.pcm.allocationAudit.repository.AllocationAuditHistoryRepository;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.repository.SiteRepository;
import com.scplatform.pcm.tam.entity.FunctionalGroupItemAllocation;
import com.scplatform.pcm.tam.entity.FunctionalGroupSupplierAllocation;
import com.scplatform.pcm.tam.entity.TAMAllocation;
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
public class TAMAllocationDeleteLoader extends BaseImporter {

    int tamDeleteCount = 0;
    Map<Integer, String> errors = null;

    private final FunctionalGroupRepository fgRepository;
    private final TAMAllocationRepository tamAllocationRepository;
    private final SiteRepository siteRepository;
    private final AllocationAuditHistoryRepository auditRepository;

    public TAMAllocationDeleteLoader(LoadJobRepository loadJobRepository,
            LoadEventRepository loadEventRepository,
            PcmConfigUtil pcmConfigUtil,
            FunctionalGroupRepository fgRepository,
            TAMAllocationRepository tamAllocationRepository,
            SiteRepository siteRepository,
            AllocationAuditHistoryRepository auditRepository) {
        super(loadJobRepository, loadEventRepository, pcmConfigUtil);
        this.fgRepository = fgRepository;
        this.tamAllocationRepository = tamAllocationRepository;
        this.siteRepository = siteRepository;
        this.auditRepository = auditRepository;
    }

    // -----------------------------------------------------------------------
    // BaseImporter abstract implementations
    // -----------------------------------------------------------------------

    @Override
    public void updateStats(MessageLoaderStatus status) {
        status.setStatistic("tamDeleteCount", tamDeleteCount);
        status.setStatistic("LoadEvents", this.loadEventCount);
    }

    @Override
    public int getCount() {
        return tamDeleteCount;
    }

    // -----------------------------------------------------------------------
    // process() — main entry point
    // -----------------------------------------------------------------------

    @Override
    public void process(XMLStreamReader xmlr, int passNumber) throws Exception {
        StopWatch batchTimer = new StopWatch();
        tamDeleteCount = 0;
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

            if ("TAMAllocationDeleteMessage".equals(localName)) {
                if (xmlr.isStartElement()) {
                    batchTimer.start();
                    tamDeleteCount = 0;
                } else if (xmlr.isEndElement()) {
                    if (log.isInfoEnabled()) {
                        batchTimer.stop();
                        log.info("TAM Allocation Delete processed: {} ({} ms)",
                                tamDeleteCount, batchTimer.getTime());
                    }
                }
            } else if ("TAMAllocationDelete".equals(localName)) {
                if (xmlr.isStartElement()) {
                    try {
                        tamDeleteCount++;
                        loadTAMAllocationDelete(xmlr);
                    } catch (MessageLoaderException e) {
                        errors.merge(tamDeleteCount, e.getMessage(), (a, b) -> a + "|" + b);
                        skipUntilEnd(xmlr, localName);
                    }
                } else if (xmlr.isEndElement()) {
                    if (tamDeleteCount % getBatchSize() == 0 && log.isInfoEnabled()) {
                        batchTimer.split();
                        log.info("TAM Allocation Delete processed so far: {} ({} ms)",
                                tamDeleteCount, batchTimer.getSplitTime());
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            tamDeleteCount = 0;
            throw new MessageLoaderException(StringUtils.join(errors.values(), "\n"));
        }

        if (log.isInfoEnabled()) {
            log.info("Total TAM Allocation Delete processed: {}", tamDeleteCount);
            this.dumpCacheStats();
        }
    }

    // -----------------------------------------------------------------------
    // Record-level processing
    // -----------------------------------------------------------------------

    private void loadTAMAllocationDelete(XMLStreamReader xmlr) throws MessageLoaderException {
        Timestamp now = new Timestamp(System.currentTimeMillis());

        String fgName      = attr(xmlr, "functionalGroupName");
        String fgType      = attr(xmlr, "fgType");
        String siteDesc    = attr(xmlr, "siteDescription");
        String siteType    = attr(xmlr, "SiteType");
        String delThisSupp = attr(xmlr, "deleteThisLevelSupplier");
        String delLowrSupp = attr(xmlr, "deleteLowerLevelSupplier");
        String delThisItem = attr(xmlr, "deleteThisLevelItem");
        String delLowrItem = attr(xmlr, "deleteLowerLevelItem");

        if (StringUtils.isBlank(fgName)) {
            throw new MessageLoaderException("functionalGroupName is required for TAMAllocationDelete");
        }
        if (StringUtils.isBlank(siteDesc)) {
            throw new MessageLoaderException("siteDescription is required for TAMAllocationDelete");
        }

        // Resolve FunctionalGroup
        FunctionalGroup fg = StringUtils.isNotBlank(fgType)
                ? fgRepository.getFunctionalGroupByNameAndType(fgName, fgType)
                : fgRepository.getFunctionalGroupByName(fgName);
        if (fg == null) {
            throw new MessageLoaderException("Functional Group not found: " + fgName);
        }

        // Resolve Site — use siteType from XML if provided, else default to SITE
        String resolvedSiteType = StringUtils.isNotBlank(siteType) ? siteType.toUpperCase() : "SITE";
        Optional<Site> siteOpt = siteRepository.findBySiteDescriptionAndSiteType(siteDesc, resolvedSiteType);
        if (siteOpt.isEmpty()) {
            throw new MessageLoaderException("Site not found: " + siteDesc + " (type=" + resolvedSiteType + ")");
        }
        Site site = siteOpt.get();

        // Find all TAMAllocations for this FG+Site (may span date ranges)
        List<TAMAllocation> allocations = tamAllocationRepository
                .getTAMAllocationByFGSiteDescriptionAndSiteTypeWithoutFilterInternal(fgName, siteDesc, resolvedSiteType);

        if (allocations == null || allocations.isEmpty()) {
            log.warn("No TAMAllocation found for FG={} Site={} — nothing to delete", fgName, siteDesc);
            return;
        }

        boolean deleteThisLevelSupplier = isTrue(delThisSupp);
        boolean deleteLowerLevelSupplier = isTrue(delLowrSupp);
        boolean deleteThisLevelItem = isTrue(delThisItem);
        boolean deleteLowerLevelItem = isTrue(delLowrItem);

        for (TAMAllocation allocation : allocations) {
            if (deleteThisLevelSupplier || deleteLowerLevelSupplier) {
                Set<FunctionalGroupSupplierAllocation> supplierAllocs = allocation.getSupplierAllocations();
                if (supplierAllocs != null) {
                    for (FunctionalGroupSupplierAllocation sa : supplierAllocs) {
                        if (deleteThisLevelItem || deleteLowerLevelItem) {
                            // Item allocations are cascaded via CascadeType.ALL on FunctionalGroupSupplierAllocation
                            // so deleting the supplier allocation also removes item allocations
                            log.debug("Deleting supplier allocation (+ item allocations) for FG={} Site={}",
                                    fgName, siteDesc);
                        }
                    }
                }
            }

            // Delete the entire TAMAllocation (cascades to supplier and item allocations)
            tamAllocationRepository.delete(allocation);

            TAMAuditHistory audit = new TAMAuditHistory(
                    activeUserId, getActiveUserRoleId(),
                    AllocationAuditHistory.ACTIONUPLOAD_DELETE,
                    "UPLOAD_TAM_ALLOCATION_DELETE",
                    allocation, site,
                    "TAM Allocation deleted: FG=" + fgName + " Site=" + siteDesc,
                    now);
            auditRepository.save(audit);
        }

        log.info("TAMAllocationDeleteLoader: deleted {} allocation(s) for FG={} Site={}",
                allocations.size(), fgName, siteDesc);
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private String attr(XMLStreamReader xmlr, String name) {
        String v = xmlr.getAttributeValue(null, name);
        return v != null ? v.trim() : "";
    }

    /** Returns true if value is "Y", "yes", or "true" (case-insensitive). */
    private boolean isTrue(String value) {
        return "Y".equalsIgnoreCase(value)
                || "yes".equalsIgnoreCase(value)
                || "true".equalsIgnoreCase(value);
    }
}
