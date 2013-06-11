/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.xeiam.xchange.currency.Currencies;

public class OngoingPriceReceiverTest extends AndroidTestCase
{
    private OngoingPriceReceiver receiver;
    public static final String defaultExchange = Exchanges.MT_GOX;
    public static final String defaultBase = Currencies.BTC;
    public static final String defaultCounter = Currencies.USD;

    public OngoingPriceReceiverTest()
    {
        super();

        receiver = new OngoingPriceReceiver();
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        setContext(new InjectorContext(getContext()));
    }

    public void testGoodIntent() throws Throwable
    {
        Uri target = FetchService.marketTarget(defaultExchange, defaultBase,
                defaultCounter);
        Intent goodIntent = new Intent(FetchService.ACTION_RESPONSE, target);

        receiver.onReceive(getContext(), goodIntent);
    }

    private class ExpectedPrefs extends MockSharedPreferences
    {
        @Override
        public boolean getBoolean(String key, boolean defValue)
        {
            if("ongoing_price".equals(key))
            {
                return true;
            }

            throw new IllegalArgumentException();
        }
    }

    private interface NotificationExaminer
    {
        public void examineNotification(int id, Notification notif);
    }

    private class NotificationExaminerManager extends MockNotificationManager
    {
        private NotificationExaminer examiner;

        public NotificationExaminerManager(NotificationExaminer examiner_)
        {
            examiner = examiner_;
        }

        @Override
        public void notify(int id, Notification notif)
        {
            examiner.examineNotification(id, notif);
        }
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
            if(Context.NOTIFICATION_SERVICE.equals(name))
            {
                return null;
            }

            throw new IllegalArgumentException();
        }

        @Override
        public String getPackageName()
        {
            return parent.getPackageName();
        }

        @Override
        public SharedPreferences getSharedPreferences(String name, int mode)
        {
            return new ExpectedPrefs();
        }
    }
}
