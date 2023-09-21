package pl.ynfuien.ycurrencies.commands.main;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.commands.Subcommand;
import pl.ynfuien.ycurrencies.messages.Lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final YCurrencies instance;
    private final Lang lang;
    public Subcommand[] subcommands;

    public MainCommand(YCurrencies instance) {
        this.instance = instance;
        this.lang = instance.getLang();

        subcommands = new Subcommand[]{
                new ReloadSubcommand(instance),
                new VersionSubcommand(instance)
        };
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Return if plugin is reloading
        if (YCurrencies.getInstance().isReloading()) {
            lang.send(sender, Lang.General.PLUGIN_IS_RELOADING);
            return true;
        }

        HashMap<String, Object> placeholders = new HashMap<>() {{put("command", label);}};

        if (args.length == 0) {
            lang.send(sender, Lang.General.COMMAND_USAGE, placeholders);
            return true;
        }

        // Loop through and check every subcommand
        String arg1 = args[0].toLowerCase();
        for (Subcommand cmd : subcommands) {
            if (!cmd.name().equals(arg1)) continue;

            if (!sender.hasPermission(cmd.permission())) {
                lang.send(sender, Lang.General.COMMANDS_NO_PERMISSION, placeholders);
                return true;
            }

            String[] argsLeft = Arrays.copyOfRange(args, 1, args.length);
            cmd.run(sender, command, label, argsLeft);
            return true;
        }

        lang.send(sender, Lang.General.COMMAND_USAGE, placeholders);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();

        if (YCurrencies.getInstance().isReloading()) return completions;
        if (args.length != 1) return completions;


        // Get commands that sender has permissions for
        List<Subcommand> canUse = Arrays.stream(subcommands).filter(cmd -> sender.hasPermission(cmd.permission())).toList();
        if (canUse.size() == 0) return completions;

        //// Tab completion for subcommands
        String arg1 = args[0].toLowerCase();
        for (Subcommand cmd : canUse) {
            String name = cmd.name();

            if (name.startsWith(arg1)) completions.add(name);
        }

        return completions;
    }
}
