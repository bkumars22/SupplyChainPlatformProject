/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.supplyAllocation.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.StatefulBase;
import com.scplatform.pcm.common.entity.TrackDelta;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.scplatform.pcm.util.stateMachine.StateMachineReactor;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import static com.scplatform.pcm.util.common.SCPlatformConstant.*;

/**
 * Entity representing a PCM Supplier Allocation.
 * Migrated from legacy {@code AbstractPcmSupplierAllocation}.
 */
@Entity
@Table(name = "PCM_SUPPLIER_ALLOCATION")
public class PcmSupplierAllocation extends StatefulBase implements TrackDelta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PCM_SUPPLIER_ALLOCATION_SEQ")
    @SequenceGenerator(name = "PCM_SUPPLIER_ALLOCATION_SEQ", sequenceName = "PCM_SUPPLIER_ALLOCATION_SEQ", allocationSize = 1)
    @Column(name = "SUPPLIER_ALLOCATION_KEY")
    private Long supplierAllocationKey;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "CUSTOMER_ITEM_GROUP_ITEM_KEY")
    private Item customerItemGroupItem;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "CUSTOMER_ITEM_KEY")
    private Item customerItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "CUSTOMER_SITE_KEY")
    private Site customerSite;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "SUPPLIER_ITEM_KEY")
    private Item supplierItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SUPPLIER_SITE_KEY")
    private Site supplierSite;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "DESTINATION_SITE_KEY")
    private Site destinationSite;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "SUPPLIER_BUSINESS_ENTITY_KEY")
    private BusinessEntity supplierBusinessEntity;

    @Column(name = "ALLOCATION", nullable = true)
    private BigDecimal allocation;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "SUPPLIER_ALLOCATION_COMMENT", nullable = true)
    private String supplyAllocationComment;

    @Column(name = "DATA_SOURCE")
    private String dataSource = "MCM";

    @Column(name = "INSERT_DT")
    private Date insertDate = new Date();

    @Column(name = "UPDATE_DT", nullable = true)
    private Date updateDate;

    @Column(name = "DELETE_FLAG", nullable = true)
    private Boolean deleteFlag = Boolean.FALSE;

    @Column(name = "CURRENT_FLAG")
    private boolean currentFlag = true;

    @Column(name = "EFFECTIVE_FROM_DT", nullable = true)
    private Date effectiveFromDt;

    @Column(name = "EFFECTIVE_TO_DT", nullable = true)
    private Date effectiveToDt;

    @Column(name = "CONTEXT_OBJECT_TYPE")
    private String contextName;

    @Column(name = "CONTEXT_OBJECT_ID")
    private String contextType;

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    public PcmSupplierAllocation() {
        super();
    }

    public PcmSupplierAllocation(Long supplierAllocationKey,
            Item customerItemGroupItem, Item customerItem, Site customerSite,
            Item supplierItem, BusinessEntity supplierBusinessEntity,
            Site supplierSite, Site destinationSite, BigDecimal allocation,
            String description, String status, Date insertDate, boolean currentFlag) {
        super();
        this.supplierAllocationKey = supplierAllocationKey;
        this.customerItemGroupItem = customerItemGroupItem;
        this.customerItem = customerItem;
        this.customerSite = customerSite;
        this.supplierItem = supplierItem;
        this.supplierBusinessEntity = supplierBusinessEntity;
        this.supplierSite = supplierSite;
        this.destinationSite = destinationSite;
        this.allocation = allocation;
        this.description = description;
        this.status = status;
        this.insertDate = insertDate;
        this.currentFlag = currentFlag;
    }

    public PcmSupplierAllocation(Long supplierAllocationKey,
            Item customerItemGroupItem, Item customerItem, Site customerSite,
            Item supplierItem, BusinessEntity supplierBusinessEntity,
            Site supplierSite, Site destinationSite, BigDecimal allocation,
            String description, String status, Date statusChangeDate,
            String statusLastChangeBy, Date insertDate, Date updateDate,
            Date effectiveFromDt, Date effectiveToDt, Boolean deleteFlag,
            boolean currentFlag, Long auditRev, Date lastRevChangeDate,
            String dataSource) {
        super();
        this.supplierAllocationKey = supplierAllocationKey;
        this.customerItemGroupItem = customerItemGroupItem;
        this.customerItem = customerItem;
        this.customerSite = customerSite;
        this.supplierItem = supplierItem;
        this.supplierBusinessEntity = supplierBusinessEntity;
        this.supplierSite = supplierSite;
        this.destinationSite = destinationSite;
        this.allocation = allocation;
        this.description = description;
        this.status = status;
        this.statusChangeDate = statusChangeDate;
        this.statusLastChangeBy = statusLastChangeBy;
        this.insertDate = insertDate;
        this.updateDate = updateDate;
        this.effectiveFromDt = effectiveFromDt;
        this.effectiveToDt = effectiveToDt;
        this.deleteFlag = deleteFlag;
        this.currentFlag = currentFlag;
        this.auditRev = auditRev;
        this.lastRevChangeDate = lastRevChangeDate;
        this.dataSource = dataSource;
    }

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

    public Long getSupplierAllocationKey() { return supplierAllocationKey; }
    public void setSupplierAllocationKey(Long supplierAllocationKey) { this.supplierAllocationKey = supplierAllocationKey; }

    public Item getCustomerItemGroupItem() { return customerItemGroupItem; }
    public void setCustomerItemGroupItem(Item customerItemGroupItem) { this.customerItemGroupItem = customerItemGroupItem; }

    public Item getCustomerItem() { return customerItem; }
    public void setCustomerItem(Item customerItem) { this.customerItem = customerItem; }

    public Site getCustomerSite() { return customerSite; }
    public void setCustomerSite(Site customerSite) { this.customerSite = customerSite; }

    public Item getSupplierItem() { return supplierItem; }
    public void setSupplierItem(Item supplierItem) { this.supplierItem = supplierItem; }

    public BusinessEntity getSupplierBusinessEntity() { return supplierBusinessEntity; }
    public void setSupplierBusinessEntity(BusinessEntity supplierBusinessEntity) { this.supplierBusinessEntity = supplierBusinessEntity; }

    public Site getSupplierSite() { return supplierSite; }
    public void setSupplierSite(Site supplierSite) { this.supplierSite = supplierSite; }

    public Site getDestinationSite() { return destinationSite; }
    public void setDestinationSite(Site destinationSite) { this.destinationSite = destinationSite; }

    public BigDecimal getAllocation() { return allocation; }
    public void setAllocation(BigDecimal allocation) { this.allocation = allocation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSupplyAllocationComment() { return supplyAllocationComment; }
    public void setSupplyAllocationComment(String supplyAllocationComment) { this.supplyAllocationComment = supplyAllocationComment; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    @Override public Date getInsertDate() { return insertDate; }
    @Override public void setInsertDate(Date insertDate) { this.insertDate = insertDate; }

    @Override public Date getUpdateDate() { return updateDate; }
    @Override public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }

    public Date getEffectiveFromDt() { return effectiveFromDt; }
    public void setEffectiveFromDt(Date effectiveFromDt) { this.effectiveFromDt = effectiveFromDt; }

    public Date getEffectiveToDt() { return effectiveToDt; }
    public void setEffectiveToDt(Date effectiveToDt) { this.effectiveToDt = effectiveToDt; }

    @Override public Boolean getDeleteFlag() { return deleteFlag; }
    @Override public void setDeleteFlag(Boolean deleteFlag) { this.deleteFlag = deleteFlag; }

    @Override public boolean getCurrentFlag() { return currentFlag; }
    @Override public void setCurrentFlag(boolean currentFlag) { this.currentFlag = currentFlag; }

    public String getContextName() { return contextName; }
    public void setContextName(String contextName) { this.contextName = contextName; }

    public String getContextType() { return contextType; }
    public void setContextType(String contextType) { this.contextType = contextType; }

    // ------------------------------------------------------------------
    // StateMachineReactor
    // ------------------------------------------------------------------

    @Override
    public Collection getChildren() { return null; }

    @Override
    public StateMachineReactor getParent() { return null; }

    // ------------------------------------------------------------------
    // equals / hashCode
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof PcmSupplierAllocation)) return false;
        PcmSupplierAllocation castOther = (PcmSupplierAllocation) other;
        return new EqualsBuilder()
            .append(getCustomerItemGroupItem(), castOther.getCustomerItemGroupItem())
            .append(getCustomerItem(), castOther.getCustomerItem())
            .append(getCustomerSite(), castOther.getCustomerSite())
            .append(getSupplierItem(), castOther.getSupplierItem())
            .append(getSupplierBusinessEntity(), castOther.getSupplierBusinessEntity())
            .append(getSupplierSite(), castOther.getSupplierSite())
            .append(getDestinationSite(), castOther.getDestinationSite())
            .append(getEffectiveFromDt(), castOther.getEffectiveFromDt())
            .append(getEffectiveToDt(), castOther.getEffectiveToDt())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(getCustomerItemGroupItem())
            .append(getCustomerItem())
            .append(getSupplierBusinessEntity())
            .toHashCode();
    }

    // ------------------------------------------------------------------
    // toString / audit titles / pseudo key
    // ------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder("SupplierAllocation:");
        if (getCustomerItemGroupItem() != null) {
            msg.append(getCustomerItemGroupItem().getItemNumber())
               .append("/").append(getCustomerItemGroupItem().getBusinessEntity().getBusinessEntityName())
               .append(";");
        }
        if (getCustomerItem() != null) {
            msg.append(getCustomerItem().getItemNumber())
               .append("/").append(getCustomerItem().getBusinessEntity().getBusinessEntityName())
               .append(";");
        }
        if (getSupplierItem() != null) {
            msg.append(getSupplierItem().getItemNumber())
               .append("/").append(getCustomerItem().getBusinessEntity().getBusinessEntityName())
               .append("/");
        }
        if (getSupplierBusinessEntity() != null) {
            msg.append(getSupplierBusinessEntity().getBusinessEntityName()).append(";");
        }
        if (getAllocation() != null) {
            msg.append(getAllocation());
        }
        return msg.toString();
    }

    @Transient
    public String getAuditWithoutAllocationTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        List<Object> args = new ArrayList<>();
        args.add(getCustomerItem() != null ? getCustomerItem().getItemNumber() : "");
        args.add(getCustomerItem() != null ? getCustomerItem().getBusinessEntity().getBusinessEntityName() : "");
        args.add(getSupplierItem() != null ? getSupplierItem().getItemNumber() : "");
        args.add(getSupplierItem() != null ? getSupplierItem().getBusinessEntity().getBusinessEntityName() : "");
        args.add(sdf.format(getEffectiveFromDt()));
        args.add(getEffectiveToDt() == null ? "" : sdf.format(getEffectiveToDt()));
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.supplierAllocationWithoutAllocation", args.toArray(), null);
    }

    @Transient
    public String getAuditWithAllocationTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_FORMAT);
        List<Object> args = new ArrayList<>();
        args.add(getCustomerItem() != null ? getCustomerItem().getItemNumber() : "");
        args.add(getCustomerItem() != null ? getCustomerItem().getBusinessEntity().getBusinessEntityName() : "");
        args.add(getSupplierItem() != null ? getSupplierItem().getItemNumber() : "");
        args.add(getSupplierItem() != null ? getSupplierItem().getBusinessEntity().getBusinessEntityName() : "");
        args.add(sdf.format(getEffectiveFromDt()));
        args.add(getEffectiveToDt() != null ? sdf.format(getEffectiveToDt()) : "");
        args.add(getAllocation() != null ? getAllocation() : "");
        return SCPlatformMessages.INSTANCE.getAuditMessage("audit.supplierAllocationWithAllocation", args.toArray(), null);
    }

    @Transient
    public String getPseudoKey() {
        StringBuilder key = new StringBuilder();
        if (getSupplierBusinessEntity() != null) {
            key.append(StringUtils.replaceChars(getSupplierBusinessEntity().getBusinessEntityName(), " .()[]", "______"));
        } else {
            key.append("NA");
        }
        key.append("^");
        if (getSupplierItem() != null) {
            key.append(StringUtils.replaceChars(getSupplierItem().getItemNumber(), ".()[]", "_____"));
        } else {
            key.append("NA");
        }
        key.append("|");
        if (getSupplierItem() != null) {
            key.append(getSupplierItem().getItemKey());
        } else {
            key.append("NA");
        }
        key.append("^");
        if (getCustomerItem() != null) {
            key.append(getCustomerItem().getItemKey());
        } else {
            key.append("NA");
        }
        key.append("^");
        if (getCustomerItemGroupItem() != null) {
            key.append(getCustomerItemGroupItem().getItemKey());
        } else {
            key.append("NA");
        }
        if (getSupplierSite() != null) {
            key.append(getSupplierSite().getSiteKey());
        } else {
            key.append("NA");
        }
        return key.toString();
    }

    public String toTestString() {
        return toString() + '(' + this.effectiveFromDt + '-' + this.effectiveToDt + ')' + ':' + contextName + "/" + contextType;
    }
}
