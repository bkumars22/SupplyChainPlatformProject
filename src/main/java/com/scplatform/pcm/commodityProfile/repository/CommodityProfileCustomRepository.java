/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.commodityProfile.repository;

import java.util.List;
import java.util.Map;

/**
 * Custom repository interface for CommodityProfile dynamic query execution.
 * Allows executing custom JPQL and native SQL queries dynamically.
 */
public interface CommodityProfileCustomRepository {
	/**
	 * Execute a native SQL query and return a list of results as Object arrays.
	 * 
	 * @param sql the native SQL query string
	 * @param parameters map of named parameters (key-value pairs), can be null
	 * @return list of results as Object arrays
	 */
	List<Object[]> executeNativeSqlQuery(String sql, Map<String, Object> parameters);

	/**
	 * Execute a native SQL query and return a list of results as Objects.
	 * 
	 * @param sql the native SQL query string
	 * @param parameters map of named parameters (key-value pairs), can be null
	 * @return list of results as objects
	 */
	List<Object> executeNativeSqlQueryAsObject(String sql, Map<String, Object> parameters);

}
