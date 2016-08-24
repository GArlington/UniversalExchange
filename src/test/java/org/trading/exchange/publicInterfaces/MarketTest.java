package org.trading.exchange.publicInterfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.Commodity;
import org.trading.exchange.interfaces.Location;
import org.trading.exchange.interfaces.mocks.ExchangeableMock;
import org.trading.exchange.interfaces.mocks.MarketMock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Created by GArlington.
 */
public class MarketTest {
    String id = UUID.randomUUID().toString();
    org.trading.exchange.publicInterfaces.Location location = org.trading.exchange.interfaces.Location.GLOBAL;
    String name = id;
    Commodity offered = Commodity.FUEL_OIL;
    Commodity required = Commodity.USD;
    Exchangeable e1 = new ExchangeableMock(offered, 1000L, required, 1000L);
    Exchangeable e2 = new ExchangeableMock(offered, 1000L, required, 1005L);
    Collection<Exchangeable> orders;

    Market victim;

    @Before
    public void setUp() throws Exception {
        orders = new ArrayList<>();
        orders.add(e1);
        orders.add(e2);
        victim = new MarketMock(id, location, name, offered, required, e1, e2);
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
        org.trading.exchange.publicInterfaces.Location location2 = org.trading.exchange.interfaces.Location.LONDON;
        Market test = new MarketMock(id, location2, name, offered, required, e1, e2);
        assertEquals(false, test.validateLocation());
    }

    @Test
    public void validateMarket() throws Exception {
        victim.validateMarket();
    }

    @Test(expected = IllegalStateException.class)
    public void validateMarketFail() throws Exception {
        org.trading.exchange.publicInterfaces.Location location = Location.ZURICH;
        Market test = new MarketMock(id, location, name, offered, required, e1, e2);

        test.validateMarket();
    }
}