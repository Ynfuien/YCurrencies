package pl.ynfuien.ycurrencies.currencies;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import pl.ynfuien.ycurrencies.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CurrencyDatabase {
    private static HikariDataSource dbSource;
    private static String dbName;

    public static boolean setup(ConfigurationSection config) {
        close();

        dbName = config.getString("name");

        HikariConfig dbConfig = new HikariConfig();
        dbConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dbConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", config.getString("host"), config.getString("port"), dbName));
        dbConfig.setUsername(config.getString("login"));
        dbConfig.setPassword(config.getString("password"));
        dbConfig.setMaximumPoolSize(config.getInt("max-connections"));

        try {
            dbSource = new HikariDataSource(dbConfig);
        } catch (Exception e) {
            Logger.logError("Plugin couldn't connect to a database! Please check connection data, because plugin can't work without the database!");
            return false;
        }

        return true;
    }

    public static void close() {
        if (dbSource != null) dbSource.close();
    }

    public static boolean balanceExists(Currency currency, UUID uuid) {
        String table = currency.getDatabaseTable();
        String query = String.format("SELECT balance FROM `%s` WHERE uuid=? LIMIT 1", table);

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            Logger.logWarning(String.format("Couldn't retrieve data from table '%s'.", table));
            e.printStackTrace();
            return false;
        }
    }

    public static int getBalance(Currency currency, UUID uuid) {
        String table = currency.getDatabaseTable();
        String query = String.format("SELECT balance FROM `%s` WHERE uuid=? LIMIT 1", table);

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, uuid.toString());
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) return resultSet.getInt("balance");
            return 0;
        } catch (SQLException e) {
            Logger.logWarning(String.format("Couldn't retrieve data from table '%s'.", table));
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean setBalance(Currency currency, UUID uuid, int balance) {
        String table = currency.getDatabaseTable();
        String query = String.format("UPDATE `%s` SET balance=? WHERE uuid=?", table);

        if (!balanceExists(currency, uuid)) {
            query = String.format("INSERT INTO `%s`(balance, uuid) VALUES(?, ?)", table);
        }

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, balance);
            stmt.setString(2, uuid.toString());
            stmt.execute();

        } catch (SQLException e) {
            Logger.logWarning(String.format("Couldn't save data to table '%s'.", table));
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean createTable(Currency currency) {
        String table = currency.getDatabaseTable();
        String query = String.format("CREATE TABLE IF NOT EXISTS `%s` (`id` int(11) NOT NULL AUTO_INCREMENT, `uuid` varchar(36) NOT NULL, `balance` int(11) NOT NULL DEFAULT 0, PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4", table);

        try (Connection conn = dbSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            Logger.logError(String.format("Couldn't create table '%s' in database '%s'", table, dbName));
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
