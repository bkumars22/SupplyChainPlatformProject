/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.cucumber;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.test.selenium.common.Configuration;
import com.test.selenium.common.JLog;
import com.test.selenium.common.RealTime;
import com.test.selenium.scplatform.utilities.MessageIO;

/**
 * The Class CukeHelper.
 */
public class CukeHelper extends com.test.selenium.common.cucumber.CukeHelper {

    protected static Map<String, Class> msgClazzes;
    protected static boolean classesWhereLoaded = false;

    /**
     * Sets the message class.
     *
     * @param key
     *            the key
     * @param clazz
     *            the clazz
     */
    public static void setMessageClass(String key, Class clazz) {
        if ((msgClazzes == null) || (msgClazzes.isEmpty())) {
            msgClazzes = new HashMap<>();
        }
        loadMsgClasses();
        msgClazzes.put(key, clazz);
        saveMsgClasses();
    }

    /**
     * Gets the message class.
     *
     * @param key
     *            the key
     * @return the message class
     */
    protected static Class getMessageClass(String key) {
        loadMsgClasses();
        if (msgClazzes.containsKey(key)) {
            return msgClazzes.get(key);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.test.selenium.common.cucumber.CukeHelper#
     * transformConfigurationReferences(java.lang.String)
     */
    @Override
    public String transformConfigurationReferences(String rawValue) {
        if (StringUtils.isNotBlank(rawValue)) {
            rawValue = transformStackProperties(rawValue);
            rawValue = transformRuntime(rawValue);
            try {
                rawValue = transformClassValues(rawValue);
            } catch (IllegalAccessException | InvocationTargetException e) {
                JLog.error(e);
            }
        }
        return rawValue;
    }

    /**
     * Transform class values.
     *
     * @param rawValue
     *            the raw value
     * @return the string
     * @throws IllegalAccessException
     *             the illegal access exception
     * @throws InvocationTargetException
     *             the invocation target exception
     */
    @Override
    protected String transformClassValues(String rawValue) throws IllegalAccessException, InvocationTargetException {
        if ((preprocessClasses == null) || (preprocessClasses.isEmpty())) {
            return rawValue;
        }
        boolean found = false;

        String patternStr = "\\$\\{([A-Z0-9_-]+)\\.(\\w+)\\}";
        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(rawValue);

        while (m.find()) {
            found = true;
            String className = m.group(1);
            String variable = m.group(2);
            Object lookup = lookupClass(className);
            String replacement = (String) getValue(lookup, variable);
            if (replacement != null) {
                rawValue = rawValue.replace(m.group(), replacement);
            }

        }
        if (!found) {
            patternStr = "\\$\\{(\\w+)\\.(\\w+)\\}";
            p = Pattern.compile(patternStr);
            m = p.matcher(rawValue);
            while (m.find()) {
                String className = m.group(1);
                String variable = m.group(2);
                Object lookup = lookupClass(className);
                String replacement = (String) getValue(lookup, variable);
                if (replacement != null) {
                    rawValue = rawValue.replace(m.group(), replacement);
                }

            }
        }
        return rawValue;
    }

    /**
     * Lookup class.
     *
     * @param className
     *            the class name
     * @return the object
     */
    protected static Object lookupClass(String className) {
        Object classObj = null;

        if (preprocessClasses.containsKey(className)) {
            classObj = preprocessClasses.get(className);
        }

        Class messageClazz = getMessageClass(className);
        if (messageClazz != null) {
            // have a message class
            MessageIO messageIO = new MessageIO(messageClazz);
            classObj = messageIO.load(className);
        }
        return classObj;
    }

    /**
     * Save msg classes.
     */
    protected static void saveMsgClasses() {
        RealTime runtime = RealTime.getInstance();
        String workingDir = runtime.getWorkingDir();

        String saveToFile = workingDir + "MsgClasses.ser";

        try (FileOutputStream fileStream = new FileOutputStream(new File(saveToFile));
                ObjectOutputStream outStream = new ObjectOutputStream(fileStream);) {

            outStream.writeObject(msgClazzes);
            fileStream.close();

            Configuration.setRuntime("MsgClasses", saveToFile);

        } catch (FileNotFoundException e) {
            JLog.error(e);
        } catch (IOException io) {
            JLog.warning(io);
        }
    }

    /**
     * Load msg classes.
     */
    @SuppressWarnings("unchecked")
    protected static void loadMsgClasses() {
        if ((msgClazzes == null) || (msgClazzes.isEmpty())) {
            msgClazzes = new HashMap<>();
        }

        if (classesWhereLoaded) {
            return;
        }
        if (!Configuration.runtimeContainsKey("MsgClasses")) {
            return;
        }

        String fullFileName = Configuration.getRuntime("MsgClasses");

        try (FileInputStream fileIn = new FileInputStream(new File(fullFileName));
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);) {

            HashMap<String, Class> loadClasses = (HashMap<String, Class>) objectIn.readObject();
            fileIn.close();

            msgClazzes.putAll(loadClasses);
            classesWhereLoaded = true;
        } catch (Exception ex) {
            JLog.warning(ex);
        }

        return;
    }

    public static Object findSavedClass(String savedClassName) {
        return lookupClass(savedClassName);
    }

}
