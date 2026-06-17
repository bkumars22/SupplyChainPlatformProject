/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class Translator {
	private static Translator instance_ = null;
	private Hashtable rbStore_ = new Hashtable(67);

	private Translator() {
	}

	public static Translator getInstance() {
		if (instance_ == null) {
			instance_ = new Translator();
		}

		return instance_;
	}

	public String translate(Locale locale, String key) {
		String retval = key;
		if (locale != null) {
			try {
				ResourceBundle translations = (ResourceBundle) this.rbStore_.get(locale.toString());
				if (translations == null) {
					translations = ResourceBundle.getBundle("com.scplatform.testing.webui.i2uitranslations", locale);
					this.rbStore_.put(locale.toString(), translations);
				}

				retval = translations.getString(key);
			} catch (MissingResourceException var5) {
			}
		}

		return retval;
	}
}