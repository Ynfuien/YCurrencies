package pl.ynfuien.ycurrencies.config;

public enum ConfigName {
    CONFIG,
    LANG;

    String getFileName() {
        return name().toLowerCase().replace('_', '-') + ".yml";
    }
}
