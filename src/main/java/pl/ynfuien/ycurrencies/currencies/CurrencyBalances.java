package pl.ynfuien.ycurrencies.currencies;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.ynfuien.ycurrencies.YCurrencies;

import java.util.HashMap;
import java.util.UUID;

public class CurrencyBalances {
    private final Currency currency;
    private final HashMap<UUID, Integer> cache = new HashMap<>();

    public CurrencyBalances(Currency currency) {
        this.currency = currency;
    }


    /**
     * Gets player's balance. Method first looks into cache, and then into database, if it didn't find uuid in the cache.
     * @param uuid Player's uuid
     * @return Player's balance
     */
    public int get(UUID uuid) {
        if (cache.containsKey(uuid)) return cache.get(uuid);

        int balance = CurrencyDatabase.getBalance(currency, uuid);
        if (Bukkit.getPlayer(uuid) != null) cache.put(uuid, balance);

        return balance;
    }

    /**
     * Sets player's balance to new value
     * @param uuid Player's uuid
     * @param balance New balance
     */
    public void set(UUID uuid, int balance) {
        int bal = Math.max(balance, 0);

        Player p = Bukkit.getPlayer(uuid);
        if (p != null && p.isOnline()) cache.put(uuid, bal);

        Bukkit.getScheduler().runTaskAsynchronously(YCurrencies.getInstance(), () -> {
            CurrencyDatabase.setBalance(currency, uuid, bal);
        });
    }

    /**
     * Adds currency to player's balance
     * @param uuid Player's uuid
     * @param amount Amount to add
     */
    public void add(UUID uuid, int amount) {
        set(uuid, get(uuid) + amount);
    }

    /**
     * Removes currency from player's balance
     * @param uuid Player's uuid
     * @param amount Amount to remove
     */
    public void remove(UUID uuid, int amount) {
        set(uuid, get(uuid) - amount);
    }

    /**
     * Removes player's balance from the cache. It's generally used on player quit event.
     * @param uuid Player's uuid
     */
    public void removeFromCache(UUID uuid) {
        cache.remove(uuid);
    }

    /**
     * Gets currency instance of witch these balances are.
     * @return Currency object
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Gets a whole cache of balances.
     * @return Balances cache
     */
    public HashMap<UUID, Integer> getCache() {
        return cache;
    }
}
