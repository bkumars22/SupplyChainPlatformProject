/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.role.entity.Role;

/**
 * Stub implementation of CostExceptionConfigRepository.
 * The original implementation was XML-config driven and needs to be migrated.
 */
@Component
public class CostExceptionConfigRepositoryImpl implements CostExceptionConfigRepository {

    @Override
    public Boolean isActionAllowedForRole(CostException costException, String action, Role role) {
        return Boolean.FALSE;
    }

    @Override
    public List<String> getRolesForCostExceptionAction(String costType, String requestType, String action) {
        return Collections.emptyList();
    }

    @Override
    public Map<String, List<String>> getEmailForCostExceptionAction(String costType, String requestType, String action) {
        return Collections.emptyMap();
    }

    @Override
    public Boolean checkODMEmailAttachmentValidation(String costType, String requestType, String action) {
        return Boolean.FALSE;
    }

    @Override
    public List<String> getSuperRoleFromExceptionConfigFile(String paramString) {
        return Collections.emptyList();
    }

    @Override
    public Set<String> getRolesForCostExceptionActionExceptSuperRoles(String costType, String requestType, String action) {
        return Collections.emptySet();
    }
}
