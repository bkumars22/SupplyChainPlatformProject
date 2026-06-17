/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.validator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


public class Messages {
    
    private static Log logger = LogFactory.getLog(Messages.class);    
    
    private static ResourceBundle messages = ResourceBundle.getBundle(
            "scplatform-validation-messages");
    
    private static String getMessageString(String messageKey) {
        String message = null;
        try {
            message = messages.getString(messageKey);
        } catch (MissingResourceException e) {
            logger.warn("Missing resource for key=" + messageKey);
        }
        return message;
    }
    
    public static String getMessage(String messageKey) {
        return getMessage(messageKey, null); 
    }
    
    public static String getMessage(String messageKey, Object arg0) {
        Object[] args = new Object[1];
        args[0] = arg0;
        return getMessage(messageKey, args);
    }
    
    public static String getMessage(String messageKey, Object arg0, 
            Object arg1) {
        Object[] args = new Object[2];
        args[0] = arg0;
        args[1] = arg1;
        return getMessage(messageKey, args);
    }
    
    public static String getMessage(String messageKey, Object[] args) {
        String message = getMessageString(messageKey);
        if (message != null) {
            MessageFormat mf = new MessageFormat(message);
            if (args != null) {
                message = mf.format(args);
            } else {
                message = mf.format(new Object[0]);
            }
        }
        return message;
    }

}
