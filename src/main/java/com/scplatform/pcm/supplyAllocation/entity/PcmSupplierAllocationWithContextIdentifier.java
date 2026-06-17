/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.supplyAllocation.entity;

import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.site.entity.Site;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity mapped to IV_SUPPLIER_ALLOCATION_CONTEXT view.
 * Standalone entity (not extending PcmSupplierAllocation) because the view
 * does not have all columns from the PCM_SUPPLIER_ALLOCATION table.
 */
@Entity
@Table(name = "IV_SUPPLIER_ALLOCATION_CONTEXT")
public class PcmSupplierAllocationWithContextIdentifier {

    @Id
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
    @JoinColumn(name = "SUPPLIER_BUSINESS_ENTITY_KEY")
    private BusinessEntity supplierBusinessEntity;

    @Column(name = "ALLOCATION")
    private BigDecimal allocation;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "SUPPLIER_ALLOCATION_COMMENT")
    private String supplyAllocationComment;

    @Column(name = "DATA_SOURCE")
    private String dataSource;

    @Column(name = "INSERT_DT")
    private Date insertDate;

    @Column(name = "UPDATE_DT")
    private Date updateDate;

    @Column(name = "DELETE_FLAG")
    private Boolean deleteFlag;

    @Column(name = "CURRENT_FLAG")
    private boolean currentFlag;

    @Column(name = "EFFECTIVE_FROM_DT")
    private Date effectiveFromDt;

    @Column(name = "EFFECTIVE_TO_DT")
    private Date effectiveToDt;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "STATUS_CHANGE_DATE")
    private Date statusChangeDate;

    @Column(name = "STATUS_LAST_CHANGE_BY")
    private String statusLastChangeBy;

    @Column(name = "AUDIT_REV")
    private Long auditRev;

    @Column(name = "LAST_REV_CHANGE_DATE")
    private Date lastRevChangeDate;

    @Column(name = "CONTEXT_OBJECT_TYPE")
    private String contextName;

    @Column(name = "CONTEXT_OBJECT_ID")
    private String contextType;

    @Column(name = "CONTEXT_IDENTIFIER")
    private String contextIdentifier;

    public PcmSupplierAllocationWithContextIdentifier() {
    }

    // Getters and setters
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

    public Site getSupplierSite() { return supplierSite; }
    public void setSupplierSite(Site supplierSite) { this.supplierSite = supplierSite; }

    public BusinessEntity getSupplierBusinessEntity() { return supplierBusinessEntity; }
    public void setSupplierBusinessEntity(BusinessEntity supplierBusinessEntity) { this.supplierBusinessEntity = supplierBusinessEntity; }

    public BigDecimal getAllocation() { return allocation; }
    public void setAllocation(BigDecimal allocation) { this.allocation = allocation; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSupplyAllocationComment() { return supplyAllocationComment; }
    public void setSupplyAllocationComment(String supplyAllocationComment) { this.supplyAllocationComment = supplyAllocationComment; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public Date getInsertDate() { return insertDate; }
    public void setInsertDate(Date insertDate) { this.insertDate = insertDate; }

    public Date getUpdateDate() { return updateDate; }
    public void setUpdateDate(Date updateDate) { this.updateDate = updateDate; }

    public Boolean getDeleteFlag() { return deleteFlag; }
    public void setDeleteFlag(Boolean deleteFlag) { this.deleteFlag = deleteFlag; }

    public boolean isCurrentFlag() { return currentFlag; }
    public void setCurrentFlag(boolean currentFlag) { this.currentFlag = currentFlag; }

    public Date getEffectiveFromDt() { return effectiveFromDt; }
    public void setEffectiveFromDt(Date effectiveFromDt) { this.effectiveFromDt = effectiveFromDt; }

    public Date getEffectiveToDt() { return effectiveToDt; }
    public void setEffectiveToDt(Date effectiveToDt) { this.effectiveToDt = effectiveToDt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getContextName() { return contextName; }
    public void setContextName(String contextName) { this.contextName = contextName; }

    public String getContextType() { return contextType; }
    public void setContextType(String contextType) { this.contextType = contextType; }

    public String getContextIdentifier() { return contextIdentifier; }
    public void setContextIdentifier(String contextIdentifier) { this.contextIdentifier = contextIdentifier; }

    public Site getDestinationSite() { return null; }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PcmSupplierAllocationWithContextIdentifier [contextIdentifier=");
        builder.append(this.contextIdentifier);
        builder.append(", supplierAllocationKey=");
        builder.append(this.supplierAllocationKey);
        builder.append("]");
        return builder.toString();
    }
}
