/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.parentFunctionalGroup.entity;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "PARENT_FUNCTIONAL_GROUP")
@SuppressWarnings("serial")
@Getter
@Setter
public class ParentFunctionalGroup implements Serializable {

	@Id
	@SequenceGenerator(name = "PARENT_FUNCTIONAL_GROUP_ID_SEQ", sequenceName = "PARENT_FUNCTIONAL_GROUP_ID_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "PARENT_FUNCTIONAL_GROUP_ID_SEQ")
	@Column(name = "PARENT_FUNCTIONAL_GROUP_ID", nullable = false, unique = true)
	private Long parentFunctionalGroupId;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "TYPE", nullable = false)
	private String type;

	@Column(name = "PURPOSE", nullable = false)
	private String purpose;

	@Column(name = "LAST_CHANGED_ON", nullable = false)
	private LocalDateTime lastChangedOn;

	@Column(name = "LAST_CHANGED_BY", nullable = false)
	private String lastChangedBy;

	@Column(name = "CREATED_ON", nullable = false)
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(
		name = "FG_PFG_MAP",
		joinColumns = @JoinColumn(name = "PARENT_FUNCTIONAL_GROUP_ID", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "FUNCTIONAL_GROUP_ID", nullable = false)
	)
	Set<FunctionalGroup> functionalGroups;

	public ParentFunctionalGroup() {
		super();
	}

	public ObjectNode getPfgNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("parentName",this.name);
        return o;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParentFunctionalGroup other = (ParentFunctionalGroup) obj;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (functionalGroups == null) {
			if (other.functionalGroups != null)
				return false;
		} else if (!functionalGroups.equals(other.functionalGroups))
			return false;
		if (lastChangedBy == null) {
			if (other.lastChangedBy != null)
				return false;
		} else if (!lastChangedBy.equals(other.lastChangedBy))
			return false;
		if (lastChangedOn == null) {
			if (other.lastChangedOn != null)
				return false;
		} else if (!lastChangedOn.equals(other.lastChangedOn))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parentFunctionalGroupId == null) {
			if (other.parentFunctionalGroupId != null)
				return false;
		} else if (!parentFunctionalGroupId
				.equals(other.parentFunctionalGroupId))
			return false;
		if (purpose == null) {
			if (other.purpose != null)
				return false;
		} else if (!purpose.equals(other.purpose))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ParentFunctionalGroup [parentFunctionalGroupId="
				+ parentFunctionalGroupId + ", name=" + name + ", description="
				+ description + ", type=" + type + ", purpose=" + purpose
				+ ", lastChangedOn=" + lastChangedOn + ", lastChangedBy="
				+ lastChangedBy + ", createdOn=" + createdOn + ", createdBy="
				+ createdBy + ", functionalGroups=" + functionalGroups + "]";
	}
	
	public static final String PURPOSE_MASS_UPDATE = "MASSUPDATE";
	public static final String PURPOSE_AGGREGATION = "AGGREGATION";
	public static final String PURPOSE_ALL = "ALL";
	public static final String PURPOSE_NONE = "";
	
}
