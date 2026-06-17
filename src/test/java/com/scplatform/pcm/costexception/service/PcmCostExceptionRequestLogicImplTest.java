/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.service;

import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.costexception.entity.CostExceptionApprover;
import com.scplatform.pcm.costexception.exception.CostRecordAuditException;
import com.scplatform.pcm.costexception.exception.CostRecordHandleException;
import com.scplatform.pcm.costexception.repository.CostExceptionRepository;
import com.scplatform.pcm.user.entity.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PcmCostExceptionRequestLogicImplTest {

    @Mock
    private CostExceptionRepository costExceptionRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private PcmCostExceptionRequestLogicImpl service;

    private Users user;
    private CostException exception;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "entityManager", entityManager);

        user = new Users();
        user.setUserId("testuser");

        exception = new CostException();
        exception.setExceptionKey(1L);
        exception.setExceptionId("EX-001");
        exception.setExceptionName("Test Exception");
    }

    // ---- saveException -------------------------------------------------------

    @Test
    void saveException_success_callsRepositorySave() throws Exception {
        when(costExceptionRepository.save(exception)).thenReturn(exception);
        service.saveException(user, exception, false, null, "CREATE");
        verify(costExceptionRepository).save(exception);
    }

    @Test
    void saveException_repositoryThrows_wrapsCostRecordHandleException() {
        when(costExceptionRepository.save(exception)).thenThrow(new RuntimeException("DB error"));
        assertThrows(CostRecordHandleException.class,
                () -> service.saveException(user, exception, false, null, "CREATE"));
    }

    // ---- getCRExceptionByExceptionId -----------------------------------------

    @Test
    void getCRExceptionByExceptionId_found_returnsException() {
        when(costExceptionRepository.findByExceptionId("EX-001"))
                .thenReturn(Optional.of(exception));
        CostException result = service.getCRExceptionByExceptionId("EX-001");
        assertSame(exception, result);
    }

    @Test
    void getCRExceptionByExceptionId_notFound_returnsNull() {
        when(costExceptionRepository.findByExceptionId("UNKNOWN"))
                .thenReturn(Optional.empty());
        assertNull(service.getCRExceptionByExceptionId("UNKNOWN"));
    }

    // ---- getCRExceptionByExceptionKey ----------------------------------------

    @Test
    void getCRExceptionByExceptionKey_found_returnsException() {
        when(costExceptionRepository.findById(1L)).thenReturn(Optional.of(exception));
        CostException result = service.getCRExceptionByExceptionKey(1L);
        assertSame(exception, result);
    }

    @Test
    void getCRExceptionByExceptionKey_notFound_returnsNull() {
        when(costExceptionRepository.findById(999L)).thenReturn(Optional.empty());
        assertNull(service.getCRExceptionByExceptionKey(999L));
    }

    // ---- getCRExceptionByExceptionName ---------------------------------------

    @Test
    void getCRExceptionByExceptionName_found_returnsException() throws Exception {
        when(costExceptionRepository.findByExceptionNameIgnoreCase("Test Exception"))
                .thenReturn(Optional.of(exception));
        CostException result = service.getCRExceptionByExceptionName("Test Exception");
        assertSame(exception, result);
    }

    @Test
    void getCRExceptionByExceptionName_notFound_returnsNull() throws Exception {
        when(costExceptionRepository.findByExceptionNameIgnoreCase("Missing"))
                .thenReturn(Optional.empty());
        assertNull(service.getCRExceptionByExceptionName("Missing"));
    }

    // ---- approveException ----------------------------------------------------

    @Test
    void approveException_success_returnsSavedException() throws Exception {
        when(costExceptionRepository.save(exception)).thenReturn(exception);
        CostException result = service.approveException(user, exception);
        assertSame(exception, result);
    }

    @Test
    void approveException_repositoryThrows_wrapsCostRecordHandleException() {
        when(costExceptionRepository.save(exception)).thenThrow(new RuntimeException("DB error"));
        assertThrows(CostRecordHandleException.class,
                () -> service.approveException(user, exception));
    }

    // ---- closeException ------------------------------------------------------

    @Test
    void closeException_success_callsRepositorySave() throws Exception {
        when(costExceptionRepository.save(exception)).thenReturn(exception);
        service.closeException(user, exception);
        verify(costExceptionRepository).save(exception);
    }

    @Test
    void closeException_repositoryThrows_wrapsCostRecordHandleException() {
        when(costExceptionRepository.save(exception)).thenThrow(new RuntimeException("close error"));
        assertThrows(CostRecordHandleException.class,
                () -> service.closeException(user, exception));
    }

    // ---- withdrawException ---------------------------------------------------

    @Test
    void withdrawException_success_callsRepositorySave() throws Exception {
        when(costExceptionRepository.save(exception)).thenReturn(exception);
        service.withdrawException(user, exception);
        verify(costExceptionRepository).save(exception);
    }

    @Test
    void withdrawException_repositoryThrows_wrapsCostRecordHandleException() {
        when(costExceptionRepository.save(exception)).thenThrow(new RuntimeException("withdraw error"));
        assertThrows(CostRecordHandleException.class,
                () -> service.withdrawException(user, exception));
    }

    // ---- rejectException -----------------------------------------------------

    @Test
    void rejectException_success_callsRepositorySave() throws Exception {
        when(costExceptionRepository.save(exception)).thenReturn(exception);
        service.rejectException(user, exception);
        verify(costExceptionRepository).save(exception);
    }

    @Test
    void rejectException_repositoryThrows_wrapsCostRecordHandleException() {
        when(costExceptionRepository.save(exception)).thenThrow(new RuntimeException("reject error"));
        assertThrows(CostRecordHandleException.class,
                () -> service.rejectException(user, exception));
    }

    // ---- reopenException -----------------------------------------------------

    @Test
    void reopenException_success_callsRepositorySave() throws Exception {
        when(costExceptionRepository.save(exception)).thenReturn(exception);
        service.reopenException(user, exception);
        verify(costExceptionRepository).save(exception);
    }

    @Test
    void reopenException_repositoryThrows_wrapsCostRecordHandleException() {
        when(costExceptionRepository.save(exception)).thenThrow(new RuntimeException("reopen error"));
        assertThrows(CostRecordHandleException.class,
                () -> service.reopenException(user, exception));
    }

    // ---- generateExceptionId -------------------------------------------------

    @Test
    void generateExceptionId_returnsERPrefixedId() {
        Query mockQuery = mock(Query.class);
        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.getSingleResult()).thenReturn(new BigDecimal("42"));

        String id = service.generateExceptionId();
        assertEquals("ER42", id);
    }

    // ---- deleteCostExceptionRoleApprovalRecords ------------------------------

    @Test
    void deleteCostExceptionRoleApprovalRecords_withApprovals_clearsAndReturnsTrue() {
        Set<CostExceptionApprover> approvers = new HashSet<>();
        approvers.add(new CostExceptionApprover());
        exception.setExceptionApproval(approvers);

        Boolean result = service.deleteCostExceptionRoleApprovalRecords(exception);

        assertTrue(result);
        assertTrue(exception.getExceptionApproval().isEmpty());
    }

    @Test
    void deleteCostExceptionRoleApprovalRecords_nullApprovals_returnsTrue() {
        exception.setExceptionApproval(null);
        Boolean result = service.deleteCostExceptionRoleApprovalRecords(exception);
        assertTrue(result);
    }

    @Test
    void deleteCostExceptionRoleApprovalRecords_emptyApprovals_returnsTrue() {
        exception.setExceptionApproval(new HashSet<>());
        Boolean result = service.deleteCostExceptionRoleApprovalRecords(exception);
        assertTrue(result);
        assertTrue(exception.getExceptionApproval().isEmpty());
    }
}
