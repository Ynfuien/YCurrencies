package pl.ynfuien.ycurrencies.currencies.modules;

import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.utils.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CurrencyModule {
    protected final Currency currency;

    protected final Type type;
    protected boolean enabled = false;
    protected Integer minAmount;
    protected Integer maxAmount;
    protected List<GameMode> ignoredGamemodes;
    protected boolean lessChanceForMore;
    protected Double chanceForMore;


    private final static String[] moduleKeys = new String[] {"min-amount", "max-amount", "ignored-gamemodes", "less-chance-for-more", "chance-for-more"};

    public CurrencyModule(Currency currency, Type type) {
        this.currency = currency;
        this.type = type;
    }

    public boolean load(ConfigurationSection config) {
        if (config == null) return false;

        enabled = config.getBoolean("enabled");
        if (!enabled) return true;

        // Check if config has all needed values
        for (String key : moduleKeys) {
            if (config.contains(key)) continue;

            logError(String.format("Configuration is missing key '%s'!", key));
            return false;
        }


        minAmount = config.getInt("min-amount");
        maxAmount = config.getInt("max-amount");

        if (minAmount > maxAmount) {
            logError("'min-amount' can't be higher than 'max-amount'!");
            return false;
        }

        ignoredGamemodes = new ArrayList<>();
        for (String item : config.getStringList("ignored-gamemodes")) {
            GameMode gm;
            try {
                gm = GameMode.valueOf(item.toUpperCase());
            } catch (IllegalArgumentException e) {
                logError(String.format("'%s' is not a correct gamemode name!", item));
                continue;
            }

            ignoredGamemodes.add(gm);
        }


        lessChanceForMore = config.getBoolean("less-chance-for-more");
        chanceForMore = config.getDouble("chance-for-more");

        return true;
    }

    public abstract void start();
    public abstract void stop();

    /**
     * "Rolls a die" according to provided chance
     * @param chance Chance in percentage of returning true
     * @return result of "rolling a die"
     */
    public static boolean chanceOf(double chance) {
        return Math.random() < (chance / 100);
    }

    private static final Random random = new Random();
    public int getRandomAmount() {
        if (!lessChanceForMore) return random.nextInt(minAmount, maxAmount + 1);

        int amount = minAmount;
        while (amount < maxAmount) {
            if (!chanceOf(chanceForMore)) break;

            amount++;
        }

        return amount;
    }

    protected void logError(String message) {
        Logger.logWarning(String.format("[Currency] [%s] [Module] [%s] %s", currency.getName(), type.getName(), message));
    }

    // Getters
    public Currency getCurrency() {
        return currency;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public Type getType() {
        return type;
    }

    public Integer getMinAmount() {
        return minAmount;
    }

    public Integer getMaxAmount() {
        return maxAmount;
    }

    public List<GameMode> getIgnoredGamemodes() {
        return ignoredGamemodes;
    }

    public boolean isIgnoringGamemode(GameMode gamemode) {
        return ignoredGamemodes.contains(gamemode);
    }

    public boolean isLessChanceForMore() {
        return lessChanceForMore;
    }

    public Double getChanceForMore() {
        return chanceForMore;
    }


    public enum Type {
        INTERVAL,
        BREAK_BLOCK,
        KILL_MOB;

        public String getName() {
            return name().toLowerCase().replace("_", "-");
        }
    }
}