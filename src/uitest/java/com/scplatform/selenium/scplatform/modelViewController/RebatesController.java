/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.modelViewController.view.PageImpl;
import com.test.selenium.scplatform.steps.General;
import com.test.selenium.scplatform.steps.HarmonyLoginUI;
import com.test.selenium.scplatform.steps.Rebates;
import com.google.common.base.Verify;

public class RebatesController extends MTCMController {

    RebatesView view;
    HarmonyLoginUI ui = new HarmonyLoginUI();
    Prop prop = Prop.getInstance();
    String dateFormatInput = "yyyy-MM-dd'T'HH:mm:ss";
    String dateFormatOutput = "MM-dd-yyyy";
    SimpleDateFormat formatInput = new SimpleDateFormat(dateFormatInput);
    SimpleDateFormat formatOutput = new SimpleDateFormat(dateFormatOutput);
    // MTCMController mc = new MTCMController();

    @Override
    public PageImpl getView() {
        view = new RebatesView();
        return view;
    }
    
    public void clickEditIconOnRow(int r) {
    	view = new RebatesView();
    	List<WebElement> elements = view.getEditIconList();
        view.executeJavaScript("arguments[0].click();", elements.get(r-1));
    }

    public boolean getPlatFormNamesAndVerifySorted() {
        view = new RebatesView();
        List<String> entities = new ArrayList<>();

        List<WebElement> gridElements = null;
        List<WebElement> elements = view.getList(By.xpath("//div[@class='eto-grid-scroll']//th"));
        gridElements = view.getList(By.xpath("//div[@class='eto-grid-scroll']"));

        int col = 0;
        String s = "";
        for (int i = 0; i < elements.size(); i++) {
            view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(i));
            s = elements.get(i).getAttribute("title");

            if (s.equals("")) {
                s = elements.get(i).getText();
            }
            if (s.equals("Platform Name")) {
                col = i + 1;
                break;
            }
        }
        // getRow col values

        if (gridElements.size() == 0) {
            gridElements = view.getList(By.xpath("//tr//td[" + col + "]"));
        } else
            gridElements = view.getList(By.xpath("//div[@class='eto-grid-scroll']//tr//td[" + col + "]"));
        // WebElement ele = null;
        for (int i = 0; i < gridElements.size(); i++) {
            // ele = view.elements(i, col).g;
            s = gridElements.get(i).getText();
            if (s.equals("")) {
                s = gridElements.get(i).getAttribute("value");
            }
            if (s.equals("")) {
                s = gridElements.get(i).getAttribute("innerText");
            }
            if (s.equals("")) {
                s = gridElements.get(i).getAttribute("innerHTML");
            }
            entities.add(s);
        }
        MTCMController controller = new MTCMController();
        boolean status = controller.checkSorting(entities);
        return status;
    }

    public void isCoulmnValuesDisplayedOnPopup() {
        view = new RebatesView();
        List<WebElement> gridElements = null;
        List<WebElement> elements = view.getList(By.xpath("//div[@class='eto-grid-scroll']//th"));
        gridElements = view.getList(By.xpath("//div[@class='eto-grid-scroll']"));
        int col = 0;
        String s = "";
        for (int i = 0; i < elements.size(); i++) {
            view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(i));
            s = elements.get(i).getAttribute("title");
            if (s.equals("")) {
                s = elements.get(i).getText();
            }
            if (s.equals("Platform Type")) {
                col = i + 1;
                break;
            }
        }
        // getRow col values

        if (gridElements.size() == 0) {
            gridElements = view.getList(By.xpath("//tr//td[" + col + "]"));
        } else
            gridElements = view.getList(By.xpath("//div[@class='eto-grid-scroll']//tr//td[" + col + "]"));
        // WebElement ele = null;
        for (int i = 0; i < gridElements.size(); i++) {
            // ele = view.elements(i, col).g;
            s = gridElements.get(i).getText();
            if (s.equals("")) {
                s = gridElements.get(i).getAttribute("value");
            }
            if (s.equals("")) {
                s = gridElements.get(i).getAttribute("innerText");
            }
            if (s.equals("")) {
                s = gridElements.get(i).getAttribute("innerHTML");
            }
            JLog.write("Actual value=" + s);
            Verify.verify(s.contains("AGILE") || s.contains("PROTEUS"),
                    "Unable to verify values under column Platform Type on row=" + i);
        }

        JLog.write("Successfully verified columnName Platform Type under search results");
    }

    public void isRebateAmtDisplayed(String expAmt) {
        view = new RebatesView();
        List<WebElement> elements = view.getRebatesItemAmounts();
        String amnt = "0";
        boolean status = false;
        for (WebElement e : elements) {
            amnt = e.getText();
            if (amnt.equals("")) {
                amnt = e.getAttribute("value");
            }
            if (amnt.equals(expAmt)) {
                status = true;
            }
        }
        Verify.verify(!status, "Rebate Amount is still displayed after delete!");
    }

    public void clickBtn(String btnID) {
        view = new RebatesView();
        WebElement ele = view.getEleByID(btnID);
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.screenCapture();
    }

    public void clickPricingTab() {
        view = new RebatesView();
        WebElement ele = view.getPricingTab();
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.screenCapture();
    }

    public void isLandedonRulesTab() {
        view = new RebatesView();
        String activeStatus = view.getEleByID("RULESTAB").getAttribute("class");
        Verify.verify(activeStatus.contains("active"), "Unable to find that control landed on Rules Tab.");
    }

    public void clickRulesAppliedLink() {
        view = new RebatesView();
        WebElement ele = view.get(By.xpath("//a[contains(@href,'goChangeTab')]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.screenCapture();
    }

    public void clickViewResultsTab() {
        view = new RebatesView();
        WebElement ele = view.getEleByID("RESULTSTAB");
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.screenCapture();
    }

    public boolean verifyViewResults() throws Throwable {
        view = new RebatesView();
        ArrayList<String> itemList = new ArrayList<>();
        ArrayList<String> business = new ArrayList<>();
        String s;
        Rebates rebates = new Rebates();
        rebates.clickRulesTab();
        List<WebElement> elements = view.getItemListUnderRulesList();
        for (int i = 0; i < elements.size(); i++) {
            s = elements.get(i).getText();
            itemList.add(s.substring(0, s.lastIndexOf(' ')));
            business.add(s.substring(s.indexOf(' ')));
        }
        rebates.clickViewResultsTab();
        elements = view.getItemListUnderViewResults();
        for (int i = 0; i < elements.size(); i++) {
            s = elements.get(i).getText();
            Verify.verify(itemList.contains(s), "Unable to verify the itemList under View Results.");
        }

        elements = view.getBusinessListUnderViewResults();
        for (int i = 0; i < elements.size(); i++) {
            s = elements.get(i).getText();
            Verify.verify(business.contains(s), "Unable to verify the Business List under View Results.");
        }
        return true;
    }

    public void clickRulesTab() {
        view = new RebatesView();
        WebElement ele = view.getEleByID("RULESTAB");
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.screenCapture();
    }

    public void clickSaveRuleBtn() {
        view = new RebatesView();
        WebElement ele = view.getEleByID("updateRule");
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.executeJavaScript("arguments[0].click();", ele);
        JLog.screenCapture();
    }

    public void mouseHoverAndVerifyText(String date, String expTxt) {
        view = new RebatesView();
        WebElement ele = view.get(By.xpath("//td[contains(@data-tooltip,'tooltip')]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        Actions builder = new Actions(view.browserSession.getDriver());
        builder.moveToElement(ele).build().perform();
        String actToolTip = "";
        List<WebElement> toolTips = view.getToolTipMsg();
        for (WebElement toolTip : toolTips) {
            if (actToolTip.equals("")) {
                actToolTip = toolTip.getText();
                if (actToolTip == null || actToolTip.equals("")) {
                    actToolTip = toolTip.getAttribute("innerText");
                }
                if (actToolTip == null || actToolTip.equals("")) {
                    actToolTip = toolTip.getAttribute("innerHTML");
                }
                if (actToolTip == null || actToolTip.equals("")) {
                    actToolTip = toolTip.getAttribute("textContent");
                }
            }
        }
        JLog.screenCapture();
        Verify.verify(actToolTip.contains(expTxt), "Unable to verify tooltip msg.");
        Verify.verify(actToolTip.contains("-"), "Unable to verify '-' on tooltip msg.");
    }

    public void verifyItemDateToolTip(String date, String row) {
        view = new RebatesView();
        JLog.screenCapture();
        boolean status = false;
        List<WebElement> elements = view.getFromToDateColumn(date);
        if (elements.size() == 0) {
            JLog.resetErrorCount();
            elements = view.getList(
                    By.xpath("//td[text()='Expired']/following-sibling::td[contains(@id,'item" + date + "Date')]"));
        }
        if (row.equals("all")) {
            for (WebElement e : elements) {
                String s = e.getAttribute("data-message-type");
                s = e.getAttribute("data-tooltip");
                status = e.getAttribute("data-message-type").equals("error")
                        && e.getAttribute("data-tooltip").contains("item" + date + "Date");
                Verify.verify(status, "Failed to verify Item " + date + " Date tooltip");
            }
            return;
        }
        int rowCount = Integer.parseInt(row);
        WebElement ele = elements.get(rowCount - 1);
        String s = ele.getAttribute("data-message-type");
        s = ele.getAttribute("data-tooltip");
        if (ele.getAttribute("data-message-type") == null || ele.getAttribute("data-tooltip") == null) {
            ele = view.get(By.xpath("//td[contains(@data-tooltip,'tooltip')]"));
        }
        s = ele.getAttribute("data-message-type");
        s = ele.getAttribute("data-tooltip");
        status = ele.getAttribute("data-message-type").equals("error")
                && ele.getAttribute("data-tooltip").contains("item" + date + "Date");
        Verify.verify(status, "Failed to verify Item " + date + " Date tooltip");
    }

    public void setEndDate(String days) {
        view = new RebatesView();
        WebElement ele = view.getEleByID("programEndDate");// view.getEndDate(row);
        ele.sendKeys(DateTime.now().plusDays(Integer.parseInt(days)).toString("MM-dd-yyyy"));
    }

    public void setEndBeforeDate(String days) {
        view = new RebatesView();
        WebElement ele = view.getEleByID("programEndDate");// view.getEndDate(row);
        ele.sendKeys(DateTime.now().minusDays(Integer.parseInt(days)).toString("MM-dd-yyyy"));
    }

    public void setFromDateFromToday(String days, String row) throws ParseException {
        // Note - end date is assumed as 5 days from start date(start date is
        // today)
        MTCMController mc = new MTCMController();
        view = new RebatesView();
        view.sleep(1);
        String currentDay = DateTime.now().toString("MMddyyyy");
        String tdy = currentDay.substring(2, 4);
        int today = Integer.parseInt(tdy);
        String currntMonth = currentDay.substring(0, 2);
        int expMonth = Integer.parseInt(currntMonth);

        WebElement ele = null;
        int r = Integer.parseInt(row);

        List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,\"showCalendar('itemFromDate\")]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(r - 1));
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", elements.get(r - 1));

        String actMonth = view.get(By.xpath("//td[@class='title']")).getText();
        actMonth = actMonth.substring(0, actMonth.indexOf(','));
        int actualMonth = getMonth(actMonth);

        if (expMonth != actualMonth) {
            if (actualMonth < expMonth) {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
            } else {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
            }
        }

        int startDay;
        if (today > 23) {
            startDay = 9; // for the prev valid range - end date can be 7th of
                          // next month , so this shld
                          // start from 8 min
            ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.sleep(2);
            mc = new MTCMController();
            mc.clickWithActionClass(ele);
            JLog.screenCapture();
        } else {
            startDay = today + Integer.parseInt(days);
        }
        view = new RebatesView();
        view.executeJavaScript("arguments[0].scrollIntoView(true);", view.get(By.xpath(
                "//tr[@class='daysrow']//td[contains(@class,'day') and not(contains(@class,'day othermonth'))]")));
        view.sleep(5);
        JLog.screenCapture();
        view = new RebatesView();
        ele = view.getCalendarDay(String.valueOf(startDay));
        if (ele == null) {
            JLog.write("Element Value found null, cannot locate element");
            JLog.screenCapture();
            view = new RebatesView();
            ele = view.getCalendarDay(String.valueOf(startDay));
            if (ele == null) {
                ele = view.get(By.xpath(
                        "//td[text()='" + String.valueOf(startDay) + "' and not(contains(@class,'day othermonth'))]"));
            }
        }
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        AbstractPage.sleep(5); // waiting to scroll into view
        JLog.screenCapture();
        // ele.click();
        // view.executeJavaScript("arguments[0].click();", ele);
        // JLog.screenCapture();
        mc = new MTCMController();
        mc.clickWithActionClass(ele);
        JLog.screenCapture();
    }

    public void setFromDateFromTodayNoClashForMultipleRebates(String days, String row) throws ParseException {
        // Note - end date is assumed as 5 days from start date(start date is
        // today)
        int r = Integer.parseInt(row);

        // String currentDay = DateTime.now().toString("MMddyyyy");
        // String tdy = currentDay.substring(2, 4);
        // int today = Integer.parseInt(tdy);
        // String currntMonth = currentDay.substring(0, 2);
        // int expMonth = Integer.parseInt(currntMonth);

        MTCMController mc = new MTCMController();
        view = new RebatesView();
        String currentDay = view.getEleByID("itemToDate" + (r - 2)).getAttribute("value").replaceAll("-", "");// DateTime.now().toString("MMddyyyy");
        String tdy = currentDay.substring(2, 4);
        int today = Integer.parseInt(tdy);
        String currntMonth = currentDay.substring(0, 2);
        int expMonth = Integer.parseInt(currntMonth);
        String todayDate = DateTime.now().toString("MMddyyyy");
        int expYear = Integer.parseInt(currentDay.substring(4, currentDay.length()));// Integer.parseInt(todayDate.substring(4,
                                                                                     // todayDate.length()));
        view = new RebatesView();

        List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,\"showCalendar('itemFromDate\")]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(r - 1));
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", elements.get(r - 1));
        String actMonth = view.get(By.xpath("//td[@class='title']")).getText();
        int actYear = Integer.parseInt(actMonth.substring(actMonth.indexOf(", ") + 2, actMonth.length()));
        actMonth = actMonth.substring(0, actMonth.indexOf(','));
        int actualMonth = getMonth(actMonth);

        if (expYear > actYear && (today > 10 && Integer.parseInt(currntMonth) == 12)) {
            expYear += 1;
        }
        WebElement ele = null;

        if (expYear != actYear) {
            if (actYear < expYear) {
                ele = view.get(By.xpath("//td[@class='title']//following-sibling::td[1]//div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            } else {
                ele = view.get(By.xpath("//td[@class='title']//preceding-sibling::td[1]//div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            }
        }

        while (expMonth != actualMonth) {
            if (actualMonth < expMonth) {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                actualMonth += 1;
            } else {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                expMonth += 1;
            }
        }

        int startDay;
        if (today > 23) {
            startDay = 10 + Integer.parseInt(days);
            ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.sleep(2);
            mc = new MTCMController();
            mc.clickWithActionClass(ele);
            JLog.screenCapture();
        } else {
            startDay = today + Integer.parseInt(days);
            if ((expMonth == 2 || actualMonth == 2) && startDay > 28)
                startDay = 1;
        }
        view = new RebatesView();
        view.executeJavaScript("arguments[0].scrollIntoView(true);", view.get(By.xpath(
                "//tr[@class='daysrow']//td[contains(@class,'day') and not(contains(@class,'day othermonth'))]")));
        view.sleep(5);
        JLog.screenCapture();
        view = new RebatesView();
        ele = view.getCalendarDay(String.valueOf(startDay));
        if (ele == null) {
            JLog.write("Element Value found null, cannot locate element");
            JLog.screenCapture();
            view = new RebatesView();
            ele = view.getCalendarDay(String.valueOf(startDay));
            if (ele == null) {
                ele = view.get(By.xpath(
                        "//td[text()='" + String.valueOf(startDay) + "' and not(contains(@class,'day othermonth'))]"));
            }
        }
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        AbstractPage.sleep(5); // waiting to scroll into view
        JLog.screenCapture();
        // ele.click();
        // view.executeJavaScript("arguments[0].click();", ele);
        // JLog.screenCapture();
        mc = new MTCMController();
        mc.clickWithActionClass(ele);
        JLog.screenCapture();
    }

    public void setAmountValue(String row, String value) {
        view = new RebatesView();
        int rowCount = Integer.parseInt(row);
        List<WebElement> elements = view.getAmountFields();
        elements.get(rowCount - 1).sendKeys(value);
    }

    public void setToDateOnRow(String endDate, String row) throws ParseException {
        view = new RebatesView();
        MTCMController mc = new MTCMController();
        String startDate = DateTime.now().toString("MMddyyyy");
        String start = startDate.substring(2, 4);
        int startDay = Integer.parseInt(start);
        String startMonth = startDate.substring(0, 2);
        int expMonth = Integer.parseInt(startMonth);
        int expYear = Integer.parseInt(startDate.substring(4, startDate.length()));

        WebElement ele = null;
        int r = Integer.parseInt(row);

        List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,\"showCalendar('itemToDate\")]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(r - 1));
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", elements.get(r - 1));

        String actMonth = view.get(By.xpath("//td[@class='title']")).getText();
        int actYear = Integer.parseInt(actMonth.substring(actMonth.indexOf(", ") + 2, actMonth.length()));
        actMonth = actMonth.substring(0, actMonth.indexOf(','));
        int actualMonth = getMonth(actMonth);

        if (expYear != actYear) {
            if (actYear < expYear) {
                ele = view.get(By.xpath("//td[@class='title']//following-sibling::td[1]//div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            } else {
                ele = view.get(By.xpath("//td[@class='title']//preceding-sibling::td[1]//div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            }
        }

        while (expMonth != actualMonth) {
            if (actualMonth < expMonth && !(actualMonth == (1) && expMonth == 12)) {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                actualMonth += 1;
            } else {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                expMonth += 1;
            }
        }

        int endDay;
        if (!(endDate.contains("-")) && startDay > 18) {
            endDay = 10; // since for the condition of end date out of program
                         // range ( for testing
                         // purpose assumed start date as today and enddate as 5
                         // days from today, so max
                         // program end date can be next month dates 5)
            ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.sleep(2);
            mc = new MTCMController();
            mc.clickWithActionClass(ele);
            JLog.screenCapture();
            // } else if ((endDate.contains("-")) && (startDay <= 7)) {
            // endDay = 27;
            // ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
            // view.executeJavaScript("arguments[0].scrollIntoView(true);",
            // ele);
            // ele.click();
            // JLog.screenCapture();
        } else {
            endDay = startDay + Integer.parseInt(endDate);
        }
        view.sleep(5);
        view = new RebatesView();
        ele = view.getCalendarDay(String.valueOf(endDay));
        if (ele == null) {
            ele = view.getCalendarDay(String.valueOf(startDay));
        }
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(5);
        mc = new MTCMController();
        mc.clickWithActionClass(ele);
        JLog.screenCapture();
    }

    public void setToDateOnRowWithoutClash(String endDate, String row) throws ParseException, InterruptedException {
        MTCMController mc = new MTCMController();
        General gen = new General();
        gen.clickButton("Save");
        int r = Integer.parseInt(row);
        view = new RebatesView();
        String startDate = view.getEleByID("itemFromDate" + (r - 1)).getAttribute("value").replaceAll("-", "");// DateTime.now().toString("MMddyyyy");
        String start = startDate.substring(2, 4);
        int startDay = Integer.parseInt(start);
        String startMonth = startDate.substring(0, 2);
        int expMonth = Integer.parseInt(startMonth);
        // String todayDate = DateTime.now().toString("MMddyyyy");
        int expYear = Integer.parseInt(startDate.substring(4, startDate.length()));

        WebElement ele = null;

        List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,\"showCalendar('itemToDate\")]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(r - 1));
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", elements.get(r - 1));

        String actMonth = view.get(By.xpath("//td[@class='title']")).getText();
        int actYear = Integer.parseInt(actMonth.substring(actMonth.indexOf(", ") + 2, actMonth.length()));
        actMonth = actMonth.substring(0, actMonth.indexOf(','));
        int actualMonth = getMonth(actMonth);

        // if (Integer.parseInt(endDate) > 7 && Integer.parseInt(startMonth) ==
        // 12) {
        // expYear += 1;
        // }

        if (expYear != actYear) {
            if (actYear < expYear) {
                ele = view.get(By.xpath("//td[@class='title']//following-sibling::td[1]//div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            } else {
                ele = view.get(By.xpath("//td[@class='title']//preceding-sibling::td[1]//div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            }
        }

        while (expMonth != actualMonth) {
            if (actualMonth < expMonth && !(actualMonth == (1) && expMonth == 12)) {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                actualMonth += 1;
            } else {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                expMonth += 1;
            }
        }

        int endDay;
        // if (!(endDate.contains("-"))&& startDay > 18) {
        // endDay = 1 + Integer.parseInt(endDate);
        // ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
        // view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        // view.sleep(2);
        // mc = new MTCMController();
        // mc.clickWithActionClass(ele);
        // JLog.screenCapture();
        // // } else if ((endDate.contains("-")) && (startDay <= 7)) {
        // // endDay = 27;
        // // ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
        // // view.executeJavaScript("arguments[0].scrollIntoView(true);",
        // // ele);
        // // ele.click();
        // // JLog.screenCapture();
        // } else {
        endDay = startDay + Integer.parseInt(endDate);
        if (endDay > 28) {
            ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.sleep(2);
            mc = new MTCMController();
            mc.clickWithActionClass(ele);
            JLog.screenCapture();
            endDay = endDay - 28;
        }
        // }
        view = new RebatesView();
        ele = view.getCalendarDay(String.valueOf(endDay));
        if (ele == null) {
            ele = view.getCalendarDay(String.valueOf(startDay));
        }
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(5);
        mc = new MTCMController();
        mc.clickWithActionClass(ele);
        JLog.screenCapture();
    }

    public void setToDate(String fromDate, String endDate) throws ParseException {
        // Note - end date is assumed as 5 days from start date(start date is
        // today)
        MTCMController mc = new MTCMController();

        view = new RebatesView();
        String startDate = "";
        if (fromDate.equals("today")) {
            startDate = DateTime.now().toString("MMddyyyy");
        }

        String start = startDate.substring(2, 4);
        int startDay = Integer.parseInt(start);
        String startMonth = startDate.substring(0, 2);
        int expMonth = Integer.parseInt(startMonth);
        int expYear = Integer.parseInt(startDate.substring(4, startDate.length()));

        WebElement ele = null;

        List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,\"showCalendar('itemToDate\")]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", elements.get(0));

        String actMonth = view.get(By.xpath("//td[@class='title']")).getText();
        int actYear = Integer.parseInt(actMonth.substring(actMonth.indexOf(", ") + 2, actMonth.length()));
        actMonth = actMonth.substring(0, actMonth.indexOf(','));
        int actualMonth = getMonth(actMonth);

        if (expYear != actYear) {
            if (actYear < expYear) {
                ele = view.get(By.xpath("//td[@class='title']//following-sibling::td[1]//div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            } else {
                ele = view.get(By.xpath("//td[@class='title']//preceding-sibling::td[1]//div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            }
        }

        if (expMonth != actualMonth) {
            if (actualMonth < expMonth && !(actualMonth == (1) && expMonth == 12)) {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            } else {
                ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
                // view.executeJavaScript("arguments[0].click();", ele);
                // ele.click();
            }
        }

        int endDay;
        if (!(endDate.contains("-")) && startDay > 23) {
            endDay = 8; // since for the condition of end date out of program
                        // range ( for testing
                        // purpose assumed start date as today and enddate as 5
                        // days from today, so max
                        // program end date can be next month dates 5)
            ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.sleep(2);
            mc = new MTCMController();
            mc.clickWithActionClass(ele);
            JLog.screenCapture();
        } else if ((endDate.contains("-")) && (startDay <= 7)) {
            endDay = 27;
            ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.sleep(2);
            mc = new MTCMController();
            mc.clickWithActionClass(ele);
            JLog.screenCapture();
        } else {
            endDay = startDay + Integer.parseInt(endDate);
            if(expMonth==2&& endDay>27) {
                endDay = 1;
                ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
                view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                view.sleep(2);
                mc = new MTCMController();
                mc.clickWithActionClass(ele);
            }
        }
        view.sleep(3);
        view = new RebatesView();
        JLog.screenCapture();
        ele = view.getCalendarDay(String.valueOf(endDay));
        if (ele == null) {
            ele = view.getCalendarDay(String.valueOf(startDay));
        }
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(5);
        JLog.screenCapture();
        mc = new MTCMController();
        mc.clickWithActionClass(ele);
        JLog.screenCapture();
    }

    public void setFromDateAsDayAfterEndDate(String startDate) throws ParseException {
        // Note - end date is assumed as 5 days from start date(start date is
        // today)
        MTCMController mc = new MTCMController();

        view = new RebatesView();
        String endDate = "";
        endDate = DateTime.now().plusDays(5).toString("MMddyyyy");

        List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,\"showCalendar('itemFromDate\")]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", elements.get(0));

        String d = endDate.substring(2, 4);
        int endDay = Integer.parseInt(d);
        String m = endDate.substring(0, 2);
        int month = Integer.parseInt(m);

        String actMonth = view.get(By.xpath("//td[@class='title']")).getText();
        actMonth = actMonth.substring(0, actMonth.indexOf(','));
        WebElement ele = null;

        int startDay = 0;
        if (endDay > 27) {
            startDay = 1;
            ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.sleep(2);
            mc = new MTCMController();
            mc.clickWithActionClass(ele);

        } else {
            startDay = endDay + 1;
            int actualMonth = getMonth(actMonth);
            if (month != actualMonth) {
                if (actualMonth < month) {
                    ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
                    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                    view.sleep(2);
                    mc = new MTCMController();
                    mc.clickWithActionClass(ele);
                } else {
                    ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
                    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                    view.sleep(2);
                    mc = new MTCMController();
                    mc.clickWithActionClass(ele);
                }
            }
        }
        JLog.screenCapture();
        view.sleep(2);
        view = new RebatesView();
        ele = view.getCalendarDay(String.valueOf(startDay));
        if (ele == null) {
            ele = view.getCalendarDay(String.valueOf(startDay));
        }
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(5);
        mc = new MTCMController();
        mc.clickWithActionClass(ele);
    }

    public void setFromDateWithBeforePgmStartDate(String startDate) throws ParseException {
        view = new RebatesView();
        MTCMController mc = new MTCMController();

        view.sleep(1);
        String currentDay = "";
        currentDay = DateTime.now().toString("MMddyyyy");

        List<WebElement> elements = view.getList(By.xpath("//i[contains(@onclick,\"showCalendar('itemFromDate\")]"));
        view.executeJavaScript("arguments[0].scrollIntoView(true);", elements.get(0));
        view.sleep(2);
        view.executeJavaScript("arguments[0].click();", elements.get(0));

        String d = currentDay.substring(2, 4);
        int day = Integer.parseInt(d);
        String m = currentDay.substring(0, 2);
        int month = Integer.parseInt(m);

        String actMonth = view.get(By.xpath("//td[@class='title']")).getText();
        actMonth = actMonth.substring(0, actMonth.indexOf(','));
        WebElement ele = null;

        int startDay = 0;
        if (day == 1) {
            startDay = 28;
            ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
            view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
            view.sleep(2);
            mc = new MTCMController();
            mc.clickWithActionClass(ele);

        } else {
            startDay = day - 1;
            int actualMonth = getMonth(actMonth);
            if (month != actualMonth) {
                if (actualMonth < month) {
                    ele = view.get(By.xpath("//tr[@class='headrow']//td[2]/div"));
                    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                    view.sleep(2);
                    mc = new MTCMController();
                    mc.clickWithActionClass(ele);
                } else {
                    ele = view.get(By.xpath("//tr[@class='headrow']//td[1]/div"));
                    view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
                    view.sleep(2);
                    mc = new MTCMController();
                    mc.clickWithActionClass(ele);
                }
            }
        }
        JLog.screenCapture();
        view.sleep(5);
        view = new RebatesView();
        ele = view.getCalendarDay(String.valueOf(startDay));
        if (ele == null) {
            ele = view.getCalendarDay(String.valueOf(startDay));
        }
        view.executeJavaScript("arguments[0].scrollIntoView(true);", ele);
        view.sleep(5);
        mc = new MTCMController();
        mc.clickWithActionClass(ele);
        JLog.screenCapture();
    }

    public int getMonth(String actMonth) {
        int actualMonth = 0;
        switch (actMonth) {
        case "January":
            actualMonth = 1;
            break;
        case "February":
            actualMonth = 2;
            break;
        case "March":
            actualMonth = 3;
            break;
        case "April":
            actualMonth = 4;
            break;
        case "May":
            actualMonth = 5;
            break;
        case "June":
            actualMonth = 6;
            break;
        case "July":
            actualMonth = 7;
            break;
        case "August":
            actualMonth = 8;
            break;
        case "September":
            actualMonth = 9;
            break;
        case "October":
            actualMonth = 10;
            break;
        case "November":
            actualMonth = 11;
            break;
        case "December":
            actualMonth = 12;
            break;

        default:
            break;
        }
        return actualMonth;
    }
}
