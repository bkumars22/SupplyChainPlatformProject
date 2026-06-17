/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags;

public interface TagModelList extends TagModel {
	void add(TagModel var1);

	void add(int var1, TagModel var2);

	TagModel get(int var1);

	TagModel get(String var1);

	void remove(int var1);

	void remove(String var1);

	void remove(TagModel var1);

	int size();
}