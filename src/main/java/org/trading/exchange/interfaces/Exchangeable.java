package org.trading.exchange.interfaces;

import org.math.SimpleDecimal;
import org.processing.PostProcessable;
import org.processing.PreProcessable;
import org.processing.Processable;

import java.io.Serializable;

/**
 * Created by GArlington.
 */
public interface Exchangeable extends org.trading.exchange.publicInterfaces.Exchangeable {
	default SimpleDecimal getExchangeRate() {
		return new SimpleDecimal(getRequiredValue()).incFractionalPrecision(5)
				.divide(new SimpleDecimal(getOfferedValue()));
	}

	default SimpleDecimal getInverseExchangeRate() {
		return new SimpleDecimal(getOfferedValue()).incFractionalPrecision(5)
				.divide(new SimpleDecimal(getRequiredValue()));
	}

	default boolean isFullyMatched(Exchangeable exchangeableFor) {
		return (isPartiallyMatched(exchangeableFor)
				&& getOfferedValue() <= exchangeableFor.getRequiredValue()
				&& getRequiredValue() <= exchangeableFor.getOfferedValue()
		);
	}

	default boolean isPartiallyMatched(Exchangeable exchangeableFor) {
		return (getOffered().equals(exchangeableFor.getRequired())
				&& getRequired().equals(exchangeableFor.getOffered())
				&& getExchangeRate().compareTo(exchangeableFor.getInverseExchangeRate()) <= 0
		);
	}

/*
	default ExchangeableAction getMatchingAction() {
        return getAction().getMatchingAction();
    }
*/

	default Processable initialise() {
		setExchangeableState(Exchangeable.State.INITIALISED);
		return org.trading.exchange.publicInterfaces.Exchangeable.super.initialise();
	}

	default Processable process() {
		setExchangeableState(Exchangeable.State.PROCESSED);
		return org.trading.exchange.publicInterfaces.Exchangeable.super.process();
	}

	default Processable finalise() {
		setExchangeableState(Exchangeable.State.FINALISED);
		return org.trading.exchange.publicInterfaces.Exchangeable.super.finalise();
	}

	default PreProcessable preProcess() {
		setExchangeableState(Exchangeable.State.PRE_PROCESSED);
		return org.trading.exchange.publicInterfaces.Exchangeable.super.preProcess();
	}

	default PostProcessable postProcess() {
		setExchangeableState(Exchangeable.State.POST_PROCESSED);
		return org.trading.exchange.publicInterfaces.Exchangeable.super.postProcess();
	}

	default Exchangeable validate() throws IllegalStateException {
		setExchangeableState(Exchangeable.State.VALIDATED);
		return this;
	}

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
}
