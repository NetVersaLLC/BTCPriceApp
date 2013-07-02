/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.PatternMatcher;

import java.math.BigDecimal;
import java.util.Date;

import com.xeiam.xchange.currency.Currencies;

import android.test.ActivityUnitTestCase;

public class MarketDataActivityTest
    extends ActivityUnitTestCase<MarketDataActivity>
{
    protected MarketDataActivity activity;
    protected MarketData marketData;
    protected MarketData cachedMarketData;
    protected String errorString;
    protected long expectResultsBy;

    public MarketDataActivityTest()
    {
        super(MarketDataActivity.class);

        errorString = "derp";
        marketData = new MarketData("mtgox", "BTC", "USD",
                new BigDecimal("1.00"), new BigDecimal("0.99"),
                new BigDecimal("1.01"), new BigDecimal("1.99"),
                new BigDecimal("0.01"), new BigDecimal("100.00"),
                new Date(1369196546000l));
        cachedMarketData = new MarketData("mtgox", "BTC", "USD",
                new BigDecimal("1.00"), new BigDecimal("0.99"),
                new BigDecimal("1.01"), new BigDecimal("1.99"),
                new BigDecimal("0.01"), new BigDecimal("100.00"),
                new Date(1369196946000l));
        expectResultsBy = 1369197946000l;
    }

    public void testRotation() throws Throwable
    {
        startActivity(new Intent(), null, null);
        activity = getActivity();

        activity.marketData = marketData;
        activity.cachedMarketData = cachedMarketData;
        activity.errorString = errorString;
        activity.expectResultsBy = expectResultsBy;

        activity.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        assertEquals("post-rotation market data",
                marketData, activity.marketData);
        assertEquals("post-rotation market data cache",
                cachedMarketData, activity.cachedMarketData);
        assertEquals("post-rotation error string",
                errorString, activity.errorString);
        assertEquals("post-rotation results timeout",
                expectResultsBy, activity.expectResultsBy);
    }

    public void testMarketDataDisplay() throws Throwable
    {
        startActivity(new Intent(), null, null);
        activity = getActivity();

        activity.marketData = marketData;
        activity.cachedMarketData = cachedMarketData;
        activity.errorString = errorString;
        activity.expectResultsBy = expectResultsBy;

        runTestOnUiThread(new Runnable() {
            public void run() {
                activity.displayMarketData();
            }
        });

        String expectedErrorText = errorString;
        String expectedPriceText =
            String.format(activity.getString(R.string.price_format),
                    cachedMarketData.lastPrice);
        String expectedCurrencyText =
            String.format(activity.getString(R.string.currency_pair_format),
                    cachedMarketData.baseCurrency,
                    cachedMarketData.counterCurrency);
        String expectedHighText =
            String.format(activity.getString(R.string.high_price_format),
                    cachedMarketData.highPrice);
        String expectedLowText =
            String.format(activity.getString(R.string.low_price_format),
                    cachedMarketData.lowPrice);
        String expectedVolumeText =
            String.format(activity.getString(R.string.volume_format),
                    cachedMarketData.volume);

        assertEquals("data-display error string",
                expectedErrorText, activity.errorView.getText());
        assertEquals("data-display price",
                expectedPriceText, activity.priceView.getText());
        assertEquals("data-display currency",
                expectedCurrencyText, activity.currencyView.getText());
        assertEquals("data-display high price",
                expectedHighText, activity.highPriceView.getText());
        assertEquals("data-display low price",
                expectedLowText, activity.lowPriceView.getText());
        assertEquals("data-display volume",
                expectedVolumeText, activity.volumeView.getText());
    }

    public void testDataReception() throws Throwable
    {
        startActivity(new Intent(), null, null);
        activity = getActivity();

        Uri target = FetchService.marketTarget(Exchanges.MT_GOX,
                Currencies.BTC, Currencies.USD);

        IntentFilter filter = new IntentFilter(FetchService.ACTION_RESPONSE);
        filter.addDataScheme(target.getScheme());
        filter.addDataAuthority(target.getAuthority(), null);
        filter.addDataPath(target.getPath(), PatternMatcher.PATTERN_LITERAL);

        // test successful fetch

        activity.registerReceiver(activity.marketDataReceiver, filter);

        final Intent successIntent =
            new Intent(FetchService.ACTION_RESPONSE, target);
        successIntent.putExtra(FetchService.EXTRA_MARKET_DATA, marketData);

        runTestOnUiThread(new Runnable() {
            public void run() {
                activity.startFetch();
                activity.marketDataReceiver.onReceive(activity, successIntent);
            }
        });

        assertEquals("good-fetch market data", marketData, activity.marketData);
        assertEquals("good-fetch market data cache", marketData,
                activity.cachedMarketData);
        assertEquals("good-fetch error string", null, activity.errorString);

        // test failed fetch

        activity.registerReceiver(activity.marketDataReceiver, filter);

        final Intent failureIntent =
            new Intent(FetchService.ACTION_RESPONSE, target);
        failureIntent.putExtra(FetchService.EXTRA_ERROR_STRING, errorString);

        runTestOnUiThread(new Runnable() {
            public void run() {
                activity.startFetch();
                activity.marketDataReceiver.onReceive(activity, failureIntent);
            }
        });

        assertEquals("bad-fetch market data", null, activity.marketData);
        assertEquals("bad-fetch market data cache", marketData,
                activity.cachedMarketData);
        assertEquals("bad-fetch error string", errorString,
                activity.errorString);
    }

    public void testTimeout() throws Throwable
    {
        startActivity(new Intent(), null, null);
        activity = getActivity();

        activity.cachedMarketData = cachedMarketData;
        activity.marketData = marketData;
        activity.errorString = null;

        String expectedError =
            activity.getString(R.string.fetch_error_timed_out);

        runTestOnUiThread(activity.timeout);

        assertEquals("timeout market data", null, activity.marketData);
        assertEquals("timeout market data cache", cachedMarketData,
                activity.cachedMarketData);
        assertEquals("timeout error string", expectedError,
                activity.errorString);
    }
}
