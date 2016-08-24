package org.trading.exchange.interfaces;

import org.trading.exchange.publicInterfaces.Exchangeable;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by GArlington.
 */
public interface Exchanged extends Serializable {
	Exchangeable getExchangeable();

	Collection<Exchangeable> getMatchedExchangeables();
}
