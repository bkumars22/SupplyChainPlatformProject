/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.businessEntity.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.repository.BusinessEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BusinessEntityService {

	private final BusinessEntityRepository businessEntityRepository;

	/**
	 * Get a business entity by key.
	 */
	public BusinessEntity getBusinessEntity(Long businessEntityKey) {
		return businessEntityRepository.findById(businessEntityKey).orElse(null);
	}

	/**
	 * Get all business entities.
	 */
	public List<BusinessEntity> getAllBusinesses() {
		return businessEntityRepository.findAll();
	}

	/**
	 * Find business entity by natural key (identifier + type key).
	 */
	public BusinessEntity findBusinessByNaturalKey(String identifier, long typeKey) {
		return businessEntityRepository
				.findBusinessByNaturalKey(identifier, typeKey)
				.orElse(null);
	}

	/**
	 * Find business entity by natural key (identifier + type name).
	 */
	public BusinessEntity findBusinessByNaturalKey(String identifier, String typeName) {
		if (identifier == null || typeName == null) {
			return null;
		}
		return businessEntityRepository
				.findBusinessByNaturalKey(identifier, typeName)
				.orElse(null);
	}

	/**
	 * Find business entities by identifier (supports ignore case).
	 */
	public List<BusinessEntity> findBusinessById(String identifier, String typeName, boolean ignoreCase) {
		if (identifier == null) {
			return Collections.emptyList();
		}

		List<BusinessEntity> matches = businessEntityRepository.findBusinessById(identifier, typeName);
		if (ignoreCase) {
			return matches;
		}

		List<BusinessEntity> exactMatches = new ArrayList<>();
		for (BusinessEntity businessEntity : matches) {
			boolean idMatches = identifier.equals(businessEntity.getBusinessEntityIdentifier());
			boolean typeMatches = typeName == null || typeName.equals(businessEntity.getBusinessEntityTypeName());
			if (idMatches && typeMatches) {
				exactMatches.add(businessEntity);
			}
		}
		return exactMatches;
	}

	/**
	 * Find business entity by external reference ID.
	 */
	public BusinessEntity findBusinessByExternalRefId(String externalRefId) {
		if (externalRefId == null) {
			return null;
		}
		return businessEntityRepository.findBusinessByExternalRefId(externalRefId).orElse(null);
	}

	/**
	 * Find business entities by name (supports ignore case).
	 */
	public List<BusinessEntity> findBusinessByName(String name, String typeName, boolean ignoreCase) {
		if (name == null) {
			return Collections.emptyList();
		}

		List<BusinessEntity> matches = businessEntityRepository.findBusinessByName(name, typeName);
		if (ignoreCase) {
			return matches;
		}

		List<BusinessEntity> exactMatches = new ArrayList<>();
		for (BusinessEntity businessEntity : matches) {
			boolean nameMatches = name.equals(businessEntity.getBusinessEntityName());
			boolean typeMatches = typeName == null || typeName.equals(businessEntity.getBusinessEntityTypeName());
			if (nameMatches && typeMatches) {
				exactMatches.add(businessEntity);
			}
		}
		return exactMatches;
	}

	/**
	 * Find unique business entity by name.
	 */
	public BusinessEntity findUniqueBusinessByName(String name, String typeName, boolean ignoreCase) {
		List<BusinessEntity> matches = findBusinessByName(name, typeName, ignoreCase);
		return matches.isEmpty() ? null : matches.get(0);
	}

	/**
	 * Find business entities by alternate name.
	 */
	public List<BusinessEntity> findBusinessByAlternate(String alternateName, String typeName, boolean ignoreCase) {
		if (alternateName == null) {
			return Collections.emptyList();
		}

		if (ignoreCase) {
			return businessEntityRepository.findBusinessByAlternate(alternateName, typeName);
		}

		List<BusinessEntity> matches = businessEntityRepository.findBusinessByAlternate(alternateName, typeName);
		List<BusinessEntity> exactMatches = new ArrayList<>();
		for (BusinessEntity businessEntity : matches) {
			if (businessEntity.getAlternates() == null) {
				continue;
			}
			businessEntity.getAlternates().forEach(alt -> {
				if (alternateName.equals(alt.getBusinessEntityName())) {
					exactMatches.add(businessEntity);
				}
			});
		}
		return exactMatches;
	}

	/**
	 * Find business entity with best match (tries ID first, then name, then alternate).
	 */
	public BusinessEntity findBusinessBestMatch(String idOrName, String typeName, boolean ignoreCase,
			boolean throwErrorOnDuplicate) {
		return findBusinessBestMatch(idOrName, idOrName, typeName, ignoreCase, throwErrorOnDuplicate);
	}

	/**
	 * Find business entity with best match using separate ID and name.
	 */
	public BusinessEntity findBusinessBestMatch(String id, String name, String typeName, boolean ignoreCase,
			boolean throwErrorOnDuplicate) {
		List<BusinessEntity> candidates = Collections.emptyList();
		if (id != null) {
			candidates = findBusinessById(id, typeName, ignoreCase);
		}
		if (candidates.isEmpty() && name != null) {
			candidates = findBusinessByName(name, typeName, ignoreCase);
			if (candidates.isEmpty()) {
				candidates = findBusinessByAlternate(name, typeName, ignoreCase);
			}
		}

		if (candidates.isEmpty()) {
			return null;
		}
		if (candidates.size() == 1) {
			return candidates.get(0);
		}
		if (throwErrorOnDuplicate) {
			throw new IllegalArgumentException("Multiple matches found for business entity (id=" + id + ", name=" + name + ")");
		}
		return candidates.get(0);
	}

	/**
	 * Find business entity by name or type (composite search).
	 */
	public BusinessEntity findBusinessEntityByNameOrType(String businessEntityIdentifier, String typeKey) {
		return findBusinessByNaturalKey(businessEntityIdentifier, Long.parseLong(typeKey));
	}

	/**
	 * Get enterprise business entity by type key.
	 */
	public BusinessEntity getEnterpriseBusinessEntity(long enterpriseTypeKey) {
		return businessEntityRepository.findByBusinessEntityTypeKey(enterpriseTypeKey).orElse(null);
	}

	/**
	 * Get enterprise business entity key.
	 */
	public Long getEnterpriseBusinessEntityKey() {
		BusinessEntity enterprise = getEnterpriseBusinessEntity(BusinessEntity.ENTERPRISE_TYPE);
		return enterprise == null ? null : enterprise.getBusinessEntityKey();
	}

	/**
	 * Check if business entity type exists by name.
	 */
	public boolean doesBusinessEntityTypeExist(String typeName) {
		if (typeName == null) {
			return false;
		}
		return businessEntityRepository.doesBusinessEntityTypeExist(typeName);
	}

	/**
	 * Find businesses by keys, optionally excluding operator type.
	 * Maps to: UMSUtil.findBusinessesByKeys(boolean, List, MultiValueMap)
	 */
	public Map<String, Object> findBusinessesByKeys(boolean includeAll, List<String> keys,
			MultiValueMap<String, String> params) {
		
		// Convert string keys to long keys
		List<Long> longKeys = new ArrayList<>();
		if (keys != null && !keys.isEmpty()) {
			longKeys = keys.stream()
				.map(Long::valueOf)
				.collect(Collectors.toList());
		}

		// Query businesses
		boolean excludeOperator = !includeAll;
		boolean noKeys = keys == null || keys.isEmpty();
		List<BusinessEntity> businesses = businessEntityRepository.findBusinessesByKeys(
			excludeOperator, 
			BusinessEntity.OPERATOR_TYPE, 
			noKeys, 
			longKeys
		);

		// Apply pagination if params are provided
		int offset = 0;
		int limit = businesses.size();
		
		if (params != null) {
			if (params.getFirst("offset") != null) {
				offset = Integer.valueOf(params.getFirst("offset"));
			}
			if (params.getFirst("limit") != null) {
				limit = Integer.valueOf(params.getFirst("limit"));
			}
		}

		// Get count before pagination
		int count = businesses.size();

		// Apply pagination manually
		int endIndex = Math.min(offset + limit, businesses.size());
		if (offset >= businesses.size()) {
			businesses = Collections.emptyList();
		} else {
			businesses = businesses.subList(offset, endIndex);
		}

		Map<String, Object> results = new HashMap<>();
		results.put("count", count);
		results.put("resultList", businesses);

		return results;
	}

	/**
	 * Get display name for a business entity in format: Name(Type).
	 * Migrated from: UMSUtil.getAccessGroupDisplayName(BusinessEntity)
	 * 
	 * @param be the BusinessEntity to get display name for
	 * @return formatted display name or null if BusinessEntity is null
	 */
	public String getAccessGroupDisplayName(BusinessEntity be) {
		if (be != null) {
			return be.getBusinessEntityName() + "(" + be.getBusinessEntityTypeName() + ")";
		}
		return null;
	}

	/**
	 * Find distinct business entity names matching the given type.
	 * Maps to Hibernate Criteria API code with distinct projection.
	 * 
	 * @param companyItemType the business entity name to search for
	 * @return list of distinct business entity names
	 */
	public List<String> findDistinctBusinessEntityNames(String companyItemType) {
		if (companyItemType == null) {
			return Collections.emptyList();
		}
		return businessEntityRepository.findDistinctBusinessEntityNames(companyItemType);
	}

}
