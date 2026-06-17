/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SearchParameterDateTest {

    @Test
    void typeIsDate() {
        SearchParameterDate d = new SearchParameterDate("n", "l");
        assertEquals("DATE", d.getType());
        assertEquals("n", d.getName());
        assertEquals("l", d.getLabelKey());
    }
}
