/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Provide access to the SQLite database, creating it if necessary.
 */
public class DatabaseAccess extends SQLiteOpenHelper
{
    public static final Pattern commentPattern = Pattern.compile("^(.*?)(--|$)");
    protected Context context;

    public DatabaseAccess(Context context_)
    {
        super(context_, DATABASE_NAME, null, DATABASE_VERSION);
        context = context_;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        try
        {
            execSqlScript(db, SCRIPT_CREATE);
        }
        catch(Exception e)
        {
            throw new RuntimeException("error executing create SQL", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        // virst db version, no upgrade possible
        onCreate(db);
    }

    // courtesy http://stackoverflow.com/questions/2545558/foreign-key-constraints-in-android-using-sqlite-on-delete-cascade
    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        if (!db.isReadOnly())
        {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    public static final String SCRIPT_CREATE = "sql/btcprice-create.sql";

    public static final String NO_GROUP = null;
    public static final String NO_HAVING = null;
    public static final String NO_ORDER = null;
    public static final String NO_WHERE = null;

    public static final String DATABASE_NAME = "btcprice.db";
    public static final int DATABASE_VERSION = 1;

    /**
     * Execute a SQL script asset on the database.  This helper function
     * prevents the necessity of embedding SQL code in Java code, improving
     * cleanliness and organization.  execSQL() will only execute the first
     * statement on a line, so a blank line is necessary between each statement
     * in the SQL file.
     */
    public void execSqlScript(SQLiteDatabase db, String assetName)
        throws IOException
    {
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(context.getAssets()
                        .open(assetName)));

        String line = null;
        String command = "";

        while((line = reader.readLine()) != null)
        {
            Matcher preCommentMatcher = commentPattern.matcher(line);
            preCommentMatcher.find();
            line = preCommentMatcher.group(1);
            System.out.println("no comment line: " + line);
            if("".equals(line))
            {
                if(!"".equals(command))
                {
                    db.execSQL(command);
                }
                command = "";
            }
            else
            {
                command += line + " ";
            }
        }
        if(!"".equals(command))
        {
            db.execSQL(command);
        }
    }
}
