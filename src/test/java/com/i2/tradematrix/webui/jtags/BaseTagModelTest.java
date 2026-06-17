/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * BaseTagModel is abstract - tested via anonymous subclass.
 */
class BaseTagModelTest {

    private BaseTagModel newInstance(String id) {
        return new BaseTagModel(id, false, true) {};
    }

    @Test
    void test_constructWithValidArgs() {
        BaseTagModel obj = newInstance("model1");
        assertNotNull(obj);
    }

    @Test
    void test_getId_returnsExpected() {
        BaseTagModel obj = newInstance("model2");
        assertEquals("model2", obj.getId());
    }

    @Test
    void test_isDisabled_returnsFalse() {
        BaseTagModel obj = newInstance("model3");
        assertFalse(obj.isDisabled());
    }

    @Test
    void test_isVisible_returnsTrue() {
        BaseTagModel obj = newInstance("model4");
        assertTrue(obj.isVisible());
    }
}
