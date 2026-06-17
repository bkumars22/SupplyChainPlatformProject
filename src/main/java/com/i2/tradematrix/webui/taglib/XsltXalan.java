/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.File;
import java.util.Enumeration;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyContent;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XsltXalan {
	public void process(BodyContent body, String xslfile, String xmlfile, boolean bypass, String parm, String xsllocale,
			PageContext pageContext) throws JspException {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = null;
			Source xmlSource = new StreamSource(body.getReader());
			if (xslfile == null) {
				xslfile = "i2uiwrapper.xsl";
			}

			String xslpath = (String) pageContext.getSession().getAttribute("XslPath");
			if (xslpath == null) {
				xslpath = pageContext.getServletContext().getRealPath("/WEB-INF/xsl");
			}

			File stylesheet = new File(xslpath, xslfile);
			if (!stylesheet.exists()) {
				System.out.println("ERROR in xslt tag: Stylesheet '" + xslfile + "'not found in '" + xslpath + "'.");
			} else {
				Source xslSource = new StreamSource(stylesheet);
				if (xslSource != null) {
					transformer = tFactory.newTransformer(xslSource);
					transformer.setParameter("cfgDirectory", "../cfg");
					HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
					if (req != null) {
						transformer.setParameter("contextPath", req.getContextPath());
						String userAgent = req.getHeader("USER-AGENT");
						if (userAgent != null) {
							if (userAgent.indexOf("MSIE") != -1) {
								transformer.setParameter("UserAgent", "MSIE");
							} else if (userAgent.indexOf("FireFox") != -1) {
								transformer.setParameter("UserAgent", "FireFox");
							} else if (userAgent.indexOf("Gecko") != -1) {
								transformer.setParameter("UserAgent", "NS6");
							} else {
								transformer.setParameter("UserAgent", "NS4");
							}
						}
					}

					if (parm != null) {
						transformer.setParameter("parm", parm);
					}

					if (!bypass) {
						Enumeration enm = pageContext.getRequest().getParameterNames();

						while (enm.hasMoreElements()) {
							String name = (String) enm.nextElement();
							String value = pageContext.getRequest().getParameter(name);
							if (value != null) {
								transformer.setParameter(name, value);
							}
						}
					}

					transformer.transform(xmlSource, new StreamResult(body.getEnclosingWriter()));
				} else {
					System.out
							.println("ERROR in xslt tag: Stylesheet '" + xslfile + "'not found in '" + xslpath + "'.");
				}
			}
		} catch (Exception var18) {
			var18.printStackTrace(System.err);
		}

	}
}