/**
 * @BuildTestNGSuites.java@
 *
 * Created on Mar 18, 2011
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

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.ProjectUtilities;
import com.test.selenium.common.RealTime;
import com.test.selenium.common.testng.GenerateTestNGSuite;
import com.test.selenium.scplatform.base.Utilities;






/**
 * @author  David Genrich
 *
 */
public class BuildTestNGSuites {

	public static void main(String[] args) {
	    ProjectUtilities utilities = new Utilities();
	    utilities.setup();
	    
		List<String> runGroups = new ArrayList<String>();
		// comment out these lines to create the default suite
//		runGroups.add("smoketest");
////		runGroups.add("US1794");
//		suiteName = "_DoNotCommitSuite";
		
		RealTime runtime = RealTime.getInstance();

		String sourceDir = runtime.getProjectDir() + "csp_CollaborativeSupplyPlanning\\SIT\\Suite\\";
		String outputDir = runtime.getProjectDir() + "Suite\\";
		
		String[] suiteFilesToProcess = new String[1];
		suiteFilesToProcess[0] = sourceDir + "_MasterTestSuite.xml";
		
		GenerateTestNGSuite generateTestNG = new GenerateTestNGSuite();
		//generateTestNG.setSuiteName("COM_SIT: Collaborative Order Management (COM) - SIT Test Suite");
		

		generateTestNG.process(suiteFilesToProcess, outputDir, BuildTestNGSuites.class.getName(), runGroups);
	}
}
