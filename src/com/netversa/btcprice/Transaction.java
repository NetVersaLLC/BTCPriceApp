/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/** Simple parcelable struct for transmitting and storing bitcoin trade data
 */
public class Transaction implements Parcelable
{
    public static final String BID = "bid";
    public static final String ASK = "ask";

    public final String type;
    public final BigDecimal quantity;
    public final String baseCurrency;
    public final String counterCurrency;
    public final BigDecimal price;
    public final Date timestamp;

    public Transaction(String type_, BigDecimal quantity_,
            String baseCurrency_, String counterCurrency_, BigDecimal price_,
            Date timestamp_)
    {
        type = type_;
        quantity = quantity_;
        baseCurrency = baseCurrency_;
        counterCurrency = counterCurrency_;
        price = price_;
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
        if(!(obj instanceof Transaction))
        {
            return false;
        }


        Transaction rhs = (Transaction) obj;

        if(!eq(rhs.type, type) ||
                !eq(rhs.quantity, quantity) ||
                !eq(rhs.baseCurrency, baseCurrency) ||
                !eq(rhs.counterCurrency, counterCurrency) ||
                !eq(rhs.price, price) ||
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
        output += (type != null ? type.hashCode() : 0) * 2;
        output += (quantity != null ? quantity.hashCode() : 0) * 3;
        output += (baseCurrency != null ? baseCurrency.hashCode() : 0) * 5;
        output += (counterCurrency != null ?
                counterCurrency.hashCode() : 0) * 7;
        output += (price != null ? price.hashCode() : 0) * 11;
        output += (timestamp != null ? timestamp.hashCode() : 0) * 13;
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
        out.writeString(type);
        out.writeString(quantity != null ? quantity.toString() : null);
        out.writeString(baseCurrency);
        out.writeString(counterCurrency);
        out.writeString(price != null ? price.toString() : null);
        out.writeLong(timestamp != null ? timestamp.getTime() : 0);
    }

    public static final Parcelable.Creator<Transaction> CREATOR
             = new Parcelable.Creator<Transaction>() {
         public Transaction createFromParcel(Parcel in) {
             return new Transaction(in);
         }

         public Transaction[] newArray(int size) {
             return new Transaction[size];
         }
     };

     private Transaction(Parcel in) {
         String temp;
         long longtemps;

         type = in.readString();

         temp = in.readString();
         quantity = temp != null ? new BigDecimal(temp) : null;

         baseCurrency = in.readString();
         counterCurrency = in.readString();

         temp = in.readString();
         price = temp != null ? new BigDecimal(temp) : null;

         longtemps = in.readLong();
         timestamp = longtemps != 0 ? new Date(longtemps) : null;
     }
}
