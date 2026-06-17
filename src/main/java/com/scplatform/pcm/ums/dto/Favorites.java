/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.ums.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for Favorites menu items
 * Used for JSON serialization/deserialization of user favorite links
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorites {

    private String id;

    private String title;

    private String text;

    @JsonProperty("isHome")
    private boolean isHome;

    private String url;

    private String app;

    private boolean hide;

    private String roleName;

    @JsonProperty("isExternal")
    private boolean isExternal;

    private List<Favorites> items;

    @Override
    public String toString() {
        return "Favorites{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", isHome=" + isHome +
                ", url='" + url + '\'' +
                ", app='" + app + '\'' +
                ", hide=" + hide +
                ", roleName='" + roleName + '\'' +
                ", isExternal=" + isExternal +
                ", items=" + items +
                '}';
    }
}

