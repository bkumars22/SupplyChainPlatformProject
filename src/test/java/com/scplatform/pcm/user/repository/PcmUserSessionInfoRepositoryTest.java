/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.repository;

import com.scplatform.pcm.user.entity.PcmUserSessionInfo;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PcmUserSessionInfoRepositoryTest {

    @Test
    void isAnnotatedAsRepositoryAndExtendsJpa() {
        assertTrue(JpaRepository.class.isAssignableFrom(PcmUserSessionInfoRepository.class));
        assertNotNull(PcmUserSessionInfoRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void findByUserId_signatureIsString() throws Exception {
        Method m = PcmUserSessionInfoRepository.class.getMethod("findByUserId", String.class);
        assertEquals(Optional.class, m.getReturnType());
    }

    @Test
    void existsByUserId_andDeleteByUserId_signatures() throws Exception {
        Method exists = PcmUserSessionInfoRepository.class.getMethod("existsByUserId", String.class);
        assertEquals(boolean.class, exists.getReturnType());
        Method del = PcmUserSessionInfoRepository.class.getMethod("deleteByUserId", String.class);
        assertEquals(void.class, del.getReturnType());
    }

    @Test
    void updateLastAccessTime_isModifyingAndQueryAnnotated() throws Exception {
        Method m = PcmUserSessionInfoRepository.class
                .getMethod("updateLastAccessTime", String.class, Timestamp.class);
        assertNotNull(m.getAnnotation(Modifying.class));
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("UPDATE PcmUserSessionInfo"));
        assertEquals(int.class, m.getReturnType());
    }

    @Test
    void mockBehaviour_findByUserId_andUpdate() {
        PcmUserSessionInfoRepository repo = mock(PcmUserSessionInfoRepository.class);
        PcmUserSessionInfo s = new PcmUserSessionInfo("u", "sid", new Timestamp(0));
        when(repo.findByUserId("u")).thenReturn(Optional.of(s));
        when(repo.existsByUserId("u")).thenReturn(true);
        when(repo.updateLastAccessTime("u", s.getLastUpdateOn())).thenReturn(1);

        assertTrue(repo.findByUserId("u").isPresent());
        assertTrue(repo.existsByUserId("u"));
        assertEquals(1, repo.updateLastAccessTime("u", s.getLastUpdateOn()));
        repo.deleteByUserId("u");
        verify(repo).deleteByUserId("u");
    }
}
