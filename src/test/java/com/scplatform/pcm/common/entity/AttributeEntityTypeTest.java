/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AttributeEntityTypeTest {

    @Test
    void hasExpectedConstants() {
        assertEquals(7, AttributeEntityType.values().length);
        AttributeEntityType.valueOf("ITEM");
        AttributeEntityType.valueOf("COST");
        AttributeEntityType.valueOf("BOMLINE");
        AttributeEntityType.valueOf("BOM");
        AttributeEntityType.valueOf("COSTFORECAST");
        AttributeEntityType.valueOf("BUSINESS_ENTITY");
        AttributeEntityType.valueOf("ITEM_AVL");
    }
}
