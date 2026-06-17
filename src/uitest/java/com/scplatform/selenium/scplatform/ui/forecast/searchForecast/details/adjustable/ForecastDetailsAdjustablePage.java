/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.forecast.searchForecast.details.adjustable;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;
import com.test.selenium.common.MathUtils;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.ForecastDetailsPage;
import com.test.selenium.scplatform.ui.forecast.searchForecast.details.adjustable.ForecastDetailsAdjustableModel.PITLabels;

public class ForecastDetailsAdjustablePage extends ForecastDetailsPage {

    public WebElement button_ShowHistory() {
        return getElement(By.partialLinkText("Show History"));
    }

    public WebElement button_Find() {
        return getElement(By.partialLinkText("Find"));
    }

    public WebElement button_AutoPopulate() {
        return getElement(By.partialLinkText("Auto Populate"));
    }

    public WebElement button_Undo() {
        return getElement(By.partialLinkText("Undo"));
    }

    public WebElement button_Delete() {
        return getElement(By.partialLinkText("Delete"));
    }

    public WebElement button_Approve() {
        return getElement(By.partialLinkText("Approve"));
    }

    public By tableLocator() {
        return By.id("fcTableADJ_data");
    }

    /**
     * @return Parses the results listing and returns a list of
     *         {@link ForecastDetailsAdjustableModel} data
     */
    public List<ForecastDetailsAdjustableModel> parseResults() {

        List<ForecastDetailsAdjustableModel> tableData = new ArrayList<ForecastDetailsAdjustableModel>();
        ForecastDetailsAdjustableModel data = new ForecastDetailsAdjustableModel();

        HashMap<String, ArrayList<String>> parsedTableData = new HashMap<String, ArrayList<String>>();

        parsedTableData = this.tableParse(tableLocator());

        if (parsedTableData == null) {
            JLog.error(this.getClass() + "parseResults() - No results returned for parsed table", TakeScreenshot.True);
            return tableData;
        }
        if (!parsedTableData.containsKey(data.getDisplayName("itemNumber"))) {
            JLog.error(this.getClass() + "parseResults() - Unable to find column in parsed data: "
                    + data.getDisplayName("itemNumber"), TakeScreenshot.True);
            return tableData;
        }

        List<String> forecastHeaders = getForecastHeaders(parsedTableData.keySet());
        int tableSize = parsedTableData.get(data.getDisplayName("itemNumber")).size();
        String pitData;

        for (int row = 0; row < tableSize; row++) {
            data = new ForecastDetailsAdjustableModel();

            try {
                data.setItemNumber(parsedTableData.get(data.getDisplayName("itemNumber")).get(row));
                data.setRegion(parsedTableData.get(data.getDisplayName("region")).get(row));
                data.setStatus(parsedTableData.get(data.getDisplayName("status")).get(row));
                data.setCommodity(parsedTableData.get(data.getDisplayName("commodity")).get(row));
                data.setMemberOfGroup(parsedTableData.get(data.getDisplayName("memberOfGroup")).get(row));
                data.setResponsibility(parsedTableData.get(data.getDisplayName("responsibility")).get(row));
                data.setRolloverExpiresOn(parsedTableData.get(data.getDisplayName("rolloverExpiresOn")).get(row));
                data.setExtendForecastTerm(parsedTableData.get(data.getDisplayName("extendForecastTerm")).get(row));
                data.setAdjustmentType(parsedTableData.get(data.getDisplayName("adjustmentType")).get(row));

                for (String heading : forecastHeaders) {
                    PITLabels pitLabels = parsePitLabelData(parsedTableData.get(heading).get(row), data);
                    data.setPitBuckets(heading, pitLabels);
                }
            } catch (NullPointerException e) {
                JLog.error("Error getting data for row " + row, e, TakeScreenshot.True);
            }
            tableData.add(data);
        }

        return tableData;
    }

    private PITLabels parsePitLabelData(String pitLabelString, ForecastDetailsAdjustableModel data) {

        PITLabels pitLabels = data.new PITLabels();
        if (StringUtils.isBlank(pitLabelString)) {
            return pitLabels;
        }
        String[] pitLabelParts = pitLabelString.split("\\|");

        pitLabels.setAdjustmentValue(
                MathUtils.format(Float.parseFloat(pitLabelParts[0]), "###.###", RoundingMode.HALF_DOWN));
        pitLabels.setAdjustmentAmount(
                MathUtils.format(Float.parseFloat(pitLabelParts[1].replace("%", "").replace("$", "")), "###.###",
                        RoundingMode.HALF_DOWN));
        pitLabels.setCalculatedValue(
                MathUtils.format(Float.parseFloat(pitLabelParts[2]), "###.####", RoundingMode.HALF_DOWN));
        return pitLabels;
    }

    private List<String> getForecastHeaders(Set<String> headings) {
        List<String> forecastHeaders = new ArrayList<String>();

        for (String columnName : headings) {
            if (isPitHeader(columnName)) {
                forecastHeaders.add(columnName);
            }
        }
        return forecastHeaders;
    }

    private boolean isPitHeader(String header) {
        boolean pitHeader = false;

        if (header.startsWith("Column-")) {
            return pitHeader;
        }
        if (header != null && header.length() >= 2) {
            String lastTwo = header.substring(header.length() - 2);

            if (StringUtils.isNumeric(lastTwo)) {
                // value ends in two digits, so this should be a forecast
                // heading
                pitHeader = true;
            }
        }
        return pitHeader;
    }

    @Override
    public HashMap<String, ArrayList<String>> tableParse(By by) {

        parsedTableData = new HashMap<String, ArrayList<String>>();
        parsedTableData.clear();

        // -------------------------------------
        // get the table
        WebElement tableObj = null;
        try {
            if (exists(by)) {
                tableObj = get(by);
            } else {
                JLog.error(String.format("Unable to find table: %s", by.toString()), TakeScreenshot.True);
                return null;
            }
        } catch (Exception onfe) {
            JLog.error(onfe.getMessage());
            return null;
        }

        List<WebElement> rowObjects = tableObj.findElements(By.id("fcTableHeaderRow"));

        int rowCount = rowObjects.size();
        int colCount = rowObjects.get(0).findElements(By.tagName("td")).size();

        // -------------------------------------
        // get the header values
        ArrayList<String> headers = new ArrayList<String>();
        String columnName;
        try {
            WebElement row = rowObjects.get(0);
            List<WebElement> cells = row.findElements(By.tagName("td"));
            for (int col = 0; col < cells.size(); col++) {
                scrollToElement(cells.get(col));
                columnName = cells.get(col).getText().replace("\n", " ").trim();
                if (columnName.equals("")) {
                    columnName = "Column-" + col;
                }
                headers.add(columnName);
            }
        } catch (Exception e) {
            JLog.error(e.getMessage());
            return null;
        }

        // -------------------------------------
        // get the row data
        HashMap<Integer, List<String>> rowData = new HashMap<Integer, List<String>>();
        List<String> rowText = new ArrayList<String>();
        String text;
        int headerIndex = 0; // used in debugging to see the header. Watch:
                             // headers.get(headerIndex)

        rowObjects = tableObj.findElements(By.xpath("//tr[contains(@class, 'tableRow')]"));
        rowCount = rowObjects.size();

        for (int row = 0; row < rowCount; row++) {
            // get the data for the base data (not the PIT buckets)
            List<WebElement> cells = rowObjects.get(row)
                    .findElements(By.xpath("./td[contains(@class, 'fixedColumn')]"));
            rowText = new ArrayList<String>();
            for (int col = 0; col < cells.size(); col++) {
                if (headers.get(headerIndex).equals("Labels")) {
                    rowText.add("");
                    headerIndex++;
                    continue;
                }
                if (headers.get(headerIndex).equals("Adjustment Type")) {
                    text = (String) getElementValue(cells.get(col).findElement(By.tagName("select")));
                    rowText.add(text.trim());
                    headerIndex++;
                    continue;
                }
                scrollToElement(cells.get(col));
                text = (String) getElementValue(cells.get(col));
                if (StringUtils.isNotEmpty(text)) {
                    rowText.add(text.replace("\n", " ").trim());
                } else {
                    try {
                        WebElement inputField = cells.get(col).findElement(By.tagName("input"));
                        text = getElementValue(inputField).toString();
                        rowText.add(text);
                    } catch (NoSuchElementException e) {
                        // ignore
                        rowText.add("");
                    }
                }
                headerIndex++;
            }

            // get the data for the PIT Buckets
            float adjustmentValue;
            String adjustmentAmount;
            float calculatedValue;
            List<WebElement> adjustmentCells = rowObjects.get(row)
                    .findElements(By.xpath("./td/div[@class='adjshown' and contains(@name, 'adjustment')]"));
            adjustmentCells.remove(0); // this is the "Labels" column
            List<WebElement> calcValueCells = rowObjects.get(row)
                    .findElements(By.xpath("./td/table/tbody/tr/td[contains(@id, 'calculatedVal')]"));
            for (int col = 0; col < adjustmentCells.size(); col++) {
                scrollToElement(adjustmentCells.get(col));

                WebElement adjustmentValueField = adjustmentCells.get(col)
                        .findElement(By.xpath("./table/tbody/tr[1]/td[1]"));
                text = getValue(adjustmentValueField);
                adjustmentValue = Float.parseFloat(text);

                WebElement adjustmentAmountField = adjustmentCells.get(col)
                        .findElement(By.xpath("./table/tbody/tr[2]/td[1]"));
                adjustmentAmount = getValue(adjustmentAmountField);

                text = getValue(calcValueCells.get(col));
                calculatedValue = Float.parseFloat(text);

                rowText.add(String.format("%f|%s|%f", adjustmentValue, adjustmentAmount, calculatedValue));
                headerIndex++;
            }

            rowData.put(row, rowText);
        }

        // covert the row data into column data so that the key=header and
        // the ArrayList=row data for header
        String[] tableData = new String[rowCount];

        for (int col = 0; col < colCount; col++) {

            // starting from row 1 rather than 0, since we don't need the header
            for (int row = 0; row < rowCount; row++) {
                try {
                    tableData[row] = rowData.get(row).get(col).trim();
                } catch (IndexOutOfBoundsException e) {
                    tableData[row] = "";
                    // JLog.fail(String.format("Row=%d; Column=%d; %s", row,
                    // col, e.toString()), e, TakeScreenshot.False);
                }
            }

            // add to the main hashmap
            if (headers.size() > col) {
                setTableData(headers.get(col), tableData);
            }
        }
        return parsedTableData;
    }

    private String getValue(WebElement field) {
        String text = getElementValue(field).toString();
        if (StringUtils.isNotEmpty(text)) {
            return text;
        } else {
            try {
                WebElement inputField = field.findElement(By.tagName("input"));
                text = getElementValue(inputField).toString();
                return text;
            } catch (NoSuchElementException e) {
                // ignore
            }
        }
        return "";
    }

}
