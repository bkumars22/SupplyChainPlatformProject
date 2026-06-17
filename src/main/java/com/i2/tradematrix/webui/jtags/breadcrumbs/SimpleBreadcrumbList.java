/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.breadcrumbs;

import com.scplatform.testing.webui.jtags.BaseTagModel;
import com.scplatform.testing.webui.jtags.TagModel;
import java.util.ArrayList;
import java.util.List;

public class SimpleBreadcrumbList extends BaseTagModel implements BreadcrumbList {
	protected String appName_;
	protected String target_;
	protected boolean autoinit_;
	protected int currentTrailSize_;
	protected List breadcrumbs_;

	public SimpleBreadcrumbList() {
		this("", (String) null, (List) null);
	}

	public SimpleBreadcrumbList(String appName, String target, List breadcrumbs) {
		super(appName, false, true);
		if (appName == null) {
			throw new IllegalArgumentException("Application name cannot be null.");
		} else {
			this.appName_ = appName;
			if (breadcrumbs != null) {
				this.breadcrumbs_ = breadcrumbs;
			} else {
				this.breadcrumbs_ = new ArrayList();
			}

			this.target_ = target;
			this.autoinit_ = true;
		}
	}

	public String getApplicationName() {
		return this.appName_;
	}

	public void setApplicationName(String appName) {
		this.appName_ = appName;
	}

	public String getTarget() {
		return this.target_;
	}

	public void setTarget(String target) {
		this.target_ = target;
	}

	public void setAutoinit(boolean autoinit) {
		this.autoinit_ = autoinit;
	}

	public boolean getAutoinit() {
		return this.autoinit_;
	}

	public TagModel get(int index) {
		return (TagModel) this.breadcrumbs_.get(index);
	}

	public TagModel get(String id) {
		int pos = this.findBreadcrumb(id);
		return pos < 0 ? null : (TagModel) this.breadcrumbs_.get(pos);
	}

	public void add(TagModel model) {
		if (model instanceof Breadcrumb) {
			int pos = this.findBreadcrumb(model.getId());
			if (pos < 0) {
				Breadcrumb crumb = (Breadcrumb) model;
				crumb.setTarget(this.target_);
				this.breadcrumbs_.add(model);
				this.currentTrailSize_ = this.breadcrumbs_.size();
			} else {
				this.currentTrailSize_ = pos + 1;
			}

		}
	}

	public void add(int pos, TagModel model) {
		if (model instanceof Breadcrumb) {
			Breadcrumb crumb = (Breadcrumb) model;
			crumb.setTarget(this.target_);
			this.breadcrumbs_.add(pos, model);
			this.currentTrailSize_ = pos + 1;
		}
	}

	public void remove(int index) {
		if (index > this.currentTrailSize_) {
			this.breadcrumbs_.remove(index);
		} else {
			this.breadcrumbs_.remove(index);
			--this.currentTrailSize_;
		}

	}

	public void remove(String id) {
		int pos = this.findBreadcrumb(id);
		this.remove(pos);
	}

	public void remove(TagModel model) {
		int pos = this.findBreadcrumb(model.getId());
		this.remove(pos);
	}

	public int size() {
		return this.currentTrailSize_;
	}

	public String toString() {
		return "Breadcrumbs: " + this.appName_ + " : " + this.size();
	}

	protected int findBreadcrumb(String id) {
		if (id == null) {
			throw new IllegalArgumentException("Id cannot be null");
		} else {
			for (int i = 0; i < this.breadcrumbs_.size(); ++i) {
				Breadcrumb crumb = (Breadcrumb) this.breadcrumbs_.get(i);
				if (id.equals(crumb.getId())) {
					return i;
				}
			}

			return -1;
		}
	}
}