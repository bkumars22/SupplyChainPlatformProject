/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.parentFunctionalGroup.preprocessor;

import com.scplatform.pcm.upload.loader.ExcelToXMLProcessor;
import com.scplatform.pcm.upload.loader.MessageLoaderException;
import com.scplatform.pcm.user.entity.Users;
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
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ParentFunctionalGroupConfigPreprocessor extends ExcelToXMLProcessor {

    private List<Map<String, String>> excelRows;

    @Override
    public void processReadExcel(Map<String, String> loadProps, String uploadType,
                                 File inFile, File outFile, Set<String> errors, Users user) throws Exception {
        long startTime = System.currentTimeMillis();
        excelRows = readExcel(inFile, startDataRow);
        log.info("Excel read took: {}s", (System.currentTimeMillis() - startTime) / 1000);

        if (!errors.isEmpty()) {
            throw new Exception(org.apache.commons.lang3.StringUtils.join(errors, "\n"));
        }

        startTime = System.currentTimeMillis();
        writeXml(outFile, "ParentFunctionalGroupConfigMessage.xsd", errors);
        log.info("XML write took: {}s", (System.currentTimeMillis() - startTime) / 1000);
    }

    @Override
    public void writeXmlHeader(XMLStreamWriter xw) throws XMLStreamException {
        xw.writeStartDocument("UTF-8", "1.0");
        xw.writeCharacters("\n");
        xw.writeStartElement("scplatform", "ParentFunctionalGroupConfigMessage", E2OPEN_NS);
        xw.writeNamespace("scplatform", E2OPEN_NS);
        xw.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xw.writeAttribute("headerVersion", "1.0");
        xw.writeAttribute("fromID", "");
        xw.writeAttribute("toID", "");
        xw.writeAttribute("messageType", "ParentFunctionalGroupConfig");
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

            writeXmlHeader(writer);

            for (Map<String, String> row : excelRows) {
                writer.writeStartElement("scplatform", "ParentFunctionalGroupConfig", E2OPEN_NS);

                putAttr(writer, "ParentName", row.get("ParentName"));

                // Excel label "Action" normalizes to key "Action"
                String opCode = row.get("Action");
                if (StringUtils.isBlank(opCode)) {
                    // fallback in case machine column name is used
                    opCode = row.get("OpCode");
                }
                putAttr(writer, "OpCode", opCode);

                // Excel label "New Parent Name" normalizes to key "NewParentName"
                String newName = row.get("NewParentName");
                if (StringUtils.isBlank(newName)) {
                    newName = row.get("Name");
                }
                putAttr(writer, "Name", newName);

                writer.writeEndElement(); // ParentFunctionalGroupConfig
                writer.writeCharacters("\n");
            }

            writer.writeEndElement(); // ParentFunctionalGroupConfigMessage
            writer.writeEndDocument();
            writer.flush();
            writer.close();

            validateXmlAgainstXsd(outFile, xsdFileName, error);
        } catch (Exception e) {
            log.error("Error writing XML: {}", e.getMessage(), e);
            throw new MessageLoaderException("Error while generating XML: " + e.getMessage(), e);
        }
    }

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
            Deque<String> contextStack = new ArrayDeque<>();

            DefaultHandler handler = new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    String parentName = attributes.getValue("ParentName");
                    if (parentName != null) {
                        contextStack.push("ParentName=" + parentName);
                    }
                }
                @Override
                public void endElement(String uri, String localName, String qName) {
                    if (!contextStack.isEmpty()) contextStack.pop();
                }
            };
            reader.setContentHandler(handler);
            reader.setErrorHandler(new DefaultHandler() {
                private String withContext(SAXParseException e, String level) {
                    if (contextStack.isEmpty()) return null;
                    String ctx = contextStack.peek();
                    String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Unknown error";
                    if (msg.contains(":")) msg = msg.substring(msg.indexOf(':') + 1).trim();
                    List<String> arg = Arrays.asList(level + ": " + msg, ctx);
                    return SCPlatformMessages.INSTANCE.getMessage("pcm.mdm.pfgconfig.validation.message", arg.toArray(), null);
                }
                @Override public void error(SAXParseException e)      { String m = withContext(e, "VALIDATION ERROR"); if (m != null) errors.add(m); }
                @Override public void fatalError(SAXParseException e)  { String m = withContext(e, "FATAL");            if (m != null) errors.add(m); }
                @Override public void warning(SAXParseException e)     { String m = withContext(e, "WARNING");          if (m != null) errors.add(m); }
            });
            reader.parse(new InputSource(new FileInputStream(inXmlFile)));
        } catch (Exception e) {
            errors.add("Validation process error: " + e.getMessage());
            log.error("XSD validation error", e);
            throw new MessageLoaderException("XSD validation error: " + e.getMessage(), e);
        }
    }

    private void putAttr(XMLStreamWriter writer, String name, String value) throws Exception {
        if (name != null && value != null && !value.isEmpty()) {
            writer.writeAttribute(name, value);
        }
    }
}
