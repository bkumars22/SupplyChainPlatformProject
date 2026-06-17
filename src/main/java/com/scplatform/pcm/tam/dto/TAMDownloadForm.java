/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import com.scplatform.pcm.tam.entity.TAMAllocation;
import lombok.Data;

import java.util.*;

@Data
public class TAMDownloadForm extends SearchForm {

	private String[] selectedPageKeys;
	private String downloadOption;
	private Long supplierCount;
	private Long itemCount;
	private Boolean globalRegionCheck;
	private String fileLocation;
	private TAMAllocation tamAllocation;
	private String searchStartDate = null;
	private Date fiscalPeriodStartDate = null;
	private Date currentSearchDate = null;
	private Map<String, List<TAMHeader>> header = new LinkedHashMap<>();
	private Map<BusinessEntity, Set<Item>> businessEntityItemList = new LinkedHashMap<>();
}
