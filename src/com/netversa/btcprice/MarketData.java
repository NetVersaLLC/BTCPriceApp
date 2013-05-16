/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/** Simple parcelable struct for transmitting and storing bitcoin market data
 */
public class MarketData implements Parcelable
{
    public static final String MT_GOX = "mtgox";

    public String exchangeName;

    public String baseCurrency;
    public String counterCurrency;

    public double lastPrice;
    public double bidPrice;
    public double askPrice;
    public double highPrice;
    public double lowPrice;

    public double volume;

    public Date timestamp;

    public MarketData()
    {
        exchangeName = "";
        baseCurrency = "";
        counterCurrency = "";
        timestamp = new Date();
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

        out.writeDouble(lastPrice);
        out.writeDouble(bidPrice);
        out.writeDouble(askPrice);
        out.writeDouble(highPrice);
        out.writeDouble(lowPrice);

        out.writeDouble(volume);

        if(timestamp != null)
        {
            out.writeLong(timestamp.getTime());
        }
        else
        {
            out.writeLong(0);
        }
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

         lastPrice = in.readDouble();
         bidPrice = in.readDouble();
         askPrice = in.readDouble();
         highPrice = in.readDouble();
         lowPrice = in.readDouble();

         volume = in.readDouble();

         timestamp = new Date(in.readLong());
     }
}
