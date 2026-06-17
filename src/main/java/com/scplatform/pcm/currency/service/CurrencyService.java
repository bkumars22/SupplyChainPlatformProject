/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.currency.service;

import com.scplatform.pcm.currency.entity.CurrencyConversion;
import com.scplatform.pcm.currency.repository.CurrencyConversionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Service for currency conversion operations
 * Handles all business logic for currency conversions
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CurrencyService {

    private static CurrencyConversionRepository currencyConversionRepository;

    /**
     * Get the average currency conversion rate for a date range
     * 
     * @param startDate the effective date (can be null for all dates)
     * @param businessEntityKey the business entity key (-1 for any)
     * @param fromCurrencyCode source currency code
     * @param toCurrencyCode target currency code
     * @return average conversion rate or null if not found
     */
    public static BigDecimal getCurrencyConversionRate(Date startDate, long businessEntityKey,
                                                       String fromCurrencyCode, String toCurrencyCode) {
        if (startDate != null) {
            // Use effective date range query
            return currencyConversionRepository.getAverageEffectiveConversionRate(
                    businessEntityKey,
                    fromCurrencyCode,
                    toCurrencyCode,
                    startDate
            );
        } else {
            // Use general query without date constraint
            return currencyConversionRepository.getAverageConversionRate(
                    businessEntityKey,
                    fromCurrencyCode,
                    toCurrencyCode
            );
        }
    }

    /**
     * Save or update a currency conversion rate
     * 
     * @param currencyConversion the conversion rate to save
     * @return the saved conversion rate
     */
    @Transactional
    public CurrencyConversion saveCurrencyConversion(CurrencyConversion currencyConversion) {
        return currencyConversionRepository.save(currencyConversion);
    }

    /**
     * Find all conversion rates between two currencies
     * 
     * @param businessEntityKey the business entity key
     * @param fromCurrency source currency
     * @param toCurrency target currency
     * @return list of conversion rates
     */
    @Transactional(readOnly = true)
    public List<CurrencyConversion> findConversionRates(Long businessEntityKey, 
                                                       String fromCurrency, 
                                                       String toCurrency) {
        return currencyConversionRepository.findByBusinessEntityKeyAndFromCurrencyAndToCurrency(
                businessEntityKey,
                fromCurrency,
                toCurrency
        );
    }

    /**
     * Find effective conversion rates on a specific date
     * 
     * @param businessEntityKey the business entity key
     * @param fromCurrency source currency
     * @param toCurrency target currency
     * @param effectiveDate the date to check
     * @return list of effective conversion rates
     */
    @Transactional(readOnly = true)
    public List<CurrencyConversion> findEffectiveConversionRates(Long businessEntityKey,
                                                                 String fromCurrency,
                                                                 String toCurrency,
                                                                 Date effectiveDate) {
        return currencyConversionRepository.findEffectiveConversionRates(
                businessEntityKey,
                fromCurrency,
                toCurrency,
                effectiveDate
        );
    }

    /**
     * Delete a currency conversion by id
     * 
     * @param currencyKey the conversion rate key to delete
     */
    @Transactional
    public void deleteCurrencyConversion(Long currencyKey) {
        currencyConversionRepository.deleteById(currencyKey);
    }

}


