/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.repository;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersRepositoryTest {

    @Test
    void isAnnotatedAsRepositoryAndExtendsJpa() {
        assertTrue(JpaRepository.class.isAssignableFrom(UsersRepository.class));
        assertNotNull(UsersRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void multipleQueryAnnotatedMethodsDeclareJpqlOrNative() throws Exception {
        String[] names = {
                "findAllByUserIdIgnoreCase",
                "findByUserId",
                "findUserByUserId",
                "findActiveUserByUserIdInternal",
                "findCommodityUserByUserIdInternal",
                "updateUserLastAccessDate",
                "findByBusinessEntityKey",
                "findEnabledUsersByBusinessEntity",
                "searchUsers",
                "updateLastAccessDate",
                "updateUserLastAccess",
                "findUserByKey",
                "findUserByUserIdWithRelationships",
                "getUsersWithRole",
                "findUsersForBusinessByKey",
                "findUsersForBusinessByKeyPaginated",
                "findUsersByBusinessEntityKeyAndEnabledAndSearchText",
                "findUsersByBusinessEntityKeyAndEnabledAndSearchTextPaginated"
        };
        for (Method m : UsersRepository.class.getDeclaredMethods()) {
            for (String n : names) {
                if (m.getName().equals(n)) {
                    assertNotNull(m.getAnnotation(Query.class),
                            "missing @Query on " + m.getName());
                }
            }
        }
    }

    @Test
    void modifyingAnnotationsPresentOnUpdateQueries() throws Exception {
        Method[] candidates = UsersRepository.class.getDeclaredMethods();
        long modCount = 0;
        for (Method m : candidates) {
            if (m.getAnnotation(Modifying.class) != null) modCount++;
        }
        assertTrue(modCount >= 2, "expected at least 2 @Modifying methods, found " + modCount);
    }

    @Test
    void findActiveUserByUserId_defaultMethod_returnsNullForNullInput() {
        UsersRepository repo = mock(UsersRepository.class);
        when(repo.findActiveUserByUserId(any())).thenCallRealMethod();
        assertNull(repo.findActiveUserByUserId(null));
    }

    @Test
    void findActiveUserByUserId_returnsFirstResultOrNull() {
        UsersRepository repo = mock(UsersRepository.class);
        Users u = new Users(1L);
        when(repo.findActiveUserByUserIdInternal("x")).thenReturn(List.of(u));
        when(repo.findActiveUserByUserIdInternal("y")).thenReturn(Collections.emptyList());
        when(repo.findActiveUserByUserId(anyString())).thenCallRealMethod();

        assertSame(u, repo.findActiveUserByUserId("x"));
        assertNull(repo.findActiveUserByUserId("y"));
    }

    @Test
    void findCommodityUserByUserId_defaultMethod() {
        UsersRepository repo = mock(UsersRepository.class);
        Users u = new Users(2L);
        when(repo.findCommodityUserByUserIdInternal("x")).thenReturn(List.of(u));
        when(repo.findCommodityUserByUserIdInternal("y")).thenReturn(Collections.emptyList());
        when(repo.findCommodityUserByUserId(any())).thenCallRealMethod();

        assertNull(repo.findCommodityUserByUserId(null));
        assertSame(u, repo.findCommodityUserByUserId("x"));
        assertNull(repo.findCommodityUserByUserId("y"));
    }

    @Test
    void mockBehaviour_simpleAccessors() {
        UsersRepository repo = mock(UsersRepository.class);
        Users u = new Users(7L);
        when(repo.findByUserId("u")).thenReturn(Optional.of(u));
        when(repo.findByEmailAddress("e@x")).thenReturn(Optional.of(u));
        when(repo.findByIsEnabledTrue()).thenReturn(List.of(u));
        when(repo.findByBusinessEntityKey(1L)).thenReturn(List.of(u));
        when(repo.findUserByKey(7L)).thenReturn(u);
        when(repo.findByUserKey(7L)).thenReturn(u);
        when(repo.searchUsers("q")).thenReturn(List.of(u));
        when(repo.findAllByUserIdIgnoreCase("u")).thenReturn(List.of(u));

        assertTrue(repo.findByUserId("u").isPresent());
        assertTrue(repo.findByEmailAddress("e@x").isPresent());
        assertEquals(1, repo.findByIsEnabledTrue().size());
        assertEquals(1, repo.findByBusinessEntityKey(1L).size());
        assertSame(u, repo.findUserByKey(7L));
        assertSame(u, repo.findByUserKey(7L));
        assertEquals(1, repo.searchUsers("q").size());
        assertEquals(1, repo.findAllByUserIdIgnoreCase("u").size());
    }

    @Test
    void mockBehaviour_paginatedSearch() {
        UsersRepository repo = mock(UsersRepository.class);
        Users u = new Users(7L);
        Pageable pg = PageRequest.of(0, 10);
        Page<Users> page = new PageImpl<>(List.of(u));
        when(repo.findUsersForBusinessByKeyPaginated(1L, "q", pg)).thenReturn(page);
        when(repo.findUsersByBusinessEntityKeyAndEnabledAndSearchTextPaginated(1L, true, "q", pg))
                .thenReturn(page);

        assertEquals(1, repo.findUsersForBusinessByKeyPaginated(1L, "q", pg).getTotalElements());
        assertEquals(1, repo.findUsersByBusinessEntityKeyAndEnabledAndSearchTextPaginated(1L, true, "q", pg)
                .getTotalElements());
    }

    @Test
    void mockBehaviour_roleAndUpdateMethods() {
        UsersRepository repo = mock(UsersRepository.class);
        Role role = new Role();
        Users u = new Users(7L);
        when(repo.getUsersWithRole(role)).thenReturn(List.of(u));
        when(repo.updateUserLastAccessDate(7L)).thenReturn(1);
        when(repo.updateUserLastAccess(7L)).thenReturn(1);

        assertEquals(1, repo.getUsersWithRole(role).size());
        assertEquals(1, repo.updateUserLastAccessDate(7L));
        assertEquals(1, repo.updateUserLastAccess(7L));
        repo.updateLastAccessDate(7L, new Timestamp(0));
        verify(repo).updateLastAccessDate(eq(7L), any(Timestamp.class));
    }
}
