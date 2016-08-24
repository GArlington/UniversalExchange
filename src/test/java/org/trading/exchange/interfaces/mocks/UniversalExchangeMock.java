package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.Exchanged;
import org.trading.exchange.interfaces.UniversalExchange;
import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Location;
import org.trading.exchange.publicInterfaces.Market;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by GArlington.
 */
public class UniversalExchangeMock implements UniversalExchange {
    private String name;
    private Strategy strategy;
    private Collection<Market> markets;
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
    public Collection<Location> getLocations() {
        return null;
    }

    @Override
    public Strategy getStrategy() {
        return strategy;
    }

    @Override
    public Collection<Market> getMarkets() {
        return markets;
    }

    @Override
    public UniversalExchange getPlatform() {
        return platform;
    }

    @Override
    public Market openMarket(Market market, UniversalExchange platform) {
        if (platform.getMarkets().contains(market)) {
            throw new IllegalStateException(market + " is already active on " + platform);
        }
        if (!platform.getMarkets().add(market)) {
            throw new IllegalStateException(market + " can not be created on " + platform);
        }
        return market;
    }

    @Override
    public Market closeMarket(Market market, UniversalExchange platform) {
        if (!platform.getMarkets().contains(market)) {
            throw new IllegalStateException(market + " is not active on " + platform);
        }
        if (!platform.getMarkets().remove(market)) {
            throw new IllegalStateException(market + " can not be closed on " + platform);
        }
        return market;
    }

    @Override
    public Exchangeable validateOrder(Exchangeable exchangeable, UniversalExchange platform) throws IllegalStateException {
        return exchangeable.validate();
    }

    @Override
    public Exchangeable acceptOrder(Exchangeable exchangeable, UniversalExchange platform) throws IllegalStateException {
        return (Exchangeable) validateOrder(exchangeable, platform).preProcess();
    }

    @Override
    public Exchangeable processOrder(Exchangeable exchangeable, Collection<Exchangeable> orders, UniversalExchange platform) {
        return (Exchangeable) validateOrder(exchangeable, platform).process();
    }

    @Override
    public Exchanged postProcessOrder(Exchangeable exchangeable, UniversalExchange platform) {
        return new ExchangedMock((Exchangeable) validateOrder(exchangeable, platform).postProcess(),
                getMatchingOrders(exchangeable, platform));
    }

    @Override
    public Exchanged finaliseOrder(Exchangeable exchangeable, UniversalExchange platform) {
        return new ExchangedMock((Exchangeable) validateOrder(exchangeable, platform).finalise(),
                getMatchingOrders(exchangeable, platform));
    }

    @Override
    public Collection<Exchangeable> getMatchingOrders(Exchangeable exchangeable, UniversalExchange platform) {
        Collection<Exchangeable> orders = new ArrayList<>();
        platform.getMarkets().stream()
                .filter(market -> (market.getOffered().equals(exchangeable.getRequired())
                        && market.getRequired().equals(exchangeable.getOffered())))
                .map(Market::getOrders)
                .forEach(orders::addAll);
        return orders;
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
