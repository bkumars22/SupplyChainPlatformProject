/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import java.util.List;

import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.ums.dto.Favorites;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.service.UserSessionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.scplatform.pcm.ums.dto.FavoriteErrorResponse;
import com.scplatform.pcm.ums.dto.FavoriteResponse;
import com.scplatform.pcm.authentication.service.AppContextService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/add_fav_link")
public class AddFavoriteLinkController {

	private final static Logger logger = LogManager.getLogger(AddFavoriteLinkController.class);

	private final UserSessionService userSessionService;
	private final EntityManager entityManager;

	public AddFavoriteLinkController(UserSessionService userSessionService, EntityManager entityManager) {
		this.userSessionService = userSessionService;
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<FavoriteResponse> addFavoriteLink(@RequestBody FavoriteForm favoriteForm, HttpServletRequest request)  {
		FavoriteResponse favoriteResponse = null;
		try {			
			ApplicationContext ac = AppContextHelper.getValidContext(request);

			Users user = userSessionService.findUserByKey(ac.getCurrentUser().getUserKey());
			String userFavorites = user.getFavorites();
			ObjectMapper objectMapper = new ObjectMapper();
			FavoritesWrap favoritesWrap = new FavoritesWrap();
			if (userFavorites != null) {
				favoritesWrap = objectMapper.readValue(userFavorites, FavoritesWrap.class);
			}						
			List<Favorites> favoritesList = favoritesWrap.getFavorites();
			boolean isNewLink = true;
			for (Favorites favorite : favoritesList) {
				String favUrl = favorite.getUrl();
				if (favoriteForm.getUrl().equalsIgnoreCase(favUrl)) {	
					favorite.setId(favoriteForm.getFavName());
					favorite.setText(favoriteForm.getFavName());
					favorite.setTitle(favoriteForm.getFavName());
					favorite.setHome(false);       // ✅ Lombok generates setHome() for isHome field
					favorite.setExternal(false);   // ✅ Lombok generates setExternal() for isExternal field
					isNewLink = false;
					break;
				}
			}
			if (isNewLink) {
				Favorites fav = new Favorites();
				fav.setId(favoriteForm.getFavName());
				fav.setTitle(favoriteForm.getFavName());
				fav.setText(favoriteForm.getFavName());
				fav.setHome(false);      // ✅ Lombok: setHome() for isHome field
				fav.setUrl(favoriteForm.getUrl());
				fav.setExternal(false);  // ✅ Lombok: setExternal() for isExternal field
				favoritesList.add(fav);
			}
			String jsonFavorites = objectMapper.writeValueAsString(favoritesWrap);
            user.setFavorites(jsonFavorites);
            HttpSession session = request.getSession();
            session.setAttribute("favoritesWrap", jsonFavorites);

			try {
				userSessionService.saveOrUpdate(user);
				entityManager.flush();
			} catch (Exception e) {				
				 logger.error("Error while saving user", e);
				 favoriteResponse = new FavoriteErrorResponse(e.getMessage());
				 return new ResponseEntity<FavoriteResponse>(favoriteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
			}			
			favoriteResponse = favoritesWrap;
			return new ResponseEntity<FavoriteResponse>(favoriteResponse, HttpStatus.OK);
		}
		catch (Exception e) {
			logger.error("Unable to add favorite link", e);	
			favoriteResponse = new FavoriteErrorResponse(e.getMessage());
		}
		return new ResponseEntity<FavoriteResponse>(favoriteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
