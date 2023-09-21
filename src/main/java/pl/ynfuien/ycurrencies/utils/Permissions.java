package pl.ynfuien.ycurrencies.utils;

public class Permissions {
    private final String basePerm;
    private final String commandPerm;

    public Permissions(String base) {
        this.basePerm = base;
        commandPerm = base + ".cmd";
    }


    public String getBase() {
        return basePerm;
    }

    public String getCommandPerm() {
        return commandPerm;
    }

    public String getCommandPerm(String cmd) {
        return String.format("%s.%s", commandPerm, cmd);
    }
}
