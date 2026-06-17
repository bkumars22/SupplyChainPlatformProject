SET DEFINE OFF;

--From 3.0 to 3.1
ALTER TABLE ITEM_CHANGE_AUDIT ADD USER_ID VARCHAR2(60);

DROP INDEX XICA_I1;
CREATE INDEX XICA_I1
	ON ITEM_CHANGE_AUDIT(OP_CODE, ITEM_KEY, LAST_CHANGE_DT)
		TABLESPACE AUDIT_INDEX;
		
CREATE INDEX XITM_MSTR_INSDT ON ITEM_MASTER (INSERT_DT, DELETE_FLAG) tablespace audit_index;
		
-- Recreate all views, packages, and triggers
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_margin_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_margin_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_best_bom_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_best_bom_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_audit_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_audit_model.sql;
