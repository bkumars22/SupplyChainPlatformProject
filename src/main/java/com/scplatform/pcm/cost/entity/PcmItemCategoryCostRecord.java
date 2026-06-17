/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.cost.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.scplatform.pcm.item.entity.ItemCategory;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "PCM_ITEM_CATEGORY_COST_RECORD")
public class PcmItemCategoryCostRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pcm_item_cat_cost_record_seq")
    @SequenceGenerator(name = "pcm_item_cat_cost_record_seq", sequenceName = "PCM_ITEM_CAT_COST_RECORD_SEQ", allocationSize = 1)
    @Column(name = "ITEM_CATEGORY_COST_RECORD_KEY")
    private Long itemCategoryCostRecordKey;

    @ManyToOne
    @JoinColumn(name = "ITEM_CATEGORY_KEY", nullable = false)
    private ItemCategory itemCategory;

    @Column(name = "CONTEXT_OBJECT_TYPE")
    private String contextName;

    @Column(name = "CONTEXT_OBJECT_ID")
    private String contextType;

    @Column(name = "COST")
    private BigDecimal cost;

    public Long getItemCategoryCostRecordKey() {
        return itemCategoryCostRecordKey;
    }

    public void setItemCategoryCostRecordKey(Long itemCategoryCostRecordKey) {
        this.itemCategoryCostRecordKey = itemCategoryCostRecordKey;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public String getContextType() {
        return contextType;
    }

    public void setContextType(String contextType) {
        this.contextType = contextType;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
}
