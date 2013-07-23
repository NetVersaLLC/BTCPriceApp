/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.Date;
import java.util.Iterator;

import android.content.Context;

/**
 * Cache historical transaction data locally for building candlestick charts
 * etc.  At least in the case of Mt. Gox, fetching enough historical data to
 * even create a graph for a day can be very slow.
 */
public class TransactionCache
{
    private Context context;
    private String exchangeName;
    private DatabaseAccess dbAccess;

    public TransactionCache(Context context_, String exchangeName_)
    {
        context = context_;
        exchangeName = exchangeName_;
        dbAccess = new DatabaseAccess(context);
    }

    public long getLatestTxId()
    {
        return 0;
    }

    public Iterator<Transaction> getSince(Date since)
    {
        return null;
    }

    public void add(Iterator<Transaction> inputs)
    {
    }

    public void cull(Date before)
    {
    }

    public String getExchangeName()
    {
        return exchangeName;
    }
}
