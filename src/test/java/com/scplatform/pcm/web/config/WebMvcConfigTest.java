/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.web.config;

import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.web.view.CascadingJspViewResolver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

class WebMvcConfigTest {

    @Test
    void jspViewResolver_returnsCascadingResolver() {
        WebMvcConfig cfg = new WebMvcConfig();
        ViewResolver resolver = cfg.jspViewResolver(mock(PcmConfigUtil.class));
        assertNotNull(resolver);
        assertTrue(resolver instanceof CascadingJspViewResolver);
    }

    @Test
    void addResourceHandlers_registersFourHandlers() {
        WebMvcConfig cfg = new WebMvcConfig();
        ResourceHandlerRegistry registry = mock(
                ResourceHandlerRegistry.class,
                org.mockito.Mockito.RETURNS_DEEP_STUBS);
        cfg.addResourceHandlers(registry);
        verify(registry, times(4)).addResourceHandler(anyString());
    }

    @Test
    void webServerCustomizer_isNonNull() {
        WebMvcConfig cfg = new WebMvcConfig();
        WebServerFactoryCustomizer<TomcatServletWebServerFactory> c = cfg.webServerCustomizer();
        assertNotNull(c);
    }

    @Test
    void webServerCustomizer_appliesWithoutThrowing() {
        WebMvcConfig cfg = new WebMvcConfig();
        TomcatServletWebServerFactory factory = mock(TomcatServletWebServerFactory.class);
        assertDoesNotThrow(() -> cfg.webServerCustomizer().customize(factory));
        // setDocumentRoot is called only when webapp dir is found; just verify addContextCustomizers is invoked
        verify(factory).addContextCustomizers(any());
    }
}
