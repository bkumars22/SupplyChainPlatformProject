/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.site.repository;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.site.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Site persistence.
 * Segregated from BomUtil for module-owned data access.
 */
@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {

    /**
     * Find all sites for a business entity ordered by description.
     * Maps to: BomUtil.findSitesForBusiness(BusinessEntity)
     */
    @Query("SELECT s FROM Site s WHERE s.businessEntity = :businessEntity ORDER BY s.siteDescription ASC, s.businessEntity ASC")
    List<Site> findSitesForBusiness(@Param("businessEntity") BusinessEntity businessEntity);

    /**
     * Find all sites for business type, ordered.
     * Maps to: BomUtil.findSitesForBusinessType(Long)
     */
    @Query("SELECT s FROM Site s WHERE s.businessEntity.businessEntityTypeKey = :businessEntityTypeKey " +
           "ORDER BY s.parentSite DESC, s.siteDescription ASC")
    List<Site> findSitesForBusinessType(@Param("businessEntityTypeKey") Long businessEntityTypeKey);

    /**
     * Find all region sites.
     * Maps to: BomUtil.findRegion()
     */
    @Query("SELECT s FROM Site s WHERE s.siteType = 'REGION' ORDER BY s.parentSite DESC, s.siteDescription ASC")
    List<Site> findRegion();

       /**
        * Find site descriptions by site keys.
        * Maps to: BomUtil.getSiteDescriptionByKey(String[])
        */
       @Query("SELECT s.siteDescription FROM Site s WHERE s.siteKey IN :siteKeys")
       List<String> findSiteDescriptionsBySiteKeys(@Param("siteKeys") List<Long> siteKeys);

    /**
     * Find sites used for region lists by configured site types.
     * Maps to: BomUtil.findSiteForRegionList()
     */
    @Query("SELECT s FROM Site s WHERE s.siteType IN :siteTypes ORDER BY s.siteDescription ASC")
    List<Site> findSiteForRegionList(@Param("siteTypes") List<String> siteTypes);

    /**
     * Find sites used for region lists by configured site types with optional cost visibility filter.
     * Maps to: BomUtil.findSiteForRegionList(boolean)
     */
    @Query("SELECT s FROM Site s LEFT JOIN s.siteDetail sd " +
           "WHERE s.siteType IN :siteTypes " +
           "AND (:hideByCostVisibleFlag = false OR sd.costVisibleFlag = true) " +
           "ORDER BY s.siteDescription ASC")
    List<Site> findSiteForRegionList(@Param("siteTypes") List<String> siteTypes,
                                     @Param("hideByCostVisibleFlag") boolean hideByCostVisibleFlag);

    /**
     * Find all sites with current and delete flags set appropriately.
     * Maps to: BomUtil.getAllSites()
     */
    @Query("SELECT s FROM Site s WHERE s.isDeleted = false AND s.isCurrent = true")
    List<Site> getAllSites();

    /**
     * Find site by natural key: business entity + site name.
     * Maps to: BomUtil.findSiteByNaturalKey(BusinessEntity, String)
     */
    @Query("SELECT s FROM Site s WHERE s.businessEntity = :businessEntity AND s.siteName = :siteName")
    Optional<Site> findSiteByNaturalKey(@Param("businessEntity") BusinessEntity businessEntity, @Param("siteName") String siteName);

    /**
     * Find site by description for a business entity.
     * Maps to: BomUtil.findSiteByDescription(BusinessEntity, String, List)
     */
    @Query("SELECT s FROM Site s WHERE s.businessEntity = :businessEntity AND s.siteDescription = :siteDescription")
    Optional<Site> findSiteByDescription(@Param("businessEntity") BusinessEntity businessEntity, @Param("siteDescription") String siteDescription);

    /**
     * Find site by description and site type (used by TAM loaders when siteType is explicitly provided).
     */
    @Query("SELECT s FROM Site s WHERE s.siteDescription = :siteDescription AND s.siteType = :siteType")
    Optional<Site> findBySiteDescriptionAndSiteType(@Param("siteDescription") String siteDescription, @Param("siteType") String siteType);

    /**
     * Find site by description only (no type filter).
     * Used by TAM loaders when the upload file does not supply an explicit siteType —
     * the legacy behaviour was to match on description alone (e.g. WW = REGION type).
     */
    @Query("SELECT s FROM Site s WHERE s.siteDescription = :siteDescription")
    List<Site> findBySiteDescriptionOnly(@Param("siteDescription") String siteDescription);

    /**
     * Find sites by types for business entity.
     * Maps to: BomUtil.findSitesForBusiness(BusinessEntity, List)
     */
    @Query("SELECT s FROM Site s WHERE s.businessEntity = :businessEntity " +
           "AND (:siteTypes IS NULL OR s.siteType IN :siteTypes) " +
           "ORDER BY s.parentSite DESC, s.siteDescription ASC")
    List<Site> findSitesForBusinessWithTypes(@Param("businessEntity") BusinessEntity businessEntity, @Param("siteTypes") List<String> siteTypes);

    /**
     * Find sites by multiple business entity keys and site types.
     * Maps to: BomUtil.findSites(Long[], String[])
     */
    @Query("SELECT s FROM Site s WHERE (:businessTypes IS NULL OR s.businessEntity.businessEntityTypeKey IN :businessTypes) " +
           "AND (:siteTypes IS NULL OR s.siteType IN :siteTypes) " +
           "ORDER BY s.parentSite DESC, s.siteDescription ASC")
    List<Site> findSites(@Param("businessTypes") List<Long> businessTypes, @Param("siteTypes") List<String> siteTypes);

    /**
     * Find sites by business entity keys and site types.
     * Maps to: BomUtil.findSitesByBusinessEntitiesKey(Set<Long>, String[])
     */
    @Query("SELECT s FROM Site s WHERE s.businessEntity.businessEntityKey IN :businessEntityKeys " +
           "AND (:siteTypes IS NULL OR s.siteType IN :siteTypes) " +
           "ORDER BY s.parentSite DESC, s.siteDescription ASC")
    List<Site> findSitesByBusinessEntitiesKey(@Param("businessEntityKeys") List<Long> businessEntityKeys, @Param("siteTypes") List<String> siteTypes);

    /**
     * Find sites by region description.
     * Maps to: BomUtil.findSitesByRegion(String)
     */
    @Query("SELECT s FROM Site s LEFT JOIN FETCH s.siteDetail sd " +
           "WHERE s.parentSite.siteDescription = :region AND s.siteType IN ('GLOBAL','REGION','SITE') " +
           "AND (sd IS NULL OR sd.tamVisibleFlag = true) ORDER BY s.siteDescription ASC")
    List<Site> findSitesByRegion(@Param("region") String region);

    /**
     * Find sites by region (XLOB version).
     * Maps to: BomUtil.findSitesByRegionXlob(String)
     */
    @Query("SELECT s FROM Site s LEFT JOIN FETCH s.siteDetail sd " +
           "WHERE s.parentSite.siteDescription = :region AND s.siteType IN ('GLOBAL','REGION','SITE') " +
           "ORDER BY s.siteDescription ASC")
    List<Site> findSitesByRegionXlob(@Param("region") String region);

    /**
     * Find CCN site by description.
     * Maps to: BomUtil.findCCNSiteByDescription(String)
     */
    @Query("SELECT s FROM Site s WHERE s.siteDescription = :siteDescription AND s.siteType IN ('CCN')")
    Optional<Site> findCCNSiteByDescription(@Param("siteDescription") String siteDescription);

    /**
     * Get all enterprise region sites.
     * Maps to: BomUtil.getEnterpriseRegionList()
     */
    @Query("SELECT s FROM Site s LEFT JOIN FETCH s.siteDetail sd " +
           "WHERE s.siteType = 'REGION' AND s.businessEntity.businessEntityTypeKey = :businessTypeKey " +
           "AND (sd IS NULL OR sd.tamVisibleFlag = true) ORDER BY s.siteDescription ASC")
    List<Site> getEnterpriseRegionList(@Param("businessTypeKey") Long businessTypeKey);

    /**
     * Get all enterprise region sites (XLOB version).
     * Maps to: BomUtil.getEnterpriseRegionListXlob()
     */
    @Query("SELECT s FROM Site s LEFT JOIN FETCH s.siteDetail sd " +
           "WHERE s.siteType = 'REGION' AND s.businessEntity.businessEntityTypeKey = :businessTypeKey " +
           "ORDER BY s.siteDescription ASC")
    List<Site> getEnterpriseRegionListXlob(@Param("businessTypeKey") Long businessTypeKey);

    /**
     * Find top-level site for business with optional site type.
     * Maps to: BomUtil.findTopSiteForBusiness(BusinessEntity, String)
     */
    @Query("SELECT s FROM Site s WHERE s.businessEntity = :businessEntity " +
           "AND (:siteType IS NULL OR s.siteType = :siteType) AND s.parentSite IS NULL")
    Optional<Site> findTopSiteForBusiness(@Param("businessEntity") BusinessEntity businessEntity, @Param("siteType") String siteType);


}
