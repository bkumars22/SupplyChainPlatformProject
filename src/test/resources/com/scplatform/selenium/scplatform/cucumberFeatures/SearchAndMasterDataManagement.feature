@HarmonySearch
Feature: Search Workflow Test Plan

  Scenario Outline: Upload Item values and verify success message, then verify status on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Then I verify the "<action>" on the Search Items page

    Examples: 
      | dataFile  | fileName                   | action               | msg | msgType |
      #|ItemUI		    	 |ItemUploadForSearchItem    |uploadItemForItem      |	    |success |
      | ItemAVLUI | ItemUploadForSearchItemAVL | uploadItemForItemAVL |     | success |

  #scplatform-4773 CSR06730922 - CLONE of - [8822998 ] -[PIT] -UCM MBI4-scplatform Release 20.1 PIT: Item Assignment - Responsibility menu blank
  Scenario Outline: Assign resp for the items uploaded
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "<itemUploaded>"
    And I click on "Apply" Button
    And I wait "50" seconds
    And I "assign" responsibility "mike_quick@dell.com" to the item "<itemUploaded>" selected
    Then I verify the responsibility "mike_quick@dell.com" "assigned" to "<itemUploaded>"
    And I wait "50" seconds
    And I verify the "Responsibility has been assigned successfully" successful message

    Examples: 
      | itemUploaded |
      #|item		    |
      | itemAVL      |

  # PDSUPPORT-24978 10806656 - UCM MBI-13: Regression - Item Assignment screen error when search with Responsibility
  Scenario: Assign resp for the items uploaded
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemAVL"
    And I select "Production" on "Responsibility" Combobox
    And I click on "Apply" Button
    And I wait "50" seconds
    Then I verify no unexpected errors has occured
    And I wait "50" seconds
    Then I verify search filter results are displayed

  Scenario Outline: UnAssign resp for the items uploaded
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "<itemUploaded>"
    And I click on "Apply" Button
    And I wait "50" seconds
    And I "unassign" responsibility "mike_quick@dell.com" to the item "<itemUploaded>" selected
    Then I verify the responsibility "" "unassigned" to "<itemUploaded>"
    And I wait "50" seconds
    And I verify the "Responsibility has been unassigned successfully" successful message

    Examples: 
      | itemUploaded |
      #|item	    	 |
      | itemAVL      |

  #CSR07372891 - One Cost Prod - Same item assigned to 2 CFGs with same name - H65KM(20.3)
  #scplatform-2784	Item Assignment - UI Improvement
  Scenario: Assign multiple resp for one item without switching pages
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemAVL"
    And I click on "Apply" Button
    And I wait "50" seconds
    #TestData clearance
    And I "unassign" responsibility "ADMIN" to the item "itemAVL" selected
    Then I verify the responsibility "" "unassigned" to "itemAVL"
    And I wait "50" seconds
    And I verify the "Responsibility has been unassigned successfully" successful message
    ###################TestData clearance ends here################
    And I "assign" responsibility "aaronrivas" to the item "itemAVL" selected
    Then I verify the responsibility "aaronrivas" "assigned" to "itemAVL"
    And I verify the "Responsibility has been assigned successfully" successful message
    And I "assign" responsibility "ADMIN" to the item "itemAVL" selected
    Then I verify the responsibility "ADMIN" "assigned" to "itemAVL"
    And I verify the "Responsibility has been reassigned successfully" successful message

  #scplatform-4831 Item assignment: The unwanted error message is pop up for successful assignment.
  #scplatform-2784	Item Assignment - UI Improvement
  Scenario: Assign and unassign resp for multiple items without switching pages
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemAVL*"
    And I click on "Apply" Button
    And I wait "50" seconds
    #TestData clearance
    And I "unassign" responsibility "ADMIN" to the item "all" selected
    Then I verify the responsibility "" "unassigned" to "itemAVL"
    And I verify the "Responsibility has been unassigned successfully" successful message
    ##############################
    And I "assign" responsibility "aaronrivas" to the item "all" selected
    Then I verify the responsibility "aaronrivas" "assigned" to "itemAVL" on all rows
    And I verify the "Responsibility has been assigned successfully" successful message
    And I "assign" responsibility "ADMIN" to the item "all" selected
    Then I verify the responsibility "ADMIN" "assigned" to "itemAVL" on all rows
    And I verify the "Responsibility has been reassigned successfully" successful message

  #PDSUPPORT-46616	Item Assignment - UI Improvement - reassign with warning message verification
  Scenario: Verify re-assin successful msg when trying to assign resp and role to item which is already assigned with
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemAVL"
    And I click on "Apply" Button
    And I wait "50" seconds
    #TestData clearance
    And I "unassign" responsibility "ADMIN" to the item "itemAVL" selected
    Then I verify the responsibility "" "unassigned" to "itemAVL"
    And I verify the "Responsibility has been unassigned successfully" successful message
    ###################TestData clearance ends here################
    And I "assign" responsibility "ADMIN" to the item "itemAVL" selected
    Then I verify the responsibility "ADMIN" "assigned" to "itemAVL"
    And I verify the "Responsibility has been assigned successfully" successful message
    And I "assign" responsibility "ADMIN" to the item "itemAVL" selected
    And I verify the warning messages "Production responsibility has already been assigned to" followed by clicking "Yes" button
    And I verify the "Responsibility has been reassigned successfully" successful message
    Then I verify the responsibility "ADMIN" "assigned" to "itemAVL"

  #scplatform-4398 CSR06612777 - S3 - One Cost prod - regional assignment 'region' not sorted
  Scenario: Verify regional assignment 'region' is sorted
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    Then I verify Region List is on sorted Order
    And I click on "Clear" Button
    When I set the itemNumber as "itemAVL"
    And I click on "Apply" Button
    And I wait "50" seconds
    And I "assign" responsibility "ADMIN" to the item "itemAVL" selected

  #scplatform-4825 Manage role setting not working for Item Assignment
  Scenario: Verify error message when trying to assign resp to self role
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPER_GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I "uncheck" AssignToSelf checkBox
    And I click on the save button
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "aaron_x_chen"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "0288T"
    And I click on "Apply" Button
    #TestData clearance
    And I "unassign" responsibility "aaron_x_chen" to the item "itemAVL" selected
    Then I verify the responsibility "" "unassigned" to "itemAVL"
    And I verify the "Responsibility has been unassigned successfully" successful message
    ###################TestData clearance ends here################
    And I "assign" responsibility "aaron_x_chen" to the item "itemAVL" selected
    Then I verify "Cannot assign to Self" error message
    And I log out of HarmonyMTCM

  #   scplatform-4751 7275852 Item Assignment - UI Improvement (Pre-Dell Demo) Defect6
  #	scplatform-4750 7275852 Item Assignment - UI Improvement (Pre-Dell Demo) Defect5
  Scenario: Verify success message for assigning same role and assign to same user for service role
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "02639"
    And I click on "Apply" Button
    #TestData clearance
    When I select all rows
    And I click on "Unassign Responsibility" Button
    And I click on "Yes, Unassign" confirmation Button
    And I verify the "Responsibility has been unassigned successfully" successful message
    And I log out of HarmonyMTCM
    ###################TestData clearance ends here################
    Given I log into HarmonyMTCM as "mtcmUser" with "jessica_sh_ho_gcm"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "02639"
    And I click on "Apply" Button
    And I "assign" "Service" responsibility to "jessica_sh_ho_sgcm" assigned with "" as existing role
    #	And I "assign" responsibility "SERVICE" to the item "2" selected
    Then I verify the "Responsibility has been assigned successfully" successful message
    Then I verify the responsibility "SERVICE" "assigned" to "02639" on any row
    And I "assign" "Production" responsibility to "jessica_sh_ho_sgcm" assigned with "SERVICE" as existing role
    Then I verify the "Responsibility has been assigned successfully" successful message
    And I log out of HarmonyMTCM

  #scplatform-4748 7275852 Item Assignment - UI Improvement (Pre-Dell Demo) Defect3
  Scenario: Verify error message Cannot unassign another user for assigning different role and assign to diff user for production role
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "02650"
    And I click on "Apply" Button
    #TestData clearance
    When I select all rows
    And I click on "Unassign Responsibility" Button
    And I click on "Yes, Unassign" confirmation Button
    And I verify the "Responsibility has been unassigned successfully" successful message
    #ends here from prev test execution
    #create test data
    And I "assign" "Production" responsibility to "ooi_jin_tan_gcm" assigned with "" as existing role
    Then I verify the "Responsibility has been assigned successfully" successful message
    Then I verify the responsibility "PRODUCTION" "assigned" to "02639" on any row
    #precondition
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I "uncheck" unAssign OtherUsers checkBox
    And I click on the save button
    And I log out of HarmonyMTCM
    #workflow starts here
    Given I log into HarmonyMTCM as "mtcmUser" with "jessica_sh_ho_gcm"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "02650"
    And I click on "Apply" Button
    #assign from production to assign to Service
    And I "assign" "Service" responsibility to "jessica_sh_ho_gcm" assigned with "PRODUCTION" as existing role
    #	Then I verify "The SERVICE responsibility cannot be assigned to user jessica" error message
    Then I verify the "Responsibility has been assigned successfully" successful message
    And I "assign" "Production" responsibility to "jessica_sh_ho_gcm" assigned with "PRODUCTION" as existing role
    And I click "Yes" on the warning popup on assign resp page with message "The Production responsibility has already been assigned to"
    Then I verify "Cannot unassign another user" error message
    #	When I select all rows
    #	And I click on "Unassign Responsibility" Button
    #	And I click on "Yes, Unassign" confirmation Button
    #	Then I verify "Cannot unassign another user" error message
    And I log out of HarmonyMTCM

  #scplatform-4745 7275852 Item Assignment - UI Improvement (Pre-Dell Demo) Defect1
  #scplatform-4747 7275852 Item Assignment - UI Improvement (Pre-Dell Demo) Defect2
  Scenario: scplatform-4747 7275852 Item Assignment - UI Improvement (Pre-Dell Demo) Defect2
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "02650"
    And I click on "Apply" Button
    #TestData clearance
    When I select all rows
    And I click on "Unassign Responsibility" Button
    And I click on "Yes, Unassign" confirmation Button
    And I verify the "Responsibility has been unassigned successfully" successful message
    #ends here from prev test execution
    #create test data
    And I "assign" "Production" responsibility to "jessica_sh_ho_gcm" assigned with "" as existing role
    Then I verify the "Responsibility has been assigned successfully" successful message
    Then I verify the responsibility "PRODUCTION" "assigned" to "02639" on any row
    #precondition
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I "uncheck" unAssign OtherUsers checkBox
    And I click on the save button
    And I log out of HarmonyMTCM
    #workflow starts here
    Given I log into HarmonyMTCM as "mtcmUser" with "jessica_sh_ho_gcm"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "02650"
    And I click on "Apply" Button
    #assign from production to assign to Service
    And I "assign" "Service" responsibility to "jessica_sh_ho_gcm" assigned with "PRODUCTION" as existing role
    Then I verify the "Responsibility has been assigned successfully" successful message
    Then I verify the responsibility "PRODUCTION" "assigned" to "02639" on any row
    Then I verify the responsibility "SERVICE" "assigned" to "02639" on any row
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "ooi_jin_tan_gcm"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "02650"
    And I click on "Apply" Button
    And I "assign" "Production" responsibility to "ooi_jin_tan_gcm" assigned with "SERVICE" as existing role
    And I click "Yes" on the warning popup on assign resp page with message "The Production responsibility has already been assigned to"
    Then I verify "Cannot unassign another user" error message
    And I "assign" "Service" responsibility to "ooi_jin_tan_gcm" assigned with "PRODUCTION" as existing role
    And I click "Yes" on the warning popup on assign resp page with message "The Service responsibility has already been assigned to"
    Then I verify "Cannot unassign another user" error message
    And I "assign" "Production" responsibility to "ooi_jin_tan_gcm" assigned with "PRODUCTION" as existing role
    And I click "Yes" on the warning popup on assign resp page with message "The Production responsibility has already been assigned to"
    Then I verify "Cannot unassign another user" error message
    And I "assign" "Service" responsibility to "ooi_jin_tan_gcm" assigned with "SERVICE" as existing role
    And I click "Yes" on the warning popup on assign resp page with message "The Service responsibility has already been assigned to"
    Then I verify "Cannot unassign another user" error message
    And I log out of HarmonyMTCM

  Scenario Outline: Upload Item values with missing column values and verify error message
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"

    Examples: 
      | dataFile  | fileName                   | action                        | msg                                             | msgType         |
      #|ItemUI		      |ItemUploadForSearchItem   |uploadItemWithMissingName     |Required value missing in column ItemIdentifier  |validationError   |
      | ItemAVLUI | ItemUploadForSearchItemAVL | uploadItemWithMissingName     | Required value missing in column ItemIdentifier | validationError |
      #|ItemUI		      |ItemUploadForSearchItem   |uploadItemWithMissingBusiness |Required value missing in column BusinessEntity  |validationError   |
      | ItemAVLUI | ItemUploadForSearchItemAVL | uploadItemWithMissingBusiness | Required value missing in column BusinessEntity | validationError |

   Scenario Outline: Upload Item and ItemAVL with business values updated
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"

    Examples: 
      | dataFile  |   fileName                   	| action                        | msg | msgType |
      | ItemUI		|  ItemUploadForSearchItem   	  |uploadItemWithUpdatedBusiness  |     | success |
      | ItemAVLUI |  ItemUploadForSearchItemAVL 	| uploadItemWithUpdatedBusiness |     | success |

  #	scplatform-4397 CSR06612785 - S3 - Please log a CSR for multiple item number search
  # implemented for bot single and multiple item searches
  Scenario Outline: Navigate to Search Items and AVL pages then verify download button visibility
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "<subMenu>"
    And I click on "Clear" Button
    Then I should see "file_download" icon is "not displayed"
    And I enter "MTCM-Tenant-123" on "value(businessName)" textfield
    When I set the itemNumber as "JD002"
#    And I select "ACTIVE" on "Item EOL State" Combobox
    And I click on "Apply" Button
    Then I verify search filter results are displayed
    Then I verify "Item EOL State" column has "ACTIVE" as value displayed under search results for all rows
    And I should see "file_download" icon is "displayed"
    And I verify labelName "Member of Group" not found on the loaded page
    #And I wait "100" seconds
    #And I click on download Button and verify the result for "itemSearch" for "verifyItemSearchResults"

    Examples: 
      | subMenu  |
      | Items    |
      | Item AVL |

  Scenario Outline: Navigate to Search Items and AVL pages then verify maximize/minimize search results screen
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "<subMenu>"
    And I click on "Clear" Button
    When I set the itemNumber as "JD002"
    And I click on "Apply" Button
    Then I verify search filter results are displayed
    When I click on the fullscreen "maximize" icon
    Then "Filters" section should not be displayed
    When I click on the fullscreen "minimize" icon
    Then "Filters" section should be displayed

    Examples: 
      | subMenu  |
      | Items    |
      | Item AVL |

  #Scenario: Search with Multiple Commodity Names on Search Items
    #Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    #When I navigate to "Search" -> "Items"
    #And I click on "Clear" Button
    #And I enter "AC ADAPTER" and "ACCESSORY" on Multiple "categoryNames" textfield
    #And I click on "Apply" Button
    #And I wait "300" seconds 
    #And I wait till the page loads for "300" seconds
    #Then I verify search filter results are displayed

  #Just clicking on the rows to make sure there are some test results
  Scenario: Search with Multiple Commodity Names by clicking on Icon on Search Items
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I click on multiple search "categories" icon
    And I wait till the page loads for "20" seconds
    And I set "AC ADAPTER" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "categories" and "confirm" the list on the popup
    Then I verify the selected names on the multiple "categoryNames" textfield
    And I click on "Apply" Button
    And I wait "100" seconds 
    #And I wait till the page loads for "100" seconds
    Then I verify search filter results are displayed

  #Scenario: Search with Multiple Commodity Names on Search Item AVL
    #Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    #When I navigate to "Search" -> "Item AVL"
    #And I click on "Clear" Button
    #And I enter "AC ADAPTER" and "ACCESSORY" on Multiple "categoryNames" textfield
    #And I click on "Apply" Button
    #And I wait "300" seconds 
    #And I wait till the page loads for "400" seconds
    #Then I verify search filter results are displayed

  #Just clicking on the rows to make sure there are some test results
  Scenario: Search with Multiple Commodity Names by clicking on Icon on Search Item AVL
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I click on multiple search "categories" icon
    And I wait till the page loads for "20" seconds
    And I set "AC ADAPTER" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "categories" and "confirm" the list on the popup
    Then I verify the selected names on the multiple "categoryNames" textfield
    And I click on "Apply" Button
    And I wait "300" seconds 
    #And I wait till the page loads for "350" seconds
    Then I verify search filter results are displayed

  #	scplatform-4397 CSR06612785 - S3 - Please log a CSR for multiple item number search
  #Scenario: Search with multiple item numbers on Search Items page
    #Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    #When I navigate to "Search" -> "Items"
    #And I click on "Clear" Button
    #And I enter "JD002" and "JD004" on Multiple "itemNumbers" textfield
    #And I click on "Apply" Button
    #And I wait "20" seconds 
    #And I wait till the page loads for "10" seconds
    #Then I verify search filter results are displayed
    #And I click on download Button and verify the result for "itemSearch" for "verifyItemSearchResults"

  Scenario: Search with multiple item numbers on Search Item AVL page
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I enter "JD002" and "JD004" on Multiple "itemNumbers" textfield
    And I click on "Apply" Button
    And I wait "10" seconds 
    #And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed

  Scenario: Search with multiple item numbers on Item Assignment
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    And I enter "JD002" and "JD004" on Multiple "itemNumbers" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed

  Scenario: Search with commodity names on Item Assignment
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    And I enter "MEMORY" on "categoryName" textfield
    And I click on "Apply" Button
    And I wait "100" seconds
    #And I wait till the page loads for "100" seconds
    Then I verify search filter results are displayed

  Scenario: Search with an item and trigger item popup on Search Items
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I select "Item" on the ItemType list
    And I set "Top Level Item" dropdown with Value "Yes" for Items
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    When I click on the "itemNumber" link to trigger the popup
    Then I should be landed on "Items" page
    Then I verify itemDetails displayed on popup

  Scenario: Search with an item and trigger item popup on Search Item AVL
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    When I click and verify details of "JD002" link on the popup
    Then I should be landed on "Item AVL" page

  Scenario: Search with an item and trigger item popup on Item Assignment
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    When I click on the "itemNumber" link to trigger the popup
    #		And I click on "Ok" Button on the popup
    Then I verify the "Items" page

  Scenario: Navigate to Items and verify the fields set for Search Items are cleared
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I enter "test descp" on "itemDescription" textfield
    And I enter "MEMORY" and "ADAPTER" on Multiple "categoryNames" textfield
    And I click on "Clear" Button
    Then I verify the fields set for Search Items are cleared

  Scenario: Navigate to Item AVL and verify the fields set for Search Item AVL are cleared
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I enter "test classification" on "itemClassification" textfield
    And I enter "MEMORY" and "ADAPTER" on Multiple "categoryNames" textfield
    And I click on "Clear" Button
    Then I verify the fields set for Search Items are cleared

  Scenario: Navigate to Item Assignment and verify the fields set for Search Item Assignment are cleared
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item Assignment"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I enter "DELL" on "business" textfield
    And I enter "JD002" and "JD004" on Multiple "itemNumbers" textfield
    And I click on "Clear" Button
    Then I verify the fields set for Search Item Assignment are cleared

  Scenario: Navigate to Search BOMs and verify the fields set for Search BOMS are cleared
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOMs"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I enter "test BOM descp" on "description" textfield
    And I enter "JD002" and "JD004" on Multiple "itemNumbers" textfield
    And I click on "Clear" Button
    Then I verify the fields set for Search BOMs are cleared

  Scenario: Navigate to Search BOMs and verify the search results the trigger the item popup details
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOMs"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    When I click on the "itemNumber" link to trigger the popup
    #		And I click on "Ok" Button on the popup
    Then I should be landed on "BOM" page

  Scenario: Navigate to Search BOMs and select the result to land on BOM Management
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOMs"
    And I click on "Clear" Button
    And I set the itemNumber as "JD002"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    And I click on the Edit icon
    Then I should be landed on "BOM Details" page
    And I click on "Back" Button
    And I wait till the page loads for "30" seconds
    Then I should be landed on "BOM" page

  Scenario: Navigate to Search BOM Management and verify multiple ItemNumbers field
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOM Management"
    And I click on "Clear" Button
    And I enter "JD002" and "JD004" on Multiple "itemNumbers" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed

  Scenario Outline: Navigate to Search BOM Management and verify the search filter results status
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOM Management"
    And I click on "Clear" Button
    And I select "<status>" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    And I verify the search results status as "<status>"

    Examples: 
      | status   |
      | Approved |
      | Closed   |
      | Pending  |

  Scenario: Navigate to Search BOM Management and verify history icon on BOM details
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "BOM Management"
    And I click on "Clear" Button
    And I select "Pending" on the Status list
    And I click on "Apply" Button
    And I click on the Edit icon
    And I wait "15" seconds
    And I should see "history" icon is "displayed" in BOM Management Page
    And I click on the "history" icon in BOM Management Page
    And I wait till the page loads for "30" seconds
    Then I should be landed on Audit History page
    # verify some thing on the page
    And I click on "Close" Button
    Then I should be landed on "BOM Details" page
    When I click on "Save" Button
    Then I should be landed on "BOM Details" page
    And I verify the "BOM changes has been saved successfully" successful message

  Scenario: Navigate to Search BOM Management and verify refresh btn on BOM details page
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "BOM Management"
    And I click on "Clear" Button
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I click on the Edit icon
    And I should see "refresh" icon is "displayed" in BOM Management Page
    And I click on the "refresh" icon in BOM Management Page
    And I wait till the page loads for "30" seconds
    Then I should be landed on "BOM Details" page
    And I should see "refresh" icon is "displayed" in BOM Management Page

  # just verifying an element on the page to make sure elements are visible are refresh
  Scenario: Navigate to Search BOM Management and verify Back btn and download btn visibility on BOM details page
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "BOM Management"
    And I click on "Clear" Button
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I click on the Edit icon
    Then I should be landed on "BOM Details" page
    And I should see "file_download" icon is "displayed" in BOM Management Page
    When I click on "Back" Button
    And I wait till the page loads for "30" seconds
    Then I should be landed on "Manage BOM" page

  Scenario: Navigate to Commodity Assignment and verify search results
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I verify search filter results are displayed
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list

  #clicking to make sure results are displayed correctly with checkbox fields
  Scenario: Navigate to Commodity Management and assign responsibilty
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I enter "ALL" on "categoryName" textfield
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Assign Responsibility" Button
    And I click on "Yes,Assign" confirmation Button
    #Then I should be landed on "Item Categories" page
    Then I verify the responsibility "OWNER" and "phe2netuser" assigned for commodity "ALL"

  Scenario: Navigate to Commodity Management and Unassign responsibilty
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I enter "ALL" on "categoryName" textfield
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Unassign Responsibility" Button
    And I click on "Yes,Unassign" confirmation Button
    #Then I should be landed on "Item Categories" page
    And I verify the responsibility "" and "" assigned for commodity "ALL"

  Scenario: Navigate to Commodity Management and verify the fields set for Search Items are cleared
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I enter "ALL" on "categoryName" textfield
    And I click on "Clear" Button
    Then I verify the fields set for Commodity Management are cleared

  Scenario: Navigate to Commodity Management and set Managed By field
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I enter "ALL" on "categoryName" textfield
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Set Managed By" Button
    And I wait till the page loads for "2" seconds
    And I select the "managedFlag" with value "EM Managed"
    And I wait till the page loads for "2" seconds
    And I click on "Set" confirmation Button
    And I verify the "Managed by has been saved successfully" successful message
    Then I should be landed on "Item Categories" page
    And I verify the managed by set to "EM" for commodity "ALL"
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Set Managed By" Button
    And I wait till the page loads for "2" seconds
    And I select the "managedFlag" with value "Not Set"
    And I wait till the page loads for "2" seconds
    And I click on "Set" confirmation Button
    And I verify the "Managed by has been saved successfully" successful message
    Then I should be landed on "Item Categories" page
    And I verify the managed by set to "" for commodity "ALL"

  Scenario: Navigate to Commodity Management and cancel button on assign responsibilty
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I enter "ALL" on "categoryName" textfield
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Assign Responsibility" Button
    And I click on "Cancel" confirmation Button
    Then I verify the responsibility "" and "" assigned for commodity "ALL"

  Scenario: Navigate to Commodity Management and cancel button on Unassign responsibilty
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I enter "ALL" on "categoryName" textfield
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Unassign Responsibility" Button
    And I click on "Cancel" confirmation Button
    Then I verify the responsibility "" and "" assigned for commodity "ALL"

  Scenario: Navigate to Commodity Management and cancel button on set Managed By popup
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I enter "ALL" on "categoryName" textfield
    And I click on "Apply" Button
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Set Managed By" Button
    And I wait till the page loads for "2" seconds
    And I select the "managedFlag" with value "EM Managed"
    And I wait till the page loads for "2" seconds
    And I click on "Cancel" confirmation Button
    Then I should be landed on "Item Categories" page
    And I verify the managed by set to "" for commodity "ALL"

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    And I set the itemNumber as "testxyz"
    And I click on "Apply" Button
    Then I should be landed on "Items" page
    Then I verify "No records found to display" message
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Master Data Management" -> "Item Assignment"
    Then I should be landed on "Items" page
    And I wait till the page loads for "2" seconds
    Then I verify "testxyz" on "itemNumber" textfield
    And I click on "Clear" Button
    And I wait till the page loads for "2" seconds
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    And I set the itemNumber as "testxyz"
    And I click on "Apply" Button
    Then I should be landed on "Items" page
    Then I verify "No records found to display" message
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    Then I should be landed on "Items" page
    Then I verify "testxyz" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    #	Then I should be landed on "Items" page
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Item Assignment"
    Then I should be landed on "Items" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I enter "Admin" on "user" textfield
    And I click on "Apply" Button
    Then I should be landed on "Item Categories" page
    Then I verify "No records found to display" message
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    And I wait till the page loads for "10" seconds
    When I navigate to "Master Data Management" -> "Commodity Management"
    Then I verify "Admin" on "user" textfield
    And I click on "Clear" Button
    Then I verify "" on "user" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I enter "Admin" on "user" textfield
    And I click on "Apply" Button
    Then I should be landed on "Item Categories" page
    Then I verify "No records found to display" message
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    Then I should be landed on "Item Categories" page
    Then I verify "Admin" on "user" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I should be landed on "Item Categories" page
    Then I verify "" on "user" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "Commodity Management"
    Then I should be landed on "Item Categories" page
    Then I verify "" on "user" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "BOM Management"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "Manage BOM" page
    And I verify search filter results are displayed
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Master Data Management" -> "BOM Management"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "BOM Management"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "Manage BOM" page
    And I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "BOM Management"
    Then I should be landed on "Manage BOM" page
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I should be landed on "Manage BOM" page
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Master Data Management" -> "BOM Management"
    Then I should be landed on "Manage BOM" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Items"
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "Items" page
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Search" -> "Items"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I should be landed on "Items" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "Items" page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Items"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I should be landed on "Items" page
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Items"
    Then I should be landed on "Items" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item AVL"
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "Items AVL" page
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Search" -> "Item AVL"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I should be landed on "Items AVL" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "Items AVL" page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item AVL"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I should be landed on "Items AVL" page
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item AVL"
    Then I should be landed on "Items AVL" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOMs"
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "BOM" page
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Search" -> "BOMs"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I should be landed on "BOM" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOMs"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "BOM" page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOMs"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I should be landed on "BOM" page
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "BOMs"
    Then I should be landed on "BOM" page
    Then I verify "" on "itemNumber" textfield

  #scplatform-4662 CFG Name is displaying in Item field and results in Item AVL & Item
  Scenario: Navigate to Search Item AVL and verify CFG and other details
    Given I log into HarmonyMTCM as "mtcmUser" with "phe2netuser"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    When I set the itemNumber as "000HC"
    And I select "Item" on the ItemType list
    And I click on "Apply" Button
    And I verify labelName "Member of Group" not found on the loaded page
    Then I verify search filter results are displayed
    #And I verify CFG and other result details for "000HC"
    #And I verify the "could not resolve property" message is not displayed
    
   #scplatform-7715-Item Assignment Audit History
    
  Scenario Outline: Upload Item values and verify success message, then verify status on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "ADMIN"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Then I verify the "<action>" on the Search Items page

    Examples: 
      | dataFile | fileName                | action            | msg | msgType |
      | ItemUI   | ItemUploadForSearchItem | uploadItemForItem |     | success |
      
  Scenario Outline: Assign Responsibility for the items uploaded
    Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "item"
    And I click on "Apply" Button
    And I should see "history" icon is "displayed"
    When I click on the "history" icon
    Then I should be landed on "Audit History" page
    And I wait till the page loads for "10" seconds
    Then I verify "No records found to display, Refine your search in the filters" message AuditHistory
    And I click on Close button on Audit History page
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Assign Responsibility" Button
    And I click on "Yes,Assign" confirmation Button
    And I verify the "Responsibility has been assigned successfully" successful message
    And I should see "history" icon is "displayed"
    Then I click on the "history" icon
    Then I should be landed on "Audit History" page
    And I wait till the page loads for "10" seconds
    Then I verify "Date Performed" column on row "0" has "today's date" as value displayed under search results for Audit History
    Then I verify "Role ID" column has "ADMIN" as value displayed under search results for all rows AuditHistory
    Then I verify "User ID" column has "ADMIN" as value displayed under search results for all rows AuditHistory
    Then I verify "Action" column has "ASSIGN_PRODUCTION" as value displayed under search results for all rows AuditHistory
    Then I verify "Type" column has "Item" as value displayed under search results for all rows AuditHistory

  Scenario Outline: ReAssign Responsibility for the existing item
    Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "item"
    And I click on "Apply" Button
    And I should see "history" icon is "displayed"
    When I click on the "history" icon
    Then I should be landed on "Audit History" page
    And I wait till the page loads for "10" seconds
    And I "assign" responsibility "adminuser3" to the item "1" selected
    And I verify the "Responsibility has been reassigned successfully" successful message
    Then I click on the "history" icon
    Then I should be landed on "Audit History" page
    And I wait till the page loads for "10" seconds
    And I should see "history" icon is "displayed"
    Then I click on the "history" icon
    Then I should be landed on "Audit History" page
    And I wait till the page loads for "10" seconds
    Then I verify "Date Performed" column on row "0" has "today's date" as value displayed under search results for Audit History
    Then I verify "Role ID" column has "ADMIN" as value displayed under search results for all rows AuditHistory
    Then I verify "User ID" column has "ADMIN" as value displayed under search results for all rows AuditHistory
    Then I verify "Action" column has "REASSIGN_PRODUCTION" as value displayed under search results for all rows AuditHistory
    Then I verify "Type" column has "Item" as value displayed under search results for all rows AuditHistory

  Scenario Outline: UnAssign Responsibility for the items uploaded
    Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "item"
    And I click on "Apply" Button
    And I should see "history" icon is "displayed"
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Unassign Responsibility" Button
    And I click on "Yes, Unassign" confirmation Button
    And I verify the "Responsibility has been unassigned successfully" successful message
    And I should see "history" icon is "displayed"
    Then I click on the "history" icon
    Then I should be landed on "Audit History" page
    And I wait till the page loads for "10" seconds
    Then I verify "Date Performed" column on row "0" has "today's date" as value displayed under search results for Audit History
    Then I verify "Role ID" column has "ADMIN" as value displayed under search results for all rows AuditHistory
    Then I verify "User ID" column has "ADMIN" as value displayed under search results for all rows AuditHistory
    Then I verify "Action" column has "UNASSIGN_PRODUCTION" as value displayed under search results for all rows AuditHistory
    Then I verify "Type" column has "Item" as value displayed under search results for all rows AuditHistory
    
Scenario: Search with the Item Description as !=NULL and verify the search result
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3" 
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I enter "#KJVV4" on "itemNumber" textfield
    And I enter "!=NULL" on "itemDescription" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "40" seconds
    Then I verify search filter results are displayed 
    
Scenario: Search with the Item Description as =NULL and verify the search result
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3" 
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I enter "#857CM-0052" on "itemNumber" textfield
    And I enter "=NULL" on "itemDescription" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed 
    
Scenario: Search with the Item Description as !=EMPTY and verify the search result
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3" 
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
     And I enter "#KJVV4" on "itemNumber" textfield
    And I enter "!=EMPTY" on "itemDescription" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "40" seconds
    Then I verify search filter results are displayed

Scenario: Search with the Item Description as !={string} and verify the search result
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3" 
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I enter "!=SI,ASSY,HD,512G,S3,7MM,HYNIX" on "itemDescription" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "40" seconds
    Then I verify search filter results are displayed 
Scenario: Search with the Supplier Name as =NULL and verify the search result
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3" 
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I enter "#857CM-0052" on "itemNumber" textfield
    And I enter "=NULL" on "supplierName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "40" seconds
    Then I verify search filter results are displayed 
Scenario: Search with the Supplier Name as =EMPTY and verify the search result
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3" 
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I enter "#857CM-0052" on "itemNumber" textfield
    And I enter "=EMPTY" on "supplierName" textfield
    And I click on "Apply" Button
    Then I verify search filter results are displayed 
Scenario:  Search with the Supplier Name as !=null to check case sensitive and verify results
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser3" 
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I enter "=null" on "supplierName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "40" seconds
    Then I verify "No records found to display, Refine your search in the filters" message
    