/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "SITE_DETAILS")
@Getter
@Setter
public class SiteDetails implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "siteDetailsKeySeqGen")
	@SequenceGenerator(name = "siteDetailsKeySeqGen", sequenceName = "SITE_DETAILS_KEY_SEQ", allocationSize = 1)
	@Column(name = "ID", nullable = false, unique = true)
	private Long id;

	@Column(name = "MRP_SITE")
	private String mrpSite;

	@Column(name = "INTERFACE_SITE")
	private String intefaceSite;

	@Column(name = "SITE_OWNER")
	private String siteOwner;

	@Column(name = "EOL_LAST_UPDATE_ON")
	private Timestamp eolLastUpdateOn;

	@Column(name = "COST_NEGOTIATION_FLAG", length = 1)
	private Boolean costNegotiationFlag;

	@Column(name = "DEMAND_FORECAST_FLAG", length = 1)
	private Boolean demandForCastFlag;

	@Column(name = "TAM_VISIBLE_FLAG", length = 1)
	private Boolean tamVisibleFlag;

	@Column(name = "TAM_PROCESSING_FLAG", length = 1)
	private Boolean tamProcessingFlag;

	@Column(name = "COST_VISIBLE_FLAG", length = 1)
	private Boolean costVisibleFlag;

	@Column(name = "COST_UPDATE_FLAG", length = 1)
	private Boolean costUpdateFlag;

	@Column(name = "TAM_UPDATE_FLAG", length = 1)
	private Boolean tamUpdateFlag;

	@Column(name = "SITE_STATE", length = 1)
	private Boolean siteState;

	@Column(name = "ODM_SUPPLIER")
	private Boolean isODMFlag;

	@Column(name = "DISCP_SITE_DESCRIPTION")
	private String discpSiteDescription;

	@Column(name = "MRP_SITE_LEGACY")
	private String mrpSiteLegacy;

	@Column(name = "SITE_PURPOSE")
	private String sitePurpose;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((costNegotiationFlag == null) ? 0 : costNegotiationFlag.hashCode());
		result = prime * result + ((demandForCastFlag == null) ? 0 : demandForCastFlag.hashCode());
		result = prime * result + ((eolLastUpdateOn == null) ? 0 : eolLastUpdateOn.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((intefaceSite == null) ? 0 : intefaceSite.hashCode());
		result = prime * result + ((isODMFlag == null) ? 0 : isODMFlag.hashCode());
		result = prime * result + ((mrpSite == null) ? 0 : mrpSite.hashCode());
		result = prime * result + ((siteOwner == null) ? 0 : siteOwner.hashCode());
		result = prime * result + ((siteState == null) ? 0 : siteState.hashCode());
		result = prime * result + ((tamProcessingFlag == null) ? 0 : tamProcessingFlag.hashCode());
		result = prime * result + ((tamVisibleFlag == null) ? 0 : tamVisibleFlag.hashCode());
		result = prime * result + ((costVisibleFlag == null) ? 0 : costVisibleFlag.hashCode());
		result = prime * result + ((costUpdateFlag == null) ? 0 : costUpdateFlag.hashCode());
		result = prime * result + ((tamUpdateFlag == null) ? 0 : tamUpdateFlag.hashCode());
		result = prime * result + ((discpSiteDescription == null) ? 0 : discpSiteDescription.hashCode());
		result = prime * result + ((mrpSiteLegacy  == null) ? 0 : mrpSiteLegacy.hashCode());
		result = prime * result + ((sitePurpose == null) ? 0 : sitePurpose.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SiteDetails other = (SiteDetails) obj;
		if (costNegotiationFlag == null) {
			if (other.costNegotiationFlag != null)
				return false;
		} else if (!costNegotiationFlag.equals(other.costNegotiationFlag))
			return false;
		if (demandForCastFlag == null) {
			if (other.demandForCastFlag != null)
				return false;
		} else if (!demandForCastFlag.equals(other.demandForCastFlag))
			return false;
		if (eolLastUpdateOn == null) {
			if (other.eolLastUpdateOn != null)
				return false;
		} else if (!eolLastUpdateOn.equals(other.eolLastUpdateOn))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (intefaceSite == null) {
			if (other.intefaceSite != null)
				return false;
		} else if (!intefaceSite.equals(other.intefaceSite))
			return false;
		if (isODMFlag == null) {
			if (other.isODMFlag != null)
				return false;
		} else if (!isODMFlag.equals(other.isODMFlag))
			return false;
		if (mrpSite == null) {
			if (other.mrpSite != null)
				return false;
		} else if (!mrpSite.equals(other.mrpSite))
			return false;
		if (siteOwner == null) {
			if (other.siteOwner != null)
				return false;
		} else if (!siteOwner.equals(other.siteOwner))
			return false;
		if (siteState == null) {
			if (other.siteState != null)
				return false;
		} else if (!siteState.equals(other.siteState))
			return false;
		if (tamProcessingFlag == null) {
			if (other.tamProcessingFlag != null)
				return false;
		} else if (!tamProcessingFlag.equals(other.tamProcessingFlag))
			return false;
		if (tamVisibleFlag == null) {
			if (other.tamVisibleFlag != null)
				return false;
		} else if (!tamVisibleFlag.equals(other.tamVisibleFlag))
			return false;
		if (costVisibleFlag == null) {
			if (other.costVisibleFlag != null)
                return false;
        } else if (!costVisibleFlag.equals(other.costVisibleFlag))
            return false;
		if (costUpdateFlag == null) {
			if (other.costUpdateFlag != null)
                return false;
        } else if (!costUpdateFlag.equals(other.costUpdateFlag))
            return false;
		if (tamUpdateFlag == null) {
			if (other.tamUpdateFlag != null)
                return false;
        } else if (!tamUpdateFlag.equals(other.tamUpdateFlag))
            return false;
		if (discpSiteDescription == null) {
			if (other.discpSiteDescription != null)
                return false;
        } else if (!discpSiteDescription.equals(other.discpSiteDescription))
            return false;
		if (mrpSiteLegacy  == null) {
			if (other.mrpSiteLegacy  != null)
                return false;
        } else if (!mrpSiteLegacy .equals(other.mrpSiteLegacy ))
            return false;
		if (sitePurpose == null) {
			if (other.sitePurpose != null)
                return false;
        } else if (!sitePurpose.equals(other.sitePurpose))
            return false;	
		
		return true;
	}

}
