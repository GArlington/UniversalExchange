package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.CollectionOfExchangeableMock;
import org.trading.exchange.interfaces.mocks.ExchangeableMock;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Market;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by GArlington.
 */
public class UniversalExchangeTest {
    String name = "UniversalExchangeName";
    UniversalExchange.Strategy strategy = mock(UniversalExchange.Strategy.class);
    CommodityImpl offered = CommodityImpl.GOLD;
    CommodityImpl required = CommodityImpl.GBP;
    CommodityImpl notRequired = CommodityImpl.USD;
    Exchangeable order1 = new ExchangeableMock(offered, 1_000L, required, 1_000L);
    Exchangeable order2 = new ExchangeableMock(offered, 1_000L, required, 1_003L);
    CollectionOfExchangeableMock orders = new CollectionOfExchangeableMock(order1, order2);
    Market market1 = new MarketMock("requiredMarketId", LocationImpl.LONDON, "marketName", offered, required, orders);

    Exchangeable order11 = new ExchangeableMock(offered, 1_000L, notRequired, 2_000L);
    Exchangeable order12 = new ExchangeableMock(offered, 1_000L, notRequired, 2_003L);
    CollectionOfExchangeableMock orders2 = new CollectionOfExchangeableMock(order11, order12);
    Market market2 = new MarketMock("notRequiredMarketId", LocationImpl.LONDON, "marketName", offered, notRequired, orders2);
    Collection<Market> markets = new ArrayList<>();

    UniversalExchange victim;

    @Before
    public void setUp() throws Exception {
        markets.add(market1);
        markets.add(market2);
        victim = new UniversalExchangeMock(name, strategy, markets);
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

        UniversalExchange test = new UniversalExchangeMock(name, strategy, markets, platform);
        assertEquals(platform, test.getPlatform());
    }

    @Test
    public void openMarket() throws Exception {
        Market market = mock(MarketImpl.class);
        when(strategy.openMarket(market, victim)).thenReturn(market);

        Market result = victim.openMarket(market);
        assertEquals(market, result);
    }

    @Test
    public void closeMarket() throws Exception {
        Market market = market1;
        when(strategy.closeMarket(market, victim)).thenReturn(market);

        Market result = victim.closeMarket(market);
        assertEquals(market, result);
    }

    @Test
    public void validateOrder() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        when(strategy.validateOrder(exchangeable, victim)).thenReturn(exchangeable);

        Exchangeable result = victim.validateOrder(exchangeable);
        assertEquals(exchangeable, result);
    }

    @Test
    public void acceptOrder() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        when(strategy.validateOrder(exchangeable, victim)).thenReturn(exchangeable);
        when(strategy.acceptOrder(exchangeable, victim)).thenReturn(exchangeable);
        when(exchangeable.preProcess()).thenReturn(exchangeable);

        Exchangeable result = victim.acceptOrder(exchangeable);
        assertEquals(exchangeable, result);
    }

    @Test
    public void processOrder() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        when(strategy.processOrder(exchangeable, victim)).thenReturn(exchangeable);
        when(exchangeable.process()).thenReturn(exchangeable);

        Exchangeable result = victim.processOrder(exchangeable);
        assertEquals(exchangeable, result);
    }

    @Test
    public void postProcessOrder() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        Exchanged exchanged = mock(Exchanged.class);
        when(strategy.postProcessOrder(exchangeable, victim)).thenReturn(exchanged);
        when(exchangeable.postProcess()).thenReturn(exchangeable);

        Exchanged result = victim.postProcessOrder(exchangeable);
        assertEquals(exchanged, result);
    }

    @Test
    public void finaliseOrder() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        Exchanged exchanged = mock(Exchanged.class);
        when(strategy.finaliseOrder(exchangeable, victim)).thenReturn(exchanged);
        when(exchangeable.finalise()).thenReturn(exchangeable);

        Exchanged result = victim.finaliseOrder(exchangeable);
        assertEquals(exchanged, result);
    }

    @Test
    public void getMatchingOrders() throws Exception {
        Exchangeable exchangeable = mock(ExchangeableImpl.class);
        Collection<Exchangeable> matchingOrders = mock(Collection.class);
        when(strategy.getMatchingOrders(exchangeable, victim)).thenReturn(matchingOrders);

        Collection<Exchangeable> result = victim.getMatchingOrders(exchangeable);
        assertEquals(matchingOrders, result);
    }


    @Test
    public void openMarketImplementation() throws Exception {
        Market market = mock(MarketImpl.class);

        Market result = victim.openMarket(market, victim);
        assertEquals(market, result);
    }

    @Test(expected = IllegalStateException.class)
    public void openMarketImplementationFail() throws Exception {
        Market market = market1;

        Market result = victim.openMarket(market, victim);
        assertEquals(market, result);
    }

    @Test
    public void closeMarketImplementation() throws Exception {
        Market market = market1;

        Market result = victim.closeMarket(market, victim);
        assertEquals(market, result);
    }

    @Test(expected = IllegalStateException.class)
    public void closeMarketImplementationFail() throws Exception {
        Market market = mock(MarketImpl.class);

        Market result = victim.closeMarket(market, victim);
        assertEquals(market, result);
    }

    @Test
    public void validateOrderImplementation() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        when(exchangeable.validate()).thenReturn(exchangeable);

        Exchangeable result = victim.validateOrder(exchangeable, victim);
        assertEquals(exchangeable, result);
    }

    @Test
    public void acceptOrderImplementation() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        when(exchangeable.validate()).thenReturn(exchangeable);
        when(exchangeable.preProcess()).thenReturn(exchangeable);

        Exchangeable result = victim.acceptOrder(exchangeable, victim);
        assertEquals(exchangeable, result);
    }

    @Test
    public void processOrderImplementation() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        when(exchangeable.validate()).thenReturn(exchangeable);
        when(exchangeable.process()).thenReturn(exchangeable);

        Exchangeable result = victim.processOrder(exchangeable, victim.getMatchingOrders(exchangeable), victim);
        assertEquals(exchangeable, result);
    }

    @Test
    public void postProcessOrderImplementation() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        when(exchangeable.validate()).thenReturn(exchangeable);
        when(exchangeable.postProcess()).thenReturn(exchangeable);
        Exchanged exchanged = mock(Exchanged.class);
        when(exchanged.getExchangeable()).thenReturn(exchangeable);

        Exchanged result = victim.postProcessOrder(exchangeable, victim);
        assertEquals(exchanged.getExchangeable(), result.getExchangeable());
    }

    @Test
    public void finaliseOrderImplementation() throws Exception {
        ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
        when(exchangeable.validate()).thenReturn(exchangeable);
        when(exchangeable.finalise()).thenReturn(exchangeable);
        Exchanged exchanged = mock(Exchanged.class);
        when(exchanged.getExchangeable()).thenReturn(exchangeable);
        when(exchangeable.finalise()).thenReturn(exchangeable);

        Exchanged result = victim.finaliseOrder(exchangeable, victim);
        assertEquals(exchanged.getExchangeable(), result.getExchangeable());
    }

    @Test
    public void getMatchingOrdersImplementation() throws Exception {
        Exchangeable exchangeable = mock(ExchangeableImpl.class);
        when(exchangeable.getRequired()).thenReturn(offered);
        when(exchangeable.getOffered()).thenReturn(required);

        Collection<Exchangeable> result = victim.getMatchingOrders(exchangeable, victim);
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
            Market market = mock(MarketImpl.class);
            when(platform.openMarket(market, platform)).thenReturn(market);

            Market result = victim.openMarket(market, platform);
            assertEquals(market, result);
        }

        @Test
        public void closeMarket() throws Exception {
            Market market = mock(MarketImpl.class);
            when(platform.closeMarket(market, platform)).thenReturn(market);

            Market result = victim.closeMarket(market, platform);
            assertEquals(market, result);
        }

        @Test
        public void validateOrder() throws Exception {
            ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
            when(platform.validateOrder(exchangeable, platform)).thenReturn(exchangeable);

            Exchangeable result = victim.validateOrder(exchangeable, platform);
            assertEquals(exchangeable, result);
        }

        @Test
        public void acceptOrder() throws Exception {
            ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
            when(platform.acceptOrder(exchangeable, platform)).thenReturn(exchangeable);

            Exchangeable result = victim.acceptOrder(exchangeable, platform);
            assertEquals(exchangeable, result);
        }

        @Test
        public void processOrder() throws Exception {
            ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
            Exchanged exchanged = mock(Exchanged.class);
            when(platform.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform), platform))
                    .thenReturn(exchangeable);

            Exchangeable result = victim.processOrder(exchangeable, platform);
            assertEquals(exchangeable, result);
        }

        @Test
        public void processOrder1() throws Exception {
            ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
            Exchanged exchanged = mock(Exchanged.class);
            when(platform.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform), platform))
                    .thenReturn(exchangeable);

            Exchangeable result = victim.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform),
                    platform);
            assertEquals(exchangeable, result);
        }

        @Test
        public void postProcessOrder() throws Exception {
            ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
            when(exchangeable.postProcess()).thenReturn(exchangeable);
            Exchanged exchanged = mock(Exchanged.class);
            when(platform.postProcessOrder(exchangeable, platform)).thenReturn(exchanged);

            Exchanged result = victim.postProcessOrder(exchangeable, platform);
            assertEquals(exchanged, result);
        }

        @Test
        public void finaliseOrder() throws Exception {
            ExchangeableImpl exchangeable = mock(ExchangeableImpl.class);
            when(exchangeable.finalise()).thenReturn(exchangeable);
            Exchanged exchanged = mock(Exchanged.class);
            when(platform.finaliseOrder(exchangeable, platform)).thenReturn(exchanged);

            Exchanged result = victim.finaliseOrder(exchangeable, platform);
            assertEquals(exchanged, result);
        }

        @Test
        public void getMatchingOrders() throws Exception {
            Exchangeable exchangeable = mock(ExchangeableImpl.class);
            Collection<Exchangeable> orders = mock(Collection.class);
            when(platform.getMatchingOrders(exchangeable, platform)).thenReturn(orders);

            Collection<Exchangeable> result = victim.getMatchingOrders(exchangeable, platform);
            assertEquals(orders, result);
        }
    }
}