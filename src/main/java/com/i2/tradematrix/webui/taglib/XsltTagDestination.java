/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.OutputStream;
import java.io.Writer;

class XsltTagDestination {
	private Writer writer;

	XsltTagDestination(Writer x) {
		this.writer = x;
	}

	public String getEncoding() {
		return null;
	}

	public OutputStream getOutputStream(String x, String y) {
		return null;
	}

	public Writer getWriter(String x, String y) {
		return this.writer;
	}

	public boolean keepOpen() {
		return false;
	}

	public XsltTagDestination resolve(String x) {
		return null;
	}
}