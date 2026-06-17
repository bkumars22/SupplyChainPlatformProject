/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import com.scplatform.pcm.authentication.service.AppContextHelper;
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
import com.scplatform.pcm.ums.dto.FavoriteResponse;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.authentication.service.AppContextService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class GetFavoritesController {

	private final static Logger logger = LogManager.getLogger(GetFavoritesController.class);
	private final UsersRepository usersRepository;
    private final AppContextService appContextService;

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<FavoriteResponse> updateFavorites(@RequestParam MultiValueMap<String, String> params, 
			HttpServletRequest request)   {
		try {
			ApplicationContext ac = AppContextHelper.getValidContext(request);
			Users user = usersRepository.findById(ac.getCurrentUser().getUserKey()).orElse(null);
			FavoriteResponse header = (FavoriteResponse) appContextService.getAccessableWorkflowsMenu(ac);
			return new ResponseEntity<FavoriteResponse>(header, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to get favorites", e);
			FavoriteResponse favoriteResponse = new FavoriteErrorResponse(e.getMessage());
			return new ResponseEntity<FavoriteResponse>(favoriteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
