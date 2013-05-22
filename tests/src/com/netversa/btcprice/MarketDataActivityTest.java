package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.Date;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.netversa.btcprice.MarketDataActivityTest \
 * com.netversa.btcprice.tests/android.test.InstrumentationTestRunner
 */
public class MarketDataActivityTest extends ActivityInstrumentationTestCase2<MarketDataActivity>
{
    protected MarketDataActivity activity;
    protected MarketData marketData;

    public MarketDataActivityTest()
    {
        super("com.netversa.btcprice", MarketDataActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        activity = getActivity();

        marketData = new MarketData("mtgox", "BTC", "USD", new BigDecimal("1.00"),
                new BigDecimal("0.99"), new BigDecimal("1.01"), new BigDecimal("1.99"),
                new BigDecimal("0.01"), new BigDecimal("100.00"), new Date(1369196546000l));

        activity.marketData = marketData;
    }

    public void testRotation()
    {
    }

}
