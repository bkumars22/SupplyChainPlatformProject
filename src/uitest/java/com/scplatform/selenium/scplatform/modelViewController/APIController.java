/*
 * @APIController.java@
 * Created on Mar 23, 2022
 *
 * Copyright (c) 2022 E2open, Inc.
 * All Rights Reserved.
 *
 * THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 * The copyright notice above does not evidence any
 * actual or intended publication of such source code.
 *
 */
/**
 *
 */
package com.test.selenium.scplatform.modelViewController;

import com.test.selenium.common.rest.GenricServer;
import com.test.selenium.common.rest.Server;
import com.test.selenium.common.rest.fasterxml.JacksonRestImpl;

/**
 * @author AParameswaran
 *
 */
public class APIController extends JacksonRestImpl {

    @Override
    public String getRestPath() {
        // TODO Auto-generated method stub
        return null;
    }

    public Server getServer() {
        GenricServer genericServer = new GenricServer();
        return genericServer.withBaseURL("https://postman-echo.com").get();
    }

}
