@HarmonyB2BRecordSource
Feature: B2B Record Source Workflow Test Plan
# scplatform-8777: Include "record source" to the sourcing lane B2B integration

  # Verify that uploading a B2B sourcing lane file with record source field populates the field on the UI
  Scenario Outline: Upload B2B sourcing lane with record source and verify the field is displayed on sourcing lane detail
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName                   | action                    | msg | msgType |
      | SourcingLaneB2BUI | SourcingLaneB2BRecordSource | uploadSourcingLaneB2BSource |   | success |

  # Verify that a B2B upload without record source field still completes successfully (backward compatibility)
  Scenario Outline: Upload B2B sourcing lane without record source and verify upload succeeds
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Upload/Manage Jobs" -> "Manage Jobs"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName                      | action                       | msg | msgType |
      | SourcingLaneB2BUI | SourcingLaneB2BNoRecordSource | uploadSourcingLaneNoRecSource |     | success |

  # Verify that uploading a B2B file with record source "scplatform" renders the value on the search result
  Scenario: Verify record source value is shown on Manage Jobs results after upload
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Manage Jobs"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
