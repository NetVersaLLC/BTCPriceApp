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

import com.xeiam.xchange.currency.Currencies;

public class MarketDataActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_data_activity);

        IntentFilter filter = new IntentFilter(FetchService.ACTION_RESPONSE);
        filter.addDataScheme(FetchService.DATA_SCHEME);
        registerReceiver(new FetchReceiver(), filter);

        startService(new Intent(FetchService.ACTION_REQUEST,
                    FetchService.marketTarget(MarketData.MT_GOX, Currencies.USD,
                        Currencies.BTC)));
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    protected class FetchReceiver extends BroadcastReceiver
    {
        public void onReceive(Context context, Intent intent)
        {
            if(!FetchService.ACTION_RESPONSE.equals(intent.getAction()) ||
                        intent.getData() == null)
            {
                return;
            }
        }
    }
}
