/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scplatform.pcm.costexception.repository.CostExceptionConfigRepository;
import com.scplatform.pcm.costexception.dto.ExceptionWraper;
import com.scplatform.pcm.ums.dto.GenericResponse;
import com.scplatform.pcm.ums.dto.ItemErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/costException")
@RequiredArgsConstructor
public class ExceptionApproverRoleController {

	private final CostExceptionConfigRepository costExceptionConfigRepository;

	@RequestMapping(value = "/approverRoles", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<GenericResponse> validateException(@RequestParam("costType") String costType,
			@RequestParam("requestType") String requestType, HttpServletRequest request) {
		try {
			ObjectMapper om = new ObjectMapper();
			ObjectNode objectNode = om.createObjectNode();
			
			List<String> roles = costExceptionConfigRepository.getRolesForCostExceptionAction(costType, requestType, "Approve");
			
			String rolesString = roles.toString().substring(1, roles.toString().length()-1).replaceAll(" ","");
			objectNode.put("roles", rolesString);
			
			ExceptionWraper exceptionWraper = new ExceptionWraper();
			if(objectNode != null) {
				exceptionWraper.setApprovalDetails(objectNode);
			}
			return new ResponseEntity<>(exceptionWraper, HttpStatus.OK);
		} catch (Exception e) {
			GenericResponse exceptionResponse = new ItemErrorResponse(e.getMessage());
			return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
