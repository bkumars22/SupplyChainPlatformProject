@HarmonyTamSuite
Feature: TAM workflow

  Scenario: Search by MRP Site and verify MRP Site column values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM*"
    And I enter "APCC19*" on "mrpSite" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "60" seconds
    Then I verify "MRP Site" column displayed under search results
    And I verify the search results has MRP Site value displayed

  #Scenario: Search by Multiple MRP Site and verify MRP Site column values
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    #When I navigate to "Supply Collaboration" -> "Download Allocation"
    #And I click on "Clear" Button
    #And I enter "APCC190320" and "APCC190308" on Multiple "mrpSites" textfield
    #And I click on "Apply" Button
    #Then I verify "MRP Site" column displayed under search results
    #And I verify the search results has MRP Site value displayed

  @skip
  Scenario: Verify MRP Site on downloaded Supplier allocation excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "BANTA*"
    And I enter "APCC" on "mrpSite" textfield
    And I enter "APCC-APCC-001" on "site" textfield
    #And I select "All" on "TAM Exist" Combobox
    When I click on "Apply" Button
    And I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "suppMRPSiteColumnValidation"
    Then I log out of HarmonyMTCM

 @skip
 Scenario: Verify MRP Site on downloaded Item allocation excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "BANTA*"
    And I enter "APCC" on "mrpSite" textfield
    And I enter "APCC-APCC-001" on "site" textfield
    And I select "All" on "TAM Exist" Combobox
    When I click on "Apply" Button
    And I click on "Item Allocation Download" Button and verify the result for "Download Allocation" for "itemMRPSiteColumnValidation"
		Then I log out of HarmonyMTCM

    # scplatform-4889 Extra column data in downloaded Supplier & item allocation excel
  @skip
  Scenario: Verify no Extra column data in downloaded Supplier allocation excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM_CFG"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "TAM Exist" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "suppExtraColumnValidation"
    And I log out of HarmonyMTCM

  ## scplatform-4889 Extra column data in downloaded Supplier & item allocation excel
  @skip
  Scenario: Verify no Extra column data in downloaded Item allocation excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I enter "APCC19*" on "mrpSite" textfield
    And I set group name as "TAM_CFG"
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "TAM Exist" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    And I click on "Item Allocation Download" Button and verify the result for "Download Allocation" for "itemExtraColumnValidation"
    And I log out of HarmonyMTCM

  @skip
  Scenario: Verify column data count and search records count on UI , fg name and type are matching on downloaded Supplier alloc excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM_CFG"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "TAM Exist" Combobox
    And I select "No" on "Hide Supplier with no allocation" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    Then I get the "Supplier" Allocation result count from UI
    And I get and verify allocation details on UI
    And I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "suppDataValidation"
    And I log out of HarmonyMTCM

  @skip
  Scenario: Verify column data count and search records count on UI, fg name and type are matching on downloaded Item alloc excel
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "TAM_CFG"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "TAM Exist" Combobox
    And I select "No" on "Hide Supplier with no allocation" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    Then I get the "Item" Allocation result count from UI
    And I get and verify allocation details on UI
    And I click on "Item Allocation Download" Button and verify the result for "Download Allocation" for "itemDataValidation"
    And I log out of HarmonyMTCM
    
     #usability changes 
		#scplatform-4679 CSR06701009 - Manage Allocation - Copy paste CFG name with special character will prompt NULL error
 	Scenario: Verifying the groupName label and tooltip after search on ww level
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "CFG 2" on "groupName" Field
    Then I verify auto suggestion list is populated for "CFG"
    And I click on "Apply" Button
    Then I verify fgName as "CFG 2" besides filter expand button
    And I verify allocation scope as "Global" "WW" besides filter expand button

  Scenario: Verifying the groupName label and tooltip after search on region level
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "CFG 2" on "groupName" Field
    And I click on "Apply" Button
    Then I verify fgName as "CFG 2" besides filter expand button
    And I verify allocation scope as "Region" "APCC" besides filter expand button

  Scenario: Verifying the groupName label and tooltip after search on site level
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "CFG 2" on "groupName" Field
    And I click on "Apply" Button
    Then I verify fgName as "CFG 2" besides filter expand button
    And I verify allocation scope as "Site" "APCC-APCC" besides filter expand button
    
 Scenario: Verify Download allocation filter should be defaulted to show all irrespective of supplier-item alloc
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    Then I verify "No" value selected on "Hide Supplier with no allocation" comboBox
    Then I verify "All" value selected on "TAM Exist" comboBox
    Then I verify "ALL" value selected on "Group Type" comboBox
       
    #PDSUPPORT-3488 CSR07011004 
    Scenario: Site - CFG name truncated after search in Manage Allocation screen
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName '0084"6' on "groupName" Field
    Then I verify auto suggestion list is populated for "0084"
    And I click on "Apply" Button
    And I expand Filter icon on Header section
    Then I verify '0084"6' on "groupName" textfield
    
    # scplatform-4679 CSR06701009 - Manage Allocation - Copy paste CFG name with special character will prompt NULL error
     #PDSUPPORT-3488 CSR07011004 
    Scenario: Region- CFG name truncated after search in Manage Allocation screen
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName '0084"6' on "groupName" Field
    Then I verify auto suggestion list is populated for "0084"
    And I click on "Apply" Button
    And I expand Filter icon on Header section
    Then I verify '0084"6' on "groupName" textfield
    
    #PDSUPPORT-3488 CSR07011004 
    Scenario: Global - CFG name truncated after search in Manage Allocation screen
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName '0084"6' on "groupName" Field
    Then I verify auto suggestion list is populated for "0084"
    And I click on "Apply" Button
    And I expand Filter icon on Header section
    Then I verify '0084"6' on "groupName" textfield
    
    #CSR06922655 - One Cost Prod - Under Admin Section - Role - rename 'Upload Documents' setting description
  Scenario: Verify TAMAllocationUploadUI is not listed on Manage Roles under Business Doc tab
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser1"
	When I navigate to "Administration" -> "Manage Roles" 
	Then I should be landed on "Available Roles" page 
	And I click on the name "ADMIN"
	And I verify labelName "Supply Allocation (Sub-Tier)" on the loaded page
	And I click on the "Business Document" tab on "Manage Roles" page
	And I verify labelName "FUNCTIONAL_GROUP" on the loaded page
	And I verify labelName "PARENT_FUNCTIONAL_GROUP" on the loaded page
	And I verify labelName "TAM" on the loaded page