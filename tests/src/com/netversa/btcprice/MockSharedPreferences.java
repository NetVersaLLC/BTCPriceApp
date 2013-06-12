/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;

public class MockSharedPreferences implements SharedPreferences
{
    public boolean contains(String key)
    {
        throw new UnsupportedOperationException();
    }
    
    public Editor edit()
    {
        throw new UnsupportedOperationException();
    }

    public Map<String, ?> getAll()
    {
        throw new UnsupportedOperationException();
    }

    public boolean getBoolean(String key, boolean defValue)
    {
        throw new UnsupportedOperationException();
    }

    public float getFloat(String key, float defValue)
    {
        throw new UnsupportedOperationException();
    }

    public int getInt(String key, int defValue)
    {
        throw new UnsupportedOperationException();
    }

    public long getLong(String key, long defValue)
    {
        throw new UnsupportedOperationException();
    }

    public String getString(String key, String defValue)
    {
        throw new UnsupportedOperationException();
    }

    public Set<String> getStringSet(String key, Set<String> defValues)
    {
        throw new UnsupportedOperationException();
    }

    public void registerOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener)
    {
        throw new UnsupportedOperationException();
    }

    public void unregisterOnSharedPreferenceChangeListener(
            OnSharedPreferenceChangeListener listener)
    {
        throw new UnsupportedOperationException();
    }
}
