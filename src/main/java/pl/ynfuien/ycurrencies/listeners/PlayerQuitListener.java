package pl.ynfuien.ycurrencies.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.currencies.Currencies;
import pl.ynfuien.ycurrencies.currencies.Currency;

public class PlayerQuitListener implements Listener {
    private final Currencies currencies;
    public PlayerQuitListener(YCurrencies instance) {
        this.currencies = instance.getCurrencies();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Remove player's balances from cache
        for (Currency currency : currencies.getAll()) {
            currency.getBalances().removeFromCache(event.getPlayer().getUniqueId());
        }
    }
}
