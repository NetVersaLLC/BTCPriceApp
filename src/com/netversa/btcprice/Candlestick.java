/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;

import android.os.Parcel;
import android.os.Parcelable;

/** Simple parcelable struct for transmitting and storing price bounds over an
 * interval.
 */
public class Candlestick implements Parcelable
{
    public final BigDecimal open;
    public final BigDecimal close;
    public final BigDecimal high;
    public final BigDecimal low;

    public Candlestick(BigDecimal open_, BigDecimal close_, BigDecimal high_,
            BigDecimal low_)
    {
        open = open_;
        close = close_;
        high = high_;
        low = low_;
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
        {
            return false;
        }
        if(obj == this)
        {
            return true;
        }
        if(!(obj instanceof Candlestick))
        {
            return false;
        }


        Candlestick rhs = (Candlestick) obj;

        if(!eq(rhs.open, open) ||
                !eq(rhs.close, close) ||
                !eq(rhs.high, high) ||
                !eq(rhs.low, low))
        {
            return false;
        }
        return true;
    }

    /** Simple equals() helper function.  Checks if either operand is null and
     * if not punts to the equals() function of the left-hand operand.
     */
    protected static boolean eq(Object lhs, Object rhs)
    {
        if(lhs == null && rhs == null)
        {
            return true;
        }
        if(lhs == null || rhs == null)
        {
            return false;
        }

        return lhs.equals(rhs);
    }

    @Override
    public int hashCode()
    {
        int output = 0;
        output += (open != null ? open.hashCode() : 0) * 2;
        output += (close != null ? close.hashCode() : 0) * 3;
        output += (high != null ? high.hashCode() : 0) * 5;
        output += (low != null ? low.hashCode() : 0) * 7;
        return output;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags)
    {
        out.writeString(open != null ? open.toString() : null);
        out.writeString(close != null ? close.toString() : null);
        out.writeString(high != null ? high.toString() : null);
        out.writeString(low != null ? low.toString() : null);
    }

    public static final Parcelable.Creator<Candlestick> CREATOR
             = new Parcelable.Creator<Candlestick>() {
         public Candlestick createFromParcel(Parcel in) {
             return new Candlestick(in);
         }

         public Candlestick[] newArray(int size) {
             return new Candlestick[size];
         }
     };
     
     private Candlestick(Parcel in) {
         String temp;

         temp = in.readString();
         open = temp != null ? new BigDecimal(temp) : null;
         temp = in.readString();
         close = temp != null ? new BigDecimal(temp) : null;
         temp = in.readString();
         high = temp != null ? new BigDecimal(temp) : null;
         temp = in.readString();
         low = temp != null ? new BigDecimal(temp) : null;
     }
}
