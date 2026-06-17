/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.jtags.wizard;

import com.scplatform.testing.webui.jtags.TagModel;

public interface Wizard extends TagModel {
	String getName();

	int getTotalStepCount();

	int getRequiredStepCount();

	int getCurrentStep();

	void setCurrentStep(int var1);

	Step get(int var1);

	Step get(String var1);

	void add(Step var1);

	void add(int var1, Step var2);

	void remove(String var1);

	void remove(Step var1);

	boolean isSummaryAvailable();

	void setSummaryAvailable(boolean var1);

	boolean isSummaryStep();

	void setSummaryStep(boolean var1);

	boolean isCancelStep();

	void setCancelStep(boolean var1);

	boolean isFinishStep();

	void setFinishStep(boolean var1);
}