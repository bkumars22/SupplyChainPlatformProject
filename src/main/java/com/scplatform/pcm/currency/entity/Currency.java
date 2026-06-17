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


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

@SuppressWarnings("serial")
@Entity
@Table(name="CURRENCY_CODE")
public class Currency implements Serializable
{
    @Id
    @Column(name="CURRENCY_CODE")
    protected String currencyCode;

    @Column(name="CURRENCY_CODE_NAME")
    protected String currencyName;

    public Currency()
    {
        super();
    }

    public Currency(String currencyCode, String currencyName)
    {
        super();
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
    }
    public String getCurrencyCode()
    {
        return currencyCode;
    }
    public void setCurrencyCode(String currencyCode)
    {
        this.currencyCode = currencyCode;
    }
    public String getCurrencyName()
    {
        return currencyName;
    }
    public void setCurrencyName(String currencyName)
    {
        this.currencyName = currencyName;
    }

    public int hashCode()
    {
        return new HashCodeBuilder(17, 37).append(this.getCurrencyCode()).toHashCode();
    }

    public boolean equals(Object other)
    {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof Currency))
            return false;
        Currency castOther = (Currency) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getCurrencyCode(), castOther.getCurrencyCode());
        return eb.isEquals();
    }

}
