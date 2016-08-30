package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.math.SimpleDecimal;
import org.trading.exchange.interfaces.mocks.ExchangeableMock;

import static org.junit.Assert.assertEquals;

/**
 * Created by GArlington.
 */
public class ExchangeableTest {
	org.trading.exchange.publicInterfaces.Commodity fromCommodity = Commodity.SILVER;
	org.trading.exchange.publicInterfaces.Commodity fromCommodity2 = Commodity.GOLD;
	long fromValue = 3;
	org.trading.exchange.publicInterfaces.Commodity toCommodity = Commodity.USD;
	long toValue = 722;
	org.trading.exchange.publicInterfaces.Commodity toCommodity2 = Commodity.GBP;

	Exchangeable victim;

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
		assertEquals(Exchangeable.State.INITIALISED, victim.getExchangeableState());
	}

	@Test
	public void preProcess() throws Exception {
		victim.preProcess();
		assertEquals(Exchangeable.State.PRE_PROCESSED, victim.getExchangeableState());
	}

	@Test
	public void process() throws Exception {
		victim.process();
		assertEquals(Exchangeable.State.PROCESSED, victim.getExchangeableState());
	}

	@Test
	public void postProcess() throws Exception {
		victim.postProcess();
		assertEquals(Exchangeable.State.POST_PROCESSED, victim.getExchangeableState());
	}

	@Test
	public void finalise() throws Exception {
		victim.finalise();
		assertEquals(Exchangeable.State.FINALISED, victim.getExchangeableState());
	}

	@Test
	public void validate() throws Exception {
		victim.validate();
		assertEquals(Exchangeable.State.VALIDATED, victim.getExchangeableState());
	}

	@Test
	public void testIsFullyMatchedAsRequested() throws Exception {
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(),
				victim.getOffered(), victim.getOfferedValue());
		boolean result = victim.isFullyMatched(exchangeable);
		assertEquals(victim + " is not matched by " + exchangeable, true, result);
		if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
	}

	@Test
	public void testIsFullyMatchedAtHigherQuantity() throws Exception {
		long extraValue = 12L;
		SimpleDecimal sd = victim.getExchangeRate().multiply(new SimpleDecimal(extraValue));
		long lv = sd.longValue(true);
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(),
				victim.getRequiredValue() + lv,
				victim.getOffered(), victim.getOfferedValue() + extraValue);
		boolean result = victim.isFullyMatched(exchangeable);
		assertEquals(victim + " is not matched by " + exchangeable, true, result);
		if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
	}

	@Test
	public void testIsFullyMatchedAtBetterExchangeRate() throws Exception {
		long extraValue = 15;
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue() + extraValue,
				victim.getOffered(), victim.getOfferedValue());
		boolean result = victim.isFullyMatched(exchangeable);
		assertEquals(victim + " is not matched by " + exchangeable, true, result);
		if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
	}

	@Test
	public void testIsFullyMatchedAtBetterExchangeRateAndHigherQuantity() throws Exception {
		long extraValue = 9;
		long lv = victim.getExchangeRate().multiply(new SimpleDecimal(extraValue)).longValue(true);
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue() + lv,
				victim.getOffered(), victim.getOfferedValue() + extraValue);
		boolean result = victim.isFullyMatched(exchangeable);
		assertEquals(victim + " is not matched by " + exchangeable, true, result);
		if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
	}

	@Test
	public void testIsNotFullyMatchedAtWorseExchangeRate() throws Exception {
		long extraValue = 4;
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue() - extraValue,
				victim.getOffered(), victim.getOfferedValue());
		boolean result = victim.isFullyMatched(exchangeable);
		assertEquals(victim + " is matched by " + exchangeable, false, result);
		if (debugOutput) System.out.println(victim + " is not matched by " + exchangeable);
	}

	@Test
	public void testIsNotFullyMatchedAtLowerQuantity() throws Exception {
		long extraValue = 11;
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue() - extraValue,
				victim.getOffered(), victim.getOfferedValue());
		boolean result = victim.isFullyMatched(exchangeable);
		assertEquals(victim + " is matched by " + exchangeable, false, result);
		if (debugOutput) System.out.println(victim + " is not matched by " + exchangeable);
	}

	@Test
	public void testIsPartiallyMatched() throws Exception {
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(),
				victim.getOffered(), victim.getOfferedValue());
		boolean result = victim.isPartiallyMatched(exchangeable);
		assertEquals(victim + " is not PartiallyMatched by " + exchangeable, true, result);
		if (debugOutput) System.out.println(victim + " is PartiallyMatched by " + exchangeable);
	}

	@Test
	public void testIsPartiallyMatched2() throws Exception {
		long extraValue = 1;
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(),
				victim.getOffered(), victim.getOfferedValue() - extraValue);
		boolean result = victim.isPartiallyMatched(exchangeable);
		assertEquals(victim + " is not PartiallyMatched by " + exchangeable, true, result);
		if (debugOutput) System.out.println(victim + " is PartiallyMatched by " + exchangeable);
	}

	@Test
	public void testIsNotPartiallyMatched() throws Exception {
		Exchangeable exchangeable = new ExchangeableMock(toCommodity2, victim.getRequiredValue(),
				victim.getOffered(), victim.getOfferedValue());
		boolean result = victim.isPartiallyMatched(exchangeable);
		assertEquals(victim + " is PartiallyMatched by " + exchangeable, false, result);
		if (debugOutput) System.out.println(victim + " is not PartiallyMatched by " + exchangeable);
	}

	@Test
	public void testIsNotPartiallyMatched2() throws Exception {
		Exchangeable exchangeable = new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(),
				fromCommodity2, victim.getOfferedValue());
		boolean result = victim.isPartiallyMatched(exchangeable);
		assertEquals(victim + " is PartiallyMatched by " + exchangeable, false, result);
		if (debugOutput) System.out.println(victim + " is not PartiallyMatched by " + exchangeable);
	}

	@Test
	public void match() throws Exception {
		org.trading.exchange.publicInterfaces.Exchangeable exchangeable =
				new ExchangeableMock(victim.getRequired(), victim.getRequiredValue(), victim.getOffered(),
						victim.getOfferedValue());
		org.trading.exchange.publicInterfaces.Exchangeable expected = exchangeable;
		org.trading.exchange.publicInterfaces.Exchangeable result = victim.match(exchangeable);
		assertEquals(victim + " is not matched by " + exchangeable, expected, result);
		if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
	}

	@Test
	public void matchDoesNotMatch() throws Exception {
		Exchangeable exchangeable = new ExchangeableMock(toCommodity2, victim.getRequiredValue(),
				victim.getOffered(), victim.getOfferedValue());
		Exchangeable expected = null;
		org.trading.exchange.publicInterfaces.Exchangeable result = victim.match(exchangeable);
		assertEquals(victim + " is matched by " + exchangeable, expected, result);
		if (debugOutput) System.out.println(victim + " is matched by " + exchangeable);
	}
}