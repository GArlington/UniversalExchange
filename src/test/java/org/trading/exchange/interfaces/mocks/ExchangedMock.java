package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.Exchanged;
import org.trading.exchange.publicInterfaces.Exchangeable;

import java.util.Collection;

/**
 * Created by GArlington.
 */
public class ExchangedMock implements Exchanged {
    Exchangeable exchangeable;
    Collection<Exchangeable> matchedExchangeables;

    public ExchangedMock(Exchangeable exchangeable, Collection<Exchangeable> matchedExchangeables) {
        this.exchangeable = exchangeable;
        this.matchedExchangeables = matchedExchangeables;
    }

    @Override
    public Exchangeable getExchangeable() {
        return exchangeable;
    }

    public Collection<Exchangeable> getMatchedExchangeables() {
        return matchedExchangeables;
    }
}
