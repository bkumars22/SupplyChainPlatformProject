/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.item.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    /**
     * Find item by number, ID, type and business entity.
     * Maps to: BomUtil.ItemUtil.findItemByNumber(String, String, String, BusinessEntity)
     */
    @Query("SELECT i FROM Item i WHERE i.itemNumber = :itemNumber " +
           "AND (:itemId IS NULL OR i.itemId = :itemId) " +
           "AND (:itemType IS NULL OR i.itemType = :itemType) " +
           "AND i.businessEntity = :businessEntity")
    Optional<Item> findByItemNumberAndBusinessEntity(
            @Param("itemNumber") String itemNumber,
            @Param("itemId") String itemId,
            @Param("itemType") String itemType,
            @Param("businessEntity") BusinessEntity businessEntity);

    /**
     * Find item by natural key: number + ID + version + revision + type + business entity.
     * Maps to: BomUtil.ItemUtil.findItemByNaturalKey(String, String, String, String, String, BusinessEntity)
     */
    @Query("SELECT i FROM Item i WHERE i.itemNumber = :itemNumber " +
           "AND (:itemId IS NULL OR i.itemId = :itemId) " +
           "AND (:revision IS NULL OR i.itemVersion.revision = :revision) " +
           "AND (:version IS NULL OR i.itemVersion.version = :version) " +
           "AND (:itemType IS NULL OR i.itemType = :itemType) " +
           "AND i.businessEntity = :businessEntity")
    Optional<Item> findByItemNumberAndItemIdAndVersionAndRevisionAndItemTypeAndBusinessEntity(
            @Param("itemNumber") String itemNumber,
            @Param("itemId") String itemId,
            @Param("version") String version,
            @Param("revision") String revision,
            @Param("itemType") String itemType,
            @Param("businessEntity") BusinessEntity businessEntity);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.functionalGroups WHERE i.itemKey = :itemKey")
    Item getItemByKeyInternal(@Param("itemKey") Long itemKey);

    default Item getItemByKey(Long itemKey) {
        return getItemByKeyInternal(itemKey);
    }

    /**
     * Count distinct data sources matching the given value.
     * Maps to Hibernate Criteria API code with distinct projection and rowCount.
     * 
     * @param companyItemType the data source value to search for
     * @return count of distinct data sources
     */
    @Query("SELECT COUNT(DISTINCT i.dataSource) FROM Item i WHERE i.dataSource = :dataSource")
    long countDistinctDataSourceByType(@Param("dataSource") String companyItemType);

    /**
     * Count total items by data source.
     * Alternative method that returns the actual count of matching records.
     * 
     * @param companyItemType the data source value to search for
     * @return count of rows matching the criteria
     */
    @Query(value = "SELECT COUNT(*) FROM ITEM WHERE data_source = :dataSource", nativeQuery = true)
    long countItemByDataSource(@Param("dataSource") String companyItemType);

    /**
     * Find distinct data sources matching the given value.
     * Maps to Hibernate Criteria API code with distinct projection.
     * 
     * @param companyItemType the data source value to search for
     * @return list of distinct data sources
     */
    @Query("SELECT DISTINCT i.dataSource FROM Item i WHERE i.dataSource = :dataSource")

    List<String> findDistinctDataSources(@Param("dataSource") String companyItemType);

    /**
     * Find an item by number, type, and business entity name.
     * Used by FunctionalGroupLoader to resolve items from upload data.
     * BE name comparison is case-insensitive.
     */
    @Query("SELECT i FROM Item i JOIN i.businessEntity be " +
           "WHERE i.itemNumber = :itemNumber AND i.itemType = :itemType AND UPPER(be.businessEntityName) = UPPER(:beName)")
    List<Item> findByItemNumberAndTypeAndBusinessEntityName(
            @Param("itemNumber") String itemNumber,
            @Param("itemType") String itemType,
            @Param("beName") String beName);

    /**
     * Find items by number and type (no business entity filter).
     */
    @Query("SELECT i FROM Item i WHERE i.itemNumber = :itemNumber AND i.itemType = :itemType")
    List<Item> findByItemNumberAndType(
            @Param("itemNumber") String itemNumber,
            @Param("itemType") String itemType);

    /**
     * Find items by number alone, no type/business-entity filter. For callers
     * (e.g. CostRecordService) that only have an item number to resolve —
     * findByItemNumberAndTypeAndBusinessEntityName's plain "= :itemType"/
     * "= :beName" equality can't be called with null there (SQL/JPQL "x = NULL"
     * is always unknown, never true, so it silently returns zero rows for
     * every item regardless of itemNumber - see BB-COST-02).
     */
    List<Item> findByItemNumber(String itemNumber);

    Page<Item> findByItemNumberContainingIgnoreCase(String itemNumber, Pageable pageable);

    @Query("SELECT i.itemKey FROM Item i WHERE i.itemNumber IN :itemNumbers")
    List<Long> findItemKeysByItemNumbers(@Param("itemNumbers") List<String> itemNumbers);

    @Query(name = "dashboard:newUnassignedItems")
    List<Object[]> findNewUnassignedItems(@Param("cutoffDate") Date cutoffDate);

    @Query(name = "dashboard:forecast")
    List<Object[]> findForecastStatus(@Param("status") List<String> status,
                                      @Param("cutoffDate") Date cutoffDate);
    @Query(name = "dashboard:forecast_ADJ")
    List<Object[]> findForecastAdjStatus(@Param("status") List<String> status,
                                         @Param("cutoffDate") Date cutoffDate);
}
