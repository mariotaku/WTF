package org.mariotaku.wtf.util;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import org.mariotaku.twidere.api.twitter.model.Status;
import org.mariotaku.twidere.model.ParcelableCredentials;

import java.io.Closeable;
import java.io.IOException;

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


    public static Uri getStatusWebLink(Status status, String accountType) {
        String url = status.getExternalUrl();
        if (!TextUtils.isEmpty(url)) {
            return Uri.parse(url);
        }
        if (ParcelableCredentials.Type.FANFOU.equals(accountType)) {
            return getFanfouStatusLink(status.getId());
        }
        return getTwitterStatusLink(status.getUser().getScreenName(), status.getId());
    }

    public static void closeSilently(Closeable c) {
        try {
            c.close();
        } catch (IOException e) {
            // Ignore
        }
    }

    static Uri getTwitterStatusLink(String screenName, String statusId) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("twitter.com");
        builder.appendPath(screenName);
        builder.appendPath("status");
        builder.appendPath(statusId);
        return builder.build();
    }

    static Uri getTwitterUserLink(String screenName) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https");
        builder.authority("twitter.com");
        builder.appendPath(screenName);
        return builder.build();
    }

    static Uri getFanfouStatusLink(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("fanfou.com");
        builder.appendPath("statuses");
        builder.appendPath(id);
        return builder.build();
    }

    static Uri getFanfouUserLink(String id) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http");
        builder.authority("fanfou.com");
        builder.appendPath(id);
        return builder.build();
    }
}
