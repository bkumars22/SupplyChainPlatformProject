@HarmonyTAMEnhancements
Feature: TAM Enhancements Workflow Test Plan
# scplatform-9077: CFG Integration to scplatform One Cost - Decision to feed generic part as item belonging to CFG
# scplatform-9247: Prevent TAM from allocation to inactive sites
# scplatform-9405: Prevent TAM from allocation to inactive sites - XLOB Item
# scplatform-9235: Restrict users from allocating TAM to generic part
# scplatform-9248: Manage user behavior against retired sites
# scplatform-9318: Manage user behavior against retired sites - UI and Download
# scplatform-9716: Default supplier TAM to 100% for single source CFG
# scplatform-9292: To Store Country Code

  # scplatform-9077: Verify generic part is treated as an item belonging to CFG for One Cost integration
  Scenario Outline: Upload CFG config for generic part and verify item is listed under CFG
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile          | fileName              | action                | msg | msgType |
      | FunctionalGroupUI | CFGGenericPartUpload  | uploadCFGGenericPart  |     | success |

  # scplatform-9247: Verify TAM upload to inactive site shows validation error
  Scenario Outline: Upload TAM to inactive site and verify validation error message
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Examples:
      | dataFile     | fileName                | action              | msg                           | msgType |
      | TAMUploadUI  | TAMInactiveSiteUpload   | tamInactiveSiteTest | Site is inactive              | error   |

  # scplatform-9405: Verify XLOB TAM upload to inactive site shows validation error
  Scenario Outline: Upload XLOB TAM to inactive site and verify validation error
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Examples:
      | dataFile        | fileName                     | action                    | msg              | msgType |
      | XLOBTAMUploadUI | XLOBTAMInactiveSiteUpload    | xlobTamInactiveSiteTest   | Site is inactive | error   |

  # scplatform-9235: Verify TAM cannot be allocated to generic part â€” restriction enforced
  Scenario Outline: Attempt to allocate TAM to generic part and verify restriction error
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Examples:
      | dataFile     | fileName                   | action                    | msg                                 | msgType |
      | TAMUploadUI  | TAMGenericPartRestriction  | tamGenericPartRestriction | Cannot allocate TAM to generic part | error   |

  # scplatform-9248: Verify user behavior is managed correctly against retired sites in TAM screen
  Scenario: Verify retired site warning is shown on TAM Allocation Management screen
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed

  # scplatform-9318: Verify retired sites validation on UI and download shows correct label
  Scenario: Verify Retired site column label is displayed in Allocation download
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM*"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "retiredSitesAllocationValidation"

  # scplatform-9716: Verify default supplier TAM is set to 100% for single source CFG
  Scenario: Verify supplier TAM defaults to 100% when CFG has single supplier
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify Supply Allocation Value "100" on all fields

  # scplatform-9292: Verify Country Code is stored and visible on UI
  Scenario Outline: Upload country code data and verify it is stored and visible
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify "Country Code" column displayed under search results
    Examples:
      | dataFile          | fileName            | action               | msg | msgType |
      | FunctionalGroupUI | CountryCodeUpload   | uploadCountryCode    |     | success |
