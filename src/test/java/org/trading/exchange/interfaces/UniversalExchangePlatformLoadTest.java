package org.trading.exchange.interfaces;

import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.mocks.MarketMock;
import org.trading.exchange.interfaces.mocks.UniversalExchangeMock;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.ExchangeOffer;
import org.trading.exchange.publicInterfaces.Location;
import org.trading.exchange.publicInterfaces.Owner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
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
		Commodity offered = mock(Commodity.class);
		Commodity required = mock(Commodity.class);
		Market market = createMarket(offered, required);
		org.trading.exchange.publicInterfaces.Market expected = market;
		assertEquals(expected, victim.open(market));
		assertEquals(1, victim.getMarkets().size());
	}

	private org.trading.exchange.interfaces.Market createMarket(Commodity offered, Commodity required) {
		Location location = mock(Location.class);
		doReturn(true).when(location).checkCommodity(offered);
		doReturn(true).when(location).checkCommodity(required);
		Owner owner = mock(Owner.class);

		return new MarketMock.Builder<MarketMock>().setId("marketId").setLocation(location).setName("marketName")
				.setOffered(offered).setRequired(required).setOwner(owner).setAutoMatching(true).build();

	}

	@Test
	public void closeMarket() throws Exception {
		Commodity offered = mock(Commodity.class);
		Commodity required = mock(Commodity.class);
		org.trading.exchange.publicInterfaces.Market market = victim.open(createMarket(offered, required));
		assertEquals(true, victim.close(market));
	}

	@Test
	public void validateOrder() throws Exception {
		Commodity offered = mock(Commodity.class);
		Commodity required = mock(Commodity.class);
		org.trading.exchange.publicInterfaces.Market market = victim.open(createMarket(offered, required));
		ExchangeOffer exchangeOffer = mock(ExchangeOffer.class);
		doReturn(offered).when(exchangeOffer).getOffered();
		doReturn(required).when(exchangeOffer).getRequired();
		doReturn(exchangeOffer).when(exchangeOffer).validate();
		ExchangeOffer expected = exchangeOffer;

		assertEquals(expected, victim.validate(exchangeOffer, market));
	}

	@Test
	public void acceptOrder() throws Exception {
		Commodity offered = mock(Commodity.class);
		Commodity required = mock(Commodity.class);
		org.trading.exchange.publicInterfaces.Market market = victim.open(createMarket(offered, required));
		org.trading.exchange.interfaces.ExchangeOffer exchangeOffer =
				mock(org.trading.exchange.interfaces.ExchangeOffer.class);
		doReturn(offered).when(exchangeOffer).getOffered();
		doReturn(required).when(exchangeOffer).getRequired();
		doReturn(exchangeOffer).when(exchangeOffer).preProcess();
		doReturn(exchangeOffer).when(exchangeOffer).validate();
		doReturn(exchangeOffer).when(exchangeOffer).open();
		doReturn(exchangeOffer).when(exchangeOffer).postProcess();
		ExchangeOffer expected = exchangeOffer;

		assertEquals(expected, victim.accept(exchangeOffer, market));
	}
}