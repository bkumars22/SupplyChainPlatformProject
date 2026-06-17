/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PcmAlertSubscriptionTest {

    private static PcmAlertSubscriptionOption option(String id, String value) {
        PcmAlertSubscriptionOption o = new PcmAlertSubscriptionOption();
        o.setOptionId(id);
        o.setOptionValue(value);
        return o;
    }

    @Test
    void noArgsConstructor_initialisesEmptyOptions() {
        PcmAlertSubscription s = new PcmAlertSubscription();
        assertNotNull(s.getOptions());
        assertTrue(s.getOptions().isEmpty());
    }

    @Test
    void builder_setsAllFields() {
        PcmAlertSubscription s = PcmAlertSubscription.builder()
                .subscriptionKey(1L)
                .alertType("CostChange")
                .userKey(42L)
                .subscribeFlag(1)
                .build();
        assertEquals(1L, s.getSubscriptionKey());
        assertEquals("CostChange", s.getAlertType());
        assertEquals(42L, s.getUserKey());
        assertEquals(1, s.getSubscribeFlag());
        assertNotNull(s.getOptions());
    }

    @Test
    void allArgsConstructor_setsEveryField() {
        List<PcmAlertSubscriptionOption> opts = new ArrayList<>();
        PcmAlertSubscription s = new PcmAlertSubscription(7L, "ItemAssignment", 3L, 0, opts);
        assertEquals(7L, s.getSubscriptionKey());
        assertEquals("ItemAssignment", s.getAlertType());
        assertEquals(3L, s.getUserKey());
        assertEquals(0, s.getSubscribeFlag());
        assertSame(opts, s.getOptions());
    }

    @Test
    void getOptionValue_returnsValueWhenPresent() {
        PcmAlertSubscription s = new PcmAlertSubscription();
        s.getOptions().add(option("responsibilityKey", "111"));
        s.getOptions().add(option("costTypeKey", "BUY"));
        assertEquals("111", s.getOptionValue("responsibilityKey"));
        assertEquals("BUY", s.getOptionValue("costTypeKey"));
    }

    @Test
    void getOptionValue_returnsNullWhenMissing() {
        PcmAlertSubscription s = new PcmAlertSubscription();
        assertNull(s.getOptionValue("anything"));
    }

    @Test
    void isExcludeOwnActions_truthyValues_returnTrue() {
        for (String truthy : new String[]{"Y", "y", "true", "TRUE", "1"}) {
            PcmAlertSubscription s = new PcmAlertSubscription();
            s.getOptions().add(option("excludeOwnActions", truthy));
            assertTrue(s.isExcludeOwnActions(), "expected true for value=" + truthy);
        }
    }

    @Test
    void isExcludeOwnActions_falsyOrAbsent_returnFalse() {
        PcmAlertSubscription absent = new PcmAlertSubscription();
        assertFalse(absent.isExcludeOwnActions());

        PcmAlertSubscription falsy = new PcmAlertSubscription();
        falsy.getOptions().add(option("excludeOwnActions", "N"));
        assertFalse(falsy.isExcludeOwnActions());

        PcmAlertSubscription zero = new PcmAlertSubscription();
        zero.getOptions().add(option("excludeOwnActions", "0"));
        assertFalse(zero.isExcludeOwnActions());
    }

    @Test
    void isSubscribed_trueWhenFlagIsOne() {
        PcmAlertSubscription s = new PcmAlertSubscription();
        s.setSubscribeFlag(1);
        assertTrue(s.isSubscribed());
    }

    @Test
    void isSubscribed_falseWhenFlagIsNullOrNotOne() {
        PcmAlertSubscription nullFlag = new PcmAlertSubscription();
        assertNull(nullFlag.getSubscribeFlag());
        assertFalse(nullFlag.isSubscribed());

        PcmAlertSubscription zero = new PcmAlertSubscription();
        zero.setSubscribeFlag(0);
        assertFalse(zero.isSubscribed());

        PcmAlertSubscription two = new PcmAlertSubscription();
        two.setSubscribeFlag(2);
        assertFalse(two.isSubscribed());
    }
}
