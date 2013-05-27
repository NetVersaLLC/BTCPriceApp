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

    public void testNullTarget() throws Throwable
    {
    }
}
