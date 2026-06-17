/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import com.scplatform.testing.webui.taglib.Settings;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.BodyContent;

public interface TagRenderer {
	void render(TagModel var1, BodyContent var2, JspWriter var3, Settings var4) throws JspException;
}