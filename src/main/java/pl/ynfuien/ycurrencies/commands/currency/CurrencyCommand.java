package pl.ynfuien.ycurrencies.commands.currency;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.commands.Subcommand;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.messages.Lang;
import pl.ynfuien.ycurrencies.utils.CurrencyFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CurrencyCommand extends BukkitCommand {
    private final YCurrencies instance;
    private final Currency currency;
    private final Lang lang;
    private Subcommand[] subcommands;

    public CurrencyCommand(YCurrencies instance, Currency currency, String name, List<String> aliases) {
        super(name);

        this.instance = instance;
        this.lang = currency.getLang();
        this.currency = currency;
        subcommands = new Subcommand[] {
                new AdminSubcommand(currency, this),
                new GiveSubcommand(currency, this),
        };

        this.setDescription(String.format("Command for %s currency", currency.getName()));
        this.setPermission(YCurrencies.getInstance().getPermissions().getCommandPerm(currency.getName()));
        this.setAliases(aliases);
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        // Return if plugin is reloading
        if (instance.isReloading()) {
            lang.send(sender, Lang.General.PLUGIN_IS_RELOADING, placeholders);
            return true;
        }

        // Subcommands
        if (args.length > 0) {
            String arg1 = args[0].toLowerCase();
            for (Subcommand subcommand : subcommands) {
                if (!sender.hasPermission(subcommand.permission())) continue;
                if (!subcommand.name().equals(arg1)) continue;

                subcommand.run(sender, this, label, Arrays.copyOfRange(args, 1, args.length));
                return true;
            }
        }

        // Showing balance
        if (!(sender instanceof Player)) {
            lang.send(sender, Lang.Currency.COMMANDS_ONLY_PLAYER, placeholders);
            return true;
        }

        int balance = currency.getBalances().get(((Player) sender).getUniqueId());
        placeholders.put("balance", balance);
        placeholders.put("balance-formatted", CurrencyFormatter.format(balance));
        lang.send(sender, Lang.Currency.COMMAND_BALANCE, placeholders);
        return true;
    }

    public static boolean checkPlayerArg(CommandSender sender, Lang lang, String arg, HashMap<String, Object> placeholders) {
        // Check if player is provided
        if (arg == null) {
            lang.send(sender, Lang.Currency.COMMAND_FAIL_NO_PLAYER, placeholders);
            return false;
        }

        // Check if provided username is correct
        if (!arg.matches("^[a-zA-z0-9_]{2,16}$")) {
            placeholders.put("username", arg);
            lang.send(sender, Lang.Currency.COMMAND_FAIL_INCORRECT_USERNAME, placeholders);
            return false;
        }

        // Check if player was ever on the server
        OfflinePlayer p = Bukkit.getOfflinePlayer(arg);
        if (!p.hasPlayedBefore()) {
            lang.send(sender, Lang.Currency.COMMAND_FAIL_PLAYER_DOESNT_EXIST, placeholders);
            return false;
        }

        return true;
    }

    public static boolean checkAmountArg(CommandSender sender, Lang lang, String arg, HashMap<String, Object> placeholders) {
        // Check if amount is provided
        if (arg == null) {
            lang.send(sender, Lang.Currency.COMMAND_FAIL_NO_AMOUNT, placeholders);
            return false;
        }

        // Check if amount is a correct integer
        int amount = 0;
        try {
            amount = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            lang.send(sender, Lang.Currency.COMMAND_FAIL_INCORRECT_AMOUNT, placeholders);
            return false;
        }

        // Check if it's not too large
        if (amount > Integer.MAX_VALUE / 100) {
            lang.send(sender, Lang.Currency.COMMAND_FAIL_AMOUNT_TOO_LARGE, placeholders);
            return false;
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        // Create new list for completions
        List<String> completions = new ArrayList<>();
        // Return empty list if args length is lower than 1 or higher than 3
        if (args.length > 4) return completions;

        // Subcommands completion
        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (Subcommand subcommand : subcommands) {
                if (!sender.hasPermission(subcommand.permission())) continue;

                String name = subcommand.name();
                if (name.startsWith(arg1)) completions.add(name);
            }

            return completions;
        }

        // Completion for selected subcommand
        for (Subcommand subcommand : subcommands) {
            if (!sender.hasPermission(subcommand.permission())) continue;
            if (!subcommand.name().equals(arg1)) continue;

            return subcommand.getTabCompletions(sender, Arrays.copyOfRange(args, 1, args.length));
        }


        return completions;
    }
}
