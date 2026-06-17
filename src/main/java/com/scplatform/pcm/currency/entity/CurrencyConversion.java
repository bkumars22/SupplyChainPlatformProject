/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.currency.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity for currency conversion rates
 * Stores conversion rates between different currencies
 */
@Entity
@Table(name = "CURRENCY")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("serial")
public class CurrencyConversion implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CURRENCY_KEY")
    private Long currencyKey;

    @Column(name = "BUSINESS_ENTITY_KEY", nullable = false)
    private Long businessEntityKey;

    @Column(name = "FROM_CURRENCY", length = 3, nullable = false)
    private String fromCurrency;

    @Column(name = "TO_CURRENCY", length = 3, nullable = false)
    private String toCurrency;

    @Column(name = "CONVERSION_RATE", precision = 19, scale = 6)
    private BigDecimal conversionRate;

    @Column(name = "START_DATE")
    
    private Date startDate;

    @Column(name = "END_DATE")
    
    private Date endDate;

    @Column(name = "INSERT_DT")
    
    private Date insertDate;

    @Column(name = "UPDATE_DT")
    
    private Date updateDate;

}

