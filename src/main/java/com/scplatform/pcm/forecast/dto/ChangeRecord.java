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

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.forecast.service.ChangeRecordService;
import com.scplatform.pcm.site.entity.Site;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ChangeRecord
{
    protected String recordId;
    Map<String, String> changes = new HashMap<String, String>();
    private String recordOperation = null;

    public ChangeRecord()
    {
        super();
    }

    public Map<String,String> getChanges()
    {
        return changes;
    }

    public void setRecordOperation(String recordOperation)
    {
        this.recordOperation = recordOperation;
    }

    public String getRecordOperation()
    {
        return recordOperation;
    }

    private ChangeRecordService changeRecordService()
    {
        return SpringContextHolder.getBean(ChangeRecordService.class);
    }

    public void record(String field, String message)
    {
        changeRecordService().record(this, field, message);
    }

    public void record(String field, Object oldData, Object newData)
    {
        changeRecordService().record(this, field, oldData, newData);
    }

    public void record(String field, Site oldData, Site newData)
    {
        changeRecordService().record(this, field, oldData, newData);
    }

    public void record(String field, BigDecimal oldData, BigDecimal newData)
    {
        changeRecordService().record(this, field, oldData, newData);
    }

    public void record(String field, Date oldData, Date newData)
    {
        changeRecordService().record(this, field, oldData, newData);
    }

    public String getRecordId()
    {
        return recordId;
    }

    public void record(String field, Object oldData, Object newData, String transactionId)
    {
        changeRecordService().record(this, field, oldData, newData, transactionId);
    }

    /**
     * @param recordId the recordId to set
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }
}