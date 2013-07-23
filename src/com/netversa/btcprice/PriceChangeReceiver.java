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

/**
 * Notify the user whenever the price changes by a threshold.
 */
public class PriceChangeReceiver extends BroadcastReceiver
{
    public static final int NOTIF_PRICE_CHANGE = 1000100;
    private NotificationCompat.Builder notifBuilder;

    public PriceChangeReceiver()
    {
        notifBuilder = null;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);
        // does the user want ongoing notifications?
        if(!prefs.getBoolean("price_change", Defaults.PRICE_CHANGE))
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

        // build and show notification
        //NotificationManager notifs = (NotificationManager)
            //context.getSystemService(Context.NOTIFICATION_SERVICE);
//
        //String exchangeLabel =
            //Exchanges.instance().label(context, data.exchangeName);
        //String valueString =
            //String.format(context.getString(R.string.ongoing_price_format),
                    //data.lastPrice, data.counterCurrency);
        //String currencyString =
            //String.format(context.getString(R.string.ongoing_currency_format),
                    //exchangeLabel, data.baseCurrency);
//
        //PendingIntent notifIntent = PendingIntent.getActivity(context, 0,
                //context.getPackageManager().getLaunchIntentForPackage(
                    //"com.netversa.btcprice"), 0);
//
        //if(notifBuilder == null)
        //{
            //notifBuilder = new NotificationCompat.Builder(context);
        //}
        //notifBuilder.setSmallIcon(R.drawable.ongoing_price_icon)
               //.setContentTitle(valueString)
               //.setContentText(currencyString)
               //.setOngoing(true)
               //.setContentIntent(notifIntent);
        //notifs.notify(NOTIF_PRICE_CHANGE, notifBuilder.build());
    }

    public void setNotificationBuilder(NotificationCompat.Builder builder)
    {
        notifBuilder = builder;
    }

    public static void onStart(Context context)
    {
        FetchService.requestMarket(context);
    }

    public static void onStop(Context context)
    {
        NotificationManager notifs = (NotificationManager)
            context.getSystemService(Context.NOTIFICATION_SERVICE);

        notifs.cancel(NOTIF_PRICE_CHANGE);
    }
}
