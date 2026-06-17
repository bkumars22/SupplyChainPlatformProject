@HarmonyCostRecordEnhancements
Feature: Cost Record Enhancements Workflow Test Plan
# scplatform-8384: Cost Forecast Variance Should Only Show Regions with Variance (24.2)
# scplatform-8423: Cost Forecast Variance Should Only Show Regions with Variance (24.3)
# scplatform-8485: Error When Try to Expire Multiple Cost Records
# scplatform-8441: Add field Last Loaded by User to Cost Record and Simple Forecast audit history records
# scplatform-8846: To Capture Updated by and Created By Details
# scplatform-8455: Visibility to price changes to capture user who load and who approves
# scplatform-9371: Cost record auto approve
# scplatform-9686: Cost currency conversion
# scplatform-9731: VA cost should be added to the Total price on the Cost record

  # scplatform-8384/8423: Verify Cost Forecast Variance report shows only regions with variance
  Scenario: Verify Cost Forecast Variance report shows only variance regions
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Cost Forecast" -> "Variance Report"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Variance" column displayed under search results

  # scplatform-8485: Verify expiring multiple cost records works without error
  Scenario: Select multiple cost records and expire them without error
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    When I select all rows
    And I click on "Expire" Button
    Then I verify "EXPIRED" rows has status value as "EXPIRED"

  # scplatform-8441: Verify Last Loaded By User column appears in Cost Record audit history
  Scenario: Verify Last Loaded By User column in Cost Record audit history
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Administration" -> "Audit History"
    And I click on "Clear" Button
    And I select "Cost Record" on the AuditType list
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Last Loaded By" column displayed under search results

  # scplatform-8846: Verify Updated By and Created By are captured on Cost Records
  Scenario: Verify Updated By and Created By columns are displayed on Cost Records
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Updated By" column displayed under search results
    And I verify "Created By" column displayed under search results

  # scplatform-8455: Verify Loaded By and Approved By columns visible on Cost Records
  Scenario: Verify Loaded By and Approved By columns visible on Cost Records
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Loaded By" column displayed under search results
    And I verify "Approved By" column displayed under search results

  # scplatform-9371: Verify cost record with auto-approve config is created as APPROVED
  Scenario Outline: Upload cost record and verify auto approval status
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "APPROVED" rows has status value as "APPROVED"
    Examples:
      | dataFile     | fileName       | action        | msg | msgType |
      | CostRecordUI | AutoApproveCR  | autoApproveCR |     | success |

  # scplatform-9686: Verify converted currency column is displayed on Cost Records
  Scenario: Verify Converted Cost column is displayed on Cost Records
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Converted Cost" column displayed under search results

  # scplatform-9731: Verify VA Cost and Total Price columns are both visible on Cost Records
  Scenario: Verify VA Cost and Total Price columns are displayed on Cost Records
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Total Price" column displayed under search results
    And I verify "VA Cost" column displayed under search results
