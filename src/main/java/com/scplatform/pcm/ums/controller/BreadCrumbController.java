/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import java.util.Stack;

import com.scplatform.pcm.workflow.entity.Workflow;
import com.scplatform.pcm.workflow.service.WorkFlowService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.scplatform.pcm.ums.dto.FavoriteErrorResponse;
import com.scplatform.pcm.ums.dto.GenericResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("mcm/api/bread_crumb")
@RequiredArgsConstructor
public class BreadCrumbController {
	
	private final static Logger logger = LogManager.getLogger(BreadCrumbController.class);
	
	private final WorkFlowService workflowService;
	private final ObjectProvider<BreadCrumb> breadCrumbProvider;
	private final ObjectProvider<PageItem> pageItemProvider;
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<GenericResponse> getBreadCrumbs(@RequestParam MultiValueMap<String, String> params,  
			HttpServletRequest request)   {
		try {
			String url = params.getFirst("url");
			Stack<Workflow> breadCrumbStack = workflowService.getBreadCrumb(url);
			BreadCrumb breadCrumb = breadCrumbProvider.getObject();
			while (!breadCrumbStack.isEmpty()) {
				Workflow workflow = breadCrumbStack.pop();
				PageItem pageItem = pageItemProvider.getObject();
				pageItem.setKey(workflow.getWorkflowKey());
				pageItem.setName(workflow.getWorkflowName());
				pageItem.setUrl(workflow.getWorkflowUrl());
				breadCrumb.getPageItems().add(pageItem);				
			}			
			return new ResponseEntity<GenericResponse>(breadCrumb, HttpStatus.OK);			
		} 
		
		catch (Exception e) {
			logger.error("Unable to get favorites", e);
			GenericResponse favoriteResponse = new FavoriteErrorResponse(e.getMessage());
			return new ResponseEntity<GenericResponse>(favoriteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
