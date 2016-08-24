package org.trading.exchange.interfaces;

import javax.xml.bind.annotation.XmlRootElement;

import static org.trading.exchange.interfaces._DefaultValues.*;

/**
 * Created by GArlington.
 */
@XmlRootElement
public enum Commodity implements org.trading.exchange.publicInterfaces.Commodity {
	// Stocks and shares
	STOCKS("#STOCKS#", "#STOCKS#", "Stocks and shares", PRICED_TO_MINIMUM_ORDER_RATIO, STOCKS_UNIT_NAME,
			STOCKS_UNIT_NAME),

	// Bonds and gilts
	BONDS_AND_GUILTS("#GUILTS#", "#GUILTS#", "Bonds and guilts", PRICED_TO_MINIMUM_ORDER_RATIO, STOCKS_UNIT_NAME,
			STOCKS_UNIT_NAME),

	// Hard commodities
	// Metals
	GOLD("AUX", "AXU", "Gold Bullion", PRECIOUS_METALS_PRICED_TO_MINIMUM_ORDER_RATIO, PRECIOUS_METALS_PRICE_UNIT_NAME,
			PRECIOUS_METALS_UNIT_NAME),
	SILVER("AGX", "AXG", "Silver Bullion", PRECIOUS_METALS_PRICED_TO_MINIMUM_ORDER_RATIO,
			PRECIOUS_METALS_PRICE_UNIT_NAME, PRECIOUS_METALS_UNIT_NAME),
	PLATINUM("PL", "???", "Platinum Bullion", PRECIOUS_METALS_PRICED_TO_MINIMUM_ORDER_RATIO,
			PRECIOUS_METALS_PRICE_UNIT_NAME, PRECIOUS_METALS_UNIT_NAME),

	// Oil
	CRUDE_OIL_WTI("CL", "CLY00", "Crude Oil WTI", PRICED_TO_MINIMUM_ORDER_RATIO, OIL_PRICE_UNIT_NAME, OIL_UNIT_NAME),
	CRUDE_OIL_BRENT("SC", "SCY00", "Crude Oil Brent", PRICED_TO_MINIMUM_ORDER_RATIO, OIL_PRICE_UNIT_NAME,
			OIL_UNIT_NAME),
	// Fuel Oil
	FUEL_OIL("Fuel", "Oil", "Fuel Bunkers", PRICED_TO_MINIMUM_ORDER_RATIO, OIL_PRICE_UNIT_NAME, OIL_UNIT_NAME),

	// Soft commodities
	SUGAR("SUG", "SUG", "Sugar", PRICED_TO_MINIMUM_ORDER_RATIO, SOFT_COMMODITIES_PRICE_UNIT_NAME,
			SOFT_COMMODITIES_UNIT_NAME),
	COFFEE("COF", "COF", "Coffee", PRICED_TO_MINIMUM_ORDER_RATIO, SOFT_COMMODITIES_PRICE_UNIT_NAME,
			SOFT_COMMODITIES_UNIT_NAME),

	// Currencies
	USD("USD", "USD", "United States Dollar", CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO),
	GBP("GBP", "GBP", "Great Britain Pound", CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO),
	EUR("EUR", "EUR", "EU Euro", CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO),
	JPY("JPY", "JPY", "Japanese Yen", PRICED_TO_MINIMUM_ORDER_RATIO),
	RUB("RUB", "RUB", "Russian Rouble", CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO);

	private final String id;
	private final String name;
	private final String description;
	private final long priceToQuantityRatio;
	private final String priceUnit;
	private final String quantityUnit;
	private final boolean global;

	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param priceToQuantityRatio multiplier where price is quoted for different units of the minimum quantity that
	 *                             can be purchased (price of Gold is quoted per 1KGram but you can buy 1Gram of
	 *                             Gold ->
	 *                             priceToQuantityRatio = 1000L)
	 */
	Commodity(String id, String name, String description, long priceToQuantityRatio) {
		this(id, name, description, priceToQuantityRatio, name, name);
	}

	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param priceToQuantityRatio multiplier where price is quoted for different units of the minimum quantity that
	 *                             can be purchased (price of Gold is quoted per 1KGram but you can buy 1Gram of
	 *                             Gold ->
	 *                             priceToQuantityRatio = 1000L)
	 * @param priceUnit            Name of price unit (KG/Tonne/Share...)
	 * @param quantityUnit         Name of quantity unit (KG/Tonne/Share...)
	 */
	Commodity(String id, String name, String description, long priceToQuantityRatio, String priceUnit,
			  String quantityUnit) {
		this(id, name, description, priceToQuantityRatio, priceUnit, quantityUnit, true);
	}

	/**
	 * @param id
	 * @param name
	 * @param description
	 * @param priceToQuantityRatio - multiplier where price is quoted for different units of the minimum quantity that
	 *                             can be purchased (price of Gold is quoted per 1KGram but you can buy 1Gram of
	 *                             Gold ->
	 *                             priceToQuantityRatio = 1000L)
	 * @param priceUnit            Name of price unit (KG/Tonne/Share...)
	 * @param quantityUnit         Name of quantity unit (KG/Tonne/Share...)
	 * @param global               Global commodity indicator - currencies are generally global
	 *                             i.e. not specific to Location
	 */
	Commodity(String id, String name, String description, long priceToQuantityRatio, String priceUnit,
			  String quantityUnit, boolean global) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.priceToQuantityRatio = priceToQuantityRatio;
		this.priceUnit = priceUnit;
		this.quantityUnit = quantityUnit;
		this.global = global;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String getPriceUnit() {
		return priceUnit;
	}

	@Override
	public String getQuantityUnit() {
		return quantityUnit;
	}

	public long getPriceToQuantityRatio() {
		return priceToQuantityRatio;
	}

	@Override
	public boolean isGlobal() {
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", priceToQuantityRatio=" + priceToQuantityRatio +
				'}';
	}
}
