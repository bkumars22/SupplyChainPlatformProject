#Author: kumar
@HarmonyBOM
Feature: BOM Workflow Test Plan

  Scenario: Validate System should process ITEM AVL Xlsx file and validate the data in the UI page
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "Admin"
    When I uploaded the XLSX file for the BOMs Item and checked it on the UI page
    When I navigate to "Search" -> "Items"
    And I entered the BOM Item Number in the UI page
    And I click on Apply Button
    And I validate the item row values with item type "Supplier Item" supplier name "10:10 COMPUTER SERVICES" and MCM value "MCM"
    When I navigate to "Administration" -> "Admin"
    When I uploaded the XLSX file for the BOM Item AVL and checked it on the UI page
    When I navigate to "Search" -> "Item AVL"
    And I entered the BOM Item Number in the UI page
    And I click on Apply Button
    And I validate the item row values with item type "Supplier Item" supplier name "SUPER MEGA" and MCM value "MCM"

  Scenario: Verify the uploaded BOMs, along with data validations and actions on the UI
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "Miscellaneous"
    When I create a new BOM via upload and validate in the UI
    When I navigate to "Administration" -> "BOM Management"
    And I entered the BOM Item Number in the UI page
    And I click on Apply Button
    Then the new BOM should be visible in the UI
    When I download the BOM template and validated
    When I download the BOM Cost record template and validated
    Then I validate the BOM view page UI values
    When Select the Managed By "" and validate in the UI
    Then I opened the bom audit history and validated the values
    Then I validated the Approved BOMs in UI
