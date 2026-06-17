/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

public abstract class BaseTagModel implements TagModel {
	protected String id_;
	protected boolean isDisabled_;
	protected boolean isVisible_;

	protected BaseTagModel(String id, boolean isDisabled, boolean isVisible) {
		this.setId(id);
		this.isDisabled_ = isDisabled;
		this.isVisible_ = isVisible;
	}

	protected BaseTagModel(String id, String isDisabled, String isVisible) {
		this(id, false, true);
		this.setDisabled(this.getBoolean(isDisabled));
		this.setVisible(this.getBoolean(isVisible));
	}

	public final String getId() {
		return this.id_;
	}

	public final void setId(String id) {
		if (id != null) {
			this.id_ = id;
		} else {
			this.id_ = "";
		}

	}

	public final boolean isDisabled() {
		return this.isDisabled_;
	}

	public final void setDisabled(boolean isDisabled) {
		this.isDisabled_ = isDisabled;
	}

	public final void setDisabled(String isDisabled) {
		this.setDisabled(this.getBoolean(isDisabled));
	}

	public final boolean isVisible() {
		return this.isVisible_;
	}

	public final void setVisible(boolean isVisible) {
		this.isVisible_ = isVisible;
	}

	public final void setVisible(String isVisible) {
		this.setVisible(this.getBoolean(isVisible));
	}

	protected final boolean getBoolean(String value) {
		return "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
	}

	public int hashCode() {
		return this.id_.hashCode();
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj instanceof BaseTagModel) {
			BaseTagModel btm = (BaseTagModel) obj;
			return btm.hashCode() == this.hashCode();
		} else {
			return false;
		}
	}

	public String toString() {
		return "BaseTagModel: " + this.id_;
	}
}