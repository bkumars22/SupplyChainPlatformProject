/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.navpad;

import com.scplatform.testing.webui.jtags.ClickableTagModel;
import com.scplatform.testing.webui.jtags.TagModelList;

public interface PadItem extends ClickableTagModel, TagModelList {
	PadItem getParent();

	void setParent(PadItem var1);

	boolean isLeaf();

	boolean isSelected();

	void setIsSelected(boolean var1);
}