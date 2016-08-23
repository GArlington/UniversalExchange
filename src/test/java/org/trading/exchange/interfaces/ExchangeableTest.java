package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.math.SimpleDecimal;
import org.trading.exchange.interfaces.mocks.ExchangeableMock;
import org.trading.exchange.publicInterfaces.Commodity;

import static org.junit.Assert.assertEquals;

/**
 * Created by GArlington.
 */
public class ExchangeableTest {
    Commodity fromCommodity = CommodityImpl.SILVER;
    Commodity fromCommodity2 = CommodityImpl.GOLD;
    long fromValue = 3;
    Commodity toCommodity = CommodityImpl.USD;
    long toValue = 722;
    Commodity toCommodity2 = CommodityImpl.GBP;

    ExchangeableImpl victim;

    boolean debugOutput = false;

    @Before
    public void setUp() throws Exception {
        victim = new ExchangeableMock(fromCommodity, fromValue, toCommodity, toValue);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void initialise() throws Exception {
        victim.initialise();
        assertEquals(ExchangeableImpl.ExchangeableState.INITIALISED, victim.getExchangeableState());
    }

    @Test
    public void preProcess() throws Exception {
        victim.preProcess();
        assertEquals(ExchangeableImpl.ExchangeableState.PRE_PROCESSED, victim.getExchangeableState());
    }

    @Test
    public void process() throws Exception {
        victim.process();
        assertEquals(ExchangeableImpl.ExchangeableState.PROCESSED, victim.getExchangeableState());
    }

    @Test
    public void postProcess() throws Exception {
        victim.postProcess();
        assertEquals(ExchangeableImpl.ExchangeableState.POST_PROCESSED, victim.getExchangeableState());
    }

    @Test
    public void finalise() throws Exception {
        victim.finalise();
        assertEquals(ExchangeableImpl.ExchangeableState.FINALISED, victim.getExchangeableState());
    }

    @Test
    public void validate() throws Exception {
        victim.validate();
        assertEquals(ExchangeableImpl.ExchangeableState.VALIDATED, victim.getExchangeableState());
    }

    @Test
    public void testIsFullyMatchedAsRequested() throws Exception {
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(),
                victim.getOffered(), victim.getOfferedValue());
        boolean result = victim.isFullyMatched(exchangeable);
        assertEquals(victim + " is not matched by " + exchangeable, true, result);
        if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
    }

    @Test
    public void testIsFullyMatchedAtHigherQuantity() throws Exception {
        long extraValue = 12L;
        SimpleDecimal sd = victim.getExchangeRate().multiply(new SimpleDecimal(extraValue));
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(),
                victim.getRequiredValue() + sd.longValue(),
                victim.getOffered(), victim.getOfferedValue() + extraValue);
        boolean result = victim.isFullyMatched(exchangeable);
        assertEquals(victim + " is not matched by " + exchangeable, true, result);
        if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
    }

    @Test
    public void testIsFullyMatchedAtBetterExchangeRate() throws Exception {
        long extraValue = 15;
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue() + extraValue,
                victim.getOffered(), victim.getOfferedValue());
        boolean result = victim.isFullyMatched(exchangeable);
        assertEquals(victim + " is not matched by " + exchangeable, true, result);
        if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
    }

    @Test
    public void testIsFullyMatchedAtBetterExchangeRateAndHigherQuantity() throws Exception {
        long extraValue = 9;
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(),
                victim.getRequiredValue()
                        + victim.getExchangeRate().multiply(new SimpleDecimal(extraValue)).longValue(),
                victim.getOffered(), victim.getOfferedValue() + extraValue);
        boolean result = victim.isFullyMatched(exchangeable);
        assertEquals(victim + " is not matched by " + exchangeable, true, result);
        if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
    }

    @Test
    public void testIsNotFullyMatchedAtWorseExchangeRate() throws Exception {
        long extraValue = 4;
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue() - extraValue,
                victim.getOffered(), victim.getOfferedValue());
        boolean result = victim.isFullyMatched(exchangeable);
        assertEquals(victim + " is matched by " + exchangeable, false, result);
        if (debugOutput) System.out.println(victim + " is not matched by " + exchangeable);
    }

    @Test
    public void testIsNotFullyMatchedAtLowerQuantity() throws Exception {
        long extraValue = 11;
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue() - extraValue,
                victim.getOffered(), victim.getOfferedValue());
        boolean result = victim.isFullyMatched(exchangeable);
        assertEquals(victim + " is matched by " + exchangeable, false, result);
        if (debugOutput) System.out.println(victim + " is not matched by " + exchangeable);
    }

    @Test
    public void testIsPartiallyMatched() throws Exception {
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(),
                victim.getOffered(), victim.getOfferedValue());
        boolean result = victim.isPartiallyMatched(exchangeable);
        assertEquals(victim + " is not PartiallyMatched by " + exchangeable, true, result);
        if (debugOutput) System.out.println(victim + " is PartiallyMatched by " + exchangeable);
    }

    @Test
    public void testIsPartiallyMatched2() throws Exception {
        long extraValue = 1;
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(),
                victim.getOffered(), victim.getOfferedValue() - extraValue);
        boolean result = victim.isPartiallyMatched(exchangeable);
        assertEquals(victim + " is not PartiallyMatched by " + exchangeable, true, result);
        if (debugOutput) System.out.println(victim + " is PartiallyMatched by " + exchangeable);
    }

    @Test
    public void testIsNotPartiallyMatched() throws Exception {
        ExchangeableImpl exchangeable = new ExchangeableMock(toCommodity2, victim.getRequiredValue(),
                victim.getOffered(), victim.getOfferedValue());
        boolean result = victim.isPartiallyMatched(exchangeable);
        assertEquals(victim + " is PartiallyMatched by " + exchangeable, false, result);
        if (debugOutput) System.out.println(victim + " is not PartiallyMatched by " + exchangeable);
    }

    @Test
    public void testIsNotPartiallyMatched2() throws Exception {
        ExchangeableImpl exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(),
                fromCommodity2, victim.getOfferedValue());
        boolean result = victim.isPartiallyMatched(exchangeable);
        assertEquals(victim + " is PartiallyMatched by " + exchangeable, false, result);
        if (debugOutput) System.out.println(victim + " is not PartiallyMatched by " + exchangeable);
    }
}