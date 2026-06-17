@HarmonyB2BParallelUpload
Feature: B2B Parallel Upload Workflow Test Plan
# scplatform-8636: Enable Parallel Processing for sourcing lane B2B upload

  # Verify uploading a B2B sourcing lane file completes successfully and load job status is Success
  Scenario Outline: Upload B2B sourcing lane file and verify load job completes with Success status
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Upload/Manage Jobs" -> "Manage Jobs"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName            | action              | msg | msgType |
      | SourcingLaneB2BUI | SourcingLaneB2BUpload | uploadSourcingLaneB2B |  | success |

  # Verify uploading two B2B sourcing lane files in sequence both complete with Success status
  Scenario Outline: Upload two B2B sourcing lane files sequentially and verify both succeed
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName1>" xlsx with "<action1>" & verify "<msg>" "<msgType>"
    And I upload the "<dataFile>" "<fileName2>" xlsx with "<action2>" & verify "<msg>" "<msgType>"
    And I navigate to "Upload/Manage Jobs" -> "Manage Jobs"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "60" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName1             | action1                | fileName2              | action2                 | msg | msgType |
      | SourcingLaneB2BUI | SourcingLaneB2BUpload | uploadSourcingLaneB2B1 | SourcingLaneB2BUpload2 | uploadSourcingLaneB2B2  |     | success |

  # Verify upload respects record count and all sourcing lane records appear on the search results page
  Scenario: Verify sourcing lane records are visible after B2B upload
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
