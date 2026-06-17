/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.supplyAllocation.repository;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;

import com.scplatform.pcm.supplyAllocation.entity.PcmSupplierAllocation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Repository
public interface PcmSupplierAllocationRepository extends JpaRepository<PcmSupplierAllocation, Long> {


    @Query("""
        SELECT s FROM PcmSupplierAllocation s
        WHERE
            (:hasCustomerItemGroupItem = true  AND s.customerItemGroupItem = :customerItemGroupItem
             OR :nullCustomerItemGroupItem = true AND s.customerItemGroupItem IS NULL)
        AND (:hasCustomerItem = true  AND s.customerItem = :customerItem
             OR :nullCustomerItem = true AND s.customerItem IS NULL)
        AND (:hasCustomerSite = true  AND s.customerSite = :customerSite
             OR :nullCustomerSite = true AND s.customerSite IS NULL)
        AND (:hasSupplierItem = true  AND s.supplierItem = :supplierItem
             OR :nullSupplierItem = true AND s.supplierItem IS NULL)
        AND (:hasSupplierBe = true  AND s.supplierBusinessEntity = :supplierBe
             OR :nullSupplierBe = true AND s.supplierBusinessEntity IS NULL)
        AND (:hasSupplierSite = true  AND s.supplierSite = :supplierSite
             OR :nullSupplierSite = true AND s.supplierSite IS NULL)
        AND (:hasDestinationSite = true  AND s.destinationSite = :destinationSite
             OR :nullDestinationSite = true AND s.destinationSite IS NULL)
        AND (:hasFromDate = true  AND s.effectiveFromDt = :fromDate
             OR :nullFromDate = true AND s.effectiveFromDt IS NULL)
        AND (:hasToDate = true  AND s.effectiveToDt = :toDate
             OR :nullToDate = true AND s.effectiveToDt IS NULL)
        """)
    Optional<PcmSupplierAllocation> findSupplierAllocationByNaturalKeyInternal(
        @Param("hasCustomerItemGroupItem")  boolean hasCustomerItemGroupItem,
        @Param("nullCustomerItemGroupItem") boolean nullCustomerItemGroupItem,
        @Param("customerItemGroupItem")     Item customerItemGroupItem,
        @Param("hasCustomerItem")           boolean hasCustomerItem,
        @Param("nullCustomerItem")          boolean nullCustomerItem,
        @Param("customerItem")              Item customerItem,
        @Param("hasCustomerSite")           boolean hasCustomerSite,
        @Param("nullCustomerSite")          boolean nullCustomerSite,
        @Param("customerSite")              Site customerSite,
        @Param("hasSupplierItem")           boolean hasSupplierItem,
        @Param("nullSupplierItem")          boolean nullSupplierItem,
        @Param("supplierItem")              Item supplierItem,
        @Param("hasSupplierBe")             boolean hasSupplierBe,
        @Param("nullSupplierBe")            boolean nullSupplierBe,
        @Param("supplierBe")                BusinessEntity supplierBe,
        @Param("hasSupplierSite")           boolean hasSupplierSite,
        @Param("nullSupplierSite")          boolean nullSupplierSite,
        @Param("supplierSite")              Site supplierSite,
        @Param("hasDestinationSite")        boolean hasDestinationSite,
        @Param("nullDestinationSite")       boolean nullDestinationSite,
        @Param("destinationSite")           Site destinationSite,
        @Param("hasFromDate")               boolean hasFromDate,
        @Param("nullFromDate")              boolean nullFromDate,
        @Param("fromDate")                  Date fromDate,
        @Param("hasToDate")                 boolean hasToDate,
        @Param("nullToDate")                boolean nullToDate,
        @Param("toDate")                    Date toDate
    );

    default PcmSupplierAllocation findSupplierAllocationByNaturalKey(
            Item customerItemGroupItem, Item customerItem, Site customerSite,
            Item supplierItem, BusinessEntity supplierBe, Site supplierSite,
            Site destinationSite, Date fromDate, Date toDate) {

        return findSupplierAllocationByNaturalKeyInternal(
            customerItemGroupItem != null, customerItemGroupItem == null, customerItemGroupItem,
            customerItem          != null, customerItem          == null, customerItem,
            customerSite          != null, customerSite          == null, customerSite,
            supplierItem          != null, supplierItem          == null, supplierItem,
            supplierBe            != null, supplierBe            == null, supplierBe,
            supplierSite          != null, supplierSite          == null, supplierSite,
            destinationSite       != null, destinationSite       == null, destinationSite,
            fromDate              != null, fromDate              == null, fromDate,
            toDate                != null, toDate                == null, toDate
        ).orElse(null);
    }

    @Query("""
        SELECT s FROM PcmSupplierAllocation s
        WHERE
            (:hasCustomerItemGroupItem = true  AND s.customerItemGroupItem = :customerItemGroupItem
             OR :nullCustomerItemGroupItem = true AND s.customerItemGroupItem IS NULL)
        AND (:hasCustomerItem = true  AND s.customerItem = :customerItem
             OR :nullCustomerItem = true AND s.customerItem IS NULL)
        AND (:hasCustomerSite = true  AND s.customerSite = :customerSite
             OR :nullCustomerSite = true AND s.customerSite IS NULL)
        AND (:hasSupplierItem = true  AND s.supplierItem = :supplierItem
             OR :nullSupplierItem = true AND s.supplierItem IS NULL)
        AND (:hasSupplierBe = true  AND s.supplierBusinessEntity = :supplierBe
             OR :nullSupplierBe = true AND s.supplierBusinessEntity IS NULL)
        AND (:hasSupplierSite = true  AND s.supplierSite = :supplierSite
             OR :nullSupplierSite = true AND s.supplierSite IS NULL)
        AND s.effectiveFromDt <= :effectiveDate
        AND (s.effectiveToDt IS NULL OR s.effectiveToDt >= :effectiveDate)
        """)
    Optional<PcmSupplierAllocation> findSupplierAllocationForDateInternal(
        @Param("hasCustomerItemGroupItem")  boolean hasCustomerItemGroupItem,
        @Param("nullCustomerItemGroupItem") boolean nullCustomerItemGroupItem,
        @Param("customerItemGroupItem")     Item customerItemGroupItem,
        @Param("hasCustomerItem")           boolean hasCustomerItem,
        @Param("nullCustomerItem")          boolean nullCustomerItem,
        @Param("customerItem")              Item customerItem,
        @Param("hasCustomerSite")           boolean hasCustomerSite,
        @Param("nullCustomerSite")          boolean nullCustomerSite,
        @Param("customerSite")              Site customerSite,
        @Param("hasSupplierItem")           boolean hasSupplierItem,
        @Param("nullSupplierItem")          boolean nullSupplierItem,
        @Param("supplierItem")              Item supplierItem,
        @Param("hasSupplierBe")             boolean hasSupplierBe,
        @Param("nullSupplierBe")            boolean nullSupplierBe,
        @Param("supplierBe")                BusinessEntity supplierBe,
        @Param("hasSupplierSite")           boolean hasSupplierSite,
        @Param("nullSupplierSite")          boolean nullSupplierSite,
        @Param("supplierSite")              Site supplierSite,
        @Param("effectiveDate")             Date effectiveDate
    );

    default PcmSupplierAllocation findSupplierAllocationForDate(
            Item customerItemGroupItem, Item customerItem, Site customerSite,
            Item supplierItem, BusinessEntity supplierBe, Site supplierSite,
            Date effectiveDate) {

        return findSupplierAllocationForDateInternal(
            customerItemGroupItem != null, customerItemGroupItem == null, customerItemGroupItem,
            customerItem          != null, customerItem          == null, customerItem,
            customerSite          != null, customerSite          == null, customerSite,
            supplierItem          != null, supplierItem          == null, supplierItem,
            supplierBe            != null, supplierBe            == null, supplierBe,
            supplierSite          != null, supplierSite          == null, supplierSite,
            effectiveDate
        ).orElse(null);
    }

    @Query("""
        SELECT s FROM PcmSupplierAllocation s
        WHERE s.customerItem = :customerItem
        AND (:hasSite = true AND s.customerSite = :site
             OR :hasSite = false AND 1=1)
        AND s.supplierItem IS NOT NULL
        AND (:hasFromDate = false
             OR s.effectiveFromDt IS NULL
             OR s.effectiveFromDt <= :effectiveFromDate)
        AND (:applyToDateFilter = false
             OR s.effectiveToDt IS NULL
             OR s.effectiveToDt >= :effectiveToDate)
        ORDER BY s.effectiveFromDt ASC
        """)
    List<PcmSupplierAllocation> findType2SupplierAllocationsByItemInternal(
        @Param("customerItem")       Item customerItem,
        @Param("hasSite")            boolean hasSite,
        @Param("site")               Site site,
        @Param("hasFromDate")        boolean hasFromDate,
        @Param("effectiveFromDate")  Date effectiveFromDate,
        @Param("applyToDateFilter")  boolean applyToDateFilter,
        @Param("effectiveToDate")    Date effectiveToDate
    );

    default List<PcmSupplierAllocation> findType2SupplierAllocationsByItem(
            Item customerItem, Site site, Date effectiveDate) {
        return findType2SupplierAllocationsByItem(customerItem, site, effectiveDate, effectiveDate);
    }

    @Query("""
        SELECT SUM(s.allocation) FROM PcmSupplierAllocation s
        WHERE s.customerItem = :customerItem
        AND (:hasSite = false OR s.customerSite = :site)
        AND s.supplierItem IS NOT NULL
        AND (:hasFromDate = false
             OR s.effectiveFromDt IS NULL
             OR s.effectiveFromDt <= :effectiveFromDate)
        AND (:applyToDateFilter = false
             OR s.effectiveToDt IS NULL
             OR s.effectiveToDt >= :effectiveToDate)
        """)
    BigDecimal getType2TotalSupplierAllocationsForItemPeriodInternal(
        @Param("customerItem") Item customerItem,
        @Param("hasSite") boolean hasSite,
        @Param("site") Site site,
        @Param("hasFromDate") boolean hasFromDate,
        @Param("effectiveFromDate") Date effectiveFromDate,
        @Param("applyToDateFilter") boolean applyToDateFilter,
        @Param("effectiveToDate") Date effectiveToDate
    );

    default BigDecimal getType2TotalSupplierAllocationsForItemPeriod(
            Item customerItem, Site site, Date effectiveFromDate, Date effectiveToDate,
            boolean fiscalPeriodValidationEnabled) {

        boolean applyToDateFilter = fiscalPeriodValidationEnabled || effectiveToDate != null;

        return getType2TotalSupplierAllocationsForItemPeriodInternal(
                customerItem,
                site != null,
                site,
                effectiveFromDate != null,
                effectiveFromDate,
                applyToDateFilter,
                effectiveToDate);
    }

    @Query("""
        SELECT s FROM PcmSupplierAllocation s
        WHERE s.customerItem = :customerItem
        AND (:hasSite = false OR s.customerSite = :site)
        AND (:hasDestinationSite = true AND s.destinationSite = :destinationSite
             OR :hasDestinationSite = false AND s.destinationSite IS NULL)
        AND s.supplierItem IS NOT NULL
        AND (:hasFromDate = false
             OR s.effectiveFromDt IS NULL
             OR s.effectiveFromDt <= :effectiveFromDate)
        AND (:applyToDateFilter = false
             OR s.effectiveToDt IS NULL
             OR s.effectiveToDt >= :effectiveToDate)
        ORDER BY s.effectiveFromDt ASC
        """)
    List<PcmSupplierAllocation> findType2SupplierAllocationsByItemDestSiteInternal(
        @Param("customerItem") Item customerItem,
        @Param("hasSite") boolean hasSite,
        @Param("site") Site site,
        @Param("hasDestinationSite") boolean hasDestinationSite,
        @Param("destinationSite") Site destinationSite,
        @Param("hasFromDate") boolean hasFromDate,
        @Param("effectiveFromDate") Date effectiveFromDate,
        @Param("applyToDateFilter") boolean applyToDateFilter,
        @Param("effectiveToDate") Date effectiveToDate
    );

    default List<PcmSupplierAllocation> findType2SupplierAllocationsByItemWithDestinationSite(
            Item customerItem, Site site, Site destinationSite,
            Date effectiveFromDate, Date effectiveToDate,
            boolean fiscalPeriodValidationEnabled) {
        boolean applyToDateFilter = fiscalPeriodValidationEnabled || effectiveToDate != null;

        return findType2SupplierAllocationsByItemDestSiteInternal(
                customerItem,
                site != null,
                site,
                destinationSite != null,
                destinationSite,
                effectiveFromDate != null,
                effectiveFromDate,
                applyToDateFilter,
                effectiveToDate);
    }

    default List<PcmSupplierAllocation> findType2SupplierAllocationsByItem(
            Item customerItem, Site site, Date effectiveFromDate, Date effectiveToDate) {

        boolean applyToDateFilter = (effectiveToDate != null);

        return findType2SupplierAllocationsByItemInternal(
            customerItem,
            site != null, site,
            effectiveFromDate != null, effectiveFromDate,
            applyToDateFilter, effectiveToDate);
    }

    @Query("""
        SELECT s FROM PcmSupplierAllocation s
        WHERE s.customerItemGroupItem IS NULL
        AND s.customerItem = :customerItem
        AND (:hasSite = false OR s.customerSite = :site)
        AND s.supplierItem = :supplierItem
        AND (:hasEffectiveDate = false
             OR (s.effectiveFromDt IS NULL OR s.effectiveFromDt <= :effectiveDate)
                AND (s.effectiveToDt IS NULL OR s.effectiveToDt >= :effectiveDate))
        ORDER BY s.effectiveFromDt ASC
        """)
    List<PcmSupplierAllocation> findType2SupplierAllocationByItemInternal(
        @Param("customerItem") Item customerItem,
        @Param("hasSite") boolean hasSite,
        @Param("site") Site site,
        @Param("supplierItem") Item supplierItem,
        @Param("hasEffectiveDate") boolean hasEffectiveDate,
        @Param("effectiveDate") Date effectiveDate
    );

    default PcmSupplierAllocation findType2SupplierAllocationByItem(
            Item customerItem, Site site, Item supplierItem, Date effectiveDate) {
        List<PcmSupplierAllocation> results = findType2SupplierAllocationByItemInternal(
                customerItem,
                site != null,
                site,
                supplierItem,
                effectiveDate != null,
                effectiveDate);
        return results.isEmpty() ? null : results.get(0);
    }

    @Query("""
        SELECT DISTINCT s.effectiveFromDt FROM PcmSupplierAllocation s
        WHERE s.customerItem = :customerItem
        AND s.customerItemGroupItem IS NULL
        AND s.supplierItem IS NOT NULL
        ORDER BY s.effectiveFromDt ASC
        """)
    List<Date> getFindType2SupplierAllocationPeriodsForItemCriteria(
        @Param("customerItem") Item customerItem
    );

    default List<Date> findType2SupplierAllocationPeriodsForItem(Item customerItem) {
        return getFindType2SupplierAllocationPeriodsForItemCriteria(customerItem);
    }

    @Query("""
        SELECT s FROM PcmSupplierAllocation s
        WHERE
            (:hasCustomerItemGroupItem = true AND s.customerItemGroupItem = :customerItemGroupItem
             OR :nullCustomerItemGroupItem = true AND s.customerItemGroupItem IS NULL)
        AND (:hasCustomerItem = true AND s.customerItem = :customerItem
             OR :nullCustomerItem = true AND s.customerItem IS NULL)
        AND (:hasSupplierItem = false OR s.supplierItem = :supplierItem)
        AND (:hasSupplierBe = false OR s.supplierBusinessEntity = :supplierBe)
        AND (:hasDestinationSite = true AND s.destinationSite = :destinationSite
             OR :hasDestinationSite = false AND s.destinationSite IS NULL)
        AND (
             (:hasSupplierSite = true AND s.supplierSite = :supplierSite)
             OR (:hasSupplierSite = false AND :includeSiteFlag = true AND s.supplierSite IS NULL)
             OR (:hasSupplierSite = false AND :includeSiteFlag = false)
        )
        AND (
             :bothDatesProvided = false
             OR (s.effectiveFromDt <= :toDate
                 AND (s.effectiveToDt >= :fromDate OR s.effectiveToDt IS NULL))
        )
        AND (
             :fromOnlyProvided = false
             OR (s.effectiveToDt >= :fromDate
                 OR s.effectiveFromDt >= :fromDate
                 OR s.effectiveToDt IS NULL)
        )
        ORDER BY s.effectiveFromDt ASC
        """)
    List<PcmSupplierAllocation> findSupplierAllocationBetweenDatesInternal(
        @Param("hasCustomerItemGroupItem") boolean hasCustomerItemGroupItem,
        @Param("nullCustomerItemGroupItem") boolean nullCustomerItemGroupItem,
        @Param("customerItemGroupItem") Item customerItemGroupItem,
        @Param("hasCustomerItem") boolean hasCustomerItem,
        @Param("nullCustomerItem") boolean nullCustomerItem,
        @Param("customerItem") Item customerItem,
        @Param("hasSupplierItem") boolean hasSupplierItem,
        @Param("supplierItem") Item supplierItem,
        @Param("hasSupplierBe") boolean hasSupplierBe,
        @Param("supplierBe") BusinessEntity supplierBe,
        @Param("hasDestinationSite") boolean hasDestinationSite,
        @Param("destinationSite") Site destinationSite,
        @Param("hasSupplierSite") boolean hasSupplierSite,
        @Param("supplierSite") Site supplierSite,
        @Param("includeSiteFlag") boolean includeSiteFlag,
        @Param("bothDatesProvided") boolean bothDatesProvided,
        @Param("fromOnlyProvided") boolean fromOnlyProvided,
        @Param("fromDate") Date fromDate,
        @Param("toDate") Date toDate
    );

    default List<PcmSupplierAllocation> findSupplierAllocationBetweenDates(
            Item customerItemGroupItem, Item customerItem, Site customerSite,
            Item supplierItem, BusinessEntity supplierBe, Site supplierSite,
            Site destinationSite, Date fromDate, Date toDate,
            BigDecimal allocation, boolean includeSiteFlag) {

        boolean bothDatesProvided = fromDate != null && toDate != null;
        boolean fromOnlyProvided = fromDate != null && toDate == null;

        return findSupplierAllocationBetweenDatesInternal(
                customerItemGroupItem != null,
                customerItemGroupItem == null,
                customerItemGroupItem,
                customerItem != null,
                customerItem == null,
                customerItem,
                supplierItem != null,
                supplierItem,
                supplierBe != null,
                supplierBe,
                destinationSite != null,
                destinationSite,
                supplierSite != null,
                supplierSite,
                includeSiteFlag,
                bothDatesProvided,
                fromOnlyProvided,
                fromDate,
                toDate);
    }

    default PcmSupplierAllocation getSupplierAllocation(Long key) {
        return findById(key).orElse(null);
    }

    @Modifying
    @Transactional
    @Query("DELETE FROM PcmSupplierAllocation s WHERE s.supplierAllocationKey IN :allocationKeys")
    int deleteSupplierAllocationsByKey(@Param("allocationKeys") List<Long> allocationKeys);

    default PcmSupplierAllocation saveOrUpdate(PcmSupplierAllocation allocation) {
        return save(allocation);
    }
}
