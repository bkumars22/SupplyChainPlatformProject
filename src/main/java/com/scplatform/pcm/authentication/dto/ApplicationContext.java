/*
 * Copyright (c) 2008 Supply Chain Platform. All Rights Reserved
 *
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 *
 * Copyright (c) 2008, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.authentication.dto;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.user.entity.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Application context for the user.   Contains all accessor methods for getting
 * the application context.   Locale, workflows, security, etc.
 */
@Setter
@Getter
@NoArgsConstructor
public class ApplicationContext implements java.io.Serializable
{
    public static final String SESSION_ATTR_NAME = ApplicationContext.class.getName();

    private List availableWorkflows = null;
    private Users currentUser;
    private String contentPageURL = "login.jsp";
    private Role currentRole;

    private List validSites;
    private Locale currentLocale;
    private TimeZone currentTimezone = TimeZone.getDefault();
    private String currentDateFormat;
    private String currentTimeFormat;
    boolean attritionRateAllowed=false;
    private Set validBusinessEntityKeys = new HashSet();
    private Date effactiveDate = new Date();
    private String reportType = "";
    private final HashMap<String,Set<String>> dataFilterKeys = new HashMap<String,Set<String>>();
    private Long enterpriseKey;

    public SimpleDateFormat getDateFormatter()
    {
                SimpleDateFormat sdf = new SimpleDateFormat(getCurrentDateFormat());
                sdf.setLenient(false);
                return sdf;
    }


}