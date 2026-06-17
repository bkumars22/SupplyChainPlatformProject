/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.businessEntity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;

@Repository
public interface BusinessEntityRepository extends JpaRepository<BusinessEntity, Long> {

    /**
     * Find business by natural key: identifier + type key.
     * Maps to: BomUtil.findBusinessByNaturalKey(String, Long)
     */
    @Query("SELECT be FROM BusinessEntity be WHERE be.businessEntityIdentifier = :identifier AND be.businessEntityTypeKey = :typeKey")
    Optional<BusinessEntity> findBusinessByNaturalKey(@Param("identifier") String identifier, @Param("typeKey") long typeKey);

    /**
     * Find business by natural key: identifier + type name.
     * Maps to: BomUtil.findBusinessByNaturalKey(String, String)
     */
    @Query(value = "SELECT be.* FROM BUSINESS_ENTITY be WHERE be.BUSINESS_ENTITY_IDENTIFIER = :identifier " +
            "AND be.BUSINESS_ENTITY_TYPE_KEY = (SELECT bt.BUSINESS_ENTITY_TYPE_KEY FROM BUSINESS_ENTITY_TYPE bt WHERE bt.BUSINESS_ENTITY_TYPE_NAME = :typeName)",
            nativeQuery = true)
    Optional<BusinessEntity> findBusinessByNaturalKey(@Param("identifier") String identifier, @Param("typeName") String typeName);

    /**
     * Find businesses by ID and optionally type with case-insensitive option.
     * Maps to: BomUtil.findBusinessById(String, String, boolean)
     */
    @Query(value = "SELECT be.* FROM BUSINESS_ENTITY be WHERE UPPER(be.BUSINESS_ENTITY_IDENTIFIER) = UPPER(:identifier) " +
            "AND (:typeName IS NULL OR be.BUSINESS_ENTITY_TYPE_KEY = (SELECT bt.BUSINESS_ENTITY_TYPE_KEY FROM BUSINESS_ENTITY_TYPE bt WHERE bt.BUSINESS_ENTITY_TYPE_NAME = :typeName))",
            nativeQuery = true)
    List<BusinessEntity> findBusinessById(@Param("identifier") String identifier, @Param("typeName") String typeName);

    /**
     * Find business by external reference ID.
     * Maps to: BomUtil.findBusinessByExternalRefId(String)
     */
    @Query("SELECT be FROM BusinessEntity be WHERE be.externalId = :externalRefId")
    Optional<BusinessEntity> findBusinessByExternalRefId(@Param("externalRefId") String externalRefId);

    /**
     * Find businesses by name and optionally type (case-insensitive).
     * Maps to: BomUtil.findBusinessByName(String, String, boolean)
     */
    @Query(value = "SELECT be.* FROM BUSINESS_ENTITY be WHERE UPPER(be.BUSINESS_ENTITY_NAME) = UPPER(:name) " +
            "AND (:typeName IS NULL OR be.BUSINESS_ENTITY_TYPE_KEY = (SELECT bt.BUSINESS_ENTITY_TYPE_KEY FROM BUSINESS_ENTITY_TYPE bt WHERE bt.BUSINESS_ENTITY_TYPE_NAME = :typeName))",
            nativeQuery = true)
    List<BusinessEntity> findBusinessByName(@Param("name") String name, @Param("typeName") String typeName);

    /**
     * Find unique business by name and type (case-insensitive).
     * Maps to: BomUtil.findUniqueBusinessByName(String, String, boolean)
     */
    @Query(value = "SELECT be.* FROM BUSINESS_ENTITY be WHERE UPPER(be.BUSINESS_ENTITY_NAME) = UPPER(:name) " +
            "AND (:typeName IS NULL OR be.BUSINESS_ENTITY_TYPE_KEY = (SELECT bt.BUSINESS_ENTITY_TYPE_KEY FROM BUSINESS_ENTITY_TYPE bt WHERE bt.BUSINESS_ENTITY_TYPE_NAME = :typeName))",
            nativeQuery = true)
    Optional<BusinessEntity> findUniqueBusinessByName(@Param("name") String name, @Param("typeName") String typeName);

    /**
     * Find businesses by alternate name.
     * Maps to: BomUtil.findBusinessByAlternate(String, String, boolean)
     */
    @Query(value = "SELECT DISTINCT be.* FROM BUSINESS_ENTITY be LEFT JOIN BUSINESS_ENTITY_ALTERNATE alt ON be.BUSINESS_ENTITY_KEY = alt.BUSINESS_ENTITY_KEY " +
            "WHERE be.BUSINESS_ENTITY_TYPE_KEY = (SELECT bt.BUSINESS_ENTITY_TYPE_KEY FROM BUSINESS_ENTITY_TYPE bt WHERE bt.BUSINESS_ENTITY_TYPE_NAME = :typeName) " +
            "AND UPPER(alt.BUSINESS_ENTITY_NAME) = UPPER(:alternateName)",
            nativeQuery = true)
    List<BusinessEntity> findBusinessByAlternate(@Param("alternateName") String alternateName, @Param("typeName") String typeName);


    @Query("SELECT be FROM BusinessEntity be WHERE be.businessEntityTypeKey = :typeKey")
    Optional<BusinessEntity> findByBusinessEntityTypeKey(@Param("typeKey") long typeKey);

    /**
     * Check if business entity type exists by name.
     * Maps to: BomUtil.doesBusinessEntityTypeExist(String)
     * Note: This queries BUSINESS_ENTITY_TYPE table directly
     */
    @Query(value = "SELECT COUNT(bt) > 0 FROM BUSINESS_ENTITY_TYPE bt WHERE bt.BUSINESS_ENTITY_TYPE_NAME = :typeName",
            nativeQuery = true)
    boolean doesBusinessEntityTypeExist(@Param("typeName") String typeName);

    @Query("SELECT be FROM BusinessEntity be WHERE (:excludeOperator = false OR be.businessEntityTypeKey != :operatorType) " +
            "AND (:noKeys = true OR be.businessEntityKey IN :keys) " +
            "ORDER BY be.businessEntityName")
    List<BusinessEntity> findBusinessesByKeys(@Param("excludeOperator") boolean excludeOperator,
                                              @Param("operatorType") long operatorType,
                                              @Param("noKeys") boolean noKeys,
                                              @Param("keys") List<Long> keys);

    /**
     * Find distinct business entity names matching the given type.
     * Maps to Hibernate Criteria API code with distinct projection.
     * 
     * @param companyItemType the business entity name to search for
     * @return list of distinct business entity names
     */
    @Query("SELECT DISTINCT be.businessEntityName FROM BusinessEntity be WHERE be.businessEntityName = :companyItemType")
    List<String> findDistinctBusinessEntityNames(@Param("companyItemType") String companyItemType);
}
