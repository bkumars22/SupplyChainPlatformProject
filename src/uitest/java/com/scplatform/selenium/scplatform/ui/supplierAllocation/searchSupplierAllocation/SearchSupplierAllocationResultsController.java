/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;
import com.test.selenium.scplatform.modelViewController.SCPlatformSearchResultsController;
import com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.details.SupplierAllocationController;
import com.test.selenium.scplatform.ui.supplierAllocation.searchSupplierAllocation.details.SupplierAllocationDetailsController;

/**
 * Navigation: Supply Allocation -> Search Supply Allocation
 *
 * @author dgenrich
 *
 * @see #select(String)
 */
public class SearchSupplierAllocationResultsController extends SCPlatformSearchResultsController {
    protected SearchSupplierAllocationResultsPage page;

    public SearchSupplierAllocationResultsController() {
        super();
        page = new SearchSupplierAllocationResultsPage();
    }

    @Override
    public PageImpl getView() {
        if (page == null) {
            page = new SearchSupplierAllocationResultsPage();
        }
        return page;
    }

    /**
     * Selects the radio button for the row where 'text' is found
     * 
     * @param text
     *            This can be any text on the row. Typically will be Item
     *            Number.
     */
    public void select(String text) {
        select(text, page.tableLocator());
    }

    /**
     * This does not do a search. The search results should already be listed.
     * The results are validated against the expectedData. If
     * validateDetails=true, then the item radio button is then selected and
     * {@link SupplierAllocationDetailsController#validate(SupplierAllocation)}
     * is called.
     * 
     * @param expectedData
     *            {@link SupplierAllocation} data to validate
     * @param validateDetails
     *            True to select the item radio button and validate the detail
     *            page.
     * @return True if validation successful.
     * 
     * @see SearchSupplierAllocationController#validate(List, boolean)
     */
    public boolean validate(SupplierAllocation expectedData, boolean validateDetails) {
        boolean success = true;
        boolean verified = true;

        String supplierSite = expectedData.getSupplierSite();

        List<SearchSupplierAllocationResultsModel> actualModel = page.parseResults();
        SearchSupplierAllocationResultsModel actual = findActual(actualModel, expectedData.getCustomerItemIdentifier(),
                supplierSite);
        if (actual == null) {
            JLog.error(String.format("Unable to find Item '%s' with Supplier Site '%s'",
                    expectedData.getCustomerItemIdentifier(), supplierSite), TakeScreenshot.True);
            return false;  // ← STOP HERE, don't continue!
        }

        verified = verify(actual.getDisplayName("itemNumber"), actual.getItemNumber(),
                expectedData.getCustomerItemIdentifier());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("supplierItemNumber"), actual.getSupplierItemNumber(),
                expectedData.getSupplierItemIdentifier());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("supplier"), actual.getSupplier(),
                expectedData.getSupplierBusinessEntity());
        success = (verified) ? success : verified;

        verified = verify(actual.getDisplayName("supplierSite"), actual.getSupplierSite(),
                expectedData.getSupplierSite());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("allocation"), actual.getAllocation(),
                    expectedData.getAllocation());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("periodStart"), actual.getPeriodStart(),
                    expectedData.getEffectiveFromDate());
            success = (verified) ? success : verified;

            verified = verify(actual.getDisplayName("periodEnd"), actual.getPeriodEnd(),
                    expectedData.getEffectiveToDate());
            success = (verified) ? success : verified;

            if (validateDetails) {
                select(expectedData.getSupplierItemIdentifier());

                SupplierAllocationController supplierAllocationController = new SupplierAllocationController();
                verified = supplierAllocationController.validate(expectedData);
                success = (verified) ? success : verified;

                SupplierAllocationDetailsController supplierAllocationDetailsController = new SupplierAllocationDetailsController();
                verified = supplierAllocationDetailsController.validate(expectedData);
                success = (verified) ? success : verified;

                supplierAllocationDetailsController.clickBack();
            }

        return success;
    }

    protected SearchSupplierAllocationResultsModel findActual(List<SearchSupplierAllocationResultsModel> actualModel,
            String itemNumber, String supplierSite) {
        for (SearchSupplierAllocationResultsModel actual : actualModel) {
            if (actual.getItemNumber().equals(itemNumber)) {
                if (StringUtils.isNotBlank(supplierSite)) {
                    if (actual.getSupplierSite().equals(supplierSite)) {
                        return actual;
                    }
                } else {
                    return actual;
                }
            }
        }
        return null;
    }
}
