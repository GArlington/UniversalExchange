package org.trading.exchange.publicInterfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.CommodityImpl;
import org.trading.exchange.interfaces.LocationImpl;
import org.trading.exchange.interfaces.mocks.ExchangeableMock;
import org.trading.exchange.interfaces.mocks.MarketMock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Created by GArlington on 19/08/2016.
 */
public class MarketTest {
    String id = UUID.randomUUID().toString();
    Location location = LocationImpl.GLOBAL;
    String name = id;
    CommodityImpl offered = CommodityImpl.FUEL_OIL;
    CommodityImpl required = CommodityImpl.USD;
    Exchangeable e1 = new ExchangeableMock(offered, 1000L, required, 1000L);
    Exchangeable e2 = new ExchangeableMock(offered, 1000L, required, 1005L);
    Collection<Exchangeable> orders;

    Market victim;

    @Before
    public void setUp() throws Exception {
        orders = new ArrayList<>();
        orders.add(e1);
        orders.add(e2);
        victim = new MarketMock(id, location, name, offered, required, orders);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void validateLocation() {
        victim.validateLocation();
    }

    @Test(expected = IllegalStateException.class)
    public void validateLocationFail() {
        Location location2 = LocationImpl.LONDON;
        Market test = new MarketMock(id, location2, name, offered, required, orders);
        assertEquals(false, test.validateLocation());
    }

    @Test
    public void validateMarket() throws Exception {
        victim.validateMarket();
    }

    @Test(expected = IllegalStateException.class)
    public void validateMarketFail() throws Exception {
        Location location = LocationImpl.ZURICH;
        Market test = new MarketMock(id, location, name, offered, required, orders);

        test.validateMarket();
    }
}