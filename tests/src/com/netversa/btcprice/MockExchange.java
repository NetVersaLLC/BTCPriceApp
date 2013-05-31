/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.service.account.polling.PollingAccountService;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;
import com.xeiam.xchange.service.trade.polling.PollingTradeService;
import com.xeiam.xchange.service.streaming.ExchangeStreamingConfiguration;
import com.xeiam.xchange.service.streaming.StreamingExchangeService;

public class MockExchange implements Exchange
{
    protected MarketData marketData;

    public MockExchange(MarketData dummyData)
    {
        marketData = dummyData;
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
        return new MockPollingMarketDataService(marketData);
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
