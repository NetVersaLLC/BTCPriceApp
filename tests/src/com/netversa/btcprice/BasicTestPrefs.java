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

    public static final boolean ongoing_price = true;
    public static final long ongoing_price_interval = 15l * 1000l;
    public static final boolean price_widget_active = true;
    public static final long price_widget_interval = 60l * 1000l;
    public static final boolean price_change = true;
    public static final long price_change_interval = 15l * 1000l;

    @Override
    public boolean getBoolean(String key, boolean defValue)
    {
        if("ongoing_price".equals(key))
        {
            return ongoing_price;
        }
        else if("price_widget_active".equals(key))
        {
            return price_widget_active;
        }
        else if("price_change".equals(key))
        {
            return price_change;
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
        if("price_widget_interval".equals(key))
        {
            return price_widget_interval;
        }
        if("price_change_interval".equals(key))
        {
            return price_change_interval;
        }

        throw new IllegalArgumentException();
    }
}
