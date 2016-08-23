package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.CommodityImpl;
import org.trading.exchange.interfaces.MarketImpl;
import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Location;

import java.util.Collection;

/**
 * Created by GArlington on 19/08/2016.
 */
public class MarketMock implements MarketImpl {
    private String id;
    private Location location;
    private String name;
    private CommodityImpl offered;
    private CommodityImpl required;
    private Collection<Exchangeable> orders;

    public MarketMock(String id, Location location, String name, CommodityImpl offered, CommodityImpl required, Collection<Exchangeable> orders) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.offered = offered;
        this.required = required;
        this.orders = orders;
        validateMarket();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CommodityImpl getOffered() {
        return offered;
    }

    @Override
    public CommodityImpl getRequired() {
        return required;
    }

    @Override
    public Collection<Exchangeable> getOrders() {
        return orders;
    }

/*
    @Override
    public String toString() {
        return "MarketMock{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", offered=" + offered +
                ", required=" + required +
                ", location=" + location +
                ", orders=" + orders +
                '}' + '\n';
    }
*/
}
