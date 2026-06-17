/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.button;

import com.scplatform.testing.webui.jtags.BaseClickableTagModel;

public class SimpleButton extends BaseClickableTagModel implements Button {
	protected boolean isEmphasized_ = false;
	protected boolean isRegular_;
	protected boolean isSmall_;
	protected boolean isPadded_;

	public SimpleButton(String id, String onclick, String label) {
		super(id, onclick, label, (String) null, (String) null);
		this.isRegular_ = !this.isEmphasized_;
		this.isSmall_ = false;
		this.isPadded_ = false;
		this.setPadded(true);
		this.setRegular(true);
	}

	public void setEmphasized(boolean isEmphasized) {
		this.isEmphasized_ = isEmphasized;
		this.isRegular_ = !isEmphasized;
	}

	public void setEmphasized(String isEmphasized) {
		this.setEmphasized(this.getBoolean(isEmphasized));
	}

	public boolean isEmphasized() {
		return this.isEmphasized_;
	}

	public void setRegular(boolean isRegular) {
		this.isRegular_ = isRegular;
		this.isEmphasized_ = !isRegular;
	}

	public void setRegular(String isRegular) {
		this.setRegular(this.getBoolean(isRegular));
	}

	public boolean isRegular() {
		return this.isRegular_;
	}

	public void setSmall(boolean isSmall) {
		this.isSmall_ = isSmall;
	}

	public void setSmall(String isSmall) {
		this.setSmall(this.getBoolean(isSmall));
	}

	public boolean isSmall() {
		return this.isSmall_;
	}

	public void setPadded(boolean isPadded) {
		this.isPadded_ = isPadded;
	}

	public void setPadded(String isPadded) {
		this.setPadded(this.getBoolean(isPadded));
	}

	public boolean isPadded() {
		return this.isPadded_;
	}
}