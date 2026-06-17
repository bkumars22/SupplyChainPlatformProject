/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import com.scplatform.testing.webui.plaf.PlafDocumentHandler;
import com.scplatform.testing.webui.plaf.Skin;
import com.scplatform.testing.webui.plaf.Skins;
import java.io.InputStream;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.TagSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SkinTag extends TagSupport {
	private static final Logger log = LoggerFactory.getLogger(SkinTag.class);
	public static final String SKIN_NAME_KEY = "i2Skin";
	public static final String SKINS_NAME_KEY = "i2SkinsMap";
	private String path_;
	private String scope_;
	private String skinName_;
	private int theScope_;

	public SkinTag() {
		this.resetCustomAttributes();
	}

	public void setPath(String path) {
		this.path_ = path;
	}

	public String getPath() {
		return this.path_;
	}

	public void setName(String skinName) {
		if ("".equals(skinName)) {
			this.skinName_ = null;
		} else {
			this.skinName_ = skinName;
		}

	}

	public String getName() {
		return this.skinName_;
	}

	public void setScope(String scope) {
		this.scope_ = scope;
	}

	public String getScope() {
		return this.scope_;
	}

	public void release() {
		super.release();
		this.resetCustomAttributes();
	}

	public void resetCustomAttributes() {
		this.path_ = "/WEB-INF/cfg/i2uiskins.xml";
		this.scope_ = null;
		this.skinName_ = null;
		this.theScope_ = 0;
	}

	public int doStartTag() throws JspException {
		Skin skin = this.findSkin();
		if (skin == null) {
			throw new JspException("Could not find skin: " + this.skinName_);
		} else {
			this.pageContext.setAttribute("i2Skin", skin, this.theScope_);
			PageContext var10001 = this.pageContext;
			if (this.theScope_ != 1) {
				PageContext var10003 = this.pageContext;
				this.pageContext.setAttribute("i2Skin", skin, 1);
			}

			return 0;
		}
	}

	private Skin findSkin() {
		Skin skin = null;
		if (this.scope_ != null) {
			this.theScope_ = this.mapScope(this.scope_);
			skin = (Skin) this.pageContext.getAttribute("i2Skin", this.theScope_);
		} else {
			PageContext var10001 = this.pageContext;
			this.theScope_ = 3;
			skin = (Skin) this.pageContext.findAttribute("i2Skin");
		}

		if (skin != null && (this.skinName_ == null || this.skinName_.equals(skin.getName()))) {
			return skin;
		} else {
			Skins skins = getSkins(this.pageContext.getServletContext(),
					(HttpServletRequest) this.pageContext.getRequest(), this.path_);
			if (skins != null) {
				return this.skinName_ != null ? skins.getSkin(this.skinName_) : skins.getDefaultSkin();
			} else {
				return null;
			}
		}
	}

	public static Skins getSkins(ServletContext servletContext, HttpServletRequest request, String configFilePath) {
		Skins skins = (Skins) request.getAttribute("i2SkinsMap");
		if (skins != null) {
			return skins;
		} else {
			skins = (Skins) servletContext.getAttribute("i2SkinsMap");
			if (skins != null) {
				return skins;
			} else {
				try {
					InputStream is = servletContext.getResourceAsStream(configFilePath);
					PlafDocumentHandler pdh = new PlafDocumentHandler(is, "");
					skins = pdh.getSkins();
					servletContext.setAttribute("i2SkinsMap", skins);
					return skins;
				} catch (Exception var6) {
					log.error("Unable to load plaf config file from " + configFilePath + ". Error = "
							+ var6.getMessage());
					return null;
				}
			}
		}
	}

	private int mapScope(String scope) {
		PageContext var10000;
		if ("session".equalsIgnoreCase(scope)) {
			var10000 = this.pageContext;
			return 3;
		} else if ("request".equalsIgnoreCase(scope)) {
			var10000 = this.pageContext;
			return 2;
		} else if ("application".equalsIgnoreCase(scope)) {
			var10000 = this.pageContext;
			return 4;
		} else if ("page".equalsIgnoreCase(scope)) {
			var10000 = this.pageContext;
			return 1;
		} else {
			return -1;
		}
	}
}