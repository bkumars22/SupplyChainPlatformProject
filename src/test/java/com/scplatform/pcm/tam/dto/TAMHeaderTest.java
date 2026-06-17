/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TAMHeaderTest {

    @Test
    void constructor3Arg_setsFields() {
        Date start = new Date(1000L);
        Date end = new Date(2000L);

        TAMHeader header = new TAMHeader(start, end, Boolean.TRUE);

        assertEquals(start, header.getStartDate());
        assertEquals(end, header.getEndDate());
        assertTrue(header.getIsInherited());
        assertNull(header.getIsSupplierDataInherited());
        assertNull(header.getIsItemDataInherited());
    }

    @Test
    void constructor5Arg_setsAllFields() {
        Date start = new Date(3000L);
        Date end = new Date(4000L);

        TAMHeader header = new TAMHeader(start, end, Boolean.FALSE, Boolean.TRUE, Boolean.FALSE);

        assertEquals(start, header.getStartDate());
        assertEquals(end, header.getEndDate());
        assertFalse(header.getIsInherited());
        assertTrue(header.getIsSupplierDataInherited());
        assertFalse(header.getIsItemDataInherited());
    }

    @Test
    void settersAndGetters_workCorrectly() {
        TAMHeader header = new TAMHeader(new Date(), new Date(), false);
        Date newDate = new Date(9999L);
        header.setStartDate(newDate);
        header.setEndDate(newDate);
        header.setIsInherited(true);
        header.setIsSupplierDataInherited(true);
        header.setIsItemDataInherited(false);

        assertEquals(newDate, header.getStartDate());
        assertEquals(newDate, header.getEndDate());
        assertTrue(header.getIsInherited());
        assertTrue(header.getIsSupplierDataInherited());
        assertFalse(header.getIsItemDataInherited());
    }
}
