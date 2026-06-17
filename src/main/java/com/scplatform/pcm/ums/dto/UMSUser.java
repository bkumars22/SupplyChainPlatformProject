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

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UMSUser {

    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private boolean status = true;
    private String preferredLocale;
    private String preferredTimezone;
    private String preferredPagination;
    private List<UMSUserAccessControlIds> accessControls = new ArrayList<>();
}
