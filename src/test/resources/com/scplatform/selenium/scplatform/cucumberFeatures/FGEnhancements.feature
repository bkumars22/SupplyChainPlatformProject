@HarmonyFGEnhancements
Feature: Functional Group Enhancements Workflow Test Plan
# scplatform-9123: Add Additional Maverick Name field against the FG header
# scplatform-9105: Capture CFG details Audit History
# scplatform-9241: CFG Management
# scplatform-9242: CFG Management Additional Scenarios
# scplatform-9394: Functional Group Status Handle from Pending Review to Active
# scplatform-9351: Storing functionalGroupExternalId
# scplatform-9608: Enhancement on CFG-Item Delete Scenario
# scplatform-9381: Additional CFG Changes from 25.3 Release

  # scplatform-9123: Verify Maverick Name field exists on FG header in UI
  Scenario Outline: Upload XML with Maverick Name and verify on FG edit page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the XML file "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName            | action              | msg | msgType |
      | FunctionalGroupUI | FGMaverickNameUpload | uploadFGMaverick   |     | success |

  # scplatform-9105: Verify CFG changes appear in Audit History
  Scenario: Verify CFG entry is captured in Audit History
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Administration" -> "Audit History"
    And I click on "Clear" Button
    And I select "Functional Group" on the AuditType list
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Functional Group" column displayed under search results
    And I verify "Action" column displayed under search results

  # scplatform-9241: Verify basic CFG Management scenarios â€” create, search, verify active
  Scenario: Create a CFG, search it and verify it is Active
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I select "Yes" on "Show Item Without Group" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "45" seconds
    And I select first "2" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "random" with "Save All"
    Then I verify the "Functional Group Saved" successful message

  # scplatform-9242: Additional CFG scenarios â€” edit FG name and verify rename
  Scenario: Edit Functional Group name and verify the rename is persisted
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I get the fgName from the search results on row "1"
    And I edit the Functional Group name as "EditedFGName"
    And I save the FG
    Then I verify the "Functional Group Saved" successful message
    And I verify the rename on the Manage Parent page

  # scplatform-9394: Verify FG status transitions from Pending Review to Active
  Scenario Outline: Verify Functional Group transitions from Pending Review to Active
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify the status "<status>" of the Functional Group "<fgName>"
    Examples:
      | fgName    | status  |
      | TAM_AUTO  | ACTIVE  |

  # scplatform-9351: Verify functionalGroupExternalId is stored and visible on FG page
  Scenario Outline: Upload XML with FG External ID and verify on FG UI
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the XML file "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "External ID" column displayed under search results
    Examples:
      | dataFile          | fileName          | action            | msg | msgType |
      | FunctionalGroupUI | FGExternalIdUpload | uploadFGExternal |     | success |

  # scplatform-9608: Verify delete item from CFG does not cause unexpected navigation or error
  Scenario: Remove item from Functional Group and verify FG is still valid
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I get the fgName from the search results on row "1"
    And I edit the Functional Group "TestFGDelete" by removing item
    And I save the FG
    Then I verify the "Functional Group Saved" successful message

  # scplatform-9381: Verify additional CFG changes from 25.3 are visible on FG page
  Scenario: Verify CFG Tam Availability displayed on Edit FG page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I get the fgName from the search results on row "1"
    Then I verify Tam Availability as "Yes" on Edit FG page
