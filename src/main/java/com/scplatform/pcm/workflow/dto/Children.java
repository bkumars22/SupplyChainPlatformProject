/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.workflow.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

/**
 * Children DTO for nested menu items in workflow navigation.
 * 
 * <p>Represents a child menu item that can be nested within a Menu or another Children.
 * Supports hierarchical navigation structures for workflows.
 * 
 * @author PCM Team
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "title",
    "text",
    "name",
    "url",
    "children"
})
public class Children {

    @JsonProperty("title")
    private String title;

    @JsonProperty("text")
    private String text;

    @JsonProperty("name")
    private String name;

    @JsonProperty("url")
    private String url;

    @JsonProperty("children")
    private List<List<Children>> children;

    /**
     * Default constructor
     */
    public Children() {
    }

    /**
     * Constructor with name and text
     * 
     * @param name the menu item name
     * @param text the display text
     */
    public Children(String name, String text) {
        this.name = name;
        this.text = text;
    }

    /**
     * Constructor with name, text, and url
     * 
     * @param name the menu item name
     * @param text the display text
     * @param url the target URL
     */
    public Children(String name, String text, String url) {
        this.name = name;
        this.text = text;
        this.url = url;
    }

    /**
     * Constructor with title, text, name, and url
     * 
     * @param title the title
     * @param text the display text
     * @param name the menu item name
     * @param url the target URL
     */
    public Children(String title, String text, String name, String url) {
        this.title = title;
        this.text = text;
        this.name = name;
        this.url = url;
    }

    /**
     * Constructor with all fields
     * 
     * @param title the title
     * @param text the display text
     * @param name the menu item name
     * @param url the target URL
     * @param children the nested children structure
     */
    public Children(String title, String text, String name, String url, List<List<Children>> children) {
        this.title = title;
        this.text = text;
        this.name = name;
        this.url = url;
        this.children = children;
    }

    @Override
    public String toString() {
        return "ClassPojo [title = " + title + ", text = " + text + ", name = " + name 
                + ", url = " + url + "]";
    }
}
