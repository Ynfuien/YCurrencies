package pl.ynfuien.ycurrencies.commands.currency;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import pl.ynfuien.ycurrencies.commands.Subcommand;
import pl.ynfuien.ycurrencies.commands.currency.admin.*;
import pl.ynfuien.ycurrencies.currencies.Currency;
import pl.ynfuien.ycurrencies.messages.Lang;

import java.util.*;


public class AdminSubcommand implements Subcommand {
    private final Currency currency;
    private final Lang lang;
    private final Subcommand[] subcommands;
    private final BukkitCommand command;

    public AdminSubcommand(Currency currency, BukkitCommand command) {
        this.currency = currency;
        this.lang = currency.getLang();
        this.command = command;

        subcommands = new Subcommand[] {
            new AddSubcommand(currency, this),
            new AddAllSubcommand(currency, this),
            new RemoveSubcommand(currency, this),
            new SetSubcommand(currency, this),
            new BalanceSubcommand(currency, this)
        };
    }

    @Override
    public String permission() {
        return String.format("%s.%s", command.getPermission(), name());
    }

    @Override
    public String name() {
        return "admin";
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

        // Get provided subcommand or show help info
        Subcommand subcommand = null;
        if (args.length > 0) {
            String arg1 = args[0].toLowerCase();
            subcommand = Arrays.stream(subcommands).filter(s -> sender.hasPermission(s.permission()) && s.name().equals(arg1)).findAny().orElse(null);
        }

        if (subcommand == null) {
            runHelp(sender, command);
            return;
        }

        subcommand.run(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
    }

    private void runHelp(CommandSender sender, Command command) {
        // Send top message
        lang.send(sender, Lang.Currency.HELP_TOP);

        // Get available commands for the sender
        Subcommand[] available = Arrays.stream(subcommands).filter(s -> sender.hasPermission(s.permission())).toArray(Subcommand[]::new);
        if (available.length == 0) {
            lang.send(sender, Lang.Currency.HELP_NO_COMMANDS);
            return;
        }

        Lang.Message template = Lang.Currency.HELP_COMMAND_TEMPLATE;
        // Get the shortest command alias
        String cmdName = command.getName();
        if (command.getAliases().size() > 0) {
            String alias = command.getAliases().stream().min(Comparator.comparing(String::length)).get();
            if (alias.length() < cmdName.length()) cmdName = alias;
        }
        String finalCmdName = cmdName;

        // Send help message for every command
        for (Subcommand subcommand : available) {
            if (!sender.hasPermission(subcommand.permission())) continue;

            String subCmdName = subcommand.name();
            String description = subcommand.description();
            String usage = subcommand.usage();

            lang.send(sender, template, new HashMap<>() {{
                put("command", String.format("%s %s %s%s", finalCmdName, name(), subCmdName, (usage != null ? " "+usage : "")));
                put("description", description);
            }});
        }
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        // Create new list for completions
        List<String> completions = new ArrayList<>();
        // Return empty list if args length is lower than 1 or higher than 3
        if (args.length > 3) return completions;

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

