/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.util;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Utility class to safely convert Excel .xls files to .xlsx format
 * Preserves all data, formulas, and formatting
 */
public class XlsToXlsxConverter {

    public static void main(String[] args) {
        String dataPath = "C:\\ImportantFiles\\selenium-scplatform\\src\\test\\resources\\com\\scplatform\\selenium\\scplatform\\data";

        try {
            convertAllXlsFiles(dataPath);
        } catch (Exception e) {
            System.err.println("Error during conversion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Convert all .xls files in the specified directory to .xlsx format
     */
    public static void convertAllXlsFiles(String directoryPath) throws IOException {
        File directory = new File(directoryPath);

        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Invalid directory: " + directoryPath);
        }

        // Find all .xls files (not .xlsx)
        File[] xlsFiles = directory
                .listFiles((dir, name) -> name.toLowerCase().endsWith(".xls") && !name.toLowerCase().endsWith(".xlsx"));

        if (xlsFiles == null || xlsFiles.length == 0) {
            System.out.println("No .xls files found in: " + directoryPath);
            return;
        }

        System.out.println("Found " + xlsFiles.length + " .xls files to convert");

        int successCount = 0;
        int failCount = 0;
        List<String> failedFiles = new ArrayList<>();

        for (File xlsFile : xlsFiles) {
            try {
                System.out.println("\nProcessing: " + xlsFile.getName());
                convertXlsToXlsx(xlsFile);
                successCount++;
                System.out.println("  ✓ Successfully converted");
            } catch (Exception e) {
                failCount++;
                failedFiles.add(xlsFile.getName());
                System.err.println("  ✗ Failed to convert: " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("Conversion Summary:");
        System.out.println("  Successfully converted: " + successCount);
        System.out.println("  Failed: " + failCount);

        if (!failedFiles.isEmpty()) {
            System.out.println("\nFailed files:");
            failedFiles.forEach(f -> System.out.println("  - " + f));
        }
    }

    /**
     * Convert a single .xls file to .xlsx format
     */
    public static void convertXlsToXlsx(File xlsFile) throws IOException {
        String xlsPath = xlsFile.getAbsolutePath();
        String xlsxPath = xlsPath.replaceAll("\\.xls$", ".xlsx");

        // Create backup of original file
        String backupPath = xlsPath + ".backup";
        Files.copy(Paths.get(xlsPath), Paths.get(backupPath), StandardCopyOption.REPLACE_EXISTING);

        System.out.println("  - Created backup: " + new File(backupPath).getName());

        try (FileInputStream fis = new FileInputStream(xlsFile);
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fis)) {

            // Create new XSSF workbook
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook();

            // Copy all sheets
            int numberOfSheets = hssfWorkbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheets; i++) {
                HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(i);
                XSSFSheet xssfSheet = xssfWorkbook.createSheet(hssfSheet.getSheetName());

                copySheet(hssfSheet, xssfSheet, hssfWorkbook, xssfWorkbook);
            }

            // Write to new .xlsx file
            try (FileOutputStream fos = new FileOutputStream(xlsxPath)) {
                xssfWorkbook.write(fos);
            }

            xssfWorkbook.close();

            System.out.println("  - Created .xlsx file: " + new File(xlsxPath).getName());

            // Delete original .xls file after successful conversion
            if (new File(xlsxPath).exists()) {
                xlsFile.delete();
                System.out.println("  - Deleted original .xls file");
            }

        } catch (Exception e) {
            // If conversion fails, restore from backup
            if (new File(backupPath).exists()) {
                Files.copy(Paths.get(backupPath), Paths.get(xlsPath), StandardCopyOption.REPLACE_EXISTING);
                System.err.println("  - Restored from backup due to error");
            }
            throw e;
        }
    }

    /**
     * Copy all content from HSSF sheet to XSSF sheet
     */
    private static void copySheet(HSSFSheet source, XSSFSheet destination,
            HSSFWorkbook sourceWb, XSSFWorkbook destWb) {

        // Copy merged regions
        for (int i = 0; i < source.getNumMergedRegions(); i++) {
            CellRangeAddress mergedRegion = source.getMergedRegion(i);
            destination.addMergedRegion(mergedRegion);
        }

        // Copy rows and cells
        for (Row sourceRow : source) {
            Row destRow = destination.createRow(sourceRow.getRowNum());
            copyRow(sourceRow, destRow, sourceWb, destWb);
        }

        // Copy column widths
        for (int i = 0; i < source.getRow(0).getLastCellNum(); i++) {
            destination.setColumnWidth(i, source.getColumnWidth(i));
        }
    }

    /**
     * Copy a row from source to destination
     */
    private static void copyRow(Row sourceRow, Row destRow,
            Workbook sourceWb, Workbook destWb) {

        destRow.setHeight(sourceRow.getHeight());

        for (Cell sourceCell : sourceRow) {
            Cell destCell = destRow.createCell(sourceCell.getColumnIndex());
            copyCell(sourceCell, destCell, sourceWb, destWb);
        }
    }

    /**
     * Copy a cell from source to destination including value, formula, and style
     */
    private static void copyCell(Cell sourceCell, Cell destCell,
            Workbook sourceWb, Workbook destWb) {

        // Copy cell style
        if (sourceCell.getCellStyle() != null) {
            CellStyle newStyle = destWb.createCellStyle();
            newStyle.cloneStyleFrom(sourceCell.getCellStyle());
            destCell.setCellStyle(newStyle);
        }

        // Copy cell value based on type
        switch (sourceCell.getCellType()) {
            case STRING:
                destCell.setCellValue(sourceCell.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(sourceCell)) {
                    destCell.setCellValue(sourceCell.getDateCellValue());
                } else {
                    destCell.setCellValue(sourceCell.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                destCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                destCell.setCellFormula(sourceCell.getCellFormula());
                break;
            case BLANK:
                destCell.setBlank();
                break;
            case ERROR:
                destCell.setCellErrorValue(sourceCell.getErrorCellValue());
                break;
            default:
                break;
        }

        // Copy cell comment if exists
        if (sourceCell.getCellComment() != null) {
            Comment comment = destCell.getSheet().createDrawingPatriarch()
                    .createCellComment(new XSSFClientAnchor());
            comment.setString(sourceCell.getCellComment().getString());
            destCell.setCellComment(comment);
        }
    }
}
