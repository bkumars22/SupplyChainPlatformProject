/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.responsibility.entity;

import java.io.Serializable;

import com.scplatform.pcm.assignment.entity.Assignment;
import com.scplatform.pcm.site.entity.Site;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name="PCM_REGIONAL_REGION_MAP")
public class RegionalResponsibility implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="REGION_ID_SEQ", sequenceName="REGION_ID_SEQ",allocationSize = 1)
	@GeneratedValue(generator="REGION_ID_SEQ")
	@Column(name="REGION_ID")
	private Long regionId;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ASSIGNMENT_KEY")
	private Assignment assignment;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="SITE_KEY")
	private Site site;

	
	public RegionalResponsibility(){
		super();
	}
	
	public RegionalResponsibility(Site site,Assignment assignment){
		this.site = site;
		this.assignment = assignment;
	}
	
	
	
	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RegionalResponsibility other = (RegionalResponsibility) obj;
		if(this.regionId!=null ){
			if(this.regionId==other.regionId){
				return true;
			}else if(this.site.getSiteKey()==other.site.getSiteKey()){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "RegionalResponsibility [regionId=" + regionId + ", assignment="
				+ assignment.getAssignmentKey() + ", site=" + site.getSiteName() + "]";
	}

}
