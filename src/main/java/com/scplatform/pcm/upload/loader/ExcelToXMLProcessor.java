/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.Validate;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ResourceUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;

@Log4j2
public abstract class ExcelToXMLProcessor implements IExcelToXml {

    protected static final String E2OPEN_NS = "http://www.scplatform.local/E2openMCM";

    protected long startDataRow = 1;
    protected List<DateTimeFormatter> datePatterns = new ArrayList<>();
    @Autowired
    protected PcmConfigUtil pcmConfigUtil;

    public void setConfig(PcmConfigUtil config) {
        this.pcmConfigUtil = config;
    }


    protected String[] getDatePatternConfig() {
        if (pcmConfigUtil != null) {
            List<String> list = pcmConfigUtil.getList("pcm.bom.all.possible.dateFormat");
            if (list != null && !list.isEmpty()) {
                return list.toArray(new String[0]);
            }
        }
        return new String[]{"M/d/yy", "M/d/yyyy", "MM/dd/yyyy"};
    }

    protected String[] getXsdFilePathConfig() {
        List<String> paths = new ArrayList<>();

        // 1. DB-configured paths (may include customer-specific + product paths)
        if (pcmConfigUtil != null) {
            List<String> dbPaths = pcmConfigUtil.getList("pcm.excel.upload.xsd.validation.config.paths");
            if (dbPaths != null) {
                paths.addAll(dbPaths);
            }
        }

        // 2. Classpath fallback: customer-specific first, then product default
        String customer = (pcmConfigUtil != null) ? pcmConfigUtil.getString("pcm.customer", null) : null;
        if (customer != null && !customer.isBlank()) {
            paths.add("classpath:config/excel/upload/xsd/" + customer);
        }
        paths.add("classpath:config/excel/upload/xsd");

        log.debug("XSD search paths: {}", paths);
        return paths.toArray(new String[0]);
    }

    /** Populates {@link #datePatterns} from configuration. */
    protected void getAllConfigDateValues() {
        String[] patterns = getDatePatternConfig();
        List<DateTimeFormatter> list = new ArrayList<>();
        for (String p : patterns) {
            try {
                list.add(DateTimeFormatter.ofPattern(p.trim(), Locale.ENGLISH));
            } catch (Exception e) {
                log.error("Invalid date pattern in config: '{}'", p, e);
            }
        }
        datePatterns = Collections.unmodifiableList(list);
    }

    /**
     * Reads the first sheet of an XLSX workbook and returns rows as header-keyed maps.
     *
     * @param inFile       input XLSX file
     * @param dataRowHeader 0-based index of the header row (data rows follow)
     * @return list of row maps ({@code headerName -> cellValue})
     */
    protected List<Map<String, String>> readExcel(File inFile, long dataRowHeader) throws Exception {
        try (OPCPackage pkg = OPCPackage.open(inFile)) {
            ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(pkg);
            XSSFReader xssfReader = new XSSFReader(pkg);
            StylesTable stylesTable = xssfReader.getStylesTable();
            getAllConfigDateValues();
            XSSFReader.SheetIterator it = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
            if (!it.hasNext()) {
                return Collections.emptyList();
            }
            try (InputStream sheetStream = it.next()) {
                return processExcel(stylesTable, sharedStrings, sheetStream, dataRowHeader);
            }
        }
    }

    protected List<Map<String, String>> processExcel(StylesTable stylesTable,
            ReadOnlySharedStringsTable readOnlySharedStringsTable,
            InputStream stream, long headerRow)
            throws ParserConfigurationException, SAXException, IOException {

        final List<String> headers = new ArrayList<>();
        final List<String> currentRow = new ArrayList<>();
        final List<Map<String, String>> allRows = new ArrayList<>();
        final long[] rowNum = {0};

        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XMLReader sheetParser = factory.newSAXParser().getXMLReader();

        DataFormatter dataFormatter = new DataFormatter();

        XSSFSheetXMLHandler.SheetContentsHandler handler = new XSSFSheetXMLHandler.SheetContentsHandler() {
            @Override
            public void startRow(int row) {
                currentRow.clear();
                rowNum[0] = row;
            }

            @Override
            public void endRow(int row) {
                if (row == headerRow) {
                    headers.clear();
                    headers.addAll(currentRow);
                } else if (row > headerRow) {
                    Map<String, String> rowMap = new LinkedHashMap<>();
                    for (int i = 0; i < headers.size(); i++) {
                        String header = headers.get(i);
                        if (header == null || header.trim().isEmpty()) continue;
                        String normalizedHeader = header.replaceAll("\\s+", "");
                        String value = (i < currentRow.size()) ? currentRow.get(i) : "";
                        if ("EffectiveFromDate".equalsIgnoreCase(normalizedHeader) ||
                                "EffectiveToDate".equalsIgnoreCase(normalizedHeader)) {
                            if (value != null && !value.trim().isEmpty()) {
                                value = formatToISO(value);
                            }
                        }
                        rowMap.put(normalizedHeader, value);
                    }
                    if (!rowMap.isEmpty()) {
                        allRows.add(rowMap);
                    }
                }
            }

            @Override
            public void cell(String cellReference, String formattedValue, XSSFComment xssfComment) {
                if (cellReference == null) {
                    currentRow.add("");
                    return;
                }
                int colIndex = new CellReference(cellReference).getCol();
                while (currentRow.size() < colIndex) {
                    currentRow.add("");
                }
                currentRow.add(formattedValue != null ? formattedValue : "");
            }
        };

        ContentHandler contentHandler = new XSSFSheetXMLHandler(
                stylesTable, null, readOnlySharedStringsTable,
                handler, dataFormatter, false);
        sheetParser.setContentHandler(contentHandler);
        sheetParser.parse(new InputSource(stream));
        return allRows;
    }

    /**
     * Attempts to parse {@code value} with any configured date pattern and returns
     * an ISO-8601 instant string, or the original value if no pattern matches.
     */
    protected String formatToISO(String value) {
        if (value == null || value.trim().isEmpty()) return "";
        for (DateTimeFormatter fmt : datePatterns) {
            try {
                LocalDate parsed = LocalDate.parse(value.trim(), fmt);
                return parsed.atStartOfDay(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ISO_INSTANT);
            } catch (Exception e) {
                log.debug("Date '{}' not matched by pattern '{}': {}", value, fmt, e.getMessage());
            }
        }
        return value;
    }

    /**
     * Locates a template Excel file by scanning the configured XSD/template paths.
     *
     * @param xsdFileName file name to search for
     * @return the found File
     * @throws IllegalArgumentException if the file cannot be found
     */
    public File getTemplateExcelFile(String xsdFileName) {
        String[] xsdFilePaths = getXsdFilePathConfig();
        Validate.notNull(xsdFilePaths, "No template paths configured (pcm.excel.upload.xsd.validation.config.paths)");

        for (String templatePath : xsdFilePaths) {
            try {
                File template = ResourceUtils.getFile(templatePath + "/" + xsdFileName);
                if (template.isFile() && template.canRead()) {
                    log.debug("Found XSD '{}' at: {}", xsdFileName, template.getAbsolutePath());
                    return template;
                }
            } catch (Exception e) {
                log.debug("XSD '{}' not found at '{}': {}", xsdFileName, templatePath, e.getMessage());
            }
        }

        throw new IllegalArgumentException(
                "Could not find excel template '" + xsdFileName + "' in paths: " + Arrays.asList(xsdFilePaths));
    }

    // -----------------------------------------------------------------------
    // IExcelToXml default (no-op) implementations — override in subclasses
    // -----------------------------------------------------------------------

    @Override
    public void processReadExcel(Map<String, String> loadProps, String uploadType,
                                 File inFile, File outFile, Set<String> error, Users user) throws Exception {
    }

    @Override
    public void writeXmlHeader(XMLStreamWriter xw) throws XMLStreamException {
    }

    @Override
    public void writeXml(File outFile, String xsdFileName, Set<String> error)
            throws IOException, MessageLoaderException {
    }

    @Override
    public void validateXmlAgainstXsd(File inXmlFile, String xsdFileName, Set<String> errors)
            throws MessageLoaderException {
    }
}
