/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.ums.dto.UMSUser;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private UserService userService;

    private List<Users> sampleUsers;

    @BeforeEach
    void setUp() {
        sampleUsers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Users u = new Users((long) i);
            u.setUserId("u" + i);
            sampleUsers.add(u);
        }
    }

    @Test
    void findUsersForBusinessByKey_noPagination_returnsAll() {
        when(usersRepository.findUsersByBusinessEntityKeyAndEnabledAndSearchText(1L, true, ""))
                .thenReturn(sampleUsers);
        Map<String, Object> r = userService.findUsersForBusinessByKey(1L, "", true, null);
        assertEquals(5, r.get("count"));
        @SuppressWarnings("unchecked")
        List<Users> list = (List<Users>) r.get("resultList");
        assertEquals(5, list.size());
    }

    @Test
    void findUsersForBusinessByKey_appliesOffsetAndLimit() {
        when(usersRepository.findUsersByBusinessEntityKeyAndEnabledAndSearchText(1L, true, "x"))
                .thenReturn(sampleUsers);
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("offset", "1");
        p.add("limit", "2");
        Map<String, Object> r = userService.findUsersForBusinessByKey(1L, "x", true, p);
        assertEquals(5, r.get("count"));
        @SuppressWarnings("unchecked")
        List<Users> list = (List<Users>) r.get("resultList");
        assertEquals(2, list.size());
        assertEquals("u1", list.get(0).getUserId());
        assertEquals("u2", list.get(1).getUserId());
    }

    @Test
    void findUsersForBusinessByKey_offsetBeyondSize_returnsEmpty() {
        when(usersRepository.findUsersByBusinessEntityKeyAndEnabledAndSearchText(1L, true, ""))
                .thenReturn(sampleUsers);
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("offset", "100");
        Map<String, Object> r = userService.findUsersForBusinessByKey(1L, "", true, p);
        @SuppressWarnings("unchecked")
        List<Users> list = (List<Users>) r.get("resultList");
        assertTrue(list.isEmpty());
    }

    @Test
    void findUsersForBusinessByKey_invalidNumbersFallBack() {
        when(usersRepository.findUsersByBusinessEntityKeyAndEnabledAndSearchText(1L, true, ""))
                .thenReturn(sampleUsers);
        MultiValueMap<String, String> p = new LinkedMultiValueMap<>();
        p.add("offset", "not-a-number");
        p.add("limit", "still-not-a-number");
        Map<String, Object> r = userService.findUsersForBusinessByKey(1L, "", true, p);
        @SuppressWarnings("unchecked")
        List<Users> list = (List<Users>) r.get("resultList");
        assertEquals(5, list.size());
    }

    @Test
    void findByUserId_delegatesToRepoOptional() {
        Users u = new Users(1L);
        when(usersRepository.findByUserId("x")).thenReturn(Optional.of(u));
        assertSame(u, userService.findByUserId("x"));
        when(usersRepository.findByUserId("missing")).thenReturn(Optional.empty());
        assertNull(userService.findByUserId("missing"));
    }

    @Test
    void getUser_findById() {
        Users u = new Users(7L);
        when(usersRepository.findById(7L)).thenReturn(Optional.of(u));
        assertSame(u, userService.getUser(7L));
        when(usersRepository.findById(8L)).thenReturn(Optional.empty());
        assertNull(userService.getUser(8L));
    }

    @Test
    void saveUser_findAllEnabled_findUsersBy_andEnabledByBE() {
        Users u = new Users(1L);
        when(usersRepository.save(u)).thenReturn(u);
        when(usersRepository.findByIsEnabledTrue()).thenReturn(List.of(u));
        when(usersRepository.findByBusinessEntityKey(2L)).thenReturn(List.of(u));
        when(usersRepository.findEnabledUsersByBusinessEntity(2L)).thenReturn(List.of(u));

        assertSame(u, userService.saveUser(u));
        assertEquals(1, userService.findAllEnabledUsers().size());
        assertEquals(1, userService.findUsersByBusinessEntity(2L).size());
        assertEquals(1, userService.findEnabledUsersByBusinessEntity(2L).size());
    }

    @Test
    void mapUserEntityWithUMSUser_appliesAllOverrides() {
        UMSUser u = new UMSUser();
        u.setEmail("e@x.com");
        u.setFirstName("F");
        u.setLastName("L");
        u.setPreferredLocale("en");
        u.setPreferredTimezone("UTC");
        u.setPreferredPagination("25");
        Users target = new Users();
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(eq("pcm.user.provision.override.pageSize"), any())).thenReturn("50");
        when(cfg.getString(eq("pcm.user.provision.default.pageSize"), any())).thenReturn(null);
        when(cfg.getString(eq("pcm.user.provision.override.timeZone"), any())).thenReturn("Europe/Paris");
        when(cfg.getString(eq("pcm.user.provision.default.timeZone"), any())).thenReturn(null);
        when(cfg.getString(eq("pcm.user.provision.override.dateFormat"), any())).thenReturn("yyyy-MM-dd");
        when(cfg.getString(eq("pcm.user.provision.override.timeFormat"), any())).thenReturn("HH:mm:ss");
        when(cfg.getString(eq("pcm.user.provision.default.dateFormat"), any())).thenReturn(null);
        when(cfg.getString(eq("pcm.user.provision.default.timeFormat"), any())).thenReturn(null);

        UserService.mapUserEntityWithUMSUser("F.L", u, target, cfg);

        assertEquals("e@x.com", target.getEmailAddress());
        assertEquals("F L", target.getUserName());
        assertEquals(50, target.getDefaultPageSize());
        assertEquals("Europe/Paris", target.getPreference("TIMEZONE"));
        assertEquals("yyyy-MM-dd", target.getPreference("DATE_FORMAT"));
        assertEquals("HH:mm:ss", target.getPreference("TIME_FORMAT"));
    }

    @Test
    void mapUserEntityWithUMSUser_usesUmsAndDefaultsWhenNoOverrides() {
        UMSUser u = new UMSUser();
        u.setFirstName("Only");
        u.setLastName("");
        u.setPreferredPagination("33");
        u.setPreferredTimezone("Asia/Tokyo");
        u.setPreferredLocale("en"); // triggers locale-derived date/time
        Users target = new Users();
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(any(), any())).thenReturn(null);

        UserService.mapUserEntityWithUMSUser("Only", u, target, cfg);
        assertEquals("Only", target.getUserName());
        assertEquals(33, target.getDefaultPageSize());
        assertEquals("Asia/Tokyo", target.getPreference("TIMEZONE"));
    }

    @Test
    void mapUserEntityWithUMSUser_usesDefaultsWhenUmsBlank() {
        UMSUser u = new UMSUser();
        u.setLastName("Last");
        Users target = new Users();
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(eq("pcm.user.provision.override.pageSize"), any())).thenReturn(null);
        when(cfg.getString(eq("pcm.user.provision.default.pageSize"), any())).thenReturn("12");
        when(cfg.getString(eq("pcm.user.provision.override.timeZone"), any())).thenReturn(null);
        when(cfg.getString(eq("pcm.user.provision.default.timeZone"), any())).thenReturn("UTC");
        when(cfg.getString(eq("pcm.user.provision.override.dateFormat"), any())).thenReturn(null);
        when(cfg.getString(eq("pcm.user.provision.default.dateFormat"), any())).thenReturn("dd/MM/yyyy");
        when(cfg.getString(eq("pcm.user.provision.override.timeFormat"), any())).thenReturn(null);
        when(cfg.getString(eq("pcm.user.provision.default.timeFormat"), any())).thenReturn("HH:mm");

        UserService.mapUserEntityWithUMSUser("L", u, target, cfg);
        assertEquals("Last", target.getUserName());
        assertEquals(12, target.getDefaultPageSize());
        assertEquals("UTC", target.getPreference("TIMEZONE"));
        assertEquals("dd/MM/yyyy", target.getPreference("DATE_FORMAT"));
        assertEquals("HH:mm", target.getPreference("TIME_FORMAT"));
    }

    @Test
    void mapUserEntityWithUMSUser_emptyEmailAndNamesProducesNoUpdates() {
        UMSUser u = new UMSUser();
        Users target = new Users();
        target.setUserName("EXISTING");
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(any(), any())).thenReturn(null);

        UserService.mapUserEntityWithUMSUser("ID", u, target, cfg);
        assertNull(target.getEmailAddress());
        assertEquals("EXISTING", target.getUserName());
    }
}
