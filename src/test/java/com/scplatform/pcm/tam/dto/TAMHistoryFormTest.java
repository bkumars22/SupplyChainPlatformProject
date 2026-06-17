/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TAMHistoryFormTest {

    @Test
    void defaultConstructor_createsInstance() {
        TAMHistoryForm form = new TAMHistoryForm();
        assertNotNull(form);
    }

    @Test
    void equalsAndHashCode_twoDefaultInstances() {
        TAMHistoryForm a = new TAMHistoryForm();
        TAMHistoryForm b = new TAMHistoryForm();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
