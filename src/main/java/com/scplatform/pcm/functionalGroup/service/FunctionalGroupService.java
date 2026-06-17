/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.functionalGroup.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.functionalGroup.repository.FunctionalGroupRepository;
import com.scplatform.pcm.item.entity.Item;

@Service
public class FunctionalGroupService {

	@Autowired
	private FunctionalGroupRepository functionalGroupRepository;

	public List<FunctionalGroup> getFunctionalGroupListByParent(Long parentID) {
		return functionalGroupRepository.getFunctionalGroupListByParent(parentID);
	}

	public List<FunctionalGroup> getFunctionalGroupByItemAndFGType(Item item, String fgType) {
		return functionalGroupRepository.getFunctionalGroupByItemAndFGType(item, fgType);
	}

	public List<FunctionalGroup> getCFGListByItem(String itemNumber) {
		return functionalGroupRepository.getCFGListByItem(itemNumber);
	}

	public List<FunctionalGroup> getXLOBListByItem(String itemNumber) {
		return functionalGroupRepository.getXLOBListByItem(itemNumber);
	}

	/**
	 * Generates a title string based on configured fields. The configuration is
	 * read from "scplatform.functionalGroup.audit.title" property, which should
	 * contain a comma-separated list of field names. If the property is not
	 * set, defaults to "name".
	 * 
	 * Example configurations: - "name" - "name,functionalGroupExternalId" -
	 * "name,functionalGroupExternalId,description"
	 * 
	 * The method constructs a string by concatenating the specified fields and
	 * their values, separated by commas. Null values are skipped.
	 * 
	 * @param functionalGroup the functional group entity
	 * @param configUtil the configuration utility
	 * @return A formatted title string.
	 */
	public String getTitle(FunctionalGroup functionalGroup, PcmConfigUtil configUtil) {
		// Default list with "name"
		List<String> titles = configUtil.getList(
				"scplatform.functionalGroup.audit.title",
				Arrays.asList("name")
		);

		// Map each field name to its value
		Map<String, String> fieldMap = new LinkedHashMap<>();
		fieldMap.put("name", functionalGroup.getName());
		fieldMap.put("functionalGroupId", functionalGroup.getFunctionalGroupExternalId());
		fieldMap.put("description", functionalGroup.getDescription());
		fieldMap.put("type", functionalGroup.getType());
		fieldMap.put("status", functionalGroup.getStatus());
		fieldMap.put("platform", functionalGroup.getPlatform() != null ? functionalGroup.getPlatform().getPlatformName() : "");

		// Collect non-null entries into a list
		List<String> parts = new ArrayList<>();
		for (String key : titles) {
			String value = fieldMap.get(key);
			if (value != null) { // skip nulls
				parts.add(key + "=" + value);
			}
		}

		// Join with comma separator (no trailing comma issue)
		return String.join(", ", parts);
	}
}
