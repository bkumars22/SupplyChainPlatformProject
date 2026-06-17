/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.rebate.dto;


import com.scplatform.pcm.searchframework.dto.SearchForm;
import lombok.Data;

@Data
public class RebateProgramSearchForm extends SearchForm {
	
	private String ownerUserId;
}
