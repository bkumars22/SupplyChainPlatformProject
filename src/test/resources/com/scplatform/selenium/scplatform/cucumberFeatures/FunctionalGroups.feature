@HarmonyFG 
Feature: Functional Groups Workflow Test Plan 

#scplatform-4688 Wrong max char limit has mentioned in error message.
 Scenario: Create a functional Group with name max 255 chars, verify more than 255 length is truncated
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "45" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "random" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 

#PDSUPPORT-3487
#scplatform-4637	Scroll bar is missing after adding responsibility to the items   
  @skip
  Scenario: Create a functional Group assigning responsibility to items added 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "TestFG" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	And I enter "Test Descp" on the "functionalGroupDescription" textfield 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	Then I verify "Test Descp" on "functionalGroupDescription" textfield
	
Scenario Outline: Verify page jumps, page size on the list search page -> verify prev btn for frst page n next btn for las page r disabled 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "100" seconds 
	And I select "<pageSize>" on "Page Size" Combobox 
	And I wait till the page loads for "25" seconds 
	Then I verify "<pageSize>" rows listed as "pagesize selection" search results 
	Examples: 
		|pageSize|
		|1		 |
		|50		 |
		|70		 |
		|100	 |
		|10		 |
		
Scenario: Verify page jumps on the list search page #verify prev btn for frst page n next btn for las page r disabled 
		Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "200" seconds 
	And I enter "3" on "pagenum" textfield 
	And I click on "Jump" Button 
	Then I verify page "3" out of total pages under the list 
	
Scenario Outline: Search and verify a group name(also search with * appended on grpname) then navigate to edit group and later show History page 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "<grpName>" 
	And I click on "Apply" Button 
	And I click on the name "QAAUTTEST" 
	Then I should be landed on "Edit Group QAAUTTEST" page 
	When I click on "Show History" Button 
	Then I should be landed on Audit History page 
	Examples: 
		|grpName|
		|QAAUTTEST	|
		|QAAUTTEST*	|
		
Scenario Outline: Search for a non existing group Name , grp name with spec chars(not allowed) and verify the message 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "<grpName>" 
	And I click on "Apply" Button 
	Then I verify "No records found to display" message 
	Examples: 
		|grpName|	
		|TEST_GROUP	|
		|TEST-GROUP	|	
		
Scenario: Create a functional Group, assign To group functionality for items responsibility is not "production" - ie to assign responsibility for item added		
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "TestFGAuto" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	Then I search and verify "2" items added to the created Group "TestFGAuto" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I wait "90" seconds 
	And I click on "Apply" Button 
	And I wait "30" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Assign To Group" Button 
	And I "assign" the items to the Functional Group "TestFGAuto" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	And I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestFGAuto" 
	And I click on "Apply" Button 
	And I wait "90" seconds  
	Then I verify search filter results are displayed 
	And I log out of HarmonyMTCM 
	
	#scplatform-4801 The allowable special character (%) currently cannot used for Group Name.
Scenario: Create a Functional Group , edit by changing the group name, search for old grp name 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait "90" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "TestFGEditGrpName" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	And I edit the Functional Group name as "newTestFG%" 
	And I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	Then I search and verify "2" items added to the created Group "newTestFG%" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestFGEditGrpName" 
	And I click on "Apply" Button 
	Then I verify "No records found to display" message 
	And I log out of HarmonyMTCM 
	
@skip
Scenario: Edit by Adding a parent to Functional Group by searching on the add parent search icon 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "QAAUTTEST" 
	And I click on "Apply" Button 
	And I click on the name "QAAUTTEST" 
	Then I should be landed on "Edit Group QAAUTTEST" page 
	When I click on the "search" icon to trigger the popup 
	And I set "FG001C" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "parentName" and "confirm" the list on the popup 
	And I "update" the items to the Functional Group "QAAUTTEST" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	
Scenario: Search for a group name , click on item number and verify the details on the popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "QAAUTTEST" 
	And I click on "Apply" Button 
	And I wait "20" seconds
	And I click on the "itemNumber" link to trigger the popup on "FG" 
	And I wait "20" seconds
	Then I verify the item details on the popup 
	
Scenario: Edit a Functional Group by creating/removing parent name and search with the parent on filters 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds 
	And I click on the name "TestingGroupNow" 
	And I click on "delete" parent for the group 
	And I click on "Create" parent for the group 
	And I save parent name as "createFGParent" 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "20" seconds 
	And I verify "Parent Functional Group" column has "createFGParent" as value displayed under search results for all rows
#	Then I verify parent "createFGParent" belongs to "TestingGroupNow" group on "FG" Page 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "8" seconds 
	Then I verify "Parent Functional Group" column has "createFGParent" as value displayed under search results for all rows
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "8" seconds 
	And I click on the name "TestingGroupNow" 
	When I wait till the page loads for "8" seconds 
	Then I verify the "createFGParent" value selected on the "Parent" comboBox 
	When I wait till the page loads for "8" seconds 
	And I click on "delete" parent for the group 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	Then I verify the "none" value selected on "Parent" comboBox 
	
Scenario: Delete a Functional Group from parent by searching on manage parent grp page -> edit, verify on Manage parent page and manage fg grp (and)-> edit page 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "20" seconds 
	And I click on the name "TestingGroupNow" 
	And I wait till the page loads for "8" seconds 
	And I click on "delete" parent for the group 
	And I click on "Create" parent for the group 
	And I save parent name as "DeleteParentFromFG" 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I set parent name as "DeleteParentFromFG" 
	And I click on "Apply" Button 
	And I wait till the page loads for "20" seconds 
	And I click on the name "DeleteParentFromFG" 
	And I wait till the page loads for "8" seconds 
	And I remove the FG "TestingGroupNow" from the parent edit page 
	And I save the parent group 
	Then I verify the "Parent Group Saved" successful message 
	And I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "20" seconds 
	And I click on the name "TestingGroupNow" 
	And I wait till the page loads for "8" seconds 
	Then I verify the "none" value selected on "Parent" comboBox 
	
	@skip
	Scenario: Edit a Functional Group by searching and adding parent name from the popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "20" seconds 
	And I click on the name "TestingGroupNow" 
	And I click on "delete" parent for the group 
	And I click on "search" parent for the group 
	And I set "FG001C" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "parent" and "confirm" the list on the popup 
	Then I verify the "FG001C" value selected on "Parent" comboBox 
	When I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "20" seconds 
	And I click on the name "TestingGroupNow" 
	And I wait till the page loads for "8" seconds 
	Then I verify the "FG001C" value selected on "Parent" comboBox 
	When I click on "delete" parent for the group 
	Then I verify the "none" value selected on "Parent" comboBox 
	When I save the FG 
	
Scenario: Edit a Functional Group by adding more than one parent name and verify the error message (try to add two different parent) 
	Create one parent and try toadd another parent by find and search from the popup
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "2" seconds 
	And I click on the name "TestingGroupNow" 
	And I wait till the page loads for "5" seconds 
	And I click on "Create" parent for the group 
	And I save parent name as "TestMultipleParent" 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	When I click on "Create" parent for the group 
	And I save parent name as "TestMultipleParentNew" 
	Then I verify the "Functional Group can't have multiple Parent" warning message 
	
	#scplatform-4609 CSR06667675 - S3 - Error when adding part to a CFG
Scenario: Edit a Functional Group by creating duplicate parent name and verify the error message 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	And I wait till the page loads for "2" seconds 
	And I click on the name "TestingGroupNow" 
	And I wait till the page loads for "5" seconds 
	When I click on "delete" parent for the group
	And I click on "Create" parent for the group 
	And I save parent name as "TESTDelete" 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	When I click on "delete" parent for the group 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	When I click on "Create" parent for the group 
	And I save parent name as "TESTDelete" 
	Then I verify the "Parent Functional Group With Same name already exists" warning message 
#	And I save parent name as "testdelete" 
#	Then I verify the "Parent Functional Group With Same name already exists" warning message 
	
	##Scenario: Edit a grp by creating a parent name with diff type and verify the error message
	#Scenario : Search with created parent name (under fg edit) on Manage parent filter and search for grp name and verify parent name
	
#Scenario: Verify search , edit Functional Group by changing inactive -active status 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set group name as "QAAUTTEST" 
#	And I click on "Apply" Button 
#	And I wait till the page loads for "50" seconds
#	And I click on the name "QAAUTTEST" 
#	Then I should be landed on "Edit Group QAAUTTEST" page 
#	When I "check" the "status" checkbox 
#	And I "update" the items to the Functional Group "QAAUTTEST" with "Save All" 
#	Then I verify the "Functional Group Saved" successful message 
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set group name as "QAAUTTEST" 
#	And I select "Inactive" on "CFG Status" Combobox 
#	And I click on "Apply" Button 
#	And I click on the name "QAAUTTEST" 
#	Then I verify "status" checkbox status as "checked" 
#	When I "uncheck" the "status" checkbox 
#	And I "update" the items to the Functional Group "QAAUTTEST" with "Save All" 
#	Then I verify the "Functional Group Saved" successful message 
#	Then I verify "status" checkbox status as "unchecked" 
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set group name as "QAAUTTEST" 
#	And I select "Active" on "CFG Status" Combobox 
#	And I click on "Apply" Button 
#	And I click on the name "QAAUTTEST" 
#	Then I verify "status" checkbox status as "unchecked" 
#	
Scenario Outline:  Try saving group creation with group name as null and with non-allowed chars 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And  I wait "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I try to create the FG "<grpName>" GroupName 
	Then I verify "<errorMsg>" message on PopUp 
	Examples: 
		|grpName	|errorMsg|
		|			|Functional Group Name Shouldn't empty|
		|Test*12	|The name of CFG can only contain alphanumeric|
		|Test<12_	|The name of CFG can only contain alphanumeric|
		|Test=34}	|The name of CFG can only contain alphanumeric|
		###, space, -._,(){}[]+"#:% allowed characters|
		
Scenario: Click on back button on create group page and verify warning popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I select an item and enter groupName later click on back button 
	Then I verify "Your changes have not been saved." message on PopUp 
	And I log out of HarmonyMTCM 
	
Scenario: Click on back button on create group page and verify return page and entered data lost 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait "150" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I select an item and enter groupName later click on back button 
	And I "accept" the confirm popup 
	Then I should be landed on "Manage Functional Group" page 
	When I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	Then I verify the entered data have lost without saving 
	And I log out of HarmonyMTCM 
	
Scenario: Click on back button on create group page and reject popup and entered data should be preserved 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "200" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I wait till the page loads for "30" seconds
	And I select an item and enter groupName later click on back button 
	And I "reject" the confirm popup 
	Then I should be landed on "Create Group" page 
	And I verify the entered data have not lost 
	And I log out of HarmonyMTCM 
	
	@skip
	Scenario: Edit -> remove without selecting an item and verify the error message 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And  I wait "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "FGRemoveItemWitOutSelect" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	And I try to removeItem without selecting a group item 
	Then I verify "No Records Selected" message on PopUp 
	And I log out of HarmonyMTCM 
	
	@skip
	Scenario: Try creating group with same existing Functional Group name (duplicate checking) 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "TestFGDuplicate" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "TestFGDuplicate" with "Save All" 
	Then I verify "Functional Group With Same name already exists" error message 
	And I log out of HarmonyMTCM 
		


#Scenario: Search with Multiple Group Names on Manage FG 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I enter "TestMultipleFG1" and "TestMultipleFG2" on Multiple "groupNames" textfield 
#	And I click on "Apply" Button 
#	And I wait till the page loads for "20" seconds 
#	Then I verify search filter results are displayed 
#	And I log out of HarmonyMTCM 
	
Scenario: Search with Multiple Item Numbers on Manage FG 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I enter "10001" and "10002" on Multiple "itemNumbers" textfield 
	And I click on "Apply" Button 
	And I wait "20" seconds 
	Then I verify search filter results are displayed
	And I log out of HarmonyMTCM 
	
Scenario Outline: Search multiple itemNumbers, groupNames by clicking "search icon" and select multiple items 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I click on multiple search "<textField>" icon 
	And I wait till the page loads for "15" seconds 
	And I select "2" "<textField>" and "confirm" the list on the popup 
	Then I verify the selected names on the multiple "<textField>" textfield 
	Examples: 
		|textField|
		|itemNumbers|
		|groupNames|
		
Scenario Outline: Search multiple items, groups and check clear btn funcionality 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I click on multiple search "<textField>" icon 
	And I wait till the page loads for "45" seconds 
	And  I set "xyz" on Find textField on popup 
	Then I verify "xyz" on "<popUPTextField>" textField on popup
	And I click on "Clear" Button on the popup 
	Then I verify "" on "<popUPTextField>" textField on popup
	When I click on "Close" Button on the popup 
	Then I should be landed on "Manage Functional Group" page 
	Examples: 
		|textField	|popUPTextField|
		|itemNumbers|itemNumber		 |
		|groupNames	|name					 |
		
		
Scenario Outline: Search multiple items, groups and search for a value on popup and confirm 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I click on multiple search "<textField>" icon 
	And I set "<searchString>" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "2" "<textField>" and "confirm" the list on the popup 
	Then I verify the selected names on the multiple "<textField>" textfield 
	Examples: 
		|searchString		|textField  |
		|1000						|itemNumbers|
		|TestFG 	 			|groupNames	|
		
Scenario Outline: Verify pagesize selection and no of rows on the popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I click on multiple search "groupNames" icon 
	And I select "<pageSize>" on "Page Size" Combobox on the popup 
	Then I verify "<pageSize>" rows listed after search on the popup 
	And I click on "Close" Button on the popup 
	Examples: 
		|pageSize|
		|1		 |
		|50		 |
		|70		 |
		|100	 |
		|10		 |
		
Scenario: Verify page jumps on the list search page on the popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I click on multiple search "groupNames" icon 
	And I set "3" on "pagenum" textField on popup 
	And I click on "Jump" Button on the popup 
	Then I verify page "3" out of total pages under the list on the popup 
	When I click on "Close" Button on the popup 
	Then I should be landed on "Manage Functional Group" page 
	
Scenario: Verify clear button on Manage FG 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestFG" 
	And I enter "10001" and "10002" on Multiple "itemNumbers" textfield 
	And I enter "TestFG1" and "TestFG2" on Multiple "groupNames" textfield 
	And I click on "Clear" Button 
	Then I verify the data entered are cleared 
	And I log out of HarmonyMTCM 
	
Scenario: Verify Saved Filter functionality using saveAs btn on Manage Functional Group 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I set group name as "FGFilter" 
	And I "save" the filter "FGFilter" by clicking on save as button 
	And I select "FGFilter" on "Saved Filters" Combobox 
	Then I verify the fields set for "FGFilter" 
	
Scenario: Verify saved filter functionality without saving by closing the popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I set group name as "FGFilter" 
	And I "close" the filter "FGFilterClose" by clicking on save as button 
	Then I should not see filter with name "FGFilterClose" 
	And I log out of HarmonyMTCM 
	
Scenario: Verify Saved Filter delete functionality 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I set group name as "FGFilter" 
	And I "save" the filter "FGFilter" by clicking on save as button 
	And I select "FGFilter" on "Saved Filters" Combobox 
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I "delete" "FGFilter" on Manage Filters 
	And I wait "10" seconds 
	Then I should not see filter with name "FGFilter" 
	And I log out of HarmonyMTCM 
	
Scenario: Verify Saved Filter cancel button on delete popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I set group name as "FGFilter" 
	And I "save" the filter "FGFilter" by clicking on save as button 
	And I select "FGFilter" on "Saved Filters" Combobox 
	And I "cancel" "FGFilter" on Manage Filters 
	And I select "FGFilter" on "Saved Filters" Combobox 
	# --> Hence verifying saved filter as the filter can be selected only if it is saved
	
Scenario: Verify Saved Filter X button on delete popup 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I set group name as "FGFilter" 
	And I "save" the filter "FGFilter" by clicking on save as button 
	And I select "FGFilter" on "Saved Filters" Combobox 
	And I "close" "FGFilter" on Manage Filters 
	And I select "FGFilter" on "Saved Filters" Combobox 
	# --> Hence verifying saved filter as the filter can be selected only if it is saved
	
Scenario: Saved Filter check -> get an existing saved filter and check for text fields data loaded 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	And I clear any testData uncleared from "Functional" SavedFilter "FG0H6_LBL"
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I select "FG0H6_LBL" on "Saved Filters" Combobox 
	Then I verify the fields set for "FG0H6_LBL" 
	
Scenario: Select from Manage Filters ,click on '->' button and verify whether data loaded 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I click on arrowNavigatorButton for the Filter "FG0H6_LBL" 
	Then I verify the fields set for "FG0H6_LBL" 
	#-> it should be landed on filter search page with saved details
	
Scenario: Manage filters -> name edit for FG(only possible to edit the name of Saved Filter) 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	And I clear any testData uncleared from "Functional" SavedFilter "RTT"
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I edit Filter "RTT" to "RTT12" 
	And I "close" "FGFilterXBtn" on Manage Filters 
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I select "RTT12" on "Saved Filters" Combobox 
	And I wait till the page loads for "10" seconds 
	And I select "Manage Filters" on "Saved Filters" Combobox 
	And I edit Filter "RTT12" to "RTT" 
	And I "close" "FGFilterXBtn" on Manage Filters 
	# Note always check test data deletion for this test if this test fail
	
#Scenario Outline: Upload FG with invalid values and verify error messages 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#	##TestData clearance due to prev case if clearnce failed there
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set group name as "FGUpload" 
#	And I click on "Apply" Button 
#	And I wait till the page loads for "10" seconds 
#	And I click on the name "FGUpload" 
#	Then I should be landed on "Edit Group FGUpload" page 
#	When I enter "FGUpload" on the "functionalGroupName" textfield 
#	When I save the FG 
#	#TestData clearance ends here
#	When I navigate to "Upload/Manage Jobs" -> "Supply Allocation" 
#	And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<errMsg>" "<msgType>" 
#	Examples: 
#		|dataFile						 |fileName  |action	|errMsg									    |msgType |
#		# 	|Functional Group Item (*.xls)	 |FGUPload	|XLOB	|Platform does not exist for XLOB type		|error	 |
#		# 	|Functional Group Item (*.xls)	 |FGUPload	|NFG	|Platform does not exist for NFG type		|error	 |
#		|FunctionalGroupItemUploadUI	 |FGUPload	|No item|Item does not exist for item				|error	 |
		
Scenario Outline: Upload Functional Group with rename values and verify success message, then verify new name on UI 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	#TestData clearance due to prev case if clearnce failed there
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I enter "FGUpload" on "functionalGroupName" textfield 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds 
	And I click on the name "FGUpload" 
	Then I should be landed on "Edit Group FGUpload" page 
	When I enter "FGUpload" on the "functionalGroupName" textfield 
	When I save the FG 
	#TestData clearance ends here
	When I navigate to "Upload/Manage Jobs" -> "Supply Allocation" 
	And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>" 
#	Then I verify the "<action>" on the Manage FG page 
	Examples: 
		|dataFile						 |fileName   |action	|msg	|msgType |
		|FunctionalGroupConfigUploadUI	 |FGUpload   |RENAME    |		|success |
		
Scenario Outline: Upload Functional Group to change the FG status values and verify success message, then verify status on UI 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Upload/Manage Jobs" -> "Supply Allocation" 
	And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>" 
	Then I verify the "<action>" on the Manage FG page 
	Examples: 
		|dataFile											 |fileName   |action		|msg	|msgType |
		|FunctionalGroupItemUploadUI	 |FGUpload   |Inactive  |			|success |
		
	#PDSUPPORT-9650 CSR07372891 - One Cost Prod - Same item assigned to 2 CFGs with same name - H65KM
#	Scenario: Create a functional Group and edit to add an item from Add Item Search popup and then assigning responsibility to items added 
#			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#			When I navigate to "Upload/Manage Jobs" -> "Admin" 
#			And I upload the "ItemAVLUI" "ItemUploadForSearchItem" with "uploadItemForValidation" & verify "" "success" 
#			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#			And I click on "Clear" Button 
#			And I select "Yes" on "Show Item Without Group" Combobox 
#			And I set the itemNumber as "FGITEM" 
#			And I click on "Apply" Button 
#			And I wait till the page loads for "10" seconds 
#			And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
#			And I click on "Create Group" Button 
#			And I "create" the items to the Functional Group "testFGItemEdit" with "Save All" 
#			Then I verify the "Functional Group Saved" successful message 
#			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#			And I click on "Clear" Button 
#			And I set the itemNumber as "FGITEM" 
#			And I click on "Apply" Button 
#			And I wait "10" seconds
#			Then I verify "1" rows listed with checkbox name "selectedPageKeys" 
#			And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
#			And I click on "Create Group" Button 
#			When I click on the "Add Item" Button 
#			And I wait till the page loads for "30" seconds 
#			And I set "FGITEM1" on Find textField on popup 
#			And I click on "Search" Button on the popup 
#			And I select "1" "itemNumbers" and "confirm" the list on the popup 
#			When I click on the "Add Item" Button 
#			And I wait till the page loads for "30" seconds 
#			And I set "FGITEM2" on Find textField on popup 
#			And I click on "Search" Button on the popup 
#			And I select "1" "itemNumbers" and "confirm" the list on the popup
#			And I "create" the items to the Functional Group "testFGItemEdit" with "Save All" 
#			Then I verify "Functional Group With Same name already exists" error message 
#			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#			And I click on "Clear" Button 
#			And I set the itemNumber as "FGITEM" 
#			And I click on "Apply" Button 
#			And I wait till the page loads for "1" seconds
#			Then I verify "1" rows listed with checkbox name "selectedPageKeys" 
#			And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
#			And I click on "Create Group" Button 
#			When I click on the "Add Item" Button 
#			And I wait till the page loads for "30" seconds 	
#			And I set "FGITEM3" on Find textField on popup 
#			And I click on "Search" Button on the popup 
#			And I select "1" "itemNumbers" and "confirm" the list on the popup
#			And I "create" the items to the Functional Group "testFGItemEdit" with "Save All" 
#			Then I verify "Functional Group With Same name already exists" error message 
#			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#			And I click on "Clear" Button 
#			And I set the itemNumber as "FGITEM" 
#			And I click on "Apply" Button 
#			And I wait till the page loads for "1" seconds
#			Then I verify "1" rows listed with checkbox name "selectedPageKeys" 
#			And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
#			And I click on "Create Group" Button 
#			When I click on the "Add Item" Button 
#			And I wait till the page loads for "30" seconds 	
#			And I set "FGITEM4" on Find textField on popup 
#			And I click on "Search" Button on the popup 
#			And I select "1" "itemNumbers" and "confirm" the list on the popup
#			And I "create" the items to the Functional Group "testFGItemEdit" with "Save All" 
#			Then I verify "Functional Group With Same name already exists" error message 
#			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#			And I click on "Clear" Button 
#			And I set the itemNumber as "FGITEM" 
#			And I click on "Apply" Button 
#			And I wait till the page loads for "1" seconds
#			Then I verify "1" rows listed with checkbox name "selectedPageKeys" 
#			
			  #PDSUPPORT-25433 10802193 - UCM MBI-13: Regression responsibility column is auto expanded
#	Scenario: Search and verify a group name(also search with * appended on grpname) then navigate to edit group and later show History page 
#			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#			And I click on "Clear" Button 
#			And I set group name as "testFGItemEdit" 
#			And I click on "Apply" Button 
#			And I wait "30" seconds  
#			Then I verify responsibility field is not expanded


#Commenting Below test case as per discussion with dev team and they have removed this functionality
				#Note: Below tests are dependent on this test
		#Scenario: CSR06919481 - One Cost - Duplicate item listed in Manage Functional Group and User is restricted in Commodity profile in Manage Allocation
			#validation to stop user adding same item twice
			#Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"	
			#When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
			#And I click on "Clear" Button 
			#And I set group name as "testFGItemEdit" 
			#And I click on "Apply" Button 
			#And I wait till the page loads for "10" seconds 
			#And I click on the name "testFGItemEdit" 
			#And I click on "Create" parent for the group 
			#When I click on the "Add Item" Button 
			#And I wait till the page loads for "30" seconds 
			#And I set "FGITEM" on Find textField on popup 
			#And I click on "Search" Button on the popup 
			#And I select "1" "itemNumbers" and "confirm" the list on the popup 
			#Then I verify the "Error While adding these items as Item already present" warning message
	
#		Scenario: Try to create FG with item belongs to another FG and verify the erorr message on FG Edit page via search popup
#			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"	
#			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#			And I click on "Clear" Button 
#			And I select "Yes" on "Show Item Without Group" Combobox 
#			And I click on "Apply" Button 
#			And I wait till the page loads for "10" seconds 
#			And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
#			And I click on "Create Group" Button 
#			When I click on the "Add Item" Button 
#			And I wait till the page loads for "30" seconds 
#			And I set "FGITEM" on Find textField on popup 
#			And I click on "Search" Button on the popup 
#			And I wait "100" seconds
#			And I select "1" "itemNumbers" and "confirm" the list on the popup 
#			Then I verify the "Error While adding these items as Item already present" warning message
#			
			@skip
			Scenario Outline: Upload item already assigned to a Functional Group to verify warning message
			#scplatform-4609 CSR06667675 - S3 - Error when adding part to a CFG
			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
			When I navigate to "Upload/Manage Jobs" -> "Supply Allocation" 
			And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>" 
			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
			And I click on "Clear" Button 
			And I set the itemNumber as "FGITEM" 
			And I click on "Apply" Button 
			And I wait till the page loads for "1" seconds
			Then I verify "1" rows listed with checkbox name "selectedPageKeys" 
			Examples: 
				|dataFile											 |fileName   |action			|msg																					|msgType |
				|FunctionalGroupItemUploadUI	 |FGUPload   |AddDupItem  |already present in the Functional Group			|error	 |
		
		#scplatform-4609 CSR06667675 - S3 - Error when adding part to a CFG
	Scenario: Upload edit a Functional Group by creating case insensitive fg Name 
			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
			When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
		  And I upload the "FunctionalGroupItemUploadUI" "uploadFG" xlsx with "caseSensitivefgDup" & verify "duplicate" "error"
		  
		#scplatform-4609 CSR06667675 - S3 - Error when adding part to a CFG
	Scenario: Upload edit a Functional Group using non existing item part 
			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
			When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
		  And I upload the "FunctionalGroupItemUploadUI" "uploadFG" xlsx with "existingFGWithNonExistingItem" & verify "already present in the Functional Group" "error"
		  
#	  #scplatform-4774 CSR06731054 - S3 - Prod - manage upload job. failed CFG upload - why no item number
#	Scenario Outline: Upload edit a Functional Group by creating duplicate parent name and verify the error message
#			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
#			When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
#		  And I upload the "FunctionalGroupItemUploadUI" "uploadFG" xlsx with "<action>" & verify "Item does not exist for item" "error"
#		  Examples:
#		  |action	|
#		  |ADD		|
#		  |UPDATE	|
#		  |DELETE	|
		  
#	Scenario: Try to create FG with item belongs to another FG and verify the erorr message on FG Edit page from search filters
#		Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"	
#		When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#		And I click on "Clear" Button 
#		And I set the itemNumber as "FGITEM" 
#		And I click on "Apply" Button 
#		And I wait "10" seconds 
#		And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
#		And I click on "Create Group" Button 
#		And I "create" the items to the Functional Group "CreateFGWitItemBelongsToAnotherFG" with "Save All" 
#		Then I verify the "Error While adding these items as Item already present" warning message
#	
#Scenario: Try to assign an item to an FG with Assign Group Functionality which belongs to another FG 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I select "Yes" on "Show Item Without Group" Combobox 
#	And I click on "Apply" Button 
#	And I wait "100" seconds 
#	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
#	And I click on "Create Group" Button 
#	And I "create" the items to the Functional Group "testFGItemAssign" with "Save All" 
#	Then I verify the "Functional Group Saved" successful message 
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set the itemNumber as "FGITEM" 
#	And I click on "Apply" Button 
#	And I wait "30" seconds 
#	And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
#	And I click on "Assign To Group" Button 
#	And I "assign" the items to the Functional Group "testFGItemAssign" with "Save All" 
#	Then I verify the "Error While adding these items as Item already present in" warning message 
#	
Scenario: Search for item assigned to an FG with Show items without group combobox set to Yes selection and verify message displayed for no serch results 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set the itemNumber as "FGITEM" 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "30" seconds 
	Then I verify "No records found to display, Refine your search in the filters" message 
	
#Scenario: Search for an item assigned to an FG with Show items without group combobox set to N/A and verify search results 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set the itemNumber as "FGITEM" 
#	And I click on "Apply" Button 
#	And I wait "100" seconds 
#	Then I verify search filter results are displayed 
#	
	#scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
Scenario: Verify search criteria retained after changing pages and return 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set the itemNumber as "testxyz" 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	Then I should be landed on "Manage Functional Group" page 
	Then I verify "No records found to display" message 
	When I navigate to "Supply Collaboration" -> "Manage Parent Group" 
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow" 
	And I click on "Apply" Button 
	Then I should be landed on "Manage Parent" page 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	Then I verify "testxyz" on "itemNumber" textfield 
	And I verify the "yes" value selected on "Show Item Without Group" comboBox 
	And I click on "Clear" Button 
	Then I verify "" on "itemNumber" textfield 
	And I verify the "" value selected on "Show Item Without Group" comboBox 
	
	#scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
Scenario: Verify search criteria retained after changing pages and return 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set the itemNumber as "testxyz" 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	Then I should be landed on "Manage Functional Group" page 
	Then I verify "No records found to display" message 
	And I log out of HarmonyMTCM 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	Then I should be landed on "Manage Functional Group" page 
	Then I verify "testxyz" on "itemNumber" textfield 
	And I verify the "yes" value selected on "Show Item Without Group" comboBox 
	And I click on "Clear" Button 
	And I click on "Apply" Button 
	And I wait till the page loads for "30" seconds 
	Then I should be landed on "Manage Functional Group" page 
	Then I verify "" on "itemNumber" textfield 
	And I verify the "" value selected on "Show Item Without Group" comboBox 
	And I log out of HarmonyMTCM 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	Then I should be landed on "Manage Functional Group" page 
	Then I verify "" on "itemNumber" textfield 
	And I verify the "" value selected on "Show Item Without Group" comboBox 
	
	@skip
	Scenario: Delete all items from an fg then search on search filter and verify there exists 1 results with null items 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestFGAuto" 
	And I click on "Apply" Button 
	And I click on the name "TestFGAuto" 
	And I wait "30" seconds
	And I select all rows 
	And I click on "Remove Item" Button 
	And I click "Yes" on the warning popup with message "Deleting Item from FG will delete Allocation for current Item" 
	And I save one "item" removed for verification 
	Then I verify the "deleted from FG" successful message 
	And I save the FG 
	Then I verify the "Functional Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "TestFGAuto" 
	And I click on "Apply" Button 
	Then I verify "1" rows listed with checkbox name "selectedPageKeys" 
	When I search with one of deleted "item" 
	Then I verify search filter results are displayed


	#scplatform-7549 Add Parent item and ODM Part in Manage functional group
  #scplatform-7460
	#scplatform-4852 Manage Functional group: The page getting hibernate when user click download icon
#Scenario: Download Fg and verify details 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set group name as "testFGItemEdit" 
#	And I click on "Apply" Button
#	And I wait "300" seconds 
#	Then I verify search filter results are displayed
#	And I get and verify the FG "testFGItemEdit" search results data from UI
	#And I click on FG Download Button and verify the results

		#CSR07209621 - OneCost prod 
		Scenario:  CFG Management screen does not exclude "Phantom" item type
			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#			When I navigate to "Main" -> "Upload" 
#			And I upload the "ItemAVLUI" "phantomItem" with "uploadPhantomItem" & verify "" "success" 
			When I navigate to "Search" -> "Item AVL"
			And I click on "Clear" Button 
			And I select "Phantom Item" on the ItemType list
			When I set the itemNumber as "JD002"
			And I click on "Apply" Button
			Then I verify search filter results are displayed
			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
			And I click on "Clear" Button 
			And I select "Yes" on "Show Item Without Group" Combobox 
			And I set the itemNumber as "JD002" 
			And I click on "Apply" Button 
			And I wait till the page loads for "10" seconds 
			Then I verify "Item Type" column has "Phantom Item" as value not displayed under search results for all rows	
			
	Scenario: scplatform-4184 [CSR06452227] Same supplier name appear twice for one item
			Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
			When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
			And I click on "Clear" Button 
			And I set the itemNumber as "071-000-036-04" 
			And I click on "Apply" Button 
			And I wait "30" seconds 
			And I select first "1" rows from the "selectedPageKeys" "checkbox" list 
		  And I verify "Suppliers" column has value "ACBEL POLYTECH INC" without duplication displayed under search results
 
 @skip
 Scenario: Create a functional Group , edit by removing an item using X button and verify 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And  I wait till the page loads for "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "FGEditRemoveItemXbtn" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	And I edit the Functional Group "FGEditRemoveItemXbtn" by removing item using X button 
	Then I verify the "Functional Group Saved" successful message 
	Then I verify "1" items listed for the Group "FGEditRemoveItemXbtn" 
	And I log out of HarmonyMTCM 
	
  @skip
  Scenario: Create a functional Group , edit by removing an item using remove button and verify 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait "30" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "FGEditRemoveItem" with "Save All" 
	Then I verify the "Functional Group Saved" successful message 
	And I edit the Functional Group "FGEditRemoveItem" by removing item 
	Then I verify the "Functional Group Saved" successful message 
	Then I verify "1" items listed for the Group "FGEditRemoveItem" 
	And I edit the Functional Group "FGEditRemoveItem" by removing item 
	Then I verify the "Functional Group Saved" successful message 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "FGEditRemoveItem" 
	And I click on "Apply" Button 
	Then I verify "1" rows listed with checkbox name "selectedPageKeys" 
	Then I verify "Group Name" column has "FGEditRemoveItem" as value displayed under search results for all rows
	Then I verify "Group Type" column has "CFG" as value displayed under search results for all rows
	Then I verify "Parent Functional Group" column has "" as value displayed under search results for all rows
	Then I verify "TAM Exist" column has "No" as value displayed under search results for all rows	
	And I log out of HarmonyMTCM 
	
	Scenario: Verify clear action clears multiple selection combobox values 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
#	And I select "Item" and "Supplier Item" on the ItemType list
	And I click on "Apply" Button 
	Then I verify search filter results are displayed
#	Then I verify "Item" and "Supplier Item" are selected on the ItemType list 
	And I click on "Clear" Button 
	Then I verify ItemType list selections are cleared
	
	
	Scenario: Create a functional Group Save All and Exit button and verifies page landed 
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Yes" on "Show Item Without Group" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "100" seconds 
	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
	And I click on "Create Group" Button 
	And I "create" the items to the Functional Group "newAutoTestFG" with "Save All And Exit" 
	And I wait till the page loads for "10" seconds 
	Then I should be landed on "Manage Functional Group" page 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "newAutoTestFG" 
	And I click on "Apply" Button 
	And I click on the name "newAutoTestFG" 
	Then I should be landed on "Edit Group newAutoTestFG" page 
	
	Scenario: Search a CFG based on CFG status and verify the results
	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I select "Active" on "CFG Status" Combobox 
	And I click on "Apply" Button 
	And I wait till the page loads for "45" seconds 
	Then I verify "Group Type" column has "CFG" as value displayed under search results for all rows
	Then I verify "Status" column has "ACTIVE" as value displayed under search results for all rows
	      
	
Scenario: Validate System should process Functional Group XML file and validate the data in the UI page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    When I navigate to "Administration" -> "Admin Upload"
    When I uploaded the XML file "CFG_TestNewest9381" for "FunctionalGroup" and checked it on the UI page
    	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
	And I click on "Clear" Button 
	And I set group name as "CFG_Test_2511" 
	And I click on "Apply" Button 
	And I click on the name "CFG_Test_2511" 
	And I wait "30" seconds
	Then I verify field name "Alias Group Name" is readonly
