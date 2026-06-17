/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.repository;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemCategory;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ItemRepositoryTest {

    @Test
    void testIsRepositoryAndExtendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(ItemRepository.class));
        assertNotNull(ItemRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void testQueryAnnotationsPresentOnAllMethods() throws Exception {
        Method[] methods = {
                ItemRepository.class.getMethod("findByItemNumberAndBusinessEntity",
                        String.class, String.class, String.class, BusinessEntity.class),
                ItemRepository.class.getMethod("findByItemNumberAndItemIdAndVersionAndRevisionAndItemTypeAndBusinessEntity",
                        String.class, String.class, String.class, String.class, String.class, BusinessEntity.class),
                ItemRepository.class.getMethod("getItemByKeyInternal", Long.class),
                ItemRepository.class.getMethod("countDistinctDataSourceByType", String.class),
                ItemRepository.class.getMethod("countItemByDataSource", String.class),
                ItemRepository.class.getMethod("findDistinctDataSources", String.class)
        };
        for (Method m : methods) {
            assertNotNull(m.getAnnotation(Query.class), "missing @Query on " + m.getName());
        }
        // native-query method should be flagged nativeQuery=true
        Method native_ = ItemRepository.class.getMethod("countItemByDataSource", String.class);
        assertTrue(native_.getAnnotation(Query.class).nativeQuery(),
                "countItemByDataSource should use native query");
    }

    @Test
    void testGetItemByKeyDefaultMethodDelegates() {
        ItemRepository repo = mock(ItemRepository.class);
        Item item = new Item();
        when(repo.getItemByKeyInternal(eq(123L))).thenReturn(item);
        when(repo.getItemByKey(123L)).thenCallRealMethod();

        Item actual = repo.getItemByKey(123L);
        assertSame(item, actual);
        verify(repo).getItemByKeyInternal(123L);
    }

    @Test
    void testFindByItemNumberAndBusinessEntity_MockBehavior() {
        ItemRepository repo = mock(ItemRepository.class);
        BusinessEntity be = mock(BusinessEntity.class);
        Item item = new Item();
        when(repo.findByItemNumberAndBusinessEntity("PN", "ID", "I", be))
                .thenReturn(Optional.of(item));
        Optional<Item> result = repo.findByItemNumberAndBusinessEntity("PN", "ID", "I", be);
        assertTrue(result.isPresent());
        assertSame(item, result.get());
    }

    @Test
    void testCountAndDistinctMethodsReturnTypes() throws Exception {
        // sanity: signatures are stable
        assertEquals(long.class, ItemRepository.class.getMethod(
                "countDistinctDataSourceByType", String.class).getReturnType());
        assertEquals(long.class, ItemRepository.class.getMethod(
                "countItemByDataSource", String.class).getReturnType());
        assertEquals(List.class, ItemRepository.class.getMethod(
                "findDistinctDataSources", String.class).getReturnType());
    }
}
