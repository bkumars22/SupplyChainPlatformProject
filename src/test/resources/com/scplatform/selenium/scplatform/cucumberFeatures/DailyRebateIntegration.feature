@HarmonyDailyRebate
Feature: Daily Rebate Integration Workflow Test Plan
# scplatform-9442: Daily Integration of Rebates

  # Verify uploading a daily rebate extract file processes successfully and records appear on UI
  Scenario Outline: Upload daily rebate extract file and verify records appear on Search Rebate Program
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile  | fileName              | action                | msg | msgType |
      | RebatesUI | DailyRebateExtract    | uploadDailyRebate     |     | success |

  # Verify uploading a daily rebate extract with Approved status creates Approved rebate records
  Scenario Outline: Upload daily rebate extract with Approved records and verify Approved status on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify the search results status as "Approved"
    Examples:
      | dataFile  | fileName                     | action                       | msg | msgType |
      | RebatesUI | DailyRebateApprovedExtract   | uploadDailyRebateApproved    |     | success |

  # Verify uploading a repeated daily extract does not overwrite previously Approved records
  Scenario Outline: Upload daily rebate extract twice and verify Approved records remain unchanged
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify the search results status as "Approved"
    Examples:
      | dataFile  | fileName                   | action                    | msg | msgType |
      | RebatesUI | DailyRebateApprovedExtract | uploadDailyRebateApproved |     | success |
