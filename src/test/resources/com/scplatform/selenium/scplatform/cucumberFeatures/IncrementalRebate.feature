@HarmonyIncrementalRebate
Feature: Incremental Rebate Data Workflow Test Plan
# scplatform-9207: Incorporating Incremental Data Sent in to MTCM - Rebates

  # Verify uploading an incremental rebate file adds new rebate records
  Scenario Outline: Upload incremental rebate file and verify new rebate records appear on Search Rebate Program
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile  | fileName                | action                   | msg | msgType |
      | RebatesUI | IncrementalRebateUpload | uploadIncrementalRebate  |     | success |

  # Verify uploading incremental rebate data with a Pending status creates records in Pending state
  Scenario Outline: Upload incremental rebate with Pending status and verify Pending records on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I select "Pending" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify the search results status as "Pending"
    Examples:
      | dataFile  | fileName                       | action                          | msg | msgType |
      | RebatesUI | IncrementalRebatePendingUpload | uploadIncrementalRebatePending  |     | success |

  # Verify that a second incremental upload does not duplicate existing rebate records
  Scenario Outline: Upload same incremental rebate file twice and verify no duplicate records
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile  | fileName                | action                  | msg | msgType |
      | RebatesUI | IncrementalRebateUpload | uploadIncrementalRebate |     | success |
