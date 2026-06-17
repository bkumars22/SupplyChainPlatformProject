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
import com.test.selenium.common.login.AbstractLogin;
import com.test.selenium.common.steps.Users;
import com.test.selenium.common.users.User;
import com.test.selenium.scplatform.login.LoginSCPlatform;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;

public class LoginUI {

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

  private static User currentLoggedInUser;
  private static AbstractLogin lastLoginObj;

  public static User getCurrentLogggedInUser() {
    return currentLoggedInUser;
  }

  @Given("I log into MTCM as {string}")
  public void login_mtcm(String userKey) throws Throwable {
    currentLoggedInUser = Users.get(userKey);
    LoginSCPlatform login = new LoginSCPlatform();
    login.login(currentLoggedInUser);
    lastLoginObj = login;
    checkForErrors();
  }

  @And("I log out of MTCM")
  public void logout_mtcm() throws Throwable {
    currentLoggedInUser = null;
    if (!genericLogout()) {
      LoginSCPlatform login = new LoginSCPlatform();
      login.logout();
    }
    checkForErrors();
  }

  private boolean genericLogout() {
    boolean success = false;
    if (lastLoginObj != null) {
      lastLoginObj.logout();
      success = true;
    }
    return success;
  }

}
