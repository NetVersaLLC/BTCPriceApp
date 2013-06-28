/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/** Simple parcelable struct for transmitting and storing bitcoin market data
 */
public class MarketData implements Parcelable
{
    public final String exchangeName;

    public final String baseCurrency;
    public final String counterCurrency;

    public final BigDecimal lastPrice;
    public final BigDecimal bidPrice;
    public final BigDecimal askPrice;
    public final BigDecimal highPrice;
    public final BigDecimal lowPrice;

    public final BigDecimal volume;

    public final Date timestamp;

    public MarketData(String exchangeName_, String baseCurrency_,
            String counterCurrency_, BigDecimal lastPrice_,
            BigDecimal bidPrice_, BigDecimal askPrice_, BigDecimal highPrice_,
            BigDecimal lowPrice_, BigDecimal volume_, Date timestamp_)
    {
        exchangeName = exchangeName_;
        baseCurrency = baseCurrency_;
        counterCurrency = counterCurrency_;
        lastPrice = lastPrice_;
        bidPrice = bidPrice_;
        askPrice = askPrice_;
        highPrice = highPrice_;
        lowPrice = lowPrice_;
        volume = volume_;
        timestamp = timestamp_;
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
        if(!(obj instanceof MarketData))
        {
            return false;
        }


        MarketData rhs = (MarketData) obj;

        if(!eq(rhs.exchangeName, exchangeName) ||
                !eq(rhs.baseCurrency, baseCurrency) ||
                !eq(rhs.counterCurrency, counterCurrency) ||
                !eq(rhs.lastPrice, lastPrice) ||
                !eq(rhs.bidPrice, bidPrice) ||
                !eq(rhs.askPrice, askPrice) ||
                !eq(rhs.highPrice, highPrice) ||
                !eq(rhs.lowPrice, lowPrice) ||
                !eq(rhs.volume, volume) ||
                !eq(rhs.timestamp, timestamp))
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
        output += (exchangeName != null ? exchangeName.hashCode() : 0) * 2;
        output += (baseCurrency != null ? baseCurrency.hashCode() : 0) * 3;
        output += (counterCurrency != null ? counterCurrency.hashCode() : 0) * 5;
        output += (lastPrice != null ? lastPrice.hashCode() : 0) * 7;
        output += (bidPrice != null ? bidPrice.hashCode() : 0) * 11;
        output += (askPrice != null ? askPrice.hashCode() : 0) * 13;
        output += (highPrice != null ? highPrice.hashCode() : 0) * 17;
        output += (lowPrice != null ? lowPrice.hashCode() : 0) * 19;
        output += (volume != null ? volume.hashCode() : 0) * 23;
        output += (timestamp != null ? timestamp.hashCode() : 0) * 29;
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
        out.writeString(exchangeName);

        out.writeString(baseCurrency);
        out.writeString(counterCurrency);

        out.writeString(lastPrice != null ? lastPrice.toString() : null);
        out.writeString(bidPrice != null ? bidPrice.toString() : null);
        out.writeString(askPrice != null ? askPrice.toString() : null);
        out.writeString(highPrice != null ? highPrice.toString() : null);
        out.writeString(lowPrice != null ? lowPrice.toString() : null);

        out.writeString(volume != null ? volume.toString() : null);

        out.writeLong(timestamp != null ? timestamp.getTime() : 0);
    }

    public static final Parcelable.Creator<MarketData> CREATOR
             = new Parcelable.Creator<MarketData>() {
         public MarketData createFromParcel(Parcel in) {
             return new MarketData(in);
         }

         public MarketData[] newArray(int size) {
             return new MarketData[size];
         }
     };
     
     private MarketData(Parcel in) {
         exchangeName = in.readString();

         baseCurrency = in.readString();
         counterCurrency = in.readString();

         String temp;
         long longtemps;

         temp = in.readString();
         lastPrice = temp != null ? new BigDecimal(temp) : null;
         temp = in.readString();
         bidPrice = temp != null ? new BigDecimal(temp) : null;
         temp = in.readString();
         askPrice = temp != null ? new BigDecimal(temp) : null;
         temp = in.readString();
         highPrice = temp != null ? new BigDecimal(temp) : null;
         temp = in.readString();
         lowPrice = temp != null ? new BigDecimal(temp) : null;

         temp = in.readString();
         volume = temp != null ? new BigDecimal(temp) : null;

         longtemps = in.readLong();
         timestamp = longtemps != 0 ? new Date(longtemps) : null;
     }
}
