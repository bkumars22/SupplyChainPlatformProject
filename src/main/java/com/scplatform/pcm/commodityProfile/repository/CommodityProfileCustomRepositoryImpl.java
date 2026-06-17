/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.commodityProfile.repository;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

/**
 * Implementation of CommodityProfileCustomRepository for dynamic query execution.
 * Uses EntityManager to execute custom JPQL and native SQL queries.
 */
@Repository
public class CommodityProfileCustomRepositoryImpl implements CommodityProfileCustomRepository {

	private static final Logger logger = LoggerFactory.getLogger(CommodityProfileCustomRepositoryImpl.class);

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * Execute a native SQL query and return a list of results.
	 */
	@Override
	public List<Object[]> executeNativeSqlQuery(String sql, Map<String, Object> parameters) {
		logger.debug("Executing Native SQL Query: {}", sql);
		
		Query query = entityManager.createNativeQuery(sql);
		setParameters(query, parameters);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		
		logger.debug("Query returned {} results", results.size());
		return results;
	}

	/**
	 * Execute a native SQL query and return a list of results as Objects.
	 */
	@Override
	public List<Object> executeNativeSqlQueryAsObject(String sql, Map<String, Object> parameters) {
		logger.debug("Executing Native SQL Query (as Object): {}", sql);
		
		Query query = entityManager.createNativeQuery(sql);
		setParameters(query, parameters);
		
		@SuppressWarnings("unchecked")
		List<Object> results = query.getResultList();
		
		logger.debug("Query returned {} results", results.size());
		return results;
	}

	/**
	 * Helper method to set parameters on a query.
	 * 
	 * @param query the query to set parameters on
	 * @param parameters map of named parameters, or null if none
	 */
	private void setParameters(Query query, Map<String, Object> parameters) {
		if (parameters != null && !parameters.isEmpty()) {
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				query.setParameter(entry.getKey(), entry.getValue());
				logger.debug("Setting parameter - {}: {}", entry.getKey(), entry.getValue());
			}
		}
	}

}
