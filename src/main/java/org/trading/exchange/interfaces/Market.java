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
		return getOrders().stream()
				.filter(order -> (state.equals(order.getExchangeableState())))
//	TODO - Do we need a separate method to get all states preceding the given state?
//				.filter(order -> (!state.precedes(order.getExchangeableState())))
				.sorted(Comparator.comparing(o -> (((Exchangeable) o).getExchangeRate())))
				.collect(Collectors.toList());
	}
}
