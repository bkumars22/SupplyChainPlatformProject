/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 */
package com.scplatform.pcm.bomCostRollUp.service;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.repository.BomRepository;
import com.scplatform.pcm.bomCostRollUp.dto.BomCostRollupForm;
import com.scplatform.pcm.bomCostRollUp.dto.BomEntry;
import com.scplatform.pcm.bomCostRollUp.dto.BomCostRollup;
import com.scplatform.pcm.bomCostRollUp.repository.BomCostRollupRepository;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.entity.PcmCostElement;
import com.scplatform.pcm.cost.repository.PcmCostElementRepository;
import com.scplatform.pcm.user.entity.Users;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Log4j2
public class BomCostRollupService {

    private static final long TIMEOUT_MS       = 120_000L;
    private static final long POLL_INTERVAL_MS =   5_000L;

    private static final String CFG_ALLOWED_ROLES        = "pcm.bom.rollup.visibility.allowedRoles";
    private static final String CFG_FIELD_LABEL          = "pcm.bom.cost.rollup.field.label";
    private static final String CFG_ROLE_VISIBLE_PREFIX  = "scplatform.bom.cost.rollup.role.visible.column.";
    private static final String CFG_FINAL_SELLING_SCALE  = "pcm.bomCostRollup.finalSelling.value";
    private static final String CFG_MIN_FRACTION_DIGITS  = "pcm.costrecord.minFractionDigits";
    private static final String CFG_ENABLE_CACHING       = "scplatform.feature.enable.rollup.caching";

    private final ConcurrentHashMap<String, Object> rollupLockMap = new ConcurrentHashMap<>();

    private final PcmConfigUtil pcmConfigUtil;
    private final BomCostRollupRepository bomCostRollupRepository;
    private final BomRepository bomRepository;
    private final PcmCostElementRepository pcmCostElementRepository;

    public void prepareSearchForm(BomCostRollupForm form, HttpServletRequest request) throws InvalidUserContext {
        ApplicationContext ac = AppContextHelper.getValidContext(request);
        Users user = ac.getCurrentUser();

        // Use a sensible "today" if the session has no effective date.
        Date effectiveDate = ac.getEffactiveDate() != null ? ac.getEffactiveDate() : new Date();
        ac.setEffactiveDate(effectiveDate);

        List<PcmCostElement> visible = resolveVisibleCostElements(user);
        if (form.getCostElements() != null) {
            form.getCostElements().clear();
            form.getCostElements().addAll(visible);
        }
    }

    private List<PcmCostElement> resolveVisibleCostElements(Users user) {
        List<String> allowedRoles = pcmConfigUtil.getList(CFG_ALLOWED_ROLES, new ArrayList<>());
        boolean isPrivileged =
                allowedRoles.contains(user.getRole().getRoleId())
                || (user.getBusinessEntity() != null
                    && user.getBusinessEntity().getBusinessEntityTypeKey() == BusinessEntity.ENTERPRISE_TYPE);

        String configKey = isPrivileged
                ? CFG_FIELD_LABEL
                : CFG_ROLE_VISIBLE_PREFIX + user.getRole().getRoleId();
        List<String> columns = pcmConfigUtil.getList(configKey, new ArrayList<>());

        if (columns.isEmpty()) {
            log.warn("No visible cost-element columns configured for role={} (privileged={}). " +
                     "Check property '{}'.",
                     user.getRole().getRoleId(), isPrivileged, configKey);
            return Collections.emptyList();
        }

        List<PcmCostElement> all = pcmCostElementRepository.getAllCostElements();
        List<PcmCostElement> resolved = new ArrayList<>(columns.size());
        for (String name : columns) {
            String trimmed = name == null ? "" : name.trim();
            PcmCostElement match = all.stream()
                    .filter(ce -> trimmed.equalsIgnoreCase(
                            ce.getCostElementName() == null ? "" : ce.getCostElementName().trim()))
                    .findFirst()
                    .orElse(null);
            if (match == null) {
                log.warn("Configured rollup column '{}' (from '{}') has no matching " +
                         "PCM_COST_ELEMENT.COST_ELEMENT_NAME — column will be hidden.",
                         trimmed, configKey);
            } else {
                resolved.add(match);
            }
        }
        return resolved;
    }

    public BomCostRollup getBomRollupData(long bomKey, HttpServletRequest request) throws InvalidUserContext {
        log.info("Accessing Bom Cost Rollup View for bomKey={}", bomKey);

        ApplicationContext ac = AppContextHelper.getValidContext(request);
        Users user = ac.getCurrentUser();
        Date effectiveDate = ac.getEffactiveDate() != null ? ac.getEffactiveDate() : new Date();

        long resolvedBomKey = resolveApprovedBomKey(bomKey, effectiveDate);

        BomCostRollup result = new BomCostRollup();
        try {
            String json = fetchOrComputeRollupJson(resolvedBomKey, user.getUserKey(), effectiveDate);
            Map<String, Object> summary = summarise(json, user);
            if (summary != null) {
                result.getJsonNodeList().add(summary);
            }
        } catch (Exception e) {
            log.error("Error processing BOM rollup for bomKey={}", bomKey, e);
        }
        log.info("BOM Rollup processing completed for bomKey={}", bomKey);
        return result;
    }

    private long resolveApprovedBomKey(long bomKey, Date effectiveDate) {
        try {
            Bom requested = bomRepository.findById(bomKey).orElse(null);
            if (requested != null && !"APPROVED".equalsIgnoreCase(requested.getStatus())) {
                log.info("BOM {} has status '{}' – resolving to APPROVED BOM.", bomKey, requested.getStatus());
                List<Bom> approved = bomRepository.findBomsForItem(
                        requested.getItem() != null ? requested.getItem().getItemKey() : null,
                        effectiveDate,
                        Collections.singletonList("APPROVED"));
                if (approved != null && !approved.isEmpty()) {
                    long resolved = approved.get(0).getBomKey();
                    log.info("Using APPROVED BOM key {} instead of {}", resolved, bomKey);
                    return resolved;
                }
                log.warn("No APPROVED BOM found for bomKey={} – falling back to original.", bomKey);
            }
        } catch (Exception e) {
            log.warn("Could not resolve APPROVED BOM for {} – using original.", bomKey, e);
        }
        return bomKey;
    }

    private String fetchOrComputeRollupJson(long bomKey, Long userKey, Date effectiveDate) {
        int status = bomCostRollupRepository.getBomCostRollupStatus(bomKey, effectiveDate);
        String existing = bomCostRollupRepository.getBomRollupDataFromTempTableAsJson(bomKey, userKey, effectiveDate);
        if (status == 1 && isNonEmptyData(existing)) {
            log.info("Rollup data already exists for BOM key {} — skipping processor.", bomKey);
            return existing;
        }
        return runRollupWithLock(bomKey, userKey, effectiveDate);
    }

    private String runRollupWithLock(Long bomKey, Long userKey, Date effectiveDate) {
        String key = String.valueOf(bomKey);
        Object lock = rollupLockMap.computeIfAbsent(key, k -> new Object());

        synchronized (lock) {
            try {
                int status = bomCostRollupRepository.getBomCostRollupStatus(bomKey, effectiveDate);
                if (status == 0) {
                    waitForOtherNode(bomKey, effectiveDate);
                }

                String result = bomCostRollupRepository.getBomRollupDataFromTempTableAsJson(bomKey, userKey, effectiveDate);

                if (pcmConfigUtil.getBooleanValue(CFG_ENABLE_CACHING, false)) {
                    result = validateAndRefreshRollup(result, bomKey, userKey, effectiveDate);
                } else {
                    result = bomCostRollupRepository.getRollupData(bomKey, userKey, effectiveDate);
                }
                return result;
            } finally {
                rollupLockMap.remove(key);
            }
        }
    }

    private void waitForOtherNode(Long bomKey, Date effectiveDate) {
        long start = System.currentTimeMillis();
        while (bomCostRollupRepository.getBomCostRollupStatus(bomKey, effectiveDate) == 0) {
            if (System.currentTimeMillis() - start > TIMEOUT_MS) {
                throw new IllegalStateException("Timeout: BOM rollup still in progress for key " + bomKey);
            }
            try {
                Thread.sleep(POLL_INTERVAL_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Thread interrupted while waiting for rollup", e);
            }
        }
    }

    private String validateAndRefreshRollup(String result, Long bomKey, Long userKey, Date effectiveDate) {
        if (result == null || result.isEmpty()) {
            return result;
        }
        try {
            JsonNode dataNode = new ObjectMapper().readTree(result).get("DATA");
            if (dataNode == null || dataNode.isEmpty()) {
                return bomCostRollupRepository.getRollupData(bomKey, userKey, effectiveDate);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Invalid BOM rollup JSON response", e);
        }
        return result;
    }

    private Map<String, Object> summarise(String json, Users user) throws Exception {
        if (!isNonEmptyData(json)) {
            return null;
        }

        int minFractionDigits        = pcmConfigUtil.getIntValue(CFG_MIN_FRACTION_DIGITS, 5);
        int maxFractionDigitsSelling = pcmConfigUtil.getIntValue(CFG_FINAL_SELLING_SCALE, 6);

        ObjectMapper mapper = new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        JsonNode dataNode = mapper.readTree(json).get("DATA");

        List<BomEntry> bomList = mapper.convertValue(dataNode, new TypeReference<List<BomEntry>>() {});
        String itemName       = dataNode.get(0).get("ITEM_NAME").asText();
        String vaCostStr      = dataNode.get(0).get("ITEM_VA_COST").asText();
        String currencyError  = dataNode.get(0).get("CURRENCY_CONVERSION_ERROR_MSG").asText();

        Map<String, List<BomEntry>> bomMap = new HashMap<>();
        bomMap.put(itemName, bomList);

        BigDecimal itemVaCost = (vaCostStr == null || vaCostStr.isEmpty() || "null".equals(vaCostStr))
                ? BigDecimal.ZERO : new BigDecimal(vaCostStr);

        Map<String, BigDecimal> totals        = aggregateTotals(bomList);
        Map<String, BigDecimal> costRecordMap = buildCostRecordMap(itemVaCost, totals, minFractionDigits);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("ITEM_NAME", itemName);
        out.put("ITEM_PART_NAME", itemName);
        out.put("CURRENCY_CONVERSION_ERROR_MSG", currencyError);

        // Project only the cost-element columns visible to the current user.
        // costRecordMap is keyed by hard-coded constants (VA_COST, DIRECT_MATERIAL, …);
        // any cost element whose COST_ELEMENT_KEY does not match one of those constants
        // is silently dropped — log it so missing columns are easy to diagnose.
        for (PcmCostElement ce : resolveVisibleCostElements(user)) {
            String key = ce.getCostElementKey();
            BigDecimal v = costRecordMap.get(key);
            if (v == null) {
                log.warn("Visible cost element '{}' (name='{}') has no aggregated value " +
                         "(no entry in totals map keyed by '{}'). Available keys={}.",
                         key, ce.getCostElementName(), key, costRecordMap.keySet());
                continue;
            }
            out.put(key, v);
        }

        BigDecimal rollup = calculateRollupPrice(itemName, bomMap, new HashSet<>());
        out.put("ROLLUP_PRICE", rollup.setScale(10, RoundingMode.HALF_UP));
        out.put("FINAL_SELLING_PRICE",
                rollup.add(itemVaCost).setScale(maxFractionDigitsSelling, RoundingMode.HALF_UP));
        return out;
    }

    private Map<String, BigDecimal> aggregateTotals(List<BomEntry> bomList) {
        Map<String, BigDecimal> t = new HashMap<>();
        for (BomEntry e : bomList) {
            BigDecimal qty = BigDecimal.valueOf(e.getItemPartQty() != null ? e.getItemPartQty() : 1.0);
            t.merge("DIRECT_MATERIAL",        getSafeBigDecimal(e.getDirectMaterial()).multiply(qty),     BigDecimal::add);
            t.merge("SHARING_COST",           getSafeBigDecimal(e.getSharingCost()).multiply(qty),        BigDecimal::add);
            t.merge("DIRECT_LABOR",           getSafeBigDecimal(e.getDirectLabor()).multiply(qty),        BigDecimal::add);
            t.merge("DIRECT_LABOR2",          getSafeBigDecimal(e.getDirectLabor2()).multiply(qty),       BigDecimal::add);
            t.merge("INDIRECT_LABOR",         getSafeBigDecimal(e.getIndirectLabor()).multiply(qty),      BigDecimal::add);
            t.merge("MACHINE_EQUIPMENT",      getSafeBigDecimal(e.getMachineEquipement()).multiply(qty),  BigDecimal::add);
            t.merge("MATERIAL_HANDLING",      getSafeBigDecimal(e.getMaterialHandling()).multiply(qty),   BigDecimal::add);
            t.merge("MATERIAL_SCRAP",         getSafeBigDecimal(e.getMaterialScrap()).multiply(qty),      BigDecimal::add);
            t.merge("FREIGHT",                getSafeBigDecimal(e.getFright()).multiply(qty),             BigDecimal::add);
            t.merge("SGA",                    getSafeBigDecimal(e.getSga()),                              BigDecimal::add);
            t.merge("FINANCIAL_RECEIVABLES",  getSafeBigDecimal(e.getFinancialReceivables()),             BigDecimal::add);
            t.merge("PROFIT_MARGIN",          getSafeBigDecimal(e.getProfitMargin()),                     BigDecimal::add);
            t.merge("ADJUSTMENTS_REDUCTIONS", getSafeBigDecimal(e.getAdjustmentsReduction()),             BigDecimal::add);
            t.merge("MISCELLANEOUS",          getSafeBigDecimal(e.getMiscellaneous()),                    BigDecimal::add);
            t.merge("TARIFF",                 getSafeBigDecimal(e.getTariff()),                           BigDecimal::add);
        }
        return t;
    }

    private Map<String, BigDecimal> buildCostRecordMap(BigDecimal itemVaCost, Map<String, BigDecimal> totals, int scale) {
        Map<String, BigDecimal> m = new HashMap<>();
        m.put("VA_COST", itemVaCost.setScale(scale, RoundingMode.HALF_UP));
        totals.forEach((k, v) -> m.put(k, v.setScale(scale, RoundingMode.HALF_UP)));
        return m;
    }

    private BigDecimal calculateRollupPrice(String partName, Map<String, List<BomEntry>> bomMap, Set<String> visited) {
        if (!visited.add(partName)) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        try {
            List<BomEntry> parts = bomMap.get(partName);
            if (parts == null) {
                return total;
            }
            for (BomEntry part : parts) {
                BigDecimal childPrice;
                if (bomMap.containsKey(part.getItemPartName())) {
                    childPrice = calculateRollupPrice(part.getItemPartName(), bomMap, visited);
                } else if (part.getItemPartTotal() != null) {
                    childPrice = BigDecimal.valueOf(part.getItemPartTotal());
                } else {
                    childPrice = part.getItemPartSellingPrice() != null
                            ? BigDecimal.valueOf(part.getItemPartSellingPrice()) : BigDecimal.ZERO;
                }
                BigDecimal qty = BigDecimal.valueOf(part.getItemPartQty() != null ? part.getItemPartQty() : 1.0);
                total = total.add(childPrice.multiply(qty));
            }
            return total;
        } finally {
            visited.remove(partName);
        }
    }

    private BigDecimal getSafeBigDecimal(Number value) {
        return value != null ? new BigDecimal(value.toString()) : BigDecimal.ZERO;
    }

    private boolean isNonEmptyData(String json) {
        if (json == null || json.isEmpty()) {
            return false;
        }
        try {
            JsonNode dataNode = new ObjectMapper().readTree(json).get("DATA");
            return dataNode != null && dataNode.isArray() && !dataNode.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
