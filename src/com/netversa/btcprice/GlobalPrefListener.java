/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

/** Listener to enforce side-affects of certain prefs.  For example, when a
 * service is enabled by a pref, the service should also be launched.
 */
public class GlobalPrefListener
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    protected Context ctx;
    protected NotificationManager notifs;

    public GlobalPrefListener(Context context)
    {
        ctx = context;
        notifs = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
        if("ongoing_price".equals(key))
        {
            if(prefs.getBoolean(key, Defaults.ONGOING_PRICE))
            {
                FetchService.requestMarket(ctx);
            }
            else
            {
                notifs.cancel(OngoingPriceReceiver.NOTIF_ONGOING_PRICE);
            }
        }
    }
}
