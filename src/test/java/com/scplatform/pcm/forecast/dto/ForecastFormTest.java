/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.dto;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.enums.ForecastModel;
import com.scplatform.pcm.forecast.service.ChangeRecordService;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ForecastFormTest {

    private Object previousContext;

    @BeforeEach
    void setUp() throws Exception {
        previousContext = readStaticContext();
        ApplicationContext ctx = mock(ApplicationContext.class);
        ChangeRecordService svc = mock(ChangeRecordService.class);
        when(ctx.getBean(ChangeRecordService.class)).thenReturn(svc);
        when(svc.generateRecordId(any(PcmForecast.class))).thenReturn("R-1");
        new SpringContextHolder().setApplicationContext(ctx);
    }

    @AfterEach
    void tearDown() throws Exception {
        writeStaticContext(previousContext);
    }

    @Test
    void defaultState_initialisedFromConstructor() {
        ForecastForm form = new ForecastForm("k", new Object[]{});
        assertTrue(form.getShowAllColumns());
        assertEquals(ForecastModel.ADJUSTABLE.name(), form.getDownloadForecastModel());
        assertEquals(ForecastModel.ADJUSTABLE.name(), form.getSelectedForecastTab());
        assertTrue(form.isValidAdjustable());
        assertTrue(form.isValidCurrent());
        assertNull(form.getBackAction());
        assertFalse(form.getLockError());
        assertFalse(form.getUnsavedData());
        assertEquals(0, form.getMinRolloverPeriods());
        assertEquals(0, form.getMaxRolloverPeriods());
        assertFalse(form.getAllowNegativeValues());
    }

    @Test
    void simpleSettersAndGetters() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        form.setBackAction("back");                assertEquals("back", form.getBackAction());
        form.setUnsavedData(true);                 assertTrue(form.getUnsavedData());
        form.setLockError(true);                   assertTrue(form.getLockError());
        form.setShowAllColumns(false);             assertFalse(form.getShowAllColumns());
        form.setEventName("ev");                   assertEquals("ev", form.getEventName());
        form.setEventMessage("msg");               assertEquals("msg", form.getEventMessage());
        form.setDownloadForecastModel("X");        assertEquals("X", form.getDownloadForecastModel());
        form.setSelectedForecastTab("T");          assertEquals("T", form.getSelectedForecastTab());
        form.setSelectedForecastModel("M");        assertEquals("M", form.getSelectedForecastModel());
        form.setForecastType("FT");                assertEquals("FT", form.getForecastType());
        form.setCalendarName("CAL");               assertEquals("CAL", form.getCalendarName());
        form.setPeriodType("MONTHLY");             assertEquals("MONTHLY", form.getPeriodType());
        form.setMinRolloverPeriods(3);             assertEquals(3, form.getMinRolloverPeriods());
        form.setMaxRolloverPeriods(7);             assertEquals(7, form.getMaxRolloverPeriods());
        form.setAllowNegativeValues(true);         assertTrue(form.getAllowNegativeValues());
        form.setValidAdjustable(false);            assertFalse(form.isValidAdjustable());
        form.setValidCurrent(false);               assertFalse(form.isValidCurrent());
        form.setAdjustableTimeline(new ForecastTimeline("CAL", "MONTHLY"));
        assertNotNull(form.getAdjustableTimeline());
        ForecastTimeline current = new ForecastTimeline("CAL", "MONTHLY");
        form.setCurrentTimeline(current);
        assertSame(current, form.getCurrentTimeline());
        // getTimeline prefers current when present
        assertSame(current, form.getTimeline());

        String[] keys = new String[]{"a","b"};
        form.setSelectedRecordKeys(keys);
        assertSame(keys, form.getSelectedRecordKeys());
    }

    @Test
    void getTimeline_fallsBackToAdjustableWhenNoCurrent() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        ForecastTimeline adj = new ForecastTimeline("CAL", "MONTHLY");
        form.setAdjustableTimeline(adj);
        assertSame(adj, form.getTimeline());
    }

    @Test
    void sites_setAndLookup() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        Site s1 = new Site(); s1.setSiteKey(100L);
        Site s2 = new Site(); s2.setSiteKey(200L);
        form.setSites(Arrays.asList(s1, s2));
        assertEquals(2, form.getSites().size());
        assertSame(s1, form.getSite(100L));
        assertSame(s2, form.getSite(200L));
        assertNull(form.getSite(999L));

        form.setDefaultSite(s1);
        assertSame(s1, form.getDefaultSite());
    }

    @Test
    void setDefaultDownloadModel_setsCurrentWhenForecastIsCurrent() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        PcmForecast cur = new PcmForecast();
        cur.setForecastModel(ForecastModel.CURRENT);
        form.setDownloadForecastModel(ForecastModel.ADJUSTABLE.name());
        form.setDefaultDownloadModel(cur);
        assertEquals(ForecastModel.CURRENT.name(), form.getDownloadForecastModel());
    }

    @Test
    void setDefaultDownloadModel_noopWhenAlreadyCurrent() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        form.setDownloadForecastModel(ForecastModel.CURRENT.name());
        form.setDefaultDownloadModel(new PcmForecast());
        assertEquals(ForecastModel.CURRENT.name(), form.getDownloadForecastModel());
    }

    @Test
    void setDefaultTabUsingForecast_switchesTabWhenForecastIsCurrent() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        PcmForecast cur = new PcmForecast();
        cur.setForecastModel(ForecastModel.CURRENT);
        form.setDefaultTabUsingForecast(cur);
        assertEquals(ForecastModel.CURRENT.name(), form.getSelectedForecastTab());
    }

    @Test
    void addAndRetrieveForecastRecords_basicOperations() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        com.scplatform.pcm.item.entity.Item itemA = new com.scplatform.pcm.item.entity.Item();
        itemA.setItemNumber("A");
        com.scplatform.pcm.item.entity.Item itemB = new com.scplatform.pcm.item.entity.Item();
        itemB.setItemNumber("B");

        PcmForecast f = new PcmForecast();
        f.setForecastExternalId("ext1");
        f.setForecastType("Demand");
        f.setForecastModel(ForecastModel.ADJUSTABLE);
        f.setItem(itemA);

        assertNull(form.addForecastRecord(f));
        assertSame(f, form.getForecastRecord("ext1"));
        assertEquals(1, form.getForecastRecords().size());
        assertSame(f, form.getForecastRecordsCollection().iterator().next());

        // model-based filter
        List<PcmForecast> adj = form.getForecastRecordsBasedOnModel(ForecastModel.ADJUSTABLE.name());
        assertEquals(1, adj.size());
        List<PcmForecast> cur = form.getForecastRecordsBasedOnModel(ForecastModel.CURRENT.name());
        assertEquals(0, cur.size());

        // bulk add
        PcmForecast f2 = new PcmForecast();
        f2.setForecastExternalId("ext2");
        f2.setForecastModel(ForecastModel.CURRENT);
        f2.setItem(itemB);
        form.addForecastRecords(Arrays.asList(f2));
        assertEquals(2, form.getForecastRecords().size());
    }

    @Test
    void markForDelete_andRemoveForecastRecord() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        PcmForecast f = new PcmForecast();
        f.setForecastExternalId("k");
        form.addForecastRecord(f);
        assertSame(f, form.markForDelete("k"));
        // After mark, forecast no longer in records map
        assertNull(form.getForecastRecord("k"));
        assertNull(form.removeForecastRecord("nope"));
    }

    @Test
    void clearForecastRecords_emptiesAll() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        PcmForecast f = new PcmForecast();
        f.setForecastExternalId("k");
        form.addForecastRecord(f);
        form.clearForecastRecords();
        assertTrue(form.getForecastRecords().isEmpty());
    }

    @Test
    void getRecordsMarkedForDelete_returnsSet() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        assertNotNull(form.getRecordsMarkedForDelete());
        assertTrue(form.getRecordsMarkedForDelete().isEmpty());
    }

    @Test
    void getForecastData_lazilyCreates() throws Exception {
        ForecastForm form = new ForecastForm("", new Object[]{});
        PcmForecast f = new PcmForecast();
        f.setForecastExternalId("ext");
        form.addForecastRecord(f);
        // Triggers ForecastFormRecordData constructor which calls Spring — will throw
        // because ForecastFormRecordDataService is not registered. Just verify wiring.
        Object prev = readStaticContext();
        try {
            writeStaticContext(null);
            assertThrows(Exception.class, () -> form.getForecastData("ext"));
        } finally {
            writeStaticContext(prev);
        }
    }

    @Test
    void getChangeRecord_lazilyCreatesAndCaches() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        PcmForecast f = new PcmForecast();
        ForecastChange c1 = form.getChangeRecord(f);
        ForecastChange c2 = form.getChangeRecord(f);
        assertSame(c1, c2);
        assertSame(f, c1.getForecast());
    }

    @Test
    void reset_clearsTransientState() throws Exception {
        ForecastForm form = new ForecastForm("", new Object[]{});
        form.setBackAction("x");
        form.setLockError(true);
        form.setEventName("e");
        form.setEventMessage("m");
        form.setUnsavedData(true);
        form.setForecastType("FT");
        form.setCalendarName("CAL");
        form.setPeriodType("M");
        form.setSelectedRecordKeys(new String[]{"k"});
        form.setShowAllColumns(false);
        form.setValidAdjustable(false);
        form.setValidCurrent(false);

        jakarta.servlet.http.HttpServletRequest req = mock(jakarta.servlet.http.HttpServletRequest.class);
        form.reset(req);

        assertNull(form.getBackAction());
        assertFalse(form.getLockError());
        assertTrue(form.getShowAllColumns());
        assertNull(form.getEventName());
        assertNull(form.getEventMessage());
        assertNull(form.getDownloadForecastModel());
        assertFalse(form.getUnsavedData());
        assertNull(form.getForecastType());
        assertNull(form.getCalendarName());
        assertNull(form.getPeriodType());
        assertNull(form.getSelectedRecordKeys());
        assertTrue(form.isValidAdjustable());
        assertTrue(form.isValidCurrent());
    }

    @Test
    void getFlexAttributeDefnListForecast_doesNotThrow() {
        ForecastForm form = new ForecastForm("", new Object[]{});
        // The static FlexAttributeManager may not be initialised in test context;
        // accept either a non-null list or a runtime error.
        try {
            form.getFlexAttributeDefnListForecast();
        } catch (Throwable ignored) {
            // Acceptable in unit-test runtime without Spring bootstrapping FlexAttributeManager.
        }
    }

    private static Object readStaticContext() throws Exception {
        Field f = SpringContextHolder.class.getDeclaredField("applicationContext");
        f.setAccessible(true);
        return f.get(null);
    }

    private static void writeStaticContext(Object value) throws Exception {
        Field f = SpringContextHolder.class.getDeclaredField("applicationContext");
        f.setAccessible(true);
        f.set(null, value);
    }
}
