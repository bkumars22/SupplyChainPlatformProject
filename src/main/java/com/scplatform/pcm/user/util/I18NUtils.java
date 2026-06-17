/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */package com.scplatform.pcm.user.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class I18NUtils
{
    public static class LocaleLabel
    {

        String label;
        String value;

        public LocaleLabel(String label, String value)
        {

            this.label = label;
            this.value = value;
        }
        public String getLabel()
        {
            return label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public String getValue()
        {
            return value;
        }

        public void setValue(String value)
        {
            this.value = value;
        }

    }

    public static List getAvailableLanguageLocales()
    {
        List results = new ArrayList();
        Locale[] l =  Locale.getAvailableLocales();
        String lastLang = "";
        for (int idx=0; idx < l.length; idx++)
        {
            if (lastLang.equals(l[idx].getLanguage()) == false)
            {
                results.add(new LocaleLabel(l[idx].getDisplayName(),l[idx].toString()));
                lastLang = l[idx].getLanguage();
            }
        }
        Collections.sort(results);
        return results;
    }

    public static List getAvailableLocales()
    {
        List results = new ArrayList();
        Locale[] l =  Locale.getAvailableLocales();

        for (int idx=0; idx < l.length; idx++)
        {
            results.add(new LocaleLabel(l[idx].getDisplayName(),l[idx].toString()));
        }
        Collections.sort(results);
        return results;
    }

    public static Locale findLocale(String key)
    {
        Locale[] l = Locale.getAvailableLocales();
        for (int idx=0; idx < l.length; idx++)
        {
            if (key.equals(l[idx].toString()))
            {
                return l[idx];
            }
        }
        return null;
    }

    public static Locale findLocale(String lang, String country)
    {
        String key = "";
        boolean fLangHasCountry = false;
        if (lang != null)
        {
            key = lang;
            fLangHasCountry = lang.indexOf('_') > -1;
        }
        if (country != null && fLangHasCountry == false)
        {
            key += "_" + country;
        }
        return findLocale(key);
    }

    /**
     * used to find the ISO country code from the Country name.
     * Needed because E2nd only stores US names
     * @param countryName
     * @return
     */
    public static String findCountryCode(String countryName)
    {
        Locale[] l = Locale.getAvailableLocales();
        for (int idx=0; idx < l.length; idx++)
        {
            if (l[idx].getDisplayCountry(Locale.US).equals(countryName))
            {
                return l[idx].getCountry();
            }
        }
        return null;

    }
}
