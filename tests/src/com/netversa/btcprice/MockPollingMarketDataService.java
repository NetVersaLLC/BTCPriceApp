/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.service.polling.PollingMarketDataService;

public class MockPollingMarketDataService implements PollingMarketDataService
{
    protected MarketData marketData;
    protected List<Transaction> trades;

    public MockPollingMarketDataService(MarketData dummyMarketData,
            List<Transaction> dummyTrades)
    {
        marketData = dummyMarketData;
        trades = dummyTrades;
    }

    public List<CurrencyPair> getExchangeSymbols()
    {
        throw new UnsupportedOperationException();
    }

    public Ticker getTicker(String tradableIdentifier, String currency)
    {
        Ticker.TickerBuilder builder = Ticker.TickerBuilder.newInstance();

        CurrencyUnit counter = CurrencyUnit.of(marketData.counterCurrency);

        builder.withTradableIdentifier(marketData.baseCurrency)
            .withLast(BigMoney.of(counter, marketData.lastPrice))
            .withBid(BigMoney.of(counter, marketData.bidPrice))
            .withAsk(BigMoney.of(counter, marketData.askPrice))
            .withHigh(BigMoney.of(counter, marketData.highPrice))
            .withLow(BigMoney.of(counter, marketData.lowPrice))
            .withVolume(marketData.volume)
            .withTimestamp(marketData.timestamp);

        return builder.build();
    }

    public OrderBook getPartialOrderBook(String tradableIdentifier,
            String currency)
    {
        throw new UnsupportedOperationException();
    }

    public OrderBook getFullOrderBook(String tradableIdentifier,
            String currency)
    {
        throw new UnsupportedOperationException();
    }

    public Trades getTrades(String tradableIdentifier, String currency,
            Object... args)
    {
        List<Trade> result = new ArrayList<Trade>();

        for(Transaction ee : trades)
        {
            Order.OrderType type = ee.type == Transaction.BID ?
                Order.OrderType.BID : Order.OrderType.ASK;
            CurrencyUnit counter = CurrencyUnit.of(ee.counterCurrency);
            result.add(new Trade(type, ee.quantity, ee.baseCurrency,
                        ee.counterCurrency, BigMoney.of(counter, ee.price),
                        ee.timestamp, ee.id));
        }

        return new Trades(result);
    }
}
