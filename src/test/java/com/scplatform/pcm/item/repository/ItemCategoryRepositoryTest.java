/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.repository;

import com.scplatform.pcm.item.entity.ItemCategory;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemCategoryRepositoryTest {

    @Test
    void testExtendsJpaRepositoryWithCorrectGenerics() {
        assertTrue(JpaRepository.class.isAssignableFrom(ItemCategoryRepository.class));

        // Verify generic type args: <ItemCategory, Long>
        Type[] genericInterfaces = ItemCategoryRepository.class.getGenericInterfaces();
        boolean foundJpa = false;
        for (Type t : genericInterfaces) {
            if (t instanceof ParameterizedType pt
                    && pt.getRawType().equals(JpaRepository.class)) {
                foundJpa = true;
                Type[] args = pt.getActualTypeArguments();
                assertEquals(ItemCategory.class, args[0]);
                assertEquals(Long.class, args[1]);
            }
        }
        assertTrue(foundJpa, "ItemCategoryRepository must extend JpaRepository");
    }

    @Test
    void testStandardJpaMethodsAvailableOnMock() {
        ItemCategoryRepository repo = mock(ItemCategoryRepository.class);
        ItemCategory cat = new ItemCategory(1L);
        when(repo.findById(1L)).thenReturn(Optional.of(cat));
        when(repo.save(cat)).thenReturn(cat);

        assertSame(cat, repo.findById(1L).orElse(null));
        assertSame(cat, repo.save(cat));
    }
}
