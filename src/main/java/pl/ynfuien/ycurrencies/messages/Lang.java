package pl.ynfuien.ycurrencies.messages;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import pl.ynfuien.ycurrencies.utils.Logger;

import java.util.HashMap;

public class Lang {
    private HashMap<String, Object> defaultPlaceholders;
    private FileConfiguration langConfig;

    public void loadConfig(FileConfiguration langConfig) {
        this.langConfig = langConfig;
    }

    public void setDefaultPlaceholders(HashMap<String, Object> placeholders) {
        defaultPlaceholders = placeholders;
    }

    // Gets message by path
    public String get(Message message) {
        return langConfig.getString(message.getPath());
    }
    public String get(String path) {
        return langConfig.getString(path);
    }
    // Gets message by path and replaces placeholders
    public String get(String path, HashMap<String, Object> placeholders) {
        HashMap<String, Object> phs = new HashMap<>(defaultPlaceholders);
        phs.putAll(placeholders);

        // Return message with used placeholders
        return Messenger.replacePlaceholders(langConfig.getString(path), phs);
    }

    public void send(CommandSender sender, Message message) {
        send(sender, message.getPath(), new HashMap<>());
    }
    public void send(CommandSender sender, Message message, HashMap<String, Object> placeholders) {
        send(sender, message.getPath(), placeholders);
    }
    public void send(CommandSender sender, String path, HashMap<String, Object> placeholders) {
        // Get message
        String message = langConfig.getString(path);

        // Return and log error if message doesn't exist
        if (message == null) {
            Logger.logError(String.format("There is no message '%s'!", path));
            Messenger.send(sender, "<red>An error occurred while executing this command. Contact with the server administrator.");
            return;
        }

        // Return if message is empty
        if (message.isEmpty()) return;

        // Get message with used placeholders
        HashMap<String, Object> phs = new HashMap<>(defaultPlaceholders);
        phs.putAll(placeholders);
        message = Messenger.replacePlaceholders(message, phs);

        Messenger.send(sender, message, placeholders);
    }


    public interface Message {
        String getPath();
    }

    public enum General implements Lang.Message {
        PREFIX,
        PLUGIN_IS_RELOADING,
        COMMANDS_NO_PERMISSION,
        COMMAND_USAGE,
        COMMAND_RELOAD_FAIL,
        COMMAND_RELOAD_SUCCESS,
        COMMAND_VERSION;

        public String getPath() {
            return name().toLowerCase().replace('_', '-');
        }
    }

    public enum Currency implements Lang.Message {
        PREFIX,
        HELP_NO_COMMANDS,
        HELP_TOP,
        HELP_COMMAND_TEMPLATE,
        COMMANDS_USAGE_PLAYER,
        COMMANDS_USAGE_AMOUNT,
        COMMANDS_USAGE_BALANCE,
        COMMANDS_ONLY_PLAYER,
        COMMAND_ADMIN_ADD_DESCRIPTION,
        COMMAND_ADMIN_ADDALL_DESCRIPTION,
        COMMAND_ADMIN_BALANCE_DESCRIPTION,
        COMMAND_ADMIN_SET_DESCRIPTION,
        COMMAND_ADMIN_REMOVE_DESCRIPTION,
        COMMAND_ADMIN_DEBUG_DESCRIPTION,
        COMMAND_BALANCE,
        COMMAND_GIVE_USAGE,
        COMMAND_GIVE_SUCCESS,
        COMMAND_GIVE_RECEIVE,
        COMMAND_GIVE_FAIL_YOURSELF,
        COMMAND_GIVE_FAIL_TOO_MUCH,
        COMMAND_GIVE_FAIL_TOO_LITTLE,
        COMMAND_FAIL_NO_AMOUNT,
        COMMAND_FAIL_NO_PLAYER,
        COMMAND_FAIL_INCORRECT_USERNAME,
        COMMAND_FAIL_INCORRECT_AMOUNT,
        COMMAND_FAIL_AMOUNT_TOO_LARGE,
        COMMAND_FAIL_PLAYER_DOESNT_EXIST,
        COMMAND_FAIL_PLAYER_ISNT_ONLINE,
        COMMAND_ADMIN_BALANCE,
        COMMAND_ADMIN_ADDALL,
        COMMAND_ADMIN_ADD,
        COMMAND_ADMIN_SET,
        COMMAND_ADMIN_REMOVE,
        MODULE_BREAK_BLOCK_GET,
        MODULE_KILL_MOB_GET,
        MODULE_INTERVAL_GET;

        public String getPath() {
            return name().toLowerCase().replace('_', '-');
        }
    }
}
