/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.tam.entity.TAMAllocation;

import java.util.*;


public class TAMAllocationHistoryForm {

	private List<Site> regionList = new ArrayList<>();
	private String siteType;
	private TAMAllocation allocation;
	private String itemNumber;
	private String groupName;
	private TAMAllocation cacheTAM;
	private String region;
	private String siteDescription;
	private Double minRange;
	private Double maxRange;
	private List<Date> calenderMonthHeader = new ArrayList<>();
	private List<Date> calenderDateHeader = new ArrayList<>();
	private String allocationStatus;
	protected Map<String, TamSupplierData> tamData = new LinkedHashMap<>();
	private Map<String, List<TAMHeader>> header = new LinkedHashMap<>();
	private Map<BusinessEntity, Set<Item>> businessEntityItemList = new LinkedHashMap<>();
	private Date currentDate;
	private String dataLocation;
	private Boolean unsavedData;
	private Boolean freshSearch;
	private Map<String, Boolean> inheritValues = new LinkedHashMap<>();
	private Map<Site, List<Site>> allSites = new LinkedHashMap<>();
	private List<Site> sitesList = new ArrayList<>();
	private String[] siteList;
	private String copyType;
	private String startDate;
	private String endDate;
	private List<BusinessEntity> inactiveEOLSupplier;
	private Set<String> currentInheritedLevel;
	private Boolean hideSupplierWithNoAllocationPref;
	private Boolean hideItemPref;
	private String cacheRegion;
	private String selectedFgType = null;
	private List<String> fgTypeOption = new LinkedList<String>();
	private String xlobDisableSiteLevel = null;
	private String searchStartDate = null;
	private Date fiscalPeriodStartDate = null;
	private Date currentSearchDate = null;
	private Boolean pastScreen = null;

	public void reset() {
		allocation = null;
		currentDate = null;
		dataLocation = null;
		groupName = null;
		siteDescription = null;
		siteType = null;
		region = null;
		unsavedData = false;
		freshSearch = true;
		allSites = null;
		copyType = null;
		siteList = null;
		itemNumber = null;
		inactiveEOLSupplier = null;
		hideSupplierWithNoAllocationPref = null;
		hideItemPref = null;
		sitesList = null;
		selectedFgType = null;
		fgTypeOption = null;
		xlobDisableSiteLevel = null;
		searchStartDate = null;
		fiscalPeriodStartDate = null;
		currentSearchDate = null;
		pastScreen = null;
	}

	public Date getCurrentSearchDate() {
		return currentSearchDate;
	}

	public void setCurrentSearchDate(Date currentSearchDate) {
		this.currentSearchDate = currentSearchDate;
	}

	public Boolean getPastScreen() {
		return pastScreen;
	}

	public void setPastScreen(Boolean pastScreen) {
		this.pastScreen = pastScreen;
	}

	public Date getFiscalPeriodStartDate() {
		return fiscalPeriodStartDate;
	}

	public void setFiscalPeriodStartDate(Date fiscalPeriodStartDate) {
		this.fiscalPeriodStartDate = fiscalPeriodStartDate;
	}

	public String getSearchStartDate() {
		return searchStartDate;
	}

	public void setSearchStartDate(String searchStartDate) {
		this.searchStartDate = searchStartDate;
	}

	public void resetBeforeSearch() {
		this.setItemNumber(null);
		this.setDataLocation(null);
		this.setAllocation(null);
		this.setCacheTAM(null);
		this.businessEntityItemList = new LinkedHashMap<>();
		this.header = new LinkedHashMap<>();
		this.tamData = new LinkedHashMap<>();
		this.setCurrentInheritedLevel(null);
	}

	public List<Site> getSitesList() {
		return sitesList;
	}

	public void setSitesList(List<Site> sitesList) {
		this.sitesList = sitesList;
	}

	public String getSiteType() {
		return siteType;
	}

	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}

	public TAMAllocation getAllocation() {
		return allocation;
	}

	public void setAllocation(TAMAllocation allocation) {
		this.allocation = allocation;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public TAMAllocation getCacheTAM() {
		return cacheTAM;
	}

	public void setCacheTAM(TAMAllocation cacheTAM) {
		this.cacheTAM = cacheTAM;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<Site> getRegionList() {
		return regionList;
	}

	public void setRegionList(List<Site> regionList) {
		this.regionList = regionList;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSiteDescription() {
		return siteDescription;
	}

	public void setSiteDescription(String siteDescription) {
		this.siteDescription = siteDescription;
	}

	public Double getMinRange() {
		return minRange;
	}

	public void setMinRange(Double minRange) {
		this.minRange = minRange;
	}

	public Double getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Double maxRange) {
		this.maxRange = maxRange;
	}

	public List<Date> getCalenderMonthHeader() {
		return calenderMonthHeader;
	}

	public void setCalenderMonthHeader(List<Date> calenderMonthHeader) {
		this.calenderMonthHeader = calenderMonthHeader;
	}

	public List<Date> getCalenderDateHeader() {
		return calenderDateHeader;
	}

	public void setCalenderDateHeader(List<Date> calenderDateHeader) {
		this.calenderDateHeader = calenderDateHeader;
	}

	public String getAllocationStatus() {
		return allocationStatus;
	}

	public void setAllocationStatus(String allocationStatus) {
		this.allocationStatus = allocationStatus;
	}

	public TamSupplierData getSupplierData(String key) {
		TamSupplierData supplierData = tamData.get(key);
		if (supplierData == null) {
			supplierData = new TamSupplierData();
			tamData.put(key, supplierData);
		}
		return supplierData;
	}

	public Map<String, List<TAMHeader>> getHeader() {
		return header;
	}

	public void setHeader(Map<String, List<TAMHeader>> header) {
		this.header = header;
	}

	public Map<BusinessEntity, Set<Item>> getBusinessEntityItemList() {
		return businessEntityItemList;
	}

	public void setBusinessEntityItemList(Map<BusinessEntity, Set<Item>> businessEntityItemList) {
		this.businessEntityItemList = businessEntityItemList;
	}

	public Date getCurrentDate() {
		if (pastScreen != null && pastScreen) {
			return currentDate;
		} else {
			Calendar currentDate = Calendar.getInstance();
			currentDate.set(Calendar.HOUR_OF_DAY, 0);
			currentDate.set(Calendar.MINUTE, 0);
			currentDate.set(Calendar.SECOND, 0);
			currentDate.set(Calendar.MILLISECOND, 0);
			return currentDate.getTime();
		}
	}

	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	public String getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(String dataLocation) {
		this.dataLocation = dataLocation;
	}

	public Boolean getUnsavedData() {
		return unsavedData;
	}

	public void setUnsavedData(Boolean unsavedData) {
		this.unsavedData = unsavedData;
	}

	public Map<String, Boolean> getInheritValues() {
		return inheritValues;
	}

	public void setInheritValues(Map<String, Boolean> inheritValues) {
		this.inheritValues = inheritValues;
	}

	public Map<Site, List<Site>> getAllSites() {
		return allSites;
	}

	public void setAllSites(Map<Site, List<Site>> allSites) {
		this.allSites = allSites;
	}

	public String[] getSiteList() {
		return siteList;
	}

	public void setSiteList(String[] siteList) {
		this.siteList = siteList;
	}

	public String getCopyType() {
		return copyType;
	}

	public void setCopyType(String copyType) {
		this.copyType = copyType;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<BusinessEntity> getInactiveEOLSupplier() {
		return inactiveEOLSupplier;
	}

	public void setInactiveEOLSupplier(List<BusinessEntity> inactiveEOLSupplier) {
		this.inactiveEOLSupplier = inactiveEOLSupplier;
	}

	public Boolean getFreshSearch() {
		return freshSearch;
	}

	public void setFreshSearch(Boolean freshSearch) {
		this.freshSearch = freshSearch;
	}

	public Boolean getHideSupplierWithNoAllocationPref() {
		return hideSupplierWithNoAllocationPref;
	}

	public void setHideSupplierWithNoAllocationPref(Boolean hideSupplierWithNoAllocationPref) {
		this.hideSupplierWithNoAllocationPref = hideSupplierWithNoAllocationPref;
	}

	public Boolean getHideItemPref() {
		return hideItemPref;
	}

	public void setHideItemPref(Boolean hideItemPref) {
		this.hideItemPref = hideItemPref;
	}

	public Set<String> getCurrentInheritedLevel() {
		return currentInheritedLevel;
	}

	public void setCurrentInheritedLevel(Set<String> currentInheritedLevel) {
		this.currentInheritedLevel = currentInheritedLevel;
	}

	public String getCacheRegion() {
		return cacheRegion;
	}

	public void setCacheRegion(String cacheRegion) {
		this.cacheRegion = cacheRegion;
	}

	public List<String> getFgTypeOption() {
		return fgTypeOption;
	}

	public void setFgTypeOption(List<String> fgTypeOption) {
		this.fgTypeOption = fgTypeOption;
	}

	public String getXlobDisableSiteLevel() {
		return xlobDisableSiteLevel;
	}

	public void setXlobDisableSiteLevel(String xlobDisableSiteLevel) {
		this.xlobDisableSiteLevel = xlobDisableSiteLevel;
	}

	public String getSelectedFgType() {
		return selectedFgType;
	}

	public void setSelectedFgType(String selectedFgType) {
		this.selectedFgType = selectedFgType;
	}
}