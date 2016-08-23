package org.trading.exchange.interfaces;

public interface _DefaultValues {
    // Order size is calculated in smallest market defined units
    // metals - gram
    // Stocks, shares and bonds - single certificate
    // Oil - barrel
    // Fuel Oil - metric ton
    //
    int INACTIVITY_TIMEOUT = 30 * 60 * 1000;    // 30 Minutes default session timeout
    long MINIMUM_ORDER_SIZE = 1L;               // This is a minimum quantity that can be traded
    long PRICED_SIZE = 1L;                      // This is the quantity used when pricing the commodity
    long PRECIOUS_METALS_PRICED_SIZE = 1000L;   // This is the quantity used when pricing precious metals
    long CURRENCY_PRICED_SIZE = 100L;           // This is the quantity used when pricing currency
    long PRICED_TO_MINIMUM_ORDER_RATIO = PRICED_SIZE / MINIMUM_ORDER_SIZE;
    long PRECIOUS_METALS_PRICED_TO_MINIMUM_ORDER_RATIO = PRECIOUS_METALS_PRICED_SIZE / MINIMUM_ORDER_SIZE;
    long CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO = CURRENCY_PRICED_SIZE / MINIMUM_ORDER_SIZE;
    int MARKET_DEPTH = 3;
}