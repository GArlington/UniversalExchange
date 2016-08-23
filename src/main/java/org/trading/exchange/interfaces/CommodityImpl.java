package org.trading.exchange.interfaces;

import org.trading.exchange.publicInterfaces.Commodity;

import javax.xml.bind.annotation.XmlRootElement;

import static org.trading.exchange.interfaces._DefaultValues.*;

/**
 * Created by GArlington.
 */
@XmlRootElement
public enum CommodityImpl implements Commodity {
    // Stocks and shares
    STOCKS("#STOCKS#", "#STOCKS#", "Stocks and shares", PRICED_TO_MINIMUM_ORDER_RATIO)

    // Bonds and gilts
    , BONDS_AND_GUILTS("#GUILTS#", "#GUILTS#", "Bonds and guilts", PRICED_TO_MINIMUM_ORDER_RATIO)

    // Hard commodities
    // Metals
    , GOLD("AUX", "AXU", "Gold Bullion", PRECIOUS_METALS_PRICED_TO_MINIMUM_ORDER_RATIO),
    SILVER("AGX", "AXG", "Silver Bullion", PRECIOUS_METALS_PRICED_TO_MINIMUM_ORDER_RATIO),
    PLATINUM("PL", "???", "Platinum Bullion", PRECIOUS_METALS_PRICED_TO_MINIMUM_ORDER_RATIO)

    // Oil
    , CRUDE_OIL_WTI("CL", "CLY00", "Crude Oil WTI", PRICED_TO_MINIMUM_ORDER_RATIO),
    CRUDE_OIL_BRENT("SC", "SCY00", "Crude Oil Brent", PRICED_TO_MINIMUM_ORDER_RATIO)
    // Fuel Oil
    , FUEL_OIL("Fuel", "Oil", "Fuel Bunkers", PRICED_TO_MINIMUM_ORDER_RATIO)

    // Soft commodities
    , SUGAR("SUG", "SUG", "Sugar", PRICED_TO_MINIMUM_ORDER_RATIO), COFFEE("COF", "COF", "Coffee", PRICED_TO_MINIMUM_ORDER_RATIO)

    // Currencies
    , USD("USD", "USD", "United States Dollar", CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO), GBP("GBP", "GBP", "Great Britain Pound", CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO), EUR("EUR", "EUR", "EU Euro", CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO), JPY("JPY", "JPY", "Japanese Yen", PRICED_TO_MINIMUM_ORDER_RATIO), RUB("RUB", "RUB", "Russian Rouble", CURRENCY_PRICED_TO_MINIMUM_ORDER_RATIO);

    private final String id;
    private final String name;
    private final String description;
    private final long priceToQuantityRatio;

    /**
     * @param id
     * @param name
     * @param description
     * @param priceToQuantityRatio - multiplier where price is quoted for different units of the minimum quantity that
     *                             can be purchased (price of Gold is quoted per 1KGram but you can buy 1Gram of
     *                             Gold ->
     *                             priceToQuantityRatio = 1000L)
     */
    CommodityImpl(String id, String name, String description, long priceToQuantityRatio) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priceToQuantityRatio = priceToQuantityRatio;
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

    public long getPriceToQuantityRatio() {
        return priceToQuantityRatio;
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
