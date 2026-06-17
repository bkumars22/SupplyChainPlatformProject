/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.dashboard.dto;

import com.scplatform.pcm.util.stateMachine.StateMachineState;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class DashboardForm  {
    private long lastLoaded = 0;

    private List<?> userAlerts;

    private List<?> newItemStatus;
    private int newItemAge = 20;
    private final HashMap<String, List<?>> recordStatus = new HashMap<String, List<?>>();
    private final Map<String, Boolean> recordStatusOwnerOnly = new HashMap<String, Boolean>();
    private final Map<String, Integer> recordStatusAge = new HashMap<String, Integer>();
    private Set<String> dashboardCards = new HashSet<>();
    private Set<String> inactiveDashboardCards = new HashSet<>();
    private boolean refresh = false;
    private String dashboardLayout;
    private String[] dashboardCard;
    private Map<String, Collection<StateMachineState>> availableStates;
    private Map<String, Integer> reviwAlert = new HashMap<String, Integer>();
    private Map<String,String> userPreferences = new HashMap<String, String>();
    private String cardsPreferences;
    private String[] costRecordStatPref;
    private String[] sourcingLaneStatPref;
    private String[] adjustableforecastStatPref;
    private String[] forecastStatPref;
    private String[] rebateStatPref;
    private String[] bomStatPref;
    private String cardType="";

    public Date getLastLoadedDate() {
        return (lastLoaded > 0) ? new Date(lastLoaded) : null;
    }

    public long getLastLoaded() {
        return lastLoaded;
    }

    public void setLastLoaded(long lastLoaded) {
        this.lastLoaded = lastLoaded;
    }

    public void clearRecordStatus() {
        recordStatus.clear();
        recordStatusOwnerOnly.clear();
        recordStatusAge.clear();
    }

    public Map<String, List<?>> getRecordStatus() {
        return recordStatus;
    }

    public Map<String, Boolean> getRecordStatusOwnerOnly() {
        return recordStatusOwnerOnly;
    }

    public Map<String, Integer> getRecordStatusAge() {
        return recordStatusAge;
    }

    public void setRecordStatus(String recordType, List<?> status, boolean ownerOnly, Integer age) {
        recordStatus.put(recordType, status);
        recordStatusOwnerOnly.put(recordType, ownerOnly);
        recordStatusAge.put(recordType, age);
    }

    /**
     * @param currentNews
     *            The currentNews to set.
     */
    public void setUserAlerts(List<?> currentNews) {
        this.userAlerts = currentNews;
    }

    /**
     * @return Returns the currentNews.
     */
    public List<?> getUserAlerts() {
        return userAlerts;
    }

    public void setNewItemStatus(List<?> results) {
        newItemStatus = results;
    }

    public List<?> getNewItemStatus() {
        return newItemStatus;
    }

    public void setNewItemAge(int newItemAge) {
        this.newItemAge = newItemAge;
    }

    public int getNewItemAge() {
        return newItemAge;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean getRefresh() {
        return refresh;
    }

    public String getDashboardLayout() {
        return dashboardLayout;
    }

    public void setDashboardLayout(String dashboardLayout) {
        this.dashboardLayout = dashboardLayout;
    }

    public Map<String, Integer> getReviwAlert() {
        return reviwAlert;
    }

    public void setReviwAlert(Map<String, Integer> reviwAlert) {
        this.reviwAlert = reviwAlert;
    }

    public Set<String> getDashboardCards() {
        return dashboardCards;
    }

    public void setDashboardCards(Set<String> dashboardCards) {
        this.dashboardCards = dashboardCards;
    }

    public Set<String> getInactiveDashboardCards() {
        return inactiveDashboardCards;
    }

    public void setInactiveDashboardCards(Set<String> inactiveDashboardCards) {
        this.inactiveDashboardCards = inactiveDashboardCards;
    }

    public String[] getDashboardCard() {
        return dashboardCard;
    }

    public void setDashboardCard(String[] dashboardCard) {
        this.dashboardCard = dashboardCard;
    }

    public Map<String, Collection<StateMachineState>> getAvailableStates() {
        return availableStates;
    }

    public void setAvailableStates(Map<String, Collection<StateMachineState>> availableStates) {
        this.availableStates = availableStates;
    }

    public String[] getCostRecordStatPref() {
        return costRecordStatPref;
    }

    public void setCostRecordStatPref(String[] costRecordStatPref) {
        this.costRecordStatPref = costRecordStatPref;
    }

    public String[] getSourcingLaneStatPref() {
        return sourcingLaneStatPref;
    }

    public void setSourcingLaneStatPref(String[] sourcingLaneStatPref) {
        this.sourcingLaneStatPref = sourcingLaneStatPref;
    }

    public String[] getAdjustableforecastStatPref() {
        return adjustableforecastStatPref;
    }

    public void setAdjustableforecastStatPref(String[] adjustableforecastStatPref) {
        this.adjustableforecastStatPref = adjustableforecastStatPref;
    }

    public String[] getForecastStatPref() {
        return forecastStatPref;
    }

    public void setForecastStatPref(String[] forecastStatPref) {
        this.forecastStatPref = forecastStatPref;
    }

    public String[] getRebateStatPref() {
        return rebateStatPref;
    }

    public void setRebateStatPref(String[] rebateStatPref) {
        this.rebateStatPref = rebateStatPref;
    }

    public String[] getBomStatPref() {
        return bomStatPref;
    }

    public void setBomStatPref(String[] bomStatPref) {
        this.bomStatPref = bomStatPref;
    }

    public String getCardsPreferences() {
        return cardsPreferences;
    }

    public void setCardsPreferences(String cardsPreferences) {
        this.cardsPreferences = cardsPreferences;
    }

    public Map getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(Map userPreferences) {
        this.userPreferences =  new HashMap(userPreferences);
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

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }
}
