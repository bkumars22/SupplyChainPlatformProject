/*
 * @DataTableTypes.java@
 * Created on Nov 21, 2022
 *
 * Copyright (c) 2022 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
/**
 *
 */
package com.test.selenium.scplatform.steps;

import java.util.Map;

import com.test.selenium.scplatform.messages.commodityCode.CommodityCodeModel;
import com.test.selenium.scplatform.steps.data.ItemData;
import com.test.selenium.scplatform.ui.costing.newSourcingLane.NewSourcingLaneModel;
import com.test.selenium.scplatform.ui.costing.newSourcingLane.details.SourcingLaneInformationModel;
import com.test.selenium.scplatform.ui.costing.searchSourcingLane.SearchSourcingLaneModel;
import com.test.selenium.scplatform.ui.masterDataManagement.commodityManagement.CommodityManagementResultsModel;

import io.cucumber.java.DataTableType;

/**
 * @author dgenrich
 *
 */
public class DataTableTypes {

  @DataTableType
  public CommodityCodeModel CommodityCodeModelTable (Map<String, String> entry)
      throws InstantiationException, IllegalAccessException, NoSuchFieldException,
      SecurityException, IllegalArgumentException {
    CommodityCodeModel commoditycodemodel = new CommodityCodeModel();
    return commoditycodemodel.defineDataTableType(CommodityCodeModel.class, entry);
  }

  @DataTableType
  public CommodityManagementResultsModel CommodityManagementResultsModelTable (Map<String, String> entry)
      throws InstantiationException, IllegalAccessException, NoSuchFieldException,
      SecurityException, IllegalArgumentException {
    CommodityManagementResultsModel commoditymanagementresultsmodel = new CommodityManagementResultsModel();
    return commoditymanagementresultsmodel.defineDataTableType(CommodityManagementResultsModel.class, entry);
  }

  @DataTableType
  public NewSourcingLaneModel NewSourcingLaneModelTable (Map<String, String> entry)
      throws InstantiationException, IllegalAccessException, NoSuchFieldException,
      SecurityException, IllegalArgumentException {
    NewSourcingLaneModel newsourcinglanemodel = new NewSourcingLaneModel();
    return newsourcinglanemodel.defineDataTableType(NewSourcingLaneModel.class, entry);
  }

  @DataTableType
  public SearchSourcingLaneModel SearchSourcingLaneModelTable (Map<String, String> entry)
      throws InstantiationException, IllegalAccessException, NoSuchFieldException,
      SecurityException, IllegalArgumentException {
    SearchSourcingLaneModel searchsourcinglanemodel = new SearchSourcingLaneModel();
    return searchsourcinglanemodel.defineDataTableType(SearchSourcingLaneModel.class, entry);
  }

  @DataTableType
  public SourcingLaneInformationModel SourcingLaneInformationModelTable (Map<String, String> entry)
      throws InstantiationException, IllegalAccessException, NoSuchFieldException,
      SecurityException, IllegalArgumentException {
    SourcingLaneInformationModel sourcinglaneinformationmodel = new SourcingLaneInformationModel();
    return sourcinglaneinformationmodel.defineDataTableType(SourcingLaneInformationModel.class, entry);
  }

  @DataTableType
  public ItemData ItemDataTable (Map<String, String> entry)
      throws InstantiationException, IllegalAccessException, NoSuchFieldException,
      SecurityException, IllegalArgumentException {
    ItemData model = new ItemData();
    return model.defineDataTableType(ItemData.class, entry);
  }
}
