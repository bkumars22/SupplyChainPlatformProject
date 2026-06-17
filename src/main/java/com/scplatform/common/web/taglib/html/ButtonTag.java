/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

public class ButtonTag extends SubmitTag {
    protected String getElementOpen() {
	return "<input type=\"button\"";
    }

    protected String getDefaultValue() {
	return "Click";
    }
}