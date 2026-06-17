/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class HeaderWrapTest {

    @Test
    void defaultHeaderIsNull() {
        HeaderWrap wrap = new HeaderWrap();
        assertNull(wrap.getHeader());
    }

    @Test
    void setHeaderRetainsReference() {
        HeaderWrap wrap = new HeaderWrap();
        Header h = new Header();
        wrap.setHeader(h);
        assertSame(h, wrap.getHeader());
    }
}
