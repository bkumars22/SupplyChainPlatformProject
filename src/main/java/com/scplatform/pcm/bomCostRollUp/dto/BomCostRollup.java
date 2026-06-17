/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 */
package com.scplatform.pcm.bomCostRollUp.dto;

import com.scplatform.pcm.ums.dto.GenericResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Response DTO returned by the BOM cost rollup REST endpoint.
 * <p>
 * This is intentionally a plain transfer object (not a JPA entity): the rollup
 * payload is a dynamic, pivoted list of cost-element columns keyed by name, so
 * it cannot be expressed as a fixed relational table mapping.
 */
@Getter
@Setter
@NoArgsConstructor
public class BomCostRollup implements GenericResponse {

    private List<Map<String, Object>> jsonNodeList = new ArrayList<>();

    public static Map<String, Object> newRow() {
        return new LinkedHashMap<>();
    }
}
