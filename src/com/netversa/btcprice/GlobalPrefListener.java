/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.content.Context;
import android.content.SharedPreferences;

/** Listener to enforce side-affects of certain prefs.  For example, when a
 * service is enabled by a pref, the service should also be launched.
 */
public class GlobalPrefListener
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    protected Context ctx;

    public GlobalPrefListener(Context context)
    {
        ctx = context;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
        if("ongoing_price".equals(key))
        {
            if(prefs.getBoolean(key, Defaults.ONGOING_PRICE))
            {
                OngoingPriceReceiver.onStart(ctx);
            }
            else
            {
                OngoingPriceReceiver.onStop(ctx);
            }
            FetchScheduler.instance().reschedule(ctx);
        }
    }
}
