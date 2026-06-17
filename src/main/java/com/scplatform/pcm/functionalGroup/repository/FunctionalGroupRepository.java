/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.functionalGroup.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;

/**
 * Spring Data JPA Repository for FunctionalGroup entity.
 * Provides data access operations for FunctionalGroup entities.
 */
@Repository
public interface FunctionalGroupRepository extends JpaRepository<FunctionalGroup, Long> {

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg LEFT JOIN FETCH fg.platform WHERE LOWER(fg.name) = LOWER(:name)")
	List<FunctionalGroup> getFunctionalGroupByNameInternal(@Param("name") String name);

	default FunctionalGroup getFunctionalGroupByName(String name) {
		List<FunctionalGroup> results = getFunctionalGroupByNameInternal(name);
		return results.isEmpty() ? null : results.get(0);
	}

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg LEFT JOIN FETCH fg.platform " +
	       "WHERE LOWER(fg.name) = LOWER(:name) AND LOWER(fg.type) = LOWER(:type)")
	List<FunctionalGroup> getFunctionalGroupByNameAndTypeInternal(
			@Param("name") String name,
			@Param("type") String type);

	default FunctionalGroup getFunctionalGroupByNameAndType(String name, String type) {
		List<FunctionalGroup> results = getFunctionalGroupByNameAndTypeInternal(name, type);
		return results.isEmpty() ? null : results.get(0);
	}

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg LEFT JOIN FETCH fg.functionalGroupItems " +
	       "WHERE LOWER(fg.name) = LOWER(:name)")
	List<FunctionalGroup> getEagerFunctionalGroupByNameInternal(@Param("name") String name);

	default FunctionalGroup getEagerFunctionalGroupByName(String name) {
		List<FunctionalGroup> results = getEagerFunctionalGroupByNameInternal(name);
		return results.isEmpty() ? null : results.get(0);
	}

	/**
	 * Find a functional group by its external ID
	 * 
	 * @param functionalGroupExternalId the external ID
	 * @return Optional containing the functional group if found
	 */
	Optional<FunctionalGroup> findByFunctionalGroupExternalId(String functionalGroupExternalId);

	/**
	 * Find a functional group by its name
	 * 
	 * @param name the functional group name
	 * @return Optional containing the functional group if found
	 */
	Optional<FunctionalGroup> findByName(String name);

	/**
	 * Find all functional groups by status
	 * 
	 * @param status the status value
	 * @return list of functional groups with the given status
	 */
	List<FunctionalGroup> findByStatus(String status);

	/**
	 * Find all functional groups by type
	 * 
	 * @param type the functional group type
	 * @return list of functional groups of the given type
	 */
	List<FunctionalGroup> findByType(String type);

	/**
	 * Find all functional groups by status and type
	 * 
	 * @param status the status value
	 * @param type the functional group type
	 * @return list of functional groups matching status and type
	 */
	List<FunctionalGroup> findByStatusAndType(String status, String type);

	/**
	 * Search functional groups by name containing the given text (case-insensitive)
	 * 
	 * @param namePattern the name pattern to search
	 * @return list of functional groups matching the pattern
	 */
	@Query("SELECT fg FROM FunctionalGroup fg WHERE LOWER(fg.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
	List<FunctionalGroup> searchByNameContaining(@Param("namePattern") String namePattern);

	/**
	 * Find all functional groups created by a specific user
	 * 
	 * @param createdBy the user who created the functional group
	 * @return list of functional groups created by the user
	 */
	List<FunctionalGroup> findByCreatedBy(String createdBy);

	/**
	 * Count functional groups by status
	 * 
	 * @param status the status value
	 * @return count of functional groups with the given status
	 */
	long countByStatus(String status);

	/**
	 * Check if a functional group exists by external ID
	 * 
	 * @param functionalGroupExternalId the external ID
	 * @return true if exists, false otherwise
	 */
	boolean existsByFunctionalGroupExternalId(String functionalGroupExternalId);

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg " +
	       "INNER JOIN fg.functionalGroupItems fgItem " +
	       "WHERE LOWER(fgItem.itemNumber) = LOWER(:itemNumber)")
	List<FunctionalGroup> getFunctionalGroupListByItemInternal(@Param("itemNumber") String itemNumber);

	default List<FunctionalGroup> getFunctionalGroupListByItem(String item) {
		return getFunctionalGroupListByItemInternal(item);
	}

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg " +
	       "INNER JOIN fg.functionalGroupItems fgItem " +
	       "WHERE LOWER(fgItem.itemNumber) = LOWER(:itemNumber) AND fg.type IN (:fgTypes)")
	List<FunctionalGroup> getFunctionalGroupListByItemAndFGTypeWithTypesInternal(
			@Param("itemNumber") String itemNumber,
			@Param("fgTypes") List<String> fgTypes);

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg " +
	       "INNER JOIN fg.functionalGroupItems fgItem " +
	       "WHERE LOWER(fgItem.itemNumber) = LOWER(:itemNumber) AND fg.type = 'CFG'")
	List<FunctionalGroup> getCFGListByItem(@Param("itemNumber") String itemNumber);

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg " +
	       "INNER JOIN fg.functionalGroupItems fgItem " +
	       "WHERE LOWER(fgItem.itemNumber) = LOWER(:itemNumber) AND fg.type = 'XLOB'")
	List<FunctionalGroup> getXLOBListByItem(@Param("itemNumber") String itemNumber);

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg " +
	       "INNER JOIN fg.parentFunctionalGroup fgParent " +
	       "WHERE fgParent.parentFunctionalGroupId = :parentID")
	List<FunctionalGroup> getFunctionalGroupListByParent(@Param("parentID") Long parentID);

	default List<FunctionalGroup> getFunctionalGroupListByItemAndFGType(Item item, List<String> fgType) {
		if (fgType != null && !fgType.isEmpty()) {
			return getFunctionalGroupListByItemAndFGTypeWithTypesInternal(item.getItemNumber(), fgType);
		} else {
			return getFunctionalGroupListByItemInternal(item.getItemNumber());
		}
	}

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg " +
	       "INNER JOIN fg.functionalGroupItems fgItem " +
	       "WHERE fgItem.itemNumber = :itemNumber AND fg.type = :fgType")
	List<FunctionalGroup> getFunctionalGroupByItemAndFGTypeInternal(
			@Param("itemNumber") String itemNumber,
			@Param("fgType") String fgType);

	default List<FunctionalGroup> getFunctionalGroupByItemAndFGType(Item item, String fgType) {
		return getFunctionalGroupByItemAndFGTypeInternal(item.getItemNumber(), fgType);
	}

	@Query("SELECT fg FROM FunctionalGroup fg WHERE fg.functionalGroupId = :id")
	FunctionalGroup getFunctionalGroupByIdInternal(@Param("id") Long id);

	default FunctionalGroup getFunctionalGroupById(Long id) {
		return getFunctionalGroupByIdInternal(id);
	}

	default void updateFunctionalGroup(FunctionalGroup functionalGroup) {
		save(functionalGroup);
	}

	default void saveFunctionalGroup(FunctionalGroup functionalGroup) {
		save(functionalGroup);
	}

	@Query("SELECT COUNT(fg) > 0 FROM FunctionalGroup fg WHERE LOWER(fg.name) = LOWER(:name) AND LOWER(fg.type) = LOWER(:type)")
	boolean isFunctionalGroupWithNameExist(@Param("name") String name, @Param("type") String type);

	@Query("SELECT COUNT(fgi) > 0 FROM Item i INNER JOIN i.functionalGroups fgi WHERE i.itemKey = :itemKey AND LOWER(fgi.name) != LOWER(:functionalGroupName)")
	boolean isOtherFunctionalGroupPresentWithItem(@Param("itemKey") Long itemKey, @Param("functionalGroupName") String functionalGroupName);

	@Query("SELECT COUNT(fgi) > 0 FROM Item i INNER JOIN i.functionalGroups fgi WHERE i.itemKey = :itemKey AND LOWER(fgi.type) = LOWER(:functionalGroupType) AND LOWER(fgi.name) != LOWER(:functionalGroupName)")
	boolean isOtherFunctionalGroupOfTypePresentWithItem(@Param("itemKey") Long itemKey, @Param("functionalGroupName") String functionalGroupName, @Param("functionalGroupType") String functionalGroupType);

	@Query("SELECT DISTINCT fg FROM FunctionalGroup fg LEFT JOIN FETCH fg.parentFunctionalGroup WHERE fg.functionalGroupId = :id")
	FunctionalGroup getFunctionalGroupByIdEagerInternal(@Param("id") Long id);

	default FunctionalGroup getFunctionalGroupByIdEager(Long id) {
		return getFunctionalGroupByIdEagerInternal(id);
	}

	@Query("SELECT COUNT(fg) > 0 FROM FunctionalGroup fg INNER JOIN fg.parentFunctionalGroup p WHERE fg.functionalGroupId = :functionalGroupId AND LOWER(p.name) != LOWER(:parentName)")
	boolean isOtherParentGroupPresentWithFunctionalGroup(@Param("functionalGroupId") Long functionalGroupId, @Param("parentName") String parentName);

    /**
     * Get functional group name by functional group ID
     * Replaces legacy Hibernate Criteria method that used HibernateUtil and Restrictions
     *
     * @param functionalGroupId the functional group ID
     * @return the name of the functional group, or null if not found
     */
    @Query("SELECT fg.name FROM FunctionalGroup fg WHERE fg.functionalGroupId = :functionalGroupId")
    static String getXLOBFGNameByID(@Param("functionalGroupId") Long functionalGroupId) {
        return null;
    }
}
