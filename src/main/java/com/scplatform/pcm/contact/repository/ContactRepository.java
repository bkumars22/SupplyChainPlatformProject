/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.contact.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.contact.entity.Contact;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;

/**
 * Spring Data JPA Repository for Contact entity.
 * Provides data access operations for Contact entities.
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

	List<Contact> findByStatus(String status);
}
