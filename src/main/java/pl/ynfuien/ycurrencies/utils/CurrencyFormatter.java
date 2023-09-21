package pl.ynfuien.ycurrencies.utils;

import org.bukkit.configuration.ConfigurationSection;

public class CurrencyFormatter {
    private final static DoubleFormatter df = new DoubleFormatter();

    private static String billions = "B";
    private static String millions = "M";
    private static String thousands = "k";

    public static void setup(ConfigurationSection config) {
        billions = config.getString("billions");
        millions = config.getString("millions");
        thousands = config.getString("thousands");
    }

    public static String format(double number) {
        // Billions
        if (number >= 1000000000) return df.format(number / 1000000000) + billions;
        // Millions
        if (number >= 1000000) return df.format(number / 1000000) + millions;
        // Thousands
        if (number >= 1000) return df.format(number / 1000) + thousands;

        return String.valueOf((int) number);
    }
}
