/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.alert.service;

import com.scplatform.pcm.alert.dto.AlertEvent;
import com.scplatform.pcm.alert.dto.AlertReceiver;
import com.scplatform.pcm.alert.entity.PcmAlertSubscription;
import com.scplatform.pcm.alert.entity.PcmAlertSubscriptionOption;
import com.scplatform.pcm.alert.enums.AlertTypes;
import com.scplatform.pcm.alert.repository.AlertSubscriptionRepository;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertSubscriptionServiceTest {

    @Mock private AlertSubscriptionRepository subscriptionRepository;
    @Mock private UsersRepository usersRepository;

    @InjectMocks private AlertSubscriptionService service;

    private static PcmAlertSubscription sub(long subKey, long userKey, int subscribeFlag,
                                            Map<String, String> opts) {
        PcmAlertSubscription s = PcmAlertSubscription.builder()
                .subscriptionKey(subKey)
                .alertType("CostChange")
                .userKey(userKey)
                .subscribeFlag(subscribeFlag)
                .build();
        opts.forEach((k, v) -> {
            PcmAlertSubscriptionOption o = new PcmAlertSubscriptionOption();
            o.setOptionId(k);
            o.setOptionValue(v);
            s.getOptions().add(o);
        });
        return s;
    }

    private static Users user(long key, String login, String name, String email) {
        Users u = new Users();
        u.setUserKey(key);
        u.setUserId(login);
        u.setUserName(name);
        u.setEmailAddress(email);
        return u;
    }

    private AlertEvent event(Long actor, Long referenceKey, Map<String, Object> filters) {
        AlertEvent ev = new AlertEvent();
        ev.setAlertType(AlertTypes.CostChange);
        ev.setActor(actor);
        ev.setReferenceKey(referenceKey);
        ev.setFilters(filters == null ? new HashMap<>() : filters);
        return ev;
    }

    @Test
    void findSubscribers_returnsReceiverForMatchingSubscription() {
        PcmAlertSubscription s1 = sub(1L, 100L, 1, Map.of());
        when(subscriptionRepository.findActiveSubscriptions("CostChange")).thenReturn(List.of(s1));
        when(usersRepository.findById(100L)).thenReturn(
                Optional.of(user(100L, "jdoe", "John Doe", "j@x.com")));

        List<AlertReceiver> receivers = service.findSubscribers(event(null, null, null));

        assertEquals(1, receivers.size());
        AlertReceiver r = receivers.get(0);
        assertEquals(100L, r.getUserId());
        assertEquals("jdoe", r.getUserLoginId());
        assertEquals("John Doe", r.getUserName());
        assertEquals("j@x.com", r.getEmail());
    }

    @Test
    void findSubscribers_excludesActor_whenExcludeOwnActionsIsY() {
        PcmAlertSubscription s = sub(1L, 100L, 1, Map.of("excludeOwnActions", "Y"));
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s));

        List<AlertReceiver> receivers = service.findSubscribers(event(100L, null, null));

        assertTrue(receivers.isEmpty());
        verify(usersRepository, never()).findById(any());
    }

    @Test
    void findSubscribers_includesActor_whenExcludeOwnActionsNotSet() {
        PcmAlertSubscription s = sub(1L, 100L, 1, Map.of());
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s));
        when(usersRepository.findById(100L)).thenReturn(
                Optional.of(user(100L, "jdoe", "n", "e")));

        List<AlertReceiver> receivers = service.findSubscribers(event(100L, null, null));
        assertEquals(1, receivers.size());
    }

    @Test
    void findSubscribers_filtersOnResponsibilityKey_mismatch() {
        PcmAlertSubscription s = sub(1L, 100L, 1, Map.of("responsibilityKey", "ENG"));
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s));

        Map<String, Object> filters = new HashMap<>();
        filters.put("responsibilityKey", "OPS");
        List<AlertReceiver> receivers = service.findSubscribers(event(null, null, filters));
        assertTrue(receivers.isEmpty());
    }

    @Test
    void findSubscribers_filtersOnResponsibilityKey_match() {
        PcmAlertSubscription s = sub(1L, 100L, 1, Map.of("responsibilityKey", "ENG"));
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s));
        when(usersRepository.findById(100L)).thenReturn(Optional.of(user(100L, "u", "n", "e")));

        Map<String, Object> filters = new HashMap<>();
        filters.put("responsibilityKey", "ENG");
        List<AlertReceiver> receivers = service.findSubscribers(event(null, null, filters));
        assertEquals(1, receivers.size());
    }

    @Test
    void findSubscribers_subscriptionWithoutFilter_matchesAll() {
        // Sub has no responsibilityKey option → matches even when event has one
        PcmAlertSubscription s = sub(1L, 100L, 1, Map.of());
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s));
        when(usersRepository.findById(100L)).thenReturn(Optional.of(user(100L, "u", "n", "e")));

        Map<String, Object> filters = new HashMap<>();
        filters.put("responsibilityKey", "OPS");
        List<AlertReceiver> receivers = service.findSubscribers(event(null, null, filters));
        assertEquals(1, receivers.size());
    }

    @Test
    void findSubscribers_eventWithoutFilterValue_passesThrough() {
        // Sub HAS option, event provides no value for that option key → still matches
        PcmAlertSubscription s = sub(1L, 100L, 1, Map.of("costTypeKey", "BUY"));
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s));
        when(usersRepository.findById(100L)).thenReturn(Optional.of(user(100L, "u", "n", "e")));

        List<AlertReceiver> receivers = service.findSubscribers(event(null, null, null));
        assertEquals(1, receivers.size());
    }

    @Test
    void findSubscribers_filtersOnItemKey_fromReferenceKey() {
        PcmAlertSubscription matching = sub(1L, 100L, 1, Map.of("itemKey", "999"));
        PcmAlertSubscription nonMatching = sub(2L, 200L, 1, Map.of("itemKey", "111"));
        when(subscriptionRepository.findActiveSubscriptions(anyString()))
                .thenReturn(List.of(matching, nonMatching));
        when(usersRepository.findById(100L)).thenReturn(Optional.of(user(100L, "u1", "n1", "e1")));

        List<AlertReceiver> receivers = service.findSubscribers(event(null, 999L, null));
        assertEquals(1, receivers.size());
        assertEquals(100L, receivers.get(0).getUserId());
    }

    @Test
    void findSubscribers_skipsUser_whenNotFoundInUsersRepository() {
        PcmAlertSubscription s = sub(1L, 100L, 1, Map.of());
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s));
        when(usersRepository.findById(100L)).thenReturn(Optional.empty());

        List<AlertReceiver> receivers = service.findSubscribers(event(null, null, null));
        assertTrue(receivers.isEmpty());
    }

    @Test
    void findSubscribers_deduplicatesByUserKey() {
        PcmAlertSubscription s1 = sub(1L, 100L, 1, Map.of());
        PcmAlertSubscription s2 = sub(2L, 100L, 1, Map.of()); // same user, different sub
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s1, s2));
        when(usersRepository.findById(100L)).thenReturn(Optional.of(user(100L, "u", "n", "e")));

        List<AlertReceiver> receivers = service.findSubscribers(event(null, null, null));
        assertEquals(1, receivers.size());
        verify(usersRepository, times(1)).findById(100L);
    }

    @Test
    void findSubscribers_filtersWithNullValue_treatedAsAbsent() {
        PcmAlertSubscription s = sub(1L, 100L, 1, Map.of("costTypeKey", "BUY"));
        when(subscriptionRepository.findActiveSubscriptions(anyString())).thenReturn(List.of(s));
        when(usersRepository.findById(100L)).thenReturn(Optional.of(user(100L, "u", "n", "e")));

        Map<String, Object> filters = new HashMap<>();
        filters.put("costTypeKey", null);
        List<AlertReceiver> receivers = service.findSubscribers(event(null, null, filters));
        assertEquals(1, receivers.size());
    }
}
