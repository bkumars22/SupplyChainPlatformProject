/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.masterDataManagement.itemAssignment;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.itemAVL.ItemAVL;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.test.selenium.scplatform.ui.dialogs.itemDetails.ItemDetailsController;

public class ItemAssignmentResultsController extends SCPlatformSearchResultsController {
    protected ItemAssignmentResultsPage page;
    protected List<String> itemProcessed;

    public ItemAssignmentResultsController() {
        super();
        page = new ItemAssignmentResultsPage();
        itemProcessed = new ArrayList<String>();
    }

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new ItemAssignmentResultsPage();
        }
        return page;
    }

    public boolean validateItemAVL(List<ItemAVL> expectedResults) {
        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        for (ItemAVL item : expectedResults) {
            if (isItemProcessed(item.getItemIdentifier()))
                continue;

            ItemAssignmentResultsModel expected = convertItemAVLToResults(item);
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
            if (item.getItemIdentifier().contains(" BOM"))
                continue;

            if ("true".equalsIgnoreCase(item.getIsTopLevel()))
                continue;

            if (isItemProcessed(item.getItemIdentifier()))
                continue;

            ItemAssignmentResultsModel expected = convertItemToResults(item);
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
            if (item.getItemIdentifier().contains(" BOM"))
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

    protected boolean validate(ItemAssignmentResultsModel expected) {
        boolean success = true;
        boolean verified = true;

        JLog.section("Verify Item Assignment - " + expected.getItemNumber());
        search(expected);

        List<ItemAssignmentResultsModel> actualModel = page.parseResults();

        ItemAssignmentResultsModel actual = findActual(actualModel, expected);
        if (actual == null) {
            JLog.error("Unable to find actual Item Number  " + expected.getItemNumber(), TakeScreenshot.True);
            return false;
        }

        verified = verify(actual.getDisplayName("itemNumber"), actual.getItemNumber(), expected.getItemNumber());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("itemType"), actual.getItemType(), expected.getItemType());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("itemDescription"), actual.getItemDescription(),
                expected.getItemDescription());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("costCommodity"), actual.getCostCommodity(),
                expected.getCostCommodity());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("businessName"), actual.getBusinessName(), expected.getBusinessName());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("responsibility"), actual.getResponsibility(),
                expected.getResponsibility());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("daysSinceAdded"), actual.getDaysSinceAdded(),
                expected.getDaysSinceAdded());
        success = (verified) ? success : verified;

        JLog.blankLine();

        return success;
    }

    protected void search(ItemAssignmentResultsModel model) {
        ItemAssignmentModel itemAssignmentModel = new ItemAssignmentModel();
        itemAssignmentModel.setItemNumber(model.getItemNumber());
        // itemAssignmentModel.setCostCommodity(model.getCostCommodity());

        ItemAssignmentController itemAssignmentController = new ItemAssignmentController();
        itemAssignmentController.setModel(itemAssignmentModel);
        itemAssignmentController.clickClear();
        itemAssignmentController.search();
    }

    protected void search(String itemNumber) {
        ItemAssignmentModel itemAssignmentModel = new ItemAssignmentModel();
        itemAssignmentModel.setItemNumber(itemNumber);
        itemAssignmentModel.setItemType("Item");

        ItemAssignmentController itemAssignmentController = new ItemAssignmentController();
        itemAssignmentController.setModel(itemAssignmentModel);
        itemAssignmentController.clickClear();
        itemAssignmentController.search();
    }

    protected ItemAssignmentResultsModel findActual(List<ItemAssignmentResultsModel> actualModel,
            ItemAssignmentResultsModel expected) {
        for (ItemAssignmentResultsModel actual : actualModel) {
            if (actual.getItemNumber().equals(expected.getItemNumber())) {
                return actual;
            }
        }
        if (actualModel.size() == 1) {
            return actualModel.get(0);
        }
        return null;
    }

    protected ItemAssignmentResultsModel convertItemToResults(Item item) {

        ItemAssignmentResultsModel model = new ItemAssignmentResultsModel();
        String costCommodity = String.format("%s (%s)",
                (StringUtils.isBlank(item.getCommodityCode())) ? "" : item.getCommodityCode(),
                (StringUtils.isBlank(item.getBusinessEntity())) ? "" : item.getBusinessEntity().toUpperCase());

        String responsibility = "-";
        if (item.getResponsibility() != null) {
            if (StringUtils.isNotBlank(item.getResponsibility().get(0).getResponsibility())) {
                responsibility = item.getResponsibility().get(0).getResponsibility();
            }
        }

        model.setItemNumber(item.getItemIdentifier());
        model.setItemType(item.getItemPartType());
        model.setItemDescription(cleanDescription(item.getDescription()));
        model.setCostCommodity(costCommodity);
        model.setBusinessName(item.getBusinessEntity());
        model.setResponsibility(responsibility);
        model.setDaysSinceAdded("0");

        return model;
    }

    protected ItemAssignmentResultsModel convertItemAVLToResults(ItemAVL item) {

        ItemAssignmentResultsModel model = new ItemAssignmentResultsModel();
        String costCommodity = String.format("%s (%s)",
                (StringUtils.isBlank(item.getCommodityCode())) ? "" : item.getCommodityCode(),
                (StringUtils.isBlank(item.getBusinessEntity())) ? "" : item.getBusinessEntity().toUpperCase());

        String responsibility = "-";

        model.setItemNumber(item.getItemIdentifier());
        model.setItemType("Item");
        model.setItemDescription(cleanDescription(item.getDescription()));
        model.setCostCommodity(costCommodity);
        model.setBusinessName(item.getBusinessEntity());
        model.setResponsibility(responsibility);
        model.setDaysSinceAdded("0");

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
}
