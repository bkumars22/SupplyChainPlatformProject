/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * BaseClickableTagModel is abstract - tested via anonymous subclass.
 */
class BaseClickableTagModelTest {

    private BaseClickableTagModel newInstance(String id) {
        return new BaseClickableTagModel(id, "onClick()", "label", "_self", "tip") {};
    }

    @Test
    void test_constructWithValidArgs() {
        BaseClickableTagModel obj = newInstance("btn1");
        assertNotNull(obj);
    }

    @Test
    void test_getId_returnsExpected() {
        BaseClickableTagModel obj = newInstance("btn2");
        assertEquals("btn2", obj.getId());
    }
}
