/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.cost.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.repository.PcmCostTypeRepository;

/**
 * Service class for PcmCostType operations.
 * Provides business logic and data access operations for cost types.
 */
@Service
@Transactional
public class PcmCostTypeService {

	private static final Logger logger = LoggerFactory.getLogger(PcmCostTypeService.class);

	@Autowired
	private PcmCostTypeRepository pcmCostTypeRepository;

	/**
	 * Get all cost types ordered by display order.
	 * 
	 * @return list of all cost types
	 */
	public List<PcmCostType> getAllCostTypes() {
		logger.info("Fetching all cost types");
		List<PcmCostType> costTypes = pcmCostTypeRepository.getAllCostTypes();
		logger.debug("Retrieved {} cost types", costTypes.size());
		return costTypes;
	}

	/**
	 * Get all cost types by filter (item category cost types).
	 * 
	 * @return list of item category cost types ordered by display order
	 */
	public List<PcmCostType> getAllItemCategoryCostTypes() {
		logger.info("Fetching all item category cost types");
		List<PcmCostType> costTypes = pcmCostTypeRepository.getAllItemCategoryCostTypes();
		logger.debug("Retrieved {} item category cost types", costTypes.size());
		return costTypes;
	}

	/**
	 * Get all cost types that are used in rollup calculations.
	 * 
	 * @return list of rollup cost types ordered by display order
	 */
	public List<PcmCostType> getAllRollupCostTypes() {
		logger.info("Fetching all rollup cost types");
		List<PcmCostType> costTypes = pcmCostTypeRepository.getAllRollupCostTypes();
		logger.debug("Retrieved {} rollup cost types", costTypes.size());
		return costTypes;
	}

	/**
	 * Get a specific cost type by its key.
	 * 
	 * @param costTypeKey the cost type key
	 * @return the cost type if found
	 */
	public PcmCostType getCostType(String costTypeKey) {
		logger.info("Fetching cost type with key: {}", costTypeKey);
		PcmCostType costType = pcmCostTypeRepository.getCostType(costTypeKey);
		if (costType != null) {
			logger.debug("Cost type found: {}", costTypeKey);
		} else {
			logger.warn("Cost type not found with key: {}", costTypeKey);
		}
		return costType;
	}

	/**
	 * Get all cost type keys as a set.
	 * Maps to Hibernate Criteria API code that collects all cost type keys.
	 * 
	 * @return set of all cost type keys
	 */
	public Set<String> getCostTypesKey() {
		logger.info("Fetching all cost type keys");
		Set<String> costTypes = pcmCostTypeRepository.getCostTypesKey();
		logger.debug("Retrieved {} cost type keys", costTypes.size());
		return costTypes;
	}

}
