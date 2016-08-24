package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.CollectionOfExchangeableMock;
import org.trading.exchange.interfaces.mocks.ExchangeableMock;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by GArlington.
 */
public class UniversalExchangeTest {
    String name = "UniversalExchangeName";
    UniversalExchange.Strategy strategy = mock(UniversalExchange.Strategy.class);
    org.trading.exchange.publicInterfaces.Commodity offered = Commodity.GOLD;
    org.trading.exchange.publicInterfaces.Commodity required = Commodity.GBP;
    org.trading.exchange.publicInterfaces.Commodity notRequired = Commodity.USD;

    org.trading.exchange.publicInterfaces.Exchangeable order1 = new ExchangeableMock(offered, 1_000L, required, 1_000L);
    org.trading.exchange.publicInterfaces.Exchangeable order2 = new ExchangeableMock(offered, 1_000L, required, 1_003L);
    CollectionOfExchangeableMock orders = new CollectionOfExchangeableMock(order1, order2);
    org.trading.exchange.publicInterfaces.Market requiredMarket = new MarketMock("requiredMarketId", Location.LONDON, "marketName", offered, required, order1, order2);

    org.trading.exchange.publicInterfaces.Exchangeable order11 = new ExchangeableMock(offered, 1_000L, notRequired, 2_000L);
    org.trading.exchange.publicInterfaces.Exchangeable order12 = new ExchangeableMock(offered, 1_000L, notRequired, 2_003L);
    CollectionOfExchangeableMock orders2 = new CollectionOfExchangeableMock(order11, order12);
    org.trading.exchange.publicInterfaces.Market notRequiredMarket = new MarketMock("notRequiredMarketId", Location.LONDON, "marketName", offered, notRequired, order11, order12);

    UniversalExchange victim;

    @Before
    public void setUp() throws Exception {
        victim = new UniversalExchangeMock(name, strategy, requiredMarket, notRequiredMarket);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void getName() throws Exception {
        assertEquals(name, victim.getName());
    }

    @Test
    public void getStrategy() throws Exception {
        assertEquals(strategy, victim.getStrategy());
    }

    @Test
    public void getPlatform() throws Exception {
        UniversalExchange platform = mock(UniversalExchange.class);

        UniversalExchange test = new UniversalExchangeMock(name, strategy, platform, requiredMarket, notRequiredMarket);
        assertEquals(platform, test.getPlatform());
    }

    @Test
    public void openMarket() throws Exception {
        org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
        when(strategy.openMarket(market, victim)).thenReturn(market);

        org.trading.exchange.publicInterfaces.Market result = victim.openMarket(market);
        assertEquals(market, result);
    }

    @Test
    public void closeMarket() throws Exception {
        org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
        when(strategy.closeMarket(market, victim)).thenReturn(market);

        org.trading.exchange.publicInterfaces.Market result = victim.closeMarket(market);
        assertEquals(market, result);
    }

    @Test
    public void validateOrder() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(strategy.validateOrder(exchangeable, victim)).thenReturn(exchangeable);

        org.trading.exchange.publicInterfaces.Exchangeable result = victim.validateOrder(exchangeable);
        assertEquals(exchangeable, result);
    }

    @Test
    public void acceptOrder() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(strategy.validateOrder(exchangeable, victim)).thenReturn(exchangeable);
        when(strategy.acceptOrder(exchangeable, victim)).thenReturn(exchangeable);
        when(exchangeable.preProcess()).thenReturn(exchangeable);

        org.trading.exchange.publicInterfaces.Exchangeable result = victim.acceptOrder(exchangeable);
        assertEquals(exchangeable, result);
    }

    @Test
    public void processOrder() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(strategy.processOrder(exchangeable, victim)).thenReturn(exchangeable);
        when(exchangeable.process()).thenReturn(exchangeable);

        org.trading.exchange.publicInterfaces.Exchangeable result = victim.processOrder(exchangeable);
        assertEquals(exchangeable, result);
    }

    @Test
    public void postProcessOrder() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        Exchanged exchanged = mock(Exchanged.class);
        when(strategy.postProcessOrder(exchangeable, victim)).thenReturn(exchanged);
        when(exchangeable.postProcess()).thenReturn(exchangeable);

        Exchanged result = victim.postProcessOrder(exchangeable);
        assertEquals(exchanged, result);
    }

    @Test
    public void finaliseOrder() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        Exchanged exchanged = mock(Exchanged.class);
        when(strategy.finaliseOrder(exchangeable, victim)).thenReturn(exchanged);
        when(exchangeable.finalise()).thenReturn(exchangeable);

        Exchanged result = victim.finaliseOrder(exchangeable);
        assertEquals(exchanged, result);
    }

    @Test
    public void getMatchingOrders() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(Exchangeable.class);
        Collection<org.trading.exchange.publicInterfaces.Exchangeable> matchingOrders = mock(Collection.class);
        when(strategy.getMatchingOrders(exchangeable, victim)).thenReturn(matchingOrders);

        Collection<org.trading.exchange.publicInterfaces.Exchangeable> result = victim.getMatchingOrders(exchangeable);
        assertEquals(matchingOrders, result);
    }


    @Test
    public void openMarketImplementation() throws Exception {
        org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);

        org.trading.exchange.publicInterfaces.Market result = victim.openMarket(market, victim);
        assertEquals(market, result);
    }

    @Test(expected = IllegalStateException.class)
    public void openMarketImplementationFail() throws Exception {
        org.trading.exchange.publicInterfaces.Market market = requiredMarket;

        org.trading.exchange.publicInterfaces.Market result = victim.openMarket(market, victim);
        assertEquals(market, result);
    }

    @Test
    public void closeMarketImplementation() throws Exception {
        org.trading.exchange.publicInterfaces.Market market = requiredMarket;

        org.trading.exchange.publicInterfaces.Market result = victim.closeMarket(market, victim);
        assertEquals(market, result);
    }

    @Test(expected = IllegalStateException.class)
    public void closeMarketImplementationFail() throws Exception {
        org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);

        org.trading.exchange.publicInterfaces.Market result = victim.closeMarket(market, victim);
        assertEquals(market, result);
    }

    @Test
    public void validateOrderImplementation() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(exchangeable.validate()).thenReturn(exchangeable);

        org.trading.exchange.publicInterfaces.Exchangeable result = victim.validateOrder(exchangeable, victim);
        assertEquals(exchangeable, result);
    }

    @Test
    public void acceptOrderImplementation() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(exchangeable.validate()).thenReturn(exchangeable);
        when(exchangeable.preProcess()).thenReturn(exchangeable);

        org.trading.exchange.publicInterfaces.Exchangeable result = victim.acceptOrder(exchangeable, victim);
        assertEquals(exchangeable, result);
    }

    @Test
    public void processOrderImplementation() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(exchangeable.validate()).thenReturn(exchangeable);
        when(exchangeable.process()).thenReturn(exchangeable);

        org.trading.exchange.publicInterfaces.Exchangeable result = victim.processOrder(exchangeable, victim.getMatchingOrders(exchangeable), victim);
        assertEquals(exchangeable, result);
    }

    @Test
    public void postProcessOrderImplementation() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(exchangeable.validate()).thenReturn(exchangeable);
        when(exchangeable.postProcess()).thenReturn(exchangeable);
        Exchanged exchanged = mock(Exchanged.class);
        when(exchanged.getExchangeable()).thenReturn(exchangeable);
        UniversalExchange spyed = spy(victim);
        Collection<org.trading.exchange.publicInterfaces.Exchangeable> matchedOrders = mock(Collection.class);
        doReturn(matchedOrders).when(spyed).getMatchingOrders(exchangeable, spyed);

        Exchanged result = spyed.postProcessOrder(exchangeable, spyed);
        assertEquals(exchanged.getExchangeable(), result.getExchangeable());
        assertEquals(matchedOrders, result.getMatchedExchangeables());
    }

    @Test
    public void finaliseOrderImplementation() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(exchangeable.validate()).thenReturn(exchangeable);
        when(exchangeable.finalise()).thenReturn(exchangeable);
        Exchanged exchanged = mock(Exchanged.class);
        when(exchanged.getExchangeable()).thenReturn(exchangeable);
        UniversalExchange spyed = spy(victim);
        Collection<org.trading.exchange.publicInterfaces.Exchangeable> matchedOrders = mock(Collection.class);
        doReturn(matchedOrders).when(spyed).getMatchingOrders(exchangeable, spyed);

        Exchanged result = spyed.finaliseOrder(exchangeable, spyed);
        assertEquals(exchanged.getExchangeable(), result.getExchangeable());
        assertEquals(matchedOrders, result.getMatchedExchangeables());
    }

    @Test
    public void getMatchingOrdersImplementation() throws Exception {
        org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
        when(exchangeable.getRequired()).thenReturn(offered);
        when(exchangeable.getOffered()).thenReturn(required);

        Collection<org.trading.exchange.publicInterfaces.Exchangeable> result = victim.getMatchingOrders(exchangeable, victim);
        assertEquals(orders.getCollection(), result);
    }

    public static class StrategyTest {
        UniversalExchange platform = mock(UniversalExchange.class);
        UniversalExchange.Strategy victim;

        @Before
        public void setUp() throws Exception {
            victim = new UniversalExchange.Strategy() {
            };
        }

        @After
        public void tearDown() throws Exception {

        }

        @Test
        public void openMarket() throws Exception {
            org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
            when(platform.openMarket(market, platform)).thenReturn(market);

            org.trading.exchange.publicInterfaces.Market result = victim.openMarket(market, platform);
            assertEquals(market, result);
        }

        @Test
        public void closeMarket() throws Exception {
            org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
            when(platform.closeMarket(market, platform)).thenReturn(market);

            org.trading.exchange.publicInterfaces.Market result = victim.closeMarket(market, platform);
            assertEquals(market, result);
        }

        @Test
        public void validateOrder() throws Exception {
            org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
            when(platform.validateOrder(exchangeable, platform)).thenReturn(exchangeable);

            org.trading.exchange.publicInterfaces.Exchangeable result = victim.validateOrder(exchangeable, platform);
            assertEquals(exchangeable, result);
        }

        @Test
        public void acceptOrder() throws Exception {
            org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
            when(platform.acceptOrder(exchangeable, platform)).thenReturn(exchangeable);

            org.trading.exchange.publicInterfaces.Exchangeable result = victim.acceptOrder(exchangeable, platform);
            assertEquals(exchangeable, result);
        }

        @Test
        public void processOrder() throws Exception {
            org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
            Exchanged exchanged = mock(Exchanged.class);
            when(platform.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform), platform))
                    .thenReturn(exchangeable);

            org.trading.exchange.publicInterfaces.Exchangeable result = victim.processOrder(exchangeable, platform);
            assertEquals(exchangeable, result);
        }

        @Test
        public void processOrder1() throws Exception {
            org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
            Exchanged exchanged = mock(Exchanged.class);
            when(platform.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform), platform))
                    .thenReturn(exchangeable);

            org.trading.exchange.publicInterfaces.Exchangeable result = victim.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform),
                    platform);
            assertEquals(exchangeable, result);
        }

        @Test
        public void postProcessOrder() throws Exception {
            org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
            when(exchangeable.postProcess()).thenReturn(exchangeable);
            Exchanged exchanged = mock(Exchanged.class);
            when(platform.postProcessOrder(exchangeable, platform)).thenReturn(exchanged);

            Exchanged result = victim.postProcessOrder(exchangeable, platform);
            assertEquals(exchanged, result);
        }

        @Test
        public void finaliseOrder() throws Exception {
            org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
            when(exchangeable.finalise()).thenReturn(exchangeable);
            Exchanged exchanged = mock(Exchanged.class);
            when(platform.finaliseOrder(exchangeable, platform)).thenReturn(exchanged);

            Exchanged result = victim.finaliseOrder(exchangeable, platform);
            assertEquals(exchanged, result);
        }

        @Test
        public void getMatchingOrders() throws Exception {
            org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
            Collection<org.trading.exchange.publicInterfaces.Exchangeable> orders = mock(Collection.class);
            when(platform.getMatchingOrders(exchangeable, platform)).thenReturn(orders);

            Collection<org.trading.exchange.publicInterfaces.Exchangeable> result = victim.getMatchingOrders(exchangeable, platform);
            assertEquals(orders, result);
        }
    }
}