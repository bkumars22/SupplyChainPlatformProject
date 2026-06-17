/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.alert.dto;
import com.scplatform.pcm.alert.entity.Alert;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class AlertForm extends SearchForm {
    private String type;
    private String currentAlertTypeURL;
    private List<Alert> alert = new ArrayList<>();
    private Map<String, Integer> alertMap = new HashMap<>();
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCurrentAlertTypeURL() { return currentAlertTypeURL; }
    public void setCurrentAlertTypeURL(String currentAlertTypeURL) { this.currentAlertTypeURL = currentAlertTypeURL; }
    public List<Alert> getAlert() { return alert; }
    public void setAlert(List<Alert> alert) { this.alert = alert; }
    public Map<String, Integer> getAlertMap() { return alertMap; }
    public void setAlertMap(Map<String, Integer> alertMap) { this.alertMap = alertMap; }
}
