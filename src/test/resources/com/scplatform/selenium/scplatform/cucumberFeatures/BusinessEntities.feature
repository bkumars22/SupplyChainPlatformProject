#Author: bkumar
@HarmonyTest
Feature: BusinessEntities Workflow

  #Scenario: Search Non-enterprises records should be present in the UI
    #Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    #When I navigate to "Administration" -> "Manage Business Entities"
    #And I click on Apply Button
    #And I verified non enterprices items are displayed in the UI page
#
  #Scenario: Validate System should process Business Entity XML file and validate the data in the UI page
    #Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    #When I navigate to "Administration" -> "Admin Upload"
    #When I uploaded the XML file for the business entity and checked it on the UI page
    #When I navigate to "Administration" -> "Manage Business Entities"
    #And I entered Business Id "Mfg001.1" are displayed in UI page
    #And I click on Apply Button
    #And I verified the Business Entities "Mfg001.1" values in the UI page
#
  #Scenario: Validate System should process ITEM AVL Xlsx file and validate the data in the UI page
    #Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    #When I navigate to "Administration" -> "Admin"
    #When I uploaded the XLSX file for the business entity Item and checked it on the UI page
    #When I navigate to "Search" -> "Items"
    #And I entered the Business Entities Item Number in the UI page
    #And I click on Apply Button
    #And I validate the table row with item type "Supplier" supplier name "Supp01 desc" and MCM value "MCM"
    #When I navigate to "Administration" -> "Admin"
    #When I uploaded the XLSX file for the business entity Item AVL and checked it on the UI page
    #When I navigate to "Search" -> "Item AVL"
    #And I entered the Business Entities Item Number in the UI page
    #And I click on Apply Button
    #And I validate the table row with item type "Supplier" supplier name "Supp01 desc" and MCM value "MCM"
#
  #Scenario: Validate System should process ITEM AVL Xlsx file and validate the data in the UI page
    #Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    #When I navigate to "Pricing" -> "Create Sourcing Lane"
    #And I entered the Business Entities Item Number in the UI page
    #And I click on Apply Button
    #And I Created a new sourcing lane with the Item Business Entities & SourceSites are present on the UI page

  Scenario: Navigates to Manage Business Entities, clicks a Business Name hyperlink, downloads the currency Excel, validates its contents, and deletes the file
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I entered Business Id "Mfg001.1" are displayed in UI page
    And I click on Apply Button
    And I downloaded the Business entities Currency records and validated in the excel file