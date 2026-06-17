/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.controller;

import java.util.Properties;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scplatform.pcm.item.dto.CategoryManagementForm;
import com.scplatform.pcm.searchframework.service.SearchService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@RequiredArgsConstructor
@Log4j2
public class CategoryController {

	private static final String VIEW_CATEGORY_MANAGEMENT = "/mdm/categoryManagementPage";
	private static final String CATEGORY_MANAGEMENT_FORM_NAME = "categoryManagementForm";

	private final SearchService searchService;

	@RequestMapping("/startCategoryManagement")
	public String init(CategoryManagementForm form, HttpServletRequest request, HttpServletResponse response,
			Model model) throws Exception {
		long startTime = System.currentTimeMillis();
		log.info("START /startCategoryManagement - init at {}", startTime);
		Properties properties = new Properties();
		properties.put("definition", "SearchDefCategoryMgmt.xml");
		model.addAttribute(CATEGORY_MANAGEMENT_FORM_NAME, form);
		searchService.init(properties, form, request, response);
		log.info("END /startCategoryManagement - init at {}, took {} ms", System.currentTimeMillis(),
				System.currentTimeMillis() - startTime);
		return VIEW_CATEGORY_MANAGEMENT;
	}

	@RequestMapping("/submitCategoryManagementSearch")
	public String search(CategoryManagementForm form, HttpServletRequest request, HttpServletResponse response,
			Model model) throws Exception {
		long startTime = System.currentTimeMillis();
		log.info("START /submitCategoryManagementSearch - search at {}", startTime);
		try {
			Properties properties = new Properties();
			form = (CategoryManagementForm) searchService.mergeRequestWithCachedForm(form, request);
			model.addAttribute(CATEGORY_MANAGEMENT_FORM_NAME, form);
			searchService.search(properties, form, request, response);
		} catch (Throwable t) {
			log.error("Errors in /submitCategoryManagementSearch", t);
		}
		log.info("END /submitCategoryManagementSearch - search at {}, took {} ms", System.currentTimeMillis(),
				System.currentTimeMillis() - startTime);
		return VIEW_CATEGORY_MANAGEMENT;
	}
}
