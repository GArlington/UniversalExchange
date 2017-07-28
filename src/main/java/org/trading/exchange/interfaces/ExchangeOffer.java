package org.trading.exchange.interfaces;

import org.math.SimpleDecimal;
import org.processing.PostProcessable;
import org.processing.PreProcessable;
import org.processing.Processable;

/**
 * Created by GArlington.
 */
public interface ExchangeOffer extends org.trading.exchange.publicInterfaces.ExchangeOffer {
	Object getLock();

	/**
	 * Match offered value
	 *
	 * @param offeredValue
	 */
	void matchOfferedValue(long offeredValue);

	/**
	 * Match required value
	 *
	 * @param requiredValue
	 */
	void matchRequiredValue(long requiredValue);

	@Override
	SimpleDecimal getExchangeRate();

	@Override
	SimpleDecimal getInverseExchangeRate();

	@Override
	default boolean isFullyMatched() {
		return getOfferedValue() <= 0;
	}

	@Override
	default ExchangeOffer match(org.trading.exchange.publicInterfaces.ExchangeOffer exchangeOffer) {
		return match((ExchangeOffer) exchangeOffer);
	}

	/**
	 * Check if this ExchangeOffer is fully matched (satisfied) by the ExchangeOffer passed as parameter
	 *
	 * @param exchangeOffer
	 */
	default boolean isFullyMatched(ExchangeOffer exchangeOffer) {
		return (isPartiallyMatched(exchangeOffer) && getOfferedValue() <= exchangeOffer.getRequiredValue());
	}

	/**
	 * Check if this ExchangeOffer is partially matched by the ExchangeOffer passed as parameter
	 *
	 * @param exchangeOffer
	 */
	default boolean isPartiallyMatched(ExchangeOffer exchangeOffer) {
		return (!State.OPEN.precedes(exchangeOffer.getState())
				&& getOffered().equals(exchangeOffer.getRequired()) && getRequired().equals(exchangeOffer.getOffered())
				&& getExchangeRate().compareTo(exchangeOffer.getInverseExchangeRate()) <= 0
		);
	}

	/**
	 * Process this ExchangeOffer and matched ExchangeOffer passed as parameter
	 *
	 * This method will change both this ExchangeOffer and ExchangeOffer passed as parameter if they match
	 *
	 * @param exchangeOffer
	 * @return processed ExchangeOffer that was passed as parameter
	 */
	default ExchangeOffer match(ExchangeOffer exchangeOffer) {
		if (isPartiallyMatched(exchangeOffer)) {
			if (!State.OPEN.precedes(getState())) {
				synchronized (getLock()) {
					if (!State.OPEN.precedes(getState())) {
						if (!State.OPEN.precedes(exchangeOffer.getState())) {
							synchronized (exchangeOffer.getLock()) {
								if (!State.OPEN.precedes(exchangeOffer.getState())) {
									long oValue = Math.min(getOfferedValue(), exchangeOffer.getRequiredValue());
									long rValue = exchangeOffer.getInverseExchangeRate()
											.multiply(new SimpleDecimal(oValue)).longValue(true);
									processAndFinalise(this, oValue, rValue);
									return processAndFinalise(exchangeOffer, rValue, oValue);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	default ExchangeOffer processAndFinalise(ExchangeOffer exchangeOffer, long oValue, long rValue) {
		exchangeOffer.matchOfferedValue(oValue);
		exchangeOffer.matchRequiredValue(rValue);
		if (exchangeOffer.isFullyMatched()) {
			exchangeOffer.process();
			exchangeOffer.dealt();
			exchangeOffer.postProcess();
			exchangeOffer.finalise();
		}
		return exchangeOffer;
	}

	default Processable initialise() {
		org.trading.exchange.publicInterfaces.ExchangeOffer.super.initialise();
		setState(State.INITIALISED);
		return this;
	}

	default Processable process() {
		org.trading.exchange.publicInterfaces.ExchangeOffer.super.process();
		setState(State.PROCESSED);
		return this;
	}

	default Processable finalise() {
		org.trading.exchange.publicInterfaces.ExchangeOffer.super.finalise();
		setState(State.FINALISED);
		return this;
	}

	default PreProcessable preProcess() {
		org.trading.exchange.publicInterfaces.ExchangeOffer.super.preProcess();
		setState(State.PRE_PROCESSED);
		return this;
	}

	default PostProcessable postProcess() {
		org.trading.exchange.publicInterfaces.ExchangeOffer.super.postProcess();
		setState(State.POST_PROCESSED);
		return this;
	}

	default ExchangeOffer open() {
		setState(State.OPEN);
		return this;
	}

	default ExchangeOffer dealt() {
		setState(State.DEALT);
		return this;
	}

/*

	enum Action implements Serializable {
		BID("Buy", "B"),
		OFFER("Sell", "S");

		private final String action;
		private final String code;

		Action(String action, String code) {
			this.action = action;
			this.code = code;
		}

		public String getAction() {
			return action;
		}

		public String getCode() {
			return code;
		}

		public Action getMatchingAction() {
			return this == BID ? OFFER : BID;
		}
	}
*/
}
