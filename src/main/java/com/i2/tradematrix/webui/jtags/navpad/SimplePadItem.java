/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.navpad;

import com.scplatform.testing.webui.jtags.BaseClickableTagModel;
import com.scplatform.testing.webui.jtags.TagModel;
import java.util.ArrayList;
import java.util.List;

public class SimplePadItem extends BaseClickableTagModel implements PadItem {
	protected List children_;
	protected PadItem parent_;
	protected boolean isSelected_;

	public SimplePadItem() {
		this("", (String) null, (String) null, (String) null, (List) null);
	}

	public SimplePadItem(String id, String onclick, String label, String tooltip, List children) {
		super(id, onclick, label, (String) null, label);
		this.children_ = null;
		this.parent_ = null;
		this.isSelected_ = false;
		this.setTooltip(tooltip);
		this.children_ = children;
	}

	public PadItem getParent() {
		return this.parent_;
	}

	public void setParent(PadItem newParent) {
		if (newParent != this.parent_) {
			this.parent_ = newParent;
			if (newParent != null && newParent.get(this.getId()) == null) {
				newParent.add(this);
			}

		}
	}

	public boolean isLeaf() {
		if (this.children_ == null) {
			return true;
		} else {
			return this.children_.size() == 0;
		}
	}

	public boolean isSelected() {
		return this.isSelected_;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected_ = isSelected;
	}

	public void add(TagModel model) {
		if (this.children_ == null) {
			this.children_ = new ArrayList();
		}

		this.add(this.children_.size(), model);
	}

	public void add(int pos, TagModel model) {
		if (!(model instanceof PadItem)) {
			throw new IllegalArgumentException("Can only add PadItems to a PadItem");
		} else {
			PadItem newChild = (PadItem) model;
			PadItem oldParent = newChild.getParent();
			if (oldParent != this) {
				if (oldParent != null) {
					oldParent.remove(newChild);
				}

				if (this.children_ == null) {
					this.children_ = new ArrayList();
				}

				this.children_.add(pos, model);
				newChild.setParent(this);
			}
		}
	}

	public TagModel get(int pos) {
		return this.children_ == null ? null : (TagModel) this.children_.get(pos);
	}

	public TagModel get(String id) {
		int pos = this.findTagModel(id);
		return pos != -1 ? (TagModel) this.children_.get(pos) : null;
	}

	public void remove(int pos) {
		if (this.children_ != null) {
			PadItem padItem = (PadItem) this.children_.get(pos);
			if (padItem != null) {
				this.children_.remove(pos);
				padItem.setParent((PadItem) null);
			}
		}

	}

	public void remove(String id) {
		int pos = this.findTagModel(id);
		if (pos != -1) {
			this.remove(pos);
		}

	}

	public void remove(TagModel model) {
		this.remove(model.getId());
	}

	public int size() {
		return this.children_ == null ? 0 : this.children_.size();
	}

	protected int findTagModel(String id) {
		if (id != null && this.children_ != null) {
			for (int i = 0; i < this.children_.size(); ++i) {
				TagModel m = (TagModel) this.children_.get(i);
				if (m.getId() != null && m.getId().equals(id)) {
					return i;
				}
			}
		}

		return -1;
	}
}