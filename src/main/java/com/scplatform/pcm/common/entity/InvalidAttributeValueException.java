/**
 *	InvalidAttributeValueException.java
 *	Created on Aug 12, 2013
 *     
 *	Copyright (c) 2010 E2open, Inc.
 *	All Rights Reserved.
 *
 *	THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *	The copyright notice above does not evidence any
 *	actual or intended publication of such source code. 
 *	
 *	Author: sgupta
 */
package com.scplatform.pcm.common.entity;

/**
 * Exception thrown by Attribute Value validators if the attribute value is invalid
 * 
 * @author sgupta
 */
public class InvalidAttributeValueException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 
     */
    public InvalidAttributeValueException() {}

    /**
     * @param message
     */
    public InvalidAttributeValueException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public InvalidAttributeValueException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public InvalidAttributeValueException(String message, Throwable cause) {
        super(message, cause);
    }

}
