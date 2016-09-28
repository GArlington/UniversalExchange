package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.math.SimpleDecimal;
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
	org.trading.exchange.publicInterfaces.Commodity offered = mock(Commodity.class), required = mock(Commodity.class),
			notRequired =
					mock(Commodity.class);
	Market matchedMarket, unmatchedMarket;
	int orderPoolSize = 10;

	UniversalExchange victim;

	static Exchangeable setUp(Exchangeable exchangeable, Commodity offered, long offeredValue, Commodity required,
							  long requiredValue) {
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).preProcess();
		org.trading.exchange.interfaces.Exchangeable ex = exchangeable;
		doReturn(ex).when(ex).open();
		doReturn(exchangeable).when(exchangeable).process();
		doReturn(exchangeable).when(exchangeable).finalise();

		doReturn(Exchangeable.State.OPEN).when(exchangeable).getExchangeableState();
		doReturn(offered).when(exchangeable).getOffered();
		doReturn(offeredValue).when(exchangeable).getOfferedValue();
		doReturn(required).when(exchangeable).getRequired();
		doReturn(requiredValue).when(exchangeable).getRequiredValue();
		doReturn(new SimpleDecimal(offeredValue).divide(new SimpleDecimal(requiredValue))).when(exchangeable)
				.getExchangeRate();
		doReturn(new SimpleDecimal(requiredValue).divide(new SimpleDecimal(offeredValue))).when(exchangeable)
				.getInverseExchangeRate();
		return exchangeable;
	}

	static void setUpToMatch(Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> orders,
							 org.trading.exchange.interfaces.Exchangeable exchangeable) {
		for (org.trading.exchange.publicInterfaces.Exchangeable ex : orders) {
			doReturn(true).when(ex).isMatching(exchangeable);
		}
	}

	static void setUpToMatch(org.trading.exchange.publicInterfaces.Exchangeable[] orders,
							 org.trading.exchange.interfaces.Exchangeable exchangeable) {
		for (org.trading.exchange.publicInterfaces.Exchangeable ex : orders) {
			doReturn(true).when(ex).isMatching(exchangeable);
			doReturn(ex).when(exchangeable).match(ex);
		}
	}

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
		Exchangeable order = mock(Exchangeable.class);
		doReturn(order).when(order).validate();

		Exchangeable result = (Exchangeable) victim.validate(order);
		assertEquals(order, result);
	}

	@Test
	public void acceptOrder() throws Exception {
		org.trading.exchange.interfaces.Exchangeable exchangeable =
				mock(org.trading.exchange.interfaces.Exchangeable.class);
		doReturn(exchangeable).when(exchangeable).validate();
		doReturn(exchangeable).when(exchangeable).preProcess();
		doReturn(exchangeable).when(exchangeable).open();
		doReturn(offered).when(exchangeable).getOffered();
		doReturn(required).when(exchangeable).getRequired();
		Market market = mock(org.trading.exchange.interfaces.Market.class);
		doReturn(true).when(market).validate(exchangeable);
		doReturn(true).when(market).accept(exchangeable);
		victim.open(market);

		Exchangeable result = (Exchangeable) victim.accept(exchangeable);
		assertEquals(exchangeable, result);
	}

	@Test
	public void getMatchingOrders() throws Exception {
		Location location = mock(Location.class);
		Exchangeable order = mock(Exchangeable.class);
		order = setUp(order, offered, 400L, required, 1L);
		matchedMarket = createMarket(location, order, 0);

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result = victim.getMatching(order);
		assertEquals(matchedMarket.getOrders(), result);
	}

	@Test
	public void getMatchingOrdersFailNoMatchingOrders() throws Exception {
		Location location = mock(Location.class);
		Exchangeable order = mock(Exchangeable.class);
		order = setUp(order, offered, 400L, required, 1L);
		unmatchedMarket = createMarket(location, order, 0);
		doReturn(notRequired).when(order).getRequired();

		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result = victim.getMatching(order);
		assertEquals(0, result.size());
	}

	@Test
	public void testMultipleMarkets() throws Exception {
		int size = orderPoolSize, i = 0;
		Exchangeable[] orders = new Exchangeable[size];
		Location location = mock(Location.class);
		Market[] markets = new Market[size];
		Exchangeable exchangeable;
		Commodity offered;

		offered = mock(Commodity.class);
		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered, 400L, required, 1L);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		offered = mock(Commodity.class);
		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered, 399L, required, 1L);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		offered = mock(Commodity.class);
		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered, 398L, required, 1L);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		offered = mock(Commodity.class);
		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered, 390L, required, 1L);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
					victim.getMatching(orders[j]);
			assertTrue(j + ": " + orders[j] + " does not get matching orders: " + result, result.size() > 0);
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result,
					markets[j].getOrders().size(), result.size());
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result, markets[j].getOrders(),
					result);
		}
	}

	@Test
	public void testCreateMarketAddExchangeablesMatchExchangeable() throws Exception {
		int size = orderPoolSize, i = 0, incr = 5;
		Exchangeable[] exchangeables = new Exchangeable[size];
		Market[] markets = new Market[size];
		Location location = mock(Location.class);

		Exchangeable exchangeable, result;
		org.trading.exchange.publicInterfaces.Commodity offered, required;

		offered = mock(Commodity.class);
		required = mock(Commodity.class);
		Collection<Commodity> commodities = new LinkedList<>();
		commodities.add(offered);
		commodities.add(required);
		doReturn(commodities).when(location).getCommodities();
		long offeredValue, requiredValue;
		offeredValue = 450L;
		requiredValue = 1L;

		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered, offeredValue, required, requiredValue);
		exchangeables[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> check =
					victim.getMatching(exchangeables[j]);
			assertTrue(check.size() > 0);
			assertEquals(size, check.size());
			assertEquals(markets[j].getOrders(), check);
		}

		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, required, requiredValue, offered, offeredValue);
		exchangeable = exchangeables[0];
		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> check;
		check = victim.getMatching(exchangeable);
		assertEquals(size, check.size());

		result = (Exchangeable) victim.accept(exchangeable);
		assertEquals(result, exchangeable);
		check = victim.getMatching(exchangeable);
		assertEquals(exchangeable + " does not get matching orders: " + check, --size, check.size());
	}

	@Test
	public void testCreateMarketAddExchangeablesMatchExchangeable2() throws Exception {
		int size = orderPoolSize, i = 0;
		Exchangeable[] exchangeables = new Exchangeable[size];
		Market[] markets = new Market[size];
		Location location = mock(Location.class);

		Exchangeable exchangeable, result;
		org.trading.exchange.publicInterfaces.Commodity offered, required;

		offered = mock(Commodity.class);
		required = mock(Commodity.class);
		long offeredValue, requiredValue;
		offeredValue = 450L;
		requiredValue = 1L;

		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered, offeredValue, required, requiredValue);
		exchangeables[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> check =
					victim.getMatching(exchangeables[j]);
			assertTrue(check.size() > 0);
			assertEquals(size, check.size());
			assertEquals(markets[j].getOrders(), check);
		}

		long numberOfOrdersToFill = 6;
		long numberOfOrdersToPrice = 3;
		offeredValue = (offeredValue + (numberOfOrdersToPrice - 1)) * numberOfOrdersToFill;
		requiredValue *= numberOfOrdersToFill;
		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered, offeredValue, required, requiredValue);
		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> check;
		check = victim.getMatching(exchangeable);
		assertEquals(size, check.size());

		result = (Exchangeable) victim.accept(exchangeable);
		assertEquals(result, exchangeable);
		check = victim.getMatching(exchangeable);
		size -= ((numberOfOrdersToPrice < numberOfOrdersToFill) ? numberOfOrdersToPrice : numberOfOrdersToFill);
		size = (size > 0 ? size : 0);
		assertEquals(exchangeable + " does not get matching orders: " + check, size, check.size());
	}

	@Test
	public void testCreateMultipleMarketsAddExchangeablesMatchExchangeable() throws Exception {
		int size = orderPoolSize, i = 0;
		Exchangeable[] orders = new Exchangeable[size];
		Location location = mock(Location.class);
		Market[] markets = new Market[size];

		org.trading.exchange.publicInterfaces.Commodity offered, required, offered2, required2;
		offered = mock(Commodity.class);
		required = mock(Commodity.class);
		long offeredValue, requiredValue, offeredValue2, requiredValue2;
		offeredValue = 450L;
		requiredValue = 1L;

		Exchangeable exchangeable;

		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered, offeredValue, required, requiredValue);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		offered2 = mock(Commodity.class);
		offeredValue2 = 1L;
		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, required, requiredValue, offered2, offeredValue2);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		required2 = mock(Commodity.class);
		requiredValue2 = 510L;
		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, offered2, offeredValue2, required2, requiredValue2);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		exchangeable = mock(Exchangeable.class);
		exchangeable = setUp(exchangeable, required2, requiredValue2, offered, offeredValue);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable, i);
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> result =
					victim.getMatching(orders[j]);
			assertTrue(j + ": " + orders[j] + " does not get matching orders: " + result, result.size() > 0);
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result, markets[j].getOrders(),
					result);
		}

		exchangeable = orders[0];
		offered = exchangeable.getOffered();
		offeredValue = exchangeable.getOfferedValue();
		required = exchangeable.getRequired();
		requiredValue = exchangeable.getRequiredValue();
		long numberOfOrdersToFill = 8;
		long numberOfOrdersToPrice = 5;
		offeredValue = (offeredValue + (numberOfOrdersToPrice - 1)) * numberOfOrdersToFill;
		requiredValue *= numberOfOrdersToFill;
/*
		exchangeable = mock(Exchangeable.class);
*/
		exchangeable = setUp(exchangeable, offered, offeredValue, required, requiredValue);
		Collection<? extends org.trading.exchange.publicInterfaces.Exchangeable> check;
		check = victim.getMatching(exchangeable);
		assertEquals(size, check.size());

		Exchangeable result = (Exchangeable) victim.accept(exchangeable);
		assertEquals(result, exchangeable);
		check = victim.getMatching(exchangeable);
		size -= ((numberOfOrdersToPrice < numberOfOrdersToFill) ? numberOfOrdersToPrice : numberOfOrdersToFill);
		size = (size > 0 ? size : 0);
		assertEquals(exchangeable + " does not get matching orders: " + check, size, check.size());
	}

	private Market createMarket(Location location, Exchangeable exchangeable, int i) {
		org.trading.exchange.publicInterfaces.Commodity offered = exchangeable.getRequired();
		long offeredValue = exchangeable.getRequiredValue();
		org.trading.exchange.publicInterfaces.Commodity required = exchangeable.getOffered();
		long requiredValue = exchangeable.getOfferedValue();

		// Setup a market to match the order
		Owner owner = mock(Owner.class);
		Market market = new MarketMock.Builder<MarketMock>().setId("MarketMock " + i).setLocation(location)
				.setName("MarketMock " + i).setOffered(offered).setRequired(required).setOwner(owner)
				.setAutoMatching(true).build();

		market = victim.open(market);
		assertEquals(victim.getMarkets().toString(), i + 1, victim.getMarkets().size());

		Exchangeable[] exchangeables = createMatchingOrders(offered, offeredValue, required, requiredValue);
		UniversalExchangeTest.setUpToMatch(exchangeables, exchangeable);
		assertEquals(orderPoolSize, exchangeables.length);
		for (Exchangeable ex : exchangeables) {
			assertEquals(ex, victim.accept(ex));
		}
		assertEquals(exchangeables.length, market.getOrders().size());
		return market;

	}

	private Exchangeable[] createMatchingOrders(org.trading.exchange.publicInterfaces.Commodity offered,
												long offeredValue,
												org.trading.exchange.publicInterfaces.Commodity required,
												long requiredValue) {
		Exchangeable[] matchedOrders = new Exchangeable[orderPoolSize];
		for (int i = 0; i < orderPoolSize; i++) {
			Exchangeable exchangeable = mock(Exchangeable.class);
			exchangeable = setUp(exchangeable, offered, offeredValue, required, requiredValue);
			matchedOrders[i] = exchangeable;
		}
		return matchedOrders;
	}
}