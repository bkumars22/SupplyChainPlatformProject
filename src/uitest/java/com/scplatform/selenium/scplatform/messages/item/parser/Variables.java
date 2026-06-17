/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.item.parser;

import com.test.selenium.common.modelViewController.model.Model;

/**
 * Used for variable data within the Items data
 * 
 * @author dgenrich
 *
 */

public class Variables extends Model {

    private static final long serialVersionUID = 1L;

    /**
     * Keyname in data file is the name within <>, for example, "DISPLAY"
     */
    private String KeyName;

    /**
     * Keyvalue is the data to replace the {@link #Keyname} value. Example, "16
     * GB"
     */
    private String KeyValue;

    /**
     * @return the keyName
     */
    public String getKeyName() {
        return KeyName;
    }

    /**
     * @param keyName
     *            the keyName to set
     */
    public void setKeyName(String keyName) {
        KeyName = keyName;
    }

    /**
     * @return the keyValue
     */
    public String getKeyValue() {
        return KeyValue;
    }

    /**
     * @param keyValue
     *            the keyValue to set
     */
    public void setKeyValue(String keyValue) {
        KeyValue = keyValue;
    }

}
