/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.Date;
import java.util.Iterator;

import android.content.Context;

public class TransactionCache
{
    private Context context;
    private DatabaseAccess dbAccess;

    public TransactionCache(Context context_)
    {
        context = context_;
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
}
