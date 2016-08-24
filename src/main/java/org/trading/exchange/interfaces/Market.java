package org.trading.exchange.interfaces;

/**
 * Created by GArlington.
 */
public interface Market extends org.trading.exchange.publicInterfaces.Market {
    @Override
    default void validateMarket() {
        if (!validateLocation()) {
            throw new IllegalStateException(this + " configuration is invalid. "
//                    + getLocation() + " can't handle offered:" + getOffered() + " or required:" + getRequired()
            );
        }
        if (!getOrders().stream().allMatch(exchangeable -> (
                (exchangeable.getOffered().equals(getOffered()) && exchangeable.getRequired().equals(getRequired()))
                        || (exchangeable.getRequired().equals(getOffered()) && exchangeable.getOffered().equals(getRequired())))
        )) {
            throw new IllegalStateException(this + " configuration is invalid. "
//                    + getOrders() + " don't match offered:" + getOffered() + " or required:" + getRequired()
            );
        }
    }

    @Override
    default boolean validateLocation() {
        return getLocation().checkCommodity(getOffered()) && getLocation().checkCommodity(getRequired());
    }
}
