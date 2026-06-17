/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.bean;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;
import com.scplatform.common.web.taglib.MessageResources;
import com.scplatform.common.web.taglib.TagUtils;

public class IncludeTag extends TagSupport {
    protected static final int BUFFER_SIZE = 256;
    protected static MessageResources messages = MessageResources.getMessageResources("com.scplatform.common.web.taglib.bean.LocalStrings");
    protected String anchor = null;
    protected String forward = null;
    protected String href = null;
    protected String id = null;
    protected String page = null;
    protected boolean transaction = false;
    protected boolean useLocalEncoding = false;

    public String getAnchor() {
        return this.anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getForward() {
        return this.forward;
    }

    public void setForward(String forward) {
        this.forward = forward;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPage() {
        return this.page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public boolean getTransaction() {
        return this.transaction;
    }

    public void setTransaction(boolean transaction) {
        this.transaction = transaction;
    }

    public boolean isUseLocalEncoding() {
        return this.useLocalEncoding;
    }

    public void setUseLocalEncoding(boolean b) {
        this.useLocalEncoding = b;
    }

    public int doStartTag() throws JspException {
        Map params = TagUtils.getInstance().computeParameters(this.pageContext, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, this.transaction);
        String urlString = null;
        URL url = null;
        HttpServletRequest conn;
        try {
            urlString = TagUtils.getInstance().computeURLWithCharEncoding(this.pageContext, this.forward, this.href, this.page, (String) null, (String) null, params, this.anchor, false, this.useLocalEncoding);
            if (urlString.indexOf(58) < 0) {
                conn = (HttpServletRequest) this.pageContext.getRequest();
                url = buildRequestUrl(conn).resolve(urlString).toURL();
            } else {
                url = new URI(urlString).toURL();
            }
        } catch (MalformedURLException | URISyntaxException arg10) {
            TagUtils.getInstance().saveException(this.pageContext, arg10);
            throw new JspException(messages.getMessage("include.url", arg10.toString()));
        }
        conn = null;
        URLConnection conn1;
        try {
            conn1 = url.openConnection();
            conn1.setAllowUserInteraction(false);
            conn1.setDoInput(true);
            conn1.setDoOutput(false);
            HttpServletRequest sb = (HttpServletRequest) this.pageContext.getRequest();
            this.addCookie(conn1, urlString, sb);
            conn1.connect();
        } catch (Exception arg9) {
            TagUtils.getInstance().saveException(this.pageContext, arg9);
            throw new JspException(messages.getMessage("include.open", url.toString(), arg9.toString()));
        }
        StringBuffer sb1 = new StringBuffer();
        try {
            BufferedInputStream e = new BufferedInputStream(conn1.getInputStream());
            InputStreamReader in = new InputStreamReader(e);
            char[] buffer = new char[256];
            boolean n = false;
            while (true) {
                int n1 = in.read(buffer);
                if (n1 < 1) {
                    in.close();
                    break;
                }
                sb1.append(buffer, 0, n1);
            }
        } catch (Exception arg11) {
            TagUtils.getInstance().saveException(this.pageContext, arg11);
            throw new JspException(messages.getMessage("include.read", url.toString(), arg11.toString()));
        }
        this.pageContext.setAttribute(this.id, sb1.toString());
        return 0;
    }

    protected URI buildRequestUrl(HttpServletRequest request) throws URISyntaxException {
        return new URI(request.getRequestURL().toString());
    }

    protected void addCookie(URLConnection conn, String urlString, HttpServletRequest request) {
        if (conn instanceof HttpURLConnection && urlString.startsWith(request.getContextPath()) && request.getRequestedSessionId() != null && request.isRequestedSessionIdFromCookie()) {
            StringBuffer sb = new StringBuffer("JSESSIONID=");
            sb.append(request.getRequestedSessionId());
            conn.setRequestProperty("Cookie", sb.toString());
        }
    }

    public void release() {
        super.release();
        this.anchor = null;
        this.forward = null;
        this.href = null;
        this.id = null;
        this.page = null;
        this.transaction = false;
    }
}