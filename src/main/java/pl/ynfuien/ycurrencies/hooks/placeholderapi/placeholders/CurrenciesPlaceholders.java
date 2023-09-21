package pl.ynfuien.ycurrencies.hooks.placeholderapi.placeholders;

import org.bukkit.OfflinePlayer;
import pl.ynfuien.ycurrencies.currencies.Currencies;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.hooks.placeholderapi.Placeholder;

import java.util.ArrayList;
import java.util.List;

public class CurrenciesPlaceholders implements Placeholder {
    private final Currencies currencies;
    public CurrenciesPlaceholders(Currencies currencies) {
        this.currencies = currencies;
    }

    @Override
    public String name() {
        return "currencies";
    }

    @Override
    public String getPlaceholder(String id, OfflinePlayer p) {
        // Placeholder: %yc_currencies_count%
        // Returns: count of all currencies
        if (id.equals("count")) return String.valueOf(currencies.getAll().size());

        // Placeholder: %yc_currencies_list%
        // Returns: list of all currencies
        if (id.equals("list")) {
            List<String> list = new ArrayList<>();
            for (Currency currency : currencies.getAll()) {
                list.add(currency.getName());
            }

            return String.join(", ", list);
        }

        return null;
    }
}
