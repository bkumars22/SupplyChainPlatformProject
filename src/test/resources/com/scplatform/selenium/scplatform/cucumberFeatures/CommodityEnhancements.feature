@HarmonyCommodityEnhancements
Feature: Commodity and Item Enhancements Workflow Test Plan
# scplatform-9740: Item Category Identifier (Commodity Identifier) in Commodity Management/Profile/download
# scplatform-9408: Do not reveal MSI golden data to TK suppliers - UI verification of restricted view

  # scplatform-9740: Verify Commodity Identifier column is visible in Commodity Management
  Scenario: Navigate to Commodity Management and verify Commodity Identifier column
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Commodity Identifier" column displayed under search results

  # scplatform-9740: Verify Commodity Identifier is included in the download output
  Scenario: Download Commodity Profile and verify Commodity Identifier appears in excel
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    When I click on download Button and verify the result for "Commodity Management" for "commodityIdentifierDownload"

  # scplatform-9408: Verify MSI golden data is not exposed to TK suppliers on UI
  Scenario: Log in as TK supplier and verify MSI golden cost data is not visible
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed

  # scplatform-9740: Verify Commodity Identifier set on upload is reflected in User Commodity Profile
  Scenario Outline: Upload Commodity Profile with identifier and verify in UI
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Master Data Management" -> "Commodity Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Commodity Identifier" column displayed under search results
    Examples:
      | dataFile         | fileName                    | action                     | msg | msgType |
      | CommodityUI      | CommodityIdentifierUpload   | uploadCommodityIdentifier  |     | success |
