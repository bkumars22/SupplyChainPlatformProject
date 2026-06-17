/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import jakarta.servlet.jsp.tagext.BodyTagSupport;

public class Xslt extends BodyTagSupport {
	public static final Boolean CreateCachedStyleSheet = new Boolean(true);
	PageContext pageContext;
	boolean bypass = false;
	String xslfile = null;
	String xmlfile = null;
	String parm = null;
	String xsllocale = null;
	String processor = null;

	public void setBypassrequestparms(String value) {
		if (value.toLowerCase().equals("yes")) {
			this.bypass = true;
		}

	}

	public void setXslfile(String value) {
		this.xslfile = value;
	}

	public void setXmlfile(String value) {
		this.xmlfile = value;
	}

	public void setParm(String value) {
		this.parm = value;
	}

	public void setXsllocale(String value) {
		this.xsllocale = value;
	}

	public void setProcessor(String value) {
		this.processor = value;
	}

	public int doAfterBody() throws JspException {
		if (this.processor == null) {
			PageContext var10003 = this.pageContext;
			this.processor = (String) this.pageContext.getAttribute("i2uixsltprocessor", 4);
			if (this.processor == null) {
				synchronized (CreateCachedStyleSheet) {
					var10003 = this.pageContext;
					this.processor = (String) this.pageContext.getAttribute("i2uixsltprocessor", 4);
					if (this.processor == null) {
						try {
							ResourceBundle props = ResourceBundle.getBundle("i2uitaglib");
							this.processor = props.getString("XSLTProcessor");
						} catch (MissingResourceException var6) {
						}

						Class c;
						if (this.processor == null) {
							try {
								c = Class.forName("com.jclark.xsl.sax.XSLProcessorImpl");
								this.processor = "xt";
							} catch (ClassNotFoundException var5) {
								System.out.println("xt failed to load; com.jclark.xsl.sax.XSLProcessorImpl not found");
							}
						}

						if (this.processor == null) {
							try {
								c = Class.forName("org.apache.xalan.transformer.TransformerImpl");
								this.processor = "xalan";
							} catch (ClassNotFoundException var4) {
								System.out.println(
										"xalan failed to load; org.apache.xalan.transformer.TransformerImpl not found");
							}
						}

						if (this.processor != null) {
							var10003 = this.pageContext;
							this.pageContext.setAttribute("i2uixsltprocessor", this.processor, 4);
						}
					}
				}
			}
		}

		if (this.processor == null) {
			System.out.println("XSLT error: no valid processor found");
		} else if (this.processor.equals("xalan")) {
			this.doXalan();
		}

		return 0;
	}

	private void doXalan() throws JspException {
		BodyContent body = this.getBodyContent();
		XsltXalan xslt = new XsltXalan();
		xslt.process(body, this.xslfile, this.xmlfile, this.bypass, this.parm, this.xsllocale, this.pageContext);
	}

	public void setPageContext(PageContext pageContext) {
		this.pageContext = pageContext;
	}

	public void release() {
		super.release();
		this.bypass = false;
		this.xslfile = null;
		this.xmlfile = null;
		this.parm = null;
		this.xsllocale = null;
		this.processor = null;
	}
}