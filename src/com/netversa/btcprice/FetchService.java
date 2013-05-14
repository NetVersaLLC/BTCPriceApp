/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.HashSet;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FetchService extends Service
{
    public static final String EXTRA_TARGET = "com.netversa.btcprice.FETCH_TARGET";

    protected TargetsLock lock;

    @Override
    public void onCreate()
    {
        lock = new TargetsLock();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String target = intent.getStringExtra(EXTRA_TARGET);
        if(target == null)
        {
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public static class TargetsLock
    {
        protected Set<String> tokens;

        public TargetsLock()
        {
            tokens = new HashSet<String>();
        }

        public synchronized boolean testAndSet(String key)
        {
            if(tokens.contains(key))
            {
                return false;
            }
            tokens.add(key);
            return true;
        }

        public synchronized void clear(String key)
        {
            tokens.remove(key);
        }
    }
}
