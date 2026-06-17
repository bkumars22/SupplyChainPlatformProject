@HarmonySourcingLaneEnhancements
Feature: Sourcing Lane Enhancements Workflow Test Plan
# scplatform-8829: Create sourcing lane via XLS between non-enterprise and non-enterprise
# scplatform-8457: Sourcing lane b2b upload â€” note: performance specific aspect not automatable via UI

  # scplatform-8829: Verify sourcing lane can be created via XLS between two non-enterprise suppliers
  Scenario Outline: Upload sourcing lane XLS between non-enterprise suppliers and verify creation
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile         | fileName                          | action                         | msg | msgType |
      | SourcingLaneB2BUI | NonEnterpriseToNonEnterpriseSL   | createNonEnterpriseSourcingLane |    | success |

  # Verify sourcing lane created between non-enterprise suppliers shows correct record source
  Scenario: Search non-enterprise sourcing lane and verify Record Source column value
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Record Source" column displayed under search results

  # Verify sourcing lane status is active after XLS upload for non-enterprise pair
  Scenario: Verify created non-enterprise sourcing lane has active status
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Search Sourcing Lane"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "ACTIVE" rows has status value as "ACTIVE"
