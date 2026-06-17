INSERT INTO PCM_USER (USER_KEY, USER_ID, USER_NAME, BUSINESS_ENTITY_KEY, CONTACT_KEY, EMAIL_ADDRESS , FOREIGN_ID, ROLE_KEY, ENABLED_FLAG) 
VALUES (pcm_user_seq.nextval, 'BATCH-CFG-OPP','BATCH-CFG-OPP', 11, NULL, NULL, NULL, 1, 'Y');
ALTER TABLE SITE_DETAILS ADD COST_VISIBLE_FLAG VARCHAR2(1);
ALTER TABLE SITE_DETAILS ADD TAM_UPDATE_FLAG VARCHAR2(1);
ALTER TABLE SITE_DETAILS ADD COST_UPDATE_FLAG VARCHAR2(1);
ALTER TABLE SITE_DETAILS ADD DISCP_SITE_DESCRIPTION VARCHAR2(255);
ALTER TABLE SITE_DETAILS ADD MRP_SITE_LEGACY VARCHAR2(255);
ALTER TABLE SITE_DETAILS ADD SITE_PURPOSE VARCHAR2(255);

ALTER TABLE FUNCTIONAL_GROUP ADD FG_STATUS VARCHAR2(255);
ALTER TABLE FUNCTIONAL_GROUP ADD ALIAS_NAME VARCHAR2(255);

--SCPlatform-9174 Add additional attributes in Item AVL 
CREATE TABLE ITEM_AVL_ADD_ATTRIBUTE
(
    ATTRIBUTE_KEY NUMBER(19) NOT NULL,
    AVL_KEY NUMBER(19) NOT NULL,
    ATTRIBUTE_NAME VARCHAR2(255) NOT NULL,
    ATTRIBUTE_TYPE VARCHAR2(255) NULL,
    ATTRIBUTE_VALUE VARCHAR2(255) NULL,
    ATTRIBUTE_GROUP_KEY NUMBER(19) NULL,
    DESCRIPTION VARCHAR2(1024) NULL,
    ATTRIBUTE_VALUE_NUM NUMBER(19, 4),
    ATTRIBUTE_VALUE_DT TIMESTAMP(6),
    CONSTRAINT XITAAA_PK PRIMARY KEY (ATTRIBUTE_KEY)
        USING INDEX TABLESPACE AUDIT_INDEX
) TABLESPACE AUDIT_DATA;

ALTER TABLE ITEM_AVL_ADD_ATTRIBUTE ADD 
    CONSTRAINT ITAAA_R01_ATRGRP 
    FOREIGN KEY (ATTRIBUTE_GROUP_KEY) REFERENCES ATTRIBUTE_GROUP;

ALTER TABLE ITEM_AVL_ADD_ATTRIBUTE ADD    
    CONSTRAINT ITAAA_R02_AVLMTR 
    FOREIGN KEY (AVL_KEY) REFERENCES ITEM_AVL ON DELETE CASCADE;
    
CREATE INDEX ITEM_AVL_ATTR_KEY ON ITEM_AVL_ADD_ATTRIBUTE(AVL_KEY) TABLESPACE AUDIT_INDEX;

CREATE UNIQUE INDEX XITAAA_U1
ON ITEM_AVL_ADD_ATTRIBUTE(
 AVL_KEY ASC
,ATTRIBUTE_NAME ASC
,ATTRIBUTE_TYPE ASC
,ATTRIBUTE_VALUE ASC
,ATTRIBUTE_GROUP_KEY ASC
) TABLESPACE AUDIT_INDEX;

-- Sice this VA Cost element is optional for customers and not required to insert, So added as a commented script for reference
-- INSERT INTO PCM_COST_ELEMENT (COST_ELEMENT_KEY, COST_TYPE_KEY, COST_ELEMENT_NAME, COST_ELEMENT_VALUE_TYPE, COST_ELEMENT_ORDER,COST_ELEMENT_TYPE, IS_REQUIRED) 
--	VALUES ('VA_COST','COMPONENT','VA Cost','PM',4,'TRANSFORMATION',NULL);
--Insert into PCM_WORKFLOW (WORKFLOW_KEY,WORKFLOW_NAME,WORKFLOW_GROUP,WORKFLOW_PARENT_KEY,WORKFLOW_URL,DISPLAY_ORDER,WORKFLOW_URL_TARGET) values ('BOM_COST_ROLLUP','BOM cost rollup','MCM','SL','startBomCostRollupManagement.do',5,null);
--Insert into PCM_ACCESS_CONTROL (ACCESS_CONTROL_KEY,ENTITY_TYPE,ROLE_KEY,USER_KEY,TARGET_ENTITY_KEY,ACL) values (2960,'WORKFLOW',1,null,'BOM_COST_ROLLUP','Execute');


-- Create permanent table for BOM_HIERARCHY
CREATE TABLE BOM_ROLLUP_HIERARCHY_STG (
    USER_KEY                NUMBER,
    ROOT_BOM_KEY            NUMBER,
    BOM_KEY                 NUMBER,
    BOM_LEVEL               NUMBER,
    BOM_ITEM_KEY            NUMBER,
    BOM_LINE_KEY            NUMBER,
    BLI_ITEM_KEY            NUMBER,
    SUB_BOM_KEY             NUMBER,
    ITEM_NAME               VARCHAR2(255),
    COST_RECORD_KEY         NUMBER,
    ITEM_ROLLUP_PRICE       NUMBER,
    ITEM_VA_COST            NUMBER,
    ITEM_PART_NAME          VARCHAR2(255),
    ITEM_PART_TOTAL_PRICE   NUMBER,
    DIRECT_MATERIAL         NUMBER,
    SHARING_COST            NUMBER,
    DIRECT_LABOR            NUMBER,
    VA_COST                 NUMBER,
    DIRECT_LABOR2           NUMBER,
    INDIRECT_LABOR          NUMBER,
    MACHINE_EQUIPMENT       NUMBER,
    MATERIAL_HANDLING       NUMBER,
    MATERIAL_SCRAP          NUMBER,
    FREIGHT                 NUMBER,
    SGA                     NUMBER,
    FINANCIAL_RECEIVABLES   NUMBER,
    PROFIT_MARGIN           NUMBER,
    ADJUSTMENTS_REDUCTIONS  NUMBER,
    MISCELLANEOUS           NUMBER,
    TARIFF                  NUMBER,
    ITEM_PART_ROLLUP_PRICE  NUMBER,
    ITEM_PART_QTY           NUMBER,
    ITEM_PART_SELLING_PRICE NUMBER,
    CREATED_ON              TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create permanent table for COST_DATA
CREATE TABLE BOM_ROLLUP_COST_DATA_STG (
    USER_KEY                NUMBER,
    ROOT_BOM_KEY            NUMBER,
    ITEM_PART_NAME          VARCHAR2(255),
    BLI_ITEM_KEY            NUMBER,
    SL_ITEM_KEY             NUMBER,
    BLI_BUSINESS_ENTITY_KEY NUMBER,
    BUSINESS_ENTITY_KEY     NUMBER,
    SOURCING_LANE_KEY       NUMBER,
    COST_RECORD_KEY         NUMBER,
    SUPPLIER_KEY            NUMBER,
    PRICE_TYPE              VARCHAR2(55),
    PRICE_ORDER             NUMBER,
    TOTAL_COST              NUMBER,
    DIRECT_MATERIAL         NUMBER,
    SHARING_COST            NUMBER,
    DIRECT_LABOR            NUMBER,
    VA_COST                 NUMBER,
    DIRECT_LABOR2           NUMBER,
    INDIRECT_LABOR          NUMBER,
    MACHINE_EQUIPMENT       NUMBER,
    MATERIAL_HANDLING       NUMBER,
    MATERIAL_SCRAP          NUMBER,
    FREIGHT                 NUMBER,
    SGA                     NUMBER,
    FINANCIAL_RECEIVABLES   NUMBER,
    PROFIT_MARGIN           NUMBER,
    ADJUSTMENTS_REDUCTIONS  NUMBER,
    MISCELLANEOUS           NUMBER,
    TARIFF                  NUMBER
);

CREATE TABLE BOM_ROLLUP_COST_LOG (
    BOM_ROLLUP_LOG_ID NUMBER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY, -- Unique identifier
    ROOT_BOM_KEY NUMBER NOT NULL,
    EFFECTIVE_DATE DATE NOT NULL,
    STATUS NUMBER(1) DEFAULT 0 CHECK (STATUS IN (0, 1, -1, 2)), -- 0: In Progress, 1: Success, -1: Failure, 2: Re-run
    CREATED_ON TIMESTAMP DEFAULT SYSTIMESTAMP NOT NULL,
    CREATED_BY NUMBER NOT NULL,
    START_TIME TIMESTAMP,
    END_TIME TIMESTAMP,
    DUPLICATE_REQUEST_BY NUMBER,
    DUPLICATE_REQUEST_ON TIMESTAMP,
    DUPLICATE_REQUEST_COUNT NUMBER DEFAULT 0
);

create or replace PROCEDURE GET_BOM_HIERARCHY_WITH_COST(
    p_root_bom_key     IN NUMBER,
    p_user_key         IN NUMBER,
    p_effective_date   IN DATE,
    p_resultset         OUT SYS_REFCURSOR
)
IS
    v_level NUMBER := 1;
    v_count NUMBER := 0;
    v_log_id NUMBER;
    v_status NUMBER;
    v_start_time TIMESTAMP := SYSTIMESTAMP;
    v_end_time TIMESTAMP;
    v_error_msg VARCHAR2(4000);
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
        PRICE_ORDER, TOTAL_COST, DIRECT_MATERIAL, SHARING_COST, DIRECT_LABOR, VA_COST, DIRECT_LABOR2, INDIRECT_LABOR, MACHINE_EQUIPMENT, MATERIAL_HANDLING, MATERIAL_SCRAP, FREIGHT,
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
        crv.COST_VALUE
      FROM BOM_ROLLUP_HIERARCHY_STG tmp
      JOIN ITEM_MASTER im
        ON tmp.ITEM_PART_NAME = im.ITEM_IDENTIFIER
       AND tmp.ROOT_BOM_KEY = p_root_bom_key
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

    -- Step 8 Now get the BOM_ROLLUP_PRICE details into json for the given BOM_KEY
    OPEN p_resultset FOR
        SELECT ITEM_NAME, ITEM_ROLLUP_PRICE,ITEM_PART_NAME, ITEM_PART_SELLING_PRICE,
					ITEM_PART_ROLLUP_PRICE, ITEM_PART_TOTAL_PRICE, ITEM_PART_QTY,
					DIRECT_MATERIAL, SHARING_COST, DIRECT_LABOR,ITEM_VA_COST, DIRECT_LABOR2,
					INDIRECT_LABOR, MACHINE_EQUIPMENT, MATERIAL_HANDLING, MATERIAL_SCRAP,
					FREIGHT, SGA, FINANCIAL_RECEIVABLES, PROFIT_MARGIN,
					ADJUSTMENTS_REDUCTIONS, MISCELLANEOUS, TARIFF,PRICE_TYPE,BOM_LEVEL
					FROM BOM_ROLLUP_HIERARCHY_STG
         WHERE ROOT_BOM_KEY = p_root_bom_key and BOM_KEY = p_root_bom_key
         ORDER BY ROOT_BOM_KEY, BOM_LEVEL, ITEM_NAME;

    UPDATE BOM_ROLLUP_COST_LOG
    SET STATUS = 1,
        END_TIME = v_end_time
    WHERE BOM_ROLLUP_LOG_ID = v_log_id AND STATUS = 0;
END;
/
ALTER TABLE BOM_HEADER ADD ROLLUP_FLAG VARCHAR2(1) DEFAULT 'Y' NOT NULL;
ALTER TABLE BOM_LINE_ITEM ADD ROLLUP_FLAG VARCHAR2(1) DEFAULT 'Y' NOT NULL;

CREATE OR REPLACE VIEW IV_SOURCING_BUSINESS_ENTITY AS 
SELECT SL.SOURCING_LANE_KEY SOURCING_LANE_KEY, CR.COST_RECORD_KEY COST_RECORD_KEY,
	SL.ITEM_KEY ITEM_KEY, IM.ITEM_IDENTIFIER, IM.BUSINESS_ENTITY_KEY BUSINESS_ENTITY_KEY,
	SL.SUPPLIER_KEY SUPPLIER_KEY, CR.COST_PROVIDER_KEY COST_PROVIDER_KEY,
	CR.COST_TYPE_KEY COST_TYPE_KEY
FROM PCM_SOURCING_LANE SL JOIN ITEM_MASTER IM ON IM.ITEM_KEY = SL.ITEM_KEY
LEFT OUTER JOIN PCM_COST_RECORD CR ON CR.SOURCING_LANE_KEY = SL.SOURCING_LANE_KEY;
