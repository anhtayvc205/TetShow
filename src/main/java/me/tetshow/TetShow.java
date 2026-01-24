package me.tetshow;

import me.tetshow.command.FWCommand;
import me.tetshow.data.ShowData;
import org.bukkit.plugin.java.JavaPlugin;

public class TetShow extends JavaPlugin {

    private static TetShow instance;
    private ShowData data;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        data = new ShowData(this);
        getCommand("fw").setExecutor(new FWCommand(this));
    }

    public static TetShow get() {
        return instance;
    }

    public ShowData getData() {
        return data;
    }
}
