/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.site.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("serial")
@Entity
@Table(name = "SITE_FACILITIES")
@Getter
@Setter
public class SiteFacility implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "siteFacilitiesKeySeqGen")
	@SequenceGenerator(name = "siteFacilitiesKeySeqGen", sequenceName = "SITE_FACILITIES_KEY_SEQ", allocationSize = 1)
	@Column(name = "ID", nullable = false, unique = true)
	private Long id;

	@Column(name = "FACILITY", nullable = false)
	private String facility;

	@Column(name = "DEFAULT_FACILITY_FLAG", length = 1)
	private Boolean defaultFacilityFlag;

	@ManyToOne
	@JoinColumn(name = "SITE_KEY", nullable = false)
	private Site site;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((defaultFacilityFlag == null) ? 0 : defaultFacilityFlag.hashCode());
		result = prime * result + ((facility == null) ? 0 : facility.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
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
		SiteFacility other = (SiteFacility) obj;
		if (defaultFacilityFlag == null) {
			if (other.defaultFacilityFlag != null)
				return false;
		} else if (!defaultFacilityFlag.equals(other.defaultFacilityFlag))
			return false;
		if (facility == null) {
			if (other.facility != null)
				return false;
		} else if (!facility.equals(other.facility))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		return true;
	}

}
