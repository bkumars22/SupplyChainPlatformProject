/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.launchpad.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class HeaderTest {

    @Test
    void defaultsAreNull() {
        Header h = new Header();
        assertNull(h.getLogo());
        assertNull(h.getBanner());
        assertNull(h.getHelp());
        assertNull(h.getUser());
        assertNull(h.getMenu());
        assertNotNull(h.getAdditionalProperties());
    }

    @Test
    void componentSettersRetainReferences() {
        Header h = new Header();
        Logo logo = new Logo();
        Banner banner = new Banner();
        Help help = new Help();
        User user = new User();
        Menu menu = new Menu();

        h.setLogo(logo);
        h.setBanner(banner);
        h.setHelp(help);
        h.setUser(user);
        h.setMenu(menu);

        assertSame(logo, h.getLogo());
        assertSame(banner, h.getBanner());
        assertSame(help, h.getHelp());
        assertSame(user, h.getUser());
        assertSame(menu, h.getMenu());
    }

    @Test
    void additionalPropertyStored() {
        Header h = new Header();
        h.setAdditionalProperty("k", 1);
        assertEquals(1, h.getAdditionalProperties().get("k"));
    }
}
