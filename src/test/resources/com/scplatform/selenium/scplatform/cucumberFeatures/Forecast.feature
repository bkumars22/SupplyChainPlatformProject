@HarmonyForecast
Feature: Forecast workflow

  Scenario Outline: Upload Item with different values and verify success message, then verify on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"

    Examples: 
      | dataFile  | fileName   | action           | msg | msgType |
      | ItemAVLUI | ItemUpload | uploadItemForTAM |     | success |

  @skip
  Scenario: Upload ApprovedForecast1 with zero values and verify the active period validations errors on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    And I upload the "CurrentForecastUI" "ApprovedForecast_Validations" with "uploadItemForForecast" & verify "ValidationError:The forecast must have a value in at least 1 active period(s)." "error"
    #Then I verify the "ValidationError:The forecast must have a value in at least 1 active period(s)." error message displayed on UploadPage

  @skip
  Scenario Outline: Verify the Adjustable Forecast error validaitons on Upload page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"

    Examples: 
      | dataFile             | fileName           | action                   | msg | msgType |
      | AdjustableForecastUI | AdjustableForecast | uploadAdjustableForecast |     | error   |

  @skip
  Scenario Outline: Upload ApprovedForecast with different values and verify success message, then verify on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"

    Examples: 
      | dataFile          | fileName         | action                | msg | msgType |
      | CurrentForecastUI | ApprovedForecast | uploadItemForForecast |     | success |

  Scenario: Create a new forecast and validate through UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    When I click on "Next" Button
    When I set forecast value "100" on row "1"
    Then I click on forecast arrow forward icon to copy the values in to eleven buckets
    When I click on the save button on forecast page
    Then I verify "APPROVED" FC status on search filter results

  Scenario: Create new forecast and copy and delete Forecast on Current Tab Current Tab.
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    When I click on "Next" Button
    When I set forecast value "100" on row "1"
    Then I click on forecast arrow forward icon to copy the values in to eleven buckets
    When I click on the save button on forecast page
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on "Copy" Button
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    When I set forecast value "100" on row "1"
    Then I click on forecast arrow forward icon to copy the values in to eleven buckets
    And I click on "Submit" Button

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    And I wait "50" seconds
    Then I should be landed on "Search Forecast" page
    Then I verify "No records found to display" message
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    And I wait "10" seconds  
    Then I should be landed on "Manage Parent" page
    When I navigate to "Cost Forecast" -> "Search Forecast"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I should be landed on "Search Forecast" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "00025"
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I should be landed on "Search Forecast" page
    And I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    Then I verify "00025" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait "50" seconds  
    #And I wait till the page loads for "30" seconds
    Then I should be landed on "Search Forecast" page
    And I verify search filter results are displayed
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    Then I should be landed on "Search Forecast" page
    Then I verify "" on "itemNumber" textfield

 #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
   Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Forecast Variance Report"
    And I click on "Clear" Button
    And I set the itemNumber as "00025"
    And I select "WW" on the Region list
    And I wait "10" seconds
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I should be landed on "Cost Forecast Variance" page
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I should be landed on "Manage Parent" page
    When I navigate to "Cost Forecast" -> "Forecast Variance Report"
    Then I verify "00025" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I should be landed on "Cost Forecast Variance" page
    Then I verify "" on "itemNumber" textfield
    Then I should be landed on "Cost Forecast Variance" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Forecast Variance Report"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I select "WW" on the Region list
    And I wait "10" seconds
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I should be landed on "Cost Forecast Variance" page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Forecast Variance Report"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    And I select "WW" on the Region list
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I should be landed on "Cost Forecast Variance" page
    And I wait "50" seconds  
    #And I wait till the page loads for "30" seconds
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Forecast Variance Report"
    Then I should be landed on "Cost Forecast Variance" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section - Data retention not applicable on New forecast
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I should be landed on "New Forecast" page
    Then I verify search filter results are displayed
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I should be landed on "Manage Parent" page
    When I navigate to "Cost Forecast" -> "New Forecast"
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section- Data retention not applicable on New forecast
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I should be landed on "New Forecast" page
    Then I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    Then I verify "" on "itemNumber" textfield

  #scplatform-4843 Cost Forecast: 'OK' button in error message is not working.
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    And I wait "50" seconds  
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Next" Button
    And I enter "24" on extended Forecast Term textfield
    And I click on the save button
    And I wait "50" seconds  
    Then I verify the "Unable to save forecast record: Validation Error" error message displayed

  #scplatform-4713 Cost Forecast: No Parent Group in search criteria
  Scenario: Verify field names are correctly displayed
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I verify labelName "CFG" on the loaded page
    Then I verify labelName "Parent Functional Group" on the loaded page
    Then I verify labelName "Functional Groups" on the loaded page
    When I navigate to "Cost Forecast" -> "Search Forecast"
    Then I verify labelName "CFG" on the loaded page
    When I click on "Clear" Button
    And I enter "CS-FG-1" on "cfgName" textfield
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I verify labelName "Parent Functional Group" on the loaded page
    Then I verify labelName "Functional Groups" on the loaded page
    
  #C668151 - Regression 21.1
  #scplatform-4783 Missing FG & Parent name in cost forecast SGF download file
  @skip
  Scenario: Verify field names are correctly displayed and data for adjustable Data
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    When I click on "Clear" Button
    And I set the itemNumber as "0002H"
    And I click on "Apply" Button
    And I wait "50" seconds  
    And I click on download Button and verify the result for "Forecast" for "verifyDataAdjustable"

  @skip
  Scenario: Verify data for current Forecast Data
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    When I click on "Clear" Button
    And I set the itemNumber as "0002H"
    And I click on "Apply" Button
    And I wait "50" seconds  
    And I click on download Button and verify the result for "Forecast" for "verifyDataCurrent"

  #scplatform-4843 Cost Forecast: 'OK' button in error message is not working.
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    And I wait "30" seconds  
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Next" Button
    And I enter "24" on extended Forecast Term textfield
    And I click on the save button
    And I wait "30" seconds  
    Then I verify the "Unable to save forecast record: Validation Error" error message displayed

  #scplatform-4713 Cost Forecast: No Parent Group in search criteria
  Scenario: Verify field names are correctly displayed
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "New Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I verify labelName "CFG" on the loaded page
    Then I verify labelName "Parent Functional Group" on the loaded page
    Then I verify labelName "Functional Groups" on the loaded page
    When I navigate to "Cost Forecast" -> "Search Forecast"
    Then I verify labelName "CFG" on the loaded page
    When I click on "Clear" Button
    And I enter "CS-FG-1" on "cfgName" textfield
    And I click on "Apply" Button
    And I wait "50" seconds  
    Then I verify labelName "Parent Functional Group" on the loaded page
    Then I verify labelName "Functional Groups" on the loaded page

  #scplatform-4783 Missing FG & Parent name in cost forecast SGF download file
  @skip
  Scenario: Verify field names are correctly displayed and data for adjustable Data
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    When I click on "Clear" Button
    And I set the itemNumber as "0002H"
    And I click on "Apply" Button
    And I wait "50" seconds
    And I click on download Button and verify the result for "Forecast" for "verifyDataAdjustable"

  @skip
  Scenario: Verify data for current Forecast Data
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    When I click on "Clear" Button
    And I set the itemNumber as "0002H"
    And I click on "Apply" Button
    And I wait "50" seconds
    And I click on download Button and verify the result for "Forecast" for "verifyDataCurrent"

     #C668140  Regression 21.1
  Scenario: Simple Forecast_creation via UI with Business Admin role
    Given I log into HarmonyMTCM as "mtcmUser" with "busadmin5"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUpload" xlsx with "uploadItemForTAM" & verify "" "success"
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
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUpload" xlsx with "uploadItemForTAM" & verify "" "success"
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
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUpload" xlsx with "uploadItemForTAM" & verify "" "success"
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

 #C668145  Regression 21.1
  @skip
  Scenario: Upload simple forecast template downloaded from forecast using GCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm4"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUpload" xlsx with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    And I upload the "CurrentForecastUI" "ApprovedForecast" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I wait "30" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "3" FC Extend forecast value on search filter results
    When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    And I upload the "CurrentForecastUI" "ApprovedForecast_closed" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I wait "30" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "CLOSED" FC status on search filter results
    Then I verify "15" FC Extend forecast value on search filter results

  #C668146 - Regression 21.1
  @skip
  Scenario: Upload simple forecast template downloaded from forecast using Super GCM role
    Given I log into HarmonyMTCM as "mtcmUser" with "supergcm5"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUpload" xlsx with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    And I upload the "CurrentForecastUI" "ApprovedForecast" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I wait "30" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "3" FC Extend forecast value on search filter results
    #When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    #And I upload the "CurrentForecastUI" "ApprovedForecast_closed" with "uploadItemForForecast" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set the itemNumber as "AutoItem"
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #Then I verify "CLOSED" FC status on search filter results
    #Then I verify "15" FC Extend forecast value on search filter results
    
  #C668147 - Regression 21.1
  @skip
  Scenario: Upload simple forecast template downloaded from forecast using Business Admin role
    Given I log into HarmonyMTCM as "mtcmUser" with "busadmin5"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUpload" xlsx with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    And I upload the "CurrentForecastUI" "ApprovedForecast" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I wait "30" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "3" FC Extend forecast value on search filter results
    #When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    #And I upload the "CurrentForecastUI" "ApprovedForecast_closed" with "uploadItemForForecast" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set the itemNumber as "AutoItem"
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #Then I verify "CLOSED" FC status on search filter results
    #Then I verify "15" FC Extend forecast value on search filter results
    
  #C668148 - Regression 21.1
  @skip
  Scenario: Upload simple forecast template downloaded from forecast using Admin role
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUpload" xlsx with "uploadItemForTAM" & verify "" "success"
    Then I verify the "uploadItemForTAM" on the Cost Records page
    When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    And I upload the "CurrentForecastUI" "ApprovedForecast" with "uploadItemForForecast" & verify "" "success"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I wait "30" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results
    Then I verify "3" FC Extend forecast value on search filter results
    #When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    #And I upload the "CurrentForecastUI" "ApprovedForecast_closed" with "uploadItemForForecast" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set the itemNumber as "AutoItem"
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #Then I verify "CLOSED" FC status on search filter results
    #Then I verify "15" FC Extend forecast value on search filter results
    
   
   #C668153 - Regression 21.1
  #Scenario: Upload simple forecast against FG through MassUpdate CostForecast By FG using GCM role
   #Given I log into HarmonyMTCM as "mtcmUser" with "gcm4"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "ItemAVLUI" "MultipleitemUplaodforForecast" with "uploadmultipleItemForForecast" & verify "" "success"
    #Then I verify the "uploadmultipleItemForForecast" on the Cost Records page
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I enter "fcItem1" and "fcItem2" and "fcItem3" on Multiple "itemNumbers" textfield on GCM role
    #And I click on "Apply" Button
    #And I wait "200" seconds
    #And I wait till the page loads for "200" seconds
    #And I select all rows
    #And I click on "Create Group" Button
    #And I "create" the items to the Functional Group for Forecast "ForecastFGGCM" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    #And I upload the "MassUpdateCostForecastByFGUI" "ApprovedForecast" with "uploadItemForForecastbyFGGCM" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGGCM"
    #And I click on "Apply" Button
    #And I wait "50" seconds
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem1" and "fcItem2" and "fcItem3" on FC itemNumbers on search filter results
    
   #C668154 - Regression 21.1
  #Scenario: Upload simple forecast against FG through MassUpdate CostForecast By FG using SuperGCM role
    #Given I log into HarmonyMTCM as "mtcmUser" with "supergcm5"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I enter "fcItem4" and "fcItem5" and "fcItem6" on Multiple "itemNumbers" textfield on SuperGCM role
    #And I click on "Apply" Button
    #And I wait "200" seconds
    #And I wait till the page loads for "200" seconds
    #And I select all rows
    #And I click on "Create Group" Button
    #And I "create" the items to the Functional Group for Forecast "ForecastFGSuperGCM" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    #And I upload the "MassUpdateCostForecastByFGUI" "ApprovedForecast" with "uploadItemForForecastbyFGSuperGCM" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGSuperGCM"
    #And I click on "Apply" Button
    #And I wait "30" seconds
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem4" and "fcItem5" and "fcItem6" on FC itemNumbers on search filter results
    
  # C668155 - Regression 21.1
  #Scenario: Upload simple forecast against FG through MassUpdate CostForecast By FG using Administrator role
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I enter "fcItem7" and "fcItem8" and "fcItem9" on Multiple "itemNumbers" textfield on adm role
    #And I click on "Apply" Button
    #And I wait "200" seconds
    #And I wait till the page loads for "20" seconds
    #And I select all rows
    #And I click on "Create Group" Button
    #And I "create" the items to the Functional Group for Forecast "ForecastFGAdmin" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    #And I upload the "MassUpdateCostForecastByFGUI" "ApprovedForecast" with "uploadItemForForecastbyFGadm" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGAdmin"
    #And I click on "Apply" Button
    #And I wait "30" seconds
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem7" and "fcItem8" and "fcItem9" on FC itemNumbers on search filter results
   
   # C668156 - Regression 21.1 
  #Scenario: Upload simple forecast against Parent FG using GCM role
    #Given I log into HarmonyMTCM as "mtcmUser" with "gcm4"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I enter "fcItem10" and "fcItem11" on Multiple item "itemNumbers" textfield on GCM role
    #And I click on "Apply" Button
    #And I wait "100" seconds
    #And I wait till the page loads for "100" seconds
    #And I select all rows
    #And I click on "Create Group" Button
    #And I "create" the items to the Functional Group for Forecast "ForecastFGGCM1" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set FGgroup name as "ForecastFGGCM1"
    #And I click on "Apply" Button
    #And I wait "30" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I wait "30" seconds
    #And I wait till the page loads for "8" seconds
    #And I click on Create parent group
    #And I save FGparent name as "ForecastPFGGCM"
    #Then I verify the "Parent Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set FGgroup name as "ForecastFGGCM1"
    #And I click on "Apply" Button
    #And I wait "30" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I wait "10" seconds
    #And I wait till the page loads for "8" seconds
    #And I click on Assign parent group
    #And I wait till the page loads for "15" seconds
    #And I set PFgroup name on parent edit page "ForecastPFGGCM"
    #And I wait till the page loads for "15" seconds
    #Then I verify the "Parent Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    #And I upload the "MassUpdateCostForecastByParentFGUI" "ApprovedForecast" with "uploadItemForForecastbyPFGGCM" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGGCM"
    #And I click on "Apply" Button
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem1" and "fcItem2" and "fcItem3" on FC itemNumbers on search filter results
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGGCM1"
    #And I click on "Apply" Button
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem10" and "fcItem11" on PFC itemNumbers on search filter results
    
    #C668157 - Regression 21.1
   #Scenario: Upload simple forecast against Parent FG using SuperGCM role
    #Given I log into HarmonyMTCM as "mtcmUser" with "supergcm5"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I enter "fcItem12" and "fcItem13" on Multiple item "itemNumbers" textfield on SuperGCM role
    #And I click on "Apply" Button
    #And I wait till the page loads for "20" seconds
    #And I select all rows
    #And I click on "Create Group" Button
    #And I "create" the items to the Functional Group for Forecast "ForecastFGSuperGCM1" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set FGgroup name as "ForecastFGSuperGCM1"
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I wait till the page loads for "8" seconds
    #And I click on Create parent group
    #And I save FGparent name as "ForecastPFGSuperGCM"
    #Then I verify the "Parent Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set FGgroup name as "ForecastFGSuperGCM1"
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I wait till the page loads for "8" seconds
    #And I click on Assign parent group
    #And I wait till the page loads for "15" seconds
    #And I set PFgroup name on parent edit page "ForecastPFGSuperGCM"
    #And I wait till the page loads for "15" seconds
    #Then I verify the "Parent Group Saved" successful message
    #When I navigate to "Main" -> "Upload"
    #And I upload the "MassUpdateCostForecastByParentFGUI" "ApprovedForecast" with "uploadItemForForecastbyPFGSGCM" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGSuperGCM"
    #And I click on "Apply" Button
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem4" and "fcItem5" and "fcItem6" on FC itemNumbers on search filter results
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGSuperGCM1"
    #And I click on "Apply" Button
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem12" and "fcItem13" on PFC itemNumbers on search filter results
    
     #C668158 - Regression 21.1
   #Scenario: Upload simple forecast against Parent FG using Admin role
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I enter "fcItem14" and "fcItem15" on Multiple item "itemNumbers" textfield on Adm role
    #And I click on "Apply" Button
    #And I wait "120" seconds
    #And I wait till the page loads for "120" seconds
    #And I select all rows
    #And I click on "Create Group" Button
    #And I "create" the items to the Functional Group for Forecast "ForecastFGAdmin1" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set FGgroup name as "ForecastFGAdmin"
    #And I click on "Apply" Button
    #And I wait "50" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I wait till the page loads for "8" seconds
    #And I click on Create parent group
    #And I save FGparent name as "ForecastPFGAdm"
    #Then I verify the "Parent Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set FGgroup name as "ForecastFGAdmin1"
    #And I click on "Apply" Button
    #And I wait "50" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I wait "8" seconds
    #And I wait till the page loads for "8" seconds
    #And I click on Assign parent group
    #And I wait "15" seconds
    #And I wait till the page loads for "15" seconds
    #And I set PFgroup name on parent edit page "ForecastPFGAdm"
    #And I wait "15" seconds
    #And I wait till the page loads for "15" seconds
    #Then I verify the "Parent Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "Cost Forecast"
    #And I upload the "MassUpdateCostForecastByParentFGUI" "ApprovedForecast" with "uploadItemForForecastbyPFGadm" & verify "" "success"
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGAdmin"
    #And I click on "Apply" Button
    #And I wait "10" seconds
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem7" and "fcItem8" and "fcItem9" on FC itemNumbers on search filter results
    #When I navigate to "Cost Forecast" -> "Search Forecast"
    #And I click on "Clear" Button
    #And I set functional group name on forecast UI page "ForecastFGAdmin1"
    #And I click on "Apply" Button
    #And I wait "10" seconds
    #And I select all rows
    #Then I verify "APPROVED" FC status on search filter results
    #Then I verify "fcItem14" and "fcItem15" on PFC itemNumbers on search filter results
    
    #PDSUPPORT-25529
    Scenario: Validate scroll bar visible after mouse hovering through simple forecast
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I wait "50" seconds
    Then I should be landed on "Search Forecast" page
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    When I click on "Next" Button
    Then I should be landed on "Forecast Details" page
    When I move To an Element with id "fcTableCUR_data"
		Then I verify scroll bar is visible under Forecast

		Scenario: Verify Search forcast Approved validations through Upload
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I verify labelName "Member of Group" not found on the loaded page
    Then I verify labelName "Last Change By" on the loaded page
    And I set the itemNumber as "AutoItem"
    And  I wait "300" seconds
    And I click on "Apply" Button
    And  I wait "30" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" FC status on search filter results

  #C668144 - Regression 21.1
  	Scenario: Verify Search forcast Closed validations through Upload
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser2"
    When I navigate to "Cost Forecast" -> "Search Forecast"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And  I wait "300" seconds
    And I click on "Apply" Button
		And  I wait "30" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "CLOSED" FC status on search filter results