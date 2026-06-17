/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.authentication.service;

import com.scplatform.pcm.accessControl.service.AccessControlService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.workflow.dto.Header;
import com.scplatform.pcm.workflow.entity.Workflow;
import com.scplatform.pcm.workflow.service.WorkFlowService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AppContextServiceTest {

    @Mock private WorkFlowService workFlowService;
    @Mock private AccessControlService accessControlService;
    @Mock private PcmConfigUtil configUtil;

    private AppContextService service;
    private ApplicationContext ctx;
    private Users user;
    private Role role;

    @BeforeEach
    void setUp() {
        service = new AppContextService(workFlowService, accessControlService, configUtil);
        user = new Users();
        user.setUserId("alice");
        user.setUserKey(7L);
        role = new Role();
        ctx = new ApplicationContext();
        ctx.setCurrentUser(user);
        ctx.setCurrentRole(role);
        ctx.setEnterpriseKey(99L);
    }

    @Test
    void getAvailableWorkflows_cachesIntoContext() {
        Workflow w = new Workflow();
        when(workFlowService.getTopLevelWorkflows()).thenReturn(Collections.singletonList(w));
        List<Workflow> result = service.getAvailableWorkflows(ctx);
        assertEquals(1, result.size());
        assertSame(result, ctx.getAvailableWorkflows());
    }

    @Test
    void getAccessableWorkflows_delegatesToAccessControl() {
        Workflow w = new Workflow();
        when(workFlowService.getTopLevelWorkflows()).thenReturn(Collections.singletonList(w));
        List<Workflow> filtered = Collections.singletonList(w);
        when(accessControlService.getAccessableWorkflows(any(), eq(role))).thenReturn(filtered);

        List<Workflow> result = service.getAccessableWorkflows(ctx);
        assertSame(filtered, result);
    }

    @Test
    void getAccessableWorkflowsGrouped_groupsByWorkflowGroup() {
        Workflow a1 = new Workflow();
        a1.setWorkflowGroup("A");
        a1.setWorkflowName("a1");
        Workflow a2 = new Workflow();
        a2.setWorkflowGroup("A");
        a2.setWorkflowName("a2");
        Workflow b1 = new Workflow();
        b1.setWorkflowGroup("B");
        b1.setWorkflowName("b1");
        when(workFlowService.getTopLevelWorkflows()).thenReturn(Arrays.asList(a1, a2, b1));
        when(accessControlService.getAccessableWorkflows(any(), eq(role)))
                .thenReturn(Arrays.asList(a1, a2, b1));

        Map<String, List<Workflow>> grouped = service.getAccessableWorkflowsGrouped(ctx);
        assertEquals(2, grouped.size());
        assertEquals(2, grouped.get("A").size());
        assertEquals(1, grouped.get("B").size());
    }

    @Test
    void hasAccess_threeArgDelegates() {
        when(accessControlService.hasAccess(eq("ENTITY"), eq("Read"), any(),
                eq(role), any())).thenReturn(true);
        assertTrue(service.hasAccess(ctx, "ENTITY", "Read"));
    }

    @Test
    void hasAccess_fourArgDelegates() {
        when(accessControlService.hasAccess(eq("ENTITY"), eq("Read"), eq("KEY"),
                any(), eq(role), any())).thenReturn(false);
        assertFalse(service.hasAccess(ctx, "ENTITY", "Read", "KEY"));
    }

    @Test
    void hasEventAccess_delegatesWithComposedKey() {
        when(accessControlService.hasAccess(eq("STATE_MODEL"), eq("MODEL_OP"), any(),
                eq(role), any())).thenReturn(true);
        assertTrue(service.hasEventAccess(ctx, "MODEL", "OP"));
    }

    @Test
    void getHasRestrictedVisibility_returnsFalseWhenGlobalAdmin() {
        when(accessControlService.hasAccess(eq("ADMIN"), eq("GlobalVisibility"), any(),
                eq(role), any())).thenReturn(true);
        assertFalse(service.getHasRestrictedVisibility(ctx));
    }

    @Test
    void getHasRestrictedVisibility_returnsTrueForExternalUserWithoutGlobalAccess() {
        when(accessControlService.hasAccess(eq("ADMIN"), eq("GlobalVisibility"), any(),
                eq(role), any())).thenReturn(false);
        when(accessControlService.getIsExternalUser(user, 99L)).thenReturn(true);
        assertTrue(service.getHasRestrictedVisibility(ctx));
    }

    @Test
    void getIsExternalUser_delegates() {
        when(accessControlService.getIsExternalUser(user, 99L)).thenReturn(true);
        assertTrue(service.getIsExternalUser(ctx));
    }

    @Test
    void getHasDataFilter_delegates() {
        when(accessControlService.doesUserHaveACLs(user, "CATEGORY", "Read")).thenReturn(true);
        assertTrue(service.getHasDataFilter(ctx, "CATEGORY"));
    }

    @Test
    void isWorkflow_returnsTrueWhenPathMatchesNestedUrl() {
        Workflow nested = new Workflow();
        nested.setWorkflowUrl("/path/foo");
        Workflow parent = new Workflow();
        parent.setNestedWorkflows(new LinkedHashSet<>(Collections.singletonList(nested)));
        when(workFlowService.getTopLevelWorkflows()).thenReturn(Collections.singletonList(parent));

        assertTrue(service.isWorkflow(ctx, "/path/foo"));
        assertFalse(service.isWorkflow(ctx, "/no/match"));
    }

    @Test
    void isWorkflowAccessible_walksAccessibleWorkflows() {
        Workflow nested = new Workflow();
        nested.setWorkflowUrl("/wf");
        Workflow parent = new Workflow();
        parent.setNestedWorkflows(new LinkedHashSet<>(Collections.singletonList(nested)));
        lenient().when(workFlowService.getTopLevelWorkflows())
                .thenReturn(Collections.singletonList(parent));
        when(accessControlService.getAccessableWorkflows(any(), eq(role)))
                .thenReturn(Collections.singletonList(parent));

        assertTrue(service.isWorkflowAccessible(ctx, "/wf"));
        assertFalse(service.isWorkflowAccessible(ctx, "/other"));
    }

    @Test
    void getAccessableWorkflowsMenuStr_returnsValidJson() throws Exception {
        // user with no favorites and no accessible workflows -> empty header JSON
        when(workFlowService.getTopLevelWorkflows()).thenReturn(Collections.<Workflow>emptyList());
        when(accessControlService.getAccessableWorkflows(any(), eq(role)))
                .thenReturn(Collections.<Workflow>emptyList());
        when(configUtil.getString(anyString(), anyString())).thenReturn("MAIN_LOADJOB");

        String json = service.getAccessableWorkflowsMenuStr(ctx);
        assertNotNull(json);
        assertTrue(json.startsWith("{"));
    }

    @Test
    void getAccessableWorkflowsMenu_buildsHeader() throws Exception {
        when(workFlowService.getTopLevelWorkflows()).thenReturn(Collections.<Workflow>emptyList());
        when(accessControlService.getAccessableWorkflows(any(), eq(role)))
                .thenReturn(Collections.<Workflow>emptyList());
        when(configUtil.getString(anyString(), anyString())).thenReturn("MAIN_LOADJOB");

        Header h = service.getAccessableWorkflowsMenu(ctx);
        assertNotNull(h);
        assertNotNull(h.getMenu());
        assertNotNull(h.getFavorites());
    }
}
