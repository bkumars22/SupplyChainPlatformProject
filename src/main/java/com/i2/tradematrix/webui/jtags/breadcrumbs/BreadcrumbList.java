/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.breadcrumbs;

import com.scplatform.testing.webui.jtags.TagModelList;

public interface BreadcrumbList extends TagModelList {
	String getApplicationName();

	void setApplicationName(String var1);

	String getTarget();

	void setTarget(String var1);

	void setAutoinit(boolean var1);

	boolean getAutoinit();
}