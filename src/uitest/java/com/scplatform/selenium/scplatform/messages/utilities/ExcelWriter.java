/*
 * ExcelWriter.java
 * Created on Mar 25, 2019
 *
 * Copyright (c) 2019 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.messages.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.ooxml.POIXMLProperties.ExtendedProperties;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import com.test.selenium.api.ssp.messages.utilities.ExcelWriter.SENDER_TYPE;
import com.test.selenium.common.JLog;
import com.test.selenium.common.excel.ExcelUtils;
import com.test.selenium.scplatform.constants.Constants;



/**
 * The goal here is to create a general Excel Writer that can take any class that implements DataStructure.
 * However, how to do that is not really known.
 *
 * @author 	dgenrich
 */
public class ExcelWriter  {

  protected Workbook workbook;
  protected Sheet excelSheet;
  protected CellStyle textCell;
  protected CellStyle wrapText;
  protected CellStyle integerCell;
  protected CellStyle floatCell;
  protected CellStyle dateCell;
  protected String dateFormatString;
  protected File saveModifiedFileAs;
  protected static boolean createXLSX = false;

  protected int headerOffset = 2;
  protected int totalColumns;


  public void setHeaderOffset (int offset){
    headerOffset = offset;
  }

  private void initializeDateFormatString()	{
    if (dateFormatString == null)
      dateFormatString = Constants.DateFormatExcelUpload;
  }
  /**
   *
   * @param createAsXLSX Set static class variable to determine if excel file created is
   * 						in XLS or XLSX format.  Default=FALSE (create as XLS)
   * @see		#getFileExtension()
   * @see		#setOddDaysToUseXLSX()
   */
  public static void setCreateXLSX (boolean createAsXLSX){
    createXLSX = createAsXLSX;
  }

  /**
   *
   * @return The current value of createXLSX
   */
  public static boolean getCreateXLSX()	{
    return createXLSX;
  }

  /**
   *
   * @return	The file extension to use, either .xlsx or .xls, based on static
   * 			value of createXLSX.
   * @see		#setCreateXLSX(boolean)
   */
  public static String getFileExtension()	{
    return ((createXLSX) ? ".xlsx" : ".xls");
  }


  /**
   * @param format	Sets the date format for Excel date fields,
   * default is {@link Constants#DateFormatExcelUpload}
   */
  public void setDateFormat (String format){
    dateFormatString = format;
  }

  protected String startOfHeader = "";
  protected boolean headerIndex = true;

  /**
   * @param headerStart  The starting character of the header (such as "#").
   * 						Default is blank.
   */
  public void setIncludeHeader (String headerStart){
    this.startOfHeader = headerStart;
  }

  /**
   * @param index  Default=true; If set true, then "#0 #1 #2" is set for the first line of the file
   */
  public void setHeaderIndex (boolean index){
    this.headerIndex = index;
  }

  protected void resetHeaderIndex()	{
    this.headerIndex = false;
  }


  /**
   * Creates the Workbook and the initial sheet
   *
   * @param excelFile		The full file, use {@link #buildExcelFileName(String, String, SENDER_TYPE, String, String, String)}
   * @param sheetName		The sheet name, often the same as the documentType (Example: DiscreteOrder)
   */
  public void createWorksheet (String excelFile, String sheetName)	{

    saveModifiedFileAs = new File(excelFile);

    workbook = (createXLSX) ? new XSSFWorkbook() : new HSSFWorkbook();

    if (createXLSX) {
      POIXMLProperties props = ((POIXMLDocument) workbook).getProperties();
      if (props == null) {
        JLog.fail(this.getClass() + ".createWorksheet(): Properties value is NULL!");
      }
      ExtendedProperties properties = props.getExtendedProperties();
      org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties ctProps =
          properties.getUnderlyingProperties();
      ctProps.setApplication("Microsoft Excel");
    }
    excelSheet = workbook.createSheet(sheetName);


    setFormatters();

  }

  /**
   * Modifies a existing Excel Workbook.  The original is copied to a new Excel file.  Original is not modified.
   *
   * @param excelFile		The original excel file.
   * @param workingFile	The working excel file to modify
   * @param sheetName		The sheet name within the Excel to work with.
   */
  public void modifyExisting (File excelFile, File workingFile, String sheetName){
    try {

      saveModifiedFileAs = workingFile;

      // get the template file
      // workbook = WorkbookFactory.create(excelFile);
      if (FilenameUtils.isExtension(workingFile.toString(), "xlsx")) {
        workbook = new XSSFWorkbook(new FileInputStream(excelFile));
      } else if (FilenameUtils.isExtension(workingFile.toString(), "xlsm")) {
        workbook = new XSSFWorkbook(OPCPackage.open(new FileInputStream(excelFile)));
      } else {
        workbook = new HSSFWorkbook(new FileInputStream(excelFile));
      }


      // get the sheet to work with
      if (sheetName == null) {
        excelSheet = workbook.getSheetAt(0);
      } else {
        excelSheet = workbook.getSheet(sheetName);
      }

      setFormatters();

    } catch (IOException | InvalidFormatException e) {
      JLog.fail(e);
    }

  }

  /**
   * Merges two excel files into a single excel file. Will handle XLS or XLSX files.
   *
   * @param baseExcelFile The base excel file - this is the one that will be modified.
   *
   * @param addExcelFile The Excel file to add. The first sheet in this workbook will be copied to
   *        the baseExcelFile
   *
   * @param newSheetIndex The index for the addExcelFile sheet. This is 0-based.
   *
   * @throws InvalidFormatException if format of excel files is incorrect
   * @throws IOException if I/O errors occur
   */
  public void mergeWorkbooks (File baseExcelFile, File addExcelFile, int newSheetIndex)
      throws InvalidFormatException, IOException {
    FileInputStream baseFile = new FileInputStream(baseExcelFile);
    workbook = WorkbookFactory.create(baseFile);
    createXLSX = FilenameUtils.getExtension(baseExcelFile.toString()).equals("xlsx");
    if (createXLSX) {
      POIXMLProperties props = ((POIXMLDocument) workbook).getProperties();
      if (props == null) {
        JLog.fail(this.getClass() + ".createWorksheet(): Properties value is NULL!");
      }
      ExtendedProperties properties = props.getExtendedProperties();
      org.openxmlformats.schemas.officeDocument.x2006.extendedProperties.CTProperties ctProps =
          properties.getUnderlyingProperties();
      ctProps.setApplication("Microsoft Excel");
    }

    FileInputStream addExcel = new FileInputStream(addExcelFile);
    XSSFWorkbook workbookExpected = new XSSFWorkbook(addExcel);
    XSSFSheet sheetToAdd = workbookExpected.getSheetAt(0);
    String sheetName = sheetToAdd.getSheetName();

    addSheet(sheetName, newSheetIndex);

    ExcelUtils.copySheets((XSSFSheet) workbook.getSheet(sheetName), sheetToAdd);
    addExcel.close();
    addExcel = null;

    FileOutputStream fileOut = new FileOutputStream(baseExcelFile);
    workbook.write(fileOut);
  }

  public void setFormatters ()	{
    initializeDateFormatString();

    // Create a text formatter
    Font font = workbook.createFont();
    font.setFontName("Arial");
    textCell = workbook.createCellStyle();
    textCell.setFont(font);

    wrapText = workbook.createCellStyle();
    wrapText.setFont(font);
    wrapText.setWrapText(true);

    // Create an Integer formatter
    integerCell = workbook.createCellStyle();
    integerCell.setFont(font);

    // Create a float formatter

    floatCell = workbook.createCellStyle();
    floatCell.setFont(font);

    // Create a date formatter
    DataFormat df = workbook.createDataFormat();
    dateCell = workbook.createCellStyle();
    dateCell.setFont(font);
    dateCell.setDataFormat(df.getFormat(dateFormatString));
  }

  public void autoSize (int numberOfColumns){
    for (int col = 0; col < numberOfColumns; col++)	{
      excelSheet.autoSizeColumn(col);
    }
  }


  public void addSheet (String sheetName, int index){
    excelSheet = workbook.createSheet(sheetName);
    workbook.setSheetOrder(sheetName, index);
  }


  /**
   * Create the header rows in excel, marking required fields in blue and bold.
   *
   * @param headers	ArrayList of the headers, this is typically the getExcelHeaders()
   * 					method of the data structure.
   * @param total		The total headers to display.  Typically will be the size of headers,
   * 					but not always (different versions can have different headers totals)
   */
  public void createWorksheetHeader (ArrayList<String> headers, int total)	{
    String firstFieldComment;
    totalColumns = total;
    String firstColumn = startOfHeader;

    try {
      // Create a Bold font, color=black
      Font font = workbook.createFont();
      font.setColor(IndexedColors.BLACK.index);
      font.setFontName("Arial");
      font.setBold(true);

      CellStyle headerFormat = workbook.createCellStyle();
      headerFormat.setFont(font);



      // Create a Bold font, color=blue
      Font blueFont = workbook.createFont();
      blueFont.setColor(IndexedColors.BLUE.index);
      blueFont.setBold(true);
      CellStyle requiredFormat = workbook.createCellStyle();
      requiredFormat.setFont(blueFont);


      CellStyle useFormat;


      for (int col = 0; col < total; col++)	{

        firstFieldComment = (col == 0) ? "#" : "";
        useFormat = (headers.get(col).contains("*")) ? requiredFormat : headerFormat;

        // first row, index
        Cell cell1 = getCell(0, col);
        cell1.setCellStyle(headerFormat);
        cell1.setCellValue(firstFieldComment+col);

        // second row, actual header
        firstFieldComment = (col == 0) ? firstColumn : "";
        Cell cell = getCell(1, col);
        cell.setCellStyle(useFormat);
        cell.setCellValue(firstFieldComment+headers.get(col));
      }

      if (headerIndex) {
        headerOffset = 2;
      } else {
        headerOffset = 1;
        Row row = excelSheet.getRow(0);	// remove index
        excelSheet.removeRow(row);
      }
    } catch (Exception e) {
      JLog.error(e);
    }

    resetHeaderIndex();
  }

  public Row getRow(int rowNumber)	{
    Row excelRow = excelSheet.getRow(rowNumber);
    if (excelRow == null) {
      excelRow = excelSheet.createRow(rowNumber);
    }

    return excelRow;
  }

  public Cell getCell(int rowNumber, int cellNumber)	{
    Row excelRow = getRow(rowNumber);
    Cell excelCell = excelRow.getCell(cellNumber);
    if (excelCell == null) {
      excelCell = excelRow.createCell(cellNumber);
    }

    return excelCell;
  }

  /**
   * Writes a string to the excel cell.
   *
   * @param value		The value to write
   * @param col		The 0 based column to write to
   * @param row		The 0 based row to write to.  The header offset is automatically
   * 					added to this value, so row=0 will be the first row under the header.
   */
  public void writeString (String value, int col, int row)	{
    Cell cell = getCell(row+headerOffset, col);
    cell.setCellStyle(textCell);
    cell.setCellValue(value);
  }

  /**
   * Writes a string to the excel cell and allows the cell to wrap
   *
   * @param value		The value to write
   * @param col		The 0 based column to write to
   * @param row		The 0 based row to write to.  The header offset is automatically
   * 					added to this value, so row=0 will be the first row under the header.
   */
  public void writeStringWithWrap (String value, int col, int row)	{
    Cell cell = getCell(row+headerOffset, col);
    cell.setCellStyle(wrapText);
    cell.setCellValue(value);
  }

  /**
   * Writes and formats a date value to the excel cell.
   *
   * @param date	String value containing the date
   * @param col		The 0 based column to write to
   * @param row		The 0 based row to write to.  The header offset is automatically
   * 					added to this value, so row=0 will be the first row under the header.
   */
  public void writeDate (String date, int col, int row)	{
    initializeDateFormatString();
    Date dateValue = null;

    try {
      DateFormat df = new SimpleDateFormat(dateFormatString);
      dateValue = df.parse(date);
    } catch (ParseException e) {
      JLog.error(e);
    }

    Cell cell = getCell(row+headerOffset, col);
    cell.setCellStyle(dateCell);
    cell.setCellValue(dateValue);

  }

  /**
   * Writes and formats a date value to the excel cell.
   *
   * @param date	DATE value containing the date
   * @param col		The 0 based column to write to
   * @param row		The 0 based row to write to.  The header offset is automatically
   * 					added to this value, so row=0 will be the first row under the header.
   */
  public void writeDate (DateTime date, int col, int row)	{

    Cell cell = getCell(row+headerOffset, col);
    cell.setCellStyle(dateCell);
    cell.setCellValue(date.toDate());
  }



  /**
   * Writes and formats an integer value to the excel cell
   *
   * @param value	Integer value to write
   * @param col		The 0 based column to write to
   * @param row		The 0 based row to write to.  The header offset is automatically
   * 					added to this value, so row=0 will be the first row under the header.
   */
  public void writeInterger (int value, int col, int row)	{

    Cell cell = getCell(row+headerOffset, col);
    cell.setCellStyle(integerCell);
    cell.setCellValue(value);

  }

  /** Overwrites the value specified by the col / row with the value specified as Object.
   *  Value can be: Integer, Float, Date or String
   *
   * @param col
   * @param row
   * @param value
   */
  public void overwriteValue(Object value, int col, int row) {
    if (value instanceof Integer) {
      writeInterger((Integer)value, col, row);
    } else if (value instanceof Float) {
      writeFloat((Float)value, col, row);
    } else if (value instanceof Date) {
      writeDate((DateTime)value, col, row);
    } else {
      writeString((String)value, col, row);
    }
  }

  /**
   * Writes and formats an float value to the excel cell
   *
   * @param value		Float value to write
   * @param col		The 0 based column to write to
   * @param row		The 0 based row to write to.  The header offset is automatically
   * 					added to this value, so row=0 will be the first row under the header.
   */
  public void writeFloat (float value, int col, int row)	{
    Cell cell = getCell(row+headerOffset, col);
    cell.setCellStyle(floatCell);
    cell.setCellValue(Float.toString(value));
  }


  /**
   * Resize the columns and close the excel workbook
   */
  public void closeExcel ()	{

    // autosize the columns
    for (int col = 0; col < totalColumns; col++)	{
      excelSheet.autoSizeColumn(col);
    }

    // write and close the workbook
    try {
      FileOutputStream out = new FileOutputStream(saveModifiedFileAs);
      workbook.write(out);
      out.close();
    } catch (IOException e) {
      JLog.error(e);
    }
  }


  public int getRowCount ()	{
    return excelSheet.getPhysicalNumberOfRows();
  }

}
