/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.controller;

import com.scplatform.pcm.costexception.dto.ExceptionWraper;
import com.scplatform.pcm.costexception.repository.CostExceptionConfigRepository;
import com.scplatform.pcm.ums.dto.GenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExceptionApproverRoleControllerTest {

    @Mock
    private CostExceptionConfigRepository costExceptionConfigRepository;

    @InjectMocks
    private ExceptionApproverRoleController controller;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
    }

    @Test
    void validateException_noRoles_returnsEmptyRolesString() {
        when(costExceptionConfigRepository.getRolesForCostExceptionAction("BUY", "BACKDATE", "Approve"))
                .thenReturn(Collections.emptyList());

        ResponseEntity<GenericResponse> response = controller.validateException("BUY", "BACKDATE", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ExceptionWraper);
        ExceptionWraper wrapper = (ExceptionWraper) response.getBody();
        assertNotNull(wrapper.getApprovalDetails());
        // empty list → substring of "[]" from index 1 to length-1 → empty string
        assertEquals("", wrapper.getApprovalDetails().get("roles").asText());
    }

    @Test
    void validateException_withRoles_returnsCommaSeparatedRoles() {
        when(costExceptionConfigRepository.getRolesForCostExceptionAction("SELL", "MIDMONTH", "Approve"))
                .thenReturn(Arrays.asList("MANAGER", "DIRECTOR"));

        ResponseEntity<GenericResponse> response = controller.validateException("SELL", "MIDMONTH", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) response.getBody();
        assertNotNull(wrapper.getApprovalDetails());
        String roles = wrapper.getApprovalDetails().get("roles").asText();
        assertTrue(roles.contains("MANAGER"));
        assertTrue(roles.contains("DIRECTOR"));
    }

    @Test
    void validateException_singleRole_returnsTrimmedRoleString() {
        when(costExceptionConfigRepository.getRolesForCostExceptionAction("BUY", "BACKDATE", "Approve"))
                .thenReturn(Collections.singletonList("ADMIN"));

        ResponseEntity<GenericResponse> response = controller.validateException("BUY", "BACKDATE", request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ExceptionWraper wrapper = (ExceptionWraper) response.getBody();
        assertEquals("ADMIN", wrapper.getApprovalDetails().get("roles").asText());
    }

    @Test
    void validateException_repositoryThrows_returnsInternalServerError() {
        when(costExceptionConfigRepository.getRolesForCostExceptionAction(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Config error"));

        ResponseEntity<GenericResponse> response = controller.validateException("BUY", "BACKDATE", request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void validateException_verifiesApproveActionUsed() {
        when(costExceptionConfigRepository.getRolesForCostExceptionAction("BUY", "BACKDATE", "Approve"))
                .thenReturn(Collections.emptyList());

        controller.validateException("BUY", "BACKDATE", request);

        verify(costExceptionConfigRepository).getRolesForCostExceptionAction("BUY", "BACKDATE", "Approve");
    }
}
