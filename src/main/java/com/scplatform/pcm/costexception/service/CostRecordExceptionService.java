/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.common.dto.FormFile;
import com.scplatform.pcm.costexception.dto.CostRecordExceptionForm;
import com.scplatform.pcm.searchframework.dto.SearchParameter;
import com.scplatform.pcm.searchframework.exception.SearchFormException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class CostRecordExceptionService {

	@Autowired
	private PcmConfigUtil pcmConfigUtil;

	/**
	 * Clear all form fields and reset to default state
	 */
	public void clear(CostRecordExceptionForm form) {
		form.setExceptionKey(null);
		form.setExceptionId(null);
		form.setExceptionName(null);
		form.setExceptionRequestor(null);
		form.setExceptionOwner(null);
		form.setExceptionApprover(null);
		form.setCostType(null);
		form.setRequestType(null);
		form.setCommodity(null);
		form.setSubTier(null);
		form.setPlatformName(null);
		form.setEventName(null);
		form.setSelectedcostType(null);
		form.setExceptionRequestor(null);
		form.setSelectedlineOfBusiness(null);
		form.setUploadType(null);
		form.setSelectedodmAcknowledgement(null);
		form.setState("");
		form.setComments("");
		form.setActionComments("");
		form.setCorrectionFlag(false);
		form.setApplicableLOB(null);
		form.setApplicableODMCM(null);
		form.setEditFlag(false);
		form.setUploadFile(null);
		form.setCostExceptionPricing(null);
		form.setLoadJob(null);
		form.setSuccessfulUpload(false);
		form.setFileUploadError(null);
		try {
			if (form.getSelectedException() != null) {
				form.getSelectedException().getCostExceptionInfo().clear();
				form.getSelectedException().getCostExceptionLOB().clear();
				form.getSelectedException().getCostExceptionOdmCm().clear();
			}
			form.getCostExceptionInfo().clear();
		} catch (LazyInitializationException e) {
		}
		form.setSelectedException(null);
		form.setBackAction(null);
		form.setIsFileUploaded(false);
		form.setDeletedEmailKeys(null);
		form.setDownloadEmailKey(null);
		form.getEmailNameObjMap().clear();
		form.getEmailSet().clear();
		form.setOdmEmailRequired(false);
		form.setOdmEmailEditable(false);
		form.setOdmUploadFiles(null);
		form.getPendingApprovalRoles().clear();
		form.setProxyApprover(null);
		form.getApprovedRoles().clear();
	}

	/**
	 * Set ODM upload file for given key
	 */
	public void setOdmUploadFile(CostRecordExceptionForm form, String key, FormFile file) {
		Map<String, FormFile> odmUploadFiles = form.getOdmUploadFiles();
		if (odmUploadFiles == null) {
			odmUploadFiles = new HashMap<String, FormFile>();
			form.setOdmUploadFiles(odmUploadFiles);
		}
		odmUploadFiles.put(key, file);
	}

	/**
	 * Set email upload files
	 */
	public void setEmailUploadFile(CostRecordExceptionForm form, Map<String, FormFile> file) {
		Map<String, FormFile> odmUploadFiles = form.getOdmUploadFiles();
		if (odmUploadFiles == null) {
			odmUploadFiles = new HashMap<String, FormFile>();
		}
		form.setOdmUploadFiles(file);
	}

	/**
	 * Populate search data from form parameters
	 */
	public void setSearchData(CostRecordExceptionForm form) {
		Map<String, Object> costSearchData = form.getCostSearchData();
		if (costSearchData == null) {
			costSearchData = new HashMap<String, Object>();
			form.setCostSearchData(costSearchData);
		}
		for (SearchParameter sp : form.getAllParameters()) {
			if (sp.getName() != null) {
				costSearchData.put(sp.getName(), sp.getValue());
			}
		}
	}

	/**
	 * Reset search data back to cached values
	 */
	public void resetSearchData(CostRecordExceptionForm form) {
		Map<String, Object> costSearchData = form.getCostSearchData();
		if (costSearchData != null) {
			for (Map.Entry<String, Object> spObj : costSearchData.entrySet()) {
				try {
					form.getSearchParameter(spObj.getKey()).setValue(spObj.getValue());
				} catch (SearchFormException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Get all configured request types
	 */
	public ObjectNode getAllRequestType() throws ParseException {
		ObjectMapper om = new ObjectMapper();
		ObjectNode o = om.createObjectNode();
		List<String> configuredCostTypes = pcmConfigUtil.getList("pcm.costrecord.exception.allowableRequestTypes",
				new ArrayList<String>());

		for (String type : configuredCostTypes) {
			String rstType = pcmConfigUtil.getString("pcm.costrecord.exception.request.type." + type, type);
			o.put(type, rstType);
		}
		return o;
	}

	/**
	 * Reset form data to initial state
	 */
	public void resetFormData(CostRecordExceptionForm form) {
		form.setExceptionId(null);
		form.setSelectedException(null);
	}
}
