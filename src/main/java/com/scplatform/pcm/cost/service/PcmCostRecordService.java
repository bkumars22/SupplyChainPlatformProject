/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.cost.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.cost.entity.PcmCostElement;
import com.scplatform.pcm.cost.entity.PcmCostRecord;
import com.scplatform.pcm.cost.entity.PcmSourcingLane;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.enums.PcmCostElementType;
import com.scplatform.pcm.cost.repository.PcmCostElementRepository;
import com.scplatform.pcm.cost.repository.PcmCostRecordRepository;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.util.datetime.DateAndTimeUtils;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.entity.*;

import com.scplatform.pcm.currency.service.CurrencyService;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import static com.scplatform.pcm.util.common.SCPlatformConstant.*;



/**
 * Service for managing cost records and cost elements.
 * Provides utility methods for ordering cost elements based on configuration
 * and cost element types, as well as counting and retrieving cost records.
 */
@Service
@RequiredArgsConstructor
public class PcmCostRecordService {

	private static final Logger log = LogManager.getLogger(PcmCostRecordService.class);

	private final PcmCostRecordRepository pcmCostRecordRepository;
	private final PcmCostElementRepository pcmCostElementRepository;
	private final PcmCostRecordRangeService pcmCostRecordRangeService;
	private final PcmConfigUtil pcmConfigUtil;
	private PcmCostRecord pcmCostRecord;
	private PcmSourcingLane sourcingLane;

	public static final String DEFAULT_UOM = "EA";

	@Value("${pcm.costRecord.approved.status:APPROVED}")
	private String approvedStatus;

	/**
	 * Gets the computed total from the active cost record range.
	 * Returns BigDecimal.ZERO (scale 6, HALF_UP) if no active range exists.
	 *
	 * @param costRecord the cost record
	 * @return the computed total
	 */
	public BigDecimal getTotal(PcmCostRecord costRecord) {
		PcmCostRecordRange activeRange = costRecord.getActiveCostRecordRange();
		if (activeRange != null) {
			return pcmCostRecordRangeService.getComputedTotal(activeRange);
		}
		return BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
	}

	/**
	 * Returns a detailed string representation of the cost record including computed total.
	 * This replaces the original PcmCostRecord.toString() that included total computation.
	 *
	 * @param costRecord the cost record
	 * @return detailed string with total, status, and date range
	 */
	public String toDetailString(PcmCostRecord costRecord) {
		StringBuilder sb = new StringBuilder();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		sb.append(costRecord.getCostType());
		PcmCostRecordRange activeRange = costRecord.getActiveCostRecordRange();
		BigDecimal total = BigDecimal.ZERO;
		if (activeRange != null) {
			total = pcmCostRecordRangeService.getComputedTotalNotOfCostElementTypeFixed(activeRange);
		}
		sb.append("[total:").append(total);
		sb.append(", status:").append(costRecord.getStatus());
		sb.append(", from:");
		if (costRecord.getEffectiveFromDt() != null) {
			sb.append(df.format(costRecord.getEffectiveFromDt()));
		}
		sb.append(", to:");
		if (costRecord.getEffectiveToDt() != null) {
			sb.append(df.format(costRecord.getEffectiveToDt()));
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Returns an ordered LinkedHashSet of cost elements based on the configured cost element type order.
	 * Elements are first sorted by type (using configuration order), then by display order, then by cost element key.
	 *
	 * @param costElements the set of cost elements to order
	 * @return LinkedHashSet with elements ordered by type, display order, and key
	 */
	public LinkedHashSet<PcmCostElement> getOrderedCostElements(Set<PcmCostElement> costElements) {
		Set<PcmCostElement> retval = new TreeSet<>(getCostElementComparator());
		retval.addAll(costElements);
		LinkedHashSet<PcmCostElement> linkedRetval = new LinkedHashSet<>();
		linkedRetval.addAll(retval);
		return linkedRetval;
	}

	/**
	 * Get cost elements ordered by display order with duplicate cost-element keys removed.
	 *
	 * @return distinct cost elements by costElementKey
	 */
	public List<PcmCostElement> getAllDistinctCostElements() {
		Map<String, PcmCostElement> uniqueByCostElementKey = new LinkedHashMap<>();
		for (PcmCostElement costElement : pcmCostElementRepository.getAllCostElements()) {
			String costElementKey = (costElement.getId() != null) ? costElement.getId().getCostElementKey() : null;
			if (!uniqueByCostElementKey.containsKey(costElementKey)) {
				uniqueByCostElementKey.put(costElementKey, costElement);
			}
		}
		return new ArrayList<>(uniqueByCostElementKey.values());
	}

	/**
	 * Returns an ordered LinkedList of cost elements based on the configured cost element type order.
	 * Elements are first sorted by type (using configuration order), then by display order, then by cost element key.
	 *
	 * @param costElements the list of cost elements to order
	 * @return LinkedList with elements ordered by type, display order, and key
	 */
	public LinkedList<PcmCostElement> getOrderedCostElements(List<PcmCostElement> costElements) {
		LinkedList<PcmCostElement> retval = new LinkedList<>();
		retval.addAll(costElements);
		Collections.sort(retval, getCostElementComparator());
		return retval;
	}

	/**
	 * Returns an ordered SortedSet (TreeSet) of cost elements based on the configured cost element type order.
	 * Elements are first sorted by type (using configuration order), then by display order, then by cost element key.
	 *
	 * @param costElements the sorted set of cost elements to order
	 * @return TreeSet with elements ordered by type, display order, and key
	 */
	public SortedSet<PcmCostElement> getOrderedCostElements(SortedSet<PcmCostElement> costElements) {
		Comparator<PcmCostElement> cptr = getCostElementComparator();
		TreeSet<PcmCostElement> retval = new TreeSet<>(cptr);
		retval.addAll(costElements);
		return retval;
	}

	/**
	 * Comparator that uses the ordered CostElementTypes based on the configuration property
	 * <code>pcm.sourcingLane.costElementTypes.order</code> to order Cost Elements.
	 * Falls back to display order and then cost element key if types are equal.
	 *
	 * @return Comparator for PcmCostElement
	 */
	private Comparator<PcmCostElement> getCostElementComparator() {
		final List<PcmCostElementType> ordElTyps = new ArrayList<>(getOrderedCostElementTypes());
		return (o1, o2) -> new CompareToBuilder()
				.append(ordElTyps.indexOf(o1.getCostElementType()),
						ordElTyps.indexOf(o2.getCostElementType()))
				.append(o1.getDisplayOrder(), o2.getDisplayOrder())
				.append(o1.getId().getCostElementKey(), o2.getId().getCostElementKey())
				.toComparison();
	}

	/**
	 * An ordered set of the PcmCostElementTypes based on the configuration property
	 * <code>pcm.sourcingLane.costElementTypes.order</code>.
	 * If not configured, returns all types in enum order.
	 *
	 * @return LinkedHashSet of PcmCostElementType in configured order
	 */
	public LinkedHashSet<PcmCostElementType> getOrderedCostElementTypes() {
		List<PcmCostElementType> alltyps = Arrays.asList(PcmCostElementType.values());
		LinkedHashSet<PcmCostElementType> retval = new LinkedHashSet<>();
        List<String> orderList = pcmConfigUtil.getList("pcm.sourcingLane.costElementTypes.order", new ArrayList<>());
		String[] order = orderList.toArray(new String[0]);
		if (order.length == 0) {
			retval.addAll(alltyps);
		} else {
			for (String val : order) {
				PcmCostElementType typ = PcmCostElementType.valueOf(val.trim());
				retval.add(typ);
			}
			retval.addAll(alltyps); // add the remaining; ones already
									// added will already be in order
		}
		return retval;
	}

	/**
	 * For a given set of cost elements, returns a Map keyed on the CostElementType
	 * with a value equal to the number of cost elements of that type in the set.
	 *
	 * @param costElements the set of cost elements to count by type
	 * @return Map of CostElementType to count
	 */
	public Map<PcmCostElementType, Integer> getNumCostElements(Set<PcmCostElement> costElements) {
		Map<PcmCostElementType, Integer> retval = new HashMap<>();
		for (PcmCostElement element : costElements) {
			Integer i = retval.get(element.getCostElementType());
			if (i == null || i == 0) {
				i = 1;
			} else {
				i++;
			}
			retval.put(element.getCostElementType(), i);
		}
		return retval;
	}

	/**
	 * Count approved-in-past records for a sourcing lane.
	 *
	 * @param sourcingLane sourcing lane
	 * @return count of approved records where effectiveFromDt is on/before today
	 */
	public int findCostRecordsApprovedInPastCount(PcmSourcingLane sourcingLane) {
		return pcmCostRecordRepository.findCostRecordsApprovedInPastCount(
			sourcingLane,
			approvedStatus,
			DateAndTimeUtils.getCurrentDateOnly());
	}

	/**
	 * Count approved-in-past records for a sourcing lane key.
	 *
	 * @param sourcingLaneKey sourcing lane key
	 * @return count of approved records where effectiveFromDt is on/before today
	 */
	public int findCostRecordsApprovedInPastCount(Long sourcingLaneKey) {
		return pcmCostRecordRepository.findCostRecordsApprovedInPastCount(
			sourcingLaneKey,
			approvedStatus,
			DateAndTimeUtils.getCurrentDateOnly());
	}

	/**
	 * Get lane cost records, excluding the provided states when supplied.
	 *
	 * @param sourcingLane sourcing lane
	 * @param states states to exclude; null/empty means no exclusion
	 * @return lane cost records with optional state exclusions
	 */
	public List<PcmCostRecord> getCostRecordsForLane(PcmSourcingLane sourcingLane, List<String> states) {
		return pcmCostRecordRepository.getCostRecordsForLane(sourcingLane, states);
	}

	/**
	 * Get future cost records in a lane with optional state/type filters.
	 *
	 * @param sourcingLane sourcing lane
	 * @param states allowed states; null/empty means no state filtering
	 * @param types allowed cost types; null/empty means no type filtering
	 * @return future cost records ordered by effectiveFromDt ascending
	 */
	public List<PcmCostRecord> getFutureCostRecordsInLane(
		PcmSourcingLane sourcingLane,
		List<String> states,
		Set<PcmCostType> types) {
		return pcmCostRecordRepository.getFutureCostRecordsInLane(
			sourcingLane,
			states,
			types,
			DateAndTimeUtils.getCurrentDateOnly());
	}

	/**
	 * Fetch neighboring/overlapping records in the same lane/status for validation checks.
	 *
	 * @param costRecord candidate cost record
	 * @return existing records to validate against
	 */
	public List<PcmCostRecord> getExistingCostRecordsToValidateAgainst(PcmCostRecord costRecord) {
		return pcmCostRecordRepository.getExistingCostRecordsToValidateAgainst(
			costRecord.getSourcingLane().getSourcingLaneKey(),
			costRecord.getEffectiveFromDt(),
			costRecord.getEffectiveToDt(),
			costRecord.getStatus(),
			costRecord.getCostRecordKey());
	}

	/**
	 * Find cost records that apply to a site combination, walking up parent sites as needed.
	 *
	 * @param item item
	 * @param supplier supplier
	 * @param currencyCode currency code
	 * @param fromSite starting from-site
	 * @param toSite starting to-site
	 * @param crStatus cost record status
	 * @param costType single cost type to locate
	 * @param effectiveDate effective date
	 * @return matching cost records
	 */
	public List<PcmCostRecord> findCostRecordsThatApplyToSites(
		Item item,
		BusinessEntity supplier,
		String currencyCode,
		Site fromSite,
		Site toSite,
		String crStatus,
		PcmCostType costType,
		Date effectiveDate) {
		Set<PcmCostType> costTypes = new HashSet<>();
		costTypes.add(costType);
		return findCostRecordsThatApplyToSites(item, supplier, currencyCode, fromSite, toSite, crStatus, costTypes,
			effectiveDate);
	}

	/**
	 * Find cost records that apply to a site combination, walking up parent sites until all requested types are found.
	 *
	 * @param item item
	 * @param supplier supplier
	 * @param currencyCode currency code
	 * @param fromSite starting from-site
	 * @param toSite starting to-site
	 * @param crStatus cost record status
	 * @param costTypes set of cost types to locate
	 * @param effectiveDate effective date
	 * @return matching cost records
	 */
	public List<PcmCostRecord> findCostRecordsThatApplyToSites(
		Item item,
		BusinessEntity supplier,
		String currencyCode,
		Site fromSite,
		Site toSite,
		String crStatus,
		Set<PcmCostType> costTypes,
		Date effectiveDate) {
		List<PcmCostRecord> results = new ArrayList<>();
		Site workToSite = toSite;
		Set<PcmCostType> searchForSet = new HashSet<>(costTypes);

		while (true) {
			Site workFromSite = fromSite;

			while (true) {
				results.addAll(pcmCostRecordRepository.findCostRecordsBySitesAndCostTypes(
					item, supplier, currencyCode, workFromSite, workToSite, crStatus, searchForSet, effectiveDate));

				if (!results.isEmpty()) {
					searchForSet.removeAll(containsTypes(results));
					if (searchForSet.isEmpty()) {
						return results;
					}
				}

				if (workFromSite == null) {
					break;
				}
				workFromSite = workFromSite.getParentSite();
			}

			if (workToSite == null) {
				break;
			}
			workToSite = workToSite.getParentSite();
		}

		return results;
	}

	private Set<PcmCostType> containsTypes(List<PcmCostRecord> results) {
		Set<PcmCostType> costTypes = new HashSet<>();
		for (PcmCostRecord costRecord : results) {
			costTypes.add(costRecord.getCostType());
		}
		return costTypes;
	}

    public PcmCostRecordValue addCostRecordValue(PcmCostElement element, BigDecimal value, String uom) {
        PcmCostRecordRange ar = assertAndGetActiveCostRecordRange();
        PcmCostRecordValue result = ar.addCostRecordValue(element, value, uom);
        return result;
    }
    public PcmCostRecordValue getCostRecordValue(PcmCostElement element) {
        Map<String, PcmCostRecordValue> crvs = pcmCostRecord.getActiveCostRecordValues();
        if (crvs == null) {
            return null;
        }
        return crvs.get(element.getId().getCostElementKey());
    }

    public PcmCostRecordValue getCostRecordValue(String elementKey) {
        Map<String, PcmCostRecordValue> crvs = pcmCostRecord.getActiveCostRecordValues();
        if (crvs == null) {
            return null;
        }
        return crvs.get(elementKey);
    }
    protected PcmCostRecordRange assertAndGetActiveCostRecordRange() {
        PcmCostRecordRange ar = pcmCostRecord.getActiveCostRecordRange();
        if (ar == null) {
            throw new IllegalStateException("Cannot call function since active range is null");
        }
        return ar;
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

    /**
     * Asserts that an active range exists and returns it. If an active range does
     * not exist an IllegalStateException is thrown
     *
     * @return the active range
     */

    public Map<String, PcmCostRecordValue> getActiveCostRecordValues() {
        PcmCostRecordRange activeRange = this.getActiveCostRecordRange();
        if (activeRange == null) {
            return null; // TODO should we return null here ???
        }
        return activeRange.getCostRecordValues();
    }

    public boolean isRangeBased() {
        if (pcmCostRecord.getPricingScenario() == null) {
            return false;
        }
        Boolean b = pcmCostRecord.getPricingScenario().isRangeBased();
        return b == null ? false : Boolean.TRUE.equals(b);
    }

    public BigDecimal getComputedTotal() {
        BigDecimal total = BigDecimal.ZERO.setScale(6, BigDecimal.ROUND_HALF_UP);
        PcmCostRecordRange activeRange = pcmCostRecord.getActiveCostRecordRange();
        if (activeRange != null) {
            total = pcmCostRecordRangeService.getComputedTotal(activeRange);
        }
        return total;
    }

    public BigDecimal getTotalByCostElementType(PcmCostElementType type) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        PcmCostRecordRange activeRange = this.getActiveCostRecordRange();
        if (activeRange != null) {
            total = pcmCostRecordRangeService.getTotalByCostElementType(activeRange, type);
        }
        return total;
    }

    public BigDecimal getTotalByCostElementTypes(Set<PcmCostElementType> types) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        PcmCostRecordRange activeRange = this.getActiveCostRecordRange();

        if (activeRange != null) {
            for (PcmCostElementType type : types) {
                total = total.add(pcmCostRecordRangeService.getTotalByCostElementType(activeRange, type));
            }
        }
        return total;
    }

    public BigDecimal getComputedTotalByCostElementType(PcmCostElementType type) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        PcmCostRecordRange activeRange = this.getActiveCostRecordRange();
        if (activeRange != null) {
            total = pcmCostRecordRangeService.getComputedTotalByCostElementType(activeRange, type);
        }
        return total;

    }

    public BigDecimal getComputedTotalByCostElementTypes(Set<PcmCostElementType> types) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        PcmCostRecordRange activeRange = this.getActiveCostRecordRange();
        if (activeRange != null) {
            total = pcmCostRecordRangeService.getComputedTotalByCostElementTypes(activeRange, types);
        }
        return total;
    }

    public BigDecimal getComputedTotalNotOfCostElementTypeFixed() {
        return getComputedTotalNotOfCostElementType(PcmCostElementType.FIXED);
    }

    public BigDecimal getComputedTotalNotOfCostElementTypeFixedInUSD() {
        String localCurrency = pcmCostRecord.getSourcingLane().getCurrencyCode();
        String defaultCurrency = pcmConfigUtil.getString("pcm.mpn.cost.defaultcurrency", "USD");
        // If local currency is USD, just return the computed total
        if(defaultCurrency.equalsIgnoreCase(localCurrency)) {
            return getComputedTotalNotOfCostElementType(PcmCostElementType.FIXED);
        }
        Date effectiveFromDate = pcmCostRecord.getEffectiveFromDt();
        long beKey = pcmCostRecord.getSourcingLane().getItem().getBusinessEntity().getBusinessEntityKey();
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


    public BigDecimal getTotalNotOfCostElementType(PcmCostElementType excludedType) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        PcmCostRecordRange activeRange = getActiveCostRecordRange();
        if (activeRange != null) {
            for (PcmCostRecordValue value : activeRange.getCostRecordValues().values()) {
                if (value.getCostElement().isNotOfType(excludedType)) {
                    total = total.add(value.getCostValue()).setScale(6, RoundingMode.HALF_UP);
                }
            }
        }
        return total;
    }

    public EnumMap<PcmCostElementType, BigDecimal> getComputedTotalByCostElementTypeMap() {
        EnumMap<PcmCostElementType,BigDecimal> retval = new EnumMap<>(PcmCostElementType.class);
        PcmCostRecordRange activeRange = getActiveCostRecordRange();
        if (activeRange != null) {
            for (PcmCostRecordValue value : activeRange.getCostRecordValues().values()) {
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

    public BigDecimal getComputedTotalNotOfCostElementType(PcmCostElementType excludedType) {
        BigDecimal total = BigDecimal.ZERO.setScale(6, RoundingMode.HALF_UP);
        List<String> excludedCostElementsToTotal = pcmConfigUtil.getList("pcm.cost.element.excluded.in.total", new ArrayList<>());
        PcmCostRecordRange activeRange = getActiveCostRecordRange();
        if (activeRange != null) {
            for (PcmCostRecordValue value : activeRange.getCostRecordValues().values()) {
                if (value.getCostElement().isNotOfType(excludedType)
                        && !excludedCostElementsToTotal.contains(value.getCostElement().getCostElementKey())) {
                    total = total.add(value.getComputedCostValue()).setScale(6, RoundingMode.HALF_UP);
                }
            }
        }
        return total;
    }

    public boolean hasCostRecordValueOfCostElementType(PcmCostElementType type) {
        PcmCostRecordRange activeRange = getActiveCostRecordRange();
        if (activeRange != null) {
            for (PcmCostRecordValue value : activeRange.getCostRecordValues().values()) {
                if (value.getCostElement().isOfType(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void initializeValues(PcmCostType costType) {
        pcmCostRecord.addDefaultCostRecordRange();
        pcmCostRecord.getCostRecordValues().clear();
        boolean initializeToZero = initTozero();
        if (initializeToZero) {
            for (PcmCostElement element : costType.getPcmCostElements()) {
                addCostRecordValue(element, BigDecimal.ZERO, DEFAULT_UOM);
            }
        }
        pcmCostRecord.setCostType(costType);
    }

    public boolean overlap(PcmCostRecord other) {
        if (pcmCostRecord.getStatus().equals(other.getStatus()) == false) {
            return false;
        }
        if (pcmCostRecord.getCostType().equals(other.getCostType()) == false) {
            return false;
        }
        if (DateAndTimeUtils.overlap(pcmCostRecord.getEffectiveFromDt(), pcmCostRecord.getEffectiveToDt(), other.getEffectiveFromDt(),
                other.getEffectiveToDt())) {
            boolean useMPNCompare = pcmConfigUtil.getBoolean("pcm.costRecord.mpnCompare.required", false);
            if (useMPNCompare) {
                List<String> mpnFilterCriteria = pcmConfigUtil.getList("pcm.costRecord.mpnValidation.required.types");
                if (mpnFilterCriteria != null && mpnFilterCriteria.contains(pcmCostRecord.getCostType().getCostTypeKey())) {
                    if (pcmCostRecord.getStringAttribute2() != null && other.getStringAttribute2() != null) {
                        if (!pcmCostRecord.getStringAttribute2().equals(other.getStringAttribute2())) {
                            return false;
                        }
                    }

                    if ((pcmCostRecord.getStringAttribute2() == null && other.getStringAttribute2() != null)
                            || pcmCostRecord.getStringAttribute2() != null && other.getStringAttribute2() == null) {
                        return false;
                    }
                }
            }
            //handling xwap cr
            if(pcmCostRecord.getCostType().getCostTypeKey().equals("XWAP") && other.getCostType().getCostTypeKey().equals("XWAP")) {
                if (pcmCostRecord.getNumberAttribute1() != null && other.getNumberAttribute1() != null) {
                    if (!pcmCostRecord.getNumberAttribute1().equals(other.getNumberAttribute1())) {
                        return false;
                    }
                }

                if ((pcmCostRecord.getNumberAttribute1() == null && other.getNumberAttribute1() != null)
                        || pcmCostRecord.getNumberAttribute1() != null && other.getNumberAttribute1() == null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }

    public String getAuditTitle() {
        String df = SCPlatformMessages.INSTANCE.getAuditMessage("audit.dateFormat", null, null);
        if (df == null) {
            df = DEFAULT_DATE_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(df);
        List<Object> args = new ArrayList<Object>();
        args.add(sourcingLane.getAuditTitle());
        args.add(pcmCostRecord.getCostType());
        BigDecimal total = getComputedTotal();
        String formatPattern = pcmConfigUtil.getString("pcm.decimal.format.pattern", "###.######");
        DecimalFormat decimalFormat = new DecimalFormat(formatPattern);
        String totalCost = decimalFormat.format(total);
        args.add(totalCost);
        args.add(pcmCostRecord.getStatus());
        args.add(sdf.format(pcmCostRecord.getEffectiveFromDt()));
        if (pcmCostRecord.getEffectiveToDt() != null) {
            args.add(sdf.format(pcmCostRecord.getEffectiveToDt()));
        } else {
            args.add(pcmConfigUtil.getString("pcm.audit.cr.effectiveToDt.configuration", "EVERGREEN"));
        }
        args.add(pcmCostRecord.getCostRecordExternalId());
        boolean useMPNCompare = pcmConfigUtil.getBoolean("pcm.costRecord.mpnCompare.required", false);
        if (useMPNCompare && !pcmCostRecord.getCostType().getCostTypeKey().equals("XWAP")) {
            args.add(pcmCostRecord.getStringAttribute2());
            return SCPlatformMessages.INSTANCE.getAuditMessage("audit.costRecord.mpn", args.toArray(), null);
        }
        if (useMPNCompare && pcmCostRecord.getCostType().getCostTypeKey().equals("XWAP")) {
            args.add(pcmCostRecord.getStringAttribute2());
            args.add(pcmCostRecord.getNumberAttribute1());
            String configuredXlobFgName = pcmConfigUtil.getString("pcm.costRecord.nonExisting.functionalGroup.id", "SOLESOURCE");
            String XFGName = (pcmCostRecord.getNumberAttribute1() == null || pcmCostRecord.getNumberAttribute1() == -1) ? configuredXlobFgName : FunctionalGroupRepository.getXLOBFGNameByID(Long.valueOf(pcmCostRecord.getNumberAttribute1()));
            args.add(XFGName);
            return SCPlatformMessages.INSTANCE.getAuditMessage("audit.costRecord.fgId", args.toArray(), null);
        } else {
            return SCPlatformMessages.INSTANCE.getAuditMessage("audit.costRecord", args.toArray(), null);
        }
    }

    public boolean initTozero() {
        return pcmConfigUtil.getBoolean("pcm.costRecord.costRecordValue.initializeNewToZero", true);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getCostRecordStatus(List<String> status,
                                              Date cutoffDate) {
        return pcmCostRecordRepository.findCostRecordStatus(status, cutoffDate);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getCostRecordStatusForOwner(List<String> status,
                                                      Date cutoffDate, String userId) {
        return pcmCostRecordRepository.findCostRecordStatusForOwner(status, cutoffDate, userId);
    }
}
