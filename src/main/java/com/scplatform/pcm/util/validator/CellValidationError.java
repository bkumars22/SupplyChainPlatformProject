/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.util.validator;

public class CellValidationError extends ValidationError {
    
    int row;
    int column;
    String value;
    
    public CellValidationError(int row, int column, String message) {
        super(message);
        setRow(row);
        setColumn(column);
    }
    
    public String toString() {
        return "Error: Cell (row=" + (1+getRow()) + ",col=" + (1+getColumn()) 
                + "): " + getMessage();
    }
    
    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getColumn() {
        return column;
    }
    public void setColumn(int column) {
        this.column = column;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    
}
