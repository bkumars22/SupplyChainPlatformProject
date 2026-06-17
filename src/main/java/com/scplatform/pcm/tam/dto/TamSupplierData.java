/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.tam.dto;

import java.util.HashMap;
import java.util.Map;


public class TamSupplierData {

	String supplierAllocation;
	Map<String, TamItemData> tamItemDatas = new HashMap<String, TamItemData>();

	public String getSupplierAllocation() {
		return supplierAllocation;
	}

	public void setSupplierAllocation(String supplierAllocation) {
		this.supplierAllocation = supplierAllocation;
	}

	public TamItemData getItemData(String key) {
		TamItemData tid = this.tamItemDatas.get(key);
		if (tid == null) {
			tid = new TamItemData();
			this.tamItemDatas.put(key, tid);
		}
		return tid;
	}

	public Map<String, TamItemData> getTamItemDatas() {
		return this.tamItemDatas;
	}

	@Override
	public String toString() {
		return "TamSupplierData [supplierAllocation=" + supplierAllocation
				+ ", tamItemData=" + tamItemDatas + "]";
	}
}
