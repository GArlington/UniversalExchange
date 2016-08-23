package org.trading.exchange.interfaces;

import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Market;

import java.util.Collection;

/**
 * UniversalExchange will provide Exchange functionality
 * <p>
 * Created by GArlington.
 */
public interface UniversalExchange extends org.trading.exchange.publicInterfaces.UniversalExchange {
    Strategy getStrategy();

    UniversalExchange getPlatform();

    /**
     * Open market
     *
     * @param market
     * @return
     */
    @Override
    default Market openMarket(Market market) {
        return getStrategy().openMarket(market, getPlatform());
    }

    /**
     * Create new market
     *
     * @param market
     * @return
     */
    @Override
    default Market closeMarket(Market market) {
        return getStrategy().closeMarket(market, getPlatform());
    }

    /**
     * Validate order
     *
     * @param exchangeable
     * @return validated Exchangeable
     * @throws IllegalStateException
     */
    @Override
    default Exchangeable validateOrder(Exchangeable exchangeable) throws IllegalStateException {
        return getStrategy().validateOrder(exchangeable, getPlatform());
    }

    /**
     * Accept order
     *
     * @param exchangeable
     * @return accepted ExchangeableImpl
     * @throws IllegalStateException
     */
    @Override
    default Exchangeable acceptOrder(Exchangeable exchangeable) throws IllegalStateException {
        return getStrategy().acceptOrder((Exchangeable) validateOrder(exchangeable).preProcess(), getPlatform());
    }

    /**
     * Process order
     *
     * @param exchangeable
     * @return processed ExchangeableImpl
     */
    default Exchangeable processOrder(Exchangeable exchangeable) {
        return getStrategy().processOrder((Exchangeable) exchangeable.process(), getPlatform());
    }

    /**
     * PostProcess order
     *
     * @param exchangeable
     * @return post-processed ExchangeableImpl
     */
    default Exchanged postProcessOrder(Exchangeable exchangeable) {
        return getStrategy().postProcessOrder((Exchangeable) exchangeable.postProcess(), getPlatform());
    }

    /**
     * Finalise order
     *
     * @param exchangeable
     * @return finalised ExchangeableImpl
     */
    default Exchanged finaliseOrder(Exchangeable exchangeable) {
        Exchangeable finalisedExchangeable = (Exchangeable) exchangeable.finalise();
        return getStrategy().finaliseOrder(finalisedExchangeable, getPlatform());
    }

    /**
     * Get matching orders
     *
     * @param exchangeable
     * @return
     */
    @Override
    default Collection<Exchangeable> getMatchingOrders(Exchangeable exchangeable) {
        return getStrategy().getMatchingOrders(exchangeable, getPlatform());
    }

    /**
     * Open market implementation
     *
     * @param market
     * @return
     */
    Market openMarket(Market market, UniversalExchange platform);

    /**
     * Close market implementation
     *
     * @param market
     * @return
     */
    Market closeMarket(Market market, UniversalExchange platform);

    /**
     * Validate order implementation
     *
     * @param exchangeable
     * @param platform
     * @return
     * @throws IllegalStateException
     */
    Exchangeable validateOrder(Exchangeable exchangeable, UniversalExchange platform) throws IllegalStateException;

    /**
     * Accept order implementation
     *
     * @param exchangeable
     * @param platform
     * @return
     * @throws IllegalStateException
     */
    Exchangeable acceptOrder(Exchangeable exchangeable, UniversalExchange platform) throws IllegalStateException;

    /**
     * Process order implementation
     *
     * @param exchangeable
     * @param orders
     * @param platform
     * @return
     */
    Exchangeable processOrder(Exchangeable exchangeable, Collection<Exchangeable> orders, UniversalExchange platform);

    /**
     * PostProcess order implementation
     *
     * @param exchangeable
     * @param platform
     * @return
     */
    Exchanged postProcessOrder(Exchangeable exchangeable, UniversalExchange platform);

    /**
     * Finalise order implementation
     *
     * @param exchangeable
     * @param platform
     * @return
     */
    Exchanged finaliseOrder(Exchangeable exchangeable, UniversalExchange platform);

    /**
     * Get matching orders implementation
     *
     * @param exchangeable
     * @param platform
     * @return
     */
    Collection<Exchangeable> getMatchingOrders(Exchangeable exchangeable, UniversalExchange platform);

    /**
     * Default strategy is to pass all functionality directly to platform specific implementation
     */
    interface Strategy {
        default Market openMarket(Market market, UniversalExchange platform) {
            return platform.openMarket(market, platform);
        }

        default Market closeMarket(Market market, UniversalExchange platform) {
            return platform.closeMarket(market, platform);
        }

        default Exchangeable validateOrder(Exchangeable exchangeable, UniversalExchange platform) throws IllegalStateException {
            return platform.validateOrder(exchangeable, platform);
        }

        default Exchangeable acceptOrder(Exchangeable order, UniversalExchange platform) {
            return platform.acceptOrder(order, platform);
        }

    /*
        default Exchanged matchOrder(ExchangeableImpl order, UniversalExchange platform) {
            return matchOrder(order, getMatchingOrders(order, platform), platform);
        }

        default Exchanged matchOrder(ExchangeableImpl order, Collection<ExchangeableImpl> matchingOrders, UniversalExchange platform) {
            platform.m
        }
    */

        default Exchangeable processOrder(Exchangeable exchangeable, UniversalExchange platform) {
            return processOrder(exchangeable, getMatchingOrders(exchangeable, platform), platform);
        }

        default Exchangeable processOrder(Exchangeable exchangeable, Collection<Exchangeable> orders, UniversalExchange platform) {
            return platform.processOrder(exchangeable, orders, platform);
        }

        default Exchanged postProcessOrder(Exchangeable exchangeable, UniversalExchange platform) {
            return platform.postProcessOrder((Exchangeable) exchangeable.postProcess(), platform);
        }

        default Exchanged finaliseOrder(Exchangeable exchangeable, UniversalExchange platform) {
            return platform.finaliseOrder((Exchangeable) exchangeable.finalise(), platform);
        }

        default Collection<Exchangeable> getMatchingOrders(Exchangeable order, UniversalExchange platform) {
            return platform.getMatchingOrders(order, platform);
        }
    }
}
