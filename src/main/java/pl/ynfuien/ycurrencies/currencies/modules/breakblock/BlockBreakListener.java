package pl.ynfuien.ycurrencies.currencies.modules.breakblock;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.api.events.CurrencyModuleGiveEvent;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.currencies.modules.CurrencyModule;
import pl.ynfuien.ycurrencies.messages.Lang;

import java.util.HashMap;

public class BlockBreakListener implements Listener {
    private final YCurrencies instance;
    private final Currency currency;
    private final Lang lang;
    private final BreakBlockCurrencyModule module;

    public BlockBreakListener(Currency currency, BreakBlockCurrencyModule module) {
        this.instance = YCurrencies.getInstance();
        this.currency = currency;
        this.lang = currency.getLang();
        this.module = module;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        Player p = e.getPlayer();

        if (p == null) return;

        if (module.isIgnoringGamemode(p.getGameMode())) return;

        // Get specified chance of getting money for killing this mob
        // or null if none specified.
        Double chance = module.getChanceForABlock(b.getType());
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
            lang.send(p, Lang.Currency.MODULE_BREAK_BLOCK_GET, phs);
        });
    }
}
