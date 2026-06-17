
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.base;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.testng.annotations.BeforeSuite;

import com.test.selenium.common.JLog;
import com.test.selenium.common.PartnersList;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.cucumber.CukeBaseTest;
import com.test.selenium.common.reporting.PublishResults;
import com.test.selenium.common.testng.BaseTest;
import com.test.selenium.common.Configuration;

public class E2BaseTest extends BaseTest {

    public static PartnersList partnersList;
    private String unique;

    @Override
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        super.beforeSuite();

        PublishResults.setNoStackInfoStatic(true);

        utils = new Utilities();
        utils.setup();

        PublishResults.setNoStackInfoStatic(true);

        utils = new Utilities();
        utils.setup();

        JLog.write("Start to initial data for WTG");

        unique = null;
        unique = DateTime.now().toString("yyMMddHHmmss");

        Configuration.setRuntime("model", "SCPlatform");
        Configuration.setRuntime("wtg_p_id", "54");
        Configuration.setRuntime("component_name", "SCPlatform");
        Configuration.setRuntime("execution_type", "UI");
        Configuration.setRuntime("BambooBuildNumber", System.getProperty("buildNumber"));
        Configuration.setRuntime("xmlSuite", System.getProperty("suite"));
        Configuration.setRuntime("test_run_id", unique);
        Configuration.setRuntime("release_version", "26.2");
    }

    protected void checkForErrors() {
        if (JLog.getErrorCount() > 0) {
            JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
        }
    }

    public void start_noBrowser() {
        utils = new Utilities();
        utils.setup();

        partnersList = new PartnersList(utils.getPartnerFile());
        doNotModifyPartners();
    }

    private void doNotModifyPartners() {
        ArrayList<String> partnersKeys = new ArrayList<String>();
        partnersKeys.add("NewBreadCarrier");
        partnersKeys.add("LTLFreightCarrier");
        partnersKeys.add("AppleCarrier");
        partnersKeys.add("RushOrderCarrier");
        partnersKeys.add("OceanLogistics");
        partnersList.setPartnerKeysNotToModify(partnersKeys);
    }
}
