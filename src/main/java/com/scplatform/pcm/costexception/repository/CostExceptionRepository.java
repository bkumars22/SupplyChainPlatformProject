/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.costexception.repository;

import com.scplatform.pcm.costexception.entity.CostException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CostExceptionRepository extends JpaRepository<CostException, Long> {

    Optional<CostException> findByExceptionId(String exceptionId);

    Optional<CostException> findByExceptionNameIgnoreCase(String exceptionName);
}
