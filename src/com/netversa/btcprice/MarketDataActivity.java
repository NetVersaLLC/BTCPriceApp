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
import android.view.View;
import android.widget.TextView;

import com.xeiam.xchange.currency.Currencies;

public class MarketDataActivity extends Activity
{
    protected MarketData marketData;
    // by when should we be hearing back from FetchService?
    protected long expectResultsBy;

    protected IntentFilter responseFilter;
    protected BroadcastReceiver responseReceiver;

    // views
    protected TextView errorView;
    protected TextView priceView;
    protected TextView currencyView;
    protected TextView highPriceView;
    protected TextView lowPriceView;
    protected TextView volumeView;
    protected CandlestickChartView chartView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.market_data_activity);

        // grab views
        errorView = (TextView) findViewById(R.id.error);
        priceView = (TextView) findViewById(R.id.price);
        currencyView = (TextView) findViewById(R.id.currency);
        highPriceView = (TextView) findViewById(R.id.high_price);
        lowPriceView = (TextView) findViewById(R.id.low_price);
        volumeView = (TextView) findViewById(R.id.volume);
        chartView = (CandlestickChartView) findViewById(R.id.chart);

        // setup data

        responseFilter = new IntentFilter(FetchService.ACTION_RESPONSE);
        responseFilter.addDataScheme(FetchService.DATA_SCHEME);
        responseReceiver = new FetchReceiver();

        if(savedInstanceState != null)
        {
            marketData = (MarketData)
                savedInstanceState.getParcelable("marketData");
            expectResultsBy = savedInstanceState.getLong("expectResultsBy");
        }

        // if there's no market data to speak of, fetch it.  If a fetch is in
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
        showError(null);

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
            showError(getString(R.string.fetch_error_generic));
        }
        priceView.setText(String.format(getString(R.string.price_format),
                    marketData.lastPrice));
        currencyView.setText(
                String.format(getString(R.string.currency_pair_format),
                    marketData.baseCurrency, marketData.counterCurrency));
        highPriceView.setText(
                String.format(getString(R.string.high_price_format),
                    marketData.highPrice));
        lowPriceView.setText(
                String.format(getString(R.string.low_price_format),
                    marketData.lowPrice));
        volumeView.setText(String.format(getString(R.string.volume_format),
                    marketData.volume));
    }

    /** Reveal the error view and show a message in it, or hide it.
     *  @param error String to display or null to clear and hide error view
     */
    protected void showError(String error)
    {
        if(error == null)
        {
            errorView.setVisibility(View.GONE);
            errorView.setText("");
            return;
        }
        errorView.setText(error);
        errorView.setVisibility(View.VISIBLE);
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
            marketData = (MarketData)
                intent.getParcelableExtra(FetchService.EXTRA_MARKET_DATA);
            completeRefresh();
        }
    }
}
