/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.dashboard.dto;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DashboardFormTest {

    @Test
    void defaults() {
        DashboardForm f = new DashboardForm();
        assertNull(f.getLastLoadedDate());
        assertEquals(0L, f.getLastLoaded());
        assertEquals(20, f.getNewItemAge());
        assertFalse(f.getRefresh());
        assertEquals("", f.getCardType());
        assertNotNull(f.getRecordStatus());
        assertTrue(f.getRecordStatus().isEmpty());
        assertNotNull(f.getDashboardCards());
        assertNotNull(f.getInactiveDashboardCards());
    }

    @Test
    void lastLoadedDate_returnsDateWhenSet() {
        DashboardForm f = new DashboardForm();
        f.setLastLoaded(1_000L);
        assertNotNull(f.getLastLoadedDate());
        assertEquals(1_000L, f.getLastLoadedDate().getTime());
        assertEquals(1_000L, f.getLastLoaded());
    }

    @Test
    void recordStatus_setAndClear() {
        DashboardForm f = new DashboardForm();
        f.setRecordStatus("BOM", Collections.singletonList("OPEN"), true, 30);
        assertEquals(1, f.getRecordStatus().size());
        assertEquals(Boolean.TRUE, f.getRecordStatusOwnerOnly().get("BOM"));
        assertEquals(Integer.valueOf(30), f.getRecordStatusAge().get("BOM"));

        f.clearRecordStatus();
        assertTrue(f.getRecordStatus().isEmpty());
        assertTrue(f.getRecordStatusOwnerOnly().isEmpty());
        assertTrue(f.getRecordStatusAge().isEmpty());
    }

    @Test
    void simpleSettersAndGetters() {
        DashboardForm f = new DashboardForm();
        f.setNewItemAge(15);
        f.setRefresh(true);
        f.setDashboardLayout("two-col");
        f.setUserAlerts(Collections.singletonList("alert-1"));
        f.setNewItemStatus(Collections.singletonList("item-1"));
        f.setCardsPreferences("prefs");
        f.setCardType("cost");

        assertEquals(15, f.getNewItemAge());
        assertTrue(f.getRefresh());
        assertEquals("two-col", f.getDashboardLayout());
        assertEquals(1, f.getUserAlerts().size());
        assertEquals(1, f.getNewItemStatus().size());
        assertEquals("prefs", f.getCardsPreferences());
        assertEquals("cost", f.getCardType());
    }

    @Test
    void dashboardCards_setters() {
        DashboardForm f = new DashboardForm();
        Set<String> cards = Collections.singleton("kpi");
        f.setDashboardCards(cards);
        f.setInactiveDashboardCards(Collections.singleton("hidden"));
        f.setDashboardCard(new String[] {"a", "b"});
        assertEquals(cards, f.getDashboardCards());
        assertEquals(1, f.getInactiveDashboardCards().size());
        assertArrayEquals(new String[] {"a", "b"}, f.getDashboardCard());
    }

    @Test
    void prefArrayGetters() {
        DashboardForm f = new DashboardForm();
        f.setCostRecordStatPref(new String[]{"OPEN"});
        f.setSourcingLaneStatPref(new String[]{"NEW"});
        f.setAdjustableforecastStatPref(new String[]{"DRAFT"});
        f.setForecastStatPref(new String[]{"FORECAST"});
        f.setRebateStatPref(new String[]{"R"});
        f.setBomStatPref(new String[]{"B"});
        assertArrayEquals(new String[]{"OPEN"}, f.getCostRecordStatPref());
        assertArrayEquals(new String[]{"NEW"}, f.getSourcingLaneStatPref());
        assertArrayEquals(new String[]{"DRAFT"}, f.getAdjustableforecastStatPref());
        assertArrayEquals(new String[]{"FORECAST"}, f.getForecastStatPref());
        assertArrayEquals(new String[]{"R"}, f.getRebateStatPref());
        assertArrayEquals(new String[]{"B"}, f.getBomStatPref());
    }

    @Test
    void reviwAlert_setterReplacesMap() {
        DashboardForm f = new DashboardForm();
        Map<String, Integer> m = new HashMap<>();
        m.put("a", 1);
        f.setReviwAlert(m);
        assertEquals(Integer.valueOf(1), f.getReviwAlert().get("a"));
    }

    @Test
    void availableStates_setter() {
        DashboardForm f = new DashboardForm();
        f.setAvailableStates(Collections.emptyMap());
        assertNotNull(f.getAvailableStates());
        assertTrue(f.getAvailableStates().isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void userPreferences_setAndGet() {
        DashboardForm f = new DashboardForm();
        Map<String, String> prefs = new HashMap<>();
        prefs.put("layout", "grid");
        prefs.put("csv", "a,b,c");
        f.setUserPreferences(prefs);

        assertEquals("grid", f.getUserPreferenceValue("layout"));
        assertNull(f.getUserPreferenceValue("missing"));
        assertNull(f.getUserPreferenceValueAsArray("missing"));
        assertArrayEquals(new String[]{"a", "b", "c"}, f.getUserPreferenceValueAsArray("csv"));

        f.setUserPreferenceValue("k", "v");
        assertEquals("v", f.getUserPreferenceValue("k"));
        assertTrue(((Map<String, String>) f.getUserPreferences()).containsKey("k"));
    }
}
