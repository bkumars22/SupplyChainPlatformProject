/*
 * Service interface for Cost Exception request operations
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.service;

import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.costexception.exception.CostRecordAuditException;
import com.scplatform.pcm.costexception.exception.CostRecordHandleException;
import com.scplatform.pcm.user.entity.Users;

public interface PcmCostExceptionRequestLogic {

	void saveException(Users user, CostException costException, Boolean checkFileUploaded, CostException oldCostException, String eventName) throws CostRecordAuditException, CostRecordHandleException;

	public CostException getCRExceptionByExceptionId(String exceptionId);
	
	public CostException getCRExceptionByExceptionKey(Long exceptionKey);
	
	public CostException getCRExceptionByExceptionName(String exceptionName) throws CostRecordHandleException;

	public CostException approveException(Users user, CostException costException) throws CostRecordAuditException, CostRecordHandleException;

	void closeException(Users user, CostException costException) throws CostRecordAuditException, CostRecordHandleException;

	void withdrawException(Users user, CostException costException) throws CostRecordAuditException, CostRecordHandleException;

	void rejectException(Users user, CostException costException) throws CostRecordAuditException, CostRecordHandleException;
	
	void reopenException(Users user, CostException costException) throws CostRecordAuditException, CostRecordHandleException;
	
	public String generateExceptionId();

	Boolean deleteCostExceptionRoleApprovalRecords(CostException costException);

}
