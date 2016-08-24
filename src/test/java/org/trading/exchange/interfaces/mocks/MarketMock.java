package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.Market;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Location;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by GArlington.
 */
public class MarketMock implements Market {
    private String id;
    private Location location;
    private String name;
    private Commodity offered;
    private Commodity required;
    private Collection<Exchangeable> orders;

    public MarketMock(String id, Location location, String name, Commodity offered, Commodity required,
                      Exchangeable... orders) {
        this.id = id;
        this.location = location;
        this.name = name;
        this.offered = offered;
        this.required = required;
        this.orders = Arrays.asList(orders);
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
    public Commodity getOffered() {
        return offered;
    }

    @Override
    public Commodity getRequired() {
        return required;
    }

    @Override
    public Collection<Exchangeable> getOrders() {
        return orders;
    }

    @Override
    public String toString() {
        return "MarketMock{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", \noffered=" + offered +
                ", \nrequired=" + required +
                ", \nlocation=" + location +
                ", \norders=\n" + orders +
                '}' + '\n';
    }
}
