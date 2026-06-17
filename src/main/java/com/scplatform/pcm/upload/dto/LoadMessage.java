/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents one error row returned from validation or loading.
 * Replaces legacy MessageLoaderErrorParser.LoadMessage.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoadMessage {

    private String line;      // location / context
    private String message;   // human-readable error text
    private String type;      // e.g. "ERROR", "WARN"
}
