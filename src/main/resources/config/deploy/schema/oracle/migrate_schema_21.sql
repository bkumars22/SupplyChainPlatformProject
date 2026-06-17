SET DEFINE OFF;

--From 2.0 to 2.1

-- Order for history
ALTER TABLE PCM_AUDIT_HISTORY ADD ACTION_ORDER NUMBER NULL;
CREATE SEQUENCE PCM_AUDIT_HISTORY_SEQ
        INCREMENT BY 1
        START WITH 1
        NOMAXVALUE
        NOMINVALUE
        CACHE 20
        NOCYCLE ORDER;

-- Insert order for the history table.
CREATE OR REPLACE TRIGGER T_PCM_AUDIT_HISTORY_ORDER
	BEFORE INSERT ON PCM_AUDIT_HISTORY
	FOR EACH ROW
BEGIN
	SELECT PCM_AUDIT_HISTORY_SEQ.NEXTVAL INTO :NEW.ACTION_ORDER FROM DUAL;
END;
/

DROP VIEW IV_ETL_ITEM_BUSINESS;
DROP VIEW IV_MARGIN_BH_BLI;
DROP VIEW IV_REBATE_BH_BLI;
DROP VIEW IV_ITEM_COMMODITY_KEYS;
DROP VIEW IV_ITEM_PLATFORM_KEYS;
DROP VIEW IV_BEST_BOM_COST_RECORD;
DROP VIEW IV_MARGIN_COST_RECORD;
DROP VIEW IV_ITEM_OWNER;

@ /scplatform/app/scplatform/deploy/schema/oracle/drop_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_margin_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_margin_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_best_bom_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_best_bom_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_audit_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_audit_model.sql;

CREATE TABLE PCM_EXTRACT_KEY
(
	OBJECT_KEY NUMBER(19) NOT NULL,
	OBJECT_TYPE VARCHAR2(32) NOT NULL
) TABLESPACE AUDIT_DATA;

CREATE UNIQUE INDEX XPEK_U1 
  ON PCM_EXTRACT_KEY(OBJECT_KEY, OBJECT_TYPE)
  TABLESPACE AUDIT_INDEX;

CREATE OR REPLACE FUNCTION concatValues(stmt IN SYS_REFCURSOR, delim IN VARCHAR, maxlen IN NUMBER) RETURN VARCHAR
AS
  v_rslt VARCHAR2(16000);
  v_next VARCHAR2(16000);
  v_end VARCHAR2(3) := '...';
  v_maxvarchar NUMBER := 16000;
  v_length NUMBER := 0;
BEGIN
  IF maxlen > v_maxvarchar THEN
     RAISE_APPLICATION_ERROR(-20000,'max length cannot be greater than ' || v_maxvarchar);
  END IF;

  LOOP
    FETCH stmt INTO v_next;
    EXIT WHEN stmt%NOTFOUND;

    IF v_length = 0 THEN
      v_rslt := v_next;
    ELSE
      IF (v_length + LENGTH(delim) + NVL(LENGTH(v_next),0) > v_maxvarchar) THEN
        v_rslt := SUBSTR(v_rslt||delim||v_next,0,v_maxvarchar - LENGTH(v_end))||v_end;
        EXIT;
      ELSE
        v_rslt := v_rslt||delim||v_next;
      END IF;
    END IF;
    
    v_length := LENGTH(v_rslt);

    IF v_length > maxlen THEN
      IF LENGTH(SUBSTR(v_rslt,0,maxlen) || v_end) > v_maxvarchar THEN
        v_rslt := SUBSTR(v_rslt,0,v_maxvarchar - LENGTH(v_end))||v_end;
      ELSE
        v_rslt := SUBSTR(v_rslt,0,maxlen) || v_end;
      END IF;
      EXIT;
    END IF;
  END LOOP;

  RETURN v_rslt;
END;
/
