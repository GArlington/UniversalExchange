package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.Exchanged;
import org.trading.exchange.publicInterfaces.Exchangeable;

import java.util.Collection;

/**
 * Created by GArlington.
 */
public class ExchangedMock implements Exchanged {
	Exchangeable exchangeable;
	Collection<? extends Exchangeable> matchedExchangeables;

	public ExchangedMock(Exchangeable exchangeable, Collection<? extends Exchangeable> matchedExchangeables) {
		this.exchangeable = exchangeable;
		this.exchangeable.setExchanged(this);
		this.matchedExchangeables = matchedExchangeables;
		if (this.matchedExchangeables != null && this.matchedExchangeables.size() > 0) {
			for (Exchangeable ex : this.matchedExchangeables) {
				if (ex != null) ex.setExchanged(this);
			}
		}
	}

	@Override
	public Exchangeable getExchangeable() {
		return exchangeable;
	}

	public Collection<? extends Exchangeable> getMatchedExchangeables() {
		return matchedExchangeables;
	}
}
