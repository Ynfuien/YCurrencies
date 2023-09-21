package pl.ynfuien.ycurrencies.currencies.modules.killmob;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.api.events.CurrencyModuleGiveEvent;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.currencies.modules.CurrencyModule;
import pl.ynfuien.ycurrencies.messages.Lang;

import java.util.HashMap;

public class EntityDeathListener implements Listener {
    private final YCurrencies instance;
    private final Currency currency;
    private final Lang lang;
    private final KillMobCurrencyModule module;

    public EntityDeathListener(Currency currency, KillMobCurrencyModule module) {
        this.instance = YCurrencies.getInstance();
        this.currency = currency;
        this.lang = currency.getLang();
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        Player p = entity.getKiller();

        if (p == null) return;

        if (module.isIgnoringGamemode(p.getGameMode())) return;

        // Get specified chance of getting money for killing this mob
        // or null if none specified.
        Double chance = module.getChanceForAMob(entity);
        if (chance == null) return;

        // Roll a die, and return if player wasn't lucky
        if (!CurrencyModule.chanceOf(chance)) return;

        Bukkit.getScheduler().runTaskAsynchronously(instance, () -> {
            // Give money to the player
            int amount = module.getRandomAmount();
            CurrencyModuleGiveEvent event = new CurrencyModuleGiveEvent(module, p, amount, e);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) return;

            amount = event.getAmount();
            currency.getBalances().add(p.getUniqueId(), amount);

            HashMap<String, Object> phs = new HashMap<>();
            phs.put("amount", amount);
            lang.send(p, Lang.Currency.MODULE_KILL_MOB_GET, phs);
        });
    }
}
