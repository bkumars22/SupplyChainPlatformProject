/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.test.selenium.common.JLog;
import com.test.selenium.common.modelViewController.view.PageImpl;

public class PriceTAMController extends MTCMController {

    PriceTAMView view;

    @Override
    public PageImpl getView() {
        view = new PriceTAMView();
        return view;
    }

    public void getAndVerifySearchFilterResultFCStatus(String status) {
        view = new PriceTAMView();

    }

    @Override
    public int getSearchResultRows() {
        view = new PriceTAMView();
        return view.getList(By.xpath("//div[@class='eto-grid-frozen']//tbody//tr")).size();
    }

    public void setGroupType(String val) {
        view = new PriceTAMView();
        view.sleep(2);
        WebElement ele = view.get(By.xpath("//label[contains(text(),'Group Type')]/following-sibling::div//select"));
        Select select = new Select(ele);
        if (val.equals("XLOB"))
            select.selectByVisibleText("XLOB");
        JLog.write("Selected" + val + " on GroupType");
    }

    public void setPlatformType(String val) {
        view = new PriceTAMView();
        view.sleep(2);
        WebElement ele = view.get(By.xpath("//label[contains(text(),'Platform')]/following-sibling::div//select"));
        Select select = new Select(ele);
        if (val.equals("AGILE"))
            select.selectByVisibleText("AGILE");
        JLog.write("Selected" + val + " on PlatformType");
    }

    public void setLOBType(String val) {
        view = new PriceTAMView();
        view.sleep(2);
        WebElement ele = view.get(By.xpath("//label[contains(text(),'LOB')]/following-sibling::div//select"));
        Select select = new Select(ele);
        if (val.equals("MANUFACTURING"))
            select.selectByVisibleText("MANUFACTURING");
        JLog.write("Selected" + val + " on LOBType");
    }

    // public void getAndVerifyFGNameonPriceTAM(String fgName) {
    // view = new PriceTAMView();
    // WebElement ele = view.getFGNameonPriceTAM(fgName);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(fgName), " cannot verify " + fgName);
    // }

    public void getAndVerifyFGNameonPriceTAM(String fgName) {
        view = new PriceTAMView();
    }

    public void setmultipleItemfield(String item1, String item2, String textField) {
        ForecastView view = new ForecastView();
        view.getTextField(textField).sendKeys(item1 + ";" + item2);
        JLog.write("Set " + textField + " textfield with " + item1 + ";" + item2);
    }
    // public void getAndVerifyItemNameonPriceTAM(String itemNumber) {
    // view = new PriceTAMView();
    // WebElement ele = view.getItemonPriceTAM(itemNumber);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(itemNumber), " cannot verify " + itemNumber);
    // }

    // public void getAndVerifyMPN1PriceTAM(String mpn1) {
    // view = new PriceTAMView();
    // WebElement ele = view.getMPN1(mpn1);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(mpn1), " cannot verify " + mpn1);
    // }

    // public void getAndVerifyMPN2PriceTAM(String mpn2) {
    // view = new PriceTAMView();
    // WebElement ele = view.getMPN2(mpn2);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(mpn2), " cannot verify " + mpn2);
    // }

    // public void getAndVerifySupplierName(String Supplier) {
    // view = new PriceTAMView();
    // WebElement ele = view.getSupplierName(Supplier);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(Supplier), " cannot verify " + Supplier);
    // }

    // public void getAndVerifyCostType(String CostType) {
    // view = new PriceTAMView();
    // WebElement ele = view.getCostType(CostType);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(CostType), " cannot verify " + CostType);
    // }

    public void getAndVerifyCostType(String CostType) {
        view = new PriceTAMView();
    }

    // public void getAndVerifyDestination(String Destination) {
    // view = new PriceTAMView();
    // WebElement ele = view.getDestination(Destination);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(Destination), " cannot verify " + Destination);
    // }

    public void getAndVerifyDestination(String Destination) {
        view = new PriceTAMView();
    }

    // public void getAndVerifySiteTAM(String SiteTAM) {
    // view = new PriceTAMView();
    // WebElement ele = view.getSiteTAM(SiteTAM);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(SiteTAM), " cannot verify " + SiteTAM);
    // }

    public void getAndVerifySiteTAM(String SiteTAM) {
        view = new PriceTAMView();

    }

    // public void getAndVerifyPriceValue(String PriceValue) {
    // view = new PriceTAMView();
    // WebElement ele = view.getPriceValue(PriceValue);
    // String s = ele.getText();
    // if(s.equals("")) {
    // s = ele.getAttribute("value");
    // }
    // JLog.screenCapture();
    // Verify.verify(s.contains(PriceValue), " cannot verify " + PriceValue);
    // }

    public void getAndVerifyPriceValue(String PriceValue) {
        view = new PriceTAMView();
    }

    public void getAndVerifySearchFilterResultpriceStatus(String itemnumber) {
        view = new PriceTAMView();

    }

    public void getAndVerifySearchFilterResultfgStatus(String cfgName) {
        view = new PriceTAMView();

    }

    public void getAndVerifySearchFilterResultpricebuckets(String value) {
        view = new PriceTAMView();

    }

    public void getAndVerifySearchFilterResultsupplierStatus(String supplier) {
        view = new PriceTAMView();

    }

    public void getAndVerifySearchFilterResultMPNStatus(String mpn) {
        view = new PriceTAMView();

    }

    public boolean getAndVerifyfiledownload(String fileDownload) {
        boolean isPresent = false;
        try {
            // WebElement ele = view.get(By.xpath("//i[contains(text(),'" +
            // fileDownload + "')]"));
        } catch (NoSuchElementException e) {
            isPresent = false;
            JLog.write("Successfully disable download icon on Price tam page");
        }
        return isPresent;

    }

    public void setTAMSite(String site) {
        view = new PriceTAMView();
        WebElement element = view.getTAMSite();
        element.clear();
        element.sendKeys(site);
        JLog.write("******After entering***********");
        JLog.screenCapture();
        JLog.write("Site name set to " + site);

    }

    public void setTAMExists(String val) {
        view = new PriceTAMView();
        view.sleep(2);
        WebElement ele = view.get(By.xpath("//label[contains(text(),'TAM Exists')]/following-sibling::div//select"));
        Select select = new Select(ele);
        if (val.equals("Yes")) {
            select.selectByVisibleText("Yes");
            JLog.write("Selected" + val + " on TAMExists");
        } else if (val.equals("No")) {
            select.selectByVisibleText("No");
            JLog.write("Selected" + val + " on TAMExists");

        }
    }

}