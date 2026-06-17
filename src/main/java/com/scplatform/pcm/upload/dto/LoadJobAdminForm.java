/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.dto;

import com.scplatform.pcm.upload.entity.LoadJob;
import lombok.Data;

import java.util.List;

/**
 * Replaces the legacy Struts {@code LoadJobAdminForm} ActionForm.
 * Carries all state needed to render loadJobDetailPage.jsp.
 */
@Data
public class LoadJobAdminForm {

    /** The job being viewed. */
    private LoadJob selectedLoadJob;

    /** PK of the job — passed as a request param and echoed back as a hidden field. */
    private String selectedLoadJobKey;

    /** Whether the user has made unsaved changes (driven by JS). */
    private boolean unsavedData = false;

    /** Whether the "clear all matching" checkbox is ticked. */
    private boolean clearAll = false;

    /** Whether the current user is permitted to replay this job. */
    private boolean replayAllowed = false;

    /** Business key for the assign-to operation. */
    private String assignToBusinessKey;

    /** Preserves search values when navigating back to the search page. */
    private String preserveSearchValues;

    /** File name used for error-file download actions. */
    private String errorFileName;

    /**
     * Keys of load events selected for clear / assign-to operations.
     * Replaces the legacy {@code long[] selectedEventKeys}.
     * Spring MVC binds repeated {@code selectedEventKeys} params into a List automatically.
     */
    private List<Long> selectedEventKeys;

    /** Optional error summary set by the service for display on the page. */
    private String jobErrorDetails;
}
