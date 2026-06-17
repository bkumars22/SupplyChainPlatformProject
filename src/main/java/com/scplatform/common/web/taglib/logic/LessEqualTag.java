/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.logic;

import jakarta.servlet.jsp.JspException;

public class LessEqualTag extends CompareTagBase {
    protected boolean condition() throws JspException {
	return this.condition(-1, 0);
    }
}