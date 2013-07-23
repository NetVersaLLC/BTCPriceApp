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

    public int size()
    {
        return 0;
    }

    public long getLatestTxId()
    {
        return 0;
    }

    public Iterator<Transaction> get()
    {
        return get(null);
    }

    public Iterator<Transaction> get(Date since)
    {
        return null;
    }

    public void put(Iterator<Transaction> inputs)
    {
    }

    public void cull(Date before)
    {
    }

    public String getExchangeName()
    {
        return exchangeName;
    }

    public static final String TABLE_TRANSACTIONS = "_transactions";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "_type";
    public static final String COLUMN_EXCHANGE = "_exchange";
    public static final String COLUMN_AMOUNT = "_amount";
    public static final String COLUMN_COUNTER = "_counter";
    public static final String COLUMN_BASE = "_base";
    public static final String COLUMN_PRICE = "_price";
    public static final String COLUMN_TIMESTAMP = "_timestamp";
    public static final String COLUMN_TX_ID = "_tx_id";
}
