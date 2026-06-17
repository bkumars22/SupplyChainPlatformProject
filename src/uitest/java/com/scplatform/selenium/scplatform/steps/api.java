/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
//import com.google.common.base.Verify;
import com.test.selenium.common.rest.fasterxml.JacksonRestImpl;

import io.cucumber.java.Scenario;
import io.cucumber.java.Before;
//import io.cucumber.java.en.And;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;

public class api extends JacksonRestImpl {

    @Before
    public void beforeMethod(Scenario scenario) {
        JLog.setScenarioForCucumber(scenario);
        JLog.resetErrorCount();
    }

    private void checkForErrors() {
        if (JLog.getErrorCount() > 0) {
            JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
        }
    }

    @Override
    public String getRestPath() {
        // TODO Auto-generated method stub
        return null;
    }

}
