package pl.ynfuien.ycurrencies;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import pl.ynfuien.ycurrencies.commands.main.MainCommand;
import pl.ynfuien.ycurrencies.config.ConfigHandler;
import pl.ynfuien.ycurrencies.config.ConfigName;
import pl.ynfuien.ycurrencies.config.ConfigObject;
import pl.ynfuien.ycurrencies.currencies.Currencies;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.currencies.CurrencyCommands;
import pl.ynfuien.ycurrencies.currencies.CurrencyDatabase;
import pl.ynfuien.ycurrencies.hooks.Hooks;
import pl.ynfuien.ycurrencies.listeners.PlayerQuitListener;
import pl.ynfuien.ycurrencies.messages.Lang;
import pl.ynfuien.ycurrencies.utils.CurrencyFormatter;
import pl.ynfuien.ycurrencies.utils.Logger;
import pl.ynfuien.ycurrencies.utils.Permissions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class YCurrencies extends JavaPlugin {
    private static YCurrencies instance;
    private final ConfigHandler configHandler = new ConfigHandler(this);
    private ConfigObject config;
    private final Lang lang = new Lang();
    private final Currencies currencies = new Currencies();
    private final CurrencyCommands currencyCommands = new CurrencyCommands(this, currencies);
    private final Permissions perms = new Permissions(this.getName().toLowerCase());

    private boolean reloading = false;

    @Override
    public void onEnable() {
        instance = this;
        Logger.setPrefix("<dark_aqua>[<aqua>Y<gold>Currencies<dark_aqua>] <white>");

        loadConfigs();
        loadLang();
        config = configHandler.get(ConfigName.CONFIG);

        CurrencyFormatter.setup(config.getConfig().getConfigurationSection("currency-formatting"));

        // Database setup
        if (!CurrencyDatabase.setup(config.getConfig().getConfigurationSection("database"))) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Load currencies from config files
        if (!loadCurrencies()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Prepare for registering commands
        if (!currencyCommands.prepare()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // And register them
        currencyCommands.register();

        // Load hooks
        Hooks.load(this);


        // Set plugin's main command
        MainCommand mainCommand = new MainCommand(this);
        getServer().getPluginCommand("ycurrencies").setExecutor(mainCommand);
        getServer().getPluginCommand("ycurrencies").setTabCompleter(mainCommand);


        currencies.startCurrencyModules();

        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);

        Logger.log("Plugin successfully <green>enabled<white>!");
    }


    @Override
    public void onDisable() {
        CurrencyDatabase.close();

        currencies.stopCurrencyModules();

        Logger.log("Plugin successfully <red>disabled<white>!");
    }


    private void loadLang() {
        // Get lang config
        FileConfiguration config = configHandler.getConfig(ConfigName.LANG);

        // Reload lang
        lang.loadConfig(config);
        lang.setDefaultPlaceholders(new HashMap<>() {{put("prefix", lang.get(Lang.General.PREFIX));}});
    }

    private void loadConfigs() {
        configHandler.load(ConfigName.CONFIG, true);
        configHandler.load(ConfigName.LANG, true, true);
    }

    private boolean loadCurrencies() {
        // Get and create directory if it doesn't exist
        File directory = new File(getDataFolder(), "currencies");
        if (!directory.exists()) {
            for (String name : new String[] {"coin", "token"}) {
                saveResource(String.format("currencies/%s/currency.yml", name), false);
                saveResource(String.format("currencies/%s/lang.yml", name), false);
            }
        }

        return currencies.load(directory);
    }

    public boolean reloadPlugin() {
        reloading = true;

        // Reload all configs
        configHandler.reloadAll();

        // Reload lang
        instance.loadLang();

        // Reload currencies
        currencies.stopCurrencyModules();
        List<Currency> currencyList = new ArrayList<>(currencies.getAll());
        if (!loadCurrencies()) {
            currencies.setCurrencies(currencyList);
            Logger.logError("Couldn't reload currencies! Using old ones...");
            return false;
        }
        currencies.startCurrencyModules();

        // Reload currency commands
        currencyCommands.unregister();
        currencyCommands.register();
        currencyCommands.syncCommands();


        CurrencyFormatter.setup(config.getConfig().getConfigurationSection("currency-formatting"));

        reloading = false;
        return true;
    }

    public boolean isReloading() {
        return reloading;
    }


    public static YCurrencies getInstance() {
        return instance;
    }

    public Lang getLang() {
        return lang;
    }
    public Currencies getCurrencies() {
        return currencies;
    }
    public CurrencyCommands getCurrencyCommands() {
        return currencyCommands;
    }
    public Permissions getPermissions() {
        return perms;
    }
}
