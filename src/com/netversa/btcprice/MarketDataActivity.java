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
    protected MarketData marketData;
    // by when should we be hearing back from FetchService?
    protected long expectResultsBy;

    protected IntentFilter responseFilter;
    protected BroadcastReceiver responseReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_data_activity);

        responseFilter = new IntentFilter(FetchService.ACTION_RESPONSE);
        responseFilter.addDataScheme(FetchService.DATA_SCHEME);
        responseReceiver = new FetchReceiver();

        if(savedInstanceState != null)
        {
            marketData = (MarketData)
                savedInstanceState.getParcelable("marketData");
            expectResultsBy = savedInstanceState.getLong("expectResultsBy");
        }

        // if there's no market data to speak of fetch it.  If a fetch is in
        // progress the request will be ignored
        if(marketData == null)
        {
            startRefresh();
        }
    }

    /** Tell the FetchService to get market data and hook into its response.
     */
    protected void startRefresh()
    {
        registerReceiver(responseReceiver, responseFilter);

        FetchService.requestMarket(this, MarketData.MT_GOX, Currencies.USD,
                Currencies.BTC);
    }

    /** Take data from MarketData object and update views.
     */
    protected void completeRefresh()
    {
        unregisterReceiver(responseReceiver);
        if(marketData == null)
        {
            // TODO error display or so
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelable("marketData", marketData);
        savedInstanceState.putLong("expectResultsBy", expectResultsBy);
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
            // TODO double-check data URI
            completeRefresh();
        }
    }
}
