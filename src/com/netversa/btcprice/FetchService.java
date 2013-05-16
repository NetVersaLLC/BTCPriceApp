/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.HashSet;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

/** Class for fetching Market data decoupled from Activity lifestyles.
 *
 * By decoupling from activities, the possibility of spamming an exchange
 * because the user is rotating their phone is eliminated without having to
 * resort to ugly AsyncTask or configuration change hacks.
 *
 * Fetch actions are organized by target URI, which dictates where and what to
 * fetch.
 *
 * If a target is requested for which a fetch is already in progress, that
 * request is ignored.
 */
public class FetchService extends Service
{
    public static final String ACTION_REQUEST =
        "com.netversa.btcprice.ACTION_FETCH_REQUEST";
    public static final String ACTION_RESPONSE =
        "com.netversa.btcprice.ACTION_FETCH_RESPONSE";
    public static final String EXTRA_MARKET_DATA =
        "com.netversa.btcprice.EXTRA_MARKET_DATA";

    public static final String DATA_SCHEME = "data";
    public static final String MARKET_DATA_URI_FORMAT = DATA_SCHEME +
        "://%s/market/%s/%s";

    protected ActiveTargetSet activeTargets;

    @Override
    public void onCreate()
    {
        activeTargets = new ActiveTargetSet();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Uri target = intent.getData();
        new Thread(new FetchRunnable(target)).start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    /** Get data from exchange and broadcast it to any interested parties
      */
    protected void doFetch(Uri target)
    {
        // if there is no target to fetch, abort and shut down if appropriate
        if(target == null)
        {
            finalizeFetch(null);
            return;
        }

        // only do any work if the work requested isn't already in progress
        if(!activeTargets.testAndSet(target))
        {
            finalizeFetch(null);
            return;
        }

        //
        // TODO Actual fetching action!
        //
        MarketData result = new MarketData();

        Intent resultIntent = new Intent(ACTION_RESPONSE, target);
        resultIntent.putExtra(EXTRA_MARKET_DATA, result);
        sendBroadcast(resultIntent);

        finalizeFetch(target);
    }

    /** Mark a fetched target inactive and stop the service if necessary.
      */
    protected void finalizeFetch(Uri target)
    {
        synchronized(activeTargets)
        {
            if(target != null)
            {
                activeTargets.unset(target);
            }

            if(activeTargets.size() == 0)
            {
                stopSelf();
            }
        }
        return;
    }

    // TODO overload with default exchange from SharedPrefs
    public static Uri marketTarget(String exchange, String baseCurrency, String
            counterCurrency)
    {
        return Uri.parse(String.format(MARKET_DATA_URI_FORMAT, exchange,
                    baseCurrency, counterCurrency));
    }

    /** Runnable wrapper that does actual fetching in a thread.
      */
    protected class FetchRunnable implements Runnable
    {
        protected Uri target;

        public FetchRunnable(Uri target)
        {
            this.target = target;
        }

        public void run()
        {
            doFetch(target);
        }
    }

    /** Simple partially atomic extension of HashSet for keeping track of which
     * targets are actively being fetched.
     */
    protected static class ActiveTargetSet extends HashSet<Uri>
    {
        public synchronized boolean testAndSet(Uri key)
        {
            if(contains(key))
            {
                return false;
            }
            add(key);
            return true;
        }

        public synchronized void unset(Uri key)
        {
            remove(key);
        }
    }
}
