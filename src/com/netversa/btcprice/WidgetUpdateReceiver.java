/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

/** Update any active widgets with fetch data. 
 */
public class WidgetUpdateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        MarketData data = (MarketData)
            intent.getParcelableExtra(FetchService.EXTRA_MARKET_DATA);
        // does the intent have valid market data?
        if(data == null || data.exchangeName == null ||
                data.baseCurrency == null || data.counterCurrency == null ||
                data.lastPrice == null)
        {
            return;
        }

        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        // is the market data for the appropriate exchange and currencies?
        if(!data.exchangeName.equals(
                    prefs.getString("def_exchange", Defaults.DEF_EXCHANGE)) ||
                !data.baseCurrency.equals(
                    prefs.getString("def_base", Defaults.DEF_BASE)) ||
                !data.counterCurrency.equals(
                    prefs.getString("def_counter",
                        Defaults.DEF_COUNTER)))
        {
            return;
        }
        // does the system handle the chosen exchange?
        if(!Exchanges.instance().known(data.exchangeName))
        {
            return;
        }

        // find and update widgets
        AppWidgetManager wManager = AppWidgetManager.getInstance(context);

        for(int widgetId : wManager.getAppWidgetIds(new ComponentName(context,
                        PriceWidgetProvider.class)))
        {
            int remoteViewsId = R.layout.price_widget;
            int priceFormatId = R.string.price_terse_format;
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                Bundle options = wManager.getAppWidgetOptions(widgetId);
                int category = options.getInt(
                        AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
                if(category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD)
                {
                    remoteViewsId = R.layout.lockscreen_price_widget;
                    priceFormatId = R.string.price_format;
                }
            }

            RemoteViews widgetViews = new RemoteViews(context.getPackageName(),
                    remoteViewsId);

            String priceText = String.format(
                    context.getString(priceFormatId),
                    data.lastPrice);
            String currencyText = String.format(
                    context.getString(R.string.currency_pair_format),
                    data.baseCurrency, data.counterCurrency);
            String exchangeText =
                Exchanges.instance().label(context, data.exchangeName);

            widgetViews.setTextViewText(R.id.price, priceText);
            widgetViews.setTextViewText(R.id.currency, currencyText);
            widgetViews.setTextViewText(R.id.exchange, exchangeText);

            PendingIntent wIntent = PendingIntent.getActivity(context, 0,
                    context.getPackageManager().getLaunchIntentForPackage(
                        context.getPackageName()), 0);

            widgetViews.setOnClickPendingIntent(R.id.widget, wIntent);

            wManager.updateAppWidget(widgetId, widgetViews);
        }
    }
}
