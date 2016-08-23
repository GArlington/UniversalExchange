package org.trading.exchange.publicInterfaces;

import org.junit.Before;
import org.junit.Test;
import org.trading.exchange.interfaces.CommodityImpl;
import org.trading.exchange.interfaces.LocationImpl;

import static org.junit.Assert.assertEquals;

/**
 * Created by GArlington on 20/08/2016.
 */
public class LocationTest {
    Location victim;

    @Before
    public void setup() {
        victim = LocationImpl.LONDON;
    }

    @Test
    public void checkCommodity() throws Exception {
        assertEquals(true, victim.checkCommodity(CommodityImpl.GOLD));
        assertEquals(true, victim.checkCommodity(CommodityImpl.SILVER));
    }

    @Test
    public void checkCommodityFail() throws Exception {
        assertEquals(false, victim.checkCommodity(CommodityImpl.COFFEE));
    }
}