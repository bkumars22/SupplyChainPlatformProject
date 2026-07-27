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
     *
     * BB-ITEM-04: this used to be a native query joining a BUSINESS_ENTITY_TYPE
     * table that doesn't exist — type is a fixed set of hardcoded constants on
     * BusinessEntity (MFG_TYPE/SUPPLIER_TYPE/OPERATOR_TYPE/ENTERPRISE_TYPE), not
     * a lookup table, so every call with a non-null typeName threw
     * "Table BUSINESS_ENTITY_TYPE not found" -> uncaught -> 500. Resolves the
     * name to a key in Java (BusinessEntity.getTypeKeyFromName) instead.
     */
    default List<BusinessEntity> findBusinessById(String identifier, String typeName) {
        if (typeName == null || typeName.isBlank()) {
            return findBusinessByIdInternal(identifier, null);
        }
        Long typeKey = BusinessEntity.getTypeKeyFromName(typeName);
        if (typeKey == null) {
            return List.of(); // unrecognized type name — no business entity can match
        }
        return findBusinessByIdInternal(identifier, typeKey);
    }

    @Query("SELECT be FROM BusinessEntity be WHERE UPPER(be.businessEntityIdentifier) = UPPER(:identifier) " +
           "AND (:typeKey IS NULL OR be.businessEntityTypeKey = :typeKey)")
    List<BusinessEntity> findBusinessByIdInternal(@Param("identifier") String identifier, @Param("typeKey") Long typeKey);

    /**
     * Find business by external reference ID.
     * Maps to: BomUtil.findBusinessByExternalRefId(String)
     */
    @Query("SELECT be FROM BusinessEntity be WHERE be.externalId = :externalRefId")
    Optional<BusinessEntity> findBusinessByExternalRefId(@Param("externalRefId") String externalRefId);

    /**
     * Find businesses by name and optionally type (case-insensitive).
     * Maps to: BomUtil.findBusinessByName(String, String, boolean)
     * BB-ITEM-04 fix — see findBusinessById's Javadoc above for why.
     */
    default List<BusinessEntity> findBusinessByName(String name, String typeName) {
        if (typeName == null || typeName.isBlank()) {
            return findBusinessByNameInternal(name, null);
        }
        Long typeKey = BusinessEntity.getTypeKeyFromName(typeName);
        if (typeKey == null) {
            return List.of();
        }
        return findBusinessByNameInternal(name, typeKey);
    }

    @Query("SELECT be FROM BusinessEntity be WHERE UPPER(be.businessEntityName) = UPPER(:name) " +
           "AND (:typeKey IS NULL OR be.businessEntityTypeKey = :typeKey)")
    List<BusinessEntity> findBusinessByNameInternal(@Param("name") String name, @Param("typeKey") Long typeKey);

    /**
     * Find unique business by name and type (case-insensitive).
     * Maps to: BomUtil.findUniqueBusinessByName(String, String, boolean)
     * BB-ITEM-04 fix — see findBusinessById's Javadoc above for why.
     */
    default Optional<BusinessEntity> findUniqueBusinessByName(String name, String typeName) {
        if (typeName == null || typeName.isBlank()) {
            return findUniqueBusinessByNameInternal(name, null);
        }
        Long typeKey = BusinessEntity.getTypeKeyFromName(typeName);
        if (typeKey == null) {
            return Optional.empty();
        }
        return findUniqueBusinessByNameInternal(name, typeKey);
    }

    @Query("SELECT be FROM BusinessEntity be WHERE UPPER(be.businessEntityName) = UPPER(:name) " +
           "AND (:typeKey IS NULL OR be.businessEntityTypeKey = :typeKey)")
    Optional<BusinessEntity> findUniqueBusinessByNameInternal(@Param("name") String name, @Param("typeKey") Long typeKey);

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
