/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import com.scplatform.pcm.ums.dto.GenericResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({   
    "header"    
})
public class HeaderWrap implements GenericResponse {
	
	@JsonProperty("header")
	private Header header;

	@JsonProperty("header")
	public Header getHeader() {
		return header;
	}

	@JsonProperty("header")
	public void setHeader(Header header) {
		this.header = header;
	}
	
}
