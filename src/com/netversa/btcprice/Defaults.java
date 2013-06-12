/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import com.xeiam.xchange.currency.Currencies;

/** Central storage for default values for SharedPreferences.
 */
public class Defaults
{
    public static final String DEF_EXCHANGE = Exchanges.MT_GOX;
    public static final String DEF_BASE = Currencies.BTC;
    public static final String DEF_COUNTER = Currencies.USD;
    // 30 seconds in ms
    public static final long FETCH_TIMEOUT = 30000;
    public static final boolean ONGOING_PRICE = false;
}
