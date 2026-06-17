/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.rebate.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.scplatform.pcm.audit.Service.PcmAuditHistoryService;
import com.scplatform.pcm.bom.service.BomService;
import com.scplatform.pcm.item.entity.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.rebate.entity.PcmRebateProgram;
import com.scplatform.pcm.rebate.repository.PcmRebateProgramRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service layer for rebate program operations.
 */
@Service
@RequiredArgsConstructor
public class PcmRebateProgramService {

    private static final Logger logger = LoggerFactory.getLogger(PcmRebateProgramService.class);

    private final PcmRebateProgramRepository rebateProgramRepository;
    private final PcmConfigUtil configUtil;
    private final BomService bomService;
    private final PcmAuditHistoryService pcmAuditHistoryService;

    public long findRebateProgramCount(String name, Boolean caseSensitive) {
        if (Boolean.TRUE.equals(caseSensitive)) {
            return rebateProgramRepository.countByRebateName(name);
        }
        return rebateProgramRepository.countByRebateNameIgnoreCaseParam(name);
    }

    public PcmRebateProgram getRebateProgram(Long key) {
        return rebateProgramRepository.findById(key).orElse(null);
    }

    @Transactional
    public PcmRebateProgram saveOrUpdate(PcmRebateProgram rp) {
        return rebateProgramRepository.save(rp);
    }

    /**
     * Finds rebate programs by BOM item keys with USE_BOM flag enabled.
     *
     * @param itemKeys Set of item keys to search for
     * @return List of rebate programs that reference the given item keys with useBom=true
     */
    @Transactional(readOnly = true)
    public List<PcmRebateProgram> findRebateProgramsByBomItemKeysWithUseBom(Set<Long> itemKeys) {
        if (itemKeys == null || itemKeys.isEmpty()) {
            logger.warn("Item keys are null or empty, skipping rebate program lookup");
            return new java.util.ArrayList<>();
        }

        try {
            List<PcmRebateProgram> results = rebateProgramRepository.findRebateProgramsByBomItemKeysWithUseBom(itemKeys);

            logger.debug("Found " + (results != null ? results.size() : 0) +
                    " rebate program(s) with useBom=true for " + itemKeys.size() + " item key(s)");

            return results != null ? results : new java.util.ArrayList<>();

        } catch (Exception e) {
            logger.error("Error finding rebate programs by item keys " + itemKeys + ": " +
                    e.getMessage(), e);
            return new java.util.ArrayList<>();
        }
    }

    public void invalidateRebateProgramsForBomChange(Bom bom, String userId, String actionPerformed) {
        logger.debug("invalidateRebateProgramsForBomChange called - bom: " +
                (bom != null ? bom.getBomKey() : "null") + ", userId: " + userId + ", action: " + actionPerformed);
        invalidateRebateProgramsForBomChange(bom, null, null, userId, actionPerformed);
    }


    /**
     * Invalidates rebate programs when BOM changes occur.
     * Sets currentFlag=false for rebate programs that reference items in the changed BOM.
     *
     * @param bom the new/updated BOM
     * @param oldBom the previous BOM version
     * @param additionalRemovedItemKeys additional item keys removed from the BOM
     * @param userId the user performing the action
     * @param actionPerformed the action performed (e.g., "ADD", "MODIFY", "DELETE")
     */
    @Transactional
    public void invalidateRebateProgramsForBomChange(Bom bom, Bom oldBom,
                                                     Set<Long> additionalRemovedItemKeys, String userId, String actionPerformed) {
        // Feature flag check — skip rebate invalidation if feature is disabled
        if (!configUtil.getBoolean("pcm.feature.enable.bom.changes.rebate.delta.extract")) {
            logger.debug("Rebate BOM invalidation feature is DISABLED (pcm.feature.enable.bom.changes.rebate.delta.extract=false). " +
                    "Skipping invalidation for action: " + actionPerformed);
            return;
        }

        logger.info("Rebate BOM invalidation feature is ENABLED. Processing invalidation for action: " + actionPerformed +
                ", bom: " + (bom != null ? bom.getBomKey() : "null") +
                ", oldBom: " + (oldBom != null ? oldBom.getBomKey() : "null") +
                ", additionalRemovedItemKeys: " + (additionalRemovedItemKeys != null ? additionalRemovedItemKeys.size() : 0) +
                ", userId: " + userId);

        if (bom == null || bom.getItem() == null) {
            logger.debug("Skipping rebate invalidation - bom is null or bom.getItem() is null");
            return;
        }

        try {
            Item parentItem = bom.getItem();
            logger.debug("Collecting item keys from BOM [" + bom.getBomKey() + "] for parent item: " + parentItem.getItemNumber());
            Set<Long> allItemKeys = bomService.collectBomItemKeys(bom);
            logger.debug("Collected " + allItemKeys.size() + " item keys from new BOM [" + bom.getBomKey() + "]");

            // Include items from the old BOM being replaced — these items may no longer
            // be in the new BOM (removed children), but rebate programs referencing them
            // with useBom=true will have their View Results change.
            if (oldBom != null) {
                Set<Long> oldBomKeys = bomService.collectBomItemKeys(oldBom);
                logger.debug("Collected " + oldBomKeys.size() + " item keys from old BOM [" + oldBom.getBomKey() + "]");
                allItemKeys.addAll(oldBomKeys);
            }

            // Include additional removed item keys (from D operation code during upload).
            // These items were explicitly deleted from the BOM and are no longer in bomLines,
            // but rebate programs referencing them need to be invalidated.
            if (additionalRemovedItemKeys != null) {
                logger.debug("Adding " + additionalRemovedItemKeys.size() + " additional removed item keys (D operation code)");
                allItemKeys.addAll(additionalRemovedItemKeys);
            }

            if (allItemKeys.isEmpty()) {
                logger.debug("No item keys found for BOM [" + bom.getBomKey() + "]. Skipping rebate invalidation.");
                return;
            }

            logger.info("Looking up rebate programs with useBom=true for " + allItemKeys.size() +
                    " item keys from BOM [" + bom.getBomKey() + "], parent item: " + parentItem.getItemNumber());

            List<PcmRebateProgram> associatedRebates = rebateProgramRepository.findRebateProgramsByBomItemKeysWithUseBom(allItemKeys);

            if (associatedRebates != null && !associatedRebates.isEmpty()) {
                logger.info("Found " + associatedRebates.size() + " rebate program(s) with useBom=true for BOM [" +
                        bom.getBomKey() + "], parent item: " + parentItem.getItemNumber());
                int updatedCount = 0;

                for (PcmRebateProgram rebateProgram : associatedRebates) {
                    if (rebateProgram.getCurrentFlag()) {
                        logger.info("Setting currentFlag=false for Rebate Program [" +
                                rebateProgram.getRebateProgramKey() + "] '" +
                                rebateProgram.getRebateName() + "' due to BOM change on BOM [" +
                                bom.getBomKey() + "] for item [" + parentItem.getItemNumber() +
                                "], action: " + actionPerformed + ", userId: " + userId);

                        rebateProgram.setCurrentFlag(false);
                        rebateProgram.setUpdateDate(new Date());
                        rebateProgramRepository.save(rebateProgram);
                        updatedCount++;

                        String auditMsg = "Rebate program '" + rebateProgram.getRebateName() +
                                "' currentFlag set to false due to BOM change on item: " + parentItem.getItemNumber();
                        pcmAuditHistoryService.writeAuditRecord(userId, actionPerformed,
                                "PcmRebateProgram",
                                rebateProgram.getRebateProgramKey(),
                                auditMsg);
                    } else {
                        logger.debug("Rebate Program [" + rebateProgram.getRebateProgramKey() + "] '" +
                                rebateProgram.getRebateName() + "' already has currentFlag=false. Skipping.");
                    }
                }

                if (updatedCount > 0) {
                    logger.info("Updated " + updatedCount + " rebate program(s) currentFlag for BOM change on item: " +
                            parentItem.getItemNumber() + ", action: " + actionPerformed);
                } else {
                    logger.info("No rebate programs needed currentFlag update for BOM [" + bom.getBomKey() +
                            "] - all already had currentFlag=false");
                }
            } else {
                logger.debug("No rebate programs with useBom=true found for BOM [" + bom.getBomKey() +
                        "], parent item: " + parentItem.getItemNumber());
            }
        } catch (Exception e) {
            logger.warn("Error invalidating rebate currentFlag for BOM [" + bom.getBomKey() + "], action: " +
                    actionPerformed + ", userId: " + userId + ": " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getRebateProgramStatus(List<String> status, Date cutoffDate) {
        return rebateProgramRepository.findRebateProgramStatus(status, cutoffDate);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getRebateProgramStatusForOwner(List<String> status, Date cutoffDate, String userId) {
        return rebateProgramRepository.findRebateProgramStatusForOwner(status, cutoffDate, userId);
    }


}
