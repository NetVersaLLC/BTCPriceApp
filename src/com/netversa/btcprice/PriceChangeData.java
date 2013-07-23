/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * Store price tracking data and thresholds of interest to the user.
 */
public class PriceChangeData
{
    private Context context;
    private String exchangeName;
    private DatabaseAccess dbAccess;

    public PriceChangeData(Context context_, String exchangeName_)
    {
        context = context_;
        exchangeName = exchangeName_;
        dbAccess = new DatabaseAccess(context);
    }

    public List<Threshold> getThresholds()
    {
        return new ArrayList<Threshold>();
    }

    public void clearThresholds()
    {
    }
    
    public void setThresholds(List<Threshold> thresholds)
    {
    }

    public String getExchangeName()
    {
        return exchangeName;
    }

    /**
     * A threshold at which a price change is considered significant.  Absolute
     * changes are denominated in the relevant currency; relative changes are
     * multiplied by the previous relevant price before being used as an
     * absolute threshold.
     */
    public static class Threshold
    {
        public static final int ABSOLUTE = 0;
        public static final int RELATIVE = 1;

        public final double magnitude;
        public final int type;

        public Threshold(double magnitude_, int type_)
        {
            magnitude = magnitude_;
            type = type_;
        }
    }

    public static final String TABLE_THRESHOLDS = "_price_change_thresholds";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "_type";
    public static final String COLUMN_EXCHANGE = "_exchange";
    public static final String COLUMN_AMOUNT = "_amount";
}
