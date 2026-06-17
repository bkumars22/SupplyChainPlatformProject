/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.accessControl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.accessControl.entity.AccessControl;
import com.scplatform.pcm.user.entity.Users;

/**
 * Spring Data JPA Repository for AccessControl entity.
 * Provides data access operations for AccessControl entities.
 */
@Repository
public interface AccessControlRepository extends JpaRepository<AccessControl, Long> {

	// ==================== Original Query Methods ====================

	@Query("SELECT a FROM AccessControl a WHERE a.user.userKey = :userKey AND a.entityType = :entityType"
	+ " AND (:aclValue IS NULL OR a.acl = :aclValue)")
	List<AccessControl> getUserEntityACLs(@Param("userKey") Long userKey,
										  @Param("entityType") String entityType,
										  @Param("aclValue") String aclValue);

	@Query("SELECT a FROM AccessControl a WHERE a.role.roleKey = :roleId")
	List<AccessControl> getRoleACLs(@Param("roleId") Long roleId);

	@Query("SELECT a FROM AccessControl a WHERE a.role.roleKey = :roleKey AND a.entityType = :entityType"
	+ " AND (:aclValue IS NULL OR a.acl = :aclValue)")
	List<AccessControl> getRoleEntityACLs(@Param("roleKey") Long roleKey,
										  @Param("entityType") String entityType,
										  @Param("aclValue") String aclValue);

	@Query("SELECT a FROM AccessControl a WHERE (a.user.userKey = :userKey OR (:roleKey IS NOT NULL AND a.role.roleKey = :roleKey))"
	+ " AND a.entityType = :entityType AND (:aclValue IS NULL OR a.acl = :aclValue)")
	List<AccessControl> getACLsForUser(@Param("userKey") Long userKey,
									    @Param("roleKey") Long roleKey,
									    @Param("entityType") String entityType,
									    @Param("aclValue") String aclValue);

	@Query("SELECT COUNT(a) FROM AccessControl a WHERE (a.user.userKey = :userKey OR (:roleKey IS NOT NULL AND a.role.roleKey = :roleKey))"
	+ " AND a.entityType = :entityType AND (:aclValue IS NULL OR a.acl = :aclValue)")
	long countACLsForUser(@Param("userKey") Long userKey,
						 @Param("roleKey") Long roleKey,
						 @Param("entityType") String entityType,
						 @Param("aclValue") String aclValue);

	/**
	 * Equivalent to old Criteria API:
	 * Criteria criteria = session.createCriteria(AccessControl.class);
	 * Disjunction disj = Restrictions.disjunction();
	 * disj.add(Restrictions.eq("user.id", userKey));
	 * disj.add(Restrictions.eq("role.id", roleKey));
	 * criteria.add(disj);
	 * criteria.add(Restrictions.eq("entityType", entityType));
	 * if (aclValue != null) { criteria.add(Restrictions.eq("acl", aclValue)); }
	 * return criteria.list();
	 * 
	 * Gets the ACLs for a user using both the user-specific ACLs and merging
	 * with the Role-specific ACLs for the given user
	 * 
	 * @param user the user entity
	 * @param entityType the entity type
	 * @param aclValue the ACL value (can be null for all)
	 * @return list of AccessControl for user
	 */
	default List<AccessControl> getACLsForUser(Users user, String entityType, String aclValue) {
		if (user == null) {
			throw new IllegalArgumentException("User must be specified");
		}
		Long userKey = user.getUserKey();
		Long roleKey = user.getRole() != null ? user.getRole().getRoleKey() : null;
		return getACLsForUser(userKey, roleKey, entityType, aclValue);
	}

	/**
	 * Determines if the user has ACLs for the given entity type and aclValue.
	 * This does a count query so is faster to check than fetching all the ACLs.
	 * 
	 * Equivalent to old Criteria API:
	 * criteria.setProjection(Projections.rowCount());
	 * Long count = (Long) criteria.uniqueResult();
	 * return count.intValue() > 0;
	 * 
	 * @param user the user entity
	 * @param entityType the entity type
	 * @param aclValue the ACL value (can be null)
	 * @return true if user has ACLs, false otherwise
	 */
	default boolean doesUserHaveACLs(Users user, String entityType, String aclValue) {
		if (user == null) {
			throw new IllegalArgumentException("User must be specified");
		}
		Long userKey = user.getUserKey();
		Long roleKey = user.getRole() != null ? user.getRole().getRoleKey() : null;
		return hasAccessControl(userKey, roleKey, entityType, aclValue);
	}

	/**
	 * Check if ACL exists for user (by either user key OR role key) for given entity type.
	 * Equivalent to: criteria.setProjection(Projections.rowCount()) with uniqueResult > 0
	 * 
	 * @param userKey the user's key
	 * @param roleKey the user's role key
	 * @param entityType the entity type
	 * @param aclValue the ACL value (can be null for all)
	 * @return true if user has any matching ACLs
	 */
	@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END "
	     + "FROM AccessControl a "
	     + "WHERE (a.user.userKey = :userKey OR a.role.roleKey = :roleKey) "
	     + "AND a.entityType = :entityType "
	     + "AND (:aclValue IS NULL OR a.acl = :aclValue)")
	boolean hasAccessControl(@Param("userKey") Long userKey,
							 @Param("roleKey") Long roleKey,
							 @Param("entityType") String entityType,
							 @Param("aclValue") String aclValue);

	/**
	 * Find all ACLs for a user using Disjunction (user OR role).
	 * Equivalent to old getACLsForUserCriteria() with Disjunction pattern.
	 * 
	 * Criteria code:
	 * Disjunction disj = Restrictions.disjunction();
	 * disj.add(Restrictions.eq("user.id", userKey));
	 * disj.add(Restrictions.eq("role.id", roleKey));
	 * criteria.add(disj);
	 * 
	 * @param userKey the user's key
	 * @param roleKey the user's role key
	 * @param entityType the entity type
	 * @param aclValue the ACL value (null for all)
	 * @return list of matching AccessControl records
	 */
	@Query("SELECT a FROM AccessControl a "
	     + "WHERE (a.user.userKey = :userKey OR a.role.roleKey = :roleKey) "
	     + "AND a.entityType = :entityType "
	     + "AND (:aclValue IS NULL OR a.acl = :aclValue)")
	List<AccessControl> findACLsForUserDisjunction(@Param("userKey") Long userKey,
												   @Param("roleKey") Long roleKey,
												   @Param("entityType") String entityType,
												   @Param("aclValue") String aclValue);

	/**
	 * Count ACLs for user using Disjunction pattern (user OR role).
	 * Equivalent to: criteria.setProjection(Projections.rowCount())
	 * 
	 * @param userKey the user's key
	 * @param roleKey the user's role key
	 * @param entityType the entity type
	 * @param aclValue the ACL value (null for all)
	 * @return count of matching AccessControl records
	 */
	@Query("SELECT COUNT(a) FROM AccessControl a "
	     + "WHERE (a.user.userKey = :userKey OR a.role.roleKey = :roleKey) "
	     + "AND a.entityType = :entityType "
	     + "AND (:aclValue IS NULL OR a.acl = :aclValue)")
	int countACLsForUserDisjunction(@Param("userKey") Long userKey,
								    @Param("roleKey") Long roleKey,
								    @Param("entityType") String entityType,
								    @Param("aclValue") String aclValue);

	/**
	 * Find first ACL for user with Disjunction (efficient existence check).
	 * More efficient than count for simple existence validation.
	 * 
	 * @param userKey the user's key
	 * @param roleKey the user's role key
	 * @param entityType the entity type
	 * @return first matching AccessControl or empty Optional
	 */
	@Query("SELECT a FROM AccessControl a "
	     + "WHERE (a.user.userKey = :userKey OR a.role.roleKey = :roleKey) "
	     + "AND a.entityType = :entityType")
	Optional<AccessControl> findFirstACLForUserDisjunction(@Param("userKey") Long userKey,
													       @Param("roleKey") Long roleKey,
													       @Param("entityType") String entityType);
}
