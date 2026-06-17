/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.authentication.service.AppContextService;
import com.scplatform.pcm.ums.dto.FavoriteErrorResponse;
import com.scplatform.pcm.ums.dto.FavoriteResponse;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/save_favorites")
@RequiredArgsConstructor
public class UpdateFavoritesController {

	private final static Logger logger = LogManager.getLogger(UpdateFavoritesController.class);

	private final UsersRepository usersRepository;

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	@Transactional
	public @ResponseBody ResponseEntity<FavoriteResponse> updateFavorites(@RequestBody FavoritesWrap favoritesWrap, HttpServletRequest request)  {
		FavoriteResponse favoriteResponse = null;
		try {			
			ApplicationContext ac = AppContextHelper.getValidContext(request);
			Users user = usersRepository.findById(ac.getCurrentUser().getUserKey()).orElse(null);			
			ObjectMapper objectMapper = new ObjectMapper();			
			String jsonFavorites = objectMapper.writeValueAsString(favoritesWrap);
            user.setFavorites(jsonFavorites);
            HttpSession session = request.getSession();
            session.setAttribute("favoritesWrap", jsonFavorites);
            
			usersRepository.save(user);			
			favoriteResponse = favoritesWrap;
			return new ResponseEntity<>(favoriteResponse, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("Unable to add favorite link", e);	
			favoriteResponse = new FavoriteErrorResponse(e.getMessage());
		}
		return new ResponseEntity<>(favoriteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
