/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;

import com.scplatform.pcm.common.dto.ChangeTracker;
import com.scplatform.pcm.common.enums.BigDecimalObjectType;
import com.scplatform.pcm.common.service.BigDecimalHelper;
import com.scplatform.pcm.forecast.dto.ForecastChange;
import com.scplatform.pcm.forecast.dto.ForecastPeriod;
import com.scplatform.pcm.forecast.dto.ForecastTimeline;
import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.enums.AdjustmentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdjustableForecastValueMutatorTest {

    @Mock private BigDecimalHelper bigDecimalHelper;
    @Mock private PcmForecastValueService pcmForecastValueService;
    @InjectMocks private AdjustableForecastValueMutator mutator;

    private ForecastChange changeRecord;
    private ForecastTimeline timeline;
    private PcmAdjustableForecastValue pfv;
    private ChangeTracker<PcmForecastValue> tracker;

    @BeforeEach
    void setUp() {
        pfv = new PcmAdjustableForecastValue();
        pfv.setEffectiveFromDt(new Date(1L));
        changeRecord = mock(ForecastChange.class);

        tracker = new ChangeTracker<>();
        tracker.setObservedObject(pfv);
        when(pcmForecastValueService.newChangeTracker(any(PcmForecastValue.class))).thenReturn(tracker);
        when(pcmForecastValueService.getTitle(any(PcmForecastValue.class), anyBoolean())).thenReturn("TITLE");
        when(bigDecimalHelper.normalize(any(BigDecimalObjectType.class), any())).thenReturn(BigDecimal.ZERO);

        timeline = mock(ForecastTimeline.class);
        ForecastPeriod period = mock(ForecastPeriod.class);
        when(timeline.getPeriod(any())).thenReturn(period);
    }

    @Test
    void apply_noopWhenTimelineIsNull() {
        mutator.apply(pfv, changeRecord, null, AdjustmentType.FIXED,
                new BigDecimal("5"), "5");
        assertNull(pfv.getAdjustmentAmount());
        verifyNoInteractions(changeRecord);
    }

    @Test
    void apply_noopWhenPeriodNotFound() {
        ForecastTimeline tl = mock(ForecastTimeline.class);
        when(tl.getPeriod(any())).thenReturn(null);
        mutator.apply(pfv, changeRecord, tl, AdjustmentType.FIXED,
                new BigDecimal("5"), "5");
        assertNull(pfv.getAdjustmentAmount());
    }

    @Test
    void apply_setsTypeAndAmountAndRecordsChanges() {
        BigDecimal amt = new BigDecimal("3.50");
        mutator.apply(pfv, changeRecord, timeline, AdjustmentType.FIXED, amt, "3.50");
        assertEquals(AdjustmentType.FIXED, pfv.getAdjustmentType());
        assertEquals(0, amt.compareTo(pfv.getAdjustmentAmount()));
    }

    @Test
    void apply_clearsTypeWhenTypeIsNull() {
        // Pre-existing value
        pfv.setAdjustmentType(AdjustmentType.PERCENT);
        mutator.apply(pfv, changeRecord, timeline, null, BigDecimal.ZERO, null);
        assertNull(pfv.getAdjustmentType());
    }

    @Test
    void apply_clearsTypeWhenAmountTextIsNull() {
        pfv.setAdjustmentType(AdjustmentType.PERCENT);
        mutator.apply(pfv, changeRecord, timeline, AdjustmentType.FIXED, BigDecimal.ZERO, null);
        assertNull(pfv.getAdjustmentType());
    }
}
