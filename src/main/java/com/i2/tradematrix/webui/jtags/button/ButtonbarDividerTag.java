/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.button;

import com.scplatform.testing.webui.jtags.TagRenderer;
import com.scplatform.testing.webui.jtags.TagRendererFactory;
import com.scplatform.testing.webui.taglib.Settings;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ButtonbarDividerTag extends TagSupport {
	private static final Logger log_ = LoggerFactory.getLogger(ButtonbarDividerTag.class);

	public int doStartTag() throws JspException {
		try {
			Settings settings = (Settings) this.pageContext.findAttribute("i2.settings");
			TagRenderer renderer = TagRendererFactory.getRenderer("buttonbardivider");
			renderer.render(ButtonbarDivider.DIVIDER, (BodyContent) null, this.pageContext.getOut(), settings);
		} catch (Exception var3) {
			log_.error(var3.getMessage(), var3);
		}

		return 0;
	}

}