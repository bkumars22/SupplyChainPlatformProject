WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

-- Model to support the Best BOM Report
CREATE TABLE BEST_BOM_EXTRACT
(
  BEST_BOM_EXTRACT_KEY VARCHAR2(32) NOT NULL,
  ITEM_KEY NUMBER(19) NOT NULL,
  ITEM_CATEGORY_KEY NUMBER(19),
  MANAGED_FLAG VARCHAR2(64),
  BEST_PRICE NUMBER(19,6),
  INTERCHANGEABLE_ITEM_KEY NUMBER(19),
  BEST_PRICE_ITEM_KEY NUMBER(19),
  BEST_PRICE_BUSINESS NUMBER(19),
  BEST_PRICE_COST_TYPE_KEY VARCHAR2(32),
  DELL_PRICE NUMBER(19,6),
  LOWEST_DELL_PRICE DECIMAL(19,6),
  LOWEST_DELL_PRICE_ITEM_KEY NUMBER(19),
  EM_PRICE NUMBER(19,6),
  LOWEST_EM_PRICE NUMBER(19,6),
  LOWEST_EM_PRICE_BUSINESS NUMBER(19),
  PEGGING_QUANTITY NUMBER(19,5)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XBBE_BBEI_I1 ON BEST_BOM_EXTRACT(best_bom_extract_key) tablespace audit_index;

-- Related Items Sets. This tabel is a cache of items related to each other. If the 
-- AVL changes then this table needs to be regenerated
CREATE TABLE BEST_BOM_RELATED_ITEM_SETS 
   (    
   SET_KEY VARCHAR2(32 BYTE) NOT NULL ENABLE, 
   RELATED_ITEM_KEY NUMBER(19,0) NOT NULL ENABLE, 
    CONSTRAINT X_BBRIS_PK PRIMARY KEY (RELATED_ITEM_KEY)
   ) TABLESPACE AUDIT_DATA;

CREATE UNIQUE INDEX X_BBRIS_UNQ ON BEST_BOM_RELATED_ITEM_SETS (SET_KEY, RELATED_ITEM_KEY) tablespace audit_index; 

-- For an extract, this table keeps a track of items and their related items.
CREATE TABLE BEST_BOM_RELATED_ITEM 
   (
   BEST_BOM_EXTRACT_KEY VARCHAR2(32 BYTE) NOT NULL ENABLE, 
   ITEM_KEY NUMBER(19,0) NOT NULL ENABLE, 
   RELATED_ITEM_SET_KEY VARCHAR2(32 BYTE) NOT NULL ENABLE, 
   CONSTRAINT X_BBRI_PK PRIMARY KEY (BEST_BOM_EXTRACT_KEY, ITEM_KEY)
   ) TABLESPACE AUDIT_DATA;
   
-- Interchangeable Items Sets. This tabel is a cache of items interchangeable with each other. If the 
-- AVL changes then this table needs to be regenerated 
CREATE TABLE INTERCHANGEABLE_ITEMS_SET 
   (SET_ID VARCHAR2(32 BYTE) NOT NULL ENABLE, 
    ITEM_KEY NUMBER(19,0) NOT NULL ENABLE, 
    CONSTRAINT X_IIS_PK PRIMARY KEY (ITEM_KEY)
    ) TABLESPACE AUDIT_DATA;
   
   
CREATE OR REPLACE VIEW IV_BEST_BOM_EXTRACT AS
 SELECT BB.BEST_BOM_EXTRACT_KEY,
 BB.ITEM_KEY,
 BBI.ITEM_IDENTIFIER,
 BBI.REVISION,
 BBI.ITEM_DESCRIPTION DESCRIPTION,
 BBI.BUSINESS_ENTITY_NAME,
 BBI.BUSINESS_ENTITY_TYPE_NAME,
 IC.ITEM_CATEGORY_NAME,
 PCA.USER_ID,
 BB.MANAGED_FLAG,
 BB.BEST_PRICE,
 II.ITEM_IDENTIFIER INTERCHANGEABLE_ITEM,
 BPI.ITEM_IDENTIFIER BEST_PRICE_ITEM,
 COALESCE(BPB.BUSINESS_ENTITY_NAME,BPI.BUSINESS_ENTITY_NAME) BEST_PRICE_BUSINESS,
 BB.BEST_PRICE_COST_TYPE_KEY,
 BB.DELL_PRICE,
 BB.LOWEST_DELL_PRICE,
 LDPI.ITEM_IDENTIFIER LOWEST_DELL_PRICE_ITEM,
 LDPI.BUSINESS_ENTITY_NAME LOWEST_DELL_PRICE_BUSINESS,
 BB.EM_PRICE,
 BB.LOWEST_EM_PRICE,
 LEPB.BUSINESS_ENTITY_NAME LOWEST_EM_PRICE_BUSINESS,
 BB.PEGGING_QUANTITY
 FROM BEST_BOM_EXTRACT BB
 JOIN IV_ITEM_BUSINESS BBI ON BBI.ITEM_KEY = BB.ITEM_KEY
 LEFT OUTER JOIN IV_ITEM_CATEGORY IC ON IC.ITEM_CATEGORY_KEY = BB.ITEM_CATEGORY_KEY
 LEFT OUTER JOIN IV_PCM_ASSIGNMENT PCA ON PCA.OBJECT_KEY = IC.ITEM_CATEGORY_KEY AND
   PCA.RESPONSIBILITY = 'OWNER'
 LEFT OUTER JOIN IV_ITEM_BUSINESS II ON II.ITEM_KEY = BB.INTERCHANGEABLE_ITEM_KEY
 LEFT OUTER JOIN IV_ITEM_BUSINESS BPI ON BPI.ITEM_KEY = BB.BEST_PRICE_ITEM_KEY
 LEFT OUTER JOIN IV_BUSINESS BPB ON BPB.BUSINESS_ENTITY_KEY = BB.BEST_PRICE_BUSINESS
 LEFT OUTER JOIN IV_ITEM_BUSINESS LDPI ON LDPI.ITEM_KEY = BB.LOWEST_DELL_PRICE_ITEM_KEY
 LEFT OUTER JOIN IV_BUSINESS LEPB ON LEPB.BUSINESS_ENTITY_KEY = BB.LOWEST_EM_PRICE_BUSINESS;

CREATE OR REPLACE VIEW IV_BEST_BOM_RELATED_ITEM ("BEST_BOM_EXTRACT_KEY", "ITEM_KEY", "RELATED_ITEM_KEY", "RELATED_ITEM_IDENTIFIER") AS 
  SELECT BBRI.BEST_BOM_EXTRACT_KEY,BBRI.ITEM_KEY,BBRIS.RELATED_ITEM_KEY,RI.ITEM_IDENTIFIER RELATED_ITEM_IDENTIFIER
    FROM BEST_BOM_RELATED_ITEM BBRI
    JOIN BEST_BOM_RELATED_ITEM_SETS BBRIS on BBRI.RELATED_ITEM_SET_KEY = BBRIS.SET_KEY
    JOIN IV_ITEM_BUSINESS RI ON RI.ITEM_KEY = BBRIS.RELATED_ITEM_KEY;
    
/*
 * Performace Monitoring 
*/

CREATE TABLE PERF_MON 
   (TRANSACT_ID VARCHAR2(32 BYTE) NOT NULL ENABLE, 
    TS TIMESTAMP (6), 
    MESSAGE VARCHAR2(4000 BYTE)
   );

create or replace package pkg_log
as
    FUNCTION logBeg(tid in varchar2,message in varchar2) RETURN TIMESTAMP;
    FUNCTION log(tid in varchar2,message in varchar2) RETURN TIMESTAMP;
    procedure logEnd(tid in varchar2,message in varchar2,startTime in TIMESTAMP);
end pkg_log;
/

create or replace package body pkg_log as
    
    v_log_enabled Boolean := false;
    
    FUNCTION logBeg(tid in varchar2,message in varchar2) RETURN TIMESTAMP AS
    BEGIN
        return log(tid,'Before ' || message);
    END logBeg;
    
    FUNCTION log(tid in varchar2,message in varchar2) RETURN TIMESTAMP is
      v_cur_ts TIMESTAMP;
    begin
       if v_log_enabled THEN
        SELECT CURRENT_TIMESTAMP into v_cur_ts from dual;
        --dbms_output.put_line('TS:' || v_cur_ts || ' M:' || MESSAGE);
        INSERT INTO PERF_MON (TRANSACT_ID,TS,MESSAGE) VALUES(tid,v_cur_ts,message);
        return v_cur_ts;
       else 
        return NULL;
       END IF;
    end log;
    
    PROCEDURE logEnd(tid in varchar2,message in varchar2,startTime in TIMESTAMP) AS 
    v_endTime TIMESTAMP;
    BEGIN 
        if v_log_enabled THEN
          v_endTime := log(tid,'After ' || message);
          --dbms_output.put_line('TS:' || v_endTime || ': DURATION for:' || message || ':'  || (v_endTime-startTime));
          INSERT INTO PERF_MON (TRANSACT_ID,TS,MESSAGE) VALUES(tid,CURRENT_TIMESTAMP,'DURATION for:' || message || ':'  || (v_endTime-startTime));
        END IF;
    END logEnd;
end pkg_log;
/

create or replace PACKAGE BestBomExtract AS
  TYPE t_InterchangeableItems IS TABLE OF iv_item_business.item_key%TYPE
    INDEX BY BINARY_INTEGER;

  TYPE t_RelatedItems IS TABLE OF iv_item_business.item_key%TYPE
    INDEX BY BINARY_INTEGER;

  TYPE t_VisitedItems IS TABLE OF iv_item_business.item_key%TYPE
    INDEX BY BINARY_INTEGER;

  v_InterchangeableItems t_InterchangeableItems;
  v_RelatedItems t_RelatedItems;
  v_VisitedItems t_VisitedItems;
  v_tryToSearchInRelatedItemSet boolean;
  v_RelatedItemsSetKey VARCHAR2(32);

  v_index INTEGER := 1;
  v_interchangeable_item_key iv_item_business.item_key%TYPE;
  v_best_price_item_key iv_item_business.item_key%TYPE;
  v_best_price_business iv_item_business.business_entity_key%TYPE;
  v_best_price_cost_type_key iv_cost_record.cost_type_key%TYPE;
  v_lowest_list_item_key iv_item_business.item_key%TYPE;
  v_lowest_emquote_business iv_item_business.business_entity_key%TYPE;
  v_enterprise_be_key business_entity.business_entity_key%TYPE;

  PROCEDURE GetBestBom (
    out_result out SYS_REFCURSOR,
    p_item_key in iv_item_business.item_key%TYPE,
    p_em_business in iv_item_business.business_entity_key%TYPE,
    p_currentDate in DATE,
    p_site_key in iv_site_business.site_key%TYPE,
    p_site_type in iv_site_business.site_type%TYPE,
    p_list_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_emquote_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_currency_code in currency_code.currency_code%TYPE,
    p_status in iv_cost_record.status%TYPE
  );

  PROCEDURE GetPrices (
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
    p_item_key in iv_item_business.item_key%TYPE,
    p_managed_flag in iv_bh_bli.managed_flag%TYPE,
    p_pegging_quantity in iv_bh_bli.item_quantity%TYPE,
    p_em_business in iv_item_business.business_entity_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate in DATE,
    p_emquote_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_list_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_currency_code in currency_code.currency_code%TYPE,
    p_status in iv_cost_record.status%TYPE
  );

  FUNCTION GetBestPrice (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_emquote_cost_type iv_cost_record.cost_type_key%TYPE,
    p_list_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE
  )RETURN NUMBER;

  FUNCTION GetDellPrice (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE
  )RETURN NUMBER;

  FUNCTION GetLowestListValue (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE
  )RETURN NUMBER;

  FUNCTION GetLowestEmquoteValue (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE
  )RETURN NUMBER;

  FUNCTION GetEmquoteValue (
    p_item_key iv_item_business.item_key%TYPE,
    p_em_business iv_item_business.business_entity_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE
  )RETURN NUMBER;

  PROCEDURE GetInterchangeableItems (
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE
  );

  PROCEDURE GetChildItems (
     p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
     p_supplier_part_key in IV_ITEM_AVL.SUPPLIER_PART_KEY%TYPE
  );

  PROCEDURE GetParentItems (
      p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
      p_supplier_part_key in IV_ITEM_AVL.SUPPLIER_PART_KEY%TYPE
  );

  PROCEDURE GetRelatedItems (
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
    p_odm_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE
  );

  PROCEDURE GetRelatedChildItems (
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
    p_odm_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
    p_supplier_part_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE
  );

  PROCEDURE GetRelatedParentItems (
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
    p_odm_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
    p_supplier_part_key in IV_ITEM_AVL.SUPPLIER_PART_KEY%TYPE
  );
  
  FUNCTION AddRelatedItemsToSet 
  RETURN VARCHAR2;
  
  PROCEDURE AddItemToRelatedItems(
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE);
END BestBomExtract;
/

create or replace PACKAGE BODY BestBomExtract AS

  v_best_bom_extract_key best_bom_extract.best_bom_extract_key%TYPE;

  PROCEDURE GetBestBom (
  out_result out SYS_REFCURSOR,
    p_item_key in iv_item_business.item_key%TYPE,
    p_em_business in iv_item_business.business_entity_key%TYPE,
    p_currentDate in DATE,
    p_site_key in iv_site_business.site_key%TYPE,
    p_site_type in iv_site_business.site_type%TYPE,
    p_list_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_emquote_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_currency_code in currency_code.currency_code%TYPE,
    p_status in iv_cost_record.status%TYPE
  ) AS

    v_start_time TIMESTAMP;
    v_site_key iv_site_business.site_key%TYPE;
    v_bom_key bom_header.bom_key%TYPE;
    v_item_key iv_item_business.item_key%TYPE;
    v_managed_flag item_master.managed_flag%TYPE;
    v_pegging_quantity iv_bh_bli.item_quantity%TYPE;

    CURSOR c_FlatBom IS
      SELECT item_key,managed_flag,SUM(pegging_qty) as pegging_qty FROM
      (SELECT bli_item_key item_key, managed_flag, calculateExpression('1'||sys_connect_by_path(item_quantity,'*')) pegging_qty FROM
        iv_bh_bli
        WHERE status = p_status
        START WITH bh_bom_key = v_bom_key and status = p_status
        CONNECT BY PRIOR bli_sub_bom_key = bh_bom_key and status = p_status)
      GROUP BY item_key,managed_flag;
  BEGIN
    BEGIN
    SELECT sys_guid() INTO v_best_bom_extract_key FROM dual;

    EXCEPTION WHEN OTHERS THEN
        RAISE_APPLICATION_ERROR(-20000, 'Cannot get the key for the Best BOM Report');
    END;

  IF p_site_key IS NULL THEN
      BEGIN
      SELECT site_key INTO v_site_key FROM iv_site_business
        WHERE business_entity_type_key = 1
        AND site_type = p_site_type
        AND parent_site_key is null;

        EXCEPTION
          WHEN NO_DATA_FOUND THEN
          RAISE_APPLICATION_ERROR(-20000, 'No ' || p_site_type || ' site for ENTERPRISE');
      END;
    ELSE
      v_site_key := p_site_key;
    END IF;

    BEGIN
      SELECT bom_key INTO v_bom_key FROM iv_bom_item
      WHERE item_key = p_item_key AND
      status = p_status;

      EXCEPTION
        WHEN NO_DATA_FOUND THEN
        RAISE_APPLICATION_ERROR(-20001, 'No BOM for item ' || p_item_key);
    END;

    SELECT business_entity_key INTO v_enterprise_be_key
  FROM business_entity
  WHERE business_entity_type_key = 1;

    -- Get the prices for the bom item
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetPrices for top item:' || p_item_key);
    GetPrices(v_best_bom_extract_key, p_item_key, null, 1, p_em_business, v_site_key, p_currentDate, p_emquote_cost_type, p_list_cost_type, p_currency_code, p_status);
    pkg_log.logEnd(v_best_bom_extract_key,'GetPrices for top item:' || p_item_key,v_start_time);

    OPEN c_FlatBom;
    LOOP
      FETCH c_FlatBom INTO v_item_key, v_managed_flag, v_pegging_quantity;
      EXIT WHEN c_FlatBom%NOTFOUND;

      -- Get the prices for the bom line items
      GetPrices(v_best_bom_extract_key, v_item_key, v_managed_flag, v_pegging_quantity, p_em_business, v_site_key, p_currentDate, p_emquote_cost_type, p_list_cost_type, p_currency_code, p_status);
    END LOOP;

    CLOSE c_FlatBom;

    COMMIT;

  OPEN out_result FOR
    SELECT ROWNUM AS REQUIRED_UNIQUE_ID, v_best_bom_extract_key AS BEST_BOM_EXTRACT_KEY FROM DUAL;

  EXCEPTION
      WHEN OTHERS THEN
        ROLLBACK;
    DELETE FROM BEST_BOM_EXTRACT WHERE BEST_BOM_EXTRACT_KEY = v_best_bom_extract_key;
    DELETE FROM BEST_BOM_RELATED_ITEM WHERE BEST_BOM_EXTRACT_KEY = v_best_bom_extract_key;
    COMMIT;
    RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END GetBestBom;

  PROCEDURE GetPrices (
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
    p_item_key in iv_item_business.item_key%TYPE,
    p_managed_flag in iv_bh_bli.managed_flag%TYPE,
    p_pegging_quantity in iv_bh_bli.item_quantity%TYPE,
    p_em_business in iv_item_business.business_entity_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate in DATE,
    p_emquote_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_list_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_currency_code in currency_code.currency_code%TYPE,
    p_status in iv_cost_record.status%TYPE
  ) AS

    v_start_time TIMESTAMP;
  v_item_category_key iv_item_category.item_category_key%TYPE;
    v_managed_flag iv_item_business.managed_flag%TYPE;
    v_best_price iv_cost_record.cr_total%TYPE;
    v_dell_price iv_cost_record.cr_total%TYPE;
    v_lowest_dell_price iv_cost_record.cr_total%TYPE;
    v_lowest_dell_price_item_key iv_item_business.item_key%TYPE;
    v_em_price iv_cost_record.cr_total%TYPE;
    v_lowest_em_price iv_cost_record.cr_total%TYPE;
    v_lowest_em_price_business iv_item_business.business_entity_key%TYPE;
  v_business_entity_type_key iv_item_business.business_entity_type_key%TYPE;

    CURSOR c_ItemCategory IS
      SELECT item_category_key FROM iv_item_item_category
      WHERE item_key = p_item_key;
  BEGIN
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetPrices for item:' || p_item_key);
    OPEN c_ItemCategory;
    LOOP
      FETCH c_ItemCategory INTO v_item_category_key;
      EXIT WHEN c_ItemCategory%NOTFOUND;
    END LOOP;
    CLOSE c_ItemCategory;

    v_managed_flag := p_managed_flag;
    IF v_managed_flag IS NULL AND v_item_category_key IS NOT NULL THEN
      BEGIN
        SELECT managed_flag INTO v_managed_flag
        FROM iv_item_category
        WHERE item_category_key = v_item_category_key;

        EXCEPTION
          WHEN NO_DATA_FOUND THEN NULL;
      END;
    END IF;

  SELECT business_entity_type_key INTO v_business_entity_type_key
  FROM iv_item_business
  WHERE item_key = p_item_key;

  IF v_business_entity_type_key != 1 THEN
    GetRelatedItems(p_best_bom_extract_key, p_item_key);
  END IF;

    v_best_price := GetBestPrice(p_item_key, p_site_key, p_currentDate, p_emquote_cost_type, p_list_cost_type, p_currency_code, p_status);
    v_dell_price := GetDellPrice(p_item_key, p_site_key, p_currentDate, p_list_cost_type, p_currency_code, p_status);
    v_lowest_dell_price := GetLowestListValue(p_item_key, p_site_key, p_currentDate, p_list_cost_type, p_currency_code, p_status);
    v_lowest_dell_price_item_key := v_lowest_list_item_key;
    v_em_price := GetEmquoteValue(p_item_key, p_em_business, p_site_key, p_currentDate, p_emquote_cost_type, p_currency_code, p_status);
    v_lowest_em_price := GetLowestEmquoteValue(p_item_key, p_site_key, p_currentDate, p_emquote_cost_type, p_currency_code, p_status);
    v_lowest_em_price_business := v_lowest_emquote_business;

    INSERT INTO best_bom_extract
    (best_bom_extract_key,item_key,item_category_key,
     managed_flag,best_price,interchangeable_item_key,best_price_item_key,
     best_price_business,best_price_cost_type_key,
     dell_price,lowest_dell_price,lowest_dell_price_item_key,
     em_price,lowest_em_price,lowest_em_price_business,
     pegging_quantity)
    VALUES
    (p_best_bom_extract_key,p_item_key,v_item_category_key,
     v_managed_flag,v_best_price,v_interchangeable_item_key,v_best_price_item_key,
     v_best_price_business,v_best_price_cost_type_key,
     v_dell_price,v_lowest_dell_price,v_lowest_dell_price_item_key,
     v_em_price,v_lowest_em_price,v_lowest_em_price_business,
     p_pegging_quantity);
     pkg_log.logEnd(v_best_bom_extract_key,'GetPrices for item:' || p_item_key,v_start_time);
  END GetPrices;

  FUNCTION GetBestPrice (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_emquote_cost_type iv_cost_record.cost_type_key%TYPE,
    p_list_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE
  ) RETURN NUMBER IS
    v_start_time TIMESTAMP;
    v_best_price iv_cost_record.cr_total%TYPE := null;
    v_next_price iv_cost_record.cr_total%TYPE := null;
  BEGIN
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetBestPrice for item:' || p_item_key);
    v_InterchangeableItems.DELETE;
    v_index := 1;
    v_interchangeable_item_key := null;
    v_best_price_item_key := null;
    v_best_price_business := null;
    v_best_price_cost_type_key := null;

    GetInterchangeableItems(p_item_key);

    v_index := v_InterchangeableItems.FIRST;
    LOOP
      v_next_price := GetLowestEmquoteValue(v_InterchangeableItems(v_index), p_site_key, p_currentDate, p_emquote_cost_type, p_currency_code, p_status);
      IF v_next_price IS NOT NULL THEN
        IF v_best_price IS NULL THEN
          v_best_price := v_next_price;
          v_interchangeable_item_key := v_InterchangeableItems(v_index);
          v_best_price_item_key := null;
          v_best_price_business := v_lowest_emquote_business;
          v_best_price_cost_type_key := p_emquote_cost_type;
        ELSIF v_best_price > v_next_price THEN
          v_best_price := v_next_price;
          v_interchangeable_item_key := v_InterchangeableItems(v_index);
          v_best_price_item_key := null;
          v_best_price_business := v_lowest_emquote_business;
          v_best_price_cost_type_key := p_emquote_cost_type;
        END IF;
      END IF;

      v_next_price := GetLowestListValue(v_InterchangeableItems(v_index), p_site_key, p_currentDate, p_list_cost_type, p_currency_code, p_status);
      IF v_next_price IS NOT NULL THEN
        IF v_best_price IS NULL THEN
          v_best_price := v_next_price;
          v_interchangeable_item_key := v_InterchangeableItems(v_index);
          v_best_price_item_key := v_lowest_list_item_key;
          v_best_price_business := null;
          v_best_price_cost_type_key := p_list_cost_type;
        ELSIF v_best_price > v_next_price THEN
          v_best_price := v_next_price;
          v_interchangeable_item_key := v_InterchangeableItems(v_index);
          v_best_price_item_key := v_lowest_list_item_key;
          v_best_price_business := null;
          v_best_price_cost_type_key := p_list_cost_type;
        END IF;
      END IF;

      EXIT WHEN v_Index = v_InterchangeableItems.LAST;
      v_index := v_InterchangeableItems.NEXT(v_index);
    END LOOP;

    IF v_interchangeable_item_key = p_item_key THEN
      v_interchangeable_item_key := null;
    END IF;
    pkg_log.logEnd(v_best_bom_extract_key,'GetBestPrice for item:' || p_item_key,v_start_time);
    RETURN v_best_price;
  END GetBestPrice;

  FUNCTION GetDellPrice (
    p_item_key in iv_item_business.item_key%TYPE,
    p_site_key in iv_site_business.site_key%TYPE,
    p_currentDate in DATE,
    p_cost_type in iv_cost_record.cost_type_key%TYPE,
    p_currency_code in currency_code.currency_code%TYPE,
    p_status in iv_cost_record.status%TYPE)
    RETURN NUMBER IS

    v_start_time TIMESTAMP;
    v_dell_price iv_cost_record.cr_total%TYPE;
    v_temp_list_value iv_cost_record.cr_total%TYPE;
    v_blended_list_value iv_cost_record.cr_total%TYPE;
    v_supplier_part_key iv_item_avl.supplier_part_key%TYPE;
    v_supplier_key iv_item_business.business_entity_key%TYPE;
    v_allocation iv_supplier_allocation.allocation%TYPE;
    v_allocation_total iv_supplier_allocation.allocation%TYPE := 0;

   CURSOR c_ItemAvl IS
      SELECT supplier_part_key, s_business_entity_key
      FROM iv_item_avl
      WHERE item_key = p_item_key;
  BEGIN
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetDellPrice for item:' || p_item_key);
    OPEN c_ItemAvl;
    LOOP
      FETCH c_ItemAvl INTO v_supplier_part_key, v_supplier_key;

      EXIT WHEN c_ItemAvl%NOTFOUND;

      v_temp_list_value := null;

      BEGIN
        SELECT cr_total
        INTO v_temp_list_value
        FROM IV_COST_RECORD
        WHERE item_key = v_supplier_part_key
        AND to_site_key = p_site_key
        AND currency_code = p_currency_code
        AND cost_type_key = p_cost_type
        AND status = p_status
        AND (effective_from_dt IS NULL OR effective_from_dt <= trunc(p_currentDate))
        AND (effective_to_dt IS NULL OR effective_to_dt >= trunc(p_currentDate));

        EXCEPTION
          WHEN NO_DATA_FOUND THEN NULL;
      END;

      IF v_temp_list_value IS NOT NULL THEN
        IF v_dell_price IS NULL THEN
          v_dell_price := v_temp_list_value;
        ELSIF v_dell_price > v_temp_list_value THEN
          v_dell_price := v_temp_list_value;
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
            WHEN NO_DATA_FOUND THEN NULL;
        END;

        IF v_allocation IS NOT NULL THEN
          IF v_blended_list_value IS NULL THEN
            v_blended_list_value := v_temp_list_value * (v_allocation / 100);
          ELSE
            v_blended_list_value := v_blended_list_value + (v_temp_list_value * (v_allocation / 100));
          END IF;
          v_allocation_total := v_allocation_total + v_allocation;
        END IF;
      END IF;
    END LOOP;

    CLOSE c_ItemAvl;

    IF v_allocation_total = 100 THEN
     v_dell_price := v_blended_list_value;
    END IF;
    pkg_log.logEnd(v_best_bom_extract_key,'GetDellPrice for item:' || p_item_key,v_start_time);
    RETURN v_dell_price;
  END GetDellPrice;

  FUNCTION GetLowestListValue (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE)
    RETURN NUMBER IS

    v_start_time TIMESTAMP;
    v_list_value iv_cost_record.cr_total%TYPE;
    v_temp_list_value iv_cost_record.cr_total%TYPE;
    v_supplier_part_key iv_item_avl.supplier_part_key%TYPE;
    v_supplier_key iv_item_avl.s_business_entity_key%TYPE;

    CURSOR c_ItemAvl IS
      SELECT supplier_part_key, s_business_entity_key
      FROM iv_item_avl
      WHERE item_key = p_item_key;
  BEGIN
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetLowestListValue for item:' || p_item_key);
    v_lowest_list_item_key := null;

    OPEN c_ItemAvl;
    LOOP
      FETCH c_ItemAvl INTO v_supplier_part_key, v_supplier_key;

      EXIT WHEN c_ItemAvl%NOTFOUND;

      v_temp_list_value := null;

      BEGIN
        SELECT cr_total
        INTO v_temp_list_value
        FROM IV_COST_RECORD
        WHERE item_key = v_supplier_part_key
    AND to_site_key = p_site_key
        AND currency_code = p_currency_code
        AND cost_type_key = p_cost_type
        AND status = p_status
        AND (effective_from_dt IS NULL OR effective_from_dt <= trunc(p_currentDate))
        AND (effective_to_dt IS NULL OR effective_to_dt >= trunc(p_currentDate));

        EXCEPTION
          WHEN NO_DATA_FOUND THEN NULL;
      END;

      IF v_temp_list_value IS NOT NULL THEN
        IF v_list_value IS NULL THEN
          v_list_value := v_temp_list_value;
          v_lowest_list_item_key := v_supplier_part_key;
        ELSIF v_list_value > v_temp_list_value THEN
          v_list_value := v_temp_list_value;
          v_lowest_list_item_key := v_supplier_part_key;
        END IF;
      END IF;
    END LOOP;

    CLOSE c_ItemAvl;
    pkg_log.logEnd(v_best_bom_extract_key,'GetLowestListValue for item:' || p_item_key,v_start_time);
    RETURN v_list_value;
  END GetLowestListValue;

  FUNCTION GetLowestEmquoteValue (
    p_item_key iv_item_business.item_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE)
    RETURN NUMBER IS

    v_start_time TIMESTAMP;
    v_emquote_value iv_cost_record.cr_total%TYPE;
    v_temp_emquote_value iv_cost_record.cr_total%TYPE;
    v_supplier_part_key iv_item_avl.supplier_part_key%TYPE;
    v_supplier_key iv_item_avl.s_business_entity_key%TYPE;
    v_cost_provider_key iv_business.business_entity_key%TYPE;
    v_avl_count INTEGER := 0;
    v_continue INTEGER := 0;

    CURSOR c_EmquoteCostRecords IS
      SELECT supplier_key,cost_provider_key,cr_total
      FROM IV_COST_RECORD
      WHERE item_key = p_item_key
    AND to_site_key = p_site_key
      AND currency_code = p_currency_code
      AND cost_type_key = p_cost_type
      AND status = p_status
      AND (effective_from_dt IS NULL OR effective_from_dt <= trunc(p_currentDate))
      AND (effective_to_dt IS NULL OR effective_to_dt >= trunc(p_currentDate));
  BEGIN
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetLowestEmquoteValue for item:' || p_item_key);
    v_lowest_emquote_business := null;

    OPEN c_EmquoteCostRecords;
    LOOP
      FETCH c_EmquoteCostRecords INTO
      v_supplier_key, v_cost_provider_key, v_temp_emquote_value;

      EXIT WHEN c_EmquoteCostRecords%NOTFOUND;

      v_continue := 0;

      IF v_cost_provider_key IS NOT NULL OR v_supplier_key IS NULL THEN
        v_continue := 1;
      ELSIF v_supplier_key IS NOT NULL THEN
        SELECT COUNT(*)
        INTO v_avl_count
        FROM iv_item_avl
    WHERE s_business_entity_key = v_supplier_key;

        IF v_avl_count > 0 THEN
          v_continue := 1;
        END IF;
      END IF;

      IF v_continue = 1 THEN
        IF v_temp_emquote_value IS NOT NULL THEN
          IF v_emquote_value IS NULL THEN
            v_emquote_value := v_temp_emquote_value;
            IF v_cost_provider_key IS NOT NULL THEN
              v_lowest_emquote_business := v_cost_provider_key;
            ELSIF v_supplier_key IS NOT NULL THEN
              v_lowest_emquote_business := v_supplier_key;
            END IF;
          ELSIF v_emquote_value > v_temp_emquote_value THEN
            v_emquote_value := v_temp_emquote_value;
            IF v_cost_provider_key IS NOT NULL THEN
              v_lowest_emquote_business := v_cost_provider_key;
            ELSIF v_supplier_key IS NOT NULL THEN
              v_lowest_emquote_business := v_supplier_key;
            END IF;
          END IF;
        END IF;
      END IF;
    END LOOP;
    CLOSE c_EmquoteCostRecords;
    pkg_log.logEnd(v_best_bom_extract_key,'GetLowestEmquoteValue for item:' || p_item_key,v_start_time);
    RETURN v_emquote_value;
  END GetLowestEmquoteValue;

  FUNCTION GetEmquoteValue (
    p_item_key iv_item_business.item_key%TYPE,
    p_em_business iv_item_business.business_entity_key%TYPE,
    p_site_key iv_site_business.site_key%TYPE,
    p_currentDate DATE,
    p_cost_type iv_cost_record.cost_type_key%TYPE,
    p_currency_code currency_code.currency_code%TYPE,
    p_status iv_cost_record.status%TYPE)
    RETURN NUMBER IS

    v_start_time TIMESTAMP;
    v_emquote_value iv_cost_record.cr_total%TYPE;
  v_temp_value iv_cost_record.cr_total%TYPE;

    CURSOR c_EmquoteCostRecords IS
      SELECT cr_total
      FROM IV_COST_RECORD
      WHERE item_key = p_item_key
      AND cost_provider_key = p_em_business
      AND to_site_key = p_site_key
      AND currency_code = p_currency_code
      AND cost_type_key = p_cost_type
      AND status = p_status
      AND (effective_from_dt IS NULL OR effective_from_dt <= trunc(p_currentDate))
      AND (effective_to_dt IS NULL OR effective_to_dt >= trunc(p_currentDate));
  BEGIN
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetEmquoteValue for item:' || p_item_key);
    IF p_em_business IS NOT NULL THEN
    OPEN c_EmquoteCostRecords;
      LOOP
        FETCH c_EmquoteCostRecords INTO v_temp_value;
        EXIT WHEN c_EmquoteCostRecords%NOTFOUND;

    IF v_emquote_value IS NULL OR v_emquote_value > v_temp_value THEN
      v_emquote_value := v_temp_value;
    END IF;
      END LOOP;

      CLOSE c_EmquoteCostRecords;
    END IF;
    pkg_log.logEnd(v_best_bom_extract_key,'GetEmquoteValue for item:' || p_item_key,v_start_time);
    RETURN v_emquote_value;
  END GetEmquoteValue;
  
  /*
  Private Procedure to compute the Interchangeable Items given an Item Key. 
  This procedure populates the INTERCHANGEABLE_ITEMS_SET table and also the
  associative array v_InterchangeableItems.
  */
  PROCEDURE GetInterchangeableItemsCompute (
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE)
  AS
    v_set_id INTERCHANGEABLE_ITEMS_SET.SET_ID%TYPE;
  BEGIN
    v_InterchangeableItems.DELETE;
    GetChildItems(p_item_key,null);
    v_set_id := sys_guid();
    FORALL i IN INDICES OF v_InterchangeableItems
      INSERT INTO INTERCHANGEABLE_ITEMS_SET (set_id,item_key) values (v_set_id,v_InterchangeableItems(i));
  END GetInterchangeableItemsCompute;
  
  /*
  Get Interchangeable Items for a item. This procedure will first check the 
  the Interchangeable_ITEMS_SET table and if it cannot find a set there it will
  call GetInterchangeableItemsCompute. In the end the associative array
  v_InterchangeableItems is populated with the Interchangeable item keys.
  */
  PROCEDURE GetInterchangeableItems(
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE) 
  AS
    v_start_time TIMESTAMP;
    v_set_id VARCHAR(32);
    l_item_key IV_ITEM_BUSINESS.ITEM_KEY%TYPE;
  BEGIN
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetInterchangeableItems for item:' || p_item_key);
    v_InterchangeableItems.DELETE;
    -- Check if interchangeable items was in a set that was computed earlier
    v_set_id := NULL;
    BEGIN
      SELECT SET_ID into v_set_id  
        FROM INTERCHANGEABLE_ITEMS_SET 
        WHERE ITEM_KEY=p_item_key;
    EXCEPTION 
      WHEN NO_DATA_FOUND THEN
      v_set_id := null;
    END;
    IF NOT v_set_id is NULL THEN
      SELECT ITEM_KEY 
      BULK COLLECT INTO v_InterchangeableItems
      from INTERCHANGEABLE_ITEMS_SET 
      WHERE SET_ID = v_set_id;
    ELSE 
      -- Interchangeable items set not found. So compute the set
      -- This populates the v_InterchangeableItems and the INTERCHANGEABLE_ITEMS_SET
      GetInterchangeableItemsCompute(p_item_key);  
    END IF;
    pkg_log.logEnd(v_best_bom_extract_key,'GetInterchangeableItems for item:' || p_item_key,v_start_time);
  END GetInterchangeableItems;

  PROCEDURE GetChildItems (
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
    p_supplier_part_key in IV_ITEM_AVL.SUPPLIER_PART_KEY%TYPE)
  AS
    v_supplier_part_key IV_ITEM_AVL.SUPPLIER_PART_KEY%TYPE;

    CURSOR c_childItems1 IS
      SELECT supplier_part_key FROM item_avl
      WHERE item_key = p_item_key
      AND supplier_part_key is not null;

    CURSOR c_childItems2 IS
      SELECT supplier_part_key FROM item_avl
      WHERE item_key = p_item_key
      AND supplier_part_key != p_supplier_part_key;
  BEGIN
    IF NOT v_InterchangeableItems.EXISTS(p_item_key) THEN
      v_InterchangeableItems(p_item_key) := p_item_key;
      IF p_supplier_part_key IS NULL THEN
        OPEN c_childItems1;
        LOOP
          FETCH c_childItems1 INTO v_supplier_part_key;

          EXIT WHEN c_childItems1%NOTFOUND;

          GetParentItems(p_item_key, v_supplier_part_key);
        END LOOP;

        CLOSE c_childItems1;
      ELSE
        OPEN c_childItems2;
        LOOP
          FETCH c_childItems2 INTO v_supplier_part_key;

          EXIT WHEN c_childItems2%NOTFOUND;

          GetParentItems(p_item_key, v_supplier_part_key);
        END LOOP;

        CLOSE c_childItems2;
      END IF;
    END IF;
  END GetChildItems;

  PROCEDURE GetParentItems (
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
    p_supplier_part_key in IV_ITEM_AVL.SUPPLIER_PART_KEY%TYPE)
  AS
    v_item_key IV_ITEM_AVL.ITEM_KEY%TYPE;

    CURSOR c_parentItems IS
      SELECT item_key FROM item_avl
      WHERE supplier_part_key = p_supplier_part_key
      AND item_key != p_item_key;
  BEGIN
    OPEN c_parentItems;
    LOOP
      FETCH c_parentItems INTO v_item_key;

      EXIT WHEN c_parentItems%NOTFOUND;

      GetChildItems(v_item_key, p_supplier_part_key);
    END LOOP;

    CLOSE c_parentItems;
  END GetParentItems;
  
  PROCEDURE GetRelatedItems (
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
  p_odm_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE)
  AS
  v_start_time TIMESTAMP;
  BEGIN
    v_start_time := pkg_log.logBeg(v_best_bom_extract_key,'GetRelatedItems for item:' || p_odm_item_key);
    v_RelatedItems.DELETE;
    v_VisitedItems.DELETE;
    v_RelatedItemsSetKey := NULL;
    v_tryToSearchInRelatedItemSet := false;
    GetRelatedChildItems(p_best_bom_extract_key, p_odm_item_key, p_odm_item_key, null);
    if v_RelatedItemsSetKey IS NULL THEN
      v_RelatedItemsSetKey := AddRelatedItemsToSet();
    END IF;
    INSERT INTO BEST_BOM_RELATED_ITEM
        (best_bom_extract_key,item_key,related_item_set_key)
      VALUES
        (p_best_bom_extract_key,p_odm_item_key,v_RelatedItemsSetKey);
    pkg_log.logEnd(v_best_bom_extract_key,'GetRelatedItems for item:' || p_odm_item_key,v_start_time);
  END GetRelatedItems;

  FUNCTION AddRelatedItemsToSet
  RETURN VARCHAR2 IS 
    v_set_id VARCHAR2(32);
  BEGIN
    v_set_id := sys_guid();
    FORALL i IN INDICES OF v_RelatedItems
       INSERT INTO BEST_BOM_RELATED_ITEM_SETS
          (SET_KEY, related_item_key)
        VALUES
          (v_set_id, v_RelatedItems(i));
    return v_set_id;
  END AddRelatedItemsToSet;

  PROCEDURE GetRelatedChildItems (
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
  p_odm_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
  p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
  p_supplier_part_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE)
  AS
    v_supplier_part_key IV_ITEM_AVL.SUPPLIER_PART_KEY%TYPE;
  v_business_entity_key IV_ITEM_AVL.BUSINESS_ENTITY_KEY%TYPE;

    CURSOR c_childItems1 IS
      SELECT ia.supplier_part_key,business_entity_key as s_business_entity_key FROM item_avl ia
      WHERE ia.item_key = p_item_key
      AND ia.supplier_part_key is not null;

    CURSOR c_childItems2 IS
      SELECT ia.supplier_part_key,business_entity_key as s_business_entity_key FROM item_avl ia
      WHERE ia.item_key = p_item_key
      AND ia.supplier_part_key != p_supplier_part_key;
  BEGIN
  IF p_odm_item_key = p_item_key THEN
    IF NOT v_VisitedItems.EXISTS(p_item_key) THEN
        v_VisitedItems(p_item_key) := p_item_key;
      END IF;
  END IF;

    IF p_supplier_part_key IS NULL THEN
      OPEN c_childItems1;
      LOOP
        FETCH c_childItems1 INTO v_supplier_part_key, v_business_entity_key;

        EXIT WHEN c_childItems1%NOTFOUND;

        IF NOT v_VisitedItems.EXISTS(v_supplier_part_key) THEN
          v_VisitedItems(v_supplier_part_key) := v_supplier_part_key;
      IF v_business_entity_key = v_enterprise_be_key THEN
      IF NOT v_RelatedItems.EXISTS(v_supplier_part_key) THEN
        AddItemToRelatedItems(p_best_bom_extract_key,v_supplier_part_key);
      END IF;
      END IF;
          IF v_RelatedItemsSetKey IS NULL THEN
            GetRelatedParentItems(p_best_bom_extract_key, p_odm_item_key, p_item_key, v_supplier_part_key);
          ELSE
            EXIT;
          END IF;
        END IF;
      END LOOP;

      CLOSE c_childItems1;
    ELSE
      OPEN c_childItems2;
      LOOP
        FETCH c_childItems2 INTO v_supplier_part_key, v_business_entity_key;

        EXIT WHEN c_childItems2%NOTFOUND;

        IF NOT v_VisitedItems.EXISTS(v_supplier_part_key) THEN
          v_VisitedItems(v_supplier_part_key) := v_supplier_part_key;
      IF v_business_entity_key = v_enterprise_be_key THEN
        IF NOT v_RelatedItems.EXISTS(v_supplier_part_key) THEN
          AddItemToRelatedItems(p_best_bom_extract_key,v_supplier_part_key);
        END IF;
      END IF;
          IF v_RelatedItemsSetKey IS NULL THEN
            GetRelatedParentItems(p_best_bom_extract_key, p_odm_item_key, p_item_key, v_supplier_part_key);
          ELSE
            EXIT;
          END IF;
        END IF;
      END LOOP;

      CLOSE c_childItems2;
    END IF;
  END GetRelatedChildItems;
  
  PROCEDURE AddItemToRelatedItems(
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE) AS
    v_ts TIMESTAMP;
  BEGIN
    -- Check if related item already exists in a set. If so we only need
    -- to set the v_RelatedItemsSetKey
    -- The v_tryToSearchInRelatedItemSet ensures that the search is done only once 
    if not v_tryToSearchInRelatedItemSet THEN
      BEGIN
        SELECT 
          set_key into v_RelatedItemsSetKey 
        FROM
          BEST_BOM_RELATED_ITEM_SETS
        WHERE 
          RELATED_ITEM_KEY = p_item_key; 
        If not v_RelatedItemsSetKey IS NULL THEN
          v_ts := pkg_log.log(p_best_bom_extract_key,'Found Related Item Set with id:' || v_RelatedItemsSetKey);
        END IF;
      EXCEPTION
        WHEN NO_DATA_FOUND THEN
        v_RelatedItemsSetKey := NULL;
      END;
      -- Set this to true so this serach is not done again. 
      -- Since if an item is not found in a set none of the items in the set will be in a set.
      v_tryToSearchInRelatedItemSet := true;
    END IF;
    IF v_RelatedItemsSetKey IS NULL THEN
      v_RelatedItems(p_item_key) := p_item_key;
    END IF;
  END AddItemToRelatedItems;

  PROCEDURE GetRelatedParentItems (
    p_best_bom_extract_key in best_bom_extract.best_bom_extract_key%TYPE,
  p_odm_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
    p_item_key in IV_ITEM_BUSINESS.ITEM_KEY%TYPE,
  p_supplier_part_key in IV_ITEM_AVL.SUPPLIER_PART_KEY%TYPE)
  AS
    v_item_key IV_ITEM_AVL.ITEM_KEY%TYPE;
    v_business_entity_key IV_ITEM_AVL.BUSINESS_ENTITY_KEY%TYPE;

    CURSOR c_parentItems IS
      SELECT ia.item_key,ia.business_entity_key FROM iv_item_avl ia
    WHERE ia.supplier_part_key = p_supplier_part_key
    AND ia.item_key != p_item_key;
  BEGIN
    OPEN c_parentItems;
    LOOP
      FETCH c_parentItems INTO v_item_key, v_business_entity_key;

      EXIT WHEN c_parentItems%NOTFOUND;

    IF NOT v_VisitedItems.EXISTS(v_item_key) THEN
        v_VisitedItems(v_item_key) := v_item_key;
    IF v_business_entity_key = v_enterprise_be_key THEN
      IF NOT v_RelatedItems.EXISTS(v_item_key) THEN
        AddItemToRelatedItems(p_best_bom_extract_key,v_item_key);
      END IF;
    END IF;
      IF v_RelatedItemsSetKey IS NULL THEN
        GetRelatedChildItems(p_best_bom_extract_key, p_odm_item_key, v_item_key, p_supplier_part_key);
      END IF;
      END IF;
    END LOOP;

    CLOSE c_parentItems;
  END GetRelatedParentItems;

END BestBomExtract;
/

-- Interchangeable Items Sets. This tabel is a cache of items interchangeable with each other. If the 
-- AVL changes then this table needs to be regenerated 

-- This trigger clears the Interchangeable Items Sets and related items table when the ITEM_AVL table gets changed. 

create or replace TRIGGER T_CLEAR_BEST_BOM_ITEM_CACHE
AFTER INSERT OR DELETE OR UPDATE OF ITEM_KEY,SUPPLIER_PART_KEY ON ITEM_AVL
	BEGIN
 		DELETE FROM INTERCHANGEABLE_ITEMS_SET;
		DELETE FROM BEST_BOM_RELATED_ITEM;
		DELETE FROM BEST_BOM_RELATED_ITEM_SETS;  
END;
/

