/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

/** Manage lifecycle and updating of numeric price widgets other than loading
 * them with fetched data.
 */
public class PriceWidgetProvider extends AppWidgetProvider
{
    /** When a widget is first placed or otherwise updated, put up a placeholder
     * graphic and request a market data fetch.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {
        RemoteViews widgetViews = new RemoteViews(context.getPackageName(),
                R.layout.price_widget_loading);

        PendingIntent wIntent = PendingIntent.getActivity(context, 0,
                context.getPackageManager().getLaunchIntentForPackage(
                    context.getPackageName()), 0);

        widgetViews.setOnClickPendingIntent(R.id.widget, wIntent);

        for(int widgetId : appWidgetIds)
        {
            appWidgetManager.updateAppWidget(widgetId, widgetViews);
        }

        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        FetchService.requestMarket(context, null,
                prefs.getString("def_exchange", Defaults.DEF_EXCHANGE),
                prefs.getString("def_base", Defaults.DEF_BASE),
                prefs.getString("def_counter", Defaults.DEF_COUNTER));
    }
}
