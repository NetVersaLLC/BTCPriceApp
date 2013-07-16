/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/** Simple parcelable struct for transmitting and storing price bounds over an
 * interval.
 */
public class Candlestick implements Parcelable
{
    public static final double NONE = -1.0;
    public final double open;
    public final double close;
    public final double high;
    public final double low;

    public Candlestick(double open_, double close_, double high_,
            double low_)
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
        output += Double.valueOf(open).hashCode() * 2;
        output += Double.valueOf(close).hashCode() * 3;
        output += Double.valueOf(high).hashCode() * 5;
        output += Double.valueOf(low).hashCode() * 7;
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
        out.writeDouble(open);
        out.writeDouble(close);
        out.writeDouble(high);
        out.writeDouble(low);
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
     
     private Candlestick(Parcel in)
     {
         open = in.readDouble();
         close = in.readDouble();
         high = in.readDouble();
         low = in.readDouble();
     }

     public static class List
             extends ArrayList<Candlestick> implements Parcelable
     {
         public List()
         {
             super();
         }

        @Override
        public int describeContents()
        {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags)
        {
            out.writeInt(size());
            for(Candlestick ee : this)
            {
                out.writeParcelable(ee, ee.describeContents());
            }
        }

        public static final Parcelable.Creator<List> CREATOR
                 = new Parcelable.Creator<List>() {
             public List createFromParcel(Parcel in) {
                 return new List(in);
             }

             public List[] newArray(int size) {
                 return new List[size];
             }
         };

         private List(Parcel in) {
             super();

             int size = in.readInt();

             for(int ii = 0; ii < size; ii++)
             {
                 add((Candlestick) 
                         in.readParcelable(Candlestick.class.getClassLoader()));
             }
         }
     }
}
