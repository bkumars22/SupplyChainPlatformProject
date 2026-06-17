/*
 * @MenuTest.java@ Created on Oct 4, 2019
 *
 * Copyright (c) 2017 E2open, Inc. All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open The copyright notice above does not
 * evidence any actual or intended publication of such source code.
 *
 */
/**
 *
 */
package com.test.selenium.scplatform.base;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import com.test.selenium.scplatform.login.LoginSCPlatformHarmony;
import com.test.selenium.common.navigation.unity.MenuNotFoundException;
import com.test.selenium.common.navigation.unity.NavigationBuilder;
import com.test.selenium.common.steps.Users;

/**
 * @author dgenrich
 *
 */
public class MenuTest extends E2BaseTest {
	LoginSCPlatformHarmony mcm = new LoginSCPlatformHarmony();

  @BeforeClass
  public void classSetup () {
    //this.setup();
  }

  @AfterClass
  public void classTeardown () {
    browserSession.closeBrowser();
  }

//  @Test(description = "Build Menu")
  public void buildMenu () throws MenuNotFoundException {
    //loginSSP(COMUsers.bootstrap());
   mcm.login(Users.get("mtcmUser"));
    NavigationBuilder navBuilder =
        new NavigationBuilder("com.test.selenium.api.scplatform.navigation", "HarmonyMTCMNavigation");
    navBuilder.generate();
    mcm.logout();
  }
  //src\main\java\com\scplatform\selenium\scplatform\navigation\HarmonyMTCMNavigation.java

}
