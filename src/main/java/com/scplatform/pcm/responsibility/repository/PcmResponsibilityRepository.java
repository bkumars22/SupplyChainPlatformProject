/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.responsibility.repository;

import java.util.List;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.responsibility.entity.PcmResponsibility;
import com.scplatform.pcm.role.entity.Role;

/**
 * Spring Data JPA Repository for PcmResponsibility entity.
 * Provides data access operations for PcmResponsibility entities.
 */
@Repository
public interface PcmResponsibilityRepository extends JpaRepository<PcmResponsibility, String>, JpaSpecificationExecutor<PcmResponsibility> {

	/**
	 * Find responsibilities with optional filtering by role, responsibility types, and exclusions.
	 * 
	 * @param role the role to filter by (optional)
	 * @param responsibilityTypes list of responsibility types to include (optional)
	 * @param exclude list of responsibility keys to exclude (optional)
	 * @return list of PcmResponsibility entities ordered by displayOrder
	 */
	default List<PcmResponsibility> findResponsibilities(Role role, List<String> responsibilityTypes,
			List<String> exclude) {
		Specification<PcmResponsibility> spec = (root, query, criteriaBuilder) -> {
			Predicate predicate = criteriaBuilder.conjunction();
			
			if (role != null) {
				predicate = criteriaBuilder.and(predicate,
					criteriaBuilder.equal(root.join("roles").get("roleKey"), role.getRoleKey()));
			}
			
			if (responsibilityTypes != null && !responsibilityTypes.isEmpty()) {
				predicate = criteriaBuilder.and(predicate,
					root.get("responsibilityType").in(responsibilityTypes));
			}
			
			if (exclude != null && !exclude.isEmpty()) {
				predicate = criteriaBuilder.and(predicate,
					criteriaBuilder.not(root.get("responsibilityKey").in(exclude)));
			}
			
			query.orderBy(criteriaBuilder.asc(root.get("displayOrder")));
			return predicate;
		};
		
		return findAll(spec);
	}

}
