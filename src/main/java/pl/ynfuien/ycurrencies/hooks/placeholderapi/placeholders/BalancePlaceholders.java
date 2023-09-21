package pl.ynfuien.ycurrencies.hooks.placeholderapi.placeholders;

import org.bukkit.OfflinePlayer;
import pl.ynfuien.ycurrencies.currencies.Currencies;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.hooks.placeholderapi.Placeholder;
import pl.ynfuien.ycurrencies.utils.CurrencyFormatter;

public class BalancePlaceholders implements Placeholder {
    private final Currencies currencies;

    public BalancePlaceholders(Currencies currencies) {
        this.currencies = currencies;
    }

    @Override
    public String name() {
        return "balance";
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

        // Placeholder: %yc_balance_<currency>%
        // Returns: player's currency balance
        int balance = cur.getBalances().get(p.getUniqueId());
        if (args.length == 1) return String.valueOf(balance);

        // Placeholder: %yc_balance_<currency>_formatted%
        // Returns: formatted player's currency balance
        if (args[1].equals("formatted")) return CurrencyFormatter.format(balance);

        return null;
    }
}
