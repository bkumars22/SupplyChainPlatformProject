INSERT INTO PCM_WORKFLOW (WORKFLOW_KEY,WORKFLOW_NAME,WORKFLOW_GROUP,WORKFLOW_PARENT_KEY,WORKFLOW_URL,DISPLAY_ORDER,WORKFLOW_URL_TARGET) VALUES 
('TAM_DELETE_TEMPLATE', 'TAM Delete Template', 'MCM', 'FUNCTIONAL_GROUP', 'downloadTAMDelete.do', 10, NULL);
ALTER TABLE COST_EXCEPTION ADD UPLOAD_TYPE VARCHAR2(32) DEFAULT 'I' NOT NULL;
ALTER TABLE COST_EXCEPTION MODIFY UPLOAD_TYPE DEFAULT NULL;

create or replace PACKAGE TamCascadeOperation AS
  PROCEDURE TAM_ALLOC_CASCADE_PROC_SP (
    vOUT_RESULT 	out SYS_REFCURSOR,
	vEXTRACT_FLAG   in  VARCHAR2
  );

  PROCEDURE TAM_ALLOC_CASCADE_DELTA_UPDATE (
    vOUT_RESULT 	out SYS_REFCURSOR
  );

END TamCascadeOperation;
/

create or replace PACKAGE BODY TamCascadeOperation AS

    PROCEDURE TAM_ALLOC_CASCADE_PROC_SP (
        vOUT_RESULT out SYS_REFCURSOR,
        vEXTRACT_FLAG  in  VARCHAR2
    ) AS
    vMONTH_START_DATE DATE;
    vWEEK_START_DATE DATE;
    BEGIN

    SELECT TO_DATE(NEXT_DAY(SYSDATE,'SATURDAY') - 7) INTO vWEEK_START_DATE FROM DUAL;

    SELECT MIN(FISCAL_PERIOD_START_DATE)  INTO vMONTH_START_DATE
    FROM fiscal_period
    WHERE FISCAL_PERIOD_TYPE IN ('M','W')
    AND FISCAL_PERIOD_START_DATE >= trunc(vWEEK_START_DATE);

  -- Capture the SITE with TAM_PROCESSING_FLAG Info --
  EXECUTE IMMEDIATE ('TRUNCATE TABLE TAM_SITE_DETAIL_PROCESSING REUSE STORAGE');
  INSERT /*+ APPEND PARALLEL */ INTO TAM_SITE_DETAIL_PROCESSING (SITE_KEY, SITE_IDENTIFIER, SITE_DESCRIPTION, SITE_TYPE,PARENT_SITE_KEY,BUSINESS_ENTITY_KEY,SITE_DETAIL_ID,TAM_PROCESSING_FLAG)
  SELECT SITE_KEY,SITE_IDENTIFIER,SITE_DESCRIPTION,SITE_TYPE,PARENT_SITE_KEY,BUSINESS_ENTITY_KEY,SITE_DETAIL_ID,
         CASE WHEN SITE_TYPE = 'GLOBAL' OR SITE_TYPE = 'REGION' THEN '1'
              ELSE TAM_PROCESSING_FLAG END AS TAM_PROCESSING_FLAG
  FROM SITE ST,SITE_DETAILS SD
      WHERE ST.SITE_DETAIL_ID = SD.ID;
  COMMIT;

  DBMS_STATS.GATHER_TABLE_STATS(OWNNAME=>USER,TABNAME=>'TAM_SITE_DETAIL_PROCESSING',CASCADE => TRUE, DEGREE=>16);

  -- 1st STG to extract all TAM details --
  EXECUTE IMMEDIATE ('TRUNCATE TABLE TAM_ALLOC_CASCADE_PROC_STG1 REUSE STORAGE');
  INSERT /*+ APPEND PARALLEL */ INTO TAM_ALLOC_CASCADE_PROC_STG1 (TAM_ALLOCATION_ID,FUNCTIONAL_GROUP_ID,FG_NAME,LAST_CHANGED_BY,LAST_CHANGED_ON,ALLOW_HEDGING,CREATED_BY,CREATED_ON,DISCP_EXTRACT_FLAG,SITE_KEY,SITE_IDENTIFIER,SITE_DESCRIPTION,SITE_TYPE,PARENT_SITE_KEY,SUPPLIER_IDENTIFIER,SUPPLIER_NAME,START_DATE,END_DATE,TAM_SUPPLIER_ALLOCATION_ID,SUPPLIER_ALLOCATION,TAM_ITEM_ALLOCATION_ID,ITEM_ALLOCATION,ITEM_IDENTIFIER,CHILD_PARENT_SITE_ID,CHILD_IS_DELTA,DATASOURCE)
select
     X.TAM_ALLOCATION_ID,X.FUNCTIONAL_GROUP_ID,X.FG_NAME,X.LAST_CHANGED_BY,X.LAST_CHANGED_ON,X.ALLOW_HEDGING,X.CREATED_BY,X.CREATED_ON,X.DISCP_EXTRACT_FLAG,X.SITE_KEY,X.SITE_IDENTIFIER,X.SITE_DESCRIPTION,X.SITE_TYPE,X.PARENT_SITE_KEY,
      BE.BUSINESS_ENTITY_IDENTIFIER  AS SUPPLIER_IDENTIFIER,
      BE.BUSINESS_ENTITY_NAME        AS SUPPLIER_NAME,
      TSA.START_DATE                 AS START_DATE,
      TSA.END_DATE                   AS END_DATE,
      TSA.TAM_SUPPLIER_ALLOCATION_ID AS TAM_SUPPLIER_ALLOCATION_ID,
      TSA.ALLOCATION                 AS SUPPLIER_ALLOCATION,
      TIA.TAM_ITEM_ALLOCATION_ID     AS TAM_ITEM_ALLOCATION_ID,
      TIA.ALLOCATION                 AS ITEM_ALLOCATION,
      IM.ITEM_IDENTIFIER             AS ITEM_IDENTIFIER,
      CHILD_PARENT_SITE_ID,
      CHILD_IS_DELTA,
      IM.DATA_SOURCE                 AS DATASOURCE
FROM
(
SELECT * FROM
(
  SELECT X.*,
  CASE WHEN SITE_TYPE = 'GLOBAL' AND (MAX(CASE WHEN DISCP_EXTRACT_FLAG = 'P' THEN 1 ELSE 0 END) OVER (PARTITION BY FUNCTIONAL_GROUP_ID)) = 1 THEN 1
       WHEN SITE_TYPE = 'REGION' AND MAX(CASE WHEN DISCP_EXTRACT_FLAG = 'P' THEN 1 ELSE 0 END) OVER (PARTITION BY FUNCTIONAL_GROUP_ID,CHILD_PARENT_SITE_ID) = 1 THEN 1
       ELSE 0 END AS CHILD_IS_DELTA
  FROM
  (
    SELECT
          TA.TAM_ALLOCATION_ID,
          FG.FUNCTIONAL_GROUP_ID,
          FG.NAME             AS FG_NAME,
          TA.LAST_CHANGED_BY AS LAST_CHANGED_BY,
          TA.LAST_CHANGED_ON AS LAST_CHANGED_ON,
          TA.ALLOW_HEDGING   AS ALLOW_HEDGING,
          TA.CREATED_BY      AS CREATED_BY,
          TA.CREATED_ON      AS CREATED_ON,
          TA.DISCP_EXTRACT_FLAG AS DISCP_EXTRACT_FLAG,
          TA.SITE_KEY                   AS SITE_KEY,
          SI.SITE_IDENTIFIER             AS SITE_IDENTIFIER,
          SI.SITE_DESCRIPTION            AS SITE_DESCRIPTION,
          SI.SITE_TYPE                   AS SITE_TYPE,
          SI.PARENT_SITE_KEY             AS PARENT_SITE_KEY,
          CASE WHEN SI.SITE_TYPE = 'SITE' THEN PARENT_SITE_KEY
             ELSE SI.SITE_KEY END AS CHILD_PARENT_SITE_ID
        FROM TAM_ALLOCATION TA
          INNER JOIN FUNCTIONAL_GROUP FG
                ON FG.FUNCTIONAL_GROUP_ID = TA.FUNCTIONAL_GROUP_ID
          INNER JOIN TAM_SITE_DETAIL_PROCESSING SI
            ON SI.SITE_KEY = TA.SITE_KEY --AND SI.SITE_DESCRIPTION NOT LIKE '%DONOTUSE'
            AND SI.TAM_PROCESSING_FLAG = '1'
            AND FG.STATUS = 'ACTIVE'
            AND SI.BUSINESS_ENTITY_KEY IN (SELECT BE.BUSINESS_ENTITY_KEY
                                            FROM BUSINESS_ENTITY BE, BUSINESS_ENTITY_TYPE BET
                                            WHERE BE.BUSINESS_ENTITY_TYPE_KEY  = BET.BUSINESS_ENTITY_TYPE_KEY
                                                AND BET.BUSINESS_ENTITY_TYPE_NAME = 'ENTERPRISE')
        WHERE EXISTS
                (
                     select 'x' from tam_supplier_allocation tsa1
                     where tsa1.TAM_ALLOCATION_ID = TA.TAM_ALLOCATION_ID
                     AND START_DATE >= vMONTH_START_DATE
                     AND (SI.SITE_TYPE <> 'GLOBAL' OR ( SI.SITE_TYPE = 'GLOBAL' AND tsa1.allocation > 0 ) )
                 )
    ) X
  ) WHERE (DISCP_EXTRACT_FLAG = NVL(vEXTRACT_FLAG,DISCP_EXTRACT_FLAG) OR CHILD_IS_DELTA = 1)
) X
    INNER JOIN TAM_SUPPLIER_ALLOCATION TSA
    ON TSA.TAM_ALLOCATION_ID = X.TAM_ALLOCATION_ID AND TSA.START_DATE >= vMONTH_START_DATE
    INNER JOIN BUSINESS_ENTITY BE
    ON TSA.BUSINESS_ENTITY_KEY = BE.BUSINESS_ENTITY_KEY
    INNER JOIN IV_TAM_ITEM_ALLOCATION TIA
    ON TIA.TAM_SUPPLIER_ALLOCATION_ID = TSA.TAM_SUPPLIER_ALLOCATION_ID
    INNER JOIN ITEM_MASTER IM
    ON IM.ITEM_KEY = TIA.ITEM_KEY;
COMMIT;

DBMS_STATS.GATHER_TABLE_STATS(OWNNAME=>USER,TABNAME=>'TAM_ALLOC_CASCADE_PROC_STG1',CASCADE => TRUE, DEGREE=>16);

-- 2nd STG Process to apply the Cascading Logic --
    EXECUTE IMMEDIATE ('TRUNCATE TABLE TAM_ALLOC_CASCADE_PROC_STG2 REUSE STORAGE');
    INSERT /*+ APPEND PARALLEL */ INTO TAM_ALLOC_CASCADE_PROC_STG2 (TAM_ALLOCATION_ID,FUNCTIONAL_GROUP_ID,FG_NAME,LAST_CHANGED_BY,LAST_CHANGED_ON,ALLOW_HEDGING,CREATED_BY,CREATED_ON,DISCP_EXTRACT_FLAG,SITE_KEY,SITE_IDENTIFIER,SITE_DESCRIPTION,SITE_TYPE,PARENT_SITE_KEY,SUPPLIER_IDENTIFIER,SUPPLIER_NAME,START_DATE,END_DATE,TAM_SUPPLIER_ALLOCATION_ID,SUPPLIER_ALLOCATION,OLD_SUPPLIER_ALLOCATION,TOT_SUPP_ALOC,TAM_ITEM_ALLOCATION_ID,ITEM_ALLOCATION,ITEM_IDENTIFIER,BUCK_ALOC,ALLOC_GLOBAL,ALLOC_REGION,REGION_PARENT_EXISTS,DEFAULT_ITEM_IDENTIFIER)
  SELECT TAM_ALLOCATION_ID,FUNCTIONAL_GROUP_ID,FG_NAME,LAST_CHANGED_BY,LAST_CHANGED_ON,ALLOW_HEDGING,CREATED_BY,CREATED_ON,DISCP_EXTRACT_FLAG,SITE_KEY,SITE_IDENTIFIER,SITE_DESCRIPTION,SITE_TYPE,PARENT_SITE_KEY,SUPPLIER_IDENTIFIER,SUPPLIER_NAME,START_DATE,END_DATE,TAM_SUPPLIER_ALLOCATION_ID,SUPPLIER_ALLOCATION,OLD_SUPPLIER_ALLOCATION,TOT_SUPP_ALOC,TAM_ITEM_ALLOCATION_ID,ITEM_ALLOCATION,ITEM_IDENTIFIER,BUCK_ALOC,ALLOC_GLOBAL,ALLOC_REGION,REGION_PARENT_EXISTS,DEFAULT_ITEM_IDENTIFIER
  FROM
  (
  select TAM_ALLOCATION_ID, FUNCTIONAL_GROUP_ID, FG_NAME, LAST_CHANGED_BY, LAST_CHANGED_ON, ALLOW_HEDGING, CREATED_BY, CREATED_ON, DISCP_EXTRACT_FLAG, SITE_KEY, SITE_IDENTIFIER, SITE_DESCRIPTION,
           SITE_TYPE, PARENT_SITE_KEY, SUPPLIER_IDENTIFIER, SUPPLIER_NAME, START_DATE, END_DATE, TAM_SUPPLIER_ALLOCATION_ID,
           SUPPLIER_ALLOCATION,OLD_SUPPLIER_ALLOCATION,
           SUM(CASE WHEN SUPP_ROW_NUM = 1 THEN SUPPLIER_ALLOCATION ELSE NULL END) OVER (PARTITION BY FUNCTIONAL_GROUP_ID,SITE_KEY,START_DATE) AS TOT_SUPP_ALOC,
           TAM_ITEM_ALLOCATION_ID, ITEM_ALLOCATION,
           ITEM_IDENTIFIER,BUCK_ALOC,alloc_global,alloc_region,REGION_PARENT_EXISTS,DEFAULT_ITEM_IDENTIFIER,
           MAX(CASE WHEN NVL(SUPPLIER_ALLOCATION,0) = 0 THEN 0 ELSE 1 END) OVER (PARTITION BY FUNCTIONAL_GROUP_ID,SITE_IDENTIFIER,SUPPLIER_IDENTIFIER) SUPP_ALLOC_NOT_NULL,
            MAX(
               CASE WHEN ((NVL(ITEM_ALLOCATION,0) = 0 AND SITE_TYPE = 'SITE') OR (NVL(ITEM_ALLOCATION,0) = 0 AND ITEM_IDENTIFIER <> DEFAULT_ITEM_IDENTIFIER)) THEN 0 
               ELSE 1 END
              ) OVER (PARTITION BY FUNCTIONAL_GROUP_ID,SITE_IDENTIFIER,SUPPLIER_IDENTIFIER,ITEM_IDENTIFIER) ITEM_ALLOC_NOT_NULL
      FROM
      ( 
           select TAM_ALLOCATION_ID, FUNCTIONAL_GROUP_ID, FG_NAME, LAST_CHANGED_BY, LAST_CHANGED_ON, ALLOW_HEDGING, CREATED_BY, CREATED_ON, DISCP_EXTRACT_FLAG, SITE_KEY, SITE_IDENTIFIER, SITE_DESCRIPTION,
           SITE_TYPE, PARENT_SITE_KEY, SUPPLIER_IDENTIFIER, SUPPLIER_NAME, START_DATE, END_DATE, TAM_SUPPLIER_ALLOCATION_ID,
           SUPPLIER_ALLOCATION,OLD_SUPPLIER_ALLOCATION,
           SUM(CASE WHEN SUPP_ROW_NUM = 1 THEN SUPPLIER_ALLOCATION ELSE NULL END) OVER (PARTITION BY FUNCTIONAL_GROUP_ID,SITE_KEY,START_DATE) AS TOT_SUPP_ALOC,
           TAM_ITEM_ALLOCATION_ID, 
           CASE WHEN ITEM_ALLOCATION IS NULL THEN
                CASE WHEN ITEM_IDENTIFIER = DEFAULT_ITEM_IDENTIFIER THEN
                 CASE WHEN SUM(ITEM_ALLOCATION) over (PARTITION BY TAM_ALLOCATION_ID,FUNCTIONAL_GROUP_ID,TAM_SUPPLIER_ALLOCATION_ID) is null THEN 100 
                 ELSE 0 END
                ELSE 0 END
                ELSE ITEM_ALLOCATION END AS ITEM_ALLOCATION,
           ITEM_IDENTIFIER,BUCK_ALOC,alloc_global,alloc_region,REGION_PARENT_EXISTS,DEFAULT_ITEM_IDENTIFIER,SUPP_ROW_NUM
      FROM
      (
        select TAM_ALLOCATION_ID, FUNCTIONAL_GROUP_ID, FG_NAME, LAST_CHANGED_BY, LAST_CHANGED_ON, ALLOW_HEDGING, CREATED_BY, CREATED_ON, DISCP_EXTRACT_FLAG, SITE_KEY, SITE_IDENTIFIER, SITE_DESCRIPTION,
                 SITE_TYPE, PARENT_SITE_KEY, SUPPLIER_IDENTIFIER, SUPPLIER_NAME, START_DATE, END_DATE, TAM_SUPPLIER_ALLOCATION_ID,
                 case when BUCK_ALOC is null AND site_type = 'SITE' AND REGION_PARENT_EXISTS = 1 then alloc_region
                      when BUCK_ALOC is null AND site_type = 'SITE' AND REGION_PARENT_EXISTS = 0 then alloc_global
                      else SUPPLIER_ALLOCATION end as SUPPLIER_ALLOCATION,
                 SUPPLIER_ALLOCATION OLD_SUPPLIER_ALLOCATION,
                 TAM_ITEM_ALLOCATION_ID, 
                 case when BUCK_ITEM_ALOC is null AND site_type = 'SITE' AND REGION_PARENT_EXISTS = 1 then alloc_item_region
                      when BUCK_ITEM_ALOC is null AND site_type = 'SITE' AND REGION_PARENT_EXISTS = 0 then alloc_item_global
                 else ITEM_ALLOCATION end as ITEM_ALLOCATION,
                 ITEM_ALLOCATION OLD_ITEM_ALLOCATION,
                 ITEM_IDENTIFIER,BUCK_ALOC,BUCK_ITEM_ALOC,alloc_region,alloc_item_region,alloc_global,alloc_item_global,REGION_PARENT_EXISTS,DEFAULT_ITEM_IDENTIFIER,
                 ROW_NUMBER() OVER (PARTITION BY FUNCTIONAL_GROUP_ID,SITE_KEY,SUPPLIER_NAME,START_DATE order by ITEM_IDENTIFIER ) AS SUPP_ROW_NUM
          FROM
          (
            select TAM_ALLOCATION_ID, FUNCTIONAL_GROUP_ID, FG_NAME, LAST_CHANGED_BY, LAST_CHANGED_ON, ALLOW_HEDGING, CREATED_BY, CREATED_ON, DISCP_EXTRACT_FLAG, SITE_KEY, SITE_IDENTIFIER, SITE_DESCRIPTION,
                 SITE_TYPE, PARENT_SITE_KEY, SUPPLIER_IDENTIFIER, SUPPLIER_NAME, START_DATE, END_DATE, TAM_SUPPLIER_ALLOCATION_ID,SUPPLIER_ALLOCATION,
                 max(case when site_type =  'REGION' then  SUPPLIER_ALLOCATION end) over (partition by CHILD_PARENT_SITE_ID,supplier_identifier,start_date,FG_NAME) alloc_region,
                 SUPPLIER_ALLOCATION OLD_SUPPLIER_ALLOCATION,
                 TAM_ITEM_ALLOCATION_ID,ITEM_ALLOCATION,
                 max(case when site_type =  'REGION' then  ITEM_ALLOCATION end) over (partition by CHILD_PARENT_SITE_ID,supplier_identifier,item_identifier,start_date,FG_NAME) alloc_item_region,
                 ITEM_ALLOCATION OLD_ITEM_ALLOCATION,
                 ITEM_IDENTIFIER,CHILD_PARENT_SITE_ID,
                 BUCK_ALOC,BUCK_ITEM_ALOC,alloc_global,alloc_item_global,REGION_PARENT_EXISTS,DEFAULT_ITEM_IDENTIFIER
          FROM
          (
            select TAM_ALLOCATION_ID, FUNCTIONAL_GROUP_ID, FG_NAME, LAST_CHANGED_BY, LAST_CHANGED_ON, ALLOW_HEDGING, CREATED_BY, CREATED_ON, DISCP_EXTRACT_FLAG, SITE_KEY, SITE_IDENTIFIER, SITE_DESCRIPTION,
                 SITE_TYPE, PARENT_SITE_KEY, SUPPLIER_IDENTIFIER, SUPPLIER_NAME, START_DATE, END_DATE, TAM_SUPPLIER_ALLOCATION_ID,
                 case when BUCK_ALOC is null AND site_type = 'REGION' then alloc_global
                 else SUPPLIER_ALLOCATION end as SUPPLIER_ALLOCATION,
                 SUPPLIER_ALLOCATION OLD_SUPPLIER_ALLOCATION,
                 TAM_ITEM_ALLOCATION_ID, 
                 case when BUCK_ITEM_ALOC is null AND site_type = 'REGION' then alloc_item_global
                 else ITEM_ALLOCATION end as ITEM_ALLOCATION,
                 ITEM_ALLOCATION OLD_ITEM_ALLOCATION,
                 ITEM_IDENTIFIER,CHILD_PARENT_SITE_ID,
                 BUCK_ALOC,BUCK_ITEM_ALOC,alloc_global,alloc_item_global,REGION_PARENT_EXISTS,DEFAULT_ITEM_IDENTIFIER
            from
            (
                select x.*,
                SUM(SUPPLIER_ALLOCATION) over (PARTITION BY TAM_ALLOCATION_ID,FUNCTIONAL_GROUP_ID,START_DATE) BUCK_ALOC,
                SUM(ITEM_ALLOCATION) over (PARTITION BY TAM_ALLOCATION_ID,FUNCTIONAL_GROUP_ID,START_DATE) BUCK_ITEM_ALOC,
                max(case when site_type =  'GLOBAL' and site_identifier = 1 then  SUPPLIER_ALLOCATION end) over (partition by supplier_identifier,start_date,FG_NAME) alloc_global,
                max(case when site_type =  'GLOBAL' and site_identifier = 1 then  ITEM_ALLOCATION end) over (partition by supplier_identifier,item_identifier,start_date,FG_NAME) alloc_item_global,
                CASE WHEN (count(CASE WHEN site_type =  'REGION' THEN 1 END) OVER (PARTITION BY FG_NAME,CHILD_PARENT_SITE_ID)) > 0 THEN 1
                     ELSE 0 END AS REGION_PARENT_EXISTS,
                CASE WHEN (count(CASE WHEN DATASOURCE =  'EMC' THEN 1 END) OVER (PARTITION BY FUNCTIONAL_GROUP_ID,SITE_KEY,SUPPLIER_IDENTIFIER,START_DATE)) > 0 
                     THEN MAX(ITEM_IDENTIFIER) OVER (PARTITION BY FUNCTIONAL_GROUP_ID,SITE_KEY,SUPPLIER_IDENTIFIER,START_DATE)
                     ELSE MIN(ITEM_IDENTIFIER) OVER (PARTITION BY FUNCTIONAL_GROUP_ID,SITE_KEY,SUPPLIER_IDENTIFIER,START_DATE)
                     END AS DEFAULT_ITEM_IDENTIFIER
                from
                (
                 select * from TAM_ALLOC_CASCADE_PROC_STG1-- where (DISCP_EXTRACT_FLAG = NVL(vEXTRACT_FLAG,DISCP_EXTRACT_FLAG) OR CHILD_IS_DELTA = 1)
                ) x
             )x
           )
          )
        )
      ) WHERE DISCP_EXTRACT_FLAG = NVL(vEXTRACT_FLAG,DISCP_EXTRACT_FLAG)
    ) WHERE SUPP_ALLOC_NOT_NULL > 0 AND ITEM_ALLOC_NOT_NULL > 0;
  COMMIT;

    DBMS_STATS.GATHER_TABLE_STATS(OWNNAME=>USER,TABNAME=>'TAM_ALLOC_CASCADE_PROC_STG2',CASCADE => TRUE, DEGREE=>16);

    OPEN VOUT_RESULT FOR
        SELECT 1 AS COUNT FROM DUAL;

    EXCEPTION
        WHEN OTHERS THEN
            RAISE;
    END TAM_ALLOC_CASCADE_PROC_SP;

    PROCEDURE TAM_ALLOC_CASCADE_DELTA_UPDATE (
        vOUT_RESULT out SYS_REFCURSOR
    ) AS
    vMONTH_START_DATE DATE;
    vWEEK_START_DATE DATE;
    BEGIN

    SELECT TO_DATE(NEXT_DAY(SYSDATE,'SATURDAY') - 7) INTO vWEEK_START_DATE FROM DUAL;

    SELECT MIN(FISCAL_PERIOD_START_DATE)  INTO vMONTH_START_DATE
     FROM fiscal_period
    WHERE FISCAL_PERIOD_TYPE IN ('M','W')
    AND FISCAL_PERIOD_START_DATE >= trunc(vWEEK_START_DATE);

    -- Insert records in FG Audit

    insert into FG_AUDIT_HISTORY(audit_key,tam_key,user_id,user_role,audit_source,site,fg_key,action,operation_code,comments)
    SELECT FG_AUDIT_KEY_SEQ.NEXTVAL,TA.TAM_ALLOCATION_ID,'ADMIN' ,'ADMIN','TAM',ta.site_key,ta.functional_group_id,'TAM_CASCADE_EXTRACT','TAM CASCADE EXTRACT CREATED', CONCAT(CONCAT('New TAM Created For FunctionalGroup: ' ,fg.NAME),concat(' and Site:  ', site.SITE_DESCRIPTION))
    from tam_allocation TA
    inner join functional_group fg on fg.functional_group_id=ta.functional_group_id
    inner join site site on site.site_key=ta.site_key
                WHERE TA.DISCP_EXTRACT_FLAG = 'P'
          AND TA.TAM_ALLOCATION_ID IN
          (
            SELECT  TAM_ALLOCATION_ID FROM TAM_ALLOC_CASCADE_PROC_STG2 TAS WHERE DISCP_EXTRACT_FLAG = 'P'
          );

          COMMIT;

     -- Update TAM_ALLOCATION to set the DISP_EXTRACT_FLAG to Y for DELTA Extractions --
     UPDATE TAM_ALLOCATION TA SET DISCP_EXTRACT_FLAG = 'Y'
          WHERE DISCP_EXTRACT_FLAG = 'P'
          AND TAM_ALLOCATION_ID IN
          (
            SELECT  TAM_ALLOCATION_ID FROM TAM_ALLOC_CASCADE_PROC_STG2 TAS WHERE DISCP_EXTRACT_FLAG = 'P' AND TAS.LAST_CHANGED_ON = TA.LAST_CHANGED_ON
          );

          COMMIT;

      OPEN VOUT_RESULT FOR
            SELECT 1 AS COUNT FROM DUAL;

        EXCEPTION
            WHEN OTHERS THEN
                RAISE;
    END TAM_ALLOC_CASCADE_DELTA_UPDATE;

END TamCascadeOperation;
/

CREATE OR REPLACE PROCEDURE TAM_DELETE_CASCADE_INSERT(
    vOUT_RESULT OUT SYS_REFCURSOR)
AS
BEGIN
  INSERT
  INTO TAM_ALLOC_CASCADE_PROC_STG2
    (
      TAM_ALLOCATION_ID,
      FUNCTIONAL_GROUP_ID,
      FG_NAME,
      LAST_CHANGED_BY,
      LAST_CHANGED_ON,
      ALLOW_HEDGING,
      CREATED_BY,
      CREATED_ON,
      DISCP_EXTRACT_FLAG,
      SITE_KEY,
      SITE_IDENTIFIER,
      SITE_DESCRIPTION,
      SITE_TYPE,
      PARENT_SITE_KEY,
      REGION_PARENT_EXISTS
    )
  SELECT t.TAM_ALLOCATION_ID,
    t.FUNCTIONAL_GROUP_ID,
    fg.NAME,
    t.LAST_CHANGED_BY,
    t.LAST_CHANGED_ON,
    t.ALLOW_HEDGING,
    t.CREATED_BY,
    t.CREATED_ON,
    t.DISCP_EXTRACT_FLAG,
    t.SITE_KEY,
    s.SITE_IDENTIFIER,
    s.SITE_DESCRIPTION,
    s.SITE_TYPE,
    s.PARENT_SITE_KEY,
    1
  FROM TAM_ALLOCATION t
  INNER JOIN FUNCTIONAL_GROUP fg
  ON t.FUNCTIONAL_GROUP_ID = fg.FUNCTIONAL_GROUP_ID
  INNER JOIN site s
  ON s.SITE_KEY                = t.SITE_KEY
  WHERE t.CURRENT_DATA_DELETED = 'Y'
  AND t.DISCP_EXTRACT_FLAG     = 'P';
  COMMIT;
  OPEN VOUT_RESULT FOR SELECT 1
AS
  COUNT FROM DUAL;
EXCEPTION
WHEN OTHERS THEN
  RAISE;
END TAM_DELETE_CASCADE_INSERT;
/

UPDATE PCM_WORKFLOW set WORKFLOW_NAME = 'Manage Supply Allocation / Item Allocation' WHERE WORKFLOW_KEY = 'ALLOCATION';
UPDATE PCM_WORKFLOW set WORKFLOW_NAME = 'Download Supply Allocation / Item Allocation' WHERE WORKFLOW_KEY = 'TAM_DOWNLOAD';
UPDATE PCM_WORKFLOW set WORKFLOW_NAME = 'Delete XLOB Allocation' WHERE WORKFLOW_KEY = 'XLOB_DELETE_TEMPLATE';
UPDATE PCM_WORKFLOW set WORKFLOW_NAME = 'Delete Supply Allocation / Item Allocation' WHERE WORKFLOW_KEY = 'TAM_DELETE_TEMPLATE';

-- TAM Rollover
create or replace PROCEDURE TAM_ROLLOVER (CURRENT_MONTH_START DATE,SPLIT_MONTH_START DATE, SPLIT_MONTH_END DATE,  ADD_MONTH_START DATE, ADD_MONTH_END DATE,
               COPY_MONTH_START DATE, COPY_MONTH_END DATE, MAX_ROLLOVER_COUNT NUMBER) AS
CURSOR cur_tam IS
SELECT t.*,fg.status, sd.*, fg.rollover_count
FROM tam_allocation t, functional_group fg,
     site s
     left join site_details sd on sd.ID = s.SITE_DETAIL_ID
WHERE t.functional_group_id = fg.functional_group_id
AND ( s.site_detail_id IN (SELECT sd.id FROM site_details sd
                   WHERE sd.site_state = '0')
    OR NOT EXISTS (SELECT 'x' FROM site_details sd1
               WHERE s.site_detail_id = sd1.id)
               )
AND t.site_key = s.site_key
AND NVL(t.ROLL_OVER_COUNT, 0) <= MAX_ROLLOVER_COUNT
AND (t.NEXT_ROLLOVER_DATE < SPLIT_MONTH_START OR (t.NEXT_ROLLOVER_DATE is null AND t.CREATED_ON < CURRENT_MONTH_START));

rec_tam cur_tam%ROWTYPE;

v_rollover_count NUMBER(6,0);

BEGIN
  OPEN cur_tam;
  LOOP
     FETCH cur_tam INTO rec_tam;
     EXIT WHEN cur_tam%NOTFOUND;

         -- Break into Weekly for TAM_SUPPLIER_ALLOCATION

         INSERT INTO tam_supplier_allocation (TAM_SUPPLIER_ALLOCATION_ID, TAM_ALLOCATION_ID, BUSINESS_ENTITY_KEY, START_DATE, END_DATE, ALLOCATION)
               SELECT TAM_SUPPLIER_ALLOCATION_KEY_SEQ.NEXTVAL, tsa.TAM_ALLOCATION_ID, tsa.BUSINESS_ENTITY_KEY, cal.w_start, cal.w_end, tsa.allocation
               FROM tam_allocation tam, tam_supplier_allocation tsa,
                    (SELECT m.FISCAL_PERIOD_START_DATE, m.FISCAL_PERIOD_END_DATE, w.FISCAL_PERIOD_START_DATE w_start, W.FISCAL_PERIOD_END_DATE w_end
                     FROM fiscal_period m, fiscal_period w
                     WHERE m.FISCAL_PERIOD_TYPE = 'M'
                     AND w.FISCAL_PERIOD_TYPE = 'W'
                     AND m.FISCAL_PERIOD_START_DATE <= w.FISCAL_PERIOD_START_DATE
                     AND m.FISCAL_PERIOD_END_DATE >= w.FISCAL_PERIOD_END_DATE
                     AND m.FISCAL_PERIOD_START_DATE >= SPLIT_MONTH_START
                     and m.FISCAL_PERIOD_END_DATE <= SPLIT_MONTH_END ) cal
               WHERE tam.tam_allocation_id = tsa.tam_allocation_id
               AND cal.FISCAL_PERIOD_START_DATE = tsa.start_date
               AND cal.FISCAL_PERIOD_END_DATE = tsa.end_date
               AND tam.tam_allocation_id = rec_tam.tam_allocation_id
               AND NOT EXISTS (SELECT 'x' FROM tam_supplier_allocation tsa1
                                WHERE tsa1.TAM_ALLOCATION_ID = tsa.TAM_ALLOCATION_ID
                                AND tsa1.BUSINESS_ENTITY_KEY = tsa.BUSINESS_ENTITY_KEY
                                AND tsa1.start_date = cal.w_start
                                AND tsa1.end_date = cal.w_end);

               -- Break into Weekly for TAM_ITEM_ALLOCATION

               INSERT INTO tam_item_allocation (tam_item_allocation_id, tam_supplier_allocation_id, item_key, allocation)
               SELECT TAM_ITEM_ALLOCATION_KEY_SEQ.NEXTVAL, wtsa.tam_supplier_allocation_id, mtia.item_key, mtia.allocation
               FROM tam_allocation tam, tam_supplier_allocation mtsa, tam_item_allocation mtia,  tam_supplier_allocation wtsa,
                    (SELECT m.FISCAL_PERIOD_START_DATE, m.FISCAL_PERIOD_END_DATE, w.FISCAL_PERIOD_START_DATE w_start, W.FISCAL_PERIOD_END_DATE w_end
                     FROM fiscal_period m, fiscal_period w
                     WHERE m.FISCAL_PERIOD_TYPE = 'M'
                     AND w.FISCAL_PERIOD_TYPE = 'W'
                     AND m.FISCAL_PERIOD_START_DATE <= w.FISCAL_PERIOD_START_DATE
                     AND m.FISCAL_PERIOD_END_DATE >= w.FISCAL_PERIOD_END_DATE
                     AND m.FISCAL_PERIOD_START_DATE >= SPLIT_MONTH_START
                     and m.FISCAL_PERIOD_END_DATE <= SPLIT_MONTH_END ) cal
               WHERE tam.tam_allocation_id = mtsa.tam_allocation_id
               AND mtsa.tam_supplier_allocation_id = mtia.tam_supplier_allocation_id
               AND mtsa.start_date = SPLIT_MONTH_START
               AND mtsa.end_date = SPLIT_MONTH_END
               AND tam.tam_allocation_id = wtsa.tam_allocation_id
               AND mtsa.BUSINESS_ENTITY_KEY = wtsa.BUSINESS_ENTITY_KEY
               AND cal.w_start = wtsa.start_date
               AND cal.w_end = wtsa.end_date
               AND tam.tam_allocation_id = rec_tam.tam_allocation_id
               AND NOT EXISTS (SELECT 'x' FROM tam_item_allocation tia1
                                WHERE tia1.tam_supplier_allocation_id = wtsa.tam_supplier_allocation_id
                                AND tia1.item_key = mtia.item_key);

         -- delete monthly

        DELETE FROM tam_item_allocation
        WHERE TAM_ITEM_ALLOCATION_ID IN (SELECT mtia.TAM_ITEM_ALLOCATION_ID
                                 FROM tam_allocation tam, tam_supplier_allocation mtsa, tam_item_allocation mtia
                                 WHERE tam.tam_allocation_id = mtsa.tam_allocation_id
                                 AND mtsa.tam_supplier_allocation_id = mtia.tam_supplier_allocation_id
                                 AND mtsa.start_date = SPLIT_MONTH_START
                                 AND mtsa.end_date = SPLIT_MONTH_END
                                 AND tam.tam_allocation_id = rec_tam.tam_allocation_id
                                 );

        -- delete monthly

               DELETE FROM tam_supplier_allocation
               WHERE TAM_SUPPLIER_ALLOCATION_ID IN (SELECT mtsa.TAM_SUPPLIER_ALLOCATION_ID
                                                FROM tam_allocation tam, tam_supplier_allocation mtsa
                                                WHERE tam.tam_allocation_id = mtsa.tam_allocation_id
                                                AND mtsa.start_date = SPLIT_MONTH_START
                                                AND mtsa.end_date = SPLIT_MONTH_END
                                                AND tam.tam_allocation_id = rec_tam.tam_allocation_id
                                               );

        --DELLOCOS-4965: once reach max skip update TAM_rollover_count 
        CASE 
          WHEN rec_tam.rollover_count IS NULL THEN
            v_rollover_count := MAX_ROLLOVER_COUNT;
          ELSE
          v_rollover_count := rec_tam.rollover_count;
        END CASE;

        -- Add 12th month

        INSERT INTO tam_supplier_allocation (TAM_SUPPLIER_ALLOCATION_ID, TAM_ALLOCATION_ID, BUSINESS_ENTITY_KEY, START_DATE, END_DATE, ALLOCATION)
	SELECT TAM_SUPPLIER_ALLOCATION_KEY_SEQ.NEXTVAL, tsa.TAM_ALLOCATION_ID, tsa.BUSINESS_ENTITY_KEY, cal.FISCAL_PERIOD_START_DATE, cal.FISCAL_PERIOD_END_DATE,
	(CASE  
        WHEN (rec_tam.STATUS = 'ACTIVE' and rec_tam.tam_visible_flag = '1' and NVL(rec_tam.ROLL_OVER_COUNT, 0) < v_rollover_count) THEN tsa.allocation
        WHEN (rec_tam.STATUS = 'ACTIVE' and rec_tam.tam_visible_flag = '1' and NVL(rec_tam.ROLL_OVER_COUNT, 0) >= v_rollover_count) THEN NULL
        WHEN (rec_tam.STATUS = 'INACTIVE') THEN NULL
        ELSE NULL 
    END)	
	FROM tam_allocation tam, tam_supplier_allocation tsa,
	    (SELECT m.FISCAL_PERIOD_START_DATE, m.FISCAL_PERIOD_END_DATE
	     FROM fiscal_period m
	     WHERE m.FISCAL_PERIOD_TYPE = 'M'
	     AND m.FISCAL_PERIOD_START_DATE >= ADD_MONTH_START  -- future month start
	     and m.FISCAL_PERIOD_END_DATE <= ADD_MONTH_END   -- future month end
	     ) cal
	WHERE tam.tam_allocation_id = tsa.tam_allocation_id
	AND tsa.start_date = COPY_MONTH_START -- last month start
	AND tsa.end_date = COPY_MONTH_END -- last month end
	AND tam.tam_allocation_id = rec_tam.tam_allocation_id
	AND NOT EXISTS (SELECT 'x' FROM tam_supplier_allocation tsa1
	                WHERE tsa1.TAM_ALLOCATION_ID = tsa.TAM_ALLOCATION_ID
	                AND tsa1.BUSINESS_ENTITY_KEY = tsa.BUSINESS_ENTITY_KEY
	                AND tsa1.start_date = cal.FISCAL_PERIOD_START_DATE
	                AND tsa1.end_date = cal.FISCAL_PERIOD_END_DATE);


       -- Add 12th month

       INSERT INTO tam_item_allocation (tam_item_allocation_id, tam_supplier_allocation_id, item_key, allocation)
       SELECT TAM_ITEM_ALLOCATION_KEY_SEQ.NEXTVAL, wtsa.tam_supplier_allocation_id, mtia.item_key, 
       (CASE
        WHEN (rec_tam.STATUS = 'ACTIVE' and rec_tam.tam_visible_flag = '1' and NVL(rec_tam.ROLL_OVER_COUNT, 0) < v_rollover_count) THEN mtia.allocation
        WHEN (rec_tam.STATUS = 'ACTIVE' and rec_tam.tam_visible_flag = '1' and NVL(rec_tam.ROLL_OVER_COUNT, 0) >= v_rollover_count) THEN NULL
        WHEN (rec_tam.STATUS = 'INACTIVE') THEN NULL
        ELSE NULL 
        END)
       FROM tam_allocation tam, tam_supplier_allocation mtsa, tam_item_allocation mtia,  tam_supplier_allocation wtsa,
           (SELECT m.FISCAL_PERIOD_START_DATE, m.FISCAL_PERIOD_END_DATE
                FROM fiscal_period m
                WHERE m.FISCAL_PERIOD_TYPE = 'M'
                AND m.FISCAL_PERIOD_START_DATE >= ADD_MONTH_START  -- future month start
                and m.FISCAL_PERIOD_END_DATE <= ADD_MONTH_END   -- future month end
            ) cal
       WHERE tam.tam_allocation_id = mtsa.tam_allocation_id
       AND mtsa.tam_supplier_allocation_id = mtia.tam_supplier_allocation_id
       AND mtsa.start_date = COPY_MONTH_START
       AND mtsa.end_date = COPY_MONTH_END
       AND tam.tam_allocation_id = wtsa.tam_allocation_id
       AND mtsa.BUSINESS_ENTITY_KEY = wtsa.BUSINESS_ENTITY_KEY
       AND wtsa.start_date = cal.FISCAL_PERIOD_START_DATE
       AND wtsa.end_date =  cal.FISCAL_PERIOD_END_DATE
       AND tam.tam_allocation_id = rec_tam.tam_allocation_id
       AND NOT EXISTS (SELECT 'x' FROM tam_item_allocation tia1
                       WHERE tia1.tam_supplier_allocation_id = wtsa.tam_supplier_allocation_id
                       AND tia1.item_key = mtia.item_key);

       IF (rec_tam.tam_visible_flag = '1' and rec_tam.STATUS = 'ACTIVE') THEN  
            IF (NVL(rec_tam.ROLL_OVER_COUNT, 0) < v_rollover_count) THEN
              UPDATE TAM_ALLOCATION
               SET NEXT_ROLLOVER_DATE = SPLIT_MONTH_START,
                   LAST_CHANGED_BY = 'BATCH',
                   LAST_CHANGED_ON= CURRENT_TIMESTAMP,
                   ROLL_OVER_COUNT = NVL(ROLL_OVER_COUNT,0) + 1,
                   EXTRACT_FLAG = 'P',
                   DISCP_ROLLOVER_EXTRACT_FLAG = 'P'
               WHERE TAM_ALLOCATION_ID = rec_tam.tam_allocation_id;
           ELSE
           --DELLOCOS-4965: once reach max skip update TAM_rollover_count 
               UPDATE TAM_ALLOCATION
               SET NEXT_ROLLOVER_DATE = SPLIT_MONTH_START,
                   LAST_CHANGED_BY = 'BATCH',
                   LAST_CHANGED_ON= CURRENT_TIMESTAMP,
                   EXTRACT_FLAG = 'P',
                   DISCP_ROLLOVER_EXTRACT_FLAG = 'P'
               WHERE TAM_ALLOCATION_ID = rec_tam.tam_allocation_id;

               IF (rec_tam.LAST_ROLLOVER_DATE is null) THEN
               UPDATE TAM_ALLOCATION SET
                   LAST_ROLLOVER_DATE = SPLIT_MONTH_START
               WHERE TAM_ALLOCATION_ID = rec_tam.tam_allocation_id;
               END IF;

           END IF;
       ELSE
       UPDATE TAM_ALLOCATION
       SET NEXT_ROLLOVER_DATE = SPLIT_MONTH_START,
           ROLL_OVER_COUNT = NVL(ROLL_OVER_COUNT,0) + 1,
           EXTRACT_FLAG = 'P',
           DISCP_ROLLOVER_EXTRACT_FLAG = 'P'
       WHERE TAM_ALLOCATION_ID = rec_tam.tam_allocation_id;
       END IF;
       INSERT INTO FG_AUDIT_HISTORY (AUDIT_KEY, DATE_PERFORMED, USER_ID, USER_ROLE, ACTION, OPERATION_CODE, COMMENTS, AUDIT_SOURCE, TAM_KEY, SITE, FG_KEY)
        SELECT FG_AUDIT_KEY_SEQ.NEXTVAL, CURRENT_TIMESTAMP, 'BATCH','ADMIN','BATCH','TAM ROLLOVER','TAM Rolloved Over for FunctionalGroup: ' || fg.name || ' and Site: ' || s.site_description,
              'TAM', tam.tam_allocation_id, s.site_key,tam.functional_group_id
       FROM tam_allocation tam, functional_group fg,
            site s
       WHERE tam.functional_group_id = fg.functional_group_id
       AND fg.status ='ACTIVE'
       AND ( s.site_detail_id IN (SELECT sd.id FROM site_details sd
                          WHERE sd.site_state = '0')
           OR NOT EXISTS (SELECT 'x' FROM site_details sd1
                      WHERE s.site_detail_id = sd1.id)
                      )
       AND tam.site_key = s.site_key
       AND tam.ROLL_OVER_COUNT <= MAX_ROLLOVER_COUNT
       AND tam.tam_allocation_id = rec_tam.tam_allocation_id;

       COMMIT;

  END LOOP;
  CLOSE cur_tam;
  COMMIT;
END;
/

