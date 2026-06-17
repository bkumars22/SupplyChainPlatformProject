@HarmonyAdmin
Feature: Admin Workflow Test Plan

  Scenario: Verify User commodity profile search action
    Given I log into HarmonyMTCM as "mtcmUser"
    #Then I verify the "Welcome to scplatform" page
    When I navigate to "Administration" -> "User Commodity Profile"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    #Then I should see the "Delete" button should be disabled
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "2" seconds
    Then I should see the Delete button should be displayed and enabled
    And I should see "file_download" icon is "displayed"

  Scenario: Verify User commodity profile clear action
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "User Commodity Profile"
    And I click on "Clear" Button
    And I enter "Test Profile" on "profileName" textfield
    And I enter "Test Company ItemType" on "companyItemType" textfield
    And I enter "Test CategoryName" on "categoryName" textfield
    And I click on "Clear" Button
    Then I verify the fields got cleared on User Commodity page

  Scenario: Navigate to Audit History and verify the title and other elements
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Audit History"
    And I click on "Clear" Button
    Then I verify the "Audit History" page
    #And I should see "file_download" icon is "not displayed"
    When I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify the "Audit History" page
    Then I verify search filter results are displayed
    And I should see "file_download" icon is "displayed"

  Scenario: Navigate to Manage Contacts and verify the clear button
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "19417" on "businessName" textfield
    And I enter "19417" on "contactName" textfield
    And I click on "Clear" Button
    Then I verify the fields are cleared on Manage Contacts page

  Scenario: Search Manage Contacts and verify the back button
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "19417" from the results
    Then I should be landed on "Contact Details" page
    And I click on "Back" Button
    Then I should be landed on "Contacts" page

  Scenario: Search Manage Contacts edit and verify contacts details
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "19417" on "contactName" textfield
    And I enter "19417" on "businessName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "19417" from the results
    Then I verify the "19417" details on Contact Details page
    When I edit the Contact Details of "19417"
    When I click on the Save and Exit button
    #	Then I should be landed on My Workspace page
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Contacts"
    And I enter "19417" on "contactName" textfield
    #And I enter "testContact@gmail.com" on the "selectedContact.email" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "19417" from the results
    Then I verify the "19417-edited" details on Contact Details page

  Scenario Outline: Try to edit contact details with invalid email address and verify validation message
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "Test Data" on "contactName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "Test Data" from the results
    Then I verify the "Test Data" details on Contact Details page
    When I enter "<url>" on the "selectedContact.email" textfield
    And I click on the Save and Exit button
    Then I verify "Please enter a valid email address." message on PopUp
    Then I log out of HarmonyMTCM

    Examples: 
      | url      |
      | test     |
      | test.com |
      | test@com |
      | test12   |

  Scenario Outline: Try to edit contact details with invalid postal code and verify validation message
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "Test Data" on "contactName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "Test Data" from the results
    Then I verify the "Test Data" details on Contact Details page
    When I enter "value" on the "selectedContact.postalCode" textfield
    And I click on the Save and Exit button
    Then I verify "Please enter valid zipcode." message on PopUp

    Examples: 
      | value   |
      | xyz     |
      | test123 |
      | test*   |
      |    123. |

  Scenario: Search Manage Contacts edit delete and verify contacts details
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "Test Data" on "contactName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "Test Data" from the results
    Then I verify the "Test Data" details on Contact Details page
    When I edit the Contact Details of "Test Data"
    When I click on the Save and Exit button
    #	Then I should be landed on My Workspace page
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "Test Data" on "contactName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "Test Data" from the results
    Then I verify the "Test Data-edited" details on Contact Details page
    When I delete the edited details of "Test Data"
    When I click on the Save and Exit button
    #	Then I should be landed on My Workspace page
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "Test Data" on "contactName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "Test Data" from the results
    Then I verify the "Test Data-deleted" details on Contact Details page

  Scenario Outline: Try to add contact details with invalid email address and verify validation message
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I click on "Add Contact" Button
    Then I should be landed on "Contact Details" page
    When I enter "contactName" on the "selectedContact.contactName" textfield
    And I enter "<url>" on the "selectedContact.email" textfield
    And I enter "contactBusinessName" on the "selectedContact.businessName" textfield
    And I click on the Save and Exit button
    Then I verify "Please enter a valid email address." message on PopUp

    Examples: 
      | url      |
      | test     |
      | test.com |
      | test@com |
      | test12   |

  Scenario Outline: Try to add contact details with invalid zip code and verify validation message
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I click on "Add Contact" Button
    Then I should be landed on "Contact Details" page
    When I enter "contactName" on the "selectedContact.contactName" textfield
    And I enter "<value>" on the "selectedContact.postalCode" textfield
    And I enter "contactBusinessName" on the "selectedContact.businessName" textfield
    And I click on the Save and Exit button
    Then I verify "Please enter valid zipcode." message on PopUp

    Examples: 
      | value   |
      | xyz     |
      | test123 |
      | test*   |
      |    123. |

  Scenario: Create Contact on Manage Contacts page and verify contacts details
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I click on "Add Contact" Button
    Then I should be landed on "Contact Details" page
    When I enter "contactName" on the "selectedContact.contactName" textfield
    And I enter "testEmail@email.com" on the "selectedContact.email" textfield
    And I enter "contactBusinessName" on the "selectedContact.businessName" textfield
    And I click on the Save and Exit button
    #	Then I should be landed on My Workspace page
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "contactName" on "contactName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify the added contact details

  Scenario: Create and Delete Contact on Manage Contacts page
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I click on "Add Contact" Button
    Then I should be landed on "Contact Details" page
    When I enter "testDeletecontactName" on the "selectedContact.contactName" textfield
    And I enter "testEmail@email.com" on the "selectedContact.email" textfield
    And I enter "contactBusinessName" on the "selectedContact.businessName" textfield
    And I click on the Save and Exit button
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "testDeletecontactName" on "contactName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Delete Contact" Button
    And I wait till the page loads for "5" seconds
    And I click on "Yes, Delete" confirmation Button
    And I wait till the page loads for "3" seconds
    Then I verify the "Contact(s) has been deleted successfully" successful message

  #scplatform-4999
  #CSR06813290 - One Cost Prod - Under Admin Section - Role - remove "TAMAllocationUploadUI" setting
  Scenario: Verify TAMAllocationUploadUI is not listed on Manage Roles under Business Doc tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "BUS_ADMIN"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "TAMAllocationUploadUI" with checkbox "" is not listed on Business Document on Manage Roles page

  #PDSUPPORT-25750 10837235: UCM MBI-13: Regression - Correction option is not available under Manage Roles
  Scenario: Verify Correction option is available under Manage Roles
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "BUS_ADMIN"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "ADMIN"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "BUYER"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "EM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "FINANCE"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPER_SERVICE_GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPER_GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SERVICE_GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page
    #Breadcrumbs
    And I click on the name "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPPLIER"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION_Correction" is listed on Business Document on Manage Roles page

  #CSR06922655 - One Cost Prod - Under Admin Section - Role - rename 'Upload Documents' setting description
  Scenario: Verify TAMAllocationUploadUI is not listed on Manage Roles under Business Doc tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "ADMIN"
    And I verify labelName "Supply Allocation" on the loaded page
    And I click on the "Business Document" tab on "Manage Roles" page
    And I verify labelName "FUNCTIONAL_GROUP" on the loaded page
    And I verify labelName "PARENT_FUNCTIONAL_GROUP" on the loaded page

  #	And I verify labelName "TAM" on the loaded page
  Scenario: Verify buttons on Manage Role page
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I should see the "Create Role" button should be displayed and enabled
    And I should see the "Copy Role" button should be disabled
    And I should see the "Delete Role" button should be disabled
    When I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "2" seconds
    Then I should see the "Create Role" button should be displayed and enabled
    And I should see the "Copy Role" button should be displayed and enabled
    And I should see the "Delete Role" button should be displayed and enabled

  Scenario: Create role with Copy role button and Verify new roles on Manage Roles page
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    And I wait till the page loads for "10" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "2" seconds
    And I click on "Copy Role" Button
    And I enter the value "copyAndCreateRole" on "roleId" field
    And I enter the value "copyAndCreateRoleName" on "roleName" field
    And I click on the save button
    Then I verify the "Role has been saved successfully" successful message
    When I navigate to "Administration" -> "Manage Roles"
    Then I verify "copyAndCreateRole" link is "visible" on Manage Roles page

  Scenario: Create role with create role button on Manage Roles page
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    And I click on "Create Role" Button
    And I enter the value "createRole" on "roleId" field
    And I enter the value "createRoleName" on "roleName" field
    And I click on the save button
    Then I verify the "Role has been saved successfully" successful message
    When I navigate to "Administration" -> "Manage Roles"
    Then I verify "createRole" link is "visible" on Manage Roles page
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    Then I log out of HarmonyMTCM

  Scenario: Delete and Verify new roles on Manage Role page
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    And I click on "Create Role" Button
    And I enter the value "createRoleForDelete" on "roleId" field
    And I enter the value "createRoleForDelete" on "roleName" field
    And I click on the save button
    Then I verify the "Role has been saved successfully" successful message
    When I navigate to "Administration" -> "Manage Roles"
    And I select the "createRoleForDelete" role checkBox to delete the role
    And I wait till the page loads for "2" seconds
    And I click on "Delete Role" Button
    And I verify the warning message "Are you sure you want to delete this role(s) ?" followed by clicking "Yes" button
    Then I verify the "Role(s) has been deleted successfully" successful message
    Then I verify "createRoleForDelete" link is "not visible" on Manage Roles page
    Then I log out of HarmonyMTCM

  Scenario: Try to create duplicate role name on Manage Roles page and verify the error message
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    And I click on "Create Role" Button
    And I enter the value "createDupRole" on "roleId" field
    And I enter the value "createDupRoleName" on "roleName" field
    And I click on the save button
    Then I verify the "Role has been saved successfully" successful message
    When I navigate to "Administration" -> "Manage Roles"
    Then I verify "createDupRole" link is "visible" on Manage Roles page
    When I navigate to "Administration" -> "Manage Roles"
    And I click on "Create Role" Button
    And I enter the value "createDupRole" on "roleId" field
    And I enter the value "createDupRoleName" on "roleName" field
    And I click on the save button
    Then I verify the "The role ID you entered is already in use, please select another ID" warning message
    Then I log out of HarmonyMTCM

  # Scenario: Create a role with specific menu options and login and verify
  # Scenario: Edit a role and login and verify
  # Scenario: Edit and verify the page/module wise buttons/functionalities and verify
  Scenario Outline: Verify error msgs with empty role fields
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    And I wait till the page loads for "25" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I wait till the page loads for "2" seconds
    And I click on "Copy Role" Button
    And I enter the value "<val1>" on "roleId" field
    And I enter the value "<val2>" on "roleName" field
    And I click on the save button
    Then I verify the "<msg>" warning message
    Then I log out of HarmonyMTCM

    Examples: 
      | val1 | val2 | msg                        |
      |      | role | You must enter a role id   |
      | role |      | You must enter a role name |
      |      |      | You must enter a role name |

  Scenario: Search Manage Users and verify the results
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "bruce_aug_scm" on "userId" textfield
    And I enter "MTCM-Tenant-123" on "businessName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify search filter results are displayed
    Then I log out of HarmonyMTCM

  Scenario: navigate to Manage Users and verify the clear button
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "bruce_aug_scm" on "userId" textfield
    And I enter "DELL" on "businessName" textfield
    And I enter "WWP Supply Chain Manager" on "roleId" textfield
    And I click on "Clear" Button
    Then I verify the fields are cleared on Manage Users page
    Then I log out of HarmonyMTCM

  Scenario: Search Manage Users and verify the back button
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "bruce_aug_scm" from the results
    Then I should be landed on "bruce_aug_scm" page
    And I click on "Back" Button
    Then I should be landed on "Users" page
    Then I log out of HarmonyMTCM

  @skip
  Scenario: Search Manage Users and verify user details
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "bruce_aug_scm" from the results
    Then I verify the "bruce_aug_scm" details on the User Details page
    Then I log out of HarmonyMTCM

  #Scenario: Search edit the user details by adding agent of business and verify user details
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Manage Users"
  #	And I click on "Clear" Button
  #	And I enter "bruce_aug_scm" on "userId" textfield
  #	And I click on "Apply" Button
  #	And I wait till the page loads for "10" seconds
  #	And I select the "bruce_aug_scm" from the results
  #	When I edit the users by "Add" Agent Of Business on the User Details page
  #	And I click on the Save and Exit button
  #	Then I verify the "User has been saved successfully" successful message
  #	Then I should be landed on "Users" page
  #	Then I should be landed on My Workspace page
  #	Then I should be landed on Home page with Welcome msg displayed
  #	Then I log out of HarmonyMTCM
  #scplatform-4737 CSR06732504 - [8823345] -[PIT] -UCM MBI4-scplatform Release 20.1 PIT: Manage User - Unable to remove agent of business
  Scenario: Search edit the user details by deleting the agent added and verify user details
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "bruce_aug_scm" from the results
    When I edit the users by "Add" Agent Of Business on the User Details page
    And I click on the Save and Exit button
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "bruce_aug_scm" from the results
    When I edit the users by "Delete" Agent Of Business on the User Details page
    And I click on the Save and Exit button
    #	Then I should be landed on "Users" page
    #	Then I should be landed on My Workspace page
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "bruce_aug_scm" from the results
    Then I verify the Agent of Business is deleted
    Then I log out of HarmonyMTCM

  Scenario: Create or change contact for users and verify on Manage users
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "aileen_zhou" on "userId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "aileen_zhou" from the results
    Then I should be landed on "aileen_zhou" page
    When I click on the "Contact Details" tab on "Manage Users" page
    #And I click on "Create Contact" Button
    #And I click on the "Change Contact" Button
    And I click on Change Contact Button
    And I set "contactName" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "selectedPageKeys" and "confirm" the list on the popup
    Then I verify the "contactName" details on Contact Details from "User" page
    And I click on the Save and Exit button
    #	Then I verify the "User has been saved successfully" successful message
    #	Then I should be landed on "Users" page
    #	Then I should be landed on My Workspace page
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "aileen_zhou" on "userId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "aileen_zhou" from the results
    Then I should be landed on "aileen_zhou" page
    When I click on the "Contact Details" tab on "Manage Users" page
    Then I verify the "contactName" details on Contact Details from "User" page
    Then I log out of HarmonyMTCM

  Scenario Outline: Verify validation error message for invalid email address on Manage Users
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I click on "Clear" Button
    And I enter "aaron_gao" on "userId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "aaron_gao" from the results
    When I enter "<url>" on the "selectedUser.emailAddress" textfield
    And I click on the Save and Exit button
    Then I verify "Please enter a valid email address." message on PopUp
    Then I log out of HarmonyMTCM

    Examples: 
      | url         |
      | test        |
      | test.com    |
      | test@com    |
      | test12      |
      | test@gmail. |

  Scenario: Navigate to Manage Business Entities and verify the clear button
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "DELL" on "businessName" textfield
    And I enter "DELL" on "businessId" textfield
    And I click on "Clear" Button
    Then I verify the fields are cleared on Manage Business Entities page
    Then I log out of HarmonyMTCM

  #scplatform-4653 CSR06688563 - S5 - One Cost prod - Manage Business entities - how is the supplier list getting sorted?
  Scenario: Search Manage Business Entities and verify list is sorted by Business Name
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I should be landed on "Business Entities" page
    And I verify list is sorted in ascending order
    Then I log out of HarmonyMTCM

  #scplatform-4478 CSR06630059 - S3 - One cost prod - Manage Users 'back' button not navigating back to previous page
  #CSR06616794 - S3 - search result in Business Entity inconsistent
  #Scenario: Search Manage Business Entities and verify the back button
    #Given I log into HarmonyMTCM as "mtcmUser"
    #When I navigate to "Administration" -> "Manage Business Entities"
    #And I click on "Clear" Button
    #And I enter "DELL" on "businessName" textfield
    #And I enter "MTCM-Tenant-123" on "businessId" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #And I select the "DELL" from the results
    #Then I should be landed on "Business Details" page
    #And I click on "Back" Button
    #Then I should be landed on "Business Entities" page
    #Then I verify "Business Name" column has "DELL" as value displayed under search results for all rows
    #Then I verify "Id" column has "MTCM-Tenant-123" as value displayed under search results for all rows
    #Then I log out of HarmonyMTCM

  #scplatform-4779 RE: [CSR06736329] One Cost prod - Manage Business entities - Save button function and Remove Alternate name
  Scenario: Verify Manage Business entities - Save button function and Remove Alternate name
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "DELL" on "businessName" textfield
    And I enter "DELL" on "businessId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "DELL" from the results
    Then I verify the "DELL" details on Business Entities page
    And I enter and verify alternate name "AUTO DELL"
    And I click on the save button
    And I delete and verify alternate names
    Then I log out of HarmonyMTCM

  #scplatform-4828 Manage Business Entities:The alternate name are gone after user click 'Save' 2 times.
	  #Scenario: Verify Alternate name are gone after user click 'Save' 2 times
	    #Given I log into HarmonyMTCM as "mtcmUser"
	    #When I navigate to "Administration" -> "Manage Business Entities"
	    #And I click on "Clear" Button
	    #And I enter "DELL" on "businessName" textfield
	    #And I enter "MTCM-Tenant-123" on "businessId" textfield
	    #And I click on "Apply" Button
	    #And I wait till the page loads for "10" seconds
	    #And I select the "DELL" from the results
	    #Then I verify the "DELL" details on Business Entities page
	    #And I enter and verify alternate name "AUTO DELL"
	    #And I click on the save button
	    #And I click on the save button
	    #And I delete and verify alternate names
	    #Then I log out of HarmonyMTCM

  # scplatform-4652 CSR06688553 - S3 - One Cost prod - Manage Business entities - missing 'Save' button, cannot Add Alternate Name, Selected Alternate name section is too small
  Scenario: Verify Manage Business entities - missing 'Save' button, cannot Add Alternate Name
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "DELL" on "businessName" textfield
    And I enter "DELL" on "businessId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "DELL" from the results
    Then I verify the "DELL" details on Business Entities page
    And I enter and verify alternate names "AUTO DELL" and "autoDELL"
    And I click on the save button
    And I delete and verify alternate names
    Then I log out of HarmonyMTCM

  Scenario Outline: Check Postal Code format validation on Manage Business Entities edit page
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "DELL" on "businessName" textfield
    And I enter "DELL" on "businessId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "DELL" from the results
    And I enter "<value>" on the "selectedBusiness.contact.postalCode" textfield
    And I click on the Save and Exit button
    Then I verify "Please enter valid zipcode." message on PopUp
    Then I log out of HarmonyMTCM

    Examples: 
      | value   |
      | xyz     |
      | test123 |
      | test*   |
      |    123. |

  Scenario: Change Contact Details on Manage Business Entities
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "DELL" on "businessName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "DELL" from the results
    And I click on Change Contact Button
    And I set "19417" on Find textField on popup
    And I click on "Search" Button on the popup
    And I select "1" "selectedPageKeys" and "confirm" the list on the popup
    Then I verify the "19417" details on Contact Details from "Business" page
    When I click on the Save and Exit button
    #Then I should be landed on My Workspace page
    Then I should be landed on Home page with Welcome msg displayed
    When I navigate to "Administration" -> "Manage Business Entities"
    And I enter "DELL" on "businessName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "DELL" from the results
    Then I verify the "19417" details on Contact Details from "Business" page
    Then I log out of HarmonyMTCM

  Scenario: Create Contact Details on Manage Business Entities
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "SYMANTEC" on "businessName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "SYMANTEC" from the results
    #And I should see the "Set Contact" button should be displayed and enabled
    And I click on "Create Contact" Button
    When I click on the Save and Exit button
    #	Then I verify the "Business entity has been saved successfully" successful message
    #	Then I should be landed on "Business Entities" page
    #doubt business entities or workspace
    Then I should be landed on Home page with Welcome msg displayed
    Then I log out of HarmonyMTCM

  #scplatform-3232 Removal of Member of Group Field and Addition of FG and Parent FG Fields [TFS:7099503]
  Scenario: Verify item details on Manage Items page
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Items"
    And I verify labelName "Member of Group" not found on the loaded page
    And I click on "Clear" Button
    When I set the itemNumber as "JD002"
    And I click on "Apply" Button
    And I click on the "itemNumber" to verify the details
    Then I verify the Item Number "JD002" on the popup
    Then I should be landed on "JD002" page
    And I verify labelName "Member of Group" not found on the loaded page
    Then I should see the "Back" button should be displayed and enabled
    Then I log out of HarmonyMTCM

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
  #Scenario: Create new news with role as All users on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	Then I should be landed on "News/Alerts" page
  #	When I click on "New" Button
  #	And I enter the "testDashboardNews" as "Title"
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapidView=9" as "URL"
  #	And I select the "alertFilter" with value "All Users & Roles"
  #	And I select the "alertTarget" with value "Default Window"
  #	And I click on the save button
  #	Then I verify the "testDashboardNews" "displayed" on Change Dashboard News page
  #	And I log out of HarmonyMTCM
  #	#Verifying Buyer
  #		Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testDashboardNews" link on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying GCM
  #		Given I log into HarmonyMTCM as "mtcmUser" with "ajay_arjunan@dell.com"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testDashboardNews" link on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Supplier
  #		Given I log into HarmonyMTCM as "mtcmUser" with "adele_ding_amd"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testDashboardNews" link on Dashboard page
  #	And I log out of HarmonyMTCM
  #
  #	Scenario: Verify new news addded with role as Admin user on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I wait till the page loads for "10" seconds
  #	And I log out of HarmonyMTCM
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I wait till the page loads for "10" seconds
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	Then I should be landed on "News/Alerts" page
  #	When I click on "New" Button
  #	And I enter the "testAdminNews" as "Title"
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapidView=9" as "URL"
  #	#And I enter "Administrator" on "alertFilter" Combobox with label "Restrict to Role"
  #	And I select "Administrator" on "Restrict to Role" Combobox
  #	#And I select the "alertFilter" with value "Administrator"
  #	And I select the "alertTarget" with value "Default Window"
  #	And I click on the save button
  #	Then I verify the "testAdminNews" "displayed" on Change Dashboard News page
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testAdminNews" link on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Buyer
  #		Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testAdminNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying GCM
  #		Given I log into HarmonyMTCM as "mtcmUser" with "ajay_arjunan@dell.com"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testAdminNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Supplier
  #		Given I log into HarmonyMTCM as "mtcmUser" with "adele_ding_amd"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testAdminNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #
  #	Scenario: Verify new news added with role as GCM user on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	Then I should be landed on "News/Alerts" page
  #	When I click on "New" Button
  #	And I enter the "testGCMNews" as "Title"
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapidView=9" as "URL"
  #	And I select "Global Commodity Manager" on "Restrict to Role" Combobox
  #	#And I select the "alertFilter" with value "Global Commodity Manager"
  #	And I select the "alertTarget" with value "Default Window"
  #	And I click on the save button
  #	Then I verify the "testGCMNews" "displayed" on Change Dashboard News page
  #	And I log out of HarmonyMTCM
  #		Given I log into HarmonyMTCM as "mtcmUser" with "ajay_arjunan@dell.com"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testGCMNews" link on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Admin not present
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testGCMNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Supplier not present
  #		Given I log into HarmonyMTCM as "mtcmUser" with "adele_ding_amd"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testGCMNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Buyer not present
  #		Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testGCMNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #
  #	Scenario: Verify new news added with role as Supplier on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	Then I should be landed on "News/Alerts" page
  #	When I click on "New" Button
  #	And I enter the "testSuppNews" as "Title"
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapidView=9" as "URL"
  #	#And I select the "alertFilter" with value "Supplier"
  #	And I select "Supplier" on "Restrict to Role" Combobox
  #	And I select the "alertTarget" with value "Default Window"
  #	And I click on the save button
  #	Then I verify the "testSuppNews" "displayed" on Change Dashboard News page
  #	And I log out of HarmonyMTCM
  #		Given I log into HarmonyMTCM as "mtcmUser" with "adele_ding_amd"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testSuppNews" link on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Buyer not present
  #		Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testSuppNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Admin not present
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testSuppNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying GCM not present
  #		Given I log into HarmonyMTCM as "mtcmUser" with "ajay_arjunan@dell.com"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "testSuppNews" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #
  #Scenario: Edit news for a specific role and login as that user then verify the news link on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	Then I should be landed on "News/Alerts" page
  #	When I click on "New" Button
  #	And I enter the "NewsForEdit" as "Title"
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapidView=9" as "URL"
  #	#And I select the "alertFilter" with value "Supplier"
  #	And I select "Supplier" on "Restrict to Role" Combobox
  #	And I select the "alertTarget" with value "Default Window"
  #	And I click on the save button
  #	Then I verify the "NewsForEdit" "displayed" on Change Dashboard News page
  #	And I log out of HarmonyMTCM
  #		Given I log into HarmonyMTCM as "mtcmUser" with "adele_ding_amd"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "NewsForEdit" link on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Buyer not present
  #		Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "NewsForEdit" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Admin not present
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "NewsForEdit" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying GCM not present
  #		Given I log into HarmonyMTCM as "mtcmUser" with "ajay_arjunan@dell.com"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "NewsForEdit" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	Then I should be landed on "News/Alerts" page
  #	When I wait till the page loads for "10" seconds
  #	And I "edit" the dashboard news with title "NewsForEdit"
  #	#changing the role to buyer
  #	And I log out of HarmonyMTCM
  #		Given I log into HarmonyMTCM as "mtcmUser" with "adele_ding_amd"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "NewsForEdit" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Buyer not present
  #		Given I log into HarmonyMTCM as "mtcmUser" with "aaron_lee"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "NewsForEdit" link on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying Admin not present
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "NewsForEdit" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #	#Verifying GCM not present
  #		Given I log into HarmonyMTCM as "mtcmUser" with "ajay_arjunan@dell.com"
  #	When I navigate to "Main" -> "Dashboard"
  #	Then I should be landed on My Workspace page
  #	And I verify the "NewsForEdit" link not on Dashboard page
  #	And I log out of HarmonyMTCM
  #Scenario: Edit news on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	And I log out of HarmonyMTCM
  #	Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	Then I verify the "Don't have Permission for this action" warning message
  #	When I click on "New" Button
  #	And I enter the "testNewsForEdit" as "Title"
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapidView=9" as "URL"
  #	And I click on the save button
  #	Then I verify the "testNewsForEdit" "displayed" on Change Dashboard News page
  #	When I "edit" the dashboard news with title "testNewsForEdit"
  #	Then I verify the "NewsForEdit" "displayed" on Change Dashboard News page
  #	Then I log out of HarmonyMTCM
  #Scenario: Delete news on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	When I click on "New" Button
  #	And I enter the "testDashboardNewsForDelete" as "Title"
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapidView=9" as "URL"
  #	And I click on the save button
  #	Then I verify the "testDashboardNewsForDelete" "displayed" on Change Dashboard News page
  #	When I "delete" the dashboard news with title "testDashboardNewsForDelete"
  #	Then I verify the "testDashboardNewsForDelete" "not displayed" on Change Dashboard News page
  #	Then I log out of HarmonyMTCM
  #
  #	Scenario: Verify cancel buttons on create new news on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	When I click on "New" Button
  #	And I enter the "testCancel" as "Title"
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapidView=9" as "URL"
  #	And I click on "Cancel" Button
  #	Then I verify the "testCancel" "not displayed" on Change Dashboard News page
  #	Then I log out of HarmonyMTCM
  #
  #	Scenario Outline: Verify URL format on create news on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	When I click on "New" Button
  #	And I enter the "testNews" as "Title"
  #	And I enter the "<url>" as "URL"
  #	And I click on the save button
  #	Then I verify "Please enter a valid Url" message on PopUp
  #	When I click on "Cancel" Button
  #	Then I should be landed on "News/Alerts" page
  #	Then I log out of HarmonyMTCM
  #	Examples:
  #	|url			|
  #	|xyz			|
  #	|test123		|
  #	|test@gmail.com	|
  #	|test.com		|
  #	|xyz.			|
  #
  #	Scenario: Verify empty title validation on create new on Change dashboard news page
  #		Given I log into HarmonyMTCM as "mtcmUser"
  #	When I navigate to "Administration" -> "Change Dashboard News"
  #	When I click on "New" Button
  #	And I enter the "https://jira.dev.scplatform.com/jira/secure/RapidBoard.jspa?rapdidView=9" as "URL"
  #	And I click on the save button
  #	Then I verify "Title field is required" message on PopUp
  #	When I click on "Cancel" Button
  #	Then I should be landed on "News/Alerts" page
  #	Then I log out of HarmonyMTCM
  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Upload/Manage Jobs" -> "Manage Upload Jobs"
    And I enter "Test" on "jobId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Manage Upload Jobs" page
    Then I verify "No records found to display" message
    When I navigate to "Upload/Manage Jobs" -> "Manage Upload Jobs"
    And I click on "Clear" Button
    And I enter "Test" on "jobId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Manage Upload Jobs" page
    When I navigate to "Upload/Manage Jobs" -> "Manage Upload Jobs"
    Then I verify "Test" on "jobId" textfield
    And I click on "Clear" Button
    Then I verify "" on "jobId" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Upload/Manage Jobs" -> "Manage Upload Jobs"
    And I enter "Test" on "jobId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Manage Upload Jobs" page
    Then I verify "No records found to display" message
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Upload/Manage Jobs" -> "Manage Upload Jobs"
    Then I should be landed on "Manage Upload Jobs" page
    Then I verify "Test" on "jobId" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I should be landed on "Manage Upload Jobs" page
    Then I verify "" on "jobId" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Upload/Manage Jobs" -> "Manage Upload Jobs"
    Then I should be landed on "Manage Upload Jobs" page
    Then I verify "" on "jobId" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I enter "Test" on "contactName" textfield
    And I click on "Apply" Button
    Then I should be landed on "Contacts" page
    Then I verify search filter results are displayed
    When I navigate to "Administration" -> "Manage Contacts"
    And I click on "Clear" Button
    And I enter "Test" on "contactName" textfield
    And I click on "Apply" Button
    Then I should be landed on "Contacts" page
    When I navigate to "Main" -> "Manage Contacts"
    Then I verify "Test" on "contactName" textfield
    And I click on "Clear" Button
    Then I verify "" on "contactName" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    And I enter "Test" on "contactName" textfield
    And I click on "Apply" Button
    Then I should be landed on "Contacts" page
    Then I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    Then I should be landed on "Contacts" page
    Then I verify "Test" on "contactName" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I should be landed on "Contacts" page
    Then I verify "" on "contactName" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Contacts"
    Then I should be landed on "Contacts" page
    Then I verify "" on "contactName" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
#  Scenario: Verify search criteria retained after changing pages and return
#    Given I log into HarmonyMTCM as "mtcmUser"
#    When I navigate to "Administration" -> "Manage Business Entities"
#    And I enter "Test" on "businessName" textfield
#    And I click on "Apply" Button
#    Then I should be landed on "Business Entities" page
#    Then I verify search filter results are displayed
#    When I navigate to "Administration" -> "Manage Business Entities"
#    And I click on "Clear" Button
#    And I enter "Test" on "businessName" textfield
#    And I click on "Apply" Button
#    Then I should be landed on "Business Entities" page
#    When I navigate to "Administration" -> "Manage Business Entities"
#    Then I verify "Test" on "businessName" textfield
#    And I click on "Clear" Button
#    Then I verify "" on "businessName" textfield
#    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I enter "Test" on "businessName" textfield
    And I click on "Apply" Button
    Then I should be landed on "Business Entities" page
    Then I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    Then I should be landed on "Business Entities" page
    Then I verify "Test" on "businessName" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I should be landed on "Business Entities" page
    Then I verify "" on "businessName" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    Then I should be landed on "Business Entities" page
    Then I verify "" on "businessName" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section - Data retention not applicable on Manage Users
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Users" page
    Then I verify search filter results are displayed
    When I navigate to "Administration" -> "Audit History"
    And I click on "Clear" Button
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Audit History" page
    When I navigate to "Administration" -> "Manage Users"
    Then I should be landed on "Users" page
    Then I verify "" on "userId" textfield
    And I click on "Clear" Button
    Then I verify "" on "userId" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section - Data retention not applicable on Manage Users
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Users" page
    Then I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    Then I should be landed on "Users" page
    Then I verify "" on "userId" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I should be landed on "Users" page
    Then I verify "" on "userId" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Users"
    Then I should be landed on "Users" page
    Then I verify "" on "userId" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Audit History"
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Audit History" page
    Then I verify "No records found to display" message
    When I navigate to "Administration" -> "Audit History"
    And I click on "Clear" Button
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Audit History" page
    When I navigate to "Administration" -> "Audit History"
    Then I verify "bruce_aug_scm" on "userId" textfield
    And I click on "Clear" Button
    Then I verify "" on "userId" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Audit History"
    And I enter "bruce_aug_scm" on "userId" textfield
    And I click on "Apply" Button
    Then I should be landed on "Audit History" page
    Then I verify "No records found to display" message
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Audit History"
    Then I should be landed on "Audit History" page
    Then I verify "bruce_aug_scm" on "userId" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I should be landed on "Audit History" page
    Then I verify "" on "userId" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Audit History"
    Then I should be landed on "Audit History" page
    Then I verify "" on "userId" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "User Commodity Profile"
    And I enter "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Apply" Button
    Then I should be landed on "User Commodity Profile" page
    Then I verify search filter results are displayed
    When I navigate to "Administration" -> "User Commodity Profile"
    And I click on "Clear" Button
    And I enter "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Apply" Button
    Then I should be landed on "User Commodity Profile" page
    When I navigate to "Administration" -> "User Commodity Profile"
    Then I verify "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Clear" Button
    Then I verify "" on "profileName" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "User Commodity Profile"
    And I click on "Clear" Button
    And I enter "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Apply" Button
    Then I should be landed on "User Commodity Profile" page
    #Then I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "User Commodity Profile"
    Then I verify "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I should be landed on "User Commodity Profile" page
    Then I verify "" on "profileName" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "User Commodity Profile"
    Then I verify "" on "profileName" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Commodity Profile"
    And I click on "Clear" Button
    And I enter "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Apply" Button
    #Then I verify search filter results are displayed
    When I navigate to "Administration" -> "Commodity Profile"
    And I click on "Clear" Button
    And I enter "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Apply" Button
    When I navigate to "Administration" -> "Commodity Profile"
    Then I verify "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Clear" Button
    Then I verify "" on "profileName" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Commodity Profile"
    And I enter "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Apply" Button
    Then I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Commodity Profile"
    Then I verify "BUYER WWP - OTHERS" on "profileName" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I verify "" on "profileName" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Commodity Profile"
    Then I verify "" on "profileName" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Items"
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "Search Results" page
    Then I verify search filter results are displayed
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Administration" -> "Manage Items"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    Then I should be landed on "Search Results" page
    Then I verify "" on "itemNumber" textfield
    Then I log out of HarmonyMTCM

  #scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Items"
    And I click on "Clear" Button
    And I set the itemNumber as "JD003"
    And I click on "Apply" Button
    Then I should be landed on "Search Results" page
    Then I verify search filter results are displayed
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Items"
    Then I verify "JD003" on "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "60" seconds
    Then I should be landed on "Search Results" page
    Then I verify "" on "itemNumber" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Items"
    Then I should be landed on "Search Results" page
    Then I verify "" on "itemNumber" textfield
    Then I log out of HarmonyMTCM

  Scenario: Verify Manage Upload jobs search
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Upload/Manage Jobs" -> "Manage Upload Jobs"
    And I click on "Clear" Button
    #Item AVL
    And I select "PENDING" on the Status list
    And I click on "Apply" Button
    And I wait "5" seconds
    Then I verify "Status" column has "PENDING" as value displayed under search results on any of the rows
    When I click on the Edit icon
    And I wait "15" seconds
    Then I should be landed on "Load Job" page
    And I verify the "PENDING" displayed on details page.

  Scenario: CSR06688587 - S3 - One Cost Prod - Manage Business Entities - Edit "Description" will change the supplier name.
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "DELL" on "businessName" textfield
    And I enter "DELL" on "businessId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    And I select the "DELL" from the results
    And I enter "testDescp" on the "selectedBusiness.businessEntityDesc" textfield
    And I click on the Save and Exit button
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "DELL" on "businessName" textfield
    And I enter "DELL" on "businessId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify "Description" column has "testDescp" as value displayed under search results for all rows
    And I select the "DELL" from the results
    And I enter "" on the "selectedBusiness.businessEntityDesc" textfield
    And I click on the Save and Exit button
    When I navigate to "Administration" -> "Manage Business Entities"
    And I click on "Clear" Button
    And I enter "DELL" on "businessName" textfield
    And I enter "DELL" on "businessId" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
    Then I verify "Description" column has "" as value displayed under search results for all rows

  # scplatform-5091 CSR06840950 - [OneCost]Excel Upload Error
  #Scenario: upload the User Commodity Profile Mapping
    #Given I log into HarmonyMTCM as "mtcmUser"
    #When I navigate to "Upload/Manage Jobs" -> "Admin"
    #And I upload the "UserCommodityProfileMappingUI" "UserCommodityProfile" with "userCommodityProfileMapping" & verify "" "success"
    #And I navigate to "Administration" -> "User Commodity Profile"
    #And I click on "Clear" Button
    #And I enter "ajay_arjunan@dell.com" on "userId" textfield
    #And I enter "GCM WWP - HDD" on "profileName" textfield
    #And I click on "Apply" Button
    #And I wait till the page loads for "10" seconds
    #Then I verify "User Id" column has "ajay_arjunan@dell.com" as value displayed under search results for all rows
    #Then I verify "User Name" column has "AJAY KUMAR ARJUNAN" as value displayed under search results for all rows
    #Then I verify "Profile Name" column has "GCM WWP - HDD" as value displayed under search results for all rows
    #Then I verify "Role Name" column has "Global Commodity Manager" as value displayed under search results for all rows

  Scenario: Verify for Admin roles options are available under Manage Role's Business Document tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "ADMIN"
    And I wait till the page loads for "10" seconds
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "View" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View Summary" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View a Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Add a new Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "DeleteFile" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Deactivate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Activate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "MoveItemEol" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddFG" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Read-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Unassign other users " with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Upload" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Download" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View All Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Delete Load Job" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Clear Load Event" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Correct Missing Business Alias" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "All" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Items" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Item AVL" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Boms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Platforms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Sourcing Lanes" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records Actions" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Expire Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Current Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Adjustable Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Supply Allocations" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Users" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CommodityProfileUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "UserCommodityProfileMappingUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupItemUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationDeleteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ItemEOLUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CostRecordMrpSiteUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Read Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Delete Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page

  Scenario: Verify for Bus Admin roles options are available under Manage Role's Business Document tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "BUS_ADMIN"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "View" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View Summary" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View a Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Add a new Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "DeleteFile" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Deactivate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Activate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "MoveItemEol" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddFG" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Read-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Unassign other users " with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Upload" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Download" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View All Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Delete Load Job" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Clear Load Event" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Correct Missing Business Alias" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "All" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Items" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Item AVL" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Boms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Platforms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Sourcing Lanes" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records Actions" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Expire Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Current Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Adjustable Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Supply Allocations" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Users" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CommodityProfileUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "UserCommodityProfileMappingUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupItemUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationDeleteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ItemEOLUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CostRecordMrpSiteUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Read Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Delete Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page

  Scenario: Verify for BUYER roles options are available under Manage Role's Business Document tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "BUYER"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "View" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View Summary" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View a Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Add a new Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "DeleteFile" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Deactivate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Activate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "MoveItemEol" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddFG" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Read-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Unassign other users " with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Upload" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Download" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View All Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Delete Load Job" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Clear Load Event" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Correct Missing Business Alias" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "All" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Items" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Item AVL" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Boms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Platforms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Sourcing Lanes" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records Actions" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Expire Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Current Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Adjustable Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Supply Allocations" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Users" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CommodityProfileUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "UserCommodityProfileMappingUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupItemUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationDeleteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ItemEOLUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CostRecordMrpSiteUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Read Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Delete Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page

  Scenario: Verify for GCM roles options are available under Manage Role's Business Document tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "View" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View Summary" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View a Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Add a new Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "DeleteFile" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Deactivate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Activate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "MoveItemEol" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddFG" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Read-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Unassign other users " with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Upload" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Download" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View All Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Delete Load Job" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Clear Load Event" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Correct Missing Business Alias" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "All" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Items" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Item AVL" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Boms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Platforms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Sourcing Lanes" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records Actions" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Expire Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Current Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Adjustable Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Supply Allocations" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Users" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CommodityProfileUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "UserCommodityProfileMappingUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupItemUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationDeleteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ItemEOLUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CostRecordMrpSiteUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Read Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Delete Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page

  Scenario: Verify for SERVICE_GCM roles options are available under Manage Role's Business Document tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SERVICE_GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "View" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View Summary" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View a Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Add a new Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "DeleteFile" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Deactivate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Activate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "MoveItemEol" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddFG" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Read-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Unassign other users " with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Upload" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Download" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View All Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Delete Load Job" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Clear Load Event" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Correct Missing Business Alias" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "All" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Items" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Item AVL" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Boms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Platforms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Sourcing Lanes" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records Actions" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Expire Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Current Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Adjustable Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Supply Allocations" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Users" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CommodityProfileUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "UserCommodityProfileMappingUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupItemUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationDeleteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ItemEOLUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CostRecordMrpSiteUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Read Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Delete Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page

  Scenario: Verify for SUPER_GCM roles options are available under Manage Role's Business Document tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPER_GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "View" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View Summary" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View a Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Add a new Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "DeleteFile" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Deactivate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Activate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "MoveItemEol" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddFG" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Read-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Unassign other users " with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Upload" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Download" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View All Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Delete Load Job" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Clear Load Event" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Correct Missing Business Alias" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "All" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Items" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Item AVL" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Boms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Platforms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Sourcing Lanes" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records Actions" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Expire Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Current Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Adjustable Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Supply Allocations" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Users" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CommodityProfileUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "UserCommodityProfileMappingUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupItemUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationDeleteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ItemEOLUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CostRecordMrpSiteUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Read Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Delete Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page

  Scenario: Verify for SUPER_SERVICE_GCM roles options are available under Manage Role's Business Document tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPER_SERVICE_GCM"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "View" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View Summary" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View a Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Add a new Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "DeleteFile" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Deactivate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Activate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "MoveItemEol" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddFG" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Read-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Unassign other users " with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Upload" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Download" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View All Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Delete Load Job" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Clear Load Event" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Correct Missing Business Alias" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "All" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Items" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Item AVL" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Boms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Platforms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Sourcing Lanes" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records Actions" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Expire Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Current Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Adjustable Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Supply Allocations" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Users" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CommodityProfileUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "UserCommodityProfileMappingUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupItemUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationDeleteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ItemEOLUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CostRecordMrpSiteUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Read Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Delete Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page

  Scenario: Verify for SUPPLIER roles options are available under Manage Role's Business Document tab
    Given I log into HarmonyMTCM as "mtcmUser"
    When I navigate to "Administration" -> "Manage Roles"
    Then I should be landed on "Available Roles" page
    And I click on the name "SUPPLIER"
    And I click on the "Business Document" tab on "Manage Roles" page
    Then I verify "View" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View Summary" with checkbox "BD_SOURCING_LANE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View a Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "Add a new Note" with checkbox "BD_COST_RECORD" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "Correction" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "DeleteFile" with checkbox "BD_COST_EXCEPTION" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_REBATE" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_SUPPLY_ALLOC" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Deactivate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Activate" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteParent" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "DeleteItem" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "MoveItemEol" with checkbox "BD_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Rename" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "AddFG" with checkbox "BD_PARENT_FUNCTIONAL_GROUP" is listed on Business Document on Manage Roles page
    Then I verify "Read-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Read-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Save-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-site" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-region" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "Delete-global" with checkbox "BD_TAM" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Create" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Copy" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Delete" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "Validate" with checkbox "BD_FORECAST" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "Unassign other users " with checkbox "BD_ITEM_ASSIGNMENT" is listed on Business Document on Manage Roles page
    Then I verify "View" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Save" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Assign To Self" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Delegate" with checkbox "BD_ITEM_CATEGORY" is listed on Business Document on Manage Roles page
    Then I verify "Upload" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Download" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "View All Jobs" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Delete Load Job" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Clear Load Event" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "Correct Missing Business Alias" with checkbox "BD_UPDOWN" is listed on Business Document on Manage Roles page
    Then I verify "All" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Items" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Item AVL" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Boms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Platforms" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Sourcing Lanes" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Cost Records Actions" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Expire Cost Records" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Current Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Adjustable Forecast" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Supply Allocations" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Users" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CommodityProfileUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "UserCommodityProfileMappingUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupItemUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "FunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ParentFunctionalGroupConfigUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateXLOBUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostForecastByParentFGUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationDeleteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "ItemEOLUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMMonthlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterly" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMItemCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMSupplierCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "TAMAllocationMassUpdateCFGMRPSiteUploadUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "CostRecordMrpSiteUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "PriceTAMQuarterlyException" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGMRPSiteODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "MassUpdateCostRecordPFGExtensionODMBuyUI" with checkbox "BD_UPLOAD_TYPE" is listed on Business Document on Manage Roles page
    Then I verify "Read Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Delete Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "Submit Report" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteSupplyAllocationException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityTamException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteNoResponsibilityPriceException" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostRecordPriceValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "ReadCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "SubmitCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
    Then I verify "DeleteCostForecastValidation" with checkbox "BD_REPORTS" is listed on Business Document on Manage Roles page
#	# review alerts	
##	
##	#Scenario: Manage usres-> update an user with specific role and login and check
