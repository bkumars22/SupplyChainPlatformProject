/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.constants.JIRA;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.messages.forecast.ForecastUtils;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.ForecastDetailsController;

public class SearchForecastResultsController extends SCPlatformSearchResultsController {
    protected SearchForecastResultsPage page;
    protected boolean validatePastPITs = true;
    protected boolean validateCurrentPITs = true;
    protected boolean validateFuturePITs = true;

    public SearchForecastResultsController() {
        super();
        page = new SearchForecastResultsPage();
    }

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new SearchForecastResultsPage();
        }
        return page;
    }

    public void clickNext() {
        clickAndCheckForPOSTError(page.button_Next());
    }

    public boolean validate(List<Forecast> expectedResults, List<Item> itemData, boolean validateDetails) {

        boolean success = true;
        boolean verified = true;

        for (Forecast expected : expectedResults) {
            JLog.section("Verify " + expected.getItemIdentifier());

            ForecastUtils utils = new ForecastUtils();
            int currentForecast = utils.findByDate(expected, DateTime.now());
            int previousForecast = utils.getForecastPreviousMonth(expected, currentForecast);
            int nextForecast = utils.getForecastNextMonth(expected, currentForecast);

            String previousForecastlabel = (previousForecast >= 0)
                    ? utils.createUILabel(expected.getPointInTime().get(previousForecast))
                    : null;
            String currentForecastLabel = (currentForecast >= 0)
                    ? utils.createUILabel(expected.getPointInTime().get(currentForecast))
                    : null;
            String nextForecastLabel = (nextForecast >= 0)
                    ? utils.createUILabel(expected.getPointInTime().get(nextForecast))
                    : null;

            previousForecastlabel = (validatePastPITs) ? previousForecastlabel : null;
            currentForecastLabel = (validateCurrentPITs) ? currentForecastLabel : null;
            nextForecastLabel = (validateFuturePITs) ? nextForecastLabel : null;

            search(expected);

            List<SearchForecastResultsModel> actualModel = page.parseResults(previousForecastlabel,
                    currentForecastLabel, nextForecastLabel);

            SearchForecastResultsModel actual = findActual(actualModel, expected.getItemIdentifier(),
                    expected.getForecastModel());
            if (actual == null) {
                JLog.error(String.format("Unable to find Item '%s' with Forecast Model '%s'",
                        expected.getItemIdentifier(), expected.getForecastModel()), TakeScreenshot.True);
                success = false;  // ← MARK AS FAILURE
                continue;
            }

            Item item = getItem(itemData, expected.getItemIdentifier());

            verified = verify(actual.getDisplayName("itemNumber"), actual.getItemNumber(),
                    expected.getItemIdentifier());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("itemDescription"), actual.getItemDescription(),
                    item.getDescription());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("region"), actual.getRegion(), expected.getSite());
            success = (verified) ? success : verified;

            // Where is MemberOfGroup mapped from?
            // verified = verify(actual.getDisplayName("memberOfGroup"),
            // actual.getMemberOfGroup(), expected.getSite());
            // success = (verified) ? success : verified;

            if (!JIRA.SCPlatform1167) {
                verified = verify(actual.getDisplayName("commodityName"), actual.getCommodityName(),
                        item.getCommodityCode());
                success = (verified) ? success : verified;

                if ((item.getItemPlatform() != null) && (!item.getItemPlatform().isEmpty())) {
                    verified = verify(actual.getDisplayName("platform"), actual.getPlatform(),
                            item.getItemPlatform().get(0).getPlatformName());
                    success = (verified) ? success : verified;
                }

                verified = verify(actual.getDisplayName("productFamily"), actual.getProductFamily(),
                        item.getProprietaryProductFamily());
                success = (verified) ? success : verified;
            }

            verified = verify(actual.getDisplayName("classification"), actual.getClassification(),
                    item.getItemClassification());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("forecastModel"), actual.getForecastModel().toUpperCase(),
                    expected.getForecastModel().toUpperCase());
            success = (verified) ? success : verified;

            String status = (expected.getForecastModel().toUpperCase().equals("CURRENT")) ? "APPROVED" : "PENDING";
            verified = verify(actual.getDisplayName("status"), actual.getStatus(), status);
            success = (verified) ? success : verified;

            // Where is ExtendForecastTerm mapped from?
            // verified = verify(actual.getDisplayName("extendForecastTerm"),
            // actual.getExtendForecastTerm(), expected.gete);
            // success = (verified) ? success : verified;

            String lastChangeBy = (StringUtils.isNotBlank(expected.getLastChangeBy())) ? expected.getLastChangeBy()
                    : "BATCH";
            verified = verify(actual.getDisplayName("lastChangeBy"), actual.getLastChangeBy(), lastChangeBy);
            success = (verified) ? success : verified;

            if ((previousForecastlabel != null) && (actual.pitBucketContains(previousForecastlabel))) {
                verified = verify(previousForecastlabel, actual.getPitBuckets(previousForecastlabel),
                        utils.calculatePITValue(expected.getPointInTime().get(previousForecast)),
                        utils.getPitCalculationMessage());
                success = (verified) ? success : verified;
            } else {
                if (previousForecastlabel != null) {
                    // if this is set, then we are expecting the data
                    success = false;
                    JLog.error("Unable to find actual data for label: " + previousForecastlabel, TakeScreenshot.True);
                }

            }

            if ((currentForecastLabel != null) && (actual.pitBucketContains(currentForecastLabel))) {
                verified = verify(currentForecastLabel, actual.getPitBuckets(currentForecastLabel),
                        utils.calculatePITValue(expected.getPointInTime().get(currentForecast)),
                        utils.getPitCalculationMessage());
                success = (verified) ? success : verified;
            } else {
                if (currentForecastLabel != null) {
                    // if this is set, then we are expecting the data
                    success = false;
                    JLog.error("Unable to find actual data for label: " + currentForecastLabel, TakeScreenshot.True);
                }
            }

            if ((nextForecastLabel != null) && (actual.pitBucketContains(nextForecastLabel))) {
                verified = verify(nextForecastLabel, actual.getPitBuckets(nextForecastLabel),
                        utils.calculatePITValue(expected.getPointInTime().get(nextForecast)),
                        utils.getPitCalculationMessage());
                success = (verified) ? success : verified;
            } else {
                if (nextForecastLabel != null) {
                    // if this is set, then we are expecting the data
                    success = false;
                    JLog.error("Unable to find actual data for label: " + nextForecastLabel, TakeScreenshot.True);
                }
            }

            // Where is Responsibility mapped from?
            // verified = verify(actual.getDisplayName("responsibility"),
            // actual.getResponsibility(), expected.getre));
            // success = (verified) ? success : verified;

            if (validateDetails) {
                check(expected.getItemIdentifier());
                clickNext();

                ForecastDetailsController forecastDetailsController = new ForecastDetailsController();
                verified = forecastDetailsController.validate(expected, item);
                success = (verified) ? success : verified;

                forecastDetailsController.clickBack();
            }
        }
        return success;
    }

    public void check(String text) {
        check(text, page.tableLocator());
    }

    private SearchForecastResultsModel findActual(List<SearchForecastResultsModel> actualModel, String itemIdentifier,
            String forecastModel) {

        for (SearchForecastResultsModel model : actualModel) {
            if (model.getItemNumber().equals(itemIdentifier)) {
                if (model.getForecastModel().equalsIgnoreCase(forecastModel)) {
                    return model;
                }

            }
        }
        return null;
    }

    protected void search(Forecast expectedModel) {
        SearchForecastModel searchForecastModel = new SearchForecastModel();
        searchForecastModel.setItemNumber(expectedModel.getItemIdentifier());

        SearchForecastController searchForecastController = new SearchForecastController();
        searchForecastController.clickClear();
        searchForecastController.setModel(searchForecastModel);
        searchForecastController.search();
    }

    protected Item getItem(List<Item> itemData, String itemName) {

        for (int row = 0; row < itemData.size(); row++) {
            if (itemData.get(row).getItemIdentifier().equals(itemName)) {
                return itemData.get(row);
            }
        }
        return null;
    }

    /**
     * @param validatePastPITs
     *            the validatePastPITs to set
     */
    public void setValidatePastPITs(boolean validatePastPITs) {
        this.validatePastPITs = validatePastPITs;
    }

    /**
     * @param validateCurrentPITs
     *            the validateCurrentPITs to set
     */
    public void setValidateCurrentPITs(boolean validateCurrentPITs) {
        this.validateCurrentPITs = validateCurrentPITs;
    }

    /**
     * @param validateFuturePITs
     *            the validateFuturePITs to set
     */
    public void setValidateFuturePITs(boolean validateFuturePITs) {
        this.validateFuturePITs = validateFuturePITs;
    }

}
