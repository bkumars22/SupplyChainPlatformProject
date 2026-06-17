/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.workflow.dto;

import java.util.List;
import com.scplatform.pcm.ums.dto.FavoriteResponse;
import com.scplatform.pcm.ums.dto.Favorites;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Header DTO for workflow menu and favorites structure.
 * 
 * <p>Represents the header response containing hierarchical menu items
 * and user's favorite workflows/items.
 * 
 * @author PCM Team
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "menu",
    "favorites"
})
public class Header implements FavoriteResponse {

    @JsonProperty("menu")
    private List<List<Menu>> menu;

    @JsonProperty("favorites")
    private List<Favorites> favorites;

    /**
     * Default constructor
     */
    public Header() {
    }

    /**
     * Constructor with menu and favorites
     * 
     * @param menu the hierarchical menu structure
     * @param favorites the list of favorites
     */
    public Header(List<List<Menu>> menu, List<Favorites> favorites) {
        this.menu = menu;
        this.favorites = favorites;
    }

    @Override
    public String toString() {
        return "Header [menu=" + menu + ", favorites=" + favorites + "]";
    }
}
