-- Loader tables to store the xml data prior to moving into production tables
CREATE TABLE PCM_LOAD_Item
(
	loadKey VARCHAR2(36)
	,itemIdentifier VARCHAR2(255)
	,itemUniqueId VARCHAR2(255)
	,description VARCHAR2(255)
	,lifeCycleCode VARCHAR2(255)
	,lifeCycleCodeOther VARCHAR2(255)
	,itemType VARCHAR2(255)
	,itemPartType VARCHAR2(255)
	,itemClassification VARCHAR2(255)
	,revision VARCHAR2(255)
	,version VARCHAR2(255)
	,revisionReleaseDate DATE
	,versionReleaseDate DATE
	,proprietaryProductFamily VARCHAR2(255)
	,commodityCode VARCHAR2(255)
	,managedBy VARCHAR2(255)
	,productUnitOfMeasureCode VARCHAR2(255)
	,makeBuy VARCHAR2(255)
	,makeBuyOther VARCHAR2(255)
	,minimumShippableRevision VARCHAR2(255)
	,isSerializationRequired VARCHAR2(5)
	,isCertificationRequired VARCHAR2(5)
	,ownerName VARCHAR2(255)
	,contactName VARCHAR2(255)
	,contactUniqueId VARCHAR2(255)
	,isTopLevel VARCHAR2(5)
	,businessEntity VARCHAR2(255)
	,businessEntityType VARCHAR2(255)
	,dataSource VARCHAR2(255)
	,effectiveFromDate DATE
	,effectiveToDate DATE
	,operationCode VARCHAR2(1)
	,lastUpdateDate DATE
	,inventory NUMBER(19,4)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPL_ITEM_I1 ON PCM_LOAD_Item(loadKey) TABLESPACE AUDIT_INDEX;
		
CREATE TABLE PCM_LOAD_ItemFlexAttribute
(
	loadKey VARCHAR2(36)
	,itemIdentifier VARCHAR2(255)
	,itemUniqueId VARCHAR2(255)
	,revision VARCHAR2(255)
	,version VARCHAR2(255)
	,businessEntity VARCHAR2(255)
	,businessEntityType VARCHAR2(255)
	,name VARCHAR2(255) 
	,associatedAttribute VARCHAR2(255) 
	,value VARCHAR2(255)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPL_ITEMFA_I1 ON PCM_LOAD_ItemFlexAttribute(loadKey) TABLESPACE AUDIT_INDEX;

CREATE TABLE PCM_LOAD_AdditionalAttribute 
(
	loadKey VARCHAR2(36)
	,itemIdentifier VARCHAR2(255)
	,itemUniqueId VARCHAR2(255)
	,revision VARCHAR2(255)
	,version VARCHAR2(255)
	,businessEntity VARCHAR2(255)
	,businessEntityType VARCHAR2(255)
	,groupLabel VARCHAR2(255)
	,name VARCHAR2(255)
	,value VARCHAR2(255)
	,type VARCHAR2(255)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPL_AA_I1 ON PCM_LOAD_AdditionalAttribute(loadKey) TABLESPACE AUDIT_INDEX;

CREATE TABLE PCM_LOAD_AVL
(	
	loadKey VARCHAR2(36)
	,itemIdentifier VARCHAR2(255)
	,itemUniqueId VARCHAR2(255)
	,revision VARCHAR2(255)
	,version VARCHAR2(255)
	,businessEntity VARCHAR2(255)
	,businessEntityType VARCHAR2(255)
	,site VARCHAR2(255)
	,description VARCHAR2(255)
	,manufacturedBy VARCHAR2(255)
	,vendorBusinessEntity VARCHAR2(255)
	,vendorBusinessEntityType VARCHAR2(255)
	,vendorItemIdentifier VARCHAR2(255)
	,vendorItemUniqueId VARCHAR2(255)
	,vendorRevision VARCHAR2(255)
	,vendorVersion VARCHAR2(255)
	,vendorContactName VARCHAR2(255)
	,vendorContactUniqueId VARCHAR2(255)
	,partStatusCode VARCHAR2(255)
	,partStatusCodeOther VARCHAR2(255)
	,preferredStatusCode VARCHAR2(255)
	,preferredStatusStartDate DATE
	,preferredStatusEndDate DATE
	,operationCode VARCHAR2(1)
	,dataSource VARCHAR2(255)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPL_AVL_I1 ON PCM_LOAD_AVL(loadKey) TABLESPACE AUDIT_INDEX;
		
CREATE TABLE PCM_LOAD_Bom
(
	loadKey VARCHAR2(36)
	,bomLoadKey VARCHAR2(36)
	,bomKey NUMBER(19)
	,itemIdentifier VARCHAR2(255)
	,itemUniqueId VARCHAR2(255)
	,revision VARCHAR2(255)
	,version VARCHAR2(255)
	,businessEntity VARCHAR2(255)
	,businessEntityType VARCHAR2(255)
	,contextBusinessEntity VARCHAR2(255)
	,contextBusinessEntityType VARCHAR2(255)
	,state VARCHAR2(255)
	,lastStateChangeBy VARCHAR2(255)
	,lastStateChangeOn DATE
	,lastUpdateDate DATE
	,bomName VARCHAR2(255)
	,bomRevision VARCHAR2(255)
	,bomVersion VARCHAR2(255)
	,bomRevisionReleaseDate DATE
	,bomVersionReleaseDate DATE
	,description VARCHAR2(255)
	,ownerName VARCHAR2(255)
	,ownerContactUniqueId VARCHAR2(255)
	,site VARCHAR2(255)
	,billOfMaterialCode VARCHAR2(255)
	,billOfMaterialCodeOther VARCHAR2(255)
	,isTopLevel VARCHAR2(5)
	,isRepairs VARCHAR2(5)
	,leadTime NUMBER(19,4)
	,effectiveFromDate DATE
	,effectiveToDate DATE
	,operationCode VARCHAR2(1)
	,dataSource VARCHAR2(255)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPL_BOM_I1 ON PCM_LOAD_Bom(loadKey) TABLESPACE AUDIT_INDEX;
CREATE INDEX XPL_BOM_I2 ON PCM_LOAD_Bom(bomLoadKey) TABLESPACE AUDIT_INDEX;
		
CREATE TABLE PCM_LOAD_BomLine
(
	loadKey VARCHAR2(36)
	,bomLoadKey VARCHAR2(36)
	,bomItemIdentifier VARCHAR2(255)
	,bomItemUniqueId VARCHAR2(255)
	,bomItemRevision VARCHAR2(255)
	,bomItemVersion VARCHAR2(255)
	,bomBusinessEntity VARCHAR2(255)
	,bomBusinessEntityType VARCHAR2(255)
	,contextBusinessEntity VARCHAR2(255)
	,contextBusinessEntityType VARCHAR2(255)
	,itemIdentifier VARCHAR2(255)
	,itemUniqueId VARCHAR2(255)
	,itemRevision VARCHAR2(255)
	,itemVersion VARCHAR2(255)
	,businessEntity VARCHAR2(255)
	,businessEntityType VARCHAR2(255)
	,managedBy VARCHAR2(255)
	,isSerializationiRequired VARCHAR2(5)
	,billOfMaterialCode VARCHAR2(255)
	,billOfMaterialCodeOther VARCHAR2(255)
	,notes VARCHAR2(255)
	,itemQuantity NUMBER(19,4)
	,leadTime NUMBER(19,4)
	,attritionRate NUMBER(19,4)
	,productQuantityTypeCode VARCHAR2(255)
	,productQuantityTypeCodeOther VARCHAR2(255)
	,description VARCHAR2(255)
	,proprietarySequenceIdentifier VARCHAR2(255)
	,effectiveFromDate DATE
	,effectiveToDate DATE
	,operationCode VARCHAR2(1)
	,dataSource VARCHAR2(255)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XPL_BOMLINE_I1 ON PCM_LOAD_BomLine(loadKey) TABLESPACE AUDIT_INDEX;
		
-- the ItemMessage load package
CREATE PACKAGE LoadItemMessage AS
  TYPE string_array IS TABLE of VARCHAR2(32767);
  
  PROCEDURE LoadAll(
  	p_load_key in pcm_load_item.loadKey%TYPE);
  	
  PROCEDURE LoadItem(
    p_load_key in pcm_load_item.loadKey%TYPE);
  
  PROCEDURE LoadCommodityCode (
    p_commodityCode in pcm_load_item.commodityCode%TYPE
    ,p_item_key in item_master.item_key%TYPE);
    
  PROCEDURE LoadAVL(
    p_load_key in pcm_load_item.loadKey%TYPE);
    
  PROCEDURE LoadAdditionalAttribute(
    p_load_key in pcm_load_item.loadKey%TYPE);
    
  PROCEDURE LoadFlexAttribute(
    p_load_key in pcm_load_item.loadKey%TYPE);
 
  PROCEDURE LoadBom(
    p_load_key in pcm_load_item.loadKey%TYPE);
  
  PROCEDURE UpdateSubBomKeys(
  	p_bom_key in bom_header.bom_key%TYPE
  	,p_item_key in item_master.item_key%TYPE
  	,p_context_object_id in bom_header.context_object_id%TYPE);
    
  PROCEDURE LoadBomLine (
    p_load_key in pcm_load_item.loadKey%TYPE);
    
  FUNCTION GetItem(
  	p_itemIdentifier in pcm_load_item.itemIdentifier%TYPE
  	,p_itemUniqueId pcm_load_item.itemUniqueId%TYPE
  	,p_revision pcm_load_item.revision%TYPE
  	,p_version pcm_load_item.version%TYPE
  	,p_business_entity_key business_entity.business_entity_key%TYPE)
  	RETURN NUMBER;
  	
  FUNCTION GetBusiness(
  	p_businessEntity pcm_load_item.businessEntity%TYPE
  	,p_business_entity_type_key business_entity_type.business_entity_type_key%TYPE)
  	RETURN NUMBER;
  
  FUNCTION GetBusinessType(
  	p_businessEntityType pcm_load_item.businessEntityType%TYPE)
  	RETURN NUMBER;
  	
  FUNCTION GetItemType(
  	p_businessEntityType pcm_load_item.businessEntityType%TYPE)
  	RETURN VARCHAR;
  	
  FUNCTION GetMostRecentBom(
  	p_item_key item_master.item_key%TYPE
  	,p_revision bom_header.revision%TYPE
  	,p_status bom_header.status%TYPE
  	,p_context_object_id in bom_header.context_object_id%TYPE)
  	RETURN NUMBER;
  	
  FUNCTION GetSite(
  	p_site pcm_load_avl.site%TYPE
  	,p_business_entity_key business_entity.business_entity_key%TYPE)
  	RETURN NUMBER;
  	
   FUNCTION split_string(
    p_str VARCHAR2,
    p_delimiter CHAR DEFAULT ',')
    RETURN string_array;
    
  FUNCTION GetFlexAttribute (
  	p_associatedAttribute pcm_load_itemflexattribute.associatedAttribute%TYPE)
  	RETURN VARCHAR;
END LoadItemMessage;
/

-- the collab extract package body
CREATE PACKAGE BODY LoadItemMessage AS
  PROCEDURE LoadAll (
    p_load_key in pcm_load_item.loadKey%TYPE)
    AS
  BEGIN
    LoadItem(p_load_key);
    LoadAVL(p_load_key);
    LoadAdditionalAttribute(p_load_key);
    LoadFlexAttribute(p_load_key);
    LoadBom(p_load_key);
    LoadBomLine(p_load_key);
    
    DELETE FROM PCM_LOAD_Item
	WHERE loadKey = p_load_key;
	
	DELETE FROM PCM_LOAD_AVL
	WHERE loadKey = p_load_key;
	
	DELETE FROM PCM_LOAD_AdditionalAttribute
	WHERE loadKey = p_load_key;
	
	DELETE FROM PCM_LOAD_ItemFlexAttribute
    WHERE loadKey = p_load_key;
		
	DELETE FROM PCM_LOAD_Bom
	WHERE loadKey = p_load_key;
	
	DELETE FROM PCM_LOAD_BomLine
	WHERE loadKey = p_load_key;
		
    COMMIT;
    
    EXCEPTION
      WHEN OTHERS THEN
        ROLLBACK;
		
		DELETE FROM PCM_LOAD_Item
		WHERE loadKey = p_load_key;
		
		DELETE FROM PCM_LOAD_AVL
	    WHERE loadKey = p_load_key;
	    
	    DELETE FROM PCM_LOAD_AdditionalAttribute
	    WHERE loadKey = p_load_key;
	    
	    DELETE FROM PCM_LOAD_ItemFlexAttribute
	    WHERE loadKey = p_load_key;
		
		DELETE FROM PCM_LOAD_Bom
		WHERE loadKey = p_load_key;
		
		DELETE FROM PCM_LOAD_BomLine
		WHERE loadKey = p_load_key;
	    
		COMMIT;
		RAISE_APPLICATION_ERROR(-20001,SQLERRM(SQLCODE));
  END LoadAll;
  
  PROCEDURE LoadItem (
    p_load_key in pcm_load_item.loadKey%TYPE)
    AS
    
    v_item_rec pcm_load_item%ROWTYPE;
    v_business_entity_type_key business_entity_type.business_entity_type_key%TYPE;
    v_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_key item_master.item_key%TYPE;
    v_item_type item_master.item_type%TYPE;
    
    v_execute_update NUMBER := 0;
    
    CURSOR c_GetLoadItems IS
    SELECT * FROM pcm_load_item
    WHERE loadKey = p_load_key;
  BEGIN
	-- get the set of items to load
	OPEN c_GetLoadItems;
	LOOP
	  FETCH c_GetLoadItems INTO v_item_rec;
	  EXIT WHEN c_GetLoadItems%NOTFOUND;
	  
	  v_business_entity_type_key := GetBusinessType(v_item_rec.businessEntityType);
	  
	  v_business_entity_key := GetBusiness(v_item_rec.businessEntity,v_business_entity_type_key);
	  IF v_business_entity_key IS NULL THEN
	    RAISE_APPLICATION_ERROR(-20002,'BusinessNotFoundOrRestricted:'||v_item_rec.businessEntity||'|'||v_item_rec.businessEntityType);
	  END IF;
	  
	  v_item_key := GetItem(v_item_rec.itemIdentifier,v_item_rec.itemUniqueId,v_item_rec.revision,v_item_rec.version,v_business_entity_key);
	  
	  IF v_item_key IS NULL THEN
	    -- create the item
	    SELECT item_master_seq.nextval INTO v_item_key FROM dual;
	    v_item_type := v_item_rec.itemType;
	    IF v_item_type IS NULL THEN
	      v_item_type := GetItemType(v_item_rec.businessEntityType);
	    END IF;
	    INSERT INTO ITEM_MASTER
	    (ITEM_KEY
	    ,ITEM_IDENTIFIER
	    ,ITEM_UNIQUE_IDENTIFIER
	    ,ITEM_DESCRIPTION
	    ,VERSION
	    ,REVISION
	    ,VERSION_DATE
	    ,REVISION_DATE
	    ,GBL_LFCYCL_PH_CODE
	    ,GBL_PROD_LFCYCL_CODE_OTHER
	    ,ITEM_CLASSIFICATION
	    ,GBL_UOM_CODE
	    ,PRODUCT_FAMILY
	    ,OWNER_NAME
	    ,ITEM_TYPE
	    ,IS_TOP_LEVEL
	    ,BUSINESS_ENTITY_KEY
	    ,MAKE_BUY
	    ,MAKE_BUY_OTHER
	    ,MANAGED_FLAG
	    ,CERTIFICATION_REQUIRED
	    ,SERIALNUMBER_REQUIRED
	    ,DATA_SOURCE
	    ,EFFECTIVE_FROM_DT
	    ,EFFECTIVE_TO_DT
	    ,DELETE_FLAG
	    ,CURRENT_FLAG
	    ,ITEM_PART_TYPE
	    ,INVENTORY)
	    values
	    (v_item_key
	    ,v_item_rec.itemIdentifier
        ,v_item_rec.itemUniqueId
        ,v_item_rec.description
        ,v_item_rec.version
        ,v_item_rec.revision
        ,v_item_rec.versionReleaseDate
        ,v_item_rec.revisionReleaseDate
        ,v_item_rec.lifeCycleCode
        ,v_item_rec.lifeCycleCodeOther
        ,v_item_rec.itemClassification
        ,v_item_rec.productUnitOfMeasureCode
        ,v_item_rec.proprietaryProductFamily
        ,v_item_rec.ownerName
        ,v_item_type
        ,decode(v_item_rec.isTopLevel, 'true', 1, 'false', 0, 0)
        ,v_business_entity_key
        ,v_item_rec.makeBuy
        ,v_item_rec.makeBuyOther
        ,v_item_rec.managedBy
        ,decode(v_item_rec.isCertificationRequired, 'true', 1, 'false', 0, 0)
        ,decode(v_item_rec.isSerializationRequired, 'true', 1, 'false', 0, 0)
        ,v_item_rec.dataSource
        ,v_item_rec.effectiveFromDate
        ,v_item_rec.effectiveToDate
        ,'N'
        ,'Y'
        ,v_item_rec.itemPartType
        ,v_item_rec.inventory);
      ELSE
        -- update the item
        IF v_item_rec.description IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET ITEM_DESCRIPTION = v_item_rec.description
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.lifeCycleCode IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET GBL_LFCYCL_PH_CODE = v_item_rec.lifeCycleCode
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.lifeCycleCodeOther IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET GBL_PROD_LFCYCL_CODE_OTHER = v_item_rec.lifeCycleCodeOther
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.itemClassification IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET ITEM_CLASSIFICATION = v_item_rec.itemClassification
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.productUnitOfMeasureCode IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET GBL_UOM_CODE = v_item_rec.productUnitOfMeasureCode
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.proprietaryProductFamily IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET PRODUCT_FAMILY = v_item_rec.proprietaryProductFamily
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.ownerName IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET OWNER_NAME = v_item_rec.ownerName
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.isTopLevel IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET IS_TOP_LEVEL = decode(v_item_rec.isTopLevel, 'true', 1, 'false', 0, 0)
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.makeBuy IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET MAKE_BUY = v_item_rec.makeBuy
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.makeBuyOther IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET MAKE_BUY_OTHER = v_item_rec.makeBuyOther
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.managedBy IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET MANAGED_FLAG = v_item_rec.managedBy
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.isCertificationRequired IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET CERTIFICATION_REQUIRED = decode(v_item_rec.isCertificationRequired, 'true', 1, 'false', 0, 0)
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.isSerializationRequired IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET SERIALNUMBER_REQUIRED = decode(v_item_rec.isSerializationRequired, 'true', 1, 'false', 0, 0)
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.effectiveFromDate IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET EFFECTIVE_FROM_DT = v_item_rec.effectiveFromDate
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.effectiveToDate IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET EFFECTIVE_TO_DT = v_item_rec.effectiveToDate
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.itemPartType IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET ITEM_PART_TYPE = v_item_rec.itemPartType
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_item_rec.inventory IS NOT NULL THEN
          UPDATE ITEM_MASTER
          SET INVENTORY = v_item_rec.inventory
          WHERE ITEM_KEY = v_item_key;
          v_execute_update := 1;
        END IF;
        IF v_execute_update = 1 THEN
          UPDATE ITEM_MASTER
          SET UPDATE_DT = sysdate
          WHERE ITEM_KEY = v_item_key;
        END IF;
	  END IF;
	  
	  IF v_item_rec.commodityCode IS NOT NULL THEN
	    LoadCommodityCode(v_item_rec.commodityCode,v_item_key);
	  END IF;
	END LOOP;
	CLOSE c_GetLoadItems;
	
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20002,SQLERRM(SQLCODE));
  END LoadItem;
  
  PROCEDURE LoadCommodityCode (
    p_commodityCode in pcm_load_item.commodityCode%TYPE
    ,p_item_key in item_master.item_key%TYPE)
    AS
    
    v_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_category_key item_category.item_category_key%TYPE;
    
  BEGIN
	BEGIN
	  SELECT business_entity_key
	  INTO v_business_entity_key
	  FROM business_entity
	  WHERE business_entity_type_key = 1;
	  
	  EXCEPTION WHEN NO_DATA_FOUND THEN
	    RAISE_APPLICATION_ERROR(-20003,'Cannot find ENTERPRISE business');
	END;
	
	BEGIN
	  SELECT item_category_key
	  INTO v_item_category_key
	  FROM item_category
	  WHERE item_category_identifier = p_commodityCode
	  AND business_entity_key = v_business_entity_key;
	  
	  EXCEPTION WHEN NO_DATA_FOUND THEN null;
	END;
	
	IF v_item_category_key IS NULL THEN
	  BEGIN
		SELECT item_category_key
	    INTO v_item_category_key
	    FROM item_category
	    WHERE item_category_name = p_commodityCode
	    AND business_entity_key = v_business_entity_key;
	  
	    EXCEPTION WHEN NO_DATA_FOUND THEN
	      RAISE_APPLICATION_ERROR(-20004,'Cannot find commodity code '||p_commodityCode);
	  END;
	END IF;
	
	-- currently we only support 1 commidty code per item
	DELETE FROM ITEM_ITEM_CATEGORY
	WHERE item_key = p_item_key;
	  
	INSERT INTO ITEM_ITEM_CATEGORY
	(ITEM_KEY
	,ITEM_CATEGORY_KEY)
	values
	(p_item_key
	,v_item_category_key);
  END LoadCommodityCode;
  
  PROCEDURE LoadAVL (
    p_load_key in pcm_load_item.loadKey%TYPE)
    AS
    
    v_avl_rec pcm_load_avl%ROWTYPE;
    v_business_entity_type_key business_entity_type.business_entity_type_key%TYPE;
    v_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_key item_master.item_key%TYPE;
    
    v_vendor_bus_entity_type_key business_entity_type.business_entity_type_key%TYPE;
    v_vendor_business_entity_key business_entity.business_entity_key%TYPE;
    v_vendor_item_key item_master.item_key%TYPE;
    v_vendor_item_type item_master.item_type%TYPE;
    
    v_avl_key item_avl.avl_key%TYPE;
    
    v_sites string_array := string_array();
    v_index NUMBER := 0;
    v_site_value site.site_description%TYPE;
    v_site_key site.site_key%TYPE;
    v_avl_site_cnt NUMBER := 0;
    
    CURSOR c_GetLoadAVL IS
    SELECT * FROM pcm_load_avl
    WHERE loadKey = p_load_key;
  BEGIN
	-- get the set of avl to load
	OPEN c_GetLoadAVL;
	LOOP
	  FETCH c_GetLoadAVL INTO v_avl_rec;
	  EXIT WHEN c_GetLoadAVL%NOTFOUND;
	  
	  -- get the item
	  v_business_entity_type_key := GetBusinessType(v_avl_rec.businessEntityType);
	  
	  v_business_entity_key := GetBusiness(v_avl_rec.businessEntity,v_business_entity_type_key);
	  
	  IF v_business_entity_key IS NULL THEN
	    RAISE_APPLICATION_ERROR(-20005,'Cannot find business '||v_avl_rec.businessEntity||' with type '||v_avl_rec.businessEntityType);
	  END IF;
	  
	  v_item_key := GetItem(v_avl_rec.itemIdentifier,v_avl_rec.itemUniqueId,v_avl_rec.revision,v_avl_rec.version,v_business_entity_key);
	  
	  IF v_item_key IS NULL THEN
	    RAISE_APPLICATION_ERROR(-20006,'Cannot find item '||v_avl_rec.itemIdentifier||' for business '||v_avl_rec.businessEntity||' with type '||v_avl_rec.businessEntityType);
	  END IF;
	  
	  -- get the vendor item
	  v_vendor_bus_entity_type_key := GetBusinessType(v_avl_rec.vendorBusinessEntityType);
	  
	  v_vendor_business_entity_key := GetBusiness(v_avl_rec.vendorBusinessEntity,v_vendor_bus_entity_type_key);
	  
	  IF v_vendor_business_entity_key IS NULL THEN
	    RAISE_APPLICATION_ERROR(-20007,'Cannot find vendor business '||v_avl_rec.vendorBusinessEntity||' with type '||v_avl_rec.vendorBusinessEntityType);
	  END IF;
	  
	  v_vendor_item_key := GetItem(v_avl_rec.vendorItemIdentifier,v_avl_rec.vendorItemUniqueId,v_avl_rec.vendorRevision,v_avl_rec.vendorVersion,v_vendor_business_entity_key);
	  
	  IF v_vendor_item_key IS NULL THEN
	    v_vendor_item_type := GetItemType(v_avl_rec.vendorBusinessEntityType);
	    SELECT item_master_seq.nextval INTO v_vendor_item_key FROM dual;
	    INSERT INTO item_master
	    (item_key
	    ,item_identifier
	    ,item_unique_identifier
	    ,item_description
	    ,version
	    ,revision
	    ,business_entity_key
	    ,item_type
	    ,current_flag
	    ,delete_flag
	    ,data_source)
	    VALUES
	    (v_vendor_item_key
	    ,v_avl_rec.vendorItemIdentifier
	    ,v_avl_rec.vendorItemUniqueId
	    ,v_avl_rec.description
	    ,v_avl_rec.vendorVersion
        ,v_avl_rec.vendorRevision
	    ,v_vendor_business_entity_key
	    ,v_vendor_item_type
	    ,'Y'
	    ,'N'
	    ,v_avl_rec.dataSource);
	  END IF;
	  
	  v_avl_key := null;
	  BEGIN
	    SELECT avl_key
	    INTO v_avl_key
	    FROM item_avl
	    WHERE item_key = v_item_key
	    AND supplier_part_key = v_vendor_item_key;
	    
	    EXCEPTION WHEN NO_DATA_FOUND THEN null;
	  END;
	  
	  IF v_avl_key IS NULL THEN
	    SELECT item_avl_seq.nextval INTO v_avl_key FROM dual;
	    INSERT INTO ITEM_AVL
	    (ITEM_KEY
	    ,BUSINESS_ENTITY_KEY
	    ,SUPPLIER_PART_KEY
	    ,DESCRIPTION
	    ,PREFERRED_STATUS_CODE
	    ,PREFERRED_STATUS_START
	    ,PREFERRED_STATUS_END
	    ,DELETE_FLAG
	    ,CURRENT_FLAG
	    ,AVL_KEY)
	    values
	    (v_item_key
	    ,v_vendor_business_entity_key
	    ,v_vendor_item_key
	    ,v_avl_rec.description
	    ,v_avl_rec.preferredStatusCode
	    ,v_avl_rec.preferredStatusStartDate
	    ,v_avl_rec.preferredStatusEndDate
	    ,'N'
	    ,'Y'
	    ,v_avl_key);
	  END IF;
	  
	  IF v_avl_rec.site IS NOT NULL THEN
	    v_sites := split_string(v_avl_rec.site);
	    v_index := 1;
	    IF v_sites.count > 0 THEN
          LOOP
            v_site_value := v_sites(v_index);
            v_site_key := GetSite(v_site_value,v_vendor_business_entity_key);
            
            IF v_site_key IS NULL THEN
	          RAISE_APPLICATION_ERROR(-20007,'Cannot find site '||v_site_value||' for business '||v_avl_rec.vendorBusinessEntity||' with type '||v_avl_rec.vendorBusinessEntityType);
	        END IF;
	        
	        SELECT count(*)
	        INTO v_avl_site_cnt
	        FROM pcm_item_avl_site
	        WHERE avl_key = v_avl_key
	        AND site_key = v_site_key;
	        
	        IF v_avl_site_cnt = 0 THEN
	          INSERT INTO pcm_item_avl_site
	          (avl_key
	          ,site_key)
	          values
	          (v_avl_key
	          ,v_site_key);
	        END IF;
	        
	        v_index := v_index + 1;
	        EXIT WHEN v_index > v_sites.count;
          END LOOP;
        END IF;
	  END IF;
	  
	END LOOP;
	CLOSE c_GetLoadAVL;
	
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20008,SQLERRM(SQLCODE));
  END LoadAVL;
  
  PROCEDURE LoadAdditionalAttribute (
    p_load_key in pcm_load_item.loadKey%TYPE)
    AS
    
    v_aa_rec pcm_load_additionalattribute%ROWTYPE;
    v_business_entity_type_key business_entity_type.business_entity_type_key%TYPE;
    v_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_key item_master.item_key%TYPE;
    
    v_attribute_group_key attribute_group.attribute_group_key%TYPE;
    v_aa_count NUMBER := 0;
    
    CURSOR c_GetLoadAA IS
    SELECT * FROM pcm_load_additionalattribute
    WHERE loadKey = p_load_key;
  BEGIN
	-- get the set of additional attributes to load
	OPEN c_GetLoadAA;
	LOOP
	  FETCH c_GetLoadAA INTO v_aa_rec;
	  EXIT WHEN c_GetLoadAA%NOTFOUND;
	  
	  -- get the item
	  v_business_entity_type_key := GetBusinessType(v_aa_rec.businessEntityType);
	  
	  v_business_entity_key := GetBusiness(v_aa_rec.businessEntity,v_business_entity_type_key);
	  
	  IF v_business_entity_key IS NULL THEN
	    RAISE_APPLICATION_ERROR(-20009,'Cannot find business '||v_aa_rec.businessEntity||' with type '||v_aa_rec.businessEntityType);
	  END IF;
	  
	  v_item_key := GetItem(v_aa_rec.itemIdentifier,v_aa_rec.itemUniqueId,v_aa_rec.revision,v_aa_rec.version,v_business_entity_key);
	  
	  IF v_item_key IS NULL THEN
	    RAISE_APPLICATION_ERROR(-20010,'Cannot find item '||v_aa_rec.itemIdentifier||' for business '||v_aa_rec.businessEntity||' with type '||v_aa_rec.businessEntityType);
	  END IF;
	  
	  v_attribute_group_key := null;
	  BEGIN
		SELECT attribute_group_key
		INTO v_attribute_group_key
		FROM attribute_group
		WHERE attribute_group_name = v_aa_rec.groupLabel
		AND object_type = 'ITEM';
		
		EXCEPTION WHEN NO_DATA_FOUND THEN null;
	  END;
	  
	  IF v_attribute_group_key IS NULL THEN
	    SELECT attribute_group_seq.nextval
	    INTO v_attribute_group_key
	    FROM dual;
	    
	    INSERT INTO ATTRIBUTE_GROUP
	    (ATTRIBUTE_GROUP_KEY
	    ,ATTRIBUTE_GROUP_NAME
	    ,OBJECT_TYPE)
	    values
	    (v_attribute_group_key
	    ,v_aa_rec.groupLabel
	    ,'ITEM');
	  END IF;
	  
	  SELECT count(*)
	  INTO v_aa_count
	  FROM ITEM_ADD_ATTRIBUTE
	  WHERE ITEM_KEY = v_item_key
      AND ATTRIBUTE_NAME = v_aa_rec.name
      AND ATTRIBUTE_TYPE = v_aa_rec.type
      AND ATTRIBUTE_GROUP_KEY = v_attribute_group_key;
      
      IF v_aa_count = 0 THEN
        IF v_aa_rec.type = 'STRING' OR v_aa_rec.type = 'BOOLEAN' THEN
          INSERT INTO ITEM_ADD_ATTRIBUTE
          (ATTRIBUTE_KEY
	      ,ITEM_KEY
	      ,ATTRIBUTE_NAME
	      ,ATTRIBUTE_TYPE
	      ,ATTRIBUTE_VALUE
	      ,ATTRIBUTE_GROUP_KEY)
	      values
	      (attribute_seq.nextval
	      ,v_item_key
	      ,v_aa_rec.name
	      ,v_aa_rec.type
	      ,v_aa_rec.value
	      ,v_attribute_group_key);
	    ELSIF v_aa_rec.type = 'INTEGER' OR v_aa_rec.type = 'FLOAT' THEN
          INSERT INTO ITEM_ADD_ATTRIBUTE
          (ATTRIBUTE_KEY
	      ,ITEM_KEY
	      ,ATTRIBUTE_NAME
	      ,ATTRIBUTE_TYPE
	      ,ATTRIBUTE_VALUE_NUM
	      ,ATTRIBUTE_GROUP_KEY)
	      values
	      (attribute_seq.nextval
	      ,v_item_key
	      ,v_aa_rec.name
	      ,v_aa_rec.type
	      ,v_aa_rec.value
	      ,v_attribute_group_key);
	    ELSIF v_aa_rec.type = 'DATE' THEN
          INSERT INTO ITEM_ADD_ATTRIBUTE
          (ATTRIBUTE_KEY
	      ,ITEM_KEY
	      ,ATTRIBUTE_NAME
	      ,ATTRIBUTE_TYPE
	      ,ATTRIBUTE_VALUE_DT
	      ,ATTRIBUTE_GROUP_KEY)
	      values
	      (attribute_seq.nextval
	      ,v_item_key
	      ,v_aa_rec.name
	      ,v_aa_rec.type
	      ,v_aa_rec.value
	      ,v_attribute_group_key);
	    END IF;
	  END IF;
	END LOOP;
	CLOSE c_GetLoadAA;
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20011,SQLERRM(SQLCODE));
  END LoadAdditionalAttribute;
  
  PROCEDURE LoadFlexAttribute (
    p_load_key in pcm_load_item.loadKey%TYPE)
    AS
    
    v_fa_rec pcm_load_itemflexattribute%ROWTYPE;
    v_business_entity_type_key business_entity_type.business_entity_type_key%TYPE;
    v_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_key item_master.item_key%TYPE;
    
    v_attribute VARCHAR2(255);
    v_update VARCHAR2(2048);
    
    CURSOR c_GetLoadFA IS
    SELECT * FROM pcm_load_itemflexattribute
    WHERE loadKey = p_load_key;
  BEGIN
	-- get the set of additional attributes to load
	OPEN c_GetLoadFA;
	LOOP
	  FETCH c_GetLoadFA INTO v_fa_rec;
	  EXIT WHEN c_GetLoadFA%NOTFOUND;
	  
	  -- get the item
	  v_business_entity_type_key := GetBusinessType(v_fa_rec.businessEntityType);
	  
	  v_business_entity_key := GetBusiness(v_fa_rec.businessEntity,v_business_entity_type_key);
	  
	  IF v_business_entity_key IS NULL THEN
	    RAISE_APPLICATION_ERROR(-20012,'Cannot find business '||v_fa_rec.businessEntity||' with type '||v_fa_rec.businessEntityType);
	  END IF;
	  
	  v_item_key := GetItem(v_fa_rec.itemIdentifier,v_fa_rec.itemUniqueId,v_fa_rec.revision,v_fa_rec.version,v_business_entity_key);
	  
	  IF v_item_key IS NULL THEN
	    RAISE_APPLICATION_ERROR(-20013,'Cannot find item '||v_fa_rec.itemIdentifier||' for business '||v_fa_rec.businessEntity||' with type '||v_fa_rec.businessEntityType);
	  END IF;
	  
	  v_attribute := GetFlexAttribute(v_fa_rec.associatedAttribute);
	  IF v_attribute IS NOT NULL THEN
	    v_update := 'UPDATE ITEM_MASTER SET '||v_attribute||' = :value, UPDATE_DT = sysdate WHERE item_key = :itemKey';
	    EXECUTE IMMEDIATE v_update USING v_fa_rec.value, v_item_key;
	  END IF;
	END LOOP;
	CLOSE c_GetLoadFA;
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20014,SQLERRM(SQLCODE));
  END LoadFlexAttribute;
  
  FUNCTION GetFlexAttribute (
  	p_associatedAttribute pcm_load_itemflexattribute.associatedAttribute%TYPE)
  	RETURN VARCHAR IS
    
    v_attribute VARCHAR2(255);
  BEGIN
    IF UPPER(SUBSTR(p_associatedAttribute,1,6)) = 'STRING' THEN
      v_attribute := 'STRING_ATTRIBUTE'||SUBSTR(p_associatedAttribute,16);
    ELSIF UPPER(SUBSTR(p_associatedAttribute,1,6)) = 'NUMBER' THEN
      v_attribute := 'NUMBER_ATTRIBUTE'||SUBSTR(p_associatedAttribute,16);
    ELSIF UPPER(SUBSTR(p_associatedAttribute,1,5)) = 'FLOAT' THEN
      v_attribute := 'FLOAT_ATTRIBUTE'||SUBSTR(p_associatedAttribute,15);
    ELSIF UPPER(SUBSTR(p_associatedAttribute,1,4)) = 'DATE' THEN
      v_attribute := 'DATE_ATTRIBUTE'||SUBSTR(p_associatedAttribute,14);
    END IF;
    return v_attribute;
  END GetFlexAttribute;
  
  PROCEDURE LoadBom (
    p_load_key in pcm_load_item.loadKey%TYPE)
    AS
    
    v_bom_rec pcm_load_bom%ROWTYPE;
    v_business_entity_type_key business_entity_type.business_entity_type_key%TYPE;
    v_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_key item_master.item_key%TYPE;
    v_bom_key bom_header.bom_key%TYPE;
    v_bom_version bom_header.version%TYPE := '00000001';
    v_bom_name bom_header.bom_name%TYPE;
    v_status bom_header.status%TYPE := 'PENDING';
    v_context_be_type_key business_entity_type.business_entity_type_key%TYPE;
    v_context_business_entity_key business_entity.business_entity_key%TYPE;
    v_context_object_type bom_header.context_object_type%TYPE;
    
    CURSOR c_GetLoadBoms IS
    SELECT * FROM pcm_load_bom
    WHERE loadKey = p_load_key;
  BEGIN
	-- get the set of boms to load
	OPEN c_GetLoadBoms;
	LOOP
	  FETCH c_GetLoadBoms INTO v_bom_rec;
	  EXIT WHEN c_GetLoadBoms%NOTFOUND;
	  
	  v_business_entity_type_key := GetBusinessType(v_bom_rec.businessEntityType);
	  
      v_business_entity_key := GetBusiness(v_bom_rec.businessEntity,v_business_entity_type_key);
      
      v_context_object_type := null;
      v_context_be_type_key := null;
      v_context_business_entity_key := null;
      IF v_bom_rec.contextBusinessEntity IS NOT NULL THEN
        v_context_object_type := 'BUSINESS';
        v_context_be_type_key := GetBusinessType(v_bom_rec.contextBusinessEntityType);
        v_context_business_entity_key := GetBusiness(v_bom_rec.contextBusinessEntity,v_context_be_type_key);
      END IF;
	  
      v_item_key := GetItem(v_bom_rec.itemIdentifier,v_bom_rec.itemUniqueId,v_bom_rec.revision,v_bom_rec.version,v_business_entity_key);
	  
      v_bom_key := GetMostRecentBom(v_item_key,v_bom_rec.bomRevision,v_status,v_context_business_entity_key);
      
      v_bom_name := v_bom_rec.bomName;
      IF v_bom_rec.bomName IS NULL THEN
        v_bom_name := v_bom_rec.itemIdentifier;
      END IF;
	  
	  IF v_bom_key IS NULL THEN
	    SELECT bom_header_seq.nextval INTO v_bom_key FROM dual;
	    INSERT INTO BOM_HEADER
	    (BOM_KEY
	    ,BOM_EXTERNAL_ID
	    ,BOM_NAME
	    ,BOM_DESCRIPTION
	    ,OWNER
	    ,ITEM_KEY
	    ,VERSION
	    ,REVISION
	    ,VERSION_DATE
	    ,REVISION_DATE
	    ,BUSINESS_ENTITY_KEY
	    ,BOM_TYPE_CODE
	    ,BOM_TYPE_CODE_OTHER
	    ,IS_TOP_LEVEL
	    ,DATA_SOURCE
        ,STATUS
	    ,LEAD_TIME
	    ,EFFECTIVE_FROM_DT
	    ,EFFECTIVE_TO_DT
	    ,DELETE_FLAG
	    ,CURRENT_FLAG
	    ,IS_REPAIRS
	    ,LOAD_SOURCE
	    ,CONTEXT_OBJECT_TYPE
	    ,CONTEXT_OBJECT_ID)
	    values
	    (v_bom_key
	    ,sys_guid()
	    ,v_bom_name
	    ,v_bom_rec.description
	    ,v_bom_rec.ownerName
	    ,v_item_key
	    ,v_bom_version
	    ,v_bom_rec.bomRevision
	    ,v_bom_rec.bomVersionReleaseDate
	    ,v_bom_rec.bomRevisionReleaseDate
	    ,v_business_entity_key
	    ,v_bom_rec.billOfMaterialCode
	    ,v_bom_rec.billOfMaterialCodeOther
	    ,decode(v_bom_rec.isTopLevel, 'true', 1, 'false', 0, 0)
	    ,v_bom_rec.dataSource
	    ,v_status
	    ,v_bom_rec.leadTime
	    ,v_bom_rec.effectiveFromDate
	    ,v_bom_rec.effectiveToDate
	    ,'N'
	    ,'Y'
	    ,decode(v_bom_rec.isRepairs, 'true', 1, 'false', 0, 0)
	    ,'B2B'
	    ,v_context_object_type
	    ,v_context_business_entity_key);
	    
	    INSERT INTO bom_version
	    (item_key,business_entity_key,current_version,context_object_type,context_object_id)
	    values
	    (v_item_key,v_business_entity_key,v_bom_version,v_context_object_type,v_context_business_entity_key);
	    
	    UpdateSubBomKeys(v_bom_key,v_item_key,v_context_business_entity_key);
	  END IF;
	  
	  UPDATE pcm_load_bom
	  SET bomKey = v_bom_key
	  WHERE bomLoadKey = v_bom_rec.bomLoadKey;
	END LOOP;
	CLOSE c_GetLoadBoms;
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20016,SQLERRM(SQLCODE));
  END LoadBom;
  
  PROCEDURE UpdateSubBomKeys(
  	p_bom_key in bom_header.bom_key%TYPE
  	,p_item_key in item_master.item_key%TYPE
  	,p_context_object_id in bom_header.context_object_id%TYPE)
  	AS
  
  	v_bom_line_item_key bom_line_item.bom_line_item_key%TYPE;
  	
    CURSOR c_BomLines IS
    SELECT bom_line_item_key
    FROM bom_line_item bli
    JOIN bom_header b ON
    b.bom_key = bli.bom_key AND
    b.status IN ('APPROVED','PENDING') AND
    (
      (p_context_object_id IS NULL AND b.context_object_id IS NULL) OR
      (p_context_object_id IS NOT NULL AND b.context_object_id = p_context_object_id)
    )
    WHERE bli.item_key = p_item_key;
  BEGIN
	OPEN c_BomLines;
	LOOP
	  FETCH c_BomLines INTO v_bom_line_item_key;
	  EXIT WHEN c_BomLines%NOTFOUND;
	  
	  UPDATE BOM_LINE_ITEM
	  SET SUB_BOM_KEY = p_bom_key
	  WHERE BOM_LINE_ITEM_KEY = v_bom_line_item_key;
	END LOOP;
	CLOSE c_BomLines;
  END UpdateSubBomKeys;
  
  PROCEDURE LoadBomLine (
    p_load_key in pcm_load_item.loadKey%TYPE)
    AS
    
    v_bomline_rec pcm_load_bomline%ROWTYPE;
    v_bom_key bom_header.bom_key%TYPE;
    v_sub_bom_key bom_header.bom_key%TYPE;
    v_bom_business_entity_type_key business_entity_type.business_entity_type_key%TYPE;
    v_bom_business_entity_key business_entity.business_entity_key%TYPE;
    v_bom_item_key item_master.item_key%TYPE;
    v_status bom_header.status%TYPE := 'APPROVED';
    v_context_be_type_key business_entity_type.business_entity_type_key%TYPE;
    v_context_business_entity_key business_entity.business_entity_key%TYPE;
    v_business_entity_type_key business_entity_type.business_entity_type_key%TYPE;
    v_business_entity_key business_entity.business_entity_key%TYPE;
    v_item_key item_master.item_key%TYPE;
    v_item_type item_master.item_type%TYPE;
    v_bomline_cnt NUMBER := 0;
    
    CURSOR c_GetLoadBomLines IS
    SELECT * FROM pcm_load_bomline
    WHERE loadKey = p_load_key;
  BEGIN
	-- -- get the set of bom lines to load
	OPEN c_GetLoadBomLines;
	LOOP
	  FETCH c_GetLoadBomLines INTO v_bomline_rec;
	  EXIT WHEN c_GetLoadBomLines%NOTFOUND;
	  
	  -- get the parent bom info
	  v_bom_business_entity_type_key := GetBusinessType(v_bomline_rec.bomBusinessEntityType);
	  
	  v_bom_business_entity_key := GetBusiness(v_bomline_rec.bomBusinessEntity,v_bom_business_entity_type_key);
	  
	  v_bom_item_key := GetItem(v_bomline_rec.bomItemIdentifier,v_bomline_rec.bomItemUniqueId,v_bomline_rec.bomItemRevision,v_bomline_rec.bomItemVersion,v_bom_business_entity_key);
	  
	  v_context_be_type_key := null;
	  v_context_business_entity_key := null;
	  IF v_bomline_rec.contextBusinessEntity IS NOT NULL THEN
        v_context_be_type_key := GetBusinessType(v_bomline_rec.contextBusinessEntityType);
        v_context_business_entity_key := GetBusiness(v_bomline_rec.contextBusinessEntity,v_context_be_type_key);
      END IF;
      
	  --SELECT bom_key
	  --INTO v_bom_key
	  --FROM bom_header
	  --WHERE item_key = v_bom_item_key
	  --AND status = v_status
	  --AND
      --(
        --(v_context_business_entity_key IS NULL AND context_object_id IS NULL) OR
        --(v_context_business_entity_key IS NOT NULL AND context_object_id = v_context_business_entity_key)
      --);
      
      BEGIN
        SELECT bomKey
        INTO v_bom_key
        FROM pcm_load_bom
        WHERE bomLoadKey = v_bomline_rec.bomLoadKey;
        
        EXCEPTION WHEN OTHERS THEN
		  RAISE_APPLICATION_ERROR(-20018, 'Mulitple boms found for bom load key '||v_bomline_rec.bomLoadKey);
      END;
	  
	  -- get the line info
	  v_business_entity_type_key := GetBusinessType(v_bomline_rec.businessEntityType);
	  
      v_business_entity_key := GetBusiness(v_bomline_rec.businessEntity,v_business_entity_type_key);
	  
      IF v_business_entity_key IS NULL THEN
	    SELECT business_entity_seq.nextval INTO v_business_entity_key FROM dual;
	    INSERT INTO business_entity
	    (business_entity_key,business_entity_identifier,business_entity_name,business_entity_type_key,data_source)
	    VALUES
	    (v_business_entity_key,v_bomline_rec.businessEntity,v_bomline_rec.businessEntity,v_business_entity_type_key,v_bomline_rec.dataSource);
	  END IF;
	  
	  v_item_key := GetItem(v_bomline_rec.itemIdentifier,v_bomline_rec.itemUniqueId,v_bomline_rec.itemRevision,v_bomline_rec.itemVersion,v_bom_business_entity_key);
	  
	  IF v_item_key IS NULL THEN
	    v_item_type := GetItemType(v_bomline_rec.businessEntityType);
	    SELECT item_master_seq.nextval INTO v_item_key FROM dual;
	    INSERT INTO item_master
	    (item_key
	    ,item_identifier
	    ,item_unique_identifier
	    ,item_description
	    ,version
	    ,revision
	    ,business_entity_key
	    ,item_type
	    ,current_flag
	    ,delete_flag
	    ,data_source)
	    VALUES
	    (v_item_key
	    ,v_bomline_rec.itemIdentifier
	    ,v_bomline_rec.itemUniqueId
	    ,v_bomline_rec.description
	    ,v_bomline_rec.itemVersion
        ,v_bomline_rec.itemRevision
	    ,v_business_entity_key
	    ,v_item_type
	    ,'Y'
	    ,'N'
	    ,v_bomline_rec.dataSource);
	  END IF;
	  
	  SELECT count(*) INTO v_bomline_cnt
	  FROM bom_line_item
	  WHERE bom_key = v_bom_key
      AND item_key = v_item_key;
      
      IF v_bomline_cnt = 0 THEN
        v_sub_bom_key := null;
        BEGIN
	      SELECT bom_key
	      INTO v_sub_bom_key
	      FROM bom_header
	      WHERE item_key = v_item_key
	      AND status = v_status
	      AND
          (
            (v_context_business_entity_key IS NULL AND context_object_id IS NULL) OR
            (v_context_business_entity_key IS NOT NULL AND context_object_id = v_context_business_entity_key)
          );
	      
	      EXCEPTION
	        WHEN NO_DATA_FOUND THEN NULL;
	        WHEN OTHERS THEN RAISE_APPLICATION_ERROR(-20019, 'Mulitple boms found for item '||v_bomline_rec.itemIdentifier);
	    END;
	    
        INSERT INTO BOM_LINE_ITEM
        (BOM_LINE_ITEM_KEY
	    ,BOM_KEY
	    ,ITEM_KEY
	    ,SUB_BOM_KEY
	    ,ITEM_QUANTITY
	    ,DESCRIPTION
	    ,SEQUENCE_IDENTIFIER
	    ,NOTES
	    ,BOM_TYPE_CODE
	    ,BOM_TYPE_CODE_OTHER
	    ,MANAGED_FLAG
	    ,IS_SERIALIZATION_REQ
	    ,PRODUCT_QTY_TYPE_CODE
	    ,PRODUCT_QTY_TYPE_CODE_OTHER
	    ,EFFECTIVE_FROM_DT
	    ,EFFECTIVE_TO_DT
	    ,DELETE_FLAG
	    ,CURRENT_FLAG
	    ,LEAD_TIME
	    ,ATTRITION_RATE)
        values
        (bom_line_item_seq.nextval
        ,v_bom_key
        ,v_item_key
        ,v_sub_bom_key
        ,v_bomline_rec.itemQuantity
        ,v_bomline_rec.description
        ,1
        ,v_bomline_rec.notes
        ,v_bomline_rec.billOfMaterialCode
        ,v_bomline_rec.billOfMaterialCodeOther
        ,v_bomline_rec.managedBy
        ,decode(v_bomline_rec.isSerializationiRequired, 'true', 1, 'false', 0, 0)
        ,v_bomline_rec.productQuantityTypeCode
        ,v_bomline_rec.productQuantityTypeCodeOther
        ,v_bomline_rec.effectiveFromDate
        ,v_bomline_rec.effectiveToDate
        ,'N'
        ,'Y'
        ,v_bomline_rec.leadTime
        ,v_bomline_rec.attritionRate);
      END IF;
	END LOOP;
	CLOSE c_GetLoadBomLines;
	
	EXCEPTION
      WHEN OTHERS THEN
		RAISE_APPLICATION_ERROR(-20020,SQLERRM(SQLCODE));
  END LoadBomLine;
  
  FUNCTION GetItem(
  	p_itemIdentifier pcm_load_item.itemIdentifier%TYPE
  	,p_itemUniqueId pcm_load_item.itemUniqueId%TYPE
  	,p_revision pcm_load_item.revision%TYPE
  	,p_version pcm_load_item.version%TYPE
  	,p_business_entity_key business_entity.business_entity_key%TYPE)
  	RETURN NUMBER IS
  	
  	v_item_key item_master.item_key%TYPE;
  	
  BEGIN
	SELECT item_key
	INTO v_item_key
	FROM item_master
	WHERE item_identifier = p_itemIdentifier
	AND business_entity_key = p_business_entity_key;
	
	return v_item_key;
	
	EXCEPTION WHEN OTHERS THEN
	  return null;
  END GetItem;
  
  FUNCTION GetBusiness(
  	p_businessEntity pcm_load_item.businessEntity%TYPE
  	,p_business_entity_type_key business_entity_type.business_entity_type_key%TYPE)
  	RETURN NUMBER IS
  	
  	v_business_entity_key business_entity.business_entity_key%TYPE;
  	
  BEGIN
	BEGIN
	  SELECT business_entity_key
	  INTO v_business_entity_key
	  FROM business_entity b
	  WHERE business_entity_identifier = p_businessEntity
	  AND business_entity_type_key = p_business_entity_type_key;
	  
	  EXCEPTION WHEN OTHERS THEN null;
	END;
	
	IF v_business_entity_key IS NULL THEN
	  SELECT business_entity_key
	  INTO v_business_entity_key
	  FROM business_entity b
	  WHERE business_entity_name = p_businessEntity
	  AND business_entity_type_key = p_business_entity_type_key;
	END IF;
	
	return v_business_entity_key;
	
	EXCEPTION WHEN OTHERS THEN
	  return null;
  END GetBusiness;
  
  FUNCTION GetBusinessType(
  	p_businessEntityType pcm_load_item.businessEntityType%TYPE)
  	RETURN NUMBER IS
  	
  	v_business_entity_type_key business_entity_type.business_entity_type_key%TYPE;
  	
  BEGIN
	SELECT business_entity_type_key
	INTO v_business_entity_type_key
	FROM business_entity_type
	WHERE business_entity_type_name = p_businessEntityType;
	
	return v_business_entity_type_key;
	
	EXCEPTION WHEN OTHERS THEN
	  return null;
  END GetBusinessType;
  
  FUNCTION GetItemType(
  	p_businessEntityType pcm_load_item.businessEntityType%TYPE)
  	RETURN VARCHAR IS
  	
  	v_item_type item_master.item_type%TYPE;
  BEGIN
	  v_item_type := 'I';
    IF p_businessEntityType = 'ENTERPRISE' THEN
      v_item_type := 'I';
    ELSIF p_businessEntityType = 'SUPPLIER' THEN
      v_item_type := 'S';
    ELSIF p_businessEntityType = 'MANUFACTURER' THEN
      v_item_type := 'M';
    END IF;
    return v_item_type;
  END GetItemType;
  
  FUNCTION GetMostRecentBom(
  	p_item_key item_master.item_key%TYPE
  	,p_revision bom_header.revision%TYPE
  	,p_status bom_header.status%TYPE
  	,p_context_object_id in bom_header.context_object_id%TYPE)
  	RETURN NUMBER IS
  	
  	v_bom_key bom_header.bom_key%TYPE;
  	
  	CURSOR c_Boms IS
  	SELECT bom_key
  	FROM bom_header
  	WHERE item_key = p_item_key
  	AND revision = p_revision
  	AND status = p_status
  	AND
    (
      (p_context_object_id IS NULL AND context_object_id IS NULL) OR
      (p_context_object_id IS NOT NULL AND context_object_id = p_context_object_id)
    )
  	ORDER BY version DESC;
  BEGIN
	OPEN c_Boms;
	LOOP
	  FETCH c_Boms INTO v_bom_key;
	  EXIT WHEN c_Boms%NOTFOUND;
	  
	  IF v_bom_key IS NOT NULL THEN
	    EXIT;
	  END IF;
	END LOOP;
	CLOSE c_Boms;
	return v_bom_key;
  END GetMostRecentBom;
  
  FUNCTION GetSite(
  	p_site pcm_load_avl.site%TYPE
  	,p_business_entity_key business_entity.business_entity_key%TYPE)
  	RETURN NUMBER IS
  	
  	v_site_key site.site_key%TYPE;
  	
  BEGIN
	BEGIN
	  SELECT site_key
	  INTO v_site_key
	  FROM site
	  WHERE site_description = p_site
	  AND business_entity_key = p_business_entity_key;
	  
	  EXCEPTION WHEN OTHERS THEN null;
	END;
	
	IF v_site_key IS NULL THEN
	  SELECT site_key
	  INTO v_site_key
	  FROM site
	  WHERE site_identifier = p_site
	  AND business_entity_key = p_business_entity_key;
	END IF;
	
	return v_site_key;
	
	EXCEPTION WHEN OTHERS THEN
	  return null;
  END GetSite;
  
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
END LoadItemMessage;
/