/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.service;

import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.costexception.exception.CostRecordAuditException;
import com.scplatform.pcm.costexception.exception.CostRecordHandleException;
import com.scplatform.pcm.costexception.repository.CostExceptionRepository;
import com.scplatform.pcm.user.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class PcmCostExceptionRequestLogicImpl implements PcmCostExceptionRequestLogic {

    private static final Logger log = LogManager.getLogger(PcmCostExceptionRequestLogicImpl.class);

    private static final String EXCEPTION_ID_QUERY = "SELECT COST_EXCEPTION_ID_SEQ.NEXTVAL FROM DUAL";

    private final CostExceptionRepository costExceptionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveException(Users user, CostException costException, Boolean checkFileUploaded,
            CostException oldCostException, String eventName)
            throws CostRecordAuditException, CostRecordHandleException {
        try {
            costExceptionRepository.save(costException);
        } catch (Exception e) {
            log.error("Error saving exception: {}", e.getMessage(), e);
            throw new CostRecordHandleException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CostException getCRExceptionByExceptionId(String exceptionId) {
        return costExceptionRepository.findByExceptionId(exceptionId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CostException getCRExceptionByExceptionKey(Long exceptionKey) {
        return costExceptionRepository.findById(exceptionKey).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public CostException getCRExceptionByExceptionName(String exceptionName) throws CostRecordHandleException {
        return costExceptionRepository.findByExceptionNameIgnoreCase(exceptionName).orElse(null);
    }

    @Override
    public CostException approveException(Users user, CostException costException)
            throws CostRecordAuditException, CostRecordHandleException {
        try {
            return costExceptionRepository.save(costException);
        } catch (Exception e) {
            log.error("Error approving exception: {}", e.getMessage(), e);
            throw new CostRecordHandleException(e.getMessage());
        }
    }

    @Override
    public void closeException(Users user, CostException costException)
            throws CostRecordAuditException, CostRecordHandleException {
        try {
            costExceptionRepository.save(costException);
        } catch (Exception e) {
            log.error("Error closing exception: {}", e.getMessage(), e);
            throw new CostRecordHandleException(e.getMessage());
        }
    }

    @Override
    public void withdrawException(Users user, CostException costException)
            throws CostRecordAuditException, CostRecordHandleException {
        try {
            costExceptionRepository.save(costException);
        } catch (Exception e) {
            log.error("Error withdrawing exception: {}", e.getMessage(), e);
            throw new CostRecordHandleException(e.getMessage());
        }
    }

    @Override
    public void rejectException(Users user, CostException costException)
            throws CostRecordAuditException, CostRecordHandleException {
        try {
            costExceptionRepository.save(costException);
        } catch (Exception e) {
            log.error("Error rejecting exception: {}", e.getMessage(), e);
            throw new CostRecordHandleException(e.getMessage());
        }
    }

    @Override
    public void reopenException(Users user, CostException costException)
            throws CostRecordAuditException, CostRecordHandleException {
        try {
            costExceptionRepository.save(costException);
        } catch (Exception e) {
            log.error("Error reopening exception: {}", e.getMessage(), e);
            throw new CostRecordHandleException(e.getMessage());
        }
    }

    @Override
    public String generateExceptionId() {
        BigDecimal result = (BigDecimal) entityManager
                .createNativeQuery(EXCEPTION_ID_QUERY)
                .getSingleResult();
        return "ER" + result.longValue();
    }

    @Override
    public Boolean deleteCostExceptionRoleApprovalRecords(CostException costException) {
        if (costException.getExceptionApproval() != null) {
            costException.getExceptionApproval().clear();
        }
        return Boolean.TRUE;
    }
}
