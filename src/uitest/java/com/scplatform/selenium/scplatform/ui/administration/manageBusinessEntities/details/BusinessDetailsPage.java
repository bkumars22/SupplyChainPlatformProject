/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageBusinessEntities.details;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.test.selenium.scplatform.modelViewController.SCPlatformPage;

public class BusinessDetailsPage extends SCPlatformPage {

    public WebElement id() {
        return getByLabel("id");
    }

    public WebElement businessName() {
        return getByLabel("businessName");
    }

    public WebElement externalId() {
        return getByLabel("externalId");
    }

    public WebElement type() {
        return getByLabel("type");
    }

    public WebElement description() {
        return getElement(By.name("selectedBusiness.businessEntityDesc"));
    }

    public WebElement alternateNames() {
        return getElement(By.name("alternateNames"));
    }

    public WebElement currencies() {
        return getElement(By.name("currencies"));
    }

    public WebElement sites() {
        return getElement(By.name("sites"));
    }

    private WebElement getByLabel(String label) {
        BusinessDetailsModel model = new BusinessDetailsModel();
        return getLabelElement(model.getDisplayName(label));
    }

    public BusinessDetailsModel parse() {
        BusinessDetailsModel model = new BusinessDetailsModel();

        model.setId(getElementValue(id()).toString());
        model.setBusinessName(getElementValue(businessName()).toString());
        model.setExternalId(getElementValue(externalId()).toString());
        model.setType(getElementValue(type()).toString());
        model.setDescription(getElementValue(description()).toString());
        model.setAlternateNames(getListBoxItems(alternateNames()));
        model.setCurrencies(getListBoxItems(currencies()));
        model.setSites(getListBoxItems(sites()));
        return model;
    }

    protected List<String> getListBoxItems(WebElement element) {
        List<String> items = new ArrayList<String>();
        Select field = new Select(element);
        List<WebElement> listItems = field.getOptions();
        for (WebElement id : listItems) {
            items.add(id.getText());
        }
        return items;
    }

}
