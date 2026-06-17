/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import java.io.Serializable;

public interface TagModel extends Serializable {
	String getId();

	void setId(String var1);

	boolean isDisabled();

	void setDisabled(boolean var1);

	boolean isVisible();

	void setVisible(boolean var1);
}