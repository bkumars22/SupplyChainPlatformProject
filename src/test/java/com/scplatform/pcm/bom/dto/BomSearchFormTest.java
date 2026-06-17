/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.dto;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.scplatform.pcm.searchframework.dto.SearchForm;

class BomSearchFormTest {

    @Test
    void extendsSearchForm() {
        assertTrue(SearchForm.class.isAssignableFrom(BomSearchForm.class));
    }

    @Test
    void canBeInstantiated() {
        BomSearchForm form = new BomSearchForm();
        assertNotNull(form);
    }

    @Test
    void hasGetFlexAttributeBomDefinitionsMethod() throws NoSuchMethodException {
        // Method requires a live Spring context (FlexAttributeManager) to invoke,
        // so verify only its presence and signature here.
        assertNotNull(BomSearchForm.class.getMethod("getFlexAttributeBomDefinitions"));
    }
}
