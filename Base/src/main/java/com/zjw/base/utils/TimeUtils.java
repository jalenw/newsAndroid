package com.zjw.base.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Frank on 2017/6/20.
 */

public class TimeUtils {

    /**
     * 获取utc时间
     * @return
     */
    public static CharSequence getUTCTime() {
        Calendar cal = Calendar.getInstance(Locale.CHINA);
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return DateFormat.format("yyyy'-'MM'-'dd'T'kk':'mm':'ss", cal);
    }
}
