/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.cucumber;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.test.selenium.common.JLog;
import com.test.selenium.common.StringUtilities;

public class Preprocessing extends com.test.selenium.common.cucumber.Preprocessing {

    private Preprocessing() {
    }

    /**
     * Process.
     *
     * @param value
     *            the value
     * @return the object
     */
    public static Object process(Object value) {
        return transform(value, 0, true);
    }

    /**
     * Process.
     *
     * @param value
     *            the value
     * @param index
     *            the index
     * @return the object
     */
    public static Object process(Object value, int index) {
        return transform(value, index, true);
    }

    /**
     * Process.
     *
     * @param <T>
     *            the generic type
     * @param cukeTable
     *            the cuke table
     * @return the list
     */
    public static <T> List<T> process(List<T> cukeTable) {
        Object newValue = null;
        List<String> ignoreList = ignore();
        List<ArrayList<String>> htmlTable = new ArrayList<>();
        ArrayList<String> header = new ArrayList<>();
        ArrayList<String> data = new ArrayList<>();
        boolean valuesTransaformed = false;

        try {
            for (int i = 0; i < cukeTable.size(); i++) {
                data.clear();

                for (Field field : getFields(cukeTable.get(i))) {
                    String name = field.getName();
                    if (ignoreList.contains(name)) {
                        continue;
                    }
                    Object value = getValue(cukeTable.get(i), name);
                    if (value == null) {
                        continue;
                    }

                    newValue = transform(value, i);
                    if (transformed) {
                        valuesTransaformed = true;
                        Method method = null;
                        try {
                            method = cukeTable.get(i).getClass().getMethod("set" + StringUtils.capitalize(name),
                                    String.class);
                        } catch (NoSuchMethodException e) {
                            try {
                                method = cukeTable.get(i).getClass().getMethod("is" + StringUtils.capitalize(name),
                                        boolean.class);
                            } catch (NoSuchMethodException e1) {
                                JLog.error(e);
                            }
                        }

                        Object[] parameters = new Object[1];
                        parameters[0] = newValue;
                        method.invoke(cukeTable.get(i), parameters);
                    }

                    if (i == 0) {
                        header.add(name);
                    }
                    StringUtilities utils = new StringUtilities();
                    data.add(utils.convertToString(newValue));
                }

                if (i == 0) {
                    htmlTable.add(header);
                }
                htmlTable.add(data);
            }
        } catch (Exception e) {
            JLog.fail(e);
        }

        if (valuesTransaformed) {
            JLog.addCucumberTable("Preprocessing Data Table", htmlTable);
        }

        return cukeTable;

    }

    /**
     * Transform.
     *
     * @param value
     *            the value
     * @param index
     *            the index
     * @return the object
     */
    protected static Object transform(Object value, int index) {
        return transform(value, index, false);
    }

    /**
     * Transform.
     *
     * @param value
     *            the value
     * @param index
     *            the index
     * @param log
     *            the log
     * @return the object
     */
    protected static Object transform(Object value, int index, boolean log) {
        StringUtilities utils = new StringUtilities();

        if (value instanceof String) {
            if (StringUtils.isBlank(utils.convertToString(value))) {
                return value;
            }

            if ("*".equals(value)) {
                return value;
            }
        }

        transformed = false;
        com.test.selenium.scplatform.cucumber.CukeHelper helper = new com.test.selenium.scplatform.cucumber.CukeHelper();
        helper.setPreprocessClasses(getPreprocessingClasses());
        helper.setRegistredClasses(getRegisteredClasses());
        CukeHelper.setIndexForClass(index);

        String newValue = helper.transformUnique(value);
        if (newValue.equals(utils.convertToString(value))) {
            return value;
        } else {
            transformed = true;
            if (log) {
                JLog.write(String.format("Transformed :: %s --> %s", value, utils.convertToString(newValue)));
            }
            return newValue;
        }
    }

}
