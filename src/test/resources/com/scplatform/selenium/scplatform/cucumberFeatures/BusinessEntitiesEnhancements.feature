@HarmonyBusinessEntitiesEnhancements
Feature: Business Entities Enhancements Workflow Test Plan
# scplatform-8803: MTCM - AdditionalAttributes not found in DB although no error from BusinessEntity e2na transaction
# scplatform-8973: CLONE of - Download Capability for Business Entity
# scplatform-8779: Download Capability for Business Entity

  # scplatform-8803: Verify BusinessEntity AdditionalAttributes are stored and visible via XML upload
  Scenario: Upload BusinessEntity XML and verify AdditionalAttributes are visible on search results
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Administration" -> "Admin"
    And I uploaded the XML file for the business entity and checked it on the UI page
    When I navigate to "Search" -> "Business Entities"
    And I entered the Business ID "BE_TEST_001" in the text field
    And I click on Apply Button
    Then I verified the Business Entities "AdditionalAttributes" values in the UI page

  # scplatform-8803: Verify Business Entity Item AVL upload reflects correctly on UI
  Scenario: Upload Business Entity Item AVL XLSX and verify data on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Administration" -> "Admin"
    And I uploaded the XLSX file for the business entity Item AVL and checked it on the UI page
    When I navigate to "Search" -> "Business Entities"
    And I entered the Business ID "BE_TEST_001" in the text field
    And I click on Apply Button
    Then I verified non enterprices items are displayed in the UI page

  # scplatform-8973/8779: Verify Download Capability for Business Entity
  Scenario: Download Business Entity and validate the exported excel content
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Search" -> "Business Entities"
    And I click on "Clear" Button
    And I entered the Business ID "BE_TEST_001" in the text field
    And I click on Apply Button
    And I wait till the page loads for "20" seconds
    Then I verified non enterprices items are displayed in the UI page
    When I downloaded the Business entities Currency records and validated in the excel file

  # Verify Business Entity Items can be uploaded via XLSX and reflected in UI
  Scenario: Upload Business Entity items via XLSX and verify data appears on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Administration" -> "Admin"
    And I uploaded the XLSX file for the business entity Item and checked it on the UI page
    When I navigate to "Search" -> "Business Entities"
    And I entered the Business ID "BE_TEST_001" in the text field
    And I click on Apply Button
    Then I verified non enterprices items are displayed in the UI page
