/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        List<Threshold> output = new ArrayList<Threshold>();

        SQLiteDatabase db = dbAccess.getReadableDatabase();
        try
        {
            Cursor c = db.query(TABLE_THRESHOLDS,
                    new String[] { COLUMN_TYPE, COLUMN_AMOUNT },
                    COLUMN_EXCHANGE + " = ?", new String[] { exchangeName },
                    null, null, null);

            try
            {
                while(c.moveToNext())
                {
                    output.add(new Threshold(c.getDouble(1), c.getInt(0)));
                }
            }
            finally
            {
                c.close();
            }
        }
        finally
        {
            db.close();
        }

        return output;
    }

    public void clearThresholds()
    {
        SQLiteDatabase db = dbAccess.getWritableDatabase();

        try
        {
            db.delete(TABLE_THRESHOLDS, COLUMN_EXCHANGE + " = ?",
                    new String[] { exchangeName });
        }
        finally
        {
            db.close();
        }
    }
    
    public void setThresholds(List<Threshold> thresholds)
    {
        clearThresholds();

        SQLiteDatabase db = dbAccess.getWritableDatabase();
        
        try
        {
            for(Threshold ee : thresholds)
            {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TYPE, ee.type);
                values.put(COLUMN_AMOUNT, ee.amount);
                values.put(COLUMN_EXCHANGE, exchangeName);
                db.insertOrThrow(TABLE_THRESHOLDS, COLUMN_ID, values);
            }
        }
        finally
        {
            db.close();
        }
    }

    public double getLastBasis()
    {
        SQLiteDatabase db = dbAccess.getReadableDatabase();

        double result = -1;

        try
        {
            Cursor c = db.query(TABLE_BASES,
                    new String[] { COLUMN_AMOUNT },
                    COLUMN_EXCHANGE + " = ?", new String[] { exchangeName },
                    null, null, null);

            try
            {
                if(c.getCount() < 1)
                {
                    return -1;
                }

                c.moveToFirst();
                result = c.getDouble(0);
            }
            finally
            {
                c.close();
            }
        }
        finally
        {
            db.close();
        }

        return result;
    }

    public void clearLastBasis()
    {
        SQLiteDatabase db = dbAccess.getWritableDatabase();

        try
        {
            db.delete(TABLE_BASES, COLUMN_EXCHANGE + " = ?",
                    new String[] { exchangeName });
        }
        finally
        {
            db.close();
        }
    }

    public void setLastBasis(double basis)
    {
        clearLastBasis();

        SQLiteDatabase db = dbAccess.getWritableDatabase();

        try
        {
            ContentValues values = new ContentValues();
            values.put(COLUMN_AMOUNT, basis);
            values.put(COLUMN_EXCHANGE, exchangeName);
            db.insertOrThrow(TABLE_BASES, COLUMN_ID, values);
        }
        finally
        {
            db.close();
        }
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

        public final double amount;
        public final int type;

        public Threshold(double amount_, int type_)
        {
            amount = amount_;
            type = type_;
        }
    }

    public static final String TABLE_THRESHOLDS = "_price_change_thresholds";
    public static final String TABLE_BASES = "_price_change_bases";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "_type";
    public static final String COLUMN_EXCHANGE = "_exchange";
    public static final String COLUMN_AMOUNT = "_amount";
}
