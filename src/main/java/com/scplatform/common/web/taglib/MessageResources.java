/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;

public final class MessageResources {
    private static final Map<String, MessageResources> CACHE = new ConcurrentHashMap<>();

    private final ResourceBundleMessageSource messageSource;

    private MessageResources(String basename) {
        this.messageSource = new ResourceBundleMessageSource();
        this.messageSource.setBasename(basename);
        this.messageSource.setDefaultEncoding("UTF-8");
        this.messageSource.setFallbackToSystemLocale(false);
    }

    public static MessageResources getMessageResources(String basename) {
        return CACHE.computeIfAbsent(basename, MessageResources::new);
    }

    public String getMessage(String key, Object... args) {
        try {
            return this.messageSource.getMessage(key, args, resolveLocale(null));
        } catch (NoSuchMessageException exception) {
            return "???" + key + "???";
        }
    }

    public String getMessage(Locale locale, String key) {
        try {
            return this.messageSource.getMessage(key, null, resolveLocale(locale));
        } catch (NoSuchMessageException exception) {
            return null;
        }
    }

    public String getMessage(Locale locale, String key, Object... args) {
        try {
            return this.messageSource.getMessage(key, args, resolveLocale(locale));
        } catch (NoSuchMessageException exception) {
            return null;
        }
    }

    public boolean isPresent(Locale locale, String key) {
        return getMessage(locale, key) != null;
    }

    private Locale resolveLocale(Locale locale) {
        if (locale != null) {
            return locale;
        }

        Locale currentLocale = LocaleContextHolder.getLocale();
        return currentLocale != null ? currentLocale : Locale.getDefault();
    }
}