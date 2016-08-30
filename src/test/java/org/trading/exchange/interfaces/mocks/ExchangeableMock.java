package org.trading.exchange.interfaces.mocks;

import org.math.SimpleDecimal;
import org.processing.Processable;
import org.trading.exchange.interfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.Exchanged;

/**
 * Created by GArlington.
 */
public class ExchangeableMock implements Exchangeable {
	private final Commodity offered;
	private final long originalOfferedValue;
	private final Commodity required;
	private final long originalRequiredValue;
	private final SimpleDecimal exchangeRate;
	private final SimpleDecimal inverseExchangeRate;
	private long offeredValue;
	private long matchedOfferedValue;
	private long requiredValue;
	private long matchedRequiredValue;
	private Exchangeable.State exchangeableState = State.INITIALISED;
	private Processable.State processState = Processable.State.INITIALISED;
	private Exchanged exchanged;

	public ExchangeableMock(org.trading.exchange.publicInterfaces.Exchangeable obj) {
		this(obj.getOffered(), obj.getOfferedValue(), obj.getRequired(), obj.getRequiredValue());
	}

	public ExchangeableMock(Commodity offered, long offeredValue, Commodity required, long requiredValue) {
		this.offered = offered;
		this.originalOfferedValue = this.offeredValue = offeredValue;
		this.required = required;
		this.originalRequiredValue = this.requiredValue = requiredValue;
		this.exchangeRate =
				new SimpleDecimal(getOriginalRequiredValue()).incFractionalPrecision(getExchangeRatePrecision())
						.divide(new SimpleDecimal(getOriginalOfferedValue()));
		this.inverseExchangeRate =
				new SimpleDecimal(getOriginalOfferedValue()).incFractionalPrecision(getExchangeRatePrecision())
						.divide(new SimpleDecimal(getOriginalRequiredValue()));
		initialise();
	}

	@Override
	public Commodity getOffered() {
		return offered;
	}

	@Override
	public long getOfferedValue() {
		return offeredValue;
	}

	@Override
	public Commodity getRequired() {
		return required;
	}

	@Override
	public long getRequiredValue() {
		return requiredValue;
	}

	@Override
	public Exchanged getExchanged() {
		return exchanged;
	}

	@Override
	public void setExchanged(Exchanged exchanged) {
		this.exchanged = exchanged;
	}

	@Override
	public Exchangeable.State getExchangeableState() {
		return exchangeableState;
	}

	@Override
	public void setExchangeableState(Exchangeable.State state) {
		if (exchangeableState.precedes(state)) {
			this.exchangeableState = state;
		}
	}

	@Override
	public void matchOfferedValue(long matchedValue) {
		this.offeredValue -= matchedValue;
		this.matchedOfferedValue += matchedValue;
	}

	@Override
	public void matchRequiredValue(long matchedValue) {
		this.requiredValue -= matchedValue;
		this.matchedRequiredValue += matchedValue;
	}

	@Override
	public SimpleDecimal getExchangeRate() {
		return exchangeRate;
	}

	@Override
	public SimpleDecimal getInverseExchangeRate() {
		return inverseExchangeRate;
	}

	public long getOriginalOfferedValue() {
		return originalOfferedValue;
	}

	public long getMatchedOfferedValue() {
		return matchedOfferedValue;
	}

	public long getOriginalRequiredValue() {
		return originalRequiredValue;
	}

	public long getMatchedRequiredValue() {
		return matchedRequiredValue;
	}

	@Override
	public Processable.State getProcessState() {
		return processState;
	}

	@Override
	public void setProcessState(Processable.State state) {
		this.processState = state;
	}

	@Override
	public String toString() {
		return "ExchangeableMock{" +
				"exchangeableState=" + exchangeableState +
				", processState=" + processState +
				", offered=" + offered +
				", offeredValue=" + offeredValue +
				", exchangeRate=" + getExchangeRate() +
				", originalOfferedValue=" + originalOfferedValue +
				", matchedOfferedValue=" + matchedOfferedValue +
				", required=" + required +
				", requiredValue=" + requiredValue +
				", inverseExchangeRate=" + getInverseExchangeRate() +
				", originalRequiredValue=" + originalRequiredValue +
				", matchedRequiredValue=" + matchedRequiredValue +
				'}' + '\n';
	}
}
