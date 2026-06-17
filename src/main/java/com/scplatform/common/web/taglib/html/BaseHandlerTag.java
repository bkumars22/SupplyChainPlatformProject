/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.common.web.taglib.html;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import jakarta.servlet.jsp.tagext.Tag;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.scplatform.common.web.taglib.TagUtils;
import com.scplatform.common.web.taglib.UiMessages;
import com.scplatform.common.web.taglib.logic.IterateTag;
import com.scplatform.common.web.taglib.MessageResources;

public abstract class BaseHandlerTag extends BodyTagSupport {
    private static Log log;
    protected static MessageResources messages;
    protected String accesskey = null;
    protected String tabindex = null;
    protected boolean indexed = false;
    private String onclick = null;
    private String ondblclick = null;
    private String onmouseover = null;
    private String onmouseout = null;
    private String onmousemove = null;
    private String onmousedown = null;
    private String onmouseup = null;
    private String onkeydown = null;
    private String onkeyup = null;
    private String onkeypress = null;
    private String onselect = null;
    private String onchange = null;
    private String onblur = null;
    private String onfocus = null;
    private boolean disabled = false;
    protected boolean doDisabled = true;
    private boolean readonly = false;
    protected boolean doReadonly = false;
    private String style = null;
    private String styleClass = null;
    private String styleId = null;
    private String errorKey = "com.test.controller.action.ERROR";
    private String errorStyle = null;
    private String errorStyleClass = null;
    private String errorStyleId = null;
    private String alt = null;
    private String altKey = null;
    private String bundle = null;
    private String locale = "com.test.controller.action.LOCALE";
    private String title = null;
    private String lang = null;
    private String dir = null;
    private String titleKey = null;
    private Class loopTagClass = null;
    private Method loopTagGetStatus = null;
    private Class loopTagStatusClass = null;
    private Method loopTagStatusGetIndex = null;
    private boolean triedJstlInit = false;
    private boolean triedJstlSuccess = false;

    public void setAccesskey(String accessKey) {
        this.accesskey = accessKey;
    }

    public String getAccesskey() {
        return this.accesskey;
    }

    public void setTabindex(String tabIndex) {
        this.tabindex = tabIndex;
    }

    public String getTabindex() {
        return this.tabindex;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public boolean getIndexed() {
        return this.indexed;
    }

    public void setOnclick(String onClick) {
        this.onclick = onClick;
    }

    public String getOnclick() {
        return this.onclick;
    }

    public void setOndblclick(String onDblClick) {
        this.ondblclick = onDblClick;
    }

    public String getOndblclick() {
        return this.ondblclick;
    }

    public void setOnmousedown(String onMouseDown) {
        this.onmousedown = onMouseDown;
    }

    public String getOnmousedown() {
        return this.onmousedown;
    }

    public void setOnmouseup(String onMouseUp) {
        this.onmouseup = onMouseUp;
    }

    public String getOnmouseup() {
        return this.onmouseup;
    }

    public void setOnmousemove(String onMouseMove) {
        this.onmousemove = onMouseMove;
    }

    public String getOnmousemove() {
        return this.onmousemove;
    }

    public void setOnmouseover(String onMouseOver) {
        this.onmouseover = onMouseOver;
    }

    public String getOnmouseover() {
        return this.onmouseover;
    }

    public void setOnmouseout(String onMouseOut) {
        this.onmouseout = onMouseOut;
    }

    public String getOnmouseout() {
        return this.onmouseout;
    }

    public void setOnkeydown(String onKeyDown) {
        this.onkeydown = onKeyDown;
    }

    public String getOnkeydown() {
        return this.onkeydown;
    }

    public void setOnkeyup(String onKeyUp) {
        this.onkeyup = onKeyUp;
    }

    public String getOnkeyup() {
        return this.onkeyup;
    }

    public void setOnkeypress(String onKeyPress) {
        this.onkeypress = onKeyPress;
    }

    public String getOnkeypress() {
        return this.onkeypress;
    }

    public void setOnchange(String onChange) {
        this.onchange = onChange;
    }

    public String getOnchange() {
        return this.onchange;
    }

    public void setOnselect(String onSelect) {
        this.onselect = onSelect;
    }

    public String getOnselect() {
        return this.onselect;
    }

    public void setOnblur(String onBlur) {
        this.onblur = onBlur;
    }

    public String getOnblur() {
        return this.onblur;
    }

    public void setOnfocus(String onFocus) {
        this.onfocus = onFocus;
    }

    public String getOnfocus() {
        return this.onfocus;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean getDisabled() {
        return this.disabled;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }

    public boolean getReadonly() {
        return this.readonly;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public String getStyleId() {
        return this.styleId;
    }

    public String getErrorKey() {
        return this.errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    public String getErrorStyle() {
        return this.errorStyle;
    }

    public void setErrorStyle(String errorStyle) {
        this.errorStyle = errorStyle;
    }

    public String getErrorStyleClass() {
        return this.errorStyleClass;
    }

    public void setErrorStyleClass(String errorStyleClass) {
        this.errorStyleClass = errorStyleClass;
    }

    public String getErrorStyleId() {
        return this.errorStyleId;
    }

    public void setErrorStyleId(String errorStyleId) {
        this.errorStyleId = errorStyleId;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getAltKey() {
        return this.altKey;
    }

    public void setAltKey(String altKey) {
        this.altKey = altKey;
    }

    public String getBundle() {
        return this.bundle;
    }

    public void setBundle(String bundle) {
        this.bundle = bundle;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleKey() {
        return this.titleKey;
    }

    public void setTitleKey(String titleKey) {
        this.titleKey = titleKey;
    }

    public String getLang() {
        return this.lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getDir() {
        return this.dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void release() {
        super.release();
        this.accesskey = null;
        this.alt = null;
        this.altKey = null;
        this.bundle = null;
        this.dir = null;
        this.errorKey = "com.test.controller.action.ERROR";
        this.errorStyle = null;
        this.errorStyleClass = null;
        this.errorStyleId = null;
        this.indexed = false;
        this.lang = null;
        this.locale = "com.test.controller.action.LOCALE";
        this.onclick = null;
        this.ondblclick = null;
        this.onmouseover = null;
        this.onmouseout = null;
        this.onmousemove = null;
        this.onmousedown = null;
        this.onmouseup = null;
        this.onkeydown = null;
        this.onkeyup = null;
        this.onkeypress = null;
        this.onselect = null;
        this.onchange = null;
        this.onblur = null;
        this.onfocus = null;
        this.disabled = false;
        this.readonly = false;
        this.style = null;
        this.styleClass = null;
        this.styleId = null;
        this.tabindex = null;
        this.title = null;
        this.titleKey = null;
    }

    protected String message(String literal, String key) throws JspException {
        if (literal != null) {
            if (key != null) {
                JspException e = new JspException(messages.getMessage("common.both"));
                TagUtils.getInstance().saveException(this.pageContext, e);
                throw e;
            } else {
                return literal;
            }
        } else {
            return key != null ? TagUtils.getInstance().message(this.pageContext, this.getBundle(), this.getLocale(), key) : null;
        }
    }

    @SuppressWarnings("unchecked")
    private Integer getJstlLoopIndex() {
        if (!this.triedJstlInit) {
            this.triedJstlInit = true;
            try {
                this.loopTagClass = loadApplicationClass("jakarta.servlet.jsp.jstl.core.LoopTag");
                this.loopTagGetStatus = this.loopTagClass.getDeclaredMethod("getLoopStatus", (Class[]) null);
                this.loopTagStatusClass = loadApplicationClass("jakarta.servlet.jsp.jstl.core.LoopTagStatus");
                this.loopTagStatusGetIndex = this.loopTagStatusClass.getDeclaredMethod("getIndex", (Class[]) null);
                this.triedJstlSuccess = true;
            } catch (ClassNotFoundException arg7) {
                ;
            } catch (NoSuchMethodException arg8) {
                ;
            }
        }
        if (this.triedJstlSuccess) {
            try {
                Tag ex = findAncestorWithClass(this, this.loopTagClass);
                if (ex == null) {
                    return null;
                }
                Object status = this.loopTagGetStatus.invoke(ex, (Object[]) null);
                return (Integer) this.loopTagStatusGetIndex.invoke(status, (Object[]) null);
            } catch (IllegalAccessException arg2) {
                log.error(arg2.getMessage(), arg2);
            } catch (IllegalArgumentException arg3) {
                log.error(arg3.getMessage(), arg3);
            } catch (InvocationTargetException arg4) {
                log.error(arg4.getMessage(), arg4);
            } catch (NullPointerException arg5) {
                log.error(arg5.getMessage(), arg5);
            } catch (ExceptionInInitializerError arg6) {
                log.error(arg6.getMessage(), arg6);
            }
        }
        return null;
    }

    private Class<?> loadApplicationClass(String className) throws ClassNotFoundException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
            return Class.forName(className, false, contextClassLoader);
        }
        return Class.forName(className);
    }

    protected void prepareIndex(StringBuffer handlers, String name) throws JspException {
        if (name != null) {
            handlers.append(name);
        }
        handlers.append("[");
        handlers.append(this.getIndexValue());
        handlers.append("]");
        if (name != null) {
            handlers.append(".");
        }
    }

    protected int getIndexValue() throws JspException {
        IterateTag iterateTag = (IterateTag) findAncestorWithClass(this, IterateTag.class);
        if (iterateTag != null) {
            return iterateTag.getIndex();
        } else {
            Integer i = this.getJstlLoopIndex();
            if (i != null) {
                return i.intValue();
            } else {
                JspException e = new JspException(messages.getMessage("indexed.noEnclosingIterate"));
                TagUtils.getInstance().saveException(this.pageContext, e);
                throw e;
            }
        }
    }

    protected String prepareStyles() throws JspException {
        StringBuffer styles = new StringBuffer();
        boolean errorsExist = this.doErrorsExist();
        if (errorsExist && this.getErrorStyleId() != null) {
            this.prepareAttribute(styles, "id", this.getErrorStyleId());
        } else {
            this.prepareAttribute(styles, "id", this.getStyleId());
        }
        if (errorsExist && this.getErrorStyle() != null) {
            this.prepareAttribute(styles, "style", this.getErrorStyle());
        } else {
            this.prepareAttribute(styles, "style", this.getStyle());
        }
        if (errorsExist && this.getErrorStyleClass() != null) {
            this.prepareAttribute(styles, "class", this.getErrorStyleClass());
        } else {
            this.prepareAttribute(styles, "class", this.getStyleClass());
        }
        this.prepareAttribute(styles, "title", this.message(this.getTitle(), this.getTitleKey()));
        this.prepareAttribute(styles, "alt", this.message(this.getAlt(), this.getAltKey()));
        this.prepareInternationalization(styles);
        return styles.toString();
    }

    protected boolean doErrorsExist() throws JspException {
        boolean errorsExist = false;
        if (this.getErrorStyleId() != null || this.getErrorStyle() != null || this.getErrorStyleClass() != null) {
            String actualName = this.prepareName();
            if (actualName != null) {
                UiMessages errors = TagUtils.getInstance().getMessages(this.pageContext, this.errorKey);
                errorsExist = errors != null && errors.size(actualName) > 0;
            }
        }
        return errorsExist;
    }

    protected String prepareName() throws JspException {
        return null;
    }

    protected String prepareEventHandlers() {
        StringBuffer handlers = new StringBuffer();
        this.prepareMouseEvents(handlers);
        this.prepareKeyEvents(handlers);
        this.prepareTextEvents(handlers);
        this.prepareFocusEvents(handlers);
        return handlers.toString();
    }

    protected void prepareMouseEvents(StringBuffer handlers) {
        this.prepareAttribute(handlers, "onclick", this.getOnclick());
        this.prepareAttribute(handlers, "ondblclick", this.getOndblclick());
        this.prepareAttribute(handlers, "onmouseover", this.getOnmouseover());
        this.prepareAttribute(handlers, "onmouseout", this.getOnmouseout());
        this.prepareAttribute(handlers, "onmousemove", this.getOnmousemove());
        this.prepareAttribute(handlers, "onmousedown", this.getOnmousedown());
        this.prepareAttribute(handlers, "onmouseup", this.getOnmouseup());
    }

    protected void prepareKeyEvents(StringBuffer handlers) {
        this.prepareAttribute(handlers, "onkeydown", this.getOnkeydown());
        this.prepareAttribute(handlers, "onkeyup", this.getOnkeyup());
        this.prepareAttribute(handlers, "onkeypress", this.getOnkeypress());
    }

    protected void prepareTextEvents(StringBuffer handlers) {
        this.prepareAttribute(handlers, "onselect", this.getOnselect());
        this.prepareAttribute(handlers, "onchange", this.getOnchange());
    }

    protected void prepareFocusEvents(StringBuffer handlers) {
        this.prepareAttribute(handlers, "onblur", this.getOnblur());
        this.prepareAttribute(handlers, "onfocus", this.getOnfocus());
    }

    protected void prepareInternationalization(StringBuffer handlers) {
        this.prepareAttribute(handlers, "lang", this.getLang());
        this.prepareAttribute(handlers, "dir", this.getDir());
    }

    protected void prepareOtherAttributes(StringBuffer handlers) {
    }

    protected void prepareAttribute(StringBuffer handlers, String name, Object value) {
        if (value != null) {
            handlers.append(" ");
            handlers.append(name);
            handlers.append("=\"");
            handlers.append(value);
            handlers.append("\"");
        }
    }

    protected boolean isXhtml() {
        return TagUtils.getInstance().isXhtml(this.pageContext);
    }

    protected String getElementClose() {
        return this.isXhtml() ? " />" : ">";
    }

    protected String lookupProperty(String beanName, String property) throws JspException {
        Object bean = TagUtils.getInstance().lookup(this.pageContext, beanName, (String) null);
        if (bean == null) {
            throw new JspException(messages.getMessage("getter.bean", beanName));
        } else {
            try {
                return BeanUtils.getProperty(bean, property);
            } catch (IllegalAccessException arg5) {
                throw new JspException(messages.getMessage("getter.access", property, beanName));
            } catch (InvocationTargetException arg6) {
                Throwable t = arg6.getTargetException();
                throw new JspException(messages.getMessage("getter.result", property, t.toString()));
            } catch (NoSuchMethodException arg7) {
                throw new JspException(messages.getMessage("getter.method", property, beanName));
            }
        }
    }

    static {
        log = LogFactory.getLog(BaseHandlerTag.class);
        messages = MessageResources.getMessageResources("com.scplatform.common.web.taglib.html.LocalStrings");
    }
}