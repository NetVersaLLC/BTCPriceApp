/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.List;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.service.polling.PollingAccountService;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.service.polling.PollingTradeService;
import com.xeiam.xchange.service.streaming.ExchangeStreamingConfiguration;
import com.xeiam.xchange.service.streaming.StreamingExchangeService;

public class MockExchange implements Exchange
{
    protected MarketData marketData;
    protected List<Transaction> trades;

    public MockExchange(MarketData dummyMarketData,
            List<Transaction> dummyTrades)
    {
        marketData = dummyMarketData;
        trades = dummyTrades;
    }

    public ExchangeSpecification getExchangeSpecification()
    {
        throw new UnsupportedOperationException();
    }

    public ExchangeSpecification getDefaultExchangeSpecification()
    {
        throw new UnsupportedOperationException();
    }

    public void applySpecification(ExchangeSpecification exchangeSpecification)
    {
        throw new UnsupportedOperationException();
    }

    public PollingMarketDataService getPollingMarketDataService()
    {
        return new MockPollingMarketDataService(marketData, trades);
    }

    public PollingMarketDataService getPollingMarketDataService(
            ExchangeStreamingConfiguration configuration)
    {
        throw new UnsupportedOperationException();
    }

    public StreamingExchangeService getStreamingExchangeService()
    {
        throw new UnsupportedOperationException();
    }

    public StreamingExchangeService getStreamingExchangeService(
            ExchangeStreamingConfiguration configuration)
    {
        throw new UnsupportedOperationException();
    }

    public PollingTradeService getPollingTradeService()
    {
        throw new UnsupportedOperationException();
    }

    public PollingTradeService getPollingTradeService(
            ExchangeStreamingConfiguration configuration)
    {
        throw new UnsupportedOperationException();
    }

    public PollingAccountService getPollingAccountService()
    {
        throw new UnsupportedOperationException();
    }

    public PollingAccountService getPollingAccountService(
            ExchangeStreamingConfiguration configuration)
    {
        throw new UnsupportedOperationException();
    }
}
