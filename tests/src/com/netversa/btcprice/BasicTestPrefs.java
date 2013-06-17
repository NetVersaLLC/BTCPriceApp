/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import com.xeiam.xchange.currency.Currencies;

public class BasicTestPrefs extends MockSharedPreferences
{
    public static final String defaultExchange = Exchanges.MT_GOX;
    public static final String defaultBase = Currencies.BTC;
    public static final String defaultCounter = Currencies.USD;

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
            return defaultExchange;
        }
        else if("def_base".equals(key))
        {
            return defaultBase;
        }
        else if("def_counter".equals(key))
        {
            return defaultCounter;
        }

        throw new IllegalArgumentException();
    }
}
