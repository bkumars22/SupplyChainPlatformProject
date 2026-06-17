/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.parentFunctionalGroup.dto;


import com.scplatform.pcm.parentFunctionalGroup.entity.ParentFunctionalGroup;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import lombok.Data;

@Data
public class ParentFunctionalGroupForm extends SearchForm {

	private Long parentFunctionalGroupId;
	private String dynamicTitleName;
	private ParentFunctionalGroup parentFunctionalGroup;
	private String backAction;
	private Boolean unsavedData;
	private String[] selectedPageKeys;
	private String selectedFunctionalGroupId;
	private String parentGroupName;
	private String parentGroupType;
	private String parentGroupDescription;
	private String parentGroupPurpose;
	private String deletefunctionalGroupList;
	private String newFunctionalGroupList;
	private String deleteFunctionalGroup;
	private ParentFunctionalGroup cacheObject;
	private Boolean isTAMRedirect = false;

	public void reset() {
		this.parentFunctionalGroup = null;
		this.unsavedData = false;
		this.parentFunctionalGroupId = null;
		this.selectedFunctionalGroupId = null;
		this.parentGroupName = null;
		this.parentGroupType = null;
		this.parentGroupDescription = null;
		this.parentGroupPurpose = null;
		this.deletefunctionalGroupList = null;
		this.newFunctionalGroupList = null;
		this.deleteFunctionalGroup = null;
		this.cacheObject = null;
	}
}
