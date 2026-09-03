/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import java.util.Map;

/**
 * isSimulation is always true and has no setter -- a UI or API consumer
 * confusing this projected/hypothetical output with real current state
 * could lead to a genuinely bad real-world procurement decision, so this
 * must never be something a caller can accidentally unset.
 */
public class ScenarioResultDto {
    private Map<String, Double> hypotheticalChanges;
    private Map<String, CascadedRiskDto> projectedImpact;
    private final boolean isSimulation = true;

    public Map<String, Double> getHypotheticalChanges() { return hypotheticalChanges; }
    public void setHypotheticalChanges(Map<String, Double> v) { this.hypotheticalChanges = v; }
    public Map<String, CascadedRiskDto> getProjectedImpact() { return projectedImpact; }
    public void setProjectedImpact(Map<String, CascadedRiskDto> v) { this.projectedImpact = v; }
    public boolean isSimulation() { return isSimulation; }
}
