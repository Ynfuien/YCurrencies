package pl.ynfuien.ycurrencies.commands.main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.commands.Subcommand;
import pl.ynfuien.ycurrencies.messages.Lang;

import java.util.ArrayList;
import java.util.List;


public class ReloadSubcommand implements Subcommand {
    private final YCurrencies instance;
    private final Lang lang;

    public ReloadSubcommand(YCurrencies instance) {
        this.instance = instance;
        this.lang = instance.getLang();
    }

    @Override
    public String permission() {
        return YCurrencies.getInstance().getPermissions().getCommandPerm("main." + name());
    }

    @Override
    public String name() {
        return "reload";
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
        // Reload plugin
        boolean success = YCurrencies.getInstance().reloadPlugin();

        // Check if reload was success
        if (success) {
            // Send success message to console if sender is player
            if (sender instanceof Player) {
                lang.send(Bukkit.getConsoleSender(), Lang.General.COMMAND_RELOAD_SUCCESS);
            }
            // Send success message to sender
            lang.send(sender, Lang.General.COMMAND_RELOAD_SUCCESS);
            return;
        }

        // Send fail message to console if sender is player
        if (sender instanceof Player) {
            lang.send(Bukkit.getConsoleSender(), Lang.General.COMMAND_RELOAD_FAIL);
        }
        // Send fail message to sender
        lang.send(sender, Lang.General.COMMAND_RELOAD_FAIL);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return new ArrayList<>();
    }
}

