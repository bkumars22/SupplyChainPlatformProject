WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

CREATE TABLE bom_auto_approve_set
(
  extract_key VARCHAR2(32) NOT NULL,
  bom_key NUMBER(19) NOT NULL
) TABLESPACE AUDIT_DATA;

CREATE INDEX XBAAS_I1 ON bom_auto_approve_set(extract_key,bom_key) TABLESPACE AUDIT_INDEX;

CREATE TABLE bom_auto_approve_log
(
  extract_key VARCHAR2(32) NOT NULL,
  message_date TIMESTAMP NOT NULL,
  message VARCHAR2(1024) NOT NULL
) TABLESPACE AUDIT_DATA;

CREATE INDEX XBAAL_I1 ON bom_auto_approve_log(extract_key) TABLESPACE AUDIT_INDEX;

CREATE PACKAGE BomAutoApprove AS
  v_extract_key bom_auto_approve_set.extract_key%TYPE;
  v_action_performed pcm_audit_history.action_performed%TYPE;
  v_user_id pcm_audit_history.user_id%TYPE;
  v_role_id pcm_audit_history.role_id%TYPE;
  
  PROCEDURE ApproveBom(
    out_result out SYS_REFCURSOR,
    p_top_item_only VARCHAR2,
    p_load_source VARCHAR2);
    
  PROCEDURE ApproveSingleBom(
    out_result out SYS_REFCURSOR,
    p_bom_key bom_header.bom_key%TYPE,
    p_action_performed pcm_audit_history.action_performed%TYPE,
    p_user_id pcm_audit_history.user_id%TYPE,
    p_role_id pcm_audit_history.role_id%TYPE);
  
  PROCEDURE updateSubBomKeysIfNeeded(
    p_bom_key bom_header.bom_key%TYPE,
	p_status bom_header.status%TYPE);
	
  PROCEDURE updateSubBomKeysIfNeededExt(
    out_result out SYS_REFCURSOR,
    p_bom_key bom_header.bom_key%TYPE,
	p_status bom_header.status%TYPE);
	
  FUNCTION getBomLineChildBom(
    p_item_key item_master.item_key%TYPE,
	p_status bom_header.status%TYPE,
	p_context_object_id bom_header.context_object_id%TYPE,
	p_context_object_type bom_header.context_object_type%TYPE)
	RETURN NUMBER;
	
  PROCEDURE processClose(
    p_root_bom_key bom_header.bom_key%TYPE,
	p_root_item_key item_master.item_key%TYPE,
    p_bom_key bom_header.bom_key%TYPE,
	p_item_key item_master.item_key%TYPE,
	p_startNode BOOLEAN);
	
  PROCEDURE processApprove(
    p_root_bom_key bom_header.bom_key%TYPE,
	p_root_item_key item_master.item_key%TYPE,
    p_bom_key bom_header.bom_key%TYPE,
	p_item_key item_master.item_key%TYPE,
	p_startNode BOOLEAN);
	
  PROCEDURE logMessage(p_message bom_auto_approve_log.message%TYPE);
END BomAutoApprove;
/

CREATE PACKAGE BODY BomAutoApprove AS
  PROCEDURE ApproveBom(
    out_result out SYS_REFCURSOR,
    p_top_item_only VARCHAR2,
    p_load_source VARCHAR2)
	AS
	abort_detected EXCEPTION;
	PRAGMA EXCEPTION_INIT(abort_detected,-20008);
	v_abort NUMBER(5) := 0;
    v_start_bom_key bom_header.bom_key%TYPE;
    v_bom_key bom_header.bom_key%TYPE;
    v_candidate_bom_key bom_header.bom_key%TYPE;
    v_old_bom_key bom_header.bom_key%TYPE;
	v_old_child_bom_key bom_header.bom_key%TYPE;
	v_candidate_child_bom_key bom_header.bom_key%TYPE;
    v_candidate_item_key item_master.item_key%TYPE;
	v_old_child_item_key item_master.item_key%TYPE;
	v_candidate_child_item_key item_master.item_key%TYPE;
	v_old_child_status bom_header.status%TYPE;
    v_candidate_child_status bom_header.status%TYPE;
	v_parent_found INTEGER;
	v_candidateBomCnt INTEGER := 0;
	v_cnt INTEGER := 0;
	v_query VARCHAR2(2048);
	v_context_object_id bom_header.context_object_id%TYPE;
	v_context_object_type bom_header.context_object_type%TYPE;
  
    CURSOR c_ParentBoms IS
    SELECT bh_bom_key bom_key
    FROM iv_bh_bli
    START WITH bli_sub_bom_key = v_candidate_bom_key
    CONNECT BY PRIOR bh_bom_key = bli_sub_bom_key;
	
	CURSOR c_SubBoms IS
    SELECT bli_sub_bom_key bom_key, bli_item_key item_key
    FROM iv_bh_bli
	WHERE bli_sub_bom_key IS NOT NULL
    START WITH bh_bom_key = v_start_bom_key
    CONNECT BY PRIOR bli_sub_bom_key = bh_bom_key;
  
    CURSOR c_CandidateBoms IS
    SELECT bom_key
    FROM bom_auto_approve_set
    WHERE extract_key = v_extract_key;
  BEGIN
	v_action_performed := 'BOM_AUTO_APPROVE';
	v_user_id := 'System';
	
    SELECT sys_guid() INTO v_extract_key FROM dual;
  
    logMessage('Starting Bom Auto Approve.');
	
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
	
    logMessage('Getting Candidate Boms.');
	
	-- Get all the boms that need to be auto approved
	v_query := 'INSERT INTO bom_auto_approve_set
      SELECT :extract_key, bh.bom_key
      FROM bom_header bh
      JOIN item_master i on i.item_key = bh.item_key';
    IF upper(p_top_item_only) = 'TRUE' THEN
      v_query := v_query||' and i.is_top_level = 1';
    END IF;
    v_query := v_query||' WHERE bh.status = :status';
    IF length(p_load_source) > 0 THEN
      v_query := v_query||' and bh.load_source in ('''||p_load_source||''')';
    END IF;
    
    EXECUTE IMMEDIATE v_query USING v_extract_key, 'PENDING';
  
    -- loop thru all the boms that need to be auto approved
    OPEN c_CandidateBoms;
    LOOP
      FETCH c_CandidateBoms INTO v_candidate_bom_key;
      EXIT WHEN c_CandidateBoms%NOTFOUND;
    
	  -- walk up each bom to see if there is a parent bom higher up
	  -- in the hierarchy that will also be approved.  If so
	  -- then remove the bom from the set of candidate boms
	  -- because auto approving the parent bom will cascade
	  -- down and approve this bom.
      OPEN c_ParentBoms;
      LOOP
        FETCH c_ParentBoms INTO v_bom_key;
        EXIT WHEN c_ParentBoms%NOTFOUND;
      
        v_parent_found := 0;
        SELECT count(*) INTO v_parent_found
        FROM bom_auto_approve_set
        WHERE bom_key = v_bom_key;
      
        IF v_parent_found > 0 THEN
          DELETE FROM bom_auto_approve_set
          WHERE extract_key = v_extract_key
          AND bom_key = v_candidate_bom_key;
          EXIT;
        END IF;
      END LOOP;
      CLOSE c_ParentBoms;
    END LOOP;
    CLOSE c_CandidateBoms;
  
    SELECT count(*) INTO v_candidateBomCnt
	FROM bom_auto_approve_set
	WHERE extract_key = v_extract_key;
    logMessage('Approving '||v_candidateBomCnt||' Candidate Boms');
	
    -- Approve each bom
    OPEN c_CandidateBoms;
    LOOP
      FETCH c_CandidateBoms INTO v_candidate_bom_key;
      EXIT WHEN c_CandidateBoms%NOTFOUND;
	  
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
	    RAISE_APPLICATION_ERROR(-20008,'Bom Auto Approve Aborted');
	  END IF;
    
	  -- Get the item key for the candidate bom
	  SELECT item_key, context_object_id, context_object_type
	  INTO v_candidate_item_key, v_context_object_id, v_context_object_type
      FROM bom_header
      WHERE bom_key = v_candidate_bom_key;
    
      BEGIN
	    -- determine if there is an old bom
	    SELECT bom_key
		INTO v_old_bom_key
        FROM bom_header
        WHERE item_key = v_candidate_item_key
        AND status = 'APPROVED'
        AND
        (
          (v_context_object_id IS NULL AND context_object_id IS NULL) OR
          (v_context_object_id IS NOT NULL AND context_object_id = v_context_object_id)
        );
      
	    -- make sure the sub bom keys are up to date for the old bom
	    updateSubBomKeysIfNeeded(v_old_bom_key, 'APPROVED');
		
		-- close the old bom
		processClose(v_old_bom_key, v_candidate_item_key, v_old_bom_key, v_candidate_item_key, true);
		
		-- cascade close the sub boms of the old bom
		v_start_bom_key := v_old_bom_key;
		OPEN c_SubBoms;
		LOOP
		  FETCH c_SubBoms INTO v_old_child_bom_key, v_old_child_item_key;
		  EXIT WHEN c_SubBoms%NOTFOUND;
		  
		  SELECT status INTO v_old_child_status
		  FROM bom_header
		  WHERE bom_key = v_old_child_bom_key;
		  
		  IF v_old_child_status != 'CLOSED' THEN
		    processClose(v_old_bom_key, v_candidate_item_key, v_old_child_bom_key, v_old_child_item_key, false);
		  END IF;
		END LOOP;
		CLOSE c_SubBoms;
      
        EXCEPTION
		    WHEN NO_DATA_FOUND THEN NULL;
      END;
	  
	  -- make sure the sub bom keys are up to date for the candidate bom
	  updateSubBomKeysIfNeeded(v_candidate_bom_key, 'PENDING');
	  
	  -- approve the candidate bom
	  processApprove(v_candidate_bom_key, v_candidate_item_key, v_candidate_bom_key, v_candidate_item_key, true);
	  
	  -- cascade approve the sub boms of the candidate bom
	  v_start_bom_key := v_candidate_bom_key;
	  OPEN c_SubBoms;
	  LOOP
		FETCH c_SubBoms INTO v_candidate_child_bom_key, v_candidate_child_item_key;
		EXIT WHEN c_SubBoms%NOTFOUND;
		
		SELECT status INTO v_candidate_child_status
		FROM bom_header
		WHERE bom_key = v_candidate_child_bom_key;
		  
		IF v_candidate_child_status != 'APPROVED' THEN
		  processApprove(v_candidate_bom_key, v_candidate_item_key, v_candidate_child_bom_key, v_candidate_child_item_key, false);
		END IF;
	  END LOOP;
	  CLOSE c_SubBoms;
	  
	  -- make sure the sub bom keys are up to date for the candidate bom
	  updateSubBomKeysIfNeeded(v_candidate_bom_key, 'APPROVED');
	  
	  v_cnt := v_cnt + 1;
	  
	  -- commit after every 100 boms
	  IF MOD(v_cnt, 100) = 0 THEN
	    logMessage('Processed '||v_cnt||' boms.');
	    COMMIT;
	  END IF;
    END LOOP;
    CLOSE c_CandidateBoms;
    
	-- Update the job status to complete
	UPDATE pcm_job_status_control
	SET job_status = 'COMPLETE',
	job_end_date = current_timestamp
	WHERE job_id = v_extract_key;
	
	-- Clean up the candidate bom set
	DELETE FROM bom_auto_approve_set
	WHERE extract_key = v_extract_key;
	
	logMessage('Processed '||v_cnt||' total boms.');
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
		
		DELETE FROM bom_auto_approve_set
		WHERE extract_key = v_extract_key;
	    
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END;
  
  PROCEDURE ApproveSingleBom(
    out_result out SYS_REFCURSOR,
    p_bom_key bom_header.bom_key%TYPE,
    p_action_performed pcm_audit_history.action_performed%TYPE,
    p_user_id pcm_audit_history.user_id%TYPE,
    p_role_id pcm_audit_history.role_id%TYPE)
	AS
	abort_detected EXCEPTION;
	PRAGMA EXCEPTION_INIT(abort_detected,-20008);
	v_abort NUMBER(5) := 0;
    v_start_bom_key bom_header.bom_key%TYPE;
    v_old_bom_key bom_header.bom_key%TYPE;
	v_old_child_bom_key bom_header.bom_key%TYPE;
	v_child_bom_key bom_header.bom_key%TYPE;
    v_item_key item_master.item_key%TYPE;
	v_old_child_item_key item_master.item_key%TYPE;
	v_child_item_key item_master.item_key%TYPE;
	v_old_child_status bom_header.status%TYPE;
    v_child_status bom_header.status%TYPE;
	v_cnt INTEGER := 0;
	v_context_object_id bom_header.context_object_id%TYPE;
	v_context_object_type bom_header.context_object_type%TYPE;
	
	CURSOR c_SubBoms IS
    SELECT bli_sub_bom_key bom_key, bli_item_key item_key
    FROM iv_bh_bli
	WHERE bli_sub_bom_key IS NOT NULL
    START WITH bh_bom_key = v_start_bom_key
    CONNECT BY PRIOR bli_sub_bom_key = bh_bom_key;
  
    CURSOR c_CandidateBoms IS
    SELECT bom_key
    FROM bom_auto_approve_set
    WHERE extract_key = v_extract_key;
  BEGIN
	v_action_performed := p_action_performed;
	v_user_id := p_user_id;
	v_role_id := p_role_id;
	
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
    
	-- Get the item key for the candidate bom
	SELECT item_key INTO v_item_key
    FROM bom_header
    WHERE bom_key = p_bom_key;
    
    BEGIN
	  BEGIN
	    SELECT context_object_id, context_object_type
	    INTO v_context_object_id, v_context_object_type
	    FROM bom_header
	    WHERE bom_key = p_bom_key;
	    
	    EXCEPTION
		  WHEN NO_DATA_FOUND THEN NULL;
	  END;
	
	  -- determine if there is an old bom
	  SELECT bom_key
	  INTO v_old_bom_key
      FROM bom_header
      WHERE item_key = v_item_key
      AND status = 'APPROVED'
      AND
      (
        (v_context_object_id IS NULL AND context_object_id IS NULL) OR
        (v_context_object_id IS NOT NULL AND context_object_id = v_context_object_id)
      );
      
	  -- make sure the sub bom keys are up to date for the old bom
	  updateSubBomKeysIfNeeded(v_old_bom_key, 'APPROVED');
		
	  -- close the old bom
	  processClose(v_old_bom_key, v_item_key, v_old_bom_key, v_item_key, true);
		
	  -- cascade close the sub boms of the old bom
	  v_start_bom_key := v_old_bom_key;
	  OPEN c_SubBoms;
	  LOOP
		FETCH c_SubBoms INTO v_old_child_bom_key, v_old_child_item_key;
		EXIT WHEN c_SubBoms%NOTFOUND;
		  
		SELECT status INTO v_old_child_status
		FROM bom_header
		WHERE bom_key = v_old_child_bom_key;
		  
		IF v_old_child_status != 'CLOSED' THEN
		  processClose(v_old_bom_key, v_item_key, v_old_child_bom_key, v_old_child_item_key, false);
		END IF;
	  END LOOP;
	  CLOSE c_SubBoms;
      
      EXCEPTION
		WHEN NO_DATA_FOUND THEN NULL;
    END;
	  
	-- make sure the sub bom keys are up to date for the candidate bom
	updateSubBomKeysIfNeeded(p_bom_key, 'PENDING');
	  
	-- approve the candidate bom
	processApprove(p_bom_key, v_item_key, p_bom_key, v_item_key, true);
	
	-- cascade approve the sub boms of the candidate bom
	v_start_bom_key := p_bom_key;
	OPEN c_SubBoms;
	LOOP
	  FETCH c_SubBoms INTO v_child_bom_key, v_child_item_key;
	  EXIT WHEN c_SubBoms%NOTFOUND;
		
	  SELECT status INTO v_child_status
	  FROM bom_header
	  WHERE bom_key = v_child_bom_key;
		  
	  IF v_child_status != 'APPROVED' THEN
		processApprove(p_bom_key, v_item_key, v_child_bom_key, v_child_item_key, false);
	  END IF;
	END LOOP;
	CLOSE c_SubBoms;
	
	-- make sure the sub bom keys are up to date for the candidate bom
	updateSubBomKeysIfNeeded(p_bom_key, 'APPROVED');
	
	v_cnt := v_cnt + 1;
	  
	-- commit after every 100 boms
	IF MOD(v_cnt, 100) = 0 THEN
	  logMessage('Processed '||v_cnt||' boms.');
	  COMMIT;
	END IF;
	  
	-- Update the job status to complete
	UPDATE pcm_job_status_control
	SET job_status = 'COMPLETE',
	job_end_date = current_timestamp
	WHERE job_id = v_extract_key;
	
	logMessage('Processed '||v_cnt||' total boms.');
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
  
  PROCEDURE updateSubBomKeysIfNeeded(
    p_bom_key bom_header.bom_key%TYPE,
	p_status bom_header.status%TYPE) AS
	
    v_item_key item_master.item_key%TYPE;
	v_sub_bom_key bom_header.bom_key%TYPE;
	v_context_object_id bom_header.context_object_id%TYPE;
	v_context_object_type bom_header.context_object_type%TYPE;
	v_bom_line_item_key bom_line_item.bom_line_item_key%TYPE;
	
	CURSOR c_BomLines IS
	SELECT item_key, sub_bom_key, bom_line_item_key
	FROM bom_line_item
	WHERE bom_key = p_bom_key;
  BEGIN
	SELECT context_object_id, context_object_type
	INTO v_context_object_id, v_context_object_type
	FROM bom_header
	WHERE bom_key = p_bom_key;
	
    OPEN c_BomLines;
	LOOP
	  FETCH c_BomLines INTO v_item_key, v_sub_bom_key, v_bom_line_item_key;
	  EXIT WHEN c_BomLines%NOTFOUND;
	  
	  IF v_sub_bom_key IS NULL THEN
	    v_sub_bom_key := getBomLineChildBom(v_item_key, p_status, v_context_object_id, v_context_object_type);
		IF v_sub_bom_key IS NOT NULL THEN
		  UPDATE bom_line_item
		  SET sub_bom_key = v_sub_bom_key
		  WHERE bom_line_item_key = v_bom_line_item_key;
		  
		  updateSubBomKeysIfNeeded(v_sub_bom_key, p_status);
		END IF;
	  END IF;
	END LOOP;
	CLOSE c_BomLines;
	
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END;
  
  PROCEDURE updateSubBomKeysIfNeededExt(
    out_result out SYS_REFCURSOR,
    p_bom_key bom_header.bom_key%TYPE,
	p_status bom_header.status%TYPE) AS
  BEGIN
    updateSubBomKeysIfNeeded(p_bom_key,p_status);
    
    OPEN out_result FOR
	SELECT 1 as count FROM dual;
  END;
  
  FUNCTION getBomLineChildBom(
    p_item_key item_master.item_key%TYPE,
	p_status bom_header.status%TYPE,
	p_context_object_id bom_header.context_object_id%TYPE,
	p_context_object_type bom_header.context_object_type%TYPE)
	RETURN NUMBER IS
	
    v_bom_key bom_header.bom_key%TYPE;
  BEGIN
    BEGIN
	  IF p_context_object_id IS NOT NULL AND p_context_object_type IS NOT NULL THEN
        SELECT bom_key INTO v_bom_key
	    FROM
	    (SELECT bom_key
	     FROM bom_header
	     WHERE item_key = p_item_key
	     AND context_object_id = p_context_object_id
	     AND context_object_type = p_context_object_type
	     AND status = p_status
	     ORDER BY version DESC, effective_from_dt DESC)
	    WHERE ROWNUM <= 1;
	  ELSE
	  	SELECT bom_key INTO v_bom_key
	    FROM
	    (SELECT bom_key
	     FROM bom_header
	     WHERE item_key = p_item_key
	     AND context_object_id IS NULL
	     AND context_object_type IS NULL
	     AND status = p_status
	     ORDER BY version DESC, effective_from_dt DESC)
	    WHERE ROWNUM <= 1;
	  END IF;
	
	  EXCEPTION
        WHEN NO_DATA_FOUND THEN NULL;
	END;
    
	RETURN v_bom_key;
	
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END;
  
  PROCEDURE processClose(
    p_root_bom_key bom_header.bom_key%TYPE,
	p_root_item_key item_master.item_key%TYPE,
    p_bom_key bom_header.bom_key%TYPE,
	p_item_key item_master.item_key%TYPE,
	p_startNode BOOLEAN)
	AS
	v_parentBomLineCnt INTEGER;
	v_altBomCnt INTEGER;
	v_new_bom_key bom_header.bom_key%TYPE;
	v_allowClose BOOLEAN := true;
	v_auditMsg VARCHAR2(1024);
	v_actionPerformed pcm_audit_history.action_performed%TYPE := v_action_performed;
	v_root_item_identifier item_master.item_identifier%TYPE;
	v_root_version bom_header.version%TYPE;
	v_root_revision bom_header.revision%TYPE;
	v_item_identifier item_master.item_identifier%TYPE;
	v_version bom_header.version%TYPE;
	v_revision bom_header.revision%TYPE;
	v_audit_record_key pcm_audit_history.audit_record_key%TYPE;
	v_context_object_id bom_header.context_object_id%TYPE;
	v_context_object_type bom_header.context_object_type%TYPE;
  BEGIN
	BEGIN
	  SELECT context_object_id, context_object_type
	  INTO v_context_object_id, v_context_object_type
	  FROM bom_header
	  WHERE bom_key = p_bom_key;
	    
	  EXCEPTION
		WHEN NO_DATA_FOUND THEN NULL;
	END; 
	
    SELECT count(*) INTO v_parentBomLineCnt
	FROM bom_line_item bli
	JOIN bom_header b ON
	b.bom_key = bli.bom_key AND b.status IN ('APPROVED','PENDING')
	AND
    (
      (v_context_object_id IS NULL AND b.context_object_id IS NULL) OR
      (v_context_object_id IS NOT NULL AND b.context_object_id = v_context_object_id)
    )
	WHERE bli.item_key = p_item_key;
	
	-- Check to see if the bom is a sub bom for any parent bom lines
	IF v_parentBomLineCnt > 0 THEN
	  SELECT count(*) INTO v_altBomCnt
	  FROM bom_header
	  WHERE bom_key != p_bom_key
	  AND item_key = p_item_key
	  AND status IN ('APPROVED','PENDING')
	  AND
      (
        (v_context_object_id IS NULL AND context_object_id IS NULL) OR
        (v_context_object_id IS NOT NULL AND context_object_id = v_context_object_id)
      );
	  
	  -- Check to see if there are any alternate boms
	  IF v_altBomCnt = 0 THEN
	    -- If the bom we are closing is the starting bom and there are no alternate boms
		-- then update all the parent bom lines so that they
		-- no longer point to the closed sub bom.
		--
		-- If the bom we are closing is not the starting node but instead is a being closed
		-- as part of a cascading close, then skip the bom if it has no alternates
	    IF p_startNode = true THEN
		  UPDATE
		  (SELECT sub_bom_key AS sub_bom_key
		   FROM bom_line_item bli
		   JOIN bom_header b ON b.bom_key = bli.bom_key AND b.status IN ('APPROVED','PENDING')
		   AND
           (
             (v_context_object_id IS NULL AND b.context_object_id IS NULL) OR
             (v_context_object_id IS NOT NULL AND b.context_object_id = v_context_object_id)
           )
		   WHERE bli.item_key = p_item_key)
		  SET sub_bom_key = null;
		ELSE
		  v_allowClose := false;
		END IF;
	  ELSE
	    -- We found an alternate so change the sub bom key
		-- of the parent bom line from the bom being closed
		-- to the alternate bom
		SELECT bom_key INTO v_new_bom_key
		FROM
		(SELECT bom_key
		 FROM bom_header
		 WHERE bom_key != p_bom_key
		 AND item_key = p_item_key
		 AND status IN ('APPROVED','PENDING')
		 AND
         (
          (v_context_object_id IS NULL AND context_object_id IS NULL) OR
          (v_context_object_id IS NOT NULL AND context_object_id = v_context_object_id)
         )
		 ORDER BY version DESC, effective_from_dt DESC)
		WHERE ROWNUM <= 1;
		
	    UPDATE
		(SELECT sub_bom_key AS sub_bom_key
		 FROM bom_line_item bli
		 JOIN bom_header b ON b.bom_key = bli.bom_key AND b.status IN ('APPROVED','PENDING')
		 AND
         (
           (v_context_object_id IS NULL AND b.context_object_id IS NULL) OR
           (v_context_object_id IS NOT NULL AND b.context_object_id = v_context_object_id)
         )
		 WHERE bli.item_key = p_item_key
		)
		SET sub_bom_key = v_new_bom_key;
	  END IF;
	END IF;
	
	IF v_allowClose = true THEN
	  UPDATE bom_header
	  SET status = 'CLOSED',
	  status_change_date = sysdate,
	  status_last_change_by = v_user_id
	  WHERE bom_key = p_bom_key;
	  
	  SELECT i.item_identifier, b.version, b.revision
	  INTO v_root_item_identifier, v_root_version, v_root_revision
	  FROM bom_header b
	  JOIN item_master i ON i.item_key = b.item_key
	  WHERE bom_key = p_root_bom_key;
		
	  IF p_bom_key = p_root_bom_key THEN
	    v_auditMsg := 'BOM[Item='||v_root_item_identifier||', Version='||v_root_revision||'/'||v_root_version||'] CLOSED';
	  ELSE
	    v_actionPerformed := '*'||v_actionPerformed;
		
	    SELECT i.item_identifier, b.version, b.revision
	    INTO v_item_identifier, v_version, v_revision
	    FROM bom_header b
		JOIN item_master i ON i.item_key = b.item_key
	    WHERE bom_key = p_bom_key;
		
		v_auditMsg := 'CLOSED BOM[Item='||v_item_identifier||', Version='||v_revision||'/'||v_version||']; as result of changing state to CLOSED for BOM[Item='||v_root_item_identifier||', Version='||v_root_revision||'/'||v_root_version||']';
	  END IF;
	  
	  SELECT sys_guid() INTO v_audit_record_key FROM dual;
	  INSERT INTO pcm_audit_history
	  (audit_record_key,action_date,action_performed,action_order,user_id,pcm_target_type,action_comment,role_id)
	  VALUES
	  (v_audit_record_key,systimestamp,v_actionPerformed,pcm_audit_history_seq.nextval,v_user_id,'Bom',v_auditMsg,v_role_id);
	  
	  INSERT INTO pcm_audit_history_targets
	  (audit_record_key,pcm_target_key)
	  VALUES
	  (v_audit_record_key,p_root_bom_key);
	  
	  IF(p_bom_key != p_root_bom_key) THEN
	    INSERT INTO pcm_audit_history_targets
	    (audit_record_key,pcm_target_key)
	    VALUES
	    (v_audit_record_key,p_bom_key);
	  END IF;
	END IF;
	
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END;
  
  PROCEDURE processApprove(
    p_root_bom_key bom_header.bom_key%TYPE,
	p_root_item_key item_master.item_key%TYPE,
    p_bom_key bom_header.bom_key%TYPE,
	p_item_key item_master.item_key%TYPE,
	p_startNode BOOLEAN)
	AS
	v_alternate_bom_key bom_header.bom_key%TYPE;
	v_auditMsg VARCHAR2(1024);
	v_actionPerformed pcm_audit_history.action_performed%TYPE := v_action_performed;
	v_root_item_identifier item_master.item_identifier%TYPE;
	v_root_version bom_header.version%TYPE;
	v_root_revision bom_header.revision%TYPE;
	v_alternate_item_identifier item_master.item_identifier%TYPE;
	v_alternate_version bom_header.version%TYPE;
	v_alternate_revision bom_header.revision%TYPE;
	v_item_identifier item_master.item_identifier%TYPE;
	v_version bom_header.version%TYPE;
	v_revision bom_header.revision%TYPE;
	v_audit_record_key pcm_audit_history.audit_record_key%TYPE;
	v_alternateFound BOOLEAN := false;
	v_context_object_id bom_header.context_object_id%TYPE;
	v_context_object_type bom_header.context_object_type%TYPE;
	
	CURSOR c_AlternateBoms IS
	SELECT bom_key
	FROM bom_header
	WHERE bom_key != p_bom_key
	AND item_key = p_item_key
	AND status = 'APPROVED'
	AND
    (
      (v_context_object_id IS NULL AND context_object_id IS NULL) OR
      (v_context_object_id IS NOT NULL AND context_object_id = v_context_object_id)
    );
	  
  BEGIN
	BEGIN
	  SELECT context_object_id, context_object_type
	  INTO v_context_object_id, v_context_object_type
	  FROM bom_header
	  WHERE bom_key = p_bom_key;
	    
	  EXCEPTION
		WHEN NO_DATA_FOUND THEN NULL;
	END;
	
    OPEN c_AlternateBoms;
	LOOP
	  FETCH c_AlternateBoms INTO v_alternate_bom_key;
	  EXIT WHEN c_AlternateBoms%NOTFOUND;
	  
	  v_alternateFound := true;
	  
	  -- close the alternate bom
	  UPDATE bom_header
	  SET status = 'CLOSED',
	  status_change_date = sysdate,
	  status_last_change_by = v_user_id,
	  effective_to_dt = sysdate
	  WHERE bom_key = v_alternate_bom_key;
	  
	  SELECT i.item_identifier, b.version, b.revision
	  INTO v_alternate_item_identifier, v_alternate_version, v_alternate_revision
	  FROM bom_header b
	  JOIN item_master i ON i.item_key = b.item_key
	  WHERE b.bom_key = v_alternate_bom_key;
	
	  SELECT i.item_identifier, b.version, b.revision
	  INTO v_root_item_identifier, v_root_version, v_root_revision
	  FROM bom_header b
	  JOIN item_master i ON i.item_key = b.item_key
	  WHERE b.bom_key = p_root_bom_key;
		
	  v_auditMsg := 'CLOSED BOM[Item='||v_alternate_item_identifier||', Version='||v_alternate_revision||'/'||v_alternate_version||']; as result of changing state to APPROVED for BOM[Item='||v_root_item_identifier||', Version='||v_root_revision||'/'||v_root_version||']';
	  
	  -- This is a cascading approval
	  IF p_bom_key != p_root_bom_key THEN
	    v_actionPerformed := '*'||v_actionPerformed;
	  END IF;
	  
	  SELECT sys_guid() INTO v_audit_record_key FROM dual;
	  INSERT INTO pcm_audit_history
	  (audit_record_key,action_date,action_performed,action_order,user_id,pcm_target_type,action_comment,role_id)
	  VALUES
	  (v_audit_record_key,systimestamp,v_actionPerformed,pcm_audit_history_seq.nextval,v_user_id,'Bom',v_auditMsg,v_role_id);
	  
	  INSERT INTO pcm_audit_history_targets
	  (audit_record_key,pcm_target_key)
	  VALUES
	  (v_audit_record_key,p_root_bom_key);
	  
	  INSERT INTO pcm_audit_history_targets
	  (audit_record_key,pcm_target_key)
	  VALUES
	  (v_audit_record_key,v_alternate_bom_key);
	END LOOP;
	CLOSE c_AlternateBoms;
	
	UPDATE bom_header
	SET status = 'APPROVED',
	current_flag = 'N',
	status_change_date = sysdate,
	status_last_change_by = v_user_id
	WHERE bom_key = p_bom_key;
	
	-- link all the sub boms
	-- to the bom being approved if the bom line lives
	-- in an APPROVED or PENDING bom
	UPDATE
	(SELECT sub_bom_key AS sub_bom_key
	 FROM bom_line_item bli
	 JOIN bom_header b ON b.bom_key = bli.bom_key AND b.status IN ('APPROVED','PENDING')
	 AND
     (
       (v_context_object_id IS NULL AND b.context_object_id IS NULL) OR
       (v_context_object_id IS NOT NULL AND b.context_object_id = v_context_object_id)
     )
	 WHERE bli.item_key = p_item_key
	)
	SET sub_bom_key = p_bom_key;
	  
	SELECT i.item_identifier, b.version, b.revision
	INTO v_root_item_identifier, v_root_version, v_root_revision
	FROM bom_header b
	JOIN item_master i ON i.item_key = b.item_key
	WHERE bom_key = p_root_bom_key;
		
	IF p_bom_key = p_root_bom_key THEN
	  v_auditMsg := 'BOM[Item='||v_root_item_identifier||', Version='||v_root_revision||'/'||v_root_version||'] APPROVED';
	ELSE
	  v_actionPerformed := '*'||v_actionPerformed;
		
	  SELECT i.item_identifier, b.version, b.revision
	  INTO v_item_identifier, v_version, v_revision
	  FROM bom_header b
	  JOIN item_master i ON i.item_key = b.item_key
	  WHERE bom_key = p_bom_key;
		
	  v_auditMsg := 'APPROVED BOM[Item='||v_item_identifier||', Version='||v_revision||'/'||v_version||']; as result of changing state to APPROVED for BOM[Item='||v_root_item_identifier||', Version='||v_root_revision||'/'||v_root_version||']';
	END IF;
	  
	SELECT sys_guid() INTO v_audit_record_key FROM dual;
	INSERT INTO pcm_audit_history
	(audit_record_key,action_date,action_performed,action_order,user_id,pcm_target_type,action_comment,role_id)
	VALUES
	(v_audit_record_key,systimestamp,v_actionPerformed,pcm_audit_history_seq.nextval,v_user_id,'Bom',v_auditMsg,v_role_id);
	
	INSERT INTO pcm_audit_history_targets
	(audit_record_key,pcm_target_key)
	VALUES
	(v_audit_record_key,p_root_bom_key);
	  
	IF(p_bom_key != p_root_bom_key) THEN
	  INSERT INTO pcm_audit_history_targets
	  (audit_record_key,pcm_target_key)
	  VALUES
	  (v_audit_record_key,p_bom_key);
	END IF;
	
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END;
  
  PROCEDURE logMessage(p_message bom_auto_approve_log.message%TYPE) AS
  BEGIN
    INSERT INTO bom_auto_approve_log
	(extract_key,message_date,message)
	VALUES
	(v_extract_key,systimestamp,p_message);
  END;
end BomAutoApprove;
/
