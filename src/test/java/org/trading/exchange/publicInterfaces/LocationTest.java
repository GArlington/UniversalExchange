package org.trading.exchange.publicInterfaces;

import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.Commodity;
import org.trading.exchange.interfaces.Location;

import static org.junit.Assert.assertEquals;

/**
 * Created by GArlington.
 */
public class LocationTest {
    org.trading.exchange.publicInterfaces.Location victim;

    @Before
    public void setup() {
        victim = Location.LONDON;
    }

    @Test
    public void checkCommodity() throws Exception {
        assertEquals(true, victim.checkCommodity(Commodity.GOLD));
        assertEquals(true, victim.checkCommodity(Commodity.SILVER));
    }

    @Test
    public void checkCommodityFail() throws Exception {
        assertEquals(false, victim.checkCommodity(Commodity.COFFEE));
    }
}