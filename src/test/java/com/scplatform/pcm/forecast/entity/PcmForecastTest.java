/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.entity;

import com.scplatform.pcm.forecast.enums.ForecastModel;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class PcmForecastTest {

    @Test
    void defaultConstructor_initialisesDefaults() {
        PcmForecast f = new PcmForecast();
        assertNotNull(f.getForecastChangeTracker());
        assertSame(f, f.getForecastChangeTracker().getObservedObject());
        assertEquals(Boolean.FALSE, f.getDeleteFlag());
        assertTrue(f.getCurrentFlag());
        assertNotNull(f.getForecastValues());
        assertTrue(f.getForecastValues().isEmpty());
    }

    @Test
    void forecastTypeItemConstructor_setsFields() {
        Item item = new Item();
        PcmForecast f = new PcmForecast("Demand", item);
        assertEquals("Demand", f.getForecastType());
        assertSame(item, f.getItem());
        assertNotNull(f.getEffectiveFromDt());
    }

    @Test
    void copyConstructor_copiesFieldsAndGeneratesExternalId() {
        PcmForecast src = new PcmForecast();
        Item item = new Item();
        src.setItem(item);
        src.setForecastType("Demand");
        src.setDescription("desc");
        src.setCalendarName("Cal-X");
        src.setPeriodType("MONTHLY");
        src.setPeriodicAdjustmentType("FIXED");
        src.setPeriodicAdjustmentAmount(new BigDecimal("1.5"));
        src.setConfidenceFactor(new BigDecimal("0.8"));
        src.setRemainingRollovers(3);
        src.setEffectiveFromDt(new Date());
        src.setEffectiveToDt(new Date());
        src.setLastChangeBy("u1");

        PcmForecast copy = new PcmForecast(src, ForecastModel.CURRENT);
        assertNotNull(copy.getForecastExternalId());
        assertNotEquals(src.getForecastExternalId(), copy.getForecastExternalId());
        assertEquals("desc", copy.getDescription());
        assertEquals("Demand", copy.getForecastType());
        assertEquals("Cal-X", copy.getCalendarName());
        assertEquals("MONTHLY", copy.getPeriodType());
        assertEquals("FIXED", copy.getPeriodicAdjustmentType());
        assertEquals(0, new BigDecimal("1.5").compareTo(copy.getPeriodicAdjustmentAmount()));
        assertEquals(0, new BigDecimal("0.8").compareTo(copy.getConfidenceFactor()));
        assertEquals(Integer.valueOf(3), copy.getRemainingRollovers());
        assertEquals("u1", copy.getLastChangeBy());
        assertEquals(ForecastModel.CURRENT, copy.getForecastModel());
        assertSame(item, copy.getItem());
    }

    @Test
    void subtractRemainingRollovers_decrementsWhenBothNonNull() {
        PcmForecast f = new PcmForecast();
        f.setRemainingRollovers(5);
        f.subtractRemainingRollovers(2);
        assertEquals(Integer.valueOf(3), f.getRemainingRollovers());
    }

    @Test
    void subtractRemainingRollovers_noopOnNullInputs() {
        PcmForecast f = new PcmForecast();
        f.subtractRemainingRollovers(2);
        assertNull(f.getRemainingRollovers());

        f.setRemainingRollovers(4);
        f.subtractRemainingRollovers(null);
        assertEquals(Integer.valueOf(4), f.getRemainingRollovers());
    }

    @Test
    void simpleAccessors_setAndGet() {
        PcmForecast f = new PcmForecast();
        f.setForecastExternalId("ext");
        assertEquals("ext", f.getForecastExternalId());
        f.setDescription("d");
        assertEquals("d", f.getDescription());
        Site site = new Site();
        f.setSite(site);
        assertSame(site, f.getSite());
        f.setForecastType("t");
        assertEquals("t", f.getForecastType());
        f.setPeriodType("MONTHLY");
        assertEquals("MONTHLY", f.getPeriodType());
        f.setCalendarName("c");
        assertEquals("c", f.getCalendarName());
        f.setPeriodicAdjustmentType("FIXED");
        assertEquals("FIXED", f.getPeriodicAdjustmentType());
        f.setPeriodicAdjustmentAmount(new BigDecimal("1"));
        assertEquals(0, new BigDecimal("1").compareTo(f.getPeriodicAdjustmentAmount()));
        f.setConfidenceFactor(new BigDecimal("2"));
        assertEquals(0, new BigDecimal("2").compareTo(f.getConfidenceFactor()));
        f.setRemainingRollovers(4);
        assertEquals(Integer.valueOf(4), f.getRemainingRollovers());
        Date d = new Date();
        f.setInsertDate(d);
        assertSame(d, f.getInsertDate());
        f.setUpdateDate(d);
        assertSame(d, f.getUpdateDate());
        f.setLastChangeBy("kw");
        assertEquals("kw", f.getLastChangeBy());
        f.setEffectiveFromDt(d);
        assertSame(d, f.getEffectiveFromDt());
        f.setEffectiveToDt(d);
        assertSame(d, f.getEffectiveToDt());
        f.setDeleteFlag(Boolean.TRUE);
        assertEquals(Boolean.TRUE, f.getDeleteFlag());
        f.setCurrentFlag(false);
        assertFalse(f.getCurrentFlag());
        f.setLastRolloverStartDate(d);
        assertSame(d, f.getLastRolloverStartDate());
        f.setForecastOriginExternalId("orig");
        assertEquals("orig", f.getForecastOriginExternalId());
        f.setLastLoadedByUser("user");
        assertEquals("user", f.getLastLoadedByUser());
        f.setForecastModel(ForecastModel.CURRENT);
        assertEquals(ForecastModel.CURRENT, f.getForecastModel());
    }

    @Test
    void getChildrenAndParent_returnNull() {
        PcmForecast f = new PcmForecast();
        assertNull(f.getChildren());
        assertNull(f.getParent());
    }

    @Test
    void addForecastValue_appendsAndCanBeFound() {
        PcmForecast f = new PcmForecast();
        Date d = new Date(1_700_000_000_000L);
        PcmSimpleForecastValue v = new PcmSimpleForecastValue(
                d, new Date(d.getTime() + 86_400_000L), "Q", BigDecimal.ONE, "EA");
        assertTrue(f.addForecastValue(v));
        assertFalse(f.addForecastValue(null));
        assertSame(v, f.getForecastValue("Q", d));
        assertNull(f.getForecastValue(null, d));
        assertNull(f.getForecastValue("Q", null));
        assertNull(f.getForecastValue("OTHER", d));
    }

    @Test
    void getForecastMeasureKeys_andByPeriod() {
        PcmForecast f = new PcmForecast();
        Date d1 = new Date(1_700_000_000_000L);
        Date d2 = new Date(d1.getTime() + 30L * 86_400_000L);
        PcmSimpleForecastValue v1 = new PcmSimpleForecastValue(
                d1, new Date(d1.getTime() + 86_400_000L), "Q", BigDecimal.ONE, "EA");
        PcmSimpleForecastValue v2 = new PcmSimpleForecastValue(
                d2, new Date(d2.getTime() + 86_400_000L), "C", BigDecimal.TEN, "USD");
        f.addForecastValue(v1);
        f.addForecastValue(v2);

        assertTrue(f.getForecastMeasureKeys().contains("Q"));
        assertTrue(f.getForecastMeasureKeys().contains("C"));

        Map<Date, Map<String, PcmForecastValue>> byPeriod = f.getForecastValuesByPeriod();
        assertEquals(2, byPeriod.size());
        assertSame(v1, byPeriod.get(d1).get("Q"));
        assertSame(v2, byPeriod.get(d2).get("C"));
    }

    @Test
    void setForecastValues_replacesSet() {
        PcmForecast f = new PcmForecast();
        SortedSet<PcmForecastValue> set = new TreeSet<>();
        f.setForecastValues(set);
        assertSame(set, f.getForecastValues());
    }

    @Test
    void equalsAndHashCode_basedOnTypeStatusItemSiteModel() {
        PcmForecast a = new PcmForecast();
        a.setForecastType("Demand");
        a.setForecastModel(ForecastModel.CURRENT);
        PcmForecast b = new PcmForecast();
        b.setForecastType("Demand");
        b.setForecastModel(ForecastModel.CURRENT);

        assertEquals(a, a);
        assertNotEquals(a, null);
        assertNotEquals(a, "string");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());

        b.setForecastType("Other");
        assertNotEquals(a, b);
    }

    @Test
    void flexAttributes_setAndGet() {
        PcmForecast f = new PcmForecast();
        f.setStringAttribute1("s1");  assertEquals("s1", f.getStringAttribute1());
        f.setStringAttribute2("s2");  assertEquals("s2", f.getStringAttribute2());
        f.setStringAttribute3("s3");  assertEquals("s3", f.getStringAttribute3());
        f.setStringAttribute4("s4");  assertEquals("s4", f.getStringAttribute4());
        f.setStringAttribute5("s5");  assertEquals("s5", f.getStringAttribute5());
        f.setStringAttribute6("s6");  assertEquals("s6", f.getStringAttribute6());
        f.setStringAttribute7("s7");  assertEquals("s7", f.getStringAttribute7());
        f.setStringAttribute8("s8");  assertEquals("s8", f.getStringAttribute8());
        f.setStringAttribute9("s9");  assertEquals("s9", f.getStringAttribute9());
        f.setStringAttribute10("s10");assertEquals("s10", f.getStringAttribute10());

        f.setNumberAttribute1(1);   assertEquals(Integer.valueOf(1), f.getNumberAttribute1());
        f.setNumberAttribute2(2);   assertEquals(Integer.valueOf(2), f.getNumberAttribute2());
        f.setNumberAttribute3(3);   assertEquals(Integer.valueOf(3), f.getNumberAttribute3());
        f.setNumberAttribute4(4);   assertEquals(Integer.valueOf(4), f.getNumberAttribute4());
        f.setNumberAttribute5(5);   assertEquals(Integer.valueOf(5), f.getNumberAttribute5());
        f.setNumberAttribute6(6);   assertEquals(Integer.valueOf(6), f.getNumberAttribute6());
        f.setNumberAttribute7(7);   assertEquals(Integer.valueOf(7), f.getNumberAttribute7());
        f.setNumberAttribute8(8);   assertEquals(Integer.valueOf(8), f.getNumberAttribute8());
        f.setNumberAttribute9(9);   assertEquals(Integer.valueOf(9), f.getNumberAttribute9());
        f.setNumberAttribute10(10); assertEquals(Integer.valueOf(10), f.getNumberAttribute10());

        BigDecimal bd = new BigDecimal("1.23");
        f.setFloatAttribute1(bd);  assertSame(bd, f.getFloatAttribute1());
        f.setFloatAttribute2(bd);  assertSame(bd, f.getFloatAttribute2());
        f.setFloatAttribute3(bd);  assertSame(bd, f.getFloatAttribute3());
        f.setFloatAttribute4(bd);  assertSame(bd, f.getFloatAttribute4());
        f.setFloatAttribute5(bd);  assertSame(bd, f.getFloatAttribute5());
        f.setFloatAttribute6(bd);  assertSame(bd, f.getFloatAttribute6());
        f.setFloatAttribute7(bd);  assertSame(bd, f.getFloatAttribute7());
        f.setFloatAttribute8(bd);  assertSame(bd, f.getFloatAttribute8());
        f.setFloatAttribute9(bd);  assertSame(bd, f.getFloatAttribute9());
        f.setFloatAttribute10(bd); assertSame(bd, f.getFloatAttribute10());

        Date d = new Date();
        f.setDateAttribute1(d);  assertSame(d, f.getDateAttribute1());
        f.setDateAttribute2(d);  assertSame(d, f.getDateAttribute2());
        f.setDateAttribute3(d);  assertSame(d, f.getDateAttribute3());
        f.setDateAttribute4(d);  assertSame(d, f.getDateAttribute4());
        f.setDateAttribute5(d);  assertSame(d, f.getDateAttribute5());
        f.setDateAttribute6(d);  assertSame(d, f.getDateAttribute6());
        f.setDateAttribute7(d);  assertSame(d, f.getDateAttribute7());
        f.setDateAttribute8(d);  assertSame(d, f.getDateAttribute8());
        f.setDateAttribute9(d);  assertSame(d, f.getDateAttribute9());
        f.setDateAttribute10(d); assertSame(d, f.getDateAttribute10());
    }
}
