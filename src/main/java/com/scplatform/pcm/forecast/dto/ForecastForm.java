/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.forecast.dto;

import java.util.*;

import com.scplatform.pcm.common.entity.FlexAttributeDefn;
import com.scplatform.pcm.common.entity.FlexAttributeManager;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.forecast.entity.PcmForecastValue;
import com.scplatform.pcm.forecast.enums.ForecastModel;
import com.scplatform.pcm.forecast.dto.ForecastFormRecordData;
import com.scplatform.pcm.searchframework.dto.SearchForm;
import com.scplatform.pcm.site.entity.Site;
import jakarta.servlet.http.HttpServletRequest;


@SuppressWarnings("serial")
public class ForecastForm extends SearchForm
{

    protected String backAction;
    protected boolean unsavedData;
    protected boolean lockError;

    protected boolean showAllColumns = true;
    protected String[] selectedRecordKeys;
    protected String eventName;
    protected String eventMessage;
    protected String downloadForecastModel= ForecastModel.ADJUSTABLE.name();

    protected String selectedForecastTab =ForecastModel.ADJUSTABLE.name();
    protected String selectedForecastModel;
    protected String forecastType = null;
    protected String calendarName = null;
    protected String periodType = null;
    protected ForecastTimeline adjustableTimeline;
    protected ForecastTimeline currentTimeline;
    protected int minRolloverPeriods = 0;
    protected int maxRolloverPeriods = 0;
    protected boolean allowNegativeValues = false;
    protected List<Site> forecastSites = null;
    protected Site defaultSite = null;
    protected boolean validAdjustable=true;
    protected boolean validCurrent=true;


    protected String adjustmentType;
    protected Map<String,PcmForecast> forecastRecords = new HashMap<String,PcmForecast>();
    protected Map<Long,PcmForecast> forecastRecordsByKey = new HashMap<Long, PcmForecast>();
    protected Set<Long> recordsMarkedForDelete = new HashSet<Long>();
    protected Map<String, ForecastFormRecordData> forecastRecordData = new HashMap<String,ForecastFormRecordData>();
    protected IdentityHashMap<PcmForecast, ForecastChange> forecastRecordChanges = new IdentityHashMap<PcmForecast, ForecastChange>();
    protected ForecastSorter forecastSorter = new ForecastSorter();
    private PcmForecastValue forecastValue;

    public ForecastForm(String key, Object[] objects) {

    }

    @Override
    public void reset( HttpServletRequest request)
    {
        super.reset( request);
        backAction = null;
        lockError = false;
        showAllColumns = true;
        eventName = null;
        eventMessage = null;
        downloadForecastModel = null;
        unsavedData = false;
        forecastType = null;
        calendarName = null;
        periodType = null;
        selectedRecordKeys = null;
        validAdjustable=true;
        validCurrent=true;
        forecastRecordData.clear();
    }


    public String[] getSelectedRecordKeys()
    {
        return selectedRecordKeys;
    }

    public void setSelectedRecordKeys(String[] selectedRecordKeys)
    {
        this.selectedRecordKeys = selectedRecordKeys;
    }

    public void setLockError(boolean lockError)
    {
        this.lockError = lockError;
    }

    public boolean getLockError()
    {
        return lockError;
    }

    public boolean getShowAllColumns()
    {
        return showAllColumns;
    }


    public void setShowAllColumns(boolean showAllColumns)
    {
        this.showAllColumns = showAllColumns;
    }

    public String getForecastType()
    {
        return forecastType;
    }

    public void setForecastType(String forecastType)
    {
        this.forecastType = forecastType;
    }

    public String getCalendarName()
    {
        return calendarName;
    }

    public void setCalendarName(String calendarName)
    {
        this.calendarName = calendarName;
    }

    public String getPeriodType()
    {
        return periodType;
    }

    public void setPeriodType(String periodType)
    {
        this.periodType = periodType;
    }


    public ForecastTimeline getAdjustableTimeline()
    {
        return adjustableTimeline;
    }

    public void setAdjustableTimeline(ForecastTimeline adjustableTimeline)
    {
        this.adjustableTimeline = adjustableTimeline;
    }

    public ForecastTimeline getCurrentTimeline()
    {
        return currentTimeline;
    }

    public void setCurrentTimeline(ForecastTimeline currentTimeline)
    {
        this.currentTimeline = currentTimeline;
    }


    public ForecastTimeline getTimeline()
    {
        return currentTimeline != null ? currentTimeline : adjustableTimeline;
    }

    public String getBackAction()
    {
        return backAction;
    }

    public void setBackAction(String backAction)
    {
        this.backAction = backAction;
    }

    public boolean getUnsavedData()
    {
        return unsavedData;
    }

    public void setUnsavedData(boolean unsavedData)
    {
        this.unsavedData = unsavedData;
    }

    public String getEventName()
    {
        return eventName;
    }

    public void setEventName(String eventName)
    {
        this.eventName = eventName;
    }

    public String getEventMessage()
    {
        return eventMessage;
    }

    public void setEventMessage(String eventMessage)
    {
        this.eventMessage = eventMessage;
    }

    public String getDownloadForecastModel()
    {
        return downloadForecastModel;
    }

    public void setDownloadForecastModel(String downloadForecastModel)
    {
        this.downloadForecastModel = downloadForecastModel;
    }

    public void setDefaultDownloadModel(PcmForecast forecast) {
        String currentModelStr = ForecastModel.CURRENT.name();
        if(currentModelStr.equals(downloadForecastModel)) {
            return;
        } else if (ForecastModel.CURRENT == forecast.getForecastModel()) {
            setDownloadForecastModel(currentModelStr);
        }

    }

    public boolean isValidAdjustable() {
        return validAdjustable;
    }


    public void setValidAdjustable(boolean validAdjustable) {
        this.validAdjustable = validAdjustable;
    }


    public boolean isValidCurrent() {
        return validCurrent;
    }


    public void setValidCurrent(boolean validCurrent) {
        this.validCurrent = validCurrent;
    }

    public void setSites(List<Site> sites)
    {
        forecastSites = new ArrayList<Site>(sites);
    }

    public List<Site> getSites()
    {
        return forecastSites;
    }

    public Site getSite(long siteKey)
    {
        for (Site site: forecastSites)
        {
            if (site.getSiteKey() == siteKey)
            {
                return site;
            }
        }
        return  null;
    }

    public void setDefaultSite(Site defaultSite)
    {
        this.defaultSite = defaultSite;
    }

    public Site getDefaultSite()
    {
        return defaultSite;
    }

    public int getMinRolloverPeriods()
    {
        return minRolloverPeriods;
    }

    public void setMinRolloverPeriods(int minRolloverPeriods)
    {
        this.minRolloverPeriods = minRolloverPeriods;
    }

    public String getSelectedForecastTab() {
        return selectedForecastTab;
    }


    public void setSelectedForecastTab(String selectedForecastTab) {
        this.selectedForecastTab = selectedForecastTab;
    }

    public void setDefaultTabUsingForecast(PcmForecast forecast) {
        String currentModelStr = ForecastModel.CURRENT.name();
        if(currentModelStr.equals(selectedForecastTab)) {
            return;
        } else if (ForecastModel.CURRENT == forecast.getForecastModel()) {
            setSelectedForecastTab(currentModelStr);
        }

    }

    public int getMaxRolloverPeriods()
    {
        return maxRolloverPeriods;
    }

    public void setMaxRolloverPeriods(int maxRolloverPeriods)
    {
        this.maxRolloverPeriods = maxRolloverPeriods;
    }


    public void setAllowNegativeValues(boolean allowNegativeValues)
    {
        this.allowNegativeValues = allowNegativeValues;
    }

    public boolean getAllowNegativeValues()
    {
        return allowNegativeValues;
    }

    public Set<Long> getRecordsMarkedForDelete()
    {
        return recordsMarkedForDelete;
    }

    public void clearForecastRecords()
    {
        this.forecastRecordsByKey.clear();
        forecastRecords.clear();
        recordsMarkedForDelete.clear();
    }

    public String getSelectedForecastModel() {
        return selectedForecastModel;
    }

    public void setSelectedForecastModel(String selectedForecastModel) {
        this.selectedForecastModel = selectedForecastModel;
    }

    public List<PcmForecast> getForecastRecords()
    {
        List<PcmForecast> sorted = new ArrayList<PcmForecast>(forecastRecords.values());
        Collections.sort(sorted, forecastSorter);
        return sorted;
    }

    public List<PcmForecast> getForecastRecordsBasedOnModel(String modelstr){
        ForecastModel model = ForecastModel.valueOf(modelstr);
        List<PcmForecast> forecastsBasedOnModel=new LinkedList<PcmForecast>();
        List<PcmForecast> forecasts=getForecastRecords();
        for(PcmForecast forecast:forecasts){
            if(forecast.getForecastModel() == model){
                forecastsBasedOnModel.add(forecast);
            }
        }
        return forecastsBasedOnModel;
    }

    public void addForecastRecords(List<PcmForecast> records)
    {
        for (PcmForecast forecast: records)
        {
            addForecastRecord(forecast);
        }
    }

    public PcmForecast addForecastRecord(PcmForecast forecast)
    {
        this.forecastRecordsByKey.put(forecast.getForecastKey(), forecast);
        return forecastRecords.put(forecast.getForecastExternalId(), forecast);
    }

    public PcmForecast getForecastRecord(String forecastRecordId)
    {
        return forecastRecords.get(forecastRecordId);
    }

    public PcmForecast getForecastRecordByKey(Long forecastRecordKey)
    {
        return forecastRecordsByKey.get(forecastRecordKey);
    }


    public PcmForecast removeForecastRecord(String forecastRecordId)
    {
        PcmForecast record = forecastRecords.remove(forecastRecordId);
        forecastRecordData.remove(forecastRecordId);
        return record;
    }

    public PcmForecast markForDelete(String forecastRecordId)
    {
        PcmForecast record = removeForecastRecord(forecastRecordId);
        if (record != null && record.getForecastKey() != null)
        {
            recordsMarkedForDelete.add(record.getForecastKey());
        }
        return record;
    }

    public ForecastFormRecordData getForecastData(String key)
    {

        ForecastFormRecordData data = forecastRecordData.get(key);
        if (data == null)
        {

            PcmForecast forecast = getForecastRecord(key);
            // This will create a form record initialized with the data from
            // the record.
            data = new ForecastFormRecordData(forecast);
            forecastRecordData.put(key, data);
        }
        return data;
    }

    public Map<String, ForecastFormRecordData> getForecastRecordData()
    {
        return forecastRecordData;
    }

    public Collection<PcmForecast> getForecastRecordsCollection()
    {
        return forecastRecords.values();
    }

    public PcmForecastValue getForecastValue(String forecastValueKey)
    {
        String[] parts = forecastValueKey.split("\\.");
        PcmForecast record = getForecastRecord(parts[0]);
        if (record != null)
        {
            Date startDate = new Date();
            startDate.setTime(Long.parseLong(parts[1]));
            return record.getForecastValue(parts[2], startDate);
        }
        return null;
    }

    protected void clearChangeRecords()
    {
        forecastRecordChanges.clear();
    }

    protected IdentityHashMap<PcmForecast,ForecastChange> getChangeRecords()
    {
        return forecastRecordChanges ;
    }

    public ForecastChange getChangeRecord(PcmForecast target)
    {
        ForecastChange change = forecastRecordChanges.get(target);
        if (change == null)
        {
            change = new ForecastChange(target);
            forecastRecordChanges.put(target, change);
        }
        return change;
    }



    public List<FlexAttributeDefn> getFlexAttributeDefnListForecast()
    {
        return FlexAttributeManager.COSTFORECAST.getFlexAttributeDefinitionList();
    }
}
