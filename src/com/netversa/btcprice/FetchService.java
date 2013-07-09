/*
 * Copyright 2013 NetVersa LLC
 */
package com.netversa.btcprice;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PatternMatcher;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.dto.Order;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.mtgox.v1.MtGoxExchange;
import com.xeiam.xchange.service.marketdata.polling.PollingMarketDataService;

/** Class for fetching Market data decoupled from Activity lifestyles.
 *
 * By decoupling from activities, the possibility of spamming an exchange
 * because the user is rotating their phone is eliminated without having to
 * resort to ugly AsyncTask or configuration change hacks.
 *
 * Fetch actions are organized by target URI, which dictates where and what to
 * fetch.
 *
 * If a target is requested for which a fetch is already in progress, that
 * request is ignored.
 */
public class FetchService extends Service
{
    public static final String ACTION_REQUEST =
        "com.netversa.btcprice.ACTION_FETCH_REQUEST";
    public static final String ACTION_RESPONSE =
        "com.netversa.btcprice.ACTION_FETCH_RESPONSE";
    public static final String EXTRA_MARKET_DATA =
        "com.netversa.btcprice.EXTRA_MARKET_DATA";
    public static final String EXTRA_LAST_TRADES =
        "com.netversa.btcprice.EXTRA_LAST_TRADES";
    public static final String EXTRA_ERROR_STRING =
        "com.netversa.btcprice.EXTRA_ERROR_STRING";
    public static final String EXTRA_SCHED_FETCH =
        "com.netversa.btcprice.EXTRA_SCHED_FETCH";

    public static final String DATA_SCHEME = "data";
    public static final String MARKET_DATA_ACTION = "market";
    public static final String LAST_TRADES_ACTION = "trades";
    public static final String MARKET_DATA_URI_FORMAT = DATA_SCHEME +
        "://%s/" + MARKET_DATA_ACTION + "/%s/%s";
    public static final String LAST_TRADES_URI_FORMAT = DATA_SCHEME +
        "://%s/" + LAST_TRADES_ACTION + "/%s/%s/%d";

    protected ActiveTargetSet activeTargets;
    // exchanges are probably cached by the ExchangeFactory singleton, but
    // having a cache inside FetchService makes for easy dependency injection
    // for testing
    protected Map<String, Exchange> exchangeCache;
    protected SharedPreferences prefs;

    public FetchService()
    {
        super();
        activeTargets = new ActiveTargetSet();
        exchangeCache = new ConcurrentHashMap<String, Exchange>();
        prefs = null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Uri target = intent.getData();
        if(intent.getBooleanExtra(EXTRA_SCHED_FETCH, false))
        {
            prefs.edit()
                .putLong("last_sched_fetch", SystemClock.elapsedRealtime())
                .commit();
        }
        new Thread(new FetchRunnable(target)).start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    /** Get data from exchange and broadcast it to any interested parties
      */
    protected void doFetch(Uri target)
    {
        // if there is no target to fetch, abort and shut down if appropriate
        if(target == null)
        {
            finalizeFetch(null);
            return;
        }

        // only do any work if the work requested isn't already in progress
        if(!activeTargets.testAndSet(target))
        {
            finalizeFetch(null);
            return;
        }

        Intent resultIntent = new Intent(ACTION_RESPONSE, target);

        String exchangeName = target.getAuthority();
        List<String> arguments = target.getPathSegments();
        // data://exchange/action/args
        if(exchangeName == null || exchangeName.length() == 0 ||
                arguments.size() == 0)
        {
            String format = getString(R.string.fetch_error_bad_target_format);
            String errorString = String.format(format, target.toString());
            resultIntent.putExtra(EXTRA_ERROR_STRING, errorString);
            sendBroadcast(resultIntent);
            finalizeFetch(target);
            return;
        }
        String fetchAction = arguments.get(0);

        // start fetching!

        // market data
        if(MARKET_DATA_ACTION.equalsIgnoreCase(fetchAction))
        {
            if(arguments.size() != 3)
            {
                String format =
                    getString(R.string.fetch_error_wrong_arity_format);
                String errorString = String.format(format, fetchAction, 2);
                resultIntent.putExtra(EXTRA_ERROR_STRING, errorString);
                sendBroadcast(resultIntent);
                finalizeFetch(target);
                return;
            }
            String baseCurrency = arguments.get(1);
            String counterCurrency = arguments.get(2);

            fetchMarketData(resultIntent, exchangeName, baseCurrency,
                    counterCurrency);
        }
        // last trades
        else if(LAST_TRADES_ACTION.equalsIgnoreCase(fetchAction))
        {
            if(arguments.size() != 4)
            {
                String format =
                    getString(R.string.fetch_error_wrong_arity_format);
                String errorString = String.format(format, fetchAction, 3);
                resultIntent.putExtra(EXTRA_ERROR_STRING, errorString);
                sendBroadcast(resultIntent);
                finalizeFetch(target);
                return;
            }
            String baseCurrency = arguments.get(1);
            String counterCurrency = arguments.get(2);
            String sinceString = arguments.get(3);
            long sinceTimestamp = 0;
            try
            {
                sinceTimestamp = Long.parseLong(sinceString);
            }
            catch(NumberFormatException e)
            {
                String format =
                    getString(R.string.fetch_error_invalid_argument_format);
                String errorString = String.format(format, sinceString,
                        "sinceTimestamp");
                resultIntent.putExtra(EXTRA_ERROR_STRING, errorString);
                sendBroadcast(resultIntent);
                finalizeFetch(target);
                return;
            }

            fetchLastTrades(resultIntent, exchangeName, baseCurrency,
                    counterCurrency, sinceTimestamp);
        }
        // unknown action
        else
        {
            String format =
                getString(R.string.fetch_error_unknown_action_format);
            String errorString = String.format(format, fetchAction);
            resultIntent.putExtra(EXTRA_ERROR_STRING, errorString);
        }

        sendBroadcast(resultIntent);

        finalizeFetch(target);
    }

    protected Intent fetchMarketData(Intent output, String exchangeName,
            String baseCurrency, String counterCurrency)
    {
        // TODO select exchange based on URI
        Ticker ticker;
        try
        {
            Exchange exchange = getExchange(exchangeName);
            PollingMarketDataService exchangeData =
                exchange.getPollingMarketDataService();
            ticker = exchangeData.getTicker(baseCurrency,
                    counterCurrency);
        }
        // lazy catch-all with pass-through to user
        catch(Throwable e)
        {
            // prefer a concise human-readable error but fall back on the
            // generally more verbose getString()
            String errorString = e.getMessage();
            if(errorString == null)
            {
                errorString = e.toString();
            }
            output.putExtra(EXTRA_ERROR_STRING, errorString);
            return output;
        }

        MarketData result = new MarketData(exchangeName, baseCurrency,
                counterCurrency, ticker.getLast().getAmount(),
                ticker.getBid().getAmount(), ticker.getAsk().getAmount(),
                ticker.getHigh().getAmount(), ticker.getLow().getAmount(),
                ticker.getVolume(), ticker.getTimestamp());

        output.putExtra(EXTRA_MARKET_DATA, result);

        return output;
    }

    protected Intent fetchLastTrades(Intent output, String exchangeName,
            String baseCurrency, String counterCurrency, long sinceTimestamp)
    {
        // TODO select exchange based on URI
        Trades trades;
        try
        {
            Exchange exchange = getExchange(exchangeName);
            PollingMarketDataService exchangeData =
                exchange.getPollingMarketDataService();
            trades = exchangeData.getTrades(baseCurrency,
                    counterCurrency);
        }
        // lazy catch-all with pass-through to user
        catch(Throwable e)
        {
            // prefer a concise human-readable error but fall back on the
            // generally more verbose getString()
            String errorString = e.getMessage();
            if(errorString == null)
            {
                errorString = e.toString();
            }
            output.putExtra(EXTRA_ERROR_STRING, errorString);
            return output;
        }

        Transaction.List result = new Transaction.List();
        List<Trade> rawTrades = trades.getTrades();
        if(rawTrades.size() > 1000)
        {
            rawTrades = rawTrades.subList(rawTrades.size() - 1000,
                    rawTrades.size());
        }
        for(Trade ee : rawTrades)
        {
            String type = ee.getType() == Order.OrderType.BID ?
                Transaction.BID : Transaction.ASK;
            result.add(new Transaction(type, ee.getTradableAmount(),
                        ee.getTradableIdentifier(), ee.getTransactionCurrency(),
                        ee.getPrice().getAmount(), ee.getTimestamp()));
        }
        output.putExtra(EXTRA_LAST_TRADES, (Parcelable) result);

        return output;
    }

    /** Mark a fetched target inactive and stop the service if necessary.
      */
    protected void finalizeFetch(Uri target)
    {
        synchronized(activeTargets)
        {
            if(target != null)
            {
                activeTargets.unset(target);
            }

            if(activeTargets.size() == 0)
            {
                stopSelf();
            }
        }
        return;
    }

    // TODO overload with default exchange from SharedPrefs
    /** Helper function to produce a data URI to request market data for an
     * exchange and currency pair.
     */
    public static Uri marketTarget(String exchange, String baseCurrency,
            String counterCurrency)
    {
        return Uri.parse(String.format(MARKET_DATA_URI_FORMAT, exchange,
                    baseCurrency, counterCurrency));
    }

    /** Helper function to produce a data URI to request the last trades for an
     * exchange and currency pair.
     */
    public static Uri tradesTarget(String exchange, String baseCurrency,
            String counterCurrency, long sinceTimestamp)
    {
        return Uri.parse(String.format(LAST_TRADES_URI_FORMAT, exchange,
                    baseCurrency, counterCurrency, sinceTimestamp));
    }

    /** Helper function to send a market data fetch request to this service.
     *  @param receiver BroadcastReceiver that will receive result, or null
     */
    public static ComponentName requestMarket(Context context,
            BroadcastReceiver receiver, String exchange, String baseCurrency,
            String counterCurrency)
    {
        Uri target = marketTarget(exchange, baseCurrency, counterCurrency);

        return sendRequest(context, target, receiver);
    }

    /** Helper function to send a last trades fetch request to this service.
     *  @param receiver BroadcastReceiver that will receive result, or null
     */
    public static ComponentName requestTrades(Context context,
            BroadcastReceiver receiver, String exchange, String baseCurrency,
            String counterCurrency, long sinceTimestamp)
    {
        Uri target = tradesTarget(exchange, baseCurrency, counterCurrency,
                sinceTimestamp);

        return sendRequest(context, target, receiver);
    }

    /** Helper function to send a generalized fetch request to this service.
     *  @param receiver BroadcastReceiver that will receive result, or null
     */
    public static ComponentName sendRequest(Context context, Uri target,
            BroadcastReceiver receiver)
    {
        if(receiver != null)
        {
            IntentFilter filter = new IntentFilter(ACTION_RESPONSE);
            filter.addDataScheme(target.getScheme());
            filter.addDataAuthority(target.getAuthority(), null);
            filter.addDataPath(target.getPath(),
                    PatternMatcher.PATTERN_LITERAL);

            context.registerReceiver(receiver, filter);
        }

        return context.startService(new Intent(ACTION_REQUEST, target));
    }

    /** Helper function to send a fetch request to this service with default
     * choices and no local receiver.
     */
    public static ComponentName requestMarket(Context context)
    {
        SharedPreferences prefs =
            PreferenceManager.getDefaultSharedPreferences(context);

        String exchange = prefs.getString("def_exchange",
                Defaults.DEF_EXCHANGE);
        String baseCurrency =
            prefs.getString("def_base", Defaults.DEF_BASE);
        String counterCurrency =
            prefs.getString("def_counter", Defaults.DEF_COUNTER);

        return requestMarket(context, null, exchange, baseCurrency,
                counterCurrency);
    }

    /** Get the XChange exchange object corresponding to a name.  Also provides
     * an injection point for testing with mock exchange objects.
     */
    protected Exchange getExchange(String exchangeName)
    {
        Exchange output = exchangeCache.get(exchangeName);
        if(output != null)
        {
            return output;
        }

        if(Exchanges.MT_GOX.equalsIgnoreCase(exchangeName))
        {
            output = ExchangeFactory.INSTANCE.createExchange(
                    MtGoxExchange.class.getName());
            exchangeCache.put(exchangeName, output);
        }
        else
        {
            String errorFormat =
                getString(R.string.fetch_error_unknown_exchange_format);
            String errorString = String.format(errorFormat, exchangeName);
            throw new IllegalArgumentException(errorString);
        }
        return output;
    }

    /** Runnable wrapper that does actual fetching in a thread.
      */
    protected class FetchRunnable implements Runnable
    {
        protected Uri target;

        public FetchRunnable(Uri target)
        {
            this.target = target;
        }

        public void run()
        {
            doFetch(target);
        }
    }

    /** Simple partially atomic extension of HashSet for keeping track of which
     * targets are actively being fetched.
     */
    protected static class ActiveTargetSet extends HashSet<Uri>
    {
        public synchronized boolean testAndSet(Uri key)
        {
            if(contains(key))
            {
                return false;
            }
            add(key);
            return true;
        }

        public synchronized void unset(Uri key)
        {
            remove(key);
        }
    }
}
