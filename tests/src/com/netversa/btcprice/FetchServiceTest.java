/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.test.ServiceTestCase;

import com.xeiam.xchange.Exchange;

public class FetchServiceTest extends ServiceTestCase<FetchService>
{
    // TODO shorten when test dependency on network is removed
    public static final long TEST_TIMEOUT_MS = 10000l;

    protected IntentFilter intentFilter;
    protected Context ctx;
    protected MarketData marketData;
    protected Map<String, Exchange> mockExchangeCache;

    public FetchServiceTest()
    {
        super(FetchService.class);

        intentFilter = new IntentFilter(FetchService.ACTION_RESPONSE);
        intentFilter.addDataScheme(FetchService.DATA_SCHEME);

        marketData = new MarketData("mtgox", "BTC", "USD",
                new BigDecimal("1.00"), new BigDecimal("0.99"),
                new BigDecimal("1.01"), new BigDecimal("1.99"),
                new BigDecimal("0.01"), new BigDecimal("100.00"),
                new Date(1369196546000l));

        mockExchangeCache = new ConcurrentHashMap<String, Exchange>();
        mockExchangeCache.put(MarketData.MT_GOX, new MockExchange(marketData));
    }

    protected void setUp() throws Exception
    {
        super.setUp();

        ctx = getContext();
    }

    public void testNullTarget() throws Throwable
    {
        startService(new Intent(FetchService.ACTION_REQUEST));
    }

    public void testNullExchange() throws Throwable
    {
        final Semaphore completeCondition = new Semaphore(0);
        String badTarget = "data:///market/USD/BTC";
        String format = ctx.getString(R.string.fetch_error_bad_target_format);
        final String expectedError = String.format(format, badTarget);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                assertEquals("null-exchange error string", expectedError,
                    intent.getStringExtra(FetchService.EXTRA_ERROR_STRING));
                assertEquals("null-exchange market data", null,
                    (MarketData) intent.getParcelableExtra(
                        FetchService.EXTRA_MARKET_DATA));
                completeCondition.release();
            }
        };

        ctx.registerReceiver(receiver, intentFilter);

        startService(new Intent(FetchService.ACTION_REQUEST,
                Uri.parse(badTarget)));

        if(!completeCondition.tryAcquire(TEST_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS))
        {
            fail("null-exchange timed out");
        }
        ctx.unregisterReceiver(receiver);
    }

    public void testUnknownAction() throws Throwable
    {
        final Semaphore completeCondition = new Semaphore(0);
        String badTarget = "data://mtgox/derp";
        String format =
            ctx.getString(R.string.fetch_error_unknown_action_format);
        final String expectedError = String.format(format, "derp");

        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                assertEquals("unknown-action error string", expectedError,
                    intent.getStringExtra(FetchService.EXTRA_ERROR_STRING));
                assertEquals("unknown-action market data", null,
                    (MarketData) intent.getParcelableExtra(
                        FetchService.EXTRA_MARKET_DATA));
                completeCondition.release();
            }
        };

        ctx.registerReceiver(receiver, intentFilter);

        startService(new Intent(FetchService.ACTION_REQUEST,
                Uri.parse(badTarget)));

        if(!completeCondition.tryAcquire(TEST_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS))
        {
            fail("unknown-action timed out");
        }
        ctx.unregisterReceiver(receiver);
    }

    public void testMarketBadArity() throws Throwable
    {
        final Semaphore completeCondition = new Semaphore(0);
        String badTarget = "data://mtgox/market/USD";
        String format = ctx.getString(R.string.fetch_error_wrong_arity_format);
        final String expectedError = String.format(format, "market", 2);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                assertEquals("market-bad-arity error string", expectedError,
                    intent.getStringExtra(FetchService.EXTRA_ERROR_STRING));
                assertEquals("market-bad-arity market data", null,
                    (MarketData) intent.getParcelableExtra(
                        FetchService.EXTRA_MARKET_DATA));
                completeCondition.release();
            }
        };

        ctx.registerReceiver(receiver, intentFilter);

        startService(new Intent(FetchService.ACTION_REQUEST,
                Uri.parse(badTarget)));

        if(!completeCondition.tryAcquire(TEST_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS))
        {
            fail("market-bad-arity timed out");
        }
        ctx.unregisterReceiver(receiver);
    }

    public void testMarketDataFetch() throws Throwable
    {
        final Semaphore completeCondition = new Semaphore(0);
        String goodTarget = "data://mtgox/market/BTC/USD";

        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                assertEquals("market-fetch error string", null,
                    intent.getStringExtra(FetchService.EXTRA_ERROR_STRING));
                assertEquals("market-fetch market data", marketData,
                    (MarketData) intent.getParcelableExtra(
                        FetchService.EXTRA_MARKET_DATA));
                completeCondition.release();
            }
        };

        ctx.registerReceiver(receiver, intentFilter);

        setupService();
        FetchService service = getService();
        assertNotNull("market-fetch service not created!", service);
        service.exchangeCache = mockExchangeCache;

        startService(new Intent(FetchService.ACTION_REQUEST,
                Uri.parse(goodTarget)));

        if(!completeCondition.tryAcquire(TEST_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS))
        {
            fail("market-fetch timed out");
        }
        ctx.unregisterReceiver(receiver);
    }
}
