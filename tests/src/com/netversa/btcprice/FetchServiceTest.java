/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

public class FetchServiceTest extends SaneServiceTestCase<FetchService>
{
    protected IntentFilter intentFilter;
    protected Context ctx;

    public FetchServiceTest()
    {
        super(FetchService.class);

        intentFilter = new IntentFilter(FetchService.ACTION_RESPONSE);
        intentFilter.addDataScheme(FetchService.DATA_SCHEME);
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
        String badTarget = "data:///market/USD/BTC";
        String format = ctx.getString(R.string.fetch_error_bad_target_format);
        final String expectedError = String.format(format, badTarget);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                assertEquals("null-exchange market data", null,
                    intent.getParcelableExtra(FetchService.EXTRA_MARKET_DATA));
                assertEquals("null-exchange error string", expectedError,
                    intent.getStringExtra(FetchService.EXTRA_ERROR_STRING));
            }
        };

        ctx.registerReceiver(receiver, intentFilter);

        startService(new Intent(FetchService.ACTION_REQUEST,
                Uri.parse(badTarget)));

        joinService();
        ctx.unregisterReceiver(receiver);
    }
}
