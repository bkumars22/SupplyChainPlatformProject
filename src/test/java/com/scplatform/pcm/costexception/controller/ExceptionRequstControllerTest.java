/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.controller;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.costexception.dto.ExceptionWraper;
import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.costexception.exception.CostRecordHandleException;
import com.scplatform.pcm.costexception.repository.CostExceptionConfigRepository;
import com.scplatform.pcm.costexception.service.PcmCostExceptionRequestLogic;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.ums.dto.GenericResponse;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionRequstControllerTest {

    @Mock
    private CostExceptionConfigRepository costExceptionConfigRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PcmConfigUtil pcmConfigUtil;

    @Mock
    private PcmCostExceptionRequestLogic costExceptionRequestLogic;

    @InjectMocks
    private ExceptionRequstController controller;

    private MockHttpServletRequest request;
    private static SCPlatformMessages prevMessages;

    @BeforeAll
    static void installSCPlatformMessages() throws Exception {
        prevMessages = SCPlatformMessages.INSTANCE;
        SCPlatformMessages mock = mock(SCPlatformMessages.class);
        when(mock.getMessage(anyString(), any(), any())).thenAnswer(inv -> "MSG:" + inv.getArgument(0));
        Field f = SCPlatformMessages.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, mock);
    }

    @AfterAll
    static void restoreSCPlatformMessages() throws Exception {
        Field f = SCPlatformMessages.class.getDeclaredField("INSTANCE");
        f.setAccessible(true);
        f.set(null, prevMessages);
    }

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
    }

    // Helper: build a valid user with a role
    private Users userWithRole(String userId, String roleId) {
        Role role = new Role();
        role.setRoleId(roleId);
        Users u = new Users();
        u.setUserId(userId);
        u.setRole(role);
        return u;
    }

    // Helper: call validateException with the given params (normal happy-path values)
    private ResponseEntity<GenericResponse> callValidate(
            String exceptionName, String exceptionApprover, String exceptionOwner,
            String costType, String requestType, String applicableODM,
            String commodity, String subTier, String platform, String lob,
            String exceptionId, String previousCostType, Boolean isFilePresent) {
        return controller.validateException(
                exceptionName, exceptionApprover, exceptionOwner,
                costType, requestType, applicableODM,
                commodity, subTier, platform, lob,
                exceptionId, previousCostType, isFilePresent, request);
    }

    // ---- Happy path: all fields valid, no existing exception by name ----------

    @Test
    void validateException_allValid_returnsOkWithEmptyErrors() throws Exception {
        Users approver = userWithRole("approver1", "MANAGER");
        Users owner = userWithRole("owner1", "OWNER_ROLE");

        when(costExceptionRequestLogic.getCRExceptionByExceptionName("My Exception"))
                .thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction("BUY", "BACKDATE", "Approve"))
                .thenReturn(Collections.singletonList("MANAGER"));
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.of(approver));
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "My Exception", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM-ABC",
                "CHIPS", "Y", "Platform1", "LOB1",
                "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody() instanceof ExceptionWraper);
        // No validation errors in costException node
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertNotNull(wrapper.getCostException());
        assertTrue(wrapper.getCostException().isEmpty());
    }

    // ---- exceptionName validations -------------------------------------------

    @Test
    void validateException_blankExceptionName_addsValidationError() throws Exception {
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("MANAGER"));
        Users approver = userWithRole("approver1", "MANAGER");
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.of(approver));
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "  ", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("exceptionName"));
    }

    @Test
    void validateException_duplicateExceptionName_addsValidationError() throws Exception {
        CostException existing = new CostException();
        existing.setExceptionId("EX-999");
        when(costExceptionRequestLogic.getCRExceptionByExceptionName("Dup Name")).thenReturn(existing);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("MANAGER"));
        Users approver = userWithRole("approver1", "MANAGER");
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.of(approver));
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "Dup Name", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "DIFFERENT-ID", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("exceptionName"));
    }

    @Test
    void validateException_sameExceptionId_noNameError() throws Exception {
        CostException existing = new CostException();
        existing.setExceptionId("EX-001");
        when(costExceptionRequestLogic.getCRExceptionByExceptionName("My Exception")).thenReturn(existing);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("MANAGER"));
        Users approver = userWithRole("approver1", "MANAGER");
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.of(approver));
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "My Exception", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "EX-001", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertFalse(wrapper.getCostException().has("exceptionName"),
                "No name error when exceptionId matches existing");
    }

    // ---- exceptionApprover validations ----------------------------------------

    @Test
    void validateException_blankApprover_addsValidationError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "  ", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("exceptionApprover"));
    }

    @Test
    void validateException_approverNotFound_addsValidationError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("MANAGER"));
        when(usersRepository.findByUserId("unknown")).thenReturn(Optional.empty());
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "unknown", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("exceptionApprover"));
    }

    @Test
    void validateException_approverWrongRole_addsValidationError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("DIRECTOR")); // required role
        Users approver = userWithRole("approver1", "MANAGER");  // has different role
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.of(approver));
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("exceptionApprover"));
    }

    // ---- required field blank validations ------------------------------------

    @Test
    void validateException_blankCostType_addsValidationError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.empty());
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.emptyList());
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.empty());

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "  ", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "  ", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("costType"));
    }

    @Test
    void validateException_blankRequestType_addsValidationError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.empty());
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.emptyList());
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.empty());

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "BUY", "  ", "ODM", "COM", "Y", "PL", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("requestType"));
    }

    @Test
    void validateException_blankODM_addsValidationError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.empty());
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.emptyList());
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.empty());

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "BUY", "BACKDATE", "  ", "COM", "Y", "PL", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("applicableODM"));
    }

    @Test
    void validateException_blankPlatform_addsValidationError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.empty());
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.emptyList());
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.empty());

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("platform"));
    }

    // ---- isFilePresent with cost type mismatch --------------------------------

    @Test
    void validateException_filePresentCostTypeMismatch_addsError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("MANAGER"));
        Users approver = userWithRole("approver1", "MANAGER");
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.of(approver));
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "SELL", true);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertTrue(wrapper.getCostException().has("costTypeChange"));
    }

    @Test
    void validateException_filePresentCostTypeMatch_noCostTypeChangeError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString())).thenReturn(null);
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("MANAGER"));
        Users approver = userWithRole("approver1", "MANAGER");
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.of(approver));
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "BUY", true);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        assertFalse(wrapper.getCostException().has("costTypeChange"));
    }

    // ---- Exception during name lookup -----------------------------------------

    @Test
    void validateException_nameLookupThrows_addsErrorMessage() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName("Name"))
                .thenThrow(new RuntimeException("DB error"));
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList("MANAGER"));
        Users approver = userWithRole("approver1", "MANAGER");
        when(usersRepository.findByUserId("approver1")).thenReturn(Optional.of(approver));
        when(pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role"))
                .thenReturn(Collections.singletonList("OWNER_ROLE"));
        Users owner = userWithRole("owner1", "OWNER_ROLE");
        when(usersRepository.findByUserId("owner1")).thenReturn(Optional.of(owner));

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) resp.getBody();
        // Error message for name lookup failure should appear
        assertTrue(wrapper.getCostException().has("exceptionName"));
        assertTrue(wrapper.getCostException().get("exceptionName").asText().contains("DB error"));
    }

    // ---- Top-level exception handling -----------------------------------------

    @Test
    void validateException_unexpectedException_returnsInternalServerError() throws Exception {
        when(costExceptionRequestLogic.getCRExceptionByExceptionName(anyString()))
                .thenThrow(new RuntimeException("Unexpected"));
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Fatal"));

        ResponseEntity<GenericResponse> resp = callValidate(
                "Name", "approver1", "owner1",
                "BUY", "BACKDATE", "ODM", "COM", "Y", "PL", "LOB", "", "BUY", false);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
    }
}
