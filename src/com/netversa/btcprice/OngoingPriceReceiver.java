/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Maintain an ongoing price notification by updating whenever anyone gets
 * fresh data.  Primarily fed by the periodic price update service, but will
 * opportunistically update whenever new data arrives.
 */
public class OngoingPriceReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
    }
}
