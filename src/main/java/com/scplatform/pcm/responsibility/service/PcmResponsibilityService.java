/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.responsibility.service;

import java.util.List;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.scplatform.pcm.responsibility.entity.PcmResponsibility;
import com.scplatform.pcm.responsibility.repository.PcmResponsibilityRepository;
import com.scplatform.pcm.role.entity.Role;

/**
 * Service class for PcmResponsibility entity operations.
 * Provides business logic for responsibility management using Spring Data JPA.
 */

@Service
@RequiredArgsConstructor
@Log4j2
public class PcmResponsibilityService {

	private final PcmResponsibilityRepository pcmResponsibilityRepository;

	/**
	 * Find responsibilities with optional filtering by role, responsibility types, and exclusions.
	 * 
	 * @param role the role to filter by (optional)
	 * @param responsibilityTypes list of responsibility types to include
	 * @param excludeResponsibilities list of responsibility keys to exclude
	 * @return list of PcmResponsibility entities ordered by displayOrder
	 */
	public List<PcmResponsibility> findResponsibilities(Role role, List<String> responsibilityTypes,
			List<String> excludeResponsibilities) {
        return pcmResponsibilityRepository.findResponsibilities(role, responsibilityTypes, excludeResponsibilities);
    }

}

