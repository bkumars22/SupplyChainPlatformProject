/*
 * Copyright (c) 2007 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.cost.dto;

import com.scplatform.pcm.cost.entity.PcmCostElement;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import jakarta.servlet.http.HttpServletRequest;

import java.util.*;

public class CostRecordSummaryForm extends SearchForm {

    private String lineEvent;
    private String lineEventMessage;
    private LinkedHashSet<String> allCostElements;
    private SortedSet<PcmCostElement> pcmCostElements = new TreeSet<>();
    private Map<String, List<String>> lineMessages = new LinkedHashMap<>();

    @Override
    public void reset(HttpServletRequest request) {
        super.reset(request);
        lineEvent = null;
        lineEventMessage = null;
    }

    public String getLineEvent() {
        return lineEvent;
    }

    public void setLineEvent(String lineEvent) {
        this.lineEvent = lineEvent;
    }

    public String getLineEventMessage() {
        return lineEventMessage;
    }

    public void setLineEventMessage(String lineEventMessage) {
        this.lineEventMessage = lineEventMessage;
    }

    public LinkedHashSet<String> getAllCostElements() {
        return allCostElements;
    }

    public void setAllCostElements(LinkedHashSet<String> allCostElements) {
        this.allCostElements = allCostElements;
    }

    public SortedSet<PcmCostElement> getPcmCostElements() {
        return this.pcmCostElements;
    }

    public void setPcmCostElements(SortedSet<PcmCostElement> costElements) {
        this.pcmCostElements = costElements;
    }

    public void clearLineMessages() {
        lineMessages.clear();
    }

    public Map<String, List<String>> getLineMessages() {
        return lineMessages;
    }

    public void setLineMessages(Map<String, List<String>> lineMessages) {
        this.lineMessages = lineMessages;
    }

    public void addLineMessage(String type, String key, String message) {
        String mapKey = type + "|" + key;
        lineMessages.computeIfAbsent(mapKey, k -> new ArrayList<>()).add(message);
    }
}
