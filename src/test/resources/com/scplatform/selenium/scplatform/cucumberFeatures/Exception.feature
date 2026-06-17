@HarmonyException
Feature: Exception Workflow Test Plan

  @bug
  Scenario Outline: Verify new exception request created with single odm attachment file for ODMBUY type and backdating req
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCREmail"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "CRApprove" selected
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoEmailExcep" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "BACKDATE" on Request Type Combobox
    And I select "ODM BUY" on Cost Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForEmailAttach"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    Then I should see "history" icon is "displayed"
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I should see "history" icon is "displayed"
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    When I click on "Approve" Button
    And I enter approve comments as "Approval by Admin"
    And I click on "Yes," Button
    Then I should see "history" icon is "displayed"
    Then I verify the "Exception request has been Approved" successful message
    And I download and verify 1 ODM email file

    Examples: 
      | dataFile  | fileName        | action                         | msg | msgType |
      | ItemAVLUI | ItemUploadForCR | uploadItemForODMEmailCRApprove |     | success |

  #PDSUPPORT-26925 ER cannot be save or submit when having ODM email.
  #scplatform-4696 CSR06706978 - Download allocation - Search result display nothing
  @bug
  Scenario Outline: Verify new exception request for no existing CR
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRApprove"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "CRApprove" selected
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRApprove"
    And I select "Yes" on "Show Item Without Group" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "100" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "itemCRGrp" with "Save All"
    Then I verify the "Functional Group Saved" successful message
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "itemCRGrp" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoException" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I enter "test /n /r autoException" on the "comments" textfield
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    When I upload the file "CREmail.msg" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForNoExistingCR"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    Then I should see "history" icon is "displayed"
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I should see "history" icon is "displayed"
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    And I download and verify 3 ODM email file
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    When I click on "Approve" Button
    And I click on "Yes," Button
    Then I should see "history" icon is "displayed"
    Then I verify the "Exception request has been Approved" successful message
    And I search by ExceptionID generated to verify the new exception details on Search Exception Page
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRApprove"
    And I select "Approved" on the Status list
    And I click on "Apply" Button
    Then I verify "1" rows listed with checkbox name "selectedPageKeys"
    And I verify the search results status as "Approved"
    #And I verify "Exception ID" has value "ER" assigned
    And I verify "Cost Type" has value "ODM BUY" assigned
    And I verify "Status" has value "APPROVED" assigned
    And I verify "Item" has value "itemCRApprove" assigned
    And I verify "Currency" has value "USD" assigned
    #And I verify "Reason Code" has value "NEW PART" assigned
    When I click on the Edit icon
    When I click on the "ODM BUY" tab
    Then I verify Record Source as "" on row "1"
    And I verify projectName as "" on row "1"
    And I verify MPN "" on row "1"
    And I verify System Action "" on row "1"
    Then I verify "exceptionID" on "1" rows
    And I verify row "1" cost records status as "APPROVED"
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I set the itemNumber as "itemCRApprove"
    And I select "No" on "Hide Supplier with no allocation" Combobox
    And I click on "Apply" Button
    Then I verify search filter results are displayed
    And I verify "Group Name" column has "" as value displayed under search results for all rows

    Examples: 
      | dataFile  | fileName        | action                 | msg | msgType |
      | ItemAVLUI | ItemUploadForCR | uploadItemForCRApprove |     | success |

  @bug
  Scenario: Verify create New Exception request for existing approved CR - new exception date within above Crs date range
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoExcepExistingCR" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForExistingCRWithinDateRange"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validattion error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    When I click on "Approve" Button
    And I enter approve comments as "Approval by Admin"
    And I click on "Yes," Button
    Then I verify the "Exception request has been Approved" successful message
    And I search by ExceptionID generated to verify the new exception details on Search Exception Page
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRApprove"
    And I click on "Apply" Button
    #Then I verify "2" rows listed with checkbox name "selectedPageKeys"
    Then I verify the search results itemNumber as "itemCRApprove"
    Then I verify "Status" column has "APPROVED" as value displayed under search results on any of the rows
    #Then I verify "1" rows has status value as "CLOSED"
    When I click on the Edit icon
    And I should see "itemCRApprove" link is displayed
    When I click on the "BUY" tab
    Then I verify Record Source as "Exception" on row "1"
    And I verify projectName as "" on row "1"
    And I verify MPN "10" on row "1"
    And I verify System Action "" on row "1"
    #Then I verify "prevExcepID" on "1" rows
    #And I verify "exceptionID" on "1" rows
    Then I verify CRs has status value as "APPROVED" under Tab

  #PDSUPPORT-26925 ER cannot be save or submit when having ODM email.
  @bug
  Scenario: Verify edit action for New Exception created by adding upload file
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "editExcep" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    And I save the exceptionName and exceptionID for verification
    And I search by ExceptionID generated to verify the new exception details on Search Exception Page
    And I click on the "ER" link
    And I verify the new exception details displayed with "NEW" status
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemAVLUI" "ItemUploadForCR" xlsx with "uploadItemForEditCRApprove" & verify "" "success"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemEditCR"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "CRApprove" selected
    Then I search by name "editExcep" and verify details with "NEW" status on Search Exception Page
    And I click on the "ER" link
    And I wait till the page loads for "10" seconds
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    When I upload the file "CRException" for "uploadExcepForNewCRApprove"
    #And I click on save button
    #Then I verify the "Exception request has been Updated" successful message
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    And I verify the new exception details displayed with "PENDING" status
    When I click on "Approve" Button
    And I enter approve comments as "Approval by Admin"
    And I click on "Yes," Button
    Then I verify the "Exception request has been Approved" successful message
    And I verify the new exception details displayed with "APPROVED" status
    And I verify the exceptionName and exceptionID after edit action
    Then I search by name "editExcep" and verify details with "APPROVED" status on Search Exception Page
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "itemEditCR"
    And I click on "Apply" Button
    Then I verify "1" rows listed with checkbox name "selectedPageKeys"
    When I click on the Edit icon
    When I click on the "BUY" tab
    Then I verify row "1" cost records status as "APPROVED"

  #includes validation of save and exit and back button as well
  @bug
  Scenario: Verify approval of new exception created from search exception page
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "searchExcepToApprove" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForNewCRApprove"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    Then I search by name "searchExcepToApprove" and verify details with "NEW" status on Search Exception Page
    When I click on the "ER" link
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    And I verify the new exception details displayed with "PENDING" status
    Then I search by name "searchExcepToApprove" and verify details with "PENDING" status on Search Exception Page
    When I click on the "ER" link
    When I click on "Approve" Button
    And I enter approve comments as "Approval by Admin"
    And I click on "Yes," Button
    Then I verify the "Exception request has been Approved" successful message
    And I verify the new exception details displayed with "APPROVED" status
    When I click on "Back" Button
    Then I should be landed on "Exception Request" page
    And I click on "Clear" Button
    And I enter "searchExcepToApprove" on "exceptionName" textfield
    And I click on "Apply" Button
    Then I verify "1" rows listed without selection option
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    And I select "Approved" on the Status list
    When I set the itemNumber as "itemEditCR"
    And I click on "Apply" Button
    Then I verify "1" rows listed with checkbox name "selectedPageKeys"
    #Then I verify "2" rows listed with checkbox name "selectedPageKeys"
    When I click on the Edit icon
    When I click on the "BUY" tab
    Then I verify row "1" cost records status as "APPROVED"

  #Then I verify row "2" cost records status as "CLOSED"
  @bug
  Scenario: Verify new exception created with costtype BUY with uploaded CR of type ODMBUY
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "diffTypeExcep" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CRException" for "uploadExcepForDiffTypeCR"
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    And I verify the new exception details displayed with "PENDING" status
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRApprove"
    And I click on "Apply" Button
    Then I verify "APPROVED" status on search filter results
    #Then I verify "2" rows listed with checkbox name "selectedPageKeys"
    #Then I verify "2" rows has status value as "APPROVED"
    #Then I verify "1" rows has status value as "CLOSED"
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I enter "diffTypeExcep" on "exceptionName" textfield
    And I click on "Apply" Button
    Then I verify "1" rows listed without selection option
    When I click on the "ER" link
    When I click on "Approve" Button
    And I enter approve comments as "Approval by Admin"
    And I click on "Yes," Button
    Then I verify the "Exception request has been Approved" successful message
    And I verify the new exception details displayed with "APPROVED" status
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRApprove"
    And I click on "Apply" Button
    #Then I verify "4" rows listed with checkbox name "selectedPageKeys"
    #Then I verify "3" rows has status value as "APPROVED"
    #Then I verify "1" rows has status value as "CLOSED"
    Then I verify "APPROVED" status on search filter results
    Then I verify "CLOSED" status on search filter results
    When I click on the Edit icon
    When I click on the "BUY" tab
    When I click on the "ODM BUY" tab
    Then I verify row "1" cost records status as "APPROVED"

  @bug
  Scenario: Verify no duplicate Exception IDs and Names allowed for new exception requests
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoException" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify validation message "Exception already exist with the same name" displayed
    When I upload the file "CRException" for "uploadExcepForExistingCRWithinDateRange"
    And I click on "Submit" Button
    And I wait till the page loads for "10" seconds
    Then I verify validation message "Exception already exist with the same name" displayed

  #PDSUPPORT-25662
  @bug
  Scenario: User able to "rename" Approved Exception Request by creating new Exception Request
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoException" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I click on save button
    Then I verify validation message "Exception already exist with the same name" displayed
    And I enter "autoExceptionNew" on the "exceptionName" textfield
    And I click on "Submit" Button
    And I search by ExceptionID of autoExcep to verify the new exception details on Search Exception Page

  Scenario: Verify error message for create New Exception request for BUY and BACKDATING ReqType with no CR uploaded for submit
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveNoCRExcep" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the "ODM Attachments cannot be empty for cost type BUY and request type BACKDATING" warning message

  #validation of No option after submit click is also included
  @bug
  Scenario: Verify error message for create New Exception request for no item exists
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveNoItemCRExcep" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForNoExistingItem"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "No," Button
    Then I verify data entered on fields
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Item doesn't exist" on popup
    And I click on "Ok" Button

  @bug
  Scenario: Verify error message for create New Exception request ODM BUY CT for wrong supplier info
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWrongSuppODMBuy" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRExceptionWrongSupp" for "uploadExcepForWrongSupp"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    When I click on "Approve" Button
    And I click on "Yes," Button
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Couldn't create AVL for" on popup
    And I click on "Ok" Button

  @bug
  Scenario: Verify error message for create New Exception request BUY CT for wrong supplier info
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWrongSuppBuy" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRExceptionWrongSupp" for "uploadExcepForWrongSupp"
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    When I click on "Approve" Button
    And I click on "Yes," Button
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Couldn't create AVL for" on popup
    And I click on "Ok" Button

  Scenario: Verify error message for create New Exception request for empty dates
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepEmptyDates" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRException_Dates" for "uploadExcepForEmptyDates"
    And I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Required value missing in column EffectiveFromDate" on popup
    And I click on "Ok" Button

  # includes validation of warning popup close button action as well
  Scenario: Verify error message for create New Exception request for no reason code
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWithNoReasonCode" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForNoReasonCode"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Reason code is mandatory" on popup
    And I click on "Ok" Button
    Then I should see the Ok button is not displayed and enabled
    Then I click and verify Close button on validation error message dialog is closed

  #PDSUPPORT-24495
  # Validation of ODM type exception is included here -> excep ->ODM BUY and uploaded CR -> BUY
  # Including validation of search exception req by item Number
  #includes condition checks for delete , download icons for CR uploads as well
  @bug
    Scenario Outline: Verify new exception request with ODM BUY cost type - CR attachment and download
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoExcepDownload" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    When I upload the file "CRException" for "uploadExcepForSearchCRApprove"
    Then I should see "file_download" icon is not displayed
    Then I verify close button is visible after CR attachment
    Then I verify labelName "CRException.xlsx" on the loaded page
    When I remove the attached CR file
    Then I verify close button is not visible after CR attachment
    And I verify labelName "CRException.xlsx" not found on the loaded page
    When I upload the file "CRException" for "uploadExcepForSearchCRApprove"
    When I upload the file "CREmail.msg" for "uploadExcepForEmailAttach"
    Then I should see "file_download" icon is not displayed
    Then I should see "file_download" icon is not displayed
    Then I verify close button is visible after CR attachment
    Then I verify labelName "CRException.xlsx" on the loaded page
    When I upload the file "CREmail.msg" for "uploadExcepForEmailAttach"
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validattion error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "has no production responsibility" on popup
    And I click on "Ok" Button
    #Then I should see "file_download" icon is disabled
    #Then I should see "delete" icon is disabled
    Then I verify labelName "CRException.xlsx" on the loaded page
    Then I verify labelName "Alex Teo CP" on the loaded page
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    And I save the exceptionName and exceptionID for verification
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRSearchApprove"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "CRApprove" selected
    #And I search by ExceptionID generated to verify the new exception details on Search Exception Page
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I enter "autoExcepDownload" on "exceptionName" textfield
    And I click on "Apply" Button
    And I click on the "ER" link
    When I upload the file "CRException" for "uploadExcepForSearchCRApprove"
    When I upload the file "CREmail.msg" for "uploadExcepForEmailAttach"
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    Then I should see "file_download" icon is "displayed" inside Exception Request
    Then I should see "delete" icon is not displayed
    Then I verify labelName "CRException.xlsx" on the loaded page
    Then I verify labelName "Alex Teo CP" on the loaded page
    And I search by ExceptionID generated to verify the new exception details on Search Exception Page
   # When I click on download Button and verify the result for "SearchExcep" for "PendingStatus"
    When I click on the "ER" link
    When I click on "Approve" Button
    And I click on "No," Button
    And I verify the new exception details displayed with "PENDING" status
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    And I click on "Yes," Button
    Then I verify the "Exception request has been Approved" successful message
    Then I should see "delete" icon is not displayed
    Then I should see "file_download" icon is "displayed" inside Exception Request
    And I verify the new exception details displayed with "APPROVED" status
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRSearchApprove"
    And I click on "Apply" Button
    Then I verify "1" rows listed with checkbox name "selectedPageKeys"
    And I verify "Status" column has "Approved" as value displayed under search results on any of the rows
    #And I verify "Exception ID" has value "ER" assigned
    And I verify "Cost Type" has value "ODM BUY" assigned
    When I click on the Edit icon
    When I click on the "ODM BUY" tab
    Then I verify "1" rows has status value as "APPROVED" under Tab
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I enter "autoExcepDownload" on "exceptionName" textfield
    #And I enter "itemCRSearchApprove" on "multipleitemNumbers" textfield
    And I click on "Apply" Button
    Then I verify "1" rows listed without selection option
    #When I click on download Button and verify the result for "SearchExcep" for "ApprovedStatus"

    Examples: 
      | dataFile  | fileName        | action                       | msg | msgType |
      | ItemAVLUI | ItemUploadForCR | uploadItemForSearchCRApprove |     | success |
 
  @bug
   Scenario Outline: Verify new exception request with ODM BUY cost type - ODM email attachment validation
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoExcepDownloadODM" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    When I upload the file "CRException" for "uploadExcepForSearchCRApprove"
    Then I should see "file_download" icon is not displayed
    Then I verify close button is visible after CR attachment
    Then I verify labelName "CRException.xlsx" on the loaded page
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    When I remove the attached CR file
    Then I verify close button is not visible after CR attachment
    And I verify labelName "CRException.xlsx" not found on the loaded page
    When I upload the file "CRException" for "uploadExcepForSearchCRApprove"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    When I upload the file "CREmail.msg" for "uploadExcepForEmailAttach"
    Then I should see "file_download" icon is not displayed
    Then I should see "file_download" icon is not displayed
    Then I verify close button is visible after CR attachment
    Then I verify labelName "CRException.xlsx" on the loaded page
    When I upload the file "CREmail.msg" for "uploadExcepForEmailAttach"
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the "Exception request has been submitted" successful message
    Then I should see "file_download" icon is "displayed" inside Exception Request
    Then I should see "delete" icon is not displayed
    Then I verify labelName "CRException.xlsx" on the loaded page
    Then I verify labelName "Alex Teo CP" on the loaded page
    And I search by ExceptionID generated to verify the new exception details on Search Exception Page
    #When I click on download Button and verify the result for "SearchExcep" for "PendingStatus"
    When I click on the "ER" link
    When I click on "Approve" Button
    And I click on "No," Button
    And I verify the new exception details displayed with "PENDING" status
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    And I click on "Yes," Button
    Then I verify the "Exception request has been Approved" successful message
    Then I should see "delete" icon is not displayed
    Then I should see "file_download" icon is "displayed" inside Exception Request
    And I verify the new exception details displayed with "APPROVED" status
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRSearchApprove"
    And I select "ODM BUY" on Cost Type Combobox
    And I click on "Apply" Button
    #Then I verify "1" rows listed with checkbox name "selectedPageKeys"
    And I verify "Status" column has "Approved" as value displayed under search results on any of the rows
    #And I verify "Exception ID" has value "ER" assigned
    And I verify "Cost Type" has value "ODM BUY" assigned
    When I click on the Edit icon
    When I click on the "ODM BUY" tab
    Then I verify "1" rows has status value as "APPROVED" under Tab
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I enter "autoExcepDownloadODM" on "exceptionName" textfield
    #And I enter "itemCRSearchApprove" on "multipleitemNumbers" textfield.
    And I click on "Apply" Button
    Then I verify "1" rows listed without selection option
    #When I click on download Button and verify the result for "SearchExcep" for "ApprovedStatus"

    Examples: 
      | dataFile  | fileName        | action                       | msg | msgType |
      | ItemAVLUI | ItemUploadForCR | uploadItemForSearchCRApprove |     | success |
 
  #Validation of ODM type exception is included here -> excep ->ODM BUY and uploaded CR -> ODM BUY
  #Including validation of search exception req by CostType
  @bug
  Scenario: Verify exception req withdraw action for allowable CT - ODM BUY
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoExcepWithDraw" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForSearchCRForWithDraw"
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    When I click on "Withdraw" Button
    And I click on "No," Button
    When I click on "Withdraw" Button
    When I click on "Yes," Button
    Then I verify the "Exception request has been withdraw" successful message
    When I enter "editExcepWithDraw" on the "exceptionName" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    And I verify the new exception details displayed with "PENDING" status
    And I click on "Close request" Button
    And I click on "No," Button
    And I click on "Close request" Button
    When I click on "Yes," Button
    Then I verify the "Exception request has been Closed" successful message
    And I verify the new exception details displayed with "CLOSED" status
    And I click on "Reopen" Button
    And I click on "No," Button
    And I click on "Reopen" Button
    When I click on "Yes," Button
    Then I verify the "Exception request has been Re-Opened" successful message
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    When I click on "Approve" Button
    And I click on "Yes," Button
    Then I verify the "Exception request has been Approved" successful message
    And I verify the new exception details displayed with "APPROVED" status
    And I search by ExceptionID generated to verify the new exception details on Search Exception Page
    When I navigate to "Pricing" -> "Search Cost Records"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCREmail"
    And I select "ODM BUY" on "Cost Type" Combobox
    And I click on "Apply" Button
    #Then I verify "1" rows listed with checkbox name "selectedPageKeys"
    #And I verify the search results status as "Approved"
    And I verify "Cost Type" has value "ODM BUY" assigned
    When I click on the Edit icon
    When I click on the "ODM BUY" tab
    Then I verify "1" rows has status value as "APPROVED" under Tab
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    #And I enter "itemCREmail" on "multipleitemNumbers" textfield
    And I click on "Apply" Button
    #Then I verify "1" rows listed without selection option
    #When I click on download Button and verify the result for "SearchExcep" for "ApprovedStatus"

  @bug
  Scenario: Verify exception req reject action
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoExcepWithDraw" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForSearchCRWithDraw"
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    When I click on "Reject" Button
    And I click on "No," Button
    When I click on "Reject" Button
    And I enter reject Comments
    When I click on "Yes," Button
    Then I verify the "Exception request has been Rejected" successful message
    And I verify the new exception details displayed with "REJECTED" status
    And I click on "Close request" Button
    When I click on "Yes," Button
    Then I verify the "Exception request has been Closed" successful message
    And I verify the new exception details displayed with "CLOSED" status
    And I click on "Reopen" Button
    And I click on "No," Button
    And I click on "Reopen" Button
    When I click on "Yes," Button
    Then I verify the "Exception request has been Re-Opened" successful message
    When I click on save button
    Then I verify the "Exception request has been Updated" successful message
    When I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    When I click on "Approve" Button
    And I click on "Yes," Button
    Then I verify the "Exception request has been Approved" successful message

  @bug
  Scenario: Verify default buttons for approved exception reqs
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select the exception state as "APPROVED"
    And I enter "*Exce*" on "exceptionName" textfield
    And I click on "Apply" Button
    #And I wait till the page loads for "45" seconds
   # And I click on "Apply" Button.
    Then I verify exception results are displayed with state as "APPROVED"
    When I click on the "ER" link
    Then I verify exception pricing details
    Then I should see the "Submit" button should not be displayed and disabled
    Then I should see the "Save" button should not be displayed and disabled
    Then I should see the "Save & Exit" button should not be displayed and disabled
    Then I should see the "Withdraw" button should not be displayed and disabled
    Then I should see the "Reopen" button should not be displayed and disabled
    Then I should see the "Close request" button should not be displayed and disabled

  @bug
  Scenario: Verify correction action on approved exception reqs
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select the exception state as "APPROVED"
    And I enter "*Exce*" on "exceptionName" textfield
    And I click on "Apply" Button
    #And I wait till the page loads for "45" seconds
    And I click on "Apply" Button
    When I click on the "ER" link
    When I click on "Correction" Button
    And I enter "adminuser8" on the exceptionApprover textfield
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    And I should see "Pre-Requisite Rule, Fiscal Calendar & ODM POC Link" link is displayed inside Exception Request
    Then I verify exception results are displayed with state as "APPROVED"
    When I click on "Back" Button
    Then I should be landed on "Exception Request" page
    Then I verify exception results are displayed with state as "APPROVED"

  @bug
  Scenario: Verify actions on reject exceptions
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select the exception state as "PENDING"
    And I click on "Apply" Button
    When I click on the "ER" link
    When I click on "Reject" Button
    And I enter reject Comments
    When I click on "Yes," Button
    Then I verify the "Exception request has been Rejected" successful message
    And I verify the new exception details displayed with "REJECTED" status
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select the exception state as "REJECTED"
    And I click on "Apply" Button
    Then I verify exception results are displayed with state as "REJECTED"
    When I click on the "ER" link
    Then I should see the "Submit" button should not be displayed and disabled
    Then I should see the "Save" button should not be displayed and disabled
    Then I should see the "Save & Exit" button should not be displayed and disabled
    Then I should see the "Close request" button should be displayed and enabled
    Then I should see the "Reopen" button should be displayed and enabled

  @skip
  Scenario: Verify error message for create New Exception request for invalid cost type on uploaded CR
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWithNoReasonCode" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the "exceptionApprover" textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CREmail1.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForInvalidCostType"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "5" seconds
    Then I verify the validation error msg "acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Value (ODM) in column CostType row 3 is invalid. Must be (one of) BUY,SELL,LIST,ODMBUY,SERVICE,EMQUOTE,WAP,XWAP" on popup
    And I click on "Ok" Button

  ## includes validation of backdating request as well
  Scenario: Verify error message for create New Exception request with diff Cost Type on uploaded Cr and exception req
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWithNoReasonCode" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the "exceptionApprover" textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I select "BACKDATE" on Request Type Combobox
    And I select "ODM BUY" on Cost Type Combobox
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForDiffCostType"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "5" seconds
    Then I verify the validation error msg "acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Value allowed in column CostType row 3 only if CostType is ODMBUY" on popup
    And I click on "Ok" Button

  #workflow changed and added another test case for that
  #	Scenario: Verify error message for create New Exception request for non existing supplier
  #Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
  #When I navigate to "Pricing" -> "New Exception Request"
  #And I enter "saveCRExcepWrongSupplier" on the "exceptionName" textfield
  #And I enter "aaron_x_chen" on the "exceptionOwner" textfield
  #And I enter "adminuser8" on the exceptionApprover textfield
  #And I select "MID MONTH" on Request Type Combobox
  #And I select "BUY" on Cost Type Combobox
  #And I select "ODM1" on the Applicable ODMs list
  #And I select "LOB1" on the Line of Business list
  #And I enter "testPlatform" on the "platformName" textfield
  #When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
  #When I upload the file "CRExceptionWrongSupp" for "uploadExcepForWrongSupp"
  #		And I click on "Submit" Button
  #And I click on "Yes," Button
  #And I wait till the page loads for "20" seconds
  #Then I verify the validation error msg "1 acknowledgement errors"
  #And I click on "View error" Button
  #Then I validate the warning message "Couldn't create AVL for" on popup
  #Then I validate the warning message "Supplier Business doesn't exist" on popup
  #And I click on "Ok" Button
  @bug
  Scenario: Verify error message for new request without resp to item , add non-prod resp and prod resp with upload file (item with no resp)
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    ##Creates testData by unassigning existing items responsibility
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRApprove"
    And I click on "Apply" Button
    And I "unassign" responsibility "mike_quick@dell.com" to the item "itemCRApprove" selected
    Then I verify the responsibility "" "unassigned" to "itemCRApprove"
    And I verify the "Responsibility has been unassigned successfully" successful message
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWithNoReasonCode" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the "exceptionApprover" textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForItemWithNoResp"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "5" seconds
    Then I verify the validation error msg "acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "has no production responsibility" on popup
    And I click on "Ok" Button
    #Adding wrong resp-> resp other than production
    When I upload the file "CRException" for "uploadExcepForItemWithNoProductionResp"
    And I click on "Submit" Button
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Yes," Button
    And I wait till the page loads for "5" seconds
    Then I verify the validation error msg "acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "has no production responsibility" on popup
    And I click on "Ok" Button
    #Adding prod resp
    When I upload the file "CRException" for "uploadExcepForItemWithProductionResp"
    And I click on "Submit" Button
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Yes," Button
    And I wait till the page loads for "5" seconds
    Then I verify the validation error msg "acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "has no production responsibility" on popup
    And I click on "Ok" Button

  Scenario: Verify invalid user message while creating new excep with CT - BUY and Req Type- BackDate for users other than Finance
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoExcepInvalidUser" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "ajay_arjunan@dell.com" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I click on save button
    Then I verify error msg "Invalid user" displayed on approver field

  @bug
  Scenario: Verify actions on reject exceptions for allowed CT - ODMBUY (According to configuration set)
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    #Create test data with pending status
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select "ODM BUY" on the CostType list
    And I select the exception state as "REJECTED"
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    When I click on the "ER" link
    And I click on "Reopen" Button
    When I click on "Yes," Button
    Then I verify the "Exception request has been Re-Opened" successful message
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    # pending status exception created froma rejected exception after reopening
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select "ODM BUY" on the CostType list
    And I select the exception state as "PENDING"
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    When I click on the "ER" link
    When I click on "Reject" Button
    And I enter reject Comments
    When I click on "Yes," Button
    Then I verify the "Exception request has been Rejected" successful message
    And I verify the new exception details displayed with "REJECTED" status
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select the exception state as "REJECTED"
    And I click on "Apply" Button
    Then I verify exception results are displayed with state as "REJECTED"
    When I click on the "ER" link
    Then I should see the "Submit" button should not be displayed and disabled
    Then I should see the "Save" button should not be displayed and disabled
    Then I should see the "Save & Exit" button should not be displayed and disabled
    Then I should see the "Close request" button should be displayed and enabled
    Then I should see the "Reopen" button should be displayed and enabled

  @bug
  Scenario: Verify actions on reject exceptions for non allowed CT BUY (according to configuration set)
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    #Create test data with pending status
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    #And I select "BUY" on the CostType list
    And I select the exception state as "REJECTED"
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    When I click on the "ER" link
    And I click on "Reopen" Button
    When I click on "Yes," Button
    Then I verify the "Exception request has been Re-Opened" successful message
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    # pending status exception created froma rejected exception after reopening
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select "BUY" on the CostType list
    And I select the exception state as "PENDING"
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    When I click on the "ER" link
    When I click on "Reject" Button
    And I enter reject Comments
    When I click on "Yes," Button
    Then I verify "Cost Exception Reject action not allowed for the role Administrator" message displayed

  @bug
  Scenario: Verify exception req withdraw action for non-allowable CT - BUY
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoExcepWithDrawNonllowable" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForSearchCRForWithDrawNonAllowableCT"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message
    When I click on "Withdraw" Button
    When I click on "Yes," Button
    Then I verify "Cost Exception Withdraw action not allowed for the role Administrator" message displayed

  @bug
  Scenario: Verify prerequisite link download
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select the exception state as "APPROVED"
    And I click on "Apply" Button
    #When I click on the "ER" link
    #And I wait till the page loads for "10" seconds
    #When I click on the "ODM POC Link" link inside Exception Request
    #Then I verify "Unable to download file" message displayed


  #Then I download and verify the Pre-Requisite Rule, Fiscal Calender & ODM POC Link
  Scenario: Verify error validation message for wrong format odm email file other than allowed types .zip, .html and .msg
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "Excep" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "SELL" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CREmail.png" for "uploadExcepForEmailAttach"
    Then I verify "but that file type is not allowed." message

  Scenario: Verify error validation message for attached single file size greater than 5MB
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "Excep" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "SELL" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CRODMEmailOverSize.html" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify "maximum allowed file size is 5 MB" message

  Scenario: Verify error validation message for multiple files with last single file size greater than 20 MB
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "ExcepCheck" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "SELL" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CREmail1.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CREmail2.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CREmail3.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CREmail4.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CRODMEmailSize4.html" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CRODMEmailSize1.html" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CRODMEmailSize2.html" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CRODMEmailSize3.html" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CRODMEmailOverSize.html" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify "maximum allowed file size is 5 MB" message

  Scenario: Verify error validation message for attached multiple files combined size greater than 20 MB
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "ExcepCombinedFiles" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "SELL" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CREmailSize1.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CREmailSize2.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CREmailSize3.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CREmailSize4.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CREmailSize5.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify "max consolidated file size per exception should be 20 MB" message

  Scenario: Verify Warning message on duplicate email file name when upload multiple ODM emails
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "ExcepDup" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "SELL" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CREmail1.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CREmail1.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify warning message "Duplicate file(s) are present" and click "No" button on popup displayed

  @bug
  Scenario Outline: Verify new exception request created with single odm attachment file for SELL type - backdating and midmonth req
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "<excepName>" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "<costType>" on Cost Type Combobox
    And I select "<reqType>" on Request Type Combobox
    When I upload the file "CREmail.msg" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForEmailAttachSell"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been created" successful message

    Examples: 
      | excepName             | costType | reqType                           |
      | autoExcepSellBackDate | SELL     | BACKDATE                          |
      | autoExcepSellMidMonth | SELL     | Fiscal_Start/End_Date_Not_Aligned |

  @bug
  Scenario Outline: Verify new exception request created with multiple odm attachment file for SELL type - backdating and midmonth req
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "<excepName>" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "<costType>" on Cost Type Combobox
    And I select "<reqType>" on Request Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForEmailAttachSell"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message

    Examples: 
      | excepName              | costType | reqType                           |
      | multiExcepSellBackDate | SELL     | BACKDATE                          |
      | multiExcepSellMidMonth | SELL     | Fiscal_Start/End_Date_Not_Aligned |

  Scenario Outline: Mandatory email attachment check on costtype ODM BUY
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "<excepName>" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "<costType>" on Cost Type Combobox
    And I select "<reqType>" on Request Type Combobox
    When I upload the file "CRException" for "uploadExcepForEmailAttach"
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify the "<msg>" warning message

    Examples: 
      | excepName | costType | reqType   | msg                                                                              |
      | Excep1    | ODM BUY  | BACKDATE  | ODM Attachments cannot be empty for cost type ODMBUY and request type BACKDATING |
      | Excep2    | ODM BUY  | MID MONTH | ODM Attachments cannot be empty for cost type ODMBUY                             |
      | Excep3    | BUY      | BACKDATE  | ODM Attachments cannot be empty for cost type BUY and request type BACKDATING    |
      | Excep4    | SELL     | BACKDATE  | ODM Attachments cannot be empty for cost type SELL and request type BACKDATING   |

  @bug
  Scenario Outline: Non Mandatory email attachment check on costtype SELL
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "<excepName>" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "<costType>" on Cost Type Combobox
    And I select "<reqType>" on Request Type Combobox
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForEmailAttachSell"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I verify no validation error msg displayed
    Then I verify the "Exception request has been submitted" successful message

    Examples: 
      | excepName | costType | reqType  |
      | Excep3    | SELL     | BACKDATE |

  Scenario: Verify Delete email attachment on New state exception request
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "ExcepDel" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CREmail.msg" for "uploadExcepForEmailAttach"
    And I delete "1" ODM email files attached
    Then I verify "0" files are displayed after delete action

  Scenario: Verify Delete email attachment on pending state exception request
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "ExcepEmailDelete" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CREmail1.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForEmailAttach"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on "Submit" Button
    And I click on "Yes," Button
    Then I should not see delete button for odm emails

  @skip
  Scenario: Verify Download email attachments on new state exception request
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "ExcepODMEmailDownload" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CREmail1.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    And I click on save button
    And I download and verify 2 ODM email file

  Scenario: Verify error validation msg on multiple files for CR upload
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "autoEmailMultipleFilesExcep" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    #When I upload the file "CRException" for "uploadExcepForExistingCRWithinDateRange"
    And I click on save button
    Then I verify the CR uploads panel is disabled on exception create page
    And I click on the Save and Exit button
    And I wait till the page loads for "10" seconds
    Then I should be landed on Home page with Welcome msg displayed

  Scenario: Verify error validation msg on wrong file ie non cr template for CR upload
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "execpCRWrongFile" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    When I upload the file "parentForMassUpdate" for "uploadExcepForEmailAttach"
    And I click on save button
    Then I verify the validation error msg "acknowledgement errors"
    And I click on "View error" Button
    And I click on "Ok" Button

  Scenario: Verify error validation msg on unsupported file format for CR upload
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "execpUnSuppFile121" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    When I upload the file "CRODMEmailWrongFile.png" for "uploadExcepForEmailAttach"
    Then I verify "but that file type is not allowed" message

  #And I click on save button
  # try clicking on submit and following action
  Scenario: Verify error validation message for file extensions other than .msg,.html and .zip
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "ExcepFileExtension" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "SELL" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CREmailException.xlsx" for "uploadExcepForEmailAttach"
    Then I verify "but that file type is not allowed" message

  @skip
  Scenario Outline: Verify actions on reject exceptions
    Given I log into HarmonyMTCM as "mtcmUser" with "Adminuser3"
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I select the exception state as "<state>"
    And I select "<costType>" on Cost Type Combobox
    And I click on "Apply" Button
    And I verify the search results status as "<state>"

    Examples: 
      | state    | costType |
      | PENDING  | BUY      |
      | PENDING  | SELL     |
      | PENDING  | ODM BUY  |
      #| NEW      | BUY      |
      | NEW      | SELL     |
      #| NEW      | ODM BUY  |
      #| APPROVED | BUY      |
      #|APPROVED|SELL	 |
      #| APPROVED | ODM BUY  |
      #| REJECTED | BUY      |
      #| REJECTED | SELL     |
      #| REJECTED | ODM BUY  |
      #|CLOSED	 |BUY		 |
      #|CLOSED	 |SELL	 |
      #| CLOSED   | ODM BUY  |

  #scplatform-6589 Search and download sourcing lane by MRP sites
  @skip
  Scenario: Verify when downloading pricing template from search sourcing lane the field MRP Site should be added into the existing excel export template
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I enter "autoEmailExcep" on "exceptionName" textfield
    And I click on "Apply" Button
    And I click on the "ER" link
  #  When I click on download Button and verify the result for "SearchExcep" for "verifyMRPSiteColumn"

  #PDSUPPORT-25494 10825275 - Not able to Download ODM attachments with Special Characters
  Scenario: Verify ability to Download ODM attachments with Special Characters
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveExcepToUPAndDownload" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I enter "saveExcepToUPAndDownload" on "exceptionName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    When I click on the "ER" link
    When I upload the file "CR_)ODMEmail.zip" for "uploadExcepForEmailAttach"
    And I click on save button
    And I download and verify 1 ODM email file

  @skip
  Scenario: Verify error message for create New Exception request for wrong supplier site
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWrongSuppSite" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRODMEmailHTML.html" for "uploadExcepForEmailAttach"
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRExceptionWrongSuppSite" for "uploadExcepForWrongSupp"
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "doesn't exist" on popup
    And I click on "Ok" Button

  Scenario: PDSUPPORT-25751 10837329: UCM MBI-13: UAT - Finance able to approve ER without ODM email attachment after correction
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "ExcepToChangeCostTyp" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the "platformName" textfield
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I enter "ExcepToChangeCostTyp" on "exceptionName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    When I click on the "ER" link
    And I select "ODM BUY" on Cost Type Combobox
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    And I log out of HarmonyMTCM
    #finance role
    Given I log into HarmonyMTCM as "mtcmUser" with "agnes_tseng"
    When I navigate to "Pricing" -> "Search Exception Request"
    And I click on "Clear" Button
    And I enter "ExcepToChangeCostTyp" on "exceptionName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "15" seconds
    When I click on the "ER" link
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the "ODM Attachments cannot be empty for cost type ODMBUY and request type BACKDATING" warning message

  # PDSUPPORT-24632 CSR07732432
  @skip
  Scenario: Verify valid CR file is still accepted after throwing validation for a CR wrong data file
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "validateExcepAfterWrongCRUpload" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the exceptionOwner textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I enter "testPlatform" on the platformName textfield
    And I select "ODM BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForEmailAttach"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been created" successful message
    And I click on the delete icon
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message
    When I upload the file "CRExceptionWrongSuppSite" for "uploadExcepForWrongSupp"
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "doesn't exist" on popup
    And I click on "Ok" Button
    When I upload the file "CRException" for "uploadExcepForEmailAttach"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the "Exception request has been Updated" successful message

  #Cost Exception - Enhanced Validations
  @skip
  Scenario: Verify error message for create New Exception request for BUY Type - Exception upload ODMBUY
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWithCTMismatch" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRException" for "uploadExcepForWrongSupp"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Value allowed in column CostType row 3 only if CostType is BUY" on popup
    And I click on "Ok" Button
    Then I should see the Ok button is not displayed and enabled
    Then I click and verify Close button on validation error message dialog is closed
    When I upload the file "CRException" for "uploadExcepForWrongSupp"
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Value allowed in column CostType row 3 only if CostType is BUY" on popup
    And I click on "Ok" Button
    Then I should see the Ok button is not displayed and enabled
    Then I click and verify Close button on validation error message dialog is closed

  Scenario: Verify error message for create New Exception request for ODMBUY Type - Exception upload BUY
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    #When I navigate to "Master Data Management" -> "Item Assignment"
    #And I click on "Clear" Button
    #When I set the itemNumber as "itemCRApprove"
    #And I click on "Apply" Button
    #And I "assign" responsibility "mike_quick@dell.com" to the item "itemCRApprove" selected
    #And I verify the "Responsibility has been assigned successfully" successful message
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWithCTMismatch" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I select "ODM BUY" on Cost Type Combobox
    And I select "MID MONTH" on Request Type Combobox
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForWrongCT"
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Value allowed in column CostType row 3 only if CostType is ODMBUY" on popup
    And I click on "Ok" Button
    Then I should see the Ok button is not displayed and enabled
    Then I click and verify Close button on validation error message dialog is closed

  @skip
  Scenario: Verify error message for create New Exception request for BUY Type and MID MONTH - Exception upload ODMBUY
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWithCTMismatch" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I select "BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRException" for "uploadExcepForWrongSupp"
    And I verify the warning messages "This file contains future dated cost records for backdating request" followed by clicking "Yes" button
    And I click on save button
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Value allowed in column CostType row 3 only if CostType is BUY" on popup
    And I click on "Ok" Button
    Then I should see the Ok button is not displayed and enabled
    Then I click and verify Close button on validation error message dialog is closed
    When I upload the file "CRException" for "uploadExcepForWrongSupp"
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Value allowed in column CostType row 3 only if CostType is BUY" on popup
    And I click on "Ok" Button
    Then I should see the Ok button is not displayed and enabled
    Then I click and verify Close button on validation error message dialog is closed

  @bug
  Scenario: Verify error message for create New Exception request for ODMBUY and BACKDATE Type - Exception upload BUY
    Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    When I navigate to "Master Data Management" -> "Item Assignment"
    And I click on "Clear" Button
    When I set the itemNumber as "itemCRApprove"
    And I click on "Apply" Button
    And I "assign" responsibility "mike_quick@dell.com" to the item "itemCRApprove" selected
    #And I verify the "Responsibility has been unassigned successfully" successful message
    When I navigate to "Pricing" -> "New Exception Request"
    And I enter "saveCRExcepWithCTMismatch" on the "exceptionName" textfield
    And I enter "aaron_x_chen" on the "exceptionOwner" textfield
    And I enter "adminuser8" on the exceptionApprover textfield
    And I select "ODM1" on the Applicable ODMs list
    And I select "LOB1" on the Line of Business list
    And I select "ODM BUY" on Cost Type Combobox
    And I select "BACKDATE" on Request Type Combobox
    And I enter "testPlatform" on the "platformName" textfield
    When I upload the file "CRODMEmail.zip" for "uploadExcepForEmailAttach"
    When I upload the file "CRException" for "uploadExcepForWrongSuppBUY"
    And I click on "Submit" Button
    And I click on "Yes," Button
    And I wait till the page loads for "20" seconds
    Then I verify the validation error msg "1 acknowledgement errors"
    And I click on "View error" Button
    Then I validate the warning message "Value allowed in column CostType row 3 only if CostType is ODMBUY" on popup
    And I click on "Ok" Button
    Then I should see the Ok button is not displayed and enabled
    Then I click and verify Close button on validation error message dialog is closed
#		Scenario: Verify error validation message for fileName characters length more than 255
    #Given I log into HarmonyMTCM as "mtcmUser" with "Admin1"
    #When I navigate to "Pricing" -> "New Exception Request"
    #And I enter "<excepName>" on the "exceptionName" textfield
    #And I enter "aaron_x_chen" on the exceptionOwner textfield
  #	And I enter "adminuser8" on the exceptionApprover textfield    
    #And I select "ODM1" on the Applicable ODMs list
    #And I select "LOB1" on the Line of Business list
    #And I enter "testPlatform" on the platformName textfield
    #And I select "BACKDATE" on Request Type Combobox
    #And I select "SELL" on Cost Type Combobox
    #When I upload the file "IAssumeThatThisFileNameContainsMoreThan255CharactersToUploadAsODMEmailAttachmentFileForNewExceptionCreateRequestForTheCostTypeAsODMBUYOrTheSELLAndRequestTypeAsBACKDATEUnderPricingModuleAsTh.msg" for "uploadExcepForEmailAttach"   
#		Then I verify "but that file type is not allowed." message
#		
