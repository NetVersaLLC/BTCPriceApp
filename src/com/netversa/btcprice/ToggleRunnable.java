/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.concurrent.atomic.AtomicBoolean;

/** Runnable class that can be safely prevented from running by the original creator.
 */
public class ToggleRunnable implements Runnable
{
    protected AtomicBoolean enabled;
    protected Runnable runnable;

    public ToggleRunnable(Runnable runnable_)
    {
        ToggleRunnable(runnable_, true);
    }

    public ToggleRunnable(Runnable runnable_, boolean enabled_)
    {
        enabled = new AtomicBoolean(enabled_);
        runnable = runnable_;
    }

    @Override
    public void run()
    {
        if(!enabled.get())
        {
            return;
        }
        runnable.run();
    }

    public boolean enable()
    {
        return enabled.getAndSet(true);
    }

    public boolean disable()
    {
        return enabled.getAndSet(false);
    }
}
