package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.ExchangeableMock;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Market;

import java.util.Collection;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by GArlington.
 */
public class UniversalExchangeIT {
	org.trading.exchange.publicInterfaces.Commodity offered = Commodity.GOLD, required = Commodity.GBP, notRequired =
			Commodity.EUR;
	Market matchedMarket, unmatchedMarket;
	int orderPoolSize = 10;

	UniversalExchange victim;

	@Before
	public void setUp() throws Exception {
		String name = "UniversalExchangeIT";
		UniversalExchange.Strategy strategy = new UniversalExchange.Strategy() {
		};
		Collection<Market> markets = new LinkedList<>();
		victim = new UniversalExchangeMock(name, strategy);
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void openMarket() throws Exception {
		Market market = mock(Market.class);

		Market result = victim.openMarket(market);
		assertEquals(market, result);
	}

	@Test(expected = IllegalStateException.class)
	public void openMarketFail() throws Exception {
		Market market = mock(Market.class);
		Market result = victim.openMarket(market);

		result = victim.openMarket(market);
	}

	@Test(expected = IllegalStateException.class)
	public void closeMarketFail() throws Exception {
		Market market = mock(Market.class);

		Market result = victim.closeMarket(market);
	}

	@Test
	public void closeMarket() throws Exception {
		Market market = mock(Market.class);
		Market result = victim.openMarket(market);

		result = victim.closeMarket(market);
		assertEquals(market, result);
	}

	@Test
	public void validateOrder() throws Exception {
		Exchangeable order = mock(Exchangeable.class);
		doReturn(order).when(order).validate();

		Exchangeable result = victim.validateOrder(order);
		assertEquals(order, result);
	}

	@Test
	public void acceptOrder() throws Exception {
		org.trading.exchange.interfaces.Exchangeable order = mock(org.trading.exchange.interfaces.Exchangeable.class);
		doReturn(order).when(order).validate();
		doReturn(order).when(order).preProcess();

		Exchangeable result = victim.acceptOrder(order);
		assertEquals(order, result);
	}

	@Test
	public void getMatchingOrders() throws Exception {
		Location location = Location.LONDON;
		Exchangeable order = new ExchangeableMock(offered, 1L, required, 1L);
		matchedMarket = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());

		Collection<? extends Exchangeable> result = victim.getMatchingOrders(order);
		assertEquals(matchedMarket.getOrders(), result);
	}

	@Test
	public void getMatchingOrdersFailNoMatchingOrders() throws Exception {
		Location location = Location.LONDON;
		Exchangeable order = mock(Exchangeable.class);
		doReturn(required).when(order).getOffered();
		doReturn(1L).when(order).getOfferedValue();
		doReturn(offered).when(order).getRequired();
		doReturn(1L).when(order).getRequiredValue();
		unmatchedMarket = createMarket(location, notRequired, order.getOfferedValue(), order.getRequired(),
				order.getRequiredValue());

		Collection<? extends Exchangeable> result = victim.getMatchingOrders(order);
		assertEquals(0, result.size());
	}

	@Test
	public void testMultipleMarkets() throws Exception {
		int size = orderPoolSize, i = 0;
		Exchangeable[] orders = new Exchangeable[size];
		Location location;
		Market[] markets = new Market[size];
		Exchangeable order;

		location = Location.LONDON;
		order = new ExchangeableMock(Commodity.GBP, 1L, Commodity.GOLD, 1L);
		orders[i] = order;
		markets[i] = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());
		i++;

		order = new ExchangeableMock(Commodity.GOLD, 1L, Commodity.SILVER, 1L);
		orders[i] = order;
		markets[i] = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());
		i++;

		order = new ExchangeableMock(Commodity.SILVER, 1L, Commodity.EUR, 1L);
		orders[i] = order;
		markets[i] = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());
		i++;

		order = new ExchangeableMock(Commodity.EUR, 1L, Commodity.GBP, 1L);
		orders[i] = order;
		markets[i] = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends Exchangeable> result = victim.getMatchingOrders(orders[j]);
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result, markets[j].getOrders(),
					result);
		}
	}

	@Test
	public void testCreateMarketAddExchangeablesMatchExchangeable() throws Exception {
		int size = orderPoolSize, i = 0, incr = 5;
		Exchangeable[] exchangeables = new Exchangeable[size];
		Market[] markets = new Market[size];
		Location location = Location.LONDON;

		Exchangeable exchangeable, result;
		Commodity offered, required;

		offered = Commodity.GBP;
		required = Commodity.SILVER;
		long offeredValue, requiredValue;
		offeredValue = 450L;
		requiredValue = 1L;

		exchangeable = new ExchangeableMock(offered, offeredValue, required, requiredValue);
		exchangeables[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(), exchangeable.getOfferedValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends Exchangeable> check = victim.getMatchingOrders(exchangeables[j]);
			assertEquals(size, check.size());
			assertEquals(markets[j].getOrders(), check);
		}

		exchangeable = new ExchangeableMock(offered, offeredValue + incr, required, requiredValue);
		Collection<? extends Exchangeable> check;
		check = victim.getMatchingOrders(exchangeable);
		assertEquals(size, check.size());

		result = victim.acceptOrder(exchangeable);
		assertEquals(result, exchangeable);
		check = victim.getMatchingOrders(exchangeable);
		assertEquals(--size, check.size());
	}

	@Test
	public void testCreateMarketAddExchangeablesMatchExchangeable2() throws Exception {
		int size = orderPoolSize, i = 0;
		Exchangeable[] exchangeables = new Exchangeable[size];
		Market[] markets = new Market[size];
		Location location = Location.LONDON;

		Exchangeable exchangeable, result;
		Commodity offered, required;

		offered = Commodity.GBP;
		required = Commodity.SILVER;
		long offeredValue, requiredValue;
		offeredValue = 450L;
		requiredValue = 1L;

		exchangeable = new ExchangeableMock(offered, offeredValue, required, requiredValue);
		exchangeables[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(), exchangeable.getOfferedValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends Exchangeable> check = victim.getMatchingOrders(exchangeables[j]);
			assertEquals(size, check.size());
			assertEquals(markets[j].getOrders(), check);
		}

		long numberOfOrdersToFill = 6;
		long numberOfOrdersToPrice = 3;
		offeredValue = (offeredValue + (numberOfOrdersToPrice - 1)) * numberOfOrdersToFill;
		requiredValue *= numberOfOrdersToFill;
		exchangeable = new ExchangeableMock(offered, offeredValue, required, requiredValue);
		Collection<? extends Exchangeable> check;
		check = victim.getMatchingOrders(exchangeable);
		assertEquals(size, check.size());

		result = victim.acceptOrder(exchangeable);
		assertEquals(result, exchangeable);
		check = victim.getMatchingOrders(exchangeable);
		size -= ((numberOfOrdersToPrice < numberOfOrdersToFill) ? numberOfOrdersToPrice : numberOfOrdersToFill);
		size = (size > 0 ? size : 0);
		assertEquals(exchangeable + " does not get matching orders: " + check, size, check.size());
	}

	@Test
	public void testCreateMultipleMarketsAddExchangeablesMatchExchangeable() throws Exception {
		int size = orderPoolSize, i = 0;
		Exchangeable[] orders = new Exchangeable[size];
		Location location = Location.LONDON;
		Market[] markets = new Market[size];

		Commodity offered, required, offered2, required2;
		offered = Commodity.GBP;
		required = Commodity.SILVER;
		long offeredValue, requiredValue, offeredValue2, requiredValue2;
		offeredValue = 450L;
		requiredValue = 1L;

		Exchangeable exchangeable;

		exchangeable = new ExchangeableMock(offered, offeredValue, required, requiredValue);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(),
				exchangeable.getOfferedValue());
		i++;

		offered2 = Commodity.GOLD;
		offeredValue2 = 1L;
		exchangeable = new ExchangeableMock(required, requiredValue, offered2, offeredValue2);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(),
				exchangeable.getOfferedValue());
		i++;

		required2 = Commodity.EUR;
		requiredValue2 = 510L;
		exchangeable = new ExchangeableMock(offered2, offeredValue2, required2, requiredValue2);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(),
				exchangeable.getOfferedValue());
		i++;

		exchangeable = new ExchangeableMock(required2, requiredValue2, offered, offeredValue);
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(),
				exchangeable.getOfferedValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends Exchangeable> result = victim.getMatchingOrders(orders[j]);
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result, markets[j].getOrders(),
					result);
		}

		long numberOfOrdersToFill = 8;
		long numberOfOrdersToPrice = 5;
		offeredValue = (offeredValue + (numberOfOrdersToPrice - 1)) * numberOfOrdersToFill;
		requiredValue *= numberOfOrdersToFill;
		exchangeable = new ExchangeableMock(offered, offeredValue, required, requiredValue);
		Collection<? extends Exchangeable> check;
		check = victim.getMatchingOrders(exchangeable);
		assertEquals(size, check.size());

		Exchangeable result = victim.acceptOrder(exchangeable);
		assertEquals(result, exchangeable);
		check = victim.getMatchingOrders(exchangeable);
		size -= ((numberOfOrdersToPrice < numberOfOrdersToFill) ? numberOfOrdersToPrice : numberOfOrdersToFill);
		size = (size > 0 ? size : 0);
		assertEquals(exchangeable + " does not get matching orders: " + check, size, check.size());
//		System.out.println(victim);
	}

	private Market createMarket(Location location,
								org.trading.exchange.publicInterfaces.Commodity offered, long offeredValue,
								org.trading.exchange.publicInterfaces.Commodity required, long requiredValue) {
		// Setup a market to match the order
		Market market = new MarketMock("matchedMarket", location,
				location.getCode() + offered.getId() + required.getId(), offered, required);
		market = victim.openMarket(market);

		Exchangeable[] exchangeables = createMatchingOrders(offered, offeredValue, required, requiredValue);
		for (Exchangeable exchangeable : exchangeables) {
			victim.acceptOrder(exchangeable);
		}
		return market;

	}

	private Exchangeable[] createMatchingOrders(org.trading.exchange.publicInterfaces.Commodity offered,
												long offeredValue,
												org.trading.exchange.publicInterfaces.Commodity required,
												long requiredValue) {
		Exchangeable[] matchedOrders = new Exchangeable[orderPoolSize];
		for (int i = 0; i < orderPoolSize; i++) {
			matchedOrders[i] = new ExchangeableMock(offered, offeredValue, required, requiredValue + i);
		}
		return matchedOrders;
	}
}