/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.cost.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import com.scplatform.pcm.cost.entity.PcmCostRecord;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.entity.PcmSourcingLane;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.cost.entity.PcmCostRecord;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.entity.PcmPricingScenario;
import com.scplatform.pcm.cost.entity.PcmSourcingLane;
import com.scplatform.pcm.cost.entity.PcmSourcingLaneException;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for PcmCostRecord entity.
 * Provides data access operations for cost records.
 * Uses constructor-based dependency injection via Spring.
 */
@Repository
public interface PcmCostRecordRepository extends JpaRepository<PcmCostRecord, Long> {

	Site NULLSITE = new Site(-9999L);

	@Query("SELECT c FROM PcmCostRecord c")
	List<PcmCostRecord> getAllCostRecords();

	/**
	 * @param lane       the sourcing lane (required)
	 * @param externalId the cost record external ID (required)
	 * @return Optional containing the matching cost record, or empty if not found
	 */
	@Query("SELECT c FROM PcmCostRecord c WHERE c.sourcingLane = :lane AND c.costRecordExternalId = :externalId")
	Optional<PcmCostRecord> findCostRecordByExternalId(
		@Param("lane") PcmSourcingLane lane,
		@Param("externalId") String externalId);

	default PcmCostRecord getCostRecord(Long key) {
		return findById(key).orElse(null);
	}

	default PcmCostRecord saveOrUpdate(PcmCostRecord pcr) {
		return save(pcr);
	}

	/**
	 *
	 * @param lane              the sourcing lane (required)
	 * @param costType          the cost type (required)
	 * @param costProvider      the cost provider (nullable with null-match, optional if null)
	 * @param status            the status (required)
	 * @param fromDate          the effective from date (required, day-level comparison)
	 * @param toDate            the effective to date (nullable with null-match)
	 * @param pricingScenario   the pricing scenario (nullable with null-match)
	 * @param mpnValue          the MPN value/stringAttribute2 (optional filter, skipped if null or empty)
	 * @param fgId              the functional group ID/numberAttribute1 (optional filter, skipped if null)
	 * @return list of matching cost records ordered by effective date and update/insert dates
	 */
	@Query("SELECT c FROM PcmCostRecord c " +
		"WHERE c.sourcingLane = :lane " +
		"AND c.costType = :costType " +
		"AND (:costProvider IS NULL AND c.costProvider IS NULL OR c.costProvider = :costProvider) " +
		"AND c.status = :status " +
		"AND CAST(c.effectiveFromDt AS DATE) = CAST(:fromDate AS DATE) " +
		"AND (:toDate IS NULL AND c.effectiveToDt IS NULL OR CAST(c.effectiveToDt AS DATE) = CAST(:toDate AS DATE)) " +
		"AND (:pricingScenario IS NULL AND c.pricingScenario IS NULL OR c.pricingScenario = :pricingScenario) " +
		"AND (:mpnValue IS NULL OR :mpnValue = '' OR c.stringAttribute2 = :mpnValue) " +
		"AND (:fgId IS NULL OR c.numberAttribute1 = :fgId) " +
		"ORDER BY c.effectiveFromDt ASC, c.updateDt ASC, c.insertDt ASC")
	List<PcmCostRecord> findCostRecordByNaturalKey(
		@Param("lane") PcmSourcingLane lane,
		@Param("costType") PcmCostType costType,
		@Param("costProvider") BusinessEntity costProvider,
		@Param("status") String status,
		@Param("fromDate") Date fromDate,
		@Param("toDate") Date toDate,
		@Param("pricingScenario") PcmPricingScenario pricingScenario,
		@Param("mpnValue") String mpnValue,
		@Param("fgId") Integer fgId);

	default List<PcmCostRecord> findCostRecordByNaturalKey(
		PcmSourcingLane lane,
		PcmCostType costType,
		BusinessEntity costProvider,
		String status,
		Date fromDate,
		Date toDate,
		String mpnValue,
		Integer fgId) {
		return findCostRecordByNaturalKey(lane, costType, costProvider, status, fromDate, toDate, null, mpnValue, fgId);
	}

	/**
	 * @param lane              the sourcing lane (required)
	 * @param costType          the cost type (required)
	 * @param costProvider      the cost provider (nullable with null-match)
	 * @param status            the status (required)
	 * @param fromDate          the effective from date (required, day-level comparison)
	 * @param toDate            the effective to date (nullable with null-match)
	 * @param mpnValue          the MPN value/stringAttribute2 (optional filter)
	 * @return list of matching cost records ordered by effective date and update/insert dates
	 */
	default List<PcmCostRecord> findCostRecordByNaturalKey(
		PcmSourcingLane lane,
		PcmCostType costType,
		BusinessEntity costProvider,
		String status,
		Date fromDate,
		Date toDate,
		String mpnValue) {
		return findCostRecordByNaturalKey(lane, costType, costProvider, status, fromDate, toDate, null, mpnValue);
	}

	/**
	 * @param lane              the sourcing lane (required)
	 * @param costType          the cost type (required)
	 * @param costProvider      the cost provider (nullable with null-match)
	 * @param status            the status (required)
	 * @param fromDate          the effective from date (required, day-level comparison)
	 * @param toDate            the effective to date (nullable with null-match)
	 * @param pricingScenario   the pricing scenario (nullable with null-match)
	 * @param mpnValue          the MPN value/stringAttribute2 (nullable with null-match)
	 * @return list of matching cost records ordered by effective date and update/insert dates
	 */
	@Query("SELECT c FROM PcmCostRecord c " +
		"WHERE c.sourcingLane = :lane " +
		"AND c.costType = :costType " +
		"AND (:costProvider IS NULL AND c.costProvider IS NULL OR c.costProvider = :costProvider) " +
		"AND c.status = :status " +
		"AND CAST(c.effectiveFromDt AS DATE) = CAST(:fromDate AS DATE) " +
		"AND (:toDate IS NULL AND c.effectiveToDt IS NULL OR CAST(c.effectiveToDt AS DATE) = CAST(:toDate AS DATE)) " +
		"AND (:pricingScenario IS NULL AND c.pricingScenario IS NULL OR c.pricingScenario = :pricingScenario) " +
		"AND (:mpnValue IS NULL AND c.stringAttribute2 IS NULL OR c.stringAttribute2 = :mpnValue) " +
		"ORDER BY c.effectiveFromDt ASC, c.updateDt ASC, c.insertDt ASC")
	List<PcmCostRecord> findCostRecordByNaturalKey(
		@Param("lane") PcmSourcingLane lane,
		@Param("costType") PcmCostType costType,
		@Param("costProvider") BusinessEntity costProvider,
		@Param("status") String status,
		@Param("fromDate") Date fromDate,
		@Param("toDate") Date toDate,
		@Param("pricingScenario") PcmPricingScenario pricingScenario,
		@Param("mpnValue") String mpnValue);

	/**
	 * @param item           the item (required)
	 * @param supplier       the supplier/cost provider (required)
	 * @param currencyCode   the currency code (required)
	 * @param fromSite       the from site (can be null for no restriction)
	 * @param toSite         the to site (can be null for no restriction)
	 * @param crStatus       the cost record status (required)
	 * @param costTypes      the set of cost types to search for (required, non-empty)
	 * @param effectiveDate  the effective date for filtering (required)
	 * @return list of matching cost records with effective dates surrounding the given date
	 */
	@Query("SELECT c FROM PcmCostRecord c LEFT JOIN c.sourcingLane sl " +
		"WHERE sl.item = :item " +
		"AND ((:matchFromNull = true AND sl.fromSite IS NULL) OR " +
		"(:matchFromNull = false AND (:fromSite IS NULL OR sl.fromSite = :fromSite))) " +
		"AND ((:matchToNull = true AND sl.toSite IS NULL) OR " +
		"(:matchToNull = false AND (:toSite IS NULL OR sl.toSite = :toSite))) " +
		"AND (:supplier IS NULL OR sl.supplier IS NULL OR sl.supplier = :supplier) " +
		"AND (:currencyCode IS NULL OR sl.currencyCode = :currencyCode) " +
		"AND (:skipCostTypes = true OR c.costType IN :costTypes) " +
		"AND (:crStatus IS NULL OR c.status = :crStatus) " +
		"AND (:effectiveDate IS NULL OR CAST(c.effectiveFromDt AS DATE) <= CAST(:effectiveDate AS DATE)) " +
		"AND (:effectiveDate IS NULL OR c.effectiveToDt IS NULL OR CAST(c.effectiveToDt AS DATE) >= CAST(:effectiveDate AS DATE)) " +
		"ORDER BY sl.item.itemKey ASC, c.costType.costTypeKey ASC, sl.sourcingLaneKey ASC")
	List<PcmCostRecord> findCostRecordsBySitesAndCostTypesInternal(
		@Param("item") Item item,
		@Param("supplier") BusinessEntity supplier,
		@Param("currencyCode") String currencyCode,
		@Param("fromSite") Site fromSite,
		@Param("toSite") Site toSite,
		@Param("crStatus") String crStatus,
		@Param("costTypes") Set<PcmCostType> costTypes,
		@Param("skipCostTypes") boolean skipCostTypes,
		@Param("matchFromNull") boolean matchFromNull,
		@Param("matchToNull") boolean matchToNull,
		@Param("effectiveDate") Date effectiveDate);

	default List<PcmCostRecord> findCostRecordsBySitesAndCostTypes(
		Item item,
		BusinessEntity supplier,
		String currencyCode,
		Site fromSite,
		Site toSite,
		String crStatus,
		Set<PcmCostType> costTypes,
		Date effectiveDate) {
		boolean matchFromNull = NULLSITE.equals(fromSite);
		boolean matchToNull = NULLSITE.equals(toSite);
		boolean skipCostTypes = costTypes == null || costTypes.isEmpty();
		Set<PcmCostType> normalizedCostTypes = skipCostTypes ? Collections.emptySet() : costTypes;
		Site normalizedFrom = matchFromNull ? null : fromSite;
		Site normalizedTo = matchToNull ? null : toSite;
		return findCostRecordsBySitesAndCostTypesInternal(item, supplier, currencyCode, normalizedFrom, normalizedTo,
			crStatus, normalizedCostTypes, skipCostTypes, matchFromNull, matchToNull, effectiveDate);
	}


	/**
	 * @param item           the item (required)
	 * @param supplier       the supplier/cost provider (optional: null allows any, non-null allows null OR match)
	 * @param currencyCode   the currency code (optional: null skips filter)
	 * @param fromSite       the from site (optional: null skips filter)
	 * @param toSite         the to site (optional: null skips filter)
	 * @param crStatus       the cost record status (optional: null skips filter)
	 * @param costTypes      the set of cost types to search for (optional: null/empty skips filter)
	 * @param effectiveDate  the effective date (required) — matches records where effective date range brackets this date
	 * @return list of matching cost records ordered by item, costType, then sourcing lane id
	 */
	@Query("SELECT c FROM PcmCostRecord c LEFT JOIN c.sourcingLane sl " +
		"WHERE sl.item = :item " +
		"AND ((:matchFromNull = true AND sl.fromSite IS NULL) OR " +
		"(:matchFromNull = false AND (:fromSite IS NULL OR sl.fromSite = :fromSite))) " +
		"AND ((:matchToNull = true AND sl.toSite IS NULL) OR " +
		"(:matchToNull = false AND (:toSite IS NULL OR sl.toSite = :toSite))) " +
		"AND (:supplier IS NULL OR sl.supplier IS NULL OR sl.supplier = :supplier) " +
		"AND (:currencyCode IS NULL OR sl.currencyCode = :currencyCode) " +
		"AND (:skipCostTypes = true OR c.costType IN :costTypes) " +
		"AND (:crStatus IS NULL OR c.status = :crStatus) " +
		"AND (:effectiveDate IS NULL OR CAST(c.effectiveFromDt AS DATE) <= CAST(:effectiveDate AS DATE)) " +
		"AND (:effectiveDate IS NULL OR c.effectiveToDt IS NULL OR CAST(c.effectiveToDt AS DATE) >= CAST(:effectiveDate AS DATE)) " +
		"ORDER BY sl.item.itemKey ASC, c.costType.costTypeKey ASC, sl.sourcingLaneKey ASC")
	List<PcmCostRecord> findCostRecordsInternal(
		@Param("item") Item item,
		@Param("supplier") BusinessEntity supplier,
		@Param("currencyCode") String currencyCode,
		@Param("fromSite") Site fromSite,
		@Param("toSite") Site toSite,
		@Param("crStatus") String crStatus,
		@Param("costTypes") Set<PcmCostType> costTypes,
		@Param("skipCostTypes") boolean skipCostTypes,
		@Param("matchFromNull") boolean matchFromNull,
		@Param("matchToNull") boolean matchToNull,
		@Param("effectiveDate") Date effectiveDate);

	default List<PcmCostRecord> findCostRecords(
		Item item,
		BusinessEntity supplier,
		String currencyCode,
		Site fromSite,
		Site toSite,
		String crStatus,
		Set<PcmCostType> costTypes,
		Date effectiveDate) {
		boolean matchFromNull = NULLSITE.equals(fromSite);
		boolean matchToNull = NULLSITE.equals(toSite);
		boolean skipCostTypes = costTypes == null || costTypes.isEmpty();
		Set<PcmCostType> normalizedCostTypes = skipCostTypes ? Collections.emptySet() : costTypes;
		Site normalizedFrom = matchFromNull ? null : fromSite;
		Site normalizedTo = matchToNull ? null : toSite;
		return findCostRecordsInternal(item, supplier, currencyCode, normalizedFrom, normalizedTo, crStatus,
			normalizedCostTypes, skipCostTypes, matchFromNull, matchToNull, effectiveDate);
	}

	/**
	 * Find all cost records for a given item, ordered by item, cost type, then sourcing lane id.
	 *
	 * @param item the item to search for (required)
	 * @return list of matching cost records ordered by item, costType, then sourcing lane id
	 */
	@Query("SELECT c FROM PcmCostRecord c LEFT JOIN c.sourcingLane sl " +
		"WHERE sl.item = :item " +
		"ORDER BY sl.item.itemKey ASC, c.costType.costTypeKey ASC, sl.sourcingLaneKey ASC")
	List<PcmCostRecord> findCostRecordsByItem(@Param("item") Item item);

	default List<PcmCostRecord> findCostRecords(Item item) {
		return findCostRecordsByItem(item);
	}

	/**
	 * Count approved cost records in the past for a given sourcing lane.
	 *
	 * @param sourcingLane    sourcing lane to match
	 * @param status          status to match (for approved state)
	 * @param effectiveFromDt upper-bound date for effective-from date
	 * @return count of matching records
	 */
	@Query("SELECT COUNT(pcr) FROM PcmCostRecord pcr " +
		"WHERE pcr.sourcingLane = :sourcingLane " +
		"AND pcr.status = :status " +
		"AND CAST(pcr.effectiveFromDt AS DATE) <= CAST(:effectiveFromDt AS DATE)")
	Long countCostRecordsApprovedInPastByLane(
		@Param("sourcingLane") PcmSourcingLane sourcingLane,
		@Param("status") String status,
		@Param("effectiveFromDt") Date effectiveFromDt);

	default int findCostRecordsApprovedInPastCount(PcmSourcingLane sourcingLane, String status, Date effectiveFromDt) {
		Long count = countCostRecordsApprovedInPastByLane(sourcingLane, status, effectiveFromDt);
		return count == null ? 0 : count.intValue();
	}

	/**
	 * Count approved cost records in the past for a sourcing lane key.
	 *
	 * @param sourcingLaneKey sourcing lane key to match
	 * @param status          status to match (for approved state)
	 * @param effectiveFromDt upper-bound date for effective-from date
	 * @return count of matching records
	 */
	@Query("SELECT COUNT(pcr) FROM PcmCostRecord pcr " +
		"WHERE pcr.sourcingLane.sourcingLaneKey = :sourcingLaneKey " +
		"AND pcr.status = :status " +
		"AND CAST(pcr.effectiveFromDt AS DATE) <= CAST(:effectiveFromDt AS DATE)")
	Long countCostRecordsApprovedInPastByLaneKey(
		@Param("sourcingLaneKey") Long sourcingLaneKey,
		@Param("status") String status,
		@Param("effectiveFromDt") Date effectiveFromDt);

	default int findCostRecordsApprovedInPastCount(Long sourcingLaneKey, String status, Date effectiveFromDt) {
		Long count = countCostRecordsApprovedInPastByLaneKey(sourcingLaneKey, status, effectiveFromDt);
		return count == null ? 0 : count.intValue();
	}

	/**
	 * Find cost records by sourcing lane, excluding provided states when present.
	 *
	 * @param sourcingLane sourcing lane to match
	 * @param states       states to exclude
	 * @param skipStates   true when states list is null/empty
	 * @return list of cost records for lane after state exclusions
	 */
	@Query("SELECT pcr FROM PcmCostRecord pcr " +
		"WHERE pcr.sourcingLane = :sourcingLane " +
		"AND (:skipStates = true OR pcr.status NOT IN :states)")
	List<PcmCostRecord> getCostRecordsForLaneInternal(
		@Param("sourcingLane") PcmSourcingLane sourcingLane,
		@Param("states") List<String> states,
		@Param("skipStates") boolean skipStates);

	default List<PcmCostRecord> getCostRecordsForLane(PcmSourcingLane sourcingLane, List<String> states) {
		boolean skipStates = states == null || states.isEmpty();
		List<String> normalizedStates = skipStates ? Collections.emptyList() : states;
		return getCostRecordsForLaneInternal(sourcingLane, normalizedStates, skipStates);
	}

	/**
	 * Find future cost records in a lane with optional state/type filtering.
	 *
	 * @param sourcingLane sourcing lane to match
	 * @param states       allowed states; null/empty means no state filtering
	 * @param types        allowed cost types; null/empty means no type filtering
	 * @param currentDate  cutoff date; records must have effectiveFromDt greater than this date
	 * @param skipStates   true when states list is null/empty
	 * @param skipTypes    true when types set is null/empty
	 * @return matching records ordered by effectiveFromDt ascending
	 */
	@Query("SELECT pcr FROM PcmCostRecord pcr " +
		"WHERE pcr.sourcingLane = :sourcingLane " +
		"AND (:skipStates = true OR pcr.status IN :states) " +
		"AND (:skipTypes = true OR pcr.costType IN :types) " +
		"AND CAST(pcr.effectiveFromDt AS DATE) > CAST(:currentDate AS DATE) " +
		"ORDER BY pcr.effectiveFromDt ASC")
	List<PcmCostRecord> getFutureCostRecordsInLaneInternal(
		@Param("sourcingLane") PcmSourcingLane sourcingLane,
		@Param("states") List<String> states,
		@Param("types") Set<PcmCostType> types,
		@Param("currentDate") Date currentDate,
		@Param("skipStates") boolean skipStates,
		@Param("skipTypes") boolean skipTypes);

	default List<PcmCostRecord> getFutureCostRecordsInLane(
		PcmSourcingLane sourcingLane,
		List<String> states,
		Set<PcmCostType> types,
		Date currentDate) {
		boolean skipStates = states == null || states.isEmpty();
		boolean skipTypes = types == null || types.isEmpty();
		List<String> normalizedStates = skipStates ? Collections.emptyList() : states;
		Set<PcmCostType> normalizedTypes = skipTypes ? Collections.emptySet() : types;
		return getFutureCostRecordsInLaneInternal(
			sourcingLane,
			normalizedStates,
			normalizedTypes,
			currentDate,
			skipStates,
			skipTypes);
	}

	/**
	 * Find neighboring/overlapping cost records for validation against a candidate record.
	 *
	 * @param sourcingLaneKey sourcing lane key
	 * @param startDate       candidate effective-from date
	 * @param endDate         candidate effective-to date (nullable)
	 * @param status          candidate status
	 * @param costRecordKey   current record key to exclude (nullable)
	 * @return matching records in same lane/status that overlap or touch candidate date range
	 */
	@Query("SELECT pcr FROM PcmCostRecord pcr " +
		"WHERE pcr.sourcingLane.sourcingLaneKey = :sourcingLaneKey " +
		"AND pcr.status = :status " +
		"AND (:costRecordKey IS NULL OR pcr.costRecordKey <> :costRecordKey) " +
		"AND (" +
		"  CAST(pcr.effectiveFromDt AS DATE) <= CAST(COALESCE(:endDate, :startDate) AS DATE) " +
		"  AND (pcr.effectiveToDt IS NULL OR CAST(pcr.effectiveToDt AS DATE) >= CAST(:startDate AS DATE))" +
		") " +
		"ORDER BY pcr.effectiveFromDt ASC")
	List<PcmCostRecord> getExistingCostRecordsToValidateAgainst(
		@Param("sourcingLaneKey") Long sourcingLaneKey,
		@Param("startDate") Date startDate,
		@Param("endDate") Date endDate,
		@Param("status") String status,
		@Param("costRecordKey") Long costRecordKey);

	/**
	 * Count cost records matching specific criteria.
	 *
	 * @param cr                  the cost record to use for sourcing lane lookup (required if not using sourcing lane exception)
	 * @param status              the cost record status (nullable with null-match)
	 * @param costTypes           the set of cost types to filter by (nullable - empty set skips filter)
	 * @param effectiveDate       the effective date for range filtering (required)
	 * @param countMe             if false, exclude the given cost record (costRecordKey) from count
	 * @return count of matching cost records
	 */
	@Query("SELECT COUNT(c) FROM PcmCostRecord c LEFT JOIN c.sourcingLane sl " +
		"WHERE " +
		"((:sl_item IS NULL AND c.sourcingLane IS NULL) OR " +
		"(:sl_item IS NOT NULL AND sl.item = :sl_item)) " +
		"AND ((:sl_supplier IS NULL OR sl.supplier IS NULL) OR sl.supplier = :sl_supplier) " +
		"AND ((:sl_currencyCode IS NULL OR sl.currencyCode = :sl_currencyCode)) " +
		"AND ((:sl_fromSite IS NULL OR sl.fromSite = :sl_fromSite)) " +
		"AND ((:sl_toSite IS NULL OR sl.toSite = :sl_toSite)) " +
		"AND (:status IS NULL OR c.status = :status) " +
		"AND (:skipCostTypes = true OR c.costType IN :costTypes) " +
		"AND (:effectiveDate IS NULL OR CAST(c.effectiveFromDt AS DATE) <= CAST(:effectiveDate AS DATE)) " +
		"AND (:effectiveDate IS NULL OR c.effectiveToDt IS NULL OR CAST(c.effectiveToDt AS DATE) >= CAST(:effectiveDate AS DATE)) " +
		"AND (:countMe = true OR c.costRecordKey != :costRecordKey)")
	long countCostRecords(
		@Param("sl_item") Item item,
		@Param("sl_supplier") BusinessEntity supplier,
		@Param("sl_currencyCode") String currencyCode,
		@Param("sl_fromSite") Site fromSite,
		@Param("sl_toSite") Site toSite,
		@Param("status") String status,
		@Param("costTypes") Set<PcmCostType> costTypes,
		@Param("skipCostTypes") boolean skipCostTypes,
		@Param("effectiveDate") Date effectiveDate,
		@Param("costRecordKey") Long costRecordKey,
		@Param("countMe") boolean countMe);

	default int findCostRecordsCount(
		PcmCostRecord cr,
		String status,
		Set<PcmCostType> costTypes,
		Date effectiveDate,
		boolean countMe) {

		PcmSourcingLane sl = cr.getSourcingLane();
		Item item = null;
		BusinessEntity supplier = null;
		String currencyCode = null;
		Site fromSite = null;
		Site toSite = null;

		if (sl != null) {
			item = sl.getItem();
			supplier = sl.getSupplier();
			currencyCode = sl.getCurrencyCode();
			fromSite = sl.getFromSite();
			toSite = sl.getToSite();
		} else {
			// Fall back to sourcing lane exception if sourcing lane is null
			PcmSourcingLaneException sle = cr.getSourcingLaneException();
			if (sle != null) {
				item = sle.getItem();
				supplier = sle.getSupplier();
				currencyCode = sle.getCurrencyCode();
				fromSite = sle.getFromSite();
				toSite = sle.getToSite();
			}
		}

		boolean skipCostTypes = costTypes == null || costTypes.isEmpty();
		Set<PcmCostType> normalizedCostTypes = skipCostTypes ? Collections.emptySet() : costTypes;
		long count = countCostRecords(item, supplier, currencyCode, fromSite, toSite, status, normalizedCostTypes,
			skipCostTypes, effectiveDate, cr.getCostRecordKey(), countMe);
		return (int) count;
	}
    
    /**
     * Find cost records by sourcing lane
     * @param sourcingLane the sourcing lane
     * @return list of cost records for that lane
     */
    List<PcmCostRecord> findBySourcingLane(PcmSourcingLane sourcingLane);
    
    /**
     * Find cost records by sourcing lane and cost type
     * @param sourcingLane the sourcing lane
     * @param costType the cost type
     * @return list of matching cost records
     */
    List<PcmCostRecord> findBySourcingLaneAndCostType(PcmSourcingLane sourcingLane, PcmCostType costType);
    
    /**
     * Find cost records by sourcing lane and status
     * @param sourcingLane the sourcing lane
     * @param status the status value
     * @return list of matching cost records
     */
    List<PcmCostRecord> findBySourcingLaneAndStatus(PcmSourcingLane sourcingLane, String status);
    
    /**
     * Find cost records by status
     * @param status the status value
     * @return list of cost records with given status
     */
    List<PcmCostRecord> findByStatus(String status);
    
    /**
     * Find cost records by cost type
     * @param costType the cost type
     * @return list of cost records with that cost type
     */
    List<PcmCostRecord> findByCostType(PcmCostType costType);
    
    /**
     * Find current (not deleted) cost records
     * @return list of current cost records
     */
    List<PcmCostRecord> findByCurrentFlagTrue();
    
    /**
     * Find current cost records for a sourcing lane
     * @param sourcingLane the sourcing lane
     * @return list of current cost records
     */
    List<PcmCostRecord> findBySourcingLaneAndCurrentFlagTrue(PcmSourcingLane sourcingLane);
    
    /**
     * Find deleted cost records
     * @return list of deleted cost records
     */
    List<PcmCostRecord> findByDeleteFlagTrue();
    
    /**
     * Find cost records effective on a specific date for a sourcing lane
     * @param sourcingLane the sourcing lane
     * @param effectiveDate the date to check
     * @return list of effective cost records on that date
     */
    @Query("SELECT cr FROM PcmCostRecord cr " +
           "WHERE cr.sourcingLane = :sourcingLane " +
           "AND cr.effectiveFromDt <= :effectiveDate " +
           "AND (cr.effectiveToDt IS NULL OR cr.effectiveToDt >= :effectiveDate)")
    List<PcmCostRecord> findEffectiveOnDate(
            @Param("sourcingLane") PcmSourcingLane sourcingLane,
            @Param("effectiveDate") Date effectiveDate
    );
    
    /**
     * Find cost records effective now for a sourcing lane
     * @param sourcingLane the sourcing lane
     * @return list of currently effective cost records
     */
    @Query("SELECT cr FROM PcmCostRecord cr " +
           "WHERE cr.sourcingLane = :sourcingLane " +
           "AND cr.effectiveFromDt <= CURRENT_DATE " +
           "AND (cr.effectiveToDt IS NULL OR cr.effectiveToDt >= CURRENT_DATE)")
    List<PcmCostRecord> findEffectiveNow(@Param("sourcingLane") PcmSourcingLane sourcingLane);
    
    /**
     * Find overlapping cost records (for duplicate detection)
     * @param sourcingLane the sourcing lane
     * @param costType the cost type
     * @param status the status
     * @param fromDate the start date
     * @param toDate the end date
     * @return list of potentially overlapping cost records
     */
    @Query("SELECT cr FROM PcmCostRecord cr " +
           "WHERE cr.sourcingLane = :sourcingLane " +
           "AND cr.costType = :costType " +
           "AND cr.status = :status " +
           "AND cr.effectiveFromDt <= :toDate " +
           "AND (cr.effectiveToDt IS NULL OR cr.effectiveToDt >= :fromDate)")
    List<PcmCostRecord> findOverlappingRecords(
            @Param("sourcingLane") PcmSourcingLane sourcingLane,
            @Param("costType") PcmCostType costType,
            @Param("status") String status,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate
    );
    
    /**
     * Find cost record by external ID
     * @param externalId the external ID
     * @return the cost record or empty Optional
     */
    Optional<PcmCostRecord> findByCostRecordExternalId(String externalId);
    
    /**
     * Count cost records by sourcing lane
     * @param sourcingLane the sourcing lane
     * @return count of cost records
     */
    long countBySourcingLane(PcmSourcingLane sourcingLane);

    @Query(name = "dashboard:costRecord")
    List<Object[]> findCostRecordStatus(@Param("status") List<String> status,
                                        @Param("cutoffDate") Date cutoffDate);

    @Query(name = "dashboard:costRecordForOwner")
    List<Object[]> findCostRecordStatusForOwner(@Param("status") List<String> status,
                                                @Param("cutoffDate") Date cutoffDate,
                                                @Param("userId") String userId);
}
