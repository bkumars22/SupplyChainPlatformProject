/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.common;

public class SCPlatformConstant {
    public static final String SC_APP = "scplatform";
    public static final String EMPTY_STRING = "";
    public static String STATE_MODEL_TYPE = "SMODEL";
    public static String NOTE_TYPE = "NOTE";
    public static String ADMIN_TYPE = "ADMIN";
    public static String WORKFLOW_TYPE = "WORKFLOW";
    public static String SYSADMIN_TYPE = "SYSADMIN";

    // These are data filter types
    public static String BUSINESS_ENTITY_TYPE = "BUSINESS_ENTITY";
    public static String SITE_TYPE = "SITE";
    public static String COMMODTY_TYPE = "COMMODITY";

    public static final String AGENT_BE_ACL = "Agent";
    public static final String ALL_OP = "All";
    public static final String ANY_ENTITY = "*";
    public static final String CATEGORY = "CATEGORY";
    public static final String PLATFORM = "PLATFORM";
    public static final String SITE = "SITE";
    public static final String COSTTYPE = "COSTTYPE";
    public static final String READ = "Read";

    public static final String PAGE_SIZE = "PAGE_SIZE";
    public static final String DEFAULT_COUNTRY = "DEFAULT_COUNTRY";
    public static final String DEFAULT_LANG = "DEFAULT_LANG";
    public static final String DATE_FORMAT = "DATE_FORMAT";
    public static final String TIME_FORMAT = "TIME_FORMAT";
    public static final String TIMEZONE = "TIMEZONE";
    public static final String MAIL_FORMAT = "MAIL_FORMAT";
    public static final String DEFAULT_DATE_FORMAT = "MM-dd-yyyy";


    public static final String OPERATION_CREATETAM = "TAM CREATED";
    public static final String OPERATION_UPDATETAM = "TAM UPDATED";
    public static final String OPERATION_DELETETAM = "TAM DELETED";
    public static final String OPERATION_COPYTAM = "TAM COPIED";
    public static final String OPERATION_MASSUPDATE_TAM = "TAM MASS UPDATED";
    public static final String OPERATION_TAMROLLOVER = "TAM ROLLOVER";

    public static final String OPERATION_TAM_CASCADE_FULL_EXTRACT="TAM Cascade Full Extract";
    public static final String OPERATION_SITE_CHANGE_TAM_CASCADE_EXTRACT="Site Change TAM Cascade Extract";
    public static final String OPERATION_TAM_ROLLOVER_CASCADE_DELTA_EXTRACT="TAM Rollover Cascade Delta Extract";
    public static final String OPERATION_TAM_ALLOCATION_DELTA_EXTRACT="TAM Allocation Delta Extract";
    public static final String OPERATION_TAM_DELTA_EXTRACT = "TAM Delta Extract";
    public static final String OPERATION_TAM_FULL_EXTRACT = "TAM Full Extract";
    public static final String OPERATION_TAM_CASCADE_DELTA_EXTRACT="TAM Cascade Delta Extract";
    public static final String TAM_ONLY_PAST = "TAMONLYPAST";
    public static final String TAM_ONLY_CURRENT = "TAMONLYCURRENT";
    public static final String TAM_PAST_CURRENT_MIX = "TAMPASTCURRENTMIX";

    public static final String ACTIONUI_CREATE = "UI:CREATE";
    public static final String ACTIONUPLOAD_CREATE = "UPLOAD:CREATE";
    public static final String ACTIONUI_DELETE = "UI:DELETE";
    public static final String ACTIONUPLOAD_DELETE = "UPLOAD:DELETE";
    public static final String ACTIONUI_UPDATE = "UI:UPDATE";
    public static final String ACTIONUPLOAD_UPDATE = "UPLOAD:UPDATE";
    public static final String ACTIONUPLOAD_MASSUPDATE = "UPLOAD:MASSUPDATE";
    public static final String ACTIONUI_MASSUPDATE = "UI:MASSUPDATE";
    public static final String ACTION_UPLOAD_MASSUPLOAD = "UPLOAD:ITEM EOL";


    public static final String OPERATION_CREATEPFG = "PFG CREATED";
    public static final String OPERATION_DELETEPFG = "PFG DELETED";
    public static final String OPERATION_REMOVEFG = "REMOVE FG";
    public static final String OPERATION_ADDFG = "ADD FG";
    public static final String OPERATION_RENAMEPARENT = "PFG RENAME";
    public static final String OPERATION_UPDATEPARENT = "PFG UPDATE";

    public static final String OPERATION_CREATEFG = "FG CREATED";
    public static final String OPERATION_RENAMEFG = "FG RENAMED";
    public static final String OPERATION_UPDATEFG = "FG UPDATED";
    public static final String OPERATION_ADDITEM = "ADD ITEM";
    public static final String OPERATION_REMOVEITEM = "REMOVE ITEM";
    public static final String OPERATION_ADDPFG = "ADD PFG";
    public static final String OPERATION_REMOVEPFG = "REMOVE PFG";
    public static final String OPERATION_DEACTIVATE = "FG INACTIVATED";
    public static final String OPERATION_DELTA_EXTRACT = "Functional Group Delta Extract";

    public static final String OPERATION_CREATE_XLOB_TAM = "XLOB CREATED";
    public static final String OPERATION_UPDATE_XLOB_TAM = "XLOB UPDATED";
    public static final String OPERATION_DELETET_XLOB_AM = "XLOB DELETED";
    public static final String OPERATION_COPY_XLOB_TAM = "TAM COPIED";
    public static final String OPERATION_MASSUPDATE_XLOB = "XLOB MASS UPDATED";
    public static final String OPERATION_XLOBROLLOVER = "XLOB ROLLOVER";

    // Dashboard recordType constants
    public static final String RECORD_TYPE_COST_RECORD = "costRecord";
    public static final String RECORD_TYPE_SOURCING_LANE = "sourcingLane";
    public static final String RECORD_TYPE_FORECAST = "forecast";
    public static final String RECORD_TYPE_FORECAST_ADJ = "forecast_ADJ";
    public static final String RECORD_TYPE_REBATE_PROGRAM = "rebateProgram";
    public static final String RECORD_TYPE_BOM = "bom";
}
