/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.functionalGroup.preprocessor;

import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.upload.loader.ExcelToXMLProcessor;
import com.scplatform.pcm.upload.loader.MessageLoaderException;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
    * FunctionalGroupPreprocessor processes a Functional Group Excel file and converts it into an XML format.
    * It reads the Excel file and writes the output to an XML file.
 */
@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FunctionalGroupPreprocessor extends ExcelToXMLProcessor {

    // Upload-type constants (replaces PcmConstantsUtil.UploadFileAction.*)
    public static final String UPLOAD_TYPE_FG_CFG_ADDITEM    = "FGItemAdd";
    public static final String UPLOAD_TYPE_FG_CFG_DELETEITEM = "FGItemDelete";
    public static final String UPLOAD_TYPE_FG_CFG_UPDATE     = "FGUpdateItem";
    public static final String UPLOAD_TYPE_FG_CFG_RENAME     = "FGRenameItem";
    public static final String UPLOAD_TYPE_FG_CFG_ACTIVATE   = "FGCFGActivate";
    public static final String UPLOAD_TYPE_FG_CFG_INACTIVE   = "FGCFGInactivate";

    List<Map<String, String>> excelRows;

    /**
     * Processes the FunctionalGroup Excel file and writes the output to an XML file.
     * @param inFile  the input Excel file
     * @param outFile the output XML file
     * @throws Exception if an error occurs during processing
     */
    @Override
    public void processReadExcel(Map<String, String> loadProps, String uploadType, File inFile, File outFile, Set<String> errors, Users user) throws Exception {
        long startTime = System.currentTimeMillis();
        startDataRow = 0;
        // FG Excel layout:
        //   Row 0 — column key names (FunctionalGroupName, Action, ...)  ← header row used by readExcel
        //   Row 1 — template/example row (placeholder text)              ← skip
        //   Row 2+ — actual data
        // readExcel(inFile, 0) returns rows 1..N; discard the first one (template).
        List<Map<String, String>> allRows = readExcel(inFile, startDataRow);
        final int FG_SKIP_ROWS = 1;
        excelRows = allRows.size() > FG_SKIP_ROWS
                ? new ArrayList<>(allRows.subList(FG_SKIP_ROWS, allRows.size()))
                : new ArrayList<>();
        log.info("Excel took : " + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
        // Fetch access flags
        String hasAddItemAccess    = loadProps.get(UPLOAD_TYPE_FG_CFG_ADDITEM);
        String hasDeleteItemAccess = loadProps.get(UPLOAD_TYPE_FG_CFG_DELETEITEM);
        String hasUpdateAccess     = loadProps.get(UPLOAD_TYPE_FG_CFG_UPDATE);
        String hasRenameAccess     = loadProps.get(UPLOAD_TYPE_FG_CFG_RENAME);
        String hasActiveAceess     = loadProps.get(UPLOAD_TYPE_FG_CFG_ACTIVATE);
        String hasInactiveAccess   = loadProps.get(UPLOAD_TYPE_FG_CFG_INACTIVE);
        // Role-based restriction check (replaces ConfigurationUtils.getBoolean)
        if (pcmConfigUtil != null && pcmConfigUtil.getBoolean("scplatform.feature.enable.cfg.rolebased.restriction", false)) {
            for (Map<String, String> row : excelRows) {
                String functionalGroupName = row.get("FunctionalGroupName");
                String action = row.get("Action");
                if (StringUtils.isBlank(action)) {
                    errors.add("Missing Action for FG: " + functionalGroupName);
                    continue;
                }
                String type = row.get("Type");
                if (type != null && FunctionalGroup.CFG.equalsIgnoreCase(type)) {
                    String error = null;
                    String normalizedAction = action.trim().toUpperCase();
                    switch (normalizedAction) {
                        case "ADD":
                            if (!"true".equalsIgnoreCase(hasAddItemAccess)) {
                                error = getMessage("errors.cfg_role_restricted", new Object[]{normalizedAction, functionalGroupName});
                            }
                            break;
                        case "DELETE":
                            if (!"true".equalsIgnoreCase(hasDeleteItemAccess)) {
                                error = getMessage("errors.cfg_role_restricted", new Object[]{normalizedAction, functionalGroupName});
                            }
                            break;
                        case "UPDATE":
                            if (!"true".equalsIgnoreCase(hasUpdateAccess)) {
                                error = getMessage("errors.cfg_role_restricted", new Object[]{normalizedAction, functionalGroupName});
                            } else {
                                if (row.get("Status") != null) {
                                    String status = row.get("Status").trim().toUpperCase();
                                    if ("ACTIVE".equalsIgnoreCase(status)) {
                                        if (!"true".equalsIgnoreCase(hasActiveAceess)) {
                                            error = getMessage("errors.cfg_role_restricted", new Object[]{status, functionalGroupName});
                                        }
                                    } else if ("INACTIVE".equalsIgnoreCase(status)) {
                                        if (!"true".equalsIgnoreCase(hasInactiveAccess)) {
                                            error = getMessage("errors.cfg_role_restricted", new Object[]{status, functionalGroupName});
                                        }
                                    }
                                }
                            }
                            break;
                        case "RENAME":
                            if (!"true".equalsIgnoreCase(hasRenameAccess)) {
                                error = getMessage("errors.cfg_role_restricted", new Object[]{normalizedAction, functionalGroupName});
                            }
                            break;
                        default:
                            error = "Unknown Action '" + normalizedAction + "' for FG: " + functionalGroupName;
                            break;
                    }
                    if (StringUtils.isNotBlank(error)) {
                        errors.add(error);
                    }
                }
            }
        }
        // Stop processing if any role-based restriction errors exist
        if (!errors.isEmpty()) {
            throw new Exception(StringUtils.join(errors, "\n"));
        }
        // Continue with conversion and writing output
        startTime = System.currentTimeMillis();
        log.info("functional group conversion took : " + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
        // Conversion process (assuming it happens in writeXml)
        startTime = System.currentTimeMillis();
        writeXml(outFile, "FunctionalGroup.xsd", errors);
        log.info("Writing XML took : " + ((System.currentTimeMillis() - startTime) / 1000) + " sec");
    }

    /**
     * Writes the XML header to the given XMLStreamWriter.
     * @param xw XMLStreamWriter instance
     * @throws XMLStreamException if writing fails
     */
    @Override
    public void writeXmlHeader(XMLStreamWriter xw) throws XMLStreamException {
        xw.writeStartDocument("UTF-8", "1.0");
        xw.writeCharacters("\n");
        xw.writeStartElement("scplatform", "FunctionalGroupMessage", E2OPEN_NS);
        xw.writeNamespace("scplatform", E2OPEN_NS);
        xw.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xw.writeAttribute("headerVersion", "1.0");
        xw.writeAttribute("fromID", "");
        xw.writeAttribute("toID", "");
        xw.writeAttribute("messageType", "FunctionalGroup");
        xw.writeAttribute("messageVersion", "MCM1.0");
        xw.writeAttribute("messageCount", "1");
        xw.writeAttribute("messageIndex", "0");
        xw.writeCharacters("\n");
    }

    @Override
    public void writeXml(File outFile, String xsdFileName, Set<String> error) throws IOException, MessageLoaderException {
        XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile))) {
            XMLStreamWriter writer = xmlOutputFactory.createXMLStreamWriter(bos, "UTF-8");

            // Start header Document
            writeXmlHeader(writer);
            // Loop over excel rows (each record becomes a FunctionalGroup)
            for (Map<String, String> row : excelRows) {
                writer.writeStartElement("scplatform", "FunctionalGroup", "http://www.scplatform.local/E2openMCM");
                putAttr(writer, "functionalGroupName", row.get("FunctionalGroupName"));
                if (row.get("AliasName") != null) {
                    // replaces ConfigurationUtils.getString(...)
                    String aliasAttr = pcmConfigUtil != null
                            ? pcmConfigUtil.getString("pcm.functionalGroup.aliasName.xmlAttr", null) : null;
                    putAttr(writer, aliasAttr, row.get("AliasName"));
                }
                putAttr(writer, "description",            row.get("Description"));
                putAttr(writer, "type",                   row.get("Type"));
                putAttr(writer, "functionalId",           row.get("FunctionalGroupId"));
                putAttr(writer, "status",                 row.get("Status"));
                putAttr(writer, "item",                   row.get("Item"));
                putAttr(writer, "itemBusinessName",       row.get("ItemBusinessName"));
                putAttr(writer, "itemType",               row.get("ItemType"));
                putAttr(writer, "userItemType",           row.get("UserItemType"));
                putAttr(writer, "platform",               row.get("Platform"));
                putAttr(writer, "lob",                    row.get("Lob"));
                putAttr(writer, "parentItemNumber",       row.get("ParentItem"));
                putAttr(writer, "parentItemBusinessName", row.get("ParentItemBusinessName"));
                putAttr(writer, "ODMPartNumber",          row.get("ODMPart"));
                putAttr(writer, "ODMPartBusinessName",    row.get("ODMPartBusinessName"));
                putAttr(writer, "value",                  row.get("Value"));
                putAttr(writer, "opCode",                 row.get("opCode") != null ? row.get("opCode") : row.get("Action"));

                writer.writeEndElement(); // FunctionalGroup
                writer.writeCharacters("\n");
            }

            writer.writeEndElement(); // FunctionalGroupMessage
            writer.writeEndDocument();
            writer.flush();
            writer.close();

            // Optional: XSD validation if required
            validateXmlAgainstXsd(outFile, xsdFileName, error);
        } catch (Exception e) {
            log.error("Error writing XML: " + e.getMessage(), e);
            throw new MessageLoaderException("Error while generating XML: " + e.getMessage(), e);
        }
    }

    /*
    This method validates the given XML file against the specified XSD schema.
    @param inXmlFile The XML file to validate
    @param xsdFileName The XSD schema file name
    @param errors Set to collect validation errors
    @throws MessageLoaderException for validation issues
     */
    @Override
    public void validateXmlAgainstXsd(File inXmlFile, String xsdFileName, Set<String> errors) throws MessageLoaderException {
        try {
            File xsdFile = getTemplateExcelFile(xsdFileName);
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsdFile);
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            spf.setSchema(schema);
            SAXParser parser = spf.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            Deque<String> itemContextStack = new ArrayDeque<>();
            DefaultHandler handler = new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    String functionalGroupName = attributes.getValue("functionalGroupName");
                    if (functionalGroupName != null) {
                        itemContextStack.push("functionalGroupName=" + functionalGroupName);
                    }
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    if (!itemContextStack.isEmpty()) {
                        itemContextStack.pop();
                    }
                }
            };
            reader.setContentHandler(handler);
            reader.setErrorHandler(new DefaultHandler() {
                private String withContext(SAXParseException e, String level) {
                    if (itemContextStack.isEmpty()) {
                        return null;
                    }
                    String ctx = itemContextStack.peek();
                    String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Unknown error";
                    if (msg.contains(":")) {
                        msg = msg.substring(msg.indexOf(':') + 1).trim();
                    }
                    List<String> arg = Arrays.asList(level + ": " + msg, ctx);
                    return SCPlatformMessages.INSTANCE.getMessage(
                            "pcm.mdm.bom.validation.message",
                            arg.toArray(),
                            null
                    );
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

    /*
    Helper method to write an attribute if the value is not null or empty.
    @param writer XMLStreamWriter instance
    @param name Attribute name
    @param value Attribute value
     */
    private void putAttr(XMLStreamWriter writer, String name, String value) throws Exception {
        if (name != null && value != null && !value.isEmpty()) {
            writer.writeAttribute(name, value);
        }
    }

    // replaces Messages.getMessage(...)
    private String getMessage(String key, Object[] args) {
        try {
            if (SCPlatformMessages.INSTANCE != null) {
                return SCPlatformMessages.INSTANCE.getMessage(key, args, null);
            }
        } catch (Exception e) {
            log.debug("Message key '{}' not found: {}", key, e.getMessage());
        }
        return key + (args != null ? " " + Arrays.toString(args) : "");
    }
}
