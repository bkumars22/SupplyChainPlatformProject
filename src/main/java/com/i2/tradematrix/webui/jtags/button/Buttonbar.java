/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.button;

import com.scplatform.testing.webui.jtags.TagModelList;

public interface Buttonbar extends TagModelList {
	String getAlignment();

	boolean isPadded();

	boolean isStandalone();

	void setStandalone(boolean var1);
}