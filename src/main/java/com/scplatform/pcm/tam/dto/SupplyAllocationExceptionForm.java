/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import com.scplatform.pcm.searchframework.dto.SearchForm;
import com.scplatform.pcm.site.entity.Site;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class SupplyAllocationExceptionForm extends SearchForm {
	private Site site;
	private String fgName  ;
	private String startDate;
	private String endDate;
	private Double allocation;
	private Timestamp updateDate;
	private String updateBY;
	private boolean cheackRows;
	private String messagePopup;
}
