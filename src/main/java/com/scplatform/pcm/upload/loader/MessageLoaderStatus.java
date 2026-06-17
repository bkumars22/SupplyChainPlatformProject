/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class MessageLoaderStatus {

    // Result code constants
    public static final int UNKNOWN = -99;
    public static final int ERROR   = -1;
    public static final int WARN    =  0;
    public static final int SUCCESS =  1;

    protected int       resultCode;
    protected int       count;

    /** Custom getter — do not let Lombok generate a plain getter for this field. */
    @Getter(AccessLevel.NONE)
    protected String    resultMessage;

    protected Throwable exceptionCause;
    protected String    loadJobId;

    protected Map<String, Integer> statistics = new LinkedHashMap<>();

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    public MessageLoaderStatus() {
        this.resultCode = SUCCESS;
        this.count = 0;
    }

    public MessageLoaderStatus(int resultCode, String resultMessage) {
        this.resultCode    = resultCode;
        this.resultMessage = resultMessage;
    }

    // -----------------------------------------------------------------------
    // Custom accessors
    // -----------------------------------------------------------------------

    /**
     * Returns the combined result message.
     * If an exception cause is present and its message is not already included,
     * it is prepended to the result message.
     */
    public String getResultMessage() {
        StringBuilder work = new StringBuilder();
        if (StringUtils.isNotEmpty(resultMessage)) {
            work.append(resultMessage);
        }
        if (exceptionCause != null) {
            String exceptionMessage = StringUtils.trimToEmpty(exceptionCause.getMessage());
            if (!work.toString().contains(exceptionMessage)) {
                work.insert(0, exceptionMessage + "\n");
            }
        }
        return work.toString();
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    // -----------------------------------------------------------------------
    // Convenience status checks
    // -----------------------------------------------------------------------

    /** Returns {@code true} when the loader run failed. */
    public boolean hasFailed() {
        return resultCode == ERROR;
    }

    /** Returns {@code true} when the loader run succeeded. */
    public boolean hasSucceeded() {
        return resultCode == SUCCESS;
    }

    // -----------------------------------------------------------------------
    // Statistics
    // -----------------------------------------------------------------------

    public void setStatistic(String statName, Integer statValue) {
        statistics.put(statName, statValue);
    }

    public Integer getStatistic(String statName) {
        Integer val = statistics.get(statName);
        return (val != null) ? val : 0;
    }

    // -----------------------------------------------------------------------
    // Exception cause
    // -----------------------------------------------------------------------

    /** Sets the result code to {@link #ERROR} and records the causing exception. */
    public void setResultStatus(Throwable cause) {
        this.resultCode      = ERROR;
        this.exceptionCause  = cause;
    }

    // -----------------------------------------------------------------------
    // toString
    // -----------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ResultCode:").append(resultCode).append("\n");
        if (resultMessage != null) {
            sb.append("ResultMessage:").append(resultMessage).append("\n");
        }
        if (exceptionCause != null) {
            sb.append("Cause:").append(exceptionCause).append("\n");
        }
        sb.append("LoadJobId:").append(loadJobId).append("\n");
        sb.append("Statistics:\n");
        if (!statistics.isEmpty()) {
            statistics.forEach((k, v) ->
                sb.append("  ").append(k).append("=").append(v).append("\n"));
        } else {
            sb.append("  none\n");
        }
        return sb.toString();
    }
}
