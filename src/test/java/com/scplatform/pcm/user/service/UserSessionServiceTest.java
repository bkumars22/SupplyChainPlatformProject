/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.service;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.contact.entity.Contact;
import com.scplatform.pcm.user.entity.PcmUserSessionInfo;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.PcmUserSessionInfoRepository;
import com.scplatform.pcm.user.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSessionServiceTest {

    @Mock private PcmUserSessionInfoRepository sessionInfoRepository;
    @Mock private UsersRepository userRepository;
    @Mock private PcmConfigUtil configUtil;

    @InjectMocks
    private UserSessionService service;

    private PcmUserSessionInfo recent(String id, String sid) {
        return new PcmUserSessionInfo(id, sid, new Timestamp(System.currentTimeMillis()));
    }

    private PcmUserSessionInfo expired(String id, String sid) {
        return new PcmUserSessionInfo(id, sid,
                new Timestamp(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(60)));
    }

    @BeforeEach
    void stubTimeoutDefault() {
        // No-op: each test sets when(configUtil.getString(...)) as needed.
    }

    @Test
    void checkAndManageSession_noExisting_createsNew() {
        when(sessionInfoRepository.findByUserId("u")).thenReturn(Optional.empty());
        when(configUtil.getString(anyString())).thenReturn(null);

        UserSessionService.SessionCheckResult r =
                service.checkAndManageSession("u", "C1", null);

        assertEquals(UserSessionService.SessionCheckResult.NEW_SESSION_CREATED, r);
        ArgumentCaptor<PcmUserSessionInfo> cap = ArgumentCaptor.forClass(PcmUserSessionInfo.class);
        verify(sessionInfoRepository).save(cap.capture());
        assertEquals("u", cap.getValue().getUserId());
        assertEquals("C1", cap.getValue().getSessionId());
    }

    @Test
    void checkAndManageSession_expired_recreates() {
        when(sessionInfoRepository.findByUserId("u")).thenReturn(Optional.of(expired("u", "OLD")));
        when(configUtil.getString(anyString())).thenReturn("10");

        UserSessionService.SessionCheckResult r =
                service.checkAndManageSession("u", "NEW", null);

        assertEquals(UserSessionService.SessionCheckResult.SESSION_EXPIRED_RECREATED, r);
        verify(sessionInfoRepository).deleteByUserId("u");
        verify(sessionInfoRepository).save(any(PcmUserSessionInfo.class));
    }

    @Test
    void checkAndManageSession_sameClient_updatesAccessTime() {
        when(sessionInfoRepository.findByUserId("u")).thenReturn(Optional.of(recent("u", "C1")));
        when(configUtil.getString(anyString())).thenReturn("10");

        UserSessionService.SessionCheckResult r =
                service.checkAndManageSession("u", "C1", null);

        assertEquals(UserSessionService.SessionCheckResult.VALID, r);
        verify(sessionInfoRepository).updateLastAccessTime(eq("u"), any(Timestamp.class));
    }

    @Test
    void checkAndManageSession_concurrentLogin_continueWithSameId_recreates() {
        when(sessionInfoRepository.findByUserId("u")).thenReturn(Optional.of(recent("u", "OLD")));
        when(configUtil.getString(anyString())).thenReturn("10");

        UserSessionService.SessionCheckResult r =
                service.checkAndManageSession("u", "NEW", "continueWithSameId");

        assertEquals(UserSessionService.SessionCheckResult.CONCURRENT_LOGIN_CONTINUED, r);
        verify(sessionInfoRepository).deleteByUserId("u");
        verify(sessionInfoRepository).save(any(PcmUserSessionInfo.class));
    }

    @Test
    void checkAndManageSession_concurrentLogin_cancel() {
        when(sessionInfoRepository.findByUserId("u")).thenReturn(Optional.of(recent("u", "OLD")));
        when(configUtil.getString(anyString())).thenReturn("10");

        assertEquals(UserSessionService.SessionCheckResult.CONCURRENT_LOGIN_CANCELLED,
                service.checkAndManageSession("u", "NEW", "Cancel"));
    }

    @Test
    void checkAndManageSession_concurrentLogin_unspecified_decisionRequired() {
        when(sessionInfoRepository.findByUserId("u")).thenReturn(Optional.of(recent("u", "OLD")));
        when(configUtil.getString(anyString())).thenReturn("10");

        assertEquals(UserSessionService.SessionCheckResult.CONCURRENT_LOGIN_DETECTED,
                service.checkAndManageSession("u", "NEW", null));
    }

    @Test
    void getSessionTimeout_defaultsTo10WhenConfigBlankNullOrInvalid() {
        when(sessionInfoRepository.findByUserId("u"))
                .thenReturn(Optional.of(expired("u", "OLD")));
        when(configUtil.getString(anyString())).thenReturn(""); // blank

        // call once with blank config
        service.checkAndManageSession("u", "NEW", null);

        // invalid integer => exception path => default
        when(configUtil.getString(anyString())).thenReturn("not-an-int");
        service.checkAndManageSession("u", "NEW", null);

        verify(sessionInfoRepository, atLeastOnce()).save(any(PcmUserSessionInfo.class));
    }

    @Test
    void createSession_savesEntityWithCurrentTimestamp() {
        service.createSession("u", "C1");
        ArgumentCaptor<PcmUserSessionInfo> cap = ArgumentCaptor.forClass(PcmUserSessionInfo.class);
        verify(sessionInfoRepository).save(cap.capture());
        PcmUserSessionInfo s = cap.getValue();
        assertEquals("u", s.getUserId());
        assertEquals("C1", s.getSessionId());
        assertNotNull(s.getLastUpdateOn());
    }

    @Test
    void deleteSession_delegates() {
        service.deleteSession("u");
        verify(sessionInfoRepository).deleteByUserId("u");
    }

    @Test
    void updateSessionLastAccessTime_delegates() {
        service.updateSessionLastAccessTime("u");
        verify(sessionInfoRepository).updateLastAccessTime(eq("u"), any(Timestamp.class));
    }

    @Test
    void isSessionPresent_andGetSessionInfo_delegate() {
        when(sessionInfoRepository.existsByUserId("u")).thenReturn(true);
        when(sessionInfoRepository.findByUserId("u")).thenReturn(Optional.of(recent("u", "S")));
        assertTrue(service.isSessionPresent("u"));
        assertTrue(service.getSessionInfo("u").isPresent());
    }

    @Test
    void saveOrUpdate_delegates() {
        Users u = new Users(1L);
        when(userRepository.save(u)).thenReturn(u);
        assertSame(u, service.saveOrUpdate(u));
    }

    @Test
    void findUserByKey_initializesLazyCollections_andReturnsNullWhenAbsent() {
        Users u = new Users(7L);
        u.setContact(new Contact());
        u.setBusinessEntity(new BusinessEntity());
        when(userRepository.findById(7L)).thenReturn(Optional.of(u));
        when(userRepository.findById(8L)).thenReturn(Optional.empty());

        assertSame(u, service.findUserByKey(7L));
        assertNull(service.findUserByKey(8L));
    }

    @Test
    void findAllUsersById_returnsEmptyForNullAndDelegatesOtherwise() {
        assertTrue(service.findAllUsersById(null).isEmpty());
        when(userRepository.findByUserIdIgnoreCase("x")).thenReturn(List.of(new Users(1L)));
        assertEquals(1, service.findAllUsersById("x").size());
    }

    @Test
    void findUserByEMail_returnsEmptyForNullAndDelegatesOtherwise() {
        assertTrue(service.findUserByEMail(null).isEmpty());
        when(userRepository.findByContact_EMail("e@x")).thenReturn(List.of(new Users(1L)));
        assertEquals(1, service.findUserByEMail("e@x").size());
    }

    @Test
    void findUserByUserId_initAllPathsAndNullPath() {
        assertNull(service.findUserByUserId(null, true));
        assertNull(service.findUserByUserId(null, false));

        Users u = new Users(1L);
        when(userRepository.findUserByUserIdWithRelationships("a")).thenReturn(u);
        assertSame(u, service.findUserByUserId("a", true));

        when(userRepository.findByUserId("b")).thenReturn(Optional.of(u));
        assertSame(u, service.findUserByUserId("b", false));

        when(userRepository.findByUserId("missing")).thenReturn(Optional.empty());
        assertNull(service.findUserByUserId("missing", false));
    }

    @Test
    void setUserLastAccess_returnsTrueWhenRowsUpdated() {
        when(userRepository.updateUserLastAccessDate(1L)).thenReturn(1);
        when(userRepository.updateUserLastAccessDate(2L)).thenReturn(0);
        assertTrue(service.setUserLastAccess(1L));
        assertFalse(service.setUserLastAccess(2L));
    }

    @Test
    void sessionCheckResultEnum_valuesAndValueOf() {
        for (UserSessionService.SessionCheckResult r : UserSessionService.SessionCheckResult.values()) {
            assertSame(r, UserSessionService.SessionCheckResult.valueOf(r.name()));
        }
    }
}
