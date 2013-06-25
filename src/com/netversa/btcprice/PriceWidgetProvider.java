/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
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
        for(int widgetId : appWidgetIds)
        {
            updateWidget(context, appWidgetManager, widgetId);
        }

        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        FetchService.requestMarket(context, null,
                prefs.getString("def_exchange", Defaults.DEF_EXCHANGE),
                prefs.getString("def_base", Defaults.DEF_BASE),
                prefs.getString("def_counter", Defaults.DEF_COUNTER));
    }

    /** Update an individual widget's views.
     */
    private void updateWidget(Context context, AppWidgetManager wManager,
            int widgetId)
    {
        int remoteViewsId = R.layout.price_widget_loading;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            Bundle options = wManager.getAppWidgetOptions(widgetId);
            int category = options.getInt(
                    AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
            if(category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD)
            {
                remoteViewsId = R.layout.lockscreen_price_widget_loading;
            }
        }

        RemoteViews widgetViews = new RemoteViews(context.getPackageName(),
                remoteViewsId);

        PendingIntent wIntent = PendingIntent.getActivity(context, 0,
                context.getPackageManager().getLaunchIntentForPackage(
                    context.getPackageName()), 0);

        widgetViews.setOnClickPendingIntent(R.id.widget, wIntent);

        wManager.updateAppWidget(widgetId, widgetViews);
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
