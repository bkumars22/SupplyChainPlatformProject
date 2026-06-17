@ForecastRegression
Feature: Forecast Regression workflow

  #C668140  Regression 21.1
  Scenario: Simple Forecast_creation via UI with Business Admin role
    Given I log into HarmonyMTCM as "mtcmUser" with "busadmin5"
    When I navigate to "Main" -> "Upload"
    And I upload the "ItemAVLUI" "ItemUpload" with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    When I click on "Next" Button
    When I set forecast value "100" on row "1"
    Then I click on forecast arrow forward icon to copy the values in to eleven buckets
    When I click on the save button on forecast page
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I verify labelName "Member of Group" not found on the loaded page
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results

 #C668141 Regression 21.1
  Scenario: C668141 Simple Forecast_creation via UI with GCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm4"
    When I navigate to "Main" -> "Upload"
    And I upload the "ItemAVLUI" "ItemUpload" with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    When I click on "Next" Button
    When I set forecast value "100" on row "1"
    Then I click on forecast arrow forward icon to copy the values in to eleven buckets
    When I click on the save button on forecast page
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I verify labelName "Member of Group" not found on the loaded page
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results

   #C668142  Regression 21.1
  Scenario: Simple Forecast_creation via UI with Super GCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "supergcm5"
    When I navigate to "Main" -> "Upload"
    And I upload the "ItemAVLUI" "ItemUpload" with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    When I click on "Next" Button
    When I set forecast value "100" on row "1"
    Then I click on forecast arrow forward icon to copy the values in to eleven buckets
    When I click on the save button on forecast page
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I verify labelName "Member of Group" not found on the loaded page
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results

 #C668145  Regression 21.1
  Scenario: Upload simple forecast template downloaded from forecast using GCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm5"
    When I navigate to "Main" -> "Upload"
    And I upload the "ItemAVLUI" "ItemUpload" with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Main" -> "Upload"
    And I upload the "CurrentForecastUI" "ApprovedForecast" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "3" FC Extend forecast value on search filter results
    When I navigate to "Main" -> "Upload"
    And I upload the "CurrentForecastUI" "ApprovedForecast_closed" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "CLOSED" FC status on search filter results
    Then I verify "15" FC Extend forecast value on search filter results

  #C668146 - Regression 21.1
  Scenario: Upload simple forecast template downloaded from forecast using Super GCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "supergcm5"
    When I navigate to "Main" -> "Upload"
    And I upload the "ItemAVLUI" "ItemUpload" with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Main" -> "Upload"
    And I upload the "CurrentForecastUI" "ApprovedForecast" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "3" FC Extend forecast value on search filter results
    When I navigate to "Main" -> "Upload"
    And I upload the "CurrentForecastUI" "ApprovedForecast_closed" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "CLOSED" FC status on search filter results
    Then I verify "15" FC Extend forecast value on search filter results
    
  #C668147 - Regression 21.1
  Scenario: Upload simple forecast template downloaded from forecast using Business Admin role
    Given I log into HarmonyMTCM as "mtcmUser" with "busadmin5"
    When I navigate to "Main" -> "Upload"
    And I upload the "ItemAVLUI" "ItemUpload" with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Main" -> "Upload"
    And I upload the "CurrentForecastUI" "ApprovedForecast" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "3" FC Extend forecast value on search filter results
    When I navigate to "Main" -> "Upload"
    And I upload the "CurrentForecastUI" "ApprovedForecast_closed" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "CLOSED" FC status on search filter results
    Then I verify "15" FC Extend forecast value on search filter results
    
  #C668148 - Regression 21.1
  Scenario: Upload simple forecast template downloaded from forecast using Admin role
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Main" -> "Upload"
    And I upload the "ItemAVLUI" "ItemUpload" with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Main" -> "Upload"
    And I upload the "CurrentForecastUI" "ApprovedForecast" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "3" FC Extend forecast value on search filter results
    When I navigate to "Main" -> "Upload"
    And I upload the "CurrentForecastUI" "ApprovedForecast_closed" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "CLOSED" FC status on search filter results
    Then I verify "15" FC Extend forecast value on search filter results
    
   
   #C668153 - Regression 21.1
  Scenario: Upload simple forecast against FG through MassUpdate CostForecast By FG using GCM role
   Given I log into HarmonyMTCM as "mtcmUser" with "gcm5"
    When I navigate to "Main" -> "Upload"
    And I upload the "ItemAVLUI" "MultipleitemUplaodforForecast" with "uploadmultipleItemForForecast" & verify "" "success"
    Then I verify the "uploadmultipleItemForForecast" on the Cost Records page
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I enter "fcItem1" and "fcItem2" and "fcItem3" on Multiple "itemNumbers" textfield on GCM role
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    And I select all rows
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group for Forecast "ForecastFGGCM" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Main" -> "Upload"
    And I upload the "MassUpdateCostForecastByFGUI" "ApprovedForecast" with "uploadItemForForecastbyFGGCM" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGGCM"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem1" and "fcItem2" and "fcItem3" on FC itemNumbers on search filter results
    
   #C668154 - Regression 21.1
  Scenario: Upload simple forecast against FG through MassUpdate CostForecast By FG using Super GCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "supergcm5"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I enter "fcItem4" and "fcItem5" and "fcItem6" on Multiple "itemNumbers" textfield on SuperGCM role
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    And I select all rows
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group for Forecast "ForecastFGSuperGCM" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Main" -> "Upload"
    And I upload the "MassUpdateCostForecastByFGUI" "ApprovedForecast" with "uploadItemForForecastbyFGSuperGCM" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGSuperGCM"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem4" and "fcItem5" and "fcItem6" on FC itemNumbers on search filter results
    
  # C668155 - Regression 21.1
  Scenario: Upload simple forecast against FG through MassUpdate CostForecast By FG using Administrator role
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I enter "fcItem7" and "fcItem8" and "fcItem9" on Multiple "itemNumbers" textfield on adm role
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    And I select all rows
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group for Forecast "ForecastFGAdmin" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Main" -> "Upload"
    And I upload the "MassUpdateCostForecastByFGUI" "ApprovedForecast" with "uploadItemForForecastbyFGadm" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGAdmin"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem7" and "fcItem8" and "fcItem9" on FC itemNumbers on search filter results
   
   # C668156 - Regression 21.1 
  Scenario: Upload simple forecast against Parent FG using GCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm5"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I enter "fcItem10" and "fcItem11" on Multiple item "itemNumbers" textfield on GCM role
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    And I select all rows
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group for Forecast "ForecastFGGCM1" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set FGgroup name as "ForecastFGGCM"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "8" seconds
    And I click on Create parent group
    And I save FGparent name as "ForecastPFGGCM"
    Then I verify the "Parent Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set FGgroup name as "ForecastFGGCM1"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "8" seconds
    And I click on Assign parent group
    And I wait till the page loads for "15" seconds
    And I set PFgroup name on parent edit page "ForecastPFGGCM"
    And I wait till the page loads for "15" seconds
    Then I verify the "Parent Group Saved" successful message
    When I navigate to "Main" -> "Upload"
    And I upload the "MassUpdateCostForecastByParentFGUI" "ApprovedForecast" with "uploadItemForForecastbyPFGGCM" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGGCM"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem1" and "fcItem2" and "fcItem3" on FC itemNumbers on search filter results
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGGCM1"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem10" and "fcItem11" on PFC itemNumbers on search filter results
    
    #C668157 - Regression 21.1
   Scenario: Upload simple forecast against Parent FG using SuperGCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "supergcm5"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I enter "fcItem12" and "fcItem13" on Multiple item "itemNumbers" textfield on SuperGCM role
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    And I select all rows
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group for Forecast "ForecastFGSuperGCM1" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set FGgroup name as "ForecastFGSuperGCM"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "8" seconds
    And I click on Create parent group
    And I save FGparent name as "ForecastPFGSuperGCM"
    Then I verify the "Parent Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set FGgroup name as "ForecastFGSuperGCM1"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "8" seconds
    And I click on Assign parent group
    And I wait till the page loads for "15" seconds
    And I set PFgroup name on parent edit page "ForecastPFGSuperGCM"
    And I wait till the page loads for "15" seconds
    Then I verify the "Parent Group Saved" successful message
    When I navigate to "Main" -> "Upload"
    And I upload the "MassUpdateCostForecastByParentFGUI" "ApprovedForecast" with "uploadItemForForecastbyPFGSGCM" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGSuperGCM"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem4" and "fcItem5" and "fcItem6" on FC itemNumbers on search filter results
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGSuperGCM1"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem12" and "fcItem13" on PFC itemNumbers on search filter results
    
     #C668158 - Regression 21.1
   Scenario: Upload simple forecast against Parent FG using Admin role
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I enter "fcItem14" and "fcItem15" on Multiple item "itemNumbers" textfield on Adm role
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    And I select all rows
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group for Forecast "ForecastFGAdmin1" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set FGgroup name as "ForecastFGAdmin"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "8" seconds
    And I click on Create parent group
    And I save FGparent name as "ForecastPFGAdm"
    Then I verify the "Parent Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set FGgroup name as "ForecastFGAdmin1"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "8" seconds
    And I click on Assign parent group
    And I wait till the page loads for "15" seconds
    And I set PFgroup name on parent edit page "ForecastPFGAdm"
    And I wait till the page loads for "15" seconds
    Then I verify the "Parent Group Saved" successful message
    When I navigate to "Main" -> "Upload"
    And I upload the "MassUpdateCostForecastByParentFGUI" "ApprovedForecast" with "uploadItemForForecastbyPFGadm" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGAdmin"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem7" and "fcItem8" and "fcItem9" on FC itemNumbers on search filter results
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set functional group name on forecast UI page "ForecastFGAdmin1"
    And I click on "Apply" Button
    And I select all rows
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "fcItem14" and "fcItem15" on PFC itemNumbers on search filter results