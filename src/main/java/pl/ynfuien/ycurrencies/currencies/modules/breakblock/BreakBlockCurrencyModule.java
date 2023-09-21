package pl.ynfuien.ycurrencies.currencies.modules.breakblock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.currencies.modules.CurrencyModule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BreakBlockCurrencyModule extends CurrencyModule {
    private final YCurrencies instance;
    private HashMap<Double, Set<Material>> blockChances = new HashMap<>();
    private BlockBreakListener listener;

    public BreakBlockCurrencyModule(Currency currency) {
        super(currency, Type.BREAK_BLOCK);
        instance = YCurrencies.getInstance();
    }

    @Override
    public boolean load(ConfigurationSection config) {
        if (!super.load(config)) return false;

        ConfigurationSection blockSection = config.getConfigurationSection("block-chances");
        if (blockSection == null) {
            logError("Configuration is missing key 'block-chances'!");
            return false;
        }

        blockChances.clear();
        for (String key : blockSection.getKeys(false)) {
            double value;
            try {
                value = Double.parseDouble(key.replace(",", "."));
            } catch (NumberFormatException e) {
                logError(String.format("Value '%s' couldn't be parsed to double. Block list with this chance will be skipped.", key));
                continue;
            }

            Set<Material> blockSet = new HashSet<>();
            List<String> list = blockSection.getStringList(key);
            list.replaceAll(String::toUpperCase);
            for (String item : list) {
                Material block = Material.matchMaterial(item);
                if (block == null) {
                    logError(String.format("'%s' is not a correct block name!", item));
                    continue;
                }

                blockSet.add(block);
            }

            blockChances.put(value, blockSet);
        }

        return true;
    }

    public void start() {
        listener = new BlockBreakListener(currency, this);
        Bukkit.getPluginManager().registerEvents(listener, instance);
    }

    public void stop() {
        HandlerList.unregisterAll(listener);
    }


    /**
     *
     * @param block
     * @return Chance to get money from that block, or null if none.
     */
    public Double getChanceForABlock(Material block) {
        if (!block.isBlock()) return null;

        for (double chance : blockChances.keySet()) {
            Set<Material> blockSet = blockChances.get(chance);

            if (blockSet.contains(block)) return chance;
        }

        return null;
    }
}
