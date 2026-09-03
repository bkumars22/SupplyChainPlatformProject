/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import jakarta.persistence.*;

/**
 * One edge in the supplier dependency graph: dependentSupplier sources
 * componentOrMaterial from upstreamSupplier. Stored as plain supplier-id
 * strings (not @ManyToOne) so the cascade algorithm can load the whole
 * edge list in one query and build an in-memory graph, rather than
 * lazy-loading a SupplierProfile per edge.
 */
@Entity
@Table(name = "SUPPLIER_DEPENDENCY")
public class SupplierDependency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dependent_supplier_id", length = 50, nullable = false)
    private String dependentSupplierId;

    @Column(name = "upstream_supplier_id", length = 50, nullable = false)
    private String upstreamSupplierId;

    @Column(name = "component_or_material")
    private String componentOrMaterial;

    @Column(name = "dependency_criticality", nullable = false)
    private Double dependencyCriticality = 0.5;   // 1.0 = essential/no substitute, 0.3 = minor input

    @Column(name = "is_sole_source", nullable = false)
    private Boolean isSoleSource = false;          // true if dependentSupplier has no alternate source for this input

    public Long getId() { return id; }
    public String getDependentSupplierId() { return dependentSupplierId; }
    public void setDependentSupplierId(String v) { this.dependentSupplierId = v; }
    public String getUpstreamSupplierId() { return upstreamSupplierId; }
    public void setUpstreamSupplierId(String v) { this.upstreamSupplierId = v; }
    public String getComponentOrMaterial() { return componentOrMaterial; }
    public void setComponentOrMaterial(String v) { this.componentOrMaterial = v; }
    public Double getDependencyCriticality() { return dependencyCriticality; }
    public void setDependencyCriticality(Double v) { this.dependencyCriticality = v; }
    public Boolean getIsSoleSource() { return isSoleSource; }
    public void setIsSoleSource(Boolean v) { this.isSoleSource = v; }
}
