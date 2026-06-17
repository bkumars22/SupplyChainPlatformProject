/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.user.dto.UserProfileForm;
import com.scplatform.pcm.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
public class EditProfileController {
    private static final String VIEW_USER_PROFILE_PAGE = "userProfilePage";
    private static final String REDIRECT_DASHBOARD = "redirect:/dashboard.do";

    private final UserService userService;

    @RequestMapping("/editProfile")
    public String viewUserProfile(HttpServletRequest request, HttpServletResponse response, Model model) {
        long startTime = System.currentTimeMillis();
        try {
            ApplicationContext appContext = AppContextHelper.getValidContext(request);
            UserProfileForm upf = new UserProfileForm();
            userService.populateUserProfileForm(upf, appContext.getCurrentUser(), appContext.getCurrentLocale());
            model.addAttribute("userProfileForm", upf);
            log.info("/editProfile success took {} ms", System.currentTimeMillis() - startTime);
            return VIEW_USER_PROFILE_PAGE;
        } catch (Exception ex) {
            log.error("Unable to load user profile page", ex);
            log.info("/editProfile failed after {} ms", System.currentTimeMillis() - startTime);
            return REDIRECT_DASHBOARD;
        }
    }

    @RequestMapping("/saveProfile")
    public String saveUserProfile(UserProfileForm upf, HttpServletRequest request, HttpServletResponse response,
            Model model) {
        long startTime = System.currentTimeMillis();
        try {
            ApplicationContext appContext = AppContextHelper.getValidContext(request);
            UserProfileForm savedForm = userService.saveUserProfile(upf, appContext);
            if (savedForm == null) {
                log.info("/saveProfile completed with redirect in {} ms", System.currentTimeMillis() - startTime);
                return REDIRECT_DASHBOARD;
            }
            model.addAttribute("userProfileForm", savedForm);
            log.info("/saveProfile success took {} ms", System.currentTimeMillis() - startTime);
            return VIEW_USER_PROFILE_PAGE;
        } catch (Exception ex) {
            log.error("Unable to save user profile", ex);
            log.info("/saveProfile failed after {} ms", System.currentTimeMillis() - startTime);
            return REDIRECT_DASHBOARD;
        }
    }
}
