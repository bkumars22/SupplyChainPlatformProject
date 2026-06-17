/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.util;

import com.scplatform.pcm.item.entity.ItemCategory;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

class CostRollupUtilTest {

    @Test
    void testNoCommodityIsItemCategoryWithMinLongKey() {
        ItemCategory ic = CostRollupUtil.NOCOMMODITY;
        assertNotNull(ic);
        assertEquals(Long.MIN_VALUE, ic.getCategoryKey());
    }

    @Test
    void testNoCommoditySingletonIdentity() {
        // Same reference returned across accesses.
        assertSame(CostRollupUtil.NOCOMMODITY, CostRollupUtil.NOCOMMODITY);
    }

    @Test
    void testClassIsFinalAndConstructorPrivate() throws Exception {
        assertTrue(Modifier.isFinal(CostRollupUtil.class.getModifiers()));
        Constructor<CostRollupUtil> ctor = CostRollupUtil.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);
        // Should be instantiable via reflection (no exception thrown).
        try {
            CostRollupUtil instance = ctor.newInstance();
            assertNotNull(instance);
        } catch (InvocationTargetException e) {
            fail("Private ctor threw: " + e.getCause());
        }
    }
}
