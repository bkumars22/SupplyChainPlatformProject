/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.dto;

import com.scplatform.pcm.ums.dto.GenericResponse;

import lombok.Data;

import java.util.Map;

/**
 * Wrapper DTO for Item response.
 * Used to wrap JSON responses for API consistency.
 * Implements GenericResponse interface for framework integration.
 */
@Data
public class ItemWrap implements GenericResponse {
    private Map<String, Object> item;
}
