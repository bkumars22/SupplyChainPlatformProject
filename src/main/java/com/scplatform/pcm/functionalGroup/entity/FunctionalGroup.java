/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.functionalGroup.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.parentFunctionalGroup.entity.ParentFunctionalGroup;
import com.scplatform.pcm.platform.entity.Platform;
import com.scplatform.pcm.tam.entity.TAMAllocation;
import com.scplatform.pcm.xlob.entity.XLOBAllocation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Getter;
import lombok.Setter;


@SuppressWarnings("serial")
@Entity
@Table(name = "FUNCTIONAL_GROUP")
@Getter
@Setter
public class FunctionalGroup implements Serializable {

	public static final String CFG = "CFG";
	public static final String EM = "EM";
	public static final String NFG = "NFG";
	public static final String XLOB = "XLOB";

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "functionalGroupIdSeqGen")
	@SequenceGenerator(name = "functionalGroupIdSeqGen", sequenceName = "FUNCTIONAL_GROUP_ID_SEQ", allocationSize = 1)
	@Column(name = "FUNCTIONAL_GROUP_ID", nullable = false)
	private Long functionalGroupId;

	@Column(name = "FUNCTIONAL_GROUP_EXTERNAL_ID", unique = true)
	private String functionalGroupExternalId;

	@Column(name = "NAME", nullable = false, unique = true)
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "STATUS")
	private String status;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "ITEM_PLATFORM_KEY", unique = true)
	private Platform platform;

	@Column(name = "FG_PLATFORM")
	private String fgPlatform;

	@Column(name = "CREATED_ON")
	private Date createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "STATUS_CHANGED_BY")
	private String statusChangedBy;

	@Column(name = "LAST_CHANGED_ON")
	private Date lastChangedOn;

	@Column(name = "LAST_CHANGED_BY")
	private String lastChangedBy;

	@Column(name = "EXTRACT_FLAG")
	private String extractFlag;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "ITEM_FG_MAP",
		joinColumns = @JoinColumn(name = "FUNCTIONAL_GROUP_ID", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "ITEM_KEY", nullable = false)
	)
	private Set<Item> functionalGroupItems;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(
		name = "FG_PFG_MAP",
		joinColumns = @JoinColumn(name = "FUNCTIONAL_GROUP_ID", nullable = false),
		inverseJoinColumns = @JoinColumn(name = "PARENT_FUNCTIONAL_GROUP_ID", nullable = false)
	)
	private Set<ParentFunctionalGroup> parentFunctionalGroup;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "FUNCTIONAL_GROUP_ID")
	private Set<TAMAllocation> allocations;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "FUNCTIONAL_GROUP_ID")
	private Set<XLOBAllocation> xlobAllocations;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "functionalGroup")
	private FunctionalGroupLob fgLob;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ITEM_KEY")
	private Item parentItem;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "ODM_PART_KEY")

	private Item ODMPart;
	
	@Column(name = "ROLLOVER_COUNT")
	private Long rollOverCount;

	@Column(name = "ALIAS_NAME")
	private String aliasName;

	public FunctionalGroup() {
		super();
	}

	public ObjectNode getFgNaturalKeyAsJSON() {
        ObjectMapper om = new ObjectMapper();
        ObjectNode o = om.createObjectNode();
        o.put("functionalGroups",this.name);
        ArrayNode pfgs = o.putArray("parentFunctionalGroups");
        getFgAsJson(pfgs);
        return o;
    }

	private void getFgAsJson(ArrayNode pfgs) {
		for (ParentFunctionalGroup pfg : getParentFunctionalGroup()) {
			pfgs.add(pfg.getPfgNaturalKeyAsJSON());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((functionalGroupId == null) ? 0 : functionalGroupId.hashCode());
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
		FunctionalGroup other = (FunctionalGroup) obj;
		if (functionalGroupId == null) {
			if (other.functionalGroupId != null)
				return false;
		} else if (!functionalGroupId.equals(other.functionalGroupId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "FunctionalGroup [name=" + name +", description=" + description + ", type=" + type + ", status="
				+ status + ", platform=" + platform + "]";
	}

	public static final String ONE_TO_MANY = "OneToMany";
	public static final String MANY_TO_MANY = "ManyToMany";
}
