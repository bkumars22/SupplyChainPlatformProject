/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.wizard;

public class SimpleStep implements Step {
	protected String name_ = null;
	protected boolean isRequired_;

	public SimpleStep(String name, boolean isRequired) {
		this.name_ = name;
		this.isRequired_ = isRequired;
	}

	public String getName() {
		return this.name_;
	}

	public void setName(String theName) {
		this.name_ = theName;
	}

	public boolean isRequired() {
		return this.isRequired_;
	}

	public void setRequired(boolean isRequired) {
		this.isRequired_ = isRequired;
	}

	public String toString() {
		return "Step: " + this.name_;
	}

	public int hashCode() {
		return this.name_.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else {
			return obj instanceof SimpleStep && obj.hashCode() == this.hashCode();
		}
	}
}