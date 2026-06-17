/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.alert.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scplatform.pcm.alert.dto.AlertForm;
import com.scplatform.pcm.alert.entity.Alert;
import com.scplatform.pcm.alert.service.AlertSearchService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@Log4j2
@RequiredArgsConstructor
public class AlertSearchController {


    private static final String FORM_ATTR    = "alertForm";
    private static final String VIEW_SUCCESS = "alertBoard";
    private static final String VIEW_FAILURE = "dashboard";

    private final AlertSearchService alertSearchService;


    private String resolveAlertType(HttpServletRequest request, AlertForm alertForm) {
        String path = request.getServletPath();
        if (path == null) {
            path = "";
        }
        if (!path.contains("AlertSearch")) {
            alertForm.setCurrentAlertTypeURL(path.replaceAll("/", "") + "Search");
        } else {
            alertForm.setCurrentAlertTypeURL(path.replaceAll("/", ""));
        }
        String type = path.replaceAll("/", "").replaceAll("AlertSearch", "").replaceAll("Alert", "");
        alertForm.setType(type);
        return type;
    }

    @RequestMapping("/alertSearch")
    public String init(AlertForm alertForm, HttpServletRequest request, HttpServletResponse response, Model model) {
        try {
            String type = resolveAlertType(request, alertForm);

            // Allow ?type=XYZ override coming from the JSP filter dropdown.
            String requestedType = request.getParameter("type");
            if (requestedType != null && !requestedType.isEmpty()) {
                type = requestedType;
                alertForm.setType(type);
            }

            alertForm.setAlert(new ArrayList<Alert>());
            alertForm.setAlertMap(new HashMap<String, Integer>());

            List<Alert> alerts = alertSearchService.getAlertsByUser(request);
            Map<String, Integer> alertMap = alertSearchService.buildAlertTypeCountMap(alerts);

            List<Alert> filtered = new ArrayList<>();
            for (Alert a : alerts) {
                if (type != null && type.equalsIgnoreCase(a.getAlertType())) {
                    filtered.add(a);
                }
            }
            alertForm.setAlertMap(alertMap);
            alertForm.setAlert(filtered);

            model.addAttribute(FORM_ATTR, alertForm);
            return VIEW_SUCCESS;
        } catch (Exception ex) {
            log.error("AlertSearch init failed", ex);
            model.addAttribute(FORM_ATTR, alertForm);
            return VIEW_FAILURE;
        }
    }
}
