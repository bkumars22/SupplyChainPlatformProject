/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.breadcrumbs;

import com.scplatform.testing.webui.jtags.BaseClickableTagModel;

public class SimpleBreadcrumb extends BaseClickableTagModel implements Breadcrumb {
	public SimpleBreadcrumb() {
		super("", (String) null, (String) null, (String) null, (String) null);
	}

	public SimpleBreadcrumb(String id, String onClick, String label) {
		super(id, onClick, label, (String) null, label);
		this.setOnMouseOver("javascript:self.status='" + label + "';return true;");
		this.setOnMouseOut("javascript:self.status='';return true;");
	}

	public String toString() {
		return "Crumb: " + this.label_ + " : " + this.onClick_;
	}
}