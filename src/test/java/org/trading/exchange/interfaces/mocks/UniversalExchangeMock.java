package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.UniversalExchange;
import org.trading.exchange.publicInterfaces.ExchangeOffer;
import org.trading.exchange.publicInterfaces.Exchanged;
import org.trading.exchange.publicInterfaces.Market;
import org.trading.exchange.publicInterfaces.Owner;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by GArlington.
 */
public class UniversalExchangeMock implements UniversalExchange {
	private String id;
	private String name;
	private Strategy strategy;
	private Collection<? extends Market> markets = new LinkedList<>();
	private UniversalExchange platform;
	private Owner owner;
	private boolean autoMatching;

	public UniversalExchangeMock(String name, Strategy strategy, Owner owner, boolean autoMatching, Market...
			markets) {
		this.id = UUID.randomUUID().toString();
		this.platform = this;
		this.name = name;
		this.strategy = strategy;
		this.owner = owner;
		for (Market market : markets) {
			assertNotNull(this.open(market, this.getPlatform()));
		}
		this.autoMatching = autoMatching;
	}

	public UniversalExchangeMock(String name, Strategy strategy, UniversalExchange platform, Owner owner,
								 boolean autoMatching,
								 Market... markets) {
		this(name, strategy, owner, autoMatching, markets);
		this.platform = platform;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isAutoMatching() {
		return autoMatching;
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public Collection<? extends Market> getMarkets() {
		return markets;
	}

	@Override
	public Strategy getStrategy() {
		return strategy;
	}

	@Override
	public UniversalExchange getPlatform() {
		return platform;
	}

	@Override
	public Market validate(Market market, UniversalExchange platform) throws IllegalStateException {
		market.validate();
		if (platform.getMarkets().contains(market)) {
			throw new IllegalStateException(market + " is already active on " + platform);
		}
		return market;
	}

	@Override
	public Market open(Market market, UniversalExchange platform) throws IllegalStateException {
		validate(market, platform);
		@SuppressWarnings("unchecked")
		Collection<Market> markets = (Collection<Market>) platform.getMarkets();
		if (!markets.add(market)) {
			throw new IllegalStateException(market + " can not be created on " + platform);
		}
		return market;
	}

	@Override
	public boolean close(Market market, UniversalExchange platform) {
		if (!platform.getMarkets().contains(market)) {
			throw new IllegalStateException(market + " is not active on " + platform);
		}
		if (!platform.getMarkets().remove(market)) {
			throw new IllegalStateException(market + " can not be closed on " + platform);
		}
		return true;
	}

	@Override
	public ExchangeOffer validate(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform)
			throws InvalidParameterException, IllegalStateException {
		if (!market.validate(exchangeOffer)) {
			throw new InvalidParameterException(exchangeOffer + " can't be handled by " + market + " on " + platform);
		}
		return exchangeOffer.validate();
	}

	@Override
	public ExchangeOffer accept(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform)
			throws IllegalStateException {
		ExchangeOffer offer = (ExchangeOffer) platform.validate(exchangeOffer, market, platform).preProcess();
		if (market.validate(offer)) {
			if (isAutoMatching() && market.isAutoMatching()) {
				Collection<? extends ExchangeOffer> offers = getMatching(offer, market, platform);
				if (offers.size() > 0) {
					match(offer, market, platform, offers.toArray(new ExchangeOffer[offers.size()]));
				}
			}
			return market.accept(offer);
		}
		return null;
	}

	@Override
	public ExchangeOffer process(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform,
								 ExchangeOffer... matching) {
		return (ExchangeOffer) validate(exchangeOffer, market, platform).process();
	}

	@Override
	public Exchanged postProcess(ExchangeOffer exchangeOffer, UniversalExchange platform, ExchangeOffer... matching) {
		// TODO - maybe change asserts to more appropriate error handling method
		assert exchangeOffer.getExchanged() != null;
		assert exchangeOffer.getExchanged().getMatchedExchangeOffers().containsAll(Arrays.asList(matching));

		return ((ExchangeOffer) exchangeOffer.postProcess()).getExchanged();
	}

	@Override
	public Exchanged finalise(ExchangeOffer exchangeOffer, UniversalExchange platform, ExchangeOffer... matching) {
		// TODO - maybe change asserts to more appropriate error handling method
		assert exchangeOffer.getExchanged() != null;
		assert exchangeOffer.getExchanged().getMatchedExchangeOffers().containsAll(Arrays.asList(matching));

		return ((ExchangeOffer) exchangeOffer.finalise()).getExchanged();
	}

	@Override
	public Collection<? extends ExchangeOffer> getMatching(ExchangeOffer exchangeOffer, Market market,
														   UniversalExchange platform) {
		if (market.validate(exchangeOffer)) {
			return market.getOffers(ExchangeOffer.State.OPEN).stream().filter(offer -> offer.isMatching(exchangeOffer))
					.collect(Collectors.toList());
		}
		return new LinkedList<>();
	}

	@Override
	public Exchanged match(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform,
						   ExchangeOffer... matching) {
		if (market.validate(exchangeOffer)) {
			Collection<ExchangeOffer> matched = new LinkedList<>();
			Stream.of(matching)
					.filter(market::validate)
					.filter(offer -> (!ExchangeOffer.State.OPEN.precedes(offer.getState()) &&
							offer.isMatching(exchangeOffer)))
					.map(offer -> (org.trading.exchange.interfaces.ExchangeOffer) offer)
					.sorted(comparing(org.trading.exchange.interfaces.ExchangeOffer::getExchangeRate))
					.forEach(offer -> matched.add(exchangeOffer.match(offer)));
			org.trading.exchange.interfaces.Exchanged exchanged = mock(org.trading.exchange.interfaces.Exchanged
					.class);

			doReturn(exchangeOffer).when(exchanged).getExchangeOffer();
			doReturn(matched).when(exchanged).getMatchedExchangeOffers();
			return exchanged;
		}
		return null;
	}

	@Override
	public String toString() {
		return "UniversalExchangeMock{" +
				"name='" + name + '\'' +
				", strategy=" + strategy +
				", \nmarkets=\n" + markets +
//                ", platform=" + platform +
				'}';
	}
}
