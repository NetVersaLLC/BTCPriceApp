/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.Notification;
import android.app.NotificationManager;

public class MockNotificationManager extends NotificationManager
{
    @Override
    public void cancel(int id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancel(String tag, int id)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelAll()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notify(int id, Notification notif)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notify(String tag, int id, Notification notif)
    {
        throw new UnsupportedOperationException();
    }
}
