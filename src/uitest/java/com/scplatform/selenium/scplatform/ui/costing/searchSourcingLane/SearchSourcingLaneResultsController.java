/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchSourcingLane;

import java.util.List;

import org.apache.commons.lang.WordUtils;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLane;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.test.selenium.scplatform.utilities.DatabaseUtils;

public class SearchSourcingLaneResultsController extends SCPlatformSearchResultsController {
  protected SearchSourcingLaneResultsPage page;

  public SearchSourcingLaneResultsController() {
    super();
    page = new SearchSourcingLaneResultsPage();
  }

  @Override
  public PageImpl getView() {
    if (page == null) {
      page = new SearchSourcingLaneResultsPage();
    }
    return page;
  }

  public boolean validate(List<SourcingLane> expectedResults, List<Item> itemData) {
    boolean success = true;
    boolean verified = true;

    for (SourcingLane sourcingLane : expectedResults) {
      JLog.section("Verify " + sourcingLane.getSourcingLaneIdentifier());

      search(sourcingLane.getSourcingLaneIdentifier());

      SearchSourcingLaneResultsModel expected = convertSourcingLaneToResults(sourcingLane, itemData);

      List<SearchSourcingLaneResultsModel> actualModel = page.parseResults();
      SearchSourcingLaneResultsModel actual = findActual(actualModel, expected);
      if (actual == null) {
        JLog.error("Unable to find Item Number:  " + expected.getItem(), TakeScreenshot.True);
        success = false;  // ← MARK AS FAILURE
        continue;
      }

      verified = verify(actual.getDisplayName("status"), actual.getStatus(), expected.getStatus());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("commodityName"), actual.getCommodityName(),
          expected.getCommodityName());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("item"), actual.getItem(), expected.getItem());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("itemDescription"), actual.getItemDescription(),
          expected.getItemDescription());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("itemBusiness"), actual.getItemBusiness(),
          expected.getItemBusiness());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("supplier"), actual.getSupplier(), expected.getSupplier());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("sourceSite"), actual.getSourceSite(), expected.getSourceSite());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("destinationSite"), actual.getDestinationSite(),
          expected.getDestinationSite());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("currency"), actual.getCurrency(), expected.getCurrency());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("productState"), actual.getProductState(),
          expected.getProductState());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("responsibility"), actual.getResponsibility(),
          expected.getResponsibility());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("nonManagedCostAdjustment"), actual.getNonManagedCostAdjustment(),
          expected.getNonManagedCostAdjustment());
      success = (verified) ? success : verified;

      JLog.blankLine();
    }
    return success;
  }

  protected void search(String sourcingLaneIdentifier) {
    SearchSourcingLaneModel searchSourcingLaneModel = new SearchSourcingLaneModel();
    searchSourcingLaneModel.setLaneName(sourcingLaneIdentifier);

    SearchSourcingLaneController searchSourcingLaneController = new SearchSourcingLaneController();
    searchSourcingLaneController.setModel(searchSourcingLaneModel);
    searchSourcingLaneController.search();
  }

  protected SearchSourcingLaneResultsModel findActual(List<SearchSourcingLaneResultsModel> actualModel,
      SearchSourcingLaneResultsModel expected) {
    for (SearchSourcingLaneResultsModel actual : actualModel) {
      if ((actual.getItem().equals(expected.getItem()))
          && (actual.getSupplier().equals(expected.getSupplier()))) {
        return actual;
      }
    }
    return null;
  }

  protected SearchSourcingLaneResultsModel convertSourcingLaneToResults(SourcingLane expectedResult,
      List<Item> itemData) {
    SearchSourcingLaneResultsModel sourcingLaneResultsModel = new SearchSourcingLaneResultsModel();
    String productState = "Production";
    if (expectedResult.getLifeCycleCode() != null) {
      productState = WordUtils.capitalize(expectedResult.getLifeCycleCode().toLowerCase());
    }
    Item item = getItem(itemData, expectedResult.getItemIdentifier(), expectedResult.getFromSite());

    sourcingLaneResultsModel.setStatus(expectedResult.getState());
    sourcingLaneResultsModel.setCommodityName(item.getCommodityCode());
    sourcingLaneResultsModel.setItem(expectedResult.getItemIdentifier());
    sourcingLaneResultsModel.setItemDescription(expectedResult.getDescription());
    sourcingLaneResultsModel.setItemBusiness(expectedResult.getBusinessEntity());
    sourcingLaneResultsModel.setSupplier(expectedResult.getFromBusinessEntity());
    if (approvedVendorListItemIndex != -1)
      sourcingLaneResultsModel
      .setSourceSite(item.getApprovedVendorListItem().get(approvedVendorListItemIndex).getSite());
    sourcingLaneResultsModel.setDestinationSite(DatabaseUtils.getSiteDescription(expectedResult.getSite()));
    sourcingLaneResultsModel.setCurrency(expectedResult.getCurrencyCode());
    sourcingLaneResultsModel.setProductState(productState);
    sourcingLaneResultsModel.setNonManagedCostAdjustment("No");

    if ((item != null) && (item.getResponsibility() != null) && (!item.getResponsibility().isEmpty())) {
      sourcingLaneResultsModel.setResponsibility(item.getResponsibility().get(0).getResponsibility());
    }

    return sourcingLaneResultsModel;
  }

  protected int approvedVendorListItemIndex = -1;

  protected Item getItem(List<Item> itemData, String itemName, String supplierSite) {
    approvedVendorListItemIndex = -1;

    for (int row = 0; row < itemData.size(); row++) {
      if (itemData.get(row).getItemIdentifier().equals(itemName)) {
        if (itemData.get(row).getApprovedVendorListItem() != null) {
          for (int index = 0; index < itemData.get(row).getApprovedVendorListItem().size(); index++) {
            if (supplierSite == null) {
              approvedVendorListItemIndex = index;
              return itemData.get(row);
            } else if (itemData.get(row).getApprovedVendorListItem().get(index).getSiteName()
                .equals(supplierSite)) {
              approvedVendorListItemIndex = index;
              return itemData.get(row);
            }
          }

        } else {
          return itemData.get(row);
        }

      }
    }

    return null;
  }

  public void select(String itemNumber) {
    select(itemNumber, page.tableLocator());
  }
}
