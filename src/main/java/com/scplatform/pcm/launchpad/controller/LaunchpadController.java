/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.controller;

import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.launchpad.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.scplatform.pcm.ums.service.AuthenticateUser;
import com.scplatform.pcm.ums.controller.GetAboutInfo;
import com.scplatform.pcm.ums.controller.MenuItemGen;
import com.scplatform.pcm.authentication.service.AppContextService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/launch_pad")
@RequiredArgsConstructor
public class LaunchpadController {

	private final static Logger logger = LogManager.getLogger(LaunchpadController.class);
	private final MenuItemGen menuItemGen;
	private final UsersRepository usersRepository;
	
	
	@RequestMapping(value = "/app_about", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<GenericResponse> appAbout(@RequestParam MultiValueMap<String, String> params,  
			HttpServletRequest request) {
		try {			
			logger.info("in app_about call");
			String tenantName = params.getFirst("tenantName");
			String solutionName = params.getFirst("solutionName");			 
			About about = new GetAboutInfo().about();
			return new ResponseEntity<GenericResponse>(about, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to get about", e);			
			GenericResponse favoriteResponse = new FavoriteErrorResponse(e.getMessage());
			return new ResponseEntity<GenericResponse>(favoriteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@RequestMapping(value = "/app_menu", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<GenericResponse> getMenuLayout(@RequestParam MultiValueMap<String, String> params,  
			HttpServletRequest request)   {
		try {
			logger.info("in app_menu call");
			checkForUserSession(request);			
			String tenantName = params.getFirst("tenantName");
			String solutionName = params.getFirst("solutionName");
			String currentApp = params.getFirst("currentApp");			
			ApplicationContext ac = AppContextHelper.getValidContext(request);
			Users user = usersRepository.findById(ac.getCurrentUser().getUserKey()).orElse(null);
			HeaderWrap headerWrap = new HeaderWrap();
			Header header = new Header();
			headerWrap.setHeader(header);

			//set logo
			Logo logo = new Logo();
			logo.setUrl("skins/e2-modern/images/main_logo_277x65.png");
			logo.setTitle("");
			header.setLogo(logo);

			//set banner
			Banner banner = new Banner();
			banner.setType(System.getProperty("e2.env.subtype"));
			header.setBanner(banner);


			//set help
			Help help = new Help();
			help.setUrl("javascript:goShowContentPageHelp()");
			help.setTitle("Help");
			help.setApp(currentApp);
			help.setLabel("Help");
			help.setName("Help");
			header.setHelp(help);

			//set user
			//TODO backtoCLP action to be implemented
			User u = new User();
			u.setUsername(user.getUserId());
			u.setName(user.getUserName());
			u.setImage("skins/e2-modern/images/account_circle.svg");
			u.setTitle("My Profile");
			u.setShowRole(true);
			u.setRole(user.getRole().getRoleName());
			//TODO backtoCLP action to be implemented
			u.setBackToCLP("javascript:MenuModule.logout()");
			header.setUser(u);

			//set menu
			Menu menu = new Menu();
			menu.setLabel("Menu");
			menu.setTitle("Menu");
			Filter filter = new Filter();
			filter.setTitle("Filter workflows by typing a few words");
			filter.setPlaceholder("Filter workflows");
			menu.setFilter(filter);
			//TODO get actual menu items			
			menu.setItems(menuItemGen.generateMenuItems(ac, currentApp));
			header.setMenu(menu);
			ObjectMapper objectMapper = new ObjectMapper();
			String headerWRapStr = objectMapper.writeValueAsString(headerWrap);
			logger.info("This is header wrap json");
			logger.info(headerWRapStr);

			return new ResponseEntity<GenericResponse>(headerWrap, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to get app menu", e);			
			GenericResponse favoriteResponse = new FavoriteErrorResponse(e.getMessage());
			return new ResponseEntity<GenericResponse>(favoriteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private void checkForUserSession(HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession(false);
		if (session != null) {
			logger.info("Already authenticated");
		}
		else {
			logger.info("Not yet authenticated, authenticating request coming from CLP");
			new AuthenticateUser().authenticate(request);
		}		
	}
}