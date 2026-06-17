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
 * Menu DTO for workflow navigation structure.
 * 
 * <p>Represents a hierarchical menu item with optional nested children.
 * Used for building workflow navigation menus in the application.
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
    "children"
})
public class Menu {

    @JsonProperty("title")
    private String title;

    @JsonProperty("text")
    private String text;

    @JsonProperty("name")
    private String name;

    @JsonProperty("children")
    private List<List<Children>> children;

    /**
     * Default constructor
     */
    public Menu() {
    }

    /**
     * Constructor with name and text
     * 
     * @param name the menu item name
     * @param text the display text
     */
    public Menu(String name, String text) {
        this.name = name;
        this.text = text;
    }

    /**
     * Constructor with name, text, and title
     * 
     * @param name the menu item name
     * @param text the display text
     * @param title the title
     */
    public Menu(String name, String text, String title) {
        this.name = name;
        this.text = text;
        this.title = title;
    }

    /**
     * Constructor with all fields
     * 
     * @param title the title
     * @param text the display text
     * @param name the menu item name
     * @param children the nested children structure
     */
    public Menu(String title, String text, String name, List<List<Children>> children) {
        this.title = title;
        this.text = text;
        this.name = name;
        this.children = children;
    }

    @Override
    public String toString() {
        return "Menu [title=" + title + ", text=" + text + ", name=" + name 
                + ", children=" + children + "]";
    }
}
