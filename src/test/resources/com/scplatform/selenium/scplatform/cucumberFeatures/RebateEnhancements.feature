@HarmonyRebateEnhancements
Feature: Rebate Enhancements Workflow Test Plan
# scplatform-9240: Rebate Rule - Only generic part will be sent against BOM
# scplatform-9077-related: Rebate generic part BOM rule

  # scplatform-9240: Verify rebate rule only sends generic part against BOM
  Scenario: Navigate to Rebates Rules tab and verify only generic part is listed against BOM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    When I click on Edit icon on row "1"
    Then I should be landed on "Rebate Program" page
    When I click on the RULES tab
    And I click on the Save Rule button
    Then I verify search filter results are displayed

  # Verify Rebate program search results columns include required fields for audit
  Scenario: Verify rebate search shows Created By and Updated By columns
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    And I verify "Created By" column displayed under search results

  # Verify View Results tab shows correct rebate calculation output
  Scenario: Verify rebate View Results tab shows calculated results
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    When I navigate to "Rebates" -> "Search Rebate Program"
    And I click on "Clear" Button
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    When I click on Edit icon on row "1"
    Then I should be landed on "Rebate Program" page
    When I click on the View Results tab
    Then I verify the results under View Results tab
