@debug
Feature: Forecast workflow

#scplatform-9394
#Scenario: Functional group status
#		Given I do setup in property "scplatform.feature.enable.userItemType.CFG.Integration" with "true"
#		Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#		When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#		And I click on "Clear" Button 
#	And I select "Yes" on "Show Item Without Group" Combobox 
#	And I click on "Apply" Button 
#	And I wait till the page loads for "45" seconds 
#	And I select first "2" rows from the "selectedPageKeys" "checkbox" list 
#	And I click on "Create Group" Button 
#	And I "create" the items to the Functional Group "FG9394" with "Save All" 
#	Then I verify the "Functional Group Saved" successful message 
#	
#		When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set group name as "FG9394" 
#	And I click on "Apply" Button
#	And I click on the name "FG9394"  
#	
#	Then I verify the status "ACTIVE" of the Functional Group "ACTIVE"

#FunctionalroupExternalID Story
#Scenario Outline: Upload Functional Group to change the FG status values and verify success message, then verify status on UI 
#	Given I log into HarmonyMTCM as "mtcmUser" with "Admin2" 
#	When I navigate to "Upload/Manage Jobs" -> "Supply Allocation" 
#	And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>" 
#	Then I verify the "<action>" on the Manage FG page 
#	Examples: 
#		|dataFile											 |fileName   |action		|msg	|msgType |
#		|FunctionalGroupItemUploadUI	 |FGUpload9370   |Inactive  |			|success |
		
#Scenario: Validate there should be only one Generic item in CFG
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin2"
    #When I navigate to "Administration" -> "Admin Upload"
    #When I uploaded the XML file "CFG_TestNewest9381" for "FunctionalGroup" and checked it on the UI page
    #	When I navigate to "Supply Collaboration" -> "Manage Functional Group" 
#	And I click on "Clear" Button 
#	And I set group name as "CFG_Test_2511" 
#	And I click on "Apply" Button 
#	And I click on the name "CFG_Test_2511" 
#	And I wait "30" seconds
#	
#	Then I verify field name "Alias Group Name" is readonly
	
    @skip
    Scenario Outline: Upload Item values and Search for an uploaded item on Manage Users and verify user details
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    And I navigate to "Administration" -> "Manage Items"
    And I click on "Clear" Button
    And I set the itemNumber as "CR"
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    #And I select the "CR" from the results
    And I click on the "itemNumbernoPopup" to verify the details
    Then I should be landed on "CR" page
    And I click on the save button
    And I click on "Back" Button
    Then I should be landed on "Search Results" page
    Then I log out of HarmonyMTCM

    Examples: 
      | dataFile  | fileName        | action          | msg | msgType |
      | ItemAVLUI | ItemUploadForCR | uploadItemForCR |     | success |