/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.dto;

import com.scplatform.pcm.ums.dto.GenericResponse;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class ExceptionWraper implements GenericResponse {
	private JsonNode costException;
	private JsonNode approvalDetails;
}
