/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

import java.util.ArrayList;
import java.util.List;

public class SimpleTagModelList extends BaseTagModel implements TagModelList {
	List tagModels_;

	public SimpleTagModelList(String id, boolean isDisabled, boolean isVisible, List tagModels) {
		super(id, isDisabled, isVisible);
		this.tagModels_ = tagModels;
		if (this.tagModels_ == null) {
			this.tagModels_ = new ArrayList();
		}

	}

	public TagModel getTagModel(int index) {
		return (TagModel) this.tagModels_.get(index);
	}

	public void add(TagModel model) {
		this.tagModels_.add(model);
	}

	public void add(int position, TagModel model) {
		this.tagModels_.add(position, model);
	}

	public TagModel get(int index) {
		return (TagModel) this.tagModels_.get(index);
	}

	public TagModel get(String id) {
		int pos = this.findTagModel(id);
		if (pos < 0) {
			return null;
		} else {
			TagModel model = (TagModel) this.tagModels_.get(pos);
			return model;
		}
	}

	public void remove(int position) {
		this.tagModels_.remove(position);
	}

	public void remove(String id) {
		int pos = this.findTagModel(id);
		if (pos >= 0) {
			this.tagModels_.remove(pos);
		}
	}

	public void remove(TagModel model) {
		int pos = this.findTagModel(model.getId());
		if (pos >= 0) {
			this.tagModels_.remove(pos);
		}
	}

	public int size() {
		return this.tagModels_.size();
	}

	protected int findTagModel(String id) {
		if (id == null) {
			throw new IllegalArgumentException("Id cannot be null");
		} else {
			for (int i = 0; i < this.tagModels_.size(); ++i) {
				TagModel model = (TagModel) this.tagModels_.get(i);
				if (id.equals(model.getId())) {
					return i;
				}
			}

			return -1;
		}
	}
}