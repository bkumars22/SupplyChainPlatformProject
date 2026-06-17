/*
 * Created on May 22, 2005
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.authentication.exception;

/**
 * @author bblasko
 *

 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NotAuthorizedException extends Exception
{
    String entityType;
    String op;
    public NotAuthorizedException(String entityType, String op)
    {    
        super("Operation " + op + " not permitted on " + entityType);
        this.entityType = entityType;
        this.op = op;        
    }
}
