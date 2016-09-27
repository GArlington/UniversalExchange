package org.trading.exchange.interfaces;

import org.math.SimpleDecimal;
import org.processing.PostProcessable;
import org.processing.PreProcessable;
import org.processing.Processable;

/**
 * Created by GArlington.
 */
public interface Exchangeable extends org.trading.exchange.publicInterfaces.Exchangeable {
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

	/**
	 * Get exchange rate
	 *
	 * @return
	 */
	SimpleDecimal getExchangeRate();

	/**
	 * Get inverse exchange rate
	 *
	 * @return
	 */
	SimpleDecimal getInverseExchangeRate();

	@Override
	default Exchangeable validate() throws IllegalStateException {
		org.trading.exchange.publicInterfaces.Exchangeable.validate(this);
		return this;
	}

	@Override
	default boolean isFullyMatched() {
		return (getOfferedValue() <= 0 || getRequiredValue() <= 0);
	}

	@Override
	default Exchangeable match(org.trading.exchange.publicInterfaces.Exchangeable exchangeable) {
		return match((Exchangeable) exchangeable);
	}

	/**
	 * Check if this Exchangeable is fully matched (satisfied) by the Exchangeable passed as parameter
	 *
	 * @param exchangeable
	 * @return
	 */
	default boolean isFullyMatched(Exchangeable exchangeable) {
		return (isPartiallyMatched(exchangeable) && getOfferedValue() <= exchangeable.getRequiredValue()
				// TODO - this check may be redundant
				&& getRequiredValue() <= exchangeable.getOfferedValue()
		);
	}

	/**
	 * Check if this Exchangeable is partially matched by the Exchangeable passed as parameter
	 *
	 * @param exchangeable
	 * @return
	 */
	default boolean isPartiallyMatched(Exchangeable exchangeable) {
		return (!State.OPEN.precedes(exchangeable.getExchangeableState())
				&& getOffered().equals(exchangeable.getRequired()) && getRequired().equals(exchangeable.getOffered())
				&& getExchangeRate().compareTo(exchangeable.getInverseExchangeRate()) <= 0
		);
	}

	/**
	 * Process this Exchangeable and matched Exchangeable passed as parameter
	 *
	 * This method will change both this Exchangeable and Exchangeable passed as parameter if they match
	 *
	 * @param exchangeable
	 * @return processed Exchangeable that was passed as parameter
	 */
	default Exchangeable match(Exchangeable exchangeable) {
		if (isPartiallyMatched(exchangeable)) {
			if (!State.OPEN.precedes(getExchangeableState())) {
				synchronized (getLock()) {
					if (!State.OPEN.precedes(getExchangeableState())) {
						if (!State.OPEN.precedes(exchangeable.getExchangeableState())) {
							synchronized (exchangeable.getLock()) {
								if (!State.OPEN.precedes(exchangeable.getExchangeableState())) {
									long oValue = Math.min(getOfferedValue(), exchangeable.getRequiredValue());
									long rValue = exchangeable.getInverseExchangeRate()
											.multiply(new SimpleDecimal(oValue)).longValue(true);
									processAndFinaliseExchangeable(this, oValue, rValue);
									return processAndFinaliseExchangeable(exchangeable, rValue, oValue);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	default Exchangeable processAndFinaliseExchangeable(Exchangeable exchangeable, long oValue, long rValue) {
		exchangeable.matchOfferedValue(oValue);
		exchangeable.matchRequiredValue(rValue);
		if (exchangeable.isFullyMatched()) {
			exchangeable.process();
			exchangeable.dealt();
			exchangeable.postProcess();
			exchangeable.finalise();
		}
		return exchangeable;
	}

	default Processable initialise() {
		org.trading.exchange.publicInterfaces.Exchangeable.super.initialise();
		setExchangeableState(State.INITIALISED);
		return this;
	}

	default Processable process() {
		org.trading.exchange.publicInterfaces.Exchangeable.super.process();
		setExchangeableState(State.PROCESSED);
		return this;
	}

	default Processable finalise() {
		org.trading.exchange.publicInterfaces.Exchangeable.super.finalise();
		setExchangeableState(State.FINALISED);
		return this;
	}

	default PreProcessable preProcess() {
		org.trading.exchange.publicInterfaces.Exchangeable.super.preProcess();
		setExchangeableState(State.PRE_PROCESSED);
		return this;
	}

	default PostProcessable postProcess() {
		org.trading.exchange.publicInterfaces.Exchangeable.super.postProcess();
		setExchangeableState(State.POST_PROCESSED);
		return this;
	}

	default Exchangeable open() {
		setExchangeableState(State.OPEN);
		return this;
	}

	default Exchangeable dealt() {
		setExchangeableState(State.DEALT);
		return this;
	}

	default int getExchangeRatePrecision() {
		return 7;
	}
/*

	enum ExchangeableAction implements Serializable {
		BID("Buy", "B"),
		OFFER("Sell", "S");

		private final String action;
		private final String code;

		ExchangeableAction(String action, String code) {
			this.action = action;
			this.code = code;
		}

		public String getAction() {
			return action;
		}

		public String getCode() {
			return code;
		}

		public ExchangeableAction getMatchingAction() {
			return this == BID ? OFFER : BID;
		}
	}
*/
}
