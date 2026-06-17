/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.scplatform.qa.e2Messages.utilities.NullValue;

public class SCPlatformSearchResultsPage extends SCPlatformSearchPage {

    protected WebElement locatePageAndRow(By tableLocator, String textToSearchFor) {
        WebElement row = tableRowContainingText(tableLocator, textToSearchFor, COMPARE.Equals);

        while ((row == null) && (!isLastPage())) {
            nextPage().click();
            row = tableRowContainingText(tableLocator, textToSearchFor, COMPARE.Equals);
        }
        return row;
    }

    public boolean isLastPage() {
        return !exists(nextPageLocator());
    }

    public boolean isNextPage() {
        return exists(nextPageLocator());
    }

    public WebElement nextPage() {
        return getElement(nextPageLocator());
    }

    private By nextPageLocator() {
        return By.xpath("//img[contains(@src, 'next_active')]");
    }

    public WebElement findRow(String itemToSelect, By tableLocator) {
        return locatePageAndRow(tableLocator, itemToSelect);
    }

    protected WebElement pageSize() {
        return getElement(By.name("pageSize"));
    }

    protected WebElement resultsFound() {
        return getElement(By.xpath("//td[contains(text(), 'results found, Page')]"));
    }

    /**
     * @return returns the Results found string:
     * 
     *         <pre>
     * 12 results found, Page 1 of 2
     *         </pre>
     *
     * @see #getResultsFoundCount()
     * @see #getResultsFoundPage()
     * @see #getResultsFoundTotalPages()
     * @see #getResultsFoundData()
     */
    public String getResultsFound() {
        return getElementValue(resultsFound()).toString().trim();
    }

    /**
     * @return Gets the Results found total count ('12' from the example below)
     * 
     *         <pre>
     * 12 results found, Page 1 of 2
     *         </pre>
     *
     * @see #getResultsFound()
     * @see #getResultsFoundPage()
     * @see #getResultsFoundTotalPages()
     * @see #getResultsFoundData()
     */
    public String getResultsFoundCount() {
        return getResultsFoundParts(RESULTS.ResultsFound);
    }

    /**
     * @return Gets the Results current page ('1' from the example below)
     * 
     *         <pre>
     * 12 results found, Page 1 of 2
     *         </pre>
     *
     * @see #getResultsFound()
     * @see #getResultsFoundCount()
     * @see #getResultsFoundTotalPages()
     * @see #getResultsFoundData()
     */
    public String getResultsFoundPage() {
        return getResultsFoundParts(RESULTS.Page);
    }

    /**
     * @return Gets the Results total pages ('2' from the example below)
     * 
     *         <pre>
     * 12 results found, Page 1 of 2
     *         </pre>
     *
     * @see #getResultsFound()
     * @see #getResultsFoundCount()
     * @see #getResultsFoundPage()
     * @see #getResultsFoundData()
     */
    public String getResultsFoundTotalPages() {
        return getResultsFoundParts(RESULTS.TotalPages);
    }

    /**
     * @return Returns a {@link ResultsFound} data set containing all of the
     *         results found data
     *
     * @see #getResultsFound()
     * @see #getResultsFoundCount()
     * @see #getResultsFoundPage()
     * @see #getResultsFoundTotalPages()
     */
    public ResultsFound getResultsFoundData() {
        ResultsFound resultsFound = new ResultsFound();
        String value;

        resultsFound.setResultsFound(getResultsFound());

        value = getResultsFoundCount();
        resultsFound.setResultsFoundCount((value == null) ? NullValue.INTEGER : Integer.parseInt(value));

        value = getResultsFoundPage();
        resultsFound.setResultsFoundPage((value == null) ? NullValue.INTEGER : Integer.parseInt(value));

        value = getResultsFoundTotalPages();
        resultsFound.setResultsFoundTotalPages((value == null) ? NullValue.INTEGER : Integer.parseInt(value));

        return resultsFound;
    }

    private String getResultsFoundParts(RESULTS resultPart) {
        String part = null;
        String resultsFound = getResultsFound();

        String patternStr = "(^\\d+) results found, Page (\\d+) of (\\d+)";
        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(resultsFound);
        while (m.find()) {
            part = m.group(resultPart.getPartIndex());
        }
        return part;
    }

    private enum RESULTS {
        ResultsFound(1), Page(2), TotalPages(3);

        private int part;

        RESULTS(int partIndex) {
            this.part = partIndex;
        }

        public int getPartIndex() {
            return part;
        }
    }

    public class ResultsFound {
        private String resultsFound;
        private int resultsFoundCount;
        private int resultsFoundPage;
        private int resultsFoundTotalPages;

        /**
         * @return the resultsFound
         */
        public String getResultsFound() {
            return resultsFound;
        }

        /**
         * @param resultsFound
         *            the resultsFound to set
         */
        public void setResultsFound(String resultsFound) {
            this.resultsFound = resultsFound;
        }

        /**
         * @return the resultsFoundCount
         */
        public int getResultsFoundCount() {
            return resultsFoundCount;
        }

        /**
         * @param resultsFoundCount
         *            the resultsFoundCount to set
         */
        public void setResultsFoundCount(int resultsFoundCount) {
            this.resultsFoundCount = resultsFoundCount;
        }

        /**
         * @return the resultsFoundPage
         */
        public int getResultsFoundPage() {
            return resultsFoundPage;
        }

        /**
         * @param resultsFoundPage
         *            the resultsFoundPage to set
         */
        public void setResultsFoundPage(int resultsFoundPage) {
            this.resultsFoundPage = resultsFoundPage;
        }

        /**
         * @return the resultsFoundTotalPages
         */
        public int getResultsFoundTotalPages() {
            return resultsFoundTotalPages;
        }

        /**
         * @param resultsFoundTotalPages
         *            the resultsFoundTotalPages to set
         */
        public void setResultsFoundTotalPages(int resultsFoundTotalPages) {
            this.resultsFoundTotalPages = resultsFoundTotalPages;
        }

    }
}
