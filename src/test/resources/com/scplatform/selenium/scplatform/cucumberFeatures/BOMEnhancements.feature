@HarmonyBOMEnhancements
Feature: BOM Enhancements Workflow Test Plan
# scplatform-8867: To show effective start date and effective end date in BOM list
# scplatform-9111: New UI For BOM cost rollup Details
# scplatform-9370: BOM full replacement via BOM xls upload
# scplatform-9482: Ability to download multiple BOMs
# scplatform-9658: BOM auto approval for non-Enterprise BOM that matches Enterprise BOM
# scplatform-9654: Enable support for mass BOM replacement through the user interface

  # scplatform-8867: Verify effective start date and end date columns appear in BOM list
  Scenario: Verify effective start date and effective end date columns are displayed in BOM list
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "BOM Management"
    And I entered the BOM Item Number in the UI page
    And I click on Apply Button
    And I wait till the page loads for "20" seconds
    Then I verify "Effective Start Date" column displayed under search results
    And I verify "Effective End Date" column displayed under search results

  # scplatform-9111: Verify new BOM Cost Rollup UI page is accessible
  Scenario: Navigate to BOM Cost Rollup Details page and verify UI loads
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "BOM Cost Rollup"
    And I wait till the page loads for "20" seconds
    Then I should be landed on page with subHeader "BOM Cost Rollup Details"

  # scplatform-9370: Verify BOM full replacement via XLS upload replaces existing BOM records
  Scenario Outline: Upload BOM XLS file and verify full BOM replacement
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Administration" -> "BOM Management"
    And I entered the BOM Item Number in the UI page
    And I click on Apply Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile | fileName          | action             | msg | msgType |
      | BOMUI    | BOMFullReplacement | bomFullReplacement |     | success |

  # scplatform-9482: Verify multiple BOMs can be selected and downloaded
  Scenario: Select all BOM rows and download multiple BOMs
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "BOM Management"
    And I entered the BOM Item Number in the UI page
    And I click on Apply Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I select all rows
    And I click on "Download" Button
    Then I verify search filter results are displayed

  # scplatform-9658: Verify non-enterprise BOM matching enterprise BOM is auto-approved
  Scenario Outline: Upload non-enterprise BOM matching enterprise and verify auto-approval
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Administration" -> "BOM Management"
    And I entered the BOM Item Number in the UI page
    And I click on Apply Button
    And I wait till the page loads for "30" seconds
    Then I verify "APPROVED" column has "APPROVED" as value displayed under search results for all rows
    Examples:
      | dataFile | fileName               | action            | msg | msgType |
      | BOMUI    | BOMAutoApproveUpload   | bomAutoApprove    |     | success |

  # scplatform-9654: Verify mass BOM replacement through UI by selecting rows and uploading
  Scenario: Select BOMs from UI and perform mass replacement via upload
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "BOM Management"
    And I entered the BOM Item Number in the UI page
    And I click on Apply Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I select all rows
    And I click on "Replace BOM" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
