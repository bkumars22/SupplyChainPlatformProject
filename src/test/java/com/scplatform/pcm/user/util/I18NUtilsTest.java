/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.user.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class I18NUtilsTest {

    @Test
    void defaultConstructor_isInvokable() {
        assertNotNull(new I18NUtils());
    }

    @Test
    void getAvailableLocales_throwsBecauseLocaleLabelIsNotComparable() {
        // Source calls Collections.sort on non-Comparable LocaleLabel — documents current behavior.
        assertThrows(ClassCastException.class, I18NUtils::getAvailableLocales);
    }

    @Test
    void getAvailableLanguageLocales_throwsBecauseLocaleLabelIsNotComparable() {
        assertThrows(ClassCastException.class, I18NUtils::getAvailableLanguageLocales);
    }

    // Note: post-sort `return results` in getAvailableLocales/getAvailableLanguageLocales
    // is unreachable in practice (LocaleLabel is non-Comparable, so Collections.sort always
    // throws). Attempting to mock Locale.getAvailableLocales to return a 1-element array
    // triggers JaCoCo-vs-JDK21-CLDR class-instrumentation failures (Mockito-inline + JaCoCo
    // 0.8.12 cannot transform sealed JDK CLDR classes). Coverage parked at the
    // ClassCastException branch.

    @Test
    void localeLabel_gettersAndSetters() {
        I18NUtils.LocaleLabel ll = new I18NUtils.LocaleLabel("English", "en");
        assertEquals("English", ll.getLabel());
        assertEquals("en", ll.getValue());
        ll.setLabel("Anglais");
        ll.setValue("fr");
        assertEquals("Anglais", ll.getLabel());
        assertEquals("fr", ll.getValue());
    }

    @Test
    void findLocale_byKey_findsKnownLocale() {
        Locale en = I18NUtils.findLocale("en");
        assertNotNull(en);
        assertEquals("en", en.getLanguage());
    }

    @Test
    void findLocale_byKey_returnsNullWhenUnknown() {
        assertNull(I18NUtils.findLocale("xx_NOPE"));
    }

    @Test
    void findLocale_byLangAndCountry_buildsKeyAndFinds() {
        Locale loc = I18NUtils.findLocale("en", "US");
        assertNotNull(loc);
        assertEquals("en", loc.getLanguage());
        assertEquals("US", loc.getCountry());
    }

    @Test
    void findLocale_byLangAndCountry_skipsCountryIfLangAlreadyHasUnderscore() {
        // "en_US" already contains '_', so country should not be re-appended
        Locale loc = I18NUtils.findLocale("en_US", "GB");
        assertNotNull(loc);
        assertEquals("US", loc.getCountry());
    }

    @Test
    void findLocale_byLangAndCountry_handlesNullLang() {
        // null lang -> key starts as "" then "_" + country which won't match any locale
        assertNull(I18NUtils.findLocale(null, "US"));
    }

    @Test
    void findLocale_byLangAndCountry_handlesNullCountry() {
        Locale loc = I18NUtils.findLocale("en", null);
        assertNotNull(loc);
        assertEquals("en", loc.getLanguage());
    }

    @Test
    void findCountryCode_returnsCodeForKnownCountryName() {
        // Use Locale.US display name in US locale
        String displayName = Locale.US.getDisplayCountry(Locale.US);
        String code = I18NUtils.findCountryCode(displayName);
        assertEquals("US", code);
    }

    @Test
    void findCountryCode_returnsNullForUnknown() {
        assertNull(I18NUtils.findCountryCode("Atlantis"));
    }
}
