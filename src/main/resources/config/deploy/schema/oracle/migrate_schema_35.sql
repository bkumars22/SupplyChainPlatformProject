SET DEFINE OFF;

--From 3.4 to 3.5

ALTER TABLE PCM_COST_RECORD ADD REASON_CODE VARCHAR2(128);

@ /scplatform/app/scplatform/deploy/schema/oracle/drop_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_mcm_views.sql;