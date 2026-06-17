/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.service;

import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.bom.entity.PcmDefectType;
import com.scplatform.pcm.bom.repository.BomRepository;
import com.scplatform.pcm.bom.repository.PcmDefectTypeRepository;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.FlexAttributeDefn;
import com.scplatform.pcm.common.entity.FlexAttributeManager;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.util.datetime.ISO8601;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.sql.Types;
import java.sql.Timestamp;
import java.util.*;

/**
 * Service class for BOM-related business operations.
 * Handles BOM transformation, serialization, cloning, and audit operations.
 * Includes native SQL methods for complex BOM logic requiring direct database access.
 */
@Service
@RequiredArgsConstructor
public class BomService {
    private static final Logger logger = LoggerFactory.getLogger(BomService.class);

    private final BomLineService bomLineService;
    private final BomRepository bomRepository;
    private final PcmDefectTypeRepository defectTypeRepository;
    private final ItemService itemService;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Loads a BOM by key.
     *
     * @param bomKey BOM key
     * @return Bom entity
     */
    public Bom getBom(Long bomKey) {
        return bomRepository.findById(bomKey).orElse(null);
    }

    /**
     * Generates an audit message for the BOM
     *
     * @param bom the BOM entity
     * @return formatted audit message
     */
    public String getAuditBom(Bom bom) {
        List<Object> args = new ArrayList<>();
        args.add(getAuditBomTitle(bom));
        args.add(bom.getStatus());
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bom", args.toArray(), null);
    }

    /**
     * Generates an audit title for the BOM
     *
     * @param bom the BOM entity
     * @return formatted BOM title
     */
    public String getAuditBomTitle(Bom bom) {
        List<Object> args = new ArrayList<>();
        if (bom.getBomName() != null) {
            args.add(bom.getBomName());
        } else {
            args.add(bom.getItem().getItemNumber());
        }
        args.add(bom.getBomVersion());
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.bomTitle", args.toArray(), null);
    }

    /**
     * Serializes a BOM to JSON representation
     *
     * @param bom the BOM to serialize
     * @return JsonNode representation of the BOM
     */
    public JsonNode getAsJson(Bom bom) {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("bomKey", bom.getBomKey());
        o.put("bomName", bom.getBomName());
        o.put("bomDesc", bom.getBomDesc());
        o.put("status", bom.getStatus());
        o.put("bomVersion", bom.getBomVersion().getVersion());
        o.put("bomRevision", bom.getBomVersion().getRevision());
        if (bom.getBusinessEntity() != null) {
            ObjectNode businessEntityAsJSON = bom.getBusinessEntity().getNaturalKeyAsJSON();
            o.set("business", businessEntityAsJSON);
        } else {
            o.putNull("business");
        }
        o.set("item", itemService.getItemNaturalKeyAsJSON(bom.getItem()));
        o.put("isTopLevel", (bom.getIsTopLevel() == null ? null : bom.getIsTopLevel().toString()));
        o.put("leadTime", (bom.getLeadTime() == null ? null : bom.getLeadTime().toString()));
        o.put("effectiveFrom", ISO8601.safeFormat(bom.getEffectiveFrom()));
        o.put("effectiveTo", ISO8601.safeFormat(bom.getEffectiveTo()));
        ArrayNode lines = o.putArray("bomLines");
        getBomLinesAsJson(bom, lines);
        return o;
    }

    /**
     * Helper method to serialize BOM lines to JSON array
     *
     * @param bom the BOM entity
     * @param lines the JSON array to populate
     */
    private void getBomLinesAsJson(Bom bom, ArrayNode lines) {
        for (BomLine line : bom.getSortedBomLines()) {
            lines.add(bomLineService.getAsJson(line));
        }
    }

    /**
     * Get flex attribute definitions for BOM
     *
     * @return list of flex attribute definitions
     */
    public List<FlexAttributeDefn> getFlexAttributeDefinitions() {
        return FlexAttributeManager.BOM.getFlexAttributeDefinitionList();
    }

    /**
     * Deep clones a BOM with optional sub-BOM cloning
     *
     * @param bom the BOM to clone
     * @param cloneSubBoms true to deep clone sub BOMs, false otherwise
     * @return cloned BOM
     */
    public Bom deepClone(Bom bom, boolean cloneSubBoms) {
        Bom target = new Bom();
        bom.copyDetailsTo(target);
        target.setBomKey(null);
        target.setSites(new HashSet<>());
        target.setAmls(new HashSet<>());
        target.setAttributes(new HashSet<>());
        target.setEffectiveFrom(bom.getEffectiveFrom());
        target.setEffectiveTo(bom.getEffectiveTo());

        // Copy sites
        for (Site s : bom.getSites()) {
            target.addSite(s);
        }

        // Deep clone BOM lines
        for (BomLine src : bom.getBomLines()) {
            BomLine lineCopy = new BomLine();
            src.copyDetailsTo(lineCopy);
            lineCopy.setBomLineKey(null);

            // Deep-copy nested subBom if requested
            if (cloneSubBoms && src.getSubBom() != null) {
                Bom subCopy = deepClone(src.getSubBom(), true);
                lineCopy.setSubBom(subCopy);
            }

            // Internally sets lineCopy.setBom(target)
            target.addBomLine(lineCopy);
        }

        return target;
    }

    /**
     * Get next BOM version for a business entity and item.
     * Uses BOM_VERSION table to track and increment versions.
     *
     * @param businessEntity the business entity
     * @param item the item
     * @return the next version as 8-digit formatted string
     */
    @Transactional
    public String getNextBomVersion(BusinessEntity businessEntity, Item item) {
        Integer currentVersion = bomRepository.findCurrentBomVersion(item.getItemKey(), businessEntity.getBusinessEntityKey());
        Integer version = (currentVersion != null) ? currentVersion + 1 : 1;

        if (currentVersion == null) {
            bomRepository.insertBomVersion(item.getItemKey(), businessEntity.getBusinessEntityKey(), version);
        } else {
            bomRepository.updateBomVersion(item.getItemKey(), businessEntity.getBusinessEntityKey(), version);
        }

        return String.format("%08d", version);
    }

    /**
     * Insert a cost rollup change record for a BOM.
     * Inserts a record into COST_ROLLUP_CHANGE table to track when BOM cost rollup is triggered.
     *
     * @param bomKey the primary key of the BOM
     * @param effectiveDate the effective date for the change
     * @param userKey the user who initiated the change
     */
    @Transactional
    public void insertBomCostRollupChangeForTrigger(Long bomKey, Date effectiveDate, Long userKey) {
        bomRepository.insertBomCostRollupChangeForTrigger(bomKey, effectiveDate, userKey);
    }

    /**
     * Calculate top-level BOM rollup for an item.
     * Fetches all top-level BOMs for an item using hierarchical SQL query (CONNECT BY),
     * then inserts cost rollup change records for each BOM in descending key order.
     *
     * @param itemKey the item key to find parent BOMs for
     * @param userKey the user initiating the rollup
     */
    @Transactional
    public void calculateTopLevelBomRollup(long itemKey, long userKey) {
        Map<Long, Date> bomMap = new TreeMap<>(Comparator.reverseOrder());

        logger.info("Fetching Top Level BOM Keys for Item Key: {}", itemKey);

        try {
            List<Object[]> results = bomRepository.findParentBomNthLevel(itemKey);

            if (results.isEmpty()) {
                logger.info("No Top Level BOMs found for Item Key: {}", itemKey);
                return;
            }

            for (Object[] row : results) {
                Long bomKey = ((Number) row[1]).longValue();
                Date effectiveFrom = null;

                if (row[2] instanceof Timestamp timestamp) {
                    effectiveFrom = new Date(timestamp.getTime());
                } else if (row[2] instanceof Date date) {
                    effectiveFrom = date;
                }

                bomMap.put(bomKey, effectiveFrom);
            }

            for (Map.Entry<Long, Date> entry : bomMap.entrySet()) {
                Long bomKey = entry.getKey();
                Date effectiveFrom = entry.getValue();

                logger.info("Processing Cost Rollup Trigger for bomKey: {}, Effective From: {}",
                        bomKey, effectiveFrom);

                bomRepository.insertBomCostRollupChangeForTrigger(bomKey, effectiveFrom, userKey);

                logger.info("Completed Cost Rollup Trigger for bomKey: {}", bomKey);
            }
        } catch (Exception e) {
            logger.error("Error while fetching Top Level BOM Keys for Item Key: {}", itemKey, e);
        }
    }

    /**
     * Compare two BOMs' hierarchy for equality.
     * Invokes stored procedure COMPARE_BOM_HIERARCHY to determine if two BOMs have identical hierarchical structure.
     *
     * @param firstBomKey the first BOM key
     * @param secondBomKey the second BOM key
     * @return true if hierarchies are equal, false otherwise
     */
    public boolean compareBomsHierarchy(Long firstBomKey, Long secondBomKey) {
        CallableStatementCreator creator = connection -> {
            java.sql.CallableStatement cs = connection.prepareCall("{ call COMPARE_BOM_HIERARCHY(?, ?, ?) }");
            cs.setLong(1, firstBomKey);
            cs.setLong(2, secondBomKey);
            cs.registerOutParameter(3, Types.VARCHAR);
            return cs;
        };
        CallableStatementCallback<Boolean> callback = cs -> {
            cs.execute();
            return "Y".equals(cs.getString(3));
        };
        return jdbcTemplate.execute(creator, callback);
    }

    /**
     * Update child BOMs data (header and lines).
     * Invokes updateChildBomsData stored procedure to update all sub-BOMs with the specified effective dates and status.
     *
     * @param bomKey the parent BOM key
     * @param effectiveFrom the new effective from date (may be null)
     * @param effectiveTo the new effective to date (may be null)
     * @param status the new status (may be null)
     */
    @Transactional
    public void updateChildBomsData(Long bomKey, Date effectiveFrom, Date effectiveTo, String status) {
        CallableStatementCreator creator = connection -> {
            java.sql.CallableStatement cs = connection.prepareCall("{ call updateChildBomsData(?, ?, ?, ?) }");
            cs.setLong(1, bomKey);
            if (effectiveFrom != null) {
                cs.setTimestamp(2, new Timestamp(effectiveFrom.getTime()));
            } else {
                cs.setNull(2, Types.TIMESTAMP);
            }
            if (effectiveTo != null) {
                cs.setTimestamp(3, new Timestamp(effectiveTo.getTime()));
            } else {
                cs.setNull(3, Types.TIMESTAMP);
            }
            if (status != null) {
                cs.setString(4, status);
            } else {
                cs.setNull(4, Types.VARCHAR);
            }
            return cs;
        };
        CallableStatementCallback<Void> callback = cs -> {
            cs.execute();
            return null;
        };
        jdbcTemplate.execute(creator, callback);
    }
    public static Set<Long> collectBomItemKeys(Bom bom) {
        Set<Long> allItemKeys = new HashSet<>();
        if (bom == null || bom.getItem() == null) {
            return allItemKeys;
        }

        // Parent BOM item
        if (bom.getItem().getItemKey() != null) {
            allItemKeys.add(bom.getItem().getItemKey());
        }

        // Child BOM line items
        if (bom.getBomLines() != null) {
            for (BomLine bomLine : bom.getBomLines()) {
                if (bomLine.getItem() != null && bomLine.getItem().getItemKey() != null) {
                    allItemKeys.add(bomLine.getItem().getItemKey());
                }
            }
        }
        return allItemKeys;
    }

    @Transactional(readOnly = true)
    public java.util.List<Object[]> getBomStatus(java.util.List<String> status, java.util.Date cutoffDate) {
        return bomRepository.findBomStatus(status, cutoffDate);
    }

    @Transactional(readOnly = true)
    public java.util.List<Object[]> getBomStatusForOwner(java.util.List<String> status,
                                                         java.util.Date cutoffDate, String userId) {
        return bomRepository.findBomStatusForOwner(status, cutoffDate, userId);
    }

    public Map<String, PcmDefectType> getDefectTypeMap() {
        List<PcmDefectType> dts = defectTypeRepository.getDefectTypes();
        Map<String, PcmDefectType> retval = new HashMap<String, PcmDefectType>(dts.size());
        for (PcmDefectType dt : dts) {
            retval.put(dt.getDefectName(), dt);
        }
        return retval;
    }

    /**
     * Get child BOM for a BOM line.
     * Returns the sub-BOM from the line, or finds and returns the first BOM for the line's item with valid status.
     *
     * @param bomLine the BOM line
     * @param bomSet set of valid BOM item keys (null means no restriction)
     * @param validStates array of valid statuses
     * @return child Bom or null
     */
    public Bom getBomLineChildBom(com.scplatform.pcm.bom.entity.BomLine bomLine, Set<Long> bomSet, String[] validStates) {
        Bom result = null;
        if (bomSet == null || bomSet.contains(bomLine.getItem().getItemKey())) {
            result = bomLine.getSubBom();
            if (result == null) {
                List<Long> childBomKeys = findChildrenBomsOfBomLine(bomLine, null);
                for (Long bomKey : childBomKeys) {
                    Bom bom = getBom(bomKey);
                    if (bom != null) {
                        for (String status : validStates) {
                            if (bom.getStatus().equals(status)) {
                                result = bom;
                                break;
                            }
                        }
                        if (result != null) break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Find all child BOMs for a given BOM line item, optionally filtered by effective dates.
     * Returns BOM keys ordered by version descending, then effective from descending.
     * 
     * This implements the original Hibernate Criteria logic:
     * - Query BOMs by item key
     * - If effectiveOn is provided, filter where:
     *   (effectiveTo IS NULL OR effectiveTo >= effectiveOn) AND 
     *   (effectiveFrom IS NULL OR effectiveFrom <= effectiveOn)
     * - Order by bomVersion.version DESC, effectiveFrom DESC
     *
     * @param bomLine the BOM line
     * @param effectiveOn optional effective date filter
     * @return list of child BOM keys
     */
    public List<Long> findChildrenBomsOfBomLine(com.scplatform.pcm.bom.entity.BomLine bomLine, Date effectiveOn) {
        List<Bom> bomsForItem;
        
        if (effectiveOn != null) {
            bomsForItem = bomRepository.findByItemKeyAndEffectiveDate(
                bomLine.getItem().getItemKey(), 
                effectiveOn
            );
        } else {
            // No date filtering - get all BOMs for the item ordered by version
            bomsForItem = bomRepository.findByItemKeyOrderByVersionDesc(
                bomLine.getItem().getItemKey()
            );
        }
        
        List<Long> result = new ArrayList<>();
        for (Bom bom : bomsForItem) {
            result.add(bom.getBomKey());
        }
        return result;
    }
}
