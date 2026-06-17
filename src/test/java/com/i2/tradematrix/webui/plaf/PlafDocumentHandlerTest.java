/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.plaf;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlafDocumentHandlerTest {

    /** Minimal valid plaf XML — <plaf><skins><defaults/></skins></plaf> */
    private static final String VALID_XML =
            "<?xml version=\"1.0\"?><plaf><skins><defaults/></skins></plaf>";

    private PlafDocumentHandler plafDocumentHandler;

    @BeforeEach
    void setUp() throws PlafException {
        InputStream is = new ByteArrayInputStream(VALID_XML.getBytes(StandardCharsets.UTF_8));
        plafDocumentHandler = new PlafDocumentHandler(is, "/ctx");
    }

    @Test
    void test_constructedSuccessfully() {
        assertNotNull(plafDocumentHandler);
    }

    @Test
    void test_getSkins_returnsNonNull() {
        Skins result = plafDocumentHandler.getSkins();
        assertNotNull(result);
    }

    @Test
    void test_setConfigFile_doesNotThrow() {
        plafDocumentHandler.setConfigFile("skin-config.xml");
        assertNotNull(plafDocumentHandler);
    }

    @Test
    void test_reload_withoutConfigFile_throwsPlafException() {
        // configFile is null when constructed from InputStream — reload() must throw
        assertThrows(PlafException.class, () -> plafDocumentHandler.reload());
    }

    @Test
    void test_constructor_invalidXml_throwsPlafException() {
        InputStream bad = new ByteArrayInputStream("NOT_XML".getBytes(StandardCharsets.UTF_8));
        assertThrows(PlafException.class, () -> new PlafDocumentHandler(bad, "/ctx"));
    }
}
