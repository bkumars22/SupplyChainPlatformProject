/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.searchframework.dto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class DefaultExpressions {
	protected Map<String, String> expressions = new LinkedHashMap<>();

	public Map<String, String> getDefaultExprs() {
		return expressions;
	}

	public void setDefaultExpr(String name, String expr) {
		expressions.put(name, expr);
	}

	public String getDefaultExpr(String name) {
		return expressions.get(name);
	}

	public Set<String> getDefaultExpressionsNames() {
		return expressions.keySet();
	}

}
