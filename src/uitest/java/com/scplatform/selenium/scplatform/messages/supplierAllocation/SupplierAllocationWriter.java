/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.messages.supplierAllocation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.BeansException;

import com.scplatform.qa.e2Messages.utilities.MessageWriter;
import com.scplatform.qa.e2Messages.utilities.MessageWriterException;
import com.scplatform.qa.e2Messages.utilities.NullValue;
import com.scplatform.qa.iris.model.FieldDefinition;
import com.scplatform.qa.iris.model.FieldDefinitionsProvider;
import com.scplatform.qa.iris.model.FieldDefinitionsProvider.FieldDefinitionsProviderFactory;
import com.test.selenium.common.JLog;
import com.test.selenium.scplatform.constants.Constants;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * @author dgenrich
 *
 * @param <T>
 *            SupplierAllocation or any class that extends it
 *
 * @see MessageWriter
 */
public class SupplierAllocationWriter<T extends SupplierAllocation> extends MessageWriter<T> {
    protected final String messageType = "SupplierAllocation";
    protected final String messageVersion = "MCM1.0";

    protected List<T> messageData;

    /**
     * @param messageClazz
     *            The SupplierAllocation Message class, typically
     *            SupplierAllocation.class, but can be any class that extends
     *            it.
     * @param messageLines
     *            The message data, typically from
     *            {@link SupplierAllocationBuilder}
     */
    public SupplierAllocationWriter(Class<T> messageClazz, Iterable<T> messageLines) {
        super(messageClazz, messageLines);
        messageData = Lists.newArrayList(messageLines);
    }

    @Override
    public String generate() throws BeansException, MessageWriterException, IOException {
        if ((currentUploadType.equals(UPLOAD_TYPE.XLS)) || (currentUploadType.equals(UPLOAD_TYPE.XLSX))) {
            String template = "/com/scplatform/selenium/e2mtcm/messages/utilities/ExcelTemplate.vm";

            this.customVelocityTemplate = template;
            this.fdProvider = getExcelUploadFields();
            this.currentMessageFormat = MESSAGE_FORMAT.CUSTOM;

            return super.generate();
        }

        try {
            openFile(saveToFile);
            buildHeader();

            for (int row = 0; row < messageData.size(); row++) {
                buildSupplierAllocation(messageData.get(row));
            }

            out.write("</scplatform:SupplierAllocationMessage>");

            closeFile();
        } catch (IOException e) {
            JLog.error(e);
        }

        return saveToFile;
    }

    protected FieldDefinitionsProvider getExcelUploadFields() {

        FieldDefinitionsProvider fdp = FieldDefinitionsProviderFactory
                .createIndexOrderedFieldDefinitionsSatisfyingPredicateProvider(new Predicate<FieldDefinition>() {
                    int counter = 0;

                    @Override
                    public boolean apply(FieldDefinition fd) {
                        if (counter > 13) {
                            return false;
                        }
                        counter++;

                        return true;
                    }
                });

        return fdp;
    }

    protected BufferedWriter out = null;

    protected void openFile(String saveToFileName) throws UnsupportedEncodingException, FileNotFoundException {
        File file = new File(saveToFileName);
        if (file.exists()) {
            file.delete();
        }
        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(saveToFileName), "UTF-8"));
    }

    protected void closeFile() throws IOException {
        if (out != null) {
            out.close();
        }
    }

    protected void buildHeader() throws IOException {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<scplatform:SupplierAllocationMessage xmlns:scplatform=\"http://www.scplatform.local/E2openMCM\" ");
        out.write("headerVersion=\"1.0\" ");
        out.write("fromID=\"" + Constants.HubCompanyID + "\" ");
        out.write("toID=\"E2OPEN\" ");
        out.write("messageType=\"" + messageType + "\" ");
        out.write("messageVersion=\"" + messageVersion + "\" ");
        out.write("messageCount=\"0\" ");
        out.write("messageIndex=\"0\" ");
        out.write(">");
        out.write("\n");
    }

    protected void buildSupplierAllocation(SupplierAllocation data) throws IOException {

        out.write("\t<scplatform:SupplierAllocation");

        writeAttribute("itemGroupUniqueId", data.getItemUniqueId());
        writeAttribute("itemGroupVersion", data.getItemGroupVersion());
        writeAttribute("itemGroupRevision", data.getItemGroupRevision());
        writeAttribute("itemIdentifier", data.getCustomerItemIdentifier());
        writeAttribute("itemUniqueId", data.getItemUniqueId());
        writeAttribute("itemRevision", data.getItemRevision());
        writeAttribute("itemVersion", data.getItemVersion());
        writeAttribute("businessEntity", data.getCustomerBusinessEntity());
        writeAttribute("businessEntityType", data.getCustomerBusinessEntityType());
        writeAttribute("site", data.getCustomerSite());
        writeAttribute("supplierItemIdentifier", data.getSupplierItemIdentifier());
        writeAttribute("supplierItemUniqueId", data.getSupplierItemUniqueId());
        writeAttribute("supplierItemRevision", data.getSupplierItemRevision());
        writeAttribute("supplierItemVersion", data.getSupplierItemVersion());
        writeAttribute("supplierBusinessEntity", data.getSupplierBusinessEntity());
        writeAttribute("supplierBusinessEntityType", data.getSupplierBusinessEntityType());
        writeAttribute("supplierSite", data.getSupplierSite());
        writeAttribute("allocation", data.getAllocation());
        writeAttribute("effectiveFromDate", data.getEffectiveFromDate());
        writeAttribute("effectiveToDate", data.getEffectiveToDate());
        writeAttribute("description", data.getDescription());
        writeAttribute("comment", data.getComment());
        writeAttribute("operationCode", data.getOperationCode());
        writeAttribute("dataSource", data.getDataSource());

        out.write(">");
        out.write("\n");

        addContext(data);

        out.write("\t</scplatform:SupplierAllocation>\n");

    }

    protected void addContext(SupplierAllocation data) throws IOException {
        if (data.getContextType() == null) {
            return;
        } else if (data.getContextType().equals("Item")) {
            addItemContext(data);
        } else if (data.getContextType().equals("Platform")) {
            addPlatformContext(data);
        }

    }

    protected void writeAttribute(String attributeName, String data) throws IOException {
        if (data != null) {
            out.write(" " + attributeName + "=\"" + formatString(data) + "\"");
        }
    }

    protected void writeAttribute(String attributeName, float data) throws IOException {
        if (data != NullValue.FLOAT) {
            DecimalFormat numberFormat = new DecimalFormat("####.####");
            out.write(" " + attributeName + "=\"" + numberFormat.format(data) + "\"");
        }
    }

    protected void writeAttribute(String attributeName, DateTime data) throws IOException {
        if (data != null) {
            out.write(" " + attributeName + "=\"" + formatDate(data) + "\"");
        }
    }

    protected String formatString(String string) {
        return string.replace("&", "&amp;").replace("'", "&apos;").replace("\"", "&quot;");
    }

    protected String dateFormatString = null;

    /**
     * 
     * @param format
     *            The date format String to use. The default is
     *            {@link Constants#DateFormatExternal}
     */
    public void setDateFormatString(String format) {
        dateFormatString = format;
    }

    protected String formatDate(DateTime date) {
        return date.toString(getDateFormatString());
    }

    protected String getDateFormatString() {
        if (dateFormatString == null) {
            dateFormatString = Constants.DateFormatInbound;
        }
        return dateFormatString;
    }

    protected void addItemContext(SupplierAllocation data) throws IOException {

        out.write("\t\t<scplatform:Context>\n");
        out.write("\t\t\t<scplatform:Item");

        writeAttribute("businessEntity", data.getContextBusinessEntity());
        writeAttribute("businessEntityType", data.getContextBusinessEntityType());
        writeAttribute("itemIdentifier", data.getContextItemIdentifier());

        out.write("/>");
        out.write("\n");
        out.write("\t\t</scplatform:Context>\n");

    }

    protected void addPlatformContext(SupplierAllocation data) throws IOException {

        out.write("\t\t<scplatform:Context>\n");
        out.write("\t\t\t<scplatform:Platform");

        writeAttribute("businessEntity", data.getContextBusinessEntity());
        writeAttribute("businessEntityType", data.getContextBusinessEntityType());
        writeAttribute("platformName", data.getContextPlatformName());

        out.write("/>");
        out.write("\n");
        out.write("\t\t</scplatform:Context>\n");
    }

}
