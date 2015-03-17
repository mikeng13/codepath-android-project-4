package com.codepath.apps.simpletweeter.helpers;

import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mng on 3/7/15.
 *
 * Copied from @nesquena ParseRelativeDate.java (https://gist.github.com/nesquena/f786232f5ef72f6e10a7)
 */
public class ParseDateHelper {

    private final static String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawJsonDate) {
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static Date parseDate(String rawJsonDate) {
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);
        Date parsedDate = null;
        try {
            parsedDate = sf.parse(rawJsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    public static String getPrettyTimeStamp(String rawJsonDate) {
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String prettyDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();

            SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm a   dd MMM yy");
            prettyDate = newFormat.format(dateMillis);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return prettyDate;
    }
}
