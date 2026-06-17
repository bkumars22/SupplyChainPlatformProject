/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.button;

import com.scplatform.testing.webui.jtags.ClickableTagModel;

public interface Button extends ClickableTagModel {
	boolean isEmphasized();

	boolean isRegular();

	boolean isSmall();

	boolean isPadded();
}