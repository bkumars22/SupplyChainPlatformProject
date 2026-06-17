/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.businessEntity.dto;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;

import java.util.List;

@Data
public class BusinessAdminForm extends SearchForm {

	private BusinessEntity selectedBusiness;
	private String selectedBusinessKey;
	private String selectedContactKey;
	private String[] alternateNames;
	private boolean contactChanged = false;
	private List<String> alternateNamesList;
	private String alternateNamesString;

	public void reset(HttpServletRequest request) {
		super.reset(request);
		selectedBusinessKey = null;
		selectedContactKey = null;
		alternateNames = null;
		contactChanged = false;
		alternateNamesList = null;
		alternateNamesString = null;
	}
}
