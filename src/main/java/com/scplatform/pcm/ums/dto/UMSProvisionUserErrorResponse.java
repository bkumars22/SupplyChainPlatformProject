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
public class UMSProvisionUserErrorResponse implements UMSResponse {

    private UMSUserErrorResponse errors = new UMSUserErrorResponse();

    @Data
    @NoArgsConstructor
    public static class UMSUserErrorResponse {
        private List<String> user = new ArrayList<>();
        private List<AccessControl> accessControls = new ArrayList<>();

        public void addAccessControl(AccessControl accessControl) {
            accessControls.add(accessControl);
        }

        public void addAccessControl(String accessControlId, String accessGroupId, List<String> errors) {
            AccessControl accessControl = new AccessControl();
            accessControl.setAccessControlId(accessControlId);
            accessControl.setAccessGroupId(accessGroupId);
            accessControl.setErrors(errors);
            accessControls.add(accessControl);
        }

        @Data
        @NoArgsConstructor
        public static class AccessControl {
            private String accessControlId;
            private String accessGroupId;
            private List<String> errors;
        }
    }
}
