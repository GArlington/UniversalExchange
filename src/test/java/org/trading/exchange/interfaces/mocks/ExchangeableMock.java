package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.ExchangeableImpl;
import org.trading.exchange.publicInterfaces.Commodity;

/**
 * Created by GArlington on 08/08/2016.
 */
public class ExchangeableMock implements ExchangeableImpl {
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
        final StringBuilder sb = new StringBuilder("ExchangeableMock{");
        sb.append("offered=").append(offered);
        sb.append(", offeredValue=").append(offeredValue);
        sb.append(", originalOfferedValue=").append(originalOfferedValue);
        sb.append(", matchedOfferedValue=").append(matchedOfferedValue);
        sb.append(", required=").append(required);
        sb.append(", requiredValue=").append(requiredValue);
        sb.append(", originalRequiredValue=").append(originalRequiredValue);
        sb.append(", matchedRequiredValue=").append(matchedRequiredValue);
        sb.append(", exchangeableState=").append(exchangeableState);
        sb.append(", processState=").append(processState);
        sb.append('}');
        return sb.toString();
    }
}
