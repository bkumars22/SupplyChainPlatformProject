SET DEFINE OFF;

--From 3.1 to 3.2
CREATE TABLE MIGRATE32_ITEM_CHANGE_AUDIT AS SELECT * FROM ITEM_CHANGE_AUDIT;

DROP TABLE ITEM_CHANGE_AUDIT;

CREATE TABLE ITEM_CHANGE_AUDIT
(
	ITEM_KEY NUMBER(19) NOT NULL,
	LAST_CHANGE_DT TIMESTAMP NOT NULL,
	OP_CODE VARCHAR2(10) NOT NULL,
	USER_ID VARCHAR2(60)
) TABLESPACE AUDIT_DATA;

CREATE INDEX XICA_I1
	ON ITEM_CHANGE_AUDIT(OP_CODE, ITEM_KEY, LAST_CHANGE_DT)
		TABLESPACE AUDIT_INDEX;

INSERT INTO ITEM_CHANGE_AUDIT (SELECT * FROM MIGRATE32_ITEM_CHANGE_AUDIT);

DROP TABLE MIGRATE32_ITEM_CHANGE_AUDIT;
		
-- Recreate all views, packages, and triggers
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_margin_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_margin_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_best_bom_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_best_bom_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_audit_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_audit_model.sql;
