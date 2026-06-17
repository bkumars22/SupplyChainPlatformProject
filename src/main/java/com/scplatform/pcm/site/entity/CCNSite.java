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
@Table(name = "CCN_SITE")
@Getter
@Setter
public class CCNSite implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "siteCcnKeySeqGen")
	@SequenceGenerator(name = "siteCcnKeySeqGen", sequenceName = "SITE_CCN_KEY_SEQ", allocationSize = 1)
	@Column(name = "ID", nullable = false, unique = true)
	private Long id;

	@Column(name = "CCN", nullable = false)
	private String ccn;

	@Column(name = "GLOBAL_REGION")
	private String globalRegion;

	@Column(name = "INTERFACE_REGION")
	private String interfaceRegion;

	@ManyToOne
	@JoinColumn(name = "SITE_KEY", nullable = false)
	private Site site;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ccn == null) ? 0 : ccn.hashCode());
		result = prime * result + ((globalRegion == null) ? 0 : globalRegion.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((interfaceRegion == null) ? 0 : interfaceRegion.hashCode());
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
		CCNSite other = (CCNSite) obj;
		if (ccn == null) {
			if (other.ccn != null)
				return false;
		} else if (!ccn.equals(other.ccn))
			return false;
		if (globalRegion == null) {
			if (other.globalRegion != null)
				return false;
		} else if (!globalRegion.equals(other.globalRegion))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (interfaceRegion == null) {
			if (other.interfaceRegion != null)
				return false;
		} else if (!interfaceRegion.equals(other.interfaceRegion))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		return true;
	}

}
