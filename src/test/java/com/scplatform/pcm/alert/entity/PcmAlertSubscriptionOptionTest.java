/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PcmAlertSubscriptionOptionTest {

    @Test
    void wellKnownConstants_haveExpectedValues() {
        assertEquals("excludeOwnActions", PcmAlertSubscriptionOption.OPT_EXCLUDE_OWN_ACTIONS);
        assertEquals("responsibilityKey", PcmAlertSubscriptionOption.OPT_RESPONSIBILITY_KEY);
        assertEquals("costTypeKey", PcmAlertSubscriptionOption.OPT_COST_TYPE_KEY);
        assertEquals("itemKey", PcmAlertSubscriptionOption.OPT_ITEM_KEY);
    }

    @Test
    void noArgsConstructor_isInvokable() {
        PcmAlertSubscriptionOption o = new PcmAlertSubscriptionOption();
        assertNull(o.getOptionKey());
        assertNull(o.getSubscription());
        assertNull(o.getOptionId());
        assertNull(o.getOptionValue());
    }

    @Test
    void builder_setsAllFields() {
        PcmAlertSubscription parent = new PcmAlertSubscription();
        PcmAlertSubscriptionOption o = PcmAlertSubscriptionOption.builder()
                .optionKey(99L)
                .subscription(parent)
                .optionId("excludeOwnActions")
                .optionValue("Y")
                .build();
        assertEquals(99L, o.getOptionKey());
        assertSame(parent, o.getSubscription());
        assertEquals("excludeOwnActions", o.getOptionId());
        assertEquals("Y", o.getOptionValue());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        PcmAlertSubscription parent = new PcmAlertSubscription();
        PcmAlertSubscriptionOption o = new PcmAlertSubscriptionOption(1L, parent, "k", "v");
        assertEquals(1L, o.getOptionKey());
        assertSame(parent, o.getSubscription());
        assertEquals("k", o.getOptionId());
        assertEquals("v", o.getOptionValue());
    }

    @Test
    void setters_updateState() {
        PcmAlertSubscriptionOption o = new PcmAlertSubscriptionOption();
        PcmAlertSubscription parent = new PcmAlertSubscription();
        o.setOptionKey(5L);
        o.setSubscription(parent);
        o.setOptionId("id");
        o.setOptionValue("val");
        assertEquals(5L, o.getOptionKey());
        assertSame(parent, o.getSubscription());
        assertEquals("id", o.getOptionId());
        assertEquals("val", o.getOptionValue());
    }
}
