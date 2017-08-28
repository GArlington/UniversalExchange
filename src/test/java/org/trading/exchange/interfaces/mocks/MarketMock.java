package org.trading.exchange.interfaces.mocks;

import org.trading.exchange.interfaces.Market;
import org.trading.exchange.publicInterfaces.Commodity;
import org.trading.exchange.publicInterfaces.ExchangeOffer;
import org.trading.exchange.publicInterfaces.Location;
import org.trading.exchange.publicInterfaces.Owner;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by GArlington.
 */
public class MarketMock implements Market {
	private final Collection<? extends ExchangeOffer> offers = new LinkedList<>();
	private String id;
	private Location location;
	private String name;
	private Commodity offered;
	private Commodity required;
	private Owner owner;
	private boolean autoMatching;

	private MarketMock(String id, Location location, String name, Commodity offered, Commodity required, Owner owner, boolean autoMatching) {
		this.id = id;
		this.location = location;
		this.name = name;
		this.offered = offered;
		this.required = required;
		this.owner = owner;
		this.autoMatching = autoMatching;
	}

	private MarketMock(String id, Location location, String name, Commodity offered, Commodity required, Owner owner, boolean autoMatching,
					   ExchangeOffer... offers) {
		this(id, location, name, offered, required, owner, autoMatching);
		for (ExchangeOffer exchangeOffer : offers) {
			accept(exchangeOffer);
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

	public Collection<? extends ExchangeOffer> getOffers() {
		return offers;
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	public boolean isAutoMatching() {
		return autoMatching;
	}

	@Override
	public ExchangeOffer accept(ExchangeOffer exchangeOffer) {
		exchangeOffer = org.trading.exchange.interfaces.Market.super.accept(exchangeOffer);
		if (exchangeOffer != null) {
			exchangeOffer = ((org.trading.exchange.interfaces.ExchangeOffer) exchangeOffer).open();
		}
		return exchangeOffer;
	}

	@Override
	public String toString() {
		return "MarketMock{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", \noffered=" + offered + ", \nrequired=" + required + ", \nlocation=" +
				location + ", \noffers=\n" + getOffers() + ", \nOPEN offers=\n" + getOffers(ExchangeOffer.State.OPEN) + '}' + '\n' + '\n';
	}

	public static class Builder<T> implements org.trading.exchange.publicInterfaces.Market.Builder {
		private Collection<? extends ExchangeOffer> offers = new LinkedList<>();
		private String id;
		private Location location;
		private String name;
		private Commodity offered;
		private Commodity required;
		private Owner owner;
		private boolean autoMatching;

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
		public Builder<T> accept(ExchangeOffer exchangeOffer) {
			@SuppressWarnings("unchecked") Collection<ExchangeOffer> orders = (Collection<ExchangeOffer>) this.offers;
			orders.add(exchangeOffer);
			return this;
		}

		@Override
		public Builder<T> setOwner(Owner owner) {
			this.owner = owner;
			return this;
		}

		public Builder<T> setAutoMatching(boolean autoMatching) {
			this.autoMatching = autoMatching;
			return this;
		}

		@Override
		public T build() {
			@SuppressWarnings("unchecked") T result =
					(T) new MarketMock(id, location, name, offered, required, owner, autoMatching, offers.toArray(new ExchangeOffer[offers.size()]));
			return result;
		}
	}
}
