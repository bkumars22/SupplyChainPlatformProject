/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.authentication.service;

import com.scplatform.pcm.accessControl.service.AccessControlService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.ums.controller.FavoritesWrap;
import com.scplatform.pcm.workflow.dto.Children;
import com.scplatform.pcm.workflow.dto.Header;
import com.scplatform.pcm.workflow.dto.Menu;
import com.scplatform.pcm.workflow.entity.Workflow;
import com.scplatform.pcm.ums.dto.Favorites;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.workflow.service.WorkFlowService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AppContextService {

    private static final Logger logger = LoggerFactory.getLogger(AppContextService.class);

    private final WorkFlowService workFlowService;
    private final AccessControlService accessControlService;
    private final PcmConfigUtil configUtil;

    public AppContextService(WorkFlowService workFlowService,
                             AccessControlService accessControlService,
                             PcmConfigUtil configUtil) {
        this.workFlowService = workFlowService;
        this.accessControlService = accessControlService;
        this.configUtil = configUtil;
    }

    // ======================== Workflow ========================

    /**
     * Loads and caches the top-level workflows into the ApplicationContext.
     *
     * @param ctx the application context
     * @return list of available workflows
     */
    public List<Workflow> getAvailableWorkflows(ApplicationContext ctx) {
        ctx.setAvailableWorkflows(workFlowService.getTopLevelWorkflows());
        return ctx.getAvailableWorkflows();
    }

    /**
     * Returns workflows the user's current role has access to.
     *
     * @param ctx the application context
     * @return list of accessible workflows
     */
    public List<Workflow> getAccessableWorkflows(ApplicationContext ctx) {
        return accessControlService.getAccessableWorkflows(
                getAvailableWorkflows(ctx), ctx.getCurrentRole());
    }

    /**
     * Returns accessible workflows grouped by their workflow group name.
     *
     * @param ctx the application context
     * @return LinkedHashMap keyed by group name → list of workflows
     */
    public Map<String, List<Workflow>> getAccessableWorkflowsGrouped(ApplicationContext ctx) {
        LinkedHashMap<String, List<Workflow>> map = new LinkedHashMap<>();
        List<Workflow> wfs = getAccessableWorkflows(ctx);
        String lastGroup = "";
        List<Workflow> currentList = null;
        for (Workflow wf : wfs) {
            if (!lastGroup.equals(wf.getWorkflowGroup())) {
                lastGroup = wf.getWorkflowGroup();
                currentList = new ArrayList<>();
                map.put(lastGroup, currentList);
            }
            currentList.add(wf);
        }
        return map;
    }

    // ======================== Menu / Favorites ========================

    /**
     * Builds the accessible workflows menu and serializes it to JSON string.
     *
     * @param ctx the application context
     * @return JSON string of the menu Header
     * @throws IOException on serialization failure
     */
    public String getAccessableWorkflowsMenuStr(ApplicationContext ctx) throws IOException {
        Header header = getAccessableWorkflowsMenu(ctx);
        return new ObjectMapper().writeValueAsString(header);
    }

    /**
     * Builds the accessible workflows menu as a {@link Header} object
     * containing menus and favorites.
     *
     * @param ctx the application context
     * @return Header with menus and favorites
     * @throws IOException on favorites parsing failure
     */
    public Header getAccessableWorkflowsMenu(ApplicationContext ctx) throws IOException {
        List<Workflow> wfs = getAccessableWorkflows(ctx);
        String mainLoadJob = configUtil.getString("pcm.menu.workflow.manage.load.job", "MAIN_LOADJOB");

        Header header = new Header();
        header.setFavorites(getUserFavoritesList(ctx));

        List<List<Menu>> listOfMenuList = new ArrayList<>();
        List<Menu> menus = new ArrayList<>();

        for (Workflow workflow : wfs) {
            if (workflow.getWorkflowUrl() == null) {
                Menu menu = new Menu();
                menu.setName(workflow.getWorkflowName());
                menu.setText(workflow.getWorkflowName());

                List<List<Children>> listOfChildrenList = new ArrayList<>();
                List<Children> childList = new ArrayList<>();

                Iterator<?> itr = workflow.getNestedWorkflows().iterator();
                while (itr.hasNext()) {
                    Workflow childWorkflow = (Workflow) itr.next();
                    if (childWorkflow.getWorkflowKey().equals(mainLoadJob)) {
                        Children child = buildChild(childWorkflow);
                        List<Children> childListManage = new ArrayList<>();
                        childListManage.add(child);
                        listOfChildrenList.add(childListManage);
                        continue;
                    }
                    childList.add(buildChild(childWorkflow));
                }

                if (workflow.getWorkflowKey().equals("MAIN_UPLOAD_MANAGE")) {
                    Children menuUpload = new Children();
                    menuUpload.setName("Upload");
                    menuUpload.setText("Upload");
                    List<List<Children>> listOfChildrenListUpload = new ArrayList<>();
                    List<Children> childListUpload = new ArrayList<>();
                    listOfChildrenListUpload.add(childList);
                    menuUpload.setChildren(listOfChildrenListUpload);
                    childListUpload.add(menuUpload);
                    listOfChildrenList.add(childListUpload);
                } else {
                    listOfChildrenList.add(childList);
                }

                menu.setChildren(listOfChildrenList);
                menus.add(menu);
            }
        }

        listOfMenuList.add(menus);
        header.setMenu(listOfMenuList);
        return header;
    }

    /**
     * Parses the user's stored favorites JSON and returns the list.
     *
     * @param ctx the application context
     * @return list of favorites, empty if no favorites are stored
     * @throws IOException on JSON parse failure
     */
    private List<Favorites> getUserFavoritesList(ApplicationContext ctx)
            throws JsonParseException, JsonMappingException, IOException {
        Users user = ctx.getCurrentUser();
        String userFavorites = user.getFavorites();
        if (userFavorites == null) {
            return new ArrayList<>();
        }
        FavoritesWrap favoritesWrap = new ObjectMapper().readValue(userFavorites, FavoritesWrap.class);
        return favoritesWrap.getFavorites() != null ? favoritesWrap.getFavorites() : new ArrayList<>();
    }

    // ======================== Access Control ========================

    /**
     * Checks entity-level access for the user's current role.
     *
     * @param ctx        the application context
     * @param entityType the entity type
     * @param op         the operation (e.g. "Read", "Write")
     * @return {@code true} if access is granted
     */
    public boolean hasAccess(ApplicationContext ctx, String entityType, String op) {
        return accessControlService.hasAccess(
                entityType, op, null, ctx.getCurrentRole(), ctx.getCurrentRole().getAcls());
    }

    /**
     * Checks entity-level access with an entity-type key.
     *
     * @param ctx           the application context
     * @param entityType    the entity type
     * @param op            the operation
     * @param entityTypeKey the specific entity key
     * @return {@code true} if access is granted
     */
    public boolean hasAccess(ApplicationContext ctx, String entityType, String op, String entityTypeKey) {
        return accessControlService.hasAccess(
                entityType, op, entityTypeKey, null, ctx.getCurrentRole(), ctx.getCurrentRole().getAcls());
    }

    /**
     * Checks state-model event access for the user's current role.
     *
     * @param ctx        the application context
     * @param stateModel the state model name
     * @param op         the event/operation name
     * @return {@code true} if access is granted
     */
    public boolean hasEventAccess(ApplicationContext ctx, String stateModel, String op) {
        return accessControlService.hasAccess(
                "STATE_MODEL", stateModel + "_" + op, null,
                ctx.getCurrentRole(), ctx.getCurrentRole().getAcls());
    }

    /**
     * Checks if the user's current role has restricted visibility
     * (is external user without global visibility admin rights).
     *
     * @param ctx the application context
     * @return {@code true} if visibility is restricted
     */
    public boolean getHasRestrictedVisibility(ApplicationContext ctx) {
        if (hasAccess(ctx, "ADMIN", "GlobalVisibility")) {
            return false;
        }
        return getIsExternalUser(ctx);
    }

    /**
     * Returns true if current user is not a member of the owning enterprise.
     *
     * @param ctx the application context
     * @return {@code true} if the user is external
     */
    public boolean getIsExternalUser(ApplicationContext ctx) {
        return accessControlService.getIsExternalUser(ctx.getCurrentUser(), ctx.getEnterpriseKey());
    }

    /**
     * Returns whether the user has a data ACL filter for the given type.
     *
     * @param ctx      the application context
     * @param dataType the data type to check
     * @return {@code true} if the user has ACLs for the data type
     */
    public boolean getHasDataFilter(ApplicationContext ctx, String dataType) {
        return accessControlService.doesUserHaveACLs(ctx.getCurrentUser(), dataType, "Read");
    }

    // ======================== Workflow path checks ========================

    /**
     * Checks whether the given request path matches any known workflow URL.
     *
     * @param ctx         the application context
     * @param requestPath the path to check
     * @return {@code true} if the path is a workflow URL
     */
    public boolean isWorkflow(ApplicationContext ctx, String requestPath) {
        List<Workflow> allWorkflows = getAvailableWorkflows(ctx);
        for (Workflow wf : allWorkflows) {
            for (Object obj : wf.getNestedWorkflows()) {
                Workflow cwf = (Workflow) obj;
                if (requestPath.equals(cwf.getWorkflowUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks whether the given request path is accessible for the user's current role.
     *
     * @param ctx         the application context
     * @param requestPath the path to check
     * @return {@code true} if the path is accessible
     */
    public boolean isWorkflowAccessible(ApplicationContext ctx, String requestPath) {
        List<Workflow> wfs = getAccessableWorkflows(ctx);
        for (Workflow wf : wfs) {
            for (Object obj : wf.getNestedWorkflows()) {
                Workflow cwf = (Workflow) obj;
                if (requestPath.equals(cwf.getWorkflowUrl())) {
                    return true;
                }
            }
        }
        return false;
    }

    // ======================== Private helpers ========================

    private Children buildChild(Workflow workflow) {
        Children child = new Children();
        child.setName(workflow.getWorkflowName());
        child.setText(workflow.getWorkflowName());
        child.setUrl(workflow.getWorkflowUrl());
        return child;
    }
}
