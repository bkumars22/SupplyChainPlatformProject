/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.forecast.ui;

import com.scplatform.pcm.testing.UiTest;
import com.test.selenium.scplatform.cucumberRunners.ForecastWorkflowRunner;

/**
 * UITest wrapper for ForecastWorkflowRunner.
 * Annotated with @UiTest for tier-segregation under -P uitest.
 */
@UiTest
public class ForecastUiTest extends ForecastWorkflowRunner {
}
