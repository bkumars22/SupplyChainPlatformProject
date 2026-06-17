/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.scplatform.pcm.bom.entity.PcmDefectType;
import com.scplatform.pcm.bom.repository.PcmDefectTypeRepository;

@ExtendWith(MockitoExtension.class)
class PcmDefectServiceTest {

    @Mock
    private PcmDefectTypeRepository defectTypeRepository;

    @InjectMocks
    private PcmDefectService service;

    @Test
    void getDefectTypes_returnsListFromRepository() {
        PcmDefectType d = new PcmDefectType();
        d.setDefectName("Yield");
        List<PcmDefectType> list = Arrays.asList(d);
        when(defectTypeRepository.getDefectTypes()).thenReturn(list);

        List<PcmDefectType> result = service.getDefectTypes();
        assertSame(list, result);
        assertEquals(1, result.size());
    }

    @Test
    void hasDefectTypes_returnsTrueWhenNonEmpty() {
        when(defectTypeRepository.getDefectTypes()).thenReturn(Arrays.asList(new PcmDefectType()));
        assertTrue(service.hasDefectTypes());
    }

    @Test
    void hasDefectTypes_returnsFalseWhenEmpty() {
        when(defectTypeRepository.getDefectTypes()).thenReturn(Collections.emptyList());
        assertFalse(service.hasDefectTypes());
    }
}
