-- DELLOCOS-4945 Revert from 22.3
-- DELETE FROM PCM_ROLE_RESPONSIBILITY WHERE ROLE_KEY NOT IN (1,2) AND RESPONSIBILITY_KEY = 'PRODUCTION';
-- INSERT INTO PCM_ROLE_RESPONSIBILITY (ROLE_KEY, RESPONSIBILITY_KEY) VALUES (1, 'PRODUCTION');
-- INSERT INTO PCM_ROLE_RESPONSIBILITY (ROLE_KEY, RESPONSIBILITY_KEY) VALUES (2, 'PRODUCTION');

INSERT INTO PCM_COST_ELEMENT (COST_ELEMENT_KEY, COST_TYPE_KEY, COST_ELEMENT_NAME, COST_ELEMENT_VALUE_TYPE, COST_ELEMENT_ORDER, COST_ELEMENT_TYPE)
VALUES ('OTHER_COST_ADDER', 'BUY', 'OTHER_COST_ADDER', 'S', '15', 'MATERIAL');
INSERT INTO PCM_COST_ELEMENT (COST_ELEMENT_KEY, COST_TYPE_KEY, COST_ELEMENT_NAME, COST_ELEMENT_VALUE_TYPE, COST_ELEMENT_ORDER, COST_ELEMENT_TYPE)
VALUES ('CONCESSION', 'BUY', 'CONCESSION', 'S', '16', 'MATERIAL');

INSERT INTO PCM_COST_ELEMENT (COST_ELEMENT_KEY, COST_TYPE_KEY, COST_ELEMENT_NAME, COST_ELEMENT_VALUE_TYPE, COST_ELEMENT_ORDER, COST_ELEMENT_TYPE)
VALUES ('OTHER_COST_ADDER', 'ODMBUY', 'OTHER_COST_ADDER', 'S', '15', 'MATERIAL');
INSERT INTO PCM_COST_ELEMENT (COST_ELEMENT_KEY, COST_TYPE_KEY, COST_ELEMENT_NAME, COST_ELEMENT_VALUE_TYPE, COST_ELEMENT_ORDER, COST_ELEMENT_TYPE)
VALUES ('CONCESSION', 'ODMBUY', 'CONCESSION', 'S', '16', 'MATERIAL');


ALTER TABLE FUNCTIONAL_GROUP ADD ROLLOVER_COUNT NUMBER(6,0);

-- CFG deactivate
 create or replace PROCEDURE CFG_DEACTIVATION_FOR_TAM_ROLLOVER_SP (MAX_ROLLOVER_COUNT NUMBER, MAX_CFG_DEACTIVATE NUMBER, 
    AUDIT_SOURCE VARCHAR2, CFG_DATE DATE, PROC_STATUS OUT VARCHAR2)
    AS
	
CURSOR cur_fg IS
SELECT t.*, fg.status
FROM FUNCTIONAL_GROUP FG
  LEFT JOIN TAM_ALLOCATION T ON FG.FUNCTIONAL_GROUP_ID = T.FUNCTIONAL_GROUP_ID
  AND T.ROLL_OVER_COUNT >= 
  (CASE 
WHEN FG.ROLLOVER_COUNT IS NULL THEN MAX_ROLLOVER_COUNT
ELSE FG.ROLLOVER_COUNT END)
WHERE ADD_MONTHS(TRUNC(t.LAST_ROLLOVER_DATE, 'DDD'),MAX_CFG_DEACTIVATE) = TRUNC(CFG_DATE, 'DDD');

rec_fg cur_fg%ROWTYPE;

BEGIN

  OPEN cur_fg;
  LOOP
     FETCH cur_fg INTO rec_fg;
     EXIT WHEN cur_fg%NOTFOUND;

	 IF (rec_fg.STATUS = 'ACTIVE') THEN

       UPDATE FUNCTIONAL_GROUP
       SET STATUS = 'INACTIVE'
       WHERE FUNCTIONAL_GROUP_ID = rec_fg.FUNCTIONAL_GROUP_ID;

	    INSERT INTO FG_AUDIT_HISTORY (AUDIT_KEY, DATE_PERFORMED, USER_ID, USER_ROLE, ACTION, OPERATION_CODE, COMMENTS, AUDIT_SOURCE, TAM_KEY, SITE, FG_KEY)
        VALUES (FG_AUDIT_KEY_SEQ.NEXTVAL, CURRENT_TIMESTAMP, 'BATCH','ADMIN','BATCH','FG UPDATED','Status update from ACTIVE to INACTIVE',
              AUDIT_SOURCE, NULL, NULL,rec_fg.functional_group_id);

       COMMIT;

	 END IF;

  END LOOP;
  CLOSE cur_fg;
  COMMIT;
       PROC_STATUS := 'SUCCESS';
EXCEPTION
   WHEN OTHERS
   THEN
      PROC_STATUS := 'FAILURE' || SUBSTR (SQLERRM, 1, 50);
END;
/

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
AND t.ROLL_OVER_COUNT <= MAX_ROLLOVER_COUNT
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
        WHEN (rec_tam.STATUS = 'ACTIVE' and rec_tam.tam_visible_flag = '1' and rec_tam.ROLL_OVER_COUNT < v_rollover_count) THEN tsa.allocation
        WHEN (rec_tam.STATUS = 'ACTIVE' and rec_tam.tam_visible_flag = '1' and rec_tam.ROLL_OVER_COUNT >= v_rollover_count) THEN NULL
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
        WHEN (rec_tam.STATUS = 'ACTIVE' and rec_tam.tam_visible_flag = '1' and rec_tam.ROLL_OVER_COUNT < v_rollover_count) THEN mtia.allocation
        WHEN (rec_tam.STATUS = 'ACTIVE' and rec_tam.tam_visible_flag = '1' and rec_tam.ROLL_OVER_COUNT >= v_rollover_count) THEN NULL
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
            IF (rec_tam.ROLL_OVER_COUNT < v_rollover_count) THEN
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

--DELLOCOS-4965: new column in tam allocation table to store last rollover date for CFG deactivation
ALTER TABLE tam_allocation ADD LAST_ROLLOVER_DATE date;