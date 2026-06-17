/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.testing.webui.taglib;

import java.util.Locale;
import java.util.Vector;

public final class Utils {
	private Utils() {
	}

	public static String[] stringToList(String str, char sep) {
		String[] retval = null;
		if (str != null && str.length() != 0) {
			if (sep != 0) {
				Vector vec = new Vector();
				int currentIndex = 0;

				for (int index = str.indexOf(sep); index != -1; index = str.indexOf(sep, currentIndex)) {
					vec.addElement(str.substring(currentIndex, index));
					currentIndex = index + 1;
				}

				vec.addElement(str.substring(currentIndex));
				retval = new String[vec.size()];
				vec.copyInto(retval);
			} else {
				retval = new String[]{str};
			}
		}

		return retval;
	}

	public static Locale nameToLocale(String name) {
		Locale retval = Locale.getDefault();
		if (name != null && name.length() > 0) {
			String[] fields = stringToList(name, '_');
			if (fields.length == 1) {
				retval = new Locale(fields[0], "");
			} else if (fields.length == 2) {
				retval = new Locale(fields[0], fields[1]);
			} else if (fields.length == 3) {
				retval = new Locale(fields[0], fields[1], fields[2]);
			}
		}

		return retval;
	}

	public static String translate(String localeString, String key) {
		return Translator.getInstance().translate(nameToLocale(localeString), key);
	}

	public static String translate(Locale locale, String key) {
		return Translator.getInstance().translate(locale, key);
	}
}