package pl.ynfuien.ycurrencies.currencies.modules.killmob;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.event.HandlerList;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.currencies.modules.CurrencyModule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KillMobCurrencyModule extends CurrencyModule {
    private final YCurrencies instance;
    private HashMap<Double, Set<String>> mobsChances = new HashMap<>();
    private EntityDeathListener listener;

    public KillMobCurrencyModule(Currency currency) {
        super(currency, Type.KILL_MOB);
        instance = YCurrencies.getInstance();
    }

    @Override
    public boolean load(ConfigurationSection config) {
        if (!super.load(config)) return false;

        ConfigurationSection mobSection = config.getConfigurationSection("mob-chances");
        if (mobSection == null) {
            logError("Configuration is missing key 'mob-chances'!");
            return false;
        }

        mobsChances.clear();
        for (String key : mobSection.getKeys(false)) {
            double value;
            try {
                value = Double.parseDouble(key.replace(",", "."));
            } catch (NumberFormatException e) {
                logError(String.format("Value '%s' couldn't be parsed to double. Mob list with this chance will be skipped.", key));
                continue;
            }

            Set<String> mobSet = new HashSet<>();
            List<String> list = mobSection.getStringList(key);
            list.replaceAll(String::toUpperCase);
            for (String item : list) {
                try {
                    MobCategory.valueOf(item);

                    mobSet.add(item);
                    continue;
                } catch (IllegalArgumentException e) {}

                try {
                    EntityType.valueOf(item);
                } catch (IllegalArgumentException e) {
                    logError(String.format("'%s' is not a correct mob name or category!", item));
                    continue;
                }

                mobSet.add(item);
            }

            mobsChances.put(value, mobSet);
        }

        return true;
    }

    public void start() {
        listener = new EntityDeathListener(currency, this);
        Bukkit.getPluginManager().registerEvents(listener, instance);
    }

    public void stop() {
        HandlerList.unregisterAll(listener);
    }


    /**
     *
     * @param mob
     * @return Chance to get money from that mob, or null if none.
     */
    public Double getChanceForAMob(Entity mob) {
        for (double chance : mobsChances.keySet()) {
            Set<String> mobSet = mobsChances.get(chance);

            if (mobSet.contains(mob.getType().name())) return chance;

            if (mobSet.contains(MobCategory.ANIMALS.name()) && mob instanceof Animals) return chance;
            if (mobSet.contains(MobCategory.MONSTER.name()) && mob instanceof Monster) return chance;
            if (mobSet.contains(MobCategory.WATER_MOB.name()) && mob instanceof WaterMob) return chance;
            if (mobSet.contains(MobCategory.AMBIENT.name()) && mob instanceof Ambient) return chance;
            if (mobSet.contains(MobCategory.BOSS.name()) && mob instanceof Boss) return chance;
            if (mobSet.contains(MobCategory.GOLEM.name()) && mob instanceof Golem) return chance;
        }

        return null;
    }


    public enum MobCategory {
        ANIMALS,
        MONSTER,
        WATER_MOB,
        AMBIENT,
        BOSS,
        GOLEM
    }
}
