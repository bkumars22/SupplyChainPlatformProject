/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.searchframework.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.searchframework.entity.SearchFilter;
import com.scplatform.pcm.user.entity.Users;

@Repository
public interface SearchFilterRepository extends JpaRepository<SearchFilter, Long> {

    /**
     * Find a search filter by name, owner, and filter type.
     * Returns only filters that are public or owned by the specified user.
     * 
     * @param name the filter name
     * @param owner the filter creator
     * @param filterType the filter type
     * @return Optional containing the search filter if found
     */
    @Query("SELECT sf FROM SearchFilter sf WHERE sf.name = :name " +
           "AND sf.filterType = :filterType " +
           "AND (sf.isPublic = true OR sf.creator = :owner)")
    Optional<SearchFilter> findByNameAndOwnerAndType(
            @Param("name") String name,
            @Param("owner") Users owner,
            @Param("filterType") String filterType);

    /**
     * Find all search filters by name, owner, and filter type.
     * Returns all filters that are public or owned by the specified user.
     * 
     * @param name the filter name (can be partial)
     * @param owner the filter creator
     * @param filterType the filter type
     * @return list of matching search filters
     */
    @Query("SELECT sf FROM SearchFilter sf WHERE " +
           "(UPPER(sf.name) LIKE UPPER(CONCAT('%', :name, '%')) OR :name IS NULL) " +
           "AND sf.filterType = :filterType " +
           "AND (sf.isPublic = true OR sf.creator = :owner) " +
           "ORDER BY sf.name ASC")
    List<SearchFilter> findAllByNameAndOwnerAndType(
            @Param("name") String name,
            @Param("owner") Users owner,
            @Param("filterType") String filterType);

    /**
     * Find all public search filters by filter type.
     * 
     * @param filterType the filter type
     * @return list of public search filters
     */
    @Query("SELECT sf FROM SearchFilter sf WHERE sf.filterType = :filterType AND sf.isPublic = true " +
           "ORDER BY sf.name ASC")
    List<SearchFilter> findPublicFiltersByType(@Param("filterType") String filterType);

    /**
     * Find all search filters created by a specific user.
     * 
     * @param creator the user who created the filters
     * @return list of filters created by the user
     */
    List<SearchFilter> findByCreator(Users creator);

    /**
     * Find search filters by name and filter type (case-insensitive).
     * 
     * @param name partial or full filter name
     * @param filterType the filter type
     * @return list of matching search filters
     */
    @Query("SELECT sf FROM SearchFilter sf WHERE UPPER(sf.name) LIKE UPPER(CONCAT('%', :name, '%')) " +
           "AND sf.filterType = :filterType ORDER BY sf.name ASC")
    List<SearchFilter> findByNameContainingIgnoreCaseAndFilterType(
            @Param("name") String name,
            @Param("filterType") String filterType);

    /**
     * Find search filters by name, owner, and filter type with optional parameters.
     * Returns filters that are public or owned by the specified user.
     * Null parameters are treated as "match any" (optional filters).
     * 
     * @param name the exact filter name (optional, can be null)
     * @param ownerKey the filter creator
     * @param filterType the filter type (optional, can be null)
     * @return list of matching search filters
     */
    @Query("SELECT sf FROM SearchFilter sf WHERE " +
           "(:name IS NULL OR sf.name = :name) " +
           "AND (:filterType IS NULL OR sf.filterType = :filterType) " +
           "AND (sf.isPublic = true OR sf.creator.userKey = :userKey)")
    List<SearchFilter> findUserSearchFilters(
            @Param("name") String name,
            @Param("userKey") Long ownerKey,
            @Param("filterType") String filterType);

}
