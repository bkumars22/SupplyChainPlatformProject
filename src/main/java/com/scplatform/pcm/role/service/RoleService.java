/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.role.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.role.repository.RoleRepository;

/**
 * Service class for Role entity operations.
 * Provides business logic for role management using Spring Data JPA.
 * Migrated from deprecated Hibernate Criteria API.
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";
    public static final String RESULT_LIST = "resultList";
    public static final String COUNT = "count";

    private final RoleRepository roleRepository;

    public Map<String, Object> findRolesByIdOrName(BusinessEntity be, String role,
            MultiValueMap<String, String> params) {

        List<Role> resultList;
        long count;

        // Determine if we need to exclude ADMIN role
        // Exclude ADMIN role if the business type is not OPERATOR or ENTERPRISE
        // to prevent the ADMIN role from being assigned to a SUPPLIER user
        boolean excludeAdmin = false;
        if (be != null) {
            excludeAdmin = be.getBusinessEntityTypeKey() != BusinessEntity.OPERATOR_TYPE
                    && be.getBusinessEntityTypeKey() != BusinessEntity.ENTERPRISE_TYPE;
        }

        // Fetch roles based on search filter
        if (role != null && !role.trim().isEmpty()) {
            // Search with role filter - add % wildcards for ILIKE behavior (anywhere match)
            String searchText = "%" + role + "%";
            if (excludeAdmin) {
                count = roleRepository.countByRoleIdOrRoleNameContainingIgnoreCaseExcludingAdmin(searchText);
                resultList = roleRepository.findByRoleIdOrRoleNameContainingIgnoreCaseExcludingAdmin(searchText);
            } else {
                count = roleRepository.countByRoleIdOrRoleNameContainingIgnoreCase(searchText);
                resultList = roleRepository.findByRoleIdOrRoleNameContainingIgnoreCase(searchText);
            }
        } else {
            // No search filter - fetch all roles ordered by roleId
            if (excludeAdmin) {
                count = roleRepository.countAllExcludingAdmin();
                resultList = roleRepository.findAllExcludingAdmin();
            } else {
                count = roleRepository.count();
                resultList = roleRepository.findAllOrderByRoleId();
            }
        }

        // Apply pagination if parameters provided
        if (params != null) {
            Integer offset = getIntegerParam(params, OFFSET, 0);
            Integer limit = getIntegerParam(params, LIMIT, null);

            if (offset > 0 || limit != null) {
                resultList = applyPagination(resultList, offset, limit);
            }
        }

        // Build result map with count and resultList
        Map<String, Object> results = new HashMap<>();
        results.put(COUNT, count);
        results.put(RESULT_LIST, resultList);

        return results;
    }

    /**
     * Helper method to apply pagination (offset and limit) to a list.
     *
     * @param list the list to paginate
     * @param offset the starting index (0-based)
     * @param limit the maximum number of items (null means no limit)
     * @return the paginated sublist
     */
    private List<Role> applyPagination(List<Role> list, int offset, Integer limit) {
        int startIndex = Math.min(offset, list.size());
        int endIndex = limit != null ? Math.min(startIndex + limit, list.size()) : list.size();

        if (startIndex >= list.size()) {
            return new ArrayList<>();
        }

        return list.subList(startIndex, endIndex);
    }

    /**
     * Helper method to extract and parse integer parameter from MultiValueMap.
     *
     * @param params the parameter map
     * @param key the parameter key to retrieve
     * @param defaultValue the default value if not found or invalid
     * @return the parsed integer value or default value
     */
    private Integer getIntegerParam(MultiValueMap<String, String> params, String key, Integer defaultValue) {
        String value = params.getFirst(key);
        if (value != null && !value.trim().isEmpty()) {
            try {
                return Integer.valueOf(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer parameter value for key: " + key + ", value: " + value);
            }
        }
        return defaultValue;
    }
}

