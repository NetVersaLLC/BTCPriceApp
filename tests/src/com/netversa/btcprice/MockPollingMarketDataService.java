/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.List;
import java.math.BigDecimal;
import java.util.Date;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.OrderBook;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

public class MockPollingMarketDataService implements PollingMarketDataService
{
    protected MarketData marketData;

    public MockPollingMarketDataService(MarketData dummyData)
    {
        marketData = dummyData;
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
        throw new UnsupportedOperationException();
    }
}
