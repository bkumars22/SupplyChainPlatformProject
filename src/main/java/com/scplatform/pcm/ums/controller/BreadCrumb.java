/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ums.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.scplatform.pcm.ums.dto.GenericResponse;

@Component
@Scope("prototype")
public class BreadCrumb implements GenericResponse {
	private List<PageItem> pageItems;
	
	public BreadCrumb() {
		this.pageItems = new ArrayList<>();
	}

	public List<PageItem> getPageItems() {
		return pageItems;
	}

	public void setPageItems(List<PageItem> pageItems) {
		this.pageItems = pageItems;
	}
}
