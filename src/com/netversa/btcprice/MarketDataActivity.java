/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

public class MarketDataActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_data_activity);

        IntentFilter filter = new IntentFilter(FetchService.ACTION_RESPONSE);
        filter.addDataScheme("exchangedata");
        registerReceiver(new FetchReceiver(),
                new IntentFilter(FetchService.ACTION_RESPONSE));
    }

    @Override
    public void onResume()
    {
        super.onResume();

        startService(new Intent(FetchService.ACTION_REQUEST, Uri.parse("exchangedata://mtgox/market/BTC/USD")));
    }

    protected class FetchReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            System.out.println("Received " + intent.getData().toString());
            unregisterReceiver(this);
        }
    }
}
