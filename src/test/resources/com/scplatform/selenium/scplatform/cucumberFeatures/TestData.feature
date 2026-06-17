@HarmonyTest

Feature: Test Workflow

Scenario: Upload Item with different values and verify success message, then verify on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "itemForTam" with "uploadTamMultipleSupplierItem" & verify "" "success"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I enter itemNumbers on Multiple ItemNumber textfield
    And I click on "Apply" Button
    And I wait till the page loads for "150" seconds
    And I select first "5" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "AutoMultipleItemGroup" with "Save All"
    Then I verify the "Functional Group Saved" successful message

  Scenario Outline: Update supp alloc for multiple items with valid combinations and verify success message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser5"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroup" on "groupName" Field
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
 