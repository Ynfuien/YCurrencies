package pl.ynfuien.ycurrencies.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.ycurrencies.currencies.modules.CurrencyModule;

/**
 * An event fired when any currency module wants to give a currency to a player.
 */
public class CurrencyModuleGiveEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final CurrencyModule module;
    private int amount;
    private final Event underlyingEvent;
    private boolean cancelled;

    public CurrencyModuleGiveEvent(CurrencyModule module, Player player, int amount, Event underlyingEvent) {
        super(true);

        this.module = module;
        this.player = player;
        this.amount = amount;
        this.underlyingEvent = underlyingEvent;
        this.cancelled = false;
    }

    public Player getPlayer() {
        return player;
    }

    public CurrencyModule getModule() {
        return module;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Gets an underlying event that was used for specified module.
     * This can be null in case of module not using any listener (Type.INTERVAL).
     * @return Underlying event or null
     */
    public Event getUnderlyingEvent() {
        return underlyingEvent;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
