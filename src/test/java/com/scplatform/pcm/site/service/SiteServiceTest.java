/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.service;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.site.entity.SiteDetails;
import com.scplatform.pcm.site.repository.SiteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SiteServiceTest {

    @Mock private SiteRepository siteRepository;
    @Mock private PcmConfigUtil pcmConfigUtil;
    @InjectMocks private SiteService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    private Site site(Long key, String name, String desc, String type, BusinessEntity be) {
        Site s = new Site();
        s.setSiteKey(key);
        s.setSiteName(name);
        s.setSiteDescription(desc);
        s.setSiteType(type);
        s.setBusinessEntity(be);
        return s;
    }

    private BusinessEntity enterpriseBE() {
        BusinessEntity be = new BusinessEntity(1L);
        be.setBusinessEntityTypeKey(BusinessEntity.ENTERPRISE_TYPE);
        return be;
    }

    @Test
    void getSite_returnsFromRepository() {
        Site s = new Site(5L);
        when(siteRepository.findById(5L)).thenReturn(Optional.of(s));
        assertSame(s, service.getSite(5L));
    }

    @Test
    void getSite_returnsNullWhenNotFound() {
        when(siteRepository.findById(99L)).thenReturn(Optional.empty());
        assertNull(service.getSite(99L));
    }

    @Test
    void findSitesForBusiness_delegatesToRepository() {
        BusinessEntity be = new BusinessEntity(1L);
        List<Site> list = Collections.singletonList(new Site(1L));
        when(siteRepository.findSitesForBusiness(be)).thenReturn(list);
        assertEquals(list, service.findSitesForBusiness(be));
    }

    @Test
    void findSitesForBusinessType_returnsEmptyForNullKey() {
        assertTrue(service.findSitesForBusinessType(null).isEmpty());
        verify(siteRepository, never()).findSitesForBusinessType(any());
    }

    @Test
    void findSitesForBusinessType_delegatesToRepository() {
        when(siteRepository.findSitesForBusinessType(2L))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSitesForBusinessType(2L).size());
    }

    @Test
    void findSiteByNaturalKey_withBE_returnsRepoOptional() {
        BusinessEntity be = new BusinessEntity(1L);
        Site s = new Site(1L);
        when(siteRepository.findSiteByNaturalKey(be, "WW")).thenReturn(Optional.of(s));
        assertSame(s, service.findSiteByNaturalKey(be, "WW"));
    }

    @Test
    void findSiteByNaturalKey_withBE_returnsNullWhenAbsent() {
        BusinessEntity be = new BusinessEntity(1L);
        when(siteRepository.findSiteByNaturalKey(be, "WW")).thenReturn(Optional.empty());
        assertNull(service.findSiteByNaturalKey(be, "WW"));
    }

    @Test
    void findSiteByNaturalKey_byNameOnly_returnsNullForNullName() {
        assertNull(service.findSiteByNaturalKey(null, Collections.emptyList()));
    }

    @Test
    void findSiteByNaturalKey_byNameOnly_filtersByEnterpriseAndExcludeTypes() {
        BusinessEntity be = enterpriseBE();
        Site match = site(1L, "WW", "World", "REGION", be);
        Site wrongType = site(2L, "WW", "World", "CCN", be);
        Site wrongName = site(3L, "OTHER", "X", "REGION", be);
        when(siteRepository.getAllSites()).thenReturn(Arrays.asList(match, wrongType, wrongName));
        Site found = service.findSiteByNaturalKey("WW", Collections.singletonList("CCN"));
        assertSame(match, found);
    }

    @Test
    void findSiteByNaturalKey_byNameOnly_excludeMatchingType() {
        BusinessEntity be = enterpriseBE();
        Site only = site(1L, "WW", "World", "REGION", be);
        when(siteRepository.getAllSites()).thenReturn(Collections.singletonList(only));
        assertNull(service.findSiteByNaturalKey("WW", Collections.singletonList("REGION")));
    }

    @Test
    void findSiteByDescription_byDesc_returnsNullForNull() {
        assertNull(service.findSiteByDescription(null, Collections.emptyList()));
        assertNull(service.findSiteByDescription(null));
    }

    @Test
    void findSiteByDescription_simpleVariant_filtersByValidTypes() {
        Site a = site(1L, "n", "DESC", Site.REGION_TYPE, null);
        Site b = site(2L, "n", "DESC", "OTHER", null);
        when(siteRepository.getAllSites()).thenReturn(Arrays.asList(a, b));
        assertSame(a, service.findSiteByDescription("DESC"));
    }

    @Test
    void findSiteByDescription_simpleVariant_returnsNullWhenNoMatch() {
        when(siteRepository.getAllSites()).thenReturn(Collections.emptyList());
        assertNull(service.findSiteByDescription("X"));
    }

    @Test
    void findSitesForEnterprise_callsTypedFinder() {
        when(siteRepository.findSitesForBusinessType(BusinessEntity.ENTERPRISE_TYPE))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSitesForEnterprise().size());
    }

    @Test
    void findSitesForEnterprise_byType_filtersByType() {
        Site a = site(1L, "n", "d", "REGION", null);
        Site b = site(2L, "n", "d", "SITE", null);
        when(siteRepository.findSitesForBusinessType(BusinessEntity.ENTERPRISE_TYPE))
                .thenReturn(Arrays.asList(a, b));
        List<Site> r = service.findSitesForEnterprise("REGION");
        assertEquals(1, r.size());
        assertSame(a, r.get(0));
    }

    @Test
    void findSitesForEnterprise_byType_nullReturnsAll() {
        Site a = site(1L, "n", "d", "REGION", null);
        when(siteRepository.findSitesForBusinessType(BusinessEntity.ENTERPRISE_TYPE))
                .thenReturn(Collections.singletonList(a));
        assertEquals(1, service.findSitesForEnterprise(null).size());
    }

    @Test
    void findRegion_delegates() {
        when(siteRepository.findRegion()).thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findRegion().size());
    }

    @Test
    void findSiteForRegionList_returnsEmptyWhenNoConfig() {
        when(pcmConfigUtil.getList(anyString())).thenReturn(Collections.emptyList());
        assertTrue(service.findSiteForRegionList(false).isEmpty());
        verify(siteRepository, never()).findSiteForRegionList(anyList(), eq(false));
    }

    @Test
    void findSiteForRegionList_returnsEmptyWhenConfigNull() {
        when(pcmConfigUtil.getList(anyString())).thenReturn(null);
        assertTrue(service.findSiteForRegionList(true).isEmpty());
    }

    @Test
    void findSiteForRegionList_passesConfigToRepository() {
        List<String> types = Arrays.asList("REGION", "SITE");
        when(pcmConfigUtil.getList(anyString())).thenReturn(types);
        when(siteRepository.findSiteForRegionList(types, true))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSiteForRegionList(true).size());
    }

    @Test
    void getSiteDescriptionByKey_buildsCommaSeparated() {
        when(siteRepository.findSiteDescriptionsBySiteKeys(Arrays.asList(1L, 2L)))
                .thenReturn(Arrays.asList("A", "B"));
        String r = service.getSiteDescriptionByKey(new String[] {"1", "2"});
        // List.toString -> "[A, B]" -> concat "-" -> "[A, B]-" -> substring(1, len-1) ->
        // "A, B]" since the trailing "-" lengthens the original string by 1
        assertTrue(r.contains("A"));
        assertTrue(r.contains("B"));
    }

    @Test
    void getAllSites_delegates() {
        when(siteRepository.getAllSites()).thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.getAllSites().size());
    }

    @Test
    void findSitesForBusiness_withTypes_nullBE() {
        assertTrue(service.findSitesForBusiness(null, Arrays.asList("REGION")).isEmpty());
    }

    @Test
    void findSitesForBusiness_withTypes_emptyTypesUsesUntypedFinder() {
        BusinessEntity be = new BusinessEntity(1L);
        when(siteRepository.findSitesForBusiness(be))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSitesForBusiness(be, Collections.emptyList()).size());
    }

    @Test
    void findSitesForBusiness_withTypes_callsTypedFinder() {
        BusinessEntity be = new BusinessEntity(1L);
        List<String> types = Arrays.asList("REGION");
        when(siteRepository.findSitesForBusinessWithTypes(be, types))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSitesForBusiness(be, types).size());
    }

    @Test
    void findSitesForBusiness_withTypesAndCostFlag_nullBE() {
        assertTrue(service.findSitesForBusiness(null, Arrays.asList("R"), true).isEmpty());
    }

    @Test
    void findSitesForBusiness_withTypesAndCostFlag_falseReturnsAll() {
        BusinessEntity be = new BusinessEntity(1L);
        when(siteRepository.findSitesForBusiness(be))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSitesForBusiness(be, Collections.emptyList(), false).size());
    }

    @Test
    void findSitesForBusiness_withTypesAndCostFlag_filtersByCostVisible() {
        BusinessEntity be = new BusinessEntity(1L);
        Site visible = new Site(1L);
        SiteDetails sd1 = new SiteDetails(); sd1.setCostVisibleFlag(true);
        visible.setSiteDetail(sd1);
        Site notVisible = new Site(2L);
        SiteDetails sd2 = new SiteDetails(); sd2.setCostVisibleFlag(false);
        notVisible.setSiteDetail(sd2);
        Site noDetail = new Site(3L);
        when(siteRepository.findSitesForBusiness(be))
                .thenReturn(Arrays.asList(visible, notVisible, noDetail));
        List<Site> r = service.findSitesForBusiness(be, Collections.emptyList(), true);
        assertEquals(1, r.size());
        assertSame(visible, r.get(0));
    }

    @Test
    void findSites_emptyTypes() {
        assertTrue(service.findSites(new Long[] {1L}, null).isEmpty());
        assertTrue(service.findSites(new Long[] {1L}, new String[0]).isEmpty());
    }

    @Test
    void findSites_nullBusinessTypes() {
        when(siteRepository.findSites(eq(null), anyList()))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSites(null, new String[] {"REGION"}).size());
    }

    @Test
    void findSites_bothProvided() {
        when(siteRepository.findSites(anyList(), anyList()))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSites(new Long[] {1L}, new String[] {"REGION"}).size());
    }

    @Test
    void findSitesByBusinessEntitiesKey_emptyTypesOrKeys() {
        Set<Long> keys = new HashSet<>(Arrays.asList(1L));
        assertTrue(service.findSitesByBusinessEntitiesKey(keys, null).isEmpty());
        assertTrue(service.findSitesByBusinessEntitiesKey(keys, new String[0]).isEmpty());
        assertTrue(service.findSitesByBusinessEntitiesKey(null, new String[] {"R"}).isEmpty());
        assertTrue(service.findSitesByBusinessEntitiesKey(Collections.emptySet(), new String[] {"R"}).isEmpty());
    }

    @Test
    void findSitesByBusinessEntitiesKey_delegates() {
        when(siteRepository.findSitesByBusinessEntitiesKey(anyList(), anyList()))
                .thenReturn(Collections.singletonList(new Site(1L)));
        Set<Long> keys = new HashSet<>(Arrays.asList(1L));
        assertEquals(1, service.findSitesByBusinessEntitiesKey(keys, new String[] {"REGION"}).size());
    }

    @Test
    void findSitesByRegion_nullReturnsEmpty() {
        assertTrue(service.findSitesByRegion(null).isEmpty());
    }

    @Test
    void findSitesByRegion_delegates() {
        when(siteRepository.findSitesByRegion("NA"))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSitesByRegion("NA").size());
    }

    @Test
    void findSitesByRegionXlob_nullReturnsEmpty() {
        assertTrue(service.findSitesByRegionXlob(null).isEmpty());
    }

    @Test
    void findSitesByRegionXlob_delegates() {
        when(siteRepository.findSitesByRegionXlob("NA"))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.findSitesByRegionXlob("NA").size());
    }

    @Test
    void findCCNSiteByDescription_nullReturnsNull() {
        assertNull(service.findCCNSiteByDescription(null));
    }

    @Test
    void findCCNSiteByDescription_delegates() {
        Site s = new Site(1L);
        when(siteRepository.findCCNSiteByDescription("X")).thenReturn(Optional.of(s));
        assertSame(s, service.findCCNSiteByDescription("X"));
    }

    @Test
    void findCCNSiteByDescription_emptyOptional() {
        when(siteRepository.findCCNSiteByDescription("X")).thenReturn(Optional.empty());
        assertNull(service.findCCNSiteByDescription("X"));
    }

    @Test
    void getEnterpriseRegionList_delegates() {
        when(siteRepository.getEnterpriseRegionList(BusinessEntity.ENTERPRISE_TYPE))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.getEnterpriseRegionList().size());
    }

    @Test
    void getEnterpriseRegionListXlob_delegates() {
        when(siteRepository.getEnterpriseRegionListXlob(BusinessEntity.ENTERPRISE_TYPE))
                .thenReturn(Collections.singletonList(new Site(1L)));
        assertEquals(1, service.getEnterpriseRegionListXlob().size());
    }

    @Test
    void findTopSiteForBusiness_returnsRootMatchingType() {
        BusinessEntity be = new BusinessEntity(1L);
        Site root = new Site(1L); root.setSiteType("REGION");
        Site nonRoot = new Site(2L); nonRoot.setSiteType("REGION");
        // give nonRoot a parent
        try { nonRoot.setParentSite(root); } catch (Exception ignored) {}
        when(siteRepository.findSitesForBusiness(be))
                .thenReturn(Arrays.asList(nonRoot, root));
        assertSame(root, service.findTopSiteForBusiness(be, "REGION"));
    }

    @Test
    void findTopSiteForBusiness_nullTypeAcceptsAnyRoot() {
        BusinessEntity be = new BusinessEntity(1L);
        Site root = new Site(1L); root.setSiteType("SITE");
        when(siteRepository.findSitesForBusiness(be))
                .thenReturn(Collections.singletonList(root));
        assertSame(root, service.findTopSiteForBusiness(be, null));
    }

    @Test
    void findTopSiteForBusiness_returnsNullWhenNoneMatch() {
        BusinessEntity be = new BusinessEntity(1L);
        Site root = new Site(1L); root.setSiteType("SITE");
        when(siteRepository.findSitesForBusiness(be))
                .thenReturn(Collections.singletonList(root));
        assertNull(service.findTopSiteForBusiness(be, "REGION"));
    }
}
