/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xeiam.xchange.currency.Currencies;

/** Simple activity to display and update prices on demand.
 */
public class MarketDataActivity extends BaseActivity
{
    protected MarketData marketData;
    protected MarketData cachedMarketData;
    protected String errorString;
    // by when should we be hearing back from FetchService?
    protected long expectResultsBy;
    // TODO recreate timeout from bundle
    protected Runnable timeout;

    protected BroadcastReceiver responseReceiver;
    protected Handler handler;

    // views
    protected TextView errorView;
    protected TextView stalenessView;
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
        timeout = new Runnable() {
            @Override
            public void run() {
                errorString = getString(R.string.fetch_error_timed_out);
                marketData = null;
                completeFetch();
            }
        };

        setContentView(R.layout.market_data_activity);

        // grab views
        errorView = (TextView) findViewById(R.id.error);
        stalenessView = (TextView) findViewById(R.id.staleness);
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

        // only automatically start a fetch if the activity is starting for the
        // first time
        if(savedInstanceState == null)
        {
            startFetch();
        }
        else
        {
            // otherwise if a fetch isn't underway display the current data
            if(expectResultsBy == 0)
            {
                displayData();
            }
            // if a fetch is underway, display refresh indicators or trigger
            // timeout as necessary
            else if(SystemClock.uptimeMillis() < expectResultsBy)
            {
                resumeFetch();
            }
            else
            {
                timeout.run();
            }
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        updateStaleness();
    }

    /** Start a fetch and sync the UI to it.
     */
    protected void startFetch()
    {
        initFetch(false);
    }

    /** Resync the UI with a fetch in progress.
     */
    protected void resumeFetch()
    {
        initFetch(true);
    }

    /** Tell the FetchService to get market data and hook into its response.
     * This method is only called by startFetch and resumeFetch.
     * @param resuming a fetch is already underway so don't modify any state
     */
    protected void initFetch(boolean resuming)
    {
        if(!resuming)
        {
            errorString = null;
        }
        displayFetchIndicators();

        FetchService.requestMarket(this, responseReceiver,
                prefs.getString("def_exchange", Defaults.DEF_EXCHANGE),
                prefs.getString("def_base", Defaults.DEF_BASE),
                prefs.getString("def_counter", Defaults.DEF_COUNTER));

        if(!resuming)
        {
            expectResultsBy = SystemClock.uptimeMillis() +
                prefs.getLong("fetch_timeout", Defaults.FETCH_TIMEOUT);
        }
        handler.postAtTime(timeout, expectResultsBy);
    }
    
    /** Show the user that a refresh is underway.
     */
    protected void displayFetchIndicators()
    {
        showError(errorString);
        stalenessView.setVisibility(View.GONE);

        priceView.setText(R.string.price_dummy);
        currencyView.setText(R.string.currency_pair_dummy);
        highPriceView.setText(R.string.high_price_dummy);
        lowPriceView.setText(R.string.low_price_dummy);
        volumeView.setText(R.string.volume_dummy);
    }

    /** Clean up after a refresh and display content.
     */
    protected void completeFetch()
    {
        expectResultsBy = 0;
        handler.removeCallbacks(timeout);
        try
        {
            unregisterReceiver(responseReceiver);
        }
        catch(IllegalArgumentException e)
        {
            // evidently Android doesn't have a way to unregister if necessary,
            // nor a way to check if a receiver is registered.
        }

        displayData();
    }

    /** Populate views with instance data.
     */
    protected void displayData()
    {
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

        updateStaleness();
    }

    protected void updateStaleness()
    {
        if(cachedMarketData == null || cachedMarketData.timestamp == null)
        {
            stalenessView.setVisibility(View.GONE);
            return;
        }

        if(CacheTools.isStale(this, cachedMarketData))
        {
            stalenessView.setText(
                    CacheTools.stalenessBanner(this, cachedMarketData));
            stalenessView.setVisibility(View.VISIBLE);
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
                startFetch();
                return true;
            case R.id.notifications:
                startActivity(new Intent().setClass(this,
                            NotificationConfigActivity.class));
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
            completeFetch();
        }
    }
}
