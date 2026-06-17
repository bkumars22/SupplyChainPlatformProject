/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.dto;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.scplatform.pcm.upload.entity.LoadJob;
import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.pcm.common.dto.FormFile;
import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.costexception.entity.CostExceptionInfo;
import com.scplatform.pcm.costexception.entity.CostExceptionODMEmail;
import com.scplatform.pcm.costexception.entity.CostExceptionPricing;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.searchframework.dto.SearchForm;

@SuppressWarnings("serial")
public class CostRecordExceptionForm extends SearchForm {
	protected Long exceptionKey;
	protected String exceptionId;
	protected String exceptionName;
	protected String exceptionApprover;
	protected String exceptionOwner;
	protected PcmCostType costType;
	protected String selectedcostType;
	protected String requestType;
	protected String exceptionRequestor;
	protected String commodity;
	protected Boolean subTier;
	protected String platformName;
	protected String selectedlineOfBusiness;
	protected String uploadType;
	protected Object selectedodmAcknowledgement;
	protected String state ="";
	protected String comments="";
	protected CostException selectedException;
	protected File file;
	protected boolean correctionFlag = false;
	protected Set<CostExceptionInfo> costExceptionInfo=new HashSet<CostExceptionInfo>();
	protected List<CostExceptionInfo> costExceptionInfoList = getCostExceptionInfoList();
	protected String[] applicableLOB;
	protected String[] applicableODMCM;
	protected String actionComments="";
	private FormFile uploadFile;
	private CostExceptionPricing costExceptionPricing;
	private LoadJob loadJob;
	private boolean successfulUpload;
	protected String backAction;
	protected String lastUpdateby;
	protected Timestamp lastUpdateOn = null;
	protected Timestamp reuestedDate = null;
	protected String eventName;
	protected boolean editFlag = false;
	protected String exceptionApprovedBy;
	protected Timestamp exceptionApproveDate = null;
	protected String exceptionClosedBy;
	protected Timestamp exceptionClosedDate = null;
	protected String exceptionRejectedBy;
	protected Timestamp exceptionRejectedDate = null;
	protected UiMessages lineMessages = new UiMessages();
	protected UiMessages messages = new UiMessages();
	private String fileDeleted;
	private Boolean isFileUploaded=false;
	private Boolean checkBackAction=false;
	private String fileLocation;
	private Map<String, Object> costSearchData;
	private String deletedEmailKeys;
	private String downloadEmailKey;
	private Map<String, CostExceptionODMEmail> emailNameObjMap = new HashMap<>();
	private Set<CostExceptionODMEmail> emailSet = new HashSet<CostExceptionODMEmail>();
	private Boolean odmEmailRequired = false;
	private Boolean odmEmailEditable = false;
	private String fileUploadError = null;
	private Map<String,FormFile> odmUploadFiles = null;
	private List<String> pendingApprovalRoles = new ArrayList<String>();
	private String[] proxyApprover;
	private List<String> approvedRoles = new ArrayList<String>();

	public List<CostExceptionInfo> getCostExceptionInfoList() {
		List<CostExceptionInfo> costExceptionInfoList = new ArrayList<CostExceptionInfo>();
		if (costExceptionInfo != null && costExceptionInfo.size() > 0) {
			for (CostExceptionInfo costExcep : costExceptionInfo) {
				costExceptionInfoList.add(costExcep);
			}
			Collections.sort(costExceptionInfoList, new Comparator<CostExceptionInfo>() {
				public int compare(CostExceptionInfo o1, CostExceptionInfo o2) {
					return o2.getStateChangeOn().compareTo(o1.getStateChangeOn());
				}
			});
		}
		return costExceptionInfoList;
	}

	public FormFile getOdmUploadFile(String key) {
		return odmUploadFiles.get(key);
	}

	public Map<String, FormFile> getOdmUploadFiles() {
		return odmUploadFiles;
	}

	public Boolean getOdmEmailRequired() {
		return odmEmailRequired;
	}

	public void setOdmEmailRequired(Boolean odmEmailRequired) {
		this.odmEmailRequired = odmEmailRequired;
	}

	public String getDeletedEmailKeys() {
		return deletedEmailKeys;
	}

	public void setDeletedEmailKeys(String deletedEmailKeys) {
		this.deletedEmailKeys = deletedEmailKeys;
	}

	public String getDownloadEmailKey() {
		return downloadEmailKey;
	}

	public void setDownloadEmailKey(String downloadEmailKey) {
		this.downloadEmailKey = downloadEmailKey;
	}

	public Set<CostExceptionODMEmail> getEmailSet() {
		return emailSet;
	}

	public void setEmailSet(Set<CostExceptionODMEmail> emailSet) {
		this.emailSet = emailSet;
	}

	public Long getExceptionKey() {
		return exceptionKey;
	}

	public void setExceptionKey(Long exceptionKey) {
		this.exceptionKey = exceptionKey;
	}

	public String getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(String exceptionId) {
		this.exceptionId = exceptionId;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}

	public String getExceptionRequestor() {
		return exceptionRequestor;
	}

	public void setExceptionRequestor(String exceptionRequestor) {
		this.exceptionRequestor = exceptionRequestor;
	}

	public String getExceptionOwner() {
		return exceptionOwner;
	}

	public void setExceptionOwner(String exceptionOwner) {
		this.exceptionOwner = exceptionOwner;
	}

	public String getExceptionApprover() {
		return exceptionApprover;
	}

	public void setExceptionApprover(String exceptionApprover) {
		this.exceptionApprover = exceptionApprover;
	}

	public PcmCostType getCostType() {
		return costType;
	}

	public void setCostType(PcmCostType costType) {
		this.costType = costType;
	}

	public String getSelectedcostType() {
		return selectedcostType;
	}

	public void setSelectedcostType(String selectedcostType) {
		this.selectedcostType = selectedcostType;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getCommodity() {
		return commodity;
	}

	public void setCommodity(String commodity) {
		this.commodity = commodity;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public Boolean getSubTier() {
		return subTier;
	}

	public void setSubTier(Boolean subTier) {
		this.subTier = subTier;
	}

	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public UiMessages getLineMessages() {
		return lineMessages;
	}

	public void setLineMessages(UiMessages lineMessages) {
		this.lineMessages = lineMessages;
	}

	public UiMessages getMessages() {
		return messages;
	}

	public void setMessages(UiMessages messages) {
		this.messages = messages;
	}

	public Set<CostExceptionInfo> getCostExceptionInfo() {
		return costExceptionInfo;
	}

	public void setCostExceptionInfo(Set<CostExceptionInfo> costExceptionInfo) {
		this.costExceptionInfo = costExceptionInfo;
		this.costExceptionInfoList = getCostExceptionInfoList();
	}

	public FormFile getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(FormFile uploadFile) {
		this.uploadFile = uploadFile;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public CostException getSelectedException() {
		return selectedException;
	}

	public void setSelectedException(CostException selectedException) {
		this.selectedException = selectedException;
	}

	public boolean isCorrectionFlag() {
		return correctionFlag;
	}

	public void setCorrectionFlag(boolean correctionFlag) {
		this.correctionFlag = correctionFlag;
	}

	public String getLastUpdateby() {
		return lastUpdateby;
	}

	public void setLastUpdateby(String lastUpdateby) {
		this.lastUpdateby = lastUpdateby;
	}

	public Timestamp getLastUpdateOn() {
		return lastUpdateOn;
	}

	public void setLastUpdateOn(Timestamp lastUpdateOn) {
		this.lastUpdateOn = lastUpdateOn;
	}

	public Timestamp getReuestedDate() {
		return reuestedDate;
	}

	public void setReuestedDate(Timestamp timestamp) {
		this.reuestedDate = timestamp;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getSelectedlineOfBusiness() {
		return selectedlineOfBusiness;
	}

	public void setSelectedlineOfBusiness(String selectedlineOfBusiness) {
		this.selectedlineOfBusiness = selectedlineOfBusiness;
	}

	public String getUploadType() {
		return uploadType;
	}

	public void setUploadType(String uploadType) {
		this.uploadType = uploadType;
	}

	public Object getSelectedodmAcknowledgement() {
		return selectedodmAcknowledgement;
	}

	public void setSelectedodmAcknowledgement(Object selectedodmAcknowledgement) {
		this.selectedodmAcknowledgement = selectedodmAcknowledgement;
	}

	public String[] getApplicableLOB() {
		return applicableLOB;
	}

	public void setApplicableLOB(String[] applicableLOB) {
		this.applicableLOB = applicableLOB;
	}

	public String[] getApplicableODMCM() {
		return applicableODMCM;
	}

	public void setApplicableODMCM(String[] applicableODMCM) {
		this.applicableODMCM = applicableODMCM;
	}

	public boolean isEditFlag() {
		return editFlag;
	}

	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	public String getActionComments() {
		return actionComments;
	}

	public void setActionComments(String actionComments) {
		this.actionComments = actionComments;
	}

	public String getExceptionClosedBy() {
		return exceptionClosedBy;
	}

	public void setExceptionClosedBy(String exceptionClosedBy) {
		this.exceptionClosedBy = exceptionClosedBy;
	}

	public Timestamp getExceptionClosedDate() {
		return exceptionClosedDate;
	}

	public void setExceptionClosedDate(Timestamp exceptionClosedDate) {
		this.exceptionClosedDate = exceptionClosedDate;
	}

	public String getExceptionRejectedBy() {
		return exceptionRejectedBy;
	}

	public void setExceptionRejectedBy(String exceptionRejectedBy) {
		this.exceptionRejectedBy = exceptionRejectedBy;
	}

	public Timestamp getExceptionRejectedDate() {
		return exceptionRejectedDate;
	}

	public void setExceptionRejectedDate(Timestamp exceptionRejectedDate) {
		this.exceptionRejectedDate = exceptionRejectedDate;
	}

	public String getExceptionApprovedBy() {
		return exceptionApprovedBy;
	}

	public void setExceptionApprovedBy(String exceptionApprovedBy) {
		this.exceptionApprovedBy = exceptionApprovedBy;
	}

	public Timestamp getExceptionApproveDate() {
		return exceptionApproveDate;
	}

	public void setExceptionApproveDate(Timestamp exceptionApproveDate) {
		this.exceptionApproveDate = exceptionApproveDate;
	}

	public CostExceptionPricing getCostExceptionPricing() {
		return costExceptionPricing;
	}

	public void setCostExceptionPricing(CostExceptionPricing costExceptionPricing) {
		this.costExceptionPricing = costExceptionPricing;
	}

	public LoadJob getLoadJob() {
		return loadJob;
	}

	public void setLoadJob(LoadJob loadJob) {
		this.loadJob = loadJob;
	}

	public boolean getSuccessfulUpload() {
		return successfulUpload;
	}

	public void setSuccessfulUpload(boolean successfulUpload) {
		this.successfulUpload = successfulUpload;
	}

	public String getFileDeleted() {
		return fileDeleted;
	}

	public void setFileDeleted(String fileDeleted) {
		this.fileDeleted = fileDeleted;
	}

	public Boolean getIsFileUploaded() {
		return isFileUploaded;
	}

	public void setIsFileUploaded(Boolean isFileUploaded) {
		this.isFileUploaded = isFileUploaded;
	}
	public Boolean getCheckBackAction() {
		return checkBackAction;
	}

	public void setCheckBackAction(Boolean checkBackAction) {
		this.checkBackAction = checkBackAction;
	}
	
	public String getFileLocation() {
		return fileLocation;
	}

	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}

	public Map<String, Object> getCostSearchData() {
		return costSearchData;
	}

	public void setCostSearchData(Map<String, Object> costSearchData) {
		this.costSearchData = costSearchData;
	}

	public Map<String, CostExceptionODMEmail> getEmailNameObjMap() {
		return emailNameObjMap;
	}

	public void setEmailNameObjMap(Map<String, CostExceptionODMEmail> emailNameObjMap) {
		this.emailNameObjMap = emailNameObjMap;
	}
	
	public Boolean getOdmEmailEditable() {
		return odmEmailEditable;
	}

	public void setOdmEmailEditable(Boolean odmEmailEditable) {
		this.odmEmailEditable = odmEmailEditable;
	}

	public String getFileUploadError() {
		return fileUploadError;
	}

	public void setFileUploadError(String fileUploadError) {
		this.fileUploadError = fileUploadError;
	}
	
	public List<String> getPendingApprovalRoles() {
		return pendingApprovalRoles;
	}

	public void setPendingApprovalRoles(List<String> pendingApprovalRoles) {
		this.pendingApprovalRoles = pendingApprovalRoles;
	}
	
	public String[] getProxyApprover() {
		return proxyApprover;
	}

	public void setProxyApprover(String[] proxyApprover) {
		this.proxyApprover = proxyApprover;
	}

	public List<String> getApprovedRoles() {
		return approvedRoles;
	}

	public void setApprovedRoles(List<String> approvedRoles) {
		this.approvedRoles = approvedRoles;
	}

	public void setOdmUploadFiles(Map<String, FormFile> odmUploadFiles) {
		this.odmUploadFiles = odmUploadFiles;
	}
}