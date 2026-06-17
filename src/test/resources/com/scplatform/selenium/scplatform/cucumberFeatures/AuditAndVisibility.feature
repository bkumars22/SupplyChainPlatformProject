@HarmonyAuditAndVisibility
Feature: Audit and Visibility Enhancements Workflow Test Plan
# scplatform-8441: Last Loaded by User in Simple Forecast audit history
# scplatform-9061: Supplier visibility into enterprise data
# scplatform-9177: Remove Dependency Of Item Owner When adding Part
# scplatform-9680: Dell OneCost - Relabeling "Maverick" to "ODW"

  # scplatform-8441: Verify Last Loaded By User appears in Simple Forecast audit history
  Scenario: Verify Last Loaded By column in Simple Forecast audit history records
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Administration" -> "Audit History"
    And I click on "Clear" Button
    And I select "Simple Forecast" on the AuditType list
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Last Loaded By" column displayed under search results

  # scplatform-9061: Verify supplier user can see enterprise data when logged in
  Scenario: Log in as supplier user and verify enterprise item data is visible
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Search" -> "Items"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Item Number" column displayed under search results

  # scplatform-9177: Verify item can be added to Part without requiring Item Owner field
  Scenario: Add a part without item owner and verify success
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I select "Yes" on "Show Item Without Group" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "45" seconds
    Then I verify search filter results are displayed
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "random" with "Save All"
    Then I verify the "Functional Group Saved" successful message

  # scplatform-9680: Verify relabeling of "Maverick" to "ODW" on B2B sourcing lane upload
  Scenario Outline: Upload B2B file and verify ODW label appears instead of Maverick
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "ODW" column has "ODW" as value displayed under search results for all rows
    Examples:
      | dataFile         | fileName               | action              | msg | msgType |
      | SourcingLaneB2BUI | SourcingLaneODWUpload | uploadSLODWLabel    |     | success |
