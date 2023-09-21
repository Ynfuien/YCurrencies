package pl.ynfuien.ycurrencies.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.currencies.Currencies;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.currencies.CurrencyBalances;

public class YCurrenciesAPI {
    /**
     * Gets currencies
     * @return Currencies object
     */
    @Nullable
    public static Currencies getCurrencies() {
        return YCurrencies.getInstance().getCurrencies();
    }

    /**
     * Gets currency by its name
     * @param name Name of the currency
     * @return Currency object or null if not found
     */
    @Nullable
    public static Currency getCurrency(@NotNull String name) {
        return getCurrencies().get(name);
    }

    /**
     * Gets currency balances
     * @param name Name of the currency
     * @return Currency balances or null if not found
     */
    @Nullable
    public static CurrencyBalances getCurrencyBalances(@NotNull String name) {
        Currency c = getCurrency(name);
        if (c == null) return null;

        return c.getBalances();
    }
}
