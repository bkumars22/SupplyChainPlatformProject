/**
 * @DownloadApplicationLogs.java@
 *
 * Created on Jun 4, 2012
 *
 *      Copyright (c) 2010 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.autoGen;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.test.selenium.common.messageResources.GetApplicationLogs;
import com.test.selenium.scplatform.base.E2BaseTest;

/**
 * @author dgenrich
 *
 */
public class DownloadApplicationLogs extends E2BaseTest {

	@BeforeClass
	public void classSetup()	{
		this.start_noBrowser();
	}
	
	@Test (description="Download E2SC and E2NA Application Logs")
	public void getLogs ()	{
		GetApplicationLogs appLogs = new GetApplicationLogs();
		appLogs.downloadFromHost();
	}
	
}
