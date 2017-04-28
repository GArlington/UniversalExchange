package org.trading.exchange.interfaces;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.math.SimpleDecimal;
import org.trading.exchange.interfaces.mocks.ExchangeOfferMock;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.Owner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by GArlington.
 */
public class ExchangeOfferTest {
	private Commodity offered;
	private Commodity required;
	private Owner owner;

	private ExchangeOffer victim;

	@Before
	public void setUp() throws Exception {
		offered = mock(Commodity.class);
		long offeredValue = 2L;
		long requiredValue = 4L;

		required = mock(Commodity.class);
		doReturn(0).when(offered).compareTo(offered);
		doReturn(0).when(required).compareTo(required);
		doReturn(1).when(offered).compareTo(required);
		doReturn(-1).when(required).compareTo(offered);
		owner = mock(Owner.class);
		doReturn("thisId").when(owner).getId();
		doReturn(true).when(owner).equals(owner);
		victim = ExchangeOfferMock.getBuilder()
				.setOffered(offered).setOfferedValue(offeredValue)
				.setRequired(required).setRequiredValue(requiredValue).setOwner(owner).build();
		assertEquals(new SimpleDecimal(((double) requiredValue / offeredValue)), victim.getExchangeRate());
		assertEquals(new SimpleDecimal((double) offeredValue / requiredValue), victim.getInverseExchangeRate());
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void isFullyMatched() throws Exception {
		assertEquals(false, victim.isFullyMatched());
	}

/*
	@Test
	public void isNotPartiallyMatchedByExchangeOffer() throws Exception {
		ExchangeOffer existingOffer = createPartiallyMatchingOffer(victim);

		assertEquals(victim + " is not partially matched by " + existingOffer,
				true, victim.isPartiallyMatched(existingOffer));
		assertEquals(victim + " is fully matched by " + existingOffer,
				false, victim.isFullyMatched(existingOffer));
	}
*/

	@Test
	public void isPartiallyMatchedByExchangeOffer() throws Exception {
		ExchangeOffer existingOffer = createPartiallyMatchingOffer(victim);

		assertEquals(victim + " is not partially matched by " + existingOffer,
				true, victim.isPartiallyMatched(existingOffer));
		assertEquals(victim + " is fully matched by " + existingOffer,
				false, victim.isFullyMatched(existingOffer));
	}

	@Test
	public void isFullyMatchedByExchangeOffer() throws Exception {
		ExchangeOffer existingOffer = createFullyMatchingOffer(victim);

		assertEquals(victim + " is not fully matched by " + existingOffer,
				true, victim.isFullyMatched(existingOffer));
		assertEquals(existingOffer + " is not fully matched by " + victim,
				true, existingOffer.isFullyMatched(victim));
	}

	@Test
	public void matchPartially() throws Exception {
		ExchangeOffer existingOffer = createPartiallyMatchingOffer(victim);

		assertEquals(victim + " is not partially matched by " + existingOffer,
				true, victim.isPartiallyMatched(existingOffer));

		assertEquals(existingOffer, victim.match(existingOffer));
		assertEquals(existingOffer + " is not fully matched by " + victim,
				true, existingOffer.isFullyMatched());
		assertEquals(victim + " is fully matched by " + existingOffer,
				false, victim.isFullyMatched());
	}

	@Test
	public void matchFully() throws Exception {
		ExchangeOffer existingOffer = createFullyMatchingOffer(victim);

		assertEquals(victim + " is not fully matched by " + existingOffer,
				true, victim.isFullyMatched(existingOffer));

		assertEquals(existingOffer, victim.match(existingOffer));
		assertEquals(existingOffer + " is not fully matched by " + victim,
				true, existingOffer.isFullyMatched());
		assertEquals(victim + " is not fully matched by " + existingOffer,
				true, victim.isFullyMatched());
	}

	@Test
	public void matchFullyWithExistingExcessiveOffer() throws Exception {
		ExchangeOffer existingOffer = createExcessiveMatchingOffer(victim);

		assertEquals(victim + " is not fully matched by " + existingOffer,
				true, victim.isFullyMatched(existingOffer));

		assertEquals(existingOffer, victim.match(existingOffer));
		assertEquals(existingOffer + " is fully matched by " + victim,
				false, existingOffer.isFullyMatched());
		assertEquals(victim + " is not fully matched by " + existingOffer,
				true, victim.isFullyMatched());
	}

	private ExchangeOffer createPartiallyMatchingOffer(ExchangeOffer victim) {
		ExchangeOffer existingOffer = ExchangeOfferMock.getBuilder()
				.setOffered(victim.getRequired()).setOfferedValue(victim.getRequiredValue())
				.setRequired(victim.getOffered()).setRequiredValue(victim.getOfferedValue() - 1)
				.setOwner(owner).build();
		existingOffer.setState(ExchangeOffer.State.OPEN);
		return existingOffer;
	}

	private ExchangeOffer createFullyMatchingOffer(ExchangeOffer victim) {
		ExchangeOffer existingOffer = ExchangeOfferMock.getBuilder()
				.setOffered(victim.getRequired()).setOfferedValue(victim.getRequiredValue())
				.setRequired(victim.getOffered()).setRequiredValue(victim.getOfferedValue())
				.setOwner(owner).build();
		existingOffer.setState(ExchangeOffer.State.OPEN);
		return existingOffer;
	}

	private ExchangeOffer createExcessiveMatchingOffer(ExchangeOffer victim) {
		ExchangeOffer existingOffer = ExchangeOfferMock.getBuilder()
				.setOffered(victim.getRequired()).setOfferedValue(victim.getRequiredValue() + 2)
				.setRequired(victim.getOffered()).setRequiredValue(victim.getOfferedValue() + 1)
				.setOwner(owner).build();
		existingOffer.setState(ExchangeOffer.State.OPEN);
		return existingOffer;
	}

	@Test
	public void processAndFinalise() throws Exception {
		ExchangeOffer existingOffer = createFullyMatchingOffer(victim);
		ExchangeOffer.State state;

		state = victim.processAndFinalise(existingOffer, victim.getRequiredValue(), victim.getOfferedValue())
				.getState();
		assertEquals(ExchangeOffer.State.FINALISED, state);

		state = victim.processAndFinalise(victim, victim.getOfferedValue(), victim.getRequiredValue()).getState();
		assertEquals(ExchangeOffer.State.FINALISED, state);
	}

	@Test
	public void initialise() throws Exception {
		assertEquals(ExchangeOffer.State.INITIALISED, ((ExchangeOffer) victim.initialise()).getState());
	}

	@Test
	public void process() throws Exception {
		assertEquals(ExchangeOffer.State.PROCESSED, ((ExchangeOffer) victim.process()).getState());
	}

	@Test
	public void finalise() throws Exception {
		assertEquals(ExchangeOffer.State.FINALISED, ((ExchangeOffer) victim.finalise()).getState());
	}

	@Test
	public void preProcess() throws Exception {
		assertEquals(ExchangeOffer.State.PRE_PROCESSED, ((ExchangeOffer) victim.preProcess()).getState());
	}

	@Test
	public void postProcess() throws Exception {
		assertEquals(ExchangeOffer.State.POST_PROCESSED, ((ExchangeOffer) victim.postProcess()).getState());
	}

	@Test
	public void open() throws Exception {
		assertEquals(ExchangeOffer.State.OPEN, victim.open().getState());
	}

	@Test
	public void dealt() throws Exception {
		assertEquals(ExchangeOffer.State.DEALT, victim.dealt().getState());
	}

}