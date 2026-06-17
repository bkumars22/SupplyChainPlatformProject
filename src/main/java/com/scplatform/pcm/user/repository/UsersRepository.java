/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.user.repository;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repository for Users entity.
 * 
 * <p>Provides CRUD operations and custom queries for PCM_USER table.
 * 
 * @author PCM Team
 */
@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {


    @Query("SELECT u FROM Users u WHERE LOWER(u.userId) = LOWER(:userId)")
    List<Users> findAllByUserIdIgnoreCase(@Param("userId") String userId);

    /**
     * Find a single user by their userId (login ID).
     * Case-insensitive search.
     * 
     * @param userId the user's login ID
     * @return Optional containing the user if found
     */
    @Query("SELECT u FROM Users u WHERE LOWER(u.userId) = LOWER(:userId)")
    Optional<Users> findByUserId(@Param("userId") String userId);


    @Query("SELECT u FROM Users u WHERE u.userId = :userId")
    Users findUserByUserId(@Param("userId") String userId);

       @Query("SELECT u FROM Users u WHERE u.userId = :userId AND u.isEnabled = true")
       List<Users> findActiveUserByUserIdInternal(@Param("userId") String userId);

       default Users findActiveUserByUserId(String userId) {
              if (userId == null) {
                     return null;
              }
              List<Users> results = findActiveUserByUserIdInternal(userId);
              return results.isEmpty() ? null : results.get(0);
       }

       @Query("SELECT DISTINCT u FROM Users u LEFT JOIN FETCH u.userProfileMapping WHERE LOWER(u.userId) = LOWER(:userId)")
       List<Users> findCommodityUserByUserIdInternal(@Param("userId") String userId);

       default Users findCommodityUserByUserId(String userId) {
              if (userId == null) {
                     return null;
              }
              List<Users> results = findCommodityUserByUserIdInternal(userId);
              return results.isEmpty() ? null : results.get(0);
       }


    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.lastAccessDate = CURRENT_TIMESTAMP WHERE u.userKey = :userKey")
    int updateUserLastAccessDate(@Param("userKey") Long userKey);

    // ... existing query methods below ...
    Optional<Users> findByEmailAddress(String emailAddress);

    /**
     * Find all enabled users.
     * 
     * @return list of enabled users
     */
    List<Users> findByIsEnabledTrue();

    /**
     * Find users by business entity key.
     *
     * @param businessEntityKey the business entity key
     * @return list of users belonging to the business entity
     */
    @Query("SELECT u FROM Users u WHERE u.businessEntity.businessEntityKey = :businessEntityKey")
    List<Users> findByBusinessEntityKey(@Param("businessEntityKey") Long businessEntityKey);

    /**
     * Find enabled users by business entity.
     * 
     * @param businessEntityKey the business entity key
     * @return list of enabled users for the business entity
     */
    @Query("SELECT u FROM Users u WHERE u.businessEntity.businessEntityKey = :businessEntityKey AND u.isEnabled = true")
    List<Users> findEnabledUsersByBusinessEntity(@Param("businessEntityKey") Long businessEntityKey);

    /**
     * Search users by userId or userName.
     * 
     * @param searchTerm the search term
     * @return list of matching users
     */
    @Query("SELECT u FROM Users u WHERE LOWER(u.userId) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Users> searchUsers(@Param("searchTerm") String searchTerm);

    /**
     * Find all users by userId (may return multiple if duplicates exist).
     *
     * @param userId the user's login ID
     * @return list of matching users
     */
    List<Users> findAllByUserId(String userId);

    /**
     * Update user's last access date.
     *
     * @param userKey the user's primary key
     * @param lastAccessDate the timestamp to set
     */
    @Modifying
    @Query("UPDATE Users u SET u.lastAccessDate = :lastAccessDate WHERE u.userKey = :userKey")
    void updateLastAccessDate(@Param("userKey") Long userKey, @Param("lastAccessDate") Timestamp lastAccessDate);

    /**
     * Update user's last access date to the current timestamp.
     *
     * @param userKey the user's primary key
     * @return number of rows updated
     */
    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.lastAccessDate = CURRENT_TIMESTAMP WHERE u.id = :userKey")
    int updateUserLastAccess(@Param("userKey") Long userKey);

    Users findByUserKey(Long userKey);

    /**
     * Find users by userId (case-insensitive).
     * @param userId the user's login ID
     * @return list of users matching the userId
     */
    List<Users> findByUserIdIgnoreCase(String userId);

    /**
     * Find users by email address (case-insensitive).
     * @param email the email address
     * @return list of users matching the email address
     */
    List<Users> findByContact_EMail(String email);

    /**
     * Find users by userName (case-insensitive).
     * @param roleKey the user's name
     * @return list of users matching the userName
     */
    List<Users> findByRole_RoleKey(Long roleKey);

    /**
     * Find user by userKey with eager loading of relationships.
     * Equivalent to legacy Hibernate method that initializes contact, businessEntity, and preferences.
     *
     * @param userKey the user's primary key
     * @return the user with initialized relationships, or null if not found
     */
    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.contact LEFT JOIN FETCH u.businessEntity LEFT JOIN FETCH u.preferences WHERE u.userKey = :userKey")
    Users findUserByKey(@Param("userKey") Long userKey);

    /**
     * Find user by userId (case-insensitive) with eager loading of relationships.
     * Equivalent to legacy Hibernate method that initializes contact, businessEntity, and preferences.
     * Returns null if userId is null.
     *
     * @param userId the user's login ID
     * @param initAll flag to initialize relationships (when true, contact, businessEntity, and preferences are eager loaded)
     * @return the user with initialized relationships if found and initAll is true, null otherwise
     */
    @Query("SELECT u FROM Users u LEFT JOIN FETCH u.contact LEFT JOIN FETCH u.businessEntity LEFT JOIN FETCH u.preferences WHERE LOWER(u.userId) = LOWER(:userId)")
    Users findUserByUserIdWithRelationships(@Param("userId") String userId);

    /**
     * Find users by role.
     * Equivalent to legacy Hibernate method that filters users by their role.
     *
     * @param role the Role entity
     * @return list of users with the specified role
     */
    @Query("SELECT u FROM Users u WHERE u.role = :role")
    List<Users> getUsersWithRole(@Param("role") Role role);

    /**
     * Find users by businessEntityKey and search text.
     * Searches in email address, user name, and user ID (case-insensitive).
     *
     * @param businessEntityKey the business entity key
     * @param searchText the search text (searches in email, userName, or userId)
     * @return list of users matching the criteria
     */
    @Query("SELECT u FROM Users u WHERE u.businessEntity.businessEntityKey = :businessEntityKey " +
           "AND (LOWER(u.emailAddress) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(u.userId) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Users> findUsersForBusinessByKey(
            @Param("businessEntityKey") Long businessEntityKey,
            @Param("searchText") String searchText);

    /**
     * Find users by businessEntityKey and search text with pagination.
     * Searches in email address, user name, and user ID (case-insensitive).
     *
     * @param businessEntityKey the business entity key
     * @param searchText the search text (searches in email, userName, or userId)
     * @param pageable the pagination parameters
     * @return page of users matching the criteria
     */
    @Query("SELECT u FROM Users u WHERE u.businessEntity.businessEntityKey = :businessEntityKey " +
           "AND (LOWER(u.emailAddress) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(u.userId) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Users> findUsersForBusinessByKeyPaginated(
            @Param("businessEntityKey") Long businessEntityKey,
            @Param("searchText") String searchText,
            Pageable pageable);

    /**
     * Find users by businessEntityKey, enabled status, and search text.
     * Searches in email address, user name, and user ID (case-insensitive).
     *
     * @param businessEntityKey the business entity key
     * @param isEnabled the enabled status
     * @param searchText the search text (searches in email, userName, or userId)
     * @return list of users matching all criteria
     */
    @Query("SELECT u FROM Users u WHERE u.businessEntity.businessEntityKey = :businessEntityKey AND u.isEnabled = :isEnabled " +
           "AND (LOWER(u.emailAddress) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(u.userId) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Users> findUsersByBusinessEntityKeyAndEnabledAndSearchText(
            @Param("businessEntityKey") Long businessEntityKey,
            @Param("isEnabled") boolean isEnabled,
            @Param("searchText") String searchText);

    /**
     * Find users by businessEntityKey, enabled status, and search text with pagination.
     * Searches in email address, user name, and user ID (case-insensitive).
     *
     * @param businessEntityKey the business entity key
     * @param isEnabled the enabled status
     * @param searchText the search text (searches in email, userName, or userId)
     * @param pageable the pagination parameters
     * @return page of users matching all criteria
     */
    @Query("SELECT u FROM Users u WHERE u.businessEntity.businessEntityKey = :businessEntityKey AND u.isEnabled = :isEnabled " +
           "AND (LOWER(u.emailAddress) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "OR LOWER(u.userId) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    Page<Users> findUsersByBusinessEntityKeyAndEnabledAndSearchTextPaginated(
            @Param("businessEntityKey") Long businessEntityKey,
            @Param("isEnabled") boolean isEnabled,
            @Param("searchText") String searchText,
            Pageable pageable);
}
