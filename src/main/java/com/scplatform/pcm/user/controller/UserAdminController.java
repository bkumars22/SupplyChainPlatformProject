/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.user.controller;

import com.scplatform.pcm.searchframework.service.SearchService;
import com.scplatform.pcm.user.dto.UserAdminForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Properties;

@Controller
@Log4j2
@RequiredArgsConstructor
public class UserAdminController {

    private final SearchService searchService;

    private static final String VIEW_USER_ADMIN_SEARCH = "/admin/userAdmin";

    @RequestMapping("/searchUser")
	public String init(UserAdminForm form, HttpServletRequest request,
                       HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        properties.put("definition", "SearchDefUser.xml");
        model.addAttribute("userAdminForm", form);
        searchService.init(properties, form, request, response);
        return VIEW_USER_ADMIN_SEARCH;
	}

    @RequestMapping("/submitUserSearch")
	public String search(UserAdminForm form, HttpServletRequest request,
                         HttpServletResponse response, Model model) throws Exception {
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        form.setSelectedUserKey(null);
        form.setSelectedTabId("");
        form.setPagingEnabled(true);
        model.addAttribute("userAdminForm", form);
        searchService.search(properties, form, request, response);
		return VIEW_USER_ADMIN_SEARCH;
	}
}
