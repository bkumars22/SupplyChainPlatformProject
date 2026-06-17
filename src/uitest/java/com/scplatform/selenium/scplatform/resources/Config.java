/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.test.selenium.common.AbstractPage;
import com.test.selenium.common.FileHelper;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.SSH;

/**
 * Provides api to access MTCM config bundle
 * (/scplatform/app/scplatform/config/pcm-config.properties)
 *
 * @author David Genrich
 */
public class Config {
    protected static String propertyName = "pcm-config.properties";
    protected static String propertyLocalName = propertyName;
    protected static String propertyDir = "/scplatform/app/scplatform/config/";
    protected static String hostKey = "stack.mcm";

    /**
     *
     * @return Refreshes SCPlatform and returns success
     */
    public static boolean refreshSCPlatform() {
        return refreshApp("scplatform");
    }

    /**
     *
     * @return Restarts SCPlatform and returns success
     */
    public static boolean restartSCPlatform() {
        return restartApp("scplatform");
    }

    /**
     * @param key
     *            The key to lookup
     * @param defaultValue
     *            default value
     * @return value for the key. If key not found, then defaultValue is
     *         returned.
     * @see #getText(String, String)
     */
    public static String getText(String key, String defaultValue) {
        String text = getText(key);
        if (text == null) {
            text = defaultValue;
        } else if (text.equals(key)) {
            text = defaultValue;
        }
        return text;
    }

    /**
     * @param key
     *            The key to lookup
     * @return value for the key. If key not found, then key is returned.
     * @see #getText(String, String)
     */
    public static String getText(String key) {
        String text = null;

        // determine if the factory is in the overridable properties
        Properties overrideable = loadOverridableProperties();
        if (overrideable != null) {
            text = overrideable.getProperty(key);
        }

        if (text == null) {
            text = key;
        }

        return text;
    }

    /**
     * Handles getting multiple values where the key is all the same. Will
     * append "=" to the key, then find all lines starting with key. For
     * example: <br>
     * <br>
     * pcm.costRecord.reasonCode=DGP NEGOTIATION CYCLE <br>
     * pcm.costRecord.reasonCode=ENTERPRISE EXCEPTION <br>
     * pcm.costRecord.reasonCode=EXTEND PRICING FOR EOL PART <br>
     * pcm.costRecord.reasonCode=NEW PART <br>
     *
     * @param key
     *            The key to lookup (example: pcm.costRecord.reasonCode)
     * @return List of String of all values starting with key.
     */
    public static List<String> getMultipleText(String key) {
        List<String> values = new ArrayList<>();
        File file = getWorkingFile();

        String fullFileName = file.toString();

        String strRead;
        String findKey = key + "=";

        try {

            BufferedReader readbuffer = new BufferedReader(new FileReader(fullFileName));

            while ((strRead = readbuffer.readLine()) != null) {
                if (strRead.startsWith(findKey)) {
                    values.add(strRead.split("=")[1].trim());
                }
            }

            readbuffer.close();
        } catch (IOException e) {
            JLog.error(e);
        }

        return values;
    }

    protected static String getHostFile() {

        String useFile = null;

        String remoteFile = propertyDir + propertyName;

        File file = getWorkingFile();
        if (file.exists()) {
            return file.toString();
        }

        // download file
        Prop prop = Prop.getInstance();
        String host = prop.get().getProperty(hostKey);
        if (host == null) {
            host = prop.get().getProperty("stack.b2b");
        }

        // check if the file exists before continue
        ArrayList<String> output;
        try {
            SSH.setQuietMode(false);
            output = (ArrayList<String>) SSH.executeSingleCommand(host, "ls " + remoteFile);
            if (SSH.doesOutputContain(output, "No such file or directory") != null) {
                // file does not exist, no need to download it
                return useFile;
            }
        } catch (IOException e2) {
            JLog.warning(JLog.getStackTraceAsString(e2));
            return useFile;
        }

        // file exists, proceed with downloading it.
        try {
            // SSH.executeCommands(host, "/scplatform/bin/eoadmin ", "chmod 666 " +
            // remoteFile, "exit", "exit");
            SSH.executeCommands(host, ". /scplatform/profile/profile.sh", "/scplatform/bin/eoadmin ", "chmod 666 " + remoteFile,
                    "exit", "exit");
        } catch (IOException e1) {
            JLog.warning(e1.toString());
        }

        try {
            FileHelper fileHelper = new FileHelper();
            String savedFile = SSH.getFile(host, remoteFile, fileHelper.getTempDirector());
            fileHelper.copyfile(savedFile, file.toString());
            useFile = file.toString();
        } catch (IOException e) {
            JLog.warning(JLog.getStackTraceAsString(e));
        }

        return useFile;

    }

    protected static File getWorkingFile() {
        Prop prop = Prop.getInstance();
        String workingDir = prop.getWorkingDir();
        String workingFile = workingDir + propertyLocalName;

        File file = new File(workingFile);
        return file;
    }

    // =========================================
    // Read the properties files
    // =========================================
    protected static Properties overridableProperties = null;
    protected static String overrideProperties_file = null;

    protected static Properties loadOverridableProperties() {
        if (overridableProperties != null) {
            return overridableProperties;
        }

        if (overrideProperties_file == null) {
            getOverridePropertiesFile();
        }

        if (overrideProperties_file == null) {
            return null;
        }

        File file = new File(overrideProperties_file);
        if (!file.exists()) {
            return null;
        }

        overridableProperties = new Properties();
        try {
            overridableProperties.load(new FileInputStream(overrideProperties_file));
        } catch (FileNotFoundException e) {
            overridableProperties = null;
            JLog.warning(JLog.getStackTraceAsString(e));
        } catch (IOException e) {
            overridableProperties = null;
            JLog.warning(JLog.getStackTraceAsString(e));
        }

        return overridableProperties;
    }

    protected static void getOverridePropertiesFile() {
        File file = getWorkingFile();

        if (file.exists()) {
            overrideProperties_file = file.toString();
        } else {
            downloadResource();
        }
    }

    public static void downloadResource() {

        overrideProperties_file = getHostFile();
    }

    protected static String forceDownloadResource() {
        overridableProperties = null;
        overrideProperties_file = null;

        FileHelper fileHelper = new FileHelper();
        File file = getWorkingFile();
        if (!fileHelper.deleteFile(file)) {
            JLog.warning("Unable to delete file: " + file.toString());
        }

        return getHostFile();
    }

    // =========================================
    // Modify the properties files
    // =========================================

    /**
     * Sets a Key/Value pair in the pcm-config.properties on the stack. You must
     * call {@link #restartSCPlatform()} for changes to take affect.
     *
     * <br>
     * <b>Logic:</b>
     * <UL>
     * <LI>If the Key/Value is already set correctly, then return true.
     * <LI>If the Key/Value is set, but incorrectly, change the Value then
     * restart scplatform
     * <LI>If the Key is not found, but is in the file commented out, replace in
     * file as uncommented with the correct Value. Restart scplatform.
     * <LI>If the Key is not found and is not in the file, then append it to the
     * end of the file. Restart scplatform.
     * </UL>
     *
     * @param key
     *            The key to change (example: pcm.upload.readonly.enterprise)
     * @param value
     *            The value to set (example: false)
     * @return Returns true if the key was successfully set.
     * @throws IOException
     *             Throws IOException is the SSH commands fails
     * @see #restartSCPlatform()
     */
    public static boolean setKeyValue(String key, String value) throws IOException {

        String currentValue = getText(key);
        if (key.equals(currentValue)) {
            currentValue = null;
        }

        // if ((currentValue != null) && (currentValue.equalsIgnoreCase(value)))
        // {
        // // value is already set as needed, no need to do anything
        // JLog.write("\"" + key + "\" correctly set to '" + currentValue + "'
        // in '" + propertyDir + propertyName
        // + "'. No changes needed.");
        // return true;
        // }

        boolean success = false;

        String commands[] = new String[6];
        commands[0] = ". /scplatform/profile/profile.sh";
        commands[1] = "/scplatform/bin/eoadmin ";
        commands[2] = "cd " + propertyDir;

        // get line containing key from file
        String currentLine = findLineInFile(key);
        if (currentLine == null) {
            // key is not there, insert it into the bottom
            commands[3] = "echo \"" + key + "=" + value + "\" >> " + propertyName;
        } else {
            // already was uncommented, just set flag as desired
            commands[3] = "sed -i 's@^" + currentLine + "@" + key + "=" + value + "@g' " + propertyName;
        }

        commands[4] = "exit";
        commands[5] = "exit";

        Prop prop = Prop.getInstance();
        String host = prop.get().getProperty(hostKey);

        List<String> output = SSH.executeCommands(host, commands);
        SSH.printOutput(output);

        // get the host files to check that the setting was done

        forceDownloadResource();
        currentLine = findLineInFile(key);
        if (currentLine != null) {
            if (currentLine.startsWith("#")) {
                JLog.error("Key " + key + " is in " + propertyLocalName + " file, but commentted out: " + currentLine);
                success = false;
            } else if (currentLine.endsWith("=" + value)) {
                success = true;
            } else {
                success = false;
                JLog.error("Expecting " + key + "=" + value + " in " + propertyLocalName + " file. Actual is: "
                        + currentLine);
            }

        } else {
            JLog.warning("Unable to find \"" + key + "\" in the " + propertyLocalName + " file!");
        }

        return success;
    }

    /**
     * Removes a Key in the pcm-config.properties on the stack by commenting out
     * the line if it exists. You must call {@link #restartSCPlatform()} for changes
     * to take affect.
     *
     *
     * @param key
     *            The key to change (example: pcm.upload.readonly.enterprise)
     * @return Returns true if the key was successfully set.
     * @throws IOException
     *             Throws IOException is the SSH commands fails
     * @see #restartSCPlatform()
     */
    public static boolean removeKey(String key) throws IOException {

        String currentValue = getText(key);
        if (key.equals(currentValue)) {
            JLog.write("\"" + key + "\" currenlty not in '" + propertyDir + propertyName + "'.  No changes needed.");
            return true;
        }

        boolean success = false;

        String commands[] = new String[6];
        commands[0] = ". /scplatform/profile/profile.sh";
        commands[1] = "/scplatform/bin/eoadmin ";
        commands[2] = "cd " + propertyDir;

        // get line containing key from file
        String currentLine = findLineInFile(key);
        if (currentLine == null) {
            JLog.write("\"" + key + "\" currenlty not in '" + propertyDir + propertyName + "'.  No changes needed.");
            return true;
        } else {
            // already was uncommented, just comment out the line
            commands[3] = "sed -i 's@^" + currentLine + "@#" + currentLine + "@g' " + propertyName;
        }

        commands[4] = "exit";
        commands[5] = "exit";

        Prop prop = Prop.getInstance();
        String host = prop.get().getProperty(hostKey);

        List<String> output = SSH.executeCommands(host, commands);
        SSH.printOutput(output);

        // get the host files to check that the setting was done

        forceDownloadResource();
        currentLine = findLineInFile(key);
        if (currentLine != null) {
            if (currentLine.startsWith("#")) {
                success = true;
            } else {
                success = false;
                JLog.error("Expecting #" + key + " in " + propertyLocalName + " file.  Actual is: " + currentLine);
            }

        } else {
            success = true;
        }

        return success;
    }

    protected static String findLineInFile(String findWhat) {
        File file = getWorkingFile();
        if (!file.exists()) {
            return null;
        }

        String fullFileName = file.toString();
        String strRead;
        String foundLine = null;

        try {

            BufferedReader readbuffer = new BufferedReader(new FileReader(fullFileName));

            while ((strRead = readbuffer.readLine()) != null) {
                if (strRead.contains(findWhat)) {
                    foundLine = strRead;
                    break;
                }
            }

            readbuffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return foundLine;
    }

    protected static boolean restartApp(String appName) {

        String completionString = String.format("Activating %s.... Success", appName);

        Prop prop = Prop.getInstance();
        String host = prop.get().getProperty(hostKey);

        int WAIT_TIMEOUT = 2 * 60 * 1000; // 2 minutes
        int OUTPUT_TIMEOUT = 5 * 60 * 1000; // 5 minutes
        List<String> output = new ArrayList<>();

        boolean exception = false;
        boolean completed = false;

        try {

            SSH.setOutputTimeOut(OUTPUT_TIMEOUT);
            SSH.setWaitTimeOut(WAIT_TIMEOUT);
            // output = SSH.executeCommands(host, "/scplatform/bin/eoadmin ", "setup
            // restart " + appName, "exit", "exit");
            output = SSH.executeCommands(host, ". /scplatform/profile/profile.sh", "/scplatform/bin/eoadmin ",
                    "setup restart " + appName, "exit", "exit");
            for (String line : output) {
                if ((line.contains("Exception")) || (line.contains(".java:"))
                        || (line.contains("No such file or directory")) || (line.contains("STRERR"))
                        || (line.contains("Operation not permitted"))
                        || ((line.contains("Permission denied")) && (!line.contains("cannot touch")))
                        || (line.contains("must be setuid root"))) {
                    exception = true;
                    JLog.warning(line);
                } else if (line.contains(completionString)) {
                    JLog.write(line);
                    completed = true;
                } else {
                    JLog.write(line);
                }
            }

            if (completed) {
                if (exception) {
                    JLog.error(appName.toUpperCase() + " Restarted Successfully, but there where exceptions!");
                } else {
                    JLog.write(appName.toUpperCase() + " Restarted Successfully!");
                }

            } else {
                JLog.error("Did not reach Completion String - " + completionString + " - " + appName.toUpperCase()
                        + " Restart might have issues!");
                exception = true;
            }

        } catch (IOException e) {
            JLog.error(e);
        }

        AbstractPage.sleep(60); // after restart, if try to login right away,
                                // get that the service is down

        return completed;

    }

    protected static boolean refreshApp(String appName) {

        String completionString = "removed";

        Prop prop = Prop.getInstance();
        String host = prop.get().getProperty(hostKey);

        int WAIT_TIMEOUT = 20 * 60 * 1000; // 20 minutes
        int OUTPUT_TIMEOUT = 15 * 60 * 1000; // 15 minutes
        List<String> output = new ArrayList<>();

        boolean exception = false;
        boolean completed = false;

        try {

            SSH.setOutputTimeOut(OUTPUT_TIMEOUT);
            SSH.setWaitTimeOut(WAIT_TIMEOUT);
            output = SSH.executeCommands(host, ". /scplatform/profile/profile.sh", "/scplatform/bin/eoadmin ", "setup remove;",
                    "exit", "exit");

            for (String line : output) {
                if ((line.contains("Exception")) || (line.contains(".java:"))
                        || (line.contains("No such file or directory")) || (line.contains("STRERR"))
                        || (line.contains("Operation not permitted"))
                        || ((line.contains("Permission denied")) && (!line.contains("cannot touch")))
                        || (line.contains("must be setuid root"))) {
                    exception = true;
                    JLog.warning(line);
                } else if (line.contains(completionString)) {
                    JLog.write(line);
                    completed = true;
                } else {
                    JLog.write(line);
                }
            }

            if (completed) {
                if (exception) {
                    JLog.error(appName.toUpperCase() + " Setup Remove done Successfully, but there where exceptions!");
                } else {
                    JLog.write(appName.toUpperCase() + " Setup Remove done Successfully!");
                }

            } else {
                JLog.error("Did not reach Completion String - " + completionString + " - " + appName.toUpperCase()
                        + " Setup Remove might have issues!");
                exception = true;
            }

        } catch (IOException e) {
            JLog.error(e);
        }

        try {

            SSH.setOutputTimeOut(OUTPUT_TIMEOUT);
            SSH.setWaitTimeOut(WAIT_TIMEOUT);
            output = SSH.executeCommands(host, ". /scplatform/profile/profile.sh", "/scplatform/bin/eoadmin ", "setup;", "exit",
                    "exit");

            for (String line : output) {
                if ((line.contains("Exception")) || (line.contains(".java:"))
                        || (line.contains("No such file or directory")) || (line.contains("STRERR"))
                        || (line.contains("Operation not permitted"))
                        || ((line.contains("Permission denied")) && (!line.contains("cannot touch")))
                        || (line.contains("must be setuid root"))) {
                    exception = true;
                    JLog.warning(line);
                } else if (line.contains(completionString)) {
                    JLog.write(line);
                    completed = true;
                } else {
                    JLog.write(line);
                }
            }
            completionString = String.format("%s.... starting", appName);

            if (completed) {
                if (exception) {
                    JLog.error(appName.toUpperCase() + " Setup done Successfully, but there where exceptions!");
                } else {
                    JLog.write(appName.toUpperCase() + " Setup done Successfully!");
                }

            } else {
                JLog.error("Did not reach Completion String - " + completionString + " - " + appName.toUpperCase()
                        + " Setup might have issues!");
                exception = true;
            }

        } catch (IOException e) {
            JLog.error(e);
        }

        AbstractPage.sleep(60); // after refresh, if try to login right away,
                                // get that the service is down

        return completed;

    }

}
