/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

class SimpleTagModelListTest {

    @Test
    void test_constructWithValidArgs() {
        SimpleTagModelList obj = new SimpleTagModelList("testId", false, true, new ArrayList<>());
        assertNotNull(obj);
    }

    @Test
    void test_constructWithNullList_defaultsToEmptyList() {
        SimpleTagModelList obj = new SimpleTagModelList("testId", false, true, null);
        assertNotNull(obj);
    }
}
