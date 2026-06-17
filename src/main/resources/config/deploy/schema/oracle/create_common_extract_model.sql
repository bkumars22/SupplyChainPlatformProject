WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

-- the common package used by collab and bom extract
CREATE PACKAGE MTCMExtractCommon AS
  PROCEDURE PruneBomLines (
    p_status in bom_header.status%TYPE,
    p_op_code in item_change_audit.op_code%TYPE,
	p_debug in NUMBER);
	
  PROCEDURE FindAffectedItemsFromBomLines (
    p_currentDate in DATE,
    p_status in bom_header.status%TYPE,
    p_op_code in item_change_audit.op_code%TYPE,
    p_update_op_code in item_change_audit.op_code%TYPE,
	p_debug in NUMBER);
END MTCMExtractCommon;
/

-- the collab extract package body
CREATE PACKAGE BODY MTCMExtractCommon AS
  PROCEDURE PruneBomLines (
	p_status in bom_header.status%TYPE,
	p_op_code in item_change_audit.op_code%TYPE,
	p_debug in NUMBER)
    AS
    
    v_item_key item_master.item_key%TYPE;
    v_bh_item_key item_master.item_key%TYPE;
    v_parent_found INTEGER;
    
    CURSOR c_FlatBoms IS
    SELECT bh_item_key item_key FROM
    iv_bh_bli
    WHERE status = p_status
    START WITH bli_item_key = v_item_key
    AND status = p_status
    CONNECT BY PRIOR bh_bom_key = bli_sub_bom_key AND PRIOR status = status;
    
    CURSOR c_AuditBomLineItems IS
    SELECT DISTINCT item_key FROM
    item_change_audit
    WHERE op_code = p_op_code;
  BEGIN
	OPEN c_AuditBomLineItems;
	LOOP
	  FETCH c_AuditBomLineItems INTO v_item_key;
      EXIT WHEN c_AuditBomLineItems%NOTFOUND;
      
	  v_parent_found := 0;
	  OPEN c_FlatBoms;
	  LOOP
	    FETCH c_FlatBoms INTO v_bh_item_key;
	    EXIT WHEN c_FlatBoms%NOTFOUND;
	    
	    SELECT count(*) INTO v_parent_found
	    FROM ITEM_CHANGE_AUDIT
	    WHERE ITEM_KEY = v_bh_item_key
	    AND OP_CODE = p_op_code;
	    
	    IF v_parent_found >= 1 THEN
	      DELETE FROM ITEM_CHANGE_AUDIT
	      WHERE item_key = v_item_key
	      AND OP_CODE = p_op_code;
	      EXIT;
	    END IF;
	  END LOOP;
	  CLOSE c_FlatBoms;
    END LOOP;
    CLOSE c_AuditBomLineItems;
  END PruneBomLines;
  
  PROCEDURE FindAffectedItemsFromBomLines (
	p_currentDate in DATE,
	p_status in bom_header.status%TYPE,
	p_op_code in item_change_audit.op_code%TYPE,
	p_update_op_code in item_change_audit.op_code%TYPE,
	p_debug in NUMBER)
    AS
    
    v_item_key item_master.item_key%TYPE;
    v_affected_item_key item_master.item_key%TYPE;
    v_cnt NUMBER;
    
    CURSOR c_FlatBoms IS
	SELECT bhbli.bli_item_key
	FROM iv_bh_bli bhbli
    START WITH bli_item_key = v_item_key
    AND status = p_status
    CONNECT BY bh_bom_key = PRIOR bli_sub_bom_key AND status = PRIOR status;
    
    CURSOR c_AuditBomLineItems IS
    SELECT DISTINCT item_key FROM
    item_change_audit
    WHERE op_code = p_op_code;
  BEGIN
	OPEN c_AuditBomLineItems;
	LOOP
	  FETCH c_AuditBomLineItems INTO v_item_key;
      EXIT WHEN c_AuditBomLineItems%NOTFOUND;
	
      OPEN c_FlatBoms;
	  LOOP
	    FETCH c_FlatBoms INTO v_affected_item_key;
	    EXIT WHEN c_FlatBoms%NOTFOUND;
			
	    v_cnt := 0;
	    SELECT sum(amount) INTO v_cnt FROM
	    (
	      SELECT count(*) amount
		  FROM pcm_sourcing_lane
		  WHERE item_key = v_affected_item_key
		  AND collaboration = 1
		  UNION ALL
		  SELECT count(*)
		  FROM item_master
		  WHERE item_key = v_affected_item_key
		  AND collaboration = 1
	    );
		  
	    IF v_cnt > 0 THEN
		  INSERT INTO ITEM_CHANGE_AUDIT
		  (ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE)
		  VALUES
		  (ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_affected_item_key,p_currentDate,p_update_op_code);
	    END IF;
	  END LOOP;
	  CLOSE c_FlatBoms;
	END LOOP;
    CLOSE c_AuditBomLineItems;
  END FindAffectedItemsFromBomLines;
END MTCMExtractCommon;
/
