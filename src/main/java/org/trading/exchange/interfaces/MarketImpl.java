package org.trading.exchange.interfaces;

import org.trading.exchange.publicInterfaces.Market;

/**
 * Created by GArlington.
 */
public interface MarketImpl extends Market {
    @Override
    default void validateMarket() {
        if (!(validateLocation() && getOrders().stream().allMatch(
                exchangeable -> exchangeable.getOffered().equals(getOffered()) &&
                        exchangeable.getRequired().equals(getRequired())))) {
            throw new IllegalStateException(this + " configuration is invalid");
        }
    }

    @Override
    default boolean validateLocation() {
        return getLocation().checkCommodity(getOffered()) && getLocation().checkCommodity(getRequired());
    }
}
