/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.role.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.role.entity.Role;

/**
 * Spring Data JPA Repository for Role entity.
 * Provides data access operations for Role entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	// ==================== Find by Primary Columns ====================
	Optional<Role> findByRoleIdIgnoreCase(String roleId);

    /**
     * Find all users ordered by userId ascending.
     * @return list of all users ordered by userId
     */
    List<Role> findAllByOrderByRoleIdAsc();

	Role findByRoleKey(Long roleKey);

	/**
	 * Get all roles ordered by roleId ascending.
	 * Equivalent to legacy Hibernate method that retrieves all roles with ordering.
	 *
	 * @return list of all roles ordered by roleId ascending
	 */
	@Query("SELECT r FROM Role r ORDER BY r.roleId ASC")
	List<Role> getAllRoles();

	/**
	 * Find a role by roleId.
	 * Equivalent to legacy Hibernate Criteria uniqueResult on roleId equality.
	 *
	 * @param roleId the role ID string
	 * @return the matching Role, or null if not found
	 */
	@Query("SELECT r FROM Role r WHERE r.roleId = :roleId")
	Role findRoleById(@Param("roleId") String roleId);

	/**
	 * Find a role by roleKey with eager loading of ACLs.
	 * Equivalent to legacy Hibernate method that initializes role.getAcls().
	 *
	 * @param roleKey the role primary key
	 * @return the matching Role with ACLs initialized, or null if not found
	 */
	@Query("SELECT r FROM Role r LEFT JOIN FETCH r.acls WHERE r.roleKey = :roleKey")
	Role getRole(@Param("roleKey") Long roleKey);

	// ==================== Custom Search Methods ====================

	/**
	 * Find all roles ordered by roleId ascending.
	 * Can optionally filter by role parameter (searches in both roleId and roleName with wildcards).
	 *
	 * @return list of all roles ordered by roleId
	 */
	@Query("SELECT r FROM Role r ORDER BY r.roleId ASC")
	List<Role> findAllOrderByRoleId();

	/**
	 * Find roles by roleId or roleName containing the search text (case-insensitive), ordered by roleId.
	 *
	 * @param searchText the text to search for in roleId or roleName (wrapped with % for ILIKE)
	 * @return list of matching roles ordered by roleId
	 */
	@Query("SELECT r FROM Role r WHERE LOWER(r.roleId) LIKE LOWER(:searchText) OR LOWER(r.roleName) LIKE LOWER(:searchText) ORDER BY r.roleId ASC")
	List<Role> findByRoleIdOrRoleNameContainingIgnoreCase(@Param("searchText") String searchText);

	/**
	 * Find roles excluding ADMIN role, ordered by roleId.
	 *
	 * @return list of non-admin roles ordered by roleId
	 */
	@Query("SELECT r FROM Role r WHERE r.roleId != 'ADMIN' ORDER BY r.roleId ASC")
	List<Role> findAllExcludingAdmin();

	/**
	 * Find roles excluding ADMIN role by roleId or roleName search (case-insensitive), ordered by roleId.
	 *
	 * @param searchText the text to search for in roleId or roleName
	 * @return list of matching non-admin roles ordered by roleId
	 */
	@Query("SELECT r FROM Role r WHERE r.roleId != 'ADMIN' AND (LOWER(r.roleId) LIKE LOWER(:searchText) OR LOWER(r.roleName) LIKE LOWER(:searchText)) ORDER BY r.roleId ASC")
	List<Role> findByRoleIdOrRoleNameContainingIgnoreCaseExcludingAdmin(@Param("searchText") String searchText);

	/**
	 * Count all roles excluding ADMIN role.
	 *
	 * @return count of non-admin roles
	 */
	@Query("SELECT COUNT(r) FROM Role r WHERE r.roleId != 'ADMIN'")
	long countAllExcludingAdmin();

	/**
	 * Count roles by roleId or roleName search (case-insensitive), excluding ADMIN role.
	 *
	 * @param searchText the text to search for in roleId or roleName
	 * @return count of matching non-admin roles
	 */
	@Query("SELECT COUNT(r) FROM Role r WHERE r.roleId != 'ADMIN' AND (LOWER(r.roleId) LIKE LOWER(:searchText) OR LOWER(r.roleName) LIKE LOWER(:searchText))")
	long countByRoleIdOrRoleNameContainingIgnoreCaseExcludingAdmin(@Param("searchText") String searchText);

	/**
	 * Count roles by roleId or roleName search (case-insensitive).
	 *
	 * @param searchText the text to search for in roleId or roleName
	 * @return count of matching roles
	 */
	@Query("SELECT COUNT(r) FROM Role r WHERE LOWER(r.roleId) LIKE LOWER(:searchText) OR LOWER(r.roleName) LIKE LOWER(:searchText)")
	long countByRoleIdOrRoleNameContainingIgnoreCase(@Param("searchText") String searchText);
}

