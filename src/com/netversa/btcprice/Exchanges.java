/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.content.Context;

/** Central storage singleton for information relevant to multiple components.
 */
public final class Exchanges
{
    public static final String MT_GOX = "mtgox";

    private static Exchanges singleton;

    private Map<String, Integer> labelIds;
    private Set<String> names;

    private Exchanges()
    {
        names = new HashSet<String>();
        names.add(MT_GOX);

        labelIds = new HashMap<String, Integer>();
        labelIds.put(MT_GOX, R.string.mtgox);
    }

    public static Exchanges instance()
    {
        if(singleton == null)
        {
            singleton = new Exchanges();
        }

        return singleton;
    }

    public boolean known(String name)
    {
        return names.contains(name);
    }

    public String label(Context context, String name)
    {
        return context.getString(labelId(name));
    }

    public int labelId(String name)
    {
        Integer id = labelIds.get(name);
        if(id == null)
        {
            throw new IllegalArgumentException(
                    "unknown exchange '" + name + "'");
        }
        return id.intValue();
    }
}
