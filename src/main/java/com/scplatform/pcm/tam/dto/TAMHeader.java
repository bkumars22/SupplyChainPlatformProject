/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TAMHeader {
	private Date startDate;
	private Date endDate;
	private Boolean isInherited;
	private Boolean isSupplierDataInherited;
	private Boolean isItemDataInherited;

	public TAMHeader(Date startDate, Date endDate, Boolean isInherited) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.isInherited = isInherited;
	}

	public TAMHeader(Date startDate, Date endDate, Boolean isInherited, Boolean isSupplierDataInherited,
                     Boolean isItemDataInherited) {
		super();
		this.startDate = startDate;
		this.endDate = endDate;
		this.isInherited = isInherited;
		this.isSupplierDataInherited = isSupplierDataInherited;
		this.isItemDataInherited = isItemDataInherited;
	}
}
