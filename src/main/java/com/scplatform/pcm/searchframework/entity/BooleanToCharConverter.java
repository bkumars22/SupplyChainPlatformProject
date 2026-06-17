/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.searchframework.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converts Java boolean to/from database CHAR(1) 'T'/'F' representation.
 * 
 * Maps:
 * - true → 'T'
 * - false → 'F'
 */
@Converter
public class BooleanToCharConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) {
            return "F";
        }
        return attribute ? "T" : "F";
    }

    @Override
    public Boolean convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return false;
        }
        return "T".equals(dbData.toUpperCase());
    }
}

