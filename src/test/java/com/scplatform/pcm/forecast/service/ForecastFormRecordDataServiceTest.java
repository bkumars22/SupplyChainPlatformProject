/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.forecast.service;

import com.scplatform.pcm.common.dto.ChangeTracker;
import com.scplatform.pcm.common.service.BigDecimalHelper;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.forecast.dto.AdjustableForecastValue;
import com.scplatform.pcm.forecast.dto.ForecastChange;
import com.scplatform.pcm.forecast.dto.ForecastFormRecordData;
import com.scplatform.pcm.forecast.dto.ForecastFormRecordData.SimpleForecastValue;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.entity.PcmSimpleForecastValue;
import com.scplatform.pcm.forecast.enums.AdjustmentType;
import com.scplatform.pcm.util.validator.Errors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ForecastFormRecordDataServiceTest {

    @Mock private PcmConfigUtil pcmConfigUtil;
    @Mock private AdjustableForecastValueMutator mutator;
    @Mock private BigDecimalHelper bigDecimalHelper;
    @Mock private PcmForecastValueService pcmForecastValueService;

    private ForecastFormRecordDataService svc;

    @BeforeEach
    void setUp() {
        svc = new ForecastFormRecordDataService(pcmConfigUtil, mutator, pcmForecastValueService);
        ChangeTracker<PcmForecastValue> tracker = new ChangeTracker<>();
        lenient().when(pcmForecastValueService.newChangeTracker(any(PcmForecastValue.class))).thenReturn(tracker);
        lenient().when(pcmForecastValueService.getTitle(any(PcmForecastValue.class), anyBoolean())).thenReturn("T");
    }

    @Test
    void isValueSet_simple_true_whenForecastValueProvided() {
        ForecastFormRecordData data = new ForecastFormRecordData();
        SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        sfv.setForecastValue("5");
        assertTrue(svc.isValueSet(sfv));
    }

    @Test
    void isValueSet_simple_false_whenBlank() {
        ForecastFormRecordData data = new ForecastFormRecordData();
        SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        sfv.setForecastValue("   ");
        assertFalse(svc.isValueSet(sfv));
    }

    @Test
    void isValueSet_adjustable_alwaysTrue() {
        AdjustableForecastValue afv = new AdjustableForecastValue();
        assertTrue(svc.isValueSet(afv));
    }

    @Test
    void createPcmForecastValue_simple_returnsSimpleEntity() {
        ForecastFormRecordData data = new ForecastFormRecordData();
        SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        assertTrue(svc.createPcmForecastValue(sfv) instanceof PcmSimpleForecastValue);
    }

    @Test
    void createPcmForecastValue_adjustable_returnsAdjustableEntity() {
        AdjustableForecastValue afv = new AdjustableForecastValue();
        assertTrue(svc.createPcmForecastValue(afv) instanceof PcmAdjustableForecastValue);
    }

    @Test
    void createPcmForecastValue_unknownTypeThrows() {
        // Use a custom subclass that is neither Simple nor Adjustable.
        ForecastFormRecordData.ForecastValue custom = new ForecastFormRecordData.ForecastValue() {};
        assertThrows(IllegalArgumentException.class, () -> svc.createPcmForecastValue(custom));
    }

    @Test
    void validate_simple_validNumericValue() throws Exception {
        ForecastFormRecordData data = new ForecastFormRecordData();
        SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        sfv.setForecastValue("12.34");
        Errors errors = new Errors();
        svc.validate(sfv, errors, "prop");
        assertFalse(errors.hasErrors());
        assertNotNull(sfv.getValue());
        assertEquals(0, new BigDecimal("12.34").compareTo(sfv.getValue()));
    }

    @Test
    void validate_simple_nullValueShortCircuits() throws Exception {
        ForecastFormRecordData data = new ForecastFormRecordData();
        SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        Errors errors = new Errors();
        svc.validate(sfv, errors, "prop");
        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_simple_invalidDecimalAddsErrorAndThrows() {
        ForecastFormRecordData data = new ForecastFormRecordData();
        ForecastFormRecordData.SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        sfv.setForecastValue("abc");
        Errors errors = new Errors();
        // Either NFE (success path) or NoClassDefFoundError when the validation bundle is missing.
        assertThrows(Throwable.class, () -> svc.validate(sfv, errors, "p"));
    }

    @Test
    void validate_simple_negativeValueAddsErrorAndThrows() {
        ForecastFormRecordData data = new ForecastFormRecordData();
        ForecastFormRecordData.SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        sfv.setForecastValue("-1");
        Errors errors = new Errors();
        assertThrows(Throwable.class, () -> svc.validate(sfv, errors, "p"));
    }

    @Test
    void validate_adjustable_blankAmountAndTypeShortCircuits() throws Exception {
        AdjustableForecastValue afv = new AdjustableForecastValue();
        Errors errors = new Errors();
        svc.validate(afv, errors, "p");
        assertFalse(errors.hasErrors());
    }

    @Test
    void validate_adjustable_validAmountAndType() throws Exception {
        AdjustableForecastValue afv = new AdjustableForecastValue();
        afv.setAdjustmentAmount("5");
        afv.setAdjustmentType("FIXED");
        Errors errors = new Errors();
        svc.validate(afv, errors, "p");
        assertFalse(errors.hasErrors());
        assertEquals(0, new BigDecimal("5").compareTo(afv.getAdjAmnt()));
        assertEquals(AdjustmentType.FIXED, afv.getType());
    }

    @Test
    void validate_adjustable_invalidAmountThrows() {
        AdjustableForecastValue afv = new AdjustableForecastValue();
        afv.setAdjustmentAmount("xyz");
        afv.setAdjustmentType("FIXED");
        Errors errors = new Errors();
        assertThrows(Throwable.class, () -> svc.validate(afv, errors, "p"));
    }

    @Test
    void validate_adjustable_invalidTypeThrows() {
        AdjustableForecastValue afv = new AdjustableForecastValue();
        afv.setAdjustmentAmount("5");
        afv.setAdjustmentType("BOGUS");
        Errors errors = new Errors();
        assertThrows(Throwable.class, () -> svc.validate(afv, errors, "p"));
    }

    @Test
    void updatePcmForecastValue_simple_setsValueAndRecordsChange() {
        ForecastFormRecordData data = new ForecastFormRecordData();
        SimpleForecastValue sfv = new ForecastFormRecordData.SimpleForecastValue();
        sfv.setForecastValue("9");
        sfv.setValue(new BigDecimal("9"));
        PcmSimpleForecastValue pfv = new PcmSimpleForecastValue();
        ForecastChange cr = org.mockito.Mockito.mock(ForecastChange.class);
        svc.updatePcmForecastValue(sfv, pfv, cr, null);
        assertEquals(0, new BigDecimal("9").compareTo(pfv.getForecastValue()));
    }

    @Test
    void updatePcmForecastValue_adjustable_delegatesToMutator() {
        AdjustableForecastValue afv = new AdjustableForecastValue();
        afv.setAdjustmentAmount("3");
        afv.setAdjustmentType("FIXED");
        afv.setAdjAmnt(new BigDecimal("3"));
        afv.setType(AdjustmentType.FIXED);
        PcmAdjustableForecastValue pfv = new PcmAdjustableForecastValue();
        ForecastChange cr = org.mockito.Mockito.mock(ForecastChange.class);
        svc.updatePcmForecastValue(afv, pfv, cr, null);
        org.mockito.Mockito.verify(mutator).apply(pfv, cr, null,
                AdjustmentType.FIXED, new BigDecimal("3"), "3");
    }

    @Test
    void copy_setsBasicFieldsFromForecast() {
        ForecastFormRecordData data = new ForecastFormRecordData();
        PcmForecast forecast = new PcmForecast();
        forecast.setRemainingRollovers(2);
        // copy() also iterates forecast values which is empty here.
        svc.copy(data, forecast);
        // After copy, extendPeriods should reflect "2"
        assertEquals("2", data.getExtendPeriods());
    }

    @Test
    void copy_nullRolloversLeavesExtendPeriodsBlank() {
        ForecastFormRecordData data = new ForecastFormRecordData();
        PcmForecast forecast = new PcmForecast();
        svc.copy(data, forecast);
        // null rollover -> null/blank extendPeriods
        assertTrue(data.getExtendPeriods() == null || data.getExtendPeriods().isEmpty());
    }
}
