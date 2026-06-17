/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.search.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;

public class SearchItemsResultsController extends SCPlatformSearchResultsController {
    protected SearchItemsResultsPage page;

    public SearchItemsResultsController() {
        super();
        page = new SearchItemsResultsPage();
    }

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new SearchItemsResultsPage();
        }
        return page;
    }

    public boolean validate(List<Item> expectedResults) {
        search(expectedResults);

        boolean success = true;
        boolean verified = true;
        List<SearchItemsResultsModel> actualModel = page.parseResults();
        List<SearchItemsResultsModel> expectedModel = convertItemToResults(expectedResults);

        JLog.blankLine();
        for (SearchItemsResultsModel expected : expectedModel) {
            SearchItemsResultsModel actual = findActual(actualModel, expected);
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

            verified = verify(actual.getDisplayName("commodityName"), actual.getCommodityName(),
                    expected.getCommodityName());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("memberOfGroup"), actual.getMemberOfGroup(),
                    expected.getMemberOfGroup());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("platform"), actual.getPlatform(), expected.getPlatform());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("classification"), actual.getClassification(),
                    expected.getClassification());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("productFamily"), actual.getProductFamily(),
                    expected.getProductFamily());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("responsibility"), actual.getResponsibility(),
                    expected.getResponsibility());
            success = (verified) ? success : verified;

            JLog.blankLine();
        }
        return success;
    }

    protected void search(List<Item> expectedModel) {
        List<String> multipleItems = new ArrayList<String>();

        for (Item model : expectedModel) {
            if (!multipleItems.contains(model.getItemIdentifier())) {
                multipleItems.add(model.getItemIdentifier());
            }
        }

        SearchItemsModel searchItemsModel = new SearchItemsModel();
        searchItemsModel.setMultipleItemNumbers(StringUtils.join(multipleItems, ";"));

        SearchItemsController searchItemsController = new SearchItemsController();
        searchItemsController.setModel(searchItemsModel);
        searchItemsController.search();
        setPageSize(100);
    }

    protected SearchItemsResultsModel findActual(List<SearchItemsResultsModel> actualModel,
            SearchItemsResultsModel expected) {
        for (SearchItemsResultsModel actual : actualModel) {
            if (actual.getItemNumber().equals(expected.getItemNumber())) {
                return actual;
            }
        }
        return null;
    }

    protected List<SearchItemsResultsModel> convertItemToResults(List<Item> expectedResults) {
        List<SearchItemsResultsModel> searchItemsResultsModel = new ArrayList<SearchItemsResultsModel>();

        for (Item item : expectedResults) {
            SearchItemsResultsModel model = new SearchItemsResultsModel();

            model.setItemNumber(item.getItemIdentifier());
            model.setItemType(item.getItemPartType());
            model.setItemDescription(item.getDescription());
            model.setItemBusiness(item.getBusinessEntity());
            model.setRevision(item.getRevision());
            model.setCommodityName(item.getCommodityCode());
            model.setMemberOfGroup(null);
            model.setClassification(item.getItemClassification());
            model.setProductFamily(item.getProprietaryProductFamily());

            if ((item.getItemPlatform() != null) && (!item.getItemPlatform().isEmpty()))
                model.setPlatform(item.getItemPlatform().get(0).getPlatformName());

            if ((item.getResponsibility() != null) && (!item.getResponsibility().isEmpty()))
                model.setResponsibility(item.getResponsibility().get(0).getResponsibility());

            searchItemsResultsModel.add(model);
        }

        return searchItemsResultsModel;
    }

}
