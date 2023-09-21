package pl.ynfuien.ycurrencies.currencies;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.ynfuien.ycurrencies.currencies.modules.CurrencyModule;
import pl.ynfuien.ycurrencies.currencies.modules.breakblock.BreakBlockCurrencyModule;
import pl.ynfuien.ycurrencies.currencies.modules.interval.IntervalCurrencyModule;
import pl.ynfuien.ycurrencies.currencies.modules.killmob.KillMobCurrencyModule;
import pl.ynfuien.ycurrencies.messages.Lang;
import pl.ynfuien.ycurrencies.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Currency {
    private final Currencies currencies;
    private final CurrencyBalances balances = new CurrencyBalances(this);
    private final Lang lang = new Lang();

    // General
    private final String name;
    private String displayname;
    private String alias;
    private String color;

    // Database
    private String databaseTable;

    // Command
    private String commandName;
    private List<String> commandAliases;

    // Modules
    private List<CurrencyModule> modules = new ArrayList<>();

    public Currency(Currencies currencies, String name) {
        this.currencies = currencies;
        this.name = name;
    }

    /**
     * Loads currency from provided configuration. You won't need it, so don't use it.
     * @param config Config file
     * @param langConfig Lang file
     * @return Whether loading succeed
     */
    public boolean load(FileConfiguration config, FileConfiguration langConfig) {
        if (config == null) return false;
        if (langConfig == null) return false;


        // General
        displayname = config.getString("general.displayname");
        alias = config.getString("general.alias");
        color = config.getString("general.color");

        // Load lang
        lang.loadConfig(langConfig);
        lang.setDefaultPlaceholders(new HashMap<>() {{
            put("prefix", lang.get(Lang.Currency.PREFIX));
            put("name", name);
            put("displayname", displayname != null ? displayname : "");
            put("alias", alias != null ? alias : "");
            put("color", color != null ? color : "");
        }});

        // Database
        databaseTable = config.getString("database.table");
        if (databaseTable == null) {
            logError("Property 'database.table' in config file is missing or incorrect! It's required for currency to work.");
            return false;
        }

        // Command
        commandName = config.getString("command.name");
        if (commandName == null) {
            logError("Property 'command.name' in config file is missing or incorrect! It's required for currency to work.");
            return false;
        }
        commandAliases = config.getStringList("command.aliases");

        // Modules
        modules.clear();
        ConfigurationSection modulesSection = config.getConfigurationSection("modules");
        if (modulesSection == null) return true;

        List<CurrencyModule> mList = new ArrayList<>();
        if (modulesSection.contains("kill-mob")) {
            CurrencyModule module = new KillMobCurrencyModule(this);
            mList.add(module);
        }

        if (modulesSection.contains("interval")) {
            CurrencyModule module = new IntervalCurrencyModule(this);
            mList.add(module);
        }

        if (modulesSection.contains("break-block")) {
            CurrencyModule module = new BreakBlockCurrencyModule(this);
            mList.add(module);
        }

        if (mList.size() == 0) {
            log("There were no modules to load...");
            return true;
        }

        for (CurrencyModule module : mList) {
            String name = module.getType().getName();
            if (!module.load(modulesSection.getConfigurationSection(name))) {
                logError(String.format("Couldn't load '%s' module!", name));
                continue;
            }

            if (!module.isEnabled()) continue;

            modules.add(module);
        }

        log(String.format("Successfully loaded %d modules!", modules.size()));

        return true;
    }

    /**
     * Starts all loaded modules for this currency.
     */
    public void startModules() {
        for (CurrencyModule module : modules) {
            module.start();
        }
    }

    /**
     * Stops all loaded modules for this currency.
     */
    public void stopModules() {
        for (CurrencyModule module : modules) {
            module.stop();
        }
    }


    private void log(String message) {
        Logger.log(String.format("[Currency] [%s] %s", name, message));
    }
    private void logError(String message) {
        Logger.logWarning(String.format("[Currency] [%s] %s", name, message));
    }


    //// Getters

    /**
     * Gets currencies instance
     * @return Currencies object
     */
    public Currencies getCurrencies() {
        return currencies;
    }
    /**
     * Gets currency balances. Use it to manage players balances of this currency.
     * @return Currency balances
     */
    public CurrencyBalances getBalances() {
        return balances;
    }
    /**
     * Gets lang for this currency. It contains all messages used in context of this currency.
     * @return Lang object
     */
    public Lang getLang() {
        return lang;
    }

    public String getName() {
        return name;
    }
    public String getDisplayname() {
        return displayname;
    }
    public String getAlias() {
        return alias;
    }
    public String getColor() {
        return color;
    }

    public String getDatabaseTable() {
        return databaseTable;
    }

    public String getCommandName() {
        return commandName;
    }
    public List<String> getCommandAliases() {
        return commandAliases;
    }

    public List<CurrencyModule> getModules() {
        return modules;
    }

}
