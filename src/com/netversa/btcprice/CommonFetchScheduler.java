/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
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
    private Set<String> schedReqPrefs;

    private CommonFetchScheduler()
    {
        schedReqPrefs = new HashSet<String>();

        // INSERT SERVICE REQUIREMENT PREFS
    }

    public CommonFetchScheduler instance()
    {
        if(singleton == null)
        {
            singleton = new CommonFetchScheduler();
        }

        return singleton;
    }

    /** Select a time for the next common fetch.
     * @return milliseconds since system boot at which to run the next fetch,
     * or zero if no fetch is necessary.
     */
    public long planNextFetch(Context context)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        long lastFetch = prefs.getLong("last_fetch", Defaults.LAST_FETCH);

        BigInteger bestInterval = BigInteger.valueOf(0);

        for(String ee : schedReqPrefs)
        {
            long interval = prefs.getLong(ee, Defaults._FETCH_INTERVAL);
            if(interval == 0)
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

        long result = bestInterval.longValue();
        if(result == 0)
        {
            return 0;
        }
        result += lastFetch;

        return result;
    }
}
