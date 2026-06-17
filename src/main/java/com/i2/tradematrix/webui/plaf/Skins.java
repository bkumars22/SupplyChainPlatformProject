/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.plaf;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class Skins implements Serializable {
	protected HashMap skinMap_;
	protected String defaultSkinName_;

	public Skins() {
		this.defaultSkinName_ = "";
		this.skinMap_ = new HashMap();
	}

	public Skins(Skin[] skinList) {
		this();

		for (int i = 0; i < skinList.length; ++i) {
			this.skinMap_.put(skinList[i].getName(), skinList[i]);
		}

	}

	public void clear() {
		this.skinMap_.clear();
	}

	public int size() {
		return this.skinMap_.size();
	}

	public void setDefaultSkin(String skinName) {
		this.defaultSkinName_ = skinName;
	}

	public Skin getDefaultSkin() {
		return this.getSkin(this.defaultSkinName_);
	}

	public void addSkin(Skin aSkin) {
		this.skinMap_.put(aSkin.getName(), aSkin);
	}

	public void removeSkin(String skinName) {
		this.skinMap_.remove(skinName);
	}

	public Skin getSkin(String skinName) {
		return (Skin) this.skinMap_.get(skinName);
	}

	public Collection values() {
		return this.skinMap_.values();
	}
}