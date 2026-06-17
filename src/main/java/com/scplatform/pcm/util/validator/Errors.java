/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Errors {
    
    private List<ValidationError> errors;
    
    private int maximumErrorCount = -1;
    private boolean maxedOut = false;

    public Errors() {
        errors = new ArrayList<ValidationError>();
    }
    
    public int size() {
        return errors.size();
    }
    
    public boolean hasErrors() {
        return (errors.size() > 0);
    }
    
    public List<ValidationError> getObjectErrors() {
        List<ValidationError> objectErrors = new LinkedList<ValidationError>();
        for (ValidationError error : errors) {
            if (!(error instanceof CellValidationError)) {
                objectErrors.add(error);
            }
        }
        return Collections.unmodifiableList(objectErrors);
    }
    
    public List<CellValidationError> getCellErrors() {
        List<CellValidationError> cellErrors = new LinkedList<CellValidationError>();
        for (ValidationError error : errors) {
            if (error instanceof CellValidationError) {
                cellErrors.add((CellValidationError) error);
            }
        }
        return Collections.unmodifiableList(cellErrors);
    }
    
    public List<ValidationError> getAllErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    public List<String> getAllErrorMessages() {
        List<String> errorMessages = new LinkedList<String>();
        for (ValidationError error : errors) {
            errorMessages.add(error.toString());
        }
        
        return Collections.unmodifiableList(errorMessages);
    }
    
    protected String formatMessage(String messageKey, Object[] errorArgs,
            String defaultMessage) {
        
        StringBuilder buffer = new StringBuilder();
        
        String formattedMessage = Messages.getMessage(messageKey, errorArgs);
        if (null == formattedMessage) {
            if (null == defaultMessage) {
                buffer.append("??").append(messageKey);
                if (errorArgs != null) {
                    buffer.append("(");
                    for (Object arg : errorArgs) {
                        buffer.append("{").append(arg).append("}");
                    }
                    buffer.append(")");
                }
                
            } else {
                buffer.append(defaultMessage);
            }   

//            if (errorArgs != null) {
//                for (Object arg : errorArgs) {
//                    buffer.append("{").append(arg).append("}");
//                }
//            }
            
        } else {
            buffer.append(formattedMessage);
        }
        
        return buffer.toString();
    }
    
    public String addError(String messageKey) {
        return addError(messageKey, null, null);
    }
    
    public String addError(String messageKey, Object[] errorArgs) {
        return addError(messageKey, errorArgs, null);
    }
    
    public String addError(String messageKey, Object[] errorArgs,
            String defaultMessage) {

        String message = null;
        if (!checkMaximum()) {
            message = formatMessage(messageKey, errorArgs, defaultMessage); 
            errors.add(new ValidationError(message));
        }
        return message;
    }
    
    public String addErrorWithCustom(String messageKey, Object[] errorArgs, 
            String customMessage) {
        StringBuilder message = new StringBuilder();
        if (!checkMaximum()) {
            message.append(formatMessage(messageKey, errorArgs, null));
            message.append(" ");
            message.append(customMessage);
            errors.add(new ValidationError(message.toString()));
        }
        return message.toString();
    }
    
    public String addCellError(int row, int col, String messageKey) {
        return addCellError(row, col, messageKey, null, null);
    }
    
    public String addCellError(int row, int col, String messageKey, 
            Object[] errorArgs) {
        return addCellError(row, col, messageKey, errorArgs, null);
    }
    
    public String addCellError(int row, int col, String messageKey, 
            Object[] errorArgs, String defaultMessage) {

        String message = null;
        if (!checkMaximum()) {
            message = formatMessage(messageKey, errorArgs, defaultMessage); 
            errors.add(new CellValidationError(row, col, message));
        }
        return message;
    }
    
    public String addCellErrorWithCustom(int row, int col, String messageKey,
            Object[] errorArgs, String customMessage) {
        StringBuilder message = new StringBuilder();
        if (!checkMaximum()) {
            message.append(formatMessage(messageKey, errorArgs, null));
            message.append(" ");
            message.append(customMessage);
            errors.add(new CellValidationError(row, col, message.toString()));
        }
        return message.toString();
    }
    
    public void addErrors(Errors otherErrors) {
        errors.addAll(otherErrors.getAllErrors());
    }
    
    public int getMaximumErrorCount() {
        return maximumErrorCount;
    }

    public void setMaximumErrorCount(int maximumErrorCount) {
        this.maximumErrorCount = maximumErrorCount;
    }
    
    private boolean checkMaximum() {
        int max = getMaximumErrorCount();
        if (!maxedOut && (size() == max)) {
            String message = formatMessage(
                    "errors.too_many_errors",
                    new Object[]{max},
                    null);
            errors.add(new ValidationError(message));
            maxedOut = true;
        }
        
        return maxedOut;
    }
    
    public static void main(String[] args) {
        
        Errors e1 = new Errors();
        e1.addError("sample");
        e1.addError("sample1", new Object[]{"1"}, "default xyzzy");
        e1.addError("sample2", new Object[]{"1","2"}, "default xyzzy");
        e1.addError("sampleQQQ");
        e1.addError("sampleQQQ", new Object[]{"1"}, "default xyzzy");
        e1.addError("sampleQQQ", new Object[]{"1","2"}, "default xyzzy");
        e1.addError("sampleQQQ", new Object[]{"arg1","arg2"});
        System.out.println();
        System.out.println("Errors:");
        for (String error : e1.getAllErrorMessages()) {
            System.out.println(error);
        }
        
        Errors e2 = new Errors();
        e2.setMaximumErrorCount(2);
        e2.addError("sample");
        e2.addError("sample1", new Object[]{"1"}, "default xyzzy");
        e2.addError("sample2", new Object[]{"1","2"}, "default xyzzy");
        e2.addError("sampleQQQ");
        e2.addError("sampleQQQ", new Object[]{"1"}, "default xyzzy");
        System.out.println();
        System.out.println("Errors:");
        for (String error : e2.getAllErrorMessages()) {
            System.out.println(error);
        }
        
        System.out.println();        
        Errors e3 = new Errors();
        e3.setMaximumErrorCount(2);
        e3.addError("sample");
        e3.addError("sample1", new Object[]{"1"}, "default xyzzy");
        System.out.println();
        System.out.println("Errors:");
        for (String error : e3.getAllErrorMessages()) {
            System.out.println(error);
        }
        
    }
        
    
    

}
