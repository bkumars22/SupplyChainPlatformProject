
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import java.util.HashMap;
import java.util.Map;

import com.scplatform.pcm.ums.dto.GenericResponse;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "logo",
    "banner",
    "help",
    "user",
    "menu"
})
public class Header implements GenericResponse  {

    @JsonProperty("logo")
    private Logo logo;
    @JsonProperty("banner")
    private Banner banner;
    @JsonProperty("help")
    private Help help;
    @JsonProperty("user")
    private User user;
    @JsonProperty("menu")
    private Menu menu;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("logo")
    public Logo getLogo() {
        return logo;
    }

    @JsonProperty("logo")
    public void setLogo(Logo logo) {
        this.logo = logo;
    }

    @JsonProperty("banner")
    public Banner getBanner() {
        return banner;
    }

    @JsonProperty("banner")
    public void setBanner(Banner banner) {
        this.banner = banner;
    }

    @JsonProperty("help")
    public Help getHelp() {
        return help;
    }

    @JsonProperty("help")
    public void setHelp(Help help) {
        this.help = help;
    }

    @JsonProperty("user")
    public User getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(User user) {
        this.user = user;
    }

    @JsonProperty("menu")
    public Menu getMenu() {
        return menu;
    }

    @JsonProperty("menu")
    public void setMenu(Menu menu) {
        this.menu = menu;
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
