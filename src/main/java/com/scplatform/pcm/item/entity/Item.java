/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.item.entity;

import com.scplatform.pcm.aml.entity.Aml;
import com.scplatform.pcm.aml.entity.AmlId;
import com.scplatform.pcm.assignment.entity.ItemAssignment;
import com.scplatform.pcm.avl.entity.Avl;
import com.scplatform.pcm.bom.entity.BaseBomEntity;
import com.scplatform.pcm.bom.entity.Bom;
import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.*;
import com.scplatform.pcm.contact.entity.Contact;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.platform.entity.Platform;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.NaturalId;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;


@NamedQueries({
    @NamedQuery(
        name = "dashboard:newUnassignedItems",
        query = "SELECT COUNT(i), COALESCE(c.categoryName, 'N/A') FROM Item i " +
                "LEFT OUTER JOIN i.categories c " +
                "WHERE i.assignments IS EMPTY " +
                "AND i.insertDate >= :cutoffDate " +
                "AND (i.isDeleted IS NULL OR i.isDeleted = false) " +
                "GROUP BY c.categoryName ORDER BY 2"
    ),
    @NamedQuery(
        name = "dashboard:forecast",
        query = "SELECT COUNT(*), sl.status FROM PcmSourcingLane sl " +
                "WHERE sl.status IN (:status) " +
                "AND COALESCE(sl.updateDate, sl.insertDate) >= :cutoffDate " +
                "GROUP BY sl.status ORDER BY 2"
    ),
    @NamedQuery(
        name = "dashboard:forecast_ADJ",
        query = "SELECT COUNT(*), sl.status FROM PcmSourcingLane sl " +
                "WHERE sl.status IN (:status) " +
                "AND COALESCE(sl.updateDate, sl.insertDate) >= :cutoffDate " +
                "GROUP BY sl.status ORDER BY 2"
    )
})
@Entity
@Table(name = "ITEM_MASTER")
@SuppressWarnings("serial")
public class Item extends BaseBomEntity implements Serializable, Comparable<Item> {
	public static final String ITEM = "I";
	public static final String MFG_ITEM = "M";
	public static final String SUP_ITEM = "S";
	public static final String PHANTOM_ITEM = "PI";
	public static final String COM_GROUP = "CG";
	public static final String USERDEF_GROUP = "UDG";
	public static final String CFG_GROUP = "CFG";

	public static final String[] ITEM_TYPES = new String[] { ITEM, MFG_ITEM, SUP_ITEM, PHANTOM_ITEM };
	public static final String[] GROUP_TYPES = new String[] { COM_GROUP, USERDEF_GROUP, CFG_GROUP };

	// Fields
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITEM_MASTER_SEQ_GEN")
	@SequenceGenerator(name = "ITEM_MASTER_SEQ_GEN", sequenceName = "ITEM_MASTER_SEQ", allocationSize = 1)
	@Column(name = "ITEM_KEY")
	private Long itemKey;

	@NaturalId(mutable = true)
	@Column(name = "ITEM_IDENTIFIER", nullable = false)
	private String itemNumber;

	@NaturalId(mutable = true)
	@Column(name = "ITEM_UNIQUE_IDENTIFIER")
	private String itemId;

	@NaturalId(mutable = true)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BUSINESS_ENTITY_KEY", nullable = false)
	private BusinessEntity businessEntity;

	@NaturalId(mutable = true)
	@Embedded
	private VersionRevision itemVersion;

	@Column(name = "ITEM_TYPE")
	private String itemType;

	@Column(name = "ITEM_EXTERNAL_ID")
	private String itemExternalId;

	@Column(name = "DATA_SOURCE", nullable = false)
	private String dataSource = "MCM";

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "CONTACT_KEY")
	private Contact contact;

	@Column(name = "GBL_LFCYCL_PH_CODE", length = 50)
	private String lifeCycleTypeCode;

	@Column(name = "GBL_PROD_LFCYCL_CODE_OTHER")
	private String lifeCycleTypeCodeOther;

	@Column(name = "ITEM_CLASSIFICATION")
	private String itemClassification;

	@Column(name = "PRODUCT_FAMILY")
	private String productFamily;

	@Column(name = "MAKE_BUY", length = 50)
	private String makeBuy;

	@Column(name = "MAKE_BUY_OTHER")
	private String makeBuyOther;

	@Column(name = "MANAGED_FLAG")
	private String managedFlag;

	@Column(name = "CERTIFICATION_REQUIRED", length = 1)
	private Boolean certRequired;

	@Column(name = "SERIALNUMBER_REQUIRED", length = 1)
	private Boolean serialNumberRequired;

	@Column(name = "OWNER_NAME")
	private String ownerName;

	@Column(name = "ITEM_DESCRIPTION")
	private String description;

	@Column(name = "IS_TOP_LEVEL", length = 1)
	private Boolean isTopLevel;

	@Column(name = "ITEM_PART_TYPE")
	private String itemPartType;

	@Column(name = "GBL_UOM_CODE")
	private String productUomCode;

	@Column(name = "COLLABORATION", length = 1)
	private Boolean collaboration;

	@Column(name = "EOL", length = 1)
	private Boolean eol;

	@Column(name = "EOL_LAST_CHANGED_ON")
	private Date eolLastChanged;

	@Column(name = "EOL_TYPE")
	private String eolType;

	@Column(name = "INVENTORY", scale = 6)
	private BigDecimal inventory;

	@Column(name = "STRING_ATTRIBUTE1", length = 1024)
	private String stringAttribute1;

	@Column(name = "STRING_ATTRIBUTE2", length = 1024)
	private String stringAttribute2;

	@Column(name = "STRING_ATTRIBUTE3", length = 1024)
	private String stringAttribute3;

	@Column(name = "STRING_ATTRIBUTE4", length = 1024)
	private String stringAttribute4;

	@Column(name = "STRING_ATTRIBUTE5", length = 1024)
	private String stringAttribute5;

	@Column(name = "STRING_ATTRIBUTE6", length = 1024)
	private String stringAttribute6;

	@Column(name = "STRING_ATTRIBUTE7", length = 1024)
	private String stringAttribute7;

	@Column(name = "STRING_ATTRIBUTE8", length = 1024)
	private String stringAttribute8;

	@Column(name = "STRING_ATTRIBUTE9", length = 1024)
	private String stringAttribute9;

	@Column(name = "STRING_ATTRIBUTE10", length = 1024)
	private String stringAttribute10;

	@Column(name = "NUMBER_ATTRIBUTE1")
	private Integer numberAttribute1;

	@Column(name = "NUMBER_ATTRIBUTE2")
	private Integer numberAttribute2;

	@Column(name = "NUMBER_ATTRIBUTE3")
	private Integer numberAttribute3;

	@Column(name = "NUMBER_ATTRIBUTE4")
	private Integer numberAttribute4;

	@Column(name = "NUMBER_ATTRIBUTE5")
	private Integer numberAttribute5;

	@Column(name = "NUMBER_ATTRIBUTE6")
	private Integer numberAttribute6;

	@Column(name = "NUMBER_ATTRIBUTE7")
	private Integer numberAttribute7;

	@Column(name = "NUMBER_ATTRIBUTE8")
	private Integer numberAttribute8;

	@Column(name = "NUMBER_ATTRIBUTE9")
	private Integer numberAttribute9;

	@Column(name = "NUMBER_ATTRIBUTE10")
	private Integer numberAttribute10;

	@Column(name = "FLOAT_ATTRIBUTE1")
	private BigDecimal floatAttribute1;

	@Column(name = "FLOAT_ATTRIBUTE2")
	private BigDecimal floatAttribute2;

	@Column(name = "FLOAT_ATTRIBUTE3")
	private BigDecimal floatAttribute3;

	@Column(name = "FLOAT_ATTRIBUTE4")
	private BigDecimal floatAttribute4;

	@Column(name = "FLOAT_ATTRIBUTE5")
	private BigDecimal floatAttribute5;

	@Column(name = "FLOAT_ATTRIBUTE6")
	private BigDecimal floatAttribute6;

	@Column(name = "FLOAT_ATTRIBUTE7")
	private BigDecimal floatAttribute7;

	@Column(name = "FLOAT_ATTRIBUTE8")
	private BigDecimal floatAttribute8;

	@Column(name = "FLOAT_ATTRIBUTE9")
	private BigDecimal floatAttribute9;

	@Column(name = "FLOAT_ATTRIBUTE10")
	private BigDecimal floatAttribute10;

	@Column(name = "DATE_ATTRIBUTE1")
	private Date dateAttribute1;

	@Column(name = "DATE_ATTRIBUTE2")
	private Date dateAttribute2;

	@Column(name = "DATE_ATTRIBUTE3")
	private Date dateAttribute3;

	@Column(name = "DATE_ATTRIBUTE4")
	private Date dateAttribute4;

	@Column(name = "DATE_ATTRIBUTE5")
	private Date dateAttribute5;

	@Column(name = "DATE_ATTRIBUTE6")
	private Date dateAttribute6;

	@Column(name = "DATE_ATTRIBUTE7")
	private Date dateAttribute7;

	@Column(name = "DATE_ATTRIBUTE8")
	private Date dateAttribute8;

	@Column(name = "DATE_ATTRIBUTE9")
	private Date dateAttribute9;

	@Column(name = "DATE_ATTRIBUTE10")
	private Date dateAttribute10;

	// Relationships
	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Bom> containsBoms = new HashSet<Bom>();

	@OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
	private Set<BomLine> whereUsedSet = new HashSet<BomLine>();

	@ElementCollection
	@CollectionTable(name = "ITEM_ADD_ATTRIBUTE", joinColumns = @JoinColumn(name = "ITEM_KEY"))
	private List<Attribute> attributes = new ArrayList<Attribute>();

	@OneToMany(mappedBy = "amlId.item", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
	private Set<Aml> amls = new HashSet<Aml>();

	@OneToMany(mappedBy = "item", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
	private Set<Avl> avls = new HashSet<Avl>();

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<ItemAssignment> assignments = new TreeSet<ItemAssignment>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "ITEM_ITEM_CATEGORY", joinColumns = @JoinColumn(name = "ITEM_KEY"), inverseJoinColumns = @JoinColumn(name = "ITEM_CATEGORY_KEY"))
	private Set<ItemCategory> categories = new HashSet<ItemCategory>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "ITEM_ITEM_PLATFORM", joinColumns = @JoinColumn(name = "ITEM_KEY"), inverseJoinColumns = @JoinColumn(name = "ITEM_PLATFORM_KEY"))
	private Set<Platform> platforms = new HashSet<Platform>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "ITEM_GROUP_ITEMS", joinColumns = @JoinColumn(name = "ITEM_KEY"), inverseJoinColumns = @JoinColumn(name = "ITEM_GROUP_ITEM_KEY"))
	private Set<Item> groupMembers = new HashSet<Item>();

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "ITEM_FG_MAP", joinColumns = @JoinColumn(name = "ITEM_KEY"), inverseJoinColumns = @JoinColumn(name = "FUNCTIONAL_GROUP_ID"))
	private Set<FunctionalGroup> functionalGroups = new HashSet<FunctionalGroup>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "ITEM_ALTERNATES", joinColumns = @JoinColumn(name = "ITEM_KEY"))
	private final Set<ItemAlternate> alternates = new HashSet<ItemAlternate>();

	/** default constructor */
	public Item() {
		super();
		itemType = ITEM;
		itemVersion = new VersionRevision();
	}

	public Item(Long ItemKey) {
		super();
		this.itemKey = ItemKey;
	}

	public String getItemExternalId() {
		return itemExternalId;
	}

	public void setItemExternalId(String itemExternalId) {
		this.itemExternalId = itemExternalId;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * 
	 */


	/**
	 * 
	 */
	public Long getItemKey() {
		return this.itemKey;
	}

	public void setItemKey(Long itemKey) {
		this.itemKey = itemKey;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getItemId() {
		return this.itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public Contact getContact() {
		return this.contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public BusinessEntity getBusinessEntity() {
		return businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	public String getLifeCycleTypeCode() {
		return this.lifeCycleTypeCode;
	}

	public void setLifeCycleTypeCode(String lifeCycleTypeCode) {
		this.lifeCycleTypeCode = lifeCycleTypeCode;
	}

	public String getLifeCycleTypeCodeOther() {
		return this.lifeCycleTypeCodeOther;
	}

	public void setLifeCycleTypeCodeOther(String lifeCycleTypeCodeOther) {
		this.lifeCycleTypeCodeOther = lifeCycleTypeCodeOther;
	}

	public String getItemClassification() {
		return this.itemClassification;
	}

	public void setItemClassification(String itemClassification) {
		this.itemClassification = itemClassification;
	}

	public String getRevision() {
		return getItemVersion().getRevision();
	}

	public String getVersion() {
		return getItemVersion().getVersion();
	}

	public String getProductFamily() {
		return this.productFamily;
	}

	public void setProductFamily(String productFamily) {
		this.productFamily = productFamily;
	}

	public String getMakeBuy() {
		return this.makeBuy;
	}

	public void setMakeBuy(String makeBuy) {
		this.makeBuy = makeBuy;
	}

	public String getMakeBuyOther() {
		return this.makeBuyOther;
	}

	public void setMakeBuyOther(String makeBuyOther) {
		this.makeBuyOther = makeBuyOther;
	}

	public Boolean getSerialNumberRequired() {
		return this.serialNumberRequired;
	}

	public void setSerialNumberRequired(Boolean serialNumberRequired) {
		this.serialNumberRequired = serialNumberRequired;
	}

	public Boolean getCertRequired() {
		return this.certRequired;
	}

	public void setCertRequired(Boolean certRequired) {
		this.certRequired = certRequired;
	}

	public String getOwnerName() {
		return this.ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getIsTopLevel() {
		return isTopLevel;
	}

	public void setIsTopLevel(Boolean isTopLevel) {
		this.isTopLevel = isTopLevel;
	}

	public Boolean getCollaboration() {
		return collaboration;
	}

	public void setCollaboration(Boolean collaboration) {
		this.collaboration = collaboration;
	}

	public String getItemPartType() {
		return itemPartType;
	}

	public void setItemPartType(String itemPartType) {
		this.itemPartType = itemPartType;
	}

	public String getProductUomCode() {
		return this.productUomCode;
	}

	public void setProductUomCode(String productUomCode) {
		this.productUomCode = productUomCode;
	}

	public String getManagedFlag() {
		return managedFlag;
	}

	public Set<BomLine> getWhereUsedSet() {
		return this.whereUsedSet;
	}

	public void setWhereUsedSet(Set<BomLine> whereUsed) {
		this.whereUsedSet = whereUsed;
	}



	/**
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name) {
		for (Attribute attribute : attributes) {
			if(attribute.getAttrValue() != null && attribute.getAttrType().equals(AttributeType.STRING)){
				return attribute.getAttrStrValue().replaceAll("\\r?\\n","");
			}
			return (Object) attribute.getAttrValue();
		}
		return null;
	}

	public boolean addAttribute(Attribute attr) {

		if (attributes.contains(attr)) {
			return false;
		}
		attributes.add(attr);
		return true;
	}

	public VersionRevision getItemVersion() {
		return itemVersion;
	}

	public void setItemVersion(VersionRevision itemVersion) {
		this.itemVersion = itemVersion;
	}

	public BigDecimal getInventory() {
		return inventory;
	}

	public void setInventory(BigDecimal inventory) {
		this.inventory = inventory;
	}

	public Set<Bom> getContainsBoms() {
		return containsBoms;
	}

	public void setContainsBoms(Set<Bom> containsBoms) {
		this.containsBoms = containsBoms;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Set<Aml> getAmls() {
		return amls;
	}

	public Set<Avl> getAvls() {
		return avls;
	}

	public Set<ItemCategory> getCategories() {
		return categories;
	}

	public void setCategories(Set<ItemCategory> categories) {
		this.categories = categories;
	}

	public Set<Platform> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(Set<Platform> platforms) {
		this.platforms = platforms;
	}

	public Set<Item> getGroupMembers() {
		return groupMembers;
	}

	public void setGroupMembers(Set<Item> groupMembers) {
		this.groupMembers = groupMembers;
	}

	public Set<ItemAssignment> getAssignments() {
		return assignments;
	}

	public Set<FunctionalGroup> getFunctionalGroups() {
		return functionalGroups;
	}

	public void setFunctionalGroups(Set<FunctionalGroup> functionalGroups) {
		this.functionalGroups = functionalGroups;
	}

	public Boolean getEol() {
		return eol;
	}

	public void setEol(Boolean eol) {
		this.eol = eol;
	}

	public Date getEolLastChanged() {
		return eolLastChanged;
	}

	public void setEolLastChanged(Date eolLastChanged) {
		this.eolLastChanged = eolLastChanged;
	}

	public String getEolType() {
		return eolType;
	}

	public void setEolType(String eolType) {
		this.eolType = eolType;
	}



	public void addAml(Aml aml) {
		aml.setItem(this);
		amls.add(aml);
	}

	public Aml getAml(AmlId id) {
		Iterator<Aml> itr = amls.iterator();
		while (itr.hasNext()) {
			Aml aml = itr.next();
			if (aml.getAmlId().equals(id)) {
				return aml;
			}
		}
		return null;
	}

	public void removeAml(Aml aml) {
		if (amls.remove(aml)) {
			aml.setItem(null);
		}
	}



	public void addAvl(Avl avl) {
		avl.setItem(this);
		avls.add(avl);
	}

	public void removeAvl(Avl avl) {
		if (avls.remove(avl)) {
			avl.setItem(null);
		}
	}

	public Avl getAvl(Item item, Item supplierItem) {
		Iterator<Avl> itr = avls.iterator();
		while (itr.hasNext()) {
			Avl avl = itr.next();
			if (avl.getItem().equals(item) && avl.getSupplierItem().equals(supplierItem)) {
				return avl;
			}
		}
		return null;
	}

	public Avl getAvlForSupplierItemKey(Long supItemKey) {
		Iterator<Avl> itr = avls.iterator();
		while (itr.hasNext()) {
			Avl avl = itr.next();
			if (avl.getSupplierItemKey().equals(supItemKey)) {
				return avl;
			}
		}
		return null;
	}

	public Set<BusinessEntity> getSuppliers() {
		Set<BusinessEntity> result = new TreeSet<BusinessEntity>(new BusinessEntity.NameSorter());
		for (Avl avl : avls) {
			if (avl.getSupplier() != null) {
				result.add(avl.getSupplier());
			}
		}
		return Collections.unmodifiableSet(result);
	}

	public Set<BusinessEntity> getManufacturers() {
		HashSet<BusinessEntity> result = new HashSet<>();
		for (Aml aml : amls) {
			result.add(aml.getMfg());
		}
		return Collections.unmodifiableSet(result);
	}



	public void addCategory(ItemCategory category) {
		categories.add(category);
	}



	public void addPlatform(Platform platform) {
		this.platforms.add(platform);
	}

	public void removePlatform(Platform platform) {
		this.platforms.remove(platform);
	}

	public void removeAllPlatforms() {
		this.platforms.clear();
	}



	public void addGroupMember(Item member) {
		this.groupMembers.add(member);
	}

	public void removeGroupMember(Item member) {
		this.groupMembers.remove(member);
	}


	public void setManagedFlag(String managedFlag) {
		this.managedFlag = StringUtils.upperCase(managedFlag);
	}



	public Set<ItemAlternate> getAlternates() {
		return alternates;
	}

	public boolean addAlternateItem(ItemAlternate ia) {
		ia.setItem(this);
		return alternates.add(ia);
	}

	public ItemAlternate getAlternateItem(Item altItem) {
		Iterator<ItemAlternate> itr = alternates.iterator();
		while (itr.hasNext()) {
			ItemAlternate ia = itr.next();
			if (ia.getAlternateItem().equals(altItem)) {
				return ia;
			}
		}
		return null;
	}



	@Override
	public int hashCode() {
		int result = new HashCodeBuilder(17, 37).append(this.getItemNumber()).append(this.getItemType())
				.append(this.getBusinessEntity()).toHashCode();
		return result;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof Item))
			return false;
		Item castOther = (Item) other;
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(this.getItemNumber(), castOther.getItemNumber());
		eb.append(this.getItemId(), castOther.getItemId());
		eb.append(this.getItemType(), castOther.getItemType());
		eb.append(this.getItemVersion(), castOther.getItemVersion());
		eb.append(this.getBusinessEntity(), castOther.getBusinessEntity());
		return eb.isEquals();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.itemKey).append(", ");
		if (this.itemId != null) {
			sb.append(this.itemId);
		}
		sb.append(", ");
		sb.append(this.itemNumber).append(", '");
		if (this.description != null) {
			sb.append(this.description);
		}
		sb.append("'");
		return sb.toString();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Item other) {
		CompareToBuilder ctb = new CompareToBuilder();
		ctb.append(this.getItemNumber(), other.getItemNumber());
		ctb.append(this.getItemId(), other.getItemId());
		ctb.append(this.getItemType(), other.getItemType());
		ctb.append(this.getItemVersion(), other.getItemVersion());
		ctb.append(this.getBusinessEntity(), other.getBusinessEntity());
		return ctb.toComparison();
	}

	@Transient
	public String getEolState() {
		if (this.eol != null && this.eol) {
			if (this.eolType != null && !this.eolType.isEmpty()) {
				return this.eolType;
			} else {
				return "INACTIVE";
			}
		}
		return "ACTIVE";
	}
}