/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.content.SharedPreferences;

/** Listener to enforce side-affects of certain prefs.  For example, when a
 * service is enabled by a pref, the service should also be launched.
 */
public class GlobalPrefListener
    implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
    {
    }
}
