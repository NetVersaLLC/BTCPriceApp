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

        assertEquals("marketData preservation",
                activity.marketData, marketData);
        assertEquals("cachedMarketData preservation",
                activity.cachedMarketData, cachedMarketData);
        assertEquals("errorString preservation",
                activity.errorString, errorString);
        assertEquals("expectResultsBy preservation",
                activity.expectResultsBy, expectResultsBy);
    }

    public void testDataDisplay() throws Throwable
    {
        startActivity(new Intent(), null, null);
        activity = getActivity();

        activity.marketData = marketData;
        activity.cachedMarketData = cachedMarketData;
        activity.errorString = errorString;
        activity.expectResultsBy = expectResultsBy;

        runTestOnUiThread(new Runnable() {
            public void run() {
                activity.completeRefresh();
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

        assertEquals("error readout correctness",
                activity.errorView.getText(), expectedErrorText);
        assertEquals("price readout correctness",
                activity.priceView.getText(), expectedPriceText);
        assertEquals("currency readout correctness",
                activity.currencyView.getText(), expectedCurrencyText);
        assertEquals("high price readout correctness",
                activity.highPriceView.getText(), expectedHighText);
        assertEquals("low price readout correctness",
                activity.lowPriceView.getText(), expectedLowText);
        assertEquals("volume readout correctness",
                activity.volumeView.getText(), expectedVolumeText);
    }

    public void testDataReception() throws Throwable
    {
        startActivity(new Intent(), null, null);
        activity = getActivity();

        Uri target = FetchService.marketTarget(MarketData.MT_GOX,
                Currencies.BTC, Currencies.USD);

        IntentFilter filter = new IntentFilter(FetchService.ACTION_RESPONSE);
        filter.addDataScheme(target.getScheme());
        filter.addDataAuthority(target.getAuthority(), null);
        filter.addDataPath(target.getPath(), PatternMatcher.PATTERN_LITERAL);

        // test successful fetch

        activity.registerReceiver(activity.responseReceiver, filter);

        final Intent successIntent =
            new Intent(FetchService.ACTION_RESPONSE, target);
        successIntent.putExtra(FetchService.EXTRA_MARKET_DATA, marketData);

        runTestOnUiThread(new Runnable() {
            public void run() {
                activity.startRefresh();
                activity.responseReceiver.onReceive(activity, successIntent);
            }
        });

        assertEquals("correct storing of good MarketData", activity.marketData,
                marketData);
        assertEquals("correct caching of good MarketData",
                activity.cachedMarketData, marketData);
        assertEquals("clear error message on successful fetch",
                activity.errorString, null);

        // test failed fetch

        activity.registerReceiver(activity.responseReceiver, filter);

        final Intent failureIntent =
            new Intent(FetchService.ACTION_RESPONSE, target);
        failureIntent.putExtra(FetchService.EXTRA_ERROR_STRING, errorString);

        runTestOnUiThread(new Runnable() {
            public void run() {
                activity.startRefresh();
                activity.responseReceiver.onReceive(activity, failureIntent);
            }
        });

        assertEquals("correct blanking of bad MarketData", activity.marketData,
                null);
        assertEquals("correct caching of previous good MarketData",
                activity.cachedMarketData, marketData);
        assertEquals("error message on failed fetch",
                activity.errorString, errorString);
    }
}
