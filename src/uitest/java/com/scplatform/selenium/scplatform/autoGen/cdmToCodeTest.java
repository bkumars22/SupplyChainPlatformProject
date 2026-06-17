/**
 * @cdmToCodeTest.java@
 *
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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.context.ApplicationContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.scplatform.qa.e2Messages.poCM.PoCM;
import com.scplatform.qa.iris.codegeneration.HubMessageCodeGenerator;
import com.scplatform.qa.iris.codegeneration.SpringContextHolder;
import com.scplatform.qa.iris.model.MessageLineStructure;
import com.scplatform.qa.iris.model.proxy.MessageLineProxy;
import com.scplatform.qa.iris.serialization.api.GenerationException;
import com.scplatform.qa.iris.specparser.ExcelSpecParser;

/**
 * 
 * Set message class type in <T extends messageType>.
 * That is, if you are extending a {@link PoCM} message type, the signature of
 * this class should be:<br>
 * <blockquote><code>
 * 	public class cdmToCodeTest<T extends PoCM> {
 * </code></blockquote>
 * Set all class variables as needed.
 * Run this class as a "TestNG Test"
 * 
 * @author dgenrich
 * 
 *         See also:
 *         http://confluence.dev.scplatform.local/display/QA/Generating+a+new+message+type
 *
 */
public class cdmToCodeTest<T extends PoCM> {

	/**
	 * specFile - File object of the spec excel file. This must be a CDM style Excel
	 * spreadsheet.
	 */
	File specFile; // defined in classSetup()

	/**
	 * sheetName - this is the sheet name of the main CDM mapping specs
	 */
	String sheetName = "SOCreation-SO Commit";

	/**
	 * rowLabelStartsOn - the row that the labels are on, typically this is row 3
	 */
	int rowLabelStartsOn = 3;

	/**
	 * existingClassToExtend - this is the existing class to extend. Will be the
	 * same type as what this test class is extending
	 */
	Class<PoCM> existingClassToExtend = PoCM.class;

	/**
	 * newClassName - the name for the new class. Typically, this is the same as the
	 * sheetName, but can be different.
	 * Set to the message name that the project uses, typically the name that is in
	 * E2NA transaction page.
	 */
	String newClassName = "SOCreation";

	/**
	 * newPackageName - this is the location the newClassName will be created.
	 * Typically this will be in your project API code
	 * under messages, then under a package named for the message type. Other
	 * classes will be added later.
	 */
	String newPackageName = "com.test.selenium.api.ssp.messages.soCreationAndCommit";

	/**
	 * endAfterRow - the excel row number to stop processing after. The row number
	 * listed is processed, but nothing after that row.
	 * Can set to 9999 to have the code automatically determine the ending line.
	 */
	int endAfterRow = 357;

	@BeforeClass
	public void classSetup() throws URISyntaxException {
		specFile = new File(getResourceFile("WF_SO.xlsx"));
	}

	@AfterClass
	public void tearDown() throws Exception {
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createExtendedMessage() throws URISyntaxException, GenerationException {

		// **** PS Spec to parse ***
		ExcelSpecParser specParser = new ExcelSpecParser(specFile, sheetName, rowLabelStartsOn, endAfterRow);

		// **** SSP CDM message line structure ***
		MessageLineStructure<T> parentMLS = (MessageLineStructure<T>) MessageLineProxy
				.newInstance(existingClassToExtend).getMessageStructure();

		// **** Create Child class ***
		System.setProperty("codegen.output.directory", getMainSrcDirectory());
		ApplicationContext context = SpringContextHolder.INSTANCE.getContext();
		HubMessageCodeGenerator generator = (HubMessageCodeGenerator) context.getBean(
				"hubMessageLineInterfaceGenerator",
				parentMLS,
				specParser.getMessageLineStructure());
		generator.setMessageClassName(newClassName);
		generator.setPackageName(newPackageName);
		File hubMessage = generator.generate();

		System.out.println();
		System.out.println("New Class Created: " + hubMessage.toString());
	}

	protected String getResourceFile(String file) throws URISyntaxException {
		URL resUrl = this.getClass().getClassLoader().getResource("spec/" + file);
		return resUrl.toURI().getPath();
	}

	protected String getMainSrcDirectory() {
		return new File("").getAbsolutePath() + "\\src\\main\\java\\";
	}

}
