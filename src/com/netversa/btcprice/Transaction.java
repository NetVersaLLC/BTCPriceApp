/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

public class Transaction
{
    public static class Iterator implements java.util.Iterator<Transaction>
    {
        @Override
        public boolean hasNext()
        {
            return false;
        }

        @Override
        public Transaction next()
        {
            return null;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
