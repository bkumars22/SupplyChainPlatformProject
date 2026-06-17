/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.upload.dto.LoadJobAdminForm;
import com.scplatform.pcm.upload.service.LoadJobAdminService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Controller for the Load Job detail page.
 * Migrated from {@code LoadJobAdminAction}.
 *
 * All write operations arrive as POST (the JSP sets document.forms[0].action and submits).
 * The GET readLoadJob is also reachable directly via link from uploadFile.jsp.
 */
@Log4j2
@Controller
@RequiredArgsConstructor
public class LoadJobAdminController {

    private final LoadJobAdminService loadJobAdminService;

    private static final String VIEW = "loadJobDetailPage";

    // -----------------------------------------------------------------------
    // GET — render page (navigate from uploadFile.jsp success link)
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/readLoadJob", method = RequestMethod.GET)
    public String readLoadJob(HttpServletRequest request,
                              @RequestParam(required = false) String selectedLoadJobKey,
                              Model model) {
        return handleRead(request, selectedLoadJobKey, model);
    }

    // -----------------------------------------------------------------------
    // POST — refresh / re-read (goRefresh submits the form back to readLoadJob.do)
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/readLoadJob", method = RequestMethod.POST)
    public String refreshLoadJob(HttpServletRequest request,
                                 @ModelAttribute LoadJobAdminForm formBean,
                                 Model model) {
        return handleRead(request, formBean.getSelectedLoadJobKey(), model);
    }

    // -----------------------------------------------------------------------
    // POST — delete
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/deleteLoadJob", method = RequestMethod.POST)
    public String deleteLoadJob(HttpServletRequest request,
                                @ModelAttribute LoadJobAdminForm formBean,
                                Model model) {
        LoadJobAdminForm form = new LoadJobAdminForm();
        try {
            form = loadJobAdminService.deleteLoadJob(request, formBean.getSelectedLoadJobKey());
        } catch (InvalidUserContext e) {
            log.warn("Access denied on deleteLoadJob: {}", e.getMessage());
            form.setJobErrorDetails(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting job", e);
            form.setJobErrorDetails(e.getLocalizedMessage());
        }
        model.addAttribute("loadJobAdminForm", form);
        addAppContext(request, model);
        return VIEW;
    }

    // -----------------------------------------------------------------------
    // POST — replay
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/replayLoadJob", method = RequestMethod.POST)
    public String replayLoadJob(HttpServletRequest request,
                                @ModelAttribute LoadJobAdminForm formBean,
                                Model model) {
        LoadJobAdminForm form = new LoadJobAdminForm();
        try {
            form = loadJobAdminService.replayLoadJob(request, formBean.getSelectedLoadJobKey());
        } catch (InvalidUserContext e) {
            log.warn("Access denied on replayLoadJob: {}", e.getMessage());
            form.setJobErrorDetails(e.getMessage());
        } catch (Exception e) {
            log.error("Error replaying job", e);
            form.setJobErrorDetails(e.getLocalizedMessage());
        }
        model.addAttribute("loadJobAdminForm", form);
        addAppContext(request, model);
        return VIEW;
    }

    // -----------------------------------------------------------------------
    // POST — refresh async status
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/updateAsnycStatus", method = RequestMethod.POST)
    public String updateAsyncStatus(HttpServletRequest request,
                                    @ModelAttribute LoadJobAdminForm formBean,
                                    Model model) {
        LoadJobAdminForm form = new LoadJobAdminForm();
        try {
            form = loadJobAdminService.updateAsyncStatus(request, formBean.getSelectedLoadJobKey());
        } catch (InvalidUserContext e) {
            log.warn("Access denied on updateAsyncStatus: {}", e.getMessage());
            form.setJobErrorDetails(e.getMessage());
        } catch (Exception e) {
            log.error("Error updating async status", e);
            form.setJobErrorDetails(e.getLocalizedMessage());
        }
        model.addAttribute("loadJobAdminForm", form);
        addAppContext(request, model);
        return VIEW;
    }

    // -----------------------------------------------------------------------
    // POST — clear load errors
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/clearLoadErrors", method = RequestMethod.POST)
    public String clearLoadErrors(HttpServletRequest request,
                                  @ModelAttribute LoadJobAdminForm formBean,
                                  @RequestParam(value = "selectedEventKeys", required = false)
                                      List<Long> selectedEventKeys,
                                  Model model) {
        LoadJobAdminForm form = new LoadJobAdminForm();
        try {
            form = loadJobAdminService.clearLoadErrors(
                    request, formBean.getSelectedLoadJobKey(), selectedEventKeys);
        } catch (InvalidUserContext e) {
            log.warn("Access denied on clearLoadErrors: {}", e.getMessage());
            form.setJobErrorDetails(e.getMessage());
        } catch (Exception e) {
            log.error("Error clearing load errors", e);
            form.setJobErrorDetails(e.getLocalizedMessage());
        }
        model.addAttribute("loadJobAdminForm", form);
        addAppContext(request, model);
        return VIEW;
    }

    // -----------------------------------------------------------------------
    // POST — set business alias (correct BE alias event)
    // -----------------------------------------------------------------------

    @RequestMapping(value = "/setBusinessAlias", method = RequestMethod.POST)
    public String setBusinessAlias(HttpServletRequest request,
                                   @ModelAttribute LoadJobAdminForm formBean,
                                   @RequestParam(value = "selectedEventKeys", required = false)
                                       List<Long> selectedEventKeys,
                                   Model model) {
        LoadJobAdminForm form = new LoadJobAdminForm();
        try {
            form = loadJobAdminService.setBusinessAlias(
                    request,
                    formBean.getSelectedLoadJobKey(),
                    formBean.getAssignToBusinessKey(),
                    formBean.isClearAll(),
                    selectedEventKeys);
        } catch (InvalidUserContext e) {
            log.warn("Access denied on setBusinessAlias: {}", e.getMessage());
            form.setJobErrorDetails(e.getMessage());
        } catch (Exception e) {
            log.error("Error setting business alias", e);
            form.setJobErrorDetails(e.getLocalizedMessage());
        }
        model.addAttribute("loadJobAdminForm", form);
        addAppContext(request, model);
        return VIEW;
    }

    // -----------------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------------

    private String handleRead(HttpServletRequest request, String loadJobKey, Model model) {
        LoadJobAdminForm form = new LoadJobAdminForm();
        try {
            form = loadJobAdminService.readLoadJob(request, loadJobKey);
        } catch (InvalidUserContext e) {
            log.warn("Invalid user context on readLoadJob: {}", e.getMessage());
            form.setJobErrorDetails(e.getMessage());
        } catch (Exception e) {
            log.error("Error loading job detail for key {}: {}", loadJobKey, e.getMessage(), e);
            form.setJobErrorDetails(e.getLocalizedMessage());
        }
        model.addAttribute("loadJobAdminForm", form);
        addAppContext(request, model);
        return VIEW;
    }

    private void addAppContext(HttpServletRequest request, Model model) {
        ApplicationContext ac = AppContextHelper.getContextOrNull(request);
        model.addAttribute("appContext", ac);
    }
}
