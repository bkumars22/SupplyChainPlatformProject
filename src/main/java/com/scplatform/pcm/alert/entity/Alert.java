/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "SC_ALERT_DETAIL")
public class Alert implements Serializable {

    public static final String ACTIVE = "ACTIVE";
    public static final String DISMISSED = "DISMISSED";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sc_alert_seq")
    @SequenceGenerator(name = "sc_alert_seq", sequenceName = "SC_ALERT_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "STATE")
    private String state;

    @Column(name = "DISMISED_BY")
    private String dismisedBy;

    @Column(name = "USER_LOGIN_ID")
    private String userId;

    @Column(name = "ALERT_LABEL")
    private String alertLabel;

    @Column(name = "ALERT_ID")
    private String alertId;

    @Column(name = "ALERT_TYPE")
    private String alertType;

    @Column(name = "CREATED")
    private Date created;

    @Column(name = "SHORT_SUMMARY")
    private String shortSummary;

    @Column(name = "LONG_SUMMARY")
    private String longSummary;

    @Column(name = "EXPIRATION_DATE")
    private Date expirationDate;

    @Column(name = "PUNCHOUT_URL")
    private String punchOutURL;

    @Column(name = "STRING_ATTRIBUTE1")
    private String stringAttribute1;

    @Column(name = "STRING_ATTRIBUTE2")
    private String stringAttribute2;

    @Column(name = "STRING_ATTRIBUTE3")
    private String stringAttribute3;

    @Column(name = "STRING_ATTRIBUTE4")
    private String stringAttribute4;

    @Column(name = "STRING_ATTRIBUTE5")
    private String stringAttribute5;

    @Column(name = "STRING_ATTRIBUTE6")
    private String stringAttribute6;

    @Column(name = "STRING_ATTRIBUTE7")
    private String stringAttribute7;

    @Column(name = "STRING_ATTRIBUTE8")
    private String stringAttribute8;

    @Column(name = "STRING_ATTRIBUTE9")
    private String stringAttribute9;

    @Column(name = "STRING_ATTRIBUTE10")
    private String stringAttribute10;

    @Column(name = "STRING_ATTRIBUTE11")
    private String stringAttribute11;

    @Column(name = "STRING_ATTRIBUTE12")
    private String stringAttribute12;

    @Column(name = "STRING_ATTRIBUTE13")
    private String stringAttribute13;

    @Column(name = "STRING_ATTRIBUTE14")
    private String stringAttribute14;

    @Column(name = "STRING_ATTRIBUTE15")
    private String stringAttribute15;

    @Column(name = "STRING_ATTRIBUTE16")
    private String stringAttribute16;

    @Column(name = "STRING_ATTRIBUTE17")
    private String stringAttribute17;

    @Column(name = "STRING_ATTRIBUTE18")
    private String stringAttribute18;

    @Column(name = "STRING_ATTRIBUTE19")
    private String stringAttribute19;

    @Column(name = "STRING_ATTRIBUTE20")
    private String stringAttribute20;

    @Column(name = "NUMERIC_ATTRIBUTE1")
    private Number numberAttribute1;

    @Column(name = "NUMERIC_ATTRIBUTE2")
    private Number numberAttribute2;

    @Column(name = "NUMERIC_ATTRIBUTE3")
    private Number numberAttribute3;

    @Column(name = "NUMERIC_ATTRIBUTE4")
    private Number numberAttribute4;

    @Column(name = "NUMERIC_ATTRIBUTE5")
    private Number numberAttribute5;

    @Column(name = "NUMERIC_ATTRIBUTE6")
    private Number numberAttribute6;

    @Column(name = "NUMERIC_ATTRIBUTE7")
    private Number numberAttribute7;

    @Column(name = "NUMERIC_ATTRIBUTE8")
    private Number numberAttribute8;

    @Column(name = "NUMERIC_ATTRIBUTE9")
    private Number numberAttribute9;

    @Column(name = "NUMERIC_ATTRIBUTE10")
    private Number numberAttribute10;

    @Column(name = "DATE_ATTRIBUTE1")
    private Date dateAttribute1;

    @Column(name = "DATE_ATTRIBUTE2")
    private Date dateAttribute2;

    @Column(name = "DATE_ATTRIBUTE3")
    private Date dateAttribute3;

    @Column(name = "DATE_ATTRIBUTE4")
    private Date dateAttribute4;

    @Column(name = "DATE_ATTRIBUTE5")
    private Date dateAttribute5;

    @Column(name = "DATE_ATTRIBUTE6")
    private Date dateAttribute6;

    @Column(name = "DATE_ATTRIBUTE7")
    private Date dateAttribute7;

    @Column(name = "DATE_ATTRIBUTE8")
    private Date dateAttribute8;

    @Column(name = "DATE_ATTRIBUTE9")
    private Date dateAttribute9;

    @Column(name = "DATE_ATTRIBUTE10")
    private Date dateAttribute10;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDismisedBy() {
        return dismisedBy;
    }

    public void setDismisedBy(String dismisedBy) {
        this.dismisedBy = dismisedBy;
    }

    public String getAlertLabel() {
        return alertLabel;
    }

    public void setAlertLabel(String alertLabel) {
        this.alertLabel = alertLabel;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getShortSummary() {
        return shortSummary;
    }

    public void setShortSummary(String shortSummary) {
        this.shortSummary = shortSummary;
    }

    public String getLongSummary() {
        return longSummary;
    }

    public void setLongSummary(String longSummary) {
        this.longSummary = longSummary;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPunchOutURL() {
        return punchOutURL;
    }

    public void setPunchOutURL(String punchOutURL) {
        this.punchOutURL = punchOutURL;
    }

    public String getStringAttribute1() {
        return stringAttribute1;
    }

    public void setStringAttribute1(String stringAttribute1) {
        this.stringAttribute1 = stringAttribute1;
    }

    public String getStringAttribute2() {
        return stringAttribute2;
    }

    public void setStringAttribute2(String stringAttribute2) {
        this.stringAttribute2 = stringAttribute2;
    }

    public String getStringAttribute3() {
        return stringAttribute3;
    }

    public void setStringAttribute3(String stringAttribute3) {
        this.stringAttribute3 = stringAttribute3;
    }

    public String getStringAttribute4() {
        return stringAttribute4;
    }

    public void setStringAttribute4(String stringAttribute4) {
        this.stringAttribute4 = stringAttribute4;
    }

    public String getStringAttribute5() {
        return stringAttribute5;
    }

    public void setStringAttribute5(String stringAttribute5) {
        this.stringAttribute5 = stringAttribute5;
    }

    public String getStringAttribute6() {
        return stringAttribute6;
    }

    public void setStringAttribute6(String stringAttribute6) {
        this.stringAttribute6 = stringAttribute6;
    }

    public String getStringAttribute7() {
        return stringAttribute7;
    }

    public void setStringAttribute7(String stringAttribute7) {
        this.stringAttribute7 = stringAttribute7;
    }

    public String getStringAttribute8() {
        return stringAttribute8;
    }

    public void setStringAttribute8(String stringAttribute8) {
        this.stringAttribute8 = stringAttribute8;
    }

    public String getStringAttribute9() {
        return stringAttribute9;
    }

    public void setStringAttribute9(String stringAttribute9) {
        this.stringAttribute9 = stringAttribute9;
    }

    public String getStringAttribute10() {
        return stringAttribute10;
    }

    public void setStringAttribute10(String stringAttribute10) {
        this.stringAttribute10 = stringAttribute10;
    }

    public String getStringAttribute11() {
        return stringAttribute11;
    }

    public void setStringAttribute11(String stringAttribute11) {
        this.stringAttribute11 = stringAttribute11;
    }

    public String getStringAttribute12() {
        return stringAttribute12;
    }

    public void setStringAttribute12(String stringAttribute12) {
        this.stringAttribute12 = stringAttribute12;
    }

    public String getStringAttribute13() {
        return stringAttribute13;
    }

    public void setStringAttribute13(String stringAttribute13) {
        this.stringAttribute13 = stringAttribute13;
    }

    public String getStringAttribute14() {
        return stringAttribute14;
    }

    public void setStringAttribute14(String stringAttribute14) {
        this.stringAttribute14 = stringAttribute14;
    }

    public String getStringAttribute15() {
        return stringAttribute15;
    }

    public void setStringAttribute15(String stringAttribute15) {
        this.stringAttribute15 = stringAttribute15;
    }

    public String getStringAttribute16() {
        return stringAttribute16;
    }

    public void setStringAttribute16(String stringAttribute16) {
        this.stringAttribute16 = stringAttribute16;
    }

    public String getStringAttribute17() {
        return stringAttribute17;
    }

    public void setStringAttribute17(String stringAttribute17) {
        this.stringAttribute17 = stringAttribute17;
    }

    public String getStringAttribute18() {
        return stringAttribute18;
    }

    public void setStringAttribute18(String stringAttribute18) {
        this.stringAttribute18 = stringAttribute18;
    }

    public String getStringAttribute19() {
        return stringAttribute19;
    }

    public void setStringAttribute19(String stringAttribute19) {
        this.stringAttribute19 = stringAttribute19;
    }

    public String getStringAttribute20() {
        return stringAttribute20;
    }

    public void setStringAttribute20(String stringAttribute20) {
        this.stringAttribute20 = stringAttribute20;
    }

    public Number getNumberAttribute1() {
        return numberAttribute1;
    }

    public void setNumberAttribute1(Number numberAttribute1) {
        this.numberAttribute1 = numberAttribute1;
    }

    public Number getNumberAttribute2() {
        return numberAttribute2;
    }

    public void setNumberAttribute2(Number numberAttribute2) {
        this.numberAttribute2 = numberAttribute2;
    }

    public Number getNumberAttribute3() {
        return numberAttribute3;
    }

    public void setNumberAttribute3(Number numberAttribute3) {
        this.numberAttribute3 = numberAttribute3;
    }

    public Number getNumberAttribute4() {
        return numberAttribute4;
    }

    public void setNumberAttribute4(Number numberAttribute4) {
        this.numberAttribute4 = numberAttribute4;
    }

    public Number getNumberAttribute5() {
        return numberAttribute5;
    }

    public void setNumberAttribute5(Number numberAttribute5) {
        this.numberAttribute5 = numberAttribute5;
    }

    public Number getNumberAttribute6() {
        return numberAttribute6;
    }

    public void setNumberAttribute6(Number numberAttribute6) {
        this.numberAttribute6 = numberAttribute6;
    }

    public Number getNumberAttribute7() {
        return numberAttribute7;
    }

    public void setNumberAttribute7(Number numberAttribute7) {
        this.numberAttribute7 = numberAttribute7;
    }

    public Number getNumberAttribute8() {
        return numberAttribute8;
    }

    public void setNumberAttribute8(Number numberAttribute8) {
        this.numberAttribute8 = numberAttribute8;
    }

    public Number getNumberAttribute9() {
        return numberAttribute9;
    }

    public void setNumberAttribute9(Number numberAttribute9) {
        this.numberAttribute9 = numberAttribute9;
    }

    public Number getNumberAttribute10() {
        return numberAttribute10;
    }

    public void setNumberAttribute10(Number numberAttribute10) {
        this.numberAttribute10 = numberAttribute10;
    }

    public Date getDateAttribute1() {
        return dateAttribute1;
    }

    public void setDateAttribute1(Date dateAttribute1) {
        this.dateAttribute1 = dateAttribute1;
    }

    public Date getDateAttribute2() {
        return dateAttribute2;
    }

    public void setDateAttribute2(Date dateAttribute2) {
        this.dateAttribute2 = dateAttribute2;
    }

    public Date getDateAttribute3() {
        return dateAttribute3;
    }

    public void setDateAttribute3(Date dateAttribute3) {
        this.dateAttribute3 = dateAttribute3;
    }

    public Date getDateAttribute4() {
        return dateAttribute4;
    }

    public void setDateAttribute4(Date dateAttribute4) {
        this.dateAttribute4 = dateAttribute4;
    }

    public Date getDateAttribute5() {
        return dateAttribute5;
    }

    public void setDateAttribute5(Date dateAttribute5) {
        this.dateAttribute5 = dateAttribute5;
    }

    public Date getDateAttribute6() {
        return dateAttribute6;
    }

    public void setDateAttribute6(Date dateAttribute6) {
        this.dateAttribute6 = dateAttribute6;
    }

    public Date getDateAttribute7() {
        return dateAttribute7;
    }

    public void setDateAttribute7(Date dateAttribute7) {
        this.dateAttribute7 = dateAttribute7;
    }

    public Date getDateAttribute8() {
        return dateAttribute8;
    }

    public void setDateAttribute8(Date dateAttribute8) {
        this.dateAttribute8 = dateAttribute8;
    }

    public Date getDateAttribute9() {
        return dateAttribute9;
    }

    public void setDateAttribute9(Date dateAttribute9) {
        this.dateAttribute9 = dateAttribute9;
    }

    public Date getDateAttribute10() {
        return dateAttribute10;
    }

    public void setDateAttribute10(Date dateAttribute10) {
        this.dateAttribute10 = dateAttribute10;
    }
}
