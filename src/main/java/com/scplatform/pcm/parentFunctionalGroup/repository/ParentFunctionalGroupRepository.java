/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.parentFunctionalGroup.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.parentFunctionalGroup.entity.ParentFunctionalGroup;

/**
 * Spring Data JPA Repository for ParentFunctionalGroup entity.
 * Provides data access operations for ParentFunctionalGroup entities.
 */
@Repository
public interface ParentFunctionalGroupRepository extends JpaRepository<ParentFunctionalGroup, Long> {

	@Query("SELECT DISTINCT pfg FROM ParentFunctionalGroup pfg LEFT JOIN FETCH pfg.functionalGroups " +
	       "WHERE LOWER(pfg.name) = LOWER(:name)")
	List<ParentFunctionalGroup> getParentFunctionalGroupByNameInternal(@Param("name") String name);

	default ParentFunctionalGroup getParentFunctionalGroupByName(String name) {
		List<ParentFunctionalGroup> results = getParentFunctionalGroupByNameInternal(name);
		return results.isEmpty() ? null : results.get(0);
	}

	@Query("SELECT DISTINCT pfg FROM ParentFunctionalGroup pfg LEFT JOIN FETCH pfg.functionalGroups " +
	       "WHERE LOWER(pfg.name) = LOWER(:name) AND pfg.type = :type")
	List<ParentFunctionalGroup> getParentFunctionalGroupByNameAndTypeInternal(
			@Param("name") String name,
			@Param("type") String type);

	default ParentFunctionalGroup getParentFunctionalGroupByName(String name, String type) {
		List<ParentFunctionalGroup> results = getParentFunctionalGroupByNameAndTypeInternal(name, type);
		return results.isEmpty() ? null : results.get(0);
	}

	default void deleteParentFunctionalGroup(ParentFunctionalGroup parentFunctionalGroup) {
		delete(parentFunctionalGroup);
	}

	/**
	 * Find a parent functional group by its name
	 * 
	 * @param name the parent functional group name
	 * @return Optional containing the parent functional group if found
	 */
	Optional<ParentFunctionalGroup> findByName(String name);

	/**
	 * Find all parent functional groups by type
	 * 
	 * @param type the parent functional group type
	 * @return list of parent functional groups of the given type
	 */
	List<ParentFunctionalGroup> findByType(String type);

	/**
	 * Find all parent functional groups by purpose
	 * 
	 * @param purpose the purpose value
	 * @return list of parent functional groups with the given purpose
	 */
	List<ParentFunctionalGroup> findByPurpose(String purpose);

	/**
	 * Find all parent functional groups by type and purpose
	 * 
	 * @param type the parent functional group type
	 * @param purpose the purpose value
	 * @return list of parent functional groups matching type and purpose
	 */
	List<ParentFunctionalGroup> findByTypeAndPurpose(String type, String purpose);

	/**
	 * Search parent functional groups by name containing the given text (case-insensitive)
	 * 
	 * @param namePattern the name pattern to search
	 * @return list of parent functional groups matching the pattern
	 */
	@Query("SELECT pfg FROM ParentFunctionalGroup pfg WHERE LOWER(pfg.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
	List<ParentFunctionalGroup> searchByNameContaining(@Param("namePattern") String namePattern);

	/**
	 * Find all parent functional groups created by a specific user
	 * 
	 * @param createdBy the user who created the parent functional group
	 * @return list of parent functional groups created by the user
	 */
	List<ParentFunctionalGroup> findByCreatedBy(String createdBy);

	/**
	 * Find all parent functional groups last changed by a specific user
	 * 
	 * @param lastChangedBy the user who last changed the parent functional group
	 * @return list of parent functional groups last changed by the user
	 */
	List<ParentFunctionalGroup> findByLastChangedBy(String lastChangedBy);

	/**
	 * Find all parent functional groups created after a specific date
	 * 
	 * @param createdOn the date to compare from
	 * @return list of parent functional groups created after the date
	 */
	@Query("SELECT pfg FROM ParentFunctionalGroup pfg WHERE pfg.createdOn > :createdOn")
	List<ParentFunctionalGroup> findCreatedAfter(@Param("createdOn") LocalDateTime createdOn);

	/**
	 * Find all parent functional groups last changed after a specific date
	 * 
	 * @param lastChangedOn the date to compare from
	 * @return list of parent functional groups last changed after the date
	 */
	@Query("SELECT pfg FROM ParentFunctionalGroup pfg WHERE pfg.lastChangedOn > :lastChangedOn")
	List<ParentFunctionalGroup> findChangedAfter(@Param("lastChangedOn") LocalDateTime lastChangedOn);

	/**
	 * Count parent functional groups by type
	 * 
	 * @param type the parent functional group type
	 * @return count of parent functional groups with the given type
	 */
	long countByType(String type);

	/**
	 * Count parent functional groups by purpose
	 * 
	 * @param purpose the purpose value
	 * @return count of parent functional groups with the given purpose
	 */
	long countByPurpose(String purpose);

	/**
	 * Check if a parent functional group exists by name
	 * 
	 * @param name the parent functional group name
	 * @return true if exists, false otherwise
	 */
	boolean existsByName(String name);

	/**
	 * Find all parent functional groups that contain a functional group with the given name
	 * 
	 * @param name the functional group name to search
	 * @return list of parent functional groups containing the functional group
	 */
	@Query("SELECT DISTINCT pfg FROM ParentFunctionalGroup pfg " +
	       "JOIN pfg.functionalGroups fg " +
	       "WHERE fg.name = :name")
	List<ParentFunctionalGroup> getParentGroupListByFunctionalGroupName(@Param("name") String name);

	default Long saveParentFunctionalGroup(ParentFunctionalGroup parentFunctionalGroup) {
		return save(parentFunctionalGroup).getParentFunctionalGroupId();
	}

	default void updateParentFunctionalGroup(ParentFunctionalGroup parentFunctionalGroup) {
		save(parentFunctionalGroup);
	}

	@Query("SELECT COUNT(pfg) > 0 FROM ParentFunctionalGroup pfg WHERE LOWER(pfg.name) = LOWER(:name)")
	boolean isParentFunctionalGroupWithNameExist(@Param("name") String name);

	@Query("SELECT DISTINCT pfg FROM ParentFunctionalGroup pfg LEFT JOIN FETCH pfg.functionalGroups WHERE pfg.parentFunctionalGroupId = :id")
	ParentFunctionalGroup getParentFunctionalGroupByIdInternal(@Param("id") Long id);

	default ParentFunctionalGroup getParentFunctionalGroupById(Long id) {
		return getParentFunctionalGroupByIdInternal(id);
	}
}
