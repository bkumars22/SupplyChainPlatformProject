@HarmonyPG 
Feature: Parent Group Workflow Test Plan 

#scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
Scenario: Verify search criteria retained after changing pages and return 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I enter "testxyz" on "functionalGroupName" textfield 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I click on "Apply" Button 
	Then I should be landed on "Manage Parent" page 
	Then I verify "No records found to display" message 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	Then I should be landed on "Manage Functional Group" page 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	Then I verify "testxyz" on "functionalGroupName" textfield 
	And I verify the "yes" value selected on "Show Group Without Parent" comboBox 
	And I click on "Clear" Button 
	Then I verify "" on "functionalGroupName" textfield 
	And I verify the "" value selected on "Show Group Without Parent" comboBox 
	
	#scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
Scenario: Verify search criteria retained after logout and then login 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I enter "testxyz" on "functionalGroupName" textfield 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I click on "Apply" Button 
	Then I should be landed on "Manage Parent" page 
	Then I verify "No records found to display" message 
	And I log out of HarmonyMTCM 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	Then I should be landed on "Manage Parent" page 
	Then I verify "testxyz" on "functionalGroupName" textfield 
	And I verify the "yes" value selected on "Show Group Without Parent" comboBox 
	And I click on "Clear" Button 
	And I click on "Apply" Button 
	And I wait till the page loads for "30" seconds 
	Then I should be landed on "Manage Parent" page 
	Then I verify "" on "functionalGroupName" textfield 
	And I verify the "" value selected on "Show Group Without Parent" comboBox 
	And I log out of HarmonyMTCM 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	Then I should be landed on "Manage Parent" page 
	Then I verify "" on "functionalGroupName" textfield 
	And I verify the "" value selected on "Show Group Without Parent" comboBox 
	
Scenario: Create a Parent Group of type CFG and verify success message 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I select "CFG" on "Parent Type" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "25" seconds 
	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Parent Group" Button 
	And I save the parent group "createParentGroup" 
	Then I verify the "Parent Group Saved" successful message 
	
Scenario: Create a Parent Group,then assign a group and verify save All and Exit button functionality 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I select "CFG" on "Parent Type" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "25" seconds 
	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Parent Group" Button 
	And I save the parent group "AssignParentGroup" 
	Then I verify the "Parent Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I select "CFG" on "Parent Type" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "15" seconds 
	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Assign to Parent Group" Button 
	And I save the parent group "AssignParentGroup" 
	Then I verify the "Parent Group Saved" successful message 
	When I click on "Save All and Exit" Button 
	Then I should be landed on "Manage Parent Group" page 
	
Scenario: Edit a Parent Group by adding FG on create PG page, verify on FG page and then remove and verify 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set parent name as "PARENT1234" 
	And I click on "Apply" Button 
	And I click on the name "PARENT1234" 
	Then I should be landed on "Edit PARENT1234" page 
	And I click on the "Add Functional Group" Button 
	And I set "00T2N_CFG" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "parentName" and "confirm" the list on the popup 
	And I save the parent group 
	Then I verify the "Parent Group Saved" successful message 
	And I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "00T2N_CFG" 
	And I click on "Apply" Button 
	And I wait till the page loads for "3" seconds 
	And I click on the name "00T2N_CFG" 
	And I wait till the page loads for "3" seconds 
	#Then I verify the "PARENT1234" value selected on "Parent" comboBox 
	When I click on "delete" parent for the group 
	Then I verify the "none" value selected on "Parent" comboBox 
	When I save the FG 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set parent name as "PARENT1234" 
	And I click on "Apply" Button 
	And I click on the name "PARENT1234" 
	Then I should be landed on "Edit PARENT1234" page 
	And I verify FG "00T2N_CFG" not listed under Group Details 
	
	# 	Scenario:Create a Functional Group , edit by changing the group name, search for old grp name
	#	Scenario: Edit -> remove without selecting an item and verify the error message 	
	# 	Scenario: Remove an existing FG on Parent Group using X button
	# 	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	#	When I navigate to "Supply Colu27277laboration" -> "Manage Parent Group" 
	# 	And I set parent name as "PARENT1234"
	# 	And I click on the name "PARENT1234"
	
Scenario Outline: Verify page jumps, page size on the list search page -> verify prev btn for frst page n next btn for las page r disabled 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I click on "Apply" Button 
	And I wait till the page loads for "30" seconds 
	And I select "<pageSize>" on "Page Size" Combobox 
	And I wait till the page loads for "20" seconds 
	Then I verify "<pageSize>" rows listed as "pagesize selection" search results 
	And I log out of HarmonyMTCM 
	Examples: 
		|pageSize|
		|1		 |
		|50		 |
		|70		 |
		|100	 |
		|10		 |
		
Scenario: Verify page jumps on the list search page #verify prev btn for frst page n next btn for las page r disabled 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I click on "Apply" Button 
	And I wait till the page loads for "45" seconds 
	And I enter "3" on "pagenum" textfield 
	And I click on "Jump" Button 
	Then I verify page "3" out of total pages under the list 
	And I log out of HarmonyMTCM 
	
Scenario Outline: Search and verify a parent name(also search with * appended on parent name then navigate to edit parent and later show History page 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
#	When I delete cookies from the broswer 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set parent name as "<parentName>" 
	And I click on "Apply" Button 
	And I click on the name "parent-update" 
	Then I should be landed on "Edit parent-update" page 
	When I click on "Show History" Button 
	Then I should be landed on Audit History page 
	When I click on Close button on Audit History page 
	Then I log out of HarmonyMTCM 
	Examples: 
		|parentName			|
		|parent-update	|
		|parent-update*	|
		
Scenario Outline: Search for a non existing parent Name , parent name with spec chars(not allowed) and verify the message 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set parent name as "<parentName>" 
	And I click on "Apply" Button 
	Then I verify "No records found to display" message 
	And I log out of HarmonyMTCM 
	Examples: 
		|parentName	|	
		|TEST_GROUP	|
		|TEST-GROUP	|	
		# scplatform-4500 CSR06640105 - FG Management â€“ Create Parent FG UI
Scenario Outline: Try saving parent group with parent group name as null and with non-allowed chars 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I click on "Apply" Button 
	And  I wait till the page loads for "10" seconds 
	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Parent Group" Button 
	And I try to create the parent group "<parentGrpName>" 
	Then I verify "<errorMsg>" message on PopUp 
	Examples: 
		|parentGrpName		|errorMsg																							 |
		|									|Parent Name Shouldn't empty													 |
		|Test*12					|Parent Name should allow only _ and - special symbols.|
		|Test<12_					|Parent Name should allow only _ and - special symbols.|
		|Test=34}					|Parent Name should allow only _ and - special symbols.|
		#, space, -._,(){}[]+"#:% allowed characters|
		
Scenario: Search with multiple parent Name autoComplete field and verify the results 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	# TestData clearance Verifying no other parent holds this group
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "006HT_KIT" 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds 
	And I click on the name "006HT_KIT" 
	And I click on "delete" parent for the group 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	Then I verify the "none" value selected on "Parent" comboBox 
	# ends here
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set parent name as "t1-test" 
	And I click on "Apply" Button 
	And I click on the name "t1-test" 
	And I add new FG "006HT_KIT" to the parent group 
	And I remove added FG "006HT_KIT" from the parent group 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set parent name as "t1-test" 
	And I click on "Apply" Button 
	And I click on the name "t1-test" 
	Then I should be landed on "Edit t1-test" page 
	And I verify FG "006HT_KIT" not listed under Group Details 
	
Scenario: Add-remove FG on Manage Parent Edit Page 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set parent name as "t1-test" 
	And I click on "Apply" Button 
	And I click on the name "t1-test" 
	And I add new FG "006HT_KIT" to the parent group 
	And I remove added FG "006HT_KIT" from the parent group 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set parent name as "t1-test" 
	And I click on "Apply" Button 
	And I click on the name "t1-test" 
	Then I should be landed on "Edit t1-test" page 
	And I verify FG "006HT_KIT" not listed under Group Details 
	
@skip
Scenario: Try to create duplicate Parent group and verify error message 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I select "CFG" on "Parent Type" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "30" seconds 
	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Parent Group" Button 
	And I save the parent group "createDupParentGroup" 
	Then I verify the "Parent Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "25" seconds 
	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Parent Group" Button 
	And I save the parent group "createDupParentGroup" 
	Then I verify "Parent Functional Group With Same name already exists" error message 
	
Scenario: Verify Saved Filter functionality using saveAs btn on Manage Parent Group 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I set parent name as "ParentFilter" 
	And I "save" the filter "ParentFilter" by clicking on save as button 
	And I select "ParentFilter" on "Saved Filters" Combobox 
	Then I verify the fields set for "ParentFilter" 
	
Scenario: Verify saved filter functionality without saving by closing the popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I set parent name as "ParentFilter" 
	And I "close" the filter "FGFilterClose" by clicking on save as button 
	Then I should not see filter with name "FGFilterClose" 
	And I log out of HarmonyMTCM 
	
Scenario: Verify Saved Filter delete functionality on Manage PG 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I set parent name as "ParentFilter" 
	And I "save" the filter "ParentFilter" by clicking on save as button 
	And I select "ParentFilter" on "Saved Filters" Combobox 
	# selecting this filter to refresh the combobox and to select Manage filter on next step
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I "delete" "ParentFilter" on Manage Filters 
	And I wait till the page loads for "10" seconds 
	Then I should not see filter with name "ParentFilter" 
	And I log out of HarmonyMTCM 
	
Scenario: Verify Saved Filter cancel button on delete manage filter (PG) popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I set parent name as "ParentFilter" 
	And I "save" the filter "ParentFilter" by clicking on save as button 
	And I select "ParentFilter" on "Saved Filters" Combobox 
	And I "cancel" "ParentFilter" on Manage Filters 
	And I select "ParentFilter" on "Saved Filters" Combobox 
	# --> Hence verifying saved filter as the filter can be selected only if it is saved
	
Scenario: Verify Saved Filter X button on delete manage filter (PG) popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I select "Yes" on "Show Group Without Parent" Combobox 
	And I set parent name as "ParentFilter" 
	And I "save" the filter "ParentFilter" by clicking on save as button 
	And I select "ParentFilter" on "Saved Filters" Combobox 
	And I "close" "ParentFilter" on Manage Filters 
	And I select "ParentFilter" on "Saved Filters" Combobox 
	# --> Hence verifying saved filter as the filter can be selected only if it is saved
	
Scenario: Saved Filter check -> get an existing saved filter and check for text fields data loaded 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I select "FVCC" on "Saved Filters" Combobox 
	Then I verify the fields set for "FVCC" 
	
Scenario: Select from Manage Filters ,click on '->' button and verify whether data loaded 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I click on arrowNavigatorButton for the Filter "FVCC" 
	Then I verify the fields set for "FVCC" 
	#-> it should be landed on filter search page with saved details
	
Scenario: Manage filters -> name edit for PG(only possible to edit the name of Saved Filter) 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group"
	And I clear any testData uncleared from "Parent" SavedFilter "ABC1"
	When I navigate to "Supply Collaboration" -> "Manage Parent Group"
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I edit Filter "ABC11" to "ABC12" 
	And I "close" "FGFilterXBtn" on Manage Filters 
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I select "ABC12" on "Saved Filters" Combobox 
	And I wait till the page loads for "10" seconds 
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I edit Filter "ABC12" to "ABC11" 
	And I "close" "FGFilterXBtn" on Manage Filters 
	# Note always check test data deletion for this test if this test fail
	
Scenario Outline: Verify pagesize selection and no of rows on the popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I click on multiple search "parentGroupNames" icon 
	And I select "<pageSize>" on "Page Size" Combobox on the popup 
	Then I verify "<pageSize>" rows listed after search on the popup 
	And I click on "Close" Button on the popup 
	Examples: 
		|pageSize|
		|1		 |
		|50		 |
		|10		 |
		
#Scenario Outline: Upload Parent Group with differnet groupType that of FG and verify error messages 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
#	#Testdata clearance if clearance failed on prev case
#	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
#	And I click on "Clear" Button 
#	And I enter "TestParent12" on "parentName" textfield 
#	And I click on "Apply" Button 
#	And I wait till the page loads for "5" seconds 
#	And I click on the name "TestParent12" 
#	Then I should be landed on "Edit TestParent12" page 
#	When I enter "TestParent12" on "parentGroupName" textfield 
#	And I click on the save button 
#	Then I verify the "Parent Group Saved" successful message 
#	#testdata clearance over here
#	When I navigate to "Upload/Manage Jobs" -> "Supply Allocation" 
#	And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>" 
#	Examples: 
#		|dataFile						 							|fileName     	|action			|errMsg									     									 |msgType |
#		|ParentFunctionalGroupUploadUI	 	|ParentUpload		|groupType	|Functional Group: ABAY_R191G Type and Parent  |error   |
#		

Scenario Outline: Upload Parent Group to add new FG and remove FG , verify success messages and verify on UI 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	#Testdata clearance if clearance failed on prev case
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I enter "TestParent12" on "parentName" textfield 
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds 
	And I click on the name "TestParent12" 
	Then I should be landed on "Edit TestParent12" page 
	When I enter "TestParent12" on the "parentGroupName" textfield 
	And I click on the save button 
	Then I verify the "Parent Group Saved" successful message 
	#testdata clearance over here
	When I navigate to "Upload/Manage Jobs" -> "Supply Allocation" 
#	And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>" 
	#Above step will add new FG
#	Then I remove the added FG via parent upload 
#	Examples: 
#		|dataFile						 |fileName      |action		    |msg      |msgType |
#		|ParentFunctionalGroupUploadUI	 |ParentUpload	|addFG      	|         |success |
#		
@skip
Scenario Outline: Upload Parent Group with rename value and verify success message then verify new name on UI 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	#Testdata clearance if clearance failed on prev case
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I enter "TestParent12" on "parentName" textfield 
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds 
	And I click on the name "TestParent12" 
	Then I should be landed on "Edit TestParent12" page 
	When I enter "TestParent12" on the "parentGroupName" textfield 
	And I click on the save button 
	Then I verify the "Parent Group Saved" successful message 
	#testdata clearance over here
	When I navigate to "Upload/Manage Jobs" -> "Supply Allocation" 
	And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>" 
	Then I verify the rename on the Manage Parent page 
	Examples: 
		|dataFile						         |fileName                |action	|msg	|msgType |
		|ParentFunctionalGroupConfigUploadUI	 |ParentUploadForDelete   |RENAME    |		|success |
		
#Scenario: Search with Multiple Parent Groups on Manage PG 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
#	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
#	And I click on "Clear" Button 
#	And I enter "MASS_CHECK" and "ABC_MASS_PARENT" on Multiple "parentGroupNames" textfield 
#	And I click on "Apply" Button 
#	And I wait till the page loads for "10" seconds 
#	Then I verify "4" rows listed as "Multiple parentGroupNames" search results 
	
Scenario: Search with Multiple Parent Groups by clicking on Icon on Manage PG 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I click on multiple search "parentGroupNames" icon 
	And I wait till the page loads for "20" seconds 
	And I select "2" "parentGroupNames" and "confirm" the list on the popup 
	Then I verify the selected names on the multiple "parentGroupNames" textfield 
	
#scplatform-4871 Manage Parent Group: Cannot add other FG right after PG has been created.
Scenario: Verify more than 1 newly created FG can be added to the parent created 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait "200" seconds
#	And I wait till the page loads for "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "FGrp1" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	And I click on "Back" Button 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "FGrp2" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set group name as "FGrp1" 
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds 
	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Parent Group" Button 
	And I save the parent group "PG-CFG" 
	Then I verify the "Parent Group Saved" successful message 
	And I click on the "Add Functional Group" Button 
	And I set "FGrp2" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "parentName" and "confirm" the list on the popup 
	And I save the parent group 
	Then I verify the "Parent Group Saved" successful message 
	And I log out of HarmonyMTCM 

@skip
Scenario: Download parent group and verify details 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin3" 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	#And I set group name as "FGrp1" 
	And I set parent name as "PG-CFG"
	And I click on "Apply" Button 
	And I wait "50" seconds
	Then I get and verify the PG "PG-CFG" search results data from UI
	And I click on PG Download Button and verify the results
