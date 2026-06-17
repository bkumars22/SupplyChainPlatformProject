@HarmonySearchEnhancements
Feature: Search and Multi-value Enhancements Workflow Test Plan
# scplatform-8627: Ability to see all values in multiple value search fields
# scplatform-9481 is excluded (needs new step defs for cross-page multi-select in popup)
# scplatform-9656: Application to Display sources of the Item/item AVL details
# scplatform-9650: To Provide Excel export capability for message types in the native MTCM application
# scplatform-9112: Cost element associated to an Item

  # scplatform-8627: Verify all values are visible in a multiple-value search field
  Scenario: Verify multiple item numbers can be entered and are all visible in search field
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I enter "ITEM_001" and "ITEM_002" on Multiple "itemNumbers" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed

  # scplatform-8627: Verify multiple supplier names can be entered and shown in search field
  Scenario: Verify multiple suppliers can be entered and are visible in search field
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I enter "SUPPLIER_A" and "SUPPLIER_B" on Multiple "supplierNames" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed

  # scplatform-9656: Verify application displays source of Item/AVL details on Items page
  Scenario: Navigate to Items search and verify source columns are displayed
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Source" column displayed under search results

  # scplatform-9656: Verify Item AVL source column is visible in Item AVL search
  Scenario: Navigate to Item AVL search and verify source columns are displayed
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Source" column displayed under search results

  # scplatform-9650: Verify Excel export button is available for message types in MTCM
  Scenario: Navigate to Uploads/Messages page and verify Excel export button is available
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Manage Jobs"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    When I click on download Button and verify the result for "Manage Jobs" for "messageExcelExport"

  # scplatform-9112: Verify Cost Element column is associated and visible on Item page
  Scenario: Navigate to Items search and verify Cost Element column is displayed
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Cost Element" column displayed under search results
