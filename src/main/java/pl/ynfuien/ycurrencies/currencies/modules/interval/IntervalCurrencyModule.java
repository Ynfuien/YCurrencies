package pl.ynfuien.ycurrencies.currencies.modules.interval;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.api.events.CurrencyModuleGiveEvent;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.currencies.CurrencyBalances;
import pl.ynfuien.ycurrencies.currencies.modules.CurrencyModule;
import pl.ynfuien.ycurrencies.messages.Lang;

import java.util.HashMap;

public class IntervalCurrencyModule extends CurrencyModule {
    private final YCurrencies instance;
    private final Lang lang;
    private long time;
    private BukkitTask interval;

    public IntervalCurrencyModule(Currency currency) {
        super(currency, Type.INTERVAL);

        this.instance = YCurrencies.getInstance();
        this.lang = currency.getLang();
    }

    @Override
    public boolean load(ConfigurationSection config) {
        if (!super.load(config)) return false;

        if (!config.contains("time")) {
            logError("Configuration is missing key 'time'!");
            return false;
        }

        time = config.getInt("time") * 20L;

        return true;
    }

    public void start() {
        stop();

        CurrencyBalances balances = currency.getBalances();
        interval = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                int amount = getRandomAmount();
                CurrencyModuleGiveEvent event = new CurrencyModuleGiveEvent(this, p, amount, null);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) return;

                amount = event.getAmount();
                balances.add(p.getUniqueId(), amount);

                HashMap<String, Object> phs = new HashMap<>();
                phs.put("amount", amount);

                lang.send(p, Lang.Currency.MODULE_INTERVAL_GET, phs);
            }
        }, time, time);
    }

    public void stop() {
        if (interval != null) interval.cancel();
    }

}
