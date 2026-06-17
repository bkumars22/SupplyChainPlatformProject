/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.user.entity;

import com.scplatform.pcm.site.entity.Site;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name="PCM_USER_DELEGATE")
@Data
public class UserDelegate
{
    @Id
    @SequenceGenerator(name="PCM_ASSIGNMENT_SEQ", sequenceName = "PCM_ASSIGNMENT_SEQ",allocationSize = 1)
    @GeneratedValue(generator = "PCM_ASSIGNMENT_SEQ")
    @Column(name="DELEGATE_KEY")
    protected Long delegateKey;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="User_KEY")
    protected Users delegator;

    @Column(name="DELEGATE_USER_ID", length=60)
    protected String delegateUserId;

    @Column(name="RESPONSIBILITY", length=36, nullable=false)
    protected String responsibility;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="SITE_KEY")
    protected Site site;

    @Column(name="EFFECTIVE_FROM_DT")
    protected Date effectiveFromDate;

    @Column(name="EFFECTIVE_TO_DT")
    protected Date effectiveToDate;
}