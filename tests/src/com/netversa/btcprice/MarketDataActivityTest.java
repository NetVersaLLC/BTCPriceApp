package com.netversa.btcprice;

import android.content.pm.ActivityInfo;

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
    protected MarketData cachedMarketData;
    protected String errorString;
    protected long expectResultsBy;

    public MarketDataActivityTest()
    {
        super("com.netversa.btcprice", MarketDataActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        activity = getActivity();

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

        activity.marketData = marketData;
        activity.cachedMarketData = cachedMarketData;
        activity.errorString = errorString;
        activity.expectResultsBy = expectResultsBy;
    }

    public void testRotation()
    {
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

        activity.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // clean up just in case
        activity.marketData = marketData;
        activity.cachedMarketData = cachedMarketData;
        activity.errorString = errorString;
        activity.expectResultsBy = expectResultsBy;
    }

    public void testDataDisplay()
    {
        // FIXME gotta run this on the activity's own thread
        activity.completeRefresh();

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

    // TODO learn up on injecting intents and test broadcastreceiver

}
