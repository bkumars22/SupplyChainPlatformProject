/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Replaces session-scoped UploadFileForm.
 * Carries all state needed to render uploadFile.jsp and process submissions.
 */
@Data
public class UploadFileResponse {

    // Available upload types keyed by extension group ("xlsx", "xls")
    // Structure: { "xlsx": ["PriceTAMMonthly", ...], "xls": ["CostRecordUI", ...] }
    private Map<String, List<String>> availableMessageTypes = new HashMap<>();

    // Types that should be labelled as *.xlsx in the UI dropdown
    private List<String> xlxsType = new ArrayList<>();

    // Which sub-menu group was requested (admin, pricing, cost_forecast, etc.)
    private String uploadMenuType;

    // --- post-submit results ---

    // Successful submission links: [{"fileName":"...", "transactionId":"...", "loadJobKey":"..."}]
    private List<Map<String, String>> successLinks = new ArrayList<>();

    // Validation / loader error rows for the error table
    private List<LoadMessage> errorDetails;

    // Whether to show the upload form (false when ACL check failed)
    private boolean error = false;

    // Max number of files allowed per upload (from pcm.upload.maxfiles config)
    private int maxFiles = 1;

    // -----------------------------------------------------------------------
    // Helpers matching UploadFileForm API used in the JSP / controller
    // -----------------------------------------------------------------------

    public void addAvailableMessageTypes(String group, List<String> types) {
        availableMessageTypes.computeIfAbsent(group, k -> new ArrayList<>()).addAll(types);
    }

    public void addSuccessLink(String fileName, String transactionId, String loadJobKey) {
        Map<String, String> link = new HashMap<>();
        link.put("fileName", fileName);
        link.put("transactionId", transactionId);
        link.put("loadJobKey", loadJobKey);
        successLinks.add(link);
    }
}
