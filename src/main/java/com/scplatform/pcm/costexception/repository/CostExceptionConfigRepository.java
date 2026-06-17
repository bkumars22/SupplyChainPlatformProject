/*
 *
 * created on Dec 22, 2020
 * 
 * Copyright (c) 2000-2018, by E2open LLC.
 * All rights reserved.
 */
package com.scplatform.pcm.costexception.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.role.entity.Role;

public interface CostExceptionConfigRepository {
	public Boolean isActionAllowedForRole(CostException costException, String action, Role role);
	
	public List<String> getRolesForCostExceptionAction(String costType, String requestType, String action);
	
	public Map<String, List<String>> getEmailForCostExceptionAction(String costType, String requestType, String action);
	
	Boolean checkODMEmailAttachmentValidation(String costType, String requestType, String action);
	
	List<String> getSuperRoleFromExceptionConfigFile(String paramString);
	
	public Set<String> getRolesForCostExceptionActionExceptSuperRoles(String costType, String requestType, String action);
}
