/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class Http extends TagSupport {
	private boolean debug = false;
	private String method = "get";
	private String url = null;
	private String silent = null;
	private String cookie = null;
	private String pagevariable = null;
	private static String sessionCookie = null;

	public String getCookie() {
		return sessionCookie;
	}

	public void setPagevariable(String value) {
		this.pagevariable = value;
	}

	public void setCookie(String value) {
		this.cookie = value;
	}

	public void setMethod(String value) {
		this.method = value;
	}

	public void setSilent(String value) {
		this.silent = value;
	}

	public void setUrl(String value) {
		this.url = value;
	}

	public void setDebug(int value) {
		if (value == 1) {
			this.debug = true;
		} else {
			this.debug = false;
		}

	}

	public int doEndTag() throws JspTagException {
		try {
			if (this.debug) {
				System.out.println("http url=[" + this.url + "]");
			}

			URL u = new URI(this.url).toURL();
			HttpURLConnection uConn = (HttpURLConnection) u.openConnection();
			uConn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			uConn.setRequestMethod(this.method.toUpperCase());
			if (this.cookie != null) {
				uConn.setRequestProperty("Cookie", this.cookie);
			} else if (sessionCookie != null) {
				uConn.setRequestProperty("Cookie", sessionCookie);
			}

			if (this.method.equals("get")) {
				uConn.setDoInput(true);
				this.doInput(uConn);
			} else if (this.method.equals("post")) {
				uConn.setDoOutput(true);
				uConn.setDoInput(true);
				uConn.setAllowUserInteraction(false);
				DataOutputStream out = new DataOutputStream(uConn.getOutputStream());
				out.writeBytes(this.makeQueryString());
				out.close();
				this.doInput(uConn);
			}
		} catch (URISyntaxException var4) {
			System.out.println("Invalid URL syntax? " + var4);
		} catch (IOException var5) {
			System.out.println("openConnection() failed - bad network?");
		}

		return 1;
	}

	private void doInput(URLConnection uConn) {
		try {
			if (this.cookie == null && sessionCookie == null) {
				getCookieFromURL(uConn);
			}

			BufferedReader in = new BufferedReader(new InputStreamReader(uConn.getInputStream()));
			StringBuffer content = new StringBuffer();

			String line;
			while ((line = in.readLine()) != null) {
				content.append(line + "\n");
				if (this.silent == null) {
					if (this.debug) {
						System.out.println("HTTP:[" + line + "]");
					}

					this.pageContext.getOut().println(line);
				}
			}

			in.close();
			if (this.pagevariable != null) {
				if (this.debug) {
					System.out.println("http set [" + this.pagevariable + "] to hold result");
				}

				this.pageContext.setAttribute(this.pagevariable, content.toString());
			} else if (this.getId() != null) {
				if (this.debug) {
					System.out.println("http set [" + this.getId() + "] to hold result");
				}

				this.pageContext.setAttribute(this.getId(), content.toString());
			}

			if (this.debug) {
				System.out.println("http content=[" + content.toString() + "]");
			}
		} catch (Exception var6) {
			System.out.println("HTTP Bummer " + var6);
		}

	}

	private String makeQueryString() {
		String queryString = "";
		int at = this.url.indexOf(63);
		if (at > -1) {
			queryString = this.url.substring(at);
		}

		return queryString;
	}

	private static void getCookieFromURL(URLConnection connection) {
		try {
			for (int j = 1; connection.getHeaderField(j) != null; ++j) {
				if (connection.getHeaderFieldKey(j).equalsIgnoreCase("set-cookie")) {
					sessionCookie = connection.getHeaderField(j);
					int cookie_end_index = sessionCookie.indexOf(";");
					sessionCookie = sessionCookie.substring(0, cookie_end_index);
					break;
				}
			}

		} catch (Exception var3) {
			if (var3 instanceof RuntimeException) {
				throw (RuntimeException) var3;
			} else {
				throw new RuntimeException(var3.toString() + "\n" + var3.getMessage());
			}
		}
	}
}