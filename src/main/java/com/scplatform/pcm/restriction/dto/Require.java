/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.restriction.dto;

import jakarta.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.List;


public class Require implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Element> element;

    public Require() {
        super();
    }

    public Require(List<Element> element) {
        super();
        this.element = element;
    }

    @XmlElement
    public List<Element> getElement() {
        return element;
    }

    public void setElement(List<Element> element) {
        this.element = element;
    }

    @Override
    public String toString() {
        return "Require [element=" + element + "]";
    }

}
