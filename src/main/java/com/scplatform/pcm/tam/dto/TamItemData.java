/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

public class TamItemData {
	
	String itemAllocation;
	
	public String getItemAllocation() {
		return itemAllocation;
	}
	public void setItemAllocation(String itemAllocation) {
		this.itemAllocation = itemAllocation;
	}
	
	@Override
	public String toString() {
		return "TamItemData [itemAllocation=" + itemAllocation + "]";
	}
	
}
