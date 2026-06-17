/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.cost.entity;

import com.scplatform.pcm.assignment.entity.Assignment;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Assignment for Cost Records - uses discriminator value "CR"
 */
@Entity
@DiscriminatorValue("CR")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"costRecord"})
public class PcmCostRecordAssignment extends Assignment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ManyToOne(optional = false)
    @JoinColumn(name = "OBJECT_KEY", nullable = false)
    private PcmCostRecord costRecord;
}
