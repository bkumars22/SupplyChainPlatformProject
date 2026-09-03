/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

/** hypotheticalChanges: supplierId -> hypothetical risk (0-1, higher is worse). */
public class ScenarioRequest {
    @NotEmpty
    private Map<String, Double> hypotheticalChanges;

    public Map<String, Double> getHypotheticalChanges() { return hypotheticalChanges; }
    public void setHypotheticalChanges(Map<String, Double> v) { this.hypotheticalChanges = v; }
}
