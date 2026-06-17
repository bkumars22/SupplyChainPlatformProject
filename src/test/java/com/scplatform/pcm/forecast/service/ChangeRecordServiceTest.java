/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;

import com.scplatform.pcm.forecast.dto.ChangeRecord;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.site.entity.Site;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChangeRecordServiceTest {

    private ChangeRecordService svc;
    private ChangeRecord rec;

    @BeforeEach
    void setUp() {
        svc = new ChangeRecordService();
        rec = new ChangeRecord();
    }

    @Test
    void generateRecordId_returnsTitleString() {
        PcmForecast f = mock(PcmForecast.class);
        when(f.getTitle()).thenReturn("MY-TITLE");
        assertEquals("MY-TITLE", svc.generateRecordId(f));
    }

    @Test
    void recordSimpleMessage_storesMessage() {
        svc.record(rec, "FIELD", "hello");
        assertEquals("hello", rec.getChanges().get("FIELD"));
    }

    @Test
    void recordObject_changeFromValueToValue() {
        svc.record(rec, "F", "old", "new");
        assertEquals("From:old To:new", rec.getChanges().get("F"));
    }

    @Test
    void recordObject_changeFromValueToValue_noEntryIfEqual() {
        svc.record(rec, "F", "same", "same");
        assertNull(rec.getChanges().get("F"));
    }

    @Test
    void recordObject_clearedWhenNewIsNull() {
        svc.record(rec, "F", (Object) "old", (Object) null);
        assertEquals("Cleared:old", rec.getChanges().get("F"));
    }

    @Test
    void recordObject_assignedWhenOldIsNull() {
        svc.record(rec, "F", (Object) null, (Object) "new");
        assertEquals("Assign:new", rec.getChanges().get("F"));
    }

    @Test
    void recordObject_bigDecimalComparedNumerically() {
        svc.record(rec, "F", new BigDecimal("1.00"), new BigDecimal("1.0"));
        assertNull(rec.getChanges().get("F"));
        svc.record(rec, "F", new BigDecimal("1"), new BigDecimal("2"));
        assertEquals("From:1 To:2", rec.getChanges().get("F"));
    }

    @Test
    void recordObject_createEntryWhenBothNullAndCreateInField() {
        svc.record(rec, "CREATE_X", (Object) null, (Object) null);
        assertEquals("", rec.getChanges().get("CREATE_X"));
    }

    @Test
    void recordSite_clearedAssignedAndChanged() {
        Site a = new Site(); a.setSiteName("S1");
        Site b = new Site(); b.setSiteName("S2");
        svc.record(rec, "SITE", a, b);
        assertTrue(rec.getChanges().get("SITE").startsWith("From:"));

        rec.getChanges().clear();
        svc.record(rec, "SITE", a, (Site) null);
        assertTrue(rec.getChanges().get("SITE").startsWith("Cleared:"));

        rec.getChanges().clear();
        svc.record(rec, "SITE", (Site) null, b);
        assertTrue(rec.getChanges().get("SITE").startsWith("Assign:"));
    }

    @Test
    void recordSite_noEntryWhenSameName() {
        Site a = new Site(); a.setSiteName("S1");
        Site b = new Site(); b.setSiteName("S1");
        svc.record(rec, "SITE", a, b);
        assertNull(rec.getChanges().get("SITE"));
    }

    @Test
    void recordBigDecimal_allBranches() {
        svc.record(rec, "BD", new BigDecimal("1"), new BigDecimal("2"));
        assertEquals("From:1 To:2", rec.getChanges().get("BD"));

        rec.getChanges().clear();
        svc.record(rec, "BD", new BigDecimal("1"), new BigDecimal("1.00"));
        assertNull(rec.getChanges().get("BD"));

        rec.getChanges().clear();
        svc.record(rec, "BD", new BigDecimal("1"), (BigDecimal) null);
        assertEquals("Cleared:1", rec.getChanges().get("BD"));

        rec.getChanges().clear();
        svc.record(rec, "BD", (BigDecimal) null, new BigDecimal("3"));
        assertEquals("Assign:3", rec.getChanges().get("BD"));
    }

    @Test
    void recordDate_allBranches() {
        Date d1 = new Date(1L), d2 = new Date(2L);
        svc.record(rec, "D", d1, d2);
        assertTrue(rec.getChanges().get("D").startsWith("From:"));

        rec.getChanges().clear();
        svc.record(rec, "D", d1, d1);
        assertNull(rec.getChanges().get("D"));

        rec.getChanges().clear();
        svc.record(rec, "D", d1, (Date) null);
        assertTrue(rec.getChanges().get("D").startsWith("Cleared:"));

        rec.getChanges().clear();
        svc.record(rec, "D", (Date) null, d2);
        assertTrue(rec.getChanges().get("D").startsWith("Assign:"));
    }

    @Test
    void recordObjectWithTransaction_changeAndCleared() {
        svc.record(rec, "F", "old", "new", "T1");
        assertEquals("From:old To:new ;Transaction:T1", rec.getChanges().get("F"));

        rec.getChanges().clear();
        svc.record(rec, "F", "old", "", "T2");
        assertEquals("Cleared:old ;Transaction:T2", rec.getChanges().get("F"));

        rec.getChanges().clear();
        svc.record(rec, "F", (Object) null, (Object) "new", "T3");
        assertEquals("Assign:new ;Transaction:T3", rec.getChanges().get("F"));

        rec.getChanges().clear();
        svc.record(rec, "CREATE_X", (Object) null, (Object) null, "T4");
        assertEquals(" ;Transaction:T4", rec.getChanges().get("CREATE_X"));
    }

    @Test
    void recordObjectWithTransaction_bigDecimalNumericCompare() {
        svc.record(rec, "F", new BigDecimal("1.00"), new BigDecimal("1.0"), "TX");
        assertNull(rec.getChanges().get("F"));
        svc.record(rec, "F", new BigDecimal("1"), new BigDecimal("2"), "TX");
        assertEquals("From:1 To:2 ;Transaction:TX", rec.getChanges().get("F"));
    }
}
