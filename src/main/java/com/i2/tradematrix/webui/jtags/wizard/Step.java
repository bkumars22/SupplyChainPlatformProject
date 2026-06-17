/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.wizard;

import java.io.Serializable;

public interface Step extends Serializable {
	String getName();

	boolean isRequired();
}