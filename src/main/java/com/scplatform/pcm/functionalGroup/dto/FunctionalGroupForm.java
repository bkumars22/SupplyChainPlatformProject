/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.functionalGroup.dto;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import com.scplatform.pcm.tam.entity.FunctionalGroupSupplierAllocation;
import com.scplatform.pcm.tam.entity.TAMAllocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class FunctionalGroupForm extends SearchForm {

	private Long functionalGroupId;
	private String dynamicTitleName;
	private FunctionalGroup functionalGroup;
	private String[] parents;
	private String functionalGroupType;
	private String itemList;
	private String backAction;
	private String functionalGroupName;
	private String functionalGroupDescription;
	private String status;
	private String parentName;
	private String parentDesc;
	private String parentType;
	private String parentPurpose;
	private String newItems;
	private String parentListByPopUpList;
	private Boolean unsavedData;
	private String deleteItem;
	private String[] selectedPageKeys;
	private String selectedParent = "";
	private FunctionalGroup cacheObject;
	private String errorFileName = "";
	private Map<Long, String> tamExistValues;
	private Set<Item> nonFGItems;
	private Set<Long> sortedFGItems;
	private Set<String> addedItemKeys;
	private String hasItemAcessError = "";
	private List<Item> selectedItems;
	private Set<String> fgSiteKeys;
	private String fileLocation;
	private String errorMessageActivationFG = "";
	private String fgPlatform;
	private String fgLob;
	private String parentItemOrODMPartKey;

	public void reset() {
		this.functionalGroup = null;
		this.dynamicTitleName = "";
		this.functionalGroupId = null;
		this.parents = null;
		this.functionalGroupType = null;
		this.itemList = null;
		this.functionalGroupName = null;
		this.status = null;
		this.parentName = null;
		this.parentDesc = null;
		this.parentType = null;
		this.parentPurpose = null;
		this.newItems = null;
		this.parentListByPopUpList = null;
		this.unsavedData = false;
		this.deleteItem = null;
		this.selectedParent = "";
		this.cacheObject = null;
		this.tamExistValues = null;
		this.nonFGItems = null;
		this.sortedFGItems = null;
		this.hasItemAcessError = "";
		this.selectedItems = null;
		this.fgSiteKeys = null;
		this.fileLocation = null;
		this.fgPlatform = null;
		this.fgLob = null;
		this.parentItemOrODMPartKey = null;
		this.addedItemKeys=null;
	}

	public String getFileLocation() {
		return fileLocation;
	}
	
	public String getFgLob() {
		return fgLob;
	}

	public void setFgLob(String fgLob) {
		this.fgLob = fgLob;
	}


	public String getFgPlatform() {
		return fgPlatform;
	}

	public void setFgPlatform(String fgPlatform) {
		this.fgPlatform = fgPlatform;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public String getHasItemAcessError() {
		return hasItemAcessError;
	}

	public void setHasItemAcessError(String hasItemAcessError) {
		this.hasItemAcessError = hasItemAcessError;
	}

	public Long getFunctionalGroupId() {
		return functionalGroupId;
	}

	public void setFunctionalGroupId(Long functionalGroupId) {
		this.functionalGroupId = functionalGroupId;
	}

	public String getDynamicTitleName() {
		return dynamicTitleName;
	}

	public void setDynamicTitleName(String dynamicTitleName) {
		this.dynamicTitleName = dynamicTitleName;
	}

	public FunctionalGroup getFunctionalGroup() {
		return functionalGroup;
	}

	public void setFunctionalGroup(FunctionalGroup functionalGroup) {
		this.functionalGroup = functionalGroup;
	}

	public void setParentList(String[] parents) {
		this.parents = parents;
	}

	public String getFunctionalGroupType() {
		return functionalGroupType;
	}

	public void setFunctionalGroupType(String functionalGroupType) {
		this.functionalGroupType = functionalGroupType;
	}

	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public String[] getParents() {
		return parents;
	}

	public String getFunctionalGroupName() {
		return functionalGroupName;
	}

	public void setFunctionalGroupName(String functionalGroupName) {
		this.functionalGroupName = functionalGroupName;
	}

	public String getFunctionalGroupDescription() {
		return functionalGroupDescription;
	}

	public void setFunctionalGroupDescription(String functionalGroupDescription) {
		this.functionalGroupDescription = functionalGroupDescription;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentDesc() {
		return parentDesc;
	}

	public void setParentDesc(String parentDesc) {
		this.parentDesc = parentDesc;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public String getParentPurpose() {
		return parentPurpose;
	}

	public void setParentPurpose(String parentPurpose) {
		this.parentPurpose = parentPurpose;
	}

	public void setParents(String[] parents) {
		this.parents = parents;
	}

	public String getNewItems() {
		return newItems;
	}

	public void setNewItems(String newItems) {
		this.newItems = newItems;
	}

	public String getParentListByPopUpList() {
		return parentListByPopUpList;
	}

	public void setParentListByPopUpList(String parentListByPopUpList) {
		this.parentListByPopUpList = parentListByPopUpList;
	}

	public Boolean getUnsavedData() {
		return unsavedData;
	}

	public void setUnsavedData(Boolean unsavedData) {
		this.unsavedData = unsavedData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDeleteItem() {
		return deleteItem;
	}

	public void setDeleteItem(String deleteItem) {
		this.deleteItem = deleteItem;
	}

	@Override
	public String[] getSelectedPageKeys() {
		return selectedPageKeys;
	}

	@Override
	public void setSelectedPageKeys(String[] selectedPageKeys) {
		this.selectedPageKeys = selectedPageKeys;
	}

	public String getSelectedParent() {
		return selectedParent;
	}

	public void setSelectedParent(String selectedParent) {
		this.selectedParent = selectedParent;
	}

	public FunctionalGroup getCacheObject() {
		return cacheObject;
	}

	public void setCacheObject(FunctionalGroup cacheObject) {
		this.cacheObject = cacheObject;
	}

	public String getItemList() {
		return itemList;
	}

	public void setItemList(String itemList) {
		this.itemList = itemList;
	}

	public String getErrorFileName() {
		return errorFileName;
	}

	public void setErrorFileName(String errorFileName) {
		this.errorFileName = errorFileName;
	}

	public Map<Long, String> getTamExistValues() {
		return tamExistValues;
	}

	public void setTamExistValues(Map<Long, String> tamExistValues) {
		this.tamExistValues = tamExistValues;
	}

	public Set<Item> getNonFGItems() {
		return nonFGItems;
	}

	public void setNonFGItems(Set<Item> nonFGItems) {
		this.nonFGItems = nonFGItems;
	}

	public Set<Long> getSortedFGItems() {
		return sortedFGItems;
	}

	public void setSortedFGItems(Set<Long> sortedFGItems) {
		this.sortedFGItems = sortedFGItems;
	}

	public List<Item> getSelectedItems() {
		return selectedItems;
	}

	public void setSelectedItems(List<Item> selectedItems) {
		this.selectedItems = selectedItems;
	}

	public Set<String> getFgSiteKeys() {
		return fgSiteKeys;
	}

	public void setFgSiteKeys(Set<String> fgSiteKeys) {
		this.fgSiteKeys = fgSiteKeys;
	}

	public String getErrorMessageActivationFG() {
		return errorMessageActivationFG;
	}

	public void setErrorMessageActivationFG(String errorMessageActivationFG) {
		this.errorMessageActivationFG = errorMessageActivationFG;
	}
	
	public void setAddedItemKeys(Set<String> addedItemKeys) {
		this.addedItemKeys = addedItemKeys;
	}
	
	public Set<String> getAddedItemKeys(){
		return addedItemKeys;
	}
	
	public Boolean isTAMExists(FunctionalGroup functionalGroup) {
		Set<TAMAllocation> tamAllocationList = functionalGroup.getAllocations();
		if(tamAllocationList == null)
			return false;
		return tamAllocationList.stream().anyMatch(tamAllocation -> {
			Set<FunctionalGroupSupplierAllocation> supplierAllocationList = tamAllocation.getSupplierAllocations();
			return isSupplierAllocationExists(supplierAllocationList) || isItemAllocationExists(supplierAllocationList);
		});
	}

	private Boolean isItemAllocationExists(Set<FunctionalGroupSupplierAllocation> supplierAllocationList) {
		return supplierAllocationList.stream().anyMatch(supplierAllocation -> {
			return supplierAllocation.getItemAllocations().stream().anyMatch(itemAllocation -> {
				return itemAllocation.getAllocation() != null
						&& (Double.compare(itemAllocation.getAllocation(), Double.valueOf(0.0)) > 0);
			});
		});
	}

	private Boolean isSupplierAllocationExists(Set<FunctionalGroupSupplierAllocation> supplierAllocationList) {
		return supplierAllocationList.stream().anyMatch(supplierAllocation -> {
			return supplierAllocation.getAllocation() != null
					&& (Double.compare(supplierAllocation.getAllocation(), Double.valueOf(0.0)) > 0);
		});
	}

	public String getParentItemOrODMPartKey() {
		return parentItemOrODMPartKey;
	}

	public void setParentItemOrODMPartKey(String parentItemOrODMPartKey) {
		this.parentItemOrODMPartKey = parentItemOrODMPartKey;
	}
	
	public boolean isAddedItemPresentInResult(Long itemKey, Long fgKey, String fgName) {
		if (addedItemKeys == null || addedItemKeys.isEmpty()) {
			return false;
		} else {
			for (String oneOfAddedItemKey : addedItemKeys) {
				if (oneOfAddedItemKey.equals(String.valueOf(itemKey))) {
					return true;
				}
			}
			return false;
		}
	}
}
