package org.trading.exchange.interfaces;

import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.Location;
import org.trading.exchange.publicInterfaces.Owner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by GArlington.
 */
// TODO - complete these tests
public class UniversalExchangePlatformLoadTest {
	private UniversalExchange victim;

	@Before
	public void setup() throws Exception {
		Owner owner = mock(Owner.class);
		victim = new UniversalExchangeMock("UniversalExchangeLoadTest", new UniversalExchange.Strategy() {
		}, owner, true);
	}

	@Test
	public void openMarket() throws Exception {
		Location location = mock(Location.class);
		Commodity offered = mock(Commodity.class);
		Commodity required = mock(Commodity.class);
		Owner owner = mock(Owner.class);
		Market market =
				new MarketMock.Builder<MarketMock>().setId("marketId").setLocation(location).setName("marketName")
						.setOffered(offered).setRequired(required).setOwner(owner).setAutoMatching(true).build();
		Market expected = market;
		org.trading.exchange.publicInterfaces.Market result = victim.open(market);
		assertEquals(expected, result);
		assertEquals(1, victim.getMarkets().size());
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