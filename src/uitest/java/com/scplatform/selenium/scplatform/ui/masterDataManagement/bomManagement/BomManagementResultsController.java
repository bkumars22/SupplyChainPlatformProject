/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.bomManagement;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.itemAVL.ItemAVL;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.test.selenium.scplatform.ui.dialogs.itemDetails.ItemDetailsController;

public class BomManagementResultsController extends SCPlatformSearchResultsController {
    protected BomManagementResultsPage page;
    protected List<String> itemProcessed;
    protected String expectedStatus = "APPROVED";

    public BomManagementResultsController() {
        super();
        page = new BomManagementResultsPage();
        itemProcessed = new ArrayList<String>();
    }

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new BomManagementResultsPage();
        }
        return page;
    }

    public void setExpectedStatus(String status) {
        this.expectedStatus = status;
    }

    public boolean validateItemAVL(List<ItemAVL> expectedResults) {
        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        for (ItemAVL item : expectedResults) {
            if (isItemProcessed(item.getItemIdentifier()))
                continue;

            BomManagementResultsModel expected = convertItemAVLToResults(item);
            verified = validate(expected);
            success = (verified) ? success : verified;
        }
        return success;
    }

    public boolean validateItemAVLDetails(List<ItemAVL> expectedResults) {
        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        for (ItemAVL item : expectedResults) {
            if (isItemProcessed(item.getItemIdentifier()))
                continue;

            search(item.getItemIdentifier());

            ItemDetailsController c = new ItemDetailsController();
            verified = c.validate(item);
            success = (verified) ? success : verified;
        }
        return success;
    }

    public boolean validateItem(List<Item> expectedResults) {
        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        for (Item item : expectedResults) {
            if (!item.getItemIdentifier().contains(" BOM"))
                continue;

            if (isItemProcessed(item.getItemIdentifier()))
                continue;

            BomManagementResultsModel expected = convertItemToResults(item);
            verified = validate(expected);
            success = (verified) ? success : verified;
        }
        return success;
    }

    public boolean validateItemDetails(List<Item> expectedResults) {
        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        for (Item item : expectedResults) {
            if (!item.getItemIdentifier().contains(" BOM"))
                continue;

            if ("true".equalsIgnoreCase(item.getIsTopLevel()))
                continue;

            if (isItemProcessed(item.getItemIdentifier()))
                continue;

            search(item.getItemIdentifier());

            ItemDetailsController c = new ItemDetailsController();
            verified = c.validate(item);
            success = (verified) ? success : verified;
        }
        return success;
    }

    protected boolean validate(BomManagementResultsModel expected) {
        boolean success = true;
        boolean verified = true;

        JLog.section("Verify BOM Management - " + expected.getItemNumber());
        search(expected);

        List<BomManagementResultsModel> actualModel = page.parseResults();

        BomManagementResultsModel actual = findActual(actualModel, expected);
        if (actual == null) {
            JLog.error("Unable to find actual Item Number  " + expected.getItemNumber(), TakeScreenshot.True);
            return false;
        }

        verified = verify(actual.getDisplayName("itemNumber"), actual.getItemNumber(), expected.getItemNumber());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("itemDescription"), actual.getItemDescription(),
                expected.getItemDescription());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("businessName"), actual.getBusinessName(), expected.getBusinessName());
        success = (verified) ? success : verified;

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

        verified = verify(actual.getDisplayName("repairs"), actual.getRepairs(), expected.getRepairs());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("responsibility"), actual.getResponsibility(),
                expected.getResponsibility());
        success = (verified) ? success : verified;

        JLog.blankLine();

        return success;
    }

    protected void search(BomManagementResultsModel model) {
        BomManagementModel BomManagementModel = new BomManagementModel();
        BomManagementModel.setItemNumber(model.getItemNumber());

        BomManagementController BomManagementController = new BomManagementController();
        BomManagementController.setModel(BomManagementModel);
        BomManagementController.clickClear();
        BomManagementController.search();
    }

    protected void search(String itemNumber) {
        BomManagementModel BomManagementModel = new BomManagementModel();
        BomManagementModel.setItemNumber(itemNumber);

        BomManagementController BomManagementController = new BomManagementController();
        BomManagementController.setModel(BomManagementModel);
        BomManagementController.clickClear();
        BomManagementController.search();
    }

    protected BomManagementResultsModel findActual(List<BomManagementResultsModel> actualModel,
            BomManagementResultsModel expected) {
        for (BomManagementResultsModel actual : actualModel) {
            if (actual.getItemNumber().equals(expected.getItemNumber())) {
                return actual;
            }
        }
        if (actualModel.size() == 1) {
            return actualModel.get(0);
        }
        return null;
    }

    protected BomManagementResultsModel convertItemToResults(Item item) {

        BomManagementResultsModel model = new BomManagementResultsModel();

        String responsibility = "";
        if (item.getResponsibility() != null) {
            if (StringUtils.isNotBlank(item.getResponsibility().get(0).getResponsibility())) {
                responsibility = item.getResponsibility().get(0).getResponsibility();
            }
        }

        String topLevelItem = "No";
        if (StringUtils.isNotBlank(item.getIsTopLevel())) {
            topLevelItem = (item.getIsTopLevel().equalsIgnoreCase("true")) ? "Yes" : "No";
        }

        String repairs = "Yes";
        if (item.getBom() != null) {
            if (StringUtils.isNotBlank(item.getBom().get(0).getIsRepairs())) {
                repairs = (item.getBom().get(0).getIsRepairs().equals("true")) ? "Yes" : "No";
            }
        }

        model.setItemNumber(item.getItemIdentifier());
        model.setItemDescription(item.getDescription());
        model.setBusinessName(item.getBusinessEntity());
        model.setStatus(this.expectedStatus);
        model.setVersion(item.getBom().get(0).getBomVersion());
        model.setRevision(item.getBom().get(0).getBomRevision());
        model.setBomDescription(item.getBom().get(0).getDescription());
        model.setTopLevelItem(topLevelItem);
        model.setRepairs(repairs);
        model.setResponsibility(responsibility);

        return model;
    }

    protected BomManagementResultsModel convertItemAVLToResults(ItemAVL item) {

        BomManagementResultsModel model = new BomManagementResultsModel();

        String responsibility = "";
        String topLevelItem = "No";
        String repairs = "Yes";

        model.setItemNumber(item.getItemIdentifier());
        model.setItemDescription(item.getDescription());
        model.setBusinessName(item.getBusinessEntity());
        model.setStatus(this.expectedStatus);
        model.setVersion(item.getVersion());
        model.setRevision(item.getRevision());
        model.setBomDescription(item.getDescription());
        model.setTopLevelItem(topLevelItem);
        model.setRepairs(repairs);
        model.setResponsibility(responsibility);

        return model;
    }

    protected String cleanDescription(String description) {
        if (description.length() <= 30) {
            return description;
        }
        return description.substring(0, 27) + "...";
    }

    /**
     * In the Item Assignment, there is only one result, even if there are
     * multiple suppliers.
     *
     * @param itemID
     * @return
     */
    protected boolean isItemProcessed(String itemID) {
        boolean isProcessed = this.itemProcessed.contains(itemID);
        if (!isProcessed) {
            this.itemProcessed.add(itemID);
        }
        return isProcessed;
    }

    public void select(String itemNumber) {
        select(itemNumber, page.tableLocator());
    }

    public void check(String itemNumber) {
        check(itemNumber, page.tableLocator());
    }

    public void clickClose() {
        clickAndCheckForPOSTError(page.closeButton());
    }

    public void clickReplace() {
        clickAndCheckForPOSTError(page.replaceButton());
    }

    public void clickCompare() {
        clickAndCheckForPOSTError(page.compareButton());
    }

    public void clickEdit() {
        clickAndCheckForPOSTError(page.editButton());
    }

    public void clickReopen() {
        clickAndCheckForPOSTError(page.reopenButton());
    }
}
