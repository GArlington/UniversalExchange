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
			new MarketMock("requiredMarketId", Location.LONDON, "marketName", offered, required
					, order2, order1
			);

	org.trading.exchange.publicInterfaces.Exchangeable order11 =
			new ExchangeableMock(offered, 1_000L, notRequired, 2_000L);
	org.trading.exchange.publicInterfaces.Exchangeable order12 =
			new ExchangeableMock(offered, 1_000L, notRequired, 2_003L);
	CollectionOfExchangeableMock orders2 = new CollectionOfExchangeableMock(order11, order12);
	org.trading.exchange.publicInterfaces.Market notRequiredMarket =
			new MarketMock("notRequiredMarketId", Location.LONDON, "marketName", offered, notRequired, order12,
					order11);

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
	public void validate() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
		doReturn(market).when(strategy).validate(market, victim);
		org.trading.exchange.publicInterfaces.Market result = victim.validate(market);
		assertEquals(market, result);
	}

	@Test
	public void openMarket() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
		doReturn(market).when(strategy).open(market, victim);

		org.trading.exchange.publicInterfaces.Market result = victim.open(market);
		assertEquals(market, result);
	}

	@Test
	public void closeMarket() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(true).when(strategy).close(market, victim);

		boolean result = victim.close(market);
		assertEquals(true, result);
	}

	@Test
	public void validateExchangeable() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(strategy).validate(exchangeable, victim);

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.validate(exchangeable);
		assertEquals(exchangeable, result);
	}

	@Test
	public void acceptExchangeable() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(strategy).validate(exchangeable, victim);
		doReturn(exchangeable).when(strategy).accept(exchangeable, victim);
		doReturn(exchangeable).when(exchangeable).preProcess();

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.accept(exchangeable);
		assertEquals(exchangeable, result);
	}

	@Test
	public void processExchangeable() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(strategy).process(exchangeable, victim);
		doReturn(exchangeable).when(exchangeable).process();

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.process(exchangeable);
		assertEquals(exchangeable, result);
	}

	@Test
	public void postProcessExchangeable() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchanged).when(strategy).postProcess(exchangeable, victim);
		doReturn(exchangeable).when(exchangeable).postProcess();

		Exchanged result = victim.postProcess(exchangeable);
		assertEquals(exchanged, result);
	}

	@Test
	public void finaliseExchangeable() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchanged).when(strategy).finalise(exchangeable, victim);
		doReturn(exchangeable).when(exchangeable).finalise();

		Exchanged result = victim.finalise(exchangeable);
		verify(strategy).finalise(exchangeable, victim);
		assertEquals(exchanged, result);
	}

	@Test
	public void getMatchingExchangeables() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(Exchangeable.class);
		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> expected = mock(Collection.class);
		doReturn(expected).when(strategy).getMatching(exchangeable, victim);

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
				victim.getMatching(exchangeable);
		verify(strategy).getMatching(exchangeable, victim);
		assertEquals(expected, result);
	}

	@Test
	public void matchExchangeable() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(offered).when(exchangeable).getRequired();
		doReturn(required).when(exchangeable).getOffered();

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> exchangeables = mock(Collection
				.class);
		org.trading.exchange.publicInterfaces.Exchanged expected =
				mock(org.trading.exchange.publicInterfaces.Exchanged.class);
		doReturn(expected).when(strategy).match(exchangeable, exchangeables, victim);

		Exchanged result = victim.match(exchangeable, exchangeables);
		verify(strategy).match(exchangeable, exchangeables, victim);
		assertEquals(expected, result);
	}


	@Test
	public void openMarketImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);

		org.trading.exchange.publicInterfaces.Market result = victim.open(market, victim);
		assertEquals(market, result);
	}

	@Test(expected = IllegalStateException.class)
	public void openMarketImplementationFail() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = requiredMarket;

		org.trading.exchange.publicInterfaces.Market result = victim.open(market, victim);
		assertEquals(market, result);
	}

	@Test
	public void closeMarketImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = requiredMarket;

		boolean result = victim.close(market, victim);
		assertEquals(true, result);
	}

	@Test(expected = IllegalStateException.class)
	public void closeMarketImplementationFail() throws Exception {
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);

		boolean result = victim.close(market, victim);
		assertEquals(false, result);
	}

	@Test
	public void validateExchangeableImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.validate(exchangeable, victim);
		assertEquals(exchangeable, result);
	}

	@Test
	public void acceptExchangeableImplementation() throws Exception {
		Exchangeable exchangeable = mock(Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).preProcess();
		doReturn(exchangeable).when(exchangeable).open();
		doReturn(offered).when(exchangeable).getOffered();
		doReturn(required).when(exchangeable).getRequired();

		org.trading.exchange.publicInterfaces.Exchangeable result = victim.accept(exchangeable, victim);
		assertEquals(exchangeable, result);
	}

	@Test
	public void processExchangeableImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).process();

		org.trading.exchange.publicInterfaces.Exchangeable result =
				victim.process(exchangeable, victim.getMatching(exchangeable), victim);
		assertEquals(exchangeable, result);
	}

	@Test
	public void postProcessExchangeableImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).postProcess();
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchangeable).when(exchanged).getExchangeable();
		UniversalExchange spyed = spy(victim);
		Collection<org.trading.exchange.publicInterfaces.Exchangeable> matchedExchangeables = mock(Collection.class);
		doReturn(matchedExchangeables).when(spyed).getMatching(exchangeable, spyed);

		Exchanged result = spyed.postProcess(exchangeable, spyed);
		assertEquals(exchanged.getExchangeable(), result.getExchangeable());
		assertEquals(matchedExchangeables, result.getMatchedExchangeables());
	}

	@Test
	public void finaliseExchangeableImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).finalise();
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchangeable).when(exchanged).getExchangeable();
		UniversalExchange spyed = spy(victim);
		Collection<org.trading.exchange.publicInterfaces.Exchangeable> matchedExchangeables = mock(Collection.class);
		doReturn(matchedExchangeables).when(spyed).getMatching(exchangeable, spyed);

		Exchanged result = spyed.finalise(exchangeable, spyed);
		assertEquals(exchanged.getExchangeable(), result.getExchangeable());
		assertEquals(matchedExchangeables, result.getMatchedExchangeables());
	}

	@Test
	public void getMatchingExchangeablesImplementation() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
		doReturn(offered).when(exchangeable).getRequired();
		doReturn(required).when(exchangeable).getOffered();

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
				victim.getMatching(exchangeable, victim);
		assertEquals(orders.getCollection(), result);
	}

	@Test
	public void matchExchangeableImplementation() throws Exception {
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

		Exchanged result = victim.match(exchangeable, exchangeables, victim);
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
			doReturn(market).when(platform).open(market, platform);

			org.trading.exchange.publicInterfaces.Market result = victim.open(market, platform);
			assertEquals(market, result);
		}

		@Test
		public void closeMarket() throws Exception {
			org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
			doReturn(true).when(platform).close(market, platform);

			boolean result = victim.close(market, platform);
			assertEquals(true, result);
		}

		@Test
		public void validateExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(platform).validate(exchangeable, platform);

			org.trading.exchange.publicInterfaces.Exchangeable result = victim.validate(exchangeable, platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void acceptExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(platform).accept(exchangeable, platform);

			org.trading.exchange.publicInterfaces.Exchangeable result = victim.accept(exchangeable, platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void processExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			Exchanged exchanged = mock(Exchanged.class);
			when(platform.process(exchangeable, platform.getMatching(exchangeable, platform), platform))
					.thenReturn(exchangeable);

			org.trading.exchange.publicInterfaces.Exchangeable result = victim.process(exchangeable, platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void processExchangeable1() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			Exchanged exchanged = mock(Exchanged.class);
			when(platform.process(exchangeable, platform.getMatching(exchangeable, platform), platform))
					.thenReturn(exchangeable);

			org.trading.exchange.publicInterfaces.Exchangeable result =
					victim.process(exchangeable, platform.getMatching(exchangeable, platform),
							platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void postProcessExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(exchangeable).postProcess();
			Exchanged exchanged = mock(Exchanged.class);
			doReturn(exchanged).when(platform).postProcess(exchangeable, platform);

			Exchanged result = victim.postProcess(exchangeable, platform);
			assertEquals(exchanged, result);
		}

		@Test
		public void finaliseExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(exchangeable).finalise();
			Exchanged exchanged = mock(Exchanged.class);
			doReturn(exchanged).when(platform).finalise(exchangeable, platform);

			Exchanged result = victim.finalise(exchangeable, platform);
			assertEquals(exchanged, result);
		}

		@Test
		public void getMatchingExchangeables() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> orders = mock(Collection.class);
			doReturn(orders).when(platform).getMatching(exchangeable, platform);

			Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
					victim.getMatching(exchangeable, platform);
			assertEquals(orders, result);
		}
	}
}