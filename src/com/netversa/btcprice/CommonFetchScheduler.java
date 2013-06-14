/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.SystemClock;
import android.preference.PreferenceManager;

/** Determine when next to fetch data automatically.  If a user want's an
 * ongoing price notification updated at least every hour, and price swings
 * checked for at least every fifteen minutes, background data fetches will be
 * scheduled every fifteen minutes.  This class is interacted with whenever a
 * common fetch runs and whenever settings relating to the frequency of
 * background services are changed.
 */
public class CommonFetchScheduler
{
    private static CommonFetchScheduler singleton;

    // set of all sharedpreference keys that contain scheduling requirements of
    // background services
    private Set<SchedPrefs> schedReqPrefs;
    private PendingIntent activeCallback;

    private CommonFetchScheduler()
    {
        activeCallback = null;
        schedReqPrefs = new HashSet<SchedPrefs>();

        schedReqPrefs.add(new SchedPrefs("ongoing_price",
                    "ongoing_price_interval"));
    }

    public static CommonFetchScheduler instance()
    {
        if(singleton == null)
        {
            singleton = new CommonFetchScheduler();
        }

        return singleton;
    }

    /** Select an interval for common fetches.
     * @return milliseconds preferred delay between fetches or zero if no fetch
     * is necessary.
     */
    public long getFetchInterval(Context context)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        BigInteger bestInterval = BigInteger.valueOf(0);

        for(SchedPrefs ee : schedReqPrefs)
        {
            boolean enabled = prefs.getBoolean(ee.enabled,
                    Defaults._FETCH_ENABLED);
            long interval = prefs.getLong(ee.interval,
                    Defaults._FETCH_INTERVAL);
            if(!enabled || interval == 0)
            {
                continue;
            }
            if(bestInterval.longValue() == 0)
            {
                bestInterval = BigInteger.valueOf(interval);
                continue;
            }

            bestInterval = bestInterval.gcd(BigInteger.valueOf(interval));
        }

        return bestInterval.longValue();
    }

    /** Select a time for the next common fetch.
     * @return milliseconds since system boot at which to run the next fetch,
     * or zero if no fetch is necessary.
     */
    public long planNextFetch(Context context)
    {
        long interval = getFetchInterval(context);
        if(interval == 0)
        {
            return 0;
        }
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        long lastFetch = prefs.getLong("last_fetch", Defaults.LAST_FETCH);

        return lastFetch + interval;
    }

    /** Register common fetch callbacks with Android, clearing any currently
     * registered callbacks.
     */
    public void rescheduleFetches(Context context)
    {
        AlarmManager scheduler = (AlarmManager)
            context.getSystemService(Context.ALARM_SERVICE);

        if(activeCallback != null)
        {
            scheduler.cancel(activeCallback);
        }

        long interval = getFetchInterval(context);
        if(interval == 0)
        {
            return;
        }

        PendingIntent callback = getCallback(context);
        activeCallback = callback;

        scheduler.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + interval, interval, callback);
    }

    /** Return the PendingIntent that is registered with Android to be called
     * periodically for common fetches.
     */
    public PendingIntent getCallback(Context context)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        String exchangeName = prefs.getString("def_exchange",
                Defaults.DEF_EXCHANGE);
        String baseCurrency = prefs.getString("def_base",
                Defaults.DEF_BASE);
        String counterCurrency = prefs.getString("def_counter",
                Defaults.DEF_COUNTER);

        Uri target = FetchService.marketTarget(exchangeName, baseCurrency,
                counterCurrency);

        Intent intent = new Intent(FetchService.ACTION_REQUEST, target);

        PendingIntent pendIntent = PendingIntent.getService(context, 0, intent,
                0);

        return pendIntent;
    }

    public static class SchedPrefs
    {
        public String enabled;
        public String interval;

        public SchedPrefs(String enabled_, String interval_)
        {
            enabled = enabled_;
            interval = interval_;
        }
    }
}
