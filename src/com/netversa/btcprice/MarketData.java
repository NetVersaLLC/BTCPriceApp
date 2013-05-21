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
    public static final String MT_GOX = "mtgox";

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

         timestamp = new Date(in.readLong());
     }
}
