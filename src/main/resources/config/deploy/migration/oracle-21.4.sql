CREATE SEQUENCE FG_LOB_SEQUENCE
        INCREMENT BY 1
        START WITH 1
        NOMAXVALUE
        NOMINVALUE
        CACHE 20
        NOCYCLE ORDER;

CREATE TABLE FG_LOB_VALUES
   (	ID NUMBER(19) NOT NULL, 
	    FUNCTIONAL_GROUP_ID NUMBER(19) NOT NULL, 
	    VALUE VARCHAR2(255 BYTE), 
	    CONSTRAINT LOB_PK PRIMARY KEY (ID) USING INDEX TABLESPACE AUDIT_INDEX,
	    CONSTRAINT FG_FK FOREIGN KEY (FUNCTIONAL_GROUP_ID)
			REFERENCES FUNCTIONAL_GROUP(FUNCTIONAL_GROUP_ID)
   )TABLESPACE AUDIT_DATA ;


CREATE UNIQUE INDEX FG_LOB_FGID
   ON FG_LOB_VALUES(FUNCTIONAL_GROUP_ID) TABLESPACE AUDIT_INDEX;


  CREATE OR REPLACE FORCE EDITIONABLE VIEW "IV_FUNCTIONAL_GROUP" ("FUNCTIONAL_GROUP_ID", "FG_NAME", "FG_DESCRIPTION", "FG_TYPE", "FG_STATUS", "STATUS_CHANGED_BY", "FG_LAST_CHAGNED_ON", "FG_LAST_CHANGED_BY", "EXTRACT_FLAG", "CREATED_ON", "CREATED_BY", "FG_LOB","FG_PLATFORM", "PARENT_FUNCTIONAL_GROUP_ID", "PG_NAME", "PG_DESCRIPTION", "PG_TYPE", "PURPOSE", "PG_LAST_CHANGED_ON", "PG_LAST_CHANGED_BY", "PFG_CREATED_ON", "PFG_CREATED_BY", "ITEM_IDENTIFIER") AS 
  SELECT
FG.FUNCTIONAL_GROUP_ID,
FG.NAME AS FG_NAME,
FG.DESCRIPTION AS FG_DESCRIPTION,
FG.TYPE AS FG_TYPE,
FG.STATUS AS FG_STATUS,
FG.STATUS_CHANGED_BY,
FG.LAST_CHANGED_ON AS FG_LAST_CHAGNED_ON,
FG.LAST_CHANGED_BY AS FG_LAST_CHANGED_BY,
FG.EXTRACT_FLAG,
FG.CREATED_ON,
FG.CREATED_BY,
FGLOB.VALUE AS FG_LOB,
FG.FG_PLATFORM,
PFG.PARENT_FUNCTIONAL_GROUP_ID,
PFG.NAME AS PG_NAME,
PFG.DESCRIPTION AS PG_DESCRIPTION,
PFG.TYPE AS PG_TYPE,
PFG.PURPOSE,
PFG.LAST_CHANGED_ON AS PG_LAST_CHANGED_ON,
PFG.LAST_CHANGED_BY AS PG_LAST_CHANGED_BY,
PFG.CREATED_ON AS PFG_CREATED_ON,
PFG.CREATED_BY AS PFG_CREATED_BY,
IM.ITEM_IDENTIFIER
FROM FUNCTIONAL_GROUP FG
LEFT OUTER JOIN FG_LOB_VALUES FGLOB ON FGLOB.FUNCTIONAL_GROUP_ID = FG.FUNCTIONAL_GROUP_ID
LEFT OUTER JOIN FG_PFG_MAP FGMAP ON FGMAP.FUNCTIONAL_GROUP_ID = FG.FUNCTIONAL_GROUP_ID
LEFT OUTER JOIN PARENT_FUNCTIONAL_GROUP PFG ON PFG.PARENT_FUNCTIONAL_GROUP_ID = FGMAP.PARENT_FUNCTIONAL_GROUP_ID
LEFT OUTER JOIN ITEM_FG_MAP IMAP ON IMAP.FUNCTIONAL_GROUP_ID = FG.FUNCTIONAL_GROUP_ID
LEFT OUTER JOIN ITEM_MASTER IM ON IM.ITEM_KEY = IMAP.ITEM_KEY
LEFT OUTER JOIN ITEM_PLATFORM IP ON IP.ITEM_PLATFORM_KEY = FG.ITEM_PLATFORM_KEY
ORDER BY FUNCTIONAL_GROUP_ID ASC;

CREATE INDEX XCR_NA1_I1 ON PCM_COST_RECORD (NUMBER_ATTRIBUTE1);

update PCM_COST_RECORD cr 
set cr.number_attribute1 = -1 
where cr.COST_TYPE_KEY = 'XWAP'
and cr.number_attribute1 is NULL;

CREATE INDEX TSA_SUPPLIER_DATE_ID ON TAM_SUPPLIER_ALLOCATION(BUSINESS_ENTITY_KEY,START_DATE,TAM_SUPPLIER_ALLOCATION_ID) TABLESPACE AUDIT_INDEX;
CREATE INDEX TIA_ITEM_ID ON TAM_ITEM_ALLOCATION(ITEM_KEY,TAM_SUPPLIER_ALLOCATION_ID) TABLESPACE AUDIT_INDEX;

/* V_PKEYS_FORECAST_EXTENSION_VIEW is created as part of (DELLOCOS-4429 ) */

CREATE OR REPLACE FORCE VIEW V_PKEYS_FORECAST_EXTENSION
(
    FUNCTIONAL_GROUP_ID,
    ITEM_KEY,
    SITE_KEY
) AS
    SELECT DISTINCT fm.functional_group_id, im.item_key, pf.site_key
     FROM pcm_forecast pf, item_master im, item_fg_map fm
    WHERE     pf.item_key = im.item_key
          AND pf.item_key = fm.item_key
          AND im.item_key = fm.item_key
          AND pf.forecast_model = 'CURRENT'
          AND pf.status = 'APPROVED'
          AND im.eol = 'Y'
          AND                           --        pf.item_key=v_parent_key and
             fm.functional_group_id IN (SELECT functional_group_id
                                          FROM functional_group
                                         WHERE TYPE = 'CFG')
          AND im.eol_type IN (SELECT FORECAST_KEY
                                FROM forecasts_to_close
                               WHERE RTYPE = 'EOL_TYPE')
    UNION
    SELECT FUNCTIONAL_GROUP_ID, itm.item_key, site_key
     FROM pcm_forecast FORCA
          INNER JOIN item_master ITM
             ON     FORCA.ITEM_KEY = ITM.ITEM_KEY
                AND FORCA.FORECAST_MODEL = 'CURRENT'
                AND FORCA.STATUS = 'APPROVED'
                AND ITM.EOL = 'Y'
                AND ITM.EOL_TYPE IN (SELECT FORECAST_KEY
                                       FROM forecasts_to_close
                                      WHERE RTYPE = 'EOL_TYPE')
          LEFT JOIN item_fg_map FCGALL
             ON ITM.ITEM_KEY = FCGALL.ITEM_KEY
    WHERE FUNCTIONAL_GROUP_ID IS NULL;

-- GET_FORECAST_KEYS_EXTENSION (DELLOCOS-4429 ) --

create or replace PROCEDURE GET_FORECAST_KEYS_EXTENSION (
    ITEM_EOL_TYPE   IN     VARCHAR2,
    FG_TYPE         IN     VARCHAR2,
    FC_MODEL        IN     VARCHAR2,
    FC_STATUS       IN     VARCHAR2,
    SITE_TYPE	   IN 	  VARCHAR2,
    PROC_STATUS        OUT VARCHAR2)
    IS
    --FG_TYPE VARCHAR2(20):='CFG';
    --FC_MODEL  VARCHAR2(20):='CURRENT';
    --FC_STATUS VARCHAR2(20):='APPROVED';
    --SITE_TYPE VARCHAR2(20) := 'CCN';

    v_site_type              VARCHAR2 (20) := SITE_TYPE;
    v_status              VARCHAR2 (20) := 'INIT';
    v_count               NUMBER := 0;
    my_count              NUMBER := 0;
    result                NUMBER := 1;
    v_output              VARCHAR2 (2000) := '';
    all_output            VARCHAR2 (2000) := '';
    V_CEA_PAR             NUMBER := 0;
    DELIMITED_EOL_TYPES   VARCHAR2 (2000) := ITEM_EOL_TYPE;
    tot_count             NUMBER := 0;
    dif_eol_type          NUMBER := 0;
    v_single_child        NUMBER := 0;
BEGIN
    EXECUTE IMMEDIATE 'TRUNCATE TABLE forecasts_to_close';

    EXECUTE IMMEDIATE 'TRUNCATE TABLE logger';

-- separests EOL types based on comma delimter and insert into forecasts_to_close table
    BEGIN
        FOR i
            IN (    SELECT TRIM (REGEXP_SUBSTR (DELIMITED_EOL_TYPES,
                                                '[^,]+',
                                                1,
                                                LEVEL))
                               l
                    FROM DUAL
                    CONNECT BY LEVEL <= REGEXP_COUNT (DELIMITED_EOL_TYPES, ',') + 1)
            LOOP
                -- dbms_output.put_line(i.l);
                INSERT INTO forecasts_to_close
                VALUES (SYSDATE, i.l, 'EOL_TYPE');
            END LOOP;
    END;

    COMMIT;

    INSERT INTO logger
    VALUES (SYSDATE, ' START OF PROCESSING ... ');

-- Fetches master records based on given parameters ( functional group id and item key)
    FOR c1 IN (SELECT * FROM V_PKEYS_FORECAST_EXTENSION)
        LOOP
            INSERT INTO logger
            VALUES (
                               SYSDATE,
                               'Starting Funtional Group ID:'
                                   || c1.functional_group_id
                                   || '--item_key '
                                   || c1.item_key
                                   || '--site_key '
                                   || c1.site_key);

            IF c1.functional_group_id IS NOT NULL
            THEN
                --select  count(*) into v_single_child from item_fg_map b where  b.functional_group_id=c1.functional_group_id and b.item_key not in (c1.item_key);
                SELECT COUNT(*)
                INTO v_single_child
                FROM pcm_forecast
                WHERE site_key IN (
                        SELECT site_key
                        FROM site
                        WHERE site_type = v_site_type
                    )
                    AND item_key IN (
                        SELECT item_key
                        FROM item_fg_map b
                        WHERE b.functional_group_id = c1.functional_group_id
                            AND b.item_key NOT IN (
                                SELECT item_key
                                FROM v_pkeys_forecast_extension
                            )
                    )
                    AND site_key = c1.site_key;


                IF v_single_child = 0
                THEN
                    FOR fc_key_single_child
                        IN (SELECT pf.forecast_key
                            FROM pcm_forecast pf, item_master im, item_fg_map fm, site s
                            WHERE     pf.item_key = im.item_key
                              AND pf.site_key = s.site_key
                              AND pf.item_key = fm.item_key
                              AND im.item_key = fm.item_key
                              AND pf.forecast_model = FC_MODEL
                              AND pf.status = FC_STATUS
                              AND im.eol = 'Y'
                              AND pf.item_key = c1.item_key
                              AND s.site_type = v_site_type
                              AND im.eol_type IN (SELECT FORECAST_KEY
                                                  FROM forecasts_to_close
                                                  WHERE RTYPE = 'EOL_TYPE'))
                        LOOP
                            INSERT INTO logger
                            VALUES (
                                               SYSDATE,
                                               ' fc_key_single_child :'
                                                   || '--->'
                                                   || fc_key_single_child.forecast_key);

                            INSERT
                            INTO forecasts_to_close (stat_date, forecast_key, RTYPE)
                            VALUES (SYSDATE, fc_key_single_child.forecast_key, 'RESULT');
                        END LOOP;

                    result := -1;
                END IF;

                -- selects all the child records of functional group
                FOR fm IN (
                    SELECT
                        b.item_key,
                        b.functional_group_id
                    FROM
                        item_fg_map  b,
                        pcm_forecast a
                    WHERE
                        a.site_key IN (
                            SELECT site_key
                            FROM site
                            WHERE site_type = v_site_type
                        )
                        AND b.functional_group_id = c1.functional_group_id
                        AND b.item_key NOT IN (
                            SELECT item_key
                            FROM v_pkeys_forecast_extension
                        )
                        AND b.item_key = a.item_key
                        AND status = 'APPROVED'
                        AND a.SITE_KEY  = c1.site_key
                ) LOOP
                        INSERT INTO logger
                        VALUES (
                                           SYSDATE,
                                           'Child records of functional group'
                                               || fm.functional_group_id
                                               || '--item_key '
                                               || fm.item_key);

                        SELECT COUNT (*)
                        INTO dif_eol_type
                        FROM pcm_forecast pf, item_master im
                        WHERE     pf.item_key = im.item_key
                          AND pf.forecast_model = FC_MODEL
                          AND pf.status = FC_STATUS
                          AND NVL (im.eol, 'N') <> 'Y'
                          AND NVL (im.eol_type, '1') NOT IN
                              (SELECT FORECAST_KEY
                               FROM forecasts_to_close
                               WHERE RTYPE = 'EOL_TYPE')
                          AND pf.item_key IN
                              (SELECT b.item_key
                               FROM item_fg_map b
                               WHERE b.functional_group_id =
                                     c1.functional_group_id);

                        INSERT INTO logger
                        VALUES (SYSDATE, 'dif_eol_type:- ' || dif_eol_type);

                        IF dif_eol_type = 0
                        THEN
                            SELECT COUNT (*)
                            INTO v_count
                            FROM pcm_forecast pf, item_master im
                            WHERE     pf.item_key = im.item_key
                              AND pf.forecast_model = FC_MODEL
                              AND pf.status <> FC_STATUS
                              AND NVL (im.eol, 'N') <> 'Y'
                              AND NVL (im.eol_type, '1') NOT IN
                                  (SELECT FORECAST_KEY
                                   FROM forecasts_to_close
                                   WHERE RTYPE = 'EOL_TYPE')
                              AND pf.item_key = fm.item_key;

                            INSERT INTO logger
                            VALUES (
                                               SYSDATE,
                                               'v_count:'
                                                   || v_count
                                                   || ' tot_count: '
                                                   || tot_count);

                            tot_count := v_count + tot_count;

                            IF tot_count = 0
                            THEN
                                result := 0;
                            ELSE
                                result := tot_count;
                            END IF;


                            INSERT INTO logger
                            VALUES (SYSDATE, 'Since v_count=0 :' || result);
                        ELSE
                            INSERT INTO logger
                            VALUES (SYSDATE, 'entered this case of exit');

                            result := -1;
                            EXIT;
                        END IF;
                    END LOOP;
            ELSE
                INSERT INTO logger
                VALUES (
                                   SYSDATE,
                                   ' Funtional Group ID is NULL:'
                                       || c1.functional_group_id);

                FOR r
                    IN (SELECT forecast_key
                        FROM pcm_forecast
                        WHERE     item_key = c1.item_key
                          AND forecast_model = FC_MODEL
                          AND status = FC_STATUS
                          AND site_key IN (SELECT site_key FROM site WHERE site_type = v_site_type)
                    )
                    LOOP
                        v_output := '[' || r.forecast_key || ']' || v_output;

                        INSERT INTO logger
                        VALUES (
                                           SYSDATE,
                                           ' v_output :'
                                               || v_output
                                               || '--->'
                                               || r.forecast_key);

                        INSERT INTO forecasts_to_close (stat_date, forecast_key, RTYPE)
                        VALUES (SYSDATE, r.forecast_key, 'RESULT');
                    END LOOP;

                result := -1;
            END IF;

            -----------------------------------
            IF result = 0
            THEN
                INSERT INTO logger
                VALUES (SYSDATE, ' Entring result=0');

                SELECT COUNT (*)
                INTO my_count
                FROM pcm_forecast
                WHERE item_key IN (c1.item_key);

                INSERT INTO logger
                VALUES (SYSDATE, ' Parent :  my_count :' || my_count);

                IF my_count >= 1
                THEN
                    INSERT INTO logger
                    VALUES (SYSDATE, ' my_count , if 1 :' || my_count);

                    SELECT COUNT (item_key)
                    INTO V_CEA_PAR
                    FROM (SELECT b.item_key
                          FROM item_fg_map b
                          WHERE b.functional_group_id IN (c1.functional_group_id)
                            AND b.item_key NOT IN
                                (SELECT item_key FROM V_PKEYS_FORECAST_EXTENSION)
                          MINUS
                          SELECT pf.item_key
                          FROM pcm_forecast pf, item_master im
                          WHERE     pf.item_key = im.item_key
                            AND pf.forecast_model = FC_MODEL
                            AND pf.status <> FC_STATUS
                            AND NVL (im.eol, 'N') <> 'Y'
                            AND NVL (im.eol_type, '1') NOT IN
                                (SELECT FORECAST_KEY
                                 FROM forecasts_to_close
                                 WHERE RTYPE = 'EOL_TYPE')
                            AND pf.item_key IN
                                (SELECT b.item_key
                                 FROM item_fg_map b
                                 WHERE b.functional_group_id IN
                                       (c1.functional_group_id)
                                   AND b.item_key NOT IN
                                       (SELECT item_key
                                        FROM V_PKEYS_FORECAST_EXTENSION)));

                    INSERT INTO logger
                    VALUES (
                                       SYSDATE,
                                       ' V_CEA_PAR , Check for 0  :'
                                           || V_CEA_PAR
                                           || '-IK->'
                                           || c1.item_key
                                           || '--FG-->'
                                           || c1.functional_group_id);

                    IF V_CEA_PAR = 0
                    THEN
                        FOR ins_fkey
                            IN (SELECT forecast_key
                                FROM pcm_forecast
                                WHERE     status = FC_STATUS
                                  AND forecast_model = FC_MODEL
                                  AND item_key IN (c1.item_key)
                                  AND site_key IN (SELECT site_key FROM site WHERE site_type = v_site_type)
                            )
                            LOOP
                                INSERT INTO logger
                                VALUES (
                                                   SYSDATE,
                                                   ' v_output inserts  :'
                                                       || ins_fkey.forecast_key);

                                INSERT
                                INTO forecasts_to_close (stat_date, forecast_key, RTYPE)
                                VALUES (SYSDATE, ins_fkey.forecast_key, 'RESULT');
                            END LOOP;
                    END IF;
                END IF;
            ELSIF result > 0
            THEN
                INSERT INTO logger
                VALUES (SYSDATE, ' Entring result<>0 : ' || result);

                FOR i IN (SELECT pf.forecast_key
                          FROM pcm_forecast pf, item_master im, item_fg_map fm, site s
                          WHERE     pf.item_key = im.item_key
                            AND pf.site_key = s.site_key
                            AND pf.item_key = fm.item_key
                            AND im.item_key = fm.item_key
                            AND pf.forecast_model = FC_MODEL
                            AND pf.status = FC_STATUS
                            AND im.eol = 'Y'
                            AND pf.item_key = c1.item_key
                            AND s.site_type = v_site_type
                            AND im.eol_type IN (SELECT FORECAST_KEY
                                                FROM forecasts_to_close
                                                WHERE RTYPE = 'EOL_TYPE'))
                    LOOP
                        INSERT INTO logger
                        VALUES (
                                           SYSDATE,
                                           ' inserting forecast key when resultt<>0 : '
                                               || i.forecast_key);

                        INSERT INTO forecasts_to_close (stat_date, forecast_key, RTYPE)
                        VALUES (SYSDATE, i.forecast_key, 'RESULT');
                    END LOOP;
            END IF;
        END LOOP;

    INSERT INTO logger
    VALUES (SYSDATE, ' END OF PROCESSING ... ');

    COMMIT;

    PROC_STATUS := 'SUCCESS';
EXCEPTION
    WHEN OTHERS
        THEN
            PROC_STATUS := 'FAILURE' || SUBSTR (SQLERRM, 1, 50);
END;
/
