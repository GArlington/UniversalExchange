package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.UniversalExchange;
import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Exchanged;
import org.trading.exchange.publicInterfaces.Market;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;

/**
 * Created by GArlington.
 */
public class UniversalExchangeMock implements UniversalExchange {
	private String name;
	private Strategy strategy;
	private Collection<? extends Market> markets;
	private UniversalExchange platform;

	public UniversalExchangeMock(String name, Strategy strategy, Market... markets) {
		this.name = name;
		this.strategy = strategy;
		this.markets = new LinkedList<>(Arrays.asList(markets));
		this.platform = this;
	}

	public UniversalExchangeMock(String name, Strategy strategy, UniversalExchange platform, Market... markets) {
		this(name, strategy, markets);
		this.platform = platform;
	}

	@Override
	public String getName() {
		return name;
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
		if (platform.getMarkets().contains(market)) {
			throw new IllegalStateException(market + " is already active on " + platform);
		}
		return market;
	}

	@Override
	public Market open(Market market, UniversalExchange platform) throws IllegalStateException {
		validate(market, platform);
		if (!((Collection<Market>) platform.getMarkets()).add(market)) {
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
	public Exchangeable validate(Exchangeable exchangeable, UniversalExchange platform)
			throws IllegalStateException {
		return exchangeable.validate();
	}

	@Override
	public Exchangeable accept(Exchangeable exchangeable, UniversalExchange platform)
			throws IllegalStateException {
		Exchangeable temp = (Exchangeable) validate(exchangeable, platform).preProcess();
		Collection<? extends Exchangeable> exchangeables = getMatching(temp, platform);
		if (exchangeables.size() > 0) {
			match(temp, platform, exchangeables.toArray(new Exchangeable[exchangeables.size()]));
		}
		for (Market market : getMarkets()) {
			if (market.validate(temp)) {
				if (market.accept(temp)) {
					temp = ((org.trading.exchange.interfaces.Exchangeable) temp).open();
					return temp;
				}
			}
		}
		return null;
	}

	@Override
	public Exchangeable process(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching) {
		return (Exchangeable) validate(exchangeable, platform).process();
	}

	@Override
	public Exchanged postProcess(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching) {
		return new org.trading.exchange.model.Exchanged((Exchangeable) validate(exchangeable, platform).postProcess(),
				matching);
	}

	@Override
	public Exchanged finalise(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching) {
		return new org.trading.exchange.model.Exchanged((Exchangeable) validate(exchangeable, platform).finalise(),
				matching);
	}

	@Override
	public Collection<? extends Exchangeable> getMatching(Exchangeable exchangeable, UniversalExchange
			platform) {
		Collection<Exchangeable> orders = new LinkedList<>();
		platform.getMarkets().stream()
				.filter(market -> market.validate(exchangeable))
				.map(market -> market.getOrders(Exchangeable.State.OPEN))
				.forEach(orders::addAll);
		return orders.stream().filter(e -> e.isMatching(exchangeable)).collect(Collectors.toList());
	}

	@Override
	public Exchanged match(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching) {
		Collection<Exchangeable> matchedOrders = new LinkedList<>();
		Stream.of(matching)
				.filter(order -> (!Exchangeable.State.OPEN.precedes(order.getExchangeableState()) &&
						order.getOffered().equals(exchangeable.getRequired()) &&
						order.getRequired().equals(exchangeable.getOffered())))
				.map(ex2 -> (org.trading.exchange.interfaces.Exchangeable) ex2)
				.sorted(comparing(org.trading.exchange.interfaces.Exchangeable::getExchangeRate))
				.forEach(ex3 -> matchedOrders.add(exchangeable.match(ex3)));
		return new org.trading.exchange.model.Exchanged(exchangeable,
				matchedOrders.toArray(new Exchangeable[matchedOrders.size()]));
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
