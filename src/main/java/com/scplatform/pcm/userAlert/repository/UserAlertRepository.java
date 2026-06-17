/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.userAlert.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.userAlert.entity.UserAlert;


@Repository
public interface UserAlertRepository extends JpaRepository<UserAlert, Long> {

    List<UserAlert> findAllByOrderByAlertDateDesc();

    List<UserAlert> findByAlertFilter(String alertFilter);

    @Query("SELECT ua FROM UserAlert ua WHERE ua.alertFilter IS NULL "
            + "OR ua.alertFilter IN :filters ORDER BY ua.alertDate DESC")
    List<UserAlert> findByAlertFilters(@Param("filters") List<String> filters);
}

