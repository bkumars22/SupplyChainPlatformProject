/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.bom.entity;

import com.scplatform.pcm.aml.entity.Aml;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.common.entity.VersionRevision;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.*;

/**
 * 
 * The BOM represents the topmost part a particular bill of material.
 * 
 * Each BOM will contain a set of BomLine items which in turn may contain BOMs.
 * 
 * This allows for an n tier bom for any item.
 * 
 * 
 * 
 * The BOM also supports attributes. Attributes are collections of user defined
 * fields
 * 
 * that are attached and managed by each BOM instance.
 * 
 */
@NamedQueries({
    @NamedQuery(
        name = "dashboard:bom",
        query = "SELECT COUNT(*), b.status FROM Bom b " +
                "WHERE b.status IN (:status) " +
                "AND COALESCE(b.updateDate, b.insertDate) >= :cutoffDate " +
                "GROUP BY b.status ORDER BY 2"
    ),
    @NamedQuery(
        name = "dashboard:bomForOwner",
        query = "SELECT COUNT(*), b.status FROM Bom b " +
                "JOIN b.item im " +
                "WHERE b.status IN (:status) " +
                "AND COALESCE(b.updateDate, b.insertDate) >= :cutoffDate " +
                "AND EXISTS (SELECT 1 FROM im.assignments ia WHERE LOWER(ia.userId) = LOWER(:userId)) " +
                "GROUP BY b.status ORDER BY 2"
    )
})
@Entity
@Table(name = "BOM_HEADER")
@Getter
@Setter
@SuppressWarnings("serial")
public class Bom extends BaseBomEntity implements Serializable, StateMachineReactor {
	protected final static Logger logger = LogManager.getLogger(Bom.class);

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOM_HEADER_SEQ_GEN")
	@SequenceGenerator(name = "BOM_HEADER_SEQ_GEN", sequenceName = "BOM_HEADER_SEQ", allocationSize = 1)
	@Column(name = "BOM_KEY", nullable = false, unique = true)
	private Long bomKey;

	@Column(name = "BOM_EXTERNAL_ID", nullable = true)
	private String bomExternalId;

	@Column(name = "BOM_NAME", nullable = true)
	private String bomName;

	@Column(name = "BOM_DESCRIPTION", length = 512, nullable = true)
	private String bomDesc;

	@Column(name = "DATA_SOURCE", nullable = false)
	private String dataSource;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY", nullable = true)
	private BusinessEntity businessEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ITEM_KEY", nullable = false)
	private Item item;

	@Column(name = "IS_TOP_LEVEL", nullable = true)
	private Boolean isTopLevel;

	@Column(name = "OWNER", length = 255, nullable = true)
	private String owner;

	@Column(name = "OWNER_TYPE", length = 255, nullable = true)
	private String ownerType;

	@Column(name = "BOM_TYPE_CODE", length = 255, nullable = true)
	private String typeCode;

	@Column(name = "BOM_TYPE_CODE_OTHER", length = 255, nullable = true)
	private String typeCodeOther;

	@Embedded
	private VersionRevision bomVersion = new VersionRevision();

	@Column(name = "STATUS", length = 40, nullable = false)
	protected String status;

	@Column(name = "STATUS_CHANGE_DATE", nullable = true)
	protected Date statusChangeDate;

	@Column(name = "STATUS_LAST_CHANGE_BY", length = 64, nullable = true)
	protected String statusLastChangeBy;

	@Column(name = "IS_REPAIRS", nullable = true)
	private Boolean isRepairs;

	@Column(name = "LEAD_TIME", nullable = true)
	private Float leadTime;

	@Column(name = "LOAD_SOURCE", nullable = true)
	private String loadSource;

	@OneToMany(mappedBy = "bom", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("SEQUENCE_IDENTIFIER")
	private Set<BomLine> bomLines = new HashSet<BomLine>();

	@OneToMany(mappedBy = "bom", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private Set<Aml> amls = new HashSet<Aml>();

	@OneToMany(mappedBy = "bom", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BomGroupLink> bomGroupLinks = new HashSet<BomGroupLink>();

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(
		name = "BOM_SITE_LINK",
		joinColumns = @JoinColumn(name = "BOM_KEY"),
		inverseJoinColumns = @JoinColumn(name = "SITE_KEY")
	)
	private Set<Site> sites = new HashSet<Site>();

	@Transient
	private Set<Attribute> attributes = new HashSet<Attribute>();

	public Bom() {
		super();
		status = "NEW";
	}

	public Bom(Long bomKey) {
		super();
		this.bomKey = bomKey;
	}

	public void addBomLine(BomLine bomLine) {
		bomLine.setBom(this);
		bomLines.add(bomLine);
	}

	public boolean removeLine(BomLine line) {
		boolean removed = false;
		if (!StringUtils.isBlank(line.getGroupId())) {
			removed = bomLines.remove(line);
		} else {
			if (bomLines != null) {
				Iterator<BomLine> itBomLine = bomLines.iterator();
				while (itBomLine.hasNext()) {
					BomLine cline = itBomLine.next();
					if (StringUtils.isBlank(cline.getGroupId()) && cline.getBom() == line.getBom()
							&& cline.getItem() == line.getItem()) {
						itBomLine.remove();
						removed = true;
						// Bom equality check should be redundent
					}
					// TODO Multiple matches should be an error
				}
			}
		}
		line.setBom(null);
		return removed;
	}

	public Set<BomLine> getSortedBomLines() {
		Set<BomLine> sortedBomLines = new TreeSet<BomLine>(bomLines);
		return sortedBomLines;
	}

	public Set<BomLine> getSortedBomLines(Comparator<BomLine> comparator) {
		Set<BomLine> sortedBomLines = new TreeSet<BomLine>(comparator);
		sortedBomLines.addAll(bomLines);
		return sortedBomLines;
	}

	public void addGroup(BomGroup group) {
		BomGroupLink link = new BomGroupLink(this, group);
		group.setBusinessEntity(this.getBusinessEntity());
		bomGroupLinks.add(link);
	}

	public void removeLink(BomGroupLink link) {
		if (bomGroupLinks.remove(link)) {
			link.getBomGroup().getBomGroupLinks().remove(link);
		}
	}

	/**
	 * 
	 * Add an attribute to this BOM
	 * 
	 * @param attr
	 *            attribute to add
	 * 
	 */
	public void addAttribute(Attribute attr) {
		attributes.add(attr);
	}

	public void removeAttribute(Attribute attr) {
		attributes.remove(attr);
	}

	public boolean addBomAml(Aml bomAvl) {
		bomAvl.setBom(this);
		return amls.add(bomAvl);
	}

	public Set<Aml> getAmlsForBom() {
		return this.getAmlsForItem(this.getItem());
	}

	public Set<Aml> getAmlsForItem(Item item) {
		Set<Aml> result = new LinkedHashSet<Aml>();
		Iterator<Aml> itr = getAmls().iterator();
		while (itr.hasNext()) {
			Aml avl = itr.next();
			if (avl.getItem().equals(item)) {
				result.add(avl);
			}
		}
		return result;
	}

	public void addSite(Site site) {
		if (site != null) {
			sites.add(site);
		}
	}

	@Override
	public void setState(String state) {
		this.status = state;
	}

	@Override
	public String getState() {
		return this.status;
	}

	@Override
	public Collection<StateMachineReactor> getChildren() {
		Collection<StateMachineReactor> results = new ArrayList<StateMachineReactor>();
		for (BomLine line : bomLines) {
			if (line.getSubBom() != null) {
				results.add(line.getSubBom());
			}
		}

		return results;
	}

	@Override
	public StateMachineReactor getParent() {
		return null;
	}

	public void copyDetailsTo(Bom target) {
		target.bomName = this.bomName;
		target.bomDesc = this.bomDesc;
		target.businessEntity = this.businessEntity;
		target.item = this.item;
		target.isTopLevel = this.isTopLevel;
		target.isRepairs = this.isRepairs;
		target.owner = this.owner;
		target.ownerType = this.ownerType;
		target.typeCode = this.typeCode;
		target.typeCodeOther = this.typeCodeOther;
		target.bomVersion.setRevision(this.bomVersion.getRevision());
		target.bomVersion.setRevisionDate(this.bomVersion.getRevisionDate());
		target.bomVersion.setVersion(this.bomVersion.getVersion());
		target.bomVersion.setVersionDate(this.bomVersion.getVersionDate());
		target.attributes.addAll(attributes);
		target.loadSource = this.loadSource;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Bom))
			return false;
		Bom castOther = (Bom) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getBusinessEntity(), castOther.getBusinessEntity());
		eb.append(this.getItem(), castOther.getItem());
		eb.append(this.getBomVersion(), castOther.getBomVersion());
		eb.append(this.getEffectiveFrom(), castOther.getEffectiveFrom());
		eb.append(this.getEffectiveTo(), castOther.getEffectiveTo());
		eb.append(this.getIsRepairs(), castOther.getIsRepairs());
		return eb.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hc = new HashCodeBuilder(17, 37);
		hc.append(this.getBusinessEntity());
		hc.append(this.getItem());
		return hc.toHashCode();
	}

	@Override
	public String toString() {
		return "Bom{" +
				"bomKey=" + bomKey +
				", bomName='" + bomName + '\'' +
				", status='" + status + '\'' +
				'}';
	}
}
