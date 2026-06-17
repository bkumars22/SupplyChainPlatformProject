/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.user.dto;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.UserDelegate;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.util.I18NUtils;
import com.scplatform.pcm.user.util.I18NUtils.LocaleLabel;
import com.scplatform.pcm.util.stateMachine.StateMachineState;

import org.apache.commons.lang3.StringUtils;

/**
 * Used with UserProfileAction to allow user to set various personal preferences
 */
public class UserProfileForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private String currentUserKey;
    private String localeKey;
    private String language;
    private String country;
    private Map userPreferences;
    private Users user;
    private Role role;
    private List availableLangs = new ArrayList();
    private Map requestDocTypes;
    private Map<String, Collection<StateMachineState>> availableStates;

    protected List<UserDelegate> whereDelegate = new ArrayList<UserDelegate>();
    protected List<UserDelegate> delegates = new ArrayList<UserDelegate>();
    protected String delegateUserId;
    protected String delegateStartDate;
    protected String delegateEndDate;
    protected int selectedDelegateIdx;

    public void reset() {
    currentUserKey = null;
    localeKey = null;
    selectedDelegateIdx = -1;
    delegateUserId = null;
    delegateStartDate = null;
    delegateEndDate = null;

    if (userPreferences != null) {
        Iterator it = userPreferences.entrySet().iterator();
        while (it.hasNext()) {
        Map.Entry pairs = (Map.Entry) it.next();
        pairs.setValue(null);
        }
    }
    }

    public void setUser(Users user) {
	this.user = user;
    }

    public Users getUser() {
	return user;
    }

    public List getAllAvailableLanguages() {
	return I18NUtils.getAvailableLanguageLocales();
    }

    public List getAllAvailableLocales() {
	return I18NUtils.getAvailableLocales();
    }

    public String getLocaleKey() {
	return localeKey;
    }

    public void setLocaleKey(String id) {
	localeKey = id;
    }

    public void setUserPreferences(Map prefs) {
	userPreferences = new HashMap(prefs);
    }

    public Map getUserPreferences() {
	return userPreferences;
    }

    public String getUserPreferenceValue(String prefName) {
	return (String) userPreferences.get(prefName);
    }

    public void setUserPreferenceValue(String prefName, String value) {
	userPreferences.put(prefName, value);
    }

    public String[] getUserPreferenceValueAsArray(String prefName) {
	String temp = getUserPreferenceValue(prefName);
	return (temp != null) ? temp.split(",") : null;
    }

    public void setUserPreferenceValueAsArray(String prefName, String[] array) {
	String value = null;

	if (array != null && array.length > 0) {
	    value = ArrayUtils.toString(array);
	    value = StringUtils.substringAfter(value, "{");
	    value = StringUtils.substringBeforeLast(value, "}");
	}
	setUserPreferenceValue(prefName, value);
    }

    /**
     * @param currentUserKey
     *            The currentUserKey to set.
     */
    public void setCurrentUserKey(String currentUserKey) {
	this.currentUserKey = StringUtils.trimToNull(currentUserKey);
    }

    /**
     * @return Returns the currentUserKey.
     */
    public String getCurrentUserKey() {
	return currentUserKey;
    }

    public Role getRole() {
	return role;
    }

    public void setRole(Role role) {
	this.role = role;
    }

    /**
     * @param language
     *            The language to set.
     */
    public void setLanguage(String language) {
	this.language = language;
    }

    /**
     * @return Returns the language.
     */
    public String getLanguage() {
	return language;
    }

    /**
     * @param country
     *            The country to set.
     */
    public void setCountry(String country) {
	this.country = country;
    }

    /**
     * @return Returns the country.
     */
    public String getCountry() {
	return country;
    }

    public void clearAvailableLanguages() {
	availableLangs.clear();
    }

    /**
     * @param lang
     *            The lang to add
     */
    public void addAvailableLanguage(LocaleLabel label) {
	this.availableLangs.add(label);
    }

    /**
     * @return Returns the availableLangs.
     */
    public List getAvailableLanguages() {
	return availableLangs;
    }

    public void setRequestDocTypes(Map types) {
	requestDocTypes = types;
    }

    public Map getRequestDocTypes() {
	return requestDocTypes;
    }

    public void setAvailableStates(Map<String, Collection<StateMachineState>> states) {
	this.availableStates = states;
    }

    public Map<String, Collection<StateMachineState>> getAvailableStates() {
	return this.availableStates;
    }

    public String getDelegateUserId() {
	return delegateUserId;
    }

    public void setDelegateUserId(String delegateUserId) {
	this.delegateUserId = StringUtils.trimToNull(delegateUserId);
    }

    public String getDelegateStartDate() {
	return delegateStartDate;
    }

    public void setDelegateStartDate(String delegateStartDate) {
	this.delegateStartDate = StringUtils.trimToNull(delegateStartDate);
    }

    public String getDelegateEndDate() {
	return delegateEndDate;
    }

    public void setDelegateEndDate(String delegateEndDate) {
	this.delegateEndDate = StringUtils.trimToNull(delegateEndDate);
    }

    public List<UserDelegate> getDelegates() {
	return delegates;
    }

    public void setDelegates(List<UserDelegate> delegates) {
	this.delegates = delegates;
    }

    public int getSelectedDelegateIdx() {
	return selectedDelegateIdx;
    }

    public void setSelectedDelegateIdx(int selectedDelegateIdx) {
	this.selectedDelegateIdx = selectedDelegateIdx;
    }

    public List<UserDelegate> getWhereDelegateFor() {
	return whereDelegate;
    }

    public void setWhereDelegateFor(List<UserDelegate> whereDelegate) {
	this.whereDelegate = whereDelegate;
    }

}
