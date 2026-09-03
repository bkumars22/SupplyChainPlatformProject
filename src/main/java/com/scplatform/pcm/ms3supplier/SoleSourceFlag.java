/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

/**
 * A single-point-of-failure warning: the supplier being scored has no
 * alternate source for componentOrMaterial, and that sole upstream
 * source currently shows elevated risk. Surfaced as its own explicit
 * flag rather than folded into one risk number, since "why" matters
 * more than the number alone to a procurement manager acting on this.
 */
public class SoleSourceFlag {
    private String upstreamSupplierId;
    private String upstreamSupplierName;
    private String componentOrMaterial;
    private double upstreamRisk;
    private int hopDistance;

    public SoleSourceFlag(String upstreamSupplierId, String upstreamSupplierName,
                           String componentOrMaterial, double upstreamRisk, int hopDistance) {
        this.upstreamSupplierId = upstreamSupplierId;
        this.upstreamSupplierName = upstreamSupplierName;
        this.componentOrMaterial = componentOrMaterial;
        this.upstreamRisk = upstreamRisk;
        this.hopDistance = hopDistance;
    }

    public String getUpstreamSupplierId() { return upstreamSupplierId; }
    public String getUpstreamSupplierName() { return upstreamSupplierName; }
    public String getComponentOrMaterial() { return componentOrMaterial; }
    public double getUpstreamRisk() { return upstreamRisk; }
    public int getHopDistance() { return hopDistance; }
}
