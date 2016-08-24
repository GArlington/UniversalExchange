package org.trading.exchange.interfaces;

import org.math.SimpleDecimal;
import org.trading.exchange.publicInterfaces.Exchangeable;

/**
 * Created by GArlington.
 */
public interface ExchangeRate {
	SimpleDecimal getExchangeRate(Exchangeable from, Exchangeable to);
}
