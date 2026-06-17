/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.fiscalPeriod.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import com.scplatform.pcm.common.enums.Tense;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@SuppressWarnings("serial")
@Entity
@Table(name = "FISCAL_PERIOD", uniqueConstraints = @UniqueConstraint(columnNames = {
        "FISCAL_PERIOD_TYPE",
        "FISCAL_PERIOD_NAME",
        "FISCAL_PERIOD",
        "FISCAL_PERIOD_START_DATE",
        "FISCAL_PERIOD_END_DATE" }))
@IdClass(FiscalPeriod.FiscalPeriodId.class)
public class FiscalPeriod implements Serializable {

    public enum PeriodType {
        WEEK("W"),
        MONTH("M"),
        QUARTER("Q"),
        YEAR("Y");

        private final String type;

        PeriodType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    @Id
    @Column(name = "FISCAL_PERIOD_TYPE", nullable = false)
    protected String fiscalPeriodType;

    @Id
    @Column(name = "FISCAL_PERIOD_NAME", nullable = false)
    protected String fiscalPeriodName;

    @Id
    @Column(name = "FISCAL_PERIOD", nullable = false)
    protected int fiscalPeriod;

    @Id
    @Column(name = "FISCAL_PERIOD_START_DATE", nullable = false)
    protected Date fiscalPeriodStartDate;

    @Id
    @Column(name = "FISCAL_PERIOD_END_DATE", nullable = false)
    protected Date fiscalPeriodEndDate;

    public FiscalPeriod() {
        super();
    }

    public FiscalPeriod(String fiscalPeriodType, int fiscalPeriod, String fiscalPeriodName,
            Date fiscalPeriodStartDate, Date fiscalPeriodEndDate) {
        super();
        this.fiscalPeriod = fiscalPeriod;
        this.fiscalPeriodType = fiscalPeriodType;
        this.fiscalPeriodName = fiscalPeriodName;
        this.fiscalPeriodStartDate = fiscalPeriodStartDate;
        this.fiscalPeriodEndDate = fiscalPeriodEndDate;
    }

    public String getFiscalPeriodType() {
        return fiscalPeriodType;
    }

    public void setFiscalPeriodType(String fiscalPeriodType) {
        this.fiscalPeriodType = fiscalPeriodType;
    }

    public String getFiscalPeriodName() {
        return fiscalPeriodName;
    }

    public void setFiscalPeriodName(String fiscalPeriodName) {
        this.fiscalPeriodName = fiscalPeriodName;
    }

    public int getFiscalPeriod() {
        return fiscalPeriod;
    }

    public void setFiscalPeriod(int fiscalPeriod) {
        this.fiscalPeriod = fiscalPeriod;
    }

    public Date getFiscalPeriodStartDate() {
        return fiscalPeriodStartDate;
    }

    public void setFiscalPeriodStartDate(Date fiscalPeriodStartDate) {
        this.fiscalPeriodStartDate = fiscalPeriodStartDate;
    }

    public Date getFiscalPeriodEndDate() {
        return fiscalPeriodEndDate;
    }

    public void setFiscalPeriodEndDate(Date fiscalPeriodEndDate) {
        this.fiscalPeriodEndDate = fiscalPeriodEndDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fiscalPeriodName, this.fiscalPeriodType, this.fiscalPeriod,
                this.fiscalPeriodStartDate, this.fiscalPeriodEndDate);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !(other instanceof FiscalPeriod)) {
            return false;
        }
        FiscalPeriod castOther = (FiscalPeriod) other;
        return this.fiscalPeriod == castOther.fiscalPeriod
                && Objects.equals(this.fiscalPeriodType, castOther.fiscalPeriodType)
                && Objects.equals(this.fiscalPeriodName, castOther.fiscalPeriodName)
                && Objects.equals(this.fiscalPeriodStartDate, castOther.fiscalPeriodStartDate)
                && Objects.equals(this.fiscalPeriodEndDate, castOther.fiscalPeriodEndDate);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type:").append(this.getFiscalPeriodType());
        sb.append(" Start:").append(this.getFiscalPeriodStartDate());
        sb.append(" End:").append(this.getFiscalPeriodEndDate());
        return sb.toString();
    }

    /**
     * Determines if the period represents a period in the PAST PRESENT (current)
     * or FUTURE. This function assumes start date is always before end date.
     */
    public Tense getPeriodTense() {
        Date start = getFiscalPeriodStartDate();
        Date end = getFiscalPeriodEndDate();
        return Tense.getTenseForPeriod(start, end);
    }

    public static class FiscalPeriodId implements Serializable {
        private static final long serialVersionUID = 1L;

        protected String fiscalPeriodType;
        protected String fiscalPeriodName;
        protected int fiscalPeriod;
        protected Date fiscalPeriodStartDate;
        protected Date fiscalPeriodEndDate;

        public FiscalPeriodId() {
        }

        public FiscalPeriodId(String fiscalPeriodType, String fiscalPeriodName, int fiscalPeriod,
                Date fiscalPeriodStartDate, Date fiscalPeriodEndDate) {
            this.fiscalPeriodType = fiscalPeriodType;
            this.fiscalPeriodName = fiscalPeriodName;
            this.fiscalPeriod = fiscalPeriod;
            this.fiscalPeriodStartDate = fiscalPeriodStartDate;
            this.fiscalPeriodEndDate = fiscalPeriodEndDate;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fiscalPeriodType, fiscalPeriodName, fiscalPeriod,
                    fiscalPeriodStartDate, fiscalPeriodEndDate);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || !(other instanceof FiscalPeriodId)) {
                return false;
            }
            FiscalPeriodId castOther = (FiscalPeriodId) other;
            return this.fiscalPeriod == castOther.fiscalPeriod
                    && Objects.equals(this.fiscalPeriodType, castOther.fiscalPeriodType)
                    && Objects.equals(this.fiscalPeriodName, castOther.fiscalPeriodName)
                    && Objects.equals(this.fiscalPeriodStartDate, castOther.fiscalPeriodStartDate)
                    && Objects.equals(this.fiscalPeriodEndDate, castOther.fiscalPeriodEndDate);
        }
    }
}
