package org.trading.exchange.interfaces;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by GArlington.
 */
public interface Market extends org.trading.exchange.publicInterfaces.Market {
	@Override
	default Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> getOrders(
			org.trading.exchange.publicInterfaces.Exchangeable.State state) {
		return org.trading.exchange.publicInterfaces.Market.super.getOrders(state).stream()
				.sorted(Comparator.comparing(o -> (((Exchangeable) o).getExchangeRate())))
				.collect(Collectors.toList());
	}
}
