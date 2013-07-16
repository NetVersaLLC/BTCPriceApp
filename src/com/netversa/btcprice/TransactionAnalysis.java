/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.List;

public class TransactionAnalysis
{
    public static Candlestick.List toCandlesticks(List<Transaction> txs,
            long startTimestamp, long endTimestamp, int intervals)
    {
        Candlestick.List result = new Candlestick.List();

        if(startTimestamp > endTimestamp)
        {
            throw new IllegalArgumentException();
        }
        long interval = (endTimestamp - startTimestamp) / intervals;

        for(int ii = 0; ii < intervals; ii++)
        {
            result.add(new Candlestick(Candlestick.NONE, Candlestick.NONE,
                        Candlestick.NONE, Candlestick.NONE));
        }

        for(Transaction ee : txs)
        {
            long ts = ee.timestamp.getTime() / 1000;
            double price = ee.price.doubleValue();
            if(ts < startTimestamp || ts >= endTimestamp)
            {
                continue;
            }

            int index = (int) ((ts - startTimestamp) / interval);

            Candlestick stick = result.get(index);

            if(stick.open == Candlestick.NONE)
            {
                result.set(index, new Candlestick(price, price, price, price));
                continue;
            }

            double open = stick.open;
            double close = price;
            double high = Math.max(stick.high, price);
            double low = Math.min(stick.low, price);

            result.set(index, new Candlestick(open, close, high, low));
        }

        return result;
    }
}
