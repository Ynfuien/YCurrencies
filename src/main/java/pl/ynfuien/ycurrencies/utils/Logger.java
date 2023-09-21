package pl.ynfuien.ycurrencies.utils;

import org.bukkit.Bukkit;
import pl.ynfuien.ycurrencies.messages.Messenger;

public class Logger {
    private static String prefix;

    public static void setPrefix(String prefix) {
        Logger.prefix = prefix;
    }

    public static void log(String message) {
        Messenger.send(Bukkit.getConsoleSender(), prefix + message);
    }

    public static void logWarning(String message) {
        log("<yellow>" + message);
    }

    public static void logError(String message) {
        log("<red>" + message);
    }
}
