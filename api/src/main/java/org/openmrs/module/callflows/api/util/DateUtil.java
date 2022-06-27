/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.module.callflows.api.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateUtil {

    private static final Log LOGGER = LogFactory.getLog(DateUtil.class);

    private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    private static final String DEFAULT_TIME_ZONE = "UTC";

    public static Date parse(String dateTime) {
        return parse(dateTime, null);
    }

    public static Date parse(String dateTime, String pattern) {
        String datePattern = pattern;
        if (StringUtils.isBlank(pattern)) {
            datePattern = ISO_DATE_TIME_FORMAT;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        Date result = null;
        try {
            result = simpleDateFormat.parse(dateTime);
        } catch (ParseException e) {
            LOGGER.error(String.format("Could not parse `%s` date using `%s` pattern", dateTime, datePattern));
        }
        return result;
    }

    public static Date now() {
        return getDateWithDefaultTimeZone(new Date());
    }

    public static Date plusSeconds(Date date, int duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, duration);
        return calendar.getTime();
    }

    public static Date getDateWithDefaultTimeZone(Date timestamp) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIME_ZONE));
        calendar.setTime(timestamp);
        return calendar.getTime();
    }

    public static String getDateWithLocalTimeZone(Date timestamp) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
        simpleDateFormat.setTimeZone(getLocalTimeZone());
        return simpleDateFormat.format(timestamp);
    }

    public static String dateToString(Date date, String timeZone, String pattern) {
        String datePattern = pattern;
        if (StringUtils.isBlank(pattern)) {
            datePattern = ISO_DATE_TIME_FORMAT;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        if (StringUtils.isNotBlank(timeZone)) {
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));
        }
        return simpleDateFormat.format(date);
    }

    public static String dateToString(Date date, String pattern) {
        return dateToString(date, DEFAULT_TIME_ZONE, pattern);
    }

    public static TimeZone getLocalTimeZone() {
        return TimeZone.getDefault();
    }

    private DateUtil() {
    }
}
