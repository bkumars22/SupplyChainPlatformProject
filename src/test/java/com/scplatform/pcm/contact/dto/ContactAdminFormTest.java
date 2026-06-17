/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.contact.dto;

import com.scplatform.pcm.contact.entity.Contact;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ContactAdminFormTest {

    @Test
    void defaultsAreInitialized() {
        ContactAdminForm f = new ContactAdminForm();
        assertNull(f.getSelectedContactKey());
        assertNull(f.getSelectedContact());
        assertNull(f.getSelectedBusinessKey());
        assertNotNull(f.getBusinessContacts());
        assertTrue(f.getBusinessContacts().isEmpty());
        assertFalse(f.isGoInit());
    }

    @Test
    void settersAndGetters() {
        ContactAdminForm f = new ContactAdminForm();
        Contact c = new Contact(7L);
        Map<String, Object> m = new HashMap<>();
        m.put("k", "v");
        f.setSelectedContactKey("CK");
        f.setSelectedContact(c);
        f.setSelectedBusinessKey("BK");
        f.setBusinessContacts(m);
        f.setGoInit(true);

        assertEquals("CK", f.getSelectedContactKey());
        assertSame(c, f.getSelectedContact());
        assertEquals("BK", f.getSelectedBusinessKey());
        assertSame(m, f.getBusinessContacts());
        assertTrue(f.isGoInit());
    }

    @Test
    void equalsAndHashCode_lombok() {
        ContactAdminForm a = new ContactAdminForm();
        a.setSelectedContactKey("K");
        ContactAdminForm b = new ContactAdminForm();
        b.setSelectedContactKey("K");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
}
