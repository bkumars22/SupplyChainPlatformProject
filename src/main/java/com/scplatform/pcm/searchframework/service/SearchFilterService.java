/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.searchframework.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.searchframework.entity.SearchFilter;
import com.scplatform.pcm.searchframework.repository.SearchFilterRepository;
import com.scplatform.pcm.user.entity.Users;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SearchFilterService {

    private static final Log logger = LogFactory.getLog(SearchFilterService.class);
    
    private final SearchFilterRepository searchFilterRepository;


    /**
     * Save a new search filter.
     * 
     * @param filter the search filter to save
     * @return the saved search filter
     */
    @Modifying
    public SearchFilter save(SearchFilter filter) {
        logger.info("Saving new search filter: " + filter.getName());
        return searchFilterRepository.save(filter);
    }

    /**
     * Delete a search filter by ID.
     * 
     * @param searchFilterId the ID of the filter to delete
     */
    public void delete(Long searchFilterId) {
        logger.info("Deleting search filter with ID: " + searchFilterId);
        searchFilterRepository.deleteById(searchFilterId);
    }

    /**
     * Delete a search filter entity.
     * 
     * @param filter the search filter to delete
     */
    public void delete(SearchFilter filter) {
        logger.info("Deleting search filter: " + filter.getName());
        searchFilterRepository.delete(filter);
    }

    /**
     * Find search filters by name, owner, and filter type with optional parameters.
     * Returns filters that are public or owned by the specified user.
     * Null parameters are treated as "match any" (optional filters).
     * 
     * @param name the exact filter name (optional, can be null)
     * @param users the filter creator
     * @return list of matching search filters
     */
    public List<SearchFilter> findUserSearchFilters(String name, Users users, String filterType) {
        return searchFilterRepository.findUserSearchFilters(name, users.getUserKey(), filterType);
    }

    /**
     * Find a single search filter by name, owner with optional parameters.
     * Returns a filter that is public or owned by the specified user.
     * 
     * @param name the exact filter name (optional, can be null)
     * @param users the filter creator
     * @return Optional containing the matching search filter if found
     */
    public Optional<SearchFilter> findUserSearchFilter(String name, Users users, String filterType) {
        return searchFilterRepository.findUserSearchFilters(name, users.getUserKey(),filterType).stream().findFirst();
    }

    /**
     * Get a search filter by its ID.
     * 
     * @param searchFilterId the ID of the filter to retrieve
     * @return Optional containing the search filter if found
     */
    public Optional<SearchFilter> getSearchFilterById(Long searchFilterId) {
        return searchFilterRepository.findById(searchFilterId);
    }

    
}
