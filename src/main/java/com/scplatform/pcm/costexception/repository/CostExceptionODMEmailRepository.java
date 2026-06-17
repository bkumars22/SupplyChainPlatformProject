/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.costexception.repository;

import com.scplatform.pcm.costexception.entity.CostExceptionODMEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CostExceptionODMEmailRepository extends JpaRepository<CostExceptionODMEmail, Long> {
}
