/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

public class FetchSchedulerTest extends AndroidTestCase
{
    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        setContext(new InjectorContext(getContext()));
    }

    public void testFetchInterval() throws Throwable
    {
        long interval =
            FetchScheduler.instance().getFetchInterval(getContext());

        assertEquals("scheduled fetch interval", 15000, interval);
    }

    private class InjectorContext extends MockContext
    {
        private Context parent;

        public InjectorContext(Context parent_)
        {
            parent = parent_;
        }

        @Override
        public SharedPreferences getSharedPreferences(String name, int mode)
        {
            return new BasicTestPrefs();
        }

        @Override
        public String getPackageName()
        {
            return parent.getPackageName();
        }
    }
}
