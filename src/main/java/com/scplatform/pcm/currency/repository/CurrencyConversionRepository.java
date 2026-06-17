/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.currency.repository;

import com.scplatform.pcm.currency.entity.CurrencyConversion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Repository for currency conversion operations
 * Handles all database queries for currency conversion rates
 */
@Repository
public interface CurrencyConversionRepository extends JpaRepository<CurrencyConversion, Long> {

    /**
     * Find conversion rates between two currencies for a specific business entity
     * 
     * @param businessEntityKey the business entity key
     * @param fromCurrency source currency code
     * @param toCurrency target currency code
     * @return list of conversion rates
     */
    List<CurrencyConversion> findByBusinessEntityKeyAndFromCurrencyAndToCurrency(
            Long businessEntityKey,
            String fromCurrency,
            String toCurrency
    );

    /**
     * Find conversion rates that are effective on a specific date
     * 
     * @param businessEntityKey the business entity key
     * @param fromCurrency source currency code
     * @param toCurrency target currency code
     * @param startDate the effective date
     * @return list of effective conversion rates
     */
    @Query("SELECT cc FROM CurrencyConversion cc " +
           "WHERE (cc.businessEntityKey = :businessEntityKey OR cc.businessEntityKey = -1) " +
           "AND cc.fromCurrency = :fromCurrency " +
           "AND cc.toCurrency = :toCurrency " +
           "AND cc.startDate <= :startDate " +
           "AND (cc.endDate IS NULL OR cc.endDate >= :startDate)")
    List<CurrencyConversion> findEffectiveConversionRates(
            @Param("businessEntityKey") Long businessEntityKey,
            @Param("fromCurrency") String fromCurrency,
            @Param("toCurrency") String toCurrency,
            @Param("startDate") Date startDate
    );

    /**
     * Get average conversion rate between two currencies
     * 
     * @param businessEntityKey the business entity key (-1 for all)
     * @param fromCurrency source currency code
     * @param toCurrency target currency code
     * @return average conversion rate or null
     */
    @Query("SELECT AVG(cc.conversionRate) FROM CurrencyConversion cc " +
           "WHERE (cc.businessEntityKey = :businessEntityKey OR cc.businessEntityKey = -1) " +
           "AND cc.fromCurrency = :fromCurrency " +
           "AND cc.toCurrency = :toCurrency")
    BigDecimal getAverageConversionRate(
            @Param("businessEntityKey") Long businessEntityKey,
            @Param("fromCurrency") String fromCurrency,
            @Param("toCurrency") String toCurrency
    );

    /**
     * Get average effective conversion rate on a specific date
     * 
     * @param businessEntityKey the business entity key (-1 for all)
     * @param fromCurrency source currency code
     * @param toCurrency target currency code
     * @param startDate the effective date
     * @return average conversion rate or null
     */
    @Query("SELECT AVG(cc.conversionRate) FROM CurrencyConversion cc " +
           "WHERE (cc.businessEntityKey = :businessEntityKey OR cc.businessEntityKey = -1) " +
           "AND cc.fromCurrency = :fromCurrency " +
           "AND cc.toCurrency = :toCurrency " +
           "AND cc.startDate <= :startDate " +
           "AND (cc.endDate IS NULL OR cc.endDate >= :startDate)")
    BigDecimal getAverageEffectiveConversionRate(
            @Param("businessEntityKey") Long businessEntityKey,
            @Param("fromCurrency") String fromCurrency,
            @Param("toCurrency") String toCurrency,
            @Param("startDate") Date startDate
    );



}

