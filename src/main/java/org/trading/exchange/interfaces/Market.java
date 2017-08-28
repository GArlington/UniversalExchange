package org.trading.exchange.interfaces;

import org.trading.exchange.publicInterfaces.ExchangeOffer;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Created by GArlington.
 */
public interface Market extends org.trading.exchange.publicInterfaces.Market {
	@Override
	default Collection<? extends ExchangeOffer> getOffers(ExchangeOffer.State state) {
		return org.trading.exchange.publicInterfaces.Market.super.getOffers(state).stream()
				.sorted(Comparator.comparing(o -> (((org.trading.exchange.interfaces.ExchangeOffer) o).getExchangeRate()))).collect(Collectors.toList());
	}
}
