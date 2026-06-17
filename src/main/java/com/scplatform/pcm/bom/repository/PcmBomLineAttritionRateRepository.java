/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.bom.entity.PcmBomLineAttritionRate;
import com.scplatform.pcm.bom.entity.PcmBomLineAttritionRateId;
import com.scplatform.pcm.item.entity.Item;

/**
 * Spring Data JPA Repository for PcmBomLineAttritionRate entity.
 * Provides data access operations for attrition rate entities (composite ID).
 */
@Repository
public interface PcmBomLineAttritionRateRepository extends JpaRepository<PcmBomLineAttritionRate, PcmBomLineAttritionRateId> {

	List<PcmBomLineAttritionRate> findByIdBomLineAndIdBomItem(BomLine bomLine, Item bomItem);
	

}
