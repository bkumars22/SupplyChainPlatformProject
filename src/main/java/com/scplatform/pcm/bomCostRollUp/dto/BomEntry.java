/*
 * Copyright (c) 2025 E2open Inc. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2025, by E2open Inc. All rights reserved.
 */
package com.scplatform.pcm.bomCostRollUp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BomEntry {

    @JsonProperty("ITEM_NAME")
    private String itemName;

    @JsonProperty("ITEM_ROLLUP_PRICE")
    private Double itemRollupPrice;

    @JsonProperty("ITEM_PART_NAME")
    private String itemPartName;

    @JsonProperty("ITEM_PART_SELLING_PRICE")
    private Double itemPartSellingPrice;

    @JsonProperty("ITEM_PART_ROLLUP_PRICE")
    private Double itemPartRollupPrice;

    @JsonProperty("ITEM_PART_TOTAL_PRICE")
    private Double itemPartTotal;

    @JsonProperty("ITEM_PART_QTY")
    private Double itemPartQty = 1.0;

    @JsonProperty("DIRECT_MATERIAL")
    private Double directMaterial;

    @JsonProperty("SHARING_COST")
    private Double sharingCost;

    @JsonProperty("DIRECT_LABOR")
    private Double directLabor;

    @JsonProperty("VA_COST")
    private Double vaCost;

    @JsonProperty("DIRECT_LABOR2")
    private Double directLabor2;

    @JsonProperty("INDIRECT_LABOR")
    private Double indirectLabor;

    @JsonProperty("MACHINE_EQUIPMENT")
    private Double machineEquipement;

    @JsonProperty("MATERIAL_HANDLING")
    private Double materialHandling;

    @JsonProperty("MATERIAL_SCRAP")
    private Double materialScrap;

    @JsonProperty("FREIGHT")
    private Double fright;

    @JsonProperty("SGA")
    private Double sga;

    @JsonProperty("FINANCIAL_RECEIVABLES")
    private Double financialReceivables;

    @JsonProperty("PROFIT_MARGIN")
    private Double profitMargin;

    @JsonProperty("ADJUSTMENTS_REDUCTIONS")
    private Double adjustmentsReduction;

    @JsonProperty("MISCELLANEOUS")
    private Double miscellaneous;

    @JsonProperty("TARIFF")
    private Double tariff;
}
