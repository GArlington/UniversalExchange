package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.Market;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.Exchangeable;
import org.trading.exchange.publicInterfaces.Location;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by GArlington.
 */
public class MarketMock implements Market {
	private final Collection<? extends Exchangeable> orders = new LinkedList<>();
	private String id;
	private Location location;
	private String name;
	private Commodity offered;
	private Commodity required;

	public MarketMock(String id, Location location, String name, Commodity offered, Commodity required,
					  Exchangeable... orders) {
		this.id = id;
		this.location = location;
		this.name = name;
		this.offered = offered;
		this.required = required;
		for (Exchangeable exchangeable : orders) {
			accept(exchangeable);
		}
		validate();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Location getLocation() {
		return location;
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
	public Collection<? extends Exchangeable> getOrders() {
		return orders;
	}

	@Override
	public boolean accept(Exchangeable exchangeable) {
		if (validate(exchangeable)) {
			synchronized (orders) {
				return ((Collection<Exchangeable>) orders)
						.add(((org.trading.exchange.interfaces.Exchangeable) exchangeable).open());
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "MarketMock{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", \noffered=" + offered +
				", \nrequired=" + required +
				", \nlocation=" + location +
				", \norders=\n" + getOrders() +
				", \nOPEN orders=\n" + getOrders(Exchangeable.State.OPEN) +
				'}' + '\n' + '\n';
	}

	public static class Builder<T> implements org.trading.exchange.publicInterfaces.Market.Builder {
		private Collection<? extends Exchangeable> orders = new LinkedList<>();
		private String id;
		private Location location;
		private String name;
		private Commodity offered;
		private Commodity required;

		@Override
		public Builder<T> setId(String s) {
			this.id = s;
			return this;
		}

		@Override
		public Builder<T> setName(String s) {
			this.name = s;
			return this;
		}

		@Override
		public Builder<T> setLocation(Location location) {
			this.location = location;
			return this;
		}

		@Override
		public Builder<T> setOffered(Commodity commodity) {
			this.offered = commodity;
			return this;
		}

		@Override
		public Builder<T> setRequired(Commodity commodity) {
			this.required = commodity;
			return this;
		}

		@Override
		public Builder<T> accept(Exchangeable exchangeable) {
			((Collection<Exchangeable>) this.orders).add(exchangeable);
			return this;
		}

		@Override
		public T build() {
			return (T) new MarketMock(id, location, name, offered, required,
					orders.toArray(new Exchangeable[orders.size()]));
		}
	}
}
