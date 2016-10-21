package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.math.SimpleDecimal;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.ExchangeOffer.State;
import org.trading.exchange.publicInterfaces.Location;
import org.trading.exchange.publicInterfaces.Owner;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by GArlington.
 */
@SuppressWarnings("unchecked")
public class UniversalExchangeTest {
	private String name = "UniversalExchangeName";
	private UniversalExchange.Strategy strategy = mock(UniversalExchange.Strategy.class);
	private Commodity offered = mock(Commodity.class);
	private Commodity required = mock(Commodity.class);
	private Commodity notRequired = mock(Commodity.class);

	private ExchangeOffer order1;
	private ExchangeOffer order2;
	private Collection<? extends ExchangeOffer> orders, orders2;
	private Location location;
	private org.trading.exchange.publicInterfaces.Market requiredMarket;

	private ExchangeOffer order11;
	private ExchangeOffer order12;
	private org.trading.exchange.publicInterfaces.Market notRequiredMarket;

	private UniversalExchange victim;

	static org.trading.exchange.publicInterfaces.Market setUp(org.trading.exchange.publicInterfaces.Market market,
															  org.trading.exchange.publicInterfaces.ExchangeOffer
																	  exchangeOffer) {
//		doReturn(UUID.randomUUID().toString()).when(market).getId();
		doReturn(true).when(market).validate(exchangeOffer);
		doReturn(true).when(market).accept(exchangeOffer);

//		doReturn(true).when(market).isMarket(exchangeOffer);

		doReturn(exchangeOffer.getOffered()).when(market).getOffered();
		doReturn(exchangeOffer.getRequired()).when(market).getRequired();
		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> offers = new LinkedList<>();
		doReturn(offers).when(market).getOffers();
		return market;
	}

	static ExchangeOffer setUp(ExchangeOffer exchangeOffer, Commodity offered, Commodity required) {
		return setUp(exchangeOffer, offered, 400L, required, 1L);
	}

	static ExchangeOffer setUp(ExchangeOffer exchangeOffer, Commodity offered, long offeredValue, Commodity required,
							   long requiredValue) {
		doReturn(exchangeOffer).when(exchangeOffer).validate();
		doReturn(exchangeOffer).when(exchangeOffer).preProcess();
		doReturn(exchangeOffer).when(exchangeOffer).open();
		doReturn(exchangeOffer).when(exchangeOffer).process();
		doReturn(exchangeOffer).when(exchangeOffer).finalise();

		doReturn(ExchangeOffer.State.OPEN).when(exchangeOffer).getState();
		doReturn(offered).when(exchangeOffer).getOffered();
		doReturn(offeredValue).when(exchangeOffer).getOfferedValue();
		doReturn(required).when(exchangeOffer).getRequired();
		doReturn(requiredValue).when(exchangeOffer).getRequiredValue();
		doReturn(new SimpleDecimal(offeredValue).divide(new SimpleDecimal(requiredValue))).when(exchangeOffer)
				.getExchangeRate();
		doReturn(new SimpleDecimal(requiredValue).divide(new SimpleDecimal(offeredValue))).when(exchangeOffer)
				.getInverseExchangeRate();
		return exchangeOffer;
	}

	static void setUpToMatch(Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> offers,
							 org.trading.exchange.publicInterfaces.ExchangeOffer exchangeOffer) {
		setUpToMatch(offers.toArray(new org.trading.exchange.publicInterfaces.ExchangeOffer[offers.size()]),
				exchangeOffer);
	}

	static void setUpToMatch(org.trading.exchange.publicInterfaces.ExchangeOffer[] offers,
							 org.trading.exchange.publicInterfaces.ExchangeOffer exchangeOffer) {
		for (org.trading.exchange.publicInterfaces.ExchangeOffer offer : offers) {
			doReturn(true).when(offer).isMatching(exchangeOffer);
			doReturn(offer).when(exchangeOffer).match(offer);
		}
	}

	@Before
	public void setUp() throws Exception {
		order1 = mock(ExchangeOffer.class);
		order2 = mock(ExchangeOffer.class);
		order1 = setUp(order1, offered, required);
		order2 = setUp(order2, offered, required);
		Owner owner = mock(Owner.class);

		orders = new LinkedList<>();
		((Collection<ExchangeOffer>) orders).add(order2);
		((Collection<ExchangeOffer>) orders).add(order1);

		location = mock(Location.class);
		requiredMarket = new MarketMock.Builder<MarketMock>().setId("requiredMarketId").setLocation(location)
				.setName("requiredMarketName").setOffered(offered).setRequired(required).setOwner(owner)
				.setAutoMatching(true).accept(order2).accept(order1).build();
		assertEquals(2, requiredMarket.getOffers().size());

		order11 = mock(ExchangeOffer.class);
		order12 = mock(ExchangeOffer.class);
		order11 = setUp(order11, offered, notRequired);
		order12 = setUp(order12, offered, notRequired);
		orders2 = new LinkedList<>();
		((Collection<ExchangeOffer>) orders2).add(order12);
		((Collection<ExchangeOffer>) orders2).add(order11);

		notRequiredMarket = new MarketMock.Builder<MarketMock>().setId("notRequiredMarketId").setLocation(location)
				.setName("notRequiredMarketName").setOffered(offered).setRequired(notRequired).setOwner(owner)
				.setAutoMatching(true).accept(order12).accept(order11).build();
		assertEquals(2, notRequiredMarket.getOffers().size());

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
	public void validateMarket() throws Exception {
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
	public void validateExchangeOffer() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(exchangeOffer).when(strategy).validate(exchangeOffer, market, victim);

		org.trading.exchange.publicInterfaces.ExchangeOffer result = victim.validate(exchangeOffer, market);
		assertEquals(exchangeOffer, result);
	}

	@Test
	public void acceptExchangeOffer() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		market = setUp(market, exchangeOffer);
		doReturn(exchangeOffer).when(strategy).validate(exchangeOffer, market, victim);
		doReturn(exchangeOffer).when(strategy).accept(exchangeOffer, market, victim);
		doReturn(exchangeOffer).when(exchangeOffer).preProcess();

		org.trading.exchange.publicInterfaces.ExchangeOffer result = victim.accept(exchangeOffer, market);
		assertEquals(exchangeOffer, result);
	}

	@Test
	public void getMatchingExchangeOffers() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		@SuppressWarnings("unchecked")
		Collection<? extends ExchangeOffer> expected = mock(Collection.class);
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(expected).when(strategy).getMatching(exchangeOffer, market, victim);

		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> result =
				victim.getMatching(exchangeOffer, market);
		verify(strategy).getMatching(exchangeOffer, market, victim);
		assertEquals(expected, result);
	}

	@Test
	public void matchExchangeOffer() throws Exception {
		org.trading.exchange.publicInterfaces.ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		doReturn(offered).when(exchangeOffer).getRequired();
		doReturn(required).when(exchangeOffer).getOffered();

		org.trading.exchange.publicInterfaces.ExchangeOffer[] exchangeOffers = new ExchangeOffer[1];
		org.trading.exchange.publicInterfaces.Exchanged expected =
				mock(org.trading.exchange.publicInterfaces.Exchanged.class);
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(expected).when(strategy).match(exchangeOffer, market, victim, exchangeOffers);

		org.trading.exchange.publicInterfaces.Exchanged result = victim.match(exchangeOffer, market, exchangeOffers);
		verify(strategy).match(exchangeOffer, market, victim, exchangeOffers);
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
	public void validateExchangeOfferImplementation() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		doReturn(exchangeOffer).when(exchangeOffer).validate();
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(true).when(market).validate(exchangeOffer);

		org.trading.exchange.publicInterfaces.ExchangeOffer result = victim.validate(exchangeOffer, market, victim);
		assertEquals(exchangeOffer, result);
	}

	@Test
	public void acceptExchangeOfferImplementation() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		setUp(exchangeOffer, offered, required);
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(true).when(market).validate(exchangeOffer);
		doReturn(true).when(market).accept(exchangeOffer);

		org.trading.exchange.publicInterfaces.ExchangeOffer result = victim.accept(exchangeOffer, market, victim);
		assertEquals(exchangeOffer, result);
	}

	@Test
	public void processExchangeOfferImplementation() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		doReturn(exchangeOffer).when(exchangeOffer).validate();
		doReturn(exchangeOffer).when(exchangeOffer).process();
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(true).when(market).validate(exchangeOffer);

		org.trading.exchange.publicInterfaces.ExchangeOffer result = victim.process(exchangeOffer, market, victim,
				victim.getMatching(exchangeOffer, market).toArray(new ExchangeOffer[1]));
		assertEquals(exchangeOffer, result);
	}

	@Test
	public void postProcessExchangeOfferImplementation() throws Exception {
		ExchangeOffer exchangeOffer =
				mock(ExchangeOffer.class);
		doReturn(exchangeOffer).when(exchangeOffer).validate();
		doReturn(exchangeOffer).when(exchangeOffer).postProcess();
		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchanged).when(exchangeOffer).getExchanged();
		doReturn(exchangeOffer).when(exchanged).getExchangeOffer();
		UniversalExchange spied = spy(victim);
		Collection<? extends ExchangeOffer> matchedExchangeOffers = orders;
		doReturn(matchedExchangeOffers).when(exchanged).getMatchedExchangeOffers();
		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(matchedExchangeOffers).when(spied).getMatching(exchangeOffer, market, spied);

		ExchangeOffer[] var = matchedExchangeOffers.toArray(new ExchangeOffer[matchedExchangeOffers.size()]);

		org.trading.exchange.publicInterfaces.Exchanged result = spied.postProcess(exchangeOffer, spied, var);
		assertEquals(exchanged.getExchangeOffer(), result.getExchangeOffer());
		assertEquals(matchedExchangeOffers, result.getMatchedExchangeOffers());
	}

	@Test
	public void finaliseExchangeOfferImplementation() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = setUp(exchangeOffer, required, offered);
		setUpToMatch(victim.getMarkets(exchangeOffer).stream().findFirst().get().getOffers(State.OPEN), exchangeOffer);
		assertEquals(1, victim.getMarkets(exchangeOffer).size());

		Exchanged exchanged = mock(Exchanged.class);
		doReturn(exchanged).when(exchangeOffer).getExchanged();
		doReturn(exchangeOffer).when(exchanged).getExchangeOffer();
		UniversalExchange spied = spy(victim);

		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> matchedExchangeOffers =
				victim.getMatching(exchangeOffer, requiredMarket, victim);
		doReturn(matchedExchangeOffers).when(exchanged).getMatchedExchangeOffers();

		ExchangeOffer[] mea = matchedExchangeOffers.toArray(new ExchangeOffer[1]);
		assertEquals(2, mea.length);

		org.trading.exchange.publicInterfaces.Exchanged result = spied.finalise(exchangeOffer, spied, mea);
		assertEquals(exchanged.getExchangeOffer(), result.getExchangeOffer());
		assertEquals(matchedExchangeOffers, result.getMatchedExchangeOffers());
	}

	@Test
	public void getMatchingExchangeOffersImplementation() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = setUp(exchangeOffer, required, offered);
		Collection<? extends org.trading.exchange.publicInterfaces.Market> markets = victim.getMarkets(exchangeOffer);
		assertEquals(1, markets.size());
		setUpToMatch(markets.stream().findFirst().get().getOffers(State.OPEN), exchangeOffer);
		assertEquals(1, victim.getMarkets(exchangeOffer).size());

		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> result =
				victim.getMatching(exchangeOffer, requiredMarket, victim);
		assertEquals(2, result.size());
		assert (orders.containsAll(result) && result.containsAll(orders));
	}

	@Test
	public void matchExchangeOfferImplementation() throws Exception {
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		doReturn(required).when(exchangeOffer).getOffered();
		doReturn(offered).when(exchangeOffer).getRequired();

		org.trading.exchange.publicInterfaces.Market market = mock(org.trading.exchange.publicInterfaces.Market.class);
		doReturn(true).when(market).validate(exchangeOffer);

		Collection<? extends ExchangeOffer> exchangeOffers = orders;
		for (ExchangeOffer ex : exchangeOffers) {
			ex = setUp(ex, offered, required);
			doReturn(ex).when(exchangeOffer).match(ex);
			doReturn(true).when(market).validate(ex);
		}
		org.trading.exchange.publicInterfaces.Exchanged expected =
				mock(org.trading.exchange.publicInterfaces.Exchanged.class);
		doReturn(exchangeOffer).when(expected).getExchangeOffer();
		doReturn(exchangeOffers).when(expected).getMatchedExchangeOffers();
		setUpToMatch(exchangeOffers, exchangeOffer);
		ExchangeOffer[] exchangeOffers1 = exchangeOffers.toArray(new ExchangeOffer[exchangeOffers.size()]);
		setUpToMatch(exchangeOffers1, exchangeOffer);
		assertEquals(2, exchangeOffers1.length);

		org.trading.exchange.publicInterfaces.Exchanged result =
				victim.match(exchangeOffer, market, victim, exchangeOffers1);
		assertEquals(expected.getExchangeOffer(), result.getExchangeOffer());
		assertEquals(expected.getMatchedExchangeOffers(), result.getMatchedExchangeOffers());
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
		public void validateExchangeOffer() throws Exception {
			ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
			org.trading.exchange.publicInterfaces.Market market =
					mock(org.trading.exchange.publicInterfaces.Market.class);
			doReturn(exchangeOffer).when(platform).validate(exchangeOffer, market, platform);

			org.trading.exchange.publicInterfaces.ExchangeOffer result =
					victim.validate(exchangeOffer, market, platform);
			assertEquals(exchangeOffer, result);
		}

		@Test
		public void acceptExchangeOffer() throws Exception {
			ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
			org.trading.exchange.publicInterfaces.Market market =
					mock(org.trading.exchange.publicInterfaces.Market.class);
			doReturn(exchangeOffer).when(platform).accept(exchangeOffer, market, platform);

			org.trading.exchange.publicInterfaces.ExchangeOffer result = victim.accept(exchangeOffer, market,
					platform);
			assertEquals(exchangeOffer, result);
		}

		@Test
		public void matchExchangeOffer() throws Exception {
			ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
			org.trading.exchange.publicInterfaces.Market market =
					mock(org.trading.exchange.publicInterfaces.Market.class);
			ExchangeOffer[] matchedExchangeOffers = platform.getMatching(exchangeOffer, market, platform)
					.toArray(new ExchangeOffer[1]);
			Exchanged exchanged = mock(Exchanged.class);
			doReturn(exchanged).when(platform).match(exchangeOffer, market, platform, matchedExchangeOffers);

			org.trading.exchange.publicInterfaces.Exchanged result =
					victim.match(exchangeOffer, market, platform, matchedExchangeOffers);
			assertEquals(exchanged, result);
		}

		@Test
		public void processExchangeOffer() throws Exception {
			ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
			org.trading.exchange.publicInterfaces.Market market =
					mock(org.trading.exchange.publicInterfaces.Market.class);
			ExchangeOffer[] matchedExchangeOffers = platform.getMatching(exchangeOffer, market, platform)
					.toArray(new ExchangeOffer[1]);
			doReturn(exchangeOffer).when(platform).process(exchangeOffer, market, platform, matchedExchangeOffers);

			org.trading.exchange.publicInterfaces.ExchangeOffer result =
					victim.process(exchangeOffer, market, platform);
			assertEquals(exchangeOffer, result);
		}

		@Test
		public void processExchangeOffer1() throws Exception {
			ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
			Exchanged exchanged = mock(Exchanged.class);
			org.trading.exchange.publicInterfaces.Market market =
					mock(org.trading.exchange.publicInterfaces.Market.class);
			ExchangeOffer[] matchedExchangeOffers = platform.getMatching(exchangeOffer, market, platform)
					.toArray(new ExchangeOffer[1]);
			doReturn(exchangeOffer).when(platform).process(exchangeOffer, market, platform, matchedExchangeOffers);

			org.trading.exchange.publicInterfaces.ExchangeOffer result =
					victim.process(exchangeOffer, market, platform, matchedExchangeOffers);
			assertEquals(exchangeOffer, result);
		}

		@Test
		public void postProcessExchangeOffer() throws Exception {
			ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
			doReturn(exchangeOffer).when(exchangeOffer).postProcess();
			Exchanged exchanged = mock(Exchanged.class);
			org.trading.exchange.publicInterfaces.Market market =
					mock(org.trading.exchange.publicInterfaces.Market.class);
			ExchangeOffer[] matchedExchangeOffers = platform.getMatching(exchangeOffer, market, platform)
					.toArray(new ExchangeOffer[1]);
			doReturn(exchanged).when(platform).postProcess(exchangeOffer, platform, matchedExchangeOffers);

			org.trading.exchange.publicInterfaces.Exchanged result =
					victim.postProcess(exchangeOffer, platform, matchedExchangeOffers);
			assertEquals(exchanged, result);
		}

		@Test
		public void finaliseExchangeOffer() throws Exception {
			ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
			doReturn(exchangeOffer).when(exchangeOffer).finalise();
			Exchanged exchanged = mock(Exchanged.class);
			org.trading.exchange.publicInterfaces.Market market =
					mock(org.trading.exchange.publicInterfaces.Market.class);
			ExchangeOffer[] matchedExchangeOffers = platform.getMatching(exchangeOffer, market, platform)
					.toArray(new ExchangeOffer[1]);
			doReturn(exchanged).when(platform).finalise(exchangeOffer, platform, matchedExchangeOffers);

			org.trading.exchange.publicInterfaces.Exchanged result =
					victim.finalise(exchangeOffer, platform, matchedExchangeOffers);
			assertEquals(exchanged, result);
		}

		@Test
		public void getMatchingExchangeOffers() throws Exception {
			ExchangeOffer exchangeOffer =
					mock(ExchangeOffer.class);
			Collection<? extends ExchangeOffer> orders = mock(Collection.class);
			org.trading.exchange.publicInterfaces.Market market =
					mock(org.trading.exchange.publicInterfaces.Market.class);
			doReturn(orders).when(platform).getMatching(exchangeOffer, market, platform);

			Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> result =
					victim.getMatching(exchangeOffer, market, platform);
			assertEquals(orders, result);
		}
	}
}