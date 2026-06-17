/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.entity.DlqAlertRecord;
import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.enums.DlqStatus;
import com.scplatform.pcm.alert.repository.DlqAlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DlqAlertServiceTest {

    @Mock private DlqAlertRepository dlqRepository;
    @Mock private JsonMapper jsonMapper;
    @Mock private JavaMailSender mailSender;

    @InjectMocks private DlqAlertService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "notifyEmail", "");
        ReflectionTestUtils.setField(service, "notifyEnabled", false);
        ReflectionTestUtils.setField(service, "applicationName", "scplatform-test");
    }

    private AlertEvent baseEvent() {
        AlertEvent ev = new AlertEvent();
        ev.setAlertEventID("evt-123");
        ev.setAlertType(AlertTypes.CostChange);
        ev.setObjectKey(42L);
        ev.setReferenceKey(7L);
        ev.setActor(1L);
        return ev;
    }

    // ── setMailSender ──

    @Test
    void setMailSender_acceptsNullWithoutFailing() {
        service.setMailSender(null);
        // No exception, no log.info expected
    }

    @Test
    void setMailSender_storesSender() {
        DlqAlertService s = new DlqAlertService(dlqRepository, jsonMapper);
        s.setMailSender(mailSender);
        assertSame(mailSender, ReflectionTestUtils.getField(s, "mailSender"));
    }

    // ── recordFailedAlert ──

    @Test
    void recordFailedAlert_persistsRecordWithExpectedFields() throws Exception {
        when(jsonMapper.writeValueAsString(any())).thenReturn("{\"id\":\"evt-123\"}");
        AlertEvent ev = baseEvent();
        ev.setPublishAttemptCount(5);

        service.recordFailedAlert(ev);

        ArgumentCaptor<DlqAlertRecord> cap = ArgumentCaptor.forClass(DlqAlertRecord.class);
        verify(dlqRepository).save(cap.capture());
        DlqAlertRecord r = cap.getValue();
        assertEquals("evt-123", r.getAlertEventId());
        assertEquals(AlertTypes.CostChange, r.getAlertType());
        assertEquals(42L, r.getObjectKey());
        assertEquals(7L, r.getReferenceKey());
        assertEquals(1L, r.getActor());
        assertEquals(5, r.getDeliveryAttempts());
        assertEquals(DlqStatus.NEW, r.getStatus());
        assertEquals("{\"id\":\"evt-123\"}", r.getEventPayload());
        assertNotNull(r.getErrorMessage());
    }

    @Test
    void recordFailedAlert_storesFallbackPayload_whenSerializationFails() throws Exception {
        when(jsonMapper.writeValueAsString(any())).thenThrow(new RuntimeException("boom"));
        service.recordFailedAlert(baseEvent());

        ArgumentCaptor<DlqAlertRecord> cap = ArgumentCaptor.forClass(DlqAlertRecord.class);
        verify(dlqRepository).save(cap.capture());
        assertTrue(cap.getValue().getEventPayload().contains("serialization failed"));
        assertTrue(cap.getValue().getEventPayload().contains("evt-123"));
    }

    @Test
    void recordFailedAlert_swallowsRepositoryFailures() {
        when(jsonMapper.writeValueAsString(any())).thenReturn("{}");
        doThrow(new RuntimeException("db down")).when(dlqRepository).save(any());
        // should not throw
        service.recordFailedAlert(baseEvent());
        verify(dlqRepository).save(any());
    }

    @Test
    void recordFailedAlert_sendsEmail_whenEnabledAndConfigured() {
        when(jsonMapper.writeValueAsString(any())).thenReturn("{}");
        when(dlqRepository.save(any())).thenAnswer(inv -> {
            DlqAlertRecord r = inv.getArgument(0);
            r.setDlqKey(99L);
            r.setReceivedDate(LocalDateTime.now());
            return r;
        });
        ReflectionTestUtils.setField(service, "notifyEnabled", true);
        ReflectionTestUtils.setField(service, "notifyEmail", "ops@example.com");
        service.setMailSender(mailSender);

        service.recordFailedAlert(baseEvent());

        ArgumentCaptor<SimpleMailMessage> cap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(cap.capture());
        SimpleMailMessage msg = cap.getValue();
        assertNotNull(msg.getTo());
        assertEquals("ops@example.com", msg.getTo()[0]);
        assertTrue(msg.getSubject().contains("CostChange"));
        assertTrue(msg.getText().contains("evt-123"));
    }

    @Test
    void recordFailedAlert_swallowsEmailException() {
        when(jsonMapper.writeValueAsString(any())).thenReturn("{}");
        ReflectionTestUtils.setField(service, "notifyEnabled", true);
        ReflectionTestUtils.setField(service, "notifyEmail", "ops@example.com");
        service.setMailSender(mailSender);
        doThrow(new RuntimeException("smtp down")).when(mailSender).send(any(SimpleMailMessage.class));

        service.recordFailedAlert(baseEvent());
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void recordFailedAlert_doesNotEmail_whenDisabled() {
        when(jsonMapper.writeValueAsString(any())).thenReturn("{}");
        service.setMailSender(mailSender);
        // notifyEnabled = false (default in setUp)
        service.recordFailedAlert(baseEvent());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void recordFailedAlert_warnsAndSkipsEmail_whenEnabledButNoMailSender() {
        when(jsonMapper.writeValueAsString(any())).thenReturn("{}");
        ReflectionTestUtils.setField(service, "notifyEnabled", true);
        ReflectionTestUtils.setField(service, "notifyEmail", "ops@example.com");
        // mailSender NOT set
        service.recordFailedAlert(baseEvent());
        verify(dlqRepository).save(any());
    }

    @Test
    void recordFailedAlert_doesNotEmail_whenNotifyEmailBlank() {
        when(jsonMapper.writeValueAsString(any())).thenReturn("{}");
        ReflectionTestUtils.setField(service, "notifyEnabled", true);
        ReflectionTestUtils.setField(service, "notifyEmail", "");
        service.setMailSender(mailSender);
        service.recordFailedAlert(baseEvent());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    // ── Ops methods ──

    @Test
    void getUnresolvedEntries_delegatesToRepository() {
        DlqAlertRecord r = DlqAlertRecord.builder().dlqKey(1L).build();
        when(dlqRepository.findByStatusOrderByReceivedDateDesc(DlqStatus.NEW))
                .thenReturn(List.of(r));
        List<DlqAlertRecord> out = service.getUnresolvedEntries();
        assertEquals(1, out.size());
        assertSame(r, out.get(0));
    }

    @Test
    void getUnresolvedCount_delegatesToRepository() {
        when(dlqRepository.countByStatus(DlqStatus.NEW)).thenReturn(7L);
        assertEquals(7L, service.getUnresolvedCount());
    }

    @Test
    void resolveEntry_passesAllArgsToRepository() {
        service.resolveEntry(11L, "ops", "fixed");
        verify(dlqRepository).markAsResolved(eq(11L), eq("ops"), eq("fixed"), any(LocalDateTime.class));
    }

    @Test
    void cleanupOldEntries_passesCutoffDateAndReturnsCount() {
        when(dlqRepository.deleteOldResolvedEntries(any(LocalDateTime.class))).thenReturn(4);
        int deleted = service.cleanupOldEntries(30);
        assertEquals(4, deleted);
        ArgumentCaptor<LocalDateTime> cap = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(dlqRepository).deleteOldResolvedEntries(cap.capture());
        // cutoff should be ~30 days ago
        LocalDateTime cutoff = cap.getValue();
        assertTrue(cutoff.isBefore(LocalDateTime.now().minusDays(29)));
        assertTrue(cutoff.isAfter(LocalDateTime.now().minusDays(31)));
    }
}
