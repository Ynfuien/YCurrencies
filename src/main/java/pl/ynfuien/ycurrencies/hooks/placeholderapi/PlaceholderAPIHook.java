package pl.ynfuien.ycurrencies.hooks.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.currencies.Currencies;
import pl.ynfuien.ycurrencies.hooks.placeholderapi.placeholders.BalancePlaceholders;
import pl.ynfuien.ycurrencies.hooks.placeholderapi.placeholders.CurrenciesPlaceholders;
import pl.ynfuien.ycurrencies.hooks.placeholderapi.placeholders.CurrencyPlaceholders;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final YCurrencies instance;
    private final Currencies currencies;

    private final Placeholder[] placeholders;

    public PlaceholderAPIHook(YCurrencies instance) {
        this.instance = instance;

        currencies = instance.getCurrencies();

        placeholders = new Placeholder[] {
            new CurrencyPlaceholders(currencies),
            new BalancePlaceholders(currencies),
            new CurrenciesPlaceholders(currencies)
        };
    }

    @Override @NotNull
    public String getAuthor() {
        return "Ynfuien";
    }

    @Override @NotNull
    public String getIdentifier() {
        return "yc";
    }

    @Override @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    // Currency
    // %yc_currency_<name>_displayname%
    // %yc_currency_<name>_alias%
    // %yc_currency_<name>_color%

    // Balance
    // %yc_balance_<currency>%
    // %yc_balance_<currency>_formatted%

    // Currencies
    // %yc_currencies_count%
    // %yc_currencies_list%


    @Override
    public String onRequest(OfflinePlayer p, @NotNull String params) {
        Placeholder placeholder = null;

        // Loop through placeholders and get that provided by name
        for (Placeholder ph : placeholders) {
            if (params.startsWith(ph.name() + "_")) {
                placeholder = ph;
                break;
            }
        }

        // If provided placeholder is incorrect
        if (placeholder == null) return "incorrect placeholder";

        // Get placeholder properties from params
        String id = params.substring(placeholder.name().length() + 1);
        // Get placeholder result
        String result = placeholder.getPlaceholder(id, p);

        // If result is null
        if (result == null) return "incorrect property";

        // Return result
        return result;
    }
}