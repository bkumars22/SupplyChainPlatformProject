WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

-- Add the forecast_model field to the pcm_forecast table and set the value
-- for all current records to ACTUAL
ALTER TABLE PCM_FORECAST ADD FORECAST_MODEL VARCHAR2(64);

DECLARE
l_cnt NUMBER := 1;
BEGIN
  WHILE l_cnt <> 0 LOOP
     UPDATE PCM_FORECAST SET FORECAST_MODEL = 'CURRENT' WHERE FORECAST_MODEL IS NULL AND ROWNUM < 25001;   
     l_cnt := SQL%ROWCOUNT;
     COMMIT;
  END LOOP;
END;
/

ALTER TABLE PCM_FORECAST MODIFY FORECAST_MODEL VARCHAR2(64) NOT NULL;

DROP INDEX XFOR_U2;
CREATE UNIQUE INDEX XFOR_U2
	ON PCM_FORECAST(ITEM_KEY, SITE_KEY, FORECAST_TYPE, FORECAST_MODEL)
	TABLESPACE AUDIT_INDEX;

-- Add the adjustable_value field to the pcm_forecast_value table
ALTER TABLE PCM_FORECAST_VALUE ADD ADJUSTABLE_VALUE DECIMAL(19,6) NULL;

-- Add the FORECAST_VALUE_TYPE field to the PCM_FORECAST_VALUE table and set the value
-- for all current records to S for simple
ALTER TABLE PCM_FORECAST_VALUE ADD FORECAST_VALUE_TYPE VARCHAR2(1);

DECLARE
l_cnt NUMBER := 1;
BEGIN
  WHILE l_cnt <> 0 LOOP
     UPDATE PCM_FORECAST_VALUE SET FORECAST_VALUE_TYPE = 'S' WHERE FORECAST_VALUE_TYPE IS NULL AND ROWNUM < 25001;   
     l_cnt := SQL%ROWCOUNT;
     COMMIT;
  END LOOP;
END;
/

ALTER TABLE PCM_FORECAST_VALUE MODIFY FORECAST_VALUE_TYPE VARCHAR2(1) NOT NULL;

DROP INDEX XFORVAL_U1;
CREATE UNIQUE INDEX XFORVAL_U1 
  ON PCM_FORECAST_VALUE(FORECAST_KEY, FORECAST_VALUE_TYPE, FORECAST_MEASURE_KEY, EFFECTIVE_FROM_DT)
  TABLESPACE AUDIT_INDEX;

-- Add the LAST_APPROVED_VALUE field to the PCM_FORECAST_VALUE table
-- to store a JSON object with the last approved forecast value information
ALTER TABLE PCM_FORECAST_VALUE ADD SAVED_STATES VARCHAR2(1024);
  
-- Add the PCM_FORECAST_VALUE_ADJUSTMENT
CREATE TABLE PCM_FORECAST_VALUE_ADJUSTMENT
(
    FORECAST_VALUE_KEY NUMBER(19) NOT NULL,
    ADJUSTMENT_AMOUNT NUMBER(19,6),
    ADJUSTMENT_TYPE VARCHAR2(32),
    CONSTRAINT PCMFA_FV_FK FOREIGN KEY(FORECAST_VALUE_KEY)
       REFERENCES PCM_FORECAST_VALUE ON DELETE CASCADE,
    CONSTRAINT PCMFA_PK PRIMARY KEY(FORECAST_VALUE_KEY)
) TABLESPACE AUDIT_DATA;

-- Add the pcm_forecast_auto_approve_cat table to store
-- the number of days to wait before a forecast that is
-- related to a commodity can be approved
CREATE TABLE PCM_FORECAST_AUTO_APPROVE_CAT
(
  ITEM_CATEGORY_KEY NUMBER(19) NOT NULL,
  WAIT_DAYS NUMBER(3) NOT NULL,
  ACTIVE NUMBER(1),
  CONSTRAINT PCMFVAABC_PK PRIMARY KEY(ITEM_CATEGORY_KEY),
  CONSTRAINT PCMFVAABC_IC_FK FOREIGN KEY(ITEM_CATEGORY_KEY)
       REFERENCES ITEM_CATEGORY ON DELETE CASCADE
) TABLESPACE AUDIT_DATA;

-- Add the forecast_origin_external_id to the pcm_forecast table
ALTER TABLE PCM_FORECAST ADD FORECAST_ORIGIN_EXTERNAL_ID VARCHAR2(64);

-- Add the last change by to the pcm_forecast table
ALTER TABLE PCM_FORECAST ADD LAST_CHANGE_BY VARCHAR2(64);

-- Update the acl to change the upload type from ForecastUI
-- to CurrentForecastUI and AdjustableForecastUI
DECLARE
  v_access_control_key pcm_access_control.access_control_key%TYPE;
  v_role_key pcm_access_control.role_key%TYPE;
  
  CURSOR c_uploadforecast IS
    SELECT access_control_key,role_key
    FROM pcm_access_control
    WHERE entity_type = 'UPLOAD_TYPE'
    AND acl = 'ForecastUI';
BEGIN
  OPEN c_uploadforecast;
  LOOP
    FETCH c_uploadforecast INTO v_access_control_key, v_role_key;
    EXIT WHEN c_uploadforecast%NOTFOUND;
    
    UPDATE pcm_access_control
    SET acl = 'CurrentForecastUI'
    WHERE access_control_key = v_access_control_key;
    
    INSERT INTO pcm_access_control
    (access_control_key,entity_type,role_key,acl)
    values
    (pcm_acl_seq.nextval,'UPLOAD_TYPE',v_role_key,'AdjustableForecastUI');
      
  END LOOP;
    
  CLOSE c_uploadforecast;
  
  COMMIT;
END;
/

CREATE INDEX XPCMCRRNG_N1 ON PCM_COST_RECORD_RANGE(COST_RECORD_KEY) TABLESPACE AUDIT_INDEX;
