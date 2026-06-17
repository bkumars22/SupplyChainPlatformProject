/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.button;

import com.scplatform.testing.webui.jtags.SimpleTagModelList;
import java.util.List;

public class SimpleButtonbar extends SimpleTagModelList implements Buttonbar {
	protected boolean isStandalone_;
	protected boolean isPadded_;
	protected String alignment_;

	public SimpleButtonbar(String id) {
		this(id, true, "right", true);
	}

	public SimpleButtonbar(String id, boolean isPadded, String alignment, boolean isStandalone) {
		super(id, false, true, (List) null);
		this.isPadded_ = isPadded;
		this.alignment_ = alignment.toLowerCase();
		this.isStandalone_ = isStandalone;
	}

	public boolean isStandalone() {
		return this.isStandalone_;
	}

	public void setStandalone(boolean isStandalone) {
		this.isStandalone_ = isStandalone;
	}

	public boolean isPadded() {
		return this.isPadded_;
	}

	public String getAlignment() {
		return this.alignment_;
	}
}