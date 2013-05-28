/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.test.ServiceTestCase;

public class SaneServiceTestCase<T extends Service> extends ServiceTestCase<T>
{
    String serviceClassName;

    public SaneServiceTestCase(Class<T> serviceClass)
    {
        super(serviceClass);
        serviceClassName = serviceClass.getName();
    }

    
    /** Wait for the Service being tested to exit.  bizarrely there's no way to
     * wait for a service to shut down, even within a service test case.  As a
     * result, broadcasts may overlap between tests.  As a dumb hack fix, this
     * method will repeatedly poll the context until the service under test is
     * no longer running.
     */
    protected void joinService()
    {
        ActivityManager manager = (ActivityManager)
            getContext().getSystemService(Context.ACTIVITY_SERVICE);
        boolean running = true;
        while(running)
        {
            running = false;
            for (RunningServiceInfo service :
                    manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClassName.equals(service.service.getClassName())) {
                    running = true;
                    break;
                }
            }
        }
    }
}
