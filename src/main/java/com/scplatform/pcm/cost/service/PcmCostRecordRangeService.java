/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.entity.PcmCostRecord;
import com.scplatform.pcm.cost.entity.PcmCostRecordRange;
import com.scplatform.pcm.cost.entity.PcmCostRecordValue;
import com.scplatform.pcm.cost.enums.PcmCostElementType;
import com.scplatform.pcm.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Service for managing PcmCostRecordRange operations
 * Provides utility methods to retrieve cost record values and calculate totals
 * Uses constructor-based dependency injection
 */
@Service
@RequiredArgsConstructor
public class PcmCostRecordRangeService {

    private final static Logger log = LogManager.getLogger(PcmCostRecordRangeService.class);
    
    private PcmCostRecord pcmCostRecord;
    private final PcmConfigUtil pcmConfigUtil;


    protected PcmCostRecordRange assertAndGetActiveCostRecordRange() {
        PcmCostRecordRange ar = this.getActiveCostRecordRange();
        if (ar == null) {
            throw new IllegalStateException("Cannot call function since active range is null");
        }
        return ar;
    }

    public BigDecimal getComputedTotal(PcmCostRecordRange range) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        if (range != null && range.getCostRecordValues() != null) {
            for (PcmCostRecordValue value : range.getCostRecordValues().values()) {
                total = total.add(value.getComputedCostValue()).setScale(6, RoundingMode.HALF_UP);
            }
        }
        return total;
    }

    public BigDecimal getTotalByCostElementType(PcmCostRecordRange range, PcmCostElementType type) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        if (range.getCostRecordValues() != null) {
            for (PcmCostRecordValue value : range.getCostRecordValues().values()) {
                if (value.getCostElement().isOfType(type)) {
                    total = total.add(value.getCostValue()).setScale(6, RoundingMode.HALF_UP);
                }
            }
        }

        return total;
    }

    public BigDecimal getComputedTotalByCostElementType(PcmCostRecordRange range, PcmCostElementType type) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        if (range.getCostRecordValues() != null) {
            for (PcmCostRecordValue value : range.getCostRecordValues().values()) {
                if (value.getCostElement().isOfType(type)) {
                    total = total.add(value.getCostValue()).setScale(6, RoundingMode.HALF_UP);
                }
            }
        }

        return total;
    }

    public BigDecimal getComputedTotalByCostElementTypes(PcmCostRecordRange range, Set<PcmCostElementType> types) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        if (range != null && types != null) {
            for (PcmCostElementType type : types) {
                total = total.add(getComputedTotalByCostElementType(range, type));
            }
        }
        return total;
    }


    public Map<String, PcmCostRecordValue> getActiveCostRecordValues() {
        PcmCostRecordRange activeRange = this.getActiveCostRecordRange();
        if (activeRange == null) {
            return null; // TODO should we return null here ???
        }
        return activeRange.getCostRecordValues();
    }

    public PcmCostRecordRange getCostRecordRangeByKey(Long key) {
        if (key == null) {
            return null;
        }
        PcmCostRecordRange result = null;
        for (PcmCostRecordRange crrng : pcmCostRecord.getCostRecordRanges()) {
            Long rangeKey = crrng.getCostRecordRangeKey();
            if (key.equals(rangeKey)) {
                result = crrng;
                break;
            }
        }
        return result;
    }

    public PcmCostRecordRange getCostRecordRange(BigDecimal fromRange, BigDecimal toRange) {
        PcmCostRecordRange result = null;
        for (PcmCostRecordRange crrng : pcmCostRecord.getCostRecordRanges()) {
            boolean frm;
            BigDecimal crFrmRange = crrng.getFromRange();
            if (crFrmRange == null) {
                frm = fromRange == null;
            } else {
                if (fromRange == null) {
                    continue;
                }
                frm = crFrmRange.compareTo(fromRange) == 0;
            }

            boolean to;
            BigDecimal crToRange = crrng.getToRange();
            if (crToRange == null) {
                to = toRange == null;
            } else {
                if (toRange == null) {
                    continue;
                }
                to = crToRange.compareTo(toRange) == 0;
            }

            if (frm && to) {
                result = crrng;
                break;
            }
        }
        return result;
    }

    public PcmCostRecordRange addCostRecordRange(BigDecimal fromRange, BigDecimal toRange, Boolean isActive) {
        PcmCostRecordRange result = getCostRecordRange(fromRange, toRange);
        if (result == null) {
            result = new PcmCostRecordRange(fromRange);
            result.setToRange(toRange);
            result.setCostRecord(pcmCostRecord);
            pcmCostRecord.getCostRecordRanges().add(result);
        }
        result.setActive(isActive);
        return result;
    }

    public void addCostRecordRange(PcmCostRecordRange range) {
        pcmCostRecord.getCostRecordRanges().add(range);
        range.setCostRecord(pcmCostRecord);
    }

    public PcmCostRecordRange addDefaultCostRecordRange() {
        PcmCostRecordRange range = getCostRecordRange(new BigDecimal(0), null);
        if (range != null) {
            return range;
        }
        if (pcmCostRecord.getCostRecordRanges() != null && pcmCostRecord.getCostRecordRanges().size() != 0) {
            throw new IllegalStateException(
                    "Cannot call this method since costRecordRanges exist for this cost record.");
        }
        range = new PcmCostRecordRange(new BigDecimal(0));
        range.setActive(Boolean.TRUE);
        range.setCostRecord(pcmCostRecord);
        pcmCostRecord.getCostRecordRanges().add(range);
        return range;
    }

    public BigDecimal getComputedTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        for (PcmCostRecordValue value : pcmCostRecord.getActiveCostRecordRange().getCostRecordValues().values()) {
            total = total.add(value.getComputedCostValue()).setScale(6, RoundingMode.HALF_UP);
        }
        return total;
    }

    public BigDecimal getComputedTotalNotOfCostElementType(PcmCostElementType excludedType) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP);
        List<String> excludedCostElementsToTotal = pcmConfigUtil.getList("pcm.cost.element.excluded.in.total", Arrays.asList(""));
        for (PcmCostRecordValue value : pcmCostRecord.getActiveCostRecordRange().getCostRecordValues().values()) {
            if (value.getCostElement().isNotOfType(excludedType)
                    && !excludedCostElementsToTotal.contains(value.getCostElement().getCostElementKey())) {
                total = total.add(value.getComputedCostValue()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return total;
    }
    public PcmCostRecordRange getActiveCostRecordRange() {
        PcmCostRecordRange retval = null;
        // Check if ranges are set
        if (pcmCostRecord.getCostRecordRanges() == null || pcmCostRecord.getCostRecordRanges().size() == 0) {
            return null;
        }
        // Iterate through the ranges to find the active range
        for (PcmCostRecordRange r : pcmCostRecord.getCostRecordRanges()) {
            Boolean active = r.getActive();
            if (active != null && Boolean.TRUE.equals(active)) {
                retval = r;
                break;
            }
        }
        if (retval == null) { // No active range
            retval = pcmCostRecord.getCostRecordRanges().first();
        }
        return retval;
    }
    public BigDecimal getTotalByCostElementType(PcmCostElementType type) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP);
        for (PcmCostRecordValue value : pcmCostRecord.getActiveCostRecordRange().getCostRecordValues().values()) {
            if (value.getCostElement().isOfType(type)) {
                total = total.add(value.getCostValue()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return total;
    }

    public BigDecimal getComputedTotalByCostElementType(PcmCostElementType type) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP);
        for (PcmCostRecordValue value : pcmCostRecord.getActiveCostRecordRange().getCostRecordValues().values()) {
            if (value.getCostElement().isOfType(type)) {
                total = total.add(value.getComputedCostValue()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return total;
    }

    public BigDecimal getTotalNotOfCostElementType(PcmCostElementType excludedType) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP);
        for (PcmCostRecordValue value : pcmCostRecord.getActiveCostRecordRange().getCostRecordValues().values()) {
            if (value.getCostElement().isNotOfType(excludedType)) {
                total = total.add(value.getCostValue()).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
        }
        return total;
    }

    public BigDecimal getTotalNotOfCostElementType(PcmCostRecordRange range, PcmCostElementType type) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);

        if (range.getCostRecordValues() != null) {
            for (PcmCostRecordValue value : range.getCostRecordValues().values()) {
                // Notice the NOT condition
                if (!value.getCostElement().isOfType(type)) {
                    total = total.add(value.getCostValue()).setScale(6, RoundingMode.HALF_UP);
                }
            }
        }
        return total;
    }

    public BigDecimal getComputedTotalNotOfCostElementTypeFixed(PcmCostRecordRange range) {
        return getComputedTotalNotOfCostElementType(range, PcmCostElementType.FIXED);
    }

    public BigDecimal getComputedTotalNotOfCostElementType(PcmCostRecordRange range, PcmCostElementType excludedType) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        List<String> excludedCostElementsToTotal = pcmConfigUtil.getList("pcm.cost.element.excluded.in.total", Arrays.asList(""));
        if (range != null && range.getCostRecordValues() != null) {
            for (PcmCostRecordValue value : range.getCostRecordValues().values()) {
                if (value.getCostElement().isNotOfType(excludedType)
                        && !excludedCostElementsToTotal.contains(value.getCostElement().getCostElementKey())) {
                    total = total.add(value.getComputedCostValue()).setScale(6, RoundingMode.HALF_UP);
                }
            }
        }
        return total;
    }

    public EnumMap<PcmCostElementType, BigDecimal> getComputedTotalByCostElementTypeMap(PcmCostRecordRange range) {
        EnumMap<PcmCostElementType, BigDecimal> retval = new EnumMap<>(PcmCostElementType.class);
        if (range != null && range.getCostRecordValues() != null) {
            for (PcmCostRecordValue value : range.getCostRecordValues().values()) {
                PcmCostElementType costElementType = value.getCostElement().getCostElementType();
                BigDecimal total = retval.get(costElementType);
                if (total == null) {
                    total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
                }
                total = total.add(value.getCostValue()).setScale(6, RoundingMode.HALF_UP);
                retval.put(costElementType, total);
            }
        }
        return retval;
    }

    public boolean hasCostRecordValueOfCostElementType(PcmCostRecordRange range, PcmCostElementType type) {
        if (range != null && range.getCostRecordValues() != null) {
            for (PcmCostRecordValue value : range.getCostRecordValues().values()) {
                if (value.getCostElement().isOfType(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public EnumMap<PcmCostElementType, BigDecimal> getComputedTotalByCostElementTypeMap() {
        EnumMap<PcmCostElementType,BigDecimal> retval = new EnumMap<PcmCostElementType, BigDecimal>(PcmCostElementType.class);
        for (PcmCostRecordValue value : pcmCostRecord.getActiveCostRecordRange().getCostRecordValues().values()) {
            PcmCostElementType costElementType = value.getCostElement().getCostElementType();
            BigDecimal total = retval.get(costElementType);
            if (total == null) {
                total = BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP);
            }
            total = total.add(value.getCostValue()).setScale(6,BigDecimal.ROUND_HALF_UP);
            retval.put(costElementType, total); // BigDecimal is immutable so have to insert again
        }
        return retval;
    }

    public BigDecimal getComputedTotalNotOfCostElementTypeFixed()
    {
        return getComputedTotalNotOfCostElementType(PcmCostElementType.FIXED);
    }

    /**
     * get Computed total excluding fixed costs in USD.
     *
     * @return Computed total excluding fixed cost in USD
     */
    public BigDecimal getComputedTotalNotOfCostElementTypeFixedInUSD() {
        String localCurrency = pcmCostRecord.getActiveCostRecordRange().getCostRecord().getSourcingLane().getCurrencyCode();
        String defaultCurrency = pcmConfigUtil.getString("pcm.mpn.cost.defaultcurrency", "USD");
        // If local currency is USD, just return the computed total
        if(defaultCurrency.equalsIgnoreCase(localCurrency)) {
            return getComputedTotalNotOfCostElementType(PcmCostElementType.FIXED);
        }
        Date effectiveFromDate = pcmCostRecord.getActiveCostRecordRange().getCostRecord().getEffectiveFromDt();
        long beKey = pcmCostRecord.getActiveCostRecordRange().getCostRecord().getSourcingLane().getItem().getBusinessEntity().getBusinessEntityKey();
        BigDecimal conversionRate = CurrencyService.getCurrencyConversionRate(effectiveFromDate, beKey, localCurrency, "USD");
        if(conversionRate != null && conversionRate.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal total = getComputedTotalNotOfCostElementType(PcmCostElementType.FIXED);
            int maxFractionDigits = pcmConfigUtil.getInteger("pcm.costrecord.maxFractionDigits", 6);
            return total.multiply(conversionRate).setScale(maxFractionDigits, RoundingMode.HALF_UP);
        } else {
            log.warn("Currency conversion rate not found for BE Key: {}, From Currency: {}, To Currency: USD", beKey, localCurrency);
            return null; // or BigDecimal.ZERO if you prefer to avoid nulls
        }
    }

    public boolean hasCostRecordValueOfCostElementType(PcmCostElementType type) {

        for (PcmCostRecordValue value : pcmCostRecord.getActiveCostRecordRange().getCostRecordValues().values()) {
            if (value.getCostElement().isOfType(type)) {

                return true;
            }
        }
        return false;
    }

}
