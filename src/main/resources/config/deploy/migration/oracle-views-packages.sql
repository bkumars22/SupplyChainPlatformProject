-- drop all views, packages, and triggers
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_margin_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_best_bom_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_audit_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_bom_auto_approve_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/drop_forecast_auto_approve_model.sql;

WHENEVER SQLERROR EXIT 5 ROLLBACK
WHENEVER OSERROR EXIT 10  ROLLBACK

-- create all views, packages, and triggers
@ /scplatform/app/scplatform/deploy/schema/oracle/create_mcm_views.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_margin_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_best_bom_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_audit_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_bom_auto_approve_model.sql;
@ /scplatform/app/scplatform/deploy/schema/oracle/create_forecast_auto_approve_model.sql;
