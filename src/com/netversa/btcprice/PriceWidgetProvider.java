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
    /** When a widget is first placed or otherwise updated outside of a market
     * fetch result, put up a placeholder graphic and request a market data
     * fetch.
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
            int[] appWidgetIds)
    {
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

    /** When the first widget is placed, mark price widgets as active in global
     * prefs so their needs are accounted for in fetch scheduling.
     */
    @Override
    public void onEnabled(Context context)
    {
        setWidgetsActive(context, true);
    }

    /** When the last widget is placed, mark price widgets as inactive in
     * global prefs so no fetches are scheduled on their behalf.
     */
    @Override
    public void onDisabled(Context context)
    {
        setWidgetsActive(context, false);
    }

    public void setWidgetsActive(Context context, boolean active)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        prefs.edit()
            .putBoolean("price_widget_active", active)
            .commit();

        FetchScheduler.instance().reschedule(context);
    }
}
