/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import com.scplatform.testing.webui.plaf.Skin;
import java.io.Serializable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.PageContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Settings implements Serializable {
	private static final Logger logCategory_ = LoggerFactory.getLogger(Settings.class);
	public static final String SETTINGS_KEY = "i2.settings";
	/** @deprecated */
	public static final String DEPRECATED_SETTINGS_KEY = "settings";
	public static final String IE = "IE";
	public static final String FF = "FF";
	public static final String CH = "CH";
	protected static final String DEFAULT_IMAGE_DIR = "/skins/e2-modern/images";
	protected static final String DEFAULT_JS_DIR = "/skins/e2-modern/js";
	protected static final String DEFAULT_CSS_DIR = "/skins/e2-modern";
	protected static final String DEFAULT_SVG_DIR = "/skins/e2-modern/svg";
	protected Locale locale_;
	protected Skin skin_;
	protected String defaultCSSStyleSheet_;
	protected String browserType_ = "IE";
	protected String bundleName_;
	protected boolean browserIsIE;
	protected boolean browserIsFF;
	protected boolean browserIsCH;
	protected boolean javascriptInline_;
	protected String clientRootPath = null;

	/** @deprecated */
	public static Settings settingsFactory(PageContext pageContext, boolean renew, boolean jsInline) {
		HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
		if (req == null) {
			logCategory_.error("Unable to get http request from PageContext.");
			return null;
		} else {
			HttpSession sess = req.getSession();
			if (sess == null) {
				sess = req.getSession(true);
			}

			Settings retSettings = (Settings) pageContext.getAttribute("i2.settings", 2);
			if (renew || retSettings == null) {
				Skin skin = (Skin) pageContext.findAttribute("i2Skin");
				retSettings = new Settings(req, skin, jsInline);
				pageContext.setAttribute("i2.settings", retSettings, 2);
			}

			return retSettings;
		}
	}

	public static Settings settingsFactory(HttpServletRequest req, boolean renew, boolean jsInline) {
		HttpSession sess = req.getSession();
		if (sess == null) {
			sess = req.getSession(true);
		}

		Settings retSettings = (Settings) req.getAttribute("i2.settings");
		if (renew || retSettings == null) {
			Skin skin = (Skin) sess.getAttribute("i2Skin");
			retSettings = new Settings(req, skin, jsInline);
			req.setAttribute("i2.settings", retSettings);
		}

		return retSettings;
	}

	public static Settings getSessionSettings(HttpServletRequest req) {
		return settingsFactory(req, false, false);
	}

	protected Settings(HttpServletRequest req, Skin skin, boolean jsInline) {
		String userAgent = req.getHeader("USER-AGENT");
		this.setBrowser(userAgent);
		this.javascriptInline_ = jsInline;
		this.locale_ = req.getLocale();
		this.bundleName_ = "com.scplatform.testing.webui.i2uitaglibBundle";
		String imageDirectory = null;
		String svgDirectory = null;
		String javascriptDirectory = null;
		String cssDirectory = null;
		String skinName = null;
		this.clientRootPath = getClientRootPath(req);
		if (skin == null) {
			if (logCategory_.isDebugEnabled()) {
				logCategory_.debug("Skin is not defined in session.  Using default skin directories.");
			}

			imageDirectory = "/skins/e2-modern/images";
			svgDirectory = "/skins/e2-modern/svg";
			javascriptDirectory = "/skins/e2-modern/js";
			cssDirectory = "/skins/e2-modern";
			skinName = "Default";
			this.skin_ = new Skin(skinName, javascriptDirectory, cssDirectory, imageDirectory, svgDirectory);
			HttpSession sess = req.getSession();
			if (sess == null) {
				sess = req.getSession(true);
			}

			req.getSession().setAttribute("i2Skin", this.skin_);
		} else {
			if (logCategory_.isDebugEnabled()) {
				logCategory_.debug("Skin is defined in session. Skin = " + skin.getName());
			}

			cssDirectory = skin.getCSSDirectory();
			this.skin_ = skin;
		}

		this.defaultCSSStyleSheet_ = cssDirectory + "/style_sheet_core.css" + this.getCacheProperty(req);
	}

	public String getCacheProperty(HttpServletRequest req) {
		Object buildNumber = req.getSession().getAttribute("cacheProperty");
		return buildNumber == null ? "" : "?cacheProperty=" + (String) buildNumber;
	}

	public void setBrowser(String browserId) {
		if (logCategory_.isDebugEnabled()) {
			logCategory_.debug("Settings:determine browser from [" + browserId + "]");
		}

		this.browserIsIE = false;
		this.browserIsFF = false;
		this.browserIsCH = false;
		if (browserId != null && browserId.indexOf("Firefox") != -1) {
			this.browserIsFF = true;
			this.browserType_ = "FF";
		} else if (browserId != null && browserId.indexOf("MSIE") != -1) {
			this.browserIsIE = true;
			this.browserType_ = "IE";
		} else if (browserId != null && browserId.indexOf("Chrome") != -1) {
			this.browserIsCH = true;
			this.browserType_ = "CH";
		} else if (browserId != null && browserId.indexOf("Safari") != -1) {
			this.browserIsCH = true;
			this.browserType_ = "CH";
		} else {
			this.browserIsIE = true;
			this.browserType_ = "IE";
		}

	}

	public String getBrowserType() {
		return this.browserType_;
	}

	public boolean isIE() {
		return this.browserIsIE;
	}

	public boolean isFF() {
		return this.browserIsFF;
	}

	public boolean isCH() {
		return this.browserIsCH;
	}

	public void setSkin(Skin aSkin) {
		this.skin_ = aSkin;
	}

	public Skin getSkin() {
		return this.skin_;
	}

	public void setImageDirectory(String value) {
		this.skin_.setImageDirectory(value);
	}

	public String getImageDirectory() {
		return this.clientRootPath + this.skin_.getImageDirectory();
	}

	public String getBrandingImageDirectory() {
		return System.getProperty("e2.stack.name") != null
				? "../" + this.clientRootPath + "/common/skins/e2-standard/branding"
				: this.clientRootPath + this.skin_.getImageDirectory();
	}

	public void setSVGDirectory(String value) {
		this.skin_.setSVGDirectory(value);
	}

	public String getSVGDirectory() {
		return this.clientRootPath + this.skin_.getSVGDirectory();
	}

	public void setJavascriptDirectory(String value) {
		this.skin_.setJavaScriptDirectory(value);
	}

	public String getJavascriptDirectory() {
		return this.clientRootPath + this.skin_.getJavaScriptDirectory();
	}

	public void setCSSDirectory(String value) {
		this.skin_.setCSSDirectory(value);
	}

	public String getCSSDirectory() {
		return this.clientRootPath + this.skin_.getCSSDirectory();
	}

	public boolean getJavascriptInline() {
		return this.javascriptInline_;
	}

	public void setJavascriptInline(boolean val) {
		this.javascriptInline_ = val;
	}

	public void setLocale(Locale aLocale) {
		this.locale_ = aLocale;
	}

	public Locale getLocale() {
		return this.locale_;
	}

	public void setResourceBundleBaseName(String resourceBundleBaseName) {
		this.bundleName_ = resourceBundleBaseName;
	}

	public ResourceBundle getResourceBundle() throws MissingResourceException {
		return ResourceBundle.getBundle(this.bundleName_, this.locale_);
	}

	public void setDefaultCSSStyleSheet(String styleSheetPath) {
		this.defaultCSSStyleSheet_ = styleSheetPath;
	}

	public String getDefaultCSSStyleSheet() {
		return this.clientRootPath + this.defaultCSSStyleSheet_;
	}

	public String getClientRootPath() {
		return this.clientRootPath;
	}

	private static String getClientRootPath(HttpServletRequest request) {
		String webAppPath = "";
		String contextPath = request.getContextPath();
		String url = request.getRequestURI().toString();
		String forwardPath = (String) request.getAttribute("jakarta.servlet.forward.servlet_path");
		if (forwardPath != null) {
			url = contextPath + forwardPath;
		}

		int pos = url.lastIndexOf(contextPath + "/");
		if (pos >= 0) {
			url = url.substring(pos + contextPath.length() + 1);
			if (url.indexOf("/") > 0) {
				int count = (new StringTokenizer(url, "/")).countTokens() - 1;

				for (int i = 0; i < count; ++i) {
					if (i == count - 1) {
						webAppPath = webAppPath + "..";
					} else {
						webAppPath = webAppPath + "../";
					}
				}
			} else {
				webAppPath = ".";
			}
		} else {
			webAppPath = ".";
		}

		if (logCategory_.isDebugEnabled()) {
			logCategory_.debug("URL=" + url + " contextPath=" + contextPath + " path=" + webAppPath);
		}

		return webAppPath;
	}

}