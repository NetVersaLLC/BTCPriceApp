/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

public class OngoingPriceReceiverTest extends AndroidTestCase
{
    private OngoingPriceReceiver receiver;
    private MarketData marketData;
    private NotificationManager notifs;

    public OngoingPriceReceiverTest()
    {
        super();

        marketData = new MarketData(BasicTestPrefs.def_exchange,
                BasicTestPrefs.def_base, BasicTestPrefs.def_counter,
                new BigDecimal("1.00"), new BigDecimal("0.99"),
                new BigDecimal("1.01"), new BigDecimal("1.99"),
                new BigDecimal("0.01"), new BigDecimal("100.00"),
                new Date(1369196546000l));
        receiver = new OngoingPriceReceiver();
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        notifs = (NotificationManager)
            getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        setContext(new InjectorContext(getContext()));
    }

    @Override
    public void tearDown() throws Exception
    {
        notifs.cancelAll();

        super.tearDown();
    }

    public void testGoodIntent() throws Throwable
    {
        Uri target = FetchService.marketTarget(BasicTestPrefs.def_exchange,
                BasicTestPrefs.def_base, BasicTestPrefs.def_counter);
        Intent goodIntent = new Intent(FetchService.ACTION_RESPONSE, target);
        goodIntent.putExtra(FetchService.EXTRA_MARKET_DATA,
                marketData);

        GoodIntentAssertBuilder builder =
            new GoodIntentAssertBuilder(getContext());
        receiver.setNotificationBuilder(builder);

        receiver.onReceive(getContext(), goodIntent);

        builder.ok();
    }

    public void testBadExchange() throws Throwable
    {
        Uri target = FetchService.marketTarget(BasicTestPrefs.def_exchange,
                BasicTestPrefs.def_base, BasicTestPrefs.def_counter);
        Intent badIntent = new Intent(FetchService.ACTION_RESPONSE, target);
        badIntent.putExtra(FetchService.EXTRA_MARKET_DATA,
                new MarketData(null, BasicTestPrefs.def_base,
                    BasicTestPrefs.def_counter,
                    new BigDecimal("1.00"), new BigDecimal("0.99"),
                    new BigDecimal("1.01"), new BigDecimal("1.99"),
                    new BigDecimal("0.01"), new BigDecimal("100.00"),
                    new Date(1369196546000l)));

        BadIntentAssertBuilder builder =
            new BadIntentAssertBuilder(getContext());
        receiver.setNotificationBuilder(builder);

        receiver.onReceive(getContext(), badIntent);
    }

    private class InjectorContext extends MockContext
    {
        private Context parent;

        public InjectorContext(Context parent_)
        {
            parent = parent_;
        }

        @Override
        public Object getSystemService(String name)
        {
            return parent.getSystemService(name);
        }

        @Override
        public String getPackageName()
        {
            return parent.getPackageName();
        }

        @Override
        public SharedPreferences getSharedPreferences(String name, int mode)
        {
            return new BasicTestPrefs();
        }

        @Override
        public Resources getResources()
        {
            return parent.getResources();
        }

        @Override
        public PackageManager getPackageManager()
        {
            return parent.getPackageManager();
        }

        @Override
        public ContentResolver getContentResolver()
        {
            return parent.getContentResolver();
        }
    }

    private class GoodIntentAssertBuilder extends NotificationCompat.Builder
    {
        private boolean titleChecked;
        private boolean textChecked;
        private boolean ongoingChecked;
        private boolean intentChecked;

        public GoodIntentAssertBuilder(Context context)
        {
            super(context);
            titleChecked = false;
            textChecked = false;
            ongoingChecked = false;
            intentChecked = false;
        }

        @Override
        public NotificationCompat.Builder setSmallIcon(int id) {
            return this;
        }

        @Override
        public NotificationCompat.Builder setContentTitle(CharSequence title) {
            assertNotNull("good notification title", title);
            assertTrue("good notification title", title.length() > 0);
            titleChecked = true;
            return this;
        }

        @Override
        public NotificationCompat.Builder setContentText(CharSequence text) {
            assertNotNull("good notification text", text);
            assertTrue("good notification text", text.length() > 0);
            textChecked = true;
            return this;
        }

        @Override
        public NotificationCompat.Builder setOngoing(boolean ongoing) {
            assertEquals("good notification is ongoing", true, ongoing);
            ongoingChecked = true;
            return this;
        }

        @Override
        public NotificationCompat.Builder setContentIntent(
                PendingIntent intent) {
            assertNotNull("good notification intent", intent);
            intentChecked = true;
            return this;
        }

        public void ok()
        {
            assertTrue("all good notification values checked",
                    titleChecked && textChecked && ongoingChecked &&
                    intentChecked);
        }
    }

    private class BadIntentAssertBuilder extends NotificationCompat.Builder
    {
        public BadIntentAssertBuilder(Context context)
        {
            super(context);
        }

        @Override
        public Notification build()
        {
            fail("attempt to build bad notification");
            return null;
        }
    }
}
