/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.web.view;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.JstlView;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CascadingJspViewResolverTest {

    @Test
    void constructor_setsDefaultsAndJstlViewClass() throws Exception {
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        CascadingJspViewResolver r = new CascadingJspViewResolver(cfg);
        // Reflectively read the protected accessors to verify defaults set in ctor
        java.lang.reflect.Method mPrefix = org.springframework.web.servlet.view.UrlBasedViewResolver.class
                .getDeclaredMethod("getPrefix");
        java.lang.reflect.Method mSuffix = org.springframework.web.servlet.view.UrlBasedViewResolver.class
                .getDeclaredMethod("getSuffix");
        java.lang.reflect.Method mViewClass = org.springframework.web.servlet.view.UrlBasedViewResolver.class
                .getDeclaredMethod("getViewClass");
        mPrefix.setAccessible(true);
        mSuffix.setAccessible(true);
        mViewClass.setAccessible(true);
        assertEquals("/WEB-INF/jsp/", mPrefix.invoke(r));
        assertEquals(".jsp", mSuffix.invoke(r));
        assertEquals(JstlView.class, mViewClass.invoke(r));
    }

    @Test
    void buildView_productDefault_returnsBaseView() throws Exception {
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(eq("pcm.customer"), eq("PCM"))).thenReturn("PCM");

        CascadingJspViewResolver r = new CascadingJspViewResolver(cfg);
        // Required to satisfy AbstractCachingViewResolver lifecycle (no Spring app context)
        r.setApplicationContext(new org.springframework.context.support.StaticApplicationContext());

        InternalResourceView v = r.buildView("login");
        assertNotNull(v);
        // No customer override -> URL is the product default
        assertEquals("/WEB-INF/jsp/login.jsp", v.getUrl());
    }

    @Test
    void buildView_customerWithoutResource_fallsBackToProduct() throws Exception {
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(eq("pcm.customer"), eq("PCM"))).thenReturn("acme");

        CascadingJspViewResolver r = new CascadingJspViewResolver(cfg);
        r.setApplicationContext(new org.springframework.context.support.StaticApplicationContext());

        // Without a ServletRequestAttributes set, resourceExists() returns false
        InternalResourceView v = r.buildView("main");
        assertEquals("/WEB-INF/jsp/main.jsp", v.getUrl());
    }

    @Test
    void buildView_blankCustomer_treatedAsDefault() throws Exception {
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(eq("pcm.customer"), eq("PCM"))).thenReturn("   ");

        CascadingJspViewResolver r = new CascadingJspViewResolver(cfg);
        r.setApplicationContext(new org.springframework.context.support.StaticApplicationContext());

        InternalResourceView v = r.buildView("home");
        assertEquals("/WEB-INF/jsp/home.jsp", v.getUrl());
    }

    @Test
    void buildView_configThrows_resolvedAsDefault() throws Exception {
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(eq("pcm.customer"), eq("PCM"))).thenThrow(new RuntimeException("db down"));

        CascadingJspViewResolver r = new CascadingJspViewResolver(cfg);
        r.setApplicationContext(new org.springframework.context.support.StaticApplicationContext());

        InternalResourceView v = r.buildView("foo");
        assertEquals("/WEB-INF/jsp/foo.jsp", v.getUrl());
    }

    @Test
    void buildView_stripsContentNegotiationExtension() throws Exception {
        PcmConfigUtil cfg = mock(PcmConfigUtil.class);
        when(cfg.getString(eq("pcm.customer"), eq("PCM"))).thenReturn("PCM");

        CascadingJspViewResolver r = new CascadingJspViewResolver(cfg);
        r.setApplicationContext(new org.springframework.context.support.StaticApplicationContext());

        InternalResourceView v = r.buildView("page.xml");
        assertEquals("/WEB-INF/jsp/page.jsp", v.getUrl());
    }
}
