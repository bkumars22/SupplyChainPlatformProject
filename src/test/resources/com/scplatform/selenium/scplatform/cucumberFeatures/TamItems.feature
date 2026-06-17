@HarmonyTam
Feature: TAM workflow

  @skip
  Scenario Outline: Test Data create functional Groups and Parent Group for Mass Update via UI upload
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "itemsForMassUpdate" with "itemMass" & verify "" "success"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    And I click on multiple search "itemNumbers" icon
    And I wait till the page loads for "5" seconds
    And I set "itemMass11" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "itemNumber" and "confirm" the list on the popup
    And I click on multiple search "itemNumbers" icon
    And I wait till the page loads for "5" seconds
    And I set "itemMass12" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "itemNumber" and "confirm" the list on the popup
    And I click on multiple search "itemNumbers" icon
    And I wait till the page loads for "5" seconds
    And I set "itemMass21" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "itemNumber" and "confirm" the list on the popup
    And I click on multiple search "itemNumbers" icon
    And I wait till the page loads for "5" seconds
    And I set "itemMass22" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "itemNumber" and "confirm" the list on the popup
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    And I select all rows
    And I click on "Assign Responsibility" Button
    And I click on "Yes,Assign" confirmation Button
    Then I verify the "Responsibility has been assigned successfully" successful message
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "FunctionalGroupItemUploadUI" "fgsForMassUpdate" with "fgMass" & verify "" "success"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I click on multiple search "groupNames" icon
    And I wait till the page loads for "5" seconds
    And I set "FGMass1" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "groupName" and "confirm" the list on the popup
    And I click on multiple search "groupNames" icon
    And I wait till the page loads for "5" seconds
    And I set "FGMass2" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "groupName" and "confirm" the list on the popup
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    #When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    #And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set parent name as "parentMassUpdate"
    #And I click on "Apply" Button
    #And I wait till the page loads for "5" seconds
    #Then I verify "2" rows listed with checkbox name "selectedPageKeys"
#
    #Examples: 
      #| dataFile                      | fileName            | action     | msg | msgType |
      #| ParentFunctionalGroupUploadUI | parentForMassUpdate | parentMass |     | success |
      
  #Scenario: Mass Update on Global level
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I enter the groupName "fgMass1" on "groupName" Field
    #And I click on "Apply" Button
    #And I set Supply Allocation Value "100"
    #And I click on "Save" Button
    #Then I verify the "Allocation Group Saved" successful message
    #And I click on "Mass Update" Button
    #And I wait till the page loads for "15" seconds
    #And I click "Yes" on the warning popup with message "MassUpdate will copy only saved data"
    #Then I verify "Mass Update Success for: [fgMass2" successful message
    #Then I verify Supply Allocation Value "100" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I enter the groupName "fgMass2" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "100" on all fields

  #Scenario: Mass Update on Region level
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I enter the groupName "fgMass1" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "100" on all fields
    #And I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value "150"
    #And I click on "Save" Button
    #Then I verify the "Allocation Group Saved" successful message
    #And I click on "Mass Update" Button
    #And I wait till the page loads for "15" seconds
    #And I click "Yes" on the warning popup with message "MassUpdate will copy only saved data"
    #Then I verify "Mass Update Success for: [fgMass2" successful message
    #Then I verify Supply Allocation Value "150" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I enter the groupName "fgMass2" on "groupName" Field
    #And I click on "Apply" Button
    #And I expand Filter icon on Header section
    #Then I verify Supply Allocation Value "150" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "DAO"
    #And I enter the groupName "fgMass2" on "groupName" Field
    #And I click on "Apply" Button
    #And I expand Filter icon on Header section
    #Then I verify Supply Allocation Value "100" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "DAO"
    #And I enter the groupName "fgMass1" on "groupName" Field
    #And I click on "Apply" Button
    #And I expand Filter icon on Header section
    #Then I verify Supply Allocation Value "100" on all fields

  @skip
  Scenario: Search with item number on global level and verify value set at region level is not updated on global
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Global" TAM Planner
    And I enter "itemMass11" on the "itemNumber" textfield
    And I click on "Apply" Button
    And I expand Filter icon on Header section
    Then I verify Supply Allocation Value "100" on all fields

  #Scenario: Mass Update on Site level
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I set "Site" dropdown with Value "APCC-APCC"
    #And I enter the groupName "fgMass1" on "groupName" Field
    #And I click on "Apply" Button
    #And I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value "180"
    #And I click on "Save" Button
    #Then I verify the "Allocation Group Saved" successful message
    #And I click on "Mass Update" Button
    #And I wait till the page loads for "15" seconds
    #And I click "Yes" on the warning popup with message "MassUpdate will copy only saved data"
    #Then I verify "Mass Update Success for: [fgMass2" successful message
    #And I verify Supply Allocation Value "180" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I set "Site" dropdown with Value "APCC-APCC"
    #And I enter the groupName "fgMass2" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "180" on all fields

  @skip
  Scenario: Search with item number on region level and verify value set at site level is not updated on global
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter "itemMass11" on the "itemNumber" textfield
    And I click on "Apply" Button
    And I expand Filter icon on Header section
    Then I verify Supply Allocation Value "150" on all fields

  #Scenario: Verify both fgs updated via mass update on above scenarios are not changed due to region & site level changes
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Global" TAM Planner
    #And I enter the groupName "fgMass1" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "100" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Global" TAM Planner
    #And I enter the groupName "fgMass2" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "100" on all fields

 #Scenario: Create 2 fgs and without creating parent try to do mass update and verify error message
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "ItemAVLUI" "itemsForMassUpdate" with "itemMassValidation" & verify "" "success"
    #When I navigate to "Master Data Management" -> "Item Assignment"
    #And I click on "Clear" Button
    #And I click on multiple search "itemNumbers" icon
    #And I wait till the page loads for "5" seconds
    #And I set "massItem11" on Find textField on popup
    #And I click on "Search" Button on the popup
    #And I select "1" "itemNumber" and "confirm" the list on the popup
    #And I click on multiple search "itemNumbers" icon
    #And I wait till the page loads for "15" seconds
    #And I set "massItem12" on Find textField on popup
    #And I click on "Search" Button on the popup
    #And I select "1" "itemNumber" and "confirm" the list on the popup
    #And I click on multiple search "itemNumbers" icon
    #And I wait till the page loads for "15" seconds
    #And I set "massItem21" on Find textField on popup
    #And I click on "Search" Button on the popup
    #And I select "1" "itemNumber" and "confirm" the list on the popup
    #And I click on multiple search "itemNumbers" icon
    #And I wait till the page loads for "15" seconds
    #And I set "massItem22" on Find textField on popup
    #And I click on "Search" Button on the popup
    #And I select "1" "itemNumber" and "confirm" the list on the popup
    #And I click on "Apply" Button
    #And I wait till the page loads for "15" seconds
    #And I select all rows
    #And I click on "Assign Responsibility" Button
    #And I click on "Yes,Assign" confirmation Button
    #Then I verify the "Responsibility has been assigned successfully" successful message
    #When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    #And I upload the "FunctionalGroupItemUploadUI" "fgsForMassUpdate" with "fgMassValidation" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I click on multiple search "groupNames" icon
    #And I wait till the page loads for "15" seconds
    #And I set "massFG1" on Find textField on popup
    #And I click on "Search" Button on the popup
    #And I select "1" "groupName" and "confirm" the list on the popup
    #And I click on multiple search "groupNames" icon
    #And I wait till the page loads for "15" seconds
    #And I set "massFG2" on Find textField on popup
    #And I click on "Search" Button on the popup
    #And I select "1" "groupName" and "confirm" the list on the popup
    #And I click on "Apply" Button
    #And I wait till the page loads for "15" seconds
    #Then I verify search filter results are displayed 
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I enter the groupName "massFG1" on "groupName" Field
    #And I click on "Apply" Button
    #And I wait till the page loads for "15" seconds
    #And I set Supply Allocation Value "100"
    #And I click on "Mass Update" Button
    #And I wait till the page loads for "15" seconds
    #And I verify warning message "MassUpdate will copy only saved data" and click "Yes" button on popup displayed
    #Then I verify "Parent doesn't exist for FG: massFG1" message displayed
    #And I verify the "Allocation Group Saved" successful message along with expected error
    #And I wait till the page loads for "15" seconds
    #When I verify warning message "Parent Group Doesn't Exist for Functional Group." and click "Cancel" button on popup displayed
    #When I click "Cancel" on the warning popup with message as "Parent Group Doesn't Exist for Functional Group."
    #Then I should be landed on "Allocation Management" page


  #Scenario: Verify mass update without sibling FG for a parent group ie parent with single FG and verify error message
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    #And I click on "Clear" Button
    #And I set group name as "massFG1"
    #And I click on "Apply" Button
    #Then I verify "1" rows listed with checkbox name "selectedPageKeys"
    #When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Parent Group" Button
    #And I save the parent group "pgOneFGMassUpdate"
    #Then I verify the "Parent Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I enter the groupName "massFG1" on "groupName" Field
    #And I click on "Apply" Button
    #And I set Supply Allocation Value "100"
    #And I click on "Mass Update" Button
    #And I wait till the page loads for "15" seconds
    #And I verify warning message "MassUpdate will copy only saved data" and click "Yes" button on popup displayed
    #Then I verify "No FG to Mass update for Parent: pgOneFGMassUpdate" message displayed
    #And I verify the "Allocation Group Saved" successful message along with expected error

  #Scenario: Create parent grp for fgs without parent from mass update screen then mass update
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create the testData for MassUpdate
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "massFG1" on "groupName" Field
    #And I click on "Apply" Button
    #And I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value "140"
    #And I click on "Mass Update" Button
    #And I verify warning message "MassUpdate will copy only saved data" and click "Yes" button on popup displayed
    #Then I verify "Parent doesn't exist for FG: massFG1" message displayed
    #And I verify the "Allocation Group Saved" successful message along with expected error
    #When I click "Create" on the warning popup with message as "To do Mass update Create/Add Parent Group"
    #And I "create" parent "createParentForMassUpdate" from allocation page
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "massFG1" on "groupName" Field
    #And I click on "Apply" Button
    #And I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value "140"
    #And I click on "Save" Button
    #And I click on "Mass Update" Button
    #And I verify warning message "MassUpdate will copy only saved data" and click "Yes" button on popup displayed
    #Then I verify "Mass Update Success for: [massFG2" successful message
    #Then I verify Supply Allocation Value "140" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "massFG1" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "140" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "massFG2" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "140" on all fields

  #Scenario: Assign a parent grp fr above, mass update and verify both fgs
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create the testData for MassUpdate
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "massFG1" on "groupName" Field
    #And I click on "Apply" Button
    #And I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value "170"
    #And I click on "Mass Update" Button
    #And I verify warning message "MassUpdate will copy only saved data" and click "Yes" button on popup displayed
    #Then I verify "Parent doesn't exist for FG: massFG1" message displayed
    #And I verify the "Allocation Group Saved" successful message along with expected error
    #And I wait till the page loads for "20" seconds
#		And I click "Assign" on the warning popup with message as "To do Mass update Create/Add Parent Group"
    #And I "Assign Parent Group" parent "createParentForMassUpdate" from allocation page
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "massFG1" on "groupName" Field
    #And I click on "Apply" Button
    #And I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value "170"
    #And I click on "Mass Update" Button
    #And I verify warning message "MassUpdate will copy only saved data" and click "Yes" button on popup displayed
    #Then I verify "Mass Update Success for: [massFG2" successful message
    #Then I verify Supply Allocation Value "170" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "massFG1" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "170" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "massFG2" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "170" on all fields
    #
    # scplatform-4074 [CSR06385974] [Dell-OneCost][S3] One Cost prod - CFG - PSU_Benderstrada_600W - error in downloading item-allocation
     @skip
     Scenario: Verify column data count and search records count on UI, fg name and type are matching on downloaded Item alloc excel
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "massFG2"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "Hide Supplier with no allocation" Combobox
    When I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I get the "Item" Allocation result count from UI
    And I click on "Item Allocation Download" Button and verify the result for "Download Allocation" for "itemDataValidation" 
    
  @skip
  Scenario: Test Data Upload Item with different values the create FG and verify success message, then verify on UI
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUpload" with "uploadItemForTAMRegionSite" & verify "" "success"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "itemRegionAlloc"
    And I click on "Apply" Button
    And I wait till the page loads for "350" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "AutoItemFG" with "Save All"
    Then I verify the "Functional Group Saved" successful message

  @skip
  Scenario: Verify default settings on global allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Inherit checkbox with name "inheritValue" status as "checked" on all fields
    Then I verify Supply Allocation Value "" on all fields
    And I verify Item Allocation Value "" on all fields
    And I verify select button "hideSupplier" status as "unchecked"
    And I verify select button "hideItem" status as "unchecked"
    And I verify select button "allowHedging" status as "unchecked"
    And I verify select button "showMRPVolume" status as "checked"
    And I should see the Delete button should be displayed and enabled
    And I should see the "Mass Update" button should be displayed and enabled
    And I should see the "Reset" button should be displayed and enabled
    And I should see the "Apply Allocation" button should be disabled

  @skip
  Scenario: Verify default settings on region allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I verify Supply Allocation Value "" on all fields
    Then I verify Item Allocation Value "" on all fields
    Then I verify Inherit checkbox with name "inheritValue" status as "checked" on all fields
    And I verify Inherit checkbox with name "inheritItemValue" status as "unChecked" on all fields
    And I verify select button "hideSupplier" status as "unchecked"
    And I verify select button "hideItem" status as "unchecked"
    And I verify select button "allowHedging" status as "unchecked"
    And I verify select button "showMRPVolume" status as "checked"
    And I should see the Delete button should be displayed and enabled
    And I should see the "Mass Update" button should be displayed and enabled
    And I should see the "Reset" button should be displayed and enabled
    And I should see the "Apply Allocation" button should be disabled

  @skip
  Scenario: Verify default settings on site allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I verify Supply Allocation Value "" on all fields
    Then I verify Item Allocation Value "" on all fields
    Then I verify Inherit checkbox with name "inheritValue" status as "checked" on all fields
    And I verify Inherit checkbox with name "inheritItemValue" status as "unChecked" on all fields
    Then I verify select button "hideSupplier" status as "unchecked"
    And I verify select button "hideItem" status as "unchecked"
    And I verify select button "allowHedging" status as "unchecked"
    And I verify select button "showMRPVolume" status as "checked"
    And I should see the Delete button should be displayed and enabled
    And I should see the "Mass Update" button should be displayed and enabled
    And I should see the "Reset" button should be displayed and enabled
    And I should see the "Apply Allocation" button should be disabled

  @skip
  Scenario: Update region and save site level values and verify the site value
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "DAO"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "1"
    And I set Item Allocation value "100" on column "1"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "DAO"
    And I set "Site" dropdown with Value "DAO-APCC"
  	And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "2"
    And I set Item Allocation value "100" on column "1"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "DAO"
    And I set "Site" dropdown with Value "DAO-APCC"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "100" on column "1"
    And I verify Supply Allocation Value "80" on column "2"

  @skip
  Scenario: Save site level values and verify the values
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "EMF"
    And I set "Site" dropdown with Value "EMF-COMCHE"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "EMF"
    And I set "Site" dropdown with Value "EMF-COMCHE"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "80" on column "1"
    Then I verify Item Allocation Value "" on column "1"
    And I verify Supply Allocation Value "100" on column "2"
    Then I verify Item Allocation Value "" on column "2"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "EMF"
    And I set "Site" dropdown with Value "EMF-COMKUN"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "" on all fields
    Then I verify Item Allocation Value "" on all fields

  @skip
  Scenario: update for a specific column in global,again update that column on Region and verify site with final value set on region level
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "200" on column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "200" on column "1"
    Then I verify Item Allocation Value "" on column "1"
    And I verify Supply Allocation Value "100" on column "2"
    And I verify Item Allocation Value "" on column "2"

  @skip
  Scenario: update for a specific column in global, dont save on region level then update and verify site values
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "CCC4"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "80" on column "1"
    Then I verify Item Allocation Value "" on column "2"
    And I verify Supply Allocation Value "100" on column "2"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "CCC4"
    And I set "Site" dropdown with Value "CCC4-PEGCHO-8480"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "2"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "CCC4"
    And I set "Site" dropdown with Value "CCC4-PEGCHO-8480"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "80" on column "1"
    Then I verify Item Allocation Value "" on column "1"
    And I verify Supply Allocation Value "80" on column "2"
    And I verify Item Allocation Value "" on column "2"

  @skip
  Scenario: update for a specific column in global,again update that column on a Region and verify another region and site are not affected
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supplier Allocation value "200" on column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "BCC"
    And I set "Site" dropdown with Value "BCC-BFC"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "80" on column "1"
    Then I verify Item Allocation Value "" on column "1"
    And I verify Supply Allocation Value "100" on column "2"
    And I verify Item Allocation Value "" on column "2"

  @skip
  Scenario: update for a specific column in global,another on region and verify site have both changes
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "CCC"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "2"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "CCC"
    And I set "Site" dropdown with Value "CCC-CCC"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "80" on column "1"
    Then I verify Item Allocation Value "" on column "1"
    And I verify Supply Allocation Value "80" on column "2"
    And I verify Item Allocation Value "" on column "2"
    
	#scplatform-4074 [CSR06385974] [Dell-OneCost][S3] One Cost prod - CFG - PSU_Benderstrada_600W - error in downloading item-allocation
  @skip
  Scenario: Save and verify value fr a specific region and sites belongs to tht region are affected n other region-sites are not
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    When I set Supply Allocation Value "100"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "GEM"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "80" on column "2"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "GEM"
    And I set "Site" dropdown with Value "HK50"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "80" on column "1"
    And I verify Supply Allocation Value "80" on column "2"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "GEM"
    And I set "Site" dropdown with Value "HK50"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "80" on column "1"
    And I verify Supply Allocation Value "80" on column "2"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "GEM"
    And I set "Site" dropdown with Value "HU10"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "80" on column "1"
    And I verify Item Allocation Value "" on column "1"
    And I verify Supply Allocation Value "100" on column "2"
    And I verify Item Allocation Value "" on column "2"

  @skip
  Scenario: Verify item alloc values once updated then delete and save still retains default val on global allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value "100"
    And I set Item Allocation value "100" on column "1"
    And I select All Item allocation icon with value from the column "1"
    And I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I set Item Allocation value "" on column "1"
    And I select All Item allocation icon with value from the column "1"
    And I "check" the "inheritItemValue" checkbox
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on all fields

  @skip
  Scenario: Verify item alloc values once updated then delete and save still retains default val as checkbx is checked on region allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value "100"
    And I set Item Allocation value "100" on column "1"
    And I select All Item allocation icon with value from the column "1"
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I set Item Allocation value "" on column "1"
    And I select All Item allocation icon with value from the column "1"
    And I "check" the "inheritItemValue" checkbox
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on all fields

  @skip
  Scenario: Verify item alloc values are set to null when inheritItem checkbox is unchecked on global allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value "100"
    And I set Item Allocation value "" on column "1"
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "" on all fields

  @skip
  Scenario: Verify item alloc values are set to null when inheritItem checkbox is unchecked on region allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value "100"
    And I set Item Allocation value "" on column "1"
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "" on all fields

  @skip
  Scenario: Verify item alloc values are set to default value -100 when inheritItem checkbox is checked on global allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
   And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value "100"
    And I set Item Allocation value "" on column "1"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on all fields

  @skip
  Scenario: Verify item alloc values are set to default value -100 when inheritItem checkbox is checked on region allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemFG" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value "100"
    And I set Item Allocation value "" on column "1"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on all fields

  #Scenario: Test Data- Upload Item and FG
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "ItemAVLUI" "itemForTam" with "uploadTamMultipleSupplierItemAlloc" & verify "" "success"
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I select "ENTERPRISE" on "Business Entity Type" Combobox
    #And I enter itemNumbers on Multiple ItemNumber textfield for item alloc testData creation
    #And I click on "Apply" Button
    #And I wait till the page loads for "150" seconds
    #And I select first "5" rows from the "selectedPageKeys" "checkbox" list
    #And I click on "Create Group" Button
    #And I "create" the items to the Functional Group "AutoMultipleItemGroupAlloc" with "Save All"
    #Then I verify the "Functional Group Saved" successful message

  #Scenario: validate item allo val set to 100 ie default val at region level when checkbox is checked and null value set on Region level even for global value inherited
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    #And I click on "Apply" Button
    #When I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value on complete row "1" with "100"
    #And I set multiple Item Allocation value "100" on row "2"
    #And I set multiple Item Allocation value "100" on row "4"
    #And I set multiple Item Allocation value "50" on row "6"
    #And I set multiple Item Allocation value "40" on row "7"
    #And I set multiple Item Allocation value "10" on row "8"
    #And I "check" the "inheritItemValue" checkbox
    #And I select All Item allocation icon with value from the column "1"
    #And I click on "Save" Button
    #Then I verify the "Allocation Group Saved" successful message
    #And I verify Item Allocation Value "100" on row "2"
    #And I verify Item Allocation Value "100" on row "4"
    #And I verify Item Allocation Value "50" on row "6"
    #And I verify Item Allocation Value "40" on row "7"
    #And I verify Item Allocation Value "10" on row "8"
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
   #And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    #And I click on "Apply" Button
    #And I "uncheck" the "inheritItemValue" checkbox
    #And I select All Item allocation icon with value from the column "1"
    #When I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value on complete row "1" with "100"
    #And I click on "Save" Button
    #Then I verify the "Allocation Group Saved" successful message
    #Then I verify Item Allocation Value "100" on row "2"
    #And I verify Item Allocation Value "100" on row "4"
    #And I verify Item Allocation Value "50" on row "6"
    #And I verify Item Allocation Value "40" on row "7"
    #And I verify Item Allocation Value "10" on row "8"
    #When I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value on complete row "1" with "100"
    #And I set multiple Item Allocation value "" on row "2"
    #And I set multiple Item Allocation value "" on row "4"
    #And I set multiple Item Allocation value "" on row "6"
    #And I set multiple Item Allocation value "" on row "7"
    #And I set multiple Item Allocation value "" on row "8"
    #And I "check" the "inheritItemValue" checkbox
    #And I select All Item allocation icon with value from the column "1"
    #And I click on "Save" Button
    #Then I verify the "Allocation Group Saved" successful message
    #Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    #And I verify Item Allocation Value "100" on row "2"
    #And I verify Item Allocation Value "100" on row "4"
    #And I verify Item Allocation Value "100" on row "6"
    #And I verify Item Allocation Value "" on row "7"
    #And I verify Item Allocation Value "" on row "8"

  #Scenario: validate item allo val set to val at region level when checkbox is checked and value set on Region level even for global value inherited
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
   #And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    #And I click on "Apply" Button
    #When I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value on complete row "1" with "100"
    #And I set multiple Item Allocation value "100" on row "2"
    #And I set multiple Item Allocation value "100" on row "4"
    #And I set multiple Item Allocation value "50" on row "6"
    #And I set multiple Item Allocation value "40" on row "7"
    #And I set multiple Item Allocation value "10" on row "8"
    #And I "check" the "inheritItemValue" checkbox
    #And I select All Item allocation icon with value from the column "1"
    #And I click on "Save" Button
    #Then I verify the "Allocation Group Saved" successful message
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    #And I click on "Apply" Button
    #When I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value on complete row "1" with "100"
    #And I set multiple Item Allocation value "100" on row "2"
    #And I set multiple Item Allocation value "100" on row "4"
    #And I set multiple Item Allocation value "10" on row "6"
    #And I set multiple Item Allocation value "20" on row "7"
    #And I set multiple Item Allocation value "70" on row "8"
    #And I "check" the "inheritItemValue" checkbox
    #And I select All Item allocation icon with value from the column "1"
    #And I click on "Save" Button
    #Then I verify the "Allocation Group Saved" successful message
    #Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    #And I verify Item Allocation Value "100" on row "2"
    #And I verify Item Allocation Value "100" on row "4"
    #And I verify Item Allocation Value "10" on row "6"
    #And I verify Item Allocation Value "20" on row "7"
    #And I verify Item Allocation Value "70" on row "8"

  @skip
  Scenario: validate item alloc default value to 100 at region level when checkbox is checked and null value set on Region level even for global value not inherited
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "100" on row "4"
    And I set multiple Item Allocation value "50" on row "6"
    And I set multiple Item Allocation value "40" on row "7"
    And I set multiple Item Allocation value "10" on row "8"
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "" on row "2"
    And I verify Item Allocation Value "" on row "4"
    And I verify Item Allocation Value "" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "" on row "2"
    And I set multiple Item Allocation value "" on row "4"
    And I set multiple Item Allocation value "" on row "6"
    And I set multiple Item Allocation value "" on row "7"
    And I set multiple Item Allocation value "" on row "8"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "100" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"

  @skip
  Scenario: validate item alloc default value to null at region level when checkbox is unchecked and value set on Region level even for global value not inherited
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "100" on row "4"
    And I set multiple Item Allocation value "50" on row "6"
    And I set multiple Item Allocation value "40" on row "7"
    And I set multiple Item Allocation value "10" on row "8"
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "" on row "2"
    And I verify Item Allocation Value "" on row "4"
    And I verify Item Allocation Value "" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "100" on row "4"
    And I set multiple Item Allocation value "100" on row "6"
    And I set multiple Item Allocation value "" on row "7"
    And I set multiple Item Allocation value "" on row "8"
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Inherit checkbox with name "inheritItemValue" status as "unChecked" on all fields
    And I verify Item Allocation Value "" on row "2"
    And I verify Item Allocation Value "" on row "4"
    And I verify Item Allocation Value "" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"

  @skip
  Scenario: validate item alloc to global inherited val at region level when checkbox is unchecked and value set on Region level for global value inherited ie checked
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "100" on row "4"
    And I set multiple Item Allocation value "50" on row "6"
    And I set multiple Item Allocation value "40" on row "7"
    And I set multiple Item Allocation value "10" on row "8"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "50" on row "6"
    And I verify Item Allocation Value "40" on row "7"
    And I verify Item Allocation Value "10" on row "8"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "100" on row "4"
    And I set multiple Item Allocation value "20" on row "6"
    And I set multiple Item Allocation value "30" on row "7"
    And I set multiple Item Allocation value "50" on row "8"
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "50" on row "6"
    And I verify Item Allocation Value "40" on row "7"
    And I verify Item Allocation Value "10" on row "8"

  @skip
  Scenario: validate item alloc default value equals to value allocated at region level when checkbox is checked and value set on Region level
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "100" on row "4"
    And I set multiple Item Allocation value "50" on row "6"
    And I set multiple Item Allocation value "40" on row "7"
    And I set multiple Item Allocation value "10" on row "8"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "50" on row "6"
    And I verify Item Allocation Value "40" on row "7"
    And I verify Item Allocation Value "10" on row "8"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "100" on row "4"
    And I set multiple Item Allocation value "30" on row "6"
    And I set multiple Item Allocation value "30" on row "7"
    And I set multiple Item Allocation value "40" on row "8"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "30" on row "6"
    And I verify Item Allocation Value "30" on row "7"
    And I verify Item Allocation Value "40" on row "8"

  @skip
  Scenario: validate item alloc default value equals to value allocated at global level when checkbox is unchecked and null value set on Region level
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "100" on row "4"
    And I set multiple Item Allocation value "50" on row "6"
    And I set multiple Item Allocation value "40" on row "7"
    And I set multiple Item Allocation value "10" on row "8"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "50" on row "6"
    And I verify Item Allocation Value "40" on row "7"
    And I verify Item Allocation Value "10" on row "8"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "" on row "2"
    And I set multiple Item Allocation value "" on row "4"
    And I set multiple Item Allocation value "" on row "6"
    And I set multiple Item Allocation value "" on row "7"
    And I set multiple Item Allocation value "" on row "8"
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Inherit checkbox with name "inheritItemValue" status as "unchecked" on all fields
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "50" on row "6"
    And I verify Item Allocation Value "40" on row "7"
    And I verify Item Allocation Value "10" on row "8"

  @skip
  Scenario: validate item alloc default value -100 for multiple supp items when checkbox is checked and values set to null on Region level
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    And I clear testData for multiple item alloc on "Global" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    When I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    When I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "100" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"

  @skip
  Scenario: Validate item alloc auto populate for other supp items if alloc non exists on on ww level
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "" on row "2"
    And I set multiple Item Allocation value "" on row "4"
    And I set multiple Item Allocation value "" on row "6"
    And I set multiple Item Allocation value "" on row "7"
    And I set multiple Item Allocation value "" on row "8"
    And I "uncheck" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify Item Allocation Value "" on row "2"
    And I verify Item Allocation Value "" on row "4"
    And I verify Item Allocation Value "" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"
    When I set multiple Item Allocation value "100" on row "6"
    And I select All Item allocation icon with value from the column "1"
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    When I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "100" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"

  @skip
  Scenario: validate item alloc auto populate for other supp items if alloc non exists on Region level
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    And I clear testData for multiple item alloc on "Global" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
   And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    When I set multiple Item Allocation value "100" on row "2"
    When I set multiple Item Allocation value "100" on row "4"
    When I set multiple Item Allocation value "100" on row "6"
    And I "check" the "inheritItemValue" checkbox
    And I select All Item allocation icon with value from the column "1"
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    When I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Inherit checkbox with name "inheritItemValue" status as "checked" on all fields
    And I verify Item Allocation Value "100" on row "2"
    And I verify Item Allocation Value "100" on row "4"
    And I verify Item Allocation Value "100" on row "6"
    And I verify Item Allocation Value "" on row "7"
    And I verify Item Allocation Value "" on row "8"

  Scenario Outline: Create a functional Group for TAM delete validation
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItemAllocDel"
    And I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "AutoItemGroupDelete" with "Save All"
    Then I verify the "Functional Group Saved" successful message

    Examples: 
      | dataFile  | fileName   | action                 | msg | msgType |
      | ItemAVLUI | ItemUpload | uploadItemForTAMDelete |     | success |

  Scenario: Supp allocation delete Band verify warning message for no records selected along with verifying close button
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter "AutoItemGroupDelete" on the "groupName" textfield
    And I click on "Apply" Button
    And I click on "Delete" Button
    And I click on Supply Allocation delete Button
    Then I verify warning message "No Records Selected." and click "OK" button on popup displayed

  Scenario: Delete Supp alloc on global and verify Region and site are also deleted along with fg is active
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    And I create testData for single suppItem delete scenario on "global" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    And I enter "AutoItemGroupDelete" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "200" on all fields
    Then I verify Item Allocation Value "100" on all fields
    And I click on "Delete" Button
    And I select "supplier" on Delete Allocations Option
    And I "check" the "selectAllDeleteSite" checkbox
    Then I verify "globalDeleteList" checkbox status as "checked"
    And I click on Supply Allocation delete Button
    When I click "Yes" on the warning popup with message "Are you sure you want to delete selected TAM. Once deleted it can't be undone."
    And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    Then I verify Supply Allocation Value "" on all fields
    And I expand Filter icon on Header section
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "" on all fields
    And I expand Filter icon on Header section
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I click on "Apply" Button
    Then I verify Supply Allocation Value "" on all fields
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set group name as "AutoItemGroupDelete"
    And I click on "Apply" Button
    And I click on the name "AutoItemGroupDelete"
    Then I should be landed on "Edit Group AutoItemGroupDelete" page
    Then I verify "status" checkbox status as "unchecked"

  #Scenario: Delete Item alloc on global and verify Region and site are also deleted along with fg is active
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "global" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I click on "Apply" Button
    #And I click on "Delete" Button
    #And I select "item" on Delete Allocations Option
    #And I "check" the "selectAllDeleteSite" checkbox
    #Then I verify "globalDeleteList" checkbox status as "checked"
    #And I click on Supply Allocation delete Button
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    #Then I verify Item Allocation Value "" on all fields
    #Then I verify Supply Allocation Value "" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "" on all fields
    #Then I verify Supply Allocation Value "" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I set "Site" dropdown with Value "APCC-APCC"
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "" on all fields
    #Then I verify Supply Allocation Value "" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set group name as "AutoItemGroupDelete"
    #And I click on "Apply" Button
    #And I click on the name "AutoItemGroupDelete"
    #Then I should be landed on "Edit Group AutoItemGroupDelete" page
    #Then I verify "status" checkbox status as "unchecked"

  #Scenario: Delete both supp and Item alloc on global and verify Region and site are also deleted along with fg is inactive
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "global" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I click on "Apply" Button
    #And I click on "Delete" Button
    #Then I verify Supply Allocation Value "200" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I select "both" on Delete Allocations Option
    #And I "check" the "selectAllDeleteSite" checkbox
    #Then I verify "globalDeleteList" checkbox status as "checked"
    #And I click on Supply Allocation delete Button
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    #Then I verify Item Allocation Value "" on all fields
    #And I verify Supply Allocation Value "" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "" on all fields
    #And I verify Supply Allocation Value "" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I set "Site" dropdown with Value "APCC-APCC"
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "" on all fields
    #And I verify Supply Allocation Value "" on all fields
    #And I verify "You are editing TAM against an Inactive Functional Group" message
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set group name as "AutoItemGroupDelete"
    #And I click on "Apply" Button
    #And I click on the name "AutoItemGroupDelete"
    #Then I should be landed on "Edit Group AutoItemGroupDelete" page
    #Then I verify "status" checkbox status as "checked"

  #Scenario: Delete supp alloc at region level and verify sites under that region is affected and other region sites and global are not affected also fg is inactive
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "region" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "160" on all fields
    #And I click on "Delete" Button
    #And I select "supplier" on Delete Allocations Option
    #And I "check" the "selectAllDeleteSite" checkbox
    #Then I verify "regionDeleteList" checkbox status as "checked"
    #And I click on Supply Allocation delete Button
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    #Then I verify Supply Allocation Value "100" on all fields
    #And I wait till the page loads for "10" seconds
    #And I expand Filter icon on Header section
    #And I click on "Global" TAM Planner
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #And I verify Supply Allocation Value "100" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #And I verify Supply Allocation Value "110" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I set "Site" dropdown with Value "BCC-BFC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #And I verify Supply Allocation Value "110" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set group name as "AutoItemGroupDelete"
    #And I click on "Apply" Button
    #And I click on the name "AutoItemGroupDelete"
    #Then I should be landed on "Edit Group AutoItemGroupDelete" page
    #Then I verify "status" checkbox status as "unchecked"

  #Scenario: Delete item alloc at region level and verify sites under that region is affected and other region sites and global are not affected also fg is active
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "region" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #And I click on "Delete" Button
    #And I select "item" on Delete Allocations Option
    #And I "check" the "selectAllDeleteSite" checkbox
    #Then I verify "regionDeleteList" checkbox status as "checked"
    #And I click on Supply Allocation delete Button
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    #Then I verify Item Allocation Value "100" on all fields
    #And I wait till the page loads for "10" seconds
    #And I expand Filter icon on Header section
    #And I click on "Global" TAM Planner
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #And I verify Item Allocation Value "100" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #And I verify Item Allocation Value "100" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I set "Site" dropdown with Value "BCC-BFC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #And I verify Item Allocation Value "100" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set group name as "AutoItemGroupDelete"
    #And I click on "Apply" Button
    #And I click on the name "AutoItemGroupDelete"
    #Then I should be landed on "Edit Group AutoItemGroupDelete" page
    #Then I verify "status" checkbox status as "unchecked"

  #Scenario: Delete both alloc at region level and verify sites under that region is affected and other region sites and global are not affected and fg is inactive
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "region" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "160" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I click on "Delete" Button
    #And I select "both" on Delete Allocations Option
    #And I "check" the "selectAllDeleteSite" checkbox
    #Then I verify "regionDeleteList" checkbox status as "checked"
    #And I click on Supply Allocation delete Button
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    #Then I verify Item Allocation Value "100" on all fields
    #Then I verify Supply Allocation Value "100" on all fields
    #And I wait till the page loads for "10" seconds
    #And I expand Filter icon on Header section
    #And I click on "Global" TAM Planner
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #And I verify Supply Allocation Value "100" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #And I verify Supply Allocation Value "110" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I set "Site" dropdown with Value "BCC-BFC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #And I verify Supply Allocation Value "110" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set group name as "AutoItemGroupDelete"
    #And I click on "Apply" Button
    #And I click on the name "AutoItemGroupDelete"
    #Then I should be landed on "Edit Group AutoItemGroupDelete" page
    #Then I verify "status" checkbox status as "unchecked"

  #Scenario: Delete item alloc at site level and verify site supply alloc are deleted and global and other region sites are not affected and fg is active
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "site" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I set "Site" dropdown with Value "APCC-APCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "140" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I click on "Delete" Button
    #And I delete "siteItem" on Delete Allocations from site level
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    #Then I verify Supply Allocation Value "140" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I wait till the page loads for "10" seconds
    #And I expand Filter icon on Header section
    #And I click on "Global" TAM Planner
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "90" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "90" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I set "Site" dropdown with Value "BCC-BFC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #And I verify Supply Allocation Value "90" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set group name as "AutoItemGroupDelete"
    #And I click on "Apply" Button
    #And I click on the name "AutoItemGroupDelete"
    #Then I should be landed on "Edit Group AutoItemGroupDelete" page
    #Then I verify "status" checkbox status as "unchecked"
#
  #Scenario: Delete supp alloc at site level and verify site supply alloc are deleted and global and other region sites are not affected and fg is active
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "site" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I set "Site" dropdown with Value "APCC-APCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "140" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I click on "Delete" Button
    #And I delete "siteSupplier" on Delete Allocations from site level
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    #Then I verify Supply Allocation Value "80" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I wait till the page loads for "10" seconds
    #And I expand Filter icon on Header section
    #And I click on "Global" TAM Planner
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #And I verify Supply Allocation Value "90" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "90" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I set "Site" dropdown with Value "BCC-BFC"
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #Then I verify Supply Allocation Value "90" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set group name as "AutoItemGroupDelete"
    #And I click on "Apply" Button
    #And I click on the name "AutoItemGroupDelete"
    #Then I should be landed on "Edit Group AutoItemGroupDelete" page
    #Then I verify "status" checkbox status as "unchecked"

  #Scenario: Delete both alloc at site level and verify site supply alloc are deleted and global and other region sites are not affected and fg is active
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "site" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "APCC"
    #And I set "Site" dropdown with Value "APCC-APCC"
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "140" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I click on "Delete" Button
    #And I delete "siteBoth" on Delete Allocations from site level
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    #Then I verify Supply Allocation Value "80" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I wait till the page loads for "10" seconds
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Global" TAM Planner
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #And I verify Supply Allocation Value "90" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Region" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I click on "Apply" Button
    #Then I verify Supply Allocation Value "90" on all fields
    #Then I verify Item Allocation Value "100" on all fields
    #And I expand Filter icon on Header section
    #And I click on "Clear" Button
    #And I click on "Site" TAM Planner
    #And I set "Region" dropdown with Value "BCC"
    #And I set "Site" dropdown with Value "BCC-BFC"
    #And I enter the groupName "AutoItemGroupDelete" on "groupName" Field
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I click on "Apply" Button
    #Then I verify Item Allocation Value "100" on all fields
    #Then I verify Supply Allocation Value "90" on all fields
    #When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    #And I click on "Clear" Button
    #And I set group name as "AutoItemGroupDelete"
    #And I enter the groupName "AutoItemGroupDelete" on "value(functionalGroupName)" Field
    #And I click on "Apply" Button
    #And I click on the name "AutoItemGroupDelete"
    #Then I should be landed on "Edit Group AutoItemGroupDelete" page
    #Then I verify "status" checkbox status as "unchecked"
#
  #Scenario Outline: Verify error message on trying to delete from global when TAM exists on region and site
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    #And I create testData for single suppItem delete scenario on "site" level
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Global" TAM Planner
    #And I enter "AutoItemGroupDelete" on the "groupName" textfield
    #And I click on "Apply" Button
    #And I click on "Delete" Button
    #And I select "<alloc>" on Delete Allocations Option
    #And I "check" the "selectAllDeleteSite" checkbox
    #Then I verify "globalDeleteList" checkbox status as "checked"
    #And I click on Supply Allocation delete Button
    #And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
#
    #Examples: 
      #| alloc    |
      #| item     |
      #| supplier |
      #| both     |

  Scenario: Verify error message on trying to delete on Global level when No TAM exists
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    And I create testData for single suppItem delete scenario on "NoAllocOnAll" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Global" TAM Planner
    And I enter "AutoItemGroupDelete" on the "groupName" textfield
    And I click on "Apply" Button
    And I click on "Delete" Button
    Then I verify the warning messages "There is no TAM for selected FG" followed by clicking "Ok" button

  Scenario: Verify error message on trying to delete on Region level when No TAM exists
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    And I create testData for single suppItem delete scenario on "NoAllocOnAll" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter "AutoItemGroupDelete" on the "groupName" textfield
    And I click on "Apply" Button
    And I click on "Delete" Button
    Then I verify the warning messages "TAM is cascaded from Global/Region. There is no TAM for selected FG for deletion" followed by clicking "Ok" button

  Scenario: Verify error message on trying to delete on Site level when No TAM exists
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    And I create testData for single suppItem delete scenario on "NoAllocOnAll" level
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter "AutoItemGroupDelete" on the "groupName" textfield
    And I click on "Apply" Button
    And I click on "Delete" Button
    And I delete "siteItem" on Delete Allocations from site level
    Then I verify the "TAM is cascaded from Global/Region. There is no TAM for selected FG for deletion" warning message
    And I click on "Delete" Button
    And I delete "siteSupplier" on Delete Allocations from site level
    Then I verify the warning messages "There is no TAM for selected FG" followed by clicking "Ok" button
    And I click on "Delete" Button
    And I delete "siteBoth" on Delete Allocations from site level
    Then I verify the warning messages "There is no TAM for selected FG" followed by clicking "Ok" button

   @skip
   Scenario: Verify Tam delete for removed supplier, also verifying tam alloc status on FG edit page for both alloc exists and non exists cases
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    When I click on "Delete" Button
    And I select "both" on Delete Allocations Option
    And I "check" the "selectAllDeleteSite" checkbox
    And I click on Supply Allocation delete Button
    And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    Then I verify Supply Allocation Value "" on all fields
    And I verify Item Allocation Value "" on all fields
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupAlloc"
    And I click on "Apply" Button
    Then I verify "TAM Exist" column has "No" as value displayed under search results for all rows
    And I click on the name "AutoMultipleItemGroupAlloc"
    Then I should be landed on "Edit Group AutoMultipleItemGroupAlloc" page
    And I verify Tam Availability as "No" on Edit FG page
    When I select first "1" rows from the "items" "checkbox" list
    When I click on "Remove Item" Button
    Then I verify warning message "Deleting Item from FG will delete Allocation for current Item" and click "Yes" button on popup displayed
    And I save one "item" removed for verification
    Then I verify the "deleted from FG" successful message
    And I save the FG
    Then I verify the "Functional Group Saved" successful message
		When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "1" with "100"
    And I set multiple Item Allocation value "100" on row "2"
    And I set multiple Item Allocation value "40" on row "4"
    And I set multiple Item Allocation value "40" on row "5"
    And I set multiple Item Allocation value "20" on row "6"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    Then I verify Supply Allocation Value "100" on all fields
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupAlloc"
    And I click on "Apply" Button
    And I click on the name "AutoMultipleItemGroupAlloc"
    Then I should be landed on "Edit Group AutoMultipleItemGroupAlloc" page
		Then I verify "status" checkbox status as "checked" 
		When I "uncheck" the "status" checkbox 
		And I "update" the items to the Functional Group "AutoMultipleItemGroupAlloc" with "Save All" 
		Then I verify the "Functional Group Saved" successful message 
		Then I verify "status" checkbox status as "unchecked"  
		When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value on complete row "3" with "100"
		And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message  
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set group name as "AutoMultipleItemGroupAlloc"
    And I click on "Apply" Button
    Then I verify "TAM Exist" column has "Yes" as value displayed under search results for all rows
    And I click on the name "AutoMultipleItemGroupAlloc"
    Then I should be landed on "Edit Group AutoMultipleItemGroupAlloc" page
    And I verify Tam Availability as "Yes" on Edit FG page
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoMultipleItemGroupAlloc" on "groupName" Field
    And I click on "Apply" Button
    When I click on "Delete" Button
    And I select "supplier" on Delete Allocations Option
    And I "check" the "selectAllDeleteSite" checkbox
    Then I verify "globalDeleteList" checkbox status as "checked"
    And I click on Supply Allocation delete Button
    And I verify the warning messages "Are you sure you want to delete selected TAM. Once deleted it can't be undone." followed by clicking "Yes" button
    Then I verify Supply Allocation Value "" on all fields
    
  	###scplatform-4833 No option to Delete allocation for Buyer role
  	####scplatform-4655 Space is getting trimmed while searching CFG Name
  Scenario: Verify No option to Delete allocation for Buyer role
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "BUYER"
    And I click on the "Business Document" tab on "Manage Roles" page
    When I "uncheck" the tam "Delete-global" checkbox
    When I "uncheck" the tam "Delete-region" checkbox
    When I "uncheck" the tam "Delete-site" checkbox
    And I click on the save button  
    Then I verify the tam "Delete-global" checkbox status as "unchecked"
    Then I verify the tam "Delete-region" checkbox status as "unchecked"
    Then I verify the tam "Delete-site" checkbox status as "unchecked"  
     And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "CFG 2" on "groupName" Field
    And I click on "Apply" Button
    Then I should not see the Delete button displayed
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
 		And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"   
    And I enter the groupName "CFG 2" on "groupName" Field
    And I click on "Apply" Button
    Then I should not see the Delete button displayed
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
		And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "CFG 2" on "groupName" Field
    And I click on "Apply" Button
    Then I should not see the Delete button displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "BUYER"
    And I click on the "Business Document" tab on "Manage Roles" page
    When I "check" the tam "Delete-global" checkbox
    When I "check" the tam "Delete-region" checkbox
    When I "check" the tam "Delete-site" checkbox
    And I click on the save button
    Then I verify the tam "Delete-global" checkbox status as "checked"
    Then I verify the tam "Delete-region" checkbox status as "checked"
    Then I verify the tam "Delete-site" checkbox status as "checked"  
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "CFG 2" on "groupName" Field
    And I click on "Apply" Button
    Then I should see the Delete button should be displayed and enabled
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
 		And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"   
    And I enter the groupName "CFG 2" on "groupName" Field
    And I click on "Apply" Button
    Then I should see the Delete button should be displayed and enabled
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
		And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "CFG 2" on "groupName" Field
    And I click on "Apply" Button
    Then I should see the Delete button should be displayed and enabled
    And I log out of HarmonyMTCM

    
    #scplatform-2784	Item Assignment - UI Improvement
#	Scenario: Assign and unassign resp for 2 items from multiple search results without switching pages 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin4" 
#	When I navigate to "Master Data Management" -> "Item Assignment" 
#	And I click on "Clear" Button 
#	And  I click on multiple search "itemNumbers" icon 
#	And  I wait till the page loads for "20" seconds 
#	And  I set "itemMass11" on Find textField on popup 
#	And  I click on "Search" Button on the popup 
#	And  I select "1" "itemNumbers" and "confirm" the list on the popup
#	And  I click on multiple search "itemNumbers" icon 
#	And  I wait till the page loads for "20" seconds 
#	And  I set "itemMass12" on Find textField on popup 
#	And  I click on "Search" Button on the popup 
#	And  I select "1" "itemNumbers" and "confirm" the list on the popup
#	And I click on "Apply" Button
#	#TestData clearance
#	And I "unassign" responsibility "ADMIN" to the item "2" selected
#	Then I verify the responsibility "" "unassigned" to "itemMass" on first 2 rows
#	And I verify the "Responsibility has been unassigned successfully" successful message
#	########################
#	And I "assign" responsibility "aaronrivas" to the item "2" selected
#	Then I verify the responsibility "aaronrivas" "assigned" to "itemMass" on first 2 rows
#	And I verify the "Responsibility has been reassigned successfully" successful message
#	And I "assign" responsibility "ADMIN" to the item "2" selected
#	And I click "Yes" on the warning popup with message "Production responsibility has already been assigned to"
#	And I verify the warning messages "Production responsibility has already been assigned to" followed by clicking "Yes" button
#	Then I verify the responsibility "PRODUCTION" "assigned" to "itemMass" on first 2 rows
#	And I verify the "Responsibility has been reassigned successfully" successful message
#	
	Scenario: Verify async download on DOwnload allocation - Item All
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "massFG2"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on the "keyboard_arrow_down" icon to trigger the popup
    And I wait till the page loads for "2" seconds
    And I click on the name "Item Allocation Download"
    And I wait till the page loads for "15" seconds
   	And I should see "Click here to view " link is displayed on variance report page
		And I click on the "Click here to view" report link
		Then I verify "Download" column displayed under search results
		Then I verify "Report Name" column displayed under search results
		Then I verify "Report Status" column displayed under search results
		Then I verify "Delete" column displayed under search results  
		
	Scenario: Verify async download on DOwnload allocation - Supp Allocation
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin4"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I set group name as "massFG2"
    And I enter "APCC19*" on "mrpSite" textfield
    And I select "CFG" on "Group Type" Combobox
    And I select "No" on "Hide Supplier with no allocation" Combobox
    #When I click on "asynchronous download" Button
    And I click on the "keyboard_arrow_down" icon to trigger the popup
    And I wait till the page loads for "5" seconds
    And I click on the name "Supplier Allocation Download"
    And I wait till the page loads for "15" seconds
   	And I should see "Click here to view " link is displayed on variance report page
		And I click on the "Click here to view" report link
		Then I verify "Download" column displayed under search results
		Then I verify "Report Name" column displayed under search results
		Then I verify "Report Status" column displayed under search results
		Then I verify "Delete" column displayed under search results  
		
#scplatform-7836 Download Allocation(TAM) Template
#Supplier and Item allocations (TAM allocations) can be deleted via file upload. But there is no option to download the template from the UI.
  #Scenario: Download Delete TAM Template using FunctionalGroupName
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    #When I navigate to "Supply Allocation" -> "TAM Delete Template"
    #And I click on "Clear" Button
    #And I enter "SEBRING" on the "value(functionalGroupName)" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "30" seconds
    #Then I verify search filter results are displayed
    #Then I should see "file_download" icon is "displayed"
    #And I click on the "file_download" icon
    
  #Scenario: Download Delete TAM Template using ItemNumber
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
    #When I navigate to "Supply Allocation" -> "TAM Delete Template"
    #And I click on "Clear" Button
    #And I enter "WW" on the "value(site)" textfield
    #And I enter "DH108" on the "value(itemNumber)" textfield
    #And I select the "value(itemAllocationExist)" with value "Yes"
    #And I click on "Apply" Button
    #And I wait till the page loads for "30" seconds
    #Then I verify search filter results are displayed
    #Then I should see "file_download" icon is "displayed"
    #And I click on the "file_download" icon
		
    