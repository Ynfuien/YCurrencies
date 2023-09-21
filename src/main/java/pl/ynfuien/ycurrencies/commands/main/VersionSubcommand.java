package pl.ynfuien.ycurrencies.commands.main;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import pl.ynfuien.ycurrencies.YCurrencies;
import pl.ynfuien.ycurrencies.commands.Subcommand;
import pl.ynfuien.ycurrencies.messages.Lang;

import java.util.HashMap;
import java.util.List;

public class VersionSubcommand implements Subcommand {
    private final YCurrencies instance;
    private final Lang lang;

    public VersionSubcommand(YCurrencies instance) {
        this.instance = instance;
        this.lang = instance.getLang();
    }


    @Override
    public String permission() {
        return YCurrencies.getInstance().getPermissions().getCommandPerm("main." + name());
    }

    @Override
    public String name() {
        return "version";
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
        PluginMeta info = YCurrencies.getInstance().getPluginMeta();

        HashMap<String, Object> placeholders = new HashMap<>() {{
            put("name", info.getName());
            put("version", info.getVersion());
            put("author", info.getAuthors().get(0));
            put("description", info.getDescription());
            put("website", info.getWebsite());
        }};

        lang.send(sender, Lang.General.COMMAND_VERSION, placeholders);
    }

    @Override
    public List<String> getTabCompletions(CommandSender sender, String[] args) {
        return null;
    }
}
