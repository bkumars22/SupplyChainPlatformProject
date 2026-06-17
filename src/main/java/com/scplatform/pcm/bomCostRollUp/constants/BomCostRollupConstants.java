/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 */
package com.scplatform.pcm.bomCostRollUp.constants;

public final class BomCostRollupConstants {

    private BomCostRollupConstants() {
    }
    public static final int STATUS_NO_RECORD = 1;
    public static final int STATUS_ERROR     = -1;


    public static final String EMPTY_DATA_JSON = "{\"DATA\":[]}";


    public static final String COL_ITEM_NAME                     = "ITEM_NAME";
    public static final String COL_ITEM_ROLLUP_PRICE             = "ITEM_ROLLUP_PRICE";
    public static final String COL_ITEM_PART_NAME                = "ITEM_PART_NAME";
    public static final String COL_ITEM_PART_SELLING_PRICE       = "ITEM_PART_SELLING_PRICE";
    public static final String COL_ITEM_PART_ROLLUP_PRICE        = "ITEM_PART_ROLLUP_PRICE";
    public static final String COL_ITEM_PART_TOTAL_PRICE         = "ITEM_PART_TOTAL_PRICE";
    public static final String COL_ITEM_PART_QTY                 = "ITEM_PART_QTY";
    public static final String COL_DIRECT_MATERIAL               = "DIRECT_MATERIAL";
    public static final String COL_SHARING_COST                  = "SHARING_COST";
    public static final String COL_DIRECT_LABOR                  = "DIRECT_LABOR";
    public static final String COL_ITEM_VA_COST                  = "ITEM_VA_COST";
    public static final String COL_DIRECT_LABOR2                 = "DIRECT_LABOR2";
    public static final String COL_INDIRECT_LABOR                = "INDIRECT_LABOR";
    public static final String COL_MACHINE_EQUIPMENT             = "MACHINE_EQUIPMENT";
    public static final String COL_MATERIAL_HANDLING             = "MATERIAL_HANDLING";
    public static final String COL_MATERIAL_SCRAP                = "MATERIAL_SCRAP";
    public static final String COL_FREIGHT                       = "FREIGHT";
    public static final String COL_SGA                           = "SGA";
    public static final String COL_FINANCIAL_RECEIVABLES         = "FINANCIAL_RECEIVABLES";
    public static final String COL_PROFIT_MARGIN                 = "PROFIT_MARGIN";
    public static final String COL_ADJUSTMENTS_REDUCTIONS        = "ADJUSTMENTS_REDUCTIONS";
    public static final String COL_MISCELLANEOUS                 = "MISCELLANEOUS";
    public static final String COL_TARIFF                        = "TARIFF";
    public static final String COL_BOM_KEY                       = "BOM_KEY";
    public static final String COL_CREATED_ON                    = "CREATED_ON";
    public static final String COL_CURRENCY_CONVERSION_ERROR_MSG = "CURRENCY_CONVERSION_ERROR_MSG";

    public static final int ROLLUP_ROW_FIELD_COUNT = 26;


    public static final String PROC_NAME            = "GET_BOM_HIERARCHY_WITH_COST";
    public static final String PARAM_ROOT_BOM_KEY   = "p_root_bom_key";
    public static final String PARAM_USER_KEY       = "p_user_key";
    public static final String PARAM_EFFECTIVE_DATE = "p_effective_date";
    public static final String PARAM_RESULTSET      = "p_resultset";

    public static final String P_BOM_KEY  = "bomKey";
    public static final String P_USER_KEY = "userKey";
    public static final String P_EFF_DATE = "effDate";


    public static final String SQL_SELECT_ROLLUP_ROWS =
              "SELECT " + COL_ITEM_NAME               + ", " + COL_ITEM_ROLLUP_PRICE       + ", "
                        + COL_ITEM_PART_NAME          + ", " + COL_ITEM_PART_SELLING_PRICE + ", "
                        + COL_ITEM_PART_ROLLUP_PRICE  + ", " + COL_ITEM_PART_TOTAL_PRICE   + ", "
                        + COL_ITEM_PART_QTY           + ", " + COL_DIRECT_MATERIAL         + ", "
                        + COL_SHARING_COST            + ", " + COL_DIRECT_LABOR            + ", "
                        + COL_ITEM_VA_COST            + ", " + COL_DIRECT_LABOR2           + ", "
                        + COL_INDIRECT_LABOR          + ", " + COL_MACHINE_EQUIPMENT       + ", "
                        + COL_MATERIAL_HANDLING       + ", " + COL_MATERIAL_SCRAP          + ", "
                        + COL_FREIGHT                 + ", " + COL_SGA                     + ", "
                        + COL_FINANCIAL_RECEIVABLES   + ", " + COL_PROFIT_MARGIN           + ", "
                        + COL_ADJUSTMENTS_REDUCTIONS  + ", " + COL_MISCELLANEOUS           + ", "
                        + COL_TARIFF                  + ", " + COL_BOM_KEY                 + ", "
                        + COL_CREATED_ON              + ", " + COL_CURRENCY_CONVERSION_ERROR_MSG
            + "  FROM BOM_ROLLUP_HIERARCHY_STG"
            + " WHERE ROOT_BOM_KEY = :" + P_BOM_KEY
            + "   AND USER_KEY     = :" + P_USER_KEY
            + "   AND BOM_KEY      = :" + P_BOM_KEY
            + " ORDER BY " + COL_ITEM_PART_NAME;

    public static final String SQL_SELECT_ROLLUP_STATUS =
              "SELECT STATUS FROM BOM_ROLLUP_COST_LOG"
            + " WHERE ROOT_BOM_KEY = :" + P_BOM_KEY
            + "   AND TRUNC(EFFECTIVE_DATE) = TRUNC(:" + P_EFF_DATE + ")";
}

