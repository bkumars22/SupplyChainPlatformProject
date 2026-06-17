WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

-- table to store the ouput of the collab extract calculation
CREATE TABLE PCM_COLLAB_EXTRACT
(
  COLLAB_EXTRACT_KEY VARCHAR2(32) NOT NULL,
  OP_CODE VARCHAR2(255) NOT NULL,
  ITEM_KEY NUMBER(19) NOT NULL,
  LINKED_ITEM_KEY NUMBER(19),
  TOP_ITEM_KEY NUMBER(19),
  ITEM_QUANTITY NUMBER(19, 5),
  SOURCING_LANE_KEY NUMBER(19),
  TOP_SOURCING_LANE_KEY NUMBER(19),
  EFFECTIVE_FROM_DT DATE,
  EFFECTIVE_TO_DT DATE,
  PARENT_EFFECTIVE_FROM_DT DATE,
  PARENT_EFFECTIVE_TO_DT DATE,
  ALTERNATE_GROUP_ID VARCHAR2(255),
  ALTERNATE_TYPE VARCHAR2(64),
  ALTERNATE_PRIORITY NUMBER(2)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPCE_I1 ON PCM_COLLAB_EXTRACT(collab_extract_key) TABLESPACE AUDIT_INDEX;
CREATE INDEX XPCE_I2 ON PCM_COLLAB_EXTRACT(item_key) TABLESPACE AUDIT_INDEX;

-- table to store a temporary cache so that we don't repeat caclucations for items
-- we have already evaluated
CREATE TABLE PCM_COLLAB_EXTRACT_ITEM_CACHE
(
  COLLAB_EXTRACT_KEY VARCHAR2(32) NOT NULL,
  ITEM_KEY NUMBER(19) NOT NULL,
  LINKED_ITEM_KEY NUMBER(19),
  TOP_ITEM_KEY NUMBER(19),
  ITEM_QUANTITY NUMBER(19, 5),
  TOP_SOURCING_LANE_KEY NUMBER(19),
  EFFECTIVE_FROM_DT DATE,
  EFFECTIVE_TO_DT DATE,
  PARENT_EFFECTIVE_FROM_DT DATE,
  PARENT_EFFECTIVE_TO_DT DATE,
  ALTERNATE_GROUP_ID VARCHAR2(255),
  ALTERNATE_TYPE VARCHAR2(64),
  ALTERNATE_PRIORITY NUMBER(2)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPCEIC_I1 ON PCM_COLLAB_EXTRACT_ITEM_CACHE(collab_extract_key,item_key) TABLESPACE AUDIT_INDEX;

-- the function to retrieve the first context sensitive supplier allocation first by item, then platform, then by no context
CREATE OR REPLACE FUNCTION F_COLLAB_SUPPLIER_ALLOCATION(
  p_customer_item_key in NUMBER
  ,p_supplier_business_entity_key in NUMBER
  ,p_from_site_key in NUMBER
  ,p_top_item_key in NUMBER)
  RETURN NUMBER
IS
	v_allocation pcm_supplier_allocation.allocation%TYPE;
	v_platform_key item_platform.item_platform_key%TYPE;
	
	CURSOR c_ItemContextAllocation IS
	SELECT allocation
	FROM pcm_supplier_allocation
	WHERE customer_item_key = p_customer_item_key AND
	supplier_business_entity_key = p_supplier_business_entity_key AND
	(supplier_site_key IS NULL OR supplier_site_key = p_from_site_key) AND
	effective_from_dt <= sysdate AND
	(effective_to_dt IS NULL OR effective_to_dt >= sysdate) AND
	context_object_type = 'ITEM' AND
	context_object_id = p_top_item_key;
	
	CURSOR c_PlatformContextAllocation IS
	SELECT allocation
	FROM pcm_supplier_allocation
	WHERE customer_item_key = p_customer_item_key AND
	supplier_business_entity_key = p_supplier_business_entity_key AND
	(supplier_site_key IS NULL OR supplier_site_key = p_from_site_key) AND
	effective_from_dt <= sysdate AND
	(effective_to_dt IS NULL OR effective_to_dt >= sysdate) AND
	context_object_type = 'PLATFORM' AND
	context_object_id = v_platform_key;
	
	CURSOR c_NoContextAllocation IS
	SELECT allocation
	FROM pcm_supplier_allocation
	WHERE customer_item_key = p_customer_item_key AND
	supplier_business_entity_key = p_supplier_business_entity_key AND
	(supplier_site_key IS NULL OR supplier_site_key = p_from_site_key) AND
	effective_from_dt <= sysdate AND
	(effective_to_dt IS NULL OR effective_to_dt >= sysdate) AND
	context_object_type IS NULL AND
	context_object_id IS NULL;
	
	CURSOR c_Platform IS
	SELECT ip.item_platform_key
	FROM item_platform ip
	JOIN item_item_platform iip ON
	iip.item_platform_key = ip.item_platform_key AND
	iip.item_key = p_top_item_key;
BEGIN
	OPEN c_ItemContextAllocation;
	LOOP
		FETCH c_ItemContextAllocation INTO v_allocation;
		EXIT WHEN c_ItemContextAllocation%NOTFOUND;
	END LOOP;
	CLOSE c_ItemContextAllocation;
	
	IF v_allocation IS NOT NULL THEN
		RETURN v_allocation;
	END IF;
	
	OPEN c_Platform;
	WHILE v_allocation IS NULL LOOP
		FETCH c_Platform INTO v_platform_key;
		EXIT WHEN c_Platform%NOTFOUND;
		
		OPEN c_PlatformContextAllocation;
		LOOP
			FETCH c_PlatformContextAllocation INTO v_allocation;
			EXIT WHEN c_PlatformContextAllocation%NOTFOUND;
		END LOOP;
		CLOSE c_PlatformContextAllocation;
	END LOOP;
	CLOSE c_Platform;
	
	IF v_allocation IS NOT NULL THEN
		RETURN v_allocation;
	END IF;
	
	OPEN c_NoContextAllocation;
	LOOP
		FETCH c_NoContextAllocation INTO v_allocation;
		EXIT WHEN c_NoContextAllocation%NOTFOUND;
	END LOOP;
	CLOSE c_NoContextAllocation;
	
	RETURN v_allocation;
END;
/

CREATE OR REPLACE FUNCTION F_COLLAB_EXTRACT_DT(
  p_effective_from_dt in DATE
  ,p_effective_to_dt in DATE
  ,p_parent_effective_dt in DATE
  ,p_effective_dt in DATE)
  RETURN DATE
IS
BEGIN
	IF p_effective_from_dt IS NOT NULL OR p_effective_to_dt IS NOT NULL THEN
		RETURN p_effective_dt;
	END IF;
	
	RETURN p_parent_effective_dt;
END;
/

-- the view to retrieve the output of the collab calculation along with other meta data
CREATE OR REPLACE VIEW IV_COLLAB_EXTRACT AS
SELECT ti.item_identifier top_item_identifier
,ci.item_identifier component_item_identifier
,li.item_identifier parent_item_identifier
,cislb.business_entity_identifier component_supplier
,tislfs.site_identifier top_source_site
,tislb.business_entity_identifier top_supplier
,pce.item_quantity
,ci.item_classification
,cisl.date_offset
,F_COLLAB_SUPPLIER_ALLOCATION(pce.item_key,cisl.supplier_key,cisl.from_site_key,ti.item_key) allocation
,F_COLLAB_EXTRACT_DT(pce.effective_from_dt,pce.effective_to_dt,pce.parent_effective_from_dt,pce.effective_from_dt) startDate
,F_COLLAB_EXTRACT_DT(pce.effective_from_dt,pce.effective_to_dt,pce.parent_effective_to_dt,pce.effective_to_dt) endDate
,pce.alternate_group_id name
,pce.alternate_type type
,pce.alternate_priority priority
,pce.op_code
,pce.collab_extract_key
FROM PCM_COLLAB_EXTRACT pce
JOIN item_master ci ON
ci.item_key = pce.item_key
LEFT JOIN item_master ti ON
ti.item_key = pce.top_item_key
LEFT JOIN item_master li ON
li.item_key = pce.linked_item_key
LEFT JOIN pcm_sourcing_lane cisl ON
cisl.sourcing_lane_key = pce.sourcing_lane_key
LEFT JOIN business_entity cislb ON
cislb.business_entity_key = cisl.supplier_key
LEFT JOIN pcm_sourcing_lane tisl ON
tisl.sourcing_lane_key = pce.top_sourcing_lane_key
LEFT JOIN business_entity tislb ON
tislb.business_entity_key = tisl.supplier_key
LEFT JOIN site tislfs ON
tislfs.site_key = tisl.from_site_key;

-- triggers to manage the delta data
CREATE OR REPLACE TRIGGER T_COLLAB_SOURCING_LANE
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
		(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'COLLAB');
	END IF;
END;
/

CREATE OR REPLACE TRIGGER T_COLLAB_COST_RECORD
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
		(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'COLLAB');
	END IF;
END;
/

CREATE OR REPLACE TRIGGER T_COLLAB_BOM_LINE_ITEM
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
	(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'COLLAB-BOMLINE');
END;
/

CREATE OR REPLACE TRIGGER T_COLLAB_SUPPLIER_ALLOCATION
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
			(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key, systimestamp,'COLLAB');
		END IF;
	END IF;
END;
/

CREATE OR REPLACE TRIGGER T_COLLAB_ITEM_MASTER
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
		(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'COLLAB');
	END IF;
END;
/

-- the collab extract package
CREATE PACKAGE CollabExtract AS
  TYPE LinkedItem IS RECORD (
    linked_item_key pcm_collab_extract.linked_item_key%TYPE,
    item_quantity pcm_collab_extract.item_quantity%TYPE
  );
  
  TYPE string_array IS TABLE of VARCHAR2(32767);
  
  v_collab_extract_key pcm_collab_extract.collab_extract_key%TYPE;
    
  PROCEDURE GetCollab(
    out_result out SYS_REFCURSOR,
    p_currentDate in DATE,
    p_status in iv_cost_record.status%TYPE,
    p_isDelta in NUMBER,
	p_debug in NUMBER);
	
  PROCEDURE GetCollabExtract (
    p_sourcing_lane_key in pcm_sourcing_lane.sourcing_lane_key%TYPE,
    p_item_key in item_master.item_key%TYPE,
    p_collaboration in pcm_sourcing_lane.collaboration%TYPE,
    p_currentDate in DATE,
    p_status in iv_cost_record.status%TYPE,
	p_debug in NUMBER);
	
  FUNCTION GetLinkedItem (
    p_path VARCHAR2,
    p_status VARCHAR2,
	p_debug NUMBER)
    RETURN LinkedItem;
    
  FUNCTION GetBomLineItem (
    p_path VARCHAR2)
    RETURN NUMBER;
    
  FUNCTION split_string(
    p_str VARCHAR2,
    p_delimiter CHAR DEFAULT ',')
    RETURN string_array;
    
  PROCEDURE GetBomLineAlternates (
    p_op_code in pcm_collab_extract.op_code%TYPE,
    p_bom_key in bom_header.bom_key%TYPE,
    p_bom_line_item_key in bom_line_item.bom_line_item_key%TYPE,
    p_group_id in bom_line_item.group_id%TYPE, 
    p_item_key in item_master.item_key%TYPE,
    p_linked_item in LinkedItem,
    p_top_item_key in item_master.item_key%TYPE,
    p_pegging_quantity in bom_line_item.item_quantity%TYPE,
    p_bli_item_quantity in bom_line_item.item_quantity%TYPE);
END CollabExtract;
/

-- the collab extract package body
CREATE PACKAGE BODY CollabExtract AS
  PROCEDURE GetCollab (
    out_result out SYS_REFCURSOR,
	p_currentDate in DATE,
    p_status in iv_cost_record.status%TYPE,
    p_isDelta in NUMBER,
	p_debug in NUMBER)
    AS
    
    abort_detected EXCEPTION;
	PRAGMA EXCEPTION_INIT(abort_detected,-20008);
	v_sourcing_lane_key pcm_sourcing_lane.sourcing_lane_key%TYPE;
    v_item_key item_master.item_key%TYPE;
	v_collaboration pcm_sourcing_lane.collaboration%TYPE;
	
    CURSOR c_SourcingLaneCollabs IS
    SELECT sourcing_lane_key,item_key,collaboration FROM pcm_sourcing_lane
	WHERE
	(p_isDelta = 0 AND collaboration = 1) OR
	(p_isDelta = 1 AND item_key IN (SELECT item_key FROM item_change_audit WHERE op_code = 'COLLAB-P'));
	
	CURSOR c_ItemCollabs IS
	SELECT item_key,collaboration FROM item_master
	WHERE
	item_key NOT IN
	(SELECT item_key FROM pcm_collab_extract WHERE collab_extract_key = v_collab_extract_key)
	AND
	(
	  (p_isDelta = 0 AND collaboration = 1) OR
	  (p_isDelta = 1 AND item_key IN (SELECT item_key FROM item_change_audit WHERE op_code = 'COLLAB-P'))
	);
  BEGIN
    BEGIN
	  SELECT sys_guid() INTO v_collab_extract_key FROM dual;
	  
	  EXCEPTION WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20000, 'Cannot get the key for the Collab Extract');
	END;
    
    -- Insert the running job status
	INSERT INTO pcm_job_status_control (
	job_id,
	job_type,
	job_status,
  	job_start_date,
  	job_end_date)
	VALUES (
	v_collab_extract_key,
	'COLLAB_EXTRACT',
	'RUNNING',
	current_timestamp,
	null);

	COMMIT;
	
	-- If this is a delta download, set the audit entries to pending
	IF p_isDelta = 1 THEN
	  UPDATE item_change_audit
	  SET op_code = 'COLLAB-P'
	  WHERE op_code = 'COLLAB'
	  AND last_change_dt <= p_currentDate;
	  
	  UPDATE item_change_audit
	  SET op_code = 'COLLAB-BOMLINE-P'
	  WHERE op_code = 'COLLAB-BOMLINE'
	  AND last_change_dt <= p_currentDate;
	  
	  MTCMExtractCommon.PruneBomLines('APPROVED','COLLAB-BOMLINE-P',0);
	  MTCMExtractCommon.FindAffectedItemsFromBomLines(p_currentDate,'APPROVED','COLLAB-BOMLINE-P','COLLAB-P',0);
	  
	  COMMIT;
	END IF;

	-- Get the collabable sourcing lanes
    OPEN c_SourcingLaneCollabs;
	LOOP
      FETCH c_SourcingLaneCollabs INTO v_sourcing_lane_key,v_item_key,v_collaboration;
      
      EXIT WHEN c_SourcingLaneCollabs%NOTFOUND;
      
      GetCollabExtract(v_sourcing_lane_key,v_item_key,v_collaboration,p_currentDate,p_status,p_debug);
    END LOOP;
    
    IF p_debug = 1 THEN	
	  DBMS_OUTPUT.put_line ('[SOURCING LANE COUNT] ' || c_SourcingLaneCollabs%ROWCOUNT);
	END IF;
	
	CLOSE c_SourcingLaneCollabs;
    
	COMMIT;
	
    -- Get the collabable items
    OPEN c_ItemCollabs;
	LOOP
	  FETCH c_ItemCollabs INTO v_item_key,v_collaboration;
      
      EXIT WHEN c_ItemCollabs%NOTFOUND;
      
      GetCollabExtract(null,v_item_key,v_collaboration,p_currentDate,p_status,p_debug);
    END LOOP;
  
    IF p_debug = 1 THEN	
	  DBMS_OUTPUT.put_line ('[ITEM COUNT] ' || c_ItemCollabs%ROWCOUNT);
	END IF;
	
    CLOSE c_ItemCollabs;

    -- Update the job status to complete
	UPDATE pcm_job_status_control
	SET job_status = 'COMPLETE',
	job_end_date = current_timestamp
	WHERE job_id = v_collab_extract_key;
	
	-- Clean up the item cache
	DELETE FROM PCM_COLLAB_EXTRACT_ITEM_CACHE
	WHERE collab_extract_key = v_collab_extract_key;
	
	-- Remove the pending audit entries
	IF p_isDelta = 1 THEN
	  DELETE FROM item_change_audit
	  WHERE op_code like 'COLLAB%-P';
	END IF;
	
	COMMIT;

    OPEN out_result FOR
	  SELECT rownum AS RN, v_collab_extract_key AS EXTRACT_KEY FROM DUAL;

    EXCEPTION
      WHEN abort_detected THEN
		UPDATE pcm_job_status_control
		SET job_status = 'ABORTED',
		job_end_date = current_timestamp
		WHERE job_id = v_collab_extract_key;
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
      WHEN OTHERS THEN
        ROLLBACK;
        
		UPDATE pcm_job_status_control
		SET job_status = 'ERROR',
		job_end_date = current_timestamp
		WHERE job_id = v_collab_extract_key;
		
		DELETE FROM PCM_COLLAB_EXTRACT
		WHERE COLLAB_EXTRACT_KEY = v_collab_extract_key;
		
		IF p_isDelta = 1 THEN
	      DELETE FROM item_change_audit
	      WHERE op_code like 'COLLAB%-P';
	    END IF;
	    
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END GetCollab;
  
  PROCEDURE GetCollabExtract (
    p_sourcing_lane_key in pcm_sourcing_lane.sourcing_lane_key%TYPE,
    p_item_key in item_master.item_key%TYPE,
    p_collaboration in pcm_sourcing_lane.collaboration%TYPE,
    p_currentDate in DATE,
    p_status in iv_cost_record.status%TYPE,
	p_debug in NUMBER)
    AS
    
    v_abort NUMBER(5) := 0;
    v_bh_item_key item_master.item_key%TYPE;
    v_path VARCHAR2(2048);
    v_linked_item_key item_master.item_key%TYPE;
    v_top_item_key item_master.item_key%TYPE;
    v_pegging_quantity bom_line_item.item_quantity%TYPE;
    v_top_sourcing_lane_key pcm_sourcing_lane.sourcing_lane_key%TYPE;
    v_item_cache_cnt NUMBER := 0;
    v_top_sourcing_lane_cnt NUMBER := 0;
    v_flat_bom_cnt NUMBER := 0;
    v_op_code pcm_collab_extract.op_code%TYPE;
    v_collab_cnt NUMBER := 0;
    v_collab_delete_cnt NUMBER := 0;
    v_bh_bom_key bom_header.bom_key%TYPE;
    v_bom_line_item_key bom_line_item.bom_line_item_key%TYPE;
    v_bh_effective_from_dt bom_header.effective_from_dt%TYPE;
    v_bh_effective_to_dt bom_header.effective_to_dt%TYPE;
    v_bli_effective_from_dt bom_line_item.effective_from_dt%TYPE;
    v_bli_effective_to_dt bom_line_item.effective_to_dt%TYPE;
    v_group_id bom_line_item.group_id%TYPE;
    v_bom_line_alt_type bom_line_item.bom_line_alt_type%TYPE;
    v_priority bom_line_item.priority%TYPE;
    v_bli_item_quantity bom_line_item.item_quantity%TYPE;
    
    v_linked_item LinkedItem;
    
    CURSOR c_ItemCache IS
    SELECT linked_item_key
    ,top_item_key
    ,item_quantity
    ,top_sourcing_lane_key
    ,parent_effective_from_dt
    ,parent_effective_to_dt
    ,effective_from_dt
    ,effective_to_dt
    ,alternate_group_id
    ,alternate_type
    ,alternate_priority
    FROM pcm_collab_extract_item_cache
	WHERE collab_extract_key = v_collab_extract_key
	AND item_key = p_item_key;
    
    CURSOR c_FlatBoms IS
    SELECT bh_item_key item_key
    ,sys_connect_by_path(bh_item_key||':'||item_quantity||':'||bom_line_item_key,'/') path
    ,calculateExpression('1'||sys_connect_by_path(item_quantity,'*')) pegging_qty
    FROM iv_bh_bli
    WHERE status = p_status
    AND CONNECT_BY_ISLEAF = 1
    START WITH bli_item_key = p_item_key
    AND status = p_status
    CONNECT BY PRIOR bh_bom_key = bli_sub_bom_key;
    
    CURSOR c_TopSourcingLanes IS
    SELECT sourcing_lane_key
    FROM pcm_sourcing_lane
    WHERE item_key = v_top_item_key
    AND status = p_status;
  BEGIN
	-- Check if the extract should abort
	BEGIN
	  SELECT count(*)
	  INTO v_abort
	  FROM pcm_job_status_control
	  WHERE job_id = v_collab_extract_key
	  AND job_status = 'ABORT';

	  EXCEPTION
		WHEN NO_DATA_FOUND THEN NULL;
	END;

    IF v_abort > 0 THEN
	  RAISE_APPLICATION_ERROR(-20008,'Collab Extract Aborted');
	END IF;
	
	-- set the op code
	IF p_collaboration = 0 THEN
	  -- check if there are any other collabable records
	  -- for the same item
	  SELECT sum(amount) INTO v_collab_cnt FROM
	  (
	  SELECT count(*) amount
	  FROM pcm_sourcing_lane
	  WHERE item_key = p_item_key
	  AND status = p_status
	  AND collaboration = 1
	  UNION ALL
	  SELECT count(*)
	  FROM item_master
	  WHERE item_key = p_item_key
	  AND collaboration = 1
	  );
	  
	  IF v_collab_cnt = 0 THEN
	    -- check if we have already recorded anything for this item
	    SELECT count(*) INTO v_collab_delete_cnt
	    FROM PCM_COLLAB_EXTRACT
	    WHERE collab_extract_key = v_collab_extract_key
	    AND item_key = p_item_key;
	    
	    IF v_collab_delete_cnt = 0 THEN
	      -- if the collab flag is not set, there a no other collabable records, and we have not already recorded a delete for this item
	      -- then this is a delete record and there is no need to get the top and linked items
	      v_op_code := 'D';
	      INSERT INTO pcm_collab_extract (collab_extract_key, op_code, item_key, linked_item_key, top_item_key, item_quantity, sourcing_lane_key, top_sourcing_lane_key)
          VALUES (v_collab_extract_key, v_op_code, p_item_key, null, null, null, null, null);
        END IF;
      END IF;
	ELSE
	  -- if the collab flag is set, then this is an update record
	  v_op_code := 'U';
	  
	  -- get the cached info instead of walking the bom each time
	  OPEN c_ItemCache;
	  LOOP
        FETCH c_ItemCache INTO v_linked_item_key
        ,v_top_item_key
        ,v_pegging_quantity
        ,v_top_sourcing_lane_key
        ,v_bh_effective_from_dt
		,v_bh_effective_to_dt
		,v_bli_effective_from_dt
		,v_bli_effective_to_dt
		,v_group_id
		,v_bom_line_alt_type
		,v_priority;
        
        EXIT WHEN c_ItemCache%NOTFOUND;
    
        INSERT INTO pcm_collab_extract (collab_extract_key, op_code, item_key, linked_item_key, top_item_key, item_quantity, sourcing_lane_key, top_sourcing_lane_key, effective_from_dt, effective_to_dt, parent_effective_from_dt, parent_effective_to_dt, alternate_group_id, alternate_type, alternate_priority)
        VALUES (v_collab_extract_key, v_op_code, p_item_key, v_linked_item_key, v_top_item_key, v_pegging_quantity, p_sourcing_lane_key, v_top_sourcing_lane_key, v_bli_effective_from_dt, v_bli_effective_to_dt, v_bh_effective_from_dt, v_bh_effective_to_dt, v_group_id, v_bom_line_alt_type, v_priority);
      END LOOP;
      
      IF p_debug = 1 THEN	
	    DBMS_OUTPUT.put_line ('[ITEM CACHE COUNT] ' || c_ItemCache%ROWCOUNT);
	  END IF;
	  
	  v_item_cache_cnt := c_ItemCache%ROWCOUNT;
	  
	  CLOSE c_ItemCache;
	  
	  IF v_item_cache_cnt = 0 THEN
	    -- no cached item info, walk the bom
	    OPEN c_FlatBoms;
	    LOOP
          FETCH c_FlatBoms
          INTO v_top_item_key
          ,v_path
          ,v_pegging_quantity;
          
          EXIT WHEN c_FlatBoms%NOTFOUND;
        
          v_linked_item := GetLinkedItem(v_path, p_status, p_debug);
          v_bom_line_item_key := GetBomLineItem(v_path);
          
          BEGIN
	        SELECT bh_bom_key
            ,bh_effective_from_dt
            ,bh_effective_to_dt
            ,bli_effective_from_dt
            ,bli_effective_to_dt
            ,group_id
            ,bom_line_alt_type
            ,priority
            ,item_quantity
            INTO
		    v_bh_bom_key
		    ,v_bh_effective_from_dt
		    ,v_bh_effective_to_dt
		    ,v_bli_effective_from_dt
		    ,v_bli_effective_to_dt
		    ,v_group_id
		    ,v_bom_line_alt_type
		    ,v_priority
		    ,v_bli_item_quantity
            FROM iv_bh_bli bh_bli
            WHERE bh_bli.bom_line_item_key = v_bom_line_item_key;
          
            OPEN c_TopSourcingLanes;
            LOOP
              FETCH c_TopSourcingLanes INTO v_top_sourcing_lane_key;
            
              EXIT WHEN c_TopSourcingLanes%NOTFOUND;
        
              INSERT INTO pcm_collab_extract (collab_extract_key, op_code, item_key, linked_item_key, top_item_key, item_quantity, sourcing_lane_key, top_sourcing_lane_key, effective_from_dt, effective_to_dt, parent_effective_from_dt, parent_effective_to_dt, alternate_group_id, alternate_type, alternate_priority)
              VALUES (v_collab_extract_key, v_op_code, p_item_key, v_linked_item.linked_item_key, v_top_item_key, v_pegging_quantity, p_sourcing_lane_key, v_top_sourcing_lane_key, v_bli_effective_from_dt, v_bli_effective_to_dt, v_bh_effective_from_dt, v_bh_effective_to_dt, v_group_id, v_bom_line_alt_type, v_priority);
              
              INSERT INTO pcm_collab_extract_item_cache (collab_extract_key, item_key, linked_item_key, top_item_key, item_quantity, top_sourcing_lane_key, effective_from_dt, effective_to_dt, parent_effective_from_dt, parent_effective_to_dt, alternate_group_id, alternate_type, alternate_priority)
              VALUES (v_collab_extract_key, p_item_key, v_linked_item.linked_item_key, v_top_item_key, v_pegging_quantity, v_top_sourcing_lane_key, v_bli_effective_from_dt, v_bli_effective_to_dt, v_bh_effective_from_dt, v_bh_effective_to_dt, v_group_id, v_bom_line_alt_type, v_priority);
            END LOOP;
          
            v_top_sourcing_lane_cnt := c_TopSourcingLanes%ROWCOUNT;
            CLOSE c_TopSourcingLanes;
          
            IF v_top_sourcing_lane_cnt = 0 THEN
              INSERT INTO pcm_collab_extract (collab_extract_key, op_code, item_key, linked_item_key, top_item_key, item_quantity, sourcing_lane_key, top_sourcing_lane_key, effective_from_dt, effective_to_dt, parent_effective_from_dt, parent_effective_to_dt, alternate_group_id, alternate_type, alternate_priority)
              VALUES (v_collab_extract_key, v_op_code, p_item_key, v_linked_item.linked_item_key, v_top_item_key, v_pegging_quantity, p_sourcing_lane_key, null, v_bli_effective_from_dt, v_bli_effective_to_dt, v_bh_effective_from_dt, v_bh_effective_to_dt, v_group_id, v_bom_line_alt_type, v_priority);
              
              INSERT INTO pcm_collab_extract_item_cache (collab_extract_key, item_key, linked_item_key, top_item_key, item_quantity, top_sourcing_lane_key, effective_from_dt, effective_to_dt, parent_effective_from_dt, parent_effective_to_dt, alternate_group_id, alternate_type, alternate_priority)
              VALUES (v_collab_extract_key, p_item_key, v_linked_item.linked_item_key, v_top_item_key, v_pegging_quantity, null, v_bli_effective_from_dt, v_bli_effective_to_dt, v_bh_effective_from_dt, v_bh_effective_to_dt, v_group_id, v_bom_line_alt_type, v_priority);
            END IF;
            
            GetBomLineAlternates(v_op_code, v_bh_bom_key, v_bom_line_item_key, v_group_id, p_item_key, v_linked_item, v_top_item_key, v_pegging_quantity, v_bli_item_quantity);
            
            EXCEPTION
		      WHEN NO_DATA_FOUND THEN NULL;
          END;
        END LOOP;

        IF p_debug = 1 THEN	
	      DBMS_OUTPUT.put_line ('[BOM COUNT] ' || c_FlatBoms%ROWCOUNT);
	    END IF;

	    v_flat_bom_cnt := c_FlatBoms%ROWCOUNT;
        CLOSE c_FlatBoms;
        
        IF v_flat_bom_cnt = 0 THEN
          INSERT INTO pcm_collab_extract (collab_extract_key, op_code, item_key, linked_item_key, top_item_key, item_quantity, sourcing_lane_key, top_sourcing_lane_key)
          VALUES (v_collab_extract_key, v_op_code, p_item_key, null, null, null, p_sourcing_lane_key, null);
      
          INSERT INTO pcm_collab_extract_item_cache (collab_extract_key, item_key, linked_item_key, top_item_key, item_quantity, top_sourcing_lane_key)
          VALUES (v_collab_extract_key, p_item_key, null, null, null, null);
        END IF;
        
      END IF;
    END IF;
  END GetCollabExtract;
  
  FUNCTION GetLinkedItem (
    p_path VARCHAR2,
    p_status VARCHAR2,
	p_debug NUMBER)
    RETURN LinkedItem IS 
    
    v_item_key item_master.item_key%TYPE;
    v_path_elements string_array := string_array();
    v_element_values string_array := string_array();
    v_linked_item LinkedItem;
    v_item_quantity NUMBER := 1;
    v_linked_item_cnt NUMBER := 0;
    v_i NUMBER := 2;
    
  BEGIN
    v_path_elements := split_string(p_path,'/');
    IF v_path_elements.count > 0 THEN
      LOOP
        v_linked_item_cnt := 0;
        
        v_element_values := split_string(v_path_elements(v_i),':');
        
        IF v_element_values.count > 0 THEN
          v_item_key := v_element_values(1);
          v_item_quantity := v_item_quantity * v_element_values(2);
        
          SELECT SUM(cnt)
          INTO v_linked_item_cnt
          FROM
          (SELECT count(*) cnt
          FROM pcm_sourcing_lane
          WHERE collaboration = 1
          AND status = p_status
          AND item_key = v_item_key
          UNION ALL
          SELECT count(*) cnt
          FROM item_master
          WHERE collaboration = 1
          AND item_key = v_item_key);
        
          IF v_linked_item_cnt > 0 THEN
            v_linked_item.linked_item_key := v_item_key;
            v_linked_item.item_quantity := v_item_quantity;
            EXIT;
          END IF;
        END IF;
        v_i := v_i + 1;
        EXIT WHEN v_i > v_path_elements.count;
      END LOOP;
    END IF;
    
    -- If we did not find a linked item then just set the linked item
    -- to the top item
    IF v_linked_item_cnt = 0 THEN
   
      v_linked_item.linked_item_key := v_item_key;
      v_linked_item.item_quantity := v_item_quantity;
    END IF;
    
    RETURN v_linked_item;
  END GetLinkedItem;
  
  FUNCTION GetBomLineItem (
    p_path VARCHAR2)
    RETURN NUMBER IS 
    
    v_path_elements string_array := string_array();
    v_element_values string_array := string_array();
    v_bom_line_item_key bom_line_item.bom_line_item_key%TYPE;
    
  BEGIN
    v_path_elements := split_string(p_path,'/');
    IF v_path_elements.count > 0 THEN
      v_element_values := split_string(v_path_elements(2),':');
      IF v_element_values.count > 0 THEN
        v_bom_line_item_key := v_element_values(3);
      END IF; 
    END IF;
    
    RETURN v_bom_line_item_key;
  END GetBomLineItem;
  
  FUNCTION split_string(
    p_str VARCHAR2,
    p_delimiter CHAR DEFAULT ',')
    RETURN string_array IS
    
    return_value string_array := string_array();
    split_str LONG DEFAULT p_str || p_delimiter;
    i NUMBER;
  BEGIN
    LOOP
      i := instr(split_str, p_delimiter);
      EXIT WHEN NVL(i,0) = 0;
      return_value.extend;
      return_value(return_value.count) := TRIM(SUBSTR(split_str, 1, i-1));
      split_str := SUBSTR(split_str, i + LENGTH(p_delimiter));
    end loop;
    return return_value;
  END split_string;
  
  PROCEDURE GetBomLineAlternates (
    p_op_code in pcm_collab_extract.op_code%TYPE,
    p_bom_key in bom_header.bom_key%TYPE,
    p_bom_line_item_key in bom_line_item.bom_line_item_key%TYPE,
    p_group_id in bom_line_item.group_id%TYPE, 
    p_item_key in item_master.item_key%TYPE,
    p_linked_item in LinkedItem,
    p_top_item_key in item_master.item_key%TYPE,
    p_pegging_quantity in bom_line_item.item_quantity%TYPE,
    p_bli_item_quantity in bom_line_item.item_quantity%TYPE)
    AS
    
    v_bh_bom_key bom_header.bom_key%TYPE;
    v_bh_item_key item_master.item_key%TYPE;
    v_bom_line_item_key bom_line_item.bom_line_item_key%TYPE;
    v_bli_item_key item_master.item_key%TYPE;
    v_bli_business_entity_key business_entity.business_entity_key%TYPE;
    v_bli_item_quantity bom_line_item.item_quantity%TYPE;
    v_pegging_quantity bom_line_item.item_quantity%TYPE;
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
	  ,v_bli_item_quantity
	  ,v_bh_effective_from_dt
	  ,v_bh_effective_to_dt
	  ,v_bli_effective_from_dt
	  ,v_bli_effective_to_dt
	  ,v_group_id
	  ,v_bom_line_alt_type
	  ,v_priority;
	  EXIT WHEN c_BomLineAlternates%NOTFOUND;
	  
	  v_pegging_quantity := p_pegging_quantity / p_bli_item_quantity * v_bli_item_quantity;
	      
	  INSERT INTO pcm_collab_extract (collab_extract_key, op_code, item_key, linked_item_key, top_item_key, item_quantity, effective_from_dt, effective_to_dt, parent_effective_from_dt, parent_effective_to_dt, alternate_group_id, alternate_type, alternate_priority)
      VALUES (v_collab_extract_key, p_op_code, v_bli_item_key, p_linked_item.linked_item_key, p_top_item_key, v_pegging_quantity, v_bli_effective_from_dt, v_bli_effective_to_dt, v_bh_effective_from_dt, v_bh_effective_to_dt, v_group_id, v_bom_line_alt_type, v_priority);
	END LOOP;
	CLOSE c_BomLineAlternates;
  END GetBomLineAlternates;
END CollabExtract;
/
