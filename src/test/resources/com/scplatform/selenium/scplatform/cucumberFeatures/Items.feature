@Harmony12
Feature: Items workflow

Scenario Outline: Upload FG with different values and verify error messages
	Given I log into HarmonyMTCM as "mtcmUser" 
	When I navigate to "Main" -> "Upload" 
	And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<errMsg>" "<msgType>"
	#And I log out of HarmonyMTCM
	Examples:
	|dataFile						 |fileName  |action	|errMsg									    |msgType |
 	|Functional Group Item (*.xls)	 |FGUPload	|XLOB	|Platform does not exist for XLOB type		|error	 |
 	|Functional Group Item (*.xls)	 |FGUPload	|NFG	|Platform does not exist for NFG type		|error	 |
	|Functional Group Item (*.xls)	 |FGUPload	|No item|not present in Functional group			|error	 |
	
	
	
	Scenario Outline: Upload FG with different values and verify success message, then verify on UI
	Given I log into HarmonyMTCM as "mtcmUser" 
	When I navigate to "Main" -> "Upload" 
	And I upload the "<dataFile>" "<fileName>" with "<action>" & verify "<msg>" "<msgType>"
	#And I log out of HarmonyMTCM
	Examples:
	|dataFile						 |fileName   |action	|msg	|msgType |
	|Functional Group Config (*.xls) |FGUPload   |RENAME    |		|success |
	
	
	
