/*
 * Copyright (c) 2014 E2open Inc. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2014, by E2open Inc. All rights reserved.
 */

package com.scplatform.pcm.ums.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UMSAccessControl {

    private String accessControlId;
    private String accessControlDisplayName;
    private String accessControlDescription;
}
