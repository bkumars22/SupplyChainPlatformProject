WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK


CREATE OR REPLACE TRIGGER T_ITEM_RESPONSIBILITY
	AFTER INSERT OR UPDATE OR DELETE ON PCM_ASSIGNMENT
	FOR EACH ROW
DECLARE
	v_item_key item_master.item_key%TYPE;
	v_item_type item_master.item_type%TYPE;
	v_config_value pcm_db_configuration.config_value%TYPE;
	v_item_config_exists number(1);
	v_continue NUMBER(1) := 0;
BEGIN
	IF DELETING THEN
		IF :old.assignment_type = 'I' THEN
			v_item_key := :old.object_key;
			v_continue := 1;
		END IF;
	ELSE
		IF :new.assignment_type = 'I' THEN
			v_item_key := :new.object_key;
			v_continue := 1;
		END IF;
	END IF;
	v_item_config_exists := 0;
	IF v_continue = 1 THEN
        SELECT ITEM_MASTER.ITEM_TYPE INTO v_item_type FROM ITEM_MASTER	WHERE ITEM_KEY = v_item_key;
		SELECT COUNT(config_value) INTO v_item_config_exists FROM PCM_DB_CONFIGURATION WHERE CONFIG_KEY = 'pcm.db.config.item.types.for.item.owner.extract' AND CONFIG_VALUE LIKE '%' ||  v_item_type || '%';
        IF v_item_config_exists != 0  THEN
            INSERT INTO ITEM_CHANGE_AUDIT
            (ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE)
            VALUES
            (ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'OWNER');
        END IF;
	END IF;
END T_ITEM_RESPONSIBILITY;
/

CREATE TRIGGER T_ITEM_AVL
	AFTER INSERT OR UPDATE OR DELETE ON ITEM_AVL
	FOR EACH ROW
DECLARE
	v_item_key item_avl.item_key%TYPE;
BEGIN
	v_item_key := :new.item_key;
	IF DELETING THEN
		v_item_key := :old.item_key;
	END IF;

	INSERT INTO ITEM_CHANGE_AUDIT
	(ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE)
	VALUES
	(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key,SYSDATE,'AVL');
END T_ITEM_AVL;
/

CREATE OR REPLACE TRIGGER T_SUPPLIER_ALLOCATION
	AFTER INSERT OR UPDATE OR DELETE ON PCM_SUPPLIER_ALLOCATION
	FOR EACH ROW
DECLARE
	v_item_key pcm_supplier_allocation.customer_item_key%TYPE;
	v_user_id pcm_supplier_allocation.status_last_change_by%TYPE;
BEGIN
	IF :new.customer_item_group_item_key IS NULL THEN
		v_item_key := :new.customer_item_key;
    	IF DELETING THEN
			v_item_key := :old.customer_item_key;
			v_user_id := :old.status_last_change_by;
		END IF;
	
		INSERT INTO ITEM_CHANGE_AUDIT
		(ITEM_CHANGE_AUDIT_KEY,ITEM_KEY,LAST_CHANGE_DT,OP_CODE,USER_ID)
		VALUES
		(ITEM_CHANGE_AUDIT_SEQ.NEXTVAL,v_item_key, systimestamp,'SA',v_user_id);
	END IF;
END T_SUPPLIER_ALLOCATION;
/
