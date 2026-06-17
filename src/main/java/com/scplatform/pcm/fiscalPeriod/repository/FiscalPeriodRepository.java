/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.fiscalPeriod.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.fiscalPeriod.entity.FiscalPeriod;

/**W
 * Spring Data JPA Repository for FiscalPeriod entity.
 * Provides data access operations for FiscalPeriod entities using Spring Data JPA annotations.
 */
@Repository
public interface FiscalPeriodRepository extends JpaRepository<FiscalPeriod, Long> {

    /**
     * Find fiscal periods that have ended before the given date.
     * Ordered by fiscal period start date in descending order.
     * 
     * @param startAt the reference date to check periods against
     * @param fiscalPeriodType the type of fiscal period to filter by
     * @return list of past FiscalPeriod entities
     */
    @Query(value = "SELECT FISCAL_PERIOD_TYPE, FISCAL_PERIOD_NAME, FISCAL_PERIOD, FISCAL_PERIOD_START_DATE, FISCAL_PERIOD_END_DATE "
            + "FROM FISCAL_PERIOD "
            + "WHERE TRUNC(FISCAL_PERIOD_END_DATE) < TRUNC(:startAt) "
            + "AND FISCAL_PERIOD_TYPE = :fiscalPeriodType "
            + "ORDER BY FISCAL_PERIOD_START_DATE DESC", nativeQuery = true)
    List<FiscalPeriod> findPastPeriods(
            @Param("startAt") Date startAt,
            @Param("fiscalPeriodType") String fiscalPeriodType);

    /**
     * Find fiscal periods that have ended before the given date with max results limit.
     * Ordered by fiscal period start date in descending order.
     * 
     * @param startAt the reference date to check periods against
     * @param fiscalPeriodType the type of fiscal period to filter by
     * @param maxResults the maximum number of results to return
     * @return list of past FiscalPeriod entities
     */
    @Query(value = "SELECT FISCAL_PERIOD_TYPE, FISCAL_PERIOD_NAME, FISCAL_PERIOD, FISCAL_PERIOD_START_DATE, FISCAL_PERIOD_END_DATE "
            + "FROM (SELECT FISCAL_PERIOD_TYPE, FISCAL_PERIOD_NAME, FISCAL_PERIOD, FISCAL_PERIOD_START_DATE, FISCAL_PERIOD_END_DATE "
            + "FROM FISCAL_PERIOD "
            + "WHERE TRUNC(FISCAL_PERIOD_END_DATE) < TRUNC(:startAt) "
            + "AND FISCAL_PERIOD_TYPE = :fiscalPeriodType "
            + "ORDER BY FISCAL_PERIOD_START_DATE DESC) "
            + "WHERE ROWNUM <= :maxResults", nativeQuery = true)
    List<FiscalPeriod> findPastPeriods(
            @Param("startAt") Date startAt,
            @Param("fiscalPeriodType") String fiscalPeriodType,
            @Param("maxResults") int maxResults);

    /**
     * Find fiscal periods that are current or in the future.
     * Ordered by fiscal period start date in ascending order.
     * 
     * @param startAt the reference date to check periods against
     * @param fiscalPeriodType the type of fiscal period to filter by
     * @return list of current and future FiscalPeriod entities
     */
    @Query(value = "SELECT FISCAL_PERIOD_TYPE, FISCAL_PERIOD_NAME, FISCAL_PERIOD, FISCAL_PERIOD_START_DATE, FISCAL_PERIOD_END_DATE "
            + "FROM FISCAL_PERIOD "
            + "WHERE TRUNC(FISCAL_PERIOD_END_DATE) >= TRUNC(:startAt) "
            + "AND FISCAL_PERIOD_TYPE = :fiscalPeriodType "
            + "ORDER BY FISCAL_PERIOD_START_DATE ASC", nativeQuery = true)
    List<FiscalPeriod> findCurrentAndFuturePeriods(
            @Param("startAt") Date startAt,
            @Param("fiscalPeriodType") String fiscalPeriodType);

    /**
     * Find fiscal periods that are current or in the future with max results limit.
     * Ordered by fiscal period start date in ascending order.
     * 
     * @param startAt the reference date to check periods against
     * @param fiscalPeriodType the type of fiscal period to filter by
     * @param maxResults the maximum number of results to return
     * @return list of current and future FiscalPeriod entities
     */
    @Query(value = "SELECT FISCAL_PERIOD_TYPE, FISCAL_PERIOD_NAME, FISCAL_PERIOD, FISCAL_PERIOD_START_DATE, FISCAL_PERIOD_END_DATE "
            + "FROM (SELECT FISCAL_PERIOD_TYPE, FISCAL_PERIOD_NAME, FISCAL_PERIOD, FISCAL_PERIOD_START_DATE, FISCAL_PERIOD_END_DATE "
            + "FROM FISCAL_PERIOD "
            + "WHERE TRUNC(FISCAL_PERIOD_END_DATE) >= TRUNC(:startAt) "
            + "AND FISCAL_PERIOD_TYPE = :fiscalPeriodType "
            + "ORDER BY FISCAL_PERIOD_START_DATE ASC) "
            + "WHERE ROWNUM <= :maxResults", nativeQuery = true)
    List<FiscalPeriod> findCurrentAndFuturePeriods(
            @Param("startAt") Date startAt,
            @Param("fiscalPeriodType") String fiscalPeriodType,
            @Param("maxResults") int maxResults);


    List<FiscalPeriod> findByFiscalPeriodTypeAndFiscalPeriodEndDateLessThanOrderByFiscalPeriodStartDateDesc(
            String fiscalPeriodType,
            Date fiscalPeriodEndDate,
            Pageable pageable);


    List<FiscalPeriod> findByFiscalPeriodTypeAndFiscalPeriodEndDateGreaterThanEqualOrderByFiscalPeriodStartDateAsc(
            String fiscalPeriodType,
            Date fiscalPeriodEndDate,
            Pageable pageable);


/**
 * Find the earliest FiscalPeriod matching the given name, ordered by start date ascending.
 * Spring Data derives: WHERE fiscalPeriodName = :name ORDER BY fiscalPeriodStartDate ASC LIMIT 1
 */
Optional<FiscalPeriod> findByFiscalPeriodName(String name) ;
}
