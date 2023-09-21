package pl.ynfuien.ycurrencies.hooks.placeholderapi.placeholders;

import org.bukkit.OfflinePlayer;
import pl.ynfuien.ycurrencies.currencies.Currencies;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.hooks.placeholderapi.Placeholder;

public class CurrencyPlaceholders implements Placeholder {
    private final Currencies currencies;

    public CurrencyPlaceholders(Currencies currencies) {
        this.currencies = currencies;
    }

    @Override
    public String name() {
        return "currency";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        Currency cur = null;
        String[] args = id.split("_");
        // Loop through all currencies to get that with provided name
        for (Currency currency : currencies.getAll()) {
            String name = currency.getName();

            // If id starts with provided name
            if (name.equalsIgnoreCase(args[0])) {
                cur = currency;
                break;
            }
        }

        // Return if currency with provided name doesn't exist
        if (cur == null) return "currency doesn't exist";

        if (args.length == 1) return "no property was provided";

        String property = args[1].toLowerCase();

        // Placeholder: %yc_currency_<name>_displayname%
        // Returns: currency displayname
        if (property.equals("displayname")) {
            return cur.getDisplayname();
        }

        // Placeholder: %yc_currency_<name>_alias%
        // Returns: currency alias
        if (property.equals("alias")) {
            return cur.getAlias();
        }

        // Placeholder: %yc_currency_<name>_color%
        // Returns: currency color
        if (property.equals("color")) {
            return cur.getColor();
        }

        return null;
    }
}
