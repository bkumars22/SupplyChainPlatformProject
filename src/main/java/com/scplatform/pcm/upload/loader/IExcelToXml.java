/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.scplatform.pcm.user.entity.Users;

public interface IExcelToXml {

    /**
     * Converts an Excel upload file into an XML file suitable for the message loader.
     *
     * @param loadProps  load properties (InterconnectConstants keys and values)
     * @param uploadType the upload type alias (e.g. {@code "CostRecordUI"})
     * @param inFile     source Excel file
     * @param outFile    destination XML file
     * @param error      set to accumulate error strings
     * @param user       the authenticated user performing the upload
     */
    void processReadExcel(Map<String, String> loadProps, String uploadType,
                          File inFile, File outFile, Set<String> error, Users user) throws Exception;

    /**
     * Writes the XML header to the given {@link XMLStreamWriter}.
     */
    void writeXmlHeader(XMLStreamWriter xw) throws XMLStreamException;

    /**
     * Writes the complete XML output file.
     *
     * @param outFile     output XML file
     * @param xsdFileName optional XSD file name (may be {@code null})
     * @param error       set to accumulate error strings
     */
    void writeXml(File outFile, String xsdFileName, Set<String> error)
            throws IOException, MessageLoaderException;

    /**
     * Validates an XML file against the specified XSD schema.
     *
     * @param inXmlFile   XML file to validate
     * @param xsdFileName XSD schema file name
     * @param errors      set to accumulate validation errors
     */
    void validateXmlAgainstXsd(File inXmlFile, String xsdFileName, Set<String> errors)
            throws MessageLoaderException;
}
