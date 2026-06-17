/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ChangeTrackerTest {

    @Test
    void defaultsUnchangedAndNoObservedObject() {
        ChangeTracker<String> t = new ChangeTracker<>();
        assertFalse(t.hasChanged());
        assertNull(t.getObservedObject());
    }

    @Test
    void markChangedAndResetWork() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.markChanged();
        assertTrue(t.hasChanged());
        t.resetChange();
        assertFalse(t.hasChanged());
    }

    @Test
    void setObservedObjectStoresReference() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.setObservedObject("hello");
        assertSame("hello", t.getObservedObject());
    }

    @Test
    void firePropertyChangeEventEqualValuesDoesNotMark() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.firePropertyChangeEvent("x", "a", "a");
        assertFalse(t.hasChanged());
    }

    @Test
    void firePropertyChangeEventDifferentValuesMarks() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.firePropertyChangeEvent("x", "a", "b");
        assertTrue(t.hasChanged());
    }

    @Test
    void firePropertyChangeEventOldNullNewNonNullMarks() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.firePropertyChangeEvent("x", null, "b");
        assertTrue(t.hasChanged());
    }

    @Test
    void firePropertyChangeEventOldNonNullNewNullMarks() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.firePropertyChangeEvent("x", "a", null);
        assertTrue(t.hasChanged());
    }

    @Test
    void firePropertyChangeEventBothNullDoesNotMark() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.firePropertyChangeEvent("x", null, null);
        assertFalse(t.hasChanged());
    }

    @Test
    void firePropertyChangeEventShortCircuitsWhenAlreadyChanged() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.markChanged();
        // Same values should still leave it changed (no-op short circuit)
        t.firePropertyChangeEvent("x", "a", "a");
        assertTrue(t.hasChanged());
    }

    @Test
    void firePropertyChangeEventUsingCompareEqualValuesDoesNotMark() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.firePropertyChangeEventUsingCompare("p", Integer.valueOf(1), Integer.valueOf(1));
        assertFalse(t.hasChanged());
    }

    @Test
    void firePropertyChangeEventUsingCompareDifferentValuesMarks() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.firePropertyChangeEventUsingCompare("p", Integer.valueOf(1), Integer.valueOf(2));
        assertTrue(t.hasChanged());
    }

    @Test
    void firePropertyChangeEventUsingCompareNullVsValueMarks() {
        ChangeTracker<String> t = new ChangeTracker<>();
        t.firePropertyChangeEventUsingCompare("p", null, Integer.valueOf(1));
        assertTrue(t.hasChanged());
    }
}
