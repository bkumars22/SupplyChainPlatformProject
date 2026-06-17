/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.modelViewController;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.test.selenium.common.JLog;

public class MTCMView extends SCPlatformPage {

  @Override
  public void setContext() {
    String[] frames = new String[1];
    frames[0] = "iframe[name='contentFrame']";
    setFrame(frames);
  }

  @Override
  public String[] homeMenu() {
    return null;
  }

  public WebElement getPopUPWindowEle() {
    return get(By.id("popupItemIdentifier"));
  }

  public WebElement getHomeScreenWelcome() {
    return get(By.xpath("//div//h2[contains(text(),'Welcome to E2open')]"));
  }

  @Override
  public WebElement saveAndExitButton() {
    WebElement e = null;
    try {
      e = browserSession.getDriver().findElement(By.id("saveAndReturnButton"));
      if (e == null) {
        JLog.resetErrorCount();
        e = browserSession.getDriver().findElement(By.xpath("//button[contains(text(),' Exit')]"));
      }
    } catch (Exception e1) {
      try {
        if (e == null) {
          JLog.resetErrorCount();
          e = browserSession.getDriver().findElement(By.id("saveExitButton"));
        }
      } catch (Exception e2) {
        JLog.resetErrorCount();
        e = browserSession.getDriver().findElement(By.xpath("//button[contains(text(),' Exit')]"));
      }
    }
    if (e == null) {
      JLog.resetErrorCount();
      e = browserSession.getDriver().findElement(By.id("saveExitButton"));
    }
    if (e == null) {
      JLog.resetErrorCount();
      e = browserSession.getDriver().findElement(By.xpath("//button[contains(text(),' Exit')]"));
    }
    return e;
  }

  @Override
  public WebElement saveButton() {
    WebElement e = null;
    try {
      e = browserSession.getDriver().findElement(By.id("saveAndContinueButton")); // getElement(By.id("saveAndContinueButton"));
      if (e == null) {
        e = getElement(By.id("saveButton"));
        JLog.resetErrorCount();
      }
    } catch (Exception e1) {
      if (e == null) {
        e = getElement(By.id("saveButton"));
        JLog.resetErrorCount();
      }
    }
    return e;
  }
  //
  // @Override
  // public WebElement saveAndExitButton() {
  // WebElement e = getElement(By.id("saveAndReturnButton"));
  // return e;
  // }
  //
  // public WebElement saveAndExitButtonWithReturnID() {
  // WebElement e = getElement(By.id("saveAndReturnButton"));
  // return e;
  // }
  //
  // public WebElement saveAndExitButtonWithExitID() {
  // WebElement e = getElement(By.id("saveExitButton"));
  // return e;
  // }

  @Override
  public WebElement editButton() {
    return getElement(By.partialLinkText("Edit"));
  }

  public WebElement getPageTitle() {
    return getElement(
        By.xpath("//span[contains(@class,'eto-dashboard__title scplatform-dashboard__title')]"));
  }

  public WebElement getCLoseDialog() {
    return get(
        By.xpath(
            "//span[text()='Validation Errors']//ancestor::header//button[@class='eto-modal__close']"));
  }

  public WebElement getCloseXIconOnPopup() {
    return get(By.xpath("//button[@class='eto-modal__close']"));
  }

  @Override
  public WebElement backButton() {
    return getElement(By.partialLinkText("Back"));
  }

  public WebElement getTotalRecordsValue() {
    return getElement(By.xpath("//span[contains(text(),'Records')]"));
  }

  public WebElement getEleByName(String name) {
    if (name == null || name.isEmpty()) {
      JLog.write("WARNING: getEleByName called with null/empty name — returning null");
      return null;
    }
    return getElement(By.name(name));
  }

  // public WebElement getEleByNameWithValue(String name) {
  // return getElement(By.name("value(" + name + ")"));
  // }

  public List<WebElement> getToolTipMsg() {
    return getList(By.xpath("//div[@class='eto-tooltip__content']"));
  }

  public WebElement downloadLink() {
    return get(By.xpath("//a[contains(@onclick, 'ACTION=DOWNLOAD')]"));
  }

  public WebElement getEleByID(String id) {
    if (id == null || id.isEmpty()) {
      JLog.write("WARNING: getEleByID called with null/empty id — returning null");
      return null;
    }
    return getElement(By.id(id));
  }

  @Override
  public WebElement cancelButton() {
    return getElement(By.partialLinkText("Cancel"));
  }

  @Override
  public WebElement addButton() {
    return getElement(By.partialLinkText("Add"));
  }

  @Override
  public WebElement searchButton() {
    return getElement(By.partialLinkText("Search"));
  }

  public WebElement getPopUpMessage() {
    return get(By.id("//p[@id='popup_modal_body']"));
  }

  public WebElement getODMTextField() {
    return getElement(
        By.xpath(
            "//div[@id='applicableODMCMDiv']//label[contains(text(),'ODM/CM')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getRegionTextField() {
    return getElement(By.xpath("div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getLOBTextField() {
    return getElement(
        By.xpath(
            "//div[@id='applicableLOBDiv']//label[contains(text(),'Line Of Business')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getStatusTextField() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectstatus']//label[contains(text(),'Status')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getForecastModelTextField() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectforecastModel']//label[contains(text(),'Forecast Model')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public List<WebElement> getAutoSuggList(String text) {
    return getList(
        By.xpath(
            "//div[@class='eto-results__scroll']//li[@role='option'][contains(text(),'" + text
                + "')]"));
  }

  public List<WebElement> getAutoSuggestionList(String text) {
    return getList(
        By.xpath(
            "//div[@class='eto-results__scroll']//li[@role='option']//b[contains(text(),'" + text
                + "')]"));
  }

  public WebElement getCTTextField() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectcostType']//label[contains(text(),'Cost Type')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getItemTypeTextField() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectitemType']//label[contains(text(),'Item Type')]/following-sibling::div//input"));
  }

  public WebElement getAuditTypeTextField() {
    return getElement(
        By.xpath("//div[@id='complexSelectauditType']//div[@role='presentation']//input"));
  }

  public WebElement getStatusValueCheckBox(String status) {
    return getElement(
        By.xpath(
            "//span[contains(text(),'" + status
                + "')]/preceding-sibling::input[@type='checkbox']"));
  }

  public WebElement getSelectAll() {
    return getElement(By.xpath("//div[@class='eto-results__select-all']"));
  }
  // public WebElement getCTValueCheckBox(String status) {
  // return getElement(
  // By.xpath("//span[contains(text(),'" + status +
  // "')]/preceding-sibling::input[@type='checkbox']"));
  // }

  @Override
  public WebElement clearButton() {
    // return getElement(By.partialLinkText("Clear"));
    return getElement(By.xpath("//div[@id='complexSelectstatus']//span[text()='expand_more']"));
  }

  public WebElement getExpandMoreBtn() {
    return getElement(By.xpath("//div[@id='complexSelectstatus']//span[text()='expand_more']"));
  }

  public WebElement getExpandMoreCTBtn() {
    return getElement(By.xpath("//div[@id='complexSelectcostType']//span[text()='expand_more']"));
  }

  public WebElement getExpandMoreBtn(String id) {
    return getElement(By.xpath("//div[@id='" + id + "']//span[text()='expand_more']"));
  }

  public WebElement getExpandMoreBtnforRegion() {
    return getElement(By.xpath("//span[text()='expand_more']"));
  }

  public WebElement getdownloadDataExpandMoreBtn() {
    return getElement(By.xpath("//div[@id='downloadData']//span[text()='expand_more']"));
  }

  public WebElement getItemTypeExpandMoreBtn() {
    return getElement(By.xpath("//div[@id='complexSelectitemType']//span[text()='expand_more']"));
  }

  public WebElement getAuditTypeExpandMoreBtn() {
    return getElement(By.xpath("//div[@id='complexSelectauditType']//span[text()='expand_more']"));
  }

  public WebElement getWarningPopupMsg() {
    // Try strict selector first (modal with 'open' class), fall back to body-only
    // selector if the 'open' class is absent — the UI class name varies across versions.
    By strict = By.xpath("//div[contains(@class,'eto-modal open')]//section[@class='eto-modal__body']//p");
    By fallback = By.xpath("//section[@class='eto-modal__body']//p");
    for (int i = 0; i < 5; i++) {
      WebElement ele = get(strict);
      if (ele != null) return ele;
      ele = get(fallback);
      if (ele != null) return ele;
      try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
    }
    return get(fallback);
  }

  public List<WebElement> getWarningPopupMessages() {
    return getList(By.xpath("//section[@class='eto-modal__body']//p"));
  }

  public List<WebElement> getWarningMessages() {
    return getList(By.xpath("//section[@class='eto-modal__body']//ul"));
  }

  @Override
  public WebElement getLabelElement(String labelName) {
    return getElement(
        By.xpath(
            "//td[@class='formLabel' and contains(.,'" + labelName
                + "')]/following-sibling::td[1]"));
  }

  public WebElement getSelectElement(String labelName) {
    return getElement(
        By.xpath("//label[contains(text(),'" + labelName + "')]/following-sibling::div//select"));
  }

  public WebElement getButton(String buttonName) {
    // Strategy 1: button with direct text content (standard case)
    List<WebElement> byText = browser().findElements(
        By.xpath("//button[contains(@class,'eto-btn') and contains(text(),'" + buttonName + "')]"));
    if (!byText.isEmpty())
      return byText.get(0);
    // Strategy 2: button whose descendant text (e.g. inside <span>) contains the label
    return get(
        By.xpath("//button[contains(@class,'eto-btn') and contains(normalize-space(.),'" + buttonName + "')]"));
  }

  public WebElement getBtn(String buttonName) {
    WebElement ele = null;
    try {
      ele = browserSession.getDriver()
          .findElement(By.xpath("//button[@class='eto-btn' and text()='" + buttonName + "']"));
    } catch (Exception e) {
      if (e.toString().contains("Exception")) {
        ele = browserSession.getDriver()
            .findElement(By.xpath("//button[contains(@class,'eto-btn') and text()='Save']"));
        JLog.resetErrorCount();
      }
    }
    if (ele == null) {
      ele = browserSession.getDriver()
          .findElement(By.xpath("//button[contains(@class,'eto-btn') and text()='Save']"));
      JLog.resetErrorCount();
    }
    return ele;
  }

  public List<WebElement> getInputElements(String name, String type) {
    if (name.contains("inherit"))// then tam
      return getList(
          By.xpath(
              "//th[not(contains(@class,'past'))]//input[@type='" + type + "' and @name='" + name
                  + "']"));
    if (name.contains("accessRights(TAM)"))
      return getList(By.xpath("//input[@name='accessRights(TAM)' and contains(@value,'Delete')]"));

    List<WebElement> elements = getList(By.xpath("//input[@type='" + type + "' and @name='" + name + "']"));
    if (elements.size() == 0) {
      JLog.resetErrorCount();
      elements = getList(By.xpath("//input[@type='" + type + "' and contains(@name,'" + name + "')]"));
    }
    return elements;
  }

  public List<WebElement> getPopupGridResultsHeaderColumns() {
    return getList(By.xpath("//div[@class='eto-grid-scroll']//th"));
  }

  public List<WebElement> getItemRespRows() {
    return getList(By.xpath("//tbody//input[@type='checkbox'  and @name='selectedPageKeys']"));
  }

  public WebElement getCheckbox() {
    return get(By.xpath("//input[@type='checkbox' and @name='selectedPageKeys']"));
  }

  public List<WebElement> getItemNumbersFromPopup() {
    return getList(By.xpath("//div[@class='eto-grid-scroll']//tr//td[1]"));
  }

  public List<WebElement> getBusEntityFromPopup() {
    return getList(By.xpath("//div[@class='eto-grid-scroll']//tr[1]//td[3]"));
  }

  public WebElement getConfirmYesButton() {
    return get(By.xpath("//input[@id='msgBoxActive']"));
  }

  public WebElement getSelectComboByName(String name) {
    return get(By.xpath("//select[contains(@name,'" + name + "')]"));
  }

  public WebElement getDynamicBtn(String btnText) {
    WebElement e = null;
    try {
      e = get(By.id("dynamicButton"));
    } catch (Exception e1) {

    }
    if (e == null) {
      JLog.resetErrorCount();
      e = get(By.xpath("//button[contains(text(),'" + btnText + "')]"));
    }
    return e;
  }

  public WebElement getEleByClassName(String className) {
    return get(By.className(className));
  }

  public List<WebElement> getElementsByClassName(String className) {
    return getList(By.className(className));
  }

  public List<WebElement> getHeaderColumns() {
    return getList(By.xpath("//th//a[contains(@class,'eto-grid-column')]"));
    // return getList(By.xpath("//table[@id='businessDetailsTable']//th"));
  }

  public List<WebElement> getHeaderColumnsForAuditHistory() {

    return getList(
        By.xpath(
            "//div//label[text()='Audit History']/following::div[@id='grid-result']/descendant::div[@class='eto-grid-column__container']"));
  }

  public WebElement getConfirmNoButton() {
    return get(By.xpath("//input[@id='msgBoxPassive']"));
  }

  public WebElement getPopupMessage() {
    // return get(By.xpath("//div[@class='simplemodal-body']"));
    return get(By.id("popup_modal_body"));
  }

  public WebElement findTextFieldOnPopup() {
    // WebElement e = get(By.name("value(categoryName)"));
    WebElement e = get(
        By.xpath(
            "//label[contains(@for,'searchField')]/following-sibling::input[contains(@id,'searchField')]"));

    return e;
  }

  public WebElement getErrMessage() {
    return get(By.xpath("//div[@data-message-type='error']//li"));
  }

  public WebElement getErrorMessageElement() {
    return get(By.xpath("//div[@data-message-type='error']"));
  }

  public WebElement getPageJump() {
    return get(By.xpath("//label[contains(text(),'of')]"));
  }

  public WebElement getAllCheckBoxes() {
    return browserSession.getDriver().findElement(By.id("selectAllItem"));
  }

  public WebElement getTextField(String name) {
    return get(By.name("value(" + name + ")"));
  }

  public WebElement getMultipleTextField(String name) {
    return get(By.xpath("//div[@id='itemNumbers']/input"));
  }

  public WebElement getTitleEle(String title) {
    return get(By.xpath("//*[contains(text(),'" + title + "')]"));
  }

  public WebElement getColumnHeader(String title) {
    List<WebElement> els = getList(
        By.xpath("//th[@role='columnheader' and contains(@data-column,'" + title + "')]"));
    if (!els.isEmpty()) {
      return els.get(0);
    }
    // Fallback: try title attribute
    els = getList(By.xpath("//th[@role='columnheader' and contains(@title,'" + title + "')]"));
    if (!els.isEmpty()) {
      return els.get(0);
    }
    // Fallback: try header text content
    els = getList(By.xpath("//th[@role='columnheader' and contains(.,'" + title + "')]"));
    if (!els.isEmpty()) {
      return els.get(0);
    }
    // Final fallback to original (will throw NoSuchElementException with meaningful context)
    return get(By.xpath("//th[@role='columnheader' and contains(@data-column,'" + title + "')]"));
  }

  public List<WebElement> getItemNumbers() {
    return getList(By.xpath("//a[contains(@href,'viewItemDetails')]"));
  }

  public List<WebElement> getItemNumbersList() {
    return getList(By.xpath("//a[contains(@data-popover,'#item-popover')]"));
  }

  public List<WebElement> getItemNumberWithDiffHref() {
    return getList(By.xpath("//a[contains(@href,'showItemWindow')]"));
  }

  public List<WebElement> getSearchResultRows() {
    List<WebElement> rows = new java.util.ArrayList<>();
    try {
      rows = getList(By.xpath("//div[@id='grid-result']//tbody//tr"));
    } catch (Exception e) {
      JLog.resetErrorCount();
    }
    if (rows.isEmpty()) {
      try {
        JLog.resetErrorCount();
        rows = getList(By.xpath("//table[contains(@id,'SearchResultTable_data')]//tbody//tr[not(contains(@class,'header'))]"));
      } catch (Exception e) {
        JLog.resetErrorCount();
      }
    }
    if (rows.isEmpty()) {
      // PriceTAM page uses eto-grid-frozen instead of grid-result
      try {
        JLog.resetErrorCount();
        rows = getList(By.xpath("//div[contains(@class,'eto-grid-frozen')]//tbody//tr"));
      } catch (Exception e) {
        JLog.resetErrorCount();
      }
    }
    return rows;
  }

  public List<WebElement> getSearchRows() {
    return getList(
        By.xpath(
            "//div[@class='eto-grid-scroll']/table/tbody//tr/td/a[contains(text(),'')]/../.."));
  }

  public List<WebElement> getSearchRowsForCR() {
    return getList(By.xpath("(//div[@class='eto-grid-scroll']//tbody//tr)[2]"));
  }

  public List<WebElement> getSearchRowsForCostRecord() {
    return getList(By.xpath("//div[@class='eto-grid-scroll']//tbody//tr"));
  }

  public WebElement getSuccessMessage() {
    return get(By.xpath("//div[@class='eto-messageblock__body']//ul//li"));
  }

  public List<WebElement> getSuccessMessages() {
    return getList(By.xpath("//div[@class='eto-messageblock__body']//ul//li"));
  }

  public List<WebElement> getErrorMessages() {
    return getList(By.xpath("//div[@class='eto-messageblock__body']//ul//li"));
  }

  public WebElement getListResultsMessage() {
    return get(By.className("eto-messageblock__body"));
  }

  public WebElement getExpandFilter() {
    return get(By.id("expand-container"));
  }

  public WebElement getHeaderFilterEle() {
    return get(By.xpath("//h3[contains(@class,'eto-expand__h3')]"));
  }

  public WebElement clickToggleBtn() {
    return get(By.xpath("//button[@class='eto-btn eto-btn--icon-only eto-dropdown__toggle']"));
  }

  public WebElement getPartialLinkText(String partialLinkText) {
    return get(By.partialLinkText(partialLinkText));
    // return get(By.xpath("//b[contains(text(),'" + partialLinkText + "')]"));
  }

  public WebElement getLinkText(String partialLinkText) {
    return get(By.xpath("//b[contains(text(),'" + partialLinkText + "')]"));
  }

  public WebElement getFilterName() {
    return get(By.name("newFilterInput"));
  }

  public WebElement getNameLink(String name) {
    return get(By.xpath("//a[contains(text(),'" + name + "')]"));
  }

  public WebElement getFilterSaveBtn() {
    return get(By.id("filterSaveModalButton"));
  }

  public WebElement getComboBox(String label) {
    return get(
        By.xpath("//label[contains(text(),'" + label + "')]/following-sibling::div//select"));
  }

  public WebElement getFilterCloseBtn() {
    return get(
        By.xpath(
            "//button[@id='filterSaveModalButton']/following-sibling::button[text()='Close']"));
  }

  public WebElement getEleWitTitle(String title) {
    return get(By.xpath("//*[@title='" + title + "']"));
  }

  public WebElement getTabItemDetails(int rowCount, int column) {
    return get(By.xpath("//tr[" + rowCount + "]//td[" + column + "]"));
    // [@class='']"));
  }

  public WebElement getSearchParentName() {
    return get(By.xpath("//input[contains(@name,'parentName')]"));
  }

  public WebElement getSelectSavedFilterCheckBox(String fN) {
    return get(By.xpath("//input[@value='" + fN + "']/ancestor::td/preceding-sibling::td//input"));
  }

  public WebElement getAnchorBtnLink(String btnText) {
    return get(By.xpath("//td[@class='buttonText']//a[contains(text(),'" + btnText + "')]"));
  }

  public WebElement getAutoCompleteTextFieldVal(String textField) {
    return get(
        By.xpath(
            "//input[@name='value(" + textField
                + ")']/ancestor::div[1]//li[@class='eto-results__option active']//b"));
  }

  public WebElement getMultipleCommoditySearchIcon() {
    return getElement(By.xpath("//*[contains(@onclick,'ItemCategoryFinder')]"));
  }

  public WebElement getPlatformSearchIcon() {
    return getElement(By.xpath("//*[contains(@onclick,'PlatformFinder')]"));
  }

  public WebElement getMultipleSearchGrpNameIcon() {
    return getElement(By.xpath("//*[contains(@onclick,'FunctionalGroupFinder')]"));
  }

  public WebElement getMultipleMRPSitesIcon() {
    return getElement(By.xpath("//*[contains(@onclick,'MRPSiteFinder')]"));
  }

  public WebElement getMultipleSearchParentGrpIcon() {
    return getElement(By.xpath("//*[contains(@onclick,'ParentGroupFinder')]"));
  }

  public WebElement getMultipleSearchDestnSitesNameIcon() {
    return getElement(By.xpath("//*[contains(@onclick,'EnterpriseCostSiteFinder')]"));
  }

  public WebElement getMultipleSearchItemNameIcon() {
    return getElement(By.xpath("//*[contains(@onclick,'EnterpriseItemFinder')]"));
  }

  public WebElement getIconBtn(String name) {
    return getElement(By.xpath("//button[@id='" + name + "']//i[text()='" + name + "']"));
  }

  public WebElement getIconButtons(String name) {
    // Strategy 1: material icon text in <i> element inside <button> (Chrome < 148)
    List<WebElement> byIcon = browser().findElements(By.xpath("//button//i[normalize-space(text())='" + name + "']"));
    if (!byIcon.isEmpty())
      return byIcon.get(0);
    // Strategy 2: material icon text in <span> inside <button> (Chrome 148+ Material Icons)
    List<WebElement> bySpan = browser().findElements(By.xpath("//button//span[normalize-space(text())='" + name + "']"));
    if (!bySpan.isEmpty())
      return bySpan.get(0);
    // Strategy 3: button identified by adjacent eto-tooltip content div
    List<WebElement> byTooltip = browser().findElements(By.xpath(
        "//div[contains(@class,'eto-tooltip__content') and normalize-space(text())='" + name + "']" +
            "/ancestor::div[contains(@class,'eto-tooltip')]/preceding-sibling::button[contains(@class,'eto-icon-btn')][1]"));
    if (!byTooltip.isEmpty()) return byTooltip.get(0);
    // Strategy 4: bare <i> element (not inside <button>) — legacy toolbar icons
    List<WebElement> byIconDirect = browser().findElements(By.xpath("//i[normalize-space(text())='" + name + "']"));
    if (!byIconDirect.isEmpty()) return byIconDirect.get(0);
    // Strategy 5: bare <span> with material class
    List<WebElement> bySpanDirect = browser().findElements(By.xpath("//span[contains(@class,'material') and normalize-space(text())='" + name + "']"));
    if (!bySpanDirect.isEmpty()) return bySpanDirect.get(0);
    // Strategy 6: aria-label or title attribute on button (accessibility-labelled icons)
    List<WebElement> byAriaLabel = browser().findElements(By.xpath("//button[@aria-label='" + name + "' or @title='" + name + "']"));
    return byAriaLabel.isEmpty() ? null : byAriaLabel.get(0);
  }

  public WebElement getIconButtonsInBOM(String name) {
    return getElement(By.xpath("//div[@id='headingDiv']//i[text()='" + name + "']"));
  }

  public WebElement getIconButtonsInRebate(String name) {
    return getElement(By.xpath("//div//i[text()='" + name + "']"));
  }

  public WebElement getCloseIconButtons(String name) {
    return getElement(By.xpath("//i[text()='" + name + "']"));
  }

  public WebElement getRebateProviderIcon() {
    return getElement(By.xpath("//*[contains(@onclick,'SupplierNameFinder')]"));
  }

  public List<WebElement> getIconBtns(String name) {
    return getList(By.xpath("//i[text()='" + name + "']"));
  }

  public WebElement getWorkSpaceTitle() {
    return getElement(
        By.xpath(
            "//div[@class='eto-dashboard__header e2mc-dashboard__header']//span[@class='eto-dashboard__title scplatform-dashboard__title']"));
  }

  public WebElement getCancelBtn() {
    return getElement(By.id("cancelButton"));
  }

  public WebElement getCloseBtn() {
    return getElement(By.id("CloseEventButton"));
  }

  public List<WebElement> getResultsStatus(String status) {
    List<WebElement> elements = getList(By.xpath("//tr//td//div[contains(text(),'" + status + "')]"));
    if (elements.size() == 0) {
      JLog.resetErrorCount();
      elements = getList(By.xpath("//tr//td[contains(text(),'" + status + "')]"));
    }
    return elements;
  }

  public WebElement getStatusOnRow(String row) {
    return get(By.xpath("//tr[" + row + "]//td[1][@class='']"));
  }

  public List<WebElement> getResultsItemNumbers(String item) {
    return getList(By.xpath("//a[@data-popover and text()='" + item + "']"));
  }

  public List<WebElement> getResultsName() {
    return getList(By.xpath("//tr//td[1]"));
  }

  public WebElement getXLOBFlexLOBField() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectxlobFGLOBs']//label[contains(text(),'XLOB Flex LOB')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getXLOBPlatformField() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectxlobFGPlatforms']//label[contains(text(),'XLOB Flex Platform')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getRegionformField() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectregion']//label[contains(text(),'Region')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getExpandMorePltformBtn() {
    return getElement(
        By.xpath("//div[@id='complexSelectxlobFGPlatforms']//span[text()='expand_more']"));
  }

  public WebElement getExpandMoreRegionButton() {
    return getElement(By.xpath("//div[@id='complexSelectregion']//span[text()='expand_more']"));
  }

  public WebElement getExpandMoreLOBsBtn() {
    return getElement(By.xpath("//div[@id='complexSelectxlobFGLOBs']//span[text()='expand_more']"));
  }

  public WebElement getXlobPlatformValueCheckBox(String value) {
    return getElement(
        By.xpath(
            "//span[contains(text(),'" + value + "')]/preceding-sibling::input[@type='checkbox']"));
  }

  public WebElement getExpandMorePlatformBtnonFG() {
    return getElement(By.xpath("//div[@id='complexSelectfgPlatform']//span[text()='expand_more']"));
  }

  public WebElement getExpandMoreLOBsBtnonFG() {
    return getElement(By.xpath("//div[@id='complexSelectlob']//span[text()='expand_more']"));
  }

  public WebElement getXLOBFlexPlatformonFG() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectfgPlatform']//label[contains(text(),'Platform')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getXLOBFlexLOBFieldonFG() {
    return getElement(
        By.xpath(
            "//div[@id='complexSelectlob']//label[contains(text(),'LOB')]/following-sibling::div//input[@class='eto-complex-combobox__field']"));
  }

  public WebElement getDropdown() {
    return getElement(By.xpath("//select[@id='messageType']"));
  }

  public WebElement getFileInput() {
    return getElement(By.xpath("//input[@type='file']"));
  }

  public WebElement getSubmitButton() {
    return getElement(By.xpath("//button[contains(text(),'Submit')]"));
  }

}
