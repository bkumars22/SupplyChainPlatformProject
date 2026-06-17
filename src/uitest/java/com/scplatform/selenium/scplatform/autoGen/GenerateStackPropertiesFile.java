/**
 * @GenerateStackPropertiesFile.java@
 *
 * Created on Jan 19, 2011
 *
 *      Copyright (c) 2010 E2open, Inc.
 *      All Rights Reserved.
 *
 *      THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *      The copyright notice above does not evidence any
 *      actual or intended publication of such source code.
 *
 */
package com.test.selenium.scplatform.autoGen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.test.selenium.common.BuildStackProperties;
import com.test.selenium.common.JLog;
import com.test.selenium.common.Prop;
import com.test.selenium.common.StringUtilities;
import com.test.selenium.scplatform.base.Utilities;

/**
 * This generates a stack property file against a given test stack.
 *
 * @author David Genrich
 * @since SSP 7.3
 *
 */
public class GenerateStackPropertiesFile {

    /**
     * Prompts for host, sshUser and sshPassword instead of requiring the values
     * to be set as member variables.<br>
     * If running within Eclipse, run as a java application and type into the
     * console view when prompted.
     */
    public static void main(String[] args) {
        try {

            // ----------------------------------------------------------------
            // WARNING! THE UTILITIES ARE PROJECT SPECFIC.
            //
            // Make sure the correct utilities instance is used as they have
            // been overridden per project. This will typically only require the
            // import statement be updated
            // ----------------------------------------------------------------
            Utilities utilities = new Utilities();
            utilities.setupPropertiesFile();

            // ----------------------------------------------------------------
            // Everything below this line is project independent
            // Note that ssh.host, ssh.user and ssh.password can be provided
            // as System properties (e.g. -Dssh.host=xxx).
            //
            // ssh.user and ssh.password will be looked for in the user
            // properties
            // file {user.dir}/e2test.properties if they aren't provided as
            // System properties
            // ----------------------------------------------------------------
            Prop prop = Prop.getInstance();
            if (prop != null) {
                // check to see if ssh.host is in the properties.
                // This could be the case if it was passed in as a system
                // property (e.g. -Dssh.host={hostname})
                String sshHost = prop.get().getProperty("ssh.host");
                if (sshHost != null) {
                    JLog.info(String.format("using ssh.host [%s]", sshHost));
                } else {
                    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
                    sshHost = StringUtilities.promptForString(in, "Enter hostname: ");
                }

                // BuildStackProperties.generateForHost(sshHost);
                BuildStackProperties.generateForHost(sshHost, getAdditionalProperties_emailAlerts());
            }

        } catch (IOException e) {
            JLog.error(e.toString());
        }
    }

    private static Map<String, String> getAdditionalProperties_emailAlerts() {
        Map<String, String> additionalProperties = new HashMap<String, String>();

        additionalProperties.put("db.scpm", "scplatform.db.user");
        additionalProperties.put("db.instance", "scplatform.db.name");
        additionalProperties.put("db.shared", "e2.db.app.user");
        additionalProperties.put("db.hostname", "scplatform.db.host.name");
        additionalProperties.put("db.password", "scplatform.db.user.password");
        additionalProperties.put("stack.mcm", "scplatform.hostnames.0");
        additionalProperties.put("stack.scpm", "scplatform.hostnames.0");
        additionalProperties.put("stack.b2b", "e2na.hostnames.0");
        additionalProperties.put("stack.name", "e2.stack.name");
        additionalProperties.put("stack.id", "e2.stack.id");
        additionalProperties.put("hub.company.name", "e2.ssp.hub.company.name");
        additionalProperties.put("mtcm.base.url", "https://${e2.webproxy.dnsname}${scplatform.junction}/scplatform");
        additionalProperties.put("mtcm.url", "https://${e2.webproxy.dnsname}${scplatform.junction}/scplatform/authenticate.do");

        return additionalProperties;
    }
}
