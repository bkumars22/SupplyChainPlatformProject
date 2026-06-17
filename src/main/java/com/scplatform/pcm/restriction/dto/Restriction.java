/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.restriction.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.List;

@XmlRootElement
public class Restriction implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Screen> screen;

    public Restriction() {
        super();
    }

    public Restriction(List<Screen> screen) {
        super();
        this.screen = screen;
    }

    @XmlElement
    public List<Screen> getScreen() {
        return this.screen;
    }

    public void setScreen(List<Screen> screen) {
        this.screen = screen;
    }

    @Override
    public String toString() {
        return "Restriction [screenList=" + screen + "]";
    }
}
