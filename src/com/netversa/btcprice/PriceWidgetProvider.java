/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PriceWidgetProvider extends AppWidgetProvider
{
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        FetchService.requestMarket(context, null,
                prefs.getString("def_exchange", Defaults.DEF_EXCHANGE),
                prefs.getString("def_base", Defaults.DEF_BASE),
                prefs.getString("def_counter", Defaults.DEF_COUNTER));
    }
}
