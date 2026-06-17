/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.commodityProfile.repository.CommodityProfileRepository;
import com.scplatform.pcm.commodityProfile.repository.RoleCommodityProfileMappingRepository;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.service.PcmCostTypeService;
import com.scplatform.pcm.item.service.ItemService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CommodityProfileServiceTest {

    @Mock private PcmConfigUtil pcmConfigUtil;
    @Mock private CommodityProfileRepository commodityProfileRepository;
    @Mock private PcmCostTypeService pcmCostTypeService;
    @Mock private BusinessEntityService businessEntityService;
    @Mock private ItemService itemService;
    @Mock private RoleCommodityProfileMappingRepository roleCommodityProfileMappingRepository;

    @InjectMocks private CommodityProfileService service;

    @BeforeEach
    void resetDefaultConfig() {
        when(pcmConfigUtil.getString(eq("pcm.commodityProfile.user.mapping.type"), anyString()))
                .thenReturn("user");
    }

    @Test
    void getCommoditytProfileFilterList_returnsListFromConfigAsSet() {
        when(pcmConfigUtil.getList(eq("pcm.commodityProfile.filter.list"), anyList()))
                .thenReturn(Arrays.asList("a", "b", "a"));

        Set<String> result = service.getCommoditytProfileFilterList();

        assertEquals(2, result.size());
        assertTrue(result.contains("a"));
        assertTrue(result.contains("b"));
    }

    @Test
    void isCompanyItemTypeExist_byBusinessEntity_returnsTrueWhenNamesPresent() {
        when(businessEntityService.findDistinctBusinessEntityNames("BE1"))
                .thenReturn(Arrays.asList("BE1"));
        assertTrue(service.isCompanyItemTypeExist("BE1", "businessEntity"));
    }

    @Test
    void isCompanyItemTypeExist_byBusinessEntity_returnsFalseWhenNoNames() {
        when(businessEntityService.findDistinctBusinessEntityNames("BE1"))
                .thenReturn(Collections.emptyList());
        assertFalse(service.isCompanyItemTypeExist("BE1", "businessEntity"));
    }

    @Test
    void isCompanyItemTypeExist_byDataSource_returnsTrueWhenItemCountNonZero() {
        when(itemService.countItemByDataSource("MCM")).thenReturn(5L);
        assertTrue(service.isCompanyItemTypeExist("MCM", "dataSource"));
    }

    @Test
    void isCompanyItemTypeExist_byDataSource_returnsFalseWhenItemCountZero() {
        when(itemService.countItemByDataSource("MCM")).thenReturn(0L);
        assertFalse(service.isCompanyItemTypeExist("MCM", "dataSource"));
    }

    @Test
    void getSQLTypePropertyValue_camelCaseConvertsToSnakeCaseUpper() {
        when(pcmConfigUtil.getString(eq("pcm.commodityProfile.companyItemType.field"), eq("dataSource")))
                .thenReturn("dataSource");
        // First uppercase is prefixed with "_" then kept as-is. lowercase remains.
        // dataSource -> data_Source
        assertEquals("data_Source", service.getSQLTypePropertyValue());
    }

    @Test
    void getSQLTypePropertyValue_singleWordRemainsUnchanged() {
        when(pcmConfigUtil.getString(eq("pcm.commodityProfile.companyItemType.field"), eq("dataSource")))
                .thenReturn("type");
        assertEquals("type", service.getSQLTypePropertyValue());
    }

    @Test
    void deleteCommodityProfileByUserKey_userMapping_callsUserDeleteAndProfileDelete() {
        List<String> keys = Arrays.asList("1~ProfA", "2~ProfB");
        when(commodityProfileRepository.getCommodityProfileCountByNameCriteria("ProfA")).thenReturn(0L);
        when(commodityProfileRepository.getCommodityProfileCountByNameCriteria("ProfB")).thenReturn(0L);
        when(commodityProfileRepository.deleteByProfileIdIn(Arrays.asList(1L, 2L))).thenReturn(2L);

        long deleted = service.deleteCommodityProfileByUserKey(keys);

        assertEquals(2L, deleted);
        verify(commodityProfileRepository).deleteUserCommodityProfileMappingByNames(Arrays.asList("ProfA", "ProfB"));
        verify(commodityProfileRepository).deleteByProfileIdIn(Arrays.asList(1L, 2L));
        verify(roleCommodityProfileMappingRepository, never()).deleteByCommodityProfile_ProfileIdIn(any());
    }

    @Test
    void deleteCommodityProfileByUserKey_userMapping_skipsUserDeleteWhenAllProfilesShared() {
        List<String> keys = Arrays.asList("1~ProfA");
        // Returning > 0 means count is non-zero so name is not added to delete list
        when(commodityProfileRepository.getCommodityProfileCountByNameCriteria("ProfA")).thenReturn(1L);
        when(commodityProfileRepository.deleteByProfileIdIn(Arrays.asList(1L))).thenReturn(1L);

        long deleted = service.deleteCommodityProfileByUserKey(keys);

        assertEquals(1L, deleted);
        verify(commodityProfileRepository, never()).deleteUserCommodityProfileMappingByNames(anyList());
        verify(commodityProfileRepository).deleteByProfileIdIn(Arrays.asList(1L));
    }

    @Test
    void deleteCommodityProfileByUserKey_roleMapping_callsRoleDelete() {
        when(pcmConfigUtil.getString(eq("pcm.commodityProfile.user.mapping.type"), anyString()))
                .thenReturn("role");
        List<String> keys = Arrays.asList("1~ProfA");
        when(commodityProfileRepository.getCommodityProfileCountByNameCriteria("ProfA")).thenReturn(0L);
        when(commodityProfileRepository.deleteByProfileIdIn(Arrays.asList(1L))).thenReturn(1L);

        long deleted = service.deleteCommodityProfileByUserKey(keys);

        assertEquals(1L, deleted);
        verify(roleCommodityProfileMappingRepository).deleteByCommodityProfile_ProfileIdIn(Arrays.asList(1L));
        verify(commodityProfileRepository, never()).deleteUserCommodityProfileMappingByNames(anyList());
        verify(commodityProfileRepository).deleteByProfileIdIn(Arrays.asList(1L));
    }

    @Test
    void deleteCommodityProfileByUserKey_emptyKeys_returnsZero() {
        long deleted = service.deleteCommodityProfileByUserKey(Collections.emptyList());
        assertEquals(0L, deleted);
        verify(commodityProfileRepository, never()).deleteByProfileIdIn(anyList());
    }

    @Test
    void deleteCommodityProfileMapping_userMapping_buildsAndDeletesPerUser() {
        // user mapping: each key parts: userKey~profileName
        List<String> keys = Arrays.asList("10~ProfA", "10~ProfB", "20~ProfA");
        when(commodityProfileRepository.deleteUserCommodityProfileMappingByUserKeyAndProfileNames(anyLong(), anySet()))
                .thenReturn(1);

        long result = service.deleteCommodityProfileMapping(keys);

        // Per source: returns 0 in the user-mapping branch
        assertEquals(0L, result);
        verify(commodityProfileRepository, times(2))
                .deleteUserCommodityProfileMappingByUserKeyAndProfileNames(anyLong(), anySet());
    }

    @Test
    void deleteCommodityProfileMapping_roleMapping_callsRoleDeleteAndReturnsCount() {
        when(pcmConfigUtil.getString(eq("pcm.commodityProfile.user.mapping.type"), anyString()))
                .thenReturn("role");
        List<String> keys = Arrays.asList("1~ProfA~3", "2~ProfB~4");
        when(roleCommodityProfileMappingRepository.deleteRoleProfileMapping(1L, "ProfA", 3L)).thenReturn(1);
        when(roleCommodityProfileMappingRepository.deleteRoleProfileMapping(2L, "ProfB", 4L)).thenReturn(1);

        long result = service.deleteCommodityProfileMapping(keys);

        assertEquals(2L, result);
        verify(roleCommodityProfileMappingRepository).deleteRoleProfileMapping(1L, "ProfA", 3L);
        verify(roleCommodityProfileMappingRepository).deleteRoleProfileMapping(2L, "ProfB", 4L);
    }

    @Test
    void deleteCommodityProfileMapping_roleMapping_skipsMalformedKeys() {
        when(pcmConfigUtil.getString(eq("pcm.commodityProfile.user.mapping.type"), anyString()))
                .thenReturn("role");
        List<String> keys = Arrays.asList("1~ProfA", "2~ProfB~4"); // first malformed (only 2 parts)
        when(roleCommodityProfileMappingRepository.deleteRoleProfileMapping(2L, "ProfB", 4L)).thenReturn(1);

        long result = service.deleteCommodityProfileMapping(keys);

        assertEquals(1L, result);
        verify(roleCommodityProfileMappingRepository, times(1))
                .deleteRoleProfileMapping(anyLong(), anyString(), anyLong());
    }
}
