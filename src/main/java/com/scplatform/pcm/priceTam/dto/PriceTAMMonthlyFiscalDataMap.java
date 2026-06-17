/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.priceTam.dto;

import java.math.BigDecimal;
import java.util.Date;

public class PriceTAMMonthlyFiscalDataMap {

	Date FISCALSTARTDATE;
	Date FISCALENDDATE;
	Long FUNCTIONALGROUPID;
	Long ITEMKEY;
	Long SUPPLIERKEY;
	Long TOSITEKEY;
	String TOSITEDESCRIPTION;
	Long SOURCINGLANEKEY;
	Long FROMSITEKEY;
	String COSTTYPENAME;
	String COSTTYPEKEY;
	String MPN;
	Double COSTVALUE;
	Long XLOBID;
	Long SITEKEY;
	String SITEDESCRIPTION;
	Double ALLOCATION;

	public PriceTAMMonthlyFiscalDataMap() {

	}

	public PriceTAMMonthlyFiscalDataMap(Date fiscalStartDate, Date fiscalEndDate, Long functionalGroupID, Long itemKey,
                                        Long supplierKey, Long toSiteKey, String toSiteDescription, Long sourcingLaneKey, Long fromSiteKey,
                                        String costTypeName, String costTypeKey, String mpn, Double costValue, Long xlobId, Long siteKey, String siteDescription,
                                        Double allocation) {
		super();
		this.FISCALSTARTDATE = fiscalStartDate;
		this.FISCALENDDATE = fiscalEndDate;
		this.FUNCTIONALGROUPID = functionalGroupID;
		this.ITEMKEY = itemKey;
		this.SUPPLIERKEY = supplierKey;
		this.TOSITEKEY = toSiteKey;
		this.TOSITEDESCRIPTION = toSiteDescription;
		this.SOURCINGLANEKEY = sourcingLaneKey;
		this.FROMSITEKEY = fromSiteKey;
		this.COSTTYPENAME = costTypeName;
		this.COSTTYPEKEY = costTypeKey;
		this.MPN = mpn;
		this.COSTVALUE = costValue;
		this.XLOBID = xlobId;
		this.SITEKEY = siteKey;
		this.SITEDESCRIPTION = siteDescription;
		this.ALLOCATION = allocation;
	}

	public Date getFiscalStartDate() {
		return FISCALSTARTDATE;
	}

	public void setFISCALSTARTDATE(Date fiscalStartDate) {
		this.FISCALSTARTDATE = fiscalStartDate;
	}

	public Date getFiscalEndDate() {
		return FISCALENDDATE;
	}

	public void setFISCALENDDATE(Date fiscalEndDate) {
		this.FISCALENDDATE = fiscalEndDate;
	}

	public Long getFunctionalGroupID() {
		return FUNCTIONALGROUPID;
	}

	public void setFUNCTIONALGROUPID(Long functionalGroupID) {
		this.FUNCTIONALGROUPID = functionalGroupID;
	}

	public Long getItemKey() {
		return ITEMKEY;
	}

	public void setITEMKEY(Long itemKey) {
		this.ITEMKEY = itemKey;
	}

	public Long getSupplierKey() {
		return SUPPLIERKEY;
	}

	public void setSUPPLIERKEY(Long supplierKey) {
		this.SUPPLIERKEY = supplierKey;
	}

	public Long getToSiteKey() {
		return TOSITEKEY;
	}

	public void setTOSITEKEY(Long toSiteKey) {
		this.TOSITEKEY = toSiteKey;
	}

	public String getToSiteDescription() {
		return TOSITEDESCRIPTION;
	}

	public void setTOSITEDESCRIPTION(String toSiteDescription) {
		this.TOSITEDESCRIPTION = toSiteDescription;
	}

	public Long getSourcingLaneKey() {
		return SOURCINGLANEKEY;
	}

	public void setSOURCINGLANEKEY(Long sourcingLaneKey) {
		this.SOURCINGLANEKEY = sourcingLaneKey;
	}

	public Long getFromSiteKey() {
		return FROMSITEKEY;
	}

	public void setFROMSITEKEY(Long fromSiteKey) {
		this.FROMSITEKEY = fromSiteKey;
	}

	public String getCostTypeName() {
		return COSTTYPENAME;
	}

	public void setCOSTTYPENAME(String costTypeName) {
		this.COSTTYPENAME = costTypeName;
	}
	
	public String getCostTypeKey() {
		return COSTTYPEKEY;
	}

	public void setCOSTTYPEKEY(String costTypeKey) {
		this.COSTTYPEKEY = costTypeKey;
	}

	public String getMpn() {
		return MPN;
	}

	public void setMPN(String mpn) {
		this.MPN = mpn;
	}

	public Double getCostValue() {
		return COSTVALUE;
	}

	public void setCOSTVALUE(Double costValue) {
		this.COSTVALUE = costValue;
	}

	public Long getXlobId() {
		return XLOBID;
	}

	public void setXLOBID(Long xlobId) {
		this.XLOBID = xlobId;
	}

	public Long getSiteKey() {
		return SITEKEY;
	}

	public void setSITEKEY(BigDecimal siteKey) {
		this.SITEKEY = siteKey == null ? null : siteKey.longValue();
	}

	public String getSiteDescription() {
		return SITEDESCRIPTION;
	}

	public void setSITEDESCRIPTION(String siteDescription) {
		this.SITEDESCRIPTION = siteDescription;
	}

	public Double getAllocation() {
		return ALLOCATION;
	}

	public void setALLOCATION(Double allocation) {
		this.ALLOCATION = allocation == null ? null : allocation.doubleValue();
	}
}