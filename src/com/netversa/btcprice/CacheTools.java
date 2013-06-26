/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** Various utility methods that aid in handling cached market data.
 */
public class CacheTools
{
    public static boolean isStale(Context context, MarketData data)
    {
        long stalenessMillis = System.currentTimeMillis() -
            data.timestamp.getTime();

        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        return stalenessMillis >= prefs.getLong("staleness_threshold",
                Defaults.STALENESS_THRESHOLD);
    }

    public static String stalenessBanner(Context context, MarketData data)
    {
        Date currentDate = new Date(System.currentTimeMillis());

        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        DateFormat dFmt =
            android.text.format.DateFormat.getDateFormat(context);
        String dateString = dFmt.format(data.timestamp);
        DateFormat tFmt =
            android.text.format.DateFormat.getTimeFormat(context);
        String timeString = tFmt.format(data.timestamp);
        String staleString = timeString;
        if(!dateString.equals(dFmt.format(currentDate)))
        {
            staleString += " " + dateString;
        }
        staleString = String.format(
                context.getString(R.string.as_of_format), staleString);

        return staleString;
    }
}
