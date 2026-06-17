/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.i2.i2ui.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

class TrimmerJavascript {
	private static final String USAGE = "java com.i2.i2ui.util.TrimmerJavascript inFileName";
	private String inFileName_ = null;
	private BufferedInputStream inFile_ = null;

	private void handleArgs(String[] args) {
		if (args.length > 0) {
			this.inFileName_ = args[0];
		} else {
			System.err.println("java com.i2.i2ui.util.TrimmerJavascript inFileName");
			System.exit(1);
		}

	}

	private void doIt() throws IOException {
		boolean bInsideComment = false;
		StringBuffer linebuffer = new StringBuffer("");
		String previousline = linebuffer.toString();

		int ch;
		do {
			ch = this.inFile_.read();
			if (ch != 10 && ch != 13 && ch != -1) {
				linebuffer.append((char) ch);
			} else if (linebuffer.length() > 0) {
				String currentline = linebuffer.toString().trim();
				if (currentline.length() > 0 && !currentline.startsWith("//")) {
					if (currentline.startsWith("/*")) {
						bInsideComment = true;
					}

					if (!bInsideComment) {
						if (!currentline.equals("{") && !currentline.equals("}") && !previousline.endsWith(";")
								&& !previousline.endsWith("||") && !previousline.endsWith("{")
								&& !previousline.endsWith("&&")) {
							if (previousline.length() > 0) {
								System.out.println(previousline);
							}

							previousline = currentline;
						} else {
							previousline = previousline + currentline;
						}
					}

					if (currentline.endsWith("*/")) {
						bInsideComment = false;
					}
				}

				linebuffer.setLength(0);
			}
		} while (ch != -1);

		System.out.println(previousline);
	}

	private void realMain(String[] args) {
		this.handleArgs(args);

		try {
			this.inFile_ = new BufferedInputStream(new FileInputStream(this.inFileName_));
			this.doIt();
		} catch (FileNotFoundException var3) {
			System.err.println("TrimmerJavascript: FileNotFoundException opening file " + this.inFileName_);
			var3.printStackTrace();
		} catch (IOException var4) {
			System.err.println("TrimmerJavascript: IOException reading file " + this.inFileName_);
			var4.printStackTrace();
		}

	}

	public static void main(String[] args) {
		TrimmerJavascript tj = new TrimmerJavascript();
		tj.realMain(args);
	}
}