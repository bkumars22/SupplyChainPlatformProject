/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.upload.loader;

import java.text.MessageFormat;

public class MessageLoaderException extends Exception {

    private Object[] details = null;
    private boolean isSoft = false;

    public MessageLoaderException() {
    }

    public MessageLoaderException(String message) {
        super(message);
    }

    public MessageLoaderException(String message, Object detail) {
        super(message);
        this.details = new Object[]{detail};
    }

    public MessageLoaderException(String message, Object[] details) {
        super(message);
        this.details = details;
    }

    public MessageLoaderException(Throwable cause) {
        super(cause);
    }

    public MessageLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageLoaderException(String message, Object detail, Throwable cause) {
        super(message, cause);
        this.details = new Object[]{detail};
    }

    public MessageLoaderException(String message, Object[] details, Throwable cause) {
        super(message, cause);
        this.details = details;
    }

    /**
     * Returns the formatted message.
     * If {@code details} were provided, formats the message pattern using {@link MessageFormat}.
     */
    @Override
    public String getMessage() {
        if (details != null) {
            return MessageFormat.format(super.getMessage(), details);
        }
        return super.getMessage();
    }

    /** Returns true if this is a soft (non-fatal) error that may be recoverable. */
    public boolean isSoft() {
        return isSoft;
    }

    public void setSoft(boolean isSoft) {
        this.isSoft = isSoft;
    }
}
