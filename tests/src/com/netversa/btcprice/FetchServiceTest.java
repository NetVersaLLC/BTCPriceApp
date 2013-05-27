/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.test.ServiceTestCase;

public class FetchServiceTest extends ServiceTestCase<FetchService>
{
    public FetchServiceTest()
    {
        super(FetchService.class);
    }

    // TODO setUp that puts service in testing mode and injects mock exchange
    // objects

    public void testNullTarget() throws Throwable
    {
    }
}
