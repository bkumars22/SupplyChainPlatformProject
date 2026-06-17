/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.navpad;

import com.scplatform.testing.webui.jtags.SimpleTagModelList;
import java.util.List;

public class SimplePadItemTree extends SimpleTagModelList implements PadItemTree {
	protected boolean isRemoveOnClick_;
	protected boolean isApplication_;
	protected String target_;

	public SimplePadItemTree() {
		this("", (List) null, (String) null, true);
	}

	public SimplePadItemTree(String id, List children, String target, boolean isApplication) {
		super(id, false, true, children);
		this.isRemoveOnClick_ = false;
		this.isApplication_ = true;
		this.target_ = target;
		this.isApplication_ = isApplication;
	}

	public SimplePadItemTree(String id, List children, String target, boolean isApplication, boolean removeOnClick) {
		super(id, false, true, children);
		this.isRemoveOnClick_ = false;
		this.isApplication_ = true;
		this.target_ = target;
		this.isApplication_ = isApplication;
		this.isRemoveOnClick_ = removeOnClick;
	}

	public String getTarget() {
		return this.target_;
	}

	public void setTarget(String newTarget) {
		this.target_ = newTarget;
	}

	public boolean isApplication() {
		return this.isApplication_;
	}

	public void setIsApplication(boolean isApplication) {
		this.isApplication_ = isApplication;
	}

	public boolean isRemoveOnClick() {
		return this.isRemoveOnClick_;
	}

	public void setIsRemoveOnClick(boolean isRemoveOnClick) {
		this.isRemoveOnClick_ = isRemoveOnClick;
	}
}