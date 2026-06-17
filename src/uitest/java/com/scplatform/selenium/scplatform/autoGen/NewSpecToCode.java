/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.autoGen;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.scplatform.qa.iris.codegeneration.IrisGen;
import com.scplatform.qa.iris.serialization.api.GenerationException;

/**
 * 
 * @author dgenrich
 * 
 *         See also:
 *         http://confluence.dev.scplatform.local/display/QA/Generating+a+new+message+type
 * 
 */
public class NewSpecToCode {

	/**
	 * specFile - File object of the spec excel file. This must be a CDM style Excel
	 * spreadsheet.
	 */
	File specFile; // defined in classSetup()

	/**
	 * sheetName - this is the sheet name of the main CDM mapping specs
	 */
	String sheetName = "ItemData";

	/**
	 * rowLabelStartsOn - the row that the labels are on
	 */
	int rowLabelStartsOn = 1;

	/**
	 * newClassName - the name for the new class. Typically, this is the same as the
	 * sheetName, but can be different.
	 * Set to the message name that the project uses, typically the name that is in
	 * E2NA transaction page.
	 */
	String newClassName = "ItemData";

	/**
	 * newPackageName - this is the location the newClassName will be created.
	 * Typically this will be in your project API code
	 * under messages, then under a package named for the message type. Other
	 * classes will be added later.
	 */
	String newPackageName = "com.test.selenium.scplatform.messages.item.parser";

	@BeforeClass
	public void classSetup() throws URISyntaxException {
		specFile = new File(getResourceFile("ItemData.xlsx"));
	}

	@Test
	public void parseSpec() throws GenerationException {
		List<String> params = new ArrayList<String>();

		params.add("-f");
		params.add(specFile.toString());

		params.add("-s");
		params.add(sheetName);

		params.add("-g");
		params.add(getMainSrcDirectory());

		params.add("-r");
		params.add(Integer.toString(rowLabelStartsOn));

		params.add("-c");
		params.add(newClassName);

		params.add("-p");
		params.add(newPackageName);

		// params.add("-b");
		// params.add("generate");

		String[] paramArray = params.toArray(new String[params.size()]);

		IrisGen.main(paramArray);
	}

	protected String getResourceFile(String file) throws URISyntaxException {
		URL resUrl = this.getClass().getClassLoader().getResource("spec/" + file);
		return resUrl.toURI().getPath();
	}

	protected String getMainSrcDirectory() {
		return new File("").getAbsolutePath() + "\\src\\main\\java\\";
	}

}
