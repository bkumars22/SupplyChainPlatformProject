/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.navpad;

import com.scplatform.testing.webui.jtags.TagModelList;

public interface PadItemTree extends TagModelList {
	boolean isApplication();

	void setIsApplication(boolean var1);

	String getTarget();

	void setTarget(String var1);
}