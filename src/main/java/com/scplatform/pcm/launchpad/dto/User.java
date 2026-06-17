
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "username",
    "name",
    "role",
    "image",
    "title",
    "showRole",
    "backToCLP",
    "availableRoles"
})
public class User {

    @JsonProperty("username")
    private String username;
    @JsonProperty("name")
    private String name;
    @JsonProperty("role")
    private String role;
    @JsonProperty("image")
    private String image;
    @JsonProperty("title")
    private String title;
    @JsonProperty("showRole")
    private Boolean showRole;
    @JsonProperty("backToCLP")
    private String backToCLP;
    @JsonProperty("availableRoles")
    private List<AvailableRole> availableRoles = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("username")
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

    @JsonProperty("image")
    public String getImage() {
        return image;
    }

    @JsonProperty("image")
    public void setImage(String image) {
        this.image = image;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("showRole")
    public Boolean getShowRole() {
        return showRole;
    }

    @JsonProperty("showRole")
    public void setShowRole(Boolean showRole) {
        this.showRole = showRole;
    }

    @JsonProperty("backToCLP")
    public String getBackToCLP() {
        return backToCLP;
    }

    @JsonProperty("backToCLP")
    public void setBackToCLP(String backToCLP) {
        this.backToCLP = backToCLP;
    }

    @JsonProperty("availableRoles")
    public List<AvailableRole> getAvailableRoles() {
        return availableRoles;
    }

    @JsonProperty("availableRoles")
    public void setAvailableRoles(List<AvailableRole> availableRoles) {
        this.availableRoles = availableRoles;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
