package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.CollectionOfExchangeableMock;
import org.trading.exchange.interfaces.mocks.ExchangeableMock;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.publicInterfaces.Exchanged;

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

	org.trading.exchange.publicInterfaces.Exchangeable order1 = new ExchangeableMock(offered, 1_000L, required,
			1_000L);
	org.trading.exchange.publicInterfaces.Exchangeable order2 = new ExchangeableMock(offered, 1_000L, required,
			1_003L);
	CollectionOfExchangeableMock orders = new CollectionOfExchangeableMock(order1, order2);
	org.trading.exchange.publicInterfaces.Market requiredMarket =
			new MarketMock("requiredMarketId", Location.LONDON, "marketName", offered, required, order2, order1);

	org.trading.exchange.publicInterfaces.Exchangeable order11 =
			new ExchangeableMock(offered, 1_000L, notRequired, 2_000L);
	org.trading.exchange.publicInterfaces.Exchangeable order12 =
			new ExchangeableMock(offered, 1_000L, notRequired, 2_003L);
	CollectionOfExchangeableMock orders2 = new CollectionOfExchangeableMock(order11, order12);
	org.trading.exchange.publicInterfaces.Market notRequiredMarket =
			new MarketMock("notRequiredMarketId", Location.LONDON, "marketName", offered, notRequired, order11,
					order12);

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

		UniversalExchange test = new UniversalExchangeMock(name, strategy, platform, requiredMarket,
				notRequiredMarket);
		assertEquals(platform, test.getPlatform());
	}

	@Test
	public void openMarket() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
		doReturn(market).when(strategy).openMarket(market, victim);

		org.trading.exchange.publicInterfaces.Market result = victim.openMarket(market);
		assertEquals(market, result);
	}

	@Test
	public void closeMarket() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(market).when(strategy).closeMarket(market, victim);

		org.trading.exchange.publicInterfaces.Market result = victim.closeMarket(market);
		assertEquals(market, result);
	}

	@Test
	public void validateOrder() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(strategy).validateOrder(exchangeable, victim);

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.validateOrder(exchangeable);
		assertEquals(exchangeable, result);
	}

	@Test
	public void acceptOrder() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(strategy).validateOrder(exchangeable, victim);
		doReturn(exchangeable).when(strategy).acceptOrder(exchangeable, victim);
		doReturn(exchangeable).when(exchangeable).preProcess();

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.acceptOrder(exchangeable);
		assertEquals(exchangeable, result);
	}

	@Test
	public void processOrder() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(strategy).processOrder(exchangeable, victim);
		doReturn(exchangeable).when(exchangeable).process();

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.processOrder(exchangeable);
		assertEquals(exchangeable, result);
	}

	@Test
	public void postProcessOrder() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchanged).when(strategy).postProcessOrder(exchangeable, victim);
		doReturn(exchangeable).when(exchangeable).postProcess();

		Exchanged result = victim.postProcessOrder(exchangeable);
		assertEquals(exchanged, result);
	}

	@Test
	public void finaliseOrder() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchanged).when(strategy).finaliseOrder(exchangeable, victim);
		doReturn(exchangeable).when(exchangeable).finalise();

		Exchanged result = victim.finaliseOrder(exchangeable);
		verify(strategy).finaliseOrder(exchangeable, victim);
		assertEquals(exchanged, result);
	}

	@Test
	public void getMatchingOrders() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(Exchangeable.class);
		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> expected = mock(Collection.class);
		doReturn(expected).when(strategy).getMatchingOrders(exchangeable, victim);

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
				victim.getMatchingOrders(exchangeable);
		verify(strategy).getMatchingOrders(exchangeable, victim);
		assertEquals(expected, result);
	}

	@Test
	public void matchOrder() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(offered).when(exchangeable).getRequired();
		doReturn(required).when(exchangeable).getOffered();

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> exchangeables = mock(Collection
				.class);
		org.trading.exchange.publicInterfaces.Exchanged expected =
				mock(org.trading.exchange.publicInterfaces.Exchanged.class);
		doReturn(expected).when(strategy).matchOrder(exchangeable, exchangeables, victim);

		Exchanged result = victim.matchOrder(exchangeable, exchangeables);
		verify(strategy).matchOrder(exchangeable, exchangeables, victim);
		assertEquals(expected, result);
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
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.validateOrder(exchangeable, victim);
		assertEquals(exchangeable, result);
	}

	@Test
	public void acceptOrderImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).preProcess();

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.acceptOrder(exchangeable, victim);
		assertEquals(exchangeable, result);
	}

	@Test
	public void processOrderImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).process();

		org.trading.exchange.publicInterfaces.Exchangeable result =
				victim.processOrder(exchangeable, victim.getMatchingOrders(exchangeable), victim);
		assertEquals(exchangeable, result);
	}

	@Test
	public void postProcessOrderImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).postProcess();
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchangeable).when(exchanged).getExchangeable();
		UniversalExchange spyed = spy(victim);
		Collection<org.trading.exchange.publicInterfaces.Exchangeable> matchedOrders = mock(Collection.class);
		doReturn(matchedOrders).when(spyed).getMatchingOrders(exchangeable, spyed);

		Exchanged result = spyed.postProcessOrder(exchangeable, spyed);
		assertEquals(exchanged.getExchangeable(), result.getExchangeable());
		assertEquals(matchedOrders, result.getMatchedExchangeables());
	}

	@Test
	public void finaliseOrderImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).finalise();
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchangeable).when(exchanged).getExchangeable();
		UniversalExchange spyed = spy(victim);
		Collection<org.trading.exchange.publicInterfaces.Exchangeable> matchedOrders = mock(Collection.class);
		doReturn(matchedOrders).when(spyed).getMatchingOrders(exchangeable, spyed);

		Exchanged result = spyed.finaliseOrder(exchangeable, spyed);
		assertEquals(exchanged.getExchangeable(), result.getExchangeable());
		assertEquals(matchedOrders, result.getMatchedExchangeables());
	}

	@Test
	public void getMatchingOrdersImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(offered).when(exchangeable).getRequired();
		doReturn(required).when(exchangeable).getOffered();

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
				victim.getMatchingOrders(exchangeable, victim);
		assertEquals(orders.getCollection(), result);
	}

	@Test
	public void matchOrderImplementation() throws Exception {
		Exchangeable exchangeable = mock(Exchangeable.class);
		doReturn(offered).when(exchangeable).getRequired();
		doReturn(required).when(exchangeable).getOffered();

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> exchangeables = orders
				.getCollection();
		for (org.trading.exchange.publicInterfaces.Exchangeable ex : exchangeables) {
			doReturn(ex).when(exchangeable).match(ex);
		}
		org.trading.exchange.publicInterfaces.Exchanged expected =
				mock(org.trading.exchange.publicInterfaces.Exchanged.class);
		doReturn(exchangeable).when(expected).getExchangeable();
		doReturn(exchangeables).when(expected).getMatchedExchangeables();

		Exchanged result = victim.matchOrder(exchangeable, exchangeables, victim);
		assertEquals(expected.getExchangeable(), result.getExchangeable());
		assertEquals(expected.getMatchedExchangeables(), result.getMatchedExchangeables());
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
			doReturn(market).when(platform).openMarket(market, platform);

			org.trading.exchange.publicInterfaces.Market result = victim.openMarket(market, platform);
			assertEquals(market, result);
		}

		@Test
		public void closeMarket() throws Exception {
			org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
			doReturn(market).when(platform).closeMarket(market, platform);

			org.trading.exchange.publicInterfaces.Market result = victim.closeMarket(market, platform);
			assertEquals(market, result);
		}

		@Test
		public void validateOrder() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(platform).validateOrder(exchangeable, platform);

			org.trading.exchange.publicInterfaces.Exchangeable result = victim.validateOrder(exchangeable, platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void acceptOrder() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(platform).acceptOrder(exchangeable, platform);

			org.trading.exchange.publicInterfaces.Exchangeable result = victim.acceptOrder(exchangeable, platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void processOrder() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			Exchanged exchanged = mock(Exchanged.class);
			when(platform.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform), platform))
					.thenReturn(exchangeable);

			org.trading.exchange.publicInterfaces.Exchangeable result = victim.processOrder(exchangeable, platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void processOrder1() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			Exchanged exchanged = mock(Exchanged.class);
			when(platform.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform), platform))
					.thenReturn(exchangeable);

			org.trading.exchange.publicInterfaces.Exchangeable result =
					victim.processOrder(exchangeable, platform.getMatchingOrders(exchangeable, platform),
							platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void postProcessOrder() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(exchangeable).postProcess();
			Exchanged exchanged = mock(Exchanged.class);
			doReturn(exchanged).when(platform).postProcessOrder(exchangeable, platform);

			Exchanged result = victim.postProcessOrder(exchangeable, platform);
			assertEquals(exchanged, result);
		}

		@Test
		public void finaliseOrder() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(exchangeable).finalise();
			Exchanged exchanged = mock(Exchanged.class);
			doReturn(exchanged).when(platform).finaliseOrder(exchangeable, platform);

			Exchanged result = victim.finaliseOrder(exchangeable, platform);
			assertEquals(exchanged, result);
		}

		@Test
		public void getMatchingOrders() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> orders = mock(Collection.class);
			doReturn(orders).when(platform).getMatchingOrders(exchangeable, platform);

			Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
					victim.getMatchingOrders(exchangeable, platform);
			assertEquals(orders, result);
		}
	}
}