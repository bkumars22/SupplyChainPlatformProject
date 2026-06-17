/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionWraperTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    void noArgsConstructor_fieldsAreNull() {
        ExceptionWraper w = new ExceptionWraper();
        assertNull(w.getCostException());
        assertNull(w.getApprovalDetails());
    }

    @Test
    void setCostException_roundTrip() {
        ExceptionWraper w = new ExceptionWraper();
        ObjectNode node = MAPPER.createObjectNode();
        node.put("exceptionId", "EX-001");
        w.setCostException(node);

        JsonNode retrieved = w.getCostException();
        assertNotNull(retrieved);
        assertEquals("EX-001", retrieved.get("exceptionId").asText());
    }

    @Test
    void setApprovalDetails_roundTrip() {
        ExceptionWraper w = new ExceptionWraper();
        ObjectNode node = MAPPER.createObjectNode();
        node.put("roles", "MANAGER,APPROVER");
        w.setApprovalDetails(node);

        JsonNode retrieved = w.getApprovalDetails();
        assertNotNull(retrieved);
        assertEquals("MANAGER,APPROVER", retrieved.get("roles").asText());
    }

    @Test
    void bothFields_independentlySet() {
        ExceptionWraper w = new ExceptionWraper();
        ObjectNode exception = MAPPER.createObjectNode();
        exception.put("state", "DRAFT");
        ObjectNode approval = MAPPER.createObjectNode();
        approval.put("approver", "user1");

        w.setCostException(exception);
        w.setApprovalDetails(approval);

        assertEquals("DRAFT", w.getCostException().get("state").asText());
        assertEquals("user1", w.getApprovalDetails().get("approver").asText());
    }

    @Test
    void implementsGenericResponse() {
        ExceptionWraper w = new ExceptionWraper();
        assertTrue(w instanceof com.scplatform.pcm.ums.dto.GenericResponse);
    }

    @Test
    void equalsAndHashCode_sameContent() {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("key", "value");

        ExceptionWraper a = new ExceptionWraper();
        a.setCostException(node);

        ExceptionWraper b = new ExceptionWraper();
        b.setCostException(node);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toString_includesFieldNames() {
        ExceptionWraper w = new ExceptionWraper();
        String s = w.toString();
        assertNotNull(s);
        assertTrue(s.contains("costException") || s.contains("ExceptionWraper"));
    }
}
