/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionAnalysis
{
    public static List<Candlestick> toCandlesticks(Transaction.List txs,
            long startTimestamp, long endTimestamp, int intervals)
    {
        List<Candlestick> result = new ArrayList<Candlestick>();

        result.add(new Candlestick(
                    new BigDecimal("2.0"),
                    new BigDecimal("3.0"),
                    new BigDecimal("4.0"),
                    new BigDecimal("1.0")));

        return result;
    }
}
