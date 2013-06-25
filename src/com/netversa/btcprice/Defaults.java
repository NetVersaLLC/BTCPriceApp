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

    // 1 minute in ms
    public static final long STALENESS_THRESHOLD = 60000;

    public static final long LAST_SCHED_FETCH = 0;
    // _FETCH_INTERVAL covers an arbitrary number of prefs relating to
    // service frequencies
    public static final long _FETCH_INTERVAL = 1000l * 60l * 60l;
    public static final boolean _FETCH_ENABLED = false;
}
