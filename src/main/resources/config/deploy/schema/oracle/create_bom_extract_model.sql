WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

-- table to store the ouput of the collab extract calculation
CREATE TABLE PCM_BOM_EXTRACT
(
  EXTRACT_KEY VARCHAR2(32) NOT NULL,
  OP_CODE VARCHAR2(32) NOT NULL,
  COMPONENT_BUSINESS_ENTITY_KEY NUMBER(19) NOT NULL,
  BOM_KEY NUMBER(19),
  ITEM_KEY NUMBER(19),
  BOM_LINE_ITEM_KEY NUMBER(19),
  COMPONENT_ITEM_KEY NUMBER(19),
  ITEM_QUANTITY NUMBER(19, 5),
  EFFECTIVE_FROM_DT DATE,
  EFFECTIVE_TO_DT DATE,
  COMPONENT_EFFECTIVE_FROM_DT DATE,
  COMPONENT_EFFECTIVE_TO_DT DATE,
  SITE_KEY NUMBER(19),
  ITEM_LEAD_TIME NUMBER(19,4),
  COMPONENT_ITEM_LEAD_TIME NUMBER(19,4),
  ITEM_COST NUMBER(19,4),
  ALTERNATE_GROUP_ID VARCHAR2(255),
  ALTERNATE_TYPE VARCHAR2(64),
  ALTERNATE_PRIORITY NUMBER(2)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPBE_I1 ON PCM_BOM_EXTRACT(extract_key) TABLESPACE AUDIT_INDEX;
CREATE INDEX XPBE_I2 ON PCM_BOM_EXTRACT(extract_key,item_key,component_item_key) TABLESPACE AUDIT_INDEX;

CREATE OR REPLACE FUNCTION F_E2CC_BOM_EXTRACT_DT(
  p_component_effective_from_dt in DATE
  ,p_component_effective_to_dt in DATE
  ,p_effective_dt in DATE
  ,p_component_effective_dt in DATE)
  RETURN DATE
IS
BEGIN
	IF p_component_effective_from_dt IS NOT NULL OR p_component_effective_to_dt IS NOT NULL THEN
		RETURN p_component_effective_dt;
	END IF;
	
	RETURN p_effective_dt;
END;
/

-- the view to retrieve the output of the collab calculation along with other meta data
CREATE OR REPLACE VIEW IV_E2CC_BOM_EXTRACT AS
SELECT ib.business_entity_identifier itemOrg
,i.item_identifier item
,cib.business_entity_identifier componentItemOrg
,ci.item_identifier componentItem
,ib.business_entity_identifier siteOrg
,ib.business_entity_identifier site
,to_number(ci.gbl_prod_lfcycl_code_other,'9999999999999999999') leadTime
,trunc(pce.item_quantity) quantityPer
,F_E2CC_BOM_EXTRACT_DT(pce.component_effective_from_dt,pce.component_effective_to_dt,pce.effective_from_dt,pce.component_effective_from_dt) startDate
,F_E2CC_BOM_EXTRACT_DT(pce.component_effective_from_dt,pce.component_effective_to_dt,pce.effective_to_dt,pce.component_effective_to_dt) endDate
,pce.item_cost itemCost
,pce.alternate_group_id name
,pce.alternate_type type
,pce.alternate_priority priority
,pce.op_code
,pce.extract_key
FROM pcm_bom_extract pce
JOIN business_entity cib ON
cib.business_entity_key = pce.component_business_entity_key
LEFT JOIN item_master ci ON
ci.item_key = pce.component_item_key
LEFT JOIN item_master i ON
i.item_key = pce.item_key
LEFT JOIN business_entity ib ON
ib.business_entity_key = i.business_entity_key;

CREATE OR REPLACE TRIGGER T_BE_SOURCING_LANE
	AFTER INSERT OR UPDATE ON PCM_SOURCING_LANE
	FOR EACH ROW
DECLARE
	v_item_key pcm_sourcing_lane.item_key%TYPE;
	v_current_flag pcm_sourcing_lane.current_flag%TYPE;
BEGIN
	v_item_key := :new.item_key;
	v_current_flag := :new.current_flag;
	IF v_current_flag = 'N' AND (:new.collaboration = 1 OR :old.collaboration = 1) THEN
		INSERT INTO ITEM_CHANGE_AUDIT
	    (ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE)
		VALUES
		(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'BOMEXTRACT');
	END IF;
END;
/

CREATE OR REPLACE TRIGGER T_BE_COST_RECORD
	AFTER INSERT OR UPDATE ON PCM_COST_RECORD
	FOR EACH ROW
DECLARE
	v_item_key pcm_sourcing_lane.item_key%TYPE;
	v_collaboration pcm_sourcing_lane.collaboration%TYPE;
	v_current_flag pcm_cost_record.current_flag%TYPE;
BEGIN
	v_current_flag := :new.current_flag;
	SELECT ITEM_KEY,COLLABORATION INTO v_item_key,v_collaboration FROM PCM_SOURCING_LANE WHERE SOURCING_LANE_KEY = :new.sourcing_lane_key;
	IF v_current_flag = 'N' AND v_collaboration = 1 THEN
		INSERT INTO ITEM_CHANGE_AUDIT
	    (ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE)
		VALUES
		(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'BOMEXTRACT');
	END IF;
END;
/

CREATE OR REPLACE TRIGGER T_BE_BOM_LINE_ITEM
	AFTER INSERT OR UPDATE OR DELETE ON BOM_LINE_ITEM FOR EACH ROW
DECLARE
	v_item_key item_master.item_key%TYPE;
BEGIN
	v_item_key := :new.item_key;
    IF DELETING THEN
		v_item_key := :old.item_key;
	END IF;
	INSERT INTO ITEM_CHANGE_AUDIT
	(ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE)
	VALUES
	(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'BOMEXTRACT-BOMLINE');
END;
/

CREATE OR REPLACE TRIGGER T_BE_SUPPLIER_ALLOCATION
	AFTER INSERT OR UPDATE OR DELETE ON PCM_SUPPLIER_ALLOCATION
	FOR EACH ROW
DECLARE
	v_item_key pcm_supplier_allocation.customer_item_key%TYPE;
	v_cnt NUMBER;
BEGIN
	IF :new.customer_item_group_item_key IS NULL THEN
		v_item_key := :new.customer_item_key;
    	IF DELETING THEN
			v_item_key := :old.customer_item_key;
		END IF;
	
		v_cnt := 0;
		SELECT sum(amount) INTO v_cnt FROM
		(
		SELECT count(*) amount
		FROM pcm_sourcing_lane
		WHERE item_key = v_item_key
		AND collaboration = 1
		UNION ALL
		SELECT count(*)
		FROM item_master
		WHERE item_key = v_item_key
		AND collaboration = 1
		);
		
		IF v_cnt > 0 THEN
			INSERT INTO ITEM_CHANGE_AUDIT
			(ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE)
			VALUES
			(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key, systimestamp,'BOMEXTRACT');
		END IF;
	END IF;
END;
/

CREATE OR REPLACE TRIGGER T_BE_ITEM_MASTER
	AFTER INSERT OR UPDATE OR DELETE ON ITEM_MASTER
	FOR EACH ROW
DECLARE
	v_item_key pcm_supplier_allocation.customer_item_key%TYPE;
	v_cnt NUMBER;
BEGIN
	v_item_key := :new.item_key;
	IF DELETING THEN
	  v_item_key := :old.item_key;
	END IF;
	IF :new.collaboration = 1 OR :old.collaboration = 1 THEN
		INSERT INTO ITEM_CHANGE_AUDIT
	    (ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE)
		VALUES
		(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'BOMEXTRACT');
	END IF;
END;
/

-- the collab extract package
CREATE PACKAGE BomExtract AS
  v_extract_key pcm_bom_extract.extract_key%TYPE;
  
  PROCEDURE GetBomExtract(
    out_result out SYS_REFCURSOR,
    p_status in bom_header.status%TYPE,
    p_isDelta in NUMBER);
 
  PROCEDURE GetItemCollabs(
    p_business_entity_key in business_entity.business_entity_key%TYPE,
    p_status in bom_header.status%TYPE);
    
  PROCEDURE GetBomLineAlternates (
    p_bom_key in bom_header.bom_key%TYPE,
    p_bom_line_item_key in bom_line_item.bom_line_item_key%TYPE,
    p_group_id in bom_line_item.group_id%TYPE);
END BomExtract;
/

-- the collab extract package body
CREATE PACKAGE BODY BomExtract AS
  PROCEDURE GetBomExtract (
    out_result out SYS_REFCURSOR,
    p_status in bom_header.status%TYPE,
    p_isDelta in NUMBER)
    AS
    
    abort_detected EXCEPTION;
	PRAGMA EXCEPTION_INIT(abort_detected,-20008);
	v_item_key item_master.item_key%TYPE;
	v_business_entity_key business_entity.business_entity_key%TYPE;
	v_currentDate DATE;
	
	CURSOR c_fullBusinessCollabs IS
	SELECT distinct i.business_entity_key
    FROM pcm_sourcing_lane sl
    JOIN item_master i ON
    i.item_key = sl.item_key
    WHERE sl.collaboration = 1
    AND status = p_status
    UNION
    SELECT distinct business_entity_key
    FROM item_master
    WHERE collaboration = 1;
	
	CURSOR c_deltaBusinessCollabs IS
	SELECT distinct i.business_entity_key
    FROM item_change_audit ica
    JOIN item_master i ON
    i.item_key = ica.item_key
    WHERE op_code = 'BOMEXTRACT-P';
  BEGIN
    BEGIN
	  SELECT sys_guid() INTO v_extract_key FROM dual;
	  SELECT sysdate INTO v_currentDate FROM dual;
	  
	  EXCEPTION WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-2000,SQLERRM(SQLCODE));
	END;
	
    -- Insert the running job status
	INSERT INTO pcm_job_status_control (
	job_id,
	job_type,
	job_status,
  	job_start_date,
  	job_end_date)
	VALUES (
	v_extract_key,
	'BOM_EXTRACT',
	'RUNNING',
	current_timestamp,
	null);

	COMMIT;
	
	-- If this is a delta download, set the audit entries to pending
	IF p_isDelta = 1 THEN
	  UPDATE item_change_audit
	  SET op_code = 'BOMEXTRACT-P'
	  WHERE op_code = 'BOMEXTRACT'
	  AND last_change_dt <= v_currentDate;
	  
	  UPDATE item_change_audit
	  SET op_code = 'BOMEXTRACT-BOMLINE-P'
	  WHERE op_code = 'BOMEXTRACT-BOMLINE'
	  AND last_change_dt <= v_currentDate;
	  
	  MTCMExtractCommon.PruneBomLines('APPROVED','BOMEXTRACT-BOMLINE-P',0);
	  MTCMExtractCommon.FindAffectedItemsFromBomLines(v_currentDate,'APPROVED','BOMEXTRACT-BOMLINE-P','BOMEXTRACT-P',0);
	  
	  COMMIT;
	  
	  OPEN c_deltaBusinessCollabs;
	  LOOP
	    FETCH c_deltaBusinessCollabs INTO v_business_entity_key;
	    EXIT WHEN c_deltaBusinessCollabs%NOTFOUND;
	    GetItemCollabs(v_business_entity_key,p_status);
	  END LOOP;
	  CLOSE c_deltaBusinessCollabs;
	ELSE
	  OPEN c_fullBusinessCollabs;
	  LOOP
	    FETCH c_fullBusinessCollabs INTO v_business_entity_key;
	    EXIT WHEN c_fullBusinessCollabs%NOTFOUND;
	    GetItemCollabs(v_business_entity_key,p_status);
	  END LOOP;
	  CLOSE c_fullBusinessCollabs;
	END IF;

    -- Update the job status to complete
	UPDATE pcm_job_status_control
	SET job_status = 'COMPLETE',
	job_end_date = current_timestamp
	WHERE job_id = v_extract_key;
	
	-- Remove the pending audit entries
	IF p_isDelta = 1 THEN
	  DELETE FROM item_change_audit
	  WHERE op_code like 'BOMEXTRACT%-P';
	END IF;
	
	COMMIT;

    OPEN out_result FOR
	  SELECT rownum AS RN, v_extract_key AS EXTRACT_KEY FROM DUAL;

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
		
		DELETE FROM PCM_BOM_EXTRACT
		WHERE EXTRACT_KEY = v_extract_key;
		
		IF p_isDelta = 1 THEN
	      DELETE FROM item_change_audit
	      WHERE op_code like 'BOMEXTRACT%-P';
	    END IF;
	    
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END GetBomExtract;
  
  PROCEDURE GetItemCollabs (
    p_business_entity_key in business_entity.business_entity_key%TYPE,
    p_status in bom_header.status%TYPE)
    AS
    
    v_abort NUMBER(5) := 0;
    v_item_key item_master.item_key%TYPE;
    v_bh_bom_key bom_header.bom_key%TYPE;
    v_bh_item_key item_master.item_key%TYPE;
    v_bom_line_item_key bom_line_item.bom_line_item_key%TYPE;
    v_bli_item_key item_master.item_key%TYPE;
    v_bli_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_quantity bom_line_item.item_quantity%TYPE;
    v_bh_effective_from_dt bom_header.effective_from_dt%TYPE;
    v_bh_effective_to_dt bom_header.effective_to_dt%TYPE;
    v_bli_effective_from_dt bom_line_item.effective_from_dt%TYPE;
    v_bli_effective_to_dt bom_line_item.effective_to_dt%TYPE;
    v_group_id bom_line_item.group_id%TYPE;
    v_bom_line_alt_type bom_line_item.bom_line_alt_type%TYPE;
    v_priority bom_line_item.priority%TYPE;
    v_cnt NUMBER := 0;
    v_item_collab_cnt NUMBER := 0;
    
    CURSOR c_ItemCollabs IS
    SELECT sl.item_key FROM pcm_sourcing_lane sl
    JOIN item_master i ON
    i.item_key = sl.item_key AND
    i.business_entity_key = p_business_entity_key
	WHERE sl.collaboration = 1
	UNION
	SELECT item_key FROM item_master
	WHERE collaboration = 1 AND
	business_entity_key = p_business_entity_key;
    
    CURSOR c_FlatBoms IS
    SELECT bh_bom_key
    ,bh_item_key
    ,bom_line_item_key
    ,bli_item_key
    ,i.business_entity_key
    ,item_quantity
    ,bh_effective_from_dt
    ,bh_effective_to_dt
    ,bli_effective_from_dt
    ,bli_effective_to_dt
    ,group_id
    ,bom_line_alt_type
    ,priority
    FROM iv_bh_bli bh_bli
    JOIN item_master i ON
    i.item_key = bh_bli.bli_item_key
    WHERE status = p_status
    START WITH bli_item_key = v_item_key
    AND status = p_status
    CONNECT BY PRIOR bh_bom_key = bli_sub_bom_key and PRIOR status = status;
  BEGIN
	-- Check if the extract should abort
	BEGIN
	  SELECT count(*)
	  INTO v_abort
	  FROM pcm_job_status_control
	  WHERE job_id = v_extract_key
	  AND job_status = 'ABORT';

	  EXCEPTION
		WHEN NO_DATA_FOUND THEN NULL;
	END;

    IF v_abort > 0 THEN
	  RAISE_APPLICATION_ERROR(-20008,'Bom Extract Aborted');
	END IF;
	
	-- get the unique set of collabable items related to the business
	OPEN c_ItemCollabs;
	LOOP
	  FETCH c_ItemCollabs INTO v_item_key;
	  EXIT WHEN c_ItemCollabs%NOTFOUND;
	    
	  -- walk the boms for each collabable item
	  OPEN c_FlatBoms;
	  LOOP
	    FETCH c_FlatBoms INTO
	    v_bh_bom_key
	    ,v_bh_item_key
	    ,v_bom_line_item_key
	    ,v_bli_item_key
	    ,v_bli_business_entity_key
	    ,v_item_quantity
	    ,v_bh_effective_from_dt
	    ,v_bh_effective_to_dt
	    ,v_bli_effective_from_dt
	    ,v_bli_effective_to_dt
	    ,v_group_id
	    ,v_bom_line_alt_type
	    ,v_priority;
	    EXIT WHEN c_FlatBoms%NOTFOUND;
	      
	    -- determine if the bom/bom line pair has already been walked
	    SELECT count(*) INTO v_cnt
	    FROM pcm_bom_extract
	    WHERE extract_key = v_extract_key
	    AND bom_key = v_bh_bom_key
	    AND bom_line_item_key = v_bom_line_item_key;
	      
	    IF v_cnt = 0 THEN
	      -- if the bom/bom line pair has not been walked, record the bom/bom line pair
          INSERT INTO pcm_bom_extract (extract_key, op_code, bom_key, item_key, bom_line_item_key, component_item_key, component_business_entity_key, item_quantity, effective_from_dt, effective_to_dt, component_effective_from_dt, component_effective_to_dt, alternate_group_id, alternate_type, alternate_priority)
          VALUES (v_extract_key, 'U', v_bh_bom_key, v_bh_item_key, v_bom_line_item_key, v_bli_item_key, v_bli_business_entity_key, v_item_quantity, v_bh_effective_from_dt, v_bh_effective_to_dt, v_bli_effective_from_dt, v_bli_effective_to_dt, v_group_id, v_bom_line_alt_type, v_priority);
          GetBomLineAlternates(v_bh_bom_key, v_bom_line_item_key, v_group_id);
	    ELSE
	      -- if the bom/bom line pair has been walked, then exit as the remaining path has also been walked
	      EXIT;
	    END IF;
	  END LOOP;
	  CLOSE c_FlatBoms;
	END LOOP;
	v_item_collab_cnt := c_ItemCollabs%ROWCOUNT;
	CLOSE c_ItemCollabs;
	  
	IF v_item_collab_cnt = 0 THEN
	  INSERT INTO pcm_bom_extract (extract_key, op_code, component_business_entity_key)
      VALUES (v_extract_key, 'D', p_business_entity_key);
	END IF;
  END GetItemCollabs;
  
  PROCEDURE GetBomLineAlternates (
    p_bom_key in bom_header.bom_key%TYPE,
    p_bom_line_item_key in bom_line_item.bom_line_item_key%TYPE,
    p_group_id in bom_line_item.group_id%TYPE)
    AS
    
    v_bh_bom_key bom_header.bom_key%TYPE;
    v_bh_item_key item_master.item_key%TYPE;
    v_bom_line_item_key bom_line_item.bom_line_item_key%TYPE;
    v_bli_item_key item_master.item_key%TYPE;
    v_bli_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_quantity bom_line_item.item_quantity%TYPE;
    v_bh_effective_from_dt bom_header.effective_from_dt%TYPE;
    v_bh_effective_to_dt bom_header.effective_to_dt%TYPE;
    v_bli_effective_from_dt bom_line_item.effective_from_dt%TYPE;
    v_bli_effective_to_dt bom_line_item.effective_to_dt%TYPE;
    v_group_id bom_line_item.group_id%TYPE;
    v_bom_line_alt_type bom_line_item.bom_line_alt_type%TYPE;
    v_priority bom_line_item.priority%TYPE;
    
    CURSOR c_BomLineAlternates IS
    SELECT bh_bom_key
    ,bh_item_key
    ,bom_line_item_key
    ,bli_item_key
    ,i.business_entity_key
    ,item_quantity
    ,bh_effective_from_dt
    ,bh_effective_to_dt
    ,bli_effective_from_dt
    ,bli_effective_to_dt
    ,group_id
    ,bom_line_alt_type
    ,priority
    FROM iv_bh_bli bh_bli
    JOIN item_master i ON
    i.item_key = bh_bli.bli_item_key
    WHERE bh_bli.bh_bom_key = p_bom_key
    AND bh_bli.bom_line_item_key != p_bom_line_item_key
    AND bh_bli.group_id = p_group_id;
  BEGIN
	OPEN c_BomLineAlternates;
	LOOP
	  FETCH c_BomLineAlternates INTO
	  v_bh_bom_key
	  ,v_bh_item_key
	  ,v_bom_line_item_key
	  ,v_bli_item_key
	  ,v_bli_business_entity_key
	  ,v_item_quantity
	  ,v_bh_effective_from_dt
	  ,v_bh_effective_to_dt
	  ,v_bli_effective_from_dt
	  ,v_bli_effective_to_dt
	  ,v_group_id
	  ,v_bom_line_alt_type
	  ,v_priority;
	  EXIT WHEN c_BomLineAlternates%NOTFOUND;
	      
	  INSERT INTO pcm_bom_extract (extract_key, op_code, bom_key, item_key, bom_line_item_key, component_item_key, component_business_entity_key, item_quantity, effective_from_dt, effective_to_dt, component_effective_from_dt, component_effective_to_dt, alternate_group_id, alternate_type, alternate_priority)
      VALUES (v_extract_key, 'U', v_bh_bom_key, v_bh_item_key, v_bom_line_item_key, v_bli_item_key, v_bli_business_entity_key, v_item_quantity, v_bh_effective_from_dt, v_bh_effective_to_dt, v_bli_effective_from_dt, v_bli_effective_to_dt, v_group_id, v_bom_line_alt_type, v_priority);
	END LOOP;
	CLOSE c_BomLineAlternates;
  END GetBomLineAlternates;
END BomExtract;
/
