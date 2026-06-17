/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.user.dto;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.ItemCategory;
import com.scplatform.pcm.platform.entity.Platform;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.user.entity.Users;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class UserAdminForm extends SearchForm {
	private Boolean unsavedData;
	private Long selectedUserKey;
	private Map userPreferences;
	private Users selectedUser;
	private Role selectedUserRole;
	private String importUserId;
	private String selectedContactKey;
	private String selectedBusinessKey;
	private Map<Long, String> agentOfBusinesses;
	private Long[] agentKeys;
	private List<ItemCategory> availableCategories;
	private String[] categoryKeys;
	private List<Site> availableSites;
	private String[] siteKeys;
	private List<Platform> availablePlatforms;
	private String[] platformKeys;

	private List<String> roleSiteKeys;
	private List<String> roleCategoryKeys;
	private List<String> rolePlatformKeys;

	private String selectedTabId;
	private List<BusinessEntity> businessEntityList = new ArrayList<>();
	private String agentKeyString;
	private String[] keys;

	public void reset(HttpServletRequest request) {
		super.reset(request);
		selectedUserKey = null;
		importUserId = null;
		selectedContactKey = null;
		selectedBusinessKey = null;
		agentKeys = null;
		selectedTabId = null;
		categoryKeys = null;
		siteKeys = null;
		platformKeys = null;
		unsavedData = false;
		businessEntityList = null;
		agentKeyString = null;
	}
}
