/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.toolbar;

import com.scplatform.testing.webui.jtags.BaseClickableTagModel;

public class SimpleToolbarButton extends BaseClickableTagModel implements ToolbarButton {
	protected String enabledImage_;
	protected String disabledImage_;

	public SimpleToolbarButton(String id, String onclick, String target, String tooltip, String enabledImage,
			String disabledImage) {
		super(id, onclick, (String) null, target, tooltip);
		this.enabledImage_ = enabledImage;
		this.disabledImage_ = disabledImage;
	}

	public String getImage() {
		return this.isDisabled_ ? this.disabledImage_ : this.enabledImage_;
	}

	public String toString() {
		return "Toolbar button: " + this.id_ + " : " + this.onClick_ + " : " + this.isDisabled_;
	}
}