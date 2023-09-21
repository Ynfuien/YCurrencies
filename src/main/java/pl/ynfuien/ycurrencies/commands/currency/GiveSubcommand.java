package pl.ynfuien.ycurrencies.commands.currency;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import pl.ynfuien.ycurrencies.commands.Subcommand;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.currencies.CurrencyBalances;
import pl.ynfuien.ycurrencies.messages.Lang;
import pl.ynfuien.ycurrencies.messages.Messenger;
import pl.ynfuien.ycurrencies.utils.CurrencyFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class GiveSubcommand implements Subcommand {
    private final Currency currency;
    private final Lang lang;
    private final BukkitCommand command;

    public GiveSubcommand(Currency currency, BukkitCommand command) {
        this.currency = currency;
        this.lang = currency.getLang();
        this.command = command;
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.getPermission(), name());
    }

    @Override
    public String name() {
        return "give";
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public String usage() {
        return null;
    }


    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        if (!(sender instanceof Player)) {
            lang.send(sender, Lang.Currency.COMMANDS_ONLY_PLAYER, placeholders);
            return;
        }

        if (args.length == 0) {
            lang.send(sender, Lang.Currency.COMMAND_GIVE_USAGE, placeholders);
            return;
        }

        // Check if player is online
        Player receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null || !receiver.isOnline()) {
            lang.send(sender, Lang.Currency.COMMAND_FAIL_PLAYER_ISNT_ONLINE, placeholders);
            return;
        }

        // Check if amount arg is correct
        String amountArg = args.length > 1 ? args[1] : null;
        if (!CurrencyCommand.checkAmountArg(sender, lang, amountArg, placeholders)) return;

        // Get provided amount
        int amount = 0;
        try {
            amount = Integer.parseInt(amountArg);
        } catch (NumberFormatException e) {return;}
        placeholders.put("amount", amount);
        placeholders.put("amount-formatted", CurrencyFormatter.format(amount));

        // Check if it's not 0 or lower
        if (amount < 1) {
            lang.send(sender, Lang.Currency.COMMAND_GIVE_FAIL_TOO_LITTLE, placeholders);
            return;
        }

        Player p = (Player) sender;

        // Check if player has enough money
        CurrencyBalances balances = currency.getBalances();
        if (amount > balances.get(p.getUniqueId())) {
            lang.send(sender, Lang.Currency.COMMAND_GIVE_FAIL_TOO_MUCH, placeholders);
            return;
        }

        // Check if player want to give money to himself
        if (p.equals(receiver)) {
            lang.send(sender, Lang.Currency.COMMAND_GIVE_FAIL_YOURSELF, placeholders);
            return;
        }

        // Transfer money from player to player
        balances.remove(p.getUniqueId(), amount);
        balances.add(receiver.getUniqueId(), amount);

        // Send messages
        placeholders.put("player-username", receiver.getName());
        placeholders.put("player-displayname", Messenger.getMiniMessage().serialize(receiver.displayName()));
        lang.send(sender, Lang.Currency.COMMAND_GIVE_SUCCESS, placeholders);

        placeholders.put("player-username", p.getName());
        placeholders.put("player-displayname", Messenger.getMiniMessage().serialize(p.displayName()));
        lang.send(receiver, Lang.Currency.COMMAND_GIVE_RECEIVE, placeholders);
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
                if (p.equals(sender)) continue;

                String name = p.getName();
                if (name.toLowerCase().startsWith(arg1)) completions.add(name);
            }

            return completions;
        }

        // Second arg
        String arg2 = args[1];
        for (String amount : Arrays.asList("10", "100", "1000")) {
            if (amount.startsWith(arg2)) completions.add(amount);
        }

        return completions;
    }
}

