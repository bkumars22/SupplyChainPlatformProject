/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 */
package com.scplatform.pcm.bomCostRollUp.repository;

import java.util.Date;

import com.scplatform.pcm.bom.entity.Bom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BomCostRollupProcedureRepository extends JpaRepository<Bom, Long> {

    @Procedure(procedureName = "GET_BOM_HIERARCHY_WITH_COST", refCursor = true)
    Object runBomHierarchyWithCost(@Param("p_root_bom_key")   Long pRootBomKey,
                                   @Param("p_user_key")       Long pUserKey,
                                   @Param("p_effective_date") Date pEffectiveDate);
}

