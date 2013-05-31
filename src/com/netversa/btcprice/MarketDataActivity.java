/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xeiam.xchange.currency.Currencies;

/** Simple activity to display and update prices on demand.
 */
public class MarketDataActivity extends Activity
{
    protected MarketData marketData;
    protected MarketData cachedMarketData;
    protected String errorString;
    // by when should we be hearing back from FetchService?
    protected long expectResultsBy;
    // TODO recreate timeout from bundle
    protected ToggleRunnable timeout;

    protected BroadcastReceiver responseReceiver;
    protected Handler handler;

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

        handler = new Handler();

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

        responseReceiver = new FetchReceiver();

        if(savedInstanceState != null)
        {
            marketData = (MarketData)
                savedInstanceState.getParcelable("marketData");
            cachedMarketData = (MarketData)
                savedInstanceState.getParcelable("cachedMarketData");
            errorString = savedInstanceState.getString("errorString");
            expectResultsBy = savedInstanceState.getLong("expectResultsBy");
        }

        // if there's no market data to speak of, fetch it.  If a fetch is in
        // progress the request will be ignored
        if(cachedMarketData == null)
        {
            startRefresh();
        }
        else
        {
            completeRefresh();
        }
    }

    /** Tell the FetchService to get market data and hook into its response.
     */
    protected void startRefresh()
    {
        showError(null);

        priceView.setText(R.string.price_dummy);
        currencyView.setText(R.string.currency_pair_dummy);
        highPriceView.setText(R.string.high_price_dummy);
        lowPriceView.setText(R.string.low_price_dummy);
        volumeView.setText(R.string.volume_dummy);

        FetchService.requestMarket(this, responseReceiver, MarketData.MT_GOX,
                Currencies.BTC, Currencies.USD);
    }

    /** Take data from MarketData object and update views.
     */
    protected void completeRefresh()
    {
        try
        {
            unregisterReceiver(responseReceiver);
        }
        catch(IllegalArgumentException e)
        {
            // evidently Android doesn't have a way to unregister if necessary,
            // nor a way to check if a receiver is registered.
        }

        if(errorString != null)
        {
            showError(errorString);
        }

        // if the fetch was successful, the cached data will be the freshest,
        // if not, then reverting to the stale cached data if available is
        // better than remaining blank
        if(cachedMarketData != null)
        {
            priceView.setText(String.format(getString(R.string.price_format),
                        cachedMarketData.lastPrice));
            currencyView.setText(
                    String.format(getString(R.string.currency_pair_format),
                        cachedMarketData.baseCurrency,
                        cachedMarketData.counterCurrency));
            highPriceView.setText(
                    String.format(getString(R.string.high_price_format),
                        cachedMarketData.highPrice));
            lowPriceView.setText(
                    String.format(getString(R.string.low_price_format),
                        cachedMarketData.lowPrice));
            volumeView.setText(String.format(getString(R.string.volume_format),
                        cachedMarketData.volume));
        }
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
        savedInstanceState.putParcelable("cachedMarketData", cachedMarketData);
        savedInstanceState.putString("errorString", errorString);
        savedInstanceState.putLong("expectResultsBy", expectResultsBy);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.refresh:
                startRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.market_data_activity, menu);
        return true;
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
            errorString =
                intent.getStringExtra(FetchService.EXTRA_ERROR_STRING);
            marketData = (MarketData)
                intent.getParcelableExtra(FetchService.EXTRA_MARKET_DATA);
            if(marketData != null)
            {
                cachedMarketData = marketData;
            }
            if(marketData == null && errorString == null)
            {
                errorString = getString(R.string.fetch_error_generic);
            }
            completeRefresh();
        }
    }
}
