/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.steps;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.scplatform.qa.e2Messages.utilities.MessageWriter.UPLOAD_TYPE;
import com.test.selenium.common.Configuration;
import com.test.selenium.common.JLog;
import com.test.selenium.common.TakeScreenshot;
import com.test.selenium.common.users.User;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.cucumber.CukeHelper;
import com.test.selenium.scplatform.cucumber.Preprocessing;
import com.test.selenium.scplatform.messages.calendar.CalendarType;
import com.test.selenium.scplatform.messages.commodityCode.CommodityCode;
import com.test.selenium.scplatform.messages.commodityCode.CommodityCodeBuilder;
import com.test.selenium.scplatform.messages.commodityCode.CommodityCodeModel;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.messages.forecast.ForecastCukeBuilder;
import com.test.selenium.scplatform.messages.utilities.SCPlatformWriterHelper;
import com.test.selenium.scplatform.utilities.MessageIO;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;


/**
 * DOCUMENTATION:
 * http://confluence.dev.scplatform.local/display/QA/Gherkin+Step+Definitions+-+MTCM+Message+Generation
 *
 * @author dgenrich
 *
 */
public class MessageGenerations {

  @Before
  public void beforeMethod(Scenario scenario) {
    JLog.setScenarioForCucumber(scenario);
    JLog.resetErrorCount();
  }
  private void checkForErrors() {
    if (JLog.getErrorCount() > 0) {
      JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
    }
  }

  //    @Given("I generate a XML BusinessEntity Message with parameters and save as {string}")
  //    public void businessEntityMessage(String saveMessageAs, DataTable parameters)
  //            throws FieldNotFoundException, InvalidValueException {
  //        List<Partner> doNotUpload = new ArrayList<Partner>();
  //
  //        BusinessEntityCukeBuilder<BusinessEntity> builder = new BusinessEntityCukeBuilder<BusinessEntity>(
  //                BusinessEntity.class, parameters);
  //
  //        List<BusinessEntity> data = builder.getBuilder().buildAsList();
  //
  //        MessageIO<BusinessEntity> messageIO = new MessageIO<BusinessEntity>(BusinessEntity.class);
  //        messageIO.save(data, saveMessageAs);
  //
  //        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
  //
  //            if (row.containsKey("doNotUploadCompanies")) {
  //                String doNotUploadCompanies = row.get("doNotUploadCompanies");
  //                doNotUpload = Utilities.getPartners(doNotUploadCompanies);
  //            }
  //            break; // only doing 1 row
  //        }
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        String filename = null;
  //        if (doNotUpload.isEmpty()) {
  //            filename = SCPlatformWriterHelper.BusinessEntity(data, BusinessEntity.class);
  //        } else {
  //            BusinessEntityUtils utils = new BusinessEntityUtils();
  //            filename = SCPlatformWriterHelper.BusinessEntity(utils.removePartners(data, doNotUpload), BusinessEntity.class);
  //        }
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, BusinessEntity.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //
  //        if (JLog.getErrorCount() > 0) {
  //            JLog.fail(JLog.getErrorCount() + " errors occurred in the test.  Check log.", TakeScreenshot.True);
  //        } else {
  //            JLog.write("Succefully created message file: " + FilenameUtils.getName(filename));
  //        }
  //
  //    }

  @Given("I generate a XML CommodityCode Message with parameters and save as {string}")
  public void commodityCodeMessage(String saveMessageAs, List<CommodityCodeModel> commodityCodes) {

    CommodityCodeBuilder<CommodityCode> builder = new CommodityCodeBuilder<>(CommodityCode.class,
        Preprocessing.process(commodityCodes));

    List<CommodityCode> data = builder.buildAsList();

    MessageIO<CommodityCode> messageIO = new MessageIO<>(CommodityCode.class);
    messageIO.save(data, saveMessageAs);

    SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
    SCPlatformWriterHelper.setToDUNS(Constants.StackID);
    String filename = SCPlatformWriterHelper.CommodityCode(data, CommodityCode.class);
    Configuration.setRuntime(saveMessageAs, filename);

    CukeHelper.setMessageClass(saveMessageAs, CommodityCode.class);
    Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);

    checkForErrors();
  }

  @Given("I generate a XML Calendar Message with parameters and save as {string}")
  //    public void calendarMessage(String saveMessageAs, DataTable parameters) {
  //        int offsetFromCurrentYear = 0;
  //        int numberOfYears = 1;
  //        CalendarType calendarType = CalendarType.Type_544;
  //        List<Calendar> calendarData = new ArrayList<Calendar>();
  //
  //        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
  //            offsetFromCurrentYear = Integer.parseInt(row.get("offsetFromCurrentYear"));
  //            numberOfYears = Integer.parseInt(row.get("numberOfYears"));
  //            calendarType = getCalendarType(row.get("calendarType"));
  //            break; // only doing 1 row
  //        }
  //
  //        DateTime startDate = null;
  //        for (int year = 0; year < numberOfYears; year++) {
  //            CalendarBuilder<Calendar> builder = new CalendarBuilder<Calendar>(Calendar.class,
  //                    offsetFromCurrentYear + year);
  //
  //            builder.withCalendarType(calendarType);
  //
  //            if (year > 0) {
  //                builder.withStartDate(startDate);
  //            }
  //
  //            List<Calendar> data = builder.buildAsList();
  //            calendarData.addAll(data);
  //
  //            startDate = data.get(data.size() - 1).getWeek_EndDate().plusDays(1);
  //
  //        }
  //
  //        MessageIO<Calendar> messageIO = new MessageIO<Calendar>(Calendar.class);
  //        messageIO.save(calendarData, saveMessageAs);
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        String filename = SCPlatformWriterHelper.Calendar(calendarData, Calendar.class);
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, Calendar.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //
  //        checkForErrors();
  //
  //        System.out.println(filename);
  //    }

  protected CalendarType getCalendarType(String type) {
    CalendarType calendarType = CalendarType.Type_544;

    if (type != null) {
      if (type.equals("445")) {
        calendarType = CalendarType.Type_445;
      } else if (type.equals("454")) {
        calendarType = CalendarType.Type_454;
      } else if (type.equals("544")) {
        calendarType = CalendarType.Type_544;
      } else {
        JLog.warning("Unknown Calendar Type: " + type + "; Using '544'");
      }
    }
    return calendarType;
  }

  //    @Given("I generate a XML Item Message with parameters and save as {string}")
  //    public void itemMessage(String saveMessageAs, DataTable parameters) throws IOException {
  //
  //        List<Item> data = buildItem(saveMessageAs, parameters);
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setUploadType(UPLOAD_TYPE.XML);
  //        String filename = SCPlatformWriterHelper.Item(data, Item.class);
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, Item.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //        checkForErrors();
  //    }

  //    @Given("I generate a XLS ItemAVL Message with parameters and save as {string}")
  //    public void itemAVLMessage(String saveMessageAs, DataTable parameters) throws IOException {
  //
  //        List<Item> itemData = buildItem("Item." + saveMessageAs, parameters);
  //
  //        ItemAVLBuilder<ItemAVL> builder = new ItemAVLBuilder<ItemAVL>(ItemAVL.class, itemData);
  //
  //        List<ItemAVL> itemAVLData = builder.buildAsList();
  //
  //        MessageIO<ItemAVL> messageIO = new MessageIO<ItemAVL>(ItemAVL.class);
  //        messageIO.save(itemAVLData, saveMessageAs);
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setUploadType(UPLOAD_TYPE.XLS);
  //        String filename = SCPlatformWriterHelper.ItemAVL(itemAVLData, ItemAVL.class);
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, ItemAVL.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //        checkForErrors();
  //    }

  //    @Given("I generate a XLS ItemPlatform Message with parameters and save as {string}")
  //    public void itemPlatformMessage(String saveMessageAs, DataTable parameters) throws IOException {
  //        String itemAVLKey = null;
  //        String platformName = null;
  //        String platformDescription = null;
  //
  //        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
  //            itemAVLKey = row.get("itemAVLKey");
  //            platformName = row.get("platformName");
  //            platformDescription = row.get("platformDescription");
  //            break; // only doing 1 row
  //        }
  //
  //        MessageIO<ItemAVL> messageIOItemAVL = new MessageIO<ItemAVL>(ItemAVL.class);
  //        List<ItemAVL> itemAVLData = messageIOItemAVL.load(itemAVLKey);
  //
  //        ItemPlatformBuilder<ItemPlatform> builder = new ItemPlatformBuilder<ItemPlatform>(ItemPlatform.class,
  //                itemAVLData, platformName, platformDescription);
  //
  //        List<ItemPlatform> itemPlatformData = builder.buildAsList();
  //
  //        ItemAVLUtils itemAVLUtils = new ItemAVLUtils();
  //        List<ItemAVL> itemAVLDataUpdated = itemAVLUtils.updateWithItemPlatform(itemAVLData, itemPlatformData);
  //
  //        MessageIO<ItemPlatform> messageIO = new MessageIO<ItemPlatform>(ItemPlatform.class);
  //        messageIO.save(itemPlatformData, saveMessageAs);
  //
  //        messageIOItemAVL.save(itemAVLDataUpdated, itemAVLKey);
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setUploadType(UPLOAD_TYPE.XLS);
  //        String filename = SCPlatformWriterHelper.ItemPlatform(itemPlatformData, ItemPlatform.class);
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, ItemPlatform.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //        checkForErrors();
  //    }
  //
  //    @Given("I generate a XLS ItemBOMAVL Message with parameters and save as {string}")
  //    public void itemBOMAVLMessage(String saveMessageAs, DataTable parameters) throws IOException {
  //
  //        List<Item> itemData = buildItem("Item." + saveMessageAs, parameters);
  //        Partner enterprisePartner = Partners.Enterprise();
  //
  //        ItemBOMAVLBuilder<ItemBOMAVL> builder = new ItemBOMAVLBuilder<ItemBOMAVL>(ItemBOMAVL.class, itemData,
  //                enterprisePartner);
  //
  //        List<ItemBOMAVL> itemBOMAVLData = builder.buildAsList();
  //
  //        MessageIO<ItemBOMAVL> messageIO = new MessageIO<ItemBOMAVL>(ItemBOMAVL.class);
  //        messageIO.save(itemBOMAVLData, saveMessageAs);
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setUploadType(UPLOAD_TYPE.XLS);
  //        String filename = SCPlatformWriterHelper.ItemBOMAVL(itemBOMAVLData, ItemBOMAVL.class);
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, ItemBOMAVL.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //        checkForErrors();
  //    }

  //    public List<Item> buildItem(String saveMessageAs, DataTable parameters) throws IOException {
  //        Iterable<BusinessEntity> businessEntityData = null;
  //        Iterable<CommodityCode> commodityCodeData = null;
  //        List<Partner> allCompanies = Partners.getAllPartners();
  //        File itemDataFile = ItemData.getItemDataFile();
  //        List<Variables> replacementVariables = null;
  //
  //        // Only ItemAndBOM are working at the current time (2014-11-26)
  //        // if changed, updated
  //        // http://confluence.dev.scplatform.local/display/QA/Item+Message+Parameters
  //        String buildAction = "ItemAndBOM";
  //
  //        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
  //
  //            MessageIO<BusinessEntity> messageIOBusinessEntity = new MessageIO<BusinessEntity>(BusinessEntity.class);
  //            businessEntityData = messageIOBusinessEntity.load(row.get("businessEntityKey"));
  //
  //            MessageIO<CommodityCode> messageIOCommodityCode = new MessageIO<CommodityCode>(CommodityCode.class);
  //            commodityCodeData = messageIOCommodityCode.load(row.get("commodityCodeKey"));
  //
  //            replacementVariables = ItemData.getByKeyName(row.get("replacementVariableKey"));
  //
  //            if (row.containsKey("buildAction")) {
  //                buildAction = row.get("buildAction");
  //            }
  //            break; // only doing 1 row
  //        }
  //
  //        String itemAppendString = "-" + DateTime.now().toString("yyMMddhhmmss");
  //        ItemBuilder<Item> builder = new ItemBuilder<Item>(Item.class, businessEntityData, commodityCodeData,
  //                allCompanies, itemDataFile, replacementVariables);
  //
  //        List<Item> data = builder.withBuildAction(buildAction).withItemAppendString(itemAppendString).buildAsList();
  //
  //        MessageIO<Item> messageIO = new MessageIO<Item>(Item.class);
  //        messageIO.save(data, saveMessageAs);
  //
  //        return data;
  //    }

  //   @Given("I generate a XML SourcingLane Message with parameters and save as {string}")
  //    public void sourcingLaneMessage(String saveMessageAs, DataTable parameters) throws IOException {
  //        List<Item> itemData = null;
  //        List<Calendar> calendarData = null;
  //        List<Partner> allCompanies = Partners.getAllPartners();
  //        boolean deleteExistingData = true;
  //
  //        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
  //            MessageIO<Item> messageIOItem = new MessageIO<Item>(Item.class);
  //            itemData = messageIOItem.load(row.get("itemKey"));
  //
  //            MessageIO<Calendar> messageIOCalendar = new MessageIO<Calendar>(Calendar.class);
  //            calendarData = messageIOCalendar.load(row.get("calendarKey"));
  //
  //            if (row.containsKey("deleteExistingData")) {
  //                deleteExistingData = row.get("deleteExistingData").equalsIgnoreCase("true");
  //            }
  //            break; // only doing 1 row
  //        }
  //
  //        if (deleteExistingData) {
  //            DatabaseUtils.deleteCostRecords();
  //            DatabaseUtils.deleteSourcingLane();
  //        }
  //
  //        SourcingLaneBuilder<SourcingLane> builder = new SourcingLaneBuilder<SourcingLane>(SourcingLane.class, itemData,
  //                calendarData, allCompanies);
  //
  //        List<SourcingLane> data = builder.buildAsList();
  //
  //        MessageIO<SourcingLane> messageIO = new MessageIO<SourcingLane>(SourcingLane.class);
  //        messageIO.save(data, saveMessageAs);
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setUploadType(UPLOAD_TYPE.XML);
  //        String filename = SCPlatformWriterHelper.SourcingLane(data, SourcingLane.class);
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, SourcingLane.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //        checkForErrors();
  //    }
  //
  //    @Given("I generate a XLS CostRecord Message with parameters and save as {string}")
  //    public void costRecordMessage(String saveMessageAs, DataTable parameters) throws IOException {
  //        sourcingLaneMessage(saveMessageAs, parameters);
  //
  //        MessageIO<SourcingLane> messageIO = new MessageIO<SourcingLane>(SourcingLane.class);
  //        List<SourcingLane> sourcingLaneData = messageIO.load(saveMessageAs);
  //
  //        // need to pass ITEMAVL here too, since can only have items that are on
  //        // the ItemAVL
  //        CostRecordBuilder<CostRecord> builder = new CostRecordBuilder<CostRecord>(CostRecord.class, sourcingLaneData);
  //
  //        List<CostRecord> costRecordData = builder.buildAsList();
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setUploadType(UPLOAD_TYPE.XLS);
  //        String filename = SCPlatformWriterHelper.CostRecord(costRecordData, CostRecord.class);
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, CostRecord.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //        checkForErrors();
  //    }
  //
  //    @Given("I generate a (XML|XLS) SupplierAllocation Message with parameters and save as {string}")
  //    public void supplierAllocationMessage(String fileUploadType, String saveMessageAs, DataTable parameters)
  //            throws IOException {
  //        UPLOAD_TYPE uploadType = getUploadType(fileUploadType);
  //
  //        List<Item> itemData = null;
  //        List<Calendar> calendarData = null;
  //        boolean deleteExistingData = true;
  //
  //        for (Map<String, String> row : parameters.asMaps(String.class, String.class)) {
  //            MessageIO<Item> messageIOItem = new MessageIO<Item>(Item.class);
  //            itemData = messageIOItem.load(row.get("itemKey"));
  //
  //            MessageIO<Calendar> messageIOCalendar = new MessageIO<Calendar>(Calendar.class);
  //            calendarData = messageIOCalendar.load(row.get("calendarKey"));
  //
  //            if (row.containsKey("deleteExistingData")) {
  //                deleteExistingData = row.get("deleteExistingData").equalsIgnoreCase("true");
  //            }
  //
  //            break; // only doing 1 row
  //        }
  //
  //        if (deleteExistingData) {
  //            DatabaseUtils.deleteAllocation();
  //        }
  //
  //        SupplierAllocationBuilder<SupplierAllocation> builder = new SupplierAllocationBuilder<SupplierAllocation>(
  //                SupplierAllocation.class, itemData, calendarData, Partners.Enterprise());
  //
  //        List<SupplierAllocation> data = builder.buildAsList();
  //
  //        if (uploadType.equals(UPLOAD_TYPE.XLS)) {
  //            for (int i = 0; i < data.size(); i++) {
  //                data.get(i).setCustomerItemGroupIdentifier("");
  //            }
  //        }
  //
  //        MessageIO<SupplierAllocation> messageIO = new MessageIO<SupplierAllocation>(SupplierAllocation.class);
  //        messageIO.save(data, saveMessageAs);
  //
  //        SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setToDUNS(Constants.StackID);
  //        SCPlatformWriterHelper.setUploadType(uploadType);
  //        String filename = SCPlatformWriterHelper.SupplierAllocation(data, SupplierAllocation.class);
  //        Configuration.setRuntime(saveMessageAs, filename);
  //
  //        CukeHelper.setMessageClass(saveMessageAs, SupplierAllocation.class);
  //        Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
  //        checkForErrors();
  //    }

  @Given("I generate a (XML|XLS) Forecast Message with parameters and save as {string}")
  public void forecastMessage(String fileUploadType, String saveMessageAs, DataTable parameters) throws IOException {
    UPLOAD_TYPE uploadType = getUploadType(fileUploadType);

    String lastChangedBy = null;
    User user = LoginUI.getCurrentLogggedInUser();
    if (user != null) {
      lastChangedBy = user.loginID;
    }

    ForecastCukeBuilder<Forecast> builder = new ForecastCukeBuilder<>(Forecast.class, parameters);
    List<Forecast> data = builder.getBuilder().withLastChangedBy(lastChangedBy).buildAsList();

    MessageIO<Forecast> messageIO = new MessageIO<>(Forecast.class);
    messageIO.save(data, saveMessageAs);

    SCPlatformWriterHelper.setFromDUNS(Constants.StackID);
    SCPlatformWriterHelper.setToDUNS(Constants.StackID);
    SCPlatformWriterHelper.setUploadType(uploadType);
    String filename = null;
    String forecastModel = builder.getForecastModel();
    if (StringUtils.isBlank(forecastModel)) {
      filename = SCPlatformWriterHelper.Forecast(data, Forecast.class);
    } else if ("CURRENT".equalsIgnoreCase(forecastModel)) {
      filename = SCPlatformWriterHelper.CurrentForecast(data, builder.getCalendar(), Forecast.class);
    } else if ("ADJUSTABLE".equalsIgnoreCase(forecastModel)) {
      filename = SCPlatformWriterHelper.AdjustableForecast(data, builder.getCalendar(), Forecast.class);
    } else {
      JLog.fail(String.format("Unknown Forecast Model:  %s", forecastModel), TakeScreenshot.False);
    }

    Configuration.setRuntime(saveMessageAs, filename);

    CukeHelper.setMessageClass(saveMessageAs, Forecast.class);
    Preprocessing.addPreprocessingClass(saveMessageAs, saveMessageAs);
    checkForErrors();
  }

  private UPLOAD_TYPE getUploadType(String fileUploadType) {
    UPLOAD_TYPE uploadType = UPLOAD_TYPE.XLS;

    if (fileUploadType.equalsIgnoreCase("XML")) {
      uploadType = UPLOAD_TYPE.XML;
    } else if (fileUploadType.equalsIgnoreCase("XLS")) {
      uploadType = UPLOAD_TYPE.XLS;
    } else {
      JLog.fail(String.format("Upload type '%s' not supported for MTCM!", fileUploadType), TakeScreenshot.True);
    }

    return uploadType;
  }

}
