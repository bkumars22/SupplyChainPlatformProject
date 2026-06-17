/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.service;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.entity.PcmCostRecord;
import com.scplatform.pcm.cost.entity.PcmSourcingLane;
import com.scplatform.pcm.cost.repository.PcmSourcingLaneRepository;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for sourcing lane retrieval and filter-scoped loading behaviors.
 */
@Service
@RequiredArgsConstructor
public class PcmSourcingLaneService {

    private static final String COMPANY_ITEM_TYPE_FIELD_KEY = "pcm.commodityProfile.companyItemType.field";
    private static final String COST_RECORD_EXCLUDE_FILTER = "costRecordExcludeFilter";
    private static final String COST_RECORD_EXCLUDE_FILTER_WITHOUT_SOURCING_KEY = "costRecordExcludeFilterWithoutSourcingKey";

    private final PcmSourcingLaneRepository sourcingLaneRepository;
    private final PcmConfigUtil pcmConfigUtil;
    private final EntityManager entityManager;

    /**
     * Maps to legacy getSourcingLaneWithCommodityProfileFilter(Long, Long).
     */
    @Transactional(readOnly = true)
    public PcmSourcingLane getSourcingLaneWithCommodityProfileFilter(Long key, Long userKey) {
        String companyItemTypeField = pcmConfigUtil.getString(COMPANY_ITEM_TYPE_FIELD_KEY, "dataSource");

        StringBuilder query = new StringBuilder("select distinct ");
        if ("businessEntity".equals(companyItemTypeField)) {
            query.append("im.businessEntity.businessEntityName");
        } else {
            query.append("im.").append(companyItemTypeField);
        }
        query.append(" from PcmSourcingLane sl join sl.item im where sl.sourcingLaneKey = :sourcingLaneKey");

        Query q = entityManager.createQuery(query.toString());
        q.setParameter("sourcingLaneKey", key);

        Object companyItemType = q.getSingleResult();

        Map<String, Object> params = new HashMap<>();
        params.put("userKey", userKey);
        params.put("companyItemType", companyItemType != null ? companyItemType.toString() : null);

        Session session = entityManager.unwrap(Session.class);
        boolean filterEnabled = false;
        try {
            Filter filter = session.enableFilter(COST_RECORD_EXCLUDE_FILTER);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                filter.setParameter(entry.getKey(), entry.getValue());
            }
            filterEnabled = true;
        } catch (IllegalArgumentException ex) {
            // Filter may be unavailable in current migration state; fallback to unfiltered lane load.
        }

        try {
            PcmSourcingLane sourcingLane = sourcingLaneRepository.findById(key).orElse(null);
            if (sourcingLane == null) {
                return null;
            }
            for (PcmCostRecord costRecord : sourcingLane.getCostRecords()) {
                costRecord.getCostType();
            }
            return sourcingLane;
        } finally {
            if (filterEnabled) {
                session.disableFilter(COST_RECORD_EXCLUDE_FILTER);
            }
        }
    }

    /**
     * Maps to legacy findSourcingLaneByNaturalKeyWithCommodityProfileFilter(Item, Bom, BusinessEntity, Site, Site, String, Long).
     */
    @Transactional(readOnly = true)
    public PcmSourcingLane findSourcingLaneByNaturalKeyWithCommodityProfileFilter(
            Item item, Bom bom, BusinessEntity fromBe, Site fromSite, Site toSite, String currencyCode, Long userKey) {

        String companyItemTypeField = pcmConfigUtil.getString(COMPANY_ITEM_TYPE_FIELD_KEY, "dataSource");

        Map<String, Object> params = new HashMap<>();
        params.put("userKey", userKey);
        if ("businessEntity".equals(companyItemTypeField)) {
            params.put("companyItemType", item.getBusinessEntity().getBusinessEntityName());
        } else {
            params.put("companyItemType", item.getDataSource());
        }
        params.put("itemKey", item.getItemKey());

        Session session = entityManager.unwrap(Session.class);
        boolean filterEnabled = false;
        try {
            Filter filter = session.enableFilter(COST_RECORD_EXCLUDE_FILTER_WITHOUT_SOURCING_KEY);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                filter.setParameter(entry.getKey(), entry.getValue());
            }
            filterEnabled = true;
        } catch (IllegalArgumentException ex) {
            // Filter may be unavailable in current migration state; fallback to unfiltered lane load.
        }

        try {
            PcmSourcingLane sourcingLane = sourcingLaneRepository
                    .findSourcingLaneByNaturalKey(item, bom, fromBe, fromSite, toSite, currencyCode);
            if (sourcingLane != null && sourcingLane.getCostRecords() != null) {
                for (PcmCostRecord pcmCostRecord : sourcingLane.getCostRecords()) {
                    pcmCostRecord.getCostType();
                }
            }
            return sourcingLane;
        } finally {
            if (filterEnabled) {
                session.disableFilter(COST_RECORD_EXCLUDE_FILTER_WITHOUT_SOURCING_KEY);
            }
        }
    }


    @Transactional(readOnly = true)
    public java.util.List<Object[]> getSourcingLaneStatus(java.util.List<String> status,
                                                          java.util.Date cutoffDate, Long userKey) {
        return sourcingLaneRepository.findSourcingLaneStatus(status, cutoffDate, userKey);
    }


    @Transactional(readOnly = true)
    public java.util.List<Object[]> getSourcingLaneStatusForOwner(java.util.List<String> status,
                                                                  java.util.Date cutoffDate, String userId) {
        return sourcingLaneRepository.findSourcingLaneStatusForOwner(status, cutoffDate, userId);
    }
}
