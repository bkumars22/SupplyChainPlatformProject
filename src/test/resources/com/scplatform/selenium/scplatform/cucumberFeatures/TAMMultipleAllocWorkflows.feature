@TamMultipleAlloc
Feature: TAM Multiple Alloc workflow

  Scenario: Upload Item with different values and verify success message, then verify on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "itemForTam" xlsx with "uploadTamMultipleSupplierItem" & verify "" "success"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I enter itemNumbers on Multiple ItemNumber textfield
    And I click on "Apply" Button
    And I wait till the page loads for "150" seconds
    And I select first "5" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "AutoMultipleItemGroupp" with "Save All"
    Then I verify the "Functional Group Saved" successful message

  Scenario Outline: Update supp alloc for multiple items with valid combinations and verify success message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Supplier Allocation value "<val1>" on row "1" and col "1"
    And I set multiple Supplier Allocation value "<val2>" on row "3" and col "1"
    And I set multiple Supplier Allocation value "<val3>" on row "5" and col "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

    Examples: 
      | val1 | val2 | val3 |
      |   30 |   30 |   40 |
      |  100 |    0 |    0 |
      |    0 |  100 |    0 |
      |    0 |    0 |  100 |
      |   50 |   50 |    0 |
      |   40 |    0 |   60 |
      |    0 |   30 |   70 |

  Scenario Outline: Update supp alloc for multiple items with valid combinations for allow hedging and verify success message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set Supply Allocation Value on complete row "3" with "0"
    And I set Supply Allocation Value on complete row "5" with "0"
    And I set Supply Allocation Value "100"
    And I set multiple Supplier Allocation value "<val1>" on row "1" and col "1"
    And I set multiple Supplier Allocation value "<val2>" on row "3" and col "1"
    And I set multiple Supplier Allocation value "<val3>" on row "5" and col "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

    Examples: 
      | val1 | val2 | val3 |
      |  130 |   30 |   40 |
      |  150 |    0 |    0 |
      |    0 |   80 |    0 |
      |   50 |  150 |    0 |
      |   40 |    0 |  160 |
      |    0 |  130 |   70 |

  Scenario Outline: Update supp alloc for multiple items with invalid combinations and verify error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set multiple Supplier Allocation value "<val1>" on row "1" and col "1"
    And I set multiple Supplier Allocation value "<val2>" on row "3" and col "1"
    And I set multiple Supplier Allocation value "<val3>" on row "5" and col "1"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Total Allocation Should be 100 %"

    Examples: 
      | val1 | val2 | val3 |
      |   35 |   30 |   40 |
      |   99 |    0 |    0 |
      |    0 |  100 |    1 |
      |    0 |    0 |   99 |
      |   51 |   50 |    0 |
      |   41 |    0 |   60 |
      |    5 |   50 |    0 |
      |    4 |    0 |   60 |
      |    0 |    0 |    0 |

  Scenario Outline: Update supp alloc for multiple items with invalid combinations for allow hedging and verify success message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set multiple Supplier Allocation value "<val1>" on row "1" and col "1"
    And I set multiple Supplier Allocation value "<val2>" on row "3" and col "1"
    And I set multiple Supplier Allocation value "<val3>" on row "5" and col "1"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Allocation Should be in the Hedging Range"

    Examples: 
      | val1 | val2 | val3 |
      |  130 |   30 |   41 |
      |    0 |    0 |    0 |
      |    0 |   60 |    0 |
      |    0 |    0 |   60 |
      |   50 |    0 |    0 |
      |   41 |    0 |   10 |
      |   41 |   10 |    0 |
      |    0 |   10 |   45 |
      |   41 |    0 |  160 |
      |   41 |  230 |    0 |
      |    0 |  230 |   45 |
      |    0 |  260 |    0 |
      |    0 |    0 |  360 |
      |  250 |    0 |    0 |

  Scenario: Set supply allocation data and assume default positive test data for all item alloc fields then verify success msg
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set multiple Supplier Allocation value "100" on row "1" and col "1"
    And I set multiple Supplier Allocation value "0" on row "3" and col "1"
    And I set multiple Supplier Allocation value "0" on row "5" and col "1"
    And I select All Supplier allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

  #### scplatform-4645 Able to save item allocation less than 100 for the supplier B
  Scenario Outline: Update item alloc for multiple items with valid combinations and verify success message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set multiple Supplier Allocation value "100" on row "1" and col "1"
    And I set multiple Supplier Allocation value "0" on row "3" and col "1"
    And I set multiple Supplier Allocation value "0" on row "5" and col "1"
    And I select All Supplier allocation icon with value from the column "1"
    And I set multiple Item Allocation value "<val1>" on row "2"
    And I set multiple Item Allocation value "<val2>" on row "4"
    And I set multiple Item Allocation value "<val3>" on row "6"
    And I set multiple Item Allocation value "<val4>" on row "7"
    And I set multiple Item Allocation value "<val5>" on row "8"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

    Examples: 
      | val1 | val2 | val3 | val4 | val5 |
      |      |      |      |      |      |
      |    0 |    0 |    0 |    0 |    0 |
      |  100 |  100 |   30 |   40 |   30 |
      |  100 |  100 |    0 |  100 |    0 |
      |  100 |  100 |    0 |    0 |  100 |
      |  100 |  100 |    0 |   30 |   70 |
      |  100 |  100 |   30 |    0 |   70 |
      |  100 |  100 |   30 |   70 |    0 |

  Scenario Outline: Update item alloc for multiple items with invalid combinations and verify error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set multiple Supplier Allocation value "100" on row "1" and col "1"
    And I set multiple Supplier Allocation value "0" on row "3" and col "1"
    And I set multiple Supplier Allocation value "0" on row "5" and col "1"
    And I select All Supplier allocation icon with value from the column "1"
    And I set multiple Item Allocation value "<val1>" on row "2"
    And I set multiple Item Allocation value "<val2>" on row "4"
    And I set multiple Item Allocation value "<val3>" on row "6"
    And I set multiple Item Allocation value "<val4>" on row "7"
    And I set multiple Item Allocation value "<val5>" on row "8"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Item Allocation Total Should be 100% For One Supplier"

    Examples: 
      | val1 | val2 | val3 | val4 | val5 |
      |  100 |   10 |   30 |   40 |   30 |
      |   10 |  100 |    0 |  100 |    0 |
      |   10 |   10 |    0 |    0 |  100 |
      |   10 |  100 |   30 |   40 |   30 |
      |  100 |   10 |   10 |    0 |   70 |
      |   10 |  100 |   30 |   30 |   10 |
      |  100 |  100 |    3 |    4 |   30 |
      |  100 |  100 |   10 |    0 |   70 |
      |  100 |  100 |   30 |      |   10 |

  Scenario: For multiple supplier FG update for a specific column in global,another on region and verify site have both changes
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set Supply Allocation Value on complete row "3" with "50"
    And I set Supply Allocation Value on complete row "5" with "50"
    And I set Supplier Allocation value "80" on column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "CCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supplier Allocation value "80" on column "2"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "CCC"
    And I set "Site" dropdown with Value "CCC-CCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Supply Allocation Value "80" on column "1"
    Then I verify Item Allocation Value "100" on column "2"
    And I verify Supply Allocation Value "80" on column "2"
    And I verify Item Allocation Value "100" on column "4"

  Scenario: validate item alloc null value for multiple supp items when checkbox is unchecked on ww level
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I "uncheck" the "inheritItemValue" checkbox
    And I set multiple Item Allocation value "" on row "2"
    And I set multiple Item Allocation value "" on row "4"
    And I set multiple Item Allocation value "" on row "6"
    And I set multiple Item Allocation value "" on row "7"
    And I set multiple Item Allocation value "" on row "8"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "" on all rows and columns
    Then I verify Inherit checkbox with name "inheritItemValue" status as "unChecked" on all fields

  Scenario: validate item alloc default value- 100 for multiple supp items when checkbox is checked and null value set on ww level
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I "uncheck" the "inheritItemValue" checkbox
    And I set multiple Item Allocation value "" on row "2"
    And I set multiple Item Allocation value "" on row "4"
    And I set multiple Item Allocation value "" on row "6"
    And I set multiple Item Allocation value "" on row "7"
    And I set multiple Item Allocation value "" on row "8"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "" on row "2"
    And I verify Item Allocation Value "" on row "4"
    And I verify Item Allocation Value "" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"
    When I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    When I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "100" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"

  Scenario: Delete multiple supp item supplier allocations from site Level
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value on complete row "1" with "100"
    And I "check" the "inheritValue" checkbox
    And I select All Supplier allocation icon with value from the column "1"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify Inherit checkbox with name "inheritValue" status as "checked" on all fields
    When I set multiple Item Allocation value "100" on row "2"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    And I click on "Delete" Button
    And I delete "siteSupplier" on Delete Allocations from site level
    And I verify warning message "Are you sure you want to delete selected TAM. Once deleted it can't be undone." and click "Yes" button on popup displayed
    Then I verify multiple Supplier Allocation value "100" on row "1"

  Scenario: Delete item allocations for multiple supp items from region Level
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "150"
    And I set Supply Allocation Value on complete row "3" with "0"
    And I set Supply Allocation Value on complete row "5" with "0"
    And I set multiple Item Allocation value "0" on row "2"
    And I set multiple Item Allocation value "0" on row "4"
    And I set multiple Item Allocation value "40" on row "6"
    And I set multiple Item Allocation value "40" on row "7"
    And I set multiple Item Allocation value "20" on row "8"
    And I "check" the "inheritValue" checkbox
    And I select All Supplier allocation icon with value from the column "1"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    And I click on "Delete" Button
    And I select "item" on Delete Allocations Option
    And I "check" the "selectAllDeleteSite" checkbox
    Then I verify "regionDeleteList" checkbox status as "checked"
    And I click on Supply Allocation delete Button
    And I verify warning message "Are you sure you want to delete selected TAM. Once deleted it can't be undone." and click "Yes" button on the popup displayed
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "100" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"
    Then I verify multiple Supplier Allocation value "150" on row "1"
    Then I verify Inherit checkbox with name "inheritValue" status as "checked" on all fields
    Then I verify Inherit checkbox with name "inheritItemValue" status as "unchecked" on all fields

  Scenario: Delete both allocations from global Level also verify for search filter: hide supplier with no allocation 
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    And I clear testData by deleting tam alloc for "AutoMultipleItemGroupp" from "Global" level
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "Yes" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I should be landed on "Download Allocation" page 
	  Then I verify "No records found to display" message 
	  When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify search filter results are displayed
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows
   	And I verify "TAM Exist" column has "No" as value displayed under search results for all rows
		
 Scenario: Validate global level one supplier allocation without value (also null) for search filter: hide supplier with no allocation
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
   	And I clear testData by deleting tam alloc for "AutoMultipleItemGroupp" from "Global" level    
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set multiple Item Allocation value "100" on row "2"
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set Supply Allocation Value on complete row "3" with "0"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    ##########********TestData creation ends here************
		When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "Yes" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "1" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows
   	And I verify "TAM Exist" column has "Yes" as value displayed under search results for all rows
   	When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "10" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows
   	
   	Scenario: Validate search filter: 'hide supplier with no allocation' for Region TAM allocation with 1 supplier with allocation
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    And I clear testData by deleting tam alloc for "AutoMultipleItemGroupp" from "Global" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set Supply Allocation Value on complete row "3" with "0"
    And I click on "Save" Button
		When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "Yes" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "1" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows
   	And I verify "TAM Exist" column has "Yes" as value displayed under search results for all rows
   	When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "10" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows

Scenario: Validate search filter: 'hide supplier with no allocation' for Site TAM allocation with 1 supplier with allocation
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    And I clear testData by deleting tam alloc for "AutoMultipleItemGroupp" from "Global" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set Supply Allocation Value on complete row "3" with "0"
    And I click on "Save" Button
    ########********TestData creation ends here************
		When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button               
    And I set group name as "AutoMultipleItemGroupp"
    And I select "Yes" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "1" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows
   	When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "10" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows

	Scenario: Validate global level have only item allocation and region have supp alloc for search filter: hide supplier with no allocation
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"   	
    And I clear testData by deleting tam alloc for "AutoMultipleItemGroupp" from "Global" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set multiple Item Allocation value "100" on row "2"
    And I set Supply Allocation Value on complete row "5" with "100"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "3" with "100"
    And I set Supply Allocation Value on complete row "5" with "0"
    And I click on "Save" Button 
    #########********TestData creation ends here************
		When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "Yes" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "2" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows
   	When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "10" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows

	Scenario: Validate global level have only item allocation and Site have supp alloc for search filter: hide supplier with no allocation
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    And I clear testData by deleting tam alloc for "AutoMultipleItemGroupp" from "Global" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set multiple Item Allocation value "100" on row "2"
    And I set Supply Allocation Value on complete row "5" with "100"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "3" with "100"
    And I set Supply Allocation Value on complete row "5" with "0"
    And I click on "Save" Button 
    ########********TestData creation ends here************
		When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "Yes" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "2" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows
   	When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "10" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows

	Scenario: Validate global, Region and Site have supp alloc each supplier for search filter: hide supplier with no allocation
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    And I clear testData by deleting tam alloc for "AutoMultipleItemGroupp" from "Global" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set multiple Item Allocation value "100" on row "2"
    And I set Supply Allocation Value on complete row "1" with "100"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "3" with "50"
    And I click on "Save" Button 
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoMultipleItemGroupp" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "5" with "50"
    And I click on "Save" Button 
    ########********TestData creation ends here************
		When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "Yes" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "3" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows
   	When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupp"
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify "10" rows listed without selection option
   	And I verify "Group Name" column has "AutoMultipleItemGroupp" as value displayed under search results for all rows	

   Scenario: Verify column data count and search records count on UI , fg name and type are matching on downloaded Supplier alloc excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM_CFG"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "Hide Supplier with no allocation" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    Then I get the "Supplier" Allocation result count from UI
    And I get and verify allocation details on UI
    #And I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "suppDataValidation"
    And I log out of HarmonyMTCM

#PDSUPPORT-25239
  @skip
  Scenario: Verify column data count and search records count on UI , fg name and type are matching on downloaded Supplier alloc excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM_CFG"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    #And I select "Yes" on "Hide Supplier with no allocation" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "40" seconds
    Then I get the "Supplier" Allocation result count from UI
    And I get and verify allocation details on UI
    And I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "suppDataValidation"
    And I log out of HarmonyMTCM

#PDSUPPORT-25529 
  @skip
  Scenario: Verify column data count and search records count on UI, fg name and type are matching on downloaded Item alloc excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM_CFG"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "Hide Supplier with no allocation" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    Then I should be landed on "Download Allocation" page
		When I move To an Element with id "grid-result"
#		Then I verify scroll bar is visible
    Then I get the "Item" Allocation result count from UI
    And I get and verify allocation details on UI
   # And I click on "Item Allocation Download" Button and verify the result for "Download Allocation" for "itemDataValidation"
    And I log out of HarmonyMTCM
      
      #PDSUPPORT-25239
  @skip
  Scenario: Verify column data count and search records count on UI, fg name and type are matching on downloaded Item alloc excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM_CFG"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    #And I select "Yes" on "Hide Supplier with no allocation" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    Then I get the "Item" Allocation result count from UI
    And I get and verify allocation details on UI
    And I click on "Item Allocation Download" Button and verify the result for "Download Allocation" for "itemDataValidation"
    And I log out of HarmonyMTCM
      
   