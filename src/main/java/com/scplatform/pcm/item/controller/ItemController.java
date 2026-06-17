/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.controller;

import com.scplatform.pcm.searchframework.dto.SearchForm;
import com.scplatform.pcm.searchframework.service.SearchService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import com.scplatform.pcm.item.dto.ItemWrap;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.ums.dto.GenericResponse;
import com.scplatform.pcm.ums.dto.ItemErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Properties;


@Controller
@RequiredArgsConstructor
@Log4j2
public class ItemController {

	private final ItemService itemService;
    private final SearchService searchService;

    private static final String VIEW_ITEM_SEARCH = "/search/itemSearchPage";
    private static final String VIEW_ITEM_ONLY_SEARCH = "/search/itemOnlySearchPage";
    private static final String ITEM_SEARCH_FORM_NAME = "itemSearchForm";
    private static final String ITEM_ONLY_SEARCH_FORM_NAME = "itemOnlySearchForm";

    @GetMapping(value = "mcm/api/loadItemDetails/itemKey/{URL}")
	public @ResponseBody ResponseEntity<GenericResponse> getItemDetails(@PathVariable Long URL,
			HttpServletRequest request) {
		try {
			Item item = itemService.getItem(URL);
			if (item == null) {
				GenericResponse itemResponse = new ItemErrorResponse("Item not found for key: " + URL);
				return new ResponseEntity<>(itemResponse, HttpStatus.NOT_FOUND);
			}
	        ItemWrap itemWrap = new ItemWrap();
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> itemMap = objectMapper.convertValue(
            		itemService.getInlineItemNaturalKeyAsJSON(item),
            		new TypeReference<Map<String, Object>>() {
            		}
            );
            itemWrap.setItem(itemMap);

			return new ResponseEntity<>(itemWrap, HttpStatus.OK);
		}
		catch (Exception e) {
			log.error("Unable to get item details", e);
			GenericResponse itemResponse = new ItemErrorResponse(e.getMessage());
			return new ResponseEntity<>(itemResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


    @RequestMapping("/itemSearch")
    public String initItemSearch(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model)
            throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /itemSearch - initItemSearch at {}", startTime);
        Properties properties = new Properties();
        properties.put("definition", "SearchDefItem.xml");
        model.addAttribute(ITEM_SEARCH_FORM_NAME, form);
        searchService.init(properties, form, request, response);
        log.info("END /itemSearch - initItemSearch at {}, took {} ms", System.currentTimeMillis(), System.currentTimeMillis() - startTime);
        return VIEW_ITEM_SEARCH;
    }

    @RequestMapping("/submitItemSearch")
    public String searchItem(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model)
            throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /submitItemSearch - searchItem at {}", startTime);
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute(ITEM_SEARCH_FORM_NAME, form);
        searchService.search(properties, form, request, response);
        log.info("END /submitItemSearch - searchItem at {}, took {} ms", System.currentTimeMillis(), System.currentTimeMillis() - startTime);
        return VIEW_ITEM_SEARCH;
    }

    @RequestMapping("/itemOnlySearch")
    public String init(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model)
            throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /itemOnlySearch - init at {}", startTime);
        Properties properties = new Properties();
        properties.put("definition", "SearchDefItemOnly.xml");
        model.addAttribute(ITEM_ONLY_SEARCH_FORM_NAME, form);
        searchService.init(properties, form, request, response);
        log.info("END /itemOnlySearch - init at {}, took {} ms", System.currentTimeMillis(), System.currentTimeMillis() - startTime);
        return VIEW_ITEM_ONLY_SEARCH;
    }

    @RequestMapping("/submitItemOnlySearch")
    public String search(SearchForm form, HttpServletRequest request, HttpServletResponse response, Model model)
            throws Exception {
        long startTime = System.currentTimeMillis();
        log.info("START /submitItemOnlySearch - search at {}", startTime);
        Properties properties = new Properties();
        form = searchService.mergeRequestWithCachedForm(form, request);
        model.addAttribute(ITEM_ONLY_SEARCH_FORM_NAME, form);
        searchService.search(properties, form, request, response);
        log.info("END /submitItemOnlySearch - search at {}, took {} ms", System.currentTimeMillis(), System.currentTimeMillis() - startTime);
        return VIEW_ITEM_ONLY_SEARCH;
    }
}
