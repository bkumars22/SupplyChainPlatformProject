/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import com.scplatform.pcm.common.entity.CompareManager;
import com.scplatform.pcm.common.entity.FlexAttributeDefn;
import com.scplatform.pcm.common.entity.FlexAttributeManager;
import com.scplatform.pcm.searchframework.dto.MdmManagementForm;
import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.pcm.bom.entity.PcmDefectType;
//import com.scplatform.pcm.bom.service.BomCompareResult;

@SuppressWarnings("serial")
public class BomManagementForm extends MdmManagementForm {
	protected boolean unsavedData;
	protected boolean mergeEnabled;
	protected Long firstBomCompareKey;
	protected String firstBomCompareName;
	//protected BomCompareResult compareResult;
	protected Map<Integer, Boolean> selectedLines = new HashMap<Integer, Boolean>();
	protected String lineEvent;
	protected String lineEventMessage;
	protected Map<String, PcmDefectType> attritionDefectTypesMap;
	protected UiMessages lineMessages = new UiMessages();
	protected boolean downloadInProgress = false;
	protected boolean compareFlatBom = false;
	protected String firstBomCompareVersion;
	protected Map<Long, String> bomKeyVersionMap = new HashMap<>();
	protected String leftBomName;
	protected String rightBomName;
	protected Map<String, Long> allBomsMap = new HashMap<>();
	protected boolean autoExpandForCompare;
    protected List<String> userComparePreference = new ArrayList<>();

    public List<String> getUserComparePreference() {
        return userComparePreference;
    }

    public void setUserComparePreference(List<String> userComparePreference) {
        this.userComparePreference = userComparePreference;
    }

    public boolean getAutoExpandForCompare() {
		return autoExpandForCompare;
	}

	public void setAutoExpandForCompare(boolean autoExpandForCompare) {
		this.autoExpandForCompare = autoExpandForCompare;
	}

	public Map<String, Long> getAllBomsMap() {
		return allBomsMap;
	}

	public void setAllBomsMap(Map<String, Long> allBomsMap) {
		this.allBomsMap = allBomsMap;
	}

	public String getLeftBomName() {
		return leftBomName;
	}

	public void setLeftBomName(String leftBomKey) {
		this.leftBomName = leftBomKey;
	}

	public String getRightBomName() {
		return rightBomName;
	}

	public void setRightBomName(String rightBomKey) {
		this.rightBomName = rightBomKey;
	}

	public Map<Long, String> getBomKeyVersionMap() {
		return bomKeyVersionMap;
	}

	public void setBomKeyVersionMap(Map<Long, String> bomKeyVersionMap) {
		this.bomKeyVersionMap = bomKeyVersionMap;
	}

	@Override
	public void reset(HttpServletRequest request) {
		super.reset(request);
		selectedLines.clear();
		mergeEnabled = false;
		unsavedData = false;
		lineEvent = null;
		lineEventMessage = null;
		firstBomCompareKey = null;
		firstBomCompareName = null;
		leftBomName = null;
		rightBomName = null;
	}

	public String getFirstBomCompareVersion() {
		return firstBomCompareVersion;
	}

	public void setFirstBomCompareVersion(String firstBomCompareVersion) {
		this.firstBomCompareVersion = firstBomCompareVersion;
	}

	public void setUnsavedData(boolean dataChanged) {
		this.unsavedData = dataChanged;
	}

	public boolean getUnsavedData() {
		return unsavedData;
	}

/* 	public void setCompareResult(BomCompareResult result) {
		this.compareResult = result;
	}

	public BomCompareResult getCompareResult() {
		return compareResult;
	} */

	public Set<Integer> getSelectedLineIndexes() {
		Set<Integer> result = new HashSet<Integer>();
		for (Map.Entry<Integer, Boolean> pair : selectedLines.entrySet()) {
			if (pair.getValue()) {
				result.add(pair.getKey());
			}
		}
		return result;
	}

	public Boolean getSelectedLine(int index) {
		return selectedLines.get(index);
	}

	public void setSelectedLine(int index, Boolean selected) {
		selectedLines.put(index, selected);
	}

	public void setMergeEnabled(boolean mergeEnabled) {
		this.mergeEnabled = mergeEnabled;
	}

	public boolean getMergeEnabled() {
		return mergeEnabled;
	}

	public String getLineEvent() {
		return lineEvent;
	}

	public void setLineEvent(String lineEvent) {
		this.lineEvent = lineEvent;
	}

	public String getLineEventMessage() {
		return lineEventMessage;
	}

	public void setLineEventMessage(String lineEventMessage) {
		this.lineEventMessage = lineEventMessage;
	}

	public String getFirstBomCompareName() {
		return this.firstBomCompareName;
	}

	public void setFirstBomCompareName(String name) {
		this.firstBomCompareName = name;
	}

	public Long getFirstBomCompareKey() {
		return firstBomCompareKey;
	}

	public void setFirstBomCompareKey(Long firstBomKey) {
		// Get around the issue where Controller sets the long to zero if it is not set.
		if (firstBomKey != null && firstBomKey == 0) {
			this.firstBomCompareKey = null;
		} else {
			this.firstBomCompareKey = firstBomKey;
		}
	}

	/**
	 * @return the attritionDefectTypes
	 */
	public Collection<PcmDefectType> getAttritionDefectTypes() {
		return this.getAttritionDefectTypesMap().values();
	}

	public Map<String, PcmDefectType> getAttritionDefectTypesMap() {
		return this.attritionDefectTypesMap;
	}

	public void setAttritionDefectTypesMap(Map<String, PcmDefectType> attritionDefectTypesMap) {
		this.attritionDefectTypesMap = attritionDefectTypesMap;
	}

	public void clearLineMessages() {
		lineMessages.clear();
	}

	public UiMessages getLineMessages() {
		return lineMessages;
	}

	public List<String> getLineMessages(String key) {
		return new ArrayList<String>(lineMessages.getMessages(key));
	}

	public void addLineMessage(String type, String key, String message) {
		lineMessages.add(type + "|" + key, message);
	}

	public Collection<String> getCompareCriteria() {
		return CompareManager.BOM.getCompareDefinition().getCompareCriteria();
	}

	/**
	 * @return the downloadInProgress
	 */
	public boolean isDownloadInProgress() {
		return downloadInProgress;
	}

	/**
	 * @param downloadInProgress
	 *            the downloadInProgress to set
	 */
	public void setDownloadInProgress(boolean downloadInProgress) {
		this.downloadInProgress = downloadInProgress;
	}

	/**
	 * @return the compareFlatBom
	 */
	public boolean isCompareFlatBom() {
		return compareFlatBom;
	}

	/**
	 * @param compareFlatBom
	 *            the compareFlatBom to set
	 */
	public void setCompareFlatBom(boolean compareFlatBom) {
		this.compareFlatBom = compareFlatBom;
	}
	
	public List<FlexAttributeDefn> getFlexAttributeBomDefinitions() {
		return FlexAttributeManager.BOM.getFlexAttributeDefinitionList();
	}

}
