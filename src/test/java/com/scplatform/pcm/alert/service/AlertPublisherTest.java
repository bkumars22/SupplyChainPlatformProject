/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.dto.AlertReceiver;
import com.scplatform.pcm.alert.entity.AlertDetail;
import com.scplatform.pcm.alert.enums.AlertDetailState;
import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.ObjectTypes;
import com.scplatform.pcm.alert.repository.AlertDetailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertPublisherTest {

    @Mock
    private AlertDetailRepository alertDetailRepository;

    @Mock
    private AlertSubscriptionService alertSubscriptionService;

    @Mock
    private AlertTemplateService alertTemplateService;

    @InjectMocks
    private AlertPublisher alertPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(alertPublisher, "alertExpiryDays", 30);
    }

    private AlertReceiver receiver(String loginId) {
        return AlertReceiver.builder().userId(1L).userLoginId(loginId).userName(loginId).email(loginId + "@x").build();
    }

    private AlertEvent baseEvent(AlertTypes type) {
        AlertEvent e = AlertEvent.builder()
                .alertType(type)
                .objectKey(100L)
                .objectType(ObjectTypes.COST_RECORD)
                .actor(7L)
                .build();
        e.setReceivers(new ArrayList<>(List.of(receiver("user1"))));
        e.setMetadata(new HashMap<>());
        return e;
    }

    @Test
    void publish_nullEvent_returnsFalse() {
        assertFalse(alertPublisher.publish(null));
        verifyNoInteractions(alertDetailRepository);
    }

    @Test
    void publish_clearAlertFlag_returnsTrueWithoutSaving() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        e.setClearAlertFlag(true);
        assertTrue(alertPublisher.publish(e));
        verify(alertDetailRepository, never()).saveAll(any());
    }

    @Test
    void publish_noReceivers_returnsTrueAndSkipsSave() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        e.setReceivers(new ArrayList<>());
        when(alertSubscriptionService.findSubscribers(e)).thenReturn(new ArrayList<>());
        assertTrue(alertPublisher.publish(e));
        verify(alertDetailRepository, never()).saveAll(any());
    }

    @Test
    void publish_usesSubscriptionServiceWhenNoExplicitReceivers() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        e.setReceivers(new ArrayList<>());
        when(alertSubscriptionService.findSubscribers(e)).thenReturn(List.of(receiver("subUser")));
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("ss");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("ls");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid-1");

        assertTrue(alertPublisher.publish(e));
        verify(alertSubscriptionService).findSubscribers(e);

        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals(1, cap.getValue().size());
        assertEquals("subUser", cap.getValue().get(0).getUserLoginId());
    }

    @Test
    void publish_buildsAlertDetailWithCoreFields() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("short");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("long");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("alertId-1");

        assertTrue(alertPublisher.publish(e));

        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        AlertDetail d = cap.getValue().get(0);
        assertEquals(AlertDetailState.ACTIVE, d.getState());
        assertEquals("user1", d.getUserLoginId());
        assertEquals("CostChange", d.getAlertType());
        assertEquals("alertId-1", d.getAlertId());
        assertEquals(AlertTypes.CostChange.getDescription(), d.getAlertLabel());
        assertEquals("short", d.getShortSummary());
        assertEquals("long", d.getLongSummary());
        assertEquals(LocalDate.now(), d.getCreated());
        assertEquals(LocalDate.now().plusDays(30), d.getExpirationDate());
        assertEquals("/pinCostRecord.do?objectKey=", d.getPunchoutUrl());
        assertNull(d.getDismissedBy());
    }

    @Test
    void publish_truncatesLongTemplateValues() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("a".repeat(300));
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("b".repeat(600));
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("c".repeat(600));

        alertPublisher.publish(e);

        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        AlertDetail d = cap.getValue().get(0);
        assertEquals(255, d.getShortSummary().length());
        assertEquals(512, d.getLongSummary().length());
        assertEquals(512, d.getAlertId().length());
    }

    @Test
    void publish_skipsDuplicateForExistingAlertIdAndUser() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        e.setReceivers(new ArrayList<>(List.of(receiver("dupUser"), receiver("newUser"))));
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid-x");
        when(alertDetailRepository.existsByAlertIdAndUserLoginId("aid-x", "dupUser")).thenReturn(true);
        when(alertDetailRepository.existsByAlertIdAndUserLoginId("aid-x", "newUser")).thenReturn(false);

        alertPublisher.publish(e);

        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals(1, cap.getValue().size());
        assertEquals("newUser", cap.getValue().get(0).getUserLoginId());
    }

    @Test
    void publish_allDuplicates_doesNotCallSaveAll() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid-x");
        when(alertDetailRepository.existsByAlertIdAndUserLoginId(eq("aid-x"), anyString())).thenReturn(true);

        assertTrue(alertPublisher.publish(e));
        verify(alertDetailRepository, never()).saveAll(any());
    }

    @Test
    void publish_mapsMetadataToFlexAttributes() {
        AlertEvent e = baseEvent(AlertTypes.SupplyAllocationChange);
        Map<String, Object> meta = e.getMetadata();
        meta.put("Item", "ITM-1");
        meta.put("Supplier", "SUP-1");
        meta.put("SourceSite", "SITE-A");
        meta.put("DestinationSite", "SITE-B");
        meta.put("Responsibility", "RESP-1");
        meta.put("CostType", "TYPE-1");
        meta.put("State", "OPEN");
        meta.put("UpdatedBy", "admin");
        meta.put("Allocation", 42.5);
        meta.put("StartDate", LocalDate.of(2025, 1, 1));
        meta.put("EndDate", new Date(125, 11, 31));

        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);

        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        AlertDetail d = cap.getValue().get(0);
        assertEquals("ITM-1", d.getStringAttribute1());
        assertEquals("SUP-1", d.getStringAttribute2());
        assertEquals("SITE-A", d.getStringAttribute3());
        assertEquals("SITE-B", d.getStringAttribute4());
        assertEquals("RESP-1", d.getStringAttribute6());
        assertEquals("TYPE-1", d.getStringAttribute7());
        assertEquals("OPEN", d.getStringAttribute8());
        assertEquals("admin", d.getStringAttribute9());
        assertEquals(42.5, d.getNumericAttribute1());
        assertEquals(LocalDate.of(2025, 1, 1), d.getDateAttribute1());
        assertNotNull(d.getDateAttribute2());
    }

    @Test
    void publish_destinationSiteFallsBackToRegion() {
        AlertEvent e = baseEvent(AlertTypes.ForecastChange);
        e.getMetadata().put("Region", "REGION-X");
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);

        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals("REGION-X", cap.getValue().get(0).getStringAttribute4());
    }

    @Test
    void publish_destinationSiteWinsOverRegion() {
        AlertEvent e = baseEvent(AlertTypes.ForecastChange);
        e.getMetadata().put("DestinationSite", "DS");
        e.getMetadata().put("Region", "REGION-X");
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);

        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals("DS", cap.getValue().get(0).getStringAttribute4());
    }

    @Test
    void publish_handlesNullMetadata() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        e.setMetadata(null);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        assertTrue(alertPublisher.publish(e));
        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        AlertDetail d = cap.getValue().get(0);
        assertNull(d.getStringAttribute1());
        assertNull(d.getNumericAttribute1());
        assertNull(d.getDateAttribute1());
    }

    @Test
    void publish_returnsFalseWhenRepositoryThrows() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");
        doThrow(new RuntimeException("db down")).when(alertDetailRepository).saveAll(any());

        assertFalse(alertPublisher.publish(e));
    }

    @Test
    void publish_punchoutUrl_costMissing() {
        AlertEvent e = baseEvent(AlertTypes.CostMissing);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);
        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals("/pinSourcingLaneItem.do?objectKey=", cap.getValue().get(0).getPunchoutUrl());
    }

    @Test
    void publish_punchoutUrl_forecast() {
        AlertEvent e = baseEvent(AlertTypes.ForecastPending);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);
        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals("/pinForecast.do?objectKey=", cap.getValue().get(0).getPunchoutUrl());
    }

    @Test
    void publish_punchoutUrl_supplyAllocation() {
        AlertEvent e = baseEvent(AlertTypes.SupplyAllocationMissing);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);
        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals("/pinSupplyAllocation.do?objectKey=", cap.getValue().get(0).getPunchoutUrl());
    }

    @Test
    void publish_punchoutUrl_item() {
        AlertEvent e = baseEvent(AlertTypes.ItemAssignment);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);
        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals("/pinItem.do?objectKey=", cap.getValue().get(0).getPunchoutUrl());
    }

    @Test
    void publish_skipsDuplicateCheckWhenLoginIdIsNull() {
        AlertEvent e = baseEvent(AlertTypes.CostChange);
        e.setReceivers(new ArrayList<>(List.of(AlertReceiver.builder().userId(1L).userLoginId(null).build())));
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        assertTrue(alertPublisher.publish(e));
        verify(alertDetailRepository, never()).existsByAlertIdAndUserLoginId(anyString(), anyString());
        verify(alertDetailRepository).saveAll(any());
    }

    @Test
    void publish_metadataNumberAcceptsInteger() {
        AlertEvent e = baseEvent(AlertTypes.SupplyAllocationChange);
        e.getMetadata().put("Allocation", 7);
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);
        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertEquals(7.0, cap.getValue().get(0).getNumericAttribute1());
    }

    @Test
    void publish_metadataNumberIgnoresNonNumber() {
        AlertEvent e = baseEvent(AlertTypes.SupplyAllocationChange);
        e.getMetadata().put("Allocation", "not-a-number");
        when(alertTemplateService.evaluateShortSummary(any())).thenReturn("s");
        when(alertTemplateService.evaluateLongSummary(any())).thenReturn("l");
        when(alertTemplateService.evaluateAlertId(any())).thenReturn("aid");

        alertPublisher.publish(e);
        ArgumentCaptor<List<AlertDetail>> cap = ArgumentCaptor.forClass(List.class);
        verify(alertDetailRepository).saveAll(cap.capture());
        assertNull(cap.getValue().get(0).getNumericAttribute1());
    }
}
