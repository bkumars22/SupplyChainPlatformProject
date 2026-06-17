/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TagUtils {
    private static final String APPLICATION_MESSAGES_BASENAME = "sc-messages";
    private static TagUtils instance = new TagUtils();
    private static final Log log;
    private static final MessageResources messages;
    private static final Map scopes;

    public static TagUtils getInstance() {
        return instance;
    }

    public static void setInstance(TagUtils instance) {
        TagUtils.instance = instance;
    }

    public Map computeParameters(PageContext pageContext, String paramId, String paramName, String paramProperty, String paramScope, String name, String property, String scope, boolean transaction) throws JspException {
        if (paramId == null && name == null && !transaction) {
            return null;
        } else {
            Map map = null;
            try {
                if (name != null) {
                    map = (Map) getInstance().lookup(pageContext, name, property, scope);
                }
            } catch (JspException arg17) {
                this.saveException(pageContext, arg17);
                throw arg17;
            }
            HashMap results = null;
            if (map != null) {
                results = new HashMap(map);
            } else {
                results = new HashMap();
            }
            String token;
            if (paramId != null && paramName != null) {
                Object session = null;
                try {
                    session = getInstance().lookup(pageContext, paramName, paramProperty, paramScope);
                } catch (JspException arg16) {
                    this.saveException(pageContext, arg16);
                    throw arg16;
                }
                if (session != null) {
                    token = null;
                    if (session instanceof String) {
                        token = (String) session;
                    } else {
                        token = session.toString();
                    }
                    Object mapValue = results.get(paramId);
                    if (mapValue == null) {
                        results.put(paramId, token);
                    } else {
                        String[] newValues;
                        if (mapValue instanceof String[]) {
                            newValues = (String[]) ((String[]) mapValue);
                            String[] newValues1 = new String[newValues.length + 1];
                            System.arraycopy(newValues, 0, newValues1, 0, newValues.length);
                            newValues1[newValues.length] = token;
                            results.put(paramId, newValues1);
                        } else {
                            newValues = new String[]{mapValue.toString(), token};
                            results.put(paramId, newValues);
                        }
                    }
                }
            }
            if (transaction) {
                HttpSession session1 = pageContext.getSession();
                token = null;
                if (session1 != null) {
                    token = (String) session1.getAttribute("com.test.controller.action.TOKEN");
                }
                if (token != null) {
                    results.put("com.scplatform.common.web.taglib.html.TOKEN", token);
                }
            }
            return results;
        }
    }

    public String computeURLWithCharEncoding(PageContext pageContext, String forward, String href, String page, String action, String module, Map params, String anchor, boolean redirect, boolean useLocalEncoding) throws MalformedURLException {
        return this.computeURLWithCharEncoding(pageContext, forward, href, page, action, module, params, anchor, redirect, true, useLocalEncoding);
    }

    public String computeURL(PageContext pageContext, String forward, String href, String page, String action, String module, Map params, String anchor, boolean redirect, boolean encodeSeparator) throws MalformedURLException {
        return this.computeURLWithCharEncoding(pageContext, forward, href, page, action, module, params, anchor, redirect, encodeSeparator, false);
    }

    public String computeURLWithCharEncoding(PageContext pageContext, String forward, String href, String page, String action, String module, Map params, String anchor, boolean redirect, boolean encodeSeparator, boolean useLocalEncoding) throws MalformedURLException {
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
        String charEncoding = "UTF-8";
        if (useLocalEncoding && response.getCharacterEncoding() != null && !response.getCharacterEncoding().isBlank()) {
            charEncoding = response.getCharacterEncoding();
        }
        int n = 0;
        if (forward != null) {
            ++n;
        }
        if (href != null) {
            ++n;
        }
        if (page != null) {
            ++n;
        }
        if (action != null) {
            ++n;
        }
        if (n != 1) {
            throw new MalformedURLException("Exactly one of forward, href, page, or action must be specified.");
        }
        String base = href != null ? href : action != null ? action : page != null ? page : forward;
        if (base == null) {
            throw new MalformedURLException("Unable to resolve a URL source.");
        }
        String trimmedBase = base.trim();
        boolean absoluteUrl = trimmedBase.matches("^[a-zA-Z][a-zA-Z0-9+.-]*:.*$") || trimmedBase.startsWith("//");
        if (!absoluteUrl && trimmedBase.startsWith("/")) {
            trimmedBase = request.getContextPath() + trimmedBase;
        }
        String fragment = null;
        int hashIndex = trimmedBase.indexOf('#');
        if (hashIndex >= 0) {
            fragment = trimmedBase.substring(hashIndex + 1);
            trimmedBase = trimmedBase.substring(0, hashIndex);
        }
        if (anchor != null) {
            fragment = anchor;
        }
        StringBuilder url = new StringBuilder(trimmedBase);
        String separator = redirect ? "&" : encodeSeparator ? "&amp;" : "&";
        boolean question = trimmedBase.indexOf('?') >= 0;
        if (params != null && !params.isEmpty()) {
            Iterator keys = params.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                Object value = params.get(key);
                String encodedKey;
                try {
                    encodedKey = URLEncoder.encode(key, charEncoding).replace("+", "%20");
                } catch (Exception exception) {
                    MalformedURLException malformedURLException = new MalformedURLException("Unable to encode URL parameter name: " + key);
                    malformedURLException.initCause(exception);
                    throw malformedURLException;
                }
                if (value instanceof String[]) {
                    String[] values = (String[]) value;
                    for (int i = 0; i < values.length; ++i) {
                        if (!question) {
                            url.append('?');
                            question = true;
                        } else {
                            url.append(separator);
                        }
                        url.append(encodedKey);
                        url.append('=');
                        if (values[i] != null) {
                            try {
                                url.append(URLEncoder.encode(values[i], charEncoding).replace("+", "%20"));
                            } catch (Exception exception) {
                                MalformedURLException malformedURLException = new MalformedURLException("Unable to encode URL parameter value for: " + key);
                                malformedURLException.initCause(exception);
                                throw malformedURLException;
                            }
                        }
                    }
                } else {
                    if (!question) {
                        url.append('?');
                        question = true;
                    } else {
                        url.append(separator);
                    }
                    url.append(encodedKey);
                    url.append('=');
                    if (value != null) {
                        try {
                            url.append(URLEncoder.encode(value.toString(), charEncoding).replace("+", "%20"));
                        } catch (Exception exception) {
                            MalformedURLException malformedURLException = new MalformedURLException("Unable to encode URL parameter value for: " + key);
                            malformedURLException.initCause(exception);
                            throw malformedURLException;
                        }
                    }
                }
            }
        }
        if (fragment != null && !fragment.isBlank()) {
            try {
                url.append('#');
                url.append(URLEncoder.encode(fragment, charEncoding).replace("+", "%20"));
            } catch (Exception exception) {
                MalformedURLException malformedURLException = new MalformedURLException("Unable to encode URL fragment.");
                malformedURLException.initCause(exception);
                throw malformedURLException;
            }
        }
        String finalUrl = url.toString();
        if (href == null && pageContext.getSession() != null) {
            return redirect ? response.encodeRedirectURL(finalUrl) : response.encodeURL(finalUrl);
        }
        return finalUrl;
    }

    public String encodeURL(String url) {
        return this.encodeURL(url, "UTF-8");
    }

    public String encodeURL(String url, String enc) {
        if (url == null) {
            return null;
        }
        try {
            return URLEncoder.encode(url, enc).replace("+", "%20");
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to encode URL value.", exception);
        }
    }

    public String filter(String value) {
        if (value == null) {
            return null;
        }
        StringBuilder filtered = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            switch (ch) {
                case '&':
                    filtered.append("&amp;");
                    break;
                case '<':
                    filtered.append("&lt;");
                    break;
                case '>':
                    filtered.append("&gt;");
                    break;
                case '"':
                    filtered.append("&quot;");
                    break;
                default:
                    filtered.append(ch);
                    break;
            }
        }
        return filtered.toString();
    }

    public UiMessages getMessages(PageContext pageContext, String paramName) throws JspException {
        Object value = pageContext.findAttribute(paramName);
        if (value != null) {
            try {
                return UiMessages.from(pageContext, value, messages);
            } catch (JspException arg6) {
                throw arg6;
            } catch (Exception arg7) {
                log.warn("Unable to retrieve messages for paramName : " + paramName, arg7);
            }
        }
        return new UiMessages();
    }

    public int getScope(String scopeName) throws JspException {
        Integer scope = (Integer) scopes.get(scopeName.toLowerCase());
        if (scope == null) {
            throw new JspException(messages.getMessage("lookup.scope", scope));
        } else {
            return scope.intValue();
        }
    }

    public Locale getUserLocale(PageContext pageContext, String locale) {
        if (locale != null) {
            Object localeAttribute = pageContext.findAttribute(locale);
            if (localeAttribute instanceof Locale) {
                return (Locale) localeAttribute;
            }
            if (localeAttribute instanceof String && !((String) localeAttribute).isBlank()) {
                return Locale.forLanguageTag(((String) localeAttribute).replace('_', '-'));
            }
        }
        Locale requestLocale = ((HttpServletRequest) pageContext.getRequest()).getLocale();
        return requestLocale != null ? requestLocale : Locale.getDefault();
    }

    public boolean isXhtml(PageContext pageContext) {
        String xhtml = (String) pageContext.getAttribute("com.test.controller.globals.XHTML", 1);
        return "true".equalsIgnoreCase(xhtml);
    }

    public Object lookup(PageContext pageContext, String name, String scopeName) throws JspException {
        if (scopeName == null) {
            return pageContext.findAttribute(name);
        } else {
            try {
                return pageContext.getAttribute(name, instance.getScope(scopeName));
            } catch (JspException arg4) {
                this.saveException(pageContext, arg4);
                throw arg4;
            }
        }
    }

    public Object lookup(PageContext pageContext, String name, String property, String scope) throws JspException {
        Object bean = this.lookup(pageContext, name, scope);
        if (bean == null) {
            JspException e = null;
            if (scope == null) {
                e = new JspException(messages.getMessage("lookup.bean.any", name));
            } else {
                e = new JspException(messages.getMessage("lookup.bean", name, scope));
            }
            this.saveException(pageContext, e);
            throw e;
        } else if (property == null) {
            return bean;
        } else {
            try {
                return PropertyUtils.getProperty(bean, property);
            } catch (IllegalAccessException arg8) {
                this.saveException(pageContext, arg8);
                throw new JspException(messages.getMessage("lookup.access", property, name));
            } catch (IllegalArgumentException arg9) {
                this.saveException(pageContext, arg9);
                throw new JspException(messages.getMessage("lookup.argument", property, name));
            } catch (InvocationTargetException arg10) {
                Object beanName1 = arg10.getTargetException();
                if (beanName1 == null) {
                    beanName1 = arg10;
                }
                this.saveException(pageContext, (Throwable) beanName1);
                throw new JspException(messages.getMessage("lookup.target", property, name));
            } catch (NoSuchMethodException arg11) {
                this.saveException(pageContext, arg11);
                String beanName = name;
                if ("com.scplatform.common.web.taglib.html.BEAN".equals(name)) {
                    Object obj = pageContext.findAttribute("com.scplatform.common.web.taglib.html.BEAN");
                    if (obj != null) {
                        beanName = obj.getClass().getName();
                    }
                }
                throw new JspException(messages.getMessage("lookup.method", property, beanName));
            }
        }
    }

    public String message(PageContext pageContext, String bundle, String locale, String key) throws JspException {
        return this.message(pageContext, bundle, locale, key, (Object[]) null);
    }

    public String message(PageContext pageContext, String bundle, String locale, String key, Object[] args) throws JspException {
        MessageResources resources = this.retrieveMessageResources(pageContext, bundle, false);
        Locale userLocale = this.getUserLocale(pageContext, locale);
        String message = null;
        if (args == null) {
            message = resources.getMessage(userLocale, key);
        } else {
            message = resources.getMessage(userLocale, key, args);
        }
        if (message == null && log.isDebugEnabled()) {
            log.debug(resources.getMessage("message.resources", key, bundle, locale));
        }
        return message;
    }

    public boolean present(PageContext pageContext, String bundle, String locale, String key) throws JspException {
        MessageResources resources = this.retrieveMessageResources(pageContext, bundle, true);
        Locale userLocale = this.getUserLocale(pageContext, locale);
        return resources.isPresent(userLocale, key);
    }

    public MessageResources retrieveMessageResources(PageContext pageContext, String bundle, boolean checkPageScope) throws JspException {
        return MessageResources.getMessageResources(APPLICATION_MESSAGES_BASENAME);
    }

    public void saveException(PageContext pageContext, Throwable exception) {
        pageContext.setAttribute("com.test.controller.action.EXCEPTION", exception, 2);
    }

    public void write(PageContext pageContext, String text) throws JspException {
        JspWriter writer = pageContext.getOut();
        try {
            writer.print(text);
        } catch (IOException arg4) {
            this.saveException(pageContext, arg4);
            throw new JspException(messages.getMessage("write.io", arg4.toString()));
        }
    }

    public void writePrevious(PageContext pageContext, String text) throws JspException {
        JspWriter writer = pageContext.getOut();
        if (writer instanceof BodyContent) {
            writer = ((BodyContent) writer).getEnclosingWriter();
        }
        try {
            writer.print(text);
        } catch (IOException arg4) {
            this.saveException(pageContext, arg4);
            throw new JspException(messages.getMessage("write.io", arg4.toString()));
        }
    }

    static {
        log = LogFactory.getLog(TagUtils.class);
        messages = MessageResources.getMessageResources("com.scplatform.common.web.LocalStrings");
        scopes = new HashMap();
        scopes.put("page", Integer.valueOf(1));
        scopes.put("request", Integer.valueOf(2));
        scopes.put("session", Integer.valueOf(3));
        scopes.put("application", Integer.valueOf(4));
    }
}