/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.plaf;

import java.io.Serializable;

public class Skin implements Serializable {
	private String jsDirectory_ = "";
	private String cssDirectory_ = "";
	private String svgDirectory_ = "";
	private String imageDirectory_ = "";
	private String name_ = "";
	private String description_ = "";

	public Skin(String name) {
		this.name_ = name;
	}

	public Skin(String name, String jsDirectory, String cssDirectory, String imageDirectory) {
		this.name_ = name;
		this.jsDirectory_ = jsDirectory;
		this.cssDirectory_ = cssDirectory;
		this.imageDirectory_ = imageDirectory;
	}

	public Skin(String name, String jsDirectory, String cssDirectory, String imageDirectory, String svgDirectory) {
		this.name_ = name;
		this.jsDirectory_ = jsDirectory;
		this.cssDirectory_ = cssDirectory;
		this.imageDirectory_ = imageDirectory;
		this.svgDirectory_ = svgDirectory;
	}

	public String getName() {
		return this.name_;
	}

	public void setJavaScriptDirectory(String aDirectory) {
		this.jsDirectory_ = aDirectory;
	}

	public void setCSSDirectory(String aDirectory) {
		this.cssDirectory_ = aDirectory;
	}

	public void setSVGDirectory(String aDirectory) {
		this.svgDirectory_ = aDirectory;
	}

	public void setImageDirectory(String aDirectory) {
		this.imageDirectory_ = aDirectory;
	}

	public void setDescription(String description) {
		this.description_ = description;
	}

	public String getJavaScriptDirectory() {
		return this.jsDirectory_;
	}

	public String getCSSDirectory() {
		return this.cssDirectory_;
	}

	public String getSVGDirectory() {
		return this.svgDirectory_;
	}

	public String getImageDirectory() {
		return this.imageDirectory_;
	}

	public String getDescription() {
		return this.description_;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("Skin: ");
		sb.append(this.name_).append("; ");
		sb.append(this.jsDirectory_).append("; ");
		sb.append(this.cssDirectory_).append("; ");
		sb.append(this.imageDirectory_).append("; ");
		sb.append(this.svgDirectory_).append("; ");
		return sb.toString();
	}
}