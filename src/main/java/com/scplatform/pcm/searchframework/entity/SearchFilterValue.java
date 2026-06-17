/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.searchframework.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

@Embeddable
@NoArgsConstructor
@Access(AccessType.PROPERTY)
public class SearchFilterValue {

    private String fieldName;
    private String fieldType;

    @Transient
    private Object fieldValue;

    public SearchFilterValue(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.setFieldValue(fieldValue);
    }

    @Column(name = "SEARCH_FILTER_FIELD_NAME", length = 50, nullable = false)
    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Column(name = "SEARCH_FILTER_FIELD_TYPE", length = 50, nullable = false)
    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    @Transient
    public Object getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
        if (fieldValue != null) {
            this.fieldType = fieldValue.getClass().getName();
        }
    }

    @Column(name = "SEARCH_FILTER_FIELD_VALUE", length = 4000, nullable = false)
    public String getFieldValueAsString() {
        if (fieldValue != null && fieldValue instanceof Date) {
            DateFormat df = DateFormat.getDateTimeInstance();
            return df.format(fieldValue);
        }
        return (fieldValue != null) ? fieldValue.toString() : null;
    }

    public void setFieldValueAsString(String stringValue) {
        if (fieldType != null && fieldType.equals(Date.class.getName())) {
            DateFormat df = DateFormat.getDateTimeInstance();
            try {
                fieldValue = df.parse(stringValue);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            fieldValue = stringValue;
            if (fieldType == null) {
                fieldType = String.class.getName();
            }
        }
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof SearchFilterValue))
            return false;
        SearchFilterValue castOther = (SearchFilterValue) other;
        EqualsBuilder eb = new EqualsBuilder();
        eb.append(this.getFieldName(), castOther.getFieldName());
        eb.append(this.getFieldType(), castOther.getFieldType());
        eb.append(this.getFieldValue(), castOther.getFieldValue());
        return eb.isEquals();
    }

    public int hashCode() {
        int result = new HashCodeBuilder(17, 37).
                append(this.getFieldName()).
                append(this.getFieldType()).
                append(this.getFieldValue()).
                toHashCode();
        return result;
    }
}