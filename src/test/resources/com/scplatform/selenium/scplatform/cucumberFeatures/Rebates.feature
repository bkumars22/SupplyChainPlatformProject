@HarmonyR 
Feature: Rebates Workflow Test Plan 

Scenario Outline: Search rebate with different status 
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I select "<status>" on the Status list 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds 
	Then I verify search filter results are displayed 
	And I verify the search results status as "<status>" 
	Examples: 
		|status  |
		|Approved|
		|Closed	 |
		|Pending |	
		
#Scenario: Search rebate with closed status and verify the closed status and button states on Rebate program page 
#	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
#	When I navigate to "Rebates" -> "Search Rebate Program" 
#	And I click on "Clear" Button 
#	And I select "Closed" on the Status list 
#	And I click on "Apply" Button 
#	And I wait till the page loads for "10" seconds
#	Then I should see the checkbox fields are "disabled"
#	And I click on Edit icon on row "1"
#	Then I should be landed on "Rebate Program" page
#	And I verify the Status as "CLOSED" on "Rebates" page
#	Then I should see "history" icon is "displayed" in Rebate Program
#	And I should see the Save And Exit button is not displayed and enabled
#	And I should see the Save button is not displayed and enabled
#	And I should see the "Back" button should be displayed and enabled
#	When I click on the Back button
#	Then I should be landed on "Rebates" page
	
	Scenario: Search rebate with pending status and verify the pending status and button states on Rebate program page 
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I select "Pending" on the Status list 
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds
#	And I select first "1" rows from the "selectedPageKeys" "radio" list
	Then I should see the checkbox fields are "enabled"
	And I click on Edit icon on row "1"
#	Then I should be landed on "Rebate Program" page
	And I verify the Status as "PENDING" on "Rebates" page
	And I should see the "Back" button should be displayed and enabled
	And I should see the "Reject" button should be displayed and enabled
	And I should see the "Close" button should be displayed and enabled
   And I should see the "Approve" button should be displayed and enabled
   And I should see the Save button should be displayed and enabled

Scenario: Search rebate with Approved status and verify the approved status and button states on Rebate program page 
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button  
	And I select "Approved" on the Status list
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds
	Then I should see the checkbox fields are "enabled"
	And I click on Edit icon on row "1"	
#	Then I should be landed on "Rebate Program" page
	And I verify the Status as "APPROVED" on "Rebates" page
	And I should see the "Back" button should be displayed and enabled
	And I should see the "Edit" button should be displayed and enabled
	And I should see the "Close" button should be displayed and enabled
   #And I should see the Save And Exit button should be displayed and enabled
   And I should see the Save button should be displayed and enabled
	And I log out of HarmonyMTCM

Scenario: Verify New rebate button on Search REbates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "New Rebate" Button
	Then I should be landed on "Rebate Program" page
	And I verify the Status as "NEW" on "Rebates" page
	And I should see the "Submit" button should be displayed and enabled
	And I should see the "Close" button should be displayed and enabled
    #And I should see the Save And Exit button should be displayed and enabled
    And I should see the Save button should be displayed and enabled
    And I should see the "Back" button should not be displayed and disabled
	Then I should be landed on "Rebates" page
	
Scenario: Verify Clear button on Search REbates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I enter "testRebate" on "name" textfield
	And I enter "testFinanceOwner" on "financeOwner" textfield
	And I enter "testProgramOwner" on "programOwner" textfield
	And I click on "Clear" Button
	Then I verify fields cleared on Search Rebates page
	
Scenario: Search with rebates name on Search REbates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I enter "Realtec entry LOM" on "name" textfield
	And I click on "Apply" Button
	And I wait till the page loads for "10" seconds
	Then I verify the search results name as "Realtec entry LOM"
	
Scenario: Search for a pending rebates and approve on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I select "Pending" on the Status list 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds
	Then I should see the checkbox fields are "enabled"
	And I click on Edit icon on row "1"
	And I click on "Approve" Button
	Then I verify the "Changes saved successfully" successful message
	And I verify the Status as "APPROVED" on "Rebates" page
	And I click on "Save" Button
	#Then I verify the "Changes saved successfully" successful message
	
	Scenario: Search for a pending rebates and reject on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button  
	And I select "Pending" on the Status list 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds
  Then I should see the checkbox fields are "enabled"
	And I click on Edit icon on row "1"
	And I click on "Reject" Button
	Then I verify the "Changes saved successfully" successful message
	And I verify the Status as "REJECTED" on "Rebates" page
	And I click on "Save" Button
	#Then I verify the "Changes saved successfully" successful message
	
	Scenario: Search for a rejected rebate and submit on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button  
	And I select "Rejected" on the Status list 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds
  Then I should see the checkbox fields are "enabled"
	And I click on Edit icon on row "1"	
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	And I verify the Status as "PENDING" on "Rebates" page
	And I click on "Save" Button
	#Then I verify the "Changes saved successfully" successful message
	
	Scenario: Search for a approved rebates and close on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I select "Approved" on the Status list 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds
  #Then I should see the checkbox fields are "enabled"
	And I click on Edit icon on row "1"	
	Then I should see the "Close" button should be displayed and enabled
	And I should see the "Edit" button should be displayed and enabled
	And I should see the "Back" button should be displayed and enabled
	#And I should see the Save And Exit button should be displayed and enabled
	And I should see the Save button should be displayed and enabled
	
Scenario: Search for rebates and close on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button  
	And I select "Approved" on the Status list 
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds
#	Then I should see /the checkbox fields are "enabled"
	And I click on Edit icon on row "1"
	And I wait "5" seconds
	Then I should be landed on "Rebate Program" page
	And I should see "history" icon is "displayed" in Rebate Program
	When I click on the "history" icon in Rebate
	And I wait "10" seconds
#	Then I should be landed on Audit History page
	When I click on Close button on Audit History page
	And I wait "5" seconds
	Then I should be landed on "Rebate Program" page
Scenario: Create New rebate button on Search Rebates page and verify Agile and Proteus platforms are auto complete
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "createRebate" on the "rebateName" textfield
	And I set program end date to "10" days from today
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	And I click on the PRICING tab
	When I click on the RULES tab
	And I click on element with ID "newRule"
	And I click on element with ID "editRuleId"
	And I click on multiple search "platform" icon
	And I set "1 SOCKET 1U AMD ODM" on Find textField on popup
	And I select "10" on "Page Size" Combobox on the popup 
	And I select "PROTEUS" on "Platform Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	Then I verify Platform Name list is sorted on alphabetical order
	And I select "1" "platform" and "confirm" the list on the popup
	And I click on the Save Rule button
	And I select the "rulePlatformKeys" with value "1 SOCKET 1U AMD ODM (PROTEUS)"
	And I click on the "platformRemove" Button on Rules Tab
	And I click on the Save Rule button
	And I click on the save button
	Then I verify the "Changes saved successfully" successful message
	And I click on multiple search "platform" icon
	And I set "1220S" on Find textField on popup
	And I select "10" on "Page Size" Combobox on the popup 
	And I select "AGILE" on "Platform Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	Then I verify Platform Name list is sorted on alphabetical order
	And I select "1" "platform" and "confirm" the list on the popup
	And I click on the Save Rule button
	And I select the "rulePlatformKeys" with value "1220S (AGILE)"
	And I click on the "platformRemove" Button on Rules Tab
	And I click on the Save Rule button
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	And I verify the Status as "PENDING" on "Rebates" page
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button 
	And I enter "createRebate" on "name" textfield
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds
	And I click on Edit icon on row "1"
	Then I should be landed on "Rebate Program" page
	And I verify the Status as "PENDING" on "Rebates" page
	
	Scenario:  Verify Agile platforms are listed in the list while filtering with Agile
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button
	And I enter "PCBA Components-ESG-Cindy Lou-$30 LCAP program" on "name" textfield
	And I click on "Apply" Button 
	And I click on Edit icon on row "1"
	Then I should be landed on "Rebate Program" page
	When I click on the RULES tab
	And I click on element with ID "editRuleId"
	And I click on multiple search "platform" icon
	And I select "10" on "Page Size" Combobox on the popup 
	And I select "AGILE" on "Platform Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	Then I verify "Platform Type" column has value "AGILE" displayed under search results for all rows on popup

Scenario:  Verify PROTEUS platforms are listed in the list while filtering with PROTEUS
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button
	And I enter "PCBA Components-ESG-Cindy Lou-$30 LCAP program" on "name" textfield
	And I click on "Apply" Button 
	And I click on Edit icon on row "1"
	Then I should be landed on "Rebate Program" page
	When I click on the RULES tab
	And I click on element with ID "editRuleId"
	And I click on multiple search "platform" icon
	And I select "10" on "Page Size" Combobox on the popup 
	And I select "PROTEUS" on "Platform Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	Then I verify "Platform Type" column has value "PROTEUS" displayed under search results for all rows on popup

	Scenario:  Verify ALL platforms are listed in the list while filtering with All on Platform Type
	Given I log into HarmonyMTCM as "mtcmUser" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button
	And I enter "PCBA Components-ESG-Cindy Lou-$30 LCAP program" on "name" textfield
	And I click on "Apply" Button 
	And I click on Edit icon on row "1"
	Then I should be landed on "Rebate Program" page
	When I click on the RULES tab
	And I click on element with ID "editRuleId"
	And I click on multiple search "platform" icon
	And I select "10" on "Page Size" Combobox on the popup 
	And I select "ALL" on "Platform Type" Combobox on the popup
	And I click on "Search" Button on the popup 
#	Then I verify Platform Type column has values either AGILE or PROTEUS displayed for all rows on popup
	
Scenario: edit rebate , approve and Verify New rebate button on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I enter "createRebate" on "name" textfield
	And I click on "Apply" Button 
	And I wait till the page loads for "25" seconds
	And I click on Edit icon on row "1"
	Then I should be landed on "Rebate Program" page
	And I wait till the page loads for "10" seconds
	And I verify the Status as "PENDING" on "Rebates" page
	And I click on "Approve" Button
	Then I verify the "Changes saved successfully" successful message
	And I verify the Status as "APPROVED" on "Rebates" page
	And I click on the save button
	Then I verify the "Changes saved successfully" successful message	
	
	Scenario: Search for created rebates, approve and close and then verify on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "createRebates" on "rebateName" textfield
#	And I enter "2WIRE" on "businessEntityName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	And I verify the Status as "PENDING" on "Rebates" page
	And I click on the save button
	Then I verify the "Changes saved successfully" successful message	
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I enter "createRebates" on "name" textfield
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds
	And I click on Edit icon on row "1"	
	Then I should be landed on "Rebate Program" page
	And I verify the Status as "PENDING" on "Rebates" page
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I enter "createRebates" on "name" textfield
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds
	And I click on Edit icon on row "1"
	Then I should be landed on "Rebate Program" page
	And I verify the Status as "PENDING" on "Rebates" page
	And I click on "Approve" Button
	Then I verify the "Changes saved successfully" successful message
	And I verify the Status as "APPROVED" on "Rebates" page
	And I click on Close button
	And I click "Yes" on the warning popup with message "this action will set the Rebate Program status to CLOSED"
	Then I verify the "Changes saved successfully" successful message
	And I verify the Status as "CLOSED" on "Rebates" page
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I enter "createRebates" on "name" textfield
	And I click on "Apply" Button 
	And I wait till the page loads for "5" seconds
	And I click on Edit icon on row "1"
#	And I select first "1" rows from the "selectedPageKeys" "radio" list
	Then I should be landed on "Rebate Program" page
	And I verify the Status as "CLOSED" on "Rebates" page
	
	@skip
	Scenario: Try to create duplicate rebate and verify error message
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "createRebatesDup" on "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	#And I enter "2WIRE" on "businessEntityName" textfield
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "createRebatesDup" on "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	#And I enter "2WIRE" on "businessEntityName" textfield
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify "The Rebate Name is being used by another Rebate Program. Please use a different Rebate Name" error message 
	And I should see the "Reject" button should be displayed and enabled
	And I should see the "Close" button should be displayed and enabled
    And I should see the "Approve" button should be displayed and enabled
    #And I should see the Save And Exit button should be displayed and enabled
    And I should see the Save button should be displayed and enabled	
		
	Scenario: Try to create rebate without rebate name and verify error message
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "2WIRE" on "businessEntityName" textfield
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify "A rebate program name is required." message on PopUp 
	
	Scenario: Try to create rebate without rebate provider and verify error message
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "createRebates" on "rebateName" textfield
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify "A rebate provider is required." message on PopUp 
	
	Scenario: Try to create rebate without end date and verify error message
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "createRebates" on "rebateName" textfield
	And I click on "Submit" Button
	Then I verify "A program end date is required." message on PopUp 

Scenario: Verify wrong end date for new rebate creation
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "createRebates" on "rebateName" textfield
	And I enter "1DayBeforeDate" on the "programEndDate" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	And I click on "Submit" Button
	Then I verify the "The end date cannot be before the start date for cost record {0}" warning message

Scenario: Verify error message on trying to create same rebate under pricing tab
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "newRebate" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	#And I enter "2WIRE" on "businessEntityName" textfield
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	When I click on "Add" Button
	And I enter amount "10" on row "1"
	And I click on "Approve" Button
	Then I verify the "Changes saved successfully" successful message
	When I click on "Add" Button
	And I enter amount "10" on row "2"
	And I click on "Approve" Button
	Then I verify the "Errors exist, see highlighted fields below." warning message
	And I verify the highlighted errors on "From" date field on "2" row
		
Scenario Outline: Verify error message on trying to create rebate with invalid date on rebate program end date ie a day beyond program end date
	#program end date is set as 5 days from today	
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "Rebate" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
#	And I enter "5daysAfterDate" on the "programEndDate" textfield
	And I set program end date to "5" days from today
	# Entered range of program is from today to 5days from today as end date
	When I click on "Add" Button
	And I enter amount "10" on row "1"
	And I set end date as "<date>" days from start Date
	# rebate validity from today to 7 days (but rebate program is set from today to 5)
	And I click on "Submit" Button
	Then I verify the "Errors exist, see highlighted fields below." warning message
	And I verify the highlighted errors on "To" date field on "1" row
	Examples:
	|date	|
	|-7		| 
	|7		|
	# neg day indiactes some day before program start date as end date
	
Scenario: Verify error message on trying to create rebate with invalid date on rebate program start date ie 1 day before program start date
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "Rebate" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
#	And I enter "5daysAfterDate" on the "programEndDate" textfield
	And I set program end date to "5" days from today
	# Entered range of program is from today to 5days from today as end date
	When I click on "Add" Button
	And I enter amount "10" on row "1"
	And I set "1" day before program start state as start date
	# rebate validity from today to 7 days (but rebate program is set from today to 5)
	And I click on "Submit" Button
	Then I verify the "Errors exist, see highlighted fields below." warning message
	And I verify the highlighted errors on "From" date field on "1" row
	
Scenario: Verify error message on trying to create rebate with invalid date on rebate program start date ie 1 day after program end date
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "Rebate" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
#	And I enter "5daysAfterDate" on the "programEndDate" textfield
	And I set program end date to "5" days from today
	# Entered range of program is from today to 5days from today as end date
	When I click on "Add" Button
	And I enter amount "10" on row "1"
	And I set "1" days after rebate end state as start date
	# rebate validity from today to 7 days (but rebate program is set from today to 5)
	And I click on "Submit" Button
	Then I verify the "Errors exist, see highlighted fields below." warning message
	And I verify the highlighted errors on "From" date field on "1" row
	
	Scenario: Verify error message on trying to create rebate with invalid date on rebate program start and enddate ie 1 day after program end date
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "Rebate" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
#	And I enter "5daysAfterDate" on the "programEndDate" textfield
	And I set program end date to "5" days from today
	When I click on "Add" Button
	And I enter amount "10" on row "1"
	And I set "1" days after rebate end state as start date
	And I set end date as "7" days from start Date
	#And I set start date from today to "7" days from start Date as end date
	And I click on "Submit" Button
	Then I verify the "Errors exist, see highlighted fields below." warning message
	And I verify the highlighted errors on "From" date field on "1" row
	And I verify the highlighted errors on "To" date field on "1" row

Scenario: Create and verify rebate with 2 rebates under pricing within the valid rebate period
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "multiRuleRebate" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	And I set program end date to "45" days from today
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	When I click on "Add" Button
	And I set end date on row "1" as "2" days from start Date
#	And I set end date as "2" days from start Date
	And I enter amount "10" on row "1"
	And I click on "Approve" Button
	Then I verify the "Changes saved successfully" successful message
	When I click on "Add" Button
#	And I set start date on row "2" as "3" days from program start date
	# to add a valid rebate range after first rebate created above( ie after 1st rebate's end date)
#	And I set end date on row "2" as "10" days from start Date
	And I set start as "3" days from program start date and end dates as "10" days from start date on row "2"
	And I enter amount "5" on row "2"
	And I click on "Approve" Button
	Then I verify the "Changes saved successfully" successful message
	
	@skip @flaky-env
	# Skipped 2026-05-11: popup row timeout (verifyPopupRows -> selectedItemAmountKeys)
	# on dev7404; see Bamboo scplatform-EEA-RWTS-2345.
	Scenario: Verify delete button under pricing tab
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "newRebateForDelete" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	And I set program end date to "90" days from today
	When I click on "Add" Button
	And I set end date on row "1" as "4" days from start Date
	And I enter amount "10" on row "1"
	When I click on "Add" Button
	And I set start as "5" days from program start date and end dates as "6" days from start date on row "2"
	And I enter amount "20" on row "2"
	When I click on "Add" Button
	And I set start as "7" days from program start date and end dates as "8" days from start date on row "3"
	And I enter amount "30" on row "3"
	When I click on "Add" Button
	And I set start as "9" days from program start date and end dates as "10" days from start date on row "4"
	And I enter amount "40" on row "4"
	And I select row "3" from the "selectedItemAmountKeys" "checkbox" list
	And I click on "Delete" Button
	Then I verify "3" rows listed with checkbox name "selectedItemAmountKeys"
	And I verify rebate amount "30" got deleted 
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	
#	Scenario: Verify delete button under pricing tab
#	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
#	When I navigate to "Rebates" -> "New Rebate Program"
#	And I enter "newRebateForDelete" on the "rebateName" textfield
#	And I click on multiple search "rebatesProvider" icon
#	And I wait till the page loads for "15" seconds
#	And I set "2WIRE" on Find textField on popup 
#	And I click on "Search" Button on the popup 
#	And I select "1" "rebateProvider" and "confirm" the list on the popup
#	And I set program end date to "30" days from today
#	When I click on "Add" Button
#	And I set start date on row "1" as "3" days from program start date
#	And I set end date on row "1" as "4" days from start Date
#	And I enter amount "10" on row "1"
#	When I click on "Add" Button
#	And I set start date on row "2" as "5" days from program start date
#	And I set end date on row "2" as "6" days from start Date
#	And I enter amount "20" on row "2"
#	When I click on "Add" Button
#	And I set start date on row "3" as "7" days from program start date
#	And I set end date on row "3" as "8" days from start Date
#	And I enter amount "30" on row "3"
#	And I set start date on row "4" as "9" days from program start date
#	And I set end date on row "4" as "10" days from start Date
#	And I enter amount "40" on row "4"
#	And I select row "3" from the "selectedItemAmountKeys" "checkbox" list
#	And I click on "Delete" Button
#	Then I verify "3" rows listed with checkbox name "selectedItemAmountKeys"
#	And I verify rebate amount "30" got deleted 
#	And I click on "Submit" Button
#	Then I verify the "Changes saved successfully" successful message
#	
	Scenario: Verify delete button without selecting checkbox under pricing tab
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "newRebateForDeleteAction" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	#And I enter "2WIRE" on "businessEntityName" textfield
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	When I click on "Add" Button
	And I enter amount "10" on row "1"
	When I click on "Add" Button
	And I enter amount "20" on row "2"
	And I click on "Delete" Button
	And I click "OK" on the warning popup with message "Please select pricing record to delete."
	Then I verify "2" rows listed with checkbox name "selectedItemAmountKeys"

	 Scenario:  Verify view results after adding pricing and rule
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "newRebateForRule" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait "15" seconds
#	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	And I click on the PRICING tab
	When I click on "Add" Button
	And I enter amount "10" on row "1"
	And I click on the save button
	Then I verify the "Changes saved successfully" successful message
	When I click on the RULES tab
	And I click on the "add" icon
	And I click on multiple search "itemNumbers" icon
	And I wait "180" seconds
#	And I wait till the page loads for "180" seconds
	And I set "JD002" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "itemNumbers" and "confirm" the list on the popup
	And I save the "itemNumber" selected from search popup for reference
	And I click on multiple search "platform" icon
	And I wait till the page loads for "15" seconds
	And I select "1" "platform" and "confirm" the list on the popup
	#And I save the "platform" selected from search popup for reference
	And I click on multiple search "categories" icon
	And I wait "15" seconds
#	And I wait till the page loads for "15" seconds
	And I set "AC ADAPTER" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "categories" and "confirm" the list on the popup
	#And I save the "categories" selected from search popup for reference
	And I select the "ruleCategoryKeys" with value "AC ADAPTER"
	And I click on the "categoryRemove" Button on Rules Tab
	And I click on the Save Rule button
	And I click on the save button
	Then I verify the "Changes saved successfully" successful message
	And I select first "1" rows from the "selectedItemAmountKeys" "checkbox" list
	And I click on "Approve" Button
	Then I verify the "Changes saved successfully" successful message
#	And I verify the results under View Results tab
#	When I click on the rulesApplied link
#	Then I should be landing on rules tab
	
	
#PDSUPPORT-7913 CSR07301651 - One Cost prod - CFG items in Item rebate rule (search item result) - NHPC2
	Scenario:  Verify edit rule on Rules Tab, only Dell items (with Item type = Item) are in the search result
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button 
	And I enter "createRebate" on "name" textfield
	And I click on "Apply" Button 
	And I click on Edit icon on row "1"
	When I click on the RULES tab
	And I click on the "add" icon
	And I click on multiple search "itemNumbers" icon
	And I wait "240" seconds
#	And I wait till the page loads for "240" seconds
	And I set "NHPC2" on Find textField on popup 
	And I click on "Search" Button on the popup 
	Then I verify "Business Entity Name" column has value "MTCM-Tenant-123" displayed under search results for all rows on popup
	Then I verify "Item Number" column has value "NHPC2" displayed under search results for all rows on popup
	And I select "1" "itemNumbers" and "confirm" the list on the popup
	And I click on multiple search "itemNumbers" icon
	And I wait "280" seconds
#	And I wait till the page loads for "280" seconds
	And I set "AT201807231847-AMPHENOL" on Find textField on popup 
	And I click on "Search" Button on the popup 
	Then I verify "0" rows listed after search on the popup
	Then I verify "No records found to display, Refine your search in the filters" message on the PopUp
	And I click on X Close Button on the popup
	Then I see popup is not displayed
	Then I should be landed on "Edit Rule" page

Scenario Outline: Search with a rebate status on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
#	And I enter "JD002" on "itemNumber" textfield
	And I select "<status>" on the Status list 
	And I click on "Apply" Button 
	And I wait till the page loads for "10" seconds 
	Then I verify search filter results are displayed 
	And I verify the search results status as "<status>"
	Examples:     
	|status	 |
	|Approved|
	|Pending |
	|New	 |
	
	#scplatform-4627 CSR06568547 - Rebates - Rebate Item and Multiple Item Numbers search filter
	Scenario: Search with a rebate item on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I enter "JD002" on "itemNumber" textfield
	And I click on "Apply" Button 
	And I wait "10" seconds
#	And I wait till the page loads for "10" seconds 
	Then I verify search filter results are displayed 
	
	#scplatform-4627 CSR06568547 - Rebates - Rebate Item and Multiple Item Numbers search filter
	Scenario: Search on multiple rebate item on Search Rebates page
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program" 
	And I click on "Clear" Button 
	And I click on multiple search "itemNumbers" icon 
	And I wait till the page loads for "15" seconds
	And I set "JD002" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "itemNumbers" and "confirm" the list on the popup
	And I click on "Apply" Button 
	And I wait "30" seconds
#	And I wait till the page loads for "10" seconds 
	Then I verify search filter results are displayed 
	
	#scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
	Scenario: Verify search criteria retained after changing pages and return
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button 
	And I set the itemNumber as "CR007" 
	And I click on "Apply" Button 
	Then I verify "No records found to display" message
	When I navigate to "Supply Allocation" -> "Manage Parent Group"
	And I click on "Clear" Button 
	And I set group name as "TestingGroupNow"  
	And I click on "Apply" Button
	Then I should be landed on "Manage Parent" page
	When I navigate to "Rebates" -> "Search Rebate Program" 
	Then I verify "CR007" on "itemNumber" textfield
	And I click on "Clear" Button 
	Then I verify "" on "itemNumber" textfield
	
	#scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
	Scenario: Verify search criteria retained after changing pages and return
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button 
	And I set the itemNumber as "CR007" 
	And I click on "Apply" Button 
	And I log out of HarmonyMTCM
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	Then I should be landed on "Rebates" page
	Then I verify "CR007" on "itemNumber" textfield
	And I click on "Clear" Button
	And I click on "Apply" Button
	And I wait till the page loads for "30" seconds
	Then I verify "" on "itemNumber" textfield
	And I log out of HarmonyMTCM
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
	When I navigate to "Rebates" -> "Search Rebate Program" 
	Then I verify "" on "itemNumber" textfield
	
	Scenario:  PDSUPPORT-2917 CSR07001225 Missing '-' -> Cosmetic issue in Rebate Program
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"  
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "newRebateToVerifyDate" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	#And I enter "2WIRE" on "businessEntityName" textfield
	And I set program end date to "45" days from today
	When I click on "Add" Button
	And I set start date on row "1" as "3" days from program start date
	And I set end date on row "1" as "4" days from start Date
	And I enter amount "20" on row "1"
	When I click on "Add" Button
	And I set start date on row "2" as "4" days from program start date
	And I set end date on row "2" as "5" days from start Date
	And I enter amount "30" on row "2"
	And I click on "Submit" Button
	Then I verify the highlighted errors on "From" date field on "2" row
	And I verify the "Errors exist, see highlighted fields below." warning message
	And I mousehover on "From" date field to verify "Date range for the same item overlaps"
	
	Scenario:  Verify Agile and Proteus platforms are auto complete and able to add/delete in the list while creating/searching Rebate Program
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button
	And I enter "PCBA Components-ESG-Cindy Lou-$30 LCAP program" on "name" textfield
	And I click on "Apply" Button 
	And I click on Edit icon on row "1"
	Then I should be landed on "Rebate Program" page
	When I click on the RULES tab
	And I click on element with ID "editRuleId"
	And I click on multiple search "platform" icon
	And I wait till the page loads for "15" seconds
	And I set "1 SOCKET 1U AMD ODM" on Find textField on popup
	And I select "10" on "Page Size" Combobox on the popup 
	And I select "PROTEUS" on "Platform Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	And I select "1" "platform" and "confirm" the list on the popup
	And I click on the Save Rule button
	And I select the "rulePlatformKeys" with value "1 SOCKET 1U AMD ODM (PROTEUS)"
	And I click on the "platformRemove" Button on Rules Tab
	And I click on the Save Rule button
	And I click on the save button
	Then I verify the "Changes saved successfully" successful message
	And I click on multiple search "platform" icon
	And I set "1220S" on Find textField on popup
	And I select "10" on "Page Size" Combobox on the popup 
	And I select "AGILE" on "Platform Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	And I select "1" "platform" and "confirm" the list on the popup
	And I click on the Save Rule button
	And I select the "rulePlatformKeys" with value "1220S (AGILE)"
	And I click on the "platformRemove" Button on Rules Tab
	And I click on the Save Rule button
	And I click on the save button
	Then I verify the "Changes saved successfully" successful message
	
Scenario:  Verify ALL platform names are listed in the list on alphabetical order 
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8"
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Clear" Button
	And I enter "PCBA Components-ESG-Cindy Lou-$30 LCAP program" on "name" textfield
	And I click on "Apply" Button 
	And I click on Edit icon on row "1"
	Then I should be landed on "Rebate Program" page
	When I click on the RULES tab
	And I click on element with ID "editRuleId"
	And I click on multiple search "platform" icon
	And I select "10" on "Page Size" Combobox on the popup 
	And I select "ALL" on "Platform Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	Then I verify Platform Name list is sorted on alphabetical order

Scenario: Create New rebate including Non enterprise items and enterprise items 
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "New Rebate Program"
	And I enter "newRebateForRulecreate" on the "rebateName" textfield
	And I click on multiple search "rebatesProvider" icon
	And I wait till the page loads for "15" seconds
	And I set "2WIRE" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "rebateProvider" and "confirm" the list on the popup
	And I set program end date to "10" days from today
	And I click on "Submit" Button
	Then I verify the "Changes saved successfully" successful message
	When I click on the RULES tab
	And I click on the "add" icon
	And I click on multiple search "itemNumbers" icon
	And I wait "180" seconds
#	And I wait till the page loads for "180" seconds
	And I set "JD002" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "itemNumbers" and "confirm" the list on the popup
	And I save the "itemNumber" selected from search popup for reference
	When I click on the RULES tab
	And I click on the "add" icon
	And I click on multiple search "itemNumbers" icon
	And I wait "200" seconds
#	And I wait till the page loads for "200" seconds
	And I select "Supplier Item" on "Item Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	And I select "1" "itemNumbers" and "confirm" the list on the popup
	#And I wait till the page loads for "30" seconds
	And I wait "30" seconds
	And I click on multiple search "itemNumbers" icon
	And I wait "200" seconds
#	And I wait till the page loads for "200" seconds
	And I select "Supplier Item" on "Item Type" Combobox on the popup
	And I set "Unisys" on "value(business)" textField on popup
	And I click on "Search" Button on the popup 
	And I select "1" "itemNumbers" and "confirm" the list on the popup

Scenario: edit New rebate including Non enterprise items and enterprise items 
	Given I log into HarmonyMTCM as "mtcmUser" with "adminuser8" 
	When I navigate to "Rebates" -> "Search Rebate Program"
	And I click on "Apply" Button 
	And I click on the Edit icon
	When I click on the RULES tab
	And I click on the "add" icon
	And I click on multiple search "itemNumbers" icon
	And I wait till the page loads for "180" seconds
	And I set "JD002" on Find textField on popup 
	And I click on "Search" Button on the popup 
	And I select "1" "itemNumbers" and "confirm" the list on the popup
	And I save the "itemNumber" selected from search popup for reference
	When I click on the RULES tab
	And I click on the "add" icon
	And I click on multiple search "itemNumbers" icon
	And I wait "200" seconds
#	And I wait till the page loads for "200" seconds
	And I select "Supplier Item" on "Item Type" Combobox on the popup
	And I click on "Search" Button on the popup 
	And I select "1" "itemNumbers" and "confirm" the list on the popup
	And I wait till the page loads for "30" seconds
	And I click on multiple search "itemNumbers" icon
	And I wait "200" seconds
#	And I wait till the page loads for "200" seconds
	And I select "Supplier Item" on "Item Type" Combobox on the popup
	And I set "Unisys" on "value(business)" textField on popup
	And I click on "Search" Button on the popup 
	And I select "1" "itemNumbers" and "confirm" the list on the popup
	
	
	
	
	
#	# delete 
#	# goto closed(check other status also other than new) and verify add rule btn is disabled
#	# search with other search criteria other than main search
#	# verify audit histry btn visibility and functionality
