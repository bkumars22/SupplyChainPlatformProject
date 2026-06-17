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
public class FavoriteErrorResponse implements FavoriteResponse, GenericResponse {

    private List<String> errors = new ArrayList<>();

    public FavoriteErrorResponse(String error) {
        errors.add(error);
    }

    public void addError(String error) {
        this.errors.add(error);
    }
}
