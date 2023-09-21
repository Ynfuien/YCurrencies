package pl.ynfuien.ycurrencies.commands.currency.admin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.ynfuien.ycurrencies.commands.Subcommand;
import pl.ynfuien.ycurrencies.commands.currency.CurrencyCommand;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.messages.Lang;
import pl.ynfuien.ycurrencies.utils.CurrencyFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class SetSubcommand implements Subcommand {
    private final Currency currency;
    private final Subcommand command;
    private final Lang lang;

    public SetSubcommand(Currency currency, Subcommand command) {
        this.currency = currency;
        this.command = command;
        this.lang = currency.getLang();
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.permission(), name());
    }

    @Override
    public String name() {
        return "set";
    }

    @Override
    public String description() {
        return lang.get(Lang.Currency.COMMAND_ADMIN_SET_DESCRIPTION);
    }

    @Override
    public String usage() {
        return String.format(
                "<%s> <%s>",
                lang.get(Lang.Currency.COMMANDS_USAGE_PLAYER),
                lang.get(Lang.Currency.COMMANDS_USAGE_BALANCE)
        );
    }

    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        String playerArg = args.length > 0 ? args[0] : null;
        String balanceArg = args.length > 1 ? args[1] : null;

        // Check if player arg is correct
        if (!CurrencyCommand.checkPlayerArg(sender, lang, playerArg, placeholders)) return;
        // Check if balance arg is correct
        if (!CurrencyCommand.checkAmountArg(sender, lang, balanceArg, placeholders)) return;

        OfflinePlayer p = Bukkit.getOfflinePlayer(playerArg);
        int balance = 0;
        try {
            balance = Integer.parseInt(balanceArg);
        } catch (NumberFormatException e) {return;}

        currency.getBalances().set(p.getUniqueId(), balance);

        placeholders.put("balance", balance);
        placeholders.put("balance-formatted", CurrencyFormatter.format(balance));
        placeholders.put("player-username", p.getName());
        lang.send(sender, Lang.Currency.COMMAND_ADMIN_SET, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // Create new list for completions
        List<String> completions = new ArrayList<>();
        if (args.length > 2) return completions;

        // First arg
        String arg1 = args[0].toLowerCase();
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                String name = p.getName();
                if (name.toLowerCase().startsWith(arg1)) completions.add(name);
            }

            return completions;
        }

        // Second arg
        String arg2 = args[1];
        for (String balance : Arrays.asList("0", "10", "100", "1000")) {
            if (balance.startsWith(arg2)) completions.add(balance);
        }

        return completions;
    }
}

