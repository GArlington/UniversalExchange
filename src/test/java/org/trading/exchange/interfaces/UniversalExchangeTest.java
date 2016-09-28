package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.math.SimpleDecimal;
import org.trading.exchange.interfaces.mocks.CollectionOfExchangeableMock;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.Exchangeable.State;
import org.trading.exchange.publicInterfaces.Exchanged;
import org.trading.exchange.publicInterfaces.Location;
import org.trading.exchange.publicInterfaces.Owner;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by GArlington.
 */
@SuppressWarnings("unchecked")
public class UniversalExchangeTest {
	String name = "UniversalExchangeName";
	UniversalExchange.Strategy strategy = mock(UniversalExchange.Strategy.class);
	org.trading.exchange.publicInterfaces.Commodity offered = mock(Commodity.class);
	org.trading.exchange.publicInterfaces.Commodity required = mock(Commodity.class);
	org.trading.exchange.publicInterfaces.Commodity notRequired = mock(Commodity.class);

	Exchangeable order1;
	Exchangeable order2;
	CollectionOfExchangeableMock orders;
	Location location;
	org.trading.exchange.publicInterfaces.Market requiredMarket;

	Exchangeable order11;
	Exchangeable order12;
	CollectionOfExchangeableMock orders2;
	org.trading.exchange.publicInterfaces.Market notRequiredMarket;

	UniversalExchange victim;

	static Exchangeable setUp(Exchangeable exchangeable, Commodity offered, Commodity required) {
		doReturn(exchangeable).when(exchangeable).open();
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).finalise();

		doReturn(State.OPEN).when(exchangeable).getExchangeableState();
		doReturn(offered).when(exchangeable).getOffered();
		doReturn(required).when(exchangeable).getRequired();
		doReturn(SimpleDecimal.ONE).when(exchangeable).getExchangeRate();
		return exchangeable;
	}

	static void setUpToMatch(Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> orders,
							 org.trading.exchange.publicInterfaces.Exchangeable exchangeable) {
		for (org.trading.exchange.publicInterfaces.Exchangeable ex : orders) {
			doReturn(true).when(ex).isMatching(exchangeable);
		}
	}

	static void setUpToMatch(org.trading.exchange.publicInterfaces.Exchangeable[] orders,
							 org.trading.exchange.publicInterfaces.Exchangeable exchangeable) {
		for (org.trading.exchange.publicInterfaces.Exchangeable ex : orders) {
			doReturn(true).when(ex).isMatching(exchangeable);
			doReturn(ex).when(exchangeable).match(ex);
		}
	}

	@Before
	public void setUp() throws Exception {
		order1 = mock(Exchangeable.class);
		order2 = mock(Exchangeable.class);
		order1 = setUp(order1, offered, required);
		order2 = setUp(order2, offered, required);
		Owner owner = mock(Owner.class);

		orders = new CollectionOfExchangeableMock(order1, order2);
		location = mock(Location.class);
		requiredMarket = new MarketMock.Builder<MarketMock>().setId("requiredMarketId").setLocation(location)
				.setName("requiredMarketName").setOffered(offered).setRequired(required).setOwner(owner)
				.setAutoMatching(true).accept(order2).accept(order1).build();
		assertEquals(2, requiredMarket.getOrders().size());

		order11 = mock(Exchangeable.class);
		order12 = mock(Exchangeable.class);
		order11 = setUp(order11, offered, notRequired);
		order12 = setUp(order12, offered, notRequired);
		orders2 = new CollectionOfExchangeableMock(order11, order12);
		notRequiredMarket = new MarketMock.Builder<MarketMock>().setId("notRequiredMarketId").setLocation(location)
				.setName("notRequiredMarketName").setOffered(offered).setRequired(notRequired).setOwner(owner)
				.setAutoMatching(true).accept(order12).accept(order11).build();
		assertEquals(2, notRequiredMarket.getOrders().size());

		victim = new UniversalExchangeMock(name, strategy, owner, true, requiredMarket, notRequiredMarket);
		assertEquals(2, victim.getMarkets().size());
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
		Owner owner = mock(Owner.class);

		UniversalExchange test =
				new UniversalExchangeMock(name, strategy, platform, owner, true, requiredMarket, notRequiredMarket);
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
	public void getMatchingExchangeables() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable = mock(Exchangeable.class);
		@SuppressWarnings("unchecked")
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

		org.trading.exchange.publicInterfaces.Exchangeable[] exchangeables = new Exchangeable[1];
		org.trading.exchange.publicInterfaces.Exchanged expected =
				mock(org.trading.exchange.publicInterfaces.Exchanged.class);
		doReturn(expected).when(strategy).match(exchangeable, victim, exchangeables);

		Exchanged result = victim.match(exchangeable, exchangeables);
		verify(strategy).match(exchangeable, victim, exchangeables);
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
				victim.process(exchangeable, victim, victim.getMatching(exchangeable).toArray(
						new org.trading.exchange.publicInterfaces.Exchangeable[1]));
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
		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> matchedExchangeables =
				orders.getCollection();
		doReturn(matchedExchangeables).when(spyed).getMatching(exchangeable, spyed);

		Exchangeable[] var = matchedExchangeables.toArray(new Exchangeable[matchedExchangeables.size()]);
		Exchanged result = spyed.postProcess(exchangeable, spyed, var);
		assertEquals(exchanged.getExchangeable(), result.getExchangeable());
		assertEquals(matchedExchangeables, result.getMatchedExchangeables());
	}

	@Test
	public void finaliseExchangeableImplementation() throws Exception {
		Exchangeable exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, required, offered);
		setUpToMatch(victim.getMarkets(exchangeable).stream().findFirst().get().getOrders(State.OPEN), exchangeable);
		assertEquals(1, victim.getMarkets(exchangeable).size());

		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchangeable).when(exchanged).getExchangeable();
		UniversalExchange spyed = spy(victim);
		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> matchedExchangeables =
				victim.getMatching(exchangeable, victim);

		Exchangeable[] mea = matchedExchangeables.toArray(new Exchangeable[1]);
		assertEquals(2, mea.length);

		Exchanged result = spyed.finalise(exchangeable, spyed, mea);
		assertEquals(exchanged.getExchangeable(), result.getExchangeable());
		assertEquals(matchedExchangeables, result.getMatchedExchangeables());
	}

	@Test
	public void getMatchingExchangeablesImplementation() throws Exception {
		Exchangeable exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, required, offered);
		Collection<? extends org.trading.exchange.publicInterfaces.Market> markets = victim.getMarkets(exchangeable);
		assertEquals(1, markets.size());
		setUpToMatch(markets.stream().findFirst().get().getOrders(State.OPEN), exchangeable);
		assertEquals(1, victim.getMarkets(exchangeable).size());

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
				victim.getMatching(exchangeable, victim);
		assertEquals(2, result.size());
		assert (orders.getCollection().containsAll(result) && result.containsAll(orders.getCollection()));
	}

	@Test
	public void matchExchangeableImplementation() throws Exception {
		Exchangeable exchangeable = mock(Exchangeable.class);
		doReturn(required).when(exchangeable).getOffered();
		doReturn(offered).when(exchangeable).getRequired();

		Collection<? extends Exchangeable> exchangeables = orders
				.getCollection();
		for (Exchangeable ex : exchangeables) {
			ex = setUp(ex, offered, required);
			doReturn(ex).when(exchangeable).match(ex);
		}
		org.trading.exchange.publicInterfaces.Exchanged expected =
				mock(org.trading.exchange.publicInterfaces.Exchanged.class);
		doReturn(exchangeable).when(expected).getExchangeable();
		doReturn(exchangeables).when(expected).getMatchedExchangeables();
		setUpToMatch(exchangeables, exchangeable);
		Exchangeable[] exchangeables1 = exchangeables.toArray(new Exchangeable[exchangeables.size()]);
		setUpToMatch(exchangeables1, exchangeable);
		assertEquals(2, exchangeables1.length);

		Exchanged result = victim.match(exchangeable, victim, exchangeables1);
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
		public void validateMarket() throws Exception {
			org.trading.exchange.publicInterfaces.Market market = mock(Market.class);
			doReturn(market).when(platform).validate(market, platform);

			org.trading.exchange.publicInterfaces.Market result = victim.validate(market, platform);
			assertEquals(market, result);
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
		public void matchExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			org.trading.exchange.publicInterfaces.Exchangeable[] matchedExchangeables =
					platform.getMatching(exchangeable, platform)
							.toArray(new org.trading.exchange.publicInterfaces.Exchangeable[1]);
			Exchanged exchanged = mock(Exchanged.class);
			doReturn(exchanged).when(platform).match(exchangeable, platform, matchedExchangeables);

			Exchanged result = victim.match(exchangeable, platform, matchedExchangeables);
			assertEquals(exchanged, result);
		}

		@Test
		public void processExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			org.trading.exchange.publicInterfaces.Exchangeable[] matchedExchangeables =
					platform.getMatching(exchangeable, platform)
							.toArray(new org.trading.exchange.publicInterfaces.Exchangeable[1]);
			doReturn(exchangeable).when(platform).process(exchangeable, platform, matchedExchangeables);

			org.trading.exchange.publicInterfaces.Exchangeable result = victim.process(exchangeable, platform);
			assertEquals(exchangeable, result);
		}

		@Test
		public void processExchangeable1() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			Exchanged exchanged = mock(Exchanged.class);
			org.trading.exchange.publicInterfaces.Exchangeable[] matchedExchangeables =
					platform.getMatching(exchangeable, platform)
							.toArray(new org.trading.exchange.publicInterfaces.Exchangeable[1]);
			doReturn(exchangeable).when(platform).process(exchangeable, platform, matchedExchangeables);

			org.trading.exchange.publicInterfaces.Exchangeable result =
					victim.process(exchangeable, platform, matchedExchangeables);
			assertEquals(exchangeable, result);
		}

		@Test
		public void postProcessExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(exchangeable).postProcess();
			Exchanged exchanged = mock(Exchanged.class);
			org.trading.exchange.publicInterfaces.Exchangeable[] matchedExchangeables =
					platform.getMatching(exchangeable, platform)
							.toArray(new org.trading.exchange.publicInterfaces.Exchangeable[1]);
			doReturn(exchanged).when(platform).postProcess(exchangeable, platform, matchedExchangeables);

			Exchanged result = victim.postProcess(exchangeable, platform, matchedExchangeables);
			assertEquals(exchanged, result);
		}

		@Test
		public void finaliseExchangeable() throws Exception {
			org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
					mock(org.trading.exchange.publicInterfaces.Exchangeable.class);
			doReturn(exchangeable).when(exchangeable).finalise();
			Exchanged exchanged = mock(Exchanged.class);
			org.trading.exchange.publicInterfaces.Exchangeable[] matchedExchangeables =
					platform.getMatching(exchangeable, platform)
							.toArray(new org.trading.exchange.publicInterfaces.Exchangeable[1]);
			doReturn(exchanged).when(platform).finalise(exchangeable, platform, matchedExchangeables);

			Exchanged result = victim.finalise(exchangeable, platform, matchedExchangeables);
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