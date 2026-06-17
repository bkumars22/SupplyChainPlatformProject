WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

CREATE SEQUENCE MARGIN_MESSAGE_SEQ
        INCREMENT BY 1
        START WITH 1
        NOMAXVALUE
        NOMINVALUE
        CACHE 20
        NOCYCLE ORDER;

CREATE TABLE MARGIN_EXTRACT
(
  MARGIN_EXTRACT_KEY VARCHAR2(32) NOT NULL,
  MARGIN_SEQUENCE NUMBER NOT NULL,
  MARGIN_SUB_SEQUENCE NUMBER NOT NULL,
  ITEM_KEY NUMBER(19) NOT NULL,
  MARKET_VALUE DECIMAL(19,6) NOT NULL,
  BUY_VALUE DECIMAL(19,6),
  DERIVED_FLAG VARCHAR2(1) DEFAULT 'N',
  BUY_BUSINESS_ENTITY_KEY NUMBER(19),
  ITEM_QUANTITY NUMBER(19,5)
) TABLESPACE AUDIT_DATA;

CREATE TABLE MARGIN_EXTRACT_MESSAGE
(
  MARGIN_MESSAGE_ID NUMBER NOT NULL,
  MARGIN_EXTRACT_KEY VARCHAR2(32) NOT NULL,
  MARGIN_MESSAGE_DATE DATE NOT NULL,
  MARGIN_MESSAGE VARCHAR2(1024) NOT NULL
) TABLESPACE AUDIT_DATA;

CREATE INDEX XME_MEK_I1 ON MARGIN_EXTRACT(margin_extract_key) tablespace audit_index;

CREATE VIEW IV_MARGIN_EXTRACT AS
 SELECT MARGIN_EXTRACT_KEY,
 MARGIN_SEQUENCE,
 MARGIN_SUB_SEQUENCE,
 I.ITEM_IDENTIFIER,
 I.OWNER_NAME,
 I.BUSINESS_ENTITY_IDENTIFIER,
 I.BUSINESS_ENTITY_TYPE_NAME,
 MARKET_VALUE,
 BUY_VALUE,
 DECODE(M.BUY_BUSINESS_ENTITY_KEY, -1, 'blended', BB.BUSINESS_ENTITY_IDENTIFIER) AS BUY_BUSINESS_ENTITY_IDENTIFIER,
 BB.BUSINESS_ENTITY_TYPE_NAME AS BUY_BUSINESS_ENTITY_TYPE_NAME,
 DERIVED_FLAG,
 ITEM_QUANTITY
 FROM MARGIN_EXTRACT M
 JOIN IV_ITEM_BUSINESS I ON I.ITEM_KEY = M.ITEM_KEY
 LEFT OUTER JOIN IV_BUSINESS BB ON BB.BUSINESS_ENTITY_KEY = M.BUY_BUSINESS_ENTITY_KEY;

CREATE PACKAGE MarginExtract AS
  TYPE BuyRec IS RECORD (
    buy_value margin_extract.buy_value%TYPE,
    buy_business_entity_key margin_extract.buy_business_entity_key%TYPE
  );
  
  v_margin_extract_key margin_extract.margin_extract_key%TYPE;
    
  PROCEDURE GetMargin(
    out_result out SYS_REFCURSOR,
    p_currentDate in DATE,
    p_site_type in iv_site_business.site_type%TYPE,
    p_buy_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_market_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_currency_code in currency_code.currency_code%TYPE,
    p_status in iv_cost_record.status%TYPE,
	p_debug in NUMBER);
  
  FUNCTION GetMarket (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_market_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE,
	p_debug NUMBER)
    RETURN NUMBER;
  
  FUNCTION GetBuy (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_buy_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE,
	p_debug NUMBER)
    RETURN BuyRec;
END MarginExtract;
/

CREATE PACKAGE BODY MarginExtract AS
  PROCEDURE GetMargin (
    out_result out SYS_REFCURSOR,
	p_currentDate in DATE,
    p_site_type in iv_site_business.site_type%TYPE,
    p_buy_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_market_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_currency_code in currency_code.currency_code%TYPE,
    p_status in iv_cost_record.status%TYPE,
	p_debug in NUMBER)
    AS
    
    abort_detected EXCEPTION;
	PRAGMA EXCEPTION_INIT(abort_detected,-20008);
	v_abort NUMBER(5) := 0;
    v_item_key iv_item_business.item_key%TYPE;
    v_item_identifier iv_item_business.item_identifier%TYPE;
    v_business_entity_key iv_item_business.business_entity_key%TYPE;
    v_business_entity_identifier iv_item_business.business_entity_identifier%TYPE;
    v_business_entity_type_name iv_item_business.business_entity_type_name%TYPE;
    v_site_key iv_site_business.site_key%TYPE;
    v_bom_key iv_bom_item.bom_key%TYPE;
    v_bom_item_key iv_bom_item.item_key%TYPE;
    v_pegging_qty iv_bh_bli.item_quantity%TYPE;
    v_market_value margin_extract.market_value%TYPE;
    v_temp_market_value margin_extract.market_value%TYPE;
    v_derived_market_value margin_extract.market_value%TYPE;
    v_derived_buy_value margin_extract.buy_value%TYPE;
	v_derived_buy_value_missing NUMBER := 0;
	v_ErrorCode NUMBER;
    v_ErrorText VARCHAR2(200);
    v_insert_cnt NUMBER := 0;
	v_process_cnt NUMBER := 0;
	v_seq NUMBER := 0;
	v_sub_seq NUMBER := 0;
    
    v_buy_rec BuyRec;
  
    CURSOR c_TopItems IS
    SELECT item_key, business_entity_key FROM iv_item_business
	WHERE is_top_level = 1;
    
    CURSOR c_FlatBom IS
    SELECT item_key,SUM(pegging_qty) as pegging_qty FROM
    (SELECT bli_item_key item_key, calculateExpression('1'||sys_connect_by_path(item_quantity,'*')) pegging_qty FROM
      iv_bh_bli
      WHERE status = p_status
      START WITH bh_item_key = v_item_key and status = p_status
      CONNECT BY PRIOR bli_item_key = bh_item_key and status = p_status)
    GROUP BY item_key;
    
  BEGIN
    BEGIN
	  SELECT sys_guid() INTO v_margin_extract_key FROM dual;
	  
	  EXCEPTION WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20000, 'Cannot get the key for the Margin Extract');
	END;
    
    -- Insert the job status
	INSERT INTO job_status_control (
	job_id,
	job_status,
  	job_start_date,
  	job_end_date)
	VALUES (
	v_margin_extract_key,
	'RUNNING',
	current_timestamp,
	null);

	COMMIT;

	BEGIN
    	SELECT site_key INTO v_site_key FROM iv_site_business
		WHERE site_type = p_site_type
    	AND parent_site_key is null
		AND business_entity_type_key = 1;

    	EXCEPTION
      	  WHEN NO_DATA_FOUND THEN
		      IF p_debug = 1 THEN	
	  		    DBMS_OUTPUT.put_line ('[NO ' || p_site_type || ' SITE] for ENTERPRISE');
			  END IF;
		  RAISE_APPLICATION_ERROR(-20000, 'No ' || p_site_type || ' site for ENTERPRISE');
	END;

    OPEN c_TopItems;

	IF p_debug = 1 THEN	
	  DBMS_OUTPUT.put_line ('[TOP ITEM COUNT] ' || c_TopItems%ROWCOUNT);
	END IF;

    LOOP
      FETCH c_TopItems INTO v_item_key, v_business_entity_key;
      
      EXIT WHEN c_TopItems%NOTFOUND;

	  -- Check if the extract should abort
	  BEGIN
		SELECT count(*)
		INTO v_abort
		FROM job_status_control
		WHERE job_id = v_margin_extract_key
		AND job_status = 'ABORT';

		EXCEPTION
		  WHEN NO_DATA_FOUND THEN NULL;
	  END;

      IF v_abort > 0 THEN
		RAISE_APPLICATION_ERROR(-20008,'Margin Extract Aborted');
	  END IF;

	  v_market_value := GetMarket(v_item_key, v_site_key, p_currentDate, p_market_cost_type, p_currency_code, p_status, p_debug);
      
	  IF v_market_value IS NOT NULL THEN
        
        v_buy_rec := GetBuy(v_item_key, v_site_key, p_currentDate, p_buy_cost_type, p_currency_code, p_status, p_debug);
        
        INSERT INTO margin_extract (margin_extract_key, margin_sequence, margin_sub_sequence, item_key, market_value, buy_value, buy_business_entity_key)
        VALUES (v_margin_extract_key, v_seq, 0, v_item_key, v_market_value, v_buy_rec.buy_value, v_buy_rec.buy_business_entity_key);
        
        v_insert_cnt := v_insert_cnt+1;

		IF mod(v_insert_cnt, 100) = 0 THEN    -- Commit every 1000 records
          COMMIT;
        END IF;
      END IF;
      
      v_seq := v_seq + 1;
      
      BEGIN
		v_derived_buy_value_missing := 0;
		v_derived_market_value := null;
		v_derived_buy_value := null;
		v_sub_seq := 1;

        OPEN c_FlatBom;
        LOOP
          FETCH c_FlatBom INTO v_bom_item_key, v_pegging_qty;
          
          EXIT WHEN c_FLATBOM%NOTFOUND;
          
          v_temp_market_value := GetMarket(v_bom_item_key, v_site_key, p_currentDate, p_market_cost_type, p_currency_code, p_status, p_debug);
          
          IF v_temp_market_value IS NOT NULL THEN
            v_buy_rec := GetBUY(v_bom_item_key, v_site_key, p_currentDate, p_buy_cost_type, p_currency_code, p_status, p_debug);
            
            IF v_derived_market_value IS NULL THEN
              v_derived_market_value := (v_temp_market_value * v_pegging_qty);
            ELSE
              v_derived_market_value := (v_temp_market_value * v_pegging_qty) + v_derived_market_value;
            END IF;
            
            IF v_buy_rec.buy_value IS NOT NULL THEN
              IF v_derived_buy_value IS NULL THEN
                v_derived_buy_value := (v_buy_rec.buy_value * v_pegging_qty);
              ELSE
                v_derived_buy_value := (v_buy_rec.buy_value * v_pegging_qty) + v_derived_buy_value;
              END IF;
			ELSE
			  v_derived_buy_value_missing := 1;
            END IF;
            
            INSERT INTO margin_extract (margin_extract_key,margin_sequence,margin_sub_sequence,item_key,market_value,buy_value,buy_business_entity_key,item_quantity)
          	VALUES (v_margin_extract_key, v_seq, v_sub_seq, v_bom_item_key, v_temp_market_value, v_buy_rec.buy_value, v_buy_rec.buy_business_entity_key, v_pegging_qty);
          
          END IF;
          
          v_sub_seq := v_sub_seq + 1;
        END LOOP;

		CLOSE c_FlatBom;
        
        IF v_derived_market_value IS NOT NULL THEN
		  IF v_derived_buy_value_missing = 1 THEN
		    v_derived_buy_value := NULL;
		  END IF;

          INSERT INTO margin_extract (margin_extract_key,margin_sequence,margin_sub_sequence,item_key,market_value,buy_value,derived_flag)
          VALUES (v_margin_extract_key, v_seq, 0, v_item_key, v_derived_market_value, v_derived_buy_value, 'Y');
          
          v_insert_cnt := v_insert_cnt+1;

		  IF mod(v_insert_cnt, 100) = 0 THEN    -- Commit every 1000 records
            COMMIT;
          END IF;
        
        END IF;
        
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
		    IF p_debug = 1 THEN	
	  		  DBMS_OUTPUT.put_line ('[NO BOM] ' || v_item_key);
			END IF;
      END;
      
	  v_process_cnt := v_process_cnt+1;

	  IF p_debug > 0 THEN
		IF mod(v_process_cnt, 1000) = 0 THEN
	      INSERT INTO MARGIN_EXTRACT_MESSAGE VALUES
		  (margin_message_seq.nextval,v_margin_extract_key,sysdate,'Processed '||v_process_cnt||' items');
		  COMMIT;
		END IF;
      END IF;
      
      v_seq := v_seq + 1;
    END LOOP;
  
    CLOSE c_TopItems;

	UPDATE job_status_control
	SET job_status = 'COMPLETE',
	job_end_date = current_timestamp
	WHERE job_id = v_margin_extract_key;
	
	COMMIT;

    OPEN out_result FOR
	  SELECT ROWNUM AS REQUIRED_UNIQUE_ID, v_margin_extract_key AS MARGIN_EXTRACT_KEY FROM DUAL;

    EXCEPTION
      WHEN abort_detected THEN
		UPDATE job_status_control
		SET job_status = 'ABORTED',
		job_end_date = current_timestamp
		WHERE job_id = v_margin_extract_key;
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
      WHEN OTHERS THEN
        ROLLBACK;
		DELETE FROM MARGIN_EXTRACT WHERE MARGIN_EXTRACT_KEY = v_margin_extract_key;
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END GetMargin;
  
  FUNCTION GetMarket (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_market_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE,
	p_debug NUMBER)
    RETURN NUMBER IS 
  
    v_market_value NUMBER := null;
  BEGIN
    SELECT cr_total
    INTO v_market_value
    FROM iv_cost_record
    WHERE item_key = p_item_key
    AND to_site_key = p_site_key
    AND currency_code = p_currency_code
    AND cost_type_key = p_market_cost_type
    AND status = p_status
    AND (effective_from_dt IS NULL OR effective_from_dt <= trunc(p_currentDate))
    AND (effective_to_dt IS NULL OR effective_to_dt >= trunc(p_currentDate));
  
    RETURN v_market_value;
    
    EXCEPTION
      WHEN NO_DATA_FOUND THEN
		IF p_debug = 1 THEN	
			DBMS_OUTPUT.put_line ('[NO ' || p_market_cost_type || '] item: ' || p_item_key);
		END IF;
		RETURN v_market_value;
      WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20021,SQLERRM(SQLCODE)||' for item '||p_item_key);
  END GetMarket;

  FUNCTION GetBuy (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_buy_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE,
	p_debug NUMBER)
    RETURN BuyRec IS 
    
    v_buy_rec BuyRec;
    
    v_buy_value margin_extract.buy_value%TYPE;
    v_temp_buy_value margin_extract.buy_value%TYPE;
    v_blended_buy_value margin_extract.buy_value%TYPE;
    v_buy_business_entity_key margin_extract.buy_business_entity_key%TYPE;
    v_supplier_part_key iv_item_avl.supplier_part_key%TYPE;
    v_supplier_key iv_item_business.business_entity_key%TYPE;
    v_allocation iv_supplier_allocation.allocation%TYPE;
    v_allocation_total iv_supplier_allocation.allocation%TYPE := 0;
	v_allocation_count NUMBER := 0;
    
    CURSOR c_ItemAvl IS
    SELECT supplier_part_key, s_business_entity_key FROM iv_item_avl
	WHERE item_key = p_item_key;
  BEGIN
    OPEN c_ItemAvl;
    LOOP
      FETCH c_ItemAvl INTO v_supplier_part_key, v_supplier_key;
      
      EXIT WHEN c_ItemAvl%NOTFOUND;

	  IF p_debug = 1 THEN	
		DBMS_OUTPUT.put_line ('[AVL] item: ' || v_supplier_part_key || ' for item: ' || p_item_key);
	  END IF;
      
      v_temp_buy_value := null;

	  BEGIN
	    SELECT cr_total
	    INTO v_temp_buy_value
	    FROM iv_cost_record
	    WHERE item_key = p_item_key
	    AND supplier_key = v_supplier_key
	    AND to_site_key = p_site_key
	    AND currency_code = p_currency_code
	    AND cost_type_key = p_buy_cost_type
	    AND status = p_status
	    AND (effective_from_dt IS NULL OR effective_from_dt <= trunc(p_currentDate))
	    AND (effective_to_dt IS NULL OR effective_to_dt >= trunc(p_currentDate));
        
        EXCEPTION
          WHEN NO_DATA_FOUND THEN
		    IF p_debug = 1 THEN	
		      DBMS_OUTPUT.put_line ('[NO ' || p_buy_cost_type || ']  item: ' || p_item_key);
			END IF;
          WHEN OTHERS THEN
          RAISE_APPLICATION_ERROR(-20022,SQLERRM(SQLCODE)||' for item '||p_item_key||' for supplier '||v_supplier_key);
      END;

	  IF v_temp_buy_value IS NOT NULL THEN
    	IF p_debug = 1 THEN	
		  DBMS_OUTPUT.put_line ('[' || p_buy_cost_type || '] item: ' || p_item_key || ' supplier: ' || v_supplier_key || ' value: ' || v_temp_buy_value);
		END IF;

		IF v_buy_value IS NULL THEN
          v_buy_value := v_temp_buy_value;
          v_buy_business_entity_key := v_supplier_key;
        ELSIF v_buy_value > v_temp_buy_value THEN
          v_buy_value := v_temp_buy_value;
          v_buy_business_entity_key := v_supplier_key;
        END IF;
        v_buy_rec.buy_value := v_buy_value;
        v_buy_rec.buy_business_entity_key := v_buy_business_entity_key;

		IF p_debug = 1 THEN	
		  DBMS_OUTPUT.put_line ('[LOWEST ' || p_buy_cost_type || '] item: ' || p_item_key || ' supplier: ' || v_buy_business_entity_key || ' value: ' || v_buy_value );
		END IF;
        
		v_allocation := null;

        BEGIN
          SELECT allocation
          INTO v_allocation
          FROM iv_supplier_allocation
          WHERE customer_item_group_item_key IS NULL
		  AND customer_item_key = p_item_key
          AND supplier_item_key = v_supplier_part_key
          AND (effective_from_dt IS NULL OR effective_from_dt <= trunc(p_currentDate))
          AND (effective_to_dt IS NULL OR effective_to_dt >= trunc(p_currentDate));
        
          EXCEPTION
            WHEN NO_DATA_FOUND THEN
			  IF p_debug = 1 THEN	
				DBMS_OUTPUT.put_line ('[NO ALLOCATION] item: ' || p_item_key || ' supplier item: ' || v_supplier_part_key);
			  END IF;
            WHEN OTHERS THEN
            RAISE_APPLICATION_ERROR(-20023,SQLERRM(SQLCODE)||' for item '||p_item_key);
        END;
        
		IF v_allocation IS NOT NULL THEN
		  IF p_debug = 1 THEN	
			DBMS_OUTPUT.put_line ('[ALLOCATION] item: ' || p_item_key || ' supplier item: ' || v_supplier_part_key || ' supplier allocation: ' || v_allocation);
		  END IF;
          IF v_blended_buy_value IS NULL THEN
            v_blended_buy_value := v_temp_buy_value * (v_allocation / 100);
          ELSE
            v_blended_buy_value := v_blended_buy_value + (v_temp_buy_value * (v_allocation / 100));
          END IF;
		  IF p_debug = 1 THEN	
			DBMS_OUTPUT.put_line ('[BLENDED VALUE] item: ' || p_item_key || ' blended ' || p_buy_cost_type || ' value: ' || v_blended_buy_value);
		  END IF;
          v_allocation_total := v_allocation_total + v_allocation;
		  v_buy_business_entity_key := v_supplier_key;
		  v_allocation_count := v_allocation_count + 1;
        END IF;
      END IF;
	END LOOP;
  
	IF p_debug = 1 THEN	
	  DBMS_OUTPUT.put_line ('[ALLOCATION TOTAL] item: ' || p_item_key || ' total allocation ' || v_allocation_total);
	END IF;

    IF v_allocation_total = 100 THEN
      v_buy_rec.buy_value := v_blended_buy_value;
	  v_buy_rec.buy_business_entity_key := v_buy_business_entity_key;
      IF v_allocation_count > 1 THEN
	    v_buy_rec.buy_business_entity_key := -1;
	  END IF;
    END IF;

	IF p_debug = 1 THEN	
	  DBMS_OUTPUT.put_line ('[TOTAL ' || p_buy_cost_type || '] item: ' || p_item_key || ' supplier: ' || v_buy_rec.buy_business_entity_key || ' value: ' || v_buy_rec.buy_value);
	END IF;
    
    RETURN v_buy_rec;
  END GetBuy;
END MarginExtract;
/
