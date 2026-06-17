/**
 * @GenerateReport.java@
 *
 * Created on Jan 20, 2011
 *
 *      Copyright (c) 2010 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.base;

import com.test.selenium.common.BrowserManager;
import com.test.selenium.common.Build;
import com.test.selenium.common.Configuration;
import com.test.selenium.common.Prop;
import com.test.selenium.common.RealTime;
import com.test.selenium.common.reporting.PublishResults;

/**
 * To be ran after the test suite, this will publish
 * the report to the automation web site and send it
 * to the email addresses defined in the stack properties
 * file under the key "email.report".
 *
 * @author David Genrich
 *
 */
public class GenerateReport {


  public static void main(String[] args) {
    Utilities utils = new Utilities ();
    utils.setup();


    GenerateReport generateReport = new GenerateReport();
    generateReport.getBuildInfo ();

    Configuration.setRuntime("model", "newProduct");

    Prop prop = Prop.getInstance();
    RealTime runtime = RealTime.getInstance();

    if (prop.get().containsKey("email.report"))	{
      String recipients = prop.get().getProperty("email.report");
      // recipients = recipients + ",dgenrich@scplatform.local";
      if (recipients.contains("@"))	{
        PublishResults results = new PublishResults ();
        results.publish(runtime.get().getProperty("testng.suite"), recipients);
      }
    }
    BrowserManager browserSession = BrowserManager.INSTANCE;
    browserSession.closeAll();

  }

  private void getBuildInfo (){
    Prop prop = Prop.getInstance();
    boolean updated = false;
    if (!prop.get().containsKey("build.1")){
      prop.get().setProperty("build.1", "ssp-ext");
      updated = true;
    }
    if (!prop.get().containsKey("build.2")){
      prop.get().setProperty("build.2", "e2sc");
      updated = true;
    }
    if (!prop.get().containsKey("build.3")){
      prop.get().setProperty("build.3", "e2na");
      updated = true;
    }
    if (updated)	{
      prop.save();
    }


    Build build = new Build();
    build.getpkgB2B();
    build.setBuildsInRuntime();

    build.getModel();

  }


}