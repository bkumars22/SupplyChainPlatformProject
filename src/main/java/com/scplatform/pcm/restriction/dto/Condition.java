/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.restriction.dto;

import jakarta.xml.bind.annotation.XmlElement;

import java.io.Serializable;

public class Condition implements Serializable {

    private static final long serialVersionUID = 1L;

    private IfExist ifExist;
    private Require require;

    public Condition() {
        super();
    }

    public Condition(IfExist ifExist, Require require) {
        super();
        this.ifExist = ifExist;
        this.require = require;
    }

    @XmlElement
    public IfExist getIfExist() {
        return ifExist;
    }

    public void setIfExist(IfExist ifExist) {
        this.ifExist = ifExist;
    }

    @XmlElement
    public Require getRequire() {
        return require;
    }

    public void setRequire(Require require) {
        this.require = require;
    }

    @Override
    public String toString() {
        return "Condition [ifExist=" + ifExist + ", require=" + require + "]";
    }
}