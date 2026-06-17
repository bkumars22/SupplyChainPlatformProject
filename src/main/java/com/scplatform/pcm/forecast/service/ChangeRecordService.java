/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.forecast.service;

import com.scplatform.pcm.forecast.dto.ChangeRecord;
import com.scplatform.pcm.forecast.entity.PcmForecast;
import com.scplatform.pcm.site.entity.Site;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * Service layer holding the change-recording logic that previously lived
 * inside the {@link ChangeRecord} DTO.
 *
 * <p>Logic, conditions, formatting strings and variable names are kept
 * byte-for-byte identical to the original implementation in
 * {@link ChangeRecord}; only the enclosing layer has changed.
 */
@Service
public class ChangeRecordService
{
    public String generateRecordId(PcmForecast target)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(target.getTitle());
        return sb.toString();
    }

    public void record(ChangeRecord changeRecord, String field, String message)
    {
        Map<String, String> changes = changeRecord.getChanges();
        changes.put(field, message);
    }

    public void record(ChangeRecord changeRecord, String field, Object oldData, Object newData)
    {
        Map<String, String> changes = changeRecord.getChanges();
        if (oldData != null)
        {
            if (newData == null)
            {
                changes.put(field, "Cleared:" + oldData);
            }
            else {
                if(oldData instanceof BigDecimal) {
                    if (((BigDecimal)oldData).compareTo((BigDecimal)newData) != 0)
                    {
                        changes.put(field, "From:" + oldData + " To:" + newData);
                    }
                }
                else if (oldData.equals(newData) == false)
                {
                    changes.put(field, "From:" + oldData + " To:" + newData);
                }
            }
        }
        else if (oldData == null && newData != null)
        {
            changes.put(field, "Assign:" + newData);
        }
        else if (oldData == null && newData == null && field.contains("CREATE")) {
            changes.put(field, "");
        }
    }

    public void record(ChangeRecord changeRecord, String field, Site oldData, Site newData)
    {
        Map<String, String> changes = changeRecord.getChanges();
        if (oldData != null)
        {
            if (newData == null)
            {
                changes.put(field, "Cleared:" + oldData);
            }
            else if (oldData.getSiteName().compareTo(newData.getSiteName()) != 0)
            {
                changes.put(field, "From:" + oldData + " To:" + newData);
            }
        }
        else if (oldData == null && newData != null)
        {
            changes.put(field, "Assign:" + newData);
        }
    }

    public void record(ChangeRecord changeRecord, String field, BigDecimal oldData, BigDecimal newData)
    {
        Map<String, String> changes = changeRecord.getChanges();
        if (oldData != null)
        {
            if (newData == null)
            {
                changes.put(field, "Cleared:" + oldData);
            }
            else if (oldData.compareTo(newData) != 0)
            {
                changes.put(field, "From:" + oldData + " To:" + newData);
            }
        }
        else if (oldData == null && newData != null)
        {
            changes.put(field, "Assign:" + newData);
        }
    }

    public void record(ChangeRecord changeRecord, String field, Date oldData, Date newData)
    {
        Map<String, String> changes = changeRecord.getChanges();
        if (oldData != null)
        {
            if (newData == null)
            {
                changes.put(field, "Cleared:" + oldData);
            }
            else if (oldData.compareTo(newData) != 0)
            {
                changes.put(field, "From:" + oldData + " To:" + newData);
            }
        }
        else if (oldData == null && newData != null)
        {
            changes.put(field, "Assign:" + newData);
        }
    }

    public void record(ChangeRecord changeRecord, String field, Object oldData, Object newData, String transactionId)
    {
        Map<String, String> changes = changeRecord.getChanges();
        if (oldData != null)
        {
            if (StringUtils.isBlank(newData.toString()))
            {
                changes.put(field, "Cleared:" + oldData + " ;Transaction:" + transactionId);
            }
            else {
                if(oldData instanceof BigDecimal) {
                    if (((BigDecimal)oldData).compareTo((BigDecimal)newData) != 0)
                    {
                        changes.put(field, "From:" + oldData + " To:" + newData + " ;Transaction:" + transactionId);
                    }
                }
                else if (oldData.equals(newData) == false)
                {
                    changes.put(field, "From:" + oldData + " To:" + newData + " ;Transaction:" + transactionId);
                }
            }
        }
        else if (oldData == null && newData != null)
        {
            changes.put(field, "Assign:" + newData + " ;Transaction:" + transactionId);
        }
        else if (oldData == null && newData == null && field.contains("CREATE")) {
            changes.put(field, "" + " ;Transaction:" + transactionId);
        }
    }
}
