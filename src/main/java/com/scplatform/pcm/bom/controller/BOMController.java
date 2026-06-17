/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bom.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.authentication.service.AppContextService;
import com.scplatform.pcm.bom.dto.BomLineDto;
import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.bom.entity.PcmBomLineAttritionRate;
import com.scplatform.pcm.bom.entity.PcmDefectType;
import com.scplatform.pcm.bom.service.BomLineService;
import com.scplatform.pcm.bom.service.BomService;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.ums.dto.FavoriteErrorResponse;
import com.scplatform.pcm.ums.dto.GenericResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/loadBOMDetails")
@RequiredArgsConstructor
public class BOMController {

	private static final Logger logger = LogManager.getLogger(BOMController.class);

	private final BomService bomService;
	private final BomLineService bomLineService;

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/bomKey/{key}", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<GenericResponse> getBOMDetails(@PathVariable Long key,
			HttpServletRequest request) {
		try {
			ApplicationContext ac = AppContextHelper.getValidContext(request);
			Bom selectedBom = bomService.getBom(key);
			if (selectedBom == null) {
				GenericResponse response = new FavoriteErrorResponse("BOM not found for key: " + key);
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}
			List<BomLineDto> lines = new ArrayList<>();
			loadBomLines(selectedBom, 1, lines, selectedBom.getItem(), ac);
			logger.info("The size of lines list : " + lines.size());
			// Build response with JSON nodes
			ObjectMapper om = new ObjectMapper();
			ArrayNode responseArray = om.createArrayNode();
			for (BomLineDto data : lines) {
				logger.info("The line key is : " + data.getLine().getBomLineKey());
				ObjectNode jsonNode = (ObjectNode) bomLineService.getAsJson(data.getLine());
				ArrayNode attritionRatesNode = jsonNode.putArray("attritionRates");
				if (ac.isAttritionRateAllowed()) {
					for (Entry<PcmDefectType, PcmBomLineAttritionRate> entry : data.getAttritionRates().entrySet()) {
						ObjectNode o = om.createObjectNode();
						o.put(entry.getKey().getDefectName(), entry.getValue().getAttritionRate());
						attritionRatesNode.add(o);
					}
				}
				responseArray.add(jsonNode);
			}
			// Return as GenericResponse wrapper
			return new ResponseEntity<>((GenericResponse) responseArray, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("Unable to get bom lines", e);
			GenericResponse favoriteResponse = new FavoriteErrorResponse(e.getMessage());
			return new ResponseEntity<>(favoriteResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void loadBomLines(Bom bom, int level, List<BomLineDto> list, Item topLevelItem,
			ApplicationContext ac) {
		for (BomLine line : bom.getSortedBomLines()) {
			logger.info("The current bom executing is : " + bom.getBomKey() + " (" + bom.getBomName() + ")");
			BomLineDto data = new BomLineDto();
			data.setLevel(level);
			data.setLine(line);
			Bom child = line.getSubBom();
			if (child != null) {
				List<BomLineDto> children = new ArrayList<>();
				loadBomLines(child, level, children, child.getItem(), ac);
				if (!children.isEmpty()) {
					logger.info("Entered into the child part of block");
					data.getChildList().addAll(children);
				}
			}
			if (ac.isAttritionRateAllowed()) {
				loadAttritionRatesForBomLine(line, topLevelItem, data);
			}
			list.add(data);
		}
	}

	private void loadAttritionRatesForBomLine(BomLine line, final Item bomItem, BomLineDto data) {
		Map<PcmDefectType, PcmBomLineAttritionRate> arMap = bomLineService.getBomLineAttritionRates(line, bomItem);
		data.setAttritionRates(arMap);
	}
}
