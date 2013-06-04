/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BaseActivity extends Activity
{
    protected SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }
}
