/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/** Activate and configure various services that produce notifications.
 * Currently, the features configured through this Activity include ongoing
 * price notifications and price delta notifications.
 */
public class NotificationConfigActivity extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_notifications);
    }
}
