/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.restriction.dto;

import jakarta.xml.bind.annotation.XmlAttribute;

import java.io.Serializable;

public class Element implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    public Element() {
        super();
    }

    public Element(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Element [name=" + name + ", value=" + value + "]";
    }

}
