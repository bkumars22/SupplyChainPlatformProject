CREATE OR REPLACE VIEW IV_REBATE_ITEM AS
SELECT
    RP.REBATE_PROGRAM_KEY,
    RP.REBATE_NAME,
    RP.REBATE_TYPE,
    RP.REBATE_EXTERNAL_ID,
    RP.REBATE_DESCRIPTION,
    RP.CONFIDENCE_FACTOR,
    RP.PROGRAM_OWNER,
    RP.FINANCIAL_PROGRAM_OWNER,
    RP.PAYMENT_TYPE,
    RP.PROGRAM_TYPE,
    RP.STATUS,
    RP.STATUS_CHANGE_DATE,
    RP.STATUS_LAST_CHANGE_BY,
    CAST(RP.INSERT_DT AS TIMESTAMP) AS INSERT_DT,
    CAST(RP.UPDATE_DT AS TIMESTAMP) AS UPDATE_DT,
    CAST(RP.EFFECTIVE_FROM_DT AS TIMESTAMP) AS EFFECTIVE_FROM_DT,
    CAST(RP.EFFECTIVE_TO_DT AS TIMESTAMP) AS EFFECTIVE_TO_DT,
    B.BUSINESS_ENTITY_IDENTIFIER,
    RI.REBATE_ITEM_KEY RI_REBATE_ITEM_KEY,
    RI.REBATE_AMOUNT RI_REBATE_AMOUNT,
    CAST(RI.EFFECTIVE_FROM_DT AS TIMESTAMP) AS RI_EFFECTIVE_FROM_DT,
    CAST(RI.EFFECTIVE_TO_DT AS TIMESTAMP) AS RI_EFFECTIVE_TO_DT,
    RP.CURRENT_FLAG CURRENT_FLAG,
    I.ITEM_KEY I_ITEM_KEY,
    I.ITEM_IDENTIFIER I_IDENTIFIER,
    I.ITEM_UNIQUE_IDENTIFIER I_UNIQUE_IDENTIFIER,
    I.VERSION I_VERSION,
    I.REVISION I_REVISION,
    I.BUSINESS_ENTITY_IDENTIFIER I_BUSINESS_ENTITY_IDENTIFIER,
    I.BUSINESS_ENTITY_TYPE_NAME I_BUSIENSS_ENTITY_TYPE_NAME
FROM PCM_REBATE_PROGRAM RP
  LEFT OUTER JOIN PCM_REBATE_ITEM RI ON RP.REBATE_PROGRAM_KEY = RI.REBATE_PROGRAM_KEY
  INNER JOIN BUSINESS_ENTITY B ON RP.REBATE_BUSINESS_ENTITY_KEY = B.BUSINESS_ENTITY_KEY
  LEFT OUTER JOIN IV_ITEM_BUSINESS I ON RI.ITEM_KEY = I.ITEM_KEY;


CREATE TABLE COST_ROLLUP_CHANGE (
    BOM_KEY        NUMBER NOT NULL,
    EFFECTIVE_DATE DATE NOT NULL,
    USER_KEY       NUMBER,
    CREATE_DATE    TIMESTAMP DEFAULT SYSTIMESTAMP
);

ALTER TABLE BOM_ROLLUP_COST_DATA_STG
ADD (
    CURRENCY_CODE VARCHAR2(3),
    MISSING_BE    VARCHAR2(100)
);

Alter TABLE BOM_ROLLUP_HIERARCHY_STG
add CURRENCY_CONVERSION_ERROR_MSG       CLOB ;

create or replace PROCEDURE GET_BOM_HIERARCHY_WITH_COST(
     p_root_bom_key     IN NUMBER,
     p_user_key         IN NUMBER,
     p_effective_date   IN DATE
 )
 IS
     v_level NUMBER := 1;
     v_count NUMBER := 0;
     v_log_id NUMBER;
     v_status NUMBER;
     v_start_time TIMESTAMP := SYSTIMESTAMP;
     v_end_time TIMESTAMP;
     v_error_msg VARCHAR2(4000);
      v_missing_be VARCHAR2(4000); -- capture BEs missing currency conversion
 BEGIN
     -- Step 0: Check for existing records with the given ROOT_BOM_KEY
     BEGIN
         SELECT BOM_ROLLUP_LOG_ID, STATUS
         INTO v_log_id, v_status
         FROM BOM_ROLLUP_COST_LOG
         WHERE ROOT_BOM_KEY = p_root_bom_key
         FOR UPDATE;

         -- Case 1: If STATUS = 0, log the duplicate request and prepare duplicate request JSON and return
         IF v_status = 0 THEN

             UPDATE BOM_ROLLUP_COST_LOG
             SET DUPLICATE_REQUEST_BY = p_user_key,
               DUPLICATE_REQUEST_ON = SYSTIMESTAMP,
               DUPLICATE_REQUEST_COUNT = NVL(DUPLICATE_REQUEST_COUNT, 0) + 1
             WHERE BOM_ROLLUP_LOG_ID = v_log_id;

             ELSIF v_status IN (1, -1) THEN
             UPDATE BOM_ROLLUP_COST_LOG
                SET STATUS        = 0,
                    EFFECTIVE_DATE = p_effective_date,
                    START_TIME     = v_start_time
              WHERE BOM_ROLLUP_LOG_ID = v_log_id;
         END IF;

     EXCEPTION
         -- Case 3: If no records exist, insert a new record and proceed
         WHEN NO_DATA_FOUND THEN
             INSERT INTO BOM_ROLLUP_COST_LOG (
                 ROOT_BOM_KEY, EFFECTIVE_DATE, STATUS, CREATED_ON, CREATED_BY, START_TIME, DUPLICATE_REQUEST_COUNT
             ) VALUES (
                 p_root_bom_key, p_effective_date, 0, SYSTIMESTAMP, p_user_key, v_start_time, 0
             )
             RETURNING BOM_ROLLUP_LOG_ID INTO v_log_id;
     END;

     -- Step 1: Clear temp tables for this ROOT_BOM_KEY
     DELETE FROM BOM_ROLLUP_HIERARCHY_STG WHERE ROOT_BOM_KEY = p_root_bom_key;
     DELETE FROM BOM_ROLLUP_COST_DATA_STG WHERE ROOT_BOM_KEY = p_root_bom_key;

     -- Step 2: Insert root BOM
     INSERT INTO BOM_ROLLUP_HIERARCHY_STG (
         USER_KEY, ROOT_BOM_KEY, BOM_KEY, BOM_LEVEL, BOM_ITEM_KEY, BOM_LINE_KEY,
         BLI_ITEM_KEY, SUB_BOM_KEY, ITEM_NAME, ITEM_PART_NAME, ITEM_PART_QTY
     )
     SELECT
         p_user_key, bh.BOM_KEY, bh.BOM_KEY, 1, bh.ITEM_KEY, bli.BOM_LINE_ITEM_KEY,
         bli.ITEM_KEY, bli.SUB_BOM_KEY, im1.ITEM_IDENTIFIER, im2.ITEM_IDENTIFIER, bli.ITEM_QUANTITY
     FROM BOM_HEADER bh
     JOIN BOM_LINE_ITEM bli ON bh.BOM_KEY = bli.BOM_KEY
     LEFT JOIN ITEM_MASTER im1 ON bh.ITEM_KEY = im1.ITEM_KEY
     LEFT JOIN ITEM_MASTER im2 ON bli.ITEM_KEY = im2.ITEM_KEY
     WHERE bh.BOM_KEY = p_root_bom_key;

     -- Step 3: Recursively insert child BOMs
     LOOP
         v_level := v_level + 1;

         INSERT INTO BOM_ROLLUP_HIERARCHY_STG (
             USER_KEY, ROOT_BOM_KEY, BOM_KEY, BOM_LEVEL, BOM_ITEM_KEY, BOM_LINE_KEY,
             BLI_ITEM_KEY, SUB_BOM_KEY, ITEM_NAME, ITEM_PART_NAME, ITEM_PART_QTY
         )
         SELECT
             p_user_key, temp.ROOT_BOM_KEY, bh.BOM_KEY, v_level, bh.ITEM_KEY, bli.BOM_LINE_ITEM_KEY,
             bli.ITEM_KEY, bli.SUB_BOM_KEY, im1.ITEM_IDENTIFIER, im2.ITEM_IDENTIFIER, bli.ITEM_QUANTITY
         FROM BOM_LINE_ITEM bli
         JOIN BOM_ROLLUP_HIERARCHY_STG temp
             ON bli.BOM_KEY = temp.SUB_BOM_KEY
            AND temp.BOM_LEVEL = v_level - 1
            AND temp.ROOT_BOM_KEY = p_root_bom_key
         JOIN BOM_HEADER bh ON bh.BOM_KEY = bli.BOM_KEY
         LEFT JOIN ITEM_MASTER im1 ON bh.ITEM_KEY = im1.ITEM_KEY
         LEFT JOIN ITEM_MASTER im2 ON bli.ITEM_KEY = im2.ITEM_KEY;

         v_count := SQL%ROWCOUNT;
         EXIT WHEN v_count = 0;
     END LOOP;

     -- Step 4: Populate BOM_ROLLUP_COST_DATA_STG
     INSERT INTO BOM_ROLLUP_COST_DATA_STG (
         USER_KEY, ROOT_BOM_KEY, ITEM_PART_NAME, BLI_ITEM_KEY, SL_ITEM_KEY, BLI_BUSINESS_ENTITY_KEY, BUSINESS_ENTITY_KEY, SOURCING_LANE_KEY, SUPPLIER_KEY, COST_RECORD_KEY, PRICE_TYPE,
         PRICE_ORDER, CURRENCY_CODE,MISSING_BE,TOTAL_COST, DIRECT_MATERIAL, SHARING_COST, DIRECT_LABOR, VA_COST, DIRECT_LABOR2, INDIRECT_LABOR, MACHINE_EQUIPMENT, MATERIAL_HANDLING, MATERIAL_SCRAP, FREIGHT,
         SGA, FINANCIAL_RECEIVABLES, PROFIT_MARGIN, ADJUSTMENTS_REDUCTIONS, MISCELLANEOUS, TARIFF
     )
     SELECT
       p_user_key,
       ROOT_BOM_KEY,
       ITEM_PART_NAME,
       BLI_ITEM_KEY,
       ITEM_KEY,
       BLI_BUSINESS_ENTITY_KEY,
       BUSINESS_ENTITY_KEY,
       SOURCING_LANE_KEY,
       SUPPLIER_KEY,
       COST_RECORD_KEY,
       PRICE_TYPE,
       PRICE_ORDER,
       CURRENCY_CODE,
       MISSING_BE,
       NVL(DIRECT_MATERIAL, 0) +
       NVL(SHARING_COST, 0) +
       NVL(DIRECT_LABOR, 0) +
       NVL(DIRECT_MATERIAL, 0) * (
         NVL(DIRECT_LABOR2, 0) +
         NVL(INDIRECT_LABOR, 0) +
         NVL(MACHINE_EQUIPMENT, 0) +
         NVL(MATERIAL_HANDLING, 0) +
         NVL(MATERIAL_SCRAP, 0) +
         NVL(FREIGHT, 0) +
         NVL(SGA, 0) +
         NVL(FINANCIAL_RECEIVABLES, 0) +
         NVL(PROFIT_MARGIN, 0) +
         NVL(ADJUSTMENTS_REDUCTIONS, 0) +
         NVL(MISCELLANEOUS, 0) +
         NVL(TARIFF, 0)
       ) AS TOTAL_COST,
       DIRECT_MATERIAL,
       SHARING_COST,
       DIRECT_LABOR,
       VA_COST,
       CASE WHEN DIRECT_MATERIAL > 0 THEN DIRECT_LABOR2 / DIRECT_MATERIAL ELSE 0 END AS DIRECT_LABOR2,
       CASE WHEN DIRECT_MATERIAL > 0 THEN INDIRECT_LABOR / DIRECT_MATERIAL ELSE 0 END AS INDIRECT_LABOR,
       CASE WHEN DIRECT_MATERIAL > 0 THEN MACHINE_EQUIPMENT / DIRECT_MATERIAL ELSE 0 END AS MACHINE_EQUIPMENT,
       CASE WHEN DIRECT_MATERIAL > 0 THEN MATERIAL_HANDLING / DIRECT_MATERIAL ELSE 0 END AS MATERIAL_HANDLING,
       CASE WHEN DIRECT_MATERIAL > 0 THEN MATERIAL_SCRAP / DIRECT_MATERIAL ELSE 0 END AS MATERIAL_SCRAP,
       CASE WHEN DIRECT_MATERIAL > 0 THEN FREIGHT / DIRECT_MATERIAL ELSE 0 END AS FREIGHT,
       CASE WHEN DIRECT_MATERIAL > 0 THEN SGA / DIRECT_MATERIAL ELSE 0 END AS SGA,
       CASE WHEN DIRECT_MATERIAL > 0 THEN FINANCIAL_RECEIVABLES / DIRECT_MATERIAL ELSE 0 END AS FINANCIAL_RECEIVABLES,
       CASE WHEN DIRECT_MATERIAL > 0 THEN PROFIT_MARGIN / DIRECT_MATERIAL ELSE 0 END AS PROFIT_MARGIN,
       CASE WHEN DIRECT_MATERIAL > 0 THEN ADJUSTMENTS_REDUCTIONS / DIRECT_MATERIAL ELSE 0 END AS ADJUSTMENTS_REDUCTIONS,
       CASE WHEN DIRECT_MATERIAL > 0 THEN MISCELLANEOUS / DIRECT_MATERIAL ELSE 0 END AS MISCELLANEOUS,
       CASE WHEN DIRECT_MATERIAL > 0 THEN TARIFF / DIRECT_MATERIAL ELSE 0 END AS TARIFF

     FROM (
       SELECT
         p_user_key,
         tmp.ROOT_BOM_KEY,
         tmp.ITEM_PART_NAME,
         tmp.BLI_ITEM_KEY,
         sl.ITEM_KEY,
         im_bli.BUSINESS_ENTITY_KEY AS BLI_BUSINESS_ENTITY_KEY,
         im.BUSINESS_ENTITY_KEY,
         sl.SOURCING_LANE_KEY,
         sl.SUPPLIER_KEY,
         cr.COST_RECORD_KEY,
         CASE
           WHEN im.BUSINESS_ENTITY_KEY = sl.SUPPLIER_KEY THEN 'COMPONENT PRICE'
           WHEN sl.SUPPLIER_KEY IS NULL THEN 'VA PRICE'
           WHEN im_bli.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY THEN 'PRICE OVERRIDE'
         END AS PRICE_TYPE,
         CASE
           WHEN im.BUSINESS_ENTITY_KEY = sl.SUPPLIER_KEY THEN 2
           WHEN sl.SUPPLIER_KEY IS NULL THEN 3
           WHEN im_bli.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY THEN 1
         END AS PRICE_ORDER,
         crv.COST_ELEMENT_KEY,
          CASE
                 WHEN crv.COST_VALUE IS NULL THEN -1
                 WHEN sl.CURRENCY_CODE = 'USD' THEN crv.COST_VALUE
           ELSE crv.COST_VALUE *
                  NVL(curr.CONVERSION_RATE, curr_def.CONVERSION_RATE)
           END AS COST_VALUE,
        sl.CURRENCY_CODE,
        CASE
       WHEN sl.CURRENCY_CODE <> 'USD'
       AND curr.CONVERSION_RATE IS NULL
       AND curr_def.CONVERSION_RATE IS NULL
  THEN be.BUSINESS_ENTITY_IDENTIFIER
  ELSE NULL
         END AS MISSING_BE
       FROM BOM_ROLLUP_HIERARCHY_STG tmp
       JOIN ITEM_MASTER im
         ON tmp.ITEM_PART_NAME = im.ITEM_IDENTIFIER
        AND tmp.ROOT_BOM_KEY = p_root_bom_key
	JOIN BUSINESS_ENTITY be
         on im.BUSINESS_ENTITY_KEY = be.BUSINESS_ENTITY_KEY
       JOIN ITEM_MASTER im_bli
         ON tmp.BLI_ITEM_KEY = im_bli.ITEM_KEY
       JOIN PCM_SOURCING_LANE sl
         ON im.ITEM_KEY = sl.ITEM_KEY
       JOIN PCM_COST_RECORD cr
         ON sl.SOURCING_LANE_KEY = cr.SOURCING_LANE_KEY
       JOIN PCM_COST_RECORD_RANGE crr
         ON cr.COST_RECORD_KEY = crr.COST_RECORD_KEY
        --AND crr.IS_ACTIVE = 1
       JOIN PCM_COST_RECORD_VALUE crv
         ON crr.COST_RECORD_RANGE_KEY = crv.COST_RECORD_RANGE_KEY
        LEFT JOIN CURRENCY curr
       ON curr.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY
      AND curr.FROM_CURRENCY = sl.CURRENCY_CODE
     AND curr.START_DATE <= cr.EFFECTIVE_FROM_DT
     LEFT JOIN CURRENCY curr_def
       ON curr_def.BUSINESS_ENTITY_KEY = -1
      AND curr_def.FROM_CURRENCY = sl.CURRENCY_CODE
      AND curr_def.START_DATE <= cr.EFFECTIVE_FROM_DT
       WHERE cr.EFFECTIVE_FROM_DT <= TRUNC(p_effective_date)
         AND (cr.EFFECTIVE_TO_DT IS NULL OR cr.EFFECTIVE_TO_DT >= TRUNC(p_effective_date))
         AND cr.STATUS = 'APPROVED'
     )
     PIVOT (
       MAX(COST_VALUE)
       FOR COST_ELEMENT_KEY IN (
         'DIRECT_MATERIAL' AS DIRECT_MATERIAL,
         'SHARING_COST' AS SHARING_COST,
         'DIRECT_LABOR' AS DIRECT_LABOR,
         'VA_COST' AS VA_COST,
         'DIRECT_LABOR2' AS DIRECT_LABOR2,
         'INDIRECT_LABOR' AS INDIRECT_LABOR,
         'MACHINE_EQUIPMENT' AS MACHINE_EQUIPMENT,
         'MATERIAL_HANDLING' AS MATERIAL_HANDLING,
         'MATERIAL_SCRAP' AS MATERIAL_SCRAP,
         'FREIGHT' AS FREIGHT,
         'SGA' AS SGA,
         'FINANCIAL_RECEIVABLES' AS FINANCIAL_RECEIVABLES,
         'PROFIT_MARGIN' AS PROFIT_MARGIN,
         'ADJUSTMENTS_REDUCTIONS' AS ADJUSTMENTS_REDUCTIONS,
         'MISCELLANEOUS' AS MISCELLANEOUS,
         'TARIFF' AS TARIFF
       )
     );

  -------------------------------------------------------------------------
     -- Step 4b: Log missing currency conversion
     -------------------------------------------------------------------------
    DECLARE
     v_missing_be VARCHAR2(4000);
 BEGIN
     -- Aggregate missing BEs
     SELECT LISTAGG(DISTINCT MISSING_BE, ',') WITHIN GROUP (ORDER BY MISSING_BE)
       INTO v_missing_be
       FROM BOM_ROLLUP_COST_DATA_STG
      WHERE ROOT_BOM_KEY = p_root_bom_key
        AND MISSING_BE IS NOT NULL;

     IF v_missing_be IS NOT NULL THEN
         -- Update hierarchy table with error message
         UPDATE BOM_ROLLUP_HIERARCHY_STG
            SET currency_conversion_error_msg =
                'Currency conversion missing for BE(s): ' || v_missing_be ||
                ' on ' || TO_CHAR(p_effective_date, 'YYYY-MM-DD')
          WHERE ROOT_BOM_KEY = p_root_bom_key;

	UPDATE BOM_ROLLUP_COST_LOG
 	SET STATUS = 1,
        END_TIME = v_end_time
    	WHERE  ROOT_BOM_KEY = p_root_bom_key AND STATUS = 0;
         -- Optional commit if safe in your context
         COMMIT;

         -- Raise error to stop main procedure
         RAISE_APPLICATION_ERROR(
             -20001,
             'Currency conversion missing for BE(s): ' || v_missing_be ||
             ' on ' || TO_CHAR(p_effective_date, 'YYYY-MM-DD')
         );
     END IF;
     END;
     -- Step 5: Pick and Update Cost details in to rollup table as per the rquirement

     -- Update Rule 1: Update from PRICE_ORDER = 1
     MERGE INTO BOM_ROLLUP_HIERARCHY_STG b
     USING (
         SELECT *
         FROM (
             SELECT
                 c.USER_KEY,
                 c.ROOT_BOM_KEY,
                 c.ITEM_PART_NAME,
                 c.COST_RECORD_KEY,
                 c.TOTAL_COST,
                 c.DIRECT_MATERIAL,
                 c.SHARING_COST,
                 c.DIRECT_LABOR,
                 c.DIRECT_LABOR2,
                 c.INDIRECT_LABOR,
                 c.MACHINE_EQUIPMENT,
                 c.MATERIAL_SCRAP,
                 c.FREIGHT,
                 c.SGA,
                 c.FINANCIAL_RECEIVABLES,
                 c.PROFIT_MARGIN,
                 c.ADJUSTMENTS_REDUCTIONS,
                 c.MISCELLANEOUS,
                 c.TARIFF,
                 c.PRICE_TYPE,
                 ROW_NUMBER() OVER (
                     PARTITION BY c.ROOT_BOM_KEY, c.ITEM_PART_NAME
                     ORDER BY c.TOTAL_COST
                 ) AS rn
             FROM BOM_ROLLUP_COST_DATA_STG c
             WHERE c.PRICE_ORDER = 1 AND c.ROOT_BOM_KEY = p_root_bom_key
         )
         WHERE rn = 1
     ) s
     ON (b.ROOT_BOM_KEY = s.ROOT_BOM_KEY AND b.ITEM_PART_NAME = s.ITEM_PART_NAME)
     WHEN MATCHED THEN UPDATE SET
         b.COST_RECORD_KEY = s.COST_RECORD_KEY,
         b.ITEM_PART_TOTAL_PRICE = s.TOTAL_COST,
         b.DIRECT_MATERIAL = s.DIRECT_MATERIAL,
         b.SHARING_COST = s.SHARING_COST,
         b.DIRECT_LABOR = s.DIRECT_LABOR,
         b.DIRECT_LABOR2 = s.DIRECT_LABOR2,
         b.INDIRECT_LABOR = s.INDIRECT_LABOR,
         b.MACHINE_EQUIPMENT = s.MACHINE_EQUIPMENT,
         b.MATERIAL_SCRAP = s.MATERIAL_SCRAP,
         b.FREIGHT = s.FREIGHT,
         b.SGA = s.SGA,
         b.FINANCIAL_RECEIVABLES = s.FINANCIAL_RECEIVABLES,
         b.PROFIT_MARGIN = s.PROFIT_MARGIN,
         b.ADJUSTMENTS_REDUCTIONS = s.ADJUSTMENTS_REDUCTIONS,
         b.MISCELLANEOUS = s.MISCELLANEOUS,
         b.TARIFF = s.TARIFF,
         b.PRICE_TYPE = s.PRICE_TYPE;
     -- Update Rule 2: Update from PRICE_ORDER = 2 + VA_COST from PRICE_ORDER = 3
     MERGE INTO BOM_ROLLUP_HIERARCHY_STG b
     USING (
         SELECT *
         FROM (
             SELECT
                 p2.USER_KEY,
                 p2.ROOT_BOM_KEY,
                 p2.ITEM_PART_NAME,
                 p2.COST_RECORD_KEY,
                 p2.TOTAL_COST,
                 p2.DIRECT_MATERIAL,
                 p2.SHARING_COST,
                 p2.DIRECT_LABOR,
                 p2.DIRECT_LABOR2,
                 p2.INDIRECT_LABOR,
                 p2.MACHINE_EQUIPMENT,
                 p2.MATERIAL_SCRAP,
                 p2.FREIGHT,
                 p2.SGA,
                 p2.FINANCIAL_RECEIVABLES,
                 p2.PROFIT_MARGIN,
                 p2.ADJUSTMENTS_REDUCTIONS,
                 p2.MISCELLANEOUS,
                 p2.TARIFF,
                 p2.PRICE_TYPE,
                 CASE
                     WHEN c3.BUSINESS_ENTITY_KEY != p2.BLI_BUSINESS_ENTITY_KEY THEN c3.VA_COST
                     ELSE 0
                 END AS VA_COST,
                 ROW_NUMBER() OVER (
                     PARTITION BY p2.ROOT_BOM_KEY, p2.ITEM_PART_NAME
                     ORDER BY (p2.TOTAL_COST +
                               CASE
                                   WHEN c3.BUSINESS_ENTITY_KEY != p2.BLI_BUSINESS_ENTITY_KEY THEN c3.VA_COST
                                   ELSE 0
                               END)
                 ) AS rn
             FROM BOM_ROLLUP_COST_DATA_STG p2
             JOIN BOM_ROLLUP_COST_DATA_STG c3
               ON c3.ROOT_BOM_KEY = p2.ROOT_BOM_KEY
              AND c3.ITEM_PART_NAME = p2.ITEM_PART_NAME
              AND c3.PRICE_ORDER = 3
              AND p2.PRICE_ORDER = 2
              AND c3.BUSINESS_ENTITY_KEY = p2.BUSINESS_ENTITY_KEY
             WHERE p2.PRICE_ORDER = 2 AND p2.ROOT_BOM_KEY = p_root_bom_key
               AND p2.ITEM_PART_NAME NOT IN (
                   SELECT ITEM_PART_NAME FROM BOM_ROLLUP_COST_DATA_STG WHERE PRICE_ORDER = 1
               )
         )
         WHERE rn = 1
     ) s
     ON (b.ROOT_BOM_KEY = s.ROOT_BOM_KEY AND b.ITEM_PART_NAME = s.ITEM_PART_NAME)
     WHEN MATCHED THEN UPDATE SET
         b.COST_RECORD_KEY = s.COST_RECORD_KEY,
         b.ITEM_PART_TOTAL_PRICE = s.TOTAL_COST,
         b.DIRECT_MATERIAL = s.DIRECT_MATERIAL,
         b.SHARING_COST = s.SHARING_COST,
         b.DIRECT_LABOR = s.DIRECT_LABOR,
         b.DIRECT_LABOR2 = s.DIRECT_LABOR2,
         b.INDIRECT_LABOR = s.INDIRECT_LABOR,
         b.MACHINE_EQUIPMENT = s.MACHINE_EQUIPMENT,
         b.MATERIAL_SCRAP = s.MATERIAL_SCRAP,
         b.FREIGHT = s.FREIGHT,
         b.SGA = s.SGA,
         b.FINANCIAL_RECEIVABLES = s.FINANCIAL_RECEIVABLES,
         b.PROFIT_MARGIN = s.PROFIT_MARGIN,
         b.ADJUSTMENTS_REDUCTIONS = s.ADJUSTMENTS_REDUCTIONS,
         b.MISCELLANEOUS = s.MISCELLANEOUS,
         b.TARIFF = s.TARIFF,
         b.PRICE_TYPE = s.PRICE_TYPE,
         b.VA_COST = NVL(s.VA_COST, b.VA_COST);

     -- Update Rule 3: Update VA_COST from PRICE_ORDER = 3 (only if no PO1 or PO2)
     MERGE INTO BOM_ROLLUP_HIERARCHY_STG b
     USING (
         SELECT *
         FROM (
             SELECT
                 USER_KEY,
                 ROOT_BOM_KEY,
                 ITEM_PART_NAME,
                 COST_RECORD_KEY,
                 VA_COST,
                 ROW_NUMBER() OVER (
                     PARTITION BY ROOT_BOM_KEY, ITEM_PART_NAME
                     ORDER BY VA_COST
                 ) AS rn
             FROM BOM_ROLLUP_COST_DATA_STG
             WHERE PRICE_ORDER = 3 AND ROOT_BOM_KEY = p_root_bom_key
               AND ITEM_PART_NAME NOT IN (
                   SELECT ITEM_PART_NAME FROM BOM_ROLLUP_COST_DATA_STG WHERE PRICE_ORDER = 1
                   UNION
                   SELECT ITEM_PART_NAME FROM BOM_ROLLUP_COST_DATA_STG WHERE PRICE_ORDER = 2
               )
         )
         WHERE rn = 1
     ) s
     ON (b.ROOT_BOM_KEY = s.ROOT_BOM_KEY AND b.ITEM_PART_NAME = s.ITEM_PART_NAME)
     WHEN MATCHED THEN UPDATE SET
         b.VA_COST = s.VA_COST,
         b.COST_RECORD_KEY = s.COST_RECORD_KEY;

     -- Step 6: Initialize rollup and selling prices

     -- Rule 1: For all BOM lines which don't have sub-BOMs
     UPDATE BOM_ROLLUP_HIERARCHY_STG tgt
     SET ITEM_PART_ROLLUP_PRICE = ITEM_PART_TOTAL_PRICE,
         ITEM_PART_SELLING_PRICE = ITEM_PART_TOTAL_PRICE + NVL(VA_COST, 0)
     WHERE tgt.ROOT_BOM_KEY = p_root_bom_key
       AND tgt.SUB_BOM_KEY IS NULL;

     UPDATE BOM_ROLLUP_HIERARCHY_STG tgt
     SET ITEM_ROLLUP_PRICE = (
         SELECT ITEM_PART_SELLING_PRICE
         FROM BOM_ROLLUP_HIERARCHY_STG src
         WHERE src.ITEM_PART_NAME = tgt.ITEM_NAME
           AND src.SUB_BOM_KEY IS NULL
           AND src.ROOT_BOM_KEY = tgt.ROOT_BOM_KEY
         FETCH FIRST 1 ROWS ONLY
     )
     WHERE tgt.ROOT_BOM_KEY = p_root_bom_key
       AND EXISTS (
           SELECT 1
           FROM BOM_ROLLUP_HIERARCHY_STG src
           WHERE src.ITEM_PART_NAME = tgt.ITEM_NAME
             AND src.SUB_BOM_KEY IS NULL
             AND src.ROOT_BOM_KEY = tgt.ROOT_BOM_KEY
       );

     -- Rule 2: For intermediate BOMs with rollup of cost elements
     DECLARE
         CURSOR c_sub_boms IS
             SELECT ITEM_PART_NAME, ROOT_BOM_KEY
             FROM BOM_ROLLUP_HIERARCHY_STG
             WHERE ROOT_BOM_KEY = p_root_bom_key AND SUB_BOM_KEY IS NOT NULL
             ORDER BY BOM_LEVEL DESC;

         v_rollup_price  NUMBER;
         v_selling_price NUMBER;
         v_va_cost       NUMBER;

         -- Cost element variables
         v_item_part_total_cost    NUMBER;
         v_direct_material         NUMBER;
         v_sharing_cost            NUMBER;
         v_direct_labor            NUMBER;
         v_direct_labor2           NUMBER;
         v_indirect_labor          NUMBER;
         v_machine_equipment       NUMBER;
         v_material_handling       NUMBER;
         v_material_scrap          NUMBER;
         v_freight                 NUMBER;
         v_sga                     NUMBER;
         v_financial_receivables   NUMBER;
         v_profit_margin           NUMBER;
         v_adjustments_reductions  NUMBER;
         v_miscellaneous           NUMBER;
         v_tariff                  NUMBER;

     BEGIN
         FOR rec IN c_sub_boms LOOP
             -- Rollup price
             SELECT SUM(ITEM_PART_SELLING_PRICE * ITEM_PART_QTY)
             INTO v_rollup_price
             FROM BOM_ROLLUP_HIERARCHY_STG
             WHERE ITEM_NAME = rec.ITEM_PART_NAME
               AND ROOT_BOM_KEY = rec.ROOT_BOM_KEY;

             -- VA cost
             SELECT DISTINCT NVL(VA_COST, 0)
             INTO v_va_cost
             FROM BOM_ROLLUP_HIERARCHY_STG
             WHERE ITEM_PART_NAME = rec.ITEM_PART_NAME
               AND ROOT_BOM_KEY = rec.ROOT_BOM_KEY
               AND ROWNUM = 1;

             v_selling_price := v_rollup_price + v_va_cost;

             -- Rollup each cost element
             SELECT
                 SUM(ITEM_PART_TOTAL_PRICE * ITEM_PART_QTY),
                 SUM(DIRECT_MATERIAL * ITEM_PART_QTY),
                 SUM(SHARING_COST * ITEM_PART_QTY),
                 SUM(DIRECT_LABOR * ITEM_PART_QTY),
                 SUM(DIRECT_LABOR2 * ITEM_PART_QTY),
                 SUM(INDIRECT_LABOR * ITEM_PART_QTY),
                 SUM(MACHINE_EQUIPMENT * ITEM_PART_QTY),
                 SUM(MATERIAL_HANDLING * ITEM_PART_QTY),
                 SUM(MATERIAL_SCRAP * ITEM_PART_QTY),
                 SUM(FREIGHT * ITEM_PART_QTY),
                 SUM(SGA * ITEM_PART_QTY),
                 SUM(FINANCIAL_RECEIVABLES * ITEM_PART_QTY),
                 SUM(PROFIT_MARGIN * ITEM_PART_QTY),
                 SUM(ADJUSTMENTS_REDUCTIONS * ITEM_PART_QTY),
                 SUM(MISCELLANEOUS * ITEM_PART_QTY),
                 SUM(TARIFF * ITEM_PART_QTY)
             INTO
                 v_item_part_total_cost,
                 v_direct_material,
                 v_sharing_cost,
                 v_direct_labor,
                 v_direct_labor2,
                 v_indirect_labor,
                 v_machine_equipment,
                 v_material_handling,
                 v_material_scrap,
                 v_freight,
                 v_sga,
                 v_financial_receivables,
                 v_profit_margin,
                 v_adjustments_reductions,
                 v_miscellaneous,
                 v_tariff
             FROM BOM_ROLLUP_HIERARCHY_STG
             WHERE ITEM_NAME = rec.ITEM_PART_NAME
               AND ROOT_BOM_KEY = rec.ROOT_BOM_KEY;

             -- Update rollup values with conditional cost updates
             UPDATE BOM_ROLLUP_HIERARCHY_STG
             SET ITEM_PART_ROLLUP_PRICE = v_rollup_price,
                 ITEM_PART_SELLING_PRICE = v_selling_price,
                 ITEM_PART_TOTAL_PRICE = CASE WHEN ITEM_PART_TOTAL_PRICE IS NULL THEN v_item_part_total_cost ELSE ITEM_PART_TOTAL_PRICE END,
                 DIRECT_MATERIAL = CASE WHEN DIRECT_MATERIAL IS NULL THEN v_direct_material ELSE DIRECT_MATERIAL END,
                 SHARING_COST = CASE WHEN SHARING_COST IS NULL THEN v_sharing_cost ELSE SHARING_COST END,
                 DIRECT_LABOR = CASE WHEN DIRECT_LABOR IS NULL THEN v_direct_labor ELSE DIRECT_LABOR END,
                 DIRECT_LABOR2 = CASE WHEN DIRECT_LABOR2 IS NULL THEN v_direct_labor2 ELSE DIRECT_LABOR2 END,
                 INDIRECT_LABOR = CASE WHEN INDIRECT_LABOR IS NULL THEN v_indirect_labor ELSE INDIRECT_LABOR END,
                 MACHINE_EQUIPMENT = CASE WHEN MACHINE_EQUIPMENT IS NULL THEN v_machine_equipment ELSE MACHINE_EQUIPMENT END,
                 MATERIAL_HANDLING = CASE WHEN MATERIAL_HANDLING IS NULL THEN v_material_handling ELSE MATERIAL_HANDLING END,
                 MATERIAL_SCRAP = CASE WHEN MATERIAL_SCRAP IS NULL THEN v_material_scrap ELSE MATERIAL_SCRAP END,
                 FREIGHT = CASE WHEN FREIGHT IS NULL THEN v_freight ELSE FREIGHT END,
                 SGA = CASE WHEN SGA IS NULL THEN v_sga ELSE SGA END,
                 FINANCIAL_RECEIVABLES = CASE WHEN FINANCIAL_RECEIVABLES IS NULL THEN v_financial_receivables ELSE FINANCIAL_RECEIVABLES END,
                 PROFIT_MARGIN = CASE WHEN PROFIT_MARGIN IS NULL THEN v_profit_margin ELSE PROFIT_MARGIN END,
                 ADJUSTMENTS_REDUCTIONS = CASE WHEN ADJUSTMENTS_REDUCTIONS IS NULL THEN v_adjustments_reductions ELSE ADJUSTMENTS_REDUCTIONS END,
                 MISCELLANEOUS = CASE WHEN MISCELLANEOUS IS NULL THEN v_miscellaneous ELSE MISCELLANEOUS END,
                 TARIFF = CASE WHEN TARIFF IS NULL THEN v_tariff ELSE TARIFF END
             WHERE ITEM_PART_NAME = rec.ITEM_PART_NAME
               AND ROOT_BOM_KEY = rec.ROOT_BOM_KEY;

             -- Update ITEM_ROLLUP_PRICE
             UPDATE BOM_ROLLUP_HIERARCHY_STG
             SET ITEM_ROLLUP_PRICE = v_selling_price,
             ITEM_VA_COST = v_va_cost
             WHERE ITEM_NAME = rec.ITEM_PART_NAME
               AND ROOT_BOM_KEY = rec.ROOT_BOM_KEY;
         END LOOP;
     END;

     -- Rule 3: Final roll-up at top-level product
     UPDATE BOM_ROLLUP_HIERARCHY_STG tgt
     SET ITEM_ROLLUP_PRICE = (
         SELECT SUM(src.ITEM_PART_SELLING_PRICE * src.ITEM_PART_QTY)
         FROM BOM_ROLLUP_HIERARCHY_STG src
         WHERE src.ITEM_NAME = tgt.ITEM_NAME
           AND src.ROOT_BOM_KEY = tgt.ROOT_BOM_KEY
     )
     WHERE tgt.ROOT_BOM_KEY = p_root_bom_key
       AND tgt.BOM_LEVEL = 1;


     -- Step 7 Now update the minimum ITEM_VA_COST for the top level bom item
     UPDATE BOM_ROLLUP_HIERARCHY_STG bht
     SET ITEM_VA_COST =
       (SELECT MIN(pvt.VA_COST)
       FROM
         (SELECT tmp.ROOT_BOM_KEY,
           tmp.USER_KEY,
           tmp.BOM_ITEM_KEY,
           sl.ITEM_KEY,
           im_bom.BUSINESS_ENTITY_KEY AS BI_BUSINESS_ENTITY_KEY,
           im.BUSINESS_ENTITY_KEY,
           CASE
             WHEN im.BUSINESS_ENTITY_KEY = sl.SUPPLIER_KEY
             THEN 2
             WHEN sl.SUPPLIER_KEY IS NULL
             THEN 3
             ELSE 1
           END AS PRICE_ORDER,
           crv.COST_ELEMENT_KEY,
           crv.COST_VALUE
         FROM BOM_ROLLUP_HIERARCHY_STG tmp
         JOIN ITEM_MASTER im
         ON tmp.ITEM_NAME     = im.ITEM_IDENTIFIER
         AND tmp.ROOT_BOM_KEY = bht.ROOT_BOM_KEY
         AND tmp.BOM_LEVEL    = 1
         JOIN ITEM_MASTER im_bom
         ON tmp.BOM_ITEM_KEY = im_bom.ITEM_KEY
         JOIN PCM_SOURCING_LANE sl
         ON im.ITEM_KEY = sl.ITEM_KEY
         JOIN PCM_COST_RECORD cr
         ON sl.SOURCING_LANE_KEY = cr.SOURCING_LANE_KEY
         JOIN PCM_COST_RECORD_RANGE crr
         ON cr.COST_RECORD_KEY = crr.COST_RECORD_KEY
         --AND crr.IS_ACTIVE     = 1
         JOIN PCM_COST_RECORD_VALUE crv
         ON crr.COST_RECORD_RANGE_KEY                    = crv.COST_RECORD_RANGE_KEY
         WHERE cr.EFFECTIVE_FROM_DT                     <= TRUNC(p_effective_date)
         AND (cr.EFFECTIVE_TO_DT                        IS NULL
         OR cr.EFFECTIVE_TO_DT                          >= TRUNC(p_effective_date))
         AND cr.STATUS                                   = 'APPROVED'
         ) PIVOT ( MAX(COST_VALUE) FOR COST_ELEMENT_KEY IN ('VA_COST' AS VA_COST) ) pvt
       WHERE pvt.ROOT_BOM_KEY                            = bht.ROOT_BOM_KEY
       AND pvt.BOM_ITEM_KEY                              = bht.BOM_ITEM_KEY
       AND pvt.PRICE_ORDER                               = 3
       )
     WHERE bht.ROOT_BOM_KEY = p_root_bom_key
     AND bht.BOM_LEVEL      = 1;

     UPDATE BOM_ROLLUP_COST_LOG
 SET STATUS = 1,
        END_TIME = v_end_time
     WHERE BOM_ROLLUP_LOG_ID = v_log_id AND STATUS = 0;
 END;
/
create or replace TRIGGER T_COST_ROLLUP_UPDATE
FOR INSERT OR UPDATE ON COST_ROLLUP_CHANGE
COMPOUND TRIGGER

  -- Temporary collection to store new/updated rows
  TYPE t_row IS TABLE OF COST_ROLLUP_CHANGE%ROWTYPE;
  g_rows t_row := t_row();

  -- Before each row: capture the new row
  BEFORE EACH ROW IS
  BEGIN
    g_rows.EXTEND;
    g_rows(g_rows.LAST).BOM_KEY := :NEW.BOM_KEY;
    g_rows(g_rows.LAST).USER_KEY := :NEW.USER_KEY;
    g_rows(g_rows.LAST).EFFECTIVE_DATE := :NEW.EFFECTIVE_DATE;
  END BEFORE EACH ROW;

  -- After statement: process all collected rows
  AFTER STATEMENT IS
  BEGIN
    FOR i IN 1 .. g_rows.COUNT LOOP
      -- Call  procedure
      GET_BOM_HIERARCHY_WITH_COST(
        g_rows(i).BOM_KEY,
        g_rows(i).USER_KEY,
        g_rows(i).EFFECTIVE_DATE
      );

      -- Delete the processed row
     DELETE FROM COST_ROLLUP_CHANGE
     WHERE BOM_KEY = g_rows(i).BOM_KEY
     AND USER_KEY = g_rows(i).USER_KEY
     AND EFFECTIVE_DATE = g_rows(i).EFFECTIVE_DATE;
    END LOOP;
  END AFTER STATEMENT;

END T_COST_ROLLUP_UPDATE;

-------------------------------------------------------------------------------------------
-- Compares the two boms hierarchy/structure is same or not for the inputs supplied based on
-- fields LEVEL_NO, ITEM_IDENTIFIER, ITEM_QUANTITY, VERSION. Assuming EFFECTIVE_FROM_DT,
-- EFFECTIVE_TO_DT are already compared before calling the procedure.
-- This procedure will return 'Y' if both hierarchies are same else 'N'.
--------------------------------------------------------------------------------------------
CREATE OR REPLACE PROCEDURE COMPARE_BOM_HIERARCHY (
  p_bom_key1          IN  NUMBER,
  p_bom_key2          IN  NUMBER,
  p_set_equal         OUT VARCHAR2  -- 'Y' if sets are equal.

) AS
  v_cnt1        NUMBER;
  v_cnt2        NUMBER;
  v_diff12      NUMBER;
  v_diff21      NUMBER;

BEGIN
  ----------------------------------------------------------------------
  -- Row counts for each hierarchy
  ----------------------------------------------------------------------
  WITH BOM_HIERARCHY (
    LEVEL_NO, PARENT_BOM_KEY, BOM_KEY, SUB_BOM_KEY, ITEM_KEY, ITEM_IDENTIFIER, ITEM_QUANTITY, EFFECTIVE_FROM_DT, EFFECTIVE_TO_DT, VERSION, BOM_PATH
  ) AS (
    SELECT
      1 AS LEVEL_NO,
      NULL AS PARENT_BOM_KEY,
      bh.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      TO_CHAR(bh.BOM_KEY) AS BOM_PATH
    FROM BOM_HEADER bh
    LEFT JOIN BOM_LINE_ITEM bli ON bh.BOM_KEY = bli.BOM_KEY
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    WHERE bh.BOM_KEY = p_bom_key1
    UNION ALL
    SELECT
      bh.LEVEL_NO + 1,
      bh.BOM_KEY AS PARENT_BOM_KEY,
      bli.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      bh.BOM_PATH || '>' || TO_CHAR(bli.BOM_KEY)
    FROM BOM_LINE_ITEM bli
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    JOIN BOM_HIERARCHY bh ON bli.BOM_KEY = bh.SUB_BOM_KEY
  )
  SELECT COUNT(*) INTO v_cnt1 FROM BOM_HIERARCHY;

  WITH BOM_HIERARCHY (
    LEVEL_NO, PARENT_BOM_KEY, BOM_KEY, SUB_BOM_KEY, ITEM_KEY, ITEM_IDENTIFIER, ITEM_QUANTITY, EFFECTIVE_FROM_DT, EFFECTIVE_TO_DT, VERSION, BOM_PATH
  ) AS (
    SELECT
      1 AS LEVEL_NO,
      NULL AS PARENT_BOM_KEY,
      bh.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      TO_CHAR(bh.BOM_KEY) AS BOM_PATH
    FROM BOM_HEADER bh
    LEFT JOIN BOM_LINE_ITEM bli ON bh.BOM_KEY = bli.BOM_KEY
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    WHERE bh.BOM_KEY = p_bom_key2
    UNION ALL
    SELECT
      bh.LEVEL_NO + 1,
      bh.BOM_KEY AS PARENT_BOM_KEY,
      bli.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      bh.BOM_PATH || '>' || TO_CHAR(bli.BOM_KEY)
    FROM BOM_LINE_ITEM bli
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    JOIN BOM_HIERARCHY bh ON bli.BOM_KEY = bh.SUB_BOM_KEY
  )
  SELECT COUNT(*) INTO v_cnt2 FROM BOM_HIERARCHY;

  ----------------------------------------------------------------------
  -- If count does  not match then the boms are not in equal hierarchy
  ----------------------------------------------------------------------
  IF v_cnt1 <> v_cnt2 THEN
    p_set_equal := 'N';
    --DBMS_OUTPUT.PUT_LINE('BOM_KEY1 = ' || p_bom_key1 || ', rows = ' || v_cnt1);
    --DBMS_OUTPUT.PUT_LINE('BOM_KEY2 = ' || p_bom_key2 || ', rows = ' || v_cnt2);
    --DBMS_OUTPUT.PUT_LINE('Set-equality: ' || p_set_equal || ' (counts differ)');
    RETURN; -- stop executing further comparisons
  END IF;

  -------------------------------------------------------------------------------------
  -- Set equality check. If both MINUS directions return zero rows, the sets are equal.
  -------------------------------------------------------------------------------------
  WITH
  BOM_HIERARCHY1 (LEVEL_NO, PARENT_BOM_KEY, BOM_KEY, SUB_BOM_KEY, ITEM_KEY, ITEM_IDENTIFIER, ITEM_QUANTITY, EFFECTIVE_FROM_DT, EFFECTIVE_TO_DT, VERSION, BOM_PATH) AS (
    SELECT
      1 AS LEVEL_NO,
      NULL AS PARENT_BOM_KEY,
      bh.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      TO_CHAR(bh.BOM_KEY) AS BOM_PATH
    FROM BOM_HEADER bh
    LEFT JOIN BOM_LINE_ITEM bli ON bh.BOM_KEY = bli.BOM_KEY
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    WHERE bh.BOM_KEY = p_bom_key1
    UNION ALL
    SELECT
      bh.LEVEL_NO + 1,
      bh.BOM_KEY AS PARENT_BOM_KEY,
      bli.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      bh.BOM_PATH || '>' || TO_CHAR(bli.BOM_KEY)
    FROM BOM_LINE_ITEM bli
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    JOIN BOM_HIERARCHY1 bh ON bli.BOM_KEY = bh.SUB_BOM_KEY
  ),
  BOM_HIERARCHY2 (LEVEL_NO, PARENT_BOM_KEY, BOM_KEY, SUB_BOM_KEY, ITEM_KEY, ITEM_IDENTIFIER, ITEM_QUANTITY, EFFECTIVE_FROM_DT, EFFECTIVE_TO_DT, VERSION, BOM_PATH) AS (
    SELECT
      1 AS LEVEL_NO,
      NULL AS PARENT_BOM_KEY,
      bh.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      TO_CHAR(bh.BOM_KEY) AS BOM_PATH
    FROM BOM_HEADER bh
    LEFT JOIN BOM_LINE_ITEM bli ON bh.BOM_KEY = bli.BOM_KEY
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    WHERE bh.BOM_KEY = p_bom_key2
    UNION ALL
    SELECT
      bh.LEVEL_NO + 1,
      bh.BOM_KEY AS PARENT_BOM_KEY,
      bli.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      bh.BOM_PATH || '>' || TO_CHAR(bli.BOM_KEY)
    FROM BOM_LINE_ITEM bli
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    JOIN BOM_HIERARCHY2 bh ON bli.BOM_KEY = bh.SUB_BOM_KEY
  )
  SELECT COUNT(*) INTO v_diff12
  FROM (
    SELECT LEVEL_NO, ITEM_IDENTIFIER, ITEM_QUANTITY, VERSION
    FROM BOM_HIERARCHY1
    MINUS
    SELECT LEVEL_NO, ITEM_IDENTIFIER, ITEM_QUANTITY, VERSION
    FROM BOM_HIERARCHY2
  );

  WITH
  BOM_HIERARCHY1 (LEVEL_NO, PARENT_BOM_KEY, BOM_KEY, SUB_BOM_KEY, ITEM_KEY, ITEM_IDENTIFIER, ITEM_QUANTITY, EFFECTIVE_FROM_DT, EFFECTIVE_TO_DT, VERSION, BOM_PATH) AS (
    SELECT
      1 AS LEVEL_NO,
      NULL AS PARENT_BOM_KEY,
      bh.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      TO_CHAR(bh.BOM_KEY) AS BOM_PATH
    FROM BOM_HEADER bh
    LEFT JOIN BOM_LINE_ITEM bli ON bh.BOM_KEY = bli.BOM_KEY
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    WHERE bh.BOM_KEY = p_bom_key1
    UNION ALL
    SELECT
      bh.LEVEL_NO + 1,
      bh.BOM_KEY AS PARENT_BOM_KEY,
      bli.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      bh.BOM_PATH || '>' || TO_CHAR(bli.BOM_KEY)
    FROM BOM_LINE_ITEM bli
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    JOIN BOM_HIERARCHY1 bh ON bli.BOM_KEY = bh.SUB_BOM_KEY
  ),
  BOM_HIERARCHY2 (LEVEL_NO, PARENT_BOM_KEY, BOM_KEY, SUB_BOM_KEY, ITEM_KEY, ITEM_IDENTIFIER, ITEM_QUANTITY, EFFECTIVE_FROM_DT, EFFECTIVE_TO_DT, VERSION, BOM_PATH) AS (
    SELECT
      1 AS LEVEL_NO,
      NULL AS PARENT_BOM_KEY,
      bh.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      TO_CHAR(bh.BOM_KEY) AS BOM_PATH
    FROM BOM_HEADER bh
    LEFT JOIN BOM_LINE_ITEM bli ON bh.BOM_KEY = bli.BOM_KEY
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    WHERE bh.BOM_KEY = p_bom_key2
    UNION ALL
    SELECT
      bh.LEVEL_NO + 1,
      bh.BOM_KEY AS PARENT_BOM_KEY,
      bli.BOM_KEY,
      bli.SUB_BOM_KEY,
      bli.ITEM_KEY,
      im.ITEM_IDENTIFIER,
      bli.ITEM_QUANTITY,
      bli.EFFECTIVE_FROM_DT,
      bli.EFFECTIVE_TO_DT,
      bh.VERSION,
      bh.BOM_PATH || '>' || TO_CHAR(bli.BOM_KEY)
    FROM BOM_LINE_ITEM bli
    LEFT JOIN ITEM_MASTER im ON im.ITEM_KEY = bli.ITEM_KEY
    JOIN BOM_HIERARCHY2 bh ON bli.BOM_KEY = bh.SUB_BOM_KEY
  )
  SELECT COUNT(*) INTO v_diff21
  FROM (
    SELECT LEVEL_NO, ITEM_IDENTIFIER, ITEM_QUANTITY, VERSION
    FROM BOM_HIERARCHY2
    MINUS
    SELECT LEVEL_NO, ITEM_IDENTIFIER, ITEM_QUANTITY, VERSION
    FROM BOM_HIERARCHY1
  );

  p_set_equal := CASE WHEN v_diff12 = 0 AND v_diff21 = 0 THEN 'Y' ELSE 'N' END;

  ----------------------------------------------------------------------
  -- Summary output
  ----------------------------------------------------------------------
  --DBMS_OUTPUT.PUT_LINE('BOM_KEY1 = ' || p_bom_key1 || ', rows = ' || v_cnt1);
  --DBMS_OUTPUT.PUT_LINE('BOM_KEY2 = ' || p_bom_key2 || ', rows = ' || v_cnt2);
  --DBMS_OUTPUT.PUT_LINE('Set-equality: ' || p_set_equal || ' (diff12=' || v_diff12 || ', diff21=' || v_diff21 || ')');

EXCEPTION
  WHEN OTHERS THEN
    --DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
    p_set_equal := 'N';

END COMPARE_BOM_HIERARCHY;

CREATE UNIQUE INDEX XBMHDR_U1
ON BOM_HEADER(
    ITEM_KEY ASC
    ,VERSION ASC
    ,REVISION ASC
    ,DATA_SOURCE ASC
    ,BUSINESS_ENTITY_KEY ASC
    ,CONTEXT_OBJECT_TYPE
    ,CONTEXT_OBJECT_ID
    ,EFFECTIVE_FROM_DT
    ,EFFECTIVE_TO_DT
) TABLESPACE AUDIT_INDEX;


INSERT INTO PCM_WORKFLOW (WORKFLOW_KEY, WORKFLOW_NAME, WORKFLOW_GROUP, WORKFLOW_PARENT_KEY, WORKFLOW_URL,DISPLAY_ORDER) VALUES ('CURRENCY', 'Currency', 'MCM', 'ADMIN', 'searchCurrency.do',13);

CREATE SEQUENCE CURRENCY_SEQ
        INCREMENT BY 1
        START WITH 1
        NOMAXVALUE
        NOMINVALUE
        CACHE 20
        NOCYCLE ORDER;

CREATE TABLE currency (
    id                    NUMBER(19),
    start_date            TIMESTAMP,
    end_date              TIMESTAMP,
    business_entity_key   NUMBER(19),
    from_currency         VARCHAR2(255),
    to_currency           VARCHAR2(255),
    conversion_rate       NUMBER(18,10),
    CONSTRAINT pk_currency  PRIMARY KEY (id),
    CONSTRAINT uq_currency_business
        UNIQUE (
            start_date,
            end_date,
            business_entity_key,
            from_currency,
            to_currency
        )
);

