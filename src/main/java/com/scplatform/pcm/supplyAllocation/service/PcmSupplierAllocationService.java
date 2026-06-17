/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.supplyAllocation.service;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.supplyAllocation.entity.PcmSupplierAllocation;
import com.scplatform.pcm.supplyAllocation.repository.PcmSupplierAllocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PcmSupplierAllocationService {

    public static final String PCM_SUPPLIER_ALLOCATION_FISCAL_VALIDATION_SWITCH =
            "pcm.supplierAllocation.fiscalCalendarValidation.enabled";

    private final PcmSupplierAllocationRepository supplierAllocationRepository;
    private final PcmConfigUtil pcmConfigUtil;

    private boolean isFiscalPeriodValidationEnabled() {
        return pcmConfigUtil.getBooleanValue(PCM_SUPPLIER_ALLOCATION_FISCAL_VALIDATION_SWITCH, true);
    }

    @Transactional(readOnly = true)
    public PcmSupplierAllocation findSupplierAllocationForDate(
            Item customerItemGroupItem, Item customerItem, Site customerSite,
            Item supplierItem, BusinessEntity supplierBe, Site supplierSite,
            Date effectiveDate) {
        return supplierAllocationRepository.findSupplierAllocationForDate(
                customerItemGroupItem, customerItem, customerSite,
                supplierItem, supplierBe, supplierSite,
                effectiveDate);
    }

    @Transactional(readOnly = true)
    public PcmSupplierAllocation getSupplierAllocation(Long key) {
        return supplierAllocationRepository.getSupplierAllocation(key);
    }

    @Transactional(readOnly = true)
    public PcmSupplierAllocation findSupplierAllocationByNaturalKey(
            Item customerItemGroupItem, Item customerItem, Site customerSite,
            Item supplierItem, BusinessEntity supplierBe, Site supplierSite,
            Site destinationSite, Date fromDate, Date toDate) {
        log.debug("Finding supplier allocation by natural key");
        return supplierAllocationRepository.findSupplierAllocationByNaturalKey(
                customerItemGroupItem, customerItem, customerSite,
                supplierItem, supplierBe, supplierSite,
                destinationSite, fromDate, toDate);
    }

    @Transactional(readOnly = true)
    public List<PcmSupplierAllocation> findSupplierAllocationBetweenDates(
            Item customerItemGroupItem, Item customerItem, Site customerSite,
            Item supplierItem, BusinessEntity supplierBe, Site supplierSite,
            Site destinationSite, Date fromDate, Date toDate,
            BigDecimal allocation, boolean includeSiteFlag) {
        log.debug("Finding supplier allocations between dates, includeSiteFlag={}", includeSiteFlag);
        return supplierAllocationRepository.findSupplierAllocationBetweenDates(
                customerItemGroupItem, customerItem, customerSite,
                supplierItem, supplierBe, supplierSite,
                destinationSite, fromDate, toDate,
                allocation, includeSiteFlag);
    }

    @Transactional(readOnly = true)
    public PcmSupplierAllocation findType2SupplierAllocationByItem(
            Item customerItem, Site site, Item supplierItem, Date effectiveDate) {
        log.debug("Finding type2 supplier allocation for customerItem={}, site={}, supplierItem={}, effectiveDate={}",
                customerItem, site, supplierItem, effectiveDate);
        return supplierAllocationRepository.findType2SupplierAllocationByItem(
                customerItem, site, supplierItem, effectiveDate);
    }

    @Transactional(readOnly = true)
    public List<PcmSupplierAllocation> findType2SupplierAllocationsByItem(
            Item customerItem, Site site, Date effectiveDate) {
        return findType2SupplierAllocationsByItem(customerItem, site, effectiveDate, effectiveDate);
    }

    @Transactional(readOnly = true)
    public List<PcmSupplierAllocation> findType2SupplierAllocationsByItem(
            Item customerItem, Site site, Date effectiveFromDate, Date effectiveToDate) {
        log.debug("Finding type2 supplier allocations for customerItem={}, site={}, effectiveFromDate={}, effectiveToDate={}",
                customerItem, site, effectiveFromDate, effectiveToDate);
        return supplierAllocationRepository.findType2SupplierAllocationsByItem(
                customerItem, site, effectiveFromDate, effectiveToDate);
    }

    @Transactional(readOnly = true)
    public List<PcmSupplierAllocation> findType2SupplierAllocationsByItemWithDestinationSite(
            Item customerItem, Site site, Site destinationSite,
            Date effectiveFromDate, Date effectiveToDate) {
        log.debug("Finding type2 supplier allocations with destinationSite for customerItem={}, site={}, destinationSite={}, effectiveFromDate={}, effectiveToDate={}",
                customerItem, site, destinationSite, effectiveFromDate, effectiveToDate);
        return supplierAllocationRepository.findType2SupplierAllocationsByItemWithDestinationSite(
                customerItem, site, destinationSite,
                effectiveFromDate, effectiveToDate,
                isFiscalPeriodValidationEnabled());
    }

    @Transactional(readOnly = true)
    public BigDecimal getType2TotalSupplierAllocationsForItemPeriod(
            Item customerItem, Site site,
            Date effectiveFromDate, Date effectiveToDate) {
        log.debug("Finding type2 total supplier allocation for customerItem={}, site={}, effectiveFromDate={}, effectiveToDate={}",
                customerItem, site, effectiveFromDate, effectiveToDate);
        return supplierAllocationRepository.getType2TotalSupplierAllocationsForItemPeriod(
                customerItem, site, effectiveFromDate, effectiveToDate,
                isFiscalPeriodValidationEnabled());
    }

    @Transactional(readOnly = true)
    public List<Date> findType2SupplierAllocationPeriodsForItem(Item customerItem) {
        return supplierAllocationRepository.findType2SupplierAllocationPeriodsForItem(customerItem);
    }

    @Transactional
    public void delete(PcmSupplierAllocation allocation) {
        supplierAllocationRepository.delete(allocation);
    }

    /**
     * Deletes supplier allocations by primary keys.
     */
    @Transactional
    public void deleteSupplierAllocationsByKey(List<Long> allocationKeys) {
        if (allocationKeys == null || allocationKeys.isEmpty()) {
            return;
        }

        try {
            supplierAllocationRepository.deleteSupplierAllocationsByKey(allocationKeys);
        } catch (Exception e) {
            log.error("Error while deleting supplier allocations for keys={}", allocationKeys, e);
            throw e;
        }
    }

    @Transactional
    public PcmSupplierAllocation saveOrUpdate(PcmSupplierAllocation allocation) {
        return supplierAllocationRepository.saveOrUpdate(allocation);
    }
}
