package org.trading.exchange.interfaces;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import static org.trading.exchange.interfaces.Commodity.*;

/**
 * Created by GArlington.
 */
@XmlRootElement
public enum Location implements org.trading.exchange.publicInterfaces.Location {
    // Metal exchange vaults
    LONDON("LON", "London vault", "Vault description", GOLD, SILVER, USD, GBP, EUR),
    ZURICH("ZUR", "Zurich vault", "Vault description", GOLD, SILVER, USD, GBP, EUR),
    NEW_YORK("NY", "New York vault", "Vault description", GOLD, SILVER, USD, GBP, EUR),
    TORONTO("TR", "Toronto vault", "Vault description", GOLD, SILVER, USD, GBP, EUR),
    SINGAPORE("SG", "Singapore vault", "Vault description", GOLD, SILVER, USD, GBP, EUR),

    // Fuel Oil exchange locations
    GLOBAL("Global", "Global Exchange", "Global Exchange", FUEL_OIL, USD),
    // Fuel Oil exchange locations
    SINGAPORE_PORT("PRTSG", "Port of Rotterdam", "Port of Rotterdam", FUEL_OIL, USD),
    ROTTERDAM_PORT("PRTRTR", "Port of Rotterdam", "Port of Rotterdam", FUEL_OIL, USD),
    FUJAIRAH_PORT("PRTFUJ", "Port of Fujairah", "Port of Fujairah", FUEL_OIL, USD);

    private final String code;
    private final String name;
    private final String description;
    private final Collection<org.trading.exchange.publicInterfaces.Commodity> commodities;

    Location(String code, String name, String description, org.trading.exchange.publicInterfaces.Commodity... commodities) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.commodities = new LinkedList<>(Arrays.asList(commodities));
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Collection<org.trading.exchange.publicInterfaces.Commodity> getCommodities() {
        return commodities;
    }

    @Override
    public boolean checkCommodity(org.trading.exchange.publicInterfaces.Commodity commodity) {
        return commodities.contains(commodity);
    }

    @Override
    public String toString() {
        return "Location{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", commodities=" + commodities +
                '}';
    }
}
