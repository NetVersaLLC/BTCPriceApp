/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
            RemoteViews widgetViews = new RemoteViews(context.getPackageName(),
                    R.layout.price_widget);

            String priceText = String.format(
                    context.getString(R.string.price_terse_format),
                    data.lastPrice);
            String currencyText = String.format(
                    context.getString(R.string.currency_pair_format),
                    data.baseCurrency, data.counterCurrency);
            String exchangeText =
                Exchanges.instance().label(context, data.exchangeName);

            widgetViews.setTextViewText(R.id.price, priceText);
            widgetViews.setTextViewText(R.id.currency, currencyText);
            widgetViews.setTextViewText(R.id.exchange, exchangeText);

            wManager.updateAppWidget(widgetId, widgetViews);
        }
    }
}
