package me.tetshow;

public class TetShowProvider {
    private static TetShow plugin;

    public static void set(TetShow p) {
        plugin = p;
    }

    public static TetShow plugin() {
        return plugin;
    }
}
