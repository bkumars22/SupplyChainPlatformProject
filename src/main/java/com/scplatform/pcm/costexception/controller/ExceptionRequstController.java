/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.controller;

import java.util.List;
import java.util.Properties;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.costexception.dto.CostRecordExceptionForm;
import com.scplatform.pcm.costexception.dto.ExceptionWraper;
import com.scplatform.pcm.costexception.service.CostRecordExceptionService;
import com.scplatform.pcm.costexception.entity.CostException;
import com.scplatform.pcm.costexception.repository.CostExceptionConfigRepository;
import com.scplatform.pcm.costexception.service.PcmCostExceptionRequestLogic;
import com.scplatform.pcm.ums.dto.GenericResponse;
import com.scplatform.pcm.ums.dto.ItemErrorResponse;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.scplatform.pcm.searchframework.service.SearchService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
@RequiredArgsConstructor
public class ExceptionRequstController {

	private final CostExceptionConfigRepository costExceptionConfigRepository;
	private final UsersRepository usersRepository;
	private final PcmConfigUtil pcmConfigUtil;
	private final PcmCostExceptionRequestLogic costExceptionRequestLogic;
	private final SearchService searchService;
	private final CostRecordExceptionService costRecordExceptionService;
	private static final String EXCEPTION_REQUEST_PAGE = "/pricing/costrecordexception";

	@RequestMapping("/searchExceptionRequest")
	public String initExceptionRequest(CostRecordExceptionForm form,HttpServletRequest request,HttpServletResponse response, Model model)
	throws Exception {
		long startTime = System.currentTimeMillis();
        log.info("START /Search Exception - initExceptionRequest at {}", startTime);
        Properties properties = new Properties();
        properties.put("definition", "SearchDefCostRecordExceptionRequest.xml");
        model.addAttribute("costRecordExceptionForm", form);
		model.addAttribute("allRequestType", costRecordExceptionService.getAllRequestType());
        searchService.init(properties, form, request, response);
        log.info("END /Search Exception - initExceptionRequest at {}, took {} ms", System.currentTimeMillis(), System.currentTimeMillis() - startTime);
		return EXCEPTION_REQUEST_PAGE;
	}

	@RequestMapping("/submitSearchExceptionRequest")
	public String submitSearchExceptionRequest(CostRecordExceptionForm form, HttpServletRequest request,HttpServletResponse response, Model model)
	throws Exception{
		long startTime = System.currentTimeMillis();
        log.info("START /submitSearchExceptionRequest - at {}", startTime);
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute("costRecordExceptionForm", form);
		model.addAttribute("allRequestType", costRecordExceptionService.getAllRequestType());
        searchService.search(properties, form, request, response);
        log.info("END /submitSearchExceptionRequest - at {}, took {} ms", System.currentTimeMillis(), System.currentTimeMillis() - startTime);
		return EXCEPTION_REQUEST_PAGE;
	}

	@RequestMapping(value = "/costException/validateException", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<GenericResponse> validateException(@RequestParam(value="exceptionName", required = true) String exceptionName,
			@RequestParam(value = "exceptionApprover",required = true) String exceptionApprover,
			@RequestParam("exceptionOwner") String exceptionOwner,@RequestParam("costType") String costType,
			@RequestParam("requestType") String requestType,@RequestParam(value = "applicableODM") String applicableODM,
			@RequestParam("commodity") String commodity,@RequestParam("subTier") String subTier,
			@RequestParam("platform") String platform,@RequestParam(value = "lob") String lob,
			@RequestParam("exceptionId") String exceptionId,@RequestParam("previousCostType") String previousCostType,
			@RequestParam("isFilePresent") Boolean isFilePresent,HttpServletRequest request) {
		try {

			ObjectMapper om = new ObjectMapper();
			ObjectNode objectNode = om.createObjectNode();
			if(exceptionName.trim().isEmpty()) {
				objectNode.put("exceptionName",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.exceptionName",null, null));
			}else {
				try {
					CostException cost = costExceptionRequestLogic.getCRExceptionByExceptionName(exceptionName);
					if (cost != null && !exceptionId.equals(cost.getExceptionId())) {
						objectNode.put("exceptionName",SCPlatformMessages.INSTANCE.getMessage("exception.cost.exceptionName.exist",null, null));
					}
				} catch (Exception ex) {
					// Log error and continue with validation
					objectNode.put("exceptionName", "Error occured while fetching exception: " + ex.getMessage());
				}
			}
			if(exceptionApprover.trim().isEmpty()) {
				objectNode.put("exceptionApprover",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.exceptionApprover",null, null));
			}else {
				List<String> roles = costExceptionConfigRepository.getRolesForCostExceptionAction(costType, requestType, "Approve");
				Users approver = usersRepository.findByUserId(exceptionApprover).orElse(null);
				if(approver == null || (roles != null && !roles.isEmpty() && !roles.contains(approver.getRole().getRoleId()))) {
					objectNode.put("exceptionApprover",SCPlatformMessages.INSTANCE.getMessage("exception.cost.exception.user.invalid.user",null, null));
				}
			}
			if(exceptionOwner.trim().isEmpty()) {
				objectNode.put("exceptionOwner",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.exceptionOwner",null, null));
			}else {
				List<String> roles = pcmConfigUtil.getList("pcm.costrecord.exception.exceptionOwner.role");
				Users owner = usersRepository.findByUserId(exceptionOwner).orElse(null);
				if(owner == null || (roles != null && !roles.isEmpty() && !roles.contains(owner.getRole().getRoleId()))) {
					objectNode.put("exceptionOwner",SCPlatformMessages.INSTANCE.getMessage("exception.cost.exception.user.invalid.user",null, null));
				}
			}
			if(costType.trim().isEmpty()) {
				objectNode.put("costType",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.costType",null, null));
			}
			if(requestType.trim().isEmpty()) {
				objectNode.put("requestType",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.requestType",null, null));
			}
			if(applicableODM.trim().isEmpty()) {
				objectNode.put("applicableODM",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.odm",null, null));
			}
			if(commodity.trim().isEmpty()) {
				objectNode.put("commodity",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.commodity",null, null));
			}
			if(subTier.trim().isEmpty()) {
				objectNode.put("subTier",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.subTier",null, null));
			}
			if(platform.isEmpty()) {
				objectNode.put("platform",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.platform",null, null));
			}
			if(lob.trim().isEmpty()) {
				objectNode.put("lob",SCPlatformMessages.INSTANCE.getMessage("exception.cost.validation.lob",null, null));
			}
			
			if(isFilePresent)
			{
				costType = costType.trim();
				previousCostType = previousCostType.trim();
				if(!costType.equals(previousCostType)) {
					objectNode.put("costTypeChange",SCPlatformMessages.INSTANCE.getMessage("errors.exception.cost.type.mismatch",null, null));
				}
			}
			ExceptionWraper exceptionWraper = new ExceptionWraper();
			exceptionWraper.setCostException(objectNode);
			return new ResponseEntity<>(exceptionWraper, HttpStatus.OK);
		} catch (Exception e) {
			GenericResponse exceptionResponse = new ItemErrorResponse(e.getMessage());
			return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
