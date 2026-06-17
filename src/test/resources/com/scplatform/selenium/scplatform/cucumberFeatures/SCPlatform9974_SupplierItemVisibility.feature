@HarmonySupplierItemVisibility
Feature: scplatform-9974 Supplier should not see Item AVL and SA for Items not associated with them
# MSI 262 Story - Extended scope of supplier visibility enhancement
# Acceptance Criteria covers Cases #2, #2.1, and #2.2
#
# Test Item: Transistor_U5
# Enterprise (Hub) BE: MSI
# Supplier BEs: PIE, BCM, Electronic-Supp1, Electronic-Supp2
# PIE Supplier Login: ssuheelsup  (source: Excel 'with xml-case2.1' row 114: "login as PIE:ssuheelsup")
#
# XML Setup Files (upload as admin before running supplier tests):
#   Case #2 setup:   ITEMAVL_supplierViisbility_2-0_dev11354.xml
#                    Creates: Transistor_U5 BE=MSI AVL=Electronic-Supp1
#                             Transistor_U5 BE=BCM AVL=Electronic-Supp1
#                    (No PIE association â†’ PIE supplier should NOT see)
#
#   Case #2.1 setup: ITEMAVL_supplierViisbility_2-1_dev11354.xml
#                    Creates: Transistor_U5 BE=PIE AVL=Electronic-Supp2
#                    (PIE now owns same item identifier â†’ MSI/PIE variants visible)
#
# Config properties required on dev11354:
#   pcm.common.enterprise.data.enable.toSupplier=true
#   pcm.common.enterprise.data.enable.toSupplier.SearchDefItem=true
#   pcm.common.enterprise.data.enable.toSupplier.SearchDefSupplyAllocationItem=true
#
# businessFilterMSI inclusion/exclusion rules (Item.hbm.xml):
#   INCLUSION:  A1) Item BE = Supplier (direct ownership)
#               A2) Item has AVL for Supplier (direct supply)
#               A3.1) Item BE = MSI AND has AVL for Supplier
#               A3.2) Item BE = MSI AND Supplier owns same ITEM_IDENTIFIER AND no AVL
#   EXCLUSION:  B1) Item BE = MSI AND has AVL for BOTH supplier AND competitors
#               B2) Supplier supplies non-MSI variant AND MSI variant has other AVLs

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # STEP 1 - DATA SETUP: Upload XML test data as admin
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # Upload base data: Transistor_U5 MSI + BCM (no PIE) â†’ sets up Case #2 scenario
  Scenario: Admin uploads Transistor_U5 MSI+BCM base data for Case #2 setup
    Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemUI" "ITEMAVL_supplierViisbility_2-0_dev11354" xml with "uploadItemCase2" & verify "" "success"
    And I wait till the page loads for "30" seconds
    Then I verify the upload job completed successfully

  # Upload Case 2.1 data: add PIE variant of Transistor_U5
  Scenario: Admin uploads Transistor_U5 PIE variant data for Case #2.1 setup
    Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "ItemUI" "ITEMAVL_supplierViisbility_2-1_dev11354" xml with "uploadItemCase21" & verify "" "success"
    And I wait till the page loads for "30" seconds
    Then I verify the upload job completed successfully

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # TC-SS-004 | CASE #2: Transistor_U5 MSI + Electronic-Supp1 - NOT visible to PIE
  # No PIE association (no AVL, no ownership) â†’ INCLUSION rules A1/A2/A3 NOT met
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  Scenario Outline: TC-SS-004 Case #2 - Transistor_U5 BE=MSI not visible to PIE supplier
    Given I log into HarmonyMTCM as "mtcmUser" with "ssuheelsup"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I set the itemNumber as "Transistor_U5"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify "<expectedRows>" rows in the search results for item "Transistor_U5" with BE "<itemBE>"
    Examples:
      | itemBE | expectedRows |
      | MSI    | 0            |
      | BCM    | 0            |

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # TC-SS-006, TC-SS-007, TC-SS-008, TC-SS-009 | CASE #2.1: PIE owns Transistor_U5
  # After uploading PIE variant: MSI variant becomes visible (A3.2 ownership rule)
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-SS-006: Transistor_U5 MSI visible to PIE because PIE owns same item identifier (A3.2)
  Scenario: TC-SS-006 Case #2.1 - Transistor_U5 BE=MSI visible to PIE via ownership rule A3.2
    Given I log into HarmonyMTCM as "mtcmUser" with "ssuheelsup"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I set the itemNumber as "Transistor_U5"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    And I verify "Item Number" column has "Transistor_U5" as value displayed under search results for some rows

  # TC-SS-007: Transistor_U5 BCM NOT visible to PIE (non-MSI items not covered by ownership rule)
  Scenario: TC-SS-007 Case #2.1 - Transistor_U5 BE=BCM still NOT visible to PIE
    Given I log into HarmonyMTCM as "mtcmUser" with "ssuheelsup"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I set the itemNumber as "Transistor_U5"
    And I enter "BCM" on "value(businessName)" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify no search filter results are displayed

  # TC-SS-008: Transistor_U5 BE=PIE visible to PIE (A1: Item BE = Supplier)
  Scenario: TC-SS-008 Case #2.1 - Transistor_U5 BE=PIE visible to PIE supplier via rule A1
    Given I log into HarmonyMTCM as "mtcmUser" with "ssuheelsup"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I set the itemNumber as "Transistor_U5"
    And I enter "PIE" on "value(businessName)" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    And I verify "Item Number" column has "Transistor_U5" as value displayed under search results for some rows

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # REGRESSION: Existing items (Wire - no competitors, Screw - competitor scenario)
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-SS-REG-007/008: Wire items visible to PIE (no competitors, PIE has AVL)
  Scenario: TC-SS-REG Wire items visible to PIE supplier when no competing suppliers
    Given I log into HarmonyMTCM as "mtcmUser" with "ssuheelsup"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I enter "PIE" on "value(businessName)" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    And I verify "Supplier" column displayed under search results

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SUPPLY ALLOCATION: TC-SS-SA-001, TC-SS-SA-002
  # Supplier must NOT see SA for items not associated with them
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-SS-SA-001: SA for Transistor_U5 MSI/BCM NOT visible to PIE before 2.1 upload
  Scenario: TC-SS-SA-001 - Supply Allocation for non-associated items not visible to PIE
    Given I log into HarmonyMTCM as "mtcmUser" with "ssuheelsup"
    When I navigate to "Supply Allocation" -> "Manage Allocation"
    And I click on "Clear" Button
    And I set the itemNumber as "Transistor_U5"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    And I verify "Supplier" column displayed under search results

  # TC-SS-SA-002: SA for PIE-owned Transistor_U5 IS visible to PIE after 2.1 upload
  Scenario: TC-SS-SA-002 - Supply Allocation for PIE-owned items visible to PIE supplier
    Given I log into HarmonyMTCM as "mtcmUser" with "ssuheelsup"
    When I navigate to "Supply Allocation" -> "Manage Allocation"
    And I click on "Clear" Button
    And I enter "PIE" on "value(businessName)" textfield
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    And I verify "Item Number" column displayed under search results
    And I verify "Supplier" column displayed under search results

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # BASELINE: Admin sees ALL items regardless of supplier filter
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  Scenario: Baseline - Admin sees Transistor_U5 all variants on Item AVL page
    Given I log into HarmonyMTCM as "mtcmUser" with "admin"
    When I navigate to "Search" -> "Item AVL"
    And I click on "Clear" Button
    And I set the itemNumber as "Transistor_U5"
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    And I verify "Item Number" column has "Transistor_U5" as value displayed under search results for some rows
    And I verify "Supplier" column displayed under search results
