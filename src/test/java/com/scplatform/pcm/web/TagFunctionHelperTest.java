/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.web;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class TagFunctionHelperTest {

    @Test
    void arrayContains_trueAndFalse() {
        assertTrue(TagFunctionHelper.arrayContains(new Object[]{"a", "b"}, "a"));
        assertFalse(TagFunctionHelper.arrayContains(new Object[]{"a", "b"}, "c"));
    }

    @Test
    void contains_collection() {
        assertTrue(TagFunctionHelper.contains(Arrays.asList("x", "y"), "y"));
        assertFalse(TagFunctionHelper.contains(Arrays.asList("x", "y"), "z"));
        assertFalse(TagFunctionHelper.contains(null, "z"));
    }

    @Test
    void addAll_targetNotNull() {
        List<String> target = new ArrayList<>(Arrays.asList("a"));
        Collection<?> result = TagFunctionHelper.addAll(target, Arrays.asList("b", "c"));
        assertSame(target, result);
        assertEquals(3, target.size());
    }

    @Test
    void addAll_targetNull_returnsSource() {
        List<String> source = Arrays.asList("a", "b");
        assertSame(source, TagFunctionHelper.addAll(null, source));
    }

    @Test
    void getMapValuesAndKeys() {
        Map<String, Integer> m = new HashMap<>();
        m.put("a", 1);
        m.put("b", 2);
        assertEquals(2, TagFunctionHelper.getMapValues(m).size());
        assertEquals(2, TagFunctionHelper.getMapKeys(m).size());
        assertNull(TagFunctionHelper.getMapValues(null));
        assertNull(TagFunctionHelper.getMapKeys(null));
    }

    @Test
    void stackOps() {
        Stack<String> s = new Stack<>();
        TagFunctionHelper.push(s, "a");
        TagFunctionHelper.push(s, "b");
        assertEquals("b", TagFunctionHelper.peek(s));
        assertEquals("b", TagFunctionHelper.pop(s));
        assertEquals("a", TagFunctionHelper.peek(s));
    }

    @Test
    void getItemAvl_alwaysEmpty() {
        Set<?> r = TagFunctionHelper.getItemAvl("anything");
        assertNotNull(r);
        assertTrue(r.isEmpty());
    }

    @Test
    void dateOnly_truncates() {
        Date d = new Date();
        Date trimmed = TagFunctionHelper.dateOnly(d);
        assertNotNull(trimmed);
        // Should be midnight of same date
        assertTrue(trimmed.getTime() <= d.getTime());
    }

    @Test
    void dateOnly_null() {
        assertNull(TagFunctionHelper.dateOnly(null));
    }

    @Test
    void getMessage_bundleHit() {
        java.util.ListResourceBundle bundle = new java.util.ListResourceBundle() {
            @Override protected Object[][] getContents() {
                return new Object[][]{{"k", "v"}};
            }
        };
        assertEquals("v", TagFunctionHelper.getMessage(bundle, "k"));
    }

    @Test
    void getMessage_bundleMiss_returnsTriQuestion() {
        java.util.ListResourceBundle bundle = new java.util.ListResourceBundle() {
            @Override protected Object[][] getContents() { return new Object[0][]; }
        };
        assertEquals("???missing???", TagFunctionHelper.getMessage(bundle, "missing"));
    }

    @Test
    void getBeanProperty_pojo() throws Exception {
        Bean b = new Bean();
        b.setName("hi");
        assertEquals("hi", TagFunctionHelper.getBeanProperty(b, "name"));
    }

    @Test
    void getMessage_localeForm_throwsWhenMcmMessagesNotInitialized() {
        // SCPlatformMessages.INSTANCE is null on the test classpath; the helper does not guard against it.
        assertThrows(NullPointerException.class,
                () -> TagFunctionHelper.getMessage("nonexistent.key", "x", Locale.ENGLISH));
    }

    @Test
    void getProductVersion_doesNotThrow() {
        // e2.deploy.dir is unset on test classpath; method should return null gracefully
        assertDoesNotThrow(TagFunctionHelper::getProductVersion);
    }

    public static class Bean {
        private String name;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
}
