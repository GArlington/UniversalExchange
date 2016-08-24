package org.trading.exchange.interfaces.mocks;

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
    private ExchangeableState exchangeableState;
    private ProcessableState processState;

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

    public long getOriginalOfferedValue() {
        return originalOfferedValue;
    }

    public long getMatchedOfferedValue() {
        return matchedOfferedValue;
    }

    @Override
    public Commodity getRequired() {
        return required;
    }

    @Override
    public long getRequiredValue() {
        return requiredValue;
    }

    public long getOriginalRequiredValue() {
        return originalRequiredValue;
    }

    public long getMatchedRequiredValue() {
        return matchedRequiredValue;
    }

    @Override
    public void setExchangeableState(ExchangeableState state) {
        this.exchangeableState = state;
    }

    @Override
    public ExchangeableState getExchangeableState() {
        return exchangeableState;
    }

    @Override
    public void setProcessState(ProcessableState state) {
        this.processState = state;
    }

    @Override
    public ProcessableState getProcessState() {
        return processState;
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
