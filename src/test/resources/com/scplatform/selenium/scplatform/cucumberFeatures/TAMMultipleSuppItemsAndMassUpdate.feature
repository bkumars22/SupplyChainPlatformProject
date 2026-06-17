@HarmonyTamMultiSuppItem
Feature: TAM Multiple Supp Items workflow

  Scenario Outline: Test data Upload Item with different values and verify success message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"

    Examples: 
      | dataFile  | fileName   | action           | msg | msgType |
      | ItemAVLUI | ItemUpload | uploadItemForTAM |     | success |

  Scenario: Test Data Create a functional Group with the items uploaded
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Functional Group"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem"
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Group" Button
    And I "create" the items to the Functional Group "AutoItemGroup" with "Save All"
    Then I verify the "Functional Group Saved" successful message

  #scplatform-4646 Cell near to supplier allocation(Item allocation column) and Unnormalized allocation is clickable
  Scenario: Create a Global TAM set supp Allocation values and verify the saved TAM on UI.
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    And I verify the cell near supp alloc and above item alloc are not clickable

  Scenario: Create a Global TAM by entering supp allocation value on a column click on select all icon and verify the saved TAM on UI.
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supplier Allocation value "100" on column "1"
    And I select All Supplier allocation icon with value from the column "1"
    And I set Item Allocation value "100" on column "1"
    And I select All Item allocation icon with value from the column "1"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

  Scenario: Update Global TAM and without saving switch to region and site level and verify warning popup
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supplier Allocation value "200" on column "1"
    And I set Item Allocation value "100" on column "1"
    And I expand Filter icon on Header section
    And I click on "Region" TAM Planner
    And I click "No" on the warning popup with message "Your changes have not been saved"
    And I click on "Site" TAM Planner
    And I click "Yes" on the warning popup with message "Your changes have not been saved"

  Scenario Outline: Search for a Global TAM then select and verify invalid values for item allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Item Allocation Value "<value>"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Item Allocation Total Should be 100% For One Supplier"

    Examples: 
      | value |
      |    -1 |
      |    10 |
      |   110 |

  Scenario Outline: Search for a Global TAM select and verify invalid values for supplier allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<value>"
    And I click on "Save" Button
    And I wait till the page loads for "10" seconds
    Then I click "OK" on the warning popup with message "Total Allocation Should be 100 %"

    Examples: 
      | value |
      |    85 |
      |   150 |

  Scenario Outline: Search for a Global TAM select and verify error message for non allowed characers values for supplier allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value "100"
    And I set Supplier Allocation value "<value>" on column "1"
    Then I click "OK" on the warning popup with message "Please Enter Only Number"

    Examples: 
      | value |
      | *     |
      | abc   |
      | xyz*  |
      | abc12 |

  Scenario Outline: Search for a Global TAM select and verify error message for non allowed characers values for item allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I set Supply Allocation Value "100"
    And I set Item Allocation value "<value>" on column "2"
    Then I click "OK" on the warning popup with message "Please Enter Only Number"

    Examples: 
      | value |
      | *     |
      | abc   |
      | xyz*  |
      | abc12 |

  Scenario Outline: Search for a Global TAM and set valid supplier values for allow hedging range 80 to 200 Percentage and verify success msg
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<hedgeValue>"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

    Examples: 
      | hedgeValue |
      |         85 |
      |        100 |
      |        150 |
      |        200 |

  Scenario Outline: Allow hedging range 80 to 200 Percentage and verify invalid values error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<hedgeValue>"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Allocation Should be in the Hedging Range"

    Examples: 
      | hedgeValue |
      |         20 |
      |         60 |
      |        250 |

  Scenario: Allow hedging range 80 to 200 Percentage and verify null values error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value ""
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Allocation Should be in the Hedging Range"

  Scenario: Allow hedging range 80 to 200 Percentage add valid value and later invalid and save to verify invalid values error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "200"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message
    When I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "220"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Allocation Should be in the Hedging Range"

  Scenario: Search for a created Global TAM and verify reset button
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "150"
    And I click on "Save" Button
    And I wait till the page loads for "5" seconds
    Then I click "OK" on the warning popup with message "Total Allocation Should be 100 %"
    And I click on "Reset" Button
    And I set Item Allocation Value "10"
    And I click on "Save" Button
    And I wait till the page loads for "5" seconds
    Then I click "OK" on the warning popup with message "Item Allocation Total Should be 100% For One Supplier"
    And I click on "Reset" Button
    And I set Supply Allocation Value "100"
    And I click on "Save" Button
    And I wait till the page loads for "2" seconds
    Then I verify the "Allocation Group Saved" successful message

  Scenario: Copy supply allocation values to other buckets using dropdown Copy supplier allocations to all the buckets option
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supplier Allocation value "10" on column "1"
    And I click on "Copy supplier allocations to all the buckets" from dropdown on column "1"
    Then I verify Supply Allocation Value "10" on all fields
    When I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supplier Allocation value "100" on column "1"
    And I click on "Copy supplier allocations to all the buckets" from dropdown on column "1"
    Then I verify Supply Allocation Value "100" on all fields

  Scenario: Copy Item allocation values to other buckets using dropdown Copy Item allocations to all the buckets option
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Item Allocation value "10" on column "1"
    And I click on "Copy item allocations to all the buckets" from dropdown on column "1"
    Then I verify Item Allocation Value "10" on all fields
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Item Allocation value "100" on column "1"
    And I click on "Copy item allocations to all the buckets" from dropdown on column "1"
    Then I verify Item Allocation Value "100" on all fields

  Scenario: Copy Item and Supplier allocation values to other buckets using dropdown Copy Supplier and Item allocations to all option
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Item Allocation value "10" on column "1"
    And I set Supplier Allocation value "10" on column "1"
    And I click on "Copy supplier & item allocations to all the buckets" from dropdown on column "1"
    Then I verify Item Allocation Value "10" on all fields
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Item Allocation value "100" on column "1"
    And I set Supplier Allocation value "100" on column "1"
    And I click on "Copy supplier & item allocations to all the buckets" from dropdown on column "1"
    Then I verify Item Allocation Value "100" on all fields
    And I verify Supply Allocation Value "100" on all fields
    When I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

  Scenario: Verify Hide Items button on Global Allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    Then I should see "AutoItem" item is "displayed" on global page
    When I "select" Hide Items action
    Then I should see "AutoItem" item is "not displayed" on global page
    When I "deSelect" Hide Items action
    Then I should see "AutoItem" item is "displayed" on global page

  Scenario: Verify Audit History page from Global Allocation page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    When I click on the "history" icon
    And I wait till the page loads for "5" seconds
    Then I should be landed on "Audit History(Allocation)" page
    When I wait till the page loads for "5" seconds
    Then I verify "User Role" column has value "ADMIN" displayed under search results for all rows
    Then I verify "Group Name" column has value "AutoItemGroup" displayed under search results for all rows
    Then I verify "Site" column has value "WW" displayed under search results for all rows
    And I click on Close button on Audit History page
    Then I should be landed on "Allocation Management" page

  Scenario: Verify error label as Start Date on Allocation Management History
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Allocation" -> "Supply Allocation Exception"
    Then I verify labelName "Group Name" on the loaded page
    Then I verify labelName "Multiple Group Names" on the loaded page
    When I navigate to "Reports" -> "Submit/View Reports"
    And I expand Filter icon on Header section
    And I select "Supply Allocation Exception Report" on "Report Type" Combobox
    And I wait till the page loads for "5" seconds
    And I expand Filter icon on Header section
    Then I verify labelName "Group Name" on the loaded page
    Then I verify labelName "Multiple Group Names" on the loaded page

  Scenario Outline: Search for a Region TAM and verify 80 to 200 percentage values for supply allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<hedgeValue>"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

    Examples: 
      | hedgeValue |
      |         85 |
      |        100 |
      |        150 |
      |        200 |

  Scenario Outline: On Region Tab Allow hedging range 80 to 200 Percentage and verify invalid values error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<hedgeValue>"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Allocation Should be in the Hedging Range"

    Examples: 
      | hedgeValue |
      |         20 |
      |         60 |
      |        250 |

  Scenario: On Region Tab Allow hedging range 80 to 200 Percentage and verify null values error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value ""
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Allocation Should be in the Hedging Range"

  Scenario: Create a Region TAM set supp Allocation values and verify the saved TAM on UI.
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

  Scenario Outline: Search for a Region TAM and verify 80 to 200 percentage values for supply allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<value>"
    And I click on "Save" Button
    And I wait till the page loads for "10" seconds
    Then I click "OK" on the warning popup with message "Total Allocation Should be 100 %"

    Examples: 
      | value |
      |    85 |
      |   150 |

  Scenario Outline: Search for a Region TAM then select and verify invalid values for item allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Item Allocation Value "<value>"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Item Allocation Total Should be 100% For One Supplier"

    Examples: 
      | value |
      |    -1 |
      |    10 |
      |   110 |

  Scenario Outline: Search for a Region TAM select and verify error message for non allowed characers values for supplier allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I set Supplier Allocation value "<value>" on column "1"
    Then I click "OK" on the warning popup with message "Please Enter Only Number"

    Examples: 
      | value |
      | *     |
      | abc   |
      | xyz*  |

  Scenario Outline: Search for a Region TAM select and verify error message for non allowed characers values for item allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I set Item Allocation value "<value>" on column "1"
    Then I click "OK" on the warning popup with message "Please Enter Only Number"

    Examples: 
      | value |
      | *     |
      | abc   |
      | xyz*  |

  Scenario: Update Region TAM and without saving switch to global and site level and verify warning popup
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supplier Allocation value "200" on column "1"
    And I set Item Allocation value "100" on column "1"
    And I expand Filter icon on Header section
    And I click on "Global" TAM Planner
    And I click "No" on the warning popup with message "Your changes have not been saved"
    And I click on "Site" TAM Planner
    And I click "Yes" on the warning popup with message "Your changes have not been saved"

  Scenario Outline: Search for a Site level TAM and verify 80 to 200 percentage values for supply allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<hedgeValue>"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

    Examples: 
      | hedgeValue |
      |         85 |
      |        100 |
      |        150 |
      |        200 |

  Scenario Outline: On Site level Tab Allow hedging range 80 to 200 Percentage and verify invalid values error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<hedgeValue>"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Allocation Should be in the Hedging Range"

    Examples: 
      | hedgeValue |
      |         20 |
      |         60 |
      |        250 |

  Scenario: On Site level Tab Allow hedging range 80 to 200 Percentage and verify null values error message
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value ""
    And I set Supplier Allocation value "" on column "2"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Allocation Should be in the Hedging Range"

  Scenario: Create a Site level TAM set supp Allocation values and verify the saved TAM on UI.
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I click on "Save" Button
    Then I verify the "Allocation Group Saved" successful message

  Scenario Outline: Search for a Site level TAM and verify 80 to 200 percentage values for supply allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "<value>"
    And I click on "Save" Button
    And I wait till the page loads for "10" seconds
    Then I click "OK" on the warning popup with message "Total Allocation Should be 100 %"

    Examples: 
      | value |
      |    85 |
      |   150 |

  Scenario Outline: Search for a Site level TAM then select and verify invalid values for item allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "deSelect" AllowHeding range between 80 to 200 Percentage
    And I set Supply Allocation Value "100"
    And I set Item Allocation Value "<value>"
    And I click on "Save" Button
    Then I click "OK" on the warning popup with message "Item Allocation Total Should be 100% For One Supplier"

    Examples: 
      | value |
      |    -1 |
      |    10 |
      |   110 |

  Scenario Outline: Search for a Site level TAM select and verify error message for non allowed characers values for supplier allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I set Supplier Allocation value "<value>" on column "1"
    Then I click "OK" on the warning popup with message "Please Enter Only Number"

    Examples: 
      | value |
      | *     |
      | abc   |
      | xyz*  |

  Scenario Outline: Search for a Site level TAM select and verify error message for non allowed characers values for item allocation values
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I set Item Allocation value "<value>" on column "1"
    Then I click "OK" on the warning popup with message "Please Enter Only Number"

    Examples: 
      | value |
      | *     |
      | abc   |
      | xyz*  |

  Scenario: Update Site level TAM and without saving switch to region and site level and verify warning popup
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Site" TAM Planner
    And I click on "Clear" Button
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I "select" AllowHeding range between 80 to 200 Percentage
    And I set Supplier Allocation value "200" on column "1"
    And I set Item Allocation value "100" on column "1"
    And I expand Filter icon on Header section
    And I click on "Region" TAM Planner
    And I click "No" on the warning popup with message "Your changes have not been saved"
    And I click on "Site" TAM Planner
    And I click "Yes" on the warning popup with message "Your changes have not been saved"

  #scplatform-4649 -Null error when click on the Past Allocation
  Scenario: Search created Tam and verify on past TAM exists for it
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I select "Past" values on allocation management
    Then I verify the "No past TAM exist for selected date" warning message

  #scplatform-4638 -Total Allocation Font is not Bold
  Scenario: Search created Tam and verify total allocation font is not bold
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    Then I verify total alloc value font is bold

  Scenario: Navigate to Allocation page and verify error message for empty group name search
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter "" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify the "Select Functional Group Name" warning message

  Scenario: Navigate to Allocation page and verify error message for non created group name search
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter "pqr" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify the "CFG Functional Group Not Exist with name pqr" warning message

  Scenario: Navigate to Allocation page and verify clear button on search filter
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter "xyz" on the "groupName" textfield
    And I enter "123" on the "itemNumber" textfield
    And I click on "Clear" Button
    Then I verify the fields got cleared on allocation page

  Scenario: Navigate to Region Allocation page and verify error message for empty group name search
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter "" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify the "Select Functional Group Name" warning message

  Scenario: Navigate to Region Allocation page and verify error message for none selection on region dropdown
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    Then I verify the "" value selected on dropdown with name "region"
    And I set "Region" dropdown with Value ""
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    Then I verify "Region Not Selected" message displayed

  Scenario: Navigate to Region Allocation page and verify error message for none selection on region dropdown and null FG name
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    Then I verify the "" value selected on dropdown with name "region"
    And I set "Region" dropdown with Value ""
    And I enter "" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify the "Region Not Selected" error message displayed
    And I verify the "Select Functional Group Name" error message displayed

  Scenario: Navigate to Region Allocation page and verify error message for non created group name search
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter "xyz" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify the "CFG Functional Group Not Exist with name xyz" warning message

  Scenario: Navigate to Region Allocation page and verify clear button on search filter
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I enter "xyz" on the "groupName" textfield
    And I enter "123" on the "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    Then I verify the fields got cleared on "region" allocation page

  Scenario: Navigate to Site level Allocation page and verify error message for empty group name search
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter "" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify the "Select Functional Group Name" warning message

  Scenario: Navigate to Site level Allocation page and verify error message for none selection on region dropdown
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    Then I verify the "" value selected on dropdown with name "region"
    And I set "Region" dropdown with Value ""
    And I enter "" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify the "Site Not Selected" error message displayed
    And I verify the "Select Functional Group Name" error message displayed

  Scenario: Navigate to Site level Allocation page and verify error message for non created group name search
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter "xyz" on the "groupName" textfield
    And I click on "Apply" Button
    Then I verify the "CFG Functional Group Not Exist with name xyz" warning message

  Scenario: Navigate to Site level Allocation page and verify clear button on search filter
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    And I set "Region" dropdown with Value "APCC"
    And I set "Site" dropdown with Value "APCC-APCC"
    And I enter "xyz" on the "groupName" textfield
    And I enter "123" on the "itemNumber" textfield
    And I click on "Clear" Button
    And I click on "Site" TAM Planner
    Then I verify the fields got cleared on "siteDescription" allocation page

  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I enter "fgMass2" on the "groupName" textfield
    And I click on "Apply" Button
    Then I should be landed on "Allocation Management" page
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    Then I should be landed on "Allocation Management" page
    And I verify "" on the "groupName" textfield

  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter "fgMass2" on the "groupName" textfield
    And I click on "Apply" Button
    Then I should be landed on "Allocation Management" page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    Then I should be landed on "Allocation Management" page
    And I verify "" on the "groupName" textfield
    And I log out of HarmonyMTCM

  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    And I click on "Clear" Button
    Then I verify labelName "Bucket Start Date" on the loaded page
    Then I verify labelName "Bucket End Date" on the loaded page
    And I select "CFG" on "Group Type" Combobox
    And I click on "Apply" Button
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    Then I verify the "CFG" value selected on "Group Type" comboBox
    And I click on "Clear" Button
    Then I verify the "" value selected on "Group Type" comboBox

  # scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    And I click on "Clear" Button
    And I select "CFG" on "Group Type" Combobox
    And I click on "Apply" Button
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    Then I verify the "CFG" value selected on "Group Type" comboBox
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify the "" value selected on "Group Type" comboBox
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    Then I verify the "" value selected on "Group Type" comboBox

  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I enter "fgMass2" on "functionalGroupName" textfield
    And I click on "Apply" Button
    Then I should be landed on "Download Allocation" page
    When I navigate to "Supply Allocation" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I set group name as "TestingGroupNow"
    And I click on "Apply" Button
    Then I should be landed on "Manage Parent" page
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    Then I verify "fgMass2" on "functionalGroupName" textfield
    And I click on "Clear" Button
    Then I should be landed on "Download Allocation" page
    Then I verify "" on "functionalGroupName" textfield

  ##  scplatform-4537 Harmony Feedback - Improvisation for Search Filter section
  Scenario: Verify search criteria retained after changing pages and return
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    And I click on "Clear" Button
    And I enter "fgMass2" on "functionalGroupName" textfield
    And I click on "Apply" Button
    Then I should be landed on "Download Allocation" page
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    Then I verify "fgMass2" on "functionalGroupName" textfield
    And I click on "Clear" Button
    And I click on "Apply" Button
    Then I should be landed on "Download Allocation" page
    And I wait till the page loads for "30" seconds
    Then I verify "" on "functionalGroupName" textfield
    And I log out of HarmonyMTCM
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Download Allocation"
    Then I should be landed on "Download Allocation" page
    Then I verify "" on "functionalGroupName" textfield

  #scplatform-4718 Allocation Management History: Null error while searching CFG with no past allocation
  #scplatform-4795 Should be Allocation Management History
  Scenario: Search for a Tam and verify no nullable error on ALloc Management History page
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Allocation Management History"
    And I enter "MASSUPDATEUI" on the "groupName" textfield
    And I set start date on Allocation Management History page
    And I click on "Apply" Button
    Then I should be landed on "Allocation Management History" page

  #scplatform-4692 Operation Code Value get reset after click on audit type drop down
  #scplatform-4958 At search filter,OpCode is reset to ALL after user click APPLY.
  #Scenario: Verify opcode is not reset to ALL
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    #When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    #And I click on "Clear" Button
    #And I select "FG" on the AuditType list
    #And I select "FG CREATED" on "Operation Code" Combobox
    #And I select "CFG" on "Group Type" Combobox
    #And I click on "Apply" Button
    #And I verify the "FG CREATED" value selected on dropdown with name "value(operationCode)"
    #And I verify the "FG" value selected on dropdown with name "Audit Type"

  ## # #scplatform-4771 Could not execute query error for TAM Cascade Allocation Delta
  #Scenario: Verify TAM Cascade Allocation Delta Download
  #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
  #When I navigate to "Administration" -> "Admin Upload"
  #And I click on the Download tab
  #And I enter "TAMCascadeAllocationDelta" on the "combobox-example-name" textfield
  ###And I select "TAMCascadeAllocationDelta" on "Data File" Combobox
  #And I click on "Download" Button
  #Then I should be landed on "Results" page
  #scplatform-4957 The total record is changed when user change the page size from 10 to 100
  Scenario: Verify The total records count remains unchanged when page size is changed
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    And I click on "Clear" Button
    #And I select "FG" on the AuditType list
    #And I select "FG CREATED" on "Operation Code" Combobox
    And I select "CFG" on "Group Type" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    And I get the total records count displayed
    And I select "100" on "Page Size" Combobox
    And I wait till the page loads for "25" seconds
    Then I verify "100" rows listed without selection option
    #And I verify the total records count remains same
    When I select "20" on "Page Size" Combobox
    And I wait till the page loads for "25" seconds
    Then I verify "20" rows listed without selection option
    #And I verify the total records count remains same

  ### ##scplatform-4695 Supply Allocation Audit History: Change type to group type in result list
  @skip
  Scenario: Verify group type column header is visible after search results
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    And I click on "Clear" Button
    And I select "FG CREATED" on "Operation Code" Combobox
    And I select "CFG" on "Group Type" Combobox
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify "Group Type" column displayed under search results

  ## ##scplatform-4665
  ### #scplatform-4722 Supply allocation exception Report: No FG-type in the download file
  ## ##scplatform-4676 The data is not sorted properly in supply allocation exception report via Report workflow.
  @skip
  Scenario: Verify group type column header is visible after search results
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Reports" -> "Submit/View Reports"
    And I expand Filter icon on Header section
    And I select "Supply Allocation Exception Report" on "Report Type" Combobox
    And I wait till the page loads for "5" seconds
    And I set "Start Date" as today on Start Date Calendar
    And I click on "Submit" Button
    And I expand Filter icon on Header section
    And I wait till the page loads for "30" seconds
    Then I should be landed on "Available Reports" page
    And I click on download Button and verify the result for "suppAllocExcepRep" for "verifySuppAllocReportDetails"

  # scplatform-4861 Wrong error message for the mass update for the suppliers not matching
  #skip ths test fr ps box, as flexattributes causing issues for uploading testDat for ths test
   #Scenario: Verify error message for the mass update for the suppliers not matching
    #Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    #And I clear the testData for MassUpdate multipleSupplier
    #When I navigate to "Supply Collaboration" -> "Manage Allocation"
    #And I click on "Clear" Button
    #And I enter "SEBRING" on the "groupName" textfield
    #And I click on "Apply" Button
    #When I "select" AllowHeding range between 80 to 200 Percentage
    #And I set Supply Allocation Value on complete row "1" with "100"
    #And I set Item Allocation value "100" on column "1"
    #And I click on "Copy item allocations to all the buckets" from dropdown on column "1"
    #And I wait "50" seconds
    #And I click on "Mass Update" Button
    #And I verify warning message "MassUpdate will copy only saved data" and click "Yes" button on popup displayed
    #Then I verify "Parent doesn't exist for FG: SEBRING" message displayed
    #And I verify the "Allocation Group Saved" successful message along with expected error
    #And I click "Assign" on the warning popup with message as "To do Mass update Create/Add Parent Group"
    #And I "Assign Parent Group" parent "PG-CFG202010JulyA" from allocation page
    #And I wait till the page loads for "15" seconds
    #And I click on "Mass Update" Button
    #Then I verify the "Suppliers are not matching for the FG: [CFG202010JulyA01, CFG20200722%AZ]" warning message
    #
  ## scplatform-4384 The date displaying in the Supplier Allocation template is not matching with the Manage Allocation screen
  Scenario: Verify supplier allocation template data
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter "SEBRING" on the "groupName" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
   # And I click on "supplierDownload" Button and verify the result for "Manage Allocation" for "suppAllocDateVerify"

  ## ##scplatform-4893 For existing CFG's the persisted data is not showing in the delete UI when click on the delete button
  Scenario: Verify supplier allocation download data for both supplier and item from Manage Allocation page.
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Collaboration" -> "Manage Allocation"
    And I click on "Clear" Button
    And I click on "Region" TAM Planner
    And I set "Region" dropdown with Value "DAO"
    And I enter the groupName "AutoItemGroup" on "groupName" Field
    And I click on "Apply" Button
    And I wait till the page loads for "10" seconds
   # And I click on "supplierDownload" Button and verify the result for "Download Allocation" for "suppAllocRowVerify"

  ##scplatform-4706 Manage Item: Functional Group column is blanked.
  Scenario: Verify FG and Parent Group column is not Blank
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    And I clear the testData for item belonging group and parent
    When I navigate to "Supply Collaboration" -> "Manage Parent Group"
    And I click on "Clear" Button
    And I select "Yes" on "Show Group Without Parent" Combobox
    And I set group name as "AutoItemGroup"
    And I click on "Apply" Button
    And I wait till the page loads for "5" seconds
    And I get the fgName from the search results on row "1"
    And I select first "1" rows from the "selectedPageKeys" "checkbox" list
    And I click on "Create Parent Group" Button
    And I save the parent group "createNewParent"
    Then I verify the "Parent Group Saved" successful message
    When I navigate to "Administration" -> "Manage Items"
    And I click on "Clear" Button
    And I set the itemNumber as "AutoItem" on Manage Items to verify the parent name
    And I click on "Apply" Button
    Then I should be landed on "Search Results" page
    And I verify functionalGroup name as "AutoItemGroup" on groupName Column
    And I verify parentName as "createNewParent" on parentName column

  Scenario: Verify Autocomplete Bucket Start Date and Bucket End Date are added in search filters
    Given I log into HarmonyMTCM as "mtcmUser" with "adminuser10"
    When I navigate to "Supply Allocation" -> "Supply Allocation Audit History"
    And I click on "Clear" Button
    And I enter the "FY2021FQ01FM01" on "value(bucketStartDate)" autoComplete Field
    And I click on "Apply" Button
    Then I verify search filter results are displayed
    #And I click on "Clear" Button
    #And I enter the "FY2021FQ01FM01" on "value(bucketEndDate)" autoComplete Field
    #And I click on "Apply" Button
    #Then I verify search filter results are displayed
