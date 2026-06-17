@HarmonyPriceTam
Feature: PriceTam Workflow Test Plan

  Scenario: Upload the price TAM item and verify on upload UI
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadForCR" xlsx with "uploadItemForPriceTAM" & verify "" "success"
    Then I verify the "uploadItemForPriceTAM" on the Cost Records page

  @skip
  Scenario: Create a new XLOB FG and verify on UI
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "PriceTAM"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the XLOB Functional Group "XLOBPriceTAM" with "Save All"
    Then I verify the "Functional Group Saved" successful message

  Scenario: Verify XLOB Price TAM FG details on Price TAM UI
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set group name as "XLOBPriceTAM"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    When I verify "XLOBPriceTAM" FG name on price TAM results page

  #Scenario: Upload the monthly price tam template and validate the data on price tam UI
    #Given I log into HarmonyMTCM as "GCM" with "gcm5"
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMMonthlyException" "PriceTAMMonthlyExceptionUpload" with "uploadXLOBFGForPriceTAM" & verify "" "success"

  Scenario: Validate the uploaded price tam bucket allocaiton data on UI
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set group name as "XLOBPriceTAM"
    And I click on "Apply" Button
    And I wait till the page loads for "150" seconds
    Then I verify "PriceTAM" FG name on price TAM results page
    Then I verify "XLOBPriceTAM" FG name on price TAM results page
    Then I verify "SERCOM desc" Supplier on price TAM results page
    Then I verify "BUY" CostType on price TAM results page
    Then I verify "WW" Destination on price TAM results page
    Then I verify "WW" SiteTAM on price TAM results page
    Then I verify "34" PriceValue on price TAM results page
#
  #Scenario: Validate Price TAM details on Costrecords UI with approved state
    #Given I log into HarmonyMTCM as "GCM" with "gcm5"
    #When I navigate to "Supply Collaboration" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I set the itemNumber as "PriceTAM"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "APPROVED" status on search filter results

  #Scenario: Upload the monthly price tam past bucket template and validate the data on price tam UI
    #Given I log into HarmonyMTCM as "GCM" with "gcm5"
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMMonthlyException" "PriceTAMPastBucketValues" with "uploadXLOBFGForPriceTAM" & verify "" "success"

  Scenario: Validate the uploaded price tam bucket allocaiton data on UI
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set group name as "XLOBPriceTAM"
    And I select "Yes" on "Past Bucket Visible" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "150" seconds
    Then I verify "XLOBPriceTAM" FG name on price TAM results page
    Then I verify "50" PriceValue on price TAM results page

  #Scenario: Upload the Quarterly price tam and validate the data on price tam UI
    #Given I log into HarmonyMTCM as "GCM" with "gcm5"
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMQuarterlyException" "PriceTAMQuarterlyException" with "uploadXLOBFGForPriceTAM" & verify "" "success"

  Scenario: Validate the uploaded Quarterly price tam bucket allocaiton data on UI
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set group name as "XLOBPriceTAM"
    And I select "Quarterly" on "Bucket Type" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "150" seconds
    Then I verify "XLOBPriceTAM" FG name on price TAM results page
    Then I verify "60" PriceValue on price TAM results page
    And I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Supply Allocation" -> "PriceTAM"
    And I select filter "Search by Project name" on Price Tam
    And I click on "Clear" Button
    And I enter "test" on "projectName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    And I wait till the page loads for "10" seconds
    When I navigate to "Supply Allocation" -> "PriceTAM"
    And I wait till the page loads for "10" seconds
    Then I verify "test" on "projectName" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "500" seconds
    Then I verify "" on "projectName" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Supply Allocation" -> "PriceTAM"
    Then I verify "" on "projectName" textfield

  #scplatform-5014 MPN (PriceTAM) Download / template changes to view or hide past data
  @skip
  Scenario Outline: Verify PriceTAM download template for view pastData
    Given I log into HarmonyMTCM as "GCM" with "gcm5"
    When I navigate to "Supply Allocation" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I enter "IRETEX" on "supplier" textfield
    And I select "Item" on the ItemType list
    And I select "Yes" on "Past Bucket Visible" Combobox
    And I click on "Apply" Button
    Then I verify the "The last effective approve pricing and the allocation for month are displayed" successful message
    And I verify the search results itemNumber as "JD002"
    And I click on download Button and verify the result for "PriceTam" for "verifyPastData"
    
    Examples: 
      | pastData         |
      | verifyPastData   |
      | verifyNoPastData |

  Scenario: Verify items which are not in any XLOB CFG should be restricted
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser16"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadForCommodity" xlsx with "uploadItemForPriceTAMCommodity" & verify "" "success"
    Then I verify the "uploadItemForPriceTAMCommodity" on the Cost Records page
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set the itemNumber as "PriceTAMCommodity"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify "No records found to display, Refine your search in the filters" message

  Scenario: To verify restricted the download iocn on price tam search of items, at the XLOB CFG level, based upon the Commodity Profile of the user
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser16"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set the itemNumber as "PriceTAMCommodity"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify "file_download" Price TAM download icon disbaled on UI page

  @skip
  Scenario: To verify restricted the search of items at the Price TAM, based upon the Commodity Profile of the user.
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser16"
    When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "PriceTAMCommodity"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the XLOB Functional Group "XLOBPriceTAMCommodity" with "Save All"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set the itemNumber as "PriceTAMCommodity"
    And I set group name as "XLOBPriceTAMCommodity"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify "No records found to display, Refine your search in the filters" message

  Scenario: To verify restricted the download iocn on price tam search of items, at the XLOB CFG level, based upon the Commodity Profile of the user
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser16"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set group name as "XLOBPriceTAMCommodity"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify "file_download" Price TAM download icon disbaled on UI page

#  Scenario: Create the XLOB FG commodity at the XLOB CFG level
#    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser16"
#    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
#    And I click on "Clear" Button
#    And I set the itemNumber as "PriceTAMCommodity"
#    And I click on "Apply" Button
#    And I wait till the page loads for "50" seconds
#    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
#    And I click on "Create Group" Button
#    And I "create" the items to the XLOB Price TAM Commodity Functional Group "XLOBPriceTAMCommodity1" with "Save All"
#    Then I verify the "Functional Group Saved" successful message

  Scenario: Verify the commodity restriction for the uploaded items at the XLOB CFG level
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser16"
    When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    And I upload the PriceTAM "PriceTAMMonthlyException" "PriceTAMCommodityUpload" with "uploadXLOBFGForPriceTAMCommodity" & verify "" "error"
    Then I verify the "Price TAM commodity profile restriction" error message displayed on UploadPage

  Scenario: Verify Price TAM template validations through upload process
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser16"
    When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    And I upload the PriceTAM "PriceTAMMonthlyException" "PriceTAMwithoutCostTypeUpload" with "uploadXLOBFGForPriceTAMCommodity" & verify "" "error"
    Then I verify the "Empty file or missing upload type for all data" error message displayed on UploadPage

  Scenario: Verify Price TAM error validations through upload process
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser16"
    When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    And I upload the PriceTAM "PriceTAMMonthlyException" "PriceTAMTemplateValidation" with "uploadXLOBFGForPriceTAMCommodity" & verify "" "error"
    Then I verify the "No value specified for Destination Site Price field" error message displayed on UploadPage
    Then I verify the "No value specified for cost type field" error message displayed on UploadPage
    Then I verify the "No value specified for FG name field" error message displayed on UploadPage
    Then I verify the "No value specified for FG type field" error message displayed on UploadPage
    Then I verify the "No value specified for site Tam field" error message displayed on UploadPage

  #C668280 - Regression 21.1
  #Scenario: Upload Price TAM with multiple MPN for a single EMC item associated with 1 supplier, all item allocation =100% via Price TAM Monthly Exception Excel ( New Item )
    #Given I log into HarmonyMTCM as "GCM" with "gcm5"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "ItemAVLUI" "EMCItemUpload" with "emcuploadItemForPriceTAM" & verify "" "success"
    #Then I verify the "emcuploadItemForPriceTAM" on the Cost Records page
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set the itemNumber as "EMCPriceTAM"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Group" Button
    #And I "create" the items to the XLOB Functional Group "EMCXLOBPriceTAM" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMMonthlyException" "EMCPriceTAMMonthlyExceptionUpload" with "emcuploadXLOBFGForPriceTAM" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set FG name as "EMCXLOBPriceTAM"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "EMCPriceTAM" itemNumber on search filter results
    #Then I verify "XLOBPriceTAM" FGname on search filter results
    #Then I verify "MPN1" mpn on search filter results
    #Then I verify "MPN2" mpn on search filter results
    #Then I verify "S.E.L. PRINT" supplier on search filter results

  #C668281- Regression 21.1
  #Scenario: Upload Price TAM with multiple MPN for a single DELL item associated with 1 supplier, with mandatory buckets and remove remaining of buckets via Price TAM Monthly Exception Excel
    #Given I log into HarmonyMTCM as "GCM" with "gcm5"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "ItemAVLUI" "ItemUploadForCR" with "delluploadItemForPriceTAM" & verify "" "success"
    #Then I verify the "delluploadItemForPriceTAM" on the Cost Records page
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set the itemNumber as "DELLPriceTAM"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Group" Button
    #And I "create" the items to the XLOB Functional Group "DELLXLOBPriceTAM" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMMonthlyException" "DELLPriceTAMMonthlyExceptionUpload" with "delluploadXLOBFGForPriceTAM" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set FG name as "DELLXLOBPriceTAM"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "DELLPriceTAM" itemNumber on search filter results
    #Then I verify "DELLXLOBPriceTAM" FGname on search filter results
    #Then I verify "MPN1" mpn on search filter results
    #Then I verify "MPN2" mpn on search filter results
    #Then I verify "SERCOM desc" supplier on search filter results
    #Then I verify "34" Pricebuckets on price TAM results page
    #Then I verify "20" Pricebuckets on price TAM results page
    #Then I verify "100" Pricebuckets on price TAM results page

  #C668282 - Regression 21.1
  #Scenario: Upload Price TAM with multiple MPN for a single DELL item associated with 1 supplier, with mandatory buckets and remove remaining of buckets via Price TAM Monthly Excel
    #Given I log into HarmonyMTCM as "GCM" with "gcm5"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "ItemAVLUI" "ItemUploadForCR" with "delluploadItemForPriceTAMMonthly" & verify "" "success"
    #Then I verify the "delluploadItemForPriceTAMMonthly" on the Cost Records page
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set the itemNumber as "DELLPriceTAMMonthly"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Group" Button
    #And I "create" the items to the XLOB Functional Group "DELLXLOBPriceTAMMonthly" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMMonthly" "DELLPriceTAMMonthlyUpload" with "delluploadXLOBFGForPriceTAMMonthly" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set FG name as "DELLXLOBPriceTAMMonthly"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "DELLPriceTAMMonthly" itemNumber on search filter results
    #Then I verify "DELLXLOBPriceTAMMonthly" FGname on search filter results
    #Then I verify "MPN1" mpn on search filter results
    #Then I verify "MPN2" mpn on search filter results
    #Then I verify "SERCOM desc" supplier on search filter results
    #Then I verify "34" Pricebuckets on price TAM results page
    #Then I verify "20" Pricebuckets on price TAM results page
    #Then I verify "100" Pricebuckets on price TAM results page

  #C668283 - Regression 21.1
  #Scenario: Upload Price TAM with multiple MPN for a single DELL item associated with 1 supplier, with mandatory buckets and remove remaining of buckets via Price TAM Quarterly Exception Excel
    #Given I log into HarmonyMTCM as "GCM" with "gcm5"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "ItemAVLUI" "ItemUploadForCR" with "delluploadItemForPriceTAMQuarterly" & verify "" "success"
    #Then I verify the "delluploadItemForPriceTAMQuarterly" on the Cost Records page
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set the itemNumber as "DELLPriceTAMQuarterly"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Group" Button
    #And I "create" the items to the XLOB Functional Group "DELLXLOBPriceTAMQuarterly" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMQuarterlyException" "DELLPriceTAMQuarterlyExceptionUpload" with "delluploadXLOBFGForPriceTAMQuarterly" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set FG name as "DELLXLOBPriceTAMQuarterly"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "DELLPriceTAMQuarterly" itemNumber on search filter results
    #Then I verify "DELLXLOBPriceTAMQuarterly" FGname on search filter results
    #Then I verify "MPN1" mpn on search filter results
    #Then I verify "MPN2" mpn on search filter results
    #Then I verify "SERCOM desc" supplier on search filter results
    #Then I verify "34" Pricebuckets on price TAM results page
    #Then I verify "45" Pricebuckets on price TAM results page
    #Then I verify "100" Pricebuckets on price TAM results page

  @skip
  Scenario: Create a new FG XLOB and validate on XLOB Delete template
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadforXLOB" with "XLOBDeleteTemplateItems" & verify "" "success"
    Then I verify the "XLOBDeleteTemplateItems" on the Cost Records page
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "XLOBDeleteItem"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the XLOB Functional Group "XLOBFGDT" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "XLOB Delete Template"
    And I click on "Clear" Button
    And I set the functionalgroup name as "XLOBFGDT"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify "XLOBFGDT" FGname on search filter results

  Scenario: Create a new FG XLOB and validate item number with FG XLOBd details on XLOB Delete template page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "XLOB Delete Template"
    And I click on "Clear" Button
    And I set the itemNumber as "XLOBDeleteItem"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify "XLOBFGDT" FGname on search filter results

  #Scenario: Create multiple FGs with single XLOB item and validate XLOB details on UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set the itemNumber as "XLOBDeleteItem"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Group" Button
    #And I "create" the items to the XLOB Functional Group "XLOBFGDT1" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "XLOB Delete Template"
    #And I click on "Clear" Button
    #And I set the functionalgroup name as "XLOBFGDT1"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #Then I verify "XLOBFGDT1" FGname on search filter results
    #And I click on "Clear" Button
    #And I set the itemNumber as "XLOBDeleteItem"
    #Then I verify "XLOBFGDT" FGname on search filter results
    #Then I verify "XLOBFGDT1" FGname on search filter results

  Scenario: XLOB delete template UI filter validations
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "XLOB Delete Template"
    And I click on "Clear" Button
    And I set the functionalgroup name as "XLOBFGDT1"
    When I set TAMSite value on search filters "APCC-APCC"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify "XLOBFGDT1" FGname on search filter results
    Then I verify "APCC-APCC" tamsite details on results page

  #Scenario: Create a new XLOB and validate TAM exists through Price TAM Upload on UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "ItemAVLUI" "ItemUploadforXLOB" with "XLOBDeleteTemplateItems" & verify "" "success"
    #Then I verify the "XLOBDeleteTemplateItems" on the Cost Records page
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set the itemNumber as "XLOBDeleteItem"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Group" Button
    #And I "create" the items to the XLOB Functional Group "XLOBFGDT" with "Save All"
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMMonthlyException" "PriceTAMMonthlyExceptionUpload" with "xlobtamupload" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set group name as "XLOBFGDT"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "XLOBFGDT" FG name on price TAM results page
    #Then I verify "100" PriceValue on price TAM results page
    #When I navigate to "Supply Collaboration" -> "XLOB Delete Template"
    #And I click on "Clear" Button
    #And I set the functionalgroup name as "XLOBFGDT"
    #And I select the tam exists as "Yes"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #Then I verify "XLOBFGDT" FGname on search filter results

  #Scenario: Upload XLOB delete template and validate deleted allocations on XLOB Delete Template UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteUploadTemplate" with "XlobFGDeleteTemp" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "XLOB Delete Template"
    #And I click on "Clear" Button
    #And I set the functionalgroup name as "XLOBFGDT"
    #And I select the tam exists as "Yes"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #Then I verify "XLOBFGDT" FGname on search filter results
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set group name as "XLOBFGDT"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "XLOBFGDT" FG name on price TAM results page
    #Then I verify "" Pricebuckets on price TAM results page

  #Scenario: Upload XLOB Delete template with multiple sites and validate deleted allocations on price tam UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I enter "XLOBDeleteItem1" and "XLOBDeleteItem2" on Multiple "itemNumbers" textfield on FG page
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #And I select all rows
    #And I click on "Create Group" Button
    #And I "create" the items to the XLOB Functional Group "XLOBPriceFGDT" with "Save All"
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "PriceTAMMonthlyException" "PriceTAMMultipleSites" with "xlobDeletepricetamitems" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set group name as "XLOBPriceFGDT"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "XLOBPriceFGDT" FG name on price TAM results page
    #Then I verify "100" PriceValue on price TAM results page
    #When I navigate to "Supply Collaboration" -> "XLOB Delete Template"
    #And I click on "Clear" Button
    #And I set the functionalgroup name as "XLOBPriceFGDT"
    #And I select the tam exists as "Yes"
    #And I click on "Apply" Button
    #And I wait till the page loads for "50" seconds
    #Then I verify "XLOBPriceFGDT" FGname on search filter results
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteMultiplesitesUpload" with "XlobFGdeletemultiplesites" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set group name as "XLOBPriceFGDT"
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #Then I verify "XLOBPriceFGDT" FG name on price TAM results page
    #Then I verify "" Pricebuckets on price TAM results page
    
   #Scenario: Xlob delete template empty/invalid data valdiations through upload UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteTemplateValidations" with "Xlobdtemptyfields" & verify "" "error"
    #Then I verify the "FG Name should not be empty" error message displayed on UploadPage
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteTemplateValidations" with "XlobdtemptyFGname" & verify "" "error"
    #Then I verify the "FG Name should not be empty" error message displayed on UploadPage
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteTemplateValidations" with "XlobdtvalidFGname" & verify "" "error"
    #Then I verify the "FG Type should not be empty" error message displayed on UploadPage
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteTemplateValidations" with "XlobdtValidFGtype" & verify "" "error"
    #Then I verify the "Site Type should not be empty" error message displayed on UploadPage
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteTemplateValidations" with "XlobdtvalidSitetype" & verify "" "error"
    #Then I verify the "Site Name should not be empty" error message displayed on UploadPage
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteTemplateValidations" with "XlobdtvalidSitename" & verify "" "error"
    #Then I verify the "From Fiscal Month should not be empty" error message displayed on UploadPage
    #When I navigate to "Upload/Manage Jobs" -> "Upload"
    #When I navigate to "Upload/Manage Jobs" -> "XLOB Price and Allocation"
    #And I upload the PriceTAM "XLOBAllocationDeleteUI" "XLOBDeleteTemplateValidations" with "XlobdtvalidFormFiscalMonth" & verify "" "error"
    #Then I verify the "From Fiscal Month should not be empty" error message displayed on UploadPage
    #
       
#PDSUPPORT-25529 
	@skip
	Scenario: Verify scroll bar is visible on Manage XLOB Price and Allocation
	 Given I log into HarmonyMTCM as "mtcmUser" with "adminuser15"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set group name as "XLOBFGDT"
    And I click on "Apply" Button
    And I wait till the page loads for "45" seconds
	  Then I should be landed on "Manage Price TAM" page
	  When I move To an Element with id "grid-result"
	  Then I verify scroll bar is visible
	 
	 #scplatform-6985 MPN Price/TAM template - Add "Hide Supplier with No Allocation" Search filter 
	 @skip
	 Scenario: Verify scroll bar is visible on Manage XLOB Price and Allocation
	 Given I log into HarmonyMTCM as "mtcmUser" with "adminuser15"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set group name as "XLOBFGDT"
    And I click on "Apply" Button
    And I wait till the page loads for "45" seconds
	  Then I should be landed on "Manage Price TAM" page
	  
#	 Scenario: Validate search filter "Hide No Allocation Supplier" for Yes option
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I set the functionalgroup name as "XLOBFGDT1"
    #And I select "Yes" on "Hide No Allocation Supplier" Combobox
    #And I click on "Apply" Button
    #And I wait till the page loads for "5" seconds
    #Then I verify "No records found to display, Refine your search in the filters" message 
    #And I select filter "Search by Project name" on Price Tam
    #And I click on "Clear" Button
    #And I select "Yes" on "Hide No Allocation Supplier" Combobox
    #And I enter "test" on "projectName" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "120" seconds
    #Then I verify "No records found to display, Refine your search in the filters" message 
    
      @skip
      Scenario: Validate search filter "Hide No Allocation Supplier" for No option
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set the functionalgroup name as "XLOBFGDT"
    And I select "No" on "Hide No Allocation Supplier" Combobox
    And I click on "Apply" Button
    Then I verify search filter results are displayed
    And I wait till the page loads for "5" seconds
    Then I verify search filter results are displayed
    Then I verify "2" rows listed without selection option
   #	Then I verify "No records found to display, Refine your search in the filters" message 
    And I select filter "Search by Project name" on Price Tam
    And I click on "Clear" Button
    And I select "No" on "Hide No Allocation Supplier" Combobox
    And I enter "test" on "projectName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify search filter results are displayed
    
     @skip
     Scenario: Download price tam ensure past and future bucket exclude null or 0% supplier allocation when filter by "Hide supplier with no allocation" = Yes
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I enter "3COM" on "supplier" textfield
    And I set the functionalgroup name as "fgForPriceTamHideSupp"
    #And I select "Yes" on "Hide No Allocation Supplier" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "5" seconds
    Then I verify "1" rows listed without selection option on Price Tam Page
    And I click on download Button and verify the result for "DownloadPriceTam" for "verifyPriceTamDataForFIlter"
       
     @skip
     Scenario: Download price tam ensure past and future bucket exclude null or 0% supplier allocation when filter by "Hide supplier with no allocation" = No
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set the functionalgroup name as "fgForPriceTamHideSupp"
    And I select "No" on "Hide No Allocation Supplier" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "5" seconds
    Then I verify "6" rows listed without selection option on Price Tam Page
    And I click on download Button and verify the result for "DownloadPriceTam" for "verifyPriceTamDataForFIlter"
      
         
      @skip
      Scenario: Download price tam ensure include non-XLOB item when filter by "Hide No XLOB Item" = No
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set the itemNumber as "5-103634-2"
    And I select "No" on "Hide No XLOB Item" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "5" seconds
    Then I verify "1" rows listed without selection option on Price Tam Page
    And I click on download Button and verify the result for "DownloadPriceTam" for "verifyPriceTamDataForFIlter"
      
      Scenario: Download price tam ensure exclude non-XLOB item when filter by "Hide No XLOB Item" = Yes
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Show default filters" on Price Tam
    And I click on "Clear" Button
    And I set the itemNumber as "5-103634-2"
    And I select "Yes" on "Hide No XLOB Item" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "5" seconds
    Then I verify "No records found to display, Refine your search in the filters" message
      
    Scenario: Download price tam ensure exclude non-XLOB item when filter by "Hide No XLOB Item" = Yes
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Search by Project name" on Price Tam
    And I click on "Clear" Button
    And I select "Yes" on "Hide No XLOB Item" Combobox
    And I enter "test" on "projectName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    Then I verify "No records found to display, Refine your search in the filters" message
    
  Scenario: Download price tam ensure exclude non-XLOB item when filter by "Hide No XLOB Item" = No
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    When I navigate to "Supply Collaboration" -> "PriceTAM"
    And I select filter "Search by Project name" on Price Tam
    And I click on "Clear" Button
    And I select "No" on "Hide No XLOB Item" Combobox
    And I enter "test" on "projectName" textfield
    And I click on "Apply" Button
    #And I wait "320" seconds
    #Then I verify search filter results are displayed
    
    #Scenario: Download price tam report for async download by search with "Show default filters"
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser13"
    #When I navigate to "Supply Collaboration" -> "PriceTAM"
    #And I select filter "Show default filters" on Price Tam
    #And I click on "Clear" Button
    #And I enter "02707" and "0270C" on Multiple "itemNumbers" textfield 
#		And I wait till the page loads for "15" seconds 
#		And I select "BUY" on "Cost Type" Combobox     
    #And I click on "asynchronous download" Button
    #And I wait till the page loads for "5" seconds
#		And I should see "Click here to view " link is displayed on variance report page
#		And I click on the "Click here to view" report link
#		Then I verify "Download" column displayed under search results
#		Then I verify "Report Name" column displayed under search results
#		Then I verify "Report Status" column displayed under search results
#		Then I verify "Delete" column displayed under search results  
#		