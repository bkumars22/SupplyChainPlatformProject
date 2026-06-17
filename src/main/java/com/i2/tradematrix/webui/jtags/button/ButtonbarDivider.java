/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.button;

import com.scplatform.testing.webui.jtags.BaseTagModel;

public class ButtonbarDivider extends BaseTagModel {
	public static final ButtonbarDivider DIVIDER = new ButtonbarDivider("divider");

	public ButtonbarDivider(String id) {
		super(id, false, true);
	}
}