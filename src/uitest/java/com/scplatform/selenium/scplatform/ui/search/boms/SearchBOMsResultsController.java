/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.boms;

import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.constants.JIRA;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.itemBOMAVL.ItemBOMAVL;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;

public class SearchBOMsResultsController extends SCPlatformSearchResultsController {
    protected SearchBOMsResultsPage page;

    public SearchBOMsResultsController() {
        super();
        page = new SearchBOMsResultsPage();
    }

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new SearchBOMsResultsPage();
        }
        return page;
    }

    public boolean validateItemBOMAVL(List<ItemBOMAVL> expectedResults) {
        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        for (ItemBOMAVL itemBOMAVL : expectedResults) {
            if (!itemBOMAVL.getItemIdentifier().contains(" BOM")) {
                continue;
            }

            JLog.section("ItemBOMAVL Validation - " + itemBOMAVL.getItemIdentifier());
            SearchBOMsResultsModel expected = convertItemBOMAVLToResults(itemBOMAVL);

            verified = validate(expected);
            success = (verified) ? success : verified;

        }
        return success;
    }

    public boolean validateItem(List<Item> expectedResults) {
        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        for (Item item : expectedResults) {
            if (!item.getItemIdentifier().contains(" BOM")) {
                continue;
            }

            JLog.section("Item Validation - " + item.getItemIdentifier());
            SearchBOMsResultsModel expected = convertItemToResults(item);

            verified = validate(expected);
            success = (verified) ? success : verified;

        }
        return success;
    }

    protected boolean validate(SearchBOMsResultsModel expected) {

        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        search(expected);

        List<SearchBOMsResultsModel> actualModel = page.parseResults();

        SearchBOMsResultsModel actual = findActual(actualModel, expected);
        if (actual == null) {
            JLog.error("Unable to find actual Item Number  " + expected.getItemNumber(), TakeScreenshot.True);
            return false;
        }

        verified = verify(actual.getDisplayName("itemNumber"), actual.getItemNumber(), expected.getItemNumber());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("businessName"), actual.getBusinessName(), expected.getBusinessName());
        success = (verified) ? success : verified;

        if (expected.getPlatform() != null) {
            verified = verify(actual.getDisplayName("platform"), actual.getPlatform(), expected.getPlatform());
            success = (verified) ? success : verified;
        }

        verified = verify(actual.getDisplayName("status"), actual.getStatus(), expected.getStatus());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("version"), actual.getVersion(), expected.getVersion());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("revision"), actual.getRevision(), expected.getRevision());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("bomDescription"), actual.getBomDescription(),
                expected.getBomDescription());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("topLevelItem"), actual.getTopLevelItem(), expected.getTopLevelItem());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("responsibility"), actual.getResponsibility(),
                expected.getResponsibility());
        success = (verified) ? success : verified;

        JLog.blankLine();
        return success;
    }

    protected void search(SearchBOMsResultsModel expectedModel) {
        SearchBOMsModel searchBOMsModel = new SearchBOMsModel();
        searchBOMsModel.setItemNumber(expectedModel.getItemNumber());

        SearchBOMsController searchBOMsController = new SearchBOMsController();
        searchBOMsController.setModel(searchBOMsModel);
        searchBOMsController.clickClear();
        searchBOMsController.search();
    }

    protected SearchBOMsResultsModel findActual(List<SearchBOMsResultsModel> actualModel,
            SearchBOMsResultsModel expected) {
        for (SearchBOMsResultsModel actual : actualModel) {
            if (actual.getItemNumber().equals(expected.getItemNumber())) {
                if (actual.getVersion().equals(expected.getVersion())) {
                    return actual;
                }
            }
        }
        return null;
    }

    protected SearchBOMsResultsModel convertItemBOMAVLToResults(ItemBOMAVL item) {
        SearchBOMsResultsModel model = new SearchBOMsResultsModel();

        model.setItemNumber(item.getItemIdentifier());
        model.setBusinessName(item.getBusinessEntity());
        model.setPlatform(null);
        model.setStatus("PENDING");
        model.setVersion(item.getVersion());
        model.setRevision(item.getRevision());
        model.setBomDescription(item.getDescription());
        model.setTopLevelItem((item.getBOMLevel().equals("1")) ? "Yes" : "No");
        model.setResponsibility(null);

        if (JIRA.SCPlatform1168) {
            model.setTopLevelItem("No");
        }

        return model;
    }

    protected SearchBOMsResultsModel convertItemToResults(Item item) {
        SearchBOMsResultsModel model = new SearchBOMsResultsModel();

        model.setItemNumber(item.getItemIdentifier());
        model.setBusinessName(item.getBusinessEntity());
        model.setPlatform(null);
        model.setStatus("APPROVED");
        model.setVersion(item.getVersion());
        model.setRevision(item.getRevision());
        model.setBomDescription(item.getDescription());
        model.setTopLevelItem((item.getIsTopLevel().equalsIgnoreCase("true")) ? "Yes" : "No");
        model.setResponsibility(null);

        if ((item.getItemPlatform() != null) && (!item.getItemPlatform().isEmpty())) {
            model.setPlatform(item.getItemPlatform().get(0).getPlatformName());
        }
        return model;
    }

}
