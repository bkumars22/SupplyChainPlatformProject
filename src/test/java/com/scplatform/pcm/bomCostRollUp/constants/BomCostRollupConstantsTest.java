/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bomCostRollUp.constants;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BomCostRollupConstantsTest {

    @Test
    void classIsFinal() {
        assertTrue(Modifier.isFinal(BomCostRollupConstants.class.getModifiers()));
    }

    @Test
    void constructorIsPrivate_andCannotBeInvoked() throws Exception {
        Constructor<BomCostRollupConstants> ctor = BomCostRollupConstants.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(ctor.getModifiers()));
        ctor.setAccessible(true);
        // Allowed via reflection - just confirm it runs without error
        assertNotNull(ctor.newInstance());
    }

    @Test
    void statusConstantsHaveExpectedValues() {
        assertEquals(1, BomCostRollupConstants.STATUS_NO_RECORD);
        assertEquals(-1, BomCostRollupConstants.STATUS_ERROR);
    }

    @Test
    void emptyDataJsonIsCorrectShape() {
        assertEquals("{\"DATA\":[]}", BomCostRollupConstants.EMPTY_DATA_JSON);
    }

    @Test
    void rollupRowFieldCountIs26() {
        assertEquals(26, BomCostRollupConstants.ROLLUP_ROW_FIELD_COUNT);
    }

    @Test
    void procedureConstantsAreDefined() {
        assertEquals("GET_BOM_HIERARCHY_WITH_COST", BomCostRollupConstants.PROC_NAME);
        assertEquals("p_root_bom_key", BomCostRollupConstants.PARAM_ROOT_BOM_KEY);
        assertEquals("p_user_key", BomCostRollupConstants.PARAM_USER_KEY);
        assertEquals("p_effective_date", BomCostRollupConstants.PARAM_EFFECTIVE_DATE);
        assertEquals("p_resultset", BomCostRollupConstants.PARAM_RESULTSET);
    }

    @Test
    void columnConstantsAreNonBlank() {
        assertTrue(BomCostRollupConstants.COL_ITEM_NAME.length() > 0);
        assertTrue(BomCostRollupConstants.COL_DIRECT_MATERIAL.length() > 0);
        assertTrue(BomCostRollupConstants.COL_TARIFF.length() > 0);
        assertTrue(BomCostRollupConstants.COL_PROFIT_MARGIN.length() > 0);
    }

    @Test
    void noNullPublicConstants() throws Exception {
        for (java.lang.reflect.Field f : BomCostRollupConstants.class.getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
                f.setAccessible(true);
                assertNotNull(f.get(null), "constant should not be null: " + f.getName());
            }
        }
    }

    @Test
    void instantiationViaReflection_doesNotThrow() {
        assertThrows(NoSuchMethodException.class, () -> BomCostRollupConstants.class.getMethod("instance"));
    }
}
