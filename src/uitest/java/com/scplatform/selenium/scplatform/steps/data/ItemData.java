/*
 * ItemData.java
 * Created on Mar 19, 2021
 *
 * Copyright (c) 2021 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.steps.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.test.selenium.common.FileHelper;
import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.model.Model;
import com.test.selenium.scplatform.messages.item.parser.Variables;

/**
 * http://confluence.dev.scplatform.local/display/QA/Item+Table
 *
 * @author dgenrich
 *
 */
public class ItemData extends Model {
  private static File itemDataFile = null;

  public static File getItemDataFile() throws IOException {
    if (itemDataFile != null) {
      return itemDataFile;
    }
    return FileHelper.getResourceFile(ItemData.class, "items.xlsx");
  }

  public static void setItemDataFile(File itemFile) {
    itemDataFile = itemFile;
  }

  public static List<Variables> getByKeyName(String replacementVariableKey) {
    if (replacementVariableKey.equals("17Inch")) {
      return get_17Inch_ItemsVariables();
    } else if (replacementVariableKey.equals("18Inch")) {
      return get_18Inch_ItemsVariables();
    } else if (replacementVariableKey.equals("30Inch")) {
      return get_30Inch_ItemsVariables();
    } else {
      JLog.error("Unknown Replacement Key: " + replacementVariableKey);
    }

    return null;
  }

  /**
   *
   * @return List of {@link Variables} data for a 17"/16GB Tablet
   */
  public static List<Variables> get_17Inch_ItemsVariables() {
    List<Variables> itemVariables = new ArrayList<>();

    // ===========================
    // Table with 7" Display and 16 MB Memory
    // ===========================
    Variables variable = new Variables();
    variable.setKeyName("DISPLAY");
    variable.setKeyValue("17 inch");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("DISPLAY_SIMPLE");
    variable.setKeyValue("17");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("DISPLAY_DISCRIPTION");
    variable.setKeyValue("17 inch Diagonal, 1920x1080 Pixels");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("MEMORY");
    variable.setKeyValue("16GB");
    itemVariables.add(variable);

    // ===========================
    return itemVariables;
  }

  /**
   *
   * @return List of {@link Variables} data for a 18"/32GB Tablet
   */
  public static List<Variables> get_18Inch_ItemsVariables() {
    List<Variables> itemVariables = new ArrayList<>();

    // ===========================
    // Table with 18" Display and 32 GB Memory
    // ===========================
    Variables variable = new Variables();
    variable.setKeyName("DISPLAY");
    variable.setKeyValue("18 inch");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("DISPLAY_SIMPLE");
    variable.setKeyValue("18");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("DISPLAY_DISCRIPTION");
    variable.setKeyValue("18 inch Diagonal, 1920x1080 Pixels");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("MEMORY");
    variable.setKeyValue("32GB");
    itemVariables.add(variable);

    // ===========================
    return itemVariables;
  }

  /**
   *
   * @return List of {@link Variables} data for a 30"/64GB
   */
  public static List<Variables> get_30Inch_ItemsVariables() {
    List<Variables> itemVariables = new ArrayList<>();

    // ===========================
    // Table with 30" Display and 64 MB Memory
    // ===========================
    Variables variable = new Variables();
    variable.setKeyName("DISPLAY");
    variable.setKeyValue("30 inch");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("DISPLAY_SIMPLE");
    variable.setKeyValue("30");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("DISPLAY_DISCRIPTION");
    variable.setKeyValue("30 inch Diagonal, 1920x1080 Pixels");
    itemVariables.add(variable);

    variable = new Variables();
    variable.setKeyName("MEMORY");
    variable.setKeyValue("64GB");
    itemVariables.add(variable);

    // ===========================
    return itemVariables;
  }

}
