/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.NotificationManager;
import android.content.Context;
import android.test.AndroidTestCase;

public class OngoingPriceReceiverTest extends AndroidTestCase
{
    private OngoingPriceReceiver receiver;
    private NotificationManager notifs;

    public OngoingPriceReceiverTest()
    {
        super();

        receiver = new OngoingPriceReceiver();
    }

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        notifs = (NotificationManager)
            getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void tearDown() throws Exception
    {
        notifs.cancelAll();
        super.tearDown();
    }

    public void testSomething() throws Throwable
    {
    }
}
