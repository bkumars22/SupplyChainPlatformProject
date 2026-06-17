@HarmonyCR
Feature: Cost Records Workflow Test Plan

  ##### Test Data
  Scenario Outline: Upload Item values and verify success message, then verify status on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Then I verify the "<action>" on the Cost Records page
    And I log out of HarmonyMTCM

    Examples: 
      | dataFile  | fileName        | action                     | msg | msgType |
      | ItemAVLUI | ItemUploadForCR | uploadItemForCR            |     | success |
      | ItemAVLUI | ItemUploadForCR | uploadItemForCRCreate      |     | success |
      | ItemAVLUI | ItemUploadForCR | uploadItemForCRStatusCheck |     | success |
      | ItemAVLUI | ItemUploadForCR | uploadItemForCRMPN         |     | success |
      | ItemAVLUI | ItemUploadForCR | uploadItemForODMBuyCRMPN   |     | success |

  #		|ItemAVLUI			 |ItemUploadForCR       |uploadItemForODMBuyCRMPN1	  |	    |success |
  
  @skip @flaky-env
  # Skipped 2026-05-11: 'selectedPageKeys' checkbox row timeout after Apply on
  # Item Assignment -> Assign Responsibility (dev7404 popup-row rendering issue).
  Scenario Outline: Assign resp for the items added
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "<item>"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "<item>" selected

    Examples: 
      | item        |
      | CR          |
      | CRCreate    |
      | CRStatus    |
      | CRMPN       |
      | CRODMBuyMPN |

  # Test Data creation ends here
  ###########################################
  #C668098 #21.1 regression
  @bug
  Scenario: Navigate to New Sourcing Lane create source lane then submit approve close and reopen
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "CR"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Then I should be landed on "New Sourcing Lane" page
    When I wait till the page loads for "10" seconds
    Then I verify the Status as "NEW" on "Pricing" page
    And I verify the no sourcing lane existing message
    And I verify the endDate checkBox is deSelected
    Then I should see "history" icon is "not displayed"
    And I should see "file_download" icon is "not displayed"
    And I should see "refresh" icon is "displayed"
    When I select "SERCOM desc" on "Supplier" Combobox
    And I select "NL-SERCOM-USD USD" on "Source Site" Combobox
    And I select "WW" on Destination Site Combobox
    And I select "USD" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    Then I verify the Lane Name for itemNumber "CR"
    #And I verify the value on existing Lane
    When I click on "Submit" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "PENDING" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    #And I verify the value on existing Lane
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "APPROVED" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    #And I verify the value on existing Lane
    And I click on the "Summary" tab
    #And I verify the supplierItem "F3763" under Summary tab
    # tat belongs to the supplier- SERCOM selected
    When I click on "Close" Button
    Then I verify the Status as "CLOSED" on "Pricing" page
    When I click on "Reopen" Button
    Then I verify the Status as "PENDING" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "APPROVED" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    When I click on the save button
    When I click on the Save and Exit button
    #	Then I verify the Dashboard page title
    Then I should be landed on Home page with Welcome msg displayed
    And I log out of HarmonyMTCM

  @bug
  Scenario Outline: Navigate to Search Sourcing Lane then search and verify item number details and souricng lane created with approved status from new sourcing lane page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    And I click on "Clear" Button
    When I set the itemNumber as "CR"
    And I click on "Apply" Button
    Then I should be landed on "<subMenu>" page
    And I wait till the page loads for "5" seconds
    Then I verify "APPROVED" status on search filter results
    When I click on the "itemNumber" link to trigger the popup on "CR"
    Then I verify the Item Number "CR" on the popup
    #	clicking some were out to get popup disappear
    And I click on "Clear" Button
    And I verify popup window is "not visible"
    And I log out of HarmonyMTCM

    Examples: 
      | subMenu              |
      | Search Sourcing Lane |
  #|Search Cost Records |
  
  @bug
  Scenario: Search and verify Search Sourcing Lane with new souricng lane created with approved status
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    When I set the itemNumber as "CR"
    And I click on "Apply" Button
    #	When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I click on the Edit icon
    Then I verify the Status as "APPROVED" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    When I select the value on existing Lane
    When I click on "Close" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "CLOSED" on "Pricing" page
    When I click on "Reopen" Button
    Then I verify the Status as "PENDING" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    And I verify the value on existing Lane
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "APPROVED" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    Then I verify the value on existing Lane
    When I click on the save button
    Then I verify the value on existing Lane
    When I click on the Save and Exit button
    #	Then I verify the Dashboard page title
    Then I should be landed on Home page with Welcome msg displayed
    And I log out of HarmonyMTCM

  #C668100 #21.1 regression
  @bug
  Scenario: Create copy and delete new Cost Records on New Sourcing Lane on Buy Tab
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "CR"
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "BUY" tab
    And I click on "Add" Button
    Then I verify row "1" cost records status as "NEW"
    When I set the from to Dates
    #And I select the "reasonCode" with value "DGP NEGOTIATION CYCLE"
    And I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    And I click on "Submit" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "PENDING"
    When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    And I click on "Copy" Button
    Then I verify row "2" cost records status as "NEW"
    When I select the copied row
    And I click on "Delete" Button
    Then I verify the copied cost record got deleted
    Then I verify no unexpected errors has occured
    And I log out of HarmonyMTCM

  @bug
  Scenario: Navigate to New Sourcing Lane create source lane for adding CR from different tabs then searching summary tab
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CR"
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    And I click on the "Summary" tab
   # And I verify the supplierItem "F3763" under Summary tab
    # tat belongs to the supplier- SERCOM selected
    When I click on the "BUY" tab
    And I click on "Add" Button
    When I set the from to Dates
    And I set the "MATERIAL" field with "10"
    #And I select the "reasonCode" with value "DGP NEGOTIATION CYCLE"
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on "Submit" Button
    When I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    #Then I verify row "2" cost records status as "APPROVED"
    When I click on the "ODM BUY" tab
    And I click on "Add" Button
    Then I verify row "1" cost records status as "NEW"
    When I set the from to Dates
    And I set the "MATERIAL" field with "30"
    #And I select the "reasonCode" with value "DGP NEGOTIATION CYCLE"
    And I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    And I click on "Submit" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "PENDING"
    When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    When I click on the "SERVICE" tab
    And I click on "Add" Button
    Then I verify row "1" cost records status as "NEW"
    When I set the from to Dates
    #And I select the "reasonCode" with value "DGP NEGOTIATION CYCLE"
    And I set the "MATERIAL" field with "20"
    And I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    And I click on "Submit" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "PENDING"
    When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify row "1" cost records status as "PENDING"
    When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    And I click on "Reject" Button
    Then I verify row "1" cost records status as "REJECTED"
    When I click on the Save and Exit button
    #	Then I verify the Dashboard page title
    Then I should be landed on Home page with Welcome msg displayed
    And I log out of HarmonyMTCM

  #Now verify the summary and click on the count links and verify the status
  #C668101 #21.1 regression
  #scplatform-4640 Getting error when creating sourcing lane for the item for this case precondition is met by adding this item to an FG
  #scplatform-4681 CSR06701800 - to enable restriction of approving sourcing lane without production owner assigned
  @bug
  Scenario: Navigate to New Sourcing Lane create source lane then submit approve close records
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    When I select "SERCOM desc" on "Supplier" Combobox
    And I select "NL-SERCOM-USD USD" on "Source Site" Combobox
    And I select "WW" on Destination Site Combobox
    And I select "USD" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    Then I verify the Lane Name for itemNumber "CRCreate"
    When I click on "Submit" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "PENDING" on "Pricing" page
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    And I click and verify msg "Cannot approve cost record because a user with one of the following (role,responsibility) combinations on the item does not exist in the system [(SERVICE_GCM,PRODUCTION), (SUPER_GCM,PRODUCTION), (GCM,PRODUCTION)] for cost record" displayed on warning button of cost record rows
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "CRCreate" selected
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "testFGItemSupp" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "APPROVED" on "Pricing" page
    When I click on "Close" Button
    Then I verify warning message "You are closing a lane with open records" and click "Yes" button on popup displayed
    Then I verify no unexpected errors has occured
    And I verify the "Cannot perform action on sourcing lanes. See below for details" error message displayed
    And I verify the "Cannot close the sourcing lane as one or more approved cost records of the sourcing lane have start dates today or in the past." error message displayed
    And I click and verify msg "Cannot close cost record because the start date is today or in the past" if displayed on warning button of cost record rows
    And I log out of HarmonyMTCM

  @bug
  Scenario Outline: Navigate to Search Sourcing Lane and Cost Records page then search and verify item number details and souricng lane created with Approved status from new sourcing lane page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    And I click on "Clear" Button
    When I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait "30" seconds
    Then I verify "APPROVED" status on search filter results
    When I click on the "itemNumber" link to trigger the popup on "CR"
    Then I verify the Item Number "CRCreate" on the popup
    Then I should be landed on "<subMenu>" page
    And I log out of HarmonyMTCM

    Examples: 
      | subMenu              |
      | Search Sourcing Lane |

  #|Search Cost Records |
  #21.2 regression #C668109
  @bug
  Scenario: Create and edit new Cost Records on New Sourcing Lane on Buy Tab
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    Then I verify the Status as "APPROVED" on "Pricing" page
    When I click on the "BUY" tab
    And I click on "Add" Button
    And I verify submit,approve,edit actions on new CR row added
    And I log out of HarmonyMTCM

  @bug
  Scenario: search for approved status Cr and check close btn functionalities
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRCreate"
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "75" seconds
    Then I should see the Close button should be disabled
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I should see the "Close" button should be displayed and enabled
    And I log out of HarmonyMTCM

  Scenario: search for New status Cr and check submit approve close btn status
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I select "New" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    Then I should see the "Close" button should be displayed and enabled
    And I should see the "Submit" button should be displayed and enabled
    And I should see the "Approve" button should be displayed and enabled
    And I log out of HarmonyMTCM

  Scenario: search for reject status Cr and check submit close btn status
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I enter "KEYBOARD - MOBILITY" on "category" textfield
    And I select "Rejected" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "50" seconds
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I should see the "Close" button should be displayed and enabled
    And I should see the "Submit" button should be displayed and enabled
    And I log out of HarmonyMTCM

  #When I click on "Submit" Button
  # upload and verify this and above scenario
  #------------------------------------------- all above r dependent tests, should preserve the order from top to bottom
  @skip
  Scenario: Navigate to New Sourcing Lane then verify the item Details after filter Search and the verify Cancel Btn
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    When I set the itemNumber as "CR"
    And I click on "Apply" Button
    And I click on the "itemNumber" link to trigger the popup on "CR"
    Then I verify the item details on the popup
    #	And I click on "Ok" Button on the popup
    Then I should be landed on "New Sourcing Lane" page
    #	When I click on Cancel button
    #	Then I verify the Dashboard page title
    And I log out of HarmonyMTCM

  Scenario: Navigate to New Sourcing Lane then verify the Clear button
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    When I set the itemNumber as "CR"
    And I select "Production" on "Responsibility" Combobox
    #And I enter "BANTA" on "supplierName" Combobox with label "Supplier Name"
    And I click on "Clear" Button
    Then I verify the New Sourcing Lane page values are cleared

  @bug
  Scenario: Navigate to New Sourcing Lane and verify back button
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CR"
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Then I verify the Status as "NEW" on "Pricing" page
    When I select "SERCOM desc" on "Supplier" Combobox
    When I click on "Back" Button
    Then I verify no unexpected errors has occured
    Then I should be landed on "New Sourcing Lane" page

  Scenario: Create and verify saved Filter on New Sourcing Lane page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I select "Production" on "Responsibility" Combobox
    And I "save" the filter "CRFilter" by clicking on save as button
    And I select "CRFilter" on "Saved Filters" Combobox
    Then I verify "PRODUCTION" set for "CRFilter" on New Sourcing Lane
    And I click on "Clear" Button
    And I select "Service" on "Responsibility" Combobox
    And I "save" the filter "CRNewFilter" by clicking on save as button
    And I select "CRNewFilter" on "Saved Filters" Combobox
    Then I verify "SERVICE" set for "CRNewFilter" on New Sourcing Lane
    And I select "CRFilter" on "Saved Filters" Combobox
    Then I verify "PRODUCTION" set for "CRFilter" on New Sourcing Lane
    Then I verify no unexpected errors has occured

  Scenario: Verify saved Filter delete functionality on New Sourcing Lane
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I select "Production" on "Responsibility" Combobox
    When I set the itemNumber as "CRDelFilter"
    #And I enter "CRDelFilter" on "itemNumber" Combobox with label "Item Number"
    And I "save" the filter "CRDelFilter" by clicking on save as button
    And I select "CRDelFilter" on "Saved Filters" Combobox
    # selecting this filter to refresh the combobox and to select Manage filter on next step
    And I select "Manage Filters" on "Saved Filters" Combobox
    And I "delete" "CRDelFilter" on Manage Filters
    Then I verify no unexpected errors has occured
    And I wait till the page loads for "10" seconds
    Then I should not see filter with name "CRDelFilter"

  #Scenario: Search with Multiple Commodity Names on New Sourcing Lane
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "New Sourcing Lane"
    #And I enter "AC ADAPTER" and "ACCESSORY" on Multiple "categories" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "500" seconds
    #And I select first "2" rows from the "selectedPageKeys" "radio" list

  #Just clicking on the rows to make sure there are some test results
  Scenario: Search with Multiple Commodity Names by clicking on Icon on New Sourcing Lane
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I select "Service" on "Responsibility" Combobox
    And I click on multiple search "categories" icon
    And I wait till the page loads for "200" seconds
    And I set "AC ADAPTER" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "categories" and "confirm" the list on the popup
    And I click on multiple search "categories" icon
    And I wait till the page loads for "200" seconds
    And I set "ALL" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "categories" and "confirm" the list on the popup
    Then I verify the selected names on the multiple "categories" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "120" seconds
    Then I verify search filter results are displayed

  @skip
  Scenario: Navigate to Search Sourcing Lane then verify the item Details and Cancel Btn after filter Search
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    When I set the itemNumber as "CR"
    And I click on "Apply" Button
    And I click on the "itemNumber" link to trigger the popup on "CR"
    Then I verify the item details on the popup
    #	And I click on "Ok" Button on the popup
    And I wait till the page loads for "10" seconds
    Then I should be landed on "Search Sourcing Lane" page
    #	When I click on Cancel button
    #	#Then I verify the Dashboard page title
    #	Then I should be landed on My Workspace page
    And I log out of HarmonyMTCM

  Scenario: Navigate to Search Sourcing Lane then verify the Clear button
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    When I set the itemNumber as "CR"
    And I select "Production" on "Responsibility" Combobox
    #	And I enter "BANTA" on "supplierName" Combobox with label "Supplier Name"
    #	And I enter "DELL" on "business" Combobox with label "Item Business"
    And I click on "Clear" Button
    Then I verify the Search Sourcing Lane page values are cleared

  #	Scenario: Create and verify saved Filter on Search Sourcing Lane page
  #	Given  I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
  #	When  I navigate to "Pricing" -> "Search Sourcing Lane"
  #	And I click on "Clear" Button
  #	When  I set the itemNumber as "CRFilter"
  #	And  I select "Production" on "Responsibility" Combobox
  #	# check the search UI and fill
  #	And  I "save" the filter "CRFilter" by clicking on save as button
  #	And  I select "CRFilter" on "Saved Filters" Combobox
  #	Then  I verify the fields set for "CRFilter" on Search Sourcing Lane
  #	And I log out of HarmonyMTCM
  Scenario: Verify saved Filter create and delete functionality on Search Sourcing Lane
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I select "Production" on "Responsibility" Combobox
    And I set the itemNumber as "CRFilter"
    #check the search UI and fill
    And I "save" the filter "CRFilter" by clicking on save as button
    And I select "CRFilter" on "Saved Filters" Combobox
    # selecting this filter to refresh the combobox and to select Manage filter on next step
    And I select "Manage Filters" on "Saved Filters" Combobox
    And I "delete" "CRFilter" on Manage Filters
    And I wait till the page loads for "10" seconds
    Then I should not see filter with name "CRFilter"
    And I log out of HarmonyMTCM

  #Scenario: Search with Multiple Commodity Names on Search Sourcing Lane
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #And I enter "AC ADAPTER" and "ACCESSORY" on Multiple "categories" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #Then I verify search filter results are displayed
    #And I log out of HarmonyMTCM

  Scenario: Search with Multiple Commodity Names by clicking on Icon on Search Sourcing Lane
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I click on multiple search "categories" icon
    And I wait till the page loads for "20" seconds
    And I set "AC ADAPTER" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "categories" and "confirm" the list on the popup
    And I click on multiple search "categories" icon
    And I wait till the page loads for "20" seconds
    And I set "ALL" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "categories" and "confirm" the list on the popup
    Then I verify the selected names on the multiple "categories" textfield
    And I select "Service" on "Responsibility" Combobox
    When I click on "Apply" Button
    And I wait "50" seconds  
    Then I verify search filter results are displayed
    And I log out of HarmonyMTCM

  Scenario: Search sourcing lane with lane name created on new sourcing lane page and verify the results
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I enter lane name on "name" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    And I click on the "CR" link to trigger the popup on "CR"
    Then I verify the Item Number "CR" on the popup
    #		And  I click on "Ok" Button on the popup
    And I log out of HarmonyMTCM

  #Scenario: Search with multiple Destination Sites on Search Sourcing Lane
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #And I enter "WW" and "APCC" on Multiple "toSites" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #Then I verify search filter results are displayed
    #And I log out of HarmonyMTCM

  Scenario: Search with Multiple Destination Sites by clicking on Icon on Search Sourcing Lane
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I click on multiple search "toSites" icon
    And I wait till the page loads for "20" seconds
    And I set "WW" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "toSites" and "confirm" the list on the popup
    And I click on multiple search "toSites" icon
    And I wait till the page loads for "20" seconds
    And I set "APCC" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "toSites" and "confirm" the list on the popup
    Then I verify the selected names on the multiple "toSites" textfield
    When I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    And I log out of HarmonyMTCM

  Scenario: Navigate to Search Cost Records then verify the Clear button
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "CR"
    And I select "Production" on "Responsibility" Combobox
    #		And I enter "Banta" on "supplierName" textfield
    #		And I enter "DELL" on "business" textfield
    #		And I enter "ADMIN" on "user" textfield
    #		And I enter "DELL" on "category" textfield
    #		And I enter "DELL" on "dataSource" textfield
    And I click on "Clear" Button
    Then I verify the Search CostRecords page values are cleared
    And I log out of HarmonyMTCM

  #Scenario: Search with Multiple Commodity Names Search Cost Records
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I enter "AC Adapter" and "KEYBOARD" on Multiple "categories" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "300" seconds
    #Then I verify search filter results are displayed
    #When I select first "2" rows from the "selectedPageKeys" "checkbox" list
    #And I log out of HarmonyMTCM

  Scenario: Search with Multiple Commodity Names by clicking on Icon Search Cost Records
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I click on multiple search "categories" icon
    And I wait till the page loads for "20" seconds
    And I set "KEYBOARD" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "categories" and "confirm" the list on the popup
    And I click on multiple search "categories" icon
    And I wait till the page loads for "20" seconds
    And I set "ACCESSORY" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "categories" and "confirm" the list on the popup
    Then I verify the selected names on the multiple "categories" textfield
    When I click on "Apply" Button
    And I wait till the page loads for "300" seconds
    Then I verify search filter results are displayed
    When I select first "2" rows from the "selectedPageKeys" "checkbox" list
    And I log out of HarmonyMTCM

  #Just clicking on the rows to make sure there are some test results
  #Scenario: Search with Destination Sites on New Sourcing Lane
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I select "Rejected" on the Status list
    #And I enter "APCC" and "BCC" on Multiple "toSites" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "60" seconds
    #Then I verify search filter results are displayed
    #And I select first "2" rows from the "selectedPageKeys" "checkbox" list
    #And I log out of HarmonyMTCM

  #Just clicking on the rows to make sure there are some test results
  Scenario: Search with Multiple Destination Sites by clicking on Icon Search Cost Records
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I click on multiple search "toSites" icon
    And I set "WW" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "toSites" and "confirm" the list on the popup
    When I click on "Apply" Button
    And I wait till the page loads for "60" seconds
    Then I verify search filter results are displayed
    When I select first "2" rows from the "selectedPageKeys" "checkbox" list
    #Just clicking on the rows to make sure there are some test results
    And I log out of HarmonyMTCM

  Scenario: Search with multiple item numbers
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I enter "0R99J" and "14PX3" on Multiple "itemNumbers" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    And I select first "2" rows from the "selectedPageKeys" "checkbox" list
    And I log out of HarmonyMTCM

  @skip
  Scenario: Navigate to Search Cost Records then search close reopen and verify
    the new souricng lane created with approved status
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "CR"
    And I click on "Apply" Button
    When I click on the Edit icon
    Then I verify no unexpected errors has occured
    #Then I verify the Status as "APPROVED" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    Then I verify the value on existing Lane
    And I verify the Lane Name for itemNumber "CR"
    When I click on Close Button to close the lane
    Then I verify no unexpected errors has occured
    Then I verify the Status as "CLOSED" on "Pricing" page
    When I click on "Reopen" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "PENDING" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    And I verify the value on existing Lane
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "APPROVED" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"
    Then I verify the value on existing Lane
    When I click on the save button
    Then I verify the value on existing Lane
    When I click on the Save and Exit button
    Then I should be landed on Home page with Welcome msg displayed
    And I log out of HarmonyMTCM

  @bug
  Scenario Outline: Click on refresh button on New Sourcing lane and Search Sourcing lane page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    And I click on "Clear" Button
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I should see "refresh" icon is "displayed"
    When I click on "refresh" icon
    Then I should be landed on page with subHeader "<header>"

    #And I log out of HarmonyMTCM
    Examples: 
      | subMenu           | header                    |
      | New Sourcing Lane | Sourcing Lane Information |

  @bug
  Scenario Outline: Click on refresh button on New Sourcing lane and Search Sourcing lane page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I click on the Edit icon
    And I should see "refresh" icon is "displayed"
    When I click on "refresh" icon
    Then I should be landed on page with subHeader "<header>"

    Examples: 
      | subMenu              | header                    |
      | Search Sourcing Lane | Sourcing Lane Information |

  #C668293
  Scenario: Click on refresh button on Search Cost Records page and Validate audit history for cost record
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN*"
    And I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    And I click on the Edit icon
    Then I verify no unexpected errors has occured
    And I wait till the page loads for "10" seconds
    Then I should see "refresh" icon is "displayed"
    When I click on the "refresh" icon
    And I wait till the page loads for "10" seconds
    Then I should be landed on page with subHeader "Sourcing Lane Information"
    When I click on the "history" icon
    Then I should be landed on Audit History page
    Then I verify "User ID" column is displayed under search results
    Then I verify "Date Performed" column is displayed under search results
    Then I verify "Comment" column is displayed under search results
    Then I verify "Action" column is displayed under search results
    Then I verify "Type" column has value "PcmSourcingLane" displayed under search results for all rows
    Then I verify "Role ID" column has value "ADMIN" displayed under search results for all rows
    And I log out of HarmonyMTCM

  @bug
  Scenario Outline: verify item details popup link on New Sourcing lane and Search Sourcing lane page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    And I click on "Clear" Button
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Given I wait "10" seconds
    Then I verify "2" item links
    When I click on the itemLinks to trigger the popup
    Then I verify no unexpected errors has occured
    Then I should be landed on page with subHeader "Sourcing Lane Information"
    And I log out of HarmonyMTCM

    Examples: 
      | subMenu           |
      | New Sourcing Lane |

  @bug
  Scenario Outline: verify item details popup link on New Sourcing lane and Search Sourcing lane page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I click on the Edit icon
    And I wait till the page loads for "10" seconds
    Then I verify "2" item links
    When I click on the itemLinks to trigger the popup
    Then I verify no unexpected errors has occured
    Then I should be landed on page with subHeader "Sourcing Lane Information"
    And I log out of HarmonyMTCM

    Examples: 
      | subMenu              |
      | Search Sourcing Lane |

  Scenario: verify item Details popup link on Search Cost Records page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN*"
    And I click on "Apply" Button
    And I wait till the page loads for "180" seconds
    When I click on the Edit icon
    And I click on the "Summary" tab
    #if not on that page
    Then I verify "2" item links
    When I click on the itemLinks to trigger the popup
    Then I should be landed on page with subHeader "Sourcing Lane Information"
    And I log out of HarmonyMTCM

  #	#scplatform-4926Dell Demo Feedback 20.3 #1
  #	#scplatform-4267 Add Project Name field to the Cost Record
  #	#scplatform-4456 Edit/approve/copy MPN cost records in UI
  @bug
  Scenario: verify Buy tab record approved and closed status for same mpn
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPN"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    When I select "SERCOM desc" on "Supplier" Combobox
    And I select "NL-SERCOM-USD USD" on "Source Site" Combobox
    And I select "WW" on Destination Site Combobox
    And I select "USD" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    When I click on "Submit" Button
    Then I verify no unexpected errors has occured
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    #Then I verify the Status as "APPROVED" on "Pricing" page
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I wait till the page loads for "5" seconds
    Then I verify Record Source as "" on row "1"
    #	And I verify recordSource field is not editable on row "1"
    And I verify reasonCode "" on row "1"
    And I verify projectName as "" on row "1"
    When I set start date from today to "5" days from start Dates as end date on row "1"
    And I set reasonCode "NEW PART" on row "1"
    And I set projectName as "testPjt" on row "1"
    And I set MPN "3" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    And I verify reasonCode field is disabled on row "1"
    And I verify projectName as "testPjt" on row "1"
    Then I verify Record Source as "UI" on row "1"
    #	And I verify recordSource field is not editable on row "1"
    When I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Copy" Button
    Then I verify reasonCode "" on row "2"
    Then I verify Record Source as "" on row "2"
    And I verify recordSource field is not editable on row "2"
    And I verify projectName as "" on row "2"
    When I wait till the page loads for "1" seconds
    When I set start date as today on row "2"
    And I set start date from today to "5" days from start Dates as end date on row "2"
    And I set reasonCode "NEW PART" on row "2"
    And I set projectName as "testPjt" on row "2"
    And I set MPN "3" on row "2"
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    #Then I verify row "2" cost records status as "CLOSED"
    And I verify projectName as "testPjt" on row "1"
    And I verify reasonCode field is disabled on row "1"
    And I verify Record Source as "" on row "1"
    #	And I verify recordSource field is not editable on row "1"
    And I click on download Button and verify the result for "CR" for "verifyPNameNewSL"

  #	#scplatform-4456 Edit/approve/copy MPN cost records in UI
  #Scenario: verify Buy tab record approved status for different mpn but same date
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "New Sourcing Lane"
    #And I set the itemNumber as "CRMPN"
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #When I select first "1" rows from the "selectedPageKeys" "radio" list
    #And I select the value on existing Lane
    #When I click on the "BUY" tab
    #And I wait till the page loads for "1" seconds
    #And I select row "1" from the "selectedRecordKeys" "checkbox" list
    #And I click on "Copy" Button
    #Then I verify no unexpected errors has occured
    #And I wait till the page loads for "10" seconds
    #When I set start date as today on row "1"
    #And I set start date from today to "5" days from start Dates as end date on row "1"
    #And I set reasonCode "NEW PART" on row "1"
    #And I set MPN "4" on row "1"
    #And I select row "1" from the "selectedRecordKeys" "checkbox" list
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    #Then I verify row "1" cost records status as "APPROVED"
    #And I verify row "2" cost records status as "APPROVED"

  #scplatform-4456 Edit/approve/copy MPN cost records in UI
  @bug
  Scenario: verify ODM Buy tab records with approved status for different mpn for same date
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPN"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "ODM BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I wait till the page loads for "5" seconds
    #Then I verify Record Source as "UI" on row "1"
    When I set start date from today to "5" days from start Dates as end date on row "1"
    And I set reasonCode "NEW PART" on row "1"
    And I set MPN "10" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Copy" Button
    Then I verify no unexpected errors has occured
    When I set start date as today on row "2"
    And I set start date from today to "5" days from start Dates as end date on row "2"
    And I set reasonCode "NEW PART" on row "2"
    And I set MPN "11" on row "2"
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    And I verify row "2" cost records status as "APPROVED"
    And I verify MPN set as "10"
    And I verify MPN set as "11"

  #scplatform-4456 Edit/approve/copy MPN cost records in UI
  @bug
  Scenario: verify ODM Buy tab records with approved status for different mpn for different date
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPN"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "ODM BUY" tab
    And I wait till the page loads for "1" seconds
    Then I verify row "1" cost records status as "APPROVED"
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on "Copy" Button
    Then I verify no unexpected errors has occured
    And I set start date from today to "6" days from start Dates as end date on row "3"
    And I set reasonCode "NEW PART" on row "3"
    And I set MPN "4" on row "3"
    And I select row "3" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify row "1" cost records status as "APPROVED"
    And I verify row "2" cost records status as "APPROVED"
    And I verify row "3" cost records status as "APPROVED"
    And I verify MPN set as "4"
    And I verify MPN set as "10"
    And I verify MPN set as "11"

  #scplatform-4456 Edit/approve/copy MPN cost records in UI
  @bug
  Scenario: verify ODM Buy tab records with approved status for different mpn for different date
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRODMBuyMPN"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    When I select "SERCOM desc" on "Supplier" Combobox
    And I select "NL-SERCOM-USD USD" on "Source Site" Combobox
    And I select "WW" on Destination Site Combobox
    And I select "USD" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    When I click on "Submit" Button
    Then I verify no unexpected errors has occured
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "APPROVED" on "Pricing" page
    When I click on the "ODM BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I wait till the page loads for "5" seconds
    And I set start date from today to "5" days from start Dates as end date on row "1"
    And I set reasonCode "NEW PART" on row "1"
    And I set MPN "3" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Copy" Button
    #	And I set start date as today on row "2"
    And I set start date from today to "6" days from start Dates as end date on row "2"
    And I set reasonCode "NEW PART" on row "2"
    And I set MPN "4" on row "2"
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify row "1" cost records status as "APPROVED"
    And I verify row "2" cost records status as "APPROVED"
    And I verify MPN set as "4"
    And I verify MPN set as "3"

  #	#scplatform-4456 Edit/approve/copy MPN cost records in UI
  @bug
  Scenario: verify ODM Buy tab records existing record with an mpn and no mpn for new one for same date
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRODMBuyMPN"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "ODM BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I wait till the page loads for "5" seconds
    And I set start date as today on row "3"
    And I set start date from today to "5" days from start Dates as end date on row "3"
    And I set reasonCode "OTHERS" on row "3"
    And I set MPN "20" on row "3"
    And I select row "3" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "3" cost records status as "APPROVED"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I wait till the page loads for "1" seconds
    And I click on "Copy" Button
    And I wait till the page loads for "10" seconds
    When I set start date as today on row "4"
    And I set start date from today to "5" days from start Dates as end date on row "4"
    And I set reasonCode "PRICE MISSING" on row "4"
    And I set MPN "" on row "4"
    And I select row "4" from the "selectedRecordKeys" "checkbox" list
    And I click on "Submit" Button
    Then I verify no unexpected errors has occured
    Then I verify row "4" cost records status as "PENDING"
    And I select row "4" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    And I verify row "2" cost records status as "APPROVED"
    And I verify row "3" cost records status as "APPROVED"
    And I verify row "4" cost records status as "APPROVED"
    And I verify MPN "" on row "4"

  #	verifying prev test record also here to confirm the status
  #scplatform-4456 Edit/approve/copy MPN cost records in UI
  @bug
  Scenario: verify Buy tab records existing record without mpn and an mpn for new one for same date
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRODMBuyMPN"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I wait till the page loads for "5" seconds
    And I set start date from today to "5" days from start Dates as end date on row "1"
    And I set reasonCode "NEW PART" on row "1"
    And I set MPN "" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Copy" Button
    Then I verify no unexpected errors has occured
    And I set start date as today on row "2"
    And I set start date from today to "5" days from start Dates as end date on row "2"
    And I set reasonCode "NEW PART" on row "2"
    And I set MPN "15" on row "2"
    And I select row "2" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    And I verify MPN "15" on row "1"
    And I verify row "2" cost records status as "APPROVED"
    And I verify MPN "" on row "2"

  Scenario Outline: Upload Item values and verify success message, then verify status on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Then I verify the "<action>" on the Cost Records page

    Examples: 
      | dataFile  | fileName        | action                     | msg | msgType |
      | ItemAVLUI | ItemUploadForCR | uploadItemForMultipleCRMPN |     | success |

  @bug
  Scenario Outline: Upload excel with same date and mpn as of existing CR on BUY Tab
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "CRMPNUpload"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "CRMPNUpload" selected
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPNUpload"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    When I select "SERCOM desc" on "Supplier" Combobox
    And I select "NL-SERCOM-USD USD" on "Source Site" Combobox
    And I select "WW" on Destination Site Combobox
    And I select "USD" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    When I click on "Submit" Button
    Then I verify no unexpected errors has occured
    #Then I verify the Status as "PENDING" on "Pricing" page
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "APPROVED" on "Pricing" page
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    #And I click on "Delete" Button
    And I click on "Add" Button
    And I wait till the page loads for "5" seconds
    And I set start date from today to "5" days from start Dates as end date on row "1"
    And I set reasonCode "NEW PART" on row "1"
    And I set MPN "3" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPNUpload"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    Then I verify row "1" cost records status as "APPROVED"
    
    #When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    #And I click on "Copy" Button
    #
    #Then I verify row "2" cost records status as "PENDING"
    #When I select row "2" from the "selectedRecordKeys" "checkbox" list
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    #	Then I verify row "1" cost records status as "APPROVED"
    #	Then I verify row "2" cost records status as "CLOSED"
    #Then I verify "APPROVED" status on search filter results
    #Then I verify "CLOSED" status on search filter results
    #And I verify MPN "3" on row "1"
    #And I verify MPN "3" on row "2"

    Examples: 
      | dataFile     | fileName  | action                     | msg | msgType |
      | CostRecordUI | mpnUpload | uploadMultipleItemForCRMPN |     | success |

  @bug
  Scenario Outline: Upload excel with same date and different mpn as of existing CR on BUY Tab
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPNUpload"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    Then I verify row "1" cost records status as "APPROVED"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPNUpload"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    #	Then I verify row "1" cost records status as "APPROVED"
    #	Then I verify row "2" cost records status as "PENDING"
    Then I verify "APPROVED" status on search filter results
    #Then I verify "PENDING" status on search filter results
    #And I verify MPN set as "3"
    #And I verify MPN set as "3"
    #And I verify MPN set as "3"
    ##When I select row "2" from the "selectedRecordKeys" "checkbox" list
    #And I select CR with "PENDING" status under Tab
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    ##	Then I verify row "1" cost records status as "APPROVED"
    ##	And I verify row "2" cost records status as "APPROVED"
    ##	And I verify row "3" cost records status as "CLOSED"
    #Then I verify "APPROVED" status on search filter results
    #Then I verify "CLOSED" status on search filter results
    #And I verify MPN set as "3"
    #And I verify MPN set as "3"
    #And I verify MPN set as "3"

    Examples: 
      | dataFile     | fileName  | action                    | msg | msgType |
      | CostRecordUI | mpnUpload | uploadMultipleItemDiffMPN |     | success |

  @bug
  Scenario Outline: Upload excel with diff date and same mpn as of existing CR on ODMBUY Tab
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPNUpload"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "ODM BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I wait till the page loads for "5" seconds
    And I set start date from today to "5" days from start Dates as end date on row "1"
    And I set reasonCode "NEW PART" on row "1"
    And I set MPN "40" on row "1"
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "APPROVED"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPNUpload"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "ODM BUY" tab
    And I wait till the page loads for "1" seconds
    #	Then I verify row "1" cost records status as "APPROVED"
    #	Then I verify row "2" cost records status as "PENDING"
    Then I verify "APPROVED" status on search filter results
    #Then I verify "PENDING" status on search filter results
    #And I verify MPN "40" on row "1"
    #And I verify MPN "40" on row "2"
    #When I select row "2" from the "selectedRecordKeys" "checkbox" list
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    ##	Then I verify row "1" cost records status as "APPROVED"
    ##	And I verify row "2" cost records status as "CLOSED"
    #Then I verify "APPROVED" status on search filter results
    #Then I verify "CLOSED" status on search filter results
    #And I verify MPN "40" on row "1"
    #And I verify MPN "40" on row "2"

    Examples: 
      | dataFile     | fileName  | action                        | msg | msgType |
      | CostRecordUI | mpnUpload | uploadMultipleItemDiffDateMPN |     | success |

  @bug
  Scenario Outline: Upload excel with diff date and diff mpn as of existing CR on ODMBUY Tab
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "CRMPNUpload"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    And I select the value on existing Lane
    When I click on the "ODM BUY" tab
    And I wait till the page loads for "1" seconds
    #	Then I verify row "1" cost records status as "APPROVED"
    #	And I verify row "2" cost records status as "CLOSED"
    Then I verify "APPROVED" status on search filter results
    #Then I verify "CLOSED" status on search filter results
    #And I verify MPN "40" on row "1"
    #And I verify MPN "40" on row "2"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    #When I navigate to "Pricing" -> "New Sourcing Lane"
    #And I set the itemNumber as "CRMPNUpload"
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #When I select first "1" rows from the "selectedPageKeys" "radio" list
    #And I select the value on existing Lane
    #When I click on the "ODM BUY" tab
    #And I wait till the page loads for "1" seconds
    #Then I verify CRs has status value as "APPROVED" under Tab
    #Then I verify CRs has status value as "PENDING" under Tab
    #Then I verify CRs has status value as "CLOSED" under Tab
    #And I verify MPN set as "50"
    #And I verify MPN set as "40"
    #When I select row "2" from the "selectedRecordKeys" "checkbox" list
    #And I select CR with "PENDING" status under Tab
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    #Then I verify CRs has status value as "APPROVED" under Tab
    #Then I verify CRs has status value as "CLOSED" under Tab
    #And I verify MPN set as "50"
    #And I verify MPN set as "40"

    Examples: 
      | dataFile     | fileName  | action                         | msg | msgType |
      | CostRecordUI | mpnUpload | uploadMultipleItemDiffDate&MPN |     | success |

  @bug
  Scenario Outline: Download a CR with and without mpn and verify file has column for mpn for value
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "<item>"
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    And I click on the Edit icon
    When I click on the "BUY" tab
    And I click on download Button and verify the result for "CR" for "<action>"
    
    Examples: 
      | item  | action            |
      | CR    | dwnloadWithoutMPN |
      | CRMPN | dwnloadWithMPN    |

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario Outline: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    And I wait "5" seconds
    And I click on "Clear" Button
    And I wait "5" seconds
    And I set the itemNumber as "JD002"
    And I click on "Apply" Button
    #	And I verify search filter results are displayed
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Pricing" -> "<subMenu>"
    Then I verify "JD002" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I verify "" on "itemNumber" textfield

    Examples: 
      | subMenu              |
      | Search Sourcing Lane |
      | Search Cost Records  |

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario Outline: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    And I click on "Clear" Button
    And I set the itemNumber as "CR04H"
    And I click on "Apply" Button
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    Then I verify "CR04H" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "<subMenu>"
    Then I verify "" on "itemNumber" textfield

    Examples: 
      | subMenu              |
      | Search Sourcing Lane |
      | Search Cost Records  |

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Price Variance Report"
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    #And I wait till the page loads for "5" seconds
    And I wait "5" seconds
    Then I should be landed on "Cost Record Price Variance" page
    #And I verify search filter results are displayed
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Pricing" -> "Price Variance Report"
    And I wait till the page loads for "5" seconds
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I should be landed on "Cost Record Price Variance" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Price Variance Report"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    And I wait till the page loads for "5" seconds
    Then I should be landed on "Cost Record Price Variance" page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Price Variance Report"
    And I wait till the page loads for "5" seconds
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I should be landed on "Cost Record Price Variance" page
    And I wait till the page loads for "5" seconds
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Price Variance Report"
    Then I should be landed on "Cost Record Price Variance" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4926Dell Demo Feedback 20.3 #1
  @skip
  Scenario: Search CR with projectName added and verify file has column for pjtName and verify the value
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I click on the Edit icon
    Then I verify no unexpected errors has occured
    #And I wait till the page loads for "10" seconds
    And I wait "10" seconds
   And I click on download Button and verify the result for "CR" for "verifyPNameSearchCR"

  #scplatform-4926Dell Demo Feedback 20.3 #1
  @bug
  Scenario: Search Sourcing lane and verify file downloaded has column for pjtName and verify the value
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I click on the Edit icon
    And I click on download Button and verify the result for "CR" for "verifyPNameSearchSL"

  #scplatform-4926Dell Demo Feedback 20.3 #1
  @skip
  Scenario: Search Cost Records with projectName and verify search results are displayed
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I enter "testPjt" on "projectName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    # Just selecting to verify that results are displayed
    And I click on the Edit icon
    Then I verify no unexpected errors has occured
    And I wait till the page loads for "3" seconds
    Then I should see "file_download" icon is "displayed"

  #Jus verifying it landed on search CR details page by verifying download icon there
  #scplatform-4926Dell Demo Feedback 20.3 #1
  #Scenario: Search Cost Records and verify projectName and MPN columns with values under search results displayed
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CRMPN"
    #And I enter "testPjt" on "projectName" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #Then I verify search filter results are displayed
    #And I verify "MPN" column displayed under search results
    #And I verify "Project Name" column displayed under search results
    #And I verify "MPN" has value "3" assigned
    #And I verify "projectName" has value "testPjt" assigned

  Scenario: Verify delete Supplier items warning messages on Manage Items page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Administration" -> "Manage Items"
    And I click on "Clear" Button
    When I set the itemNumber as "CRStatus"
    And I click on "Apply" Button
    And I click on the "itemNumber-noPopup" to verify the details
    And I "check" the "selectedAvls" checkbox
    And I click on the save button
    And I click "No" on the warning popup with message "You are about to remove AVLs from this item, are you sure?"
    Then I should be landed on "CRStatus" page

  #
  #Scenario: Verify delete Supplier items warning messages on Manage Items page
  #	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
  #	When I navigate to "Administration" -> "Manage Items"
  #	And I click on "Clear" Button
  #	When I set the itemNumber as "CRStatus"
  #	And I click on "Apply" Button
  #	And I click on the "itemNumber-noPopup" to verify the details
  #	And I "check" the "selectedAvls" checkbox
  #	And I click on the save button
  #	And I click "Yes" on the warning popup with message "You are about to remove AVLs from this item, are you sure?"
  #	Then I should be landed on "CRStatus" page
  #	And I verify the "The AVL is in use by other data in the system" warning message
  Scenario: Verify items marked for delete checkbox on Manage Items page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Administration" -> "Manage Items"
    And I click on "Clear" Button
    And I set the itemNumber as "CRStatus"
    And I click on "Apply" Button
    And I click on the "itemNumber-noPopup" to verify the details
    And I "check" the "itemMarkedForDelete" checkbox
    And I click on the save button
    Then I should be landed on "CRStatus" page

  #C66810 #C668102 #21.1 regression
  #Comment this as pass in local
  #Scenario: As supergcm Create copy and delete new Cost Records from Search Sourcing Lane
    #Given I log into HarmonyMTCM as "mtcmUser" with "supergcm3"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CR"
    #And I click on "Apply" Button
    #And I wait till the page loads for "1" seconds
    #And I click on the Edit icon
    #When I click on the "BUY" tab
    #And I click on "Add" Button
    #Then I verify row "2" cost records status as "NEW"
    #When I set the from to Dates
    #And I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    #And I click on "Submit" Button
    #Then I verify no unexpected errors has occured
    #Then I verify row "1" cost records status as "PENDING"
    #When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    #Then I click and verify msg "" if displayed on warning button of cost record rows
    #Then I verify row "1" cost records status as "APPROVED"
    #And I log out of HarmonyMTCM

  #Scenario: As servicegcm Create copy and delete new Cost Records from Search Sourcing Lane
    #Given I log into HarmonyMTCM as "mtcmUser" with "servicegcm3"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CRCreate"
    #And I click on "Apply" Button
    #And I wait till the page loads for "1" seconds
    #And I click on the Edit icon
    #When I click on the "BUY" tab
    #And I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    #And I click on "Edit" Button
    #Then I verify no unexpected errors has occured
    #And I set reasonCode "NEW PART" on row "1"
    #When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    #Then I verify the Status as "APPROVED" on "Pricing" page
    #And I log out of HarmonyMTCM

  #C668104 #21.1 regression
  #Scenario: As busadmin Create copy and delete new Cost Records from Search Sourcing Lane
    #Given I log into HarmonyMTCM as "mtcmUser" with "busadmin3"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CRCreate"
    #And I click on "Apply" Button
    #And I wait till the page loads for "1" seconds
    #And I click on the Edit icon
    #When I click on the "BUY" tab
    #And I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    #And I click on "Edit" Button
    #Then I verify no unexpected errors has occured
    #When I select first "1" rows from the "selectedRecordKeys" "checkbox" list
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    #Then I verify the Status as "APPROVED" on "Pricing" page
    #And I log out of HarmonyMTCM

  Scenario: As supplier verify that supplier cannot view items which are created for other roles
    Given I log into HarmonyMTCM as "mtcmUser" with "supplier1"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    Then I verify "No records found to display" message

  Scenario: As EM verify that em can only be view items which are created for em role
    Given I log into HarmonyMTCM as "mtcmUser" with "em1"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    Then I verify "No records found to display" message

  #C668106 #21.1 regression
  @bug
  Scenario Outline: As a supp Create new Cost Records from New Sourcing Lane
    Given I log into HarmonyMTCM as "mtcmUser" with "supplier1"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Then I verify the "<action>" on the Cost Records page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "CRSuppCreate"
    And I click on "Apply" Button
    And I wait "5" seconds
    And I "assign" responsibility "mike_quick@dell.com" to the item "all" selected
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "CRSuppCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "CRSuppGrp" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "supplier1"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "CRSuppCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Then I verify the Status as "NEW" on "Pricing" page
    #CommentingDownload,Need to check functionality-When I select "DSSMITH" on "Supplier" Combobox
    #And I select "NL-DSSMITH-PLN PLN" on "Source Site" Combobox
    #And I select "DOMOCSite1 desc" on Destination Site Combobox
    #And I select "PLN" on "Currency" Combobox
    #And I enter "2" on the "selectedLane.dateOffset" textfield
    #When I click on "Submit" Button
    #Then I verify no unexpected errors has occured
    #When I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    #Then I verify the Status as "APPROVED" on "Pricing" page
    #Then I should see "history" icon is "displayed"
    #And I should see "file_download" icon is "displayed"
    #And I should see "refresh" icon is "displayed"
		And I log out of HarmonyMTCM
    Examples: 
      | dataFile  | fileName      | action                       | msg | msgType |
      | ItemAVLUI | ItemForSuppCR | updateItemsForSupplierCreate |     | success |

  #C668107 #21.1 regression
  @bug
  Scenario Outline: As a EM Create new Cost Records from New Sourcing Lane
    Given I log into HarmonyMTCM as "mtcmUser" with "em1"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Then I verify the "<action>" on the Cost Records page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "CREMCreate"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "all" selected
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "em1"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I click on "Clear" Button
    And I set the itemNumber as "CREMCreate"
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Then I verify the Status as "NEW" on "Pricing" page
    When I select "WISTRON (ACER PERIPHERALS)" on "Supplier" Combobox
    And I select "NL-WISTRON-CNY CNY" on "Source Site" Combobox
    And I select "DOMOCSite1 desc" on Destination Site Combobox
    And I select "CNY" on "Currency" Combobox
    And I enter "2" on the "selectedLane.dateOffset" textfield
    When I click on "Submit" Button
    Then I verify no unexpected errors has occured
    When I click on "Approve" Button
    Then I verify no unexpected errors has occured
    Then I verify the Status as "APPROVED" on "Pricing" page
    Then I should see "history" icon is "displayed"
    And I should see "file_download" icon is "displayed"
    And I should see "refresh" icon is "displayed"

    Examples: 
      | dataFile  | fileName    | action                 | msg | msgType |
      | ItemAVLUI | ItemForEMCR | updateItemsForEMCreate |     |         |

  	#	#C668110 #21.1 regression
  #Scenario: Mass approve cost records via UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #When I set the itemNumber as "CR"
    #And I click on "Apply" Button
    #And I click on the Edit icon
    #And I select "APCC" on Destination Site Combobox
    #When I click on the "BUY" tab
    #And I wait till the page loads for "1" seconds
    #And I click on "Add" Button
    #And I select row "1" from the "selectedRecordKeys" "checkbox" list
    #And I click on Submit button
    #Then I verify row "1" cost records status as "PENDING"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #When I set the itemNumber as "CRCreate"
    #And I click on "Apply" Button
    #And I click on the Edit icon
    #And I select "APCC" on Destination Site Combobox
    #When I click on the "BUY" tab
    #And I wait till the page loads for "1" seconds
    #And I click on "Add" Button
    #And I select row "1" from the "selectedRecordKeys" "checkbox" list
    #And I click on Submit button
    #Then I verify row "1" cost records status as "PENDING"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I select "Pending" on the Status list
    #And I enter "CR" and "CRCreate" on Multiple "itemNumbers" textfield
    #And I click on "Apply" Button
#		And I wait "300" seconds
    #Then I verify search filter results are displayed
    #And I select all rows
    #And I click on "Approve" Button
    #Then I verify no unexpected errors has occured
    #Then I verify the "This record has been" successful message
    #And I click on "Clear" Button
    #And I select "Approved" on the Status list
    #And I enter "CR" and "CRCreate" on Multiple "itemNumbers" textfield
    #And I click on "Apply" Button
    #And I wait "300" seconds
    #Then I verify "Status" column has "APPROVED" as value displayed under search results for all rows
    #And I log out of HarmonyMTCM

  #C752257 #21.1 regression
  @bug
  Scenario: Mass reject cost records via UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    When I set the itemNumber as "CR"
    And I click on "Apply" Button
    And I click on the Edit icon
    And I select "BCC-BRH" on Destination Site Combobox
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I wait "5" seconds
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on Submit button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "PENDING"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    When I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait "30" seconds
    And I click on the Edit icon
    And I select "BCC-BRH" on Destination Site Combobox
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on Submit button
    Then I verify no unexpected errors has occured
    Then I verify row "1" cost records status as "PENDING"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I select "Pending" on the Status list
    And I enter "CR" and "CRCreate" on Multiple "itemNumbers" textfield
    And I click on "Apply" Button
    And I wait "30" seconds
    #CommentingDownload,Need to check functionality-Then I verify search filter results are displayed
    #And I select all rows
    #And I click on "Reject" Button
    #Then I verify no unexpected errors has occured
    #--Then I verify the "This record has been" successful message
    #And I click on "Clear" Button
    #And I select "Rejected" on the Status list
    #And I enter "CR" and "CRCreate" on Multiple "itemNumbers" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "45" seconds
    #Then I verify "Status" column has "REJECTED" as value displayed under search results for all rows
    And I log out of HarmonyMTCM

  #C668111 #PS regression
  #Scenario Outline: As an admin create new sourcing lane via excel upload
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
    #When I navigate to "Pricing" -> "Search Sourcing Lane"
    #And I click on "Clear" Button
    #And I enter "APCC" on "toSite" textfield
    #When I set the itemNumber as "CRMPN"
    #And I click on "Apply" Button
    #And I wait "80" seconds
    #Then I verify "1" rows listed in CostRecord
#
    #Examples: 
      #| dataFile       | fileName   | action           | msg | msgType |
      #| SourcingLaneUI | newSLAdmin | uploadNewSLAdmin |     | success |

  #C668112 #PS regression
  #CommentingForNow-needfixing
  #Scenario Outline: As a gcm user create new cost records via excel upload
    #Given I log into HarmonyMTCM as "mtcmUser" with "gcm3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I enter "APCC" on "toSite" textfield
    #When I set the itemNumber as "CRODMBuyMPN"
    #And I click on "Apply" Button
    #And I wait "30" seconds
    #Then I verify "1" rows listed in CostRecord
    #Examples: 
      #| dataFile     | fileName    | action                  | msg | msgType |
      #| CostRecordUI | newCRUpload | uploadCostRecordsForGCM |     | success |

  #C668113 #PS regression
  #Scenario Outline: As a super gcm user create new cost record via excel upload
  #Given I log into HarmonyMTCM as "mtcmUser" with "supergcm3"
  #When I navigate to "Main" -> "Upload"
  #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
  #When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #When I set the itemNumber as "CR"
  #And I enter "CCC" on "toSite" textfield
  #And I click on "Apply" Button
  #And I wait till the page loads for "10" seconds
  #Then I verify "1" rows listed with checkbox name "selectedPageKeys"
  #Examples:
  #| dataFile			| fileName      | action          		| msg | msgType |
  #| CostRecordUI	| newCRUpload		| uploadNewCRSuperGCM	|     | success |
  #
  #C668114 #PS regression
  #Scenario Outline: As a service gcm user create new cost record via excel upload
  #Given I log into HarmonyMTCM as "mtcmUser" with "servicegcm3"
  #When I navigate to "Main" -> "Upload"
  #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
  #When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #When I set the itemNumber as "CRCreate"
  #And I enter "CCC-CCC" on "toSite" textfield
  #And I click on "Apply" Button
  #And I wait till the page loads for "10" seconds
  #Then I verify "1" rows listed with checkbox name "selectedPageKeys"
  #Examples:
  #| dataFile			| fileName      | action          			| msg | msgType |
  #| CostRecordUI	| newCRUpload		| uploadNewCRServiceGCM	|     | success |
  #
  #C668113 #PS regression
  #Scenario Outline: As a supplier create new cost record via excel upload
  #Given I log into HarmonyMTCM as "mtcmUser" with "supplier1"
  #When I navigate to "Main" -> "Upload"
  #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
  #When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #When I set the itemNumber as "CRSuppCreate"
  #And I enter "CCC-CCC" on "toSite" textfield
  #And I click on "Apply" Button
  #And I wait till the page loads for "10" seconds
  #Then I verify "1" rows listed with checkbox name "selectedPageKeys"
  #Examples:
  #| dataFile			| fileName      | action          		| msg | msgType |
  #| CostRecordUI	| newCRUpload		| uploadNewCRSupplier	|     | success |
  #
  #C668114 #PS regression
  #Scenario Outline: As a em1 user create new cost record via excel upload
  #Given I log into HarmonyMTCM as "mtcmUser" with "em1"
  #When I navigate to "Main" -> "Upload"
  #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
  #When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #When I set the itemNumber as "CREMCreate"
  #And I enter "CCC-CCC" on "toSite" textfield
  #And I click on "Apply" Button
  #And I wait till the page loads for "10" seconds
  #Then I verify "1" rows listed with checkbox name "selectedPageKeys"
  #Examples:
  #| dataFile			| fileName      | action       	| msg | msgType |
  #| CostRecordUI	| newCRUpload		| uploadNewCREM	|     | success |
  #
  #C668115 #PS regression
  #CommentingForNow-needfixing
  #Scenario Outline: As a business admin user create new cost record via excel upload
    #Given I log into HarmonyMTCM as "mtcmUser" with "busadmin3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #When I set the itemNumber as "CRCreate"
    #And I enter "CCC-CCC" on "toSite" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #Then I verify "1" rows listed
#
    #Examples: 
      #| dataFile     | fileName    | action              | msg | msgType |
      #| CostRecordUI | newCRUpload | uploadNewCRBusAdmin |     | success |

  #C668119 #21.1 regression
  #Scenario Outline: Mass update By Functional Group with non-sell cost type via Excel Upload
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CR"
    #And I click on "Apply" Button
    #Given I wait "10" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Group" Button
    #And I "create" the items to the Functional Group "CRGrp" with "Save All"
    #Then I verify the "Functional Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
#
    #Examples: 
      #| dataFile                   | fileName              | action                  | errMsg                                                                 | msgType |
      #| MassUpdateCostRecordByFGUI | massUpdateCRFGInvalid | massUpdateCRByFGInvalid | Mass update has been attempted against an incorrect cost type for item | error   |

  #C752330 #21.1 regression
  #Scenario Outline: Mass update By Parent Group with non-sell cost type via Excel Upload
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set group name as "CRGrp"
    #And I click on "Apply" Button
    #And I wait till the page loads for "5" seconds
    #And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Parent Group" Button
    #And I save the parent group "CRPG"
    #Then I verify the "Parent Group Saved" successful message
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
#
    #Examples: 
      #| dataFile                         | fileName              | action                  | errMsg                                                                 | msgType |
      #| MassUpdateCostRecordByParentFGUI | massUpdateCRFGInvalid | massUpdateCRByPGInvalid | Mass update has been attempted against an incorrect cost type for item | error   |

  #C668120 #21.1 regression
  #Scenario Outline: Mass update upload with sell cost type and non-USD currency
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
#
    #Examples: 
      #| dataFile                   | fileName             | action          			  | errMsg 																						| msgType |
      #| MassUpdateCostRecordByFGUI | massUploadCRSellType | massUpdateCRCurrInvalid | [USD] currencyCode only allowed for mass update   | error   |

  #C668120 #21.1 regression
  #Scenario Outline: Mass update upload with sell cost type and non-USD currency
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
#
    #Examples: 
      #| dataFile                         | fileName             | action            			| errMsg 																					| msgType |
      #| MassUpdateCostRecordByParentFGUI | massUploadCRSellType | massUpdateCRCurrInvalid |[USD] currencyCode only allowed for mass update  | error   |

  #PDSUPPORT-9028	CSR07321873 - One Cost Prod - Mass Update SELL cost record by CFG and Parent FG
  #C752331 #C668128 #21.1 regression
  #Scenario Outline: Mass update by parent functional group with sell cost type and ww site via exc
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
    #When I navigate to "Admininstration" -> "Audit History"
    #And I click on "Clear" Button
    #And I enter "Mass Update Cost Record By ParentFG" on "type" textfield
    #And I click on "Apply" Button
    #Then I verify "Date Performed" column on row "1" has "today's date" as value displayed under search results
    #Then I verify "Role ID" column has "ADMIN" as value displayed under search results for all rows
    #Then I verify "User ID" column has "adminuser" as value displayed under search results for all rows
    #Then I verify "Action" column has "UPLOAD" as value displayed under search results for all rows
    #Then I verify "Type" column has "Mass Update Cost Record By ParentFG" as value displayed under search results for all rows
#
    #Examples: 
      #| dataFile                         | fileName             | action            | errMsg | msgType |
      #| MassUpdateCostRecordByParentFGUI | massUploadCRSellType | massUpdateCRValid |        | success |

  #C668127	 #C668121 #21.1 regression
  #Scenario Outline: Mass update by functional group with sell cost type and ww site via excel upload
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
    #When I navigate to "Admininstration" -> "Audit History"
    #And I click on "Clear" Button
    #And I enter "Mass Update Cost Record By ParentFG" on "type" textfield
    #And I click on "Apply" Button
    #Then I verify "Date Performed" column on row "1" has "today's date" as value displayed under search results
    #Then I verify "Role ID" column has "ADMIN" as value displayed under search results for all rows
    #Then I verify "User ID" column has "adminuser" as value displayed under search results for all rows
    #Then I verify "Action" column has "UPLOAD" as value displayed under search results for all rows
    #Then I verify "Type" column has "Mass Update Cost Record" as value displayed under search results for all rows
#
    #Examples: 
      #| dataFile                   | fileName             | action            | errMsg | msgType |
      #| MassUpdateCostRecordByFGUI | massUploadCRSellType | massUpdateCRValid |        | success |

  #C668131 #21.1 regression
  #Scenario Outline: As Admin, mass approve CRs via excel upload is not possibe, instead CR will goto pending state
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I select "Pending" on the Status list
    #When I set the itemNumber as "CRSuppCreate"
    #And I enter "DOMOCSite1 desc" on "toSite" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #Then I verify "1" rows listed
#
    #Examples: 
      #| dataFile     | fileName      | action       | errMsg | msgType |
      #| CostRecordUI | massApproveCR | addPendingCR |        | success |

  #scplatform-4628 CSR06685675 - S5 - One Cost prod - limitation in setting search filter favorite
  #Scenario: search CRs with 255char string lenth on multiple item search to save as saved filter
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I enter "255Chars" on "itemNumbers" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "25" seconds
    #Then I verify search filter results are displayed

  # PDSUPPORT-24897 10749553 - UCM MBI-13: Error in creating new Sourcing Lane
  @bug
  Scenario: Search and verify Search Sourcing Lane with new souricng lane for Buyer role
    Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    When I set the itemNumber as "CRCreate"
    And I click on "Apply" Button
    And I wait "30" seconds
    And I click on the Edit icon
    When I select the value on existing Lane
    And I select " SuppSite001 desc " on "Source Site" Combobox
    When I select "SERCOM desc" on "Supplier" Combobox
    Then I verify no unexpected errors has occured
    And I log out of HarmonyMTCM

  #scplatform-4499 CSR06640110 - Cost Record â€“ Cosmetic error after approving a cost record
  @bug
  Scenario: Create CR with approved status and search for approved status Cr and check close btn functionalities
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I should be landed on "Cost Records" page
    And I save number of rows after search
    Then I should see the "Reject" button should not be displayed and disabled
    Then I should see the "Approve" button should not be displayed and disabled
    Then I should see the "Submit" button should not be displayed and disabled
    And I should see the Close button should be disabled
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "2" seconds
    Then I should see the "Reject" button should not be displayed and disabled
    Then I should see the "Approve" button should not be displayed and disabled
    Then I should see the "Submit" button should not be displayed and disabled
    And I should see the "Close" button should be displayed and enabled
    And I click on Close button
    Then I verify no unexpected errors has occured
    And I verify no of rows got reduced 1 after "Close" button action
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I select "Closed" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I click on the Edit icon
    When I click on the "BUY" tab
    When I expand Filter view icon on CR SL
    Then I see Show All button is displayed
    Then I verify Status List under Hide By Status button
    Then I verify Columns List under Hide By Columns button
    And I log out of HarmonyMTCM

  #scplatform-4499 CSR06640110 - Cost Record â€“ Cosmetic error after approving a cost record
  @bug
  Scenario: Create CR with pending status and search for pending status Cr and check approve and close btn functionalities
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    When I click on the Edit icon
    And I select "APCC-APCC" on Destination Site Combobox
    And I select "NL-SERCOM-EUR EUR" on "Source Site" Combobox
    When I click on "Submit" Button
    Then I verify no unexpected errors has occured
    When I click on the "BUY" tab
    And I wait till the page loads for "1" seconds
    And I click on "Add" Button
    And I select row "1" from the "selectedRecordKeys" "checkbox" list
    And I click on "Submit" Button
    When I click on the save button
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I select "Pending" on the Status list
    And I click on "Apply" Button
    And I save number of rows after search
    Then I should see the "Reject" button should be disabled
    And I should see the Close button should be disabled
    And I should see the "Approve" button should be disabled
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "5" seconds
    And I should see the "Reject" button should be displayed and enabled
    And I should see the "Close" button should be displayed and enabled
    And I should see the "Approve" button should be displayed and enabled
    And I click on "Approve" Button
    Then I verify no unexpected errors has occured
    And I verify no of rows got reduced 1 after "Close" button action
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    When I click on the Edit icon
    When I click on the "BUY" tab
    When I expand Filter view icon on CR SL
    Then I see Show All button is displayed
    Then I verify Status List under Hide By Status button
    Then I verify Columns List under Hide By Columns button
    And I log out of HarmonyMTCM

  #scplatform-4499 CSR06640110 - Cost Record â€“ Cosmetic error after approving a cost record
  @bug
  Scenario: Create CR with closed status and search for closed status Cr and check close btn functionalities
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I set the itemNumber as "CRMPN"
    And I select "Closed" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "25" seconds
    Then I should be landed on "Cost Records" page
    Then I should see the "Reject" button should not be displayed and disabled
    Then I should see the "Approve" button should not be displayed and disabled
    Then I should see the "Submit" button should not be displayed and disabled
    Then I should see the "Close" button should not be displayed and disabled
    When I click on the Edit icon
    When I click on the "BUY" tab
    When I expand Filter view icon on CR SL
    Then I see Show All button is displayed
    Then I verify Status List under Hide By Status button
    Then I verify Columns List under Hide By Columns button
    And I log out of HarmonyMTCM

  #PDSUPPORT-26322
  #C668131 #21.1 regression
  #PDSUPPORT-26322 UCM MBI-13: Regression - Unable to Reject cost record via Excel upload
  Scenario: As GCM, mass approve and reject CRs via excel upload
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I select "Approved" on the Status list
    When I set the itemNumber as "CRODMBuyMPN"
    And I select "BUY" on Cost Type Combobox
    And I enter "DELL" on "business" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "2" seconds
    And I select and "Close" records if any
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "CostRecordUI" "uploadCRs" xlsx with "uploadCostRecordsForGCM" & verify "" "success"
    #	When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I select "Pending" on the Status list
    #When I set the itemNumber as "CRODMBuyMPN"
    #And I click on "Apply" Button
    #And I wait till the page loads for "2" seconds
    #Then I verify "4" rows listed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "gcm3"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "CRODMBuyMPN"
    And I select "Approved" on the Status list
    And I click on multiple search "toSites" icon
    And I wait till the page loads for "20" seconds
    And I set "CCC-CCC" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "toSites" and "confirm" the list on the popup
    And I click on multiple search "toSites" icon
    And I wait "20" seconds
    And I set "CCC" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "toSites" and "confirm" the list on the popup
    And I click on "Apply" Button
    And I wait "20" seconds
    And I save number of rows after search
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "CostRecordActionUI" "massApproveRejectCRsAsGCM" xlsx with "approveCR" & verify "" "success"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "CRODMBuyMPN"
    And I select "Rejected" on the Status list
    And I click on multiple search "toSites" icon
    And I wait "20" seconds
    And I set "APCC-1234" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "toSites" and "confirm" the list on the popup
    And I click on multiple search "toSites" icon
    And I wait till the page loads for "20" seconds
    And I set "BCC" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "toSites" and "confirm" the list on the popup
    And I click on "Apply" Button
    And I wait "20" seconds
    #Then I verify "1" rows listed in CostRecord
    #And I click on "Clear" Button
    #When I set the itemNumber as "CRODMBuyMPN"
    #And I select "Approved" on the Status list
    #And I click on multiple search "toSites" icon
    #And I wait till the page loads for "20" seconds
    #And I set "CCC-CCC" on Find textField on popup
    #And I click on "Search" Button on the popup
    #And I select "1" "toSites" and "confirm" the list on the popup
    #And I click on multiple search "toSites" icon
    #And I wait "20" seconds
    #And I set "CCC" on Find textField on popup
    #And I click on "Search" Button on the popup
    #And I select "1" "toSites" and "confirm" the list on the popup
    #And I click on "Apply" Button
    #And I wait "20" seconds
    #Then I verify row count increased by "2"
    And I log out of HarmonyMTCM

  #Scenario: PDSUPPORT-26322 UCM MBI-13: Regression - Unable to Approve cost record via Excel upload
  #Given I log into HarmonyMTCM as "mtcmUser" with "gcm3"
  #When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #And I select "Pending" on the Status list
  #When I set the itemNumber as "CRCreate"
  #And I enter "CCC-CCC" on "toSite" textfield
  #And I click on "Apply" Button
  #And I wait till the page loads for "10" seconds
  #Then I verify "1" rows listed
  #When I navigate to "Main" -> "Upload"
  #And I upload the "CostRecordActionUI" "approveRejectCRs" with "ApproveCR" & verify "" "success"
  #When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #And I select "Approved" on the Status list
  #When I set the itemNumber as "CRCreate"
  #And I enter "CCC-CCC" on "toSite" textfield
  #And I click on "Apply" Button
  #And I wait till the page loads for "10" seconds
  #Then I verify "1" rows listed
  #
  #Scenario Outline: As an admin Mass update by parent functional group with Buy cost type site via excel
  #	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
  #	When I navigate to "Main" -> "Upload"
  #	And I upload the "CostRecordUI" "uploadCRs" with "uploadCostRecordsForAdmin" & verify "" "success"
  #	When I navigate to "Main" -> "Upload"
  #	And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
  #		#verifying that records are updated
  #	When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #And I select "Pending" on the Status list
  #When I set the itemNumber as "CRODMBuyMPN"
  #And I select "BUY" on Cost Type Combobox
  #And I enter "DELL" on "business" textfield
  #And I click on "Apply" Button
  #And I wait till the page loads for "2" seconds
  #	Then I verify "1" rows listed
  #
  #	Examples:
  #		|dataFile						 											|fileName     					|action									|errMsg		|msgType	 |
  #		|MassUpdateCostRecordPFGExtensionBuyUI	 	|massUploadCRBuyType		|massUpdateCRValidAdmin	|  				|success   |

 @bug
 Scenario Outline: As an admin Mass update by parent functional group with Buy cost type site via excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #TestData
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadForCR" with "uploadItemForODMBuyCRMPN1" & verify "" "success"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "CRODMBuyMPN1"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "all" selected
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set group name as "CYCFG_TEST004"
    And I click on "Apply" Button
    Given I wait "10" seconds
    And I click on the name "CYCFG_TEST004"
    And I edit the Functional Group "CYCFG_TEST004" by adding item "CRODMBuyMPN1"
    #When I "uncheck" the "status" checkbox
    And I save the FG
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "CYCFG_TEST004" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "1"
    And I set Item Allocation value "100" on column "1"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set group name as "CYCFG_TEST004"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I click on the name "CYCFG_TEST004"
    When I "uncheck" the "status" checkbox
    And I save the FG
    Then I verify the "Functional Group Saved" successful message
    #TestData creation ends here
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "CostRecordUI" "uploadCRs" xlsx with "uploadCostRecordsForAdmin" & verify "" "success"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<errMsg>" "<msgType>"
    #verifying that records are updated
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I select "Pending" on the Status list
    When I set the itemNumber as "CRODMBuyMPN1"
    And I select "BUY" on Cost Type Combobox
    And I enter "MTCM-Tenant-123" on "business" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "2" seconds
    #Then I verify "Status" column has "PENDING" as value displayed under search results for all rows
    #Then I verify "Cost Type" column has "BUY" as value displayed under search results for all rows
    #Then I verify "Destination Site" column has "WW" as value displayed under search results for all rows
    #Then I verify "Currency" column has "USD" as value displayed under search results for all rows
    #Then I verify "Supplier" column has "HITACHI" as value displayed under search results for all rows
    #Then I verify "Item Business" column has "MTCM-Tenant-123" as value displayed under search results for all rows
    #Then I verify "Commodity Name" column has "MISCELLANEOUS" as value displayed under search results for all rows
    #Then I verify "Item" column has "CRODMBuyMPN" as value displayed under search results for all rows
    #Then I verify "Item Description" column has "SI,CUS,DIMM,256MB,PANASONIC" as value displayed under search results for all rows
    #Then I verify "Functional Group (CFG Only)" column has "CYCFG_TEST004" as value displayed under search results for all rows
    #Then I verify "Parent Functional Group (CFG Only)" column has "ParentTest202" as value displayed under search results for all rows
    #Then I verify "MATERIAL" column has "10.0" as value displayed under search results on any of the rows
    #When I click on the Edit icon
    #When I click on the "history" icon
    #Then I should be landed on Audit History page
    #Then I verify "User ID" column is displayed under search results
    #Then I verify "Date Performed" column is displayed under search results
    #Then I verify "Comment" column is displayed under search results
    #Then I verify "Action" column is displayed under search results
    #Then I verify "Type" column has value "PcmSourcingLane" displayed under search results for all rows
    #Then I verify "Role ID" column has value "ADMIN" displayed under search results for all rows
 #		Then I verify "Action" column has value "BUY UPLOAD" displayed under search results for all rows
 		
    Examples: 
      | dataFile                              | fileName            | action                 | errMsg | msgType |
      | MassUpdateCostRecordPFGExtensionBuyUI | massUploadCRBuyType | massUpdateCRValidAdmin |        | success |

  #Scenario: Verify validation error for mass upload against Price : ODMBUY Cost Record Parent(*.xls) for invalid Cost Type
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminUser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "MassUpdateCostRecordPFGExtensionODMBuyUI" "massUploadCRBuyType" with "massUpdateCRValidAdmin" & verify "Mass update has been attempted against an incorrect cost type for item" "error"

  @bug
  Scenario: Verify validation error for mass upload against Price : BUY Cost Record Parent(*.xls) for non-existing supplier
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPPLIER"
    And I click on the "Business Document" tab on "Manage Roles" page
    When I "check" the CR "MassUpdateCostRecordPFGExtensionBuyUI" checkbox
    When I "check" the CR "MassUpdateCostRecordPFGExtensionODMBuyUI" checkbox
    And I click on the save button
    Then I verify the CR "MassUpdateCostRecordPFGExtensionBuyUI" checkbox status as "checked"
    Then I verify the CR "MassUpdateCostRecordPFGExtensionODMBuyUI" checkbox status as "checked"
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "supplier1"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "MassUpdateCostRecordPFGExtensionBuyUI" "massUploadCRBuyType" with "" & verify "BusinessNotFoundOrRestricted" "error"

  Scenario: Verify data file type not exists for roles with Mass Update by Parent FG by BUY and ODM BUY disabled
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPPLIER"
    And I click on the "Business Document" tab on "Manage Roles" page
    When I "uncheck" the CR "MassUpdateCostRecordPFGExtensionBuyUI" checkbox
    When I "uncheck" the CR "MassUpdateCostRecordPFGExtensionODMBuyUI" checkbox
    And I click on the save button
    Then I verify the CR "MassUpdateCostRecordPFGExtensionBuyUI" checkbox status as "unchecked"
    Then I verify the CR "MassUpdateCostRecordPFGExtensionODMBuyUI" checkbox status as "unchecked"
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "supplier1"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    Then I should not see option with name "Price : BUY Cost Record Parent Functional Group(*.xls)" on Uploads
    Then I should not see option with name "Price : ODMBUY Cost Record Parent(*.xls)" on Uploads

  #Scenario Outline: As an superGCM Mass update by parent functional group with Buy cost type site via excel
  #	Given I log into HarmonyMTCM as "mtcmUser" with "adminUser3"
  #	When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #And I select "Approved" on the Status list
  #When I set the itemNumber as "CRODMBuyMPN"
  #And I enter "DELL" on "business" textfield
  #And I click on "Apply" Button
  #And I wait till the page loads for "2" seconds
  #And I select and "Close" records if any
  #And I select "Rejected" on the Status list
  #And I click on "Apply" Button
  #And I wait till the page loads for "2" seconds
  #And I select and "Close" records if any
  #	When I navigate to "Main" -> "Upload"
  #	And I upload the "CostRecordUI" "uploadCRs" with "uploadCostRecordsForSuperGCM" & verify "" "success"
  #	When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #And I select "Pending" on the Status list
  #When I set the itemNumber as "CRODMBuyMPN"
  #And I click on "Apply" Button
  #And I wait till the page loads for "2" seconds
  #Then I verify "1" rows listed
  #And I log out of HarmonyMTCM
  #Given I log into HarmonyMTCM as "mtcmUser" with "supergcm3"
  #When I navigate to "Main" -> "Upload"
  #	And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
  #		#verifying that records are updated
  #	When I navigate to "Pricing" -> "Search Cost Records"
  #And I click on "Clear" Button
  #And I enter "DELL" on "business" textfield
  #And I enter "Changed as part if BUY Mass Update" on "itemDescription" textfield
  #And I select "BUY" on Cost Type Combobox
  #When I set the itemNumber as "CRODMBuyMPN"
  #And I click on "Apply" Button
  #And I wait till the page loads for "2" seconds
  #	Then I verify "1" rows listed
  #
  #	Examples:
  #		|dataFile						 											|fileName     					|action											|errMsg		|msgType	 |
  #		|MassUpdateCostRecordPFGExtensionBuyUI	 	|massUploadCRBuyType		|massUpdateSuperGCMCR				|  				|success   |
  #
  #	Examples:
  #		|dataFile						 											|fileName     					|action											|errMsg		|msgType	 |
  #		|MassUpdateCostRecordPFGExtensionBuyUI	 	|massUploadCRBuyType		|massUpdateCRValidSuperGCM	|  				|success   |
  #
  Scenario Outline: Validate upload failure for Mass update by parent functional group with List CT
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"

    Examples: 
      | dataFile                              | fileName             | action                      | errMsg                                                                   | msgType         |
      | MassUpdateCostRecordPFGExtensionBuyUI | massUploadCRListType | massUpdateCRValidonListType | Value allowed in column SLC row 3 only if CostType is BUY,ODMBUY,SERVICE | validationError |

  #Scenario Outline: Validate upload failure for Mass update by parent functional group for DELL Item with multiple CT
  #	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
  #	When I navigate to "Main" -> "Upload"
  #	And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
  #	Examples:
  #		|dataFile						 											|fileName     					|action													|errMsg																																		|msgType						 |
  #		|MassUpdateCostRecordPFGExtensionBuyUI	 	|massUploadCRBuyType		|massUpdateCRValidonMultipleCR	|Value allowed in column SLC row 3 only if CostType is BUY,ODMBUY,SERVICE |validationError   	 |
  #
  #Scenario Outline: Validate upload failure for Mass update by parent functional group - BUY and ODMBUY for Non Dell Item and multiple CT
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
#
    #Examples: 
      #| dataFile                              | fileName                      | action                        | errMsg                                           | msgType |
      #| MassUpdateCostRecordPFGExtensionBuyUI | massUploadCRNonDellMultipleCT | massUpdateCRValidonMultipleCR | Mass update can be done only for [BUY] cost type | error   |

  Scenario Outline: Verify validation error for mass upload against Parent CFG for non-DELL ITEM AVL
    Given I log into HarmonyMTCM as "mtcmUser" with "adminUser3"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
  	And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"

    Examples: 
      | dataFile                                 | fileName            | action           | errMsg                                                         | msgType         |
      | MassUpdateCostRecordPFGExtensionBuyUI    | massUploadCRNonDell | uploadCostRecord | Value (SUPPLIER) in column BusinessEntityType row 3 is invalid | validationError |
      | MassUpdateCostRecordPFGExtensionODMBuyUI | massUploadCRNonDell | uploadCostRecord | Value (SUPPLIER) in column BusinessEntityType row 3 is invalid | validationError |

#Scenario Outline: Verify validation error for mass upload against Parent CFG for BUY and ODM BUy non allowable Commodity
#	Given I log into HarmonyMTCM as "mtcmUser" with "adminUser3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
  #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
  #Examples: 
      #| dataFile                                 | fileName            		| action           | errMsg                                                         							 |msgType|
      #| MassUpdateCostRecordPFGExtensionBuyUI    | NonAllowedCommodity 		| uploadCostRecord | The following Site WW with Item 0325T has disallowed item category [KEYBOARD] | error |
      #| MassUpdateCostRecordPFGExtensionODMBuyUI | NonAllowedCommodityODM | uploadCostRecord | The following Site WW with Item 0325T has disallowed item category [KEYBOARD] | error |

  ##
  @bug
  Scenario: Verify error message displayed when trying to one or more lanes existing for the item
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    When I navigate to "Pricing" -> "New Sourcing Lane"
    And I set the itemNumber as "0288D"
    And I enter "INTEL" on "supplierName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "1" seconds
    When I select first "1" rows from the "selectedPageKeys" "radio" list
    Then I verify "One or more sourcing lanes already exist for this item for Functional Group for Functional Group =[19410,TestFG1]" message displayed
    
#Scenario: Verify SUPERGCM data file type not exists for roles with Mass Update by Parent FG by BUY and ODM BUY disabled
#	 Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Administration" -> "Manage Roles"
    #Then I should be landed on "Available Roles" page
    #And I click on the name "SUPER_GCM"
    #And I click on the "Business Document" tab on "Manage Roles" page
    #When I "uncheck" the CR "MassUpdateCostRecordPFGExtensionBuyUI" checkbox
    #When I "uncheck" the CR "MassUpdateCostRecordPFGExtensionODMBuyUI" checkbox
    #And I click on the save button  
    #Then I verify the CR "MassUpdateCostRecordPFGExtensionBuyUI" checkbox status as "unchecked"
    #Then I verify the CR "MassUpdateCostRecordPFGExtensionODMBuyUI" checkbox status as "unchecked"
    #And I log out of HarmonyMTCM
#		Given I log into HarmonyMTCM as "mtcmUser" with "supergcm3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
#		Then I should not see option with name "Price : BUY Cost Record Parent Functional Group(*.xls)" on Uploads
#		Then I should not see option with name "Price : ODMBUY Cost Record Parent(*.xls)" on Uploads
#		And I log out of HarmonyMTCM
#	 Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Administration" -> "Manage Roles"
    #Then I should be landed on "Available Roles" page
    #And I click on the name "SUPER_GCM"
    #And I click on the "Business Document" tab on "Manage Roles" page
    #When I "check" the CR "MassUpdateCostRecordPFGExtensionBuyUI" checkbox
    #When I "check" the CR "MassUpdateCostRecordPFGExtensionODMBuyUI" checkbox
    #And I click on the save button  
    #Then I verify the CR "MassUpdateCostRecordPFGExtensionBuyUI" checkbox status as "checked"
    #Then I verify the CR "MassUpdateCostRecordPFGExtensionODMBuyUI" checkbox status as "checked"
    #And I log out of HarmonyMTCM
#		Given I log into HarmonyMTCM as "mtcmUser" with "supergcm3"
    #When I navigate to "Upload/Manage Jobs" -> "Pricing"
#		Then I should see option with name "Price : BUY Cost Record Parent Functional Group(*.xls)" on Uploads
#		Then I should see option with name "Price : ODMBUY Cost Record Parent(*.xls)" on Uploads
#		And I log out of HarmonyMTCM
		
		@bug
		Scenario Outline: As an admin Mass update by parent functional group with ODM Buy cost type site via excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #TestData
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadForCR" with "uploadItemForODMBuyCRMPN2" & verify "" "success"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "CRODMBuyMPN2"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "all" selected
   	When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set group name as "UI201905241439"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I click on the name "UI201905241439"
    And I edit the Functional Group "UI201905241439" by adding item "CRODMBuyMPN2"
    And I save the FG
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "UI201905241439" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "1"
    And I set Item Allocation value "100" on column "1"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set group name as "UI201905241439"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I click on the name "UI201905241439"
    When I "uncheck" the "status" checkbox
    And I save the FG
    #TestData creation ends here
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "CostRecordUI" "uploadCRs" xlsx with "uploadCostRecordsForODMBUY" & verify "" "success"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<errMsg>" "<msgType>"
    #verifying that records are updated
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I select "Pending" on the Status list
    When I set the itemNumber as "CRODMBuyMPN2"
    And I select "ODM BUY" on Cost Type Combobox
    And I enter "MTCM-Tenant-123" on "business" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "2" seconds
    Then I verify "Status" column has "PENDING" as value displayed under search results for all rows
    Then I verify "Cost Type" column has "ODM BUY" as value displayed under search results for all rows
    Then I verify "Destination Site" column has "WW" as value displayed under search results for all rows
    Then I verify "Currency" column has "USD" as value displayed under search results for all rows
    Then I verify "Supplier" column has "HITACHI" as value displayed under search results for all rows
    Then I verify "Item Business" column has "MTCM-Tenant-123" as value displayed under search results for all rows
    Then I verify "Commodity Name" column has "MISCELLANEOUS" as value displayed under search results for all rows
    Then I verify "Item" column has "CRODMBuyMPN" as value displayed under search results for all rows
    Then I verify "Item Description" column has "SI,CUS,DIMM,256MB,PANASONIC" as value displayed under search results for all rows
    Then I verify "Functional Group (CFG Only)" column has "UI201905241439" as value displayed under search results for all rows
    Then I verify "Parent Functional Group (CFG Only)" column has "ODMBUYParent" as value displayed under search results for all rows
    Then I verify "MATERIAL" column has "10.0" as value displayed under search results on any of the rows
		When I click on the Edit icon
		When I click on the "history" icon
    Then I should be landed on Audit History page
    Then I verify "User ID" column is displayed under search results
    Then I verify "Date Performed" column is displayed under search results
    Then I verify "Comment" column is displayed under search results
    Then I verify "Action" column is displayed under search results
    Then I verify "Type" column has value "PcmSourcingLane" displayed under search results for all rows
    Then I verify "Role ID" column has value "ADMIN" displayed under search results for all rows
 		Then I verify "Action" column has value "ODMBUY UPLOAD" displayed under search results for all rows
		    Examples: 
      | dataFile                              	 | fileName            | action                 | errMsg | msgType |
      | MassUpdateCostRecordPFGExtensionODMBuyUI | massUploadCRBuyType | massUpdateCRValidODMBUY|        | success |

@bug
Scenario: Verify validation error for mass upload against Parent CFG against ODMBUY for non allowed cost Type
	Given I log into HarmonyMTCM as "mtcmUser" with "adminUser3"
  When I navigate to "Upload/Manage Jobs" -> "Pricing"
	And I upload the "MassUpdateCostRecordPFGExtensionODMBuyUI" "nonAllowedCTForODMBUYMassUpdate" with "massUpdateCR" & verify "Mass update has been attempted against an incorrect cost type for item : #KJVV4, Mass update can be done only for [ODM BUY] cost type" "error"
	  
	  #scplatform-7383 Cost Record Details page - Filter View
#		 Scenario: search for cost records by filtering with LOB and Platform values
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3"
    #When I navigate to "Pricing" -> "Search Cost Records"
    #And I click on "Clear" Button
    #And I set the itemNumber as "CRCreate"
    #And I select "Approved" on the Status list
    #And I click on "Apply" Button
    #And I wait till the page loads for "75" seconds
    #Then I should see the Close button should be disabled
    #When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #Then I should see the "Close" button should be displayed and enabled
    #And I log out of HarmonyMTCM
#		
#
### tabs under new sourcing lane - nt correctly created and SUmmary verfication pending

#-Not Required as multiple item number text has been verified already
#Scenario: Verify Multiple Item text field validations in the UI page
#	Given I log into HarmonyMTCM as "mtcmUser" with "adminUser3"
#  When I navigate to "Pricing" -> "Search Cost Records"
#  And I click on Multiple Item text and Enter valid inputs in the UI page
#  And I click on "Apply" Button
 
 Scenario: Search using multiple item names along with valid search cost records
    Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    When I navigate to "Pricing" -> "Search Cost Records"
     And I clicked on Clear Button
    And I enter "002M6" and "002008129" on Multiple "itemNumbers" textfield
    #And I enter "002M6" and "002008129" in "categories"
    And I click on "Apply" Button
    And I wait till the page loads for "500" seconds
    And I select first "2" rows from the "selectedPageKeys" "checkbox" list

  Scenario: Search using multiple item names along with invalid search cost records
    Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    When I navigate to "Pricing" -> "Search Cost Records"
    And I clicked on Clear Button
    And I enter "001" and InvalidMultiple "002" in "categories"
    And I click on "Apply" Button
    And I wait till the page loads for "500" seconds
    Then I validated the No records found error message in the UI"
	
