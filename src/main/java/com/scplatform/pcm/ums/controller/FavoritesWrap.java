/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import java.util.ArrayList;
import java.util.List;

import com.scplatform.pcm.ums.dto.FavoriteResponse;
import com.scplatform.pcm.ums.dto.Favorites;


public class FavoritesWrap implements FavoriteResponse {

	private List<Favorites> favorites;
	
	public FavoritesWrap() {
		this.favorites = new ArrayList<>();
	}

	public List<Favorites> getFavorites() {
		return favorites;
	}

	public void setFavorites(List<Favorites> favorites) {
		this.favorites = favorites;
	}
	
	
}
