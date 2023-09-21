package pl.ynfuien.ycurrencies.currencies;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.ynfuien.ycurrencies.utils.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Currencies {
    private final HashMap<String, Currency> currencies = new HashMap<>();

    /**
     * Loads currencies from files in provided directory. You won't need it, so don't use it.
     * @param mainDirectory Directory of currencies configs
     * @return Whether loading succeed
     */
    public boolean load(File mainDirectory) {
        if (mainDirectory == null) return false;

        currencies.clear();
        File[] fileList;
        try {
            fileList = mainDirectory.listFiles();
        } catch (SecurityException e) {
            logError("Couldn't read files of 'currencies' directory.");
            e.printStackTrace();
            return false;
        }

        // Loop through all 'currencies' directories and load those
        currencies : for (File currencyDirectory : fileList) {
            if (!currencyDirectory.isDirectory()) continue;

            // Get files
            File configFile = new File(currencyDirectory, "currency.yml");
            File langFile = new File(currencyDirectory, "lang.yml");

            // Check if files exist and are files
            for (File f : new File[] {configFile, langFile}) {
                if (!f.exists()) {
                    logError(String.format("File '%s' in '%s' directory doesn't exist!", f.getName(), currencyDirectory.getPath()));
                    continue currencies;
                }

                if (!f.isFile()) {
                    logError(String.format("File '%s' in '%s' directory isn't a correct file!", f.getName(), currencyDirectory.getPath()));
                    continue currencies;
                }
            }

            // Check for currency name in config file
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            String name = config.getString("general.name");
            if (name == null) {
                logError(String.format("Value 'general.name' in file '%s' is incorrect! Currency won't be loaded!", configFile.getPath()));
                continue;
            }

            // Get lang config
            FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);

            // Load currency
            Currency currency = new Currency(this, name);
            if (!currency.load(config, langConfig)) {
                logError(String.format("Couldn't load '%s' currency.", name));
                continue;
            }

            // Database table
            if (!CurrencyDatabase.createTable(currency)) {
                logError(String.format("Without database table currency won't be loaded.", name));
                continue;
            }

            logInfo(String.format("Currency '%s' successfully loaded!", name));
            currencies.put(name, currency);
        }


        if (currencies.size() == 0) {
            logInfo("There were no currencies to load!");
            return true;
        }

        logInfo(String.format("Successfully loaded %d currencies!", currencies.size()));
        return true;
    }

    /**
     * Starts all loaded modules for all loaded currencies.
     */
    public void startCurrencyModules() {
        for (Currency currency : currencies.values()) {
            currency.startModules();
        }
    }

    /**
     * Stops all loaded modules for all loaded currencies.
     */
    public void stopCurrencyModules() {
        for (Currency currency : currencies.values()) {
            currency.stopModules();
        }
    }


    //// Log methods
    private void logError(String message) {
        Logger.logWarning("[Currencies] " + message);
    }
    private void logInfo(String message) {
        Logger.log("[Currencies] " + message);
    }


    //// Getters

    /**
     * Gets list of all currencies
     * @return Currency list
     */
    public List<Currency> getAll() {
        return new ArrayList<>(currencies.values());
    }

    /**
     * Gets currency by its name
     * @param name Currency name
     * @return Currency object or null if none found
     */
    public Currency get(String name) {
        return currencies.get(name);
    }


    //// Setters
    /**
     * Sets currencies from provided list. Don't use it, it's for plugin's reloading purposes.
     * @param currencyList List of currencies
     */
    public void setCurrencies(List<Currency> currencyList) {
        currencies.clear();

        for (Currency c : currencyList) {
            currencies.put(c.getName(), c);
        }
    }
}
