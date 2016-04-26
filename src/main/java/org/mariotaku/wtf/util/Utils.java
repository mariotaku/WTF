package org.mariotaku.wtf.util;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mariotaku on 16/4/26.
 */
public class Utils {

    @SuppressWarnings("deprecation")
    public static String formatSameDayTime(final Context context, final long timestamp) {
        if (context == null) return null;
        if (DateUtils.isToday(timestamp))
            return DateUtils.formatDateTime(context, timestamp,
                    DateFormat.is24HourFormat(context) ? DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR
                            : DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_12HOUR);
        return DateUtils.formatDateTime(context, timestamp, DateUtils.FORMAT_SHOW_DATE);
    }

    public static void closeSilently(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            // Ignore
        }
    }
}
