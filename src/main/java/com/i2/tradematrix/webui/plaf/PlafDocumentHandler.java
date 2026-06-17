/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.plaf;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public final class PlafDocumentHandler {
	private static final Logger logCategory_ = LoggerFactory.getLogger(PlafDocumentHandler.class);
	private Skins skins_ = new Skins();
	private String configFile_;
	private String contextPath_;

	public PlafDocumentHandler(InputStream istream, String contextPath) throws PlafException {
		this.contextPath_ = contextPath;
		this.init(istream);
	}

	public PlafDocumentHandler(String ifile, String contextPath) throws PlafException {
		this.contextPath_ = contextPath;
		this.configFile_ = ifile;
		InputStream istream = this.openInputStream();
		this.init(istream);
		this.closeInputStream(istream);
	}

	public void reload() throws PlafException {
		if (this.configFile_ == null) {
			throw new PlafException("Cannot reload.  Config file is undefined.");
		} else {
			InputStream istream = this.openInputStream();
			this.reload(istream);
			this.closeInputStream(istream);
		}
	}

	public void reload(InputStream istream) throws PlafException {
		this.skins_.clear();
		this.init(istream);
	}

	public void setConfigFile(String configFile) {
		this.configFile_ = configFile;
	}

	public Skins getSkins() {
		return this.skins_;
	}

	private void init(InputStream istream) throws PlafException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new InputSource(istream));
			this.populateSkins(document);
		} catch (Throwable var5) {
			String msg = "Unable to parse plaf XML config file: " + var5.getMessage();
			logCategory_.error(msg, var5);
			throw new PlafException(msg);
		}
	}

	private InputStream openInputStream() throws PlafException {
		try {
			FileInputStream fis = new FileInputStream(this.configFile_);
			return fis;
		} catch (Exception var2) {
			throw new PlafException("Unable to open config file " + this.configFile_);
		}
	}

	private void closeInputStream(InputStream istream) {
		try {
			istream.close();
		} catch (Exception var3) {
		}

	}

	private String buildContextPath(String path) {
		if (path.equals("/")) {
			return this.contextPath_;
		} else {
			return path.startsWith("/") ? this.contextPath_ + path : this.contextPath_ + "/" + path;
		}
	}

	private void populateSkins(Node root) {
		Node plaf = this.findChildNode(root, "plaf");
		Node skins = this.findChildNode(plaf, "skins");
		Node plafDefaults = this.findChildNode(skins, "defaults");
		String defaultJSDir = this.buildContextPath("");
		String defaultCSSDir = this.buildContextPath("");
		String defaultSVGDir = this.buildContextPath("");
		String defaultImageDir = this.buildContextPath("");
		Node n1 = this.findChildNode(plafDefaults, "skinName");
		if (n1 != null) {
			this.skins_.setDefaultSkin(this.getNodeValue(n1));
		}

		Node directories = this.findChildNode(plafDefaults, "directories");
		n1 = this.findChildNode(directories, "javascript");
		if (n1 != null) {
			defaultJSDir = this.buildContextPath(this.getNodeValue(n1));
		}

		n1 = this.findChildNode(directories, "css");
		if (n1 != null) {
			defaultCSSDir = this.buildContextPath(this.getNodeValue(n1));
		}

		n1 = this.findChildNode(directories, "svg");
		if (n1 != null) {
			defaultSVGDir = this.buildContextPath(this.getNodeValue(n1));
		}

		n1 = this.findChildNode(directories, "image");
		if (n1 != null) {
			defaultImageDir = this.buildContextPath(this.getNodeValue(n1));
		}

		NodeList nodes = skins.getChildNodes();

		for (int i = 0; i < nodes.getLength(); ++i) {
			String jsDir = defaultJSDir;
			String cssDir = defaultCSSDir;
			String svgDir = defaultSVGDir;
			String imageDir = defaultImageDir;
			Node skinNode = nodes.item(i);
			if ("skin".equals(skinNode.getNodeName())) {
				Node skinName = skinNode.getAttributes().getNamedItem("name");
				Skin theSkin = new Skin(skinName.getNodeValue().trim());
				n1 = this.findChildNode(skinNode, "info");
				if (n1 != null) {
					theSkin.setDescription(this.getNodeValue(n1));
				}

				directories = this.findChildNode(skinNode, "directories");
				n1 = this.findChildNode(directories, "javascript");
				if (n1 != null) {
					jsDir = this.buildContextPath(this.getNodeValue(n1));
				}

				n1 = this.findChildNode(directories, "css");
				if (n1 != null) {
					cssDir = this.buildContextPath(this.getNodeValue(n1));
				}

				n1 = this.findChildNode(directories, "svg");
				if (n1 != null) {
					svgDir = this.buildContextPath(this.getNodeValue(n1));
				}

				n1 = this.findChildNode(directories, "image");
				if (n1 != null) {
					imageDir = this.buildContextPath(this.getNodeValue(n1));
				}

				theSkin.setJavaScriptDirectory(jsDir);
				theSkin.setCSSDirectory(cssDir);
				theSkin.setImageDirectory(imageDir);
				theSkin.setSVGDirectory(svgDir);
				this.skins_.addSkin(theSkin);
			}
		}

	}

	private String getNodeValue(Node node) {
		return node.getFirstChild().getNodeValue().trim();
	}

	private Node findChildNode(Node parent, String nodeName) {
		if (parent != null && nodeName != null) {
			NodeList nodes = parent.getChildNodes();

			for (int i = 0; i < nodes.getLength(); ++i) {
				Node child = nodes.item(i);
				if (nodeName.equals(child.getNodeName())) {
					return child;
				}

				Node match = this.findChildNode(nodes.item(i), nodeName);
				if (match != null) {
					return match;
				}
			}

			return null;
		} else {
			return null;
		}
	}

}