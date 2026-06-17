/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.itemAVL;

import java.util.List;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.constants.JIRA;
import com.test.selenium.scplatform.messages.itemAVL.ItemAVL;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;

public class SearchItemAVLResultsController extends SCPlatformSearchResultsController {
    protected SearchItemAVLResultsPage page;

    public SearchItemAVLResultsController() {
        super();
        page = new SearchItemAVLResultsPage();
    }

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new SearchItemAVLResultsPage();
        }
        return page;
    }

    public boolean validate(List<ItemAVL> expectedResults) {

        boolean success = true;
        boolean verified = true;

        JLog.blankLine();
        for (ItemAVL itemAVL : expectedResults) {
            JLog.section("Verify ItemAVL - " + itemAVL.getItemIdentifier());
            search(itemAVL);

            SearchItemAVLResultsModel expected = convertItemToResults(itemAVL);
            List<SearchItemAVLResultsModel> actualModel = page.parseResults();

            SearchItemAVLResultsModel actual = findActual(actualModel, expected);
            if (actual == null) {
                JLog.error("Unable to find actual Item Number  " + expected.getItemNumber(), TakeScreenshot.True);
                success = false;  // ← MARK AS FAILURE
                continue;
            }

            verified = verify(actual.getDisplayName("itemNumber"), actual.getItemNumber(), expected.getItemNumber());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("itemType"), actual.getItemType(), expected.getItemType());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("itemDescription"), actual.getItemDescription(),
                    expected.getItemDescription());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("itemBusiness"), actual.getItemBusiness(),
                    expected.getItemBusiness());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("revision"), actual.getRevision(), expected.getRevision());
            success = (verified) ? success : verified;

            // verified = verify(actual.getDisplayName("memberOfGroup"),
            // actual.getMemberOfGroup(),
            // expected.getMemberOfGroup());
            // success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("classification"), actual.getClassification(),
                    expected.getClassification());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("productFamily"), actual.getProductFamily(),
                    expected.getProductFamily());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("responsibility"), actual.getResponsibility(),
                    expected.getResponsibility());
            success = (verified) ? success : verified;

            if (!JIRA.SCPlatform1167) {
                verified = verify(actual.getDisplayName("commodityName"), actual.getCommodityName(),
                        expected.getCommodityName());
                success = (verified) ? success : verified;

                verified = verify(actual.getDisplayName("platform"), actual.getPlatform(), expected.getPlatform());
                success = (verified) ? success : verified;

                verified = verify(actual.getDisplayName("supplierName"), actual.getSupplierName(),
                        expected.getSupplierName());
                success = (verified) ? success : verified;

                verified = verify(actual.getDisplayName("supplierSite"), actual.getSupplierSite(),
                        expected.getSupplierSite());
                success = (verified) ? success : verified;

                verified = verify(actual.getDisplayName("supplierItemNumber"), actual.getSupplierItemNumber(),
                        expected.getSupplierItemNumber());
                success = (verified) ? success : verified;
            }

            JLog.blankLine();
        }
        return success;
    }

    protected void search(ItemAVL model) {
        SearchItemAVLModel searchItemsModel = new SearchItemAVLModel();
        searchItemsModel.setItemNumber(model.getItemIdentifier());
        // searchItemsModel.setSupplierName(model.getVendorBusinessEntity());
        // searchItemsModel.setSupplierItemNumber(model.getVendorItemIdentifier());

        SearchItemAVLController searchItemsController = new SearchItemAVLController();
        searchItemsController.setModel(searchItemsModel);
        searchItemsController.clickClear();
        searchItemsController.search();
    }

    protected SearchItemAVLResultsModel findActual(List<SearchItemAVLResultsModel> actualModel,
            SearchItemAVLResultsModel expected) {
        for (SearchItemAVLResultsModel actual : actualModel) {
            if (actual.getItemNumber().equals(expected.getItemNumber())) {
                return actual;
                // if
                // (actual.getSupplierSite().equals(expected.getSupplierSite()))
                // {
                // return actual;
                // }
            }
        }
        if (actualModel.size() == 1) {
            return actualModel.get(0);
        }
        return null;
    }

    protected SearchItemAVLResultsModel convertItemToResults(ItemAVL item) {

        SearchItemAVLResultsModel model = new SearchItemAVLResultsModel();

        model.setItemNumber(item.getItemIdentifier());
        model.setItemType("Item");
        model.setItemDescription(item.getDescription());
        model.setItemBusiness(item.getBusinessEntity());
        model.setRevision(item.getRevision());
        model.setCommodityName(item.getCommodityCode());
        model.setMemberOfGroup(item.getManagedBy());
        model.setClassification(item.getItemClassification());
        // model.setProductFamily(item.getVerification_ProductFamily());
        model.setProductFamily(null);
        model.setPlatform(item.getVerification_Platform());
        model.setResponsibility(item.getVerification_Responsibility());
        model.setSupplierName(item.getVendorBusinessEntity());
        model.setSupplierSite(item.getVendorSite());
        model.setSupplierItemNumber(item.getVendorItemIdentifier());

        return model;
    }

}
