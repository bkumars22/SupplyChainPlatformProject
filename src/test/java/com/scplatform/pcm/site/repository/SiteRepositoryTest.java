/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SiteRepositoryTest {

    @Test
    void extendsJpaRepository() {
        assertTrue(JpaRepository.class.isAssignableFrom(SiteRepository.class));
    }

    @Test
    void hasRepositoryAnnotation() {
        assertNotNull(SiteRepository.class.getAnnotation(Repository.class));
    }

    @Test
    void findSitesForBusiness_hasJpqlQuery() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("findSitesForBusiness",
                com.scplatform.pcm.businessEntity.entity.BusinessEntity.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertFalse(q.nativeQuery());
        assertTrue(q.value().contains("FROM Site"));
        assertTrue(q.value().contains("businessEntity"));
    }

    @Test
    void findRegion_filtersByRegionType() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("findRegion");
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("REGION"));
    }

    @Test
    void getAllSites_filtersDeletedAndCurrent() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("getAllSites");
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("isDeleted"));
        assertTrue(q.value().contains("isCurrent"));
    }

    @Test
    void findCCNSiteByDescription_filtersByCCN() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("findCCNSiteByDescription", String.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("CCN"));
    }

    @Test
    void findSitesByRegion_filtersByVisibleFlag() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("findSitesByRegion", String.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("tamVisibleFlag"));
    }

    @Test
    void getEnterpriseRegionList_filtersByVisibleFlag() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("getEnterpriseRegionList", Long.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("tamVisibleFlag"));
    }

    @Test
    void getEnterpriseRegionListXlob_doesNotFilterByVisibleFlag() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("getEnterpriseRegionListXlob", Long.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertFalse(q.value().contains("tamVisibleFlag = true"));
    }

    @Test
    void findTopSiteForBusiness_filtersNullParentSite() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("findTopSiteForBusiness",
                com.scplatform.pcm.businessEntity.entity.BusinessEntity.class, String.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("parentSite IS NULL"));
    }

    @Test
    void findSiteDescriptionsBySiteKeys_returnsListOfStrings() throws NoSuchMethodException {
        Method m = SiteRepository.class.getMethod("findSiteDescriptionsBySiteKeys", java.util.List.class);
        Query q = m.getAnnotation(Query.class);
        assertNotNull(q);
        assertTrue(q.value().contains("siteDescription"));
    }
}
