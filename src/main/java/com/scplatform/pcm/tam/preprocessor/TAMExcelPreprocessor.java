/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.tam.preprocessor;

import com.scplatform.pcm.upload.loader.ExcelToXMLProcessor;
import com.scplatform.pcm.upload.loader.MessageLoaderException;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import lombok.extern.log4j.Log4j2;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TAMExcelPreprocessor extends ExcelToXMLProcessor {

    /**
     * Maps each upload-type UI string to [xsdFileName, rowElementName, rootElementName].
     */
    private static final Map<String, List<String>> UI_MAP = new HashMap<>();

    static {
        UI_MAP.put("TAMSupplierCFGUploadUI",
                List.of("TAMSupplierCFGUploadMessage.xsd", "TAMSupplierAllocation", "TAMMessage"));
        UI_MAP.put("TAMItemCFGUploadUI",
                List.of("TAMItemCFGUploadMessage.xsd", "TAMItemAllocation", "TAMMessage"));
        UI_MAP.put("TAMAllocationMassUpdateCFGUploadUI",
                List.of("TAMAllocationMassUpdateCFGUploadMessage.xsd", "TAMAllocation", "TAMMessage"));
        UI_MAP.put("TAMAllocationDeleteUI",
                List.of("TAMAllocationDeleteMessage.xsd", "TAMAllocationDelete", "TAMAllocationDeleteMessage"));
        // MRP-site variants
        UI_MAP.put("TAMSupplierCFGMRPSiteUploadUI",
                List.of("TAMSupplierCFGMRPSiteUploadMessage.xsd", "TAMSupplierAllocationMRPSite", "TAMMessage"));
        UI_MAP.put("TAMItemCFGMRPSiteUploadUI",
                List.of("TAMItemCFGMRPSiteUploadMessage.xsd", "TAMItemAllocationMRPSite", "TAMMessage"));
        UI_MAP.put("TAMAllocationMassUpdateCFGMRPSiteUploadUI",
                List.of("TAMAllocationMassUpdateCFGMRPSiteUploadMessage.xsd", "TAMAllocationMRPSite", "TAMMessage"));
    }

    /** Bucket allocation column headers in the Excel template. */
    private static final List<String> BUCKET_KEYS = List.of(
            "M1W1", "M1W2", "M1W3", "M1W4", "M1W5",
            "M2W1", "M2W2", "M2W3", "M2W4", "M2W5",
            "M3", "M4", "M5", "M6", "M7", "M8", "M9", "M10", "M11", "M12"
    );

    // -----------------------------------------------------------------------
    // Per-invocation state (reset on each processReadExcel call)
    // -----------------------------------------------------------------------

    /** Upload type resolved at {@link #processReadExcel} time; drives element names. */
    private String currentUploadType;

    /** Rows read from the Excel file. */
    private List<Map<String, String>> excelRows = new ArrayList<>();

    // -----------------------------------------------------------------------
    // IExcelToXml implementation
    // -----------------------------------------------------------------------

    @Override
    public void processReadExcel(Map<String, String> loadProps, String uploadType,
                                 File inFile, File outFile, Set<String> errors, Users user) throws Exception {
        this.currentUploadType = uploadType;
        this.excelRows = new ArrayList<>();

        // TAM Excel layout:
        //   Row 0 — column key names (ParentFGName, FGName, ...)  ← header row
        //   Row 1 — section labels  (TAM Details, Allocation)     ← skip
        //   Row 2 — display labels  (PFG Name, FG Name, ...)      ← skip
        //   Row 3+ — actual data
        // readExcel(inFile, 0) uses row 0 as the header; rows 1..N are returned as data.
        // We discard the first 2 returned rows (section labels + display labels).
        startDataRow = 0;

        long t = System.currentTimeMillis();
        List<Map<String, String>> allRows = readExcel(inFile, startDataRow);
        final int TAM_SKIP_ROWS = 2;
        excelRows = allRows.size() > TAM_SKIP_ROWS
                ? new ArrayList<>(allRows.subList(TAM_SKIP_ROWS, allRows.size()))
                : new ArrayList<>();
        log.info("TAMExcelPreprocessor: Excel read in {} ms", System.currentTimeMillis() - t);

        if (excelRows.isEmpty()) {
            errors.add("No data rows found in Excel file: " + inFile.getName());
            return;
        }

        // Feature-flag: UserItemType restriction check.
        // FlexAttributeManager / PcmUtil.checkItemAndSupplierHasUserItemType are legacy modules
        // not yet migrated to Spring Boot — this block is skipped when the flag is false (default).
        boolean tamRestrictionEnabled = pcmConfigUtil != null
                && pcmConfigUtil.getBoolean("pcm.feature.enable.tam.restriction.for.UserItemType", false);
        if (tamRestrictionEnabled) {
            List<String> genericItemTypes = (pcmConfigUtil != null)
                    ? pcmConfigUtil.getList("pcm.item.flexattribute.usertItemType.genericTypes")
                    : Collections.emptyList();
            if (genericItemTypes == null) {
                genericItemTypes = Collections.emptyList();
            }
            // TODO: Wire FlexAttributeManager.ITEM.getFlexAttributeDefn("userItemType") and
            //       PcmUtil.checkItemAndSupplierHasUserItemType(...) once those modules are migrated.
            log.warn("TAMExcelPreprocessor: pcm.feature.enable.tam.restriction.for.UserItemType=true "
                    + "but FlexAttributeManager/PcmUtil are not yet migrated — UserItemType restriction skipped.");
        }

        if (!errors.isEmpty()) {
            throw new Exception(String.join("\n", errors));
        }

        List<String> meta = UI_MAP.get(uploadType);
        if (meta == null) {
            throw new MessageLoaderException("No UI mapping found for upload type: " + uploadType);
        }

        t = System.currentTimeMillis();
        writeXml(outFile, meta.get(0), errors);
        log.info("TAMExcelPreprocessor: XML written in {} ms", System.currentTimeMillis() - t);
    }

    @Override
    public void writeXmlHeader(XMLStreamWriter xw) throws XMLStreamException {
        List<String> meta = UI_MAP.get(currentUploadType);
        String rootElement  = (meta != null) ? meta.get(2) : "TAMMessage";
        String messageType  = resolveMessageType(currentUploadType);

        xw.writeStartDocument("UTF-8", "1.0");
        xw.writeCharacters("\n");
        xw.writeStartElement("scplatform", rootElement, E2OPEN_NS);
        xw.writeNamespace("scplatform", E2OPEN_NS);
        xw.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xw.writeAttribute("headerVersion", "1.0");
        xw.writeAttribute("fromID", "");
        xw.writeAttribute("toID", "");
        xw.writeAttribute("messageType", messageType);
        xw.writeAttribute("messageVersion", "MCM1.0");
        xw.writeAttribute("messageCount", "1");
        xw.writeAttribute("messageIndex", "0");
        xw.writeCharacters("\n");
    }

    @Override
    public void writeXml(File outFile, String xsdFileName, Set<String> errors)
            throws IOException, MessageLoaderException {
        List<String> meta = UI_MAP.get(currentUploadType);
        String rowElement = (meta != null) ? meta.get(1) : "TAMSupplierAllocation";
        boolean isDelete  = "TAMAllocationDeleteUI".equals(currentUploadType);

        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile))) {
            XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(bos, "UTF-8");

            writeXmlHeader(writer);

            for (Map<String, String> row : excelRows) {
                writer.writeStartElement("scplatform", rowElement, E2OPEN_NS);
                if (isDelete) {
                    writeDeleteAttributes(writer, row);
                } else {
                    writeConditionalAttributes(writer, row);
                    writeCommonAttributes(writer, row);
                    writeBucketAttributes(writer, row);
                }
                writer.writeEndElement();
                writer.writeCharacters("\n");
            }

            writer.writeEndElement(); // root element
            writer.writeEndDocument();
            writer.flush();
            writer.close();

            validateXmlAgainstXsd(outFile, xsdFileName, errors);
        } catch (XMLStreamException e) {
            log.error("Error writing TAM XML: {}", e.getMessage(), e);
            throw new MessageLoaderException("Error while generating XML: " + e.getMessage(), e);
        }
    }

    @Override
    public void validateXmlAgainstXsd(File inXmlFile, String xsdFileName, Set<String> errors)
            throws MessageLoaderException {
        try {
            File xsdFile = getTemplateExcelFile(xsdFileName);
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);

            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setSchema(schema);

            SAXParser parser = spf.newSAXParser();
            XMLReader reader = parser.getXMLReader();

            Deque<String> contextStack = new ArrayDeque<>();
            reader.setContentHandler(new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attrs) {
                    String fgName = attrs.getValue("functionalGroupName");
                    if (fgName != null) {
                        contextStack.push("functionalGroupName=" + fgName);
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    if (!contextStack.isEmpty()) {
                        contextStack.pop();
                    }
                }
            });

            reader.setErrorHandler(new DefaultHandler() {
                private String withContext(SAXParseException e, String level) {
                    if (contextStack.isEmpty()) {
                        return null;
                    }
                    String ctx = contextStack.peek();
                    String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Unknown error";
                    if (msg.contains(":")) {
                        msg = msg.substring(msg.indexOf(':') + 1).trim();
                    }
                    return SCPlatformMessages.INSTANCE.getMessage(
                            "pcm.mdm.sa.validation.message",
                            new Object[]{level + ": " + msg, ctx},
                            null);
                }

                @Override
                public void error(SAXParseException e) {
                    String message = withContext(e, "VALIDATION ERROR");
                    if (message != null) {
                        errors.add(message);
                    }
                }

                @Override
                public void fatalError(SAXParseException e) {
                    String message = withContext(e, "FATAL");
                    if (message != null) {
                        errors.add(message);
                    }
                }

                @Override
                public void warning(SAXParseException e) {
                    String message = withContext(e, "WARNING");
                    if (message != null) {
                        errors.add(message);
                    }
                }
            });

            reader.parse(new InputSource(new FileInputStream(inXmlFile)));

        } catch (Exception e) {
            errors.add("Validation process error: " + e.getMessage());
            log.error("XSD validation error", e);
            throw new MessageLoaderException("XSD validation error: " + e.getMessage(), e);
        }
    }

    // -----------------------------------------------------------------------
    // Attribute writers
    // -----------------------------------------------------------------------

    /**
     * Writes attributes present on all non-delete TAM row elements.
     */
    private void writeCommonAttributes(XMLStreamWriter writer, Map<String, String> row)
            throws XMLStreamException {
        putAttr(writer, "functionalGroupName", row.get("FGName"));
        putAttr(writer, "fgType",              row.get("FGType"));
        putAttr(writer, "siteDescription",     row.get("Site"));
        putAttr(writer, "supplierName",        row.get("SupplierName"));
        putAttr(writer, "fgStatus",            row.get("FGStatus"));
        putAttr(writer, "userItemType",        row.get("UserItemType"));
        putAttr(writer, "mrpSite",             row.get("MRPSite"));
    }

    /**
     * Writes upload-type-specific conditional attributes.
     */
    private void writeConditionalAttributes(XMLStreamWriter writer, Map<String, String> row)
            throws XMLStreamException {
        switch (currentUploadType) {
            case "TAMSupplierCFGUploadUI":
            case "TAMSupplierCFGMRPSiteUploadUI":
            case "TAMAllocationMassUpdateCFGUploadUI":
            case "TAMAllocationMassUpdateCFGMRPSiteUploadUI":
                putAttr(writer, "parentFunctionalGroupName", row.get("ParentFGName"));
                putAttr(writer, "allowHedging",              row.get("AllowHedging"));
                break;
            case "TAMItemCFGUploadUI":
            case "TAMItemCFGMRPSiteUploadUI":
                putAttr(writer, "itemIdentifier", row.get("ItemIdentifier"));
                break;
            default:
                break;
        }
    }

    /**
     * Writes bucket allocation period values (M1W1..M12) as attributes.
     * Numeric values are floor'd to integers; non-numeric values are written as-is.
     */
    private void writeBucketAttributes(XMLStreamWriter writer, Map<String, String> row)
            throws XMLStreamException {
        for (String col : BUCKET_KEYS) {
            String val = row.get(col);
            if (val != null && !val.trim().isEmpty()) {
                try {
                    writer.writeAttribute(col,
                            String.valueOf((int) Math.floor(Double.parseDouble(val.trim()))));
                } catch (NumberFormatException e) {
                    writer.writeAttribute(col, val.trim());
                }
            }
        }
    }

    /**
     * Writes attributes for the {@code TAMAllocationDelete} row element.
     */
    private void writeDeleteAttributes(XMLStreamWriter writer, Map<String, String> row)
            throws XMLStreamException {
        putAttr(writer, "functionalGroupName",      row.get("FGName"));
        putAttr(writer, "fgType",                   row.get("FGType"));
        putAttr(writer, "SiteType",                 row.get("SiteType"));
        putAttr(writer, "siteDescription",          row.get("SiteName"));
        putAttr(writer, "deleteThisLevelSupplier",  row.get("DeleteThisLevelSupplier"));
        putAttr(writer, "deleteLowerLevelSupplier", row.get("DeleteLowerLevelSupplier"));
        putAttr(writer, "deleteThisLevelItem",      row.get("DeleteThisLevelItem"));
        putAttr(writer, "deleteLowerLevelItem",     row.get("DeleteLowerLevelItem"));
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /**
     * Writes an XML attribute only when {@code value} is non-null and non-blank.
     */
    private void putAttr(XMLStreamWriter writer, String name, String value) throws XMLStreamException {
        if (name != null && !name.trim().isEmpty() && value != null && !value.trim().isEmpty()) {
            writer.writeAttribute(name, value.trim());
        }
    }

    /**
     * Returns the {@code messageType} XML header attribute value for a given upload type.
     */
    private String resolveMessageType(String uploadType) {
        if (uploadType == null) {
            return "TAMMessage";
        }
        switch (uploadType) {
            case "TAMSupplierCFGUploadUI":
            case "TAMSupplierCFGMRPSiteUploadUI":
                return "TAMSupplierCFG";
            case "TAMItemCFGUploadUI":
            case "TAMItemCFGMRPSiteUploadUI":
                return "TAMItemCFG";
            case "TAMAllocationMassUpdateCFGUploadUI":
            case "TAMAllocationMassUpdateCFGMRPSiteUploadUI":
                return "TAMAllocationMassUpdateCFG";
            case "TAMAllocationDeleteUI":
                return "TAMAllocationDelete";
            default:
                return "TAMMessage";
        }
    }
}
