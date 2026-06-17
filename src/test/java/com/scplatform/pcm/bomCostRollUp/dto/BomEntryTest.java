/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.bomCostRollUp.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class BomEntryTest {

    @Test
    void defaultConstructor_setsItemPartQtyToOne() {
        BomEntry e = new BomEntry();
        assertEquals(1.0, e.getItemPartQty());
    }

    @Test
    void allSettersAndGetters_work() {
        BomEntry e = new BomEntry();
        e.setItemName("I");
        e.setItemRollupPrice(2.5);
        e.setItemPartName("P");
        e.setItemPartSellingPrice(3.0);
        e.setItemPartRollupPrice(4.0);
        e.setItemPartTotal(5.0);
        e.setItemPartQty(6.0);
        e.setDirectMaterial(1.0);
        e.setSharingCost(1.1);
        e.setDirectLabor(1.2);
        e.setVaCost(1.3);
        e.setDirectLabor2(1.4);
        e.setIndirectLabor(1.5);
        e.setMachineEquipement(1.6);
        e.setMaterialHandling(1.7);
        e.setMaterialScrap(1.8);
        e.setFright(1.9);
        e.setSga(2.0);
        e.setFinancialReceivables(2.1);
        e.setProfitMargin(2.2);
        e.setAdjustmentsReduction(2.3);
        e.setMiscellaneous(2.4);
        e.setTariff(2.5);

        assertEquals("I", e.getItemName());
        assertEquals(2.5, e.getItemRollupPrice());
        assertEquals("P", e.getItemPartName());
        assertEquals(3.0, e.getItemPartSellingPrice());
        assertEquals(4.0, e.getItemPartRollupPrice());
        assertEquals(5.0, e.getItemPartTotal());
        assertEquals(6.0, e.getItemPartQty());
        assertEquals(1.0, e.getDirectMaterial());
        assertEquals(1.1, e.getSharingCost());
        assertEquals(1.2, e.getDirectLabor());
        assertEquals(1.3, e.getVaCost());
        assertEquals(1.4, e.getDirectLabor2());
        assertEquals(1.5, e.getIndirectLabor());
        assertEquals(1.6, e.getMachineEquipement());
        assertEquals(1.7, e.getMaterialHandling());
        assertEquals(1.8, e.getMaterialScrap());
        assertEquals(1.9, e.getFright());
        assertEquals(2.0, e.getSga());
        assertEquals(2.1, e.getFinancialReceivables());
        assertEquals(2.2, e.getProfitMargin());
        assertEquals(2.3, e.getAdjustmentsReduction());
        assertEquals(2.4, e.getMiscellaneous());
        assertEquals(2.5, e.getTariff());
    }

    @Test
    void equalsAndHashCode_workViaLombok() {
        BomEntry a = new BomEntry();
        a.setItemName("X"); a.setItemPartName("P");
        BomEntry b = new BomEntry();
        b.setItemName("X"); b.setItemPartName("P");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        b.setItemName("Y");
        assertNotEquals(a, b);
    }

    @Test
    void toString_isNotNull() {
        assertNotNull(new BomEntry().toString());
    }
}
