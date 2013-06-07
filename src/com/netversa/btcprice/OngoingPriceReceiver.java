/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

/** Maintain an ongoing price notification by updating whenever anyone gets
 * fresh data.  Primarily fed by the periodic price update service, but will
 * opportunistically update whenever new data arrives.
 */
public class OngoingPriceReceiver extends BroadcastReceiver
{
    public static final int NOTIF_ONGOING_PRICE = 1000000;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);
        // does the user want ongoing notifications?
        if(!prefs.getBoolean("ongoing_price", Defaults.ONGOING_PRICE))
        {
            return;
        }
        MarketData data = (MarketData)
            intent.getParcelableExtra(FetchService.EXTRA_MARKET_DATA);
        // does the intent have valid market data?
        if(data == null || data.exchangeName == null ||
                data.baseCurrency == null || data.counterCurrency == null ||
                data.lastPrice == null)
        {
            return;
        }
        // is the market data for the appropriate exchange and currencies?
        if(!data.exchangeName.equals(
                    prefs.getString("exchange", Defaults.EXCHANGE)) ||
                !data.baseCurrency.equals(
                    prefs.getString("base_currency", Defaults.BASE_CURRENCY)) ||
                !data.counterCurrency.equals(
                    prefs.getString("counter_currency",
                        Defaults.COUNTER_CURRENCY)))
        {
            return;
        }

        // build and show notification
        NotificationManager notifs = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);

        String valueString =
            String.format(context.getString(R.string.price_format),
                    data.lastPrice);
        String currencyString =
            String.format(context.getString(R.string.currency_pair_format),
                    data.baseCurrency, data.counterCurrency);

        PendingIntent notifIntent = PendingIntent.getActivity(context, 0,
                context.getPackageManager().getLaunchIntentForPackage(
                    "com.netversa.btcprice"), 0);

        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.launcher_icon)
               .setContentTitle(valueString)
               .setContentText(currencyString)
               .setOngoing(true)
               .setContentIntent(notifIntent);
        notifs.notify(NOTIF_ONGOING_PRICE, builder.build());
    }
}