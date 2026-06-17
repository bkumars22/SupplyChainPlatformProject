@HarmonyCFGDeactivate
Feature: CFG Deactivate Workflow Test Plan
# scplatform-8527: Deactivate CFG when all TAM is removed via Excel upload

  # Verify that uploading a CFG Excel file with all TAM entries removed deactivates the CFG
  Scenario Outline: Upload Excel to remove all TAM from CFG and verify CFG is deactivated
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName              | action              | msg | msgType |
      | FunctionalGroupUI | CFGDeactivateAllTAM   | deactivateCFGUpload |     | success |

  # Verify that a CFG without any TAM shows as Inactive status on the Manage Functional Group page
  Scenario: Verify CFG status is Inactive after all TAM removed via upload
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed

  # Verify that uploading a CFG Excel with TAM entries re-activates a previously deactivated CFG
  Scenario Outline: Re-upload CFG with TAM entries and verify CFG is re-activated
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName           | action           | msg | msgType |
      | FunctionalGroupUI | CFGReactivateWithTAM | reactivateCFG  |     | success |
