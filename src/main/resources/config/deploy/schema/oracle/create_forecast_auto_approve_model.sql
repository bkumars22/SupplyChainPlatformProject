WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

CREATE TABLE forecast_auto_approve_log
(
  extract_key VARCHAR2(32) NOT NULL,
  message_date TIMESTAMP NOT NULL,
  message VARCHAR2(1024) NOT NULL
) TABLESPACE AUDIT_DATA;

CREATE INDEX XFAAL_I1 ON forecast_auto_approve_log(extract_key) TABLESPACE AUDIT_INDEX;

CREATE PACKAGE ForecastAutoApprove AS
  v_extract_key VARCHAR2(32);
  v_action_performed pcm_audit_history.action_performed%TYPE;
  v_user_id pcm_audit_history.user_id%TYPE;
  v_role_id pcm_audit_history.role_id%TYPE;
  
  PROCEDURE AutoApprove(
    out_result out SYS_REFCURSOR,
    p_forecast_type pcm_forecast.forecast_type%TYPE,
    p_period_type pcm_forecast.period_type%TYPE,
    p_calendar_name pcm_forecast.calendar_name%TYPE,
    p_wait_days NUMBER,
    p_user_id pcm_user.user_id%TYPE);
    
  PROCEDURE approveForecast(
    p_adj_forecast_key pcm_forecast.forecast_key%TYPE,
    p_current_forecast_key pcm_forecast.forecast_key%TYPE,
    p_item_key item_master.item_key%TYPE,
    p_site_key site.site_key%TYPE,
    p_forecast_type pcm_forecast.forecast_type%TYPE,
    p_period_type pcm_forecast.period_type%TYPE,
    p_calendar_name pcm_forecast.calendar_name%TYPE,
    p_user_id pcm_user.user_id%TYPE,
    p_forecast_external_id pcm_forecast.forecast_external_id%TYPE,
    p_evalDate DATE);
    
  FUNCTION calculateForecastValue(
    forecastValue IN pcm_forecast_value%ROWTYPE,
    forecastValueAdj IN pcm_forecast_value_adjustment%ROWTYPE)
    RETURN NUMBER;
    
  FUNCTION getJsonSavedStates(
    forecastValue IN pcm_forecast_value%ROWTYPE,
    forecastValueAdj IN pcm_forecast_value_adjustment%ROWTYPE,
    p_user_id IN pcm_user.user_id%TYPE,
    p_timestamp IN TIMESTAMP  WITH TIME ZONE,
    p_state_key IN VARCHAR2)
    RETURN VARCHAR2;
    
  FUNCTION getISO8601(
    p_timestamp IN TIMESTAMP WITH TIME ZONE)
    RETURN VARCHAR2;
    
  PROCEDURE auditStateChange(
    p_forecast_key pcm_forecast.forecast_key%TYPE,
    p_user_id pcm_user.user_id%TYPE,
    p_previousState pcm_forecast.status%TYPE);
    
  PROCEDURE auditSave(
    p_forecast_key pcm_forecast.forecast_key%TYPE,
    p_user_id pcm_user.user_id%TYPE);
    
  PROCEDURE auditCreateForecast(
    p_forecast_key pcm_forecast.forecast_key%TYPE,
    p_user_id pcm_user.user_id%TYPE);
    
  PROCEDURE auditForecastValue(
    p_forecast_key pcm_forecast.forecast_key%TYPE,
    p_user_id pcm_user.user_id%TYPE,
    p_effective_from_dt pcm_forecast_value.effective_from_dt%TYPE,
    p_effective_to_dt pcm_forecast_value.effective_to_dt%TYPE,
    p_from_value pcm_forecast_value.forecast_value%TYPE,
    p_to_value pcm_forecast_value.forecast_value%TYPE);
    
  FUNCTION getForecastAuditStr(
    p_forecast_key IN pcm_forecast.forecast_key%TYPE)
    RETURN VARCHAR2;
	
  PROCEDURE logMessage(p_message forecast_auto_approve_log.message%TYPE);
END ForecastAutoApprove;
/

CREATE PACKAGE BODY ForecastAutoApprove AS
  PROCEDURE AutoApprove(
    out_result out SYS_REFCURSOR,
    p_forecast_type pcm_forecast.forecast_type%TYPE,
    p_period_type pcm_forecast.period_type%TYPE,
    p_calendar_name pcm_forecast.calendar_name%TYPE,
    p_wait_days NUMBER,
    p_user_id pcm_user.user_id%TYPE)
	AS
	abort_detected EXCEPTION;
	PRAGMA EXCEPTION_INIT(abort_detected,-20008);
	v_abort NUMBER(5) := 0;
	v_evalDate DATE;
	v_cnt NUMBER := 0;
	v_adj_forecast_key pcm_forecast.forecast_key%TYPE;
	v_adj_item_key item_master.item_key%TYPE;
	v_adj_site_key site.site_key%TYPE;
	v_adj_forecast_model pcm_forecast.forecast_model%TYPE;
	v_adj_forecast_external_id pcm_forecast.forecast_external_id%TYPE;
	v_current_forecast_key pcm_forecast.forecast_key%TYPE;
	v_current_item_key item_master.item_key%TYPE;
	v_current_site_key site.site_key%TYPE;
	v_current_forecast_model pcm_forecast.forecast_model%TYPE;
  
	-- get the ADJUSTABLE PENDING forecast f1 where
	-- there is a CURRENT forecast f2 for the same item and site that is NEW, PENDING, or CLOSED or it is APPROVED and was created from an ADJUSTABLE
	-- there is a CURRENT forecast f3 for the same item but with GLOBAL site that is NEW, PENDING, or CLOSED or it is APPROVED and was created from an ADJUSTABLE
    CURSOR c_Forecasts IS
    SELECT  f1.forecast_key, f1.item_key, f1.site_key, f1.forecast_model, f1.forecast_external_id 
	  FROM pcm_forecast f1
	  JOIN item_item_category iic on
	  iic.item_key = f1.item_key
	  left join pcm_forecast_auto_approve_cat r on
	  r.item_category_key = iic.item_category_key
	  left join pcm_forecast f2 on
	  f2.item_key = f1.item_key and
	  f2.site_key = f1.site_key and
	  f2.period_type = f1.period_type and
	  f2.calendar_name = f1.calendar_name and
	  f2.forecast_type = f1.forecast_type and
	  f2.forecast_model = 'CURRENT'
	  left join site s2 on
	  s2.site_key = f2.site_key
	  left join pcm_forecast f3 on
	  f3.item_key = f1.item_key and
	  f3.period_type = f1.period_type and
	  f3.calendar_name = f1.calendar_name and
	  f3.forecast_type = f1.forecast_type and
	  f3.forecast_model = 'CURRENT' and
	  f3.site_key in (select site_key from site where site_type = 'GLOBAL')
	  WHERE f1.period_type = p_period_type
	  and f1.calendar_name = p_calendar_name
	  and f1.forecast_type = p_forecast_type
	  and f1.forecast_model = 'ADJUSTABLE'
	  and f1.status = 'PENDING'
	  and
	  (
	    (f2.forecast_key is null OR f2.status in ('NEW','PENDING','CLOSED') OR (f2.status = 'APPROVED' and f2.forecast_origin_external_id is not null and f2.status_last_change_by = p_user_id)) AND
	    (f3.forecast_key is null OR f3.status in ('NEW','PENDING','CLOSED') OR (f3.status = 'APPROVED' and f3.forecast_origin_external_id is not null and f3.status_last_change_by = p_user_id)) AND
	    ((p_wait_days >= 0 and trunc(f1.status_change_date) + p_wait_days <= trunc(v_evalDate) and r.active is null) OR 
	     (trunc(f1.status_change_date) + r.wait_days <= trunc(v_evalDate) AND r.active = 1)
	    )
	  )
	  and exists
	  (
	    select 1
	    from pcm_forecast_value fv
	    where fv.forecast_key = f1.forecast_key
	    and effective_to_dt >= trunc(v_evalDate)
	  );
  BEGIN
	v_action_performed := 'FORECAST_AUTO_APPROVE';
	
    SELECT sys_guid() INTO v_extract_key FROM dual;
  
    -- Insert the running job status
	INSERT INTO pcm_job_status_control (
	job_id,
	job_type,
	job_status,
  	job_start_date,
  	job_end_date)
	VALUES (
	v_extract_key,
	v_action_performed,
	'RUNNING',
	current_timestamp,
	null);
	
	COMMIT;
	
	SELECT sysdate INTO v_evalDate FROM dual;
	
    -- loop thru all the boms that need to be auto approved
    OPEN c_Forecasts;
    LOOP
      FETCH c_Forecasts
      INTO v_adj_forecast_key,
      v_adj_item_key,
      v_adj_site_key,
      v_adj_forecast_model,
      v_adj_forecast_external_id;
      EXIT WHEN c_Forecasts%NOTFOUND;
      
      v_current_forecast_key := NULL;
	  v_current_item_key := NULL;
	  v_current_site_key := NULL;
	  v_current_forecast_model := NULL;
	  
	  -- get the current forecast for the adjustable forecast
      BEGIN
	    SELECT forecast_key
	    ,item_key
	    ,site_key
	    ,forecast_model
	    INTO
	    v_current_forecast_key
	    ,v_current_item_key
	    ,v_current_site_key
	    ,v_current_forecast_model
	    FROM pcm_forecast
	    WHERE item_key = v_adj_item_key
	    AND site_key = v_adj_site_key
	    AND forecast_model = 'CURRENT';
	    
	    EXCEPTION
		  WHEN NO_DATA_FOUND THEN NULL;
	  END;
      
      approveForecast(v_adj_forecast_key,
      v_current_forecast_key,
      v_adj_item_key,
      v_adj_site_key,
      p_forecast_type,
      p_period_type,
      p_calendar_name,
      p_user_id,
      v_adj_forecast_external_id,
      v_evalDate);
    END LOOP;
    v_cnt := c_Forecasts%ROWCOUNT;
    CLOSE c_Forecasts;
    
	-- Update the job status to complete
	UPDATE pcm_job_status_control
	SET job_status = 'COMPLETE',
	job_end_date = current_timestamp
	WHERE job_id = v_extract_key;
	
	COMMIT;
	
	OPEN out_result FOR
	SELECT v_cnt as count FROM dual;
	
	EXCEPTION
      WHEN abort_detected THEN
		UPDATE pcm_job_status_control
		SET job_status = 'ABORTED',
		job_end_date = current_timestamp
		WHERE job_id = v_extract_key;
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
      WHEN OTHERS THEN
        ROLLBACK;
        
		UPDATE pcm_job_status_control
		SET job_status = 'ERROR',
		job_end_date = current_timestamp
		WHERE job_id = v_extract_key;
	    
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END;
  
  PROCEDURE approveForecast(
    p_adj_forecast_key pcm_forecast.forecast_key%TYPE,
    p_current_forecast_key pcm_forecast.forecast_key%TYPE,
    p_item_key item_master.item_key%TYPE,
    p_site_key site.site_key%TYPE,
    p_forecast_type pcm_forecast.forecast_type%TYPE,
    p_period_type pcm_forecast.period_type%TYPE,
    p_calendar_name pcm_forecast.calendar_name%TYPE,
    p_user_id pcm_user.user_id%TYPE,
    p_forecast_external_id pcm_forecast.forecast_external_id%TYPE,
    p_evalDate DATE)
    AS
    v_current_forecast_key pcm_forecast.forecast_key%TYPE;
    v_forecastValue pcm_forecast_value%ROWTYPE;
    v_forecastValueAdj pcm_forecast_value_adjustment%ROWTYPE;
    v_current_forecastValue pcm_forecast_value%ROWTYPE;
    v_value pcm_forecast_value.forecast_value%TYPE;
    v_saved_states pcm_forecast_value.saved_states%TYPE;
    v_timestamp TIMESTAMP WITH TIME ZONE;
    v_previousState pcm_forecast.status%TYPE := 'PENDING';
    
    CURSOR c_ForecastValues IS
    SELECT *
    FROM pcm_forecast_value
    WHERE forecast_key = p_adj_forecast_key
    AND (effective_to_dt IS NULL or effective_to_dt >= p_evalDate)
    ORDER BY effective_from_dt ASC;
    
    CURSOR c_RemoveCurrentForecastValues IS
    SELECT *
    FROM pcm_forecast_value
    WHERE forecast_key = v_current_forecast_key
    AND effective_from_dt > v_forecastValue.effective_to_dt
    ORDER BY effective_from_dt ASC;
  BEGIN
	  v_current_forecast_key := p_current_forecast_key;
	  
	  -- set the status to APPROVED for the adjustable forecast
	  UPDATE pcm_forecast
	  SET status = 'APPROVED',
	  status_change_date = systimestamp,
	  status_last_change_by = p_user_id,
	  last_change_by = p_user_id,
	  update_dt = systimestamp,
	  current_flag = 'N'
	  WHERE forecast_key = p_adj_forecast_key;
	  
	  -- create audit record
	  auditStateChange(p_adj_forecast_key, p_user_id, v_previousState);
	  auditSave(p_adj_forecast_key, p_user_id);
	  
	  -- create a current forecast if none exists
	  IF v_current_forecast_key IS NULL THEN
	    SELECT pcm_forecast_seq.nextval
	    INTO v_current_forecast_key
	    FROM DUAL;
	    
	    INSERT INTO pcm_forecast
	    (forecast_key,
	    forecast_external_id,
	    item_key,
	    site_key,
	    forecast_type,
	    period_type,
	    calendar_name,
	    forecast_model,
	    status,
	    insert_dt,
	    delete_flag,
	    current_flag,
	    audit_rev)
	    values
	    (v_current_forecast_key,
	    sys_guid(),
	    p_item_key,
	    p_site_key,
	    p_forecast_type,
	    p_period_type,
	    p_calendar_name,
	    'CURRENT',
	    'APPROVED',
	    systimestamp,
	    'N',
	    'N',
	    0);
	    
	    -- create audit record
	    auditCreateForecast(v_current_forecast_key,p_user_id);
	    v_previousState := null;
	  END IF;
	  
	  SELECT systimestamp
	  INTO v_timestamp
	  FROM DUAL;
	  
	  -- update the status to approved for the current forecast
	  UPDATE pcm_forecast
	  SET status = 'APPROVED',
	  status_change_date = v_timestamp,
	  status_last_change_by = p_user_id,
	  last_change_by = p_user_id,
	  update_dt = v_timestamp,
	  remaining_rollovers = null,
	  current_flag = 'N',
	  forecast_origin_external_id = p_forecast_external_id
	  WHERE forecast_key = v_current_forecast_key;
	  
	  -- create audit record
	  auditStateChange(v_current_forecast_key, p_user_id, v_previousState);
	  auditSave(v_current_forecast_key, p_user_id);
	  
	  -- copy the adjustable forecast values to the current forecast
	  OPEN c_ForecastValues;
	  LOOP
	    FETCH c_ForecastValues INTO v_forecastValue;
	    EXIT WHEN c_ForecastValues%NOTFOUND;
	    
	    v_forecastValueAdj := NULL;
	    BEGIN
		  SELECT *
		  INTO v_forecastValueAdj
		  FROM pcm_forecast_value_adjustment
		  WHERE forecast_value_key = v_forecastValue.forecast_value_key;
		  
		  EXCEPTION
		    WHEN NO_DATA_FOUND THEN NULL;
		END;
	    
		-- get the calculated forecast value and the json string
		v_value := calculateForecastValue(v_forecastValue, v_forecastValueAdj);
		v_saved_states := getJsonSavedStates(v_forecastValue, v_forecastValueAdj, p_user_id, v_timestamp, 'lav');
		
		-- set the json saved state
		UPDATE pcm_forecast_value
		SET saved_states = v_saved_states
		WHERE forecast_value_key = v_forecastValue.forecast_value_key;
		  
	    BEGIN
		  -- get the json saved states for the current forecast
		  v_saved_states := getJsonSavedStates(v_forecastValue, v_forecastValueAdj, p_user_id, v_timestamp, 'aprAdjVal');
		  
		  -- get the current forecast value for the same time frame
		  -- as the adjustable forecast value
		  SELECT * INTO v_current_forecastValue
		  FROM pcm_forecast_value
		  WHERE forecast_key = v_current_forecast_key
		  AND effective_from_dt = v_forecastValue.effective_from_dt
		  AND effective_to_dt = v_forecastValue.effective_to_dt;
		  
		  -- update the current forecast value
		  -- with the adjustable forecast value
		  UPDATE pcm_forecast_value
		  SET forecast_value = v_value,
		  saved_states = v_saved_states
		  WHERE forecast_value_key = v_current_forecastValue.forecast_value_key;
		  
		  -- create audit record
		  auditForecastValue(v_current_forecast_key, p_user_id, v_forecastValue.effective_from_dt, v_forecastValue.effective_to_dt, v_current_forecastValue.forecast_value, v_value);
		  
		  -- No current forecast value exists, create one
		  EXCEPTION
		    WHEN NO_DATA_FOUND THEN
		    INSERT INTO pcm_forecast_value
		    (forecast_value_key, forecast_key, forecast_value, forecast_measure_key, effective_from_dt, effective_to_dt, audit_rev, forecast_value_type, saved_states)
		    VALUES
		    (pcm_forecast_value_seq.nextval,
		    v_current_forecast_key,
		    v_value,
		    v_forecastValue.forecast_measure_key,
		    v_forecastValue.effective_from_dt,
		    v_forecastValue.effective_to_dt,
		    0,
		    'S',
		    v_saved_states
		    );
		    
		    -- create audit record
		    auditForecastValue(v_current_forecast_key, p_user_id, v_forecastValue.effective_from_dt, v_forecastValue.effective_to_dt, null, v_value);
	    END;
	  END LOOP;
	  CLOSE c_ForecastValues;
	  
	  -- Audit the removal of all future current forecast values
	  -- that are beyond the adjustable forecast values
	  OPEN c_RemoveCurrentForecastValues;
	  LOOP
	    FETCH c_RemoveCurrentForecastValues INTO v_current_forecastValue;
	    EXIT WHEN c_RemoveCurrentForecastValues%NOTFOUND;
	    
	    -- create audit record
	    auditForecastValue(v_current_forecast_key, p_user_id, v_current_forecastValue.effective_from_dt, v_current_forecastValue.effective_to_dt, null, null);
	  END LOOP;
	  CLOSE c_RemoveCurrentForecastValues;
	  
	  -- remove of all future current forecast values
	  -- that are beyond the adjustable forecast values
	  DELETE FROM pcm_forecast_value
	  WHERE forecast_key = p_current_forecast_key
	  AND effective_from_dt > v_forecastValue.effective_to_dt;
  END;
  
  FUNCTION calculateForecastValue(
    forecastValue IN pcm_forecast_value%ROWTYPE,
    forecastValueAdj IN pcm_forecast_value_adjustment%ROWTYPE)
    RETURN NUMBER IS
    v_forecast_value pcm_forecast_value.forecast_value%TYPE;
  BEGIN
	IF forecastValueAdj.forecast_value_key IS NULL THEN
	  v_forecast_value := forecastValue.adjustable_value;
	ELSE
      IF forecastValueAdj.adjustment_type = 'FIXED' THEN
        v_forecast_value := forecastValue.adjustable_value + forecastValueAdj.adjustment_amount;
      END IF;
    
      IF forecastValueAdj.adjustment_type = 'PERCENT' THEN
        v_forecast_value := forecastValue.adjustable_value + (forecastValue.adjustable_value * (forecastValueAdj.adjustment_amount/100));
      END IF;
    END IF;
    
    IF v_forecast_value IS NOT NULL THEN
      v_forecast_value := ROUND(v_forecast_value,6);
    END IF;
    
    RETURN v_forecast_value;
  END;
  
  FUNCTION getJsonSavedStates(
    forecastValue IN pcm_forecast_value%ROWTYPE,
    forecastValueAdj IN pcm_forecast_value_adjustment%ROWTYPE,
    p_user_id IN pcm_user.user_id%TYPE,
    p_timestamp IN TIMESTAMP WITH TIME ZONE,
    p_state_key IN VARCHAR2)
    RETURN VARCHAR2 IS
    v_saved_states pcm_forecast_value.saved_states%TYPE;
  BEGIN
	v_saved_states := '{"'||p_state_key||'":{';
	v_saved_states := v_saved_states||'"mk":"'||forecastValue.forecast_measure_key||'"';
    v_saved_states := v_saved_states||',"uom":';
    IF forecastValue.forecast_uom IS NULL THEN
      v_saved_states := v_saved_states||'null';
    ELSE
      v_saved_states := v_saved_states||'"'||forecastValue.forecast_uom||'"';
    END IF;
    v_saved_states := v_saved_states||',"fdt":"'||getISO8601(forecastValue.effective_from_dt)||'"';
    v_saved_states := v_saved_states||',"tdt":"'||getISO8601(forecastValue.effective_from_dt)||'"';
    v_saved_states := v_saved_states||',"av":'||forecastValue.adjustable_value;
    v_saved_states := v_saved_states||',"amt":';
    IF forecastValueAdj.adjustment_amount IS NULL THEN
      v_saved_states := v_saved_states||'null';
    ELSE
      v_saved_states := v_saved_states||forecastValueAdj.adjustment_amount;
    END IF;
    v_saved_states := v_saved_states||',"at":';
    IF forecastValueAdj.adjustment_type IS NULL THEN
      v_saved_states := v_saved_states||'null';
    ELSE
      v_saved_states := v_saved_states||'"'||forecastValueAdj.adjustment_type||'"';
    END IF;
    v_saved_states := v_saved_states||',"aprUsr":{"uid":"'||p_user_id||'","uname":"'||p_user_id||'"}';
    v_saved_states := v_saved_states||',"aprDt":"'||getISO8601(p_timestamp)||'"}}';
	RETURN v_saved_states;
  END;
  
  FUNCTION getISO8601(
    p_timestamp IN TIMESTAMP WITH TIME ZONE)
    RETURN VARCHAR2 IS
    v_iso_timestamp VARCHAR2(256);
  BEGIN
    SELECT TO_CHAR(p_timestamp,'YYYY-MM-DD"T"hh24:mi:sstzh:tzm')
    INTO v_iso_timestamp
    FROM dual;
    
    RETURN v_iso_timestamp;
  END;
  
  PROCEDURE auditStateChange(
    p_forecast_key pcm_forecast.forecast_key%TYPE,
    p_user_id pcm_user.user_id%TYPE,
    p_previousState pcm_forecast.status%TYPE) AS
    v_audit_record_key pcm_audit_history.audit_record_key%TYPE;
    v_action_comment pcm_audit_history.action_comment%TYPE;
  BEGIN
	SELECT sys_guid() INTO v_audit_record_key FROM DUAL;
	
	v_action_comment := getForecastAuditStr(p_forecast_key)||';Event:Auto Approve';
	IF p_previousState IS NULL THEN
	  v_action_comment := v_action_comment||' Assign:APPROVED';
	ELSE
	  v_action_comment := v_action_comment||' From:'||p_previousState||' To:APPROVED';
	END IF;
	
	INSERT INTO pcm_audit_history
	(audit_record_key,
	action_date,
	action_performed,
	action_order,
	user_id,
	pcm_target_type,
	action_comment)
	VALUES
	(v_audit_record_key,
	systimestamp,
	'STATE_CHANGE',
	pcm_audit_history_seq.nextval,
	p_user_id,
	'PcmForecast',
	v_action_comment);
	
	INSERT INTO pcm_audit_history_targets
	(audit_record_key,
	pcm_target_key)
	VALUES
	(v_audit_record_key,
	to_char(p_forecast_key));
  END;
  
  PROCEDURE auditSave(
    p_forecast_key pcm_forecast.forecast_key%TYPE,
    p_user_id pcm_user.user_id%TYPE) AS
    v_audit_record_key pcm_audit_history.audit_record_key%TYPE;
  BEGIN
	SELECT sys_guid() INTO v_audit_record_key FROM DUAL;
	
	INSERT INTO pcm_audit_history
	(audit_record_key,
	action_date,
	action_performed,
	action_order,
	user_id,
	pcm_target_type,
	action_comment)
	VALUES
	(v_audit_record_key,
	systimestamp,
	'SAVE',
	pcm_audit_history_seq.nextval,
	p_user_id,
	'PcmForecast',
	getForecastAuditStr(p_forecast_key));
	
	INSERT INTO pcm_audit_history_targets
	(audit_record_key,
	pcm_target_key)
	VALUES
	(v_audit_record_key,
	to_char(p_forecast_key));
  END;
  
  PROCEDURE auditCreateForecast(
    p_forecast_key pcm_forecast.forecast_key%TYPE,
    p_user_id pcm_user.user_id%TYPE) AS
    v_audit_record_key pcm_audit_history.audit_record_key%TYPE;
  BEGIN
	SELECT sys_guid() INTO v_audit_record_key FROM DUAL;
	
	INSERT INTO pcm_audit_history
	(audit_record_key,
	action_date,
	action_performed,
	action_order,
	user_id,
	pcm_target_type,
	action_comment)
	VALUES
	(v_audit_record_key,
	systimestamp,
	'CREATE_FROM_ADJUSTABLE_APPROVE',
	pcm_audit_history_seq.nextval,
	p_user_id,
	'PcmForecast',
	getForecastAuditStr(p_forecast_key));
	
	INSERT INTO pcm_audit_history_targets
	(audit_record_key,
	pcm_target_key)
	VALUES
	(v_audit_record_key,
	to_char(p_forecast_key));
  END;
  
  PROCEDURE auditForecastValue(
    p_forecast_key pcm_forecast.forecast_key%TYPE,
    p_user_id pcm_user.user_id%TYPE,
    p_effective_from_dt pcm_forecast_value.effective_from_dt%TYPE,
    p_effective_to_dt pcm_forecast_value.effective_to_dt%TYPE,
    p_from_value pcm_forecast_value.forecast_value%TYPE,
    p_to_value pcm_forecast_value.forecast_value%TYPE) AS
    v_audit_record_key pcm_audit_history.audit_record_key%TYPE;
    v_action_comment pcm_audit_history.action_comment%TYPE;
  BEGIN
	SELECT sys_guid() INTO v_audit_record_key FROM DUAL;
	
	v_action_comment := getForecastAuditStr(p_forecast_key)||';VALUE:ACTUALFORECAST';
	v_action_comment := v_action_comment||'[Start='||TO_CHAR(p_effective_from_dt,'DD-MON-YYYY');
	v_action_comment := v_action_comment||', End='||TO_CHAR(p_effective_to_dt,'DD-MON-YYYY')||']';
	IF p_to_value IS NULL THEN
	  v_action_comment := v_action_comment||' Cleared' ;
	ELSE
	  IF p_from_value IS NULL THEN
	    v_action_comment := v_action_comment||' Assign:'||p_to_value;
	  ELSE
	    v_action_comment := v_action_comment||' From:'||p_from_value||' To:'||p_to_value;
	  END IF;
	END IF;
	
	INSERT INTO pcm_audit_history
	(audit_record_key,
	action_date,
	action_performed,
	action_order,
	user_id,
	pcm_target_type,
	action_comment)
	VALUES
	(v_audit_record_key,
	systimestamp,
	'FIELD_CHANGE',
	pcm_audit_history_seq.nextval,
	p_user_id,
	'PcmForecast',
	v_action_comment);
	
	INSERT INTO pcm_audit_history_targets
	(audit_record_key,
	pcm_target_key)
	VALUES
	(v_audit_record_key,
	to_char(p_forecast_key));
  END;
  
  FUNCTION getForecastAuditStr(
    p_forecast_key IN pcm_forecast.forecast_key%TYPE)
    RETURN VARCHAR2 IS
    v_forecast_audit VARCHAR2(1024);
    v_forecast_type pcm_forecast.forecast_type%TYPE;
    v_item_identifier item_master.item_identifier%TYPE;
    v_site_description site.site_description%TYPE;
    v_status pcm_forecast.status%TYPE;
    v_forecast_model pcm_forecast.forecast_model%TYPE;
  BEGIN
	SELECT f.forecast_type
	,i.item_identifier
	,s.site_description
	,f.status
	,f.forecast_model
	INTO
	v_forecast_type
	,v_item_identifier
	,v_site_description
	,v_status
	,v_forecast_model
	FROM pcm_forecast f
	JOIN item_master i ON
	i.item_key = f.item_key
	JOIN site s ON
	s.site_key = f.site_key
	WHERE f.forecast_key = p_forecast_key;
	
	v_forecast_audit := v_forecast_type||' [Item='||v_item_identifier||', Site='||v_site_description||', Status='||v_status||', Model='||v_forecast_model||']';
	
	RETURN v_forecast_audit;
  END;
  
  PROCEDURE logMessage(p_message forecast_auto_approve_log.message%TYPE) AS
  BEGIN
    INSERT INTO forecast_auto_approve_log
	(extract_key,message_date,message)
	VALUES
	(v_extract_key,systimestamp,p_message);
  END;
end ForecastAutoApprove;
/
