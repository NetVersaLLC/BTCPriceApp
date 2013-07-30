/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

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

/** Determine when next to fetch data automatically.  If a user wants an
 * ongoing price notification updated at least every hour, and price swings
 * checked for at least every fifteen minutes, background data fetches will be
 * scheduled every fifteen minutes.  This class is interacted with whenever a
 * scheduled fetch runs and whenever settings relating to the frequency of
 * background services are changed.
 */
public class FetchScheduler
{
    private static FetchScheduler singleton;

    // set of all sharedpreference keys that contain scheduling requirements of
    // background services
    private Set<SchedPrefs> schedReqPrefs;
    private PendingIntent activeCallback;

    private FetchScheduler()
    {
        activeCallback = null;
        schedReqPrefs = new HashSet<SchedPrefs>();

        schedReqPrefs.add(new SchedPrefs("ongoing_price",
                    "ongoing_price_interval"));
        schedReqPrefs.add(new SchedPrefs("price_widget_active",
                    "price_widget_interval"));
        schedReqPrefs.add(new SchedPrefs("price_change",
                    "price_change_interval"));
    }

    public static FetchScheduler instance()
    {
        if(singleton == null)
        {
            singleton = new FetchScheduler();
        }

        return singleton;
    }

    /** Select an interval for background fetches.
     * @return milliseconds preferred delay between fetches or zero if no fetch
     * is necessary.
     */
    public long getFetchInterval(Context context)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        long bestInterval = 0;

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
            if(bestInterval == 0)
            {
                bestInterval = interval;
                continue;
            }

            bestInterval = Math.min(bestInterval, interval);
        }

        return bestInterval;
    }

    /** Select a time for the next scheduled fetch.
     * @return milliseconds since system boot at which to run the next fetch,
     * or zero if no fetch is necessary.
     */
    public long getNextFetchTime(Context context)
    {
        long interval = getFetchInterval(context);
        if(interval == 0)
        {
            return 0;
        }
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        long lastFetch = prefs.getLong("last_sched_fetch",
                Defaults.LAST_SCHED_FETCH);

        return lastFetch + interval;
    }

    /** Register scheduled fetch callbacks with Android, clearing any currently
     * registered callbacks.
     */
    public void reschedule(Context context)
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
     * periodically for scheduled fetches.
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
        intent.putExtra(FetchService.EXTRA_SCHED_FETCH, true);

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
