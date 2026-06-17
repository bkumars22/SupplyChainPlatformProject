/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.dto;

import java.util.List;
import com.scplatform.pcm.common.entity.FlexAttributeDefn;
import com.scplatform.pcm.common.entity.FlexAttributeManager;
import com.scplatform.pcm.searchframework.dto.SearchForm;

/**
 * Form for BomSearch Page
 * @author averma
 *
 */
public class BomSearchForm extends SearchForm {
	
	public List<FlexAttributeDefn> getFlexAttributeBomDefinitions() {
		return FlexAttributeManager.BOM.getFlexAttributeDefinitionList();
	}
}
