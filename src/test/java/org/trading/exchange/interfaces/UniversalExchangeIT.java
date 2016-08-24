package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
		Exchangeable order = mock(Exchangeable.class);
		doReturn(order).when(order).validate();
		doReturn(order).when(order).preProcess();

		Exchangeable result = victim.acceptOrder(order);
		assertEquals(order, result);
	}

	@Test
	public void getMatchingOrders() throws Exception {
		Location location = Location.LONDON;
		Exchangeable order = mock(Exchangeable.class);
		doReturn(required).when(order).getOffered();
		doReturn(1L).when(order).getOfferedValue();
		doReturn(offered).when(order).getRequired();
		doReturn(1L).when(order).getRequiredValue();
		matchedMarket = createMarket(location, order.getOffered(), order.getOfferedValue(), order.getRequired(),
				order.getRequiredValue());

		Collection<Exchangeable> result = victim.getMatchingOrders(order);
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

		Collection<Exchangeable> result = victim.getMatchingOrders(order);
		assertEquals(0, result.size());
	}

	@Test
	public void testMultipleMarkets() throws Exception {
		int size = 10, i = 0;
		Exchangeable[] orders = new Exchangeable[size];
		Location location;
		Market[] markets = new Market[size];
		Exchangeable order;

		location = Location.LONDON;
		order = mock(Exchangeable.class);
		doReturn(Commodity.GBP).when(order).getOffered();
		doReturn(1L).when(order).getOfferedValue();
		doReturn(Commodity.GOLD).when(order).getRequired();
		doReturn(1L).when(order).getRequiredValue();
		orders[i] = order;
		markets[i] = createMarket(location,
				order.getOffered(), order.getOfferedValue(), order.getRequired(), order.getRequiredValue());
		i++;

		order = mock(Exchangeable.class);
		doReturn(Commodity.GOLD).when(order).getOffered();
		doReturn(1L).when(order).getOfferedValue();
		doReturn(Commodity.SILVER).when(order).getRequired();
		doReturn(1L).when(order).getRequiredValue();
		orders[i] = order;
		markets[i] = createMarket(location,
				order.getOffered(), order.getOfferedValue(), order.getRequired(), order.getRequiredValue());
		i++;

		order = mock(Exchangeable.class);
		doReturn(Commodity.SILVER).when(order).getOffered();
		doReturn(1L).when(order).getOfferedValue();
		doReturn(Commodity.EUR).when(order).getRequired();
		doReturn(1L).when(order).getRequiredValue();
		orders[i] = order;
		markets[i] = createMarket(location,
				order.getOffered(), order.getOfferedValue(), order.getRequired(), order.getRequiredValue());
		i++;

		order = mock(Exchangeable.class);
		doReturn(Commodity.EUR).when(order).getOffered();
		doReturn(1L).when(order).getOfferedValue();
		doReturn(Commodity.GBP).when(order).getRequired();
		doReturn(1L).when(order).getRequiredValue();
		orders[i] = order;
		markets[i] = createMarket(location,
				order.getOffered(), order.getOfferedValue(), order.getRequired(), order.getRequiredValue());
		i++;

		for (int j = 0; j < i; j++) {
			Collection<Exchangeable> result = victim.getMatchingOrders(orders[j]);
			assertEquals(markets[j].getOrders(), result);
		}
//        System.out.println(victim);
	}


	private Market createMarket(Location location,
								org.trading.exchange.publicInterfaces.Commodity offered, long offeredValue,
								org.trading.exchange.publicInterfaces.Commodity required, long requiredValue) {
		// Setup a market to match the order
		return victim.openMarket(new MarketMock("matchedMarket",
				location, location.getCode() + required.getId() + offered.getId(),
				required, offered, createMatchingOrders(offered, offeredValue, required, requiredValue)));

	}

	private Exchangeable[] createMatchingOrders(org.trading.exchange.publicInterfaces.Commodity offered,
												long offeredValue,
												org.trading.exchange.publicInterfaces.Commodity required,
												long requiredValue) {
		int size = 10;
		Exchangeable[] matchedOrders = new Exchangeable[size];
		Exchangeable matchedOrder;
		for (int i = 0; i < size; i++) {
			matchedOrder = mock(Exchangeable.class);
			doReturn(offered).when(matchedOrder).getOffered();
			doReturn(offeredValue + i).when(matchedOrder).getOfferedValue();
			doReturn(required).when(matchedOrder).getRequired();
			doReturn(requiredValue + i).when(matchedOrder).getRequiredValue();
			doReturn(Exchangeable.State.VALIDATED).when(matchedOrder).getExchangeableState();
			matchedOrders[i] = matchedOrder;
		}
		return matchedOrders;
	}
}