/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** Manage data relevant to all parts of the application.  A global preference
 * change listener is registered here so that whenever a service is enabled it
 * gets started.
 */
public class BtcPriceApplication extends Application
{
    protected GlobalPrefListener prefListener;
    protected SharedPreferences defaultPrefs;

    @Override
    public void onCreate()
    {
        super.onCreate();

        defaultPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefListener = new GlobalPrefListener(this);
        defaultPrefs.registerOnSharedPreferenceChangeListener(prefListener);
    }
}
