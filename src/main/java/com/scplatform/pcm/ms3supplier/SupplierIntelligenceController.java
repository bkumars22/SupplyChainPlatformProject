/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import com.scplatform.pcm.common.response.ApiResponse;
import com.scplatform.pcm.common.response.BaseApiController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Phase 2 of scip_master_plan: ESG risk, what-if scenario modeling, and
 * action suggestions -- all built on top of the Phase 1 dependency
 * cascade (SupplierRiskCascadeService). Kept in a separate controller
 * from SupplierController so this phase's work never touches that file.
 */
@RestController
@Tag(name = "Supplier Risk Intelligence", description = "ESG scoring, scenario simulation, and action suggestions")
public class SupplierIntelligenceController extends BaseApiController {

    @Autowired private SupplierEsgService esgService;
    @Autowired private ScenarioSimulationService scenarioService;
    @Autowired private ActionSuggestionService actionSuggestionService;

    @PostMapping("/api/suppliers/{supplierId}/esg-profile")
    @Operation(summary = "Set/update a supplier's ESG (sustainability) data")
    public ResponseEntity<ApiResponse<SupplierEsgProfile>> upsertEsgProfile(
            @PathVariable String supplierId, @RequestBody SupplierEsgRequest req) {
        return ok(esgService.upsertEsgProfile(supplierId, req));
    }

    @GetMapping("/api/suppliers/{supplierId}/esg-risk")
    @Operation(summary = "ESG/sustainability risk score -- reports honestly when data is missing or partial")
    public ResponseEntity<ApiResponse<EsgRiskDto>> getEsgRisk(@PathVariable String supplierId) {
        return ok(esgService.getEsgRisk(supplierId));
    }

    @GetMapping("/api/suppliers/{supplierId}/action-suggestions")
    @Operation(summary = "Human-approved action suggestions from this supplier's dependency and ESG risk")
    public ResponseEntity<ApiResponse<List<ActionSuggestion>>> getActionSuggestions(@PathVariable String supplierId) {
        return ok(actionSuggestionService.generateSuggestions(supplierId));
    }

    @PostMapping("/api/scenarios/simulate")
    @Operation(summary = "Read-only what-if simulation: overlay hypothetical supplier risk and project the cascading impact")
    public ResponseEntity<ApiResponse<ScenarioResultDto>> simulateScenario(@Valid @RequestBody ScenarioRequest req) {
        return ok(scenarioService.simulate(req.getHypotheticalChanges()));
    }
}
