/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.costing.searchCostRecords;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.test.selenium.common.JLog;
import com.test.selenium.common.MathUtils;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLane;
import com.test.selenium.scplatform.messages.sourcingLane.subClasses.CostRecord;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.test.selenium.scplatform.ui.costing.searchCostRecords.SearchCostRecordsResultsModel.Range;
import com.test.selenium.scplatform.utilities.DatabaseUtils;

public class SearchCostRecordsResultsController extends SCPlatformSearchResultsController {
  protected SearchCostRecordsResultsPage page;

  public SearchCostRecordsResultsController() {
    super();
    page = new SearchCostRecordsResultsPage();
  }

  @Override
  public PageImpl getView() {
    if (page == null) {
      page = new SearchCostRecordsResultsPage();
    }
    return page;
  }

  public boolean validate(List<SourcingLane> expectedResults, List<Item> itemData) {

    boolean success = true;
    boolean verified = true;

    for (SourcingLane expected : expectedResults) {
      String laneName = buildLaneName(expected);
      JLog.section("Verify " + laneName);

      search(expected);

      String sourceSite = DatabaseUtils.getSiteDescription(expected.getFromSite());

      List<SearchCostRecordsResultsModel> actualModel = page.parseResults();
      SearchCostRecordsResultsModel actual = findActual(actualModel, expected.getItemIdentifier(), sourceSite);
      if (actual == null) {
        JLog.error(String.format("Unable to find Item '%s' with Source Site '%s'", expected.getItemIdentifier(),
            sourceSite), TakeScreenshot.True);
        success = false;  // â† MARK AS FAILURE
        continue;
      }

      List<CostRecord> costRecord = expected.getCostRecord();
      Item item = getItem(itemData, expected.getItemIdentifier(), expected.getFromSite());

      verified = verify(actual.getDisplayName("status"), actual.getStatus(), expected.getState());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("commodityName"), actual.getCommodityName(),
          item.getCommodityCode());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("item"), actual.getItem(), expected.getItemIdentifier());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("itemDescription"), actual.getItemDescription(),
          expected.getDescription());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("itemBusiness"), actual.getItemBusiness(),
          expected.getBusinessEntity());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("supplier"), actual.getSupplier(),
          expected.getFromBusinessEntity());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("sourceSite"), actual.getSourceSite(), sourceSite);
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("destinationSite"), actual.getDestinationSite(),
          DatabaseUtils.getSiteDescription(expected.getSite()));
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("costType"), actual.getCostType(),
          DatabaseUtils.getCostTypeName(costRecord.get(0).getCostType()));
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("pricingScenario"),
          WordUtils.capitalizeFully(actual.getPricingScenario()),
          WordUtils.capitalizeFully(costRecord.get(0).getPricingScenario()));
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("startDate"), actual.getStartDate(),
          costRecord.get(0).getEffectiveFromDate().toString(Constants.DateFormatUI()));
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("endDate"), actual.getEndDate(),
          costRecord.get(0).getEffectiveToDate().toString(Constants.DateFormatUI()));
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("currency"), actual.getCurrency(), expected.getCurrencyCode());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("productState"), actual.getProductState(),
          costRecord.get(0).getState());
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("nonManagedCostAdjustment"), actual.getNonManagedCostAdjustment(),
          "No");
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("laneName"), actual.getLaneName(), laneName);
      success = (verified) ? success : verified;

      if ((item != null) && (item.getResponsibility() != null) && (!item.getResponsibility().isEmpty())) {
        verified = verify(actual.getDisplayName("responsibility"), actual.getResponsibility(),
            item.getResponsibility().get(0).getResponsibility());
        success = (verified) ? success : verified;
      }

      verified = verify(actual.getDisplayName("comment"), actual.getComment(),
          cleanComment(costRecord.get(0).getComment()));
      success = (verified) ? success : verified;

      verified = verify(actual.getDisplayName("reasonCode"), actual.getReasonCode(),
          costRecord.get(0).getReasonCode());
      success = (verified) ? success : verified;

      // --------------
      // HANDLE Cost Values
      // --------------
      String costElementType;
      float totalPrice;
      float costValue;
      float actualCost;

      if ((costRecord.get(0).getCostRecordRange() != null)
          && (costRecord.get(0).getCostRecordRange().size() > 1)) {
        for (int index = 0; index < costRecord.get(0).getCostRecordRange().size(); index++) {
          Range range = getRange(actual, costRecord.get(0).getCostRecordRange().get(index).getFromRange(),
              costRecord.get(0).getCostRecordRange().get(index).getToRange());

          if (range != null) {
            totalPrice = 0;

            verified = verify("From Range", range.getCostRecordRange_from(),
                costRecord.get(0).getCostRecordRange().get(index).getFromRange());
            success = (verified) ? success : verified;

            verified = verify("To Range", range.getCostRecordRange_to(),
                costRecord.get(0).getCostRecordRange().get(index).getToRange());
            success = (verified) ? success : verified;

            for (int x = 0; x < costRecord.get(0).getCostRecordRange().get(index).getCostRecordValue()
                .size(); x++) {
              costElementType = costRecord.get(0).getCostRecordRange().get(index).getCostRecordValue()
                  .get(x).getCostElementType();
              costValue = MathUtils.Round(Float.parseFloat(costRecord.get(0).getCostRecordRange()
                  .get(index).getCostRecordValue().get(x).getCostValue()), 2);
              totalPrice += costValue;

              actualCost = range.getCostElementList().get(costElementType);

              verified = verify(costElementType, actualCost, costValue);
              success = (verified) ? success : verified;
            }

            verified = verify("Total", range.getCostTotal(), totalPrice);
            success = (verified) ? success : verified;
          }
        }
      } else if (costRecord.get(0).getCostRecordValue() != null) {
        totalPrice = 0;
        Range range = actual.getRange().get(0);

        verified = verify("From Range", range.getCostRecordRange_from(), NullValue.FLOAT);
        success = (verified) ? success : verified;

        verified = verify("To Range", range.getCostRecordRange_to(), NullValue.FLOAT);
        success = (verified) ? success : verified;

        for (int x = 0; x < costRecord.get(0).getCostRecordValue().size(); x++) {
          costElementType = costRecord.get(0).getCostRecordValue().get(x).getCostElementType();
          costValue = MathUtils
              .Round(Float.parseFloat(costRecord.get(0).getCostRecordValue().get(x).getCostValue()), 2);
          totalPrice += costValue;

          actualCost = range.getCostElementList().get(costElementType);

          verified = verify(costElementType, actualCost, costValue);
          success = (verified) ? success : verified;
        }
        verified = verify("Total", range.getCostTotal(), totalPrice);
        success = (verified) ? success : verified;
      }

      JLog.blankLine();
    }
    return success;
  }

  protected void search(SourcingLane expectedModel) {
    SearchCostRecordsModel searchCostRecordsModel = new SearchCostRecordsModel();
    searchCostRecordsModel.setItemNumber(expectedModel.getItemIdentifier());
    searchCostRecordsModel.setSupplierName(expectedModel.getFromBusinessEntity());

    SearchCostRecordsController searchCostRecordsController = new SearchCostRecordsController();
    searchCostRecordsController.setModel(searchCostRecordsModel);
    searchCostRecordsController.search();
  }

  protected SearchCostRecordsResultsModel findActual(List<SearchCostRecordsResultsModel> actualModel,
      String itemNumber, String sourceSite) {
    for (SearchCostRecordsResultsModel actual : actualModel) {
      if ((StringUtils.isNotBlank(actual.getItem())) && (actual.getItem().equals(itemNumber))) {
        if (StringUtils.isNotBlank(sourceSite)) {
          if (actual.getSourceSite().equals(sourceSite)) {
            return actual;
          }
        } else {
          return actual;
        }
      }
    }
    return null;
  }

  protected String buildLaneName(SourcingLane data) {
    StringBuilder laneName = new StringBuilder();

    laneName.append(data.getItemIdentifier());
    laneName.append("-");
    laneName.append(data.getFromBusinessEntity());
    laneName.append("-");
    laneName.append(DatabaseUtils.getSiteDescription(data.getFromSite()));
    laneName.append("-");
    laneName.append(DatabaseUtils.getSiteDescription(data.getSite()));
    laneName.append("-");
    laneName.append(data.getCurrencyCode());

    return laneName.toString();
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

  protected String cleanComment(String comment) {
    if (comment.length() <= 25) {
      return comment;
    }
    return comment.substring(0, 25) + " ...";
  }

  protected Range getRange(SearchCostRecordsResultsModel costRecordResults, float fromRange, float toRange) {
    List<Range> possibleRanges = new ArrayList<>();

    for (int index = 0; index < costRecordResults.getRange().size(); index++) {
      if ((fromRange == NullValue.FLOAT)
          || (costRecordResults.getRange().get(index).getCostRecordRange_from() == fromRange)) {
        possibleRanges.add(costRecordResults.getRange().get(index));
      }
    }

    for (int index = 0; index < possibleRanges.size(); index++) {
      if ((toRange == NullValue.FLOAT) || (possibleRanges.get(index).getCostRecordRange_to() == toRange)) {
        return possibleRanges.get(index);
      }
    }

    if (possibleRanges.isEmpty()) {
      return null;
    }

    return possibleRanges.get(0);
  }

}
