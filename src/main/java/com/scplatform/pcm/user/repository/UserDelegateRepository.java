/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.user.repository;

import com.scplatform.pcm.user.entity.UserDelegate;
import com.scplatform.pcm.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDelegateRepository extends JpaRepository<UserDelegate, Long> {

    /**
     * Find all user delegates for a given delegator user.
     * 
     * @param delegator the Users entity whose delegates to find
     * @return list of UserDelegate records where delegator matches the provided user
     */
    List<UserDelegate> findByDelegator(Users delegator);

    /**
     * Find all user delegates for a given delegator user by user key.
     * 
     * @param delegatorKey the user key of the delegator
     * @return list of UserDelegate records where delegator user key matches
     */
    @Query("SELECT ud FROM UserDelegate ud WHERE ud.delegator.userKey = :delegatorKey")
    List<UserDelegate> findByDelegatorUserKey(@Param("delegatorKey") Long delegatorKey);

    /**
     * Find all user delegates for a given delegate user ID.
     * 
     * @param delegateUserId the delegate user ID to search for
     * @return list of UserDelegate records where delegateUserId matches
     */
    List<UserDelegate> findByDelegateUserId(String delegateUserId);

    /**
     * Find user delegates by responsibility.
     * 
     * @param responsibility the responsibility code
     * @return list of UserDelegate records with matching responsibility
     */
    List<UserDelegate> findByResponsibility(String responsibility);
}
