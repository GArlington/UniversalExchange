package org.trading.exchange.interfaces.mocks;

import org.processing.Processable;
import org.trading.exchange.interfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Commodity;

/**
 * Created by GArlington.
 */
public class ExchangeableMock implements Exchangeable {
	private Commodity offered;
	private long offeredValue;
	private long originalOfferedValue;
	private long matchedOfferedValue;
	private Commodity required;
	private long requiredValue;
	private long originalRequiredValue;
	private long matchedRequiredValue;
	private Exchangeable.State exchangeableState;
	private Processable.State processState;

	public ExchangeableMock(Commodity offered, long offeredValue, Commodity required, long requiredValue) {
		this.offered = offered;
		this.originalOfferedValue = this.offeredValue = offeredValue;
		this.required = required;
		this.originalRequiredValue = this.requiredValue = requiredValue;
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
	public Exchangeable.State getExchangeableState() {
		return exchangeableState;
	}

	@Override
	public void setExchangeableState(Exchangeable.State state) {
		this.exchangeableState = state;
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
		return "ExchangeableMock{" + "offered=" + offered +
				", offeredValue=" + offeredValue +
				", originalOfferedValue=" + originalOfferedValue +
				", matchedOfferedValue=" + matchedOfferedValue +
				", required=" + required +
				", requiredValue=" + requiredValue +
				", originalRequiredValue=" + originalRequiredValue +
				", matchedRequiredValue=" + matchedRequiredValue +
				", exchangeableState=" + exchangeableState +
				", processState=" + processState +
				'}';
	}
}
