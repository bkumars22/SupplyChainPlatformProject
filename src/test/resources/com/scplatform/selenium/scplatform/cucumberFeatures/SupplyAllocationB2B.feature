@HarmonySupplyAllocationB2B
Feature: Supply and Item Allocation B2B Integration Workflow Test Plan
# scplatform-9577: New Supply and Item Allocation Integration from Hermes to scplatform via B2B

  # Verify uploading a Supply Allocation B2B file creates allocation records visible on the UI
  Scenario Outline: Upload Supply Allocation B2B file and verify allocation records appear on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Supply Collaboration" -> "Supply Allocation"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile            | fileName               | action                    | msg | msgType |
      | SupplyAllocationB2B | SupplyAllocationHermes | uploadSupplyAllocationB2B |     | success |

  # Verify uploading an Item Allocation B2B file creates item allocation records visible on the UI
  Scenario Outline: Upload Item Allocation B2B file and verify item allocation records appear on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Supply Collaboration" -> "Item Allocation"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName             | action                  | msg | msgType |
      | ItemAllocationB2B | ItemAllocationHermes | uploadItemAllocationB2B |     | success |

  # Verify uploading a Supply Allocation B2B file with invalid data returns an error status on Manage Jobs
  Scenario Outline: Upload Supply Allocation B2B file with invalid data and verify error status on Manage Jobs
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Upload/Manage Jobs" -> "Manage Jobs"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile            | fileName                      | action                          | msg | msgType |
      | SupplyAllocationB2B | SupplyAllocationHermesInvalid | uploadSupplyAllocationB2BInvalid |    | error   |

  # Verify that duplicate B2B supply allocation upload does not create duplicate records
  Scenario Outline: Upload same Supply Allocation B2B file twice and verify no duplicate records created
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Supply Collaboration" -> "Supply Allocation"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile            | fileName               | action                    | msg | msgType |
      | SupplyAllocationB2B | SupplyAllocationHermes | uploadSupplyAllocationB2B |     | success |
