package org.trading.exchange.interfaces;

import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.model.Commodity;

import static org.junit.Assert.assertEquals;

/**
 * Created by GArlington.
 */
public class UniversalExchangeLoadTest {
	UniversalExchange victim;

	@Before
	public void setup() throws Exception {
		victim = new UniversalExchangeMock("UniversalExchangeLoadTest", new UniversalExchange.Strategy() {
		});
	}

	@Test
	public void openMarket() throws Exception {
		Market market =
				new MarketMock("marketId", org.trading.exchange.model.Location.LONDON, "marketId", Commodity.GOLD,
						Commodity.GBP);
		Market expected = market;
		org.trading.exchange.publicInterfaces.Market result = victim.open(market);
		assertEquals(expected, result);
	}

	@Test
	public void closeMarket() throws Exception {

	}

	@Test
	public void validateOrder() throws Exception {

	}

	@Test
	public void acceptOrder() throws Exception {

	}
}