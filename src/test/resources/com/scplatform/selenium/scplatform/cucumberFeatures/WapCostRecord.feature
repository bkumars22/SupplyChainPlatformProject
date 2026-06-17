@HarmoneyWAPCR
Feature: WAP & XWAP Cost Records Workflow Test Plan

  Scenario: Verify and create new WAP cost record through New sourcing Lane and Download a WAPCR with material value
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadForCR" xlsx with "uploadItemForWAPCR" & verify "" "success"
    Then I verify the "uploadItemForWAPCR" on the Cost Records page
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "CRWP"
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Then I should be landed on "New Sourcing Lane" page
    When I select "SERCOM desc" on "Supplier" Combobox
    And I select "NL-SERCOM-USD USD" on "Source Site" Combobox
    And I select "WW" on Destination Site Combobox
    And I select "USD" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    Then I verify the Lane Name for itemNumber "CRWP"
    When I click on "Submit" Button
    When I click on the "XWAP" tab
    And I click on "Add" Button
    And I set reasonCode "NEW PART" on row "1"
    And I set material value "3" on row "1"
    And I set projectName as "WapCostProject" on row "1"
    When I set FunctionalGroup ID as "SOLESOURCE" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on Approve button
    Then I verify row "1" cost records status as "APPROVED"
    Then I verify Material value "3.0" on row "1"
    And I verify projectName as "WapCostProject" on row "1"
    Then I verify FunctionalGroup ID value "SOLESOURCE" on row "1"

  #Scenario: Download XWAP cost record and verify that data on Excel.
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CRWP"
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on SL "mode_edit" Button
    #When I click on the "XWAP" tab
    #And I click on download Button and verify the result for "CRWP" for "downloadMaterialval"
#
  #Scenario: Edit/Copy and deleted the approved costrecords validations on UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #When I set the itemNumber as "CRWP"
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on SL "mode_edit" Button
    #And I select the value on existing Lane
    #When I click on the "XWAP" tab
    #And I select row "1" from the "selectedRecordKeys" "checkbox" list
    #And I click on "Edit" Button
    #Then I verify row "1" cost records status as "PENDING"
    #When I set material value "5" on row "1"
    #And I set projectName as "CRWAPEDITPRO" on row "1"
    #When I set FunctionalGroup ID as "SOLESOURCE" on row "1"
    #And I select row "1" from the "selectedRecordKeys" "checkbox" list
    #And I click on "Approve" Button
    #Then I verify row "1" cost records status as "APPROVED"
    #Then I verify Material value "5.0" on row "1"
    #And I verify projectName as "CRWAPEDITPRO" on row "1"
    #And I select row "1" from the "selectedRecordKeys" "checkbox" list
    #And I click on "Copy" Button
    #Then I verify row "2" cost records status as "NEW"
    #When I select the copied row
    #And I click on "Delete" Button
    #Then I verify the copied cost record got deleted

  #Scenario: Search WAP cost records on UI and close actions on UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #When I set the itemNumber as "CRWP"
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on SL "mode_edit" Button
    #And I click on Close Button to close the lane
    #And I click "Yes" on the warning popup with message "You are closing a lane with open records."
    #Then I verify the Status as "CLOSED" on "Pricing" page

  #Scenario: To verify Upload WAP cost records on UI with Approved status
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "CostRecordUI" "CRWAPNewRecord" xlsx with "uploadPendingWAPCR" & verify "" "success"
    #Then I verify the "uploadPendingWAPCR" on the Cost Records page
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CRWP"
    #And I select "Pending" on the Status list
    #And I click on "Apply" Button
    #And I wait till the page loads for "2" seconds
    #Then I verify "PENDING" status on search filter results
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "CostRecordActionUI" "CRWAPApprovedRecord" xlsx with "uploadApprovedWAPCR" & verify "" "success"
    #Then I verify the "uploadApprovedWAPCR" on the Cost Records page
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CRWP"
    #And I select "Approved" on the Status list
    #And I click on "Apply" Button
    #And I wait till the page loads for "2" seconds
    #Then I verify "APPROVED" status on search filter results
    #And I log out of HarmonyMTCM

  Scenario: Validate invalid data on material field
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    When I set the itemNumber as "CRWP"
    And I click on "Apply" Button
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on SL "mode_edit" Button
    And I select the value on existing Lane
    When I click on the "XWAP" tab
    And I click on "Add" Button
    And I set reasonCode "NEW PART" on row "2"
    When I set material value "abc" on row "2"
    And I set projectName as "WapCostProject" on row "2"
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on Approve button
    Then I verify "abc is not a valid cost value" message displayed

  Scenario: Create a new XWAP cost record and validate user action permissions on GCM user login
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadForCR" xlsx with "uploadItemForXWAPCR" & verify "" "success"
    Then I verify the "uploadItemForXWAPCR" on the Cost Records page
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "UPXWPCR"
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Then I should be landed on "New Sourcing Lane" page
    When I select "SERCOM desc" on "Supplier" Combobox
    And I select "NL-SERCOM-USD USD" on "Source Site" Combobox
    And I select "WW" on Destination Site Combobox
    And I select "USD" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    Then I verify the Lane Name for itemNumber "UPXWPCR"
    When I click on "Submit" Button
    When I click on the "XWAP" tab
    And I click on "Add" Button
    And I set reasonCode "OTHERS" on row "1"
    And I set material value "3" on row "1"
    And I set projectName as "WapCostProject" on row "1"
    When I set FunctionalGroup ID as "SOLESOURCE" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on Approve button
    Then I verify row "1" cost records status as "APPROVED"
    Then I verify Material value "3.0" on row "1"
    And I verify projectName as "WapCostProject" on row "1"
    Then I verify FunctionalGroup ID value "SOLESOURCE" on row "1"
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm5"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "UPXWPCR"
    And I click on "Apply" Button
    And I wait till the page loads for "2" seconds
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on SL "mode_edit" Button
    And I wait till the page loads for "100" seconds
    When I click on the "XWAP" tab
    And I verify "edit" button diabled XWAP cost record page
    And I verify "Add" button diabled XWAP cost record page
    And I verify "Submit" button diabled XWAP cost record page
    And I verify "Approved" button diabled XWAP cost record page
    And I verify "copy" button diabled XWAP cost record page

  Scenario: Upload new XWAP cost record GCM & Super GCM Upload and validate data on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "CostRecordUI" "XWAPCostRecord" xlsx with "uploadForXWAPcostrecord" & verify "" "success"
    Then I verify the "uploadForXWAPcostrecord" on the Cost Records page
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "UPXWPCR"
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "2" seconds
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" status on search filter results
    #And I log out of HarmonyMTCM
    #CommentingDownloadNeedtocheckissuewithDev-Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "CostRecordUI" "XWAPCostRecord" with "uploadForXWAPcostrecord" & verify "" "error"
    #Then I verify the "The save action for XWAP cost records is allowed only for [ADMIN] role(s)" on the Cost Records page
    #And I log out of HarmonyMTCM
    #Given I log into HarmonyMTCM as "mtcmUser" with "supergcm5"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "CostRecordUI" "XWAPCostRecord" with "uploadForXWAPcostrecord" & verify "" "error"
    #Then I verify the "The save action for XWAP cost records is allowed only for [ADMIN] role(s)" on the Cost Records page

  Scenario: Upload non admin users approve actions through UI
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm5"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "UPXWPCR"
    #And I select "Pending" on the Status list
    #And I click on "Apply" Button
    #And I wait till the page loads for "2" seconds
    #When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Approve" Button
    #And I click on "Clear" Button
    #And I set the itemNumber as "UPXWPCR"
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "2" seconds
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I verify "APPROVED" status on search filter results
    
    Scenario: Create XLOB FG through FG Upload and Validate LOB and Platform Fields
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadForCR" xlsx with "uploadItemForPriceTAM" & verify "" "success"
    Then I verify the "uploadItemForPriceTAM" on the Cost Records page
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "PriceTAM"
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the XLOB Functional Group "XLOBPriceTAM" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "PriceTAM"
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Then I should be landed on "New Sourcing Lane" page
    When I select "SERCOM desc" on "Supplier" Combobox
    And I select "NL-SERCOM-USD USD" on "Source Site" Combobox
    And I select "WW" on Destination Site Combobox
    And I select "USD" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    Then I verify the Lane Name for itemNumber "PriceTAM"
    When I click on "Submit" Button
    When I click on the "XWAP" tab
    And I click on "Add" Button
    And I set reasonCode "NEW PART" on row "1"
    And I set material value "3" on row "1"
    And I set projectName as "WapCostProject" on row "1"
    #When I set the Functional GroupName as "XLOBPriceTAM" on row "1"
    When I set FunctionalGroup ID as "XLOBPriceTAM" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on Approve button
    Then I verify row "1" cost records status as "APPROVED"
    Then I verify Material value "3.0" on row "1"
    And I verify projectName as "WapCostProject" on row "1"
    #Then I verify FunctionalGroup Name value "XLOBPriceTAM" on row "1"
    Then I verify FunctionalGroup ID value "XLOBPriceTAM" on row "1"

  #Scenario: Validate XLOB FG Name, XLOB Platform and XLOB LOB search fields data in cost record page
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I wait till the page loads for "30" seconds
    #And I select "AGILE" on the XLOBPlatform list
    #And I wait till the page loads for "30" seconds
    #And I select "MANUFACTURING" on the XLOBFlexLOB list
    #And I set the XLOBFG name "XLOBPriceTAM" on cost record search filters
    #And I click on "Apply" Button
    #And I wait till the page loads for "2" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #Then I verify XLOB LOB value "MANUFACTURING"
    #Then I verify XLOB Platform value "AGILE"
    #Then I verify XLOB FGName value "XLOBPriceTAM"
    #
   #Scenario: Validate XLOB Platform and XLOB LOB search fields data in FunctionalGroup page
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser14"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I select "MANUFACTURING" on the XLOBFlexLOBonFG list
    #And I select "AGILE" on the XLOBPlatform on FG list
    #And I click on "Apply" Button
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #Then I verify XLOB LOB value "MANUFACTURING"
    #Then I verify XLOB Platform value "AGILE"
    #And I log out of HarmonyMTCM
#
