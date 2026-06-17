/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.common.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.common.entity.MultiPurposeUses;
import com.scplatform.pcm.common.repository.MultiPurposeUsesRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class MultiPurposeUsesService {

	private static final Logger logger = LoggerFactory.getLogger(MultiPurposeUsesService.class);

	private final MultiPurposeUsesRepository multiPurposeUsesRepository;

	/**
	 * Get all MultiPurposeUses filtered by userId and filterType.
	 * Maps to Hibernate Criteria API code with eq and like restrictions.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type pattern (stringParam2 with LIKE)
	 * @return set of matching MultiPurposeUses
	 */
	public Set<MultiPurposeUses> getAllMultiPurposeList(long userId, String filterType) {
		logger.info("Fetching MultiPurposeUses with userId: {} and filterType: {}", userId, filterType);
		Set<MultiPurposeUses> result = multiPurposeUsesRepository.getAllMultiPurposeList(userId, filterType);
		logger.debug("Retrieved {} MultiPurposeUses records", result.size());
		return result;
	}

	/**
	 * Get all display grids filtered by userId and filterType.
	 * Maps to Hibernate HQL query and converts results to a Map.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2)
	 * @return map of id to stringParam1 for GRID_VIEW objects
	 */
	public Map<Long, String> getAllDisplay(long userId, String filterType) {
		logger.info("Fetching all displays with userId: {} and filterType: {}", userId, filterType);
		
		Map<Long, String> allDisplay = new HashMap<>();
		List<Object[]> listResult = multiPurposeUsesRepository.getAllDisplay(userId, filterType);
		
		if (listResult != null && !listResult.isEmpty()) {
			for (Object[] aRow : listResult) {
				try {
					Long id = Long.parseLong(aRow[0].toString());
					String displayName = aRow[1] != null ? aRow[1].toString() : "";
					allDisplay.put(id, displayName);
					logger.debug("Added display - id: {}, name: {}", id, displayName);
				} catch (NumberFormatException e) {
					logger.warn("Invalid ID format in row: {}", aRow[0], e);
				}
			}
		}
		
		logger.debug("Retrieved {} display records", allDisplay.size());
		return allDisplay;
	}

	/**
	 * Update display configuration with column data.
	 * Maps to Hibernate saveOrUpdate logic with transaction management.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param selectedDisplayId the selected display ID
	 * @param filterType the filter type (search definition name/stringParam2)
	 * @param columnsData the column configuration data (CLOB)
	 * @return updated MultiPurposeUses entity or null if not found
	 */
	public MultiPurposeUses updateDisplay(long userId, long selectedDisplayId, String filterType, String columnsData) {
		try {
			Optional<MultiPurposeUses> existingDisplay = multiPurposeUsesRepository.findByUserIdAndDisplayId(userId, selectedDisplayId);
			if (existingDisplay.isPresent()) {
				MultiPurposeUses multiPurposeUses = existingDisplay.get();
				multiPurposeUses.setClobData(columnsData);
				// Save or update the entity
				MultiPurposeUses updated = multiPurposeUsesRepository.save(multiPurposeUses);
				return updated;
			} else {
				logger.warn("Display configuration not found - userId: {}, displayId: {}", userId, selectedDisplayId);
				return null;
			}
		} catch (Exception e) {
			logger.error("Error updating display configuration - userId: {}, displayId: {}", userId, selectedDisplayId, e);
			throw new RuntimeException("Failed to update display configuration: " + e.getMessage(), e);
		}
	}

	/**
	 * Delete display configuration by userId and displayId.
	 * Maps to Hibernate delete logic with transaction management.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param selectedDisplayId the selected display ID to delete
	 * @return map containing deletion result with updated available displays and default display info
	 */
	public void deleteDisplay(long userId, long selectedDisplayId) {
        // Find and delete the display
        Optional<MultiPurposeUses> displayToDelete = multiPurposeUsesRepository.findByUserIdAndDisplayId(userId, selectedDisplayId);

        if (displayToDelete.isPresent()) {
            multiPurposeUsesRepository.delete(displayToDelete.get());
        }
	}

	/**
	 * Find default display for user by userId and filterType.
	 * Returns display marked as default (longParam2 == 1).
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2)
	 * @return Optional containing the default display if found
	 */
	public Optional<MultiPurposeUses> findDefaultDisplay(long userId, String filterType) {
		logger.debug("Finding default display for userId: {}, filterType: {}", userId, filterType);
		return multiPurposeUsesRepository.findDefaultDisplay(userId, filterType);
	}

	/**
	 * Clear default display flag for all displays of a user with specific filterType.
	 * Sets longParam2 to 0 for all records matching userId and filterType.
	 * Maps to Hibernate update query.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2)
	 * @return true if at least one record was updated, false otherwise
	 */
	public boolean updateDefaultDisplay(long userId, String filterType) {
		logger.info("Updating default display flag - userId: {}, filterType: {}", userId, filterType);
		
		try {
			int rowsUpdated = multiPurposeUsesRepository.updateDefaultDisplay(userId, filterType);
			boolean updated = rowsUpdated > 0;
			
			if (updated) {
				logger.debug("Default display flag cleared for {} records - userId: {}, filterType: {}", rowsUpdated, userId, filterType);
			} else {
				logger.debug("No records updated for default display flag - userId: {}, filterType: {}", userId, filterType);
			}
			
			return updated;
		} catch (Exception e) {
			logger.error("Error updating default display flag - userId: {}, filterType: {}", userId, filterType, e);
			throw new RuntimeException("Failed to update default display flag: " + e.getMessage(), e);
		}
	}
	
	public MultiPurposeUses findById(long id) {
		return multiPurposeUsesRepository.findById(id).orElse(null);
	}

	/**
	 * Get available column configuration by userId, filterType, and displayName.
	 * Maps to Hibernate HQL query that selects clobData.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2)
	 * @param displayName the display name (stringParam1)
	 * @return column configuration data (clobData) or empty string if not found
	 */
	public String getAvailableColumn(long userId, String filterType, String displayName) {
		logger.debug("Fetching available columns - userId: {}, filterType: {}, displayName: {}", userId, filterType, displayName);
		
		try {
			Optional<String> columnData = multiPurposeUsesRepository.getAvailableColumn(userId, filterType, displayName);
			
			if (columnData.isPresent()) {
				String result = columnData.get();
				logger.debug("Column data found for displayName: {}, data length: {}", displayName, result != null ? result.length() : 0);
				return result != null ? result : "";
			} else {
				logger.debug("No column data found for userId: {}, filterType: {}, displayName: {}", userId, filterType, displayName);
				return "";
			}
		} catch (Exception e) {
			logger.error("Error fetching available columns - userId: {}, filterType: {}, displayName: {}", userId, filterType, displayName, e);
			throw new RuntimeException("Failed to fetch available columns: " + e.getMessage(), e);
		}
	}

	/**
	 * Check if display already exists by userId, filterType, displayName, and optionally objectType.
	 * Maps to Hibernate Criteria API code with eq and like restrictions.
	 * 
	 * @param userId the user ID (longParam1)
	 * @param filterType the filter type (stringParam2 with LIKE)
	 * @param displayName the display name (stringParam1 with LIKE)
	 * @param objectType the object type (optional, with LIKE)
	 * @return MultiPurposeUses if found, null otherwise
	 */
	public MultiPurposeUses checkDisplayAlreadyExist(long userId, String filterType, String displayName, String objectType) {
		logger.debug("Checking if display exists - userId: {}, filterType: {}, displayName: {}, objectType: {}", userId, filterType, displayName, objectType);
		
		try {
			Optional<MultiPurposeUses> existingDisplay = multiPurposeUsesRepository.checkDisplayAlreadyExist(userId, filterType, displayName, objectType);
			
			if (existingDisplay.isPresent()) {
				MultiPurposeUses display = existingDisplay.get();
				logger.debug("Display found - id: {}, displayName: {}, objectType: {}", display.getId(), display.getStringParam1(), display.getObjectType());
				return display;
			} else {
				logger.debug("Display does not exist - userId: {}, filterType: {}, displayName: {}, objectType: {}", userId, filterType, displayName, objectType);
				return null;
			}
		} catch (Exception e) {
			logger.error("Error checking if display exists - userId: {}, filterType: {}, displayName: {}, objectType: {}", userId, filterType, displayName, objectType, e);
			throw new RuntimeException("Failed to check if display exists: " + e.getMessage(), e);
		}
	}
}
