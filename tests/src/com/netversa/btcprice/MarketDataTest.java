/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.TestCase;

public class MarketDataTest extends TestCase
{
    protected MarketData base;
    protected MarketData same;
    protected MarketData different;
    protected MarketData onesNull;
    protected MarketData allNull;
    protected MarketData sameNull;

    protected void setUp() throws Exception
    {
        super.setUp();

        base = new MarketData("mtgox", "BTC", "USD",
                new BigDecimal("1.00"), new BigDecimal("0.99"),
                new BigDecimal("1.01"), new BigDecimal("1.99"),
                new BigDecimal("0.01"), new BigDecimal("100.00"),
                new Date(1369196546000l));

        same = new MarketData("mtgox", "BTC", "USD",
                new BigDecimal("1.00"), new BigDecimal("0.99"),
                new BigDecimal("1.01"), new BigDecimal("1.99"),
                new BigDecimal("0.01"), new BigDecimal("100.00"),
                new Date(1369196546000l));

        different = new MarketData("btce", "LTC", "RUR",
                new BigDecimal("9.00"), new BigDecimal("0.11"),
                new BigDecimal("9.09"), new BigDecimal("9.11"),
                new BigDecimal("0.09"), new BigDecimal("900.00"),
                new Date(1369968093000l));

        onesNull = new MarketData(null, "BTC", "USD",
                new BigDecimal("1.00"), new BigDecimal("0.99"),
                new BigDecimal("1.01"), new BigDecimal("1.99"),
                new BigDecimal("0.01"), new BigDecimal("100.00"),
                new Date(1369196546000l));

        allNull = new MarketData(null, null, null,
                null, null,
                null, null,
                null, null,
                null);

        sameNull = new MarketData(null, null, null,
                null, null,
                null, null,
                null, null,
                null);
    }

    public void testEquals() throws Throwable
    {
        assertTrue(base.equals(base));

        assertTrue(base.equals(same));
        assertTrue(same.equals(base));

        assertFalse(base.equals(different));
        assertFalse(different.equals(base));
        assertFalse(base.equals(onesNull));
        assertFalse(onesNull.equals(base));
        assertFalse(base.equals(allNull));
        assertFalse(allNull.equals(base));

        assertTrue(allNull.equals(allNull));
        assertTrue(allNull.equals(sameNull));
        assertTrue(sameNull.equals(allNull));
    }

    public void testHashCode() throws Throwable
    {
        int baseCode = base.hashCode();
        int sameCode = same.hashCode();
        int differentCode = different.hashCode();
        int onesNullCode = onesNull.hashCode();
        int allNullCode = allNull.hashCode();
        int sameNullCode = sameNull.hashCode();

        assertTrue(baseCode == sameCode);

        assertFalse(baseCode == differentCode);
        assertFalse(baseCode == onesNullCode);
        assertFalse(baseCode == allNullCode);

        assertTrue(allNullCode == sameNullCode);
    }
}
