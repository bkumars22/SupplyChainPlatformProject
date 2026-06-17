/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.common.dto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public interface FormFile {
    String getContentType();

    void setContentType(String arg0);

    int getFileSize();

    void setFileSize(int arg0);

    String getFileName();

    void setFileName(String arg0);

    byte[] getFileData() throws FileNotFoundException, IOException;

    InputStream getInputStream() throws FileNotFoundException, IOException;

    void destroy();
}