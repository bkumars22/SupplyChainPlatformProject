/**
 *	Tense.java
 *	Created on May 3, 2012
 *
 *	Copyright (c) 2012 E2open, Inc.
 *	All Rights Reserved.
 *
 *	THIS IS UNPUBLISHED PROPRIETARY SOURCE CODE OF E2open
 *	The copyright notice above does not evidence any
 *	actual or intended publication of such source code.
 *
 *	Author: dillo
 */
package com.scplatform.pcm.common.enums;

import com.scplatform.pcm.util.datetime.DateAndTimeUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Enumeration for time tense
 *
 * @author sgupta
 */
public enum Tense {
    PAST, PRESENT, FUTURE;

    /**
     * Figure out is the given period is in the past present or future based on the current time
     *
     * @param start the start date defaults to evergreen if not specified
     * @param end the end date defaults to evergreen if not specified
     * @return
     */
    public static Tense getTenseForPeriod(Date start, Date end) {
        Date now = Calendar.getInstance().getTime();
        return getTenseForPeriod(now, start, end);
    }

    /**
     * Figure out is the given period is in the past present or future based on the reference date
     *
     * @param referenceDate
     * @param start the start date defaults to evergreen if not specified
     * @param end the end date defaults to evergreen if not specified
     * @return
     */
    public static Tense getTenseForPeriod(Date referenceDate, Date start, Date end) {
        if (DateAndTimeUtils.between(referenceDate, start, end)) {
            return PRESENT;
        } else if (DateAndTimeUtils.before(referenceDate, start, false)) {
            return FUTURE;
        } else {
            return PAST;
        }
    }

}
