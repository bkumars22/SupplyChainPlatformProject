/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.validator;

public class ValidationError {
    
    String message;
    
    public ValidationError(String message) {
        setMessage(message);
    }
    
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String toString() {
        return "Error: Sheet: " + message;
    }

}
