package pl.ynfuien.ycurrencies.commands.currency.admin;

import org.bukkit.Bukkit;
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


public class AddAllSubcommand implements Subcommand {
    private final Currency currency;
    private final Subcommand command;
    private final Lang lang;

    public AddAllSubcommand(Currency currency, Subcommand command) {
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
        return "addall";
    }

    @Override
    public String description() {
        return lang.get(Lang.Currency.COMMAND_ADMIN_ADDALL_DESCRIPTION);
    }

    @Override
    public String usage() {
        return String.format(
                "<%s>",
                lang.get(Lang.Currency.COMMANDS_USAGE_AMOUNT)
        );
    }


    @Override
    public void run(CommandSender sender, Command command, String label, String[] args) {
        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        String amountArg = args.length > 0 ? args[0] : null;

        // Check if amount arg is correct
        if (!CurrencyCommand.checkAmountArg(sender, lang, amountArg, placeholders)) return;

        int amount = 0;
        try {
            amount = Integer.parseInt(amountArg);
        } catch (NumberFormatException e) {return;}

        for (Player p : Bukkit.getOnlinePlayers()) {
            currency.getBalances().add(p.getUniqueId(), amount);
        }

        placeholders.put("amount", amount);
        placeholders.put("amount-formatted", CurrencyFormatter.format(amount));
        lang.send(sender, Lang.Currency.COMMAND_ADMIN_ADDALL, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // Create new list for completions
        List<String> completions = new ArrayList<>();
        if (args.length > 1) return completions;


        String arg1 = args[0];
        for (String amount : Arrays.asList("10", "100", "1000")) {
            if (amount.startsWith(arg1)) completions.add(amount);
        }

        return completions;
    }
}

