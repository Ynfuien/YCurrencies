package pl.ynfuien.ycurrencies.currencies;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.commands.currency.CurrencyCommand;
import pl.ynfuien.ycurrencies.utils.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CurrencyCommands {
    private final YCurrencies instance;
    private final String prefix;
    private final Currencies currencies;
    private List<BukkitCommand> commands = new ArrayList<>();

    private Method syncCommandsMethod;
    private CommandMap commandMap;
    private Map<String, Command> knownCommands;
    private Set<String> aliases;

    public CurrencyCommands(YCurrencies instance, Currencies currencies) {
        this.instance = instance;
        this.prefix = instance.getName().toLowerCase();
        this.currencies = currencies;
    }

    public boolean prepare() {
        try {
            // Get command map
            final Field bukkitCommandMap;
            bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            // Get known commands
            final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
            knownCommandsField.setAccessible(true);
            knownCommands = (Map<String, Command>) knownCommandsField.get(commandMap);

            // Get sync commands method
            try {
                Class<?> craftServer;
                String revision = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
                craftServer = Class.forName("org.bukkit.craftbukkit." + revision + ".CraftServer");

                syncCommandsMethod = craftServer.getDeclaredMethod("syncCommands");
                if (syncCommandsMethod != null) syncCommandsMethod.setAccessible(true);
            } catch (ClassNotFoundException | NoSuchMethodException e) {}

            // Get aliases
            try {
                final Field aliasesField = SimpleCommandMap.class.getDeclaredField("aliases");
                aliasesField.setAccessible(true);
                aliases = (Set<String>) aliasesField.get(commandMap);
            } catch (NoSuchFieldException e) {}
        } catch (NoSuchFieldException|IllegalAccessException e) {
            logError("Couldn't initialize command map for registering commands! Error:");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void logInfo(String message) {
        Logger.log("[CurrencyCommands] " + message);
    }
    private void logError(String message) {
        Logger.logWarning("[CurrencyCommands] " + message);
    }

    public void register() {
        for (Currency currency : currencies.getAll()) {
            CurrencyCommand command = new CurrencyCommand(instance, currency, currency.getCommandName(), currency.getCommandAliases());
            commandMap.register(prefix, command);

            commands.add(command);
        }

        logInfo(String.format("Successfully registered commands for %d currencies!", commands.size()));
    }

    public void unregister() {
        for (BukkitCommand cmd : commands) {
            String name = cmd.getName();

            knownCommands.remove(name);
            knownCommands.remove(prefix + ":" + name);

            if (aliases != null) aliases.removeAll(cmd.getAliases());

            for (String alias : cmd.getAliases()) {
                knownCommands.remove(alias);
                knownCommands.remove(prefix + ":" + alias);
            }

            cmd.unregister(commandMap);
            cmd.setAliases(new ArrayList<>());
        }

        commands.clear();
    }

    // Reloads available commands, because they are still in tab completion, after unregistering them
    public boolean syncCommands() {
        if (syncCommandsMethod == null) return false;

        try {
            syncCommandsMethod.invoke(Bukkit.getServer());
            return true;
        } catch (Exception e) { return false; }
    }
}
