package org.trading.exchange.interfaces;

import org.trading.exchange.publicInterfaces.ExchangeOffer;
import org.trading.exchange.publicInterfaces.Exchanged;
import org.trading.exchange.publicInterfaces.Market;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * UniversalExchange will provide Exchange functionality
 * <p>
 * Created by GArlington.
 */
public interface UniversalExchange extends org.trading.exchange.publicInterfaces.UniversalExchange {
//	SSOTokenManager ssoManager = SSOTokenManager.getInstance();

/*
	BusinessLogic getBusinessLogic();
*/

	@Override
	default Market validate(Market market) throws InvalidParameterException {
		return getStrategy().validate(market, getPlatform());
	}

	@Override
	default Market open(Market market) throws IllegalStateException, InvalidParameterException {
		return getStrategy().open(market, getPlatform());
	}

	@Override
	default boolean close(Market market) throws IllegalStateException {
		return getStrategy().close(market, getPlatform());
	}

	@Override
	default ExchangeOffer validate(ExchangeOffer exchangeOffer, Market market) throws InvalidParameterException {
		return getStrategy().validate(exchangeOffer, market, getPlatform());
	}

	@Override
	default ExchangeOffer accept(ExchangeOffer exchangeOffer, Market market) throws InvalidParameterException {
		validate(exchangeOffer, market);
		return getStrategy().accept((ExchangeOffer) exchangeOffer.preProcess(), market, getPlatform());
	}

	@Override
	default Collection<? extends ExchangeOffer> getMatching(ExchangeOffer exchangeOffer, Market market) {
		return getStrategy().getMatching(exchangeOffer, market, getPlatform());
	}

	@Override
	default Exchanged match(ExchangeOffer exchangeOffer, Market market, ExchangeOffer... matchingOrders) {
		return getStrategy().match(exchangeOffer, market, getPlatform(), matchingOrders);
	}

	Strategy getStrategy();

	UniversalExchange getPlatform();

	/**
	 * Validate Market implementation
	 *
	 * @param market
	 * @param platform
	 * @return
	 */
	Market validate(Market market, UniversalExchange platform);

	/**
	 * Open market implementation
	 *
	 * @param market
	 * @return
	 */
	Market open(Market market, UniversalExchange platform) throws IllegalStateException, InvalidParameterException;

	/**
	 * Close market implementation
	 *
	 * @param market
	 * @return
	 */
	boolean close(Market market, UniversalExchange platform) throws IllegalStateException;

	/**
	 * Validate ExchangeOffer implementation
	 *
	 * @param exchangeOffer
	 * @param market
	 * @param platform
	 * @return
	 * @throws InvalidParameterException
	 * @throws IllegalStateException
	 */
	ExchangeOffer validate(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform)
			throws InvalidParameterException, IllegalStateException;

	/**
	 * Accept ExchangeOffer implementation
	 *
	 * @param exchangeOffer
	 * @param market
	 * @param platform
	 * @return
	 * @throws IllegalStateException
	 */
	ExchangeOffer accept(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform)
			throws IllegalStateException;

	/**
	 * Process ExchangeOffer implementation
	 *
	 * @param exchangeOffer
	 * @param market
	 * @param platform
	 * @param matching
	 * @return
	 */
	ExchangeOffer process(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform, ExchangeOffer...
			matching);

	/**
	 * PostProcess ExchangeOffer implementation
	 *
	 * @param exchangeOffer
	 * @param platform
	 * @param matching
	 * @return
	 */
	Exchanged postProcess(ExchangeOffer exchangeOffer, UniversalExchange platform, ExchangeOffer... matching);

	/**
	 * Finalise ExchangeOffer implementation
	 *
	 * @param exchangeOffer
	 * @param platform
	 * @param matching
	 * @return
	 */
	Exchanged finalise(ExchangeOffer exchangeOffer, UniversalExchange platform, ExchangeOffer... matching);

	/**
	 * Get matching orders implementation
	 *
	 * @param exchangeOffer
	 * @param market
	 * @param platform
	 * @return
	 */
	Collection<? extends ExchangeOffer> getMatching(ExchangeOffer exchangeOffer, Market market, UniversalExchange
			platform);

	/**
	 * Get matching orders implementation
	 *
	 * @param exchangeOffer
	 * @param markets
	 * @param maxChainDepth
	 * @return
	 */
	default List<? extends ExchangeOffer> getCrossMarketsOffers(ExchangeOffer exchangeOffer, Collection<? extends Market> markets, int maxChainDepth) {
		return new ArrayList<>();
	}

	/**
	 * Process ExchangeOffer with matched orders implementation
	 *
	 * @param exchangeOffer
	 * @param market
	 * @param platform
	 * @param matching
	 * @return
	 */
	Exchanged match(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform, ExchangeOffer... matching);

	/**
	 * Default strategy is to pass all functionality directly to platform specific implementation
	 */
	interface Strategy {
		default Market validate(Market market, UniversalExchange platform)
				throws InvalidParameterException {
			return platform.validate(market, platform);
		}

		default Market open(Market market, UniversalExchange platform) {
			return platform.open(market, platform);
		}

		default boolean close(Market market, UniversalExchange platform) {
			return platform.close(market, platform);
		}

		default ExchangeOffer validate(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform)
				throws IllegalStateException {
			return platform.validate(exchangeOffer, market, platform);
		}

		default ExchangeOffer accept(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform) {
			return platform.accept(exchangeOffer, market, platform);
		}

		default Exchanged match(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform,
								ExchangeOffer... matching) {
			return platform.match(exchangeOffer, market, platform, matching);
		}

		default ExchangeOffer process(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform) {
			return process(exchangeOffer, market, platform,
					getMatching(exchangeOffer, market, platform).toArray(new ExchangeOffer[1]));
		}

		default ExchangeOffer process(ExchangeOffer exchangeOffer, Market market, UniversalExchange platform,
									  ExchangeOffer... matching) {
			return platform.process(exchangeOffer, market, platform, matching);
		}

		default Exchanged postProcess(ExchangeOffer exchangeOffer, UniversalExchange platform, ExchangeOffer...
				matching) {
			return platform.postProcess((ExchangeOffer) exchangeOffer.postProcess(), platform, matching);
		}

		default Exchanged finalise(ExchangeOffer exchangeOffer, UniversalExchange platform, ExchangeOffer...
				matching) {
			return platform.finalise((ExchangeOffer) exchangeOffer.finalise(), platform, matching);
		}

		default Collection<? extends ExchangeOffer> getMatching(ExchangeOffer exchangeOffer, Market market,
																UniversalExchange platform) {
			return platform.getMatching(exchangeOffer, market, platform);
		}
	}
}
