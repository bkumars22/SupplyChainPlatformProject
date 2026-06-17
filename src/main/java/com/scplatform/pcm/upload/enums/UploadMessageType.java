/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representing the logical grouping of upload message types.
 * Each constant owns one or more upload type strings.
 */
public enum UploadMessageType {

    Item("ItemBOMAVLUI", "ItemUI", "ItemAVLUI", "ItemXMLUploadUI", "ItemAVLXMLUploadUI", "ItemPlatformUI"),
    CommodityProfile("CommodityProfileUI"),
    FunctionalGroup("FunctionalGroupItemUploadUI"),
    FunctionalGroupConfig("FunctionalGroupConfigUploadUI"),
    UserCommodityProfile("UserCommodityProfileMappingUI"),
    SourcingLane("SourcingLaneUI", "CostRecordUI", "CostRecordActionUI", "CostRecordExpireUI",
            "MassUpdateCostRecordByParentFGUI", "MassUpdateCostRecordByFGUI"),
    SupplierAllocation("SupplierAllocationUI"),
    Currency("CurrencyUploadUI"),
    TAMSupplierCFG("TAMSupplierCFGUploadUI"),
    TAMItemCFG("TAMItemCFGUploadUI"),
    TAMAllocationMassUpdateCFG("TAMAllocationMassUpdateCFGUploadUI"),
    TAMAllocationDelete("TAMAllocationDeleteUI"),
    Forecast("CurrentForecastUI", "AdjustableForecastUI"),
    MassUpdateCostForecast("MassUpdateCostForecastByParentFGUI", "MassUpdateCostForecastByFGUI"),
    ParentFunctionalGroup("ParentFunctionalGroupUploadUI"),
    ParentFunctionalGroupConfig("ParentFunctionalGroupConfigUploadUI"),
    TAMAllocationMassUpdateCFGMRPSite("TAMAllocationMassUpdateCFGMRPSiteUploadUI"),
    TamSupplierAllocationCFGMRPSite("TAMSupplierCFGMRPSiteUploadUI"),
    TAMItemAllocationCFGMRPSite("TAMItemCFGMRPSiteUploadUI"),
    CostRecordMrpSite("CostRecordMrpSiteUI"),
    MassUpdateCostRecordExtension("MassUpdateCostRecordPFGExtensionODMBuyUI", "MassUpdateCostRecordPFGExtensionBuyUI"),
    XLOBAllocationDelete("XLOBAllocationDeleteUI"),
    MassUpdateCostRecordMRPSite("MassUpdateCostRecordFGMRPSiteBuyUI", "MassUpdateCostRecordPFGMRPSiteBuyUI",
            "MassUpdateCostRecordFGMRPSiteODMBuyUI", "MassUpdateCostRecordPFGMRPSiteODMBuyUI"),
    PriceTAMMonthly("PriceTAMMonthly"),
    PriceTAMMonthlyException("PriceTAMMonthlyException"),
    PriceTAMQuarterly("PriceTAMQuarterly"),
    PriceTAMQuarterlyException("PriceTAMQuarterlyException"),
    BusinessEntity("BusinessEntitySiteUploadUI", "BusinessEntityCurrencyUploadUI",
            "BusinessEntityAdditionalAttributeUploadUI");

    private final List<String> messageTypes;

    UploadMessageType(String... types) {
        this.messageTypes = List.of(types);
    }

    public List<String> getMessageTypes() {
        return messageTypes;
    }

    /**
     * Looks up the UploadMessageType whose list contains the given uploadType string (case-insensitive).
     *
     * @param uploadType the UI upload type string (e.g. "CostRecordUI")
     * @return matching enum constant, or null if not found
     */
    public static UploadMessageType lookupMessageType(String uploadType) {
        return Arrays.stream(values())
                .filter(e -> e.messageTypes.stream()
                        .anyMatch(t -> t.equalsIgnoreCase(uploadType)))
                .findFirst()
                .orElse(null);
    }
}
