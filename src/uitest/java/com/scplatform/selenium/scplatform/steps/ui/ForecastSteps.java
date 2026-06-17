/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps.ui;

import java.util.List;
import java.util.Map;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.navigation.SCPlatformNavigation;
import com.test.selenium.scplatform.ui.forecast.searchForecast.SearchForecastResultsController;
import com.test.selenium.scplatform.utilities.MessageIO;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import io.cucumber.java.Scenario;
import io.cucumber.datatable.DataTable;

public class ForecastSteps {

    protected SCPlatformNavigation nav;

    @Before
    public void beforeMethod(Scenario scenario) {
        JLog.setScenarioForCucumber(scenario);
        JLog.resetErrorCount();
        nav = new SCPlatformNavigation();
    }

    private void checkForErrors() {
        if (JLog.getErrorCount() > 0) {
            JLog.fail(JLog.getErrorCount() + " errors occured in the test.  Check log.", TakeScreenshot.True);
        }
    }

    /**
     * DOCUMENTATION http://confluence.dev.scplatform.local/display/QA/Search+Forecast
     */
    @Given("I validate the {string} Forecast Data and {string} Item data")
    public void validateForecast(String forecastSaveKey, String itemSaveKey) {
        MessageIO<Forecast> messageIOForecast = new MessageIO<Forecast>(Forecast.class);
        List<Forecast> forecastData = messageIOForecast.load(forecastSaveKey);

        MessageIO<Item> messageIOItem = new MessageIO<Item>(Item.class);
        List<Item> itemData = messageIOItem.load(itemSaveKey);

        nav.SearchForecast();

        SearchForecastResultsController c = new SearchForecastResultsController();
        c.validate(forecastData, itemData, true);

        checkForErrors();
    }

    /**
     * DOCUMENTATION http://confluence.dev.scplatform.local/display/QA/Search+Forecast
     */
//    @Given("I validate the Forecast Data with parameters")
//    public void validateForecastWithParameters(DataTable parameters) {
//        String forecastSaveKey = null;
//        String itemSaveKey = null;
//        boolean validatePastPITs = true;
//        boolean validateCurrentPITs = true;
//        boolean validateFuturePITs = true;
//
//        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
//            forecastSaveKey = row.get("forecastSaveKey");
//            itemSaveKey = row.get("itemSaveKey");
//
//            if (row.containsKey("validatePastPITs")) {
//                validatePastPITs = row.get("validatePastPITs").equalsIgnoreCase("true");
//            }
//            if (row.containsKey("validateCurrentPITs")) {
//                validatePastPITs = row.get("validateCurrentPITs").equalsIgnoreCase("true");
//            }
//            if (row.containsKey("validateFuturePITs")) {
//                validatePastPITs = row.get("validateFuturePITs").equalsIgnoreCase("true");
//            }
//            break; // only doing 1 row
//        }
//
//        MessageIO<Forecast> messageIOForecast = new MessageIO<Forecast>(Forecast.class);
//        List<Forecast> forecastData = messageIOForecast.load(forecastSaveKey);
//
//        MessageIO<Item> messageIOItem = new MessageIO<Item>(Item.class);
//        List<Item> itemData = messageIOItem.load(itemSaveKey);
//
//        nav.SearchForecast();
//
//        SearchForecastResultsController c = new SearchForecastResultsController();
//        c.setValidateCurrentPITs(validateCurrentPITs);
//        c.setValidateFuturePITs(validateFuturePITs);
//        c.setValidatePastPITs(validatePastPITs);
//        c.validate(forecastData, itemData, true);
//
//        checkForErrors();
//    }

}
