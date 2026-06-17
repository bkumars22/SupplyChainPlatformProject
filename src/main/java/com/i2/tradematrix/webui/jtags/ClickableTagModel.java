/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

public interface ClickableTagModel extends TagModel {
	String getLabel();

	void setLabel(String var1);

	String getOnClick();

	String getOnMouseOver();

	String getOnMouseOut();

	String getTarget();

	void setTarget(String var1);

	String getTooltip();

	void setTooltip(String var1);
}