@HarmonySupplyAllocationEnhancements
Feature: Supply Allocation Enhancements Workflow Test Plan
# scplatform-8475: Support excel download of past dated Supply Allocation & Item Allocation
# scplatform-8597: Differentiate past/current allocation with filter
# scplatform-8725: Past allocation template display reverse order
# scplatform-9176: Auto End Previous Supply Allocation
# scplatform-9734: Supplier should be able to upload/create (UI) Supply Allocation

  # scplatform-8475: Verify excel download works for past dated Supplier Allocation
  Scenario: Download past dated Supplier Allocation and verify the exported excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM*"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "pastDatedAllocationDownload"

  # scplatform-8475: Verify excel download works for past dated Item Allocation
  Scenario: Download past dated Item Allocation and verify the exported excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM*"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I click on "Item Allocation Download" Button and verify the result for "Download Allocation" for "pastDatedItemAllocationDownload"

  # scplatform-8597: Verify filter differentiates past and current allocations
  Scenario: Verify past allocation filter shows only past allocations
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I select "Past" on "Allocation Period" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed

  # scplatform-8725: Verify past allocation template columns are in correct display order
  Scenario: Download past allocation template and verify column order
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM*"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "pastAllocationOrderValidation"

  # scplatform-9176: Verify uploading new supply allocation auto-ends previous one
  Scenario Outline: Upload new supply allocation and verify previous allocation auto-ends
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile           | fileName                  | action              | msg | msgType |
      | SupplyAllocationUI | NewSupplyAllocationUpload | newAllocationUpload |     | success |

  # scplatform-9734: Verify supplier can create Supply Allocation through the UI
  Scenario: Supplier sets Supply Allocation value through UI and saves
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I set Supply Allocation Value "100"
    When I click on save button
    Then I verify Supply Allocation Value "100" on all fields
