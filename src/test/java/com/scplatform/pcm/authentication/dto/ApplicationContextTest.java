/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.authentication.dto;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.Users;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationContextTest {

    @Test
    void defaultsAreSet() {
        ApplicationContext ctx = new ApplicationContext();
        assertEquals("login.jsp", ctx.getContentPageURL());
        assertNotNull(ctx.getCurrentTimezone(), "default timezone should be set");
        assertNotNull(ctx.getValidBusinessEntityKeys());
        assertTrue(ctx.getValidBusinessEntityKeys().isEmpty());
        assertNotNull(ctx.getDataFilterKeys());
        assertTrue(ctx.getDataFilterKeys().isEmpty());
        assertNotNull(ctx.getEffactiveDate());
        assertEquals("", ctx.getReportType());
        assertFalse(ctx.isAttritionRateAllowed());
        assertNull(ctx.getCurrentUser());
        assertNull(ctx.getCurrentRole());
        assertNull(ctx.getCurrentLocale());
        assertNull(ctx.getEnterpriseKey());
    }

    @Test
    void sessionAttrName_matchesClassName() {
        assertEquals(ApplicationContext.class.getName(), ApplicationContext.SESSION_ATTR_NAME);
    }

    @Test
    void implementsSerializable() {
        assertTrue(Serializable.class.isAssignableFrom(ApplicationContext.class));
    }

    @Test
    void settersAndGetters_userAndRole() {
        ApplicationContext ctx = new ApplicationContext();
        Users u = new Users();
        Role r = new Role();
        ctx.setCurrentUser(u);
        ctx.setCurrentRole(r);
        assertSame(u, ctx.getCurrentUser());
        assertSame(r, ctx.getCurrentRole());
    }

    @Test
    void settersAndGetters_localeAndTimezone() {
        ApplicationContext ctx = new ApplicationContext();
        ctx.setCurrentLocale(Locale.GERMAN);
        TimeZone tz = TimeZone.getTimeZone("UTC");
        ctx.setCurrentTimezone(tz);
        assertEquals(Locale.GERMAN, ctx.getCurrentLocale());
        assertSame(tz, ctx.getCurrentTimezone());
    }

    @Test
    void settersAndGetters_dateAndTimeFormat() {
        ApplicationContext ctx = new ApplicationContext();
        ctx.setCurrentDateFormat("yyyy-MM-dd");
        ctx.setCurrentTimeFormat("HH:mm");
        assertEquals("yyyy-MM-dd", ctx.getCurrentDateFormat());
        assertEquals("HH:mm", ctx.getCurrentTimeFormat());
    }

    @Test
    void settersAndGetters_enterpriseKeyAndAttrition() {
        ApplicationContext ctx = new ApplicationContext();
        ctx.setEnterpriseKey(42L);
        ctx.setAttritionRateAllowed(true);
        assertEquals(42L, ctx.getEnterpriseKey());
        assertTrue(ctx.isAttritionRateAllowed());
    }

    @Test
    void settersAndGetters_validBusinessEntityKeysAndEffactiveDate() {
        ApplicationContext ctx = new ApplicationContext();
        Set<Long> keys = new HashSet<>();
        keys.add(1L);
        keys.add(2L);
        ctx.setValidBusinessEntityKeys(keys);
        assertEquals(2, ctx.getValidBusinessEntityKeys().size());
        Date d = new Date(0L);
        ctx.setEffactiveDate(d);
        assertSame(d, ctx.getEffactiveDate());
    }

    @Test
    void settersAndGetters_reportTypeAndContentPageUrl() {
        ApplicationContext ctx = new ApplicationContext();
        ctx.setReportType("CSV");
        ctx.setContentPageURL("home.jsp");
        assertEquals("CSV", ctx.getReportType());
        assertEquals("home.jsp", ctx.getContentPageURL());
    }
}
