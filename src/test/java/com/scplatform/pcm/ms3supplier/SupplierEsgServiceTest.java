/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.ms3supplier;

import com.scplatform.pcm.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Per the source plan: "explicitly tested with suppliers that have partial
 * or zero ESG data, confirming the system says so clearly rather than
 * fabricating a misleadingly confident score."
 */
@ExtendWith(MockitoExtension.class)
class SupplierEsgServiceTest {

    @Mock private SupplierRepository supplierRepo;
    @Mock private SupplierEsgProfileRepository esgRepo;
    @InjectMocks private SupplierEsgService service;

    private static SupplierEsgProfile profile(String id, String certs, Double carbon, Integer violations, Double labor) {
        SupplierEsgProfile p = new SupplierEsgProfile();
        p.setSupplierId(id);
        p.setEsgCertifications(certs);
        p.setCarbonIntensityScore(carbon);
        p.setComplianceViolations12mo(violations);
        p.setLaborAuditScore(labor);
        return p;
    }

    @Test
    void noEsgRowAtAll_reportsUnavailableNotAFabricatedScore() {
        when(supplierRepo.existsById("SUPP-999")).thenReturn(true);
        when(esgRepo.findById("SUPP-999")).thenReturn(Optional.empty());

        EsgRiskDto result = service.getEsgRisk("SUPP-999");

        assertFalse(result.isEsgDataAvailable());
        assertNull(result.getEsgRiskScore());
        assertNotNull(result.getMessage());
    }

    @Test
    void unknownSupplier_throws() {
        when(supplierRepo.existsById("GHOST")).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.getEsgRisk("GHOST"));
    }

    @Test
    void completeData_scoresWithNoMissingFields() {
        // Low carbon intensity (good), zero violations, strong labor audit.
        SupplierEsgProfile p = profile("SUPP-001", "ISO14001,SA8000", 20.0, 0, 85.0);

        EsgRiskDto dto = SupplierEsgService.score(p);

        assertTrue(dto.isEsgDataAvailable());
        assertTrue(dto.getMissingFields().isEmpty());
        assertEquals(List.of("ISO14001", "SA8000"), dto.getEsgCertifications());
        // carbon component: 100-20=80, compliance: 100-0=100, labor: 85 -> avg = 88.33...
        assertEquals(88.3, dto.getEsgRiskScore(), 0.05);
    }

    @Test
    void partialData_defaultsMissingFieldsAndListsThem() {
        SupplierEsgProfile p = profile("SUPP-003", null, 55.0, null, null);

        EsgRiskDto dto = SupplierEsgService.score(p);

        assertTrue(dto.isEsgDataAvailable());
        assertEquals(List.of("complianceViolations12mo", "laborAuditScore"), dto.getMissingFields());
        assertTrue(dto.getEsgCertifications().isEmpty());
        // carbon: 100-55=45, compliance: neutral 50, labor: neutral 50 -> avg ~48.33
        assertEquals(48.3, dto.getEsgRiskScore(), 0.05);
    }

    @Test
    void multipleViolations_dragScoreDownButNeverBelowZero() {
        SupplierEsgProfile p = profile("SUPP-005", "ISO14001", 45.0, 6, 60.0);

        EsgRiskDto dto = SupplierEsgService.score(p);

        // compliance component: 100 - 6*20 = -20 -> clamped to 0
        assertEquals(6, dto.getComplianceViolations12mo());
        assertTrue(dto.getEsgRiskScore() >= 0);
    }

    @Test
    void upsert_rejectsUnknownSupplier() {
        when(supplierRepo.existsById("GHOST")).thenReturn(false);
        SupplierEsgRequest req = new SupplierEsgRequest();
        assertThrows(ResourceNotFoundException.class, () -> service.upsertEsgProfile("GHOST", req));
        verify(esgRepo, never()).save(any());
    }
}
