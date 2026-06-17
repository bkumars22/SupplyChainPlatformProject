/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.restriction.dto;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.List;

public class Screen implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private List<Condition> condition;

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Screen() {
        super();
    }

    public Screen(String name, List<Condition> condition) {
        super();
        this.name = name;
        this.condition = condition;
    }

    @XmlElement
    public List<Condition> getCondition() {
        return condition;
    }

    public void setCondition(List<Condition> condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "Screen [name=" + name + ", condition=" + condition + "]";
    }


}
