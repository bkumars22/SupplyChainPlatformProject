/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.costexception.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Models cost exception approvers and their approval states.
 */
@Entity
@Table(name = "COST_EXCEPTION_APPROVER")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"exception"})
public class CostExceptionApprover implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COST_EXCEPTION_APPROVER_SEQ")
        @SequenceGenerator(name = "COST_EXCEPTION_APPROVER_SEQ", sequenceName = "COST_EXCEPTION_APPROVER_SEQ", allocationSize = 1, initialValue = 1)
        @Column(name = "ID")
        private Long id;

        @ManyToOne
        @JoinColumn(name = "EXCEPTION_KEY")
        private CostException exception;

        @Column(name = "APPROVER_ROLE")
        private String approverRole;

        @Column(name = "APPROVER", nullable = true)
        private String approver;

        @Column(name = "STATE")
        private String state;

        @Column(name = "PROXY_APPROVER", nullable = true)
        private String proxyApprover;

        @Column(name = "PROXY_APPROVER_ROLE", nullable = true)
        private String proxyApproverRole;

        @Column(name = "ACTION_DATE", nullable = true)
        private Timestamp actionDate;

        @Column(name = "ACTUAL_USER", nullable = true)
        private String actualUser;
}
