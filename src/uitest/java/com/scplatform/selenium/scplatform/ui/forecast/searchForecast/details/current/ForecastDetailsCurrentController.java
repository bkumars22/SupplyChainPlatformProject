/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast.details.current;

import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.messages.forecast.ForecastUtils;
import com.test.selenium.scplatform.messages.forecast.subClasses.PointInTime;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.ForecastDetailsController;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.current.ForecastDetailsCurrentModel;

public class ForecastDetailsCurrentController extends ForecastDetailsController {

	private ForecastDetailsCurrentPage page;

	public ForecastDetailsCurrentController() {
		super();
		page = new ForecastDetailsCurrentPage();
	}

	@Override
	public PageImpl getView() {
		return new ForecastDetailsCurrentPage();
	}

	public void clickAutoPopulate() {
		clickAndCheckForPOSTError(page.button_AutoPopulate());
	}

	public void clickClose() {
		clickAndCheckForPOSTError(page.button_Close());
	}

	public void clickCopy() {
		clickAndCheckForPOSTError(page.button_Copy());
	}

	public void clickDelete() {
		clickAndCheckForPOSTError(page.button_Delete());
	}

	public void clickFind() {
		clickAndCheckForPOSTError(page.button_Find());
	}

	public void clickShowHistory() {
		clickAndCheckForPOSTError(page.button_ShowHistory());
	}

	public void clickUndo() {
		clickAndCheckForPOSTError(page.button_Undo());
	}

	public boolean validate(Forecast expected, Item item) {

		JLog.section("Verify Current Forecast Line Details - " + expected.getItemIdentifier());

		boolean success = true;
		boolean verified = true;

		List<ForecastDetailsCurrentModel> actualList = page.parseResults();
		// will this ever be more than one row?
		ForecastDetailsCurrentModel actual = actualList.get(0);

		verified = verify(actual.getDisplayName("itemNumber"), actual.getItemNumber(), expected.getItemIdentifier());
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("region"), actual.getRegion(), expected.getSite());
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("status"), actual.getStatus(), "APPROVED");
		success = (verified) ? success : verified;

		verified = verify(actual.getDisplayName("commodity"), actual.getCommodity(), item.getCommodityCode());
		success = (verified) ? success : verified;

		// Where is MemberOfGroup mapped from?
		// verified = verify(actual.getDisplayName("memberOfGroup"),
		// actual.getMemberOfGroup(), expected.getSite());
		// success = (verified) ? success : verified;

		// Where is Responsibility mapped from?
		// verified = verify(actual.getDisplayName("responsibility"),
		// actual.getResponsibility(), expected.getre));
		// success = (verified) ? success : verified;

		// Where is RolloverExpiresOn mapped from?
		// verified = verify(actual.getDisplayName("rolloverExpiresOn"),
		// actual.getRolloverExpiresOn(), expected.getSite());
		// success = (verified) ? success : verified;

		// Where is ExtendForecastTerm mapped from?
		// verified = verify(actual.getDisplayName("extendForecastTerm"),
		// actual.getExtendForecastTerm(), expected.gete);
		// success = (verified) ? success : verified;

		ForecastUtils utils = new ForecastUtils();
		String label;
		boolean pitFound = false;
		List<String> labelList = new ArrayList<String>();
		for (PointInTime pit : expected.getPointInTime()) {
			label = utils.createUILabel(pit);
			labelList.add(label);
			if (actual.doesPitBucketExist(label)) {
				pitFound = true;

				verified = verify(label, actual.getPitBuckets(label), utils.calculatePITValue(pit),
						utils.getPitCalculationMessage());
				success = (verified) ? success : verified;

			}
		}
		if (!pitFound) {
			JLog.error("No PIT Buckets where found.  Looking for the following buckets: " + labelList.toString(),
					TakeScreenshot.True);
			success = false;
		}

		return success;
	}

}
