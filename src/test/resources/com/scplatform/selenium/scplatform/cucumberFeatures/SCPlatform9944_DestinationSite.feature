@Harmonyscplatform9944DestinationSite
Feature: scplatform-9944 Destination Site in Sourcing Lane and Supply Allocation
# scplatform-9944: Add Destination Site to Sourcing Lane and Supply Allocation Data
# Covers: Feature flag UI/download checks, SA upload validation, SA upload workflows,
#         SA download, AVL upload, CR MSI upload, CR Supplier upload

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SECTION 1: FEATURE FLAG â€” DISABLED (TC-1a, TC-1b)
  # Precondition: pcm.supplierAllocation.destinationSite.enabled=false
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-1a: UI grid shows NO Destination Site column when feature disabled
  @9944DestSiteDisabled
  Scenario: TC-1a Verify no Destination Site column in SA grid when feature is disabled
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Destination Site" column NOT displayed under search results

  # TC-1b: Downloaded XLSX has NO DestinationSite column when feature disabled
  @9944DestSiteDisabled
  Scenario: TC-1b Verify downloaded XLSX has no DestinationSite column when feature is disabled
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "destinationSiteColumnAbsent"

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SECTION 2: FEATURE FLAG â€” ENABLED: UPLOAD VALIDATION (TC-2a, TC-2b, TC-3b)
  # Precondition: pcm.supplierAllocation.destinationSite.enabled=true
  #               pcm.supplierAllocation.fiscalCalendarValidation.enabled=false
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-2a: Upload SA with invalid DestinationSite KUL â†’ COMPLETED/FAILURE
  @9944DestSiteEnabled
  Scenario Outline: TC-2a Upload SA with invalid DestinationSite and verify failure error
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Examples:
      | dataFile           | fileName                    | action                     | msg                                         | msgType |
      | SupplierAllocationUI | SA_TC2a_InvalidDestSite_KUL | uploadSAInvalidDestSiteKUL  | Invalid Source Destination Site combination | failure |

  # TC-2b: Upload SA with invalid SupplierSite INVALID_SITE_XYZ â†’ COMPLETED/FAILURE
  @9944DestSiteEnabled
  Scenario Outline: TC-2b Upload SA with invalid SupplierSite and verify failure error
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Examples:
      | dataFile           | fileName                       | action                         | msg                                   | msgType |
      | SupplierAllocationUI | SA_TC2b_InvalidSupplierSite    | uploadSAInvalidSupplierSiteXYZ | SupplierAllocationInvalidSupplierSite | failure |

  # TC-3b: Upload SA with valid DestinationSite KUL (GLOBALâ†’KUL mapping) â†’ COMPLETED/SUCCESS
  @9944DestSiteEnabled
  Scenario Outline: TC-3b Upload SA with valid DestinationSite KUL and verify success
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    Examples:
      | dataFile           | fileName               | action                  | msg | msgType |
      | SupplierAllocationUI | SA_TC3_ValidUpload_KUL | uploadSAValidDestSiteKUL |     | success |

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SECTION 3: SA UPLOAD WORKFLOWS (TC-SA-UP1 to TC-SA-UP5)
  # Precondition: pcm.supplierAllocation.destinationSite.enabled=true
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-SA-UP1: New supplier (Murata) same sites GLOBAL/GLOBAL, DestSite=GLOBAL â†’ Create New SA
  @9944DestSiteEnabled
  Scenario Outline: TC-SA-UP1 Upload new SA with GLOBAL/GLOBAL sites and DestSite GLOBAL
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I enter item number from "<fileName>" xlsx on "itemNumber" textfield
    And I set start date on Allocation Management History page
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile           | fileName      | action          | msg | msgType |
      | SupplierAllocationUI | SA_TC-SA-UP1  | saUploadTCSAUP1 |     | success |

  # TC-SA-UP2: Different SourceSite (ALPHATEC/WeeNet) â†’ end old GLOBAL SA â†’ Create New SA
  @9944DestSiteEnabled
  Scenario Outline: TC-SA-UP2 Upload SA with different SourceSite and verify old SA ended
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I enter item number from "<fileName>" xlsx on "itemNumber" textfield
    And I set start date on Allocation Management History page
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile           | fileName      | action          | msg | msgType |
      | SupplierAllocationUI | SA_TC-SA-UP2  | saUploadTCSAUP2 |     | success |

  # TC-SA-UP3: Same supplier/source, new DestSite=MY2 â†’ New SA alongside existing (GLOBAL unchanged)
  @9944DestSiteEnabled
  Scenario Outline: TC-SA-UP3 Upload SA with new DestSite MY2 and verify existing GLOBAL SA unchanged
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I enter item number from "<fileName>" xlsx on "itemNumber" textfield
    And I set start date on Allocation Management History page
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile           | fileName      | action          | msg | msgType |
      | SupplierAllocationUI | SA_TC-SA-UP3  | saUploadTCSAUP3 |     | success |

  # TC-SA-UP4: Re-upload same keys, different allocation values â†’ Update allocation on existing SA
  @9944DestSiteEnabled
  Scenario Outline: TC-SA-UP4 Re-upload SA with same keys and verify allocation values updated
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I enter item number from "<fileName>" xlsx on "itemNumber" textfield
    And I set start date on Allocation Management History page
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile           | fileName      | action          | msg | msgType |
      | SupplierAllocationUI | SA_TC-SA-UP4  | saUploadTCSAUP4 |     | success |

  # TC-SA-UP5: New Start Date (1-Mar) â†’ New SA created + old SA auto-ended at 28-Feb
  @9944DestSiteEnabled
  Scenario Outline: TC-SA-UP5 Upload SA with new start date and verify old SA auto-ended
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Supply Allocation"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I enter item number from "<fileName>" xlsx on "itemNumber" textfield
    And I set start date on Allocation Management History page
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile           | fileName      | action          | msg | msgType |
      | SupplierAllocationUI | SA_TC-SA-UP5  | saUploadTCSAUP5 |     | success |

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SECTION 4: FEATURE FLAG â€” ENABLED: UI/DOWNLOAD COLUMN (TC-4a, TC-4b)
  # Precondition: SA records exist (SA-UP1..5 already uploaded above)
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-4a: UI grid shows Destination Site column header when feature enabled
  @9944DestSiteEnabled
  Scenario: TC-4a Verify Destination Site column appears in SA grid when feature is enabled
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Supply Allocation" -> "Allocation Management"
    And I click on "Clear" Button
    And I enter item number from "SA_TC-SA-UP1" xlsx on "itemNumber" textfield
    And I set start date on Allocation Management History page
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I verify "Destination Site" column displayed under search results

  # TC-4b: Downloaded XLSX contains DestinationSite column when feature enabled
  @9944DestSiteEnabled
  Scenario: TC-4b Verify downloaded XLSX has DestinationSite column when feature is enabled
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "destinationSiteColumnPresent"

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SECTION 5: SA DOWNLOAD (TC-SA-DL1) â€” DEF-002 Regression Check
  # (SA records now exist from SECTION 3 uploads)
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-SA-DL1: SA Download with feature enabled â†’ XLSX contains DestinationSite column with KUL values
  @9944DestSiteEnabled
  Scenario: TC-SA-DL1 Download SA and verify DestinationSite column present with correct values
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Supply Allocation" -> "Download Allocation"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "30" seconds
    Then I verify search filter results are displayed
    When I click on "Supplier Allocation Download" Button and verify the result for "Download Allocation" for "destinationSiteValueInDownload"

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SECTION 6: AVL UPLOAD WITH DESTINATION SITE (TC-AVL-UP1 to TC-AVL-UP4)
  # Precondition: pcm.supplierAllocation.destinationSite.enabled=true
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-AVL-UP1: Upload AVL with DestSite=Avnet (existing GLOBAL) â†’ UI shows GLOBAL, Avnet
  @9944DestSiteEnabled
  Scenario Outline: TC-AVL-UP1 Upload AVL with DestSite Avnet and verify sites appended in UI
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "AVL"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile | fileName   | action         | msg | msgType |
      | ItemAVLUI | AVL_TC-UP1 | avlUploadTCUP1 |     | success |

  # TC-AVL-UP2: Second upload DestSite=Alphatec â†’ UI shows GLOBAL, Avnet, Alphatec
  @9944DestSiteEnabled
  Scenario Outline: TC-AVL-UP2 Upload AVL with DestSite Alphatec and verify three sites shown
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "AVL"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile | fileName   | action         | msg | msgType |
      | ItemAVLUI | AVL_TC-UP2 | avlUploadTCUP2 |     | success |

  # TC-AVL-UP3: New SourceSite=0000448, DestSite=Tti â†’ UI shows both source and 4 dest sites
  @9944DestSiteEnabled
  Scenario Outline: TC-AVL-UP3 Upload AVL with new SourceSite and DestSite Tti and verify all sites
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "AVL"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile | fileName   | action         | msg | msgType |
      | ItemAVLUI | AVL_TC-UP3 | avlUploadTCUP3 |     | success |

  # TC-AVL-UP4: New supplier (Walsin), SourceSite=0821572, DestSite=AVX â†’ new AVL row created
  @9944DestSiteEnabled
  Scenario Outline: TC-AVL-UP4 Upload AVL with new supplier Walsin and DestSite AVX and verify new row
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Admin"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "AVL"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile | fileName   | action         | msg | msgType |
      | ItemAVLUI | AVL_TC-UP4 | avlUploadTCUP4 |     | success |

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SECTION 7: COST RECORD â€” MSI UPLOAD (TC-CR-MSI-1 to TC-CR-MSI-5)
  # Precondition: pcm.supplierAllocation.destinationSite.enabled=true
  # All MSI CRs are expected to be Auto Approved
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-CR-MSI-1: Same supplier/source, different DestSite US1 â†’ New CR Auto Approved
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-MSI-1 MSI upload CR with different DestSite US1 and verify Auto Approved
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I wait till the page loads for "60" seconds
    And I verify "1" rows has status value as "APPROVED"
    Examples:
      | dataFile     | fileName   | action         | msg | msgType |
      | CostRecordUI | CR_MSI_TC1 | crMsiUploadTC1 |     | success |

  # TC-CR-MSI-2: Same supplier, different SourceSite XYZ â†’ New CR Auto Approved
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-MSI-2 MSI upload CR with different SourceSite and verify Auto Approved
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I wait till the page loads for "60" seconds
    And I verify "1" rows has status value as "APPROVED"
    Examples:
      | dataFile     | fileName   | action         | msg | msgType |
      | CostRecordUI | CR_MSI_TC2 | crMsiUploadTC2 |     | success |

  # TC-CR-MSI-3: Different Supplier (Supp1), same sites â†’ New CR Auto Approved
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-MSI-3 MSI upload CR with different supplier and verify Auto Approved
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I wait till the page loads for "60" seconds
    And I verify "1" rows has status value as "APPROVED"
    Examples:
      | dataFile     | fileName   | action         | msg | msgType |
      | CostRecordUI | CR_MSI_TC3 | crMsiUploadTC3 |     | success |

  # TC-CR-MSI-4: Different start date (1-Feb) â†’ New CR Auto Approved
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-MSI-4 MSI upload CR with different start date and verify Auto Approved
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I wait till the page loads for "60" seconds
    And I verify "1" rows has status value as "APPROVED"
    Examples:
      | dataFile     | fileName   | action         | msg | msgType |
      | CostRecordUI | CR_MSI_TC4 | crMsiUploadTC4 |     | success |

  # TC-CR-MSI-5: Same all keys, re-upload with new price â†’ Old CR closed, new CR Auto Approved
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-MSI-5 MSI re-upload CR with new price and verify old CR closed and new approved
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    And I wait till the page loads for "60" seconds
    And I verify "1" rows has status value as "APPROVED"
    Examples:
      | dataFile     | fileName   | action         | msg | msgType |
      | CostRecordUI | CR_MSI_TC5 | crMsiUploadTC5 |     | success |

  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
  # SECTION 8: COST RECORD â€” SUPPLIER UPLOAD (TC-CR-SUPP-1 to TC-CR-SUPP-6)
  # Precondition: pcm.supplierAllocation.destinationSite.enabled=true
  # Supplier CRs go through pending â†’ auto-approval against MSI CR
  # â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

  # TC-CR-SUPP-1: Same supplier, different DestSite US1 â†’ New CR pending/auto-approval vs MSI CR
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-SUPP-1 Supplier upload CR with different DestSite US1 and verify created
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile     | fileName    | action          | msg | msgType |
      | CostRecordUI | CR_SUPP_TC1 | crSuppUploadTC1 |     | success |

  # TC-CR-SUPP-2: Same supplier, different SourceSite XYZ â†’ New CR pending/auto-approval
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-SUPP-2 Supplier upload CR with different SourceSite and verify created
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile     | fileName    | action          | msg | msgType |
      | CostRecordUI | CR_SUPP_TC2 | crSuppUploadTC2 |     | success |

  # TC-CR-SUPP-3: Different Supplier (Supp2) â†’ New CR pending/auto-approval
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-SUPP-3 Different supplier upload CR and verify created
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile     | fileName    | action          | msg | msgType |
      | CostRecordUI | CR_SUPP_TC3 | crSuppUploadTC3 |     | success |

  # TC-CR-SUPP-4: Different start date (1-Feb) â†’ New CR pending/auto-approval
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-SUPP-4 Supplier upload CR with different start date and verify created
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile     | fileName    | action          | msg | msgType |
      | CostRecordUI | CR_SUPP_TC4 | crSuppUploadTC4 |     | success |

  # TC-CR-SUPP-5: Same keys, existing=Approved, re-upload Pending â†’ Close old, create replacement
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-SUPP-5 Re-upload Pending CR when existing Approved and verify replacement created
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile     | fileName    | action          | msg | msgType |
      | CostRecordUI | CR_SUPP_TC5 | crSuppUploadTC5 |     | success |

  # TC-CR-SUPP-6: Same keys, existing=Pending, re-upload Pending â†’ Replace existing Pending CR
  @9944DestSiteEnabled
  Scenario Outline: TC-CR-SUPP-6 Re-upload Pending CR when existing Pending and verify replaced
    Given I log into HarmonyMTCM as "mtcmUser" with "kswamy"
    When I navigate to "Upload/Manage Jobs" -> "Pricing"
    And I upload the "<dataFile>" "<fileName>" xlsx with "<action>" & verify "<msg>" "<msgType>"
    When I navigate to "Pricing" -> "Cost Records"
    And I click on "Clear" Button
    And I click on "Apply" Button
    And I wait till the page loads for "20" seconds
    Then I verify search filter results are displayed
    Examples:
      | dataFile     | fileName    | action          | msg | msgType |
      | CostRecordUI | CR_SUPP_TC6 | crSuppUploadTC6 |     | success |
