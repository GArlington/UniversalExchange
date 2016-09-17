package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.model.Commodity;
import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Location;
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

		Market result = victim.open(market);
		assertEquals(market, result);
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
	}

	@Test
	public void validateOrder() throws Exception {
		Exchangeable order = mock(Exchangeable.class);
		doReturn(order).when(order).validate();

		Exchangeable result = victim.validate(order);
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

		Exchangeable result = victim.accept(exchangeable);
		assertEquals(exchangeable, result);
	}

	@Test
	public void getMatchingOrders() throws Exception {
		Location location = org.trading.exchange.model.Location.LONDON;
		Exchangeable order =
				new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
						.setOffered(offered).setOfferedValue(1L).setRequired(required).setRequiredValue(1_000L)
						.build();
		matchedMarket = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());

		Collection<? extends Exchangeable> result = victim.getMatching(order);
		assertEquals(matchedMarket.getOrders(), result);
	}

	@Test
	public void getMatchingOrdersFailNoMatchingOrders() throws Exception {
		Location location = org.trading.exchange.model.Location.LONDON;
		Exchangeable order = mock(Exchangeable.class);
		doReturn(required).when(order).getOffered();
		doReturn(1L).when(order).getOfferedValue();
		doReturn(offered).when(order).getRequired();
		doReturn(1L).when(order).getRequiredValue();
		unmatchedMarket = createMarket(location, notRequired, order.getOfferedValue(), order.getRequired(),
				order.getRequiredValue());

		Collection<? extends Exchangeable> result = victim.getMatching(order);
		assertEquals(0, result.size());
	}

	@Test
	public void testMultipleMarkets() throws Exception {
		int size = orderPoolSize, i = 0;
		Exchangeable[] orders = new Exchangeable[size];
		Location location = org.trading.exchange.model.Location.LONDON;
		Market[] markets = new Market[size];
		Exchangeable order;

		order = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(Commodity.GBP).setOfferedValue(1L).setRequired(Commodity.GOLD).setRequiredValue(1_000L)
				.build();
		orders[i] = order;
		markets[i] = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());
		i++;

		order = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(Commodity.GOLD).setOfferedValue(1L).setRequired(Commodity.SILVER).setRequiredValue(1_000L)
				.build();
		orders[i] = order;
		markets[i] = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());
		i++;

		order = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(Commodity.SILVER).setOfferedValue(1L).setRequired(Commodity.EUR).setRequiredValue(1_000L)
				.build();
		orders[i] = order;
		markets[i] = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());
		i++;

		order = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(Commodity.EUR).setOfferedValue(1L).setRequired(Commodity.GBP).setRequiredValue(1_000L)
				.build();
		orders[i] = order;
		markets[i] = createMarket(location, order.getRequired(), order.getRequiredValue(), order.getOffered(),
				order.getOfferedValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends Exchangeable> result = victim.getMatching(orders[j]);
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result, markets[j].getOrders(),
					result);
		}
	}

	@Test
	public void testCreateMarketAddExchangeablesMatchExchangeable() throws Exception {
		int size = orderPoolSize, i = 0, incr = 5;
		Exchangeable[] exchangeables = new Exchangeable[size];
		Market[] markets = new Market[size];
		Location location = org.trading.exchange.model.Location.LONDON;

		Exchangeable exchangeable, result;
		org.trading.exchange.publicInterfaces.Commodity offered, required;

		offered = Commodity.GBP;
		required = Commodity.SILVER;
		long offeredValue, requiredValue;
		offeredValue = 450L;
		requiredValue = 1L;

		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(offered).setOfferedValue(offeredValue).setRequired(required)
				.setRequiredValue(requiredValue)
				.build();
		exchangeables[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(), exchangeable.getOfferedValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends Exchangeable> check = victim.getMatching(exchangeables[j]);
			assertEquals(size, check.size());
			assertEquals(markets[j].getOrders(), check);
		}

		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(offered).setOfferedValue(offeredValue + incr).setRequired(required).setRequiredValue
						(requiredValue)
				.build();
		Collection<? extends Exchangeable> check;
		check = victim.getMatching(exchangeable);
		assertEquals(size, check.size());

		result = victim.accept(exchangeable);
		assertEquals(result, exchangeable);
		check = victim.getMatching(exchangeable);
		assertEquals(exchangeable + " does not get matching orders: " + check, --size, check.size());
	}

	@Test
	public void testCreateMarketAddExchangeablesMatchExchangeable2() throws Exception {
		int size = orderPoolSize, i = 0;
		Exchangeable[] exchangeables = new Exchangeable[size];
		Market[] markets = new Market[size];
		Location location = org.trading.exchange.model.Location.LONDON;

		Exchangeable exchangeable, result;
		org.trading.exchange.publicInterfaces.Commodity offered, required;

		offered = Commodity.GBP;
		required = Commodity.SILVER;
		long offeredValue, requiredValue;
		offeredValue = 450L;
		requiredValue = 1L;

		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(offered).setOfferedValue(offeredValue).setRequired(required).setRequiredValue
						(requiredValue)
				.build();
		exchangeables[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(), exchangeable.getOfferedValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends Exchangeable> check = victim.getMatching(exchangeables[j]);
			assertEquals(size, check.size());
			assertEquals(markets[j].getOrders(), check);
		}

		long numberOfOrdersToFill = 6;
		long numberOfOrdersToPrice = 3;
		offeredValue = (offeredValue + (numberOfOrdersToPrice - 1)) * numberOfOrdersToFill;
		requiredValue *= numberOfOrdersToFill;
		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(offered).setOfferedValue(offeredValue).setRequired(required).setRequiredValue
						(requiredValue)
				.build();
		Collection<? extends Exchangeable> check;
		check = victim.getMatching(exchangeable);
		assertEquals(size, check.size());

		result = victim.accept(exchangeable);
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
		Location location = org.trading.exchange.model.Location.LONDON;
		Market[] markets = new Market[size];

		org.trading.exchange.publicInterfaces.Commodity offered, required, offered2, required2;
		offered = Commodity.GBP;
		required = Commodity.SILVER;
		long offeredValue, requiredValue, offeredValue2, requiredValue2;
		offeredValue = 450L;
		requiredValue = 1L;

		Exchangeable exchangeable;

		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(offered).setOfferedValue(offeredValue).setRequired(required).setRequiredValue
						(requiredValue)
				.build();
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(),
				exchangeable.getOfferedValue());
		i++;

		offered2 = Commodity.GOLD;
		offeredValue2 = 1L;
		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(required).setOfferedValue(requiredValue).setRequired(offered2)
				.setRequiredValue(offeredValue2)
				.build();
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(),
				exchangeable.getOfferedValue());
		i++;

		required2 = Commodity.EUR;
		requiredValue2 = 510L;
		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(offered2).setOfferedValue(offeredValue2).setRequired(required2)
				.setRequiredValue(requiredValue2)
				.build();
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(),
				exchangeable.getOfferedValue());
		i++;

		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(required2).setOfferedValue(requiredValue2).setRequired(offered)
				.setRequiredValue(requiredValue)
				.build();
		orders[i] = exchangeable;
		markets[i] = createMarket(location, exchangeable.getRequired(), exchangeable.getRequiredValue(),
				exchangeable.getOffered(),
				exchangeable.getOfferedValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<? extends Exchangeable> result = victim.getMatching(orders[j]);
			assertEquals(j + ": " + orders[j] + " does not get matching orders: " + result, markets[j].getOrders(),
					result);
		}

		long numberOfOrdersToFill = 8;
		long numberOfOrdersToPrice = 5;
		offeredValue = (offeredValue + (numberOfOrdersToPrice - 1)) * numberOfOrdersToFill;
		requiredValue *= numberOfOrdersToFill;
		exchangeable = new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
				.setOffered(offered).setOfferedValue(offeredValue).setRequired(required).setRequiredValue
						(requiredValue)
				.build();
		Collection<? extends Exchangeable> check;
		check = victim.getMatching(exchangeable);
		assertEquals(size, check.size());

		Exchangeable result = victim.accept(exchangeable);
		assertEquals(result, exchangeable);
		check = victim.getMatching(exchangeable);
		size -= ((numberOfOrdersToPrice < numberOfOrdersToFill) ? numberOfOrdersToPrice : numberOfOrdersToFill);
		size = (size > 0 ? size : 0);
		assertEquals(exchangeable + " does not get matching orders: " + check, size, check.size());
	}

	private Market createMarket(Location location,
								org.trading.exchange.publicInterfaces.Commodity offered, long offeredValue,
								org.trading.exchange.publicInterfaces.Commodity required, long requiredValue) {
		// Setup a market to match the order
		Market market = new org.trading.exchange.model.Market.Builder<org.trading.exchange.model.Market>()
				.setId("matchedMarket").setLocation(location)
				.setName(location.getCode() + offered.getId() + required.getId()).setOffered(offered)
				.setRequired(required).build();
		market = victim.open(market);

		Exchangeable[] exchangeables = createMatchingOrders(offered, offeredValue, required, requiredValue);
		for (Exchangeable exchangeable : exchangeables) {
			victim.accept(exchangeable);
		}
		return market;

	}

	private Exchangeable[] createMatchingOrders(org.trading.exchange.publicInterfaces.Commodity offered,
												long offeredValue,
												org.trading.exchange.publicInterfaces.Commodity required,
												long requiredValue) {
		Exchangeable[] matchedOrders = new Exchangeable[orderPoolSize];
		for (int i = 0; i < orderPoolSize; i++) {
			matchedOrders[i] =
					new org.trading.exchange.model.Exchangeable.Builder<org.trading.exchange.model.Exchangeable>()
							.setOffered(offered).setOfferedValue(offeredValue).setRequired(required)
							.setRequiredValue(requiredValue + i).build();
		}
		return matchedOrders;
	}
}