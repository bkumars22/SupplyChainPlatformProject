/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.utilities;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeansException;

import com.scplatform.qa.e2Messages.utilities.MessageNameBuilder;
import com.scplatform.qa.e2Messages.utilities.MessageWriter.MESSAGE_FORMAT;
import com.scplatform.qa.e2Messages.utilities.MessageWriter.UPLOAD_TYPE;
import com.scplatform.qa.e2Messages.utilities.MessageWriterException;
import com.scplatform.qa.iris.model.FieldDefinitionsProvider;
import com.scplatform.qa.iris.serialization.excel.TextToExcelSerializer;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.scplatform.constants.Constants;
import com.test.selenium.scplatform.messages.adjustableForecast.AdjustableForecastWriter;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntity;
import com.test.selenium.scplatform.messages.businessEntity.BusinessEntityWriter;
import com.test.selenium.scplatform.messages.calendar.Calendar;
import com.test.selenium.scplatform.messages.calendar.CalendarWriter;
import com.test.selenium.scplatform.messages.commodityCode.CommodityCode;
import com.test.selenium.scplatform.messages.commodityCode.CommodityCodeWriter;
import com.test.selenium.scplatform.messages.costRecord.CostRecord;
import com.test.selenium.scplatform.messages.costRecord.CostRecordWriter;
import com.test.selenium.scplatform.messages.currentForecast.CurrentForecastWriter;
import com.test.selenium.scplatform.messages.forecast.Forecast;
import com.test.selenium.scplatform.messages.forecast.ForecastWriter;
import com.test.selenium.scplatform.messages.item.Item;
import com.test.selenium.scplatform.messages.item.ItemWriter;
import com.test.selenium.scplatform.messages.itemAVL.ItemAVL;
import com.test.selenium.scplatform.messages.itemAVL.ItemAVLWriter;
import com.test.selenium.scplatform.messages.itemBOMAVL.ItemBOMAVL;
import com.test.selenium.scplatform.messages.itemBOMAVL.ItemBOMAVLWriter;
import com.test.selenium.scplatform.messages.itemPlatform.ItemPlatform;
import com.test.selenium.scplatform.messages.itemPlatform.ItemPlatformWriter;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLane;
import com.test.selenium.scplatform.messages.sourcingLane.SourcingLaneWriter;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocation;
import com.test.selenium.scplatform.messages.supplierAllocation.SupplierAllocationWriter;

public class SCPlatformWriterHelper {

    // =================================================================
    public static String BusinessEntity(List<BusinessEntity> businessEntityData) {
        return BusinessEntity(businessEntityData, BusinessEntity.class);
    }

    public static <T extends BusinessEntity> String BusinessEntity(List<T> businessEntityData,
            Class<T> businessEntityClazz) {
        setDefaultGenDir();

        setUploadType(UPLOAD_TYPE.XML);

        String file = null;
        String template;
        try {
            file = makeFileName("BusinessEntity");
            template = "/com/scplatform/selenium/scplatform/messages/businessEntity/BusinessEntityTemplate.vm";
            BusinessEntityWriter<T> writer = new BusinessEntityWriter<T>(businessEntityClazz, businessEntityData);
            writer.withFileSaveName(file).withUploadType(UPLOAD_TYPE.TXT).withMessageFormat(MESSAGE_FORMAT.CUSTOM)
                    .withCustomVelocityTemplate(template).addToMergeContext("fromID", Constants.HubCompanyID)
                    .generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String CommodityCode(List<CommodityCode> commodityCodeData) {
        return CommodityCode(commodityCodeData, CommodityCode.class);
    }

    public static <T extends CommodityCode> String CommodityCode(List<T> commodityCodeData,
            Class<T> commodityCodeClazz) {
        setDefaultGenDir();

        setUploadType(UPLOAD_TYPE.XML);

        String file = null;
        String template;
        try {
            file = makeFileName("CommodityCode");
            template = "/com/scplatform/selenium/scplatform/messages/commodityCode/CommodityCodeTemplate.vm";
            CommodityCodeWriter<T> writer = new CommodityCodeWriter<T>(commodityCodeClazz, commodityCodeData);
            writer.withFileSaveName(file).withUploadType(UPLOAD_TYPE.TXT).withMessageFormat(MESSAGE_FORMAT.CUSTOM)
                    .withCustomVelocityTemplate(template.toString()).addToMergeContext("fromID", Constants.HubCompanyID)
                    .generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String Calendar(List<Calendar> calendarData) {
        return Calendar(calendarData, Calendar.class);
    }

    public static <T extends Calendar> String Calendar(List<T> calendarData, Class<T> calendarClazz) {
        setDefaultGenDir();

        setUploadType(UPLOAD_TYPE.XML);

        String file = null;
        try {
            file = makeFileName("Calendar");
            CalendarWriter<T> writer = new CalendarWriter<T>(calendarClazz, calendarData);
            writer.withFileSaveName(file).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String Item(List<Item> itemData) {
        return Item(itemData, Item.class);
    }

    public static <T extends Item> String Item(List<T> itemData, Class<T> itemClazz) {
        setDefaultGenDir();

        String file = null;
        try {
            file = makeFileName("Item");
            ItemWriter<T> writer = new ItemWriter<T>(itemClazz, itemData);
            writer.withFileSaveName(file).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String SourcingLane(List<SourcingLane> sourcingLaneData) {
        return SourcingLane(sourcingLaneData, SourcingLane.class);
    }

    public static <T extends SourcingLane> String SourcingLane(List<T> sourcingLaneData, Class<T> sourcingLaneClazz) {
        setDefaultGenDir();

        String file = null;
        try {
            file = makeFileName("SourcingLane");
            SourcingLaneWriter<T> writer = new SourcingLaneWriter<T>(sourcingLaneClazz, sourcingLaneData);
            writer.withFileSaveName(file).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String SupplierAllocation(List<SupplierAllocation> supplierAllocationData) {
        return SupplierAllocation(supplierAllocationData, SupplierAllocation.class);
    }

    public static <T extends SupplierAllocation> String SupplierAllocation(List<T> supplierAllocationData,
            Class<T> supplierAllocationClazz) {
        setDefaultGenDir();

        String file = null;
        try {
            file = makeFileName("SupplierAllocation");
            SupplierAllocationWriter<T> writer = new SupplierAllocationWriter<T>(supplierAllocationClazz,
                    supplierAllocationData);
            writer.withFileSaveName(file).withUploadType(uploadType).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String Forecast(List<Forecast> forecastData) {
        return Forecast(forecastData, Forecast.class);
    }

    public static <T extends Forecast> String Forecast(List<T> forecastData, Class<T> forecastClazz) {
        setDefaultGenDir();

        String file = null;
        try {
            file = makeFileName("Forecast");
            ForecastWriter<T> writer = new ForecastWriter<T>(forecastClazz, forecastData);
            writer.withFileSaveName(file).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String AdjustableForecast(List<Forecast> forecastData, List<Calendar> calendarData) {
        return AdjustableForecast(forecastData, calendarData, Forecast.class);
    }

    public static <T extends Forecast> String AdjustableForecast(List<T> forecastData, List<Calendar> calendarData,
            Class<T> forecastClazz) {
        setDefaultGenDir();

        String file = null;
        try {
            file = makeFileName("AdjustableForecast");
            AdjustableForecastWriter<T> writer = new AdjustableForecastWriter<T>(forecastClazz, forecastData,
                    calendarData);
            writer.withFileSaveName(file).withUploadType(uploadType).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String CurrentForecast(List<Forecast> forecastData, List<Calendar> calendarData) {
        return CurrentForecast(forecastData, calendarData, Forecast.class);
    }

    public static <T extends Forecast> String CurrentForecast(List<T> forecastData, List<Calendar> calendarData,
            Class<T> forecastClazz) {
        setDefaultGenDir();

        String file = null;
        try {
            file = makeFileName("CurrentForecast");
            CurrentForecastWriter<T> writer = new CurrentForecastWriter<T>(forecastClazz, forecastData, calendarData);
            writer.withFileSaveName(file).withUploadType(uploadType).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String ItemAVL(List<ItemAVL> businessEntityData) {
        return ItemAVL(businessEntityData, ItemAVL.class);
    }

    public static <T extends ItemAVL> String ItemAVL(List<T> businessEntityData, Class<T> businessEntityClazz) {
        setDefaultGenDir();

        String file = null;
        String template;
        try {
            file = makeFileName("ItemAVL");
            template = "/com/scplatform/selenium/scplatform/messages/utilities/ExcelTemplate.vm";
            ItemAVLWriter<T> writer = new ItemAVLWriter<T>(businessEntityClazz, businessEntityData);
            writer.withFileSaveName(file).withUploadType(uploadType).withMessageFormat(MESSAGE_FORMAT.CUSTOM)
                    .withCustomVelocityTemplate(template).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String ItemBOMAVL(List<ItemBOMAVL> businessEntityData) {
        return ItemBOMAVL(businessEntityData, ItemBOMAVL.class);
    }

    public static <T extends ItemBOMAVL> String ItemBOMAVL(List<T> businessEntityData, Class<T> businessEntityClazz) {
        setDefaultGenDir();

        String file = null;
        String template;
        try {
            file = makeFileName("ItemBOMAVL");
            template = "/com/scplatform/selenium/scplatform/messages/utilities/ExcelTemplate.vm";
            ItemBOMAVLWriter<T> writer = new ItemBOMAVLWriter<T>(businessEntityClazz, businessEntityData);
            writer.withFileSaveName(file).withUploadType(uploadType).withMessageFormat(MESSAGE_FORMAT.CUSTOM)
                    .withCustomVelocityTemplate(template.toString()).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String ItemPlatform(List<ItemPlatform> businessEntityData) {
        return ItemPlatform(businessEntityData, ItemPlatform.class);
    }

    public static <T extends ItemPlatform> String ItemPlatform(List<T> businessEntityData,
            Class<T> businessEntityClazz) {
        setDefaultGenDir();

        String file = null;
        String template;
        try {
            file = makeFileName("ItemPlatform");
            template = "/com/scplatform/selenium/scplatform/messages/utilities/ExcelTemplate.vm";
            ItemPlatformWriter<T> writer = new ItemPlatformWriter<T>(businessEntityClazz, businessEntityData);
            writer.withFileSaveName(file).withUploadType(uploadType).withMessageFormat(MESSAGE_FORMAT.CUSTOM)
                    .withCustomVelocityTemplate(template.toString()).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    // =================================================================
    public static String CostRecord(List<CostRecord> businessEntityData) {
        return CostRecord(businessEntityData, CostRecord.class);
    }

    public static <T extends CostRecord> String CostRecord(List<T> businessEntityData, Class<T> businessEntityClazz) {
        setDefaultGenDir();

        String file = null;
        String template;
        try {
            file = makeFileName("CostRecord");
            template = "/com/scplatform/selenium/scplatform/messages/utilities/ExcelTemplate.vm";
            CostRecordWriter<T> writer = new CostRecordWriter<T>(businessEntityClazz, businessEntityData);
            writer.withFileSaveName(file).withUploadType(uploadType).withMessageFormat(MESSAGE_FORMAT.CUSTOM)
                    .withCustomVelocityTemplate(template.toString()).generate();
        } catch (BeansException | MessageWriterException | IOException e) {
            JLog.fail(e);
        }
        fdProvider = null;
        return file;
    }

    private static String fromDUNS = null;
    private static String toDUNS = null;
    protected static UPLOAD_TYPE uploadType = UPLOAD_TYPE.XLS;
    private static File genDir = null;

    private static String fromDUNSPrevious = null;
    private static String toDUNSPrevious = null;
    private static UPLOAD_TYPE uploadTypePrevious = UPLOAD_TYPE.XLS;
    private static File genDirPrevious = null;
    protected static FieldDefinitionsProvider fdProvider = null;

    /**
     * Resets values of fromDuns, toDuns, uploadType, and genDir to the values
     * that where set prior to the last set call. This is used so that a test
     * can change the values, then reset them after setting.
     *
     * @see #setFromDUNS(String)
     * @see #setToDUNS(String)
     * @see #setUploadType(UPLOAD_TYPE)
     * @see #setGenDir(File)
     */
    public static void resetToPreviousValues() {
        SCPlatformWriterHelper.fromDUNS = SCPlatformWriterHelper.fromDUNSPrevious;
        SCPlatformWriterHelper.toDUNS = SCPlatformWriterHelper.toDUNSPrevious;
        SCPlatformWriterHelper.uploadType = SCPlatformWriterHelper.uploadTypePrevious;
        SCPlatformWriterHelper.genDir = SCPlatformWriterHelper.genDirPrevious;
    }

    /**
     * Resets values of fromDuns, toDuns, uploadType, and genDir to null.
     *
     * @see #setFromDUNS(String)
     * @see #setToDUNS(String)
     * @see #setGenDir(File)
     */
    public static void resetToNull() {
        SCPlatformWriterHelper.fromDUNS = null;
        SCPlatformWriterHelper.toDUNS = null;
        SCPlatformWriterHelper.genDir = null;
    }

    /**
     * Serialize the messages lines with only fields provided by the
     * {@link FieldDefinitionsProvider} in the order provided by the provider
     *
     * @param fieldDefinitionsProvider
     * @return
     */
    public static void setFieldDefinitionsProvider(FieldDefinitionsProvider fieldDefinitionsProvider) {
        fdProvider = fieldDefinitionsProvider;
    }

    /**
     * Gets the Stack Company Name with the desired profile
     *
     * <br>
     * <b>Examples for Stack SSPDEV106:</b>
     * <UL>
     * <LI>getStackCompany(STACKPROFILES.Tenant) ==> SSPDEV106
     * <LI>getStackCompany(STACKPROFILES.Customer) ==> SSPDEV106-Customer
     * <LI>getStackCompany(STACKPROFILES.LogisticsProvider) ==>
     * SSPDEV106-LogisticsProvider
     * <LI>getStackCompany(STACKPROFILES.Supplier) ==> SSPDEV106-Supplier
     * <LI>getStackCompany(STACKPROFILES.Vmi_3pl) ==> SSPDEV106-Vmi-3pl
     * </UL>
     *
     * @param profile
     *            STACKPROFILES enum for the profile to return
     * @param getCompanyName
     *            Gets the hub.company.name rather than the hub.company.id (if
     *            hub.company.id exists)
     * @return Gets the stack name (stack.name in stack properties file) and
     *         concatenates the profile name to it.
     */
    // protected static String getStackCompany (STACKPROFILES profile, boolean
    // getCompanyName) {
    // Prop prop = Prop.getInstance();
    //
    // String stackCompany = null;
    // if ((prop.get().containsKey("hub.company.id")) && (!getCompanyName)) {
    // stackCompany = prop.get().getProperty("hub.company.id") +
    // profile.toString();
    // } else {
    // stackCompany = prop.get().getProperty("hub.company.name") +
    // profile.toString();
    // }
    //
    // return stackCompany;
    // }

    protected static String setDefaultFromDuns(String defaultDuns) {
        if (StringUtils.isNotBlank(SCPlatformWriterHelper.fromDUNS)) {
            return SCPlatformWriterHelper.fromDUNS;
        }
        return defaultDuns;
    }

    /**
     * @param fromDUNS
     *            the fromDUNS to set <br>
     *            Call {@link #resetToPreviousValues()} to change values back to
     *            the previous values
     */
    public static void setFromDUNS(String fromDUNS) {
        SCPlatformWriterHelper.fromDUNSPrevious = SCPlatformWriterHelper.fromDUNS;
        SCPlatformWriterHelper.fromDUNS = fromDUNS;
    }

    protected static String setDefaultToDuns(String defaultDuns) {
        if (StringUtils.isNotBlank(SCPlatformWriterHelper.toDUNS)) {
            return SCPlatformWriterHelper.toDUNS;
        }
        return defaultDuns;
    }

    /**
     * @param toDUNS
     *            the toDUNS to set <br>
     *            Call {@link #resetToPreviousValues()} to change values back to
     *            the previous values
     */
    public static void setToDUNS(String toDUNS) {
        SCPlatformWriterHelper.toDUNSPrevious = SCPlatformWriterHelper.toDUNS;
        SCPlatformWriterHelper.toDUNS = toDUNS;
    }

    /**
     * @param uploadType
     *            the uploadType to set <br>
     *            Call {@link #resetToPreviousValues()} to change values back to
     *            the previous values
     */
    public static void setUploadType(UPLOAD_TYPE uploadType) {
        SCPlatformWriterHelper.uploadTypePrevious = SCPlatformWriterHelper.uploadType;
        SCPlatformWriterHelper.uploadType = uploadType;
        TextToExcelSerializer.setExcelType(uploadType.toString());
    }

    public static void setDefaultGenDir() {
        if (SCPlatformWriterHelper.genDir == null) {
            Prop prop = Prop.getInstance();
            if (prop != null) {
                setGenDir(new File(prop.getWorkingDir()));
            }
        }
    }

    /**
     * @param genDir
     *            the genDir to set
     */
    public static void setGenDir(File genDir) {
        SCPlatformWriterHelper.genDirPrevious = SCPlatformWriterHelper.genDir;
        SCPlatformWriterHelper.genDir = genDir;

    }

    protected static String getUniqueID() {
        return DateTime.now().toString("yyyyMMddHHmmss");
    }

    public static String makeFileName(String messageType) {
        return makeFileName(messageType, fromDUNS, toDUNS, uploadType);
    }

    public static String makeFileName(String messageType, String fromDuns, String toDuns, UPLOAD_TYPE uploadType) {
        MessageNameBuilder messageNameBuilder = new MessageNameBuilder(messageType, fromDuns, toDuns);
        return messageNameBuilder.withExtension(uploadType).setDefaultSaveLocation(genDir.toString())
                .withUniqueID(getUniqueID()).withVersion("MCM1.0").build();
    }

}
