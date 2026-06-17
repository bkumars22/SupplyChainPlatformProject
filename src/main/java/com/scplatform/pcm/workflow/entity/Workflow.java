/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.workflow.entity;

import java.util.Set;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import com.scplatform.pcm.accessControl.entity.AccessControl;


@Entity
@Table(name = "PCM_WORKFLOW_SPRING")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"parentWorkflow", "nestedWorkflows", "acls"})
@SuppressWarnings("serial")
public class Workflow implements java.io.Serializable
{

    @Id
    @Column(name = "WORKFLOW_KEY")
    private String workflowKey;

    @Column(name = "WORKFLOW_NAME")
    private String workflowName;

    @Column(name = "WORKFLOW_GROUP")
    private String workflowGroup;

    @Column(name = "WORKFLOW_URL")
    private String workflowUrl;

    @Column(name = "WORKFLOW_URL_TARGET")
    private String workflowUrlTarget;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKFLOW_PARENT_KEY")
    private Workflow parentWorkflow;

    @Column(name = "DISPLAY_ORDER")
    private int displayOrder;

    @OneToMany(mappedBy = "parentWorkflow", fetch = FetchType.LAZY)
    private Set<Workflow> nestedWorkflows;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_ENTITY_KEY", insertable = false, updatable = false)
    @SQLRestriction("ENTITY_TYPE='WORKFLOW'")
    private Set<AccessControl> acls;

}
