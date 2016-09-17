package org.trading.exchange.interfaces;

import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Exchanged;
import org.trading.exchange.publicInterfaces.Market;

import java.security.InvalidParameterException;
import java.util.Collection;

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
	default Exchangeable validate(Exchangeable exchangeable) throws InvalidParameterException {
		return getStrategy().validate(exchangeable, getPlatform());
	}

	@Override
	default Exchangeable accept(Exchangeable exchangeable) throws InvalidParameterException {
		validate(exchangeable);
		return getStrategy().accept((Exchangeable) exchangeable.preProcess(), getPlatform());
	}

	@Override
	default Collection<? extends Exchangeable> getMatching(Exchangeable exchangeable) {
		return getStrategy().getMatching(exchangeable, getPlatform());
	}

	@Override
	default Exchanged match(Exchangeable exchangeable, Exchangeable... matchingOrders) {
		return getStrategy().match(exchangeable, getPlatform(), matchingOrders);
	}

	Strategy getStrategy();

	UniversalExchange getPlatform();

	/**
	 * Process Exchangeable
	 *
	 * @param exchangeable
	 * @return processed Exchangeable
	 */
/*
	default Exchangeable process(Exchangeable exchangeable) {
		return getStrategy().process((Exchangeable) exchangeable.process(), getPlatform());
	}
*/

	/**
	 * PostProcess Exchangeable
	 *
	 * @param exchangeable
	 * @return post-processed Exchangeable
	 */
/*
	default Exchanged postProcess(Exchangeable exchangeable) {
		return getStrategy().postProcess((Exchangeable) exchangeable.postProcess(), getPlatform());
	}
*/

	/**
	 * Finalise Exchangeable
	 *
	 * @param exchangeable
	 * @return finalised Exchangeable
	 */
/*
	default Exchanged finalise(Exchangeable exchangeable) {
		return getStrategy().finalise((Exchangeable) exchangeable.finalise(), getPlatform());
	}
*/


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
	boolean close(Market market, UniversalExchange platform);

	/**
	 * Validate Exchangeable implementation
	 *
	 * @param exchangeable
	 * @param platform
	 * @return
	 * @throws IllegalStateException
	 */
	Exchangeable validate(Exchangeable exchangeable, UniversalExchange platform) throws IllegalStateException;

	/**
	 * Accept Exchangeable implementation
	 *
	 * @param exchangeable
	 * @param platform
	 * @return
	 * @throws IllegalStateException
	 */
	Exchangeable accept(Exchangeable exchangeable, UniversalExchange platform) throws IllegalStateException;

	/**
	 * Process Exchangeable implementation
	 *
	 * @param exchangeable
	 * @param platform
	 * @param matching
	 * @return
	 */
	Exchangeable process(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching);

	/**
	 * PostProcess Exchangeable implementation
	 *
	 * @param exchangeable
	 * @param platform
	 * @param matching
	 * @return
	 */
	Exchanged postProcess(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching);

	/**
	 * Finalise Exchangeable implementation
	 *
	 * @param exchangeable
	 * @param platform
	 * @param matching
	 * @return
	 */
	Exchanged finalise(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching);

	/**
	 * Get matching orders implementation
	 *
	 * @param exchangeable
	 * @param platform
	 * @return
	 */
	Collection<? extends Exchangeable> getMatching(Exchangeable exchangeable, UniversalExchange platform);


	/**
	 * Process Exchangeable with matched orders implementation
	 *
	 * @param exchangeable
	 * @param platform
	 * @param matching
	 * @return
	 */
	Exchanged match(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching);

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

		default Exchangeable validate(Exchangeable exchangeable, UniversalExchange platform)
				throws IllegalStateException {
			return platform.validate(exchangeable, platform);
		}

		default Exchangeable accept(Exchangeable exchangeable, UniversalExchange platform) {
			return platform.accept(exchangeable, platform);
		}

		default Exchanged match(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching) {
			return platform.match(exchangeable, platform, matching);
		}

		default Exchangeable process(Exchangeable exchangeable, UniversalExchange platform) {
			return process(exchangeable, platform, getMatching(exchangeable, platform).toArray(new Exchangeable[1]));
		}

		default Exchangeable process(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching) {
			return platform.process(exchangeable, platform, matching);
		}

		default Exchanged postProcess(Exchangeable exchangeable, UniversalExchange platform, Exchangeable...
				matching) {
			return platform.postProcess((Exchangeable) exchangeable.postProcess(), platform, matching);
		}

		default Exchanged finalise(Exchangeable exchangeable, UniversalExchange platform, Exchangeable... matching) {
			return platform.finalise((Exchangeable) exchangeable.finalise(), platform, matching);
		}

		default Collection<? extends Exchangeable> getMatching(Exchangeable exchangeable, UniversalExchange platform) {
			return platform.getMatching(exchangeable, platform);
		}
	}
}
