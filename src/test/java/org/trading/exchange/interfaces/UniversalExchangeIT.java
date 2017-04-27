package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.Location;
import org.trading.exchange.publicInterfaces.Market;
import org.trading.exchange.publicInterfaces.Owner;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by GArlington.
 */
public class UniversalExchangeIT {
	private org.trading.exchange.publicInterfaces.Commodity offered = mock(Commodity.class),
			required = mock(Commodity.class), notRequired = mock(Commodity.class);
	private Market matchedMarket, unmatchedMarket;
	private int orderPoolSize = 10;

	private UniversalExchange victim;

	@Before
	public void setUp() throws Exception {
		String name = "UniversalExchangeIT";
		UniversalExchange.Strategy strategy = new UniversalExchange.Strategy() {
		};
		Collection<Market> markets = new LinkedList<>();
		Owner owner = mock(Owner.class);
		victim = new UniversalExchangeMock(name, strategy, owner, true);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void openMarket() throws Exception {
		Market market = mock(Market.class);

		Market result = victim.open(market);
		assertEquals(market, result);
		assertEquals(1, victim.getMarkets().size());
	}

	@Test(expected = IllegalStateException.class)
	public void openMarketFail() throws Exception {
		Market market = mock(Market.class);
		Market result = victim.open(market);

		result = victim.open(market);
	}

	@Test(expected = IllegalStateException.class)
	public void closeMarketFail() throws Exception {
		Market market = mock(Market.class);

		victim.close(market);
	}

	@Test
	public void closeMarket() throws Exception {
		Market market = mock(Market.class);
		Market result = victim.open(market);

		boolean bResult = victim.close(market);
		assertEquals(true, bResult);
		assertEquals(0, victim.getMarkets().size());
	}

	@Test
	public void validateOrder() throws Exception {
		ExchangeOffer order = mock(ExchangeOffer.class);
		doReturn(order).when(order).validate();
		Market market = mock(Market.class);
		doReturn(true).when(market).validate(order);

		ExchangeOffer result = (ExchangeOffer) victim.validate(order, market);
		assertEquals(order, result);
	}

	@Test
	public void acceptOrder() throws Exception {
		ExchangeOffer exchangeOffer =
				mock(ExchangeOffer.class);
		doReturn(exchangeOffer).when(exchangeOffer).validate();
		doReturn(exchangeOffer).when(exchangeOffer).preProcess();
		doReturn(exchangeOffer).when(exchangeOffer).open();
		doReturn(offered).when(exchangeOffer).getOffered();
		doReturn(required).when(exchangeOffer).getRequired();
		Market market = mock(org.trading.exchange.interfaces.Market.class);
		doReturn(true).when(market).validate(exchangeOffer);
		doReturn(exchangeOffer).when(market).accept(exchangeOffer);
		victim.open(market);

		ExchangeOffer result = (ExchangeOffer) victim.accept(exchangeOffer, market);
		assertEquals(exchangeOffer, result);
	}

	@Test
	public void getMatchingOrders() throws Exception {
		Location location = mock(Location.class);
		doReturn(true).when(location).checkCommodity(offered);
		doReturn(true).when(location).checkCommodity(required);
		ExchangeOffer order = mock(ExchangeOffer.class);
		order = UniversalExchangeTest.setUpExchangeOffer(order, offered, 400L, required, 1L);
		matchedMarket = createMarket(location, order, 0);

		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> result =
				victim.getMatching(order, matchedMarket);
		assertEquals(matchedMarket.getOffers(), result);
	}

	@Test
	public void getMatchingOrdersFailNoMatchingOrders() throws Exception {
		Location location = mock(Location.class);
		doReturn(true).when(location).checkCommodity(offered);
		doReturn(true).when(location).checkCommodity(required);
		ExchangeOffer order = mock(ExchangeOffer.class);
		order = UniversalExchangeTest.setUpExchangeOffer(order, offered, 400L, required, 1L);
		unmatchedMarket = createMarket(location, order, 0);
		doReturn(notRequired).when(order).getRequired();

		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> result =
				victim.getMatching(order, unmatchedMarket);
		assertEquals(0, result.size());
	}

	@Test
	public void testMultipleMarkets() throws Exception {
		int size = orderPoolSize, i = 0;
		ExchangeOffer[] orders = new ExchangeOffer[size];
		Commodity offered;
		offered = mock(Commodity.class);
		Location location = mock(Location.class);
		doReturn(true).when(location).checkCommodity(offered);
		doReturn(true).when(location).checkCommodity(required);

		Market[] markets = new Market[size];
		ExchangeOffer exchangeOffer;
		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, 400L, required, 1L);
		orders[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		offered = mock(Commodity.class);
		doReturn(true).when(location).checkCommodity(offered);
		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, 399L, required, 1L);
		orders[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		offered = mock(Commodity.class);
		doReturn(true).when(location).checkCommodity(offered);
		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, 398L, required, 1L);
		orders[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		offered = mock(Commodity.class);
		doReturn(true).when(location).checkCommodity(offered);
		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, 390L, required, 1L);
		orders[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> result =
					victim.getMatching(orders[j], markets[j]);
			assertTrue(j + ": " + orders[j] + " does not get matching orders: " + result, result.size() > 0);
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result,
					markets[j].getOffers().size(), result.size());
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result, markets[j].getOffers(),
					result);
		}
	}

	@Test
	public void testCreateMarketAddExchangeOffersMatchExchangeOffer() throws Exception {
		int size = orderPoolSize, i = 0, incr = 5;
		ExchangeOffer[] exchangeOffers = new ExchangeOffer[size];
		Market[] markets = new Market[size];
		Location location = mock(Location.class);

		ExchangeOffer exchangeOffer, result;
		org.trading.exchange.publicInterfaces.Commodity offered, required;

		offered = mock(Commodity.class);
		required = mock(Commodity.class);
		Collection<Commodity> commodities = new LinkedList<>();
		commodities.add(offered);
		commodities.add(required);
		doReturn(commodities).when(location).getCommodities();
		doReturn(true).when(location).checkCommodity(offered);
		doReturn(true).when(location).checkCommodity(required);
		long offeredValue, requiredValue;
		offeredValue = 450L;
		requiredValue = 1L;

		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer =
				UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, offeredValue, required,
						requiredValue);
		exchangeOffers[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> check =
					victim.getMatching(exchangeOffers[j], markets[j]);
			assertTrue(check.size() > 0);
			assertEquals(size, check.size());
			assertEquals(markets[j].getOffers(), check);
		}

//		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = exchangeOffers[0];
		Market market = markets[0];
		exchangeOffer =
				UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, required, requiredValue, offered,
						offeredValue);
		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> check;
		check = victim.getMatching(exchangeOffer, market);
		assertEquals(size, check.size());


		result = (ExchangeOffer) victim.accept(exchangeOffer, market);
		assertEquals(result, exchangeOffer);
		check = victim.getMatching(exchangeOffer, market);
		// TODO - this test is only valid for complete implementation
//		assertEquals(exchangeOffer + " does not get matching orders: " + "\n" + market, --size, check.size());
	}

	@Test
	public void testCreateMarketAddExchangeOffersMatchExchangeOffer2() throws Exception {
		int size = orderPoolSize, i = 0;
		ExchangeOffer[] exchangeOffers = new ExchangeOffer[size];
		Market[] markets = new Market[size];
		Location location = mock(Location.class);

		ExchangeOffer exchangeOffer, result;
		org.trading.exchange.publicInterfaces.Commodity offered, required;

		offered = mock(Commodity.class);
		required = mock(Commodity.class);
		doReturn(true).when(location).checkCommodity(offered);
		doReturn(true).when(location).checkCommodity(required);
		long offeredValue, requiredValue;
		offeredValue = 450L;
		requiredValue = 1L;

		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer =
				UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, offeredValue, required,
						requiredValue);
		exchangeOffers[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> check =
					victim.getMatching(exchangeOffers[j], markets[j]);
			assertTrue(check.size() > 0);
			assertEquals(size, check.size());
			assertEquals(markets[j].getOffers(), check);
		}

//		exchangeOffer = mock(ExchangeOffer.class);
		Market market = markets[0];
		exchangeOffer = exchangeOffers[0];
		long numberOfOrdersToFill = 6;
		long numberOfOrdersToPrice = 3;
		offeredValue = (offeredValue + (numberOfOrdersToPrice - 1)) * numberOfOrdersToFill;
		requiredValue *= numberOfOrdersToFill;
		exchangeOffer =
				UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, offeredValue, required,
						requiredValue);
		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> check;
		check = victim.getMatching(exchangeOffer, market);
		assertEquals(size, check.size());

		result = (ExchangeOffer) victim.accept(exchangeOffer, market);
		assertEquals(result, exchangeOffer);
		check = victim.getMatching(exchangeOffer, market);
		size -= ((numberOfOrdersToPrice < numberOfOrdersToFill) ? numberOfOrdersToPrice : numberOfOrdersToFill);
		size = (size > 0 ? size : 0);
		// TODO - this test is only valid for complete implementation
//		assertEquals(exchangeOffer + " does not get matching orders: " + "\n" + market, size, check.size());
	}

	@Test
	public void testCreateMultipleMarketsAddExchangeOffersMatchExchangeOffer() throws Exception {
		int size = orderPoolSize, i = 0;
		ExchangeOffer[] orders = new ExchangeOffer[size];
		Location location = mock(Location.class);
		Market[] markets = new Market[size];

		org.trading.exchange.publicInterfaces.Commodity offered, required, offered2, required2;
		offered = mock(Commodity.class);
		required = mock(Commodity.class);
		doReturn(true).when(location).checkCommodity(offered);
		doReturn(true).when(location).checkCommodity(required);
		long offeredValue, requiredValue, offeredValue2, requiredValue2;
		offeredValue = 450L;
		requiredValue = 1L;

		ExchangeOffer exchangeOffer;

		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer =
				UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, offeredValue, required,
						requiredValue);
		orders[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		offered2 = mock(Commodity.class);
		doReturn(true).when(location).checkCommodity(offered2);
		offeredValue2 = 1L;
		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = UniversalExchangeTest
				.setUpExchangeOffer(exchangeOffer, required, requiredValue, offered2, offeredValue2);
		orders[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		required2 = mock(Commodity.class);
		doReturn(true).when(location).checkCommodity(required2);
		requiredValue2 = 510L;
		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = UniversalExchangeTest
				.setUpExchangeOffer(exchangeOffer, offered2, offeredValue2, required2, requiredValue2);
		orders[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		exchangeOffer = mock(ExchangeOffer.class);
		exchangeOffer = UniversalExchangeTest
				.setUpExchangeOffer(exchangeOffer, required2, requiredValue2, offered, offeredValue);
		orders[i] = exchangeOffer;
		markets[i] = createMarket(location, exchangeOffer, i);
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> result =
					victim.getMatching(orders[j], markets[j]);
			assertTrue(j + ": " + orders[j] + " does not get matching orders: " + result, result.size() > 0);
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result, markets[j].getOffers(),
					result);
		}

		exchangeOffer = orders[0];
		offered = exchangeOffer.getOffered();
		offeredValue = exchangeOffer.getOfferedValue();
		required = exchangeOffer.getRequired();
		requiredValue = exchangeOffer.getRequiredValue();
		long numberOfOrdersToFill = 8;
		long numberOfOrdersToPrice = 5;
		offeredValue = (offeredValue + (numberOfOrdersToPrice - 1)) * numberOfOrdersToFill;
		requiredValue *= numberOfOrdersToFill;
		exchangeOffer =
				UniversalExchangeTest.setUpExchangeOffer(exchangeOffer, offered, offeredValue, required,
						requiredValue);
		Collection<? extends org.trading.exchange.publicInterfaces.ExchangeOffer> check;
		Market market = markets[0];
		check = victim.getMatching(exchangeOffer, market);
		assertEquals(size, check.size());

		ExchangeOffer result = (ExchangeOffer) victim.accept(exchangeOffer, market);
		assertEquals(exchangeOffer, result);
		assertEquals(org.trading.exchange.publicInterfaces.ExchangeOffer.State.OPEN, result.getState());
		check = victim.getMatching(exchangeOffer, market);
		size -= ((numberOfOrdersToPrice < numberOfOrdersToFill) ? numberOfOrdersToPrice : numberOfOrdersToFill);
		size = (size > 0 ? size : 0);
		// TODO - this test is only valid for complete implementation
//		assertEquals(exchangeOffer + " does not get matching orders: " + "\n" + market, size, check.size());
	}

	private Market createMarket(Location location, ExchangeOffer exchangeOffer, int i) {
		org.trading.exchange.publicInterfaces.Commodity offered = exchangeOffer.getRequired();
		long offeredValue = exchangeOffer.getRequiredValue();
		org.trading.exchange.publicInterfaces.Commodity required = exchangeOffer.getOffered();
		long requiredValue = exchangeOffer.getOfferedValue();

		// Setup a market to match the order
		Owner owner = mock(Owner.class);
		Market market = new MarketMock.Builder<MarketMock>().setId("MarketMock " + i).setLocation(location)
				.setName("MarketMock " + i).setOffered(offered).setRequired(required).setOwner(owner)
				.setAutoMatching(true).build();

		market = victim.open(market);
		assertEquals(victim.getMarkets().toString(), i + 1, victim.getMarkets().size());

		ExchangeOffer[] exchangeOffers = createMatchingOrders(offered, offeredValue, required, requiredValue);
		UniversalExchangeTest.setUpExchangeOfferToMatch(exchangeOffers, exchangeOffer);
		assertEquals(orderPoolSize, exchangeOffers.length);
		for (ExchangeOffer ex : exchangeOffers) {
			assertEquals(ex, victim.accept(ex, market));
		}
		assertEquals(exchangeOffers.length, market.getOffers().size());
		return market;

	}

	private ExchangeOffer[] createMatchingOrders(org.trading.exchange.publicInterfaces.Commodity offered,
												 long offeredValue,
												 org.trading.exchange.publicInterfaces.Commodity required,
												 long requiredValue) {
		ExchangeOffer[] matchedOrders = new ExchangeOffer[orderPoolSize];
		for (int i = 0; i < orderPoolSize; i++) {
			ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
			exchangeOffer = UniversalExchangeTest
					.setUpExchangeOffer(exchangeOffer, offered, offeredValue, required, requiredValue);
			matchedOrders[i] = exchangeOffer;
		}
		return matchedOrders;
	}
}