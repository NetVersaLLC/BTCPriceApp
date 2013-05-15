/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.HashSet;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/** Class for fetching Market data decoupled from Activity lifestyles.
 *
 * By decoupling from activities, the possibility of spamming an exchange
 * because the user is rotating their phone is eliminated without having to
 * resort to ugly AsyncTask or configuration change hacks.
 *
 * Fetch actions are organized by target, which may be something along the
 * lines of 'market data for BTCUSD' but in a more structured format.
 *
 * If a target is requested for which a fetch is already in progress, that
 * request is ignored.
 */
public class FetchService extends Service
{
    public static final String EXTRA_TARGET =
        "com.netversa.btcprice.FETCH_TARGET";

    protected ActiveTargetSet activeTargets;

    @Override
    public void onCreate()
    {
        activeTargets = new ActiveTargetSet();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        String target = intent.getStringExtra(EXTRA_TARGET);
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
    protected void doFetch(String target)
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
        // Actual fetching action!
        //

        finalizeFetch(target);
    }

    /** Mark a fetched target inactive and stop the service if necessary.
      */
    protected void finalizeFetch(String target)
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

    /** Runnable wrapper that does actual fetching in a thread.
      */
    protected class FetchRunnable implements Runnable
    {
        protected String target;

        public FetchRunnable(String target)
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
    protected static class ActiveTargetSet extends HashSet<String>
    {
        public synchronized boolean testAndSet(String key)
        {
            if(contains(key))
            {
                return false;
            }
            add(key);
            return true;
        }

        public synchronized void unset(String key)
        {
            remove(key);
        }
    }
}
