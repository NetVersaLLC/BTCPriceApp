/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import com.xeiam.xchange.currency.Currencies;

public class BasicTestPrefs extends MockSharedPreferences
{
    public static final String def_exchange = Exchanges.MT_GOX;
    public static final String def_base = Currencies.BTC;
    public static final String def_counter = Currencies.USD;

    public static final long ongoing_price_interval = 15l * 1000l;

    @Override
    public boolean getBoolean(String key, boolean defValue)
    {
        if("ongoing_price".equals(key))
        {
            return true;
        }

        throw new IllegalArgumentException();
    }

    @Override
    public String getString(String key, String defValue)
    {
        if("def_exchange".equals(key))
        {
            return def_exchange;
        }
        else if("def_base".equals(key))
        {
            return def_base;
        }
        else if("def_counter".equals(key))
        {
            return def_counter;
        }

        throw new IllegalArgumentException();
    }

    @Override
    public long getLong(String key, long defValue)
    {
        if("ongoing_price_interval".equals(key))
        {
            return ongoing_price_interval;
        }

        throw new IllegalArgumentException();
    }
}
