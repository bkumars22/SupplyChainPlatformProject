/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.utilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.DBUtility;
import com.test.selenium.common.JLog;

public class Database extends DBUtility {
    private static java.sql.Connection dbConnection;
    private static ResultSet rs;

    public static void closeConnection() {
        closeSafely(rs);
        closeStatement();
        closeSafely(dbConnection);

        dbConnection = null;
        rs = null;
    }

    /**
     * @param SQL
     *            SQL Query to execute against the SCPM Database
     * @return {@link ResultSet}
     * @see #getResult(String, String)
     * @see #getResults(String, String...)
     */
    public static ResultSet executeQuery(String SQL) {
        closeSafely(rs);
        closeStatement();
        rs = null;
        try {
            // query the SVPM database
            dbConnection = DBUtility.openSCPMConnection();
            rs = DBUtility.executeQuery(dbConnection, SQL);
        } catch (SQLException e) {
            JLog.warning(SQL);
            JLog.warning("Error running sql: " + e.toString());
        }

        return rs;
    }

    /**
     * @param SQL
     *            SQL update statement against the SCPM Database
     * @return either (1) the row count for SQL Data Manipulation Language (DML)
     *         statements or (2) 0 for SQL statements that return nothing
     */
    public static int executeUpdateQuery(String SQL) {
        int i = -1;

        try {
            dbConnection = DBUtility.openSCPMConnection();
            JLog.write("Execute Update SQL: " + SQL);
            i = DBUtility.executeUpdate(dbConnection, SQL);
            DBUtility.closeConnection(dbConnection);
        } catch (SQLException e) {
            JLog.warning(SQL);
            JLog.warning("Error running sql: " + e.toString());
        }

        return i;
    }

    /**
     * Execute a SQL Query against the SCPM database, returning a single value.
     * If multiple values and/or rows are needed, use
     * {@link #getResults(String, String...)}.
     * 
     * @param sql
     *            SQL statement to execute
     * @param columnToReturn
     *            A single column to return
     * @return A single value for a SQL Query
     */
    public static String getResult(String sql, String columnToReturn) {
        return getResult(sql, columnToReturn, 1);
    }

    /**
     * Execute a SQL Query against the SCPM database, returning a single value.
     * If multiple values and/or rows are needed, use
     * {@link #getResults(String, String...)}.
     * 
     * @param sql
     *            SQL statement to execute
     * @param columnToReturn
     *            A single column to return
     * @return A single value for a SQL Query
     */
    public static String getResult(String sql, String columnToReturn, int retry) {
        String result = null;
        int attempt = 0;

        while ((attempt < retry) || (StringUtils.isBlank(result))) {
            rs = executeQuery(sql);

            try {
                rs.next();

                // the getString() uses the column name from the SQL query
                result = rs.getString(columnToReturn);
                break;
            } catch (SQLException e) {
                JLog.warning(sql);
                JLog.warning("Error running sql: " + e.toString());
                attempt++;
                AbstractPage.sleep(20);
            }
        }

        return result;
    }

    /**
     * Executes a SQL Query against the SCPM database, returning multiple rows
     * and/or multiple columns. <br>
     * Example:<br>
     * 
     * <pre>
     * String sql = "select MinReferringOrderPrice, MaxReferringOrderPrice from SOME_TABLE where Site='Austin'";
     * HashMap<String, List<String>> results = Database.getResults(sql, "MinReferringOrderPrice",
     *         "MaxReferringOrderPrice");
     * String firstMinPrice = results.get("MinReferringOrderPrice").get(0);
     * </pre>
     * 
     * @param sql
     *            The SQL query to execute
     * @param columnsToRetuns
     *            String array of the column names to return
     * @return {@link HashMap} of String and List<String>. The key is the column
     *         name and the value is a List of the sql values.
     */
    public static HashMap<String, List<String>> getResults(String sql, String... columnsToRetuns) {
        HashMap<String, List<String>> results = new HashMap<String, List<String>>();
        List<String> values = new ArrayList<String>();
        String delimiter = "|~~|";

        // get multiple rows and multiple results
        rs = executeQuery(sql);

        try {
            while (rs.next()) {
                for (String column : columnsToRetuns) {
                    values.add(column + delimiter + rs.getString(column));
                }

            }
        } catch (SQLException e) {
            JLog.warning(sql);
            JLog.error("Error running sql: " + e.toString(), e);
        }

        // parse values into a hashmap
        for (String column : columnsToRetuns) {
            List<String> columnData = new ArrayList<String>();

            for (String data : values) {
                if (data.startsWith(column + delimiter)) {
                    String value = data.replace(column + delimiter, "");
                    columnData.add(value);
                }

            }

            results.put(column, columnData);
        }

        return results;
    }

}
