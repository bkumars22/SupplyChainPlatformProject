/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.rebate.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PcmRebateRuleTest {

    @Test
    void defaultConstructor() {
        PcmRebateRule rule = new PcmRebateRule();
        assertNull(rule.getRebateRuleKey());
        assertNull(rule.getRebateRuleId());
        assertNull(rule.getRebateProgram());
    }

    @Test
    void settersAndGetters() {
        PcmRebateRule rule = new PcmRebateRule();
        rule.setRebateRuleKey(10L);
        rule.setRebateRuleId("RULE-001");
        PcmRebateProgram program = new PcmRebateProgram();
        rule.setRebateProgram(program);

        assertEquals(10L, rule.getRebateRuleKey());
        assertEquals("RULE-001", rule.getRebateRuleId());
        assertSame(program, rule.getRebateProgram());
    }
}
