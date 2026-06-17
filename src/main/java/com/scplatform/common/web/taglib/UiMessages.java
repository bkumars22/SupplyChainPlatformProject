/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.scplatform.pcm.util.message.SCPlatformMessages;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

public class UiMessages implements Serializable {
    public static final String GLOBAL_MESSAGE = "com.scplatform.pcm.GLOBAL_MESSAGE";
    public static final String ERROR_ATTRIBUTE = "com.scplatform.pcm.ERROR";
    public static final String SUCCESS_MESSAGE = "com.scplatform.pcm.SUCCESS_MESSAGE";
    public static final String DEFAULT_LOCALE_KEY = "com.scplatform.pcm.LOCALE";

    private final Map<String, List<String>> messagesByProperty = new LinkedHashMap<>();

	public static UiMessages from(PageContext pageContext, Object value, MessageResources messages) throws JspException {
	return from(value, resolveLocale(pageContext, DEFAULT_LOCALE_KEY), messages);
    }

    public static UiMessages from(Object value, Locale locale, MessageResources messages) throws JspException {
	UiMessages resolvedMessages = new UiMessages();
	if (value == null) {
	    return resolvedMessages;
	}

	if (value instanceof UiMessages) {
	    return (UiMessages) value;
	}

	if (value instanceof String) {
	    resolvedMessages.addGlobal(resolveMessageText((String) value, locale));
	    return resolvedMessages;
	}

	if (value instanceof String[]) {
	    for (String message : (String[]) value) {
		resolvedMessages.addGlobal(resolveMessageText(message, locale));
	    }
	    return resolvedMessages;
	}

	if (value instanceof Collection<?>) {
	    for (Object item : (Collection<?>) value) {
		if (!(item instanceof String)) {
		    throw new JspException(messages.getMessage("actionMessages.errors", item.getClass().getName()));
		}

		resolvedMessages.addGlobal(resolveMessageText((String) item, locale));
	    }
	    return resolvedMessages;
	}

	if (value instanceof Map<?, ?>) {
	    Map<?, ?> map = (Map<?, ?>) value;
	    for (Map.Entry<?, ?> entry : map.entrySet()) {
			String property = entry.getKey() != null ? entry.getKey().toString() : GLOBAL_MESSAGE;
			Object entryValue = entry.getValue();
			if (entryValue instanceof Collection<?>) {
				for (Object item : (Collection<?>) entryValue) {
					resolvedMessages.add(property, resolveMessageText(item.toString(), locale));
				}
			} else if (entryValue instanceof String) {
				resolvedMessages.add(property, resolveMessageText((String) entryValue, locale));
			} else if (entryValue != null) {
				resolvedMessages.add(property, resolveMessageText(entryValue.toString(), locale));
			}
	    }
	    return resolvedMessages;
	}

	throw new JspException(messages.getMessage("actionMessages.errors", value.getClass().getName()));
    }

    public void add(String property, String message) {
	if (message == null) {
	    return;
	}

	messagesByProperty.computeIfAbsent(normalizeProperty(property), key -> new ArrayList<>()).add(message);
    }

    public void addGlobal(String message) {
	add(GLOBAL_MESSAGE, message);
    }

    public void addAll(String property, Collection<String> messages) {
	if (messages == null) {
	    return;
	}

	for (String message : messages) {
	    add(property, message);
	}
    }

    public void addGlobalKey(String key, Locale locale, Object... args) {
	addGlobal(resolveKey(key, locale, args));
    }

    public void addKey(String property, String key, Locale locale, Object... args) {
	add(property, resolveKey(key, locale, args));
    }

    public Iterator<String> get() {
	return getMessages().iterator();
    }

    public Iterator<String> get(String property) {
	return getMessages(property).iterator();
    }

    public List<String> getMessages() {
	List<String> messages = new ArrayList<>();
	for (List<String> values : messagesByProperty.values()) {
	    messages.addAll(values);
	}

	return messages;
    }

    public List<String> getMessages(String property) {
	List<String> messages = messagesByProperty.get(normalizeProperty(property));
	if (messages == null) {
	    return Collections.emptyList();
	}

	return new ArrayList<>(messages);
    }

    public boolean isEmpty() {
	return messagesByProperty.isEmpty() || size() == 0;
    }

	public void clear() {
	messagesByProperty.clear();
	}

    public int size() {
	int size = 0;
	for (List<String> values : messagesByProperty.values()) {
	    size += values.size();
	}

	return size;
    }

    public int size(String property) {
	return getMessages(property).size();
    }

    private String normalizeProperty(String property) {
	return property == null ? GLOBAL_MESSAGE : property;
    }

    private String resolveKey(String key, Locale locale, Object... args) {
	String resolved = SCPlatformMessages.INSTANCE.getMessage(key, args, locale);
	return resolved.equals("???" + key + "???") ? key : resolved;
    }

    private static String resolveMessageText(String keyOrText, Locale locale) {
	String resolved = SCPlatformMessages.INSTANCE.getMessage(keyOrText, null, locale);
	return resolved.equals("???" + keyOrText + "???") ? keyOrText : resolved;
    }

    private static Locale resolveLocale(PageContext pageContext, String localeKey) {
	Object localeAttribute = pageContext.findAttribute(localeKey);
	if (localeAttribute instanceof Locale) {
	    return (Locale) localeAttribute;
	}

	if (localeAttribute instanceof String localeString && !localeString.isBlank()) {
	    return Locale.forLanguageTag(localeString.replace('_', '-'));
	}

	Locale requestLocale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
	return requestLocale != null ? requestLocale : Locale.getDefault();
    }
}